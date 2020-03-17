/*
 * Renderer - Light Management?
 */
package thump.render;

import java.util.logging.Logger;
import thump.game.Game;
import thump.game.Player;
import static thump.global.Defines.SCREENHEIGHT;
import static thump.global.Defines.SCREENWIDTH;
import thump.global.FixedPoint;
import static thump.global.FixedPoint.FRACBITS;
import static thump.global.FixedPoint.FRACUNIT;
import static thump.global.Tables.*;
import thump.maplevel.MapTexture;
import thump.menu.MenuManager;
import static thump.render.Node.NF_SUBSECTOR;
import thump.render.colfuncs.ColFunc_DrawColumn;
import thump.render.colfuncs.ColFunc_DrawColumnLow;
import thump.render.colfuncs.ColFunc_DrawFuzzColumn;
import thump.render.colfuncs.ColFunc_DrawTranslatedColumn;
import thump.render.colfuncs.ColumnFunction;

/**
 *
 * @author mark
 */
public class Renderer {
    
    //
    // Sky    r_sky.c rolled in here.
    //
    // SKY, store the number for name.
    public static final String SKYFLATNAME = "F_SKY1";

    // The sky map is 256*128*4 maps.
    public static final int ANGLETOSKYSHIFT = 22;
    //
    // sky mapping
    //
    public int  skyflatnum;
    public MapTexture  skytexture;
    public int  skytexturemid;
    
    
    //
    // Lighting LUT.
    // Used for z-depth cuing per column/row,
    //  and other lighting effects (sector ambient, flash).
    //

    // Lighting constants.
    // Now why not 32 levels here?
    public static final int LIGHTLEVELS        = 16;
    public static final int LIGHTSEGSHIFT      = 4;

    public static final int MAXLIGHTSCALE      = 48;
    public static final int LIGHTSCALESHIFT    = 12;
    public static final int MAXLIGHTZ          = 128;
    public static final int LIGHTZSHIFT        = 20;

    public byte[][][]	scalelight      = new byte[LIGHTLEVELS][MAXLIGHTSCALE][NUMCOLORMAPS];
    public byte[][]	scalelightfixed = new byte[MAXLIGHTSCALE][NUMCOLORMAPS];
    public byte[][][]	zlight          = new byte[LIGHTLEVELS][MAXLIGHTZ][NUMCOLORMAPS];

    // bumped light from gun blasts
    public int		extralight;
    public byte[]	fixedcolormap;


    // Number of diminishing brightness levels.
    // There a 0-31, i.e. 32 LUT in the COLORMAP lump.
    public static final int NUMCOLORMAPS   =   32;


    // Blocky/low detail mode.
    //B remove this?
    //  0 = high, 1 = low
    public	boolean		detailshift = false;	


        // Fineangles in the SCREENWIDTH wide window.
    private static final int FIELDOFVIEW = 2048;

    protected int		viewangleoffset;

    // increment every time a check is made
    public int			validcount = 1;		


    //extern byte[][]	walllights;   //in Segs.java

    public int			centerx;
    public int			centery;

    public int			centerxfrac;
    public int			centeryfrac;
    public int			projection;

    // just for profiling purposes
    private int			framecount;	

    public int			sscount;
    private int			linecount;
    private int			loopcount;

    public  int			viewx;
    public  int			viewy;
    public  int			viewz;

    public long                 viewangle;

    public int			viewcos;
    public int			viewsin;

    public Player		viewplayer;
    
    public final Data           data;
    public final Draw           draw;
    public final Game           game;
    public final Plane          plane;
    public final Bsp            bsp;

    //
    // precalculated math tables
    //
    long clipangle;

    // The viewangletox[viewangle + FINEANGLES/4] lookup
    // maps the visible view angles to screen X coordinates,
    // flattening the arc to a flat projection plane.
    // There will be many angles mapped to the same X. 
    long[] viewangletox = new long[FINEANGLES/2];

    // The xtoviewangleangle[] table maps a screen pixel
    // to the lowest viewangle that maps back to x ranges
    // from clipangle to -clipangle.
    long[]   xtoviewangle = new long[SCREENWIDTH+1];
    
    //void (*colfunc) (void);
    public ColumnFunction colfunc;
    //void (*basecolfunc) (void);
    public ColumnFunction basecolfunc;
    //void (*fuzzcolfunc) (void);
    public ColumnFunction fuzzcolfunc;
    //void (*transcolfunc) (void);
    public ColumnFunction transcolfunc;
    //void (*spanfunc) (void);
    public ColumnFunction spanfunc;
    private Logger logger;


    // UNUSED.
    // The finetangentgent[angle+FINEANGLES/4] table
    // holds the int tangent values for view angles,
    // ranging from MININT to 0 to MAXINT.
    // int		finetangent[FINEANGLES/2];

    // int		finesine[5*FINEANGLES/4];
//?    int[]		finecosine = finesine[FINEANGLES/4];


    //byte[]		scalelight[LIGHTLEVELS][MAXLIGHTSCALE];
    //byte[]		scalelightfixed[MAXLIGHTSCALE];
    //byte[]		zlight[LIGHTLEVELS][MAXLIGHTZ];

    public Renderer( Game game ) {
        this.game = game;
        data    = new Data(this);
        draw    = new Draw(this);
        plane   = new Plane(this);
        bsp     = new Bsp(this);
    }


    public void R_Init () {
        MenuManager menu = MenuManager.getInstance();
        logger = thump.global.Defines.logger;
        
        logger.config("R_InitData\n");
        data.R_InitData ();
        
        //UNUSED - gets points from a table.
        //logger.config("\nR_InitPointToAngle");
        //R_InitPointToAngle ();

        //UNUSED - gets points from a table.
        //logger.config("\nR_InitTables");
        //R_InitTables ();
        // viewwidth / viewheight / detailLevel are set by the defaults

        R_SetViewSize (menu.screenblocks, menu.detailLevel);
        logger.config("R_InitPlanes\n");
        game.renderer.plane.R_InitPlanes ();   //todo  Handled in r_planes.c
        
        logger.config("R_InitLightTables\n");
        R_InitLightTables ();
        
        logger.config("R_InitSkyMap\n");
        R_InitSkyMap ();   //todo handled in r_sky.c
        
        logger.config("R_InitTranslationTables\n");
        draw.R_InitTranslationTables ();  //todo handled in r_draw.c

        framecount = 0;
        
        game.videoInterface.I_SetPalette(game.wad.paletteList.get(0));
    }

    /**
     * R_AddPointToBox
     * Expand a given bbox so that it encloses a given point.
     * 
     * @param x
     * @param y
     * @param box to add x/y point to.
     */
    void
    R_AddPointToBox
    ( int		x,
      int		y,
      BoundingBox	box )
    {
        if (x< box.left) {
            box.left = x;
        }
        if (x> box.right) {
            box.right = x;
        }
        if (y< box.bottom) {
            box.bottom = y;
        }
        if (y> box.top) {
            box.top = y;
        }
    }

    /**
     * Traverse BSP (sub) tree, check point against partition plane.
     * 
     * @param x
     * @param y
     * @param node
     * @return side.  false=(front) or true=(back).
     */
    boolean R_PointOnSide( int x, int y, Node node ) {
        int	dx;
        int	dy;
        int	left;
        int	right;

        if (node.dx == 0) {
            if (x <= node.x) {
                return node.dy > 0;
            }

            return node.dy < 0;
        }
        if (node.dy == 0) {
            if (y <= node.y) {
                return node.dx < 0;
            }

            return node.dx > 0;
        }

        dx = (x - node.x);
        dy = (y - node.y);

        // Try to quickly decide by looking at sign bits.
        if ( ((node.dy ^ node.dx ^ dx ^ dy)&0x80000000) > 0 ) {
            // (left is negative)
            
            return ((node.dy ^ dx) & 0x80000000) > 0;
        }

        left =  FixedPoint.mul(node.dy>>FRACBITS , dx );
        right = FixedPoint.mul( dy , node.dx>>FRACBITS );
        // back side
			
        return right >= left;			
    }


    /**
     * 
     * @param x
     * @param y
     * @param line
     * @return true if point is on Seg side
     */
    boolean R_PointOnSegSide(
        int x, int y,
        Seg	line   ) {
    
        int	lx;
        int	ly;
        int	ldx;
        int	ldy;
        int	dx;
        int	dy;
        int	left;
        int	right;

        lx = line.v1.x;
        ly = line.v1.y;

        ldx = line.v2.x - lx;
        ldy = line.v2.y - ly;

        if (ldx==0) {
            if (x <= lx) {
                return ldy > 0;
            }

            return ldy < 0;
        }
        if (ldy==0) {
            if (y <= ly) {
                return ldx < 0;
            }

            return ldx > 0;
        }

        dx = (x - lx);
        dy = (y - ly);

        // Try to quickly decide by looking at sign bits.
        if ( ((ldy ^ ldx ^ dx ^ dy)&0x80000000) > 0 ) {
            // (left is negative)            
            return ((ldy ^ dx) & 0x80000000) > 0;
        }

        left = FixedPoint.mul ( ldy>>FRACBITS , dx );
        right = FixedPoint.mul ( dy , ldx>>FRACBITS );
        
        // back side			
        return right >= left;			
    }


    /**
     * R_PointToAngle
     * To get a global angle from cartesian coordinates, the coordinates are 
     * flipped until they are in the first octant of the coordinate system, 
     * then the y (<=x) is scaled and divided by x to get a tangent (slope) 
     * value which is looked up in the tantoangle(] table.
     * 
     * @param px
     * @param py
     * @return angle
     */
    public long R_PointToAngle(int px, int py) {
        int x = px;
        int y = py;

        x -= viewx;
        y -= viewy;

        if ((x == 0) && (y == 0)) {
            return 0;
        }

        if (x >= 0) {
            
            // x >=0
            if (y >= 0) {
                //logger.warning("R_PointToAngle: x>= 0  y>=0\n");
                // y>= 0
                if (x > y) {
                    // octant 0
                    logger.config("R_PointToAngle: octant 0\n");
                    return tantoangle(SlopeDiv(y, x));
                } else {
                    // octant 1
                    logger.config("R_PointToAngle: octant 1\n");
                    return (short) (ANG90 - 1 - tantoangle(SlopeDiv(x, y)));
                }
            } else {
                //logger.warning("R_PointToAngle: x>= 0  y<0\n");
                // y<0
                y = -y;

                if (x > y) {
                    // octant 8
                    logger.config("R_PointToAngle: octant 7\n");
                    return -tantoangle(SlopeDiv(y, x));
                } else {
                    // octant 7
                    logger.config("R_PointToAngle: octant 6\n");
                    return ANG270 + tantoangle(SlopeDiv(x, y));
                }
            }
        } else {
            // x<0
            x = -x;

            if (y >= 0) {
            //logger.warning("R_PointToAngle: x< 0  y>=0\n");
                // y>= 0
                if (x > y) {
                    // octant 3
                    logger.config("R_PointToAngle: octant 3\n");
                    return ANG180 - 1 - tantoangle(SlopeDiv(y, x));
                } else {
                    // octant 2
                    logger.config("R_PointToAngle: octant 2\n");
                    return ANG90 + tantoangle(SlopeDiv(x, y));
                }
            } else {
                // y<0
                //logger.warning("R_PointToAngle: x< 0  y<0\n");
                y = -y;

                if (x > y) {
                    // octant 4
                    logger.config("R_PointToAngle: octant 4\n");
                    return ANG180 + tantoangle(SlopeDiv(y, x));
                } else {
                    // octant 5
                    logger.config("R_PointToAngle: octant 5\n");
                    return ANG270 - 1 - tantoangle(SlopeDiv(x, y));
                }
            }
        }
        //return 0;    //Unreacahble
    }


    public long R_PointToAngle2(int x1, int y1, int x2, int y2) {
        viewx = x1;
        viewy = y1;

        return R_PointToAngle(x2, y2);
    }


    int R_PointToDist( int x,int y ) {
        long	angle;
        int	dx;
        int	dy;
        int	temp;
        int	dist;

        dx = Math.abs(x - viewx);
        dy = Math.abs(y - viewy);

        if (dy>dx)
        {
            temp = dx;
            dx = dy;
            dy = temp;
        }

        angle = (tantoangle( FixedPoint.div(dy,dx)>>DBITS )+ANG90) >> ANGLETOFINESHIFT;

        // use as cosine
        dist = FixedPoint.div (dx, finesine(angle) );	

        return dist;
    }


/******    NOT USED 
    //
    // R_InitPointToAngle
    //
    void R_InitPointToAngle () {
        // UNUSED - now getting from tables.c
    #if 0
        int	i;
        long	t;
        float	f;
    //
    // slope (tangent) to angle lookup
    //
        for (i=0 ; i<=SLOPERANGE ; i++)
        {
            f = atan( (float)i/SLOPERANGE )/(3.141592657*2);
            t = 0xffffffff*f;
            tantoangle(i] = t;
        }
    #endif
    }
*/


    //
    // R_ScaleFromGlobalAngle
    // Returns the texture mapping scale
    //  for the current line (horizontal span)
    //  at the given angle.
    // rw_distance must be calculated first.
    //
    int R_ScaleFromGlobalAngle (long visangle){
        int scale;
        long anglea;
        long angleb;
        int sinea;
        int sineb;
        int num;
        int den;

// UNUSED
//    {
//        int		dist;
//        int		z;
//        int		sinv;
//        int		cosv;
//
//        sinv = finesine[(visangle-rw_normalangle)>>ANGLETOFINESHIFT];	
//        dist = FixedPoint.div (rw_distance, sinv);
//        cosv = finecosine[(viewangle-visangle)>>ANGLETOFINESHIFT];
//        z = abs(FixedPoint.mul (dist, cosv));
//        scale = FixedPoint.div(projection, z);
//        return scale;
//    }

        
        anglea = ANG90 + (visangle-viewangle);
        angleb = ANG90 + (visangle-Segs.getInstance().rw_normalangle);

        // both sines are allways positive
        sinea = finesine(anglea>>ANGLETOFINESHIFT);	
        sineb = finesine(angleb>>ANGLETOFINESHIFT);
        num = FixedPoint.mul(projection,sineb)<<(detailshift?1:0);
        den = FixedPoint.mul(Segs.getInstance().rw_distance,sinea);

        if (den > num>>16) {
            scale = FixedPoint.div (num, den);

            if (scale > 64*FRACUNIT) {
                scale = 64*FRACUNIT;
            } else if (scale < 256) {
                scale = 256;
            }
        } else {
            scale = 64*FRACUNIT;
        }

        return scale;
    }



//    //
//    // R_InitTables
//    //
//    void R_InitTables (void)
//    {
//        // UNUSED: now getting from tables.c
//    #if 0
//        int		i;
//        float	a;
//        float	fv;
//        int		t;
//
//        // viewangle tangent table
//        for (i=0 ; i<FINEANGLES/2 ; i++)
//        {
//            a = (i-FINEANGLES/4+0.5)*PI*2/FINEANGLES;
//            fv = FRACUNIT*tan (a);
//            t = fv;
//            finetangent(i) = t;
//        }
//
//        // finesine table
//        for (i=0 ; i<5*FINEANGLES/4 ; i++)
//        {
//            // OPTIMIZE: mirror...
//            a = (i+0.5)*PI*2/FINEANGLES;
//            t = FRACUNIT*sin (a);
//            finesine[i] = t;
//        }
//    #endif
//
//    }



    //
    // R_InitTextureMapping
    //
    void R_InitTextureMapping () {
        int			i;
        int			x;
        int			t;
        int		focallength;

        //Stats stats = Stats.getInstance();
        
        // Use tangent table to generate viewangletox:
        //  viewangletox will give the next greatest x
        //  after the view angle.
        //
        // Calc focallength
        //  so FIELDOFVIEW angles covers SCREENWIDTH.
        focallength = FixedPoint.div (centerxfrac,
                                finetangent(FINEANGLES/4+FIELDOFVIEW/2) );

        for (i=0 ; i<FINEANGLES/2 ; i++) {
            int finetangent = finetangent(i);
            if (finetangent > FRACUNIT*2) {
                t = -1;
            } else if (finetangent < -FRACUNIT*2) {
                t = game.renderer.draw.viewwidth+1;
            } else {
                t = FixedPoint.mul (finetangent, focallength);
                t = (centerxfrac - t+FRACUNIT-1)>>FRACBITS;

                if (t < -1) {
                    t = -1;
                } else if (t>game.renderer.draw.viewwidth+1) {
                    t = game.renderer.draw.viewwidth+1;
                }
            }
            viewangletox[i] = t;
        }

        // Scan viewangletox[] to generate xtoviewangle[]:
        //  xtoviewangle will give the smallest view angle
        //  that maps to x.	
        for (x=0;x<=game.renderer.draw.viewwidth;x++) {
            i = 0;
            while (viewangletox[i]>x) {
                i++;
            }
            xtoviewangle[x] = (i<<ANGLETOFINESHIFT)-ANG90;
        }

        // Take out the fencepost cases from viewangletox.
        for (i=0 ; i<FINEANGLES/2 ; i++) {
// Aperently unused
//            t = FixedPoint.mul (finetangent(i), focallength);
//            t = centerx - t;

            if (viewangletox[i] == -1) {
                viewangletox[i] = 0;
            } else if (viewangletox[i] == game.renderer.draw.viewwidth+1) {
                viewangletox[i]  = game.renderer.draw.viewwidth;
            }
        }

        clipangle = xtoviewangle[0];
    }


    //
    // R_InitLightTables
    // Only inits the zlight table,
    //  because the scalelight table changes with view size.
    //
    public static final int  DISTMAP = 2;

    void R_InitLightTables () {
        int		i;
        int		j;
        int		level;
        int		startmap; 	
        int		scale;

        // Calculate the light levels to use
        //  for each level / distance combination.
        for (i=0 ; i< LIGHTLEVELS ; i++) {
            startmap = ((LIGHTLEVELS-1-i)*2)*NUMCOLORMAPS/LIGHTLEVELS;
            for (j=0 ; j<MAXLIGHTZ ; j++) {
                scale = FixedPoint.div ((SCREENWIDTH/2*FRACUNIT), (j+1)<<LIGHTZSHIFT);
                scale >>= LIGHTSCALESHIFT;
                level = startmap - scale/DISTMAP;

                if (level < 0) {
                    level = 0;
                }

                if (level >= NUMCOLORMAPS) {
                    level = NUMCOLORMAPS-1;
                }

                zlight[i][j] = data.colormaps[level];
            }
        }
    }



    //
    // R_SetViewSize
    // Do not really change anything here,
    //  because it might be in the middle of a refresh.
    // The change will take effect next refresh.
    //
    public boolean	setsizeneeded;
    public int		setblocks;
    public int		setdetail;


    public void R_SetViewSize ( int blocks, boolean detail ) {
        setsizeneeded = true;
        setblocks = blocks;
        setdetail = detail?1:0;
    }


    //
    // R_ExecuteSetViewSize
    //
    public void R_ExecuteSetViewSize () {
        int cosadj;
        int dy;
        int i;
        int j;
        int level;
        int startmap;
        
        setsizeneeded = false;

        if (setblocks == 11){
            game.renderer.draw.scaledviewwidth = SCREENWIDTH;
            game.renderer.draw.viewheight = SCREENHEIGHT;
        } else {
            game.renderer.draw.scaledviewwidth = setblocks*32;
            game.renderer.draw.viewheight = (setblocks*168/10)&~7;
        }

        detailshift = setdetail>0;
        game.renderer.draw.viewwidth = game.renderer.draw.scaledviewwidth>>setdetail;

        centery = game.renderer.draw.viewheight/2;
        centerx = game.renderer.draw.viewwidth/2;
        centerxfrac = centerx<<FRACBITS;
        centeryfrac = centery<<FRACBITS;
        projection = centerxfrac;

        if (!detailshift) {
            basecolfunc = new ColFunc_DrawColumn();
            colfunc = basecolfunc;
            fuzzcolfunc = new ColFunc_DrawFuzzColumn();
            transcolfunc = new ColFunc_DrawTranslatedColumn();  // apparently not used?
            //spanfunc = R_DrawSpan;
        } else {
            basecolfunc = new ColFunc_DrawColumnLow();
            colfunc = basecolfunc;
            fuzzcolfunc = new ColFunc_DrawFuzzColumn();
            transcolfunc = new ColFunc_DrawTranslatedColumn();  // apparently not used?
            //spanfunc = R_DrawSpanLow;
        }

        draw.R_InitBuffer (draw.scaledviewwidth, draw.viewheight);

        R_InitTextureMapping ();
        int viewwidth   = draw.viewwidth;
        int viewheight  = draw.viewheight;
        
        // psprite scales
        game.things.pspritescale = FRACUNIT*viewwidth/SCREENWIDTH;
        game.things.pspriteiscale = FRACUNIT*SCREENWIDTH/viewwidth;

        // thing clipping
        for (i=0 ; i<viewwidth ; i++) {
            game.things.screenheightarray[i] = viewheight;
        }

        // planes
        for (i=0 ; i<viewheight ; i++) {
            dy = ((i-viewheight/2)<<FRACBITS)+FRACUNIT/2;
            dy = Math.abs(dy);
            plane.yslope[i] = FixedPoint.div( (viewwidth<<setdetail)/2*FRACUNIT, dy);
        }

        for (i=0 ; i<viewwidth ; i++) {
            cosadj = Math.abs(finecosine(xtoviewangle[i]>>ANGLETOFINESHIFT));
            plane.distscale[i] = FixedPoint.div(FRACUNIT,cosadj);
        }

        // Calculate the light levels to use
        //  for each level / scale combination.
        for (i=0 ; i< LIGHTLEVELS ; i++) {
            startmap = ((LIGHTLEVELS-1-i)*2)*NUMCOLORMAPS/LIGHTLEVELS;
            for (j=0 ; j<MAXLIGHTSCALE ; j++)
            {
                level = startmap - j*SCREENWIDTH/(viewwidth<<setdetail)/DISTMAP;

                if (level < 0) {
                    level = 0;
                }

                if (level >= NUMCOLORMAPS) {
                    level = NUMCOLORMAPS-1;
                }

                scalelight[i][j] = data.colormaps[level];
            }
        }
    }



    //
    // R_PointInSubsector
    //
    public SubSector R_PointInSubsector(
            int x,
            int y               ) {
        Node node;
        int side;
        int nodenum;

        // single subsector is a special case
        if (game.playerSetup.nodes.length==0) {
            return game.playerSetup.subsectors[0];
        }

        nodenum = game.playerSetup.nodes.length-1;

        while ( (nodenum & NF_SUBSECTOR)==0 ) {
            node = game.playerSetup.nodes[nodenum];
            side = R_PointOnSide (x, y, node)?1:0;
            nodenum = node.children[side];
        }

        return game.playerSetup.subsectors[(nodenum&0xFFFF) & ~NF_SUBSECTOR];
    }





    //
    // R_SetupFrame
    //
    void R_SetupFrame (Player player)
    {		
        int		i;

        viewplayer = player;
        viewx = player.mo.x;
        viewy = player.mo.y;
        viewangle = player.mo.angle + viewangleoffset;
        extralight = player.extralight;

        viewz = player.viewz;

        viewsin = finesine(viewangle>>ANGLETOFINESHIFT);
        viewcos = finecosine(viewangle>>ANGLETOFINESHIFT);

        sscount = 0;

        if (player.fixedcolormap>0) {
            fixedcolormap = data.colormaps[player.fixedcolormap];

            Segs.getInstance().walllights = scalelightfixed;

            for (i=0 ; i<MAXLIGHTSCALE ; i++) {
                scalelightfixed[i] = fixedcolormap;
            }
        } else {
            fixedcolormap = null;
        }

        framecount++;
        validcount++;
    }


    //
    // R_RenderView
    //
    public void R_RenderPlayerView (Player player) {	
        R_SetupFrame (player);

        // Clear buffers.
        bsp.R_ClearClipSegs ();
        bsp.R_ClearDrawSegs ();
        plane.R_ClearPlanes ();
        game.things.R_ClearSprites ();

        // check for new console commands.
        game.net.NetUpdate ();

        // The head node is the last node output.
        //bsp.R_RenderBSPNode (numnodes-1);
        bsp.R_RenderBSPNode (game.playerSetup.nodes.length-1);

        // Check for new console commands.
        game.net.NetUpdate ();

        plane.R_DrawPlanes ();

        // Check for new console commands.
        game.net.NetUpdate ();

        game.things.R_DrawMasked ();

        // Check for new console commands.
        game.net.NetUpdate ();				
    }
    
    /**
     * Called whenever the view size changes.
     */
    void R_InitSkyMap () {
        skyflatnum = game.wad.getFlats().getNumForName(SKYFLATNAME);
        skytexturemid = 100*FRACUNIT;
    }

    
}
