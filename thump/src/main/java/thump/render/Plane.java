/*
 * Plane
 */
package thump.render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import thump.game.Game;
import static thump.global.Defines.SCREENHEIGHT;
import static thump.global.Defines.SCREENWIDTH;
import static thump.global.Defines.logger;
import thump.global.FixedPoint;
import thump.global.SystemInterface;
import static thump.global.Tables.ANG90;
import static thump.global.Tables.ANGLETOFINESHIFT;
import static thump.global.Tables.finecosine;
import static thump.global.Tables.finesine;
import static thump.render.Renderer.ANGLETOSKYSHIFT;
import static thump.render.Renderer.LIGHTLEVELS;
import static thump.render.Renderer.LIGHTSEGSHIFT;
import static thump.render.Renderer.LIGHTZSHIFT;
import static thump.render.Renderer.MAXLIGHTZ;
import static thump.render.Renderer.NUMCOLORMAPS;

/**
 *
 * @author mark
 */
public class Plane {
    // Apparently not used anywhere:
//    planefunction_t		floorfunc;
//    planefunction_t		ceilingfunc;

    //
    // opening
    //

    // Here comes the obnoxious "visplane".
    public static final int     MAXVISPLANES = 128;
    //Visplane    visplanes[]     = new Visplane[MAXVISPLANES];
    ArrayList<Visplane> visplanes = new ArrayList<>();
    //Visplane    lastvisplane;
    
    public Visplane    floorplane;
    public Visplane    ceilingplane;

    // ?
    public static final int     MAXOPENINGS = SCREENWIDTH * 64;
    int         openings[]      = new int[MAXOPENINGS];
    int       lastopening;


    //
    // Clip values are the solid pixel bounding the range.
    //  floorclip starts out SCREENHEIGHT
    //  ceilingclip starts out -1
    //
    public int     floorclip[]     = new int[SCREENWIDTH];
    public int     ceilingclip[]   = new int[SCREENWIDTH];

    //
    // spanstart holds the start of a plane span
    // initialized to 0 at start
    //
    int     spanstart[]     = new int[SCREENHEIGHT];
    int     spanstop[]      = new int[SCREENHEIGHT];

    //
    // texture mapping
    //
    byte        planezlight[][]   = new byte[MAXLIGHTZ][NUMCOLORMAPS];
    int         planeheight;

    int         yslope[]            = new int[SCREENHEIGHT];
    int         distscale[]         = new int[SCREENWIDTH];
    int         basexscale;
    int         baseyscale;

    int         cachedheight[]      = new int[SCREENHEIGHT];
    int         cacheddistance[]    = new int[SCREENHEIGHT];
    int         cachedxstep[]       = new int[SCREENHEIGHT];
    int         cachedystep[]       = new int[SCREENHEIGHT];
    
    private final Renderer renderer;

    public Plane( Renderer renderer ) {
        this.renderer = renderer;
    }


    

    //
    // R_InitPlanes
    // Only at game startup.
    //
    void R_InitPlanes () {
      // Doh!
    }


    //
    // R_MapPlane
    //
    // Uses global vars:
    //  planeheight
    //  ds_source
    //  basexscale
    //  baseyscale
    //  viewx
    //  viewy
    //
    // BASIC PRIMITIVE
    //
    void R_MapPlane(
            int y,
            int x1,
            int x2) {
        int angle;
        int distance;
        int length;

//    #ifdef RANGECHECK
//        if (x2 < x1
//            || x1<0
//            || x2>=viewwidth
//            || (unsigned)y>viewheight)
//        {
//            I_Error ("R_MapPlane: %i, %i at %i",x1,x2,y);
//        }
//    #endif
        Draw draw = renderer.draw;
        
        if (planeheight != cachedheight[y]) {
            cachedheight[y] = planeheight;
            cacheddistance[y] = FixedPoint.mul (planeheight, yslope[y]);
            distance = cacheddistance[y];
            cachedxstep[y] = FixedPoint.mul (distance,basexscale);
            draw.ds_xstep = cachedxstep[y];
            cachedystep[y] = FixedPoint.mul (distance,baseyscale);
            draw.ds_ystep = cachedystep[y];
        } else {
            distance = cacheddistance[y];
            draw.ds_xstep = cachedxstep[y];
            draw.ds_ystep = cachedystep[y];
        }

        length = FixedPoint.mul (distance,distscale[x1]);
        angle = (int)((renderer.viewangle + renderer.xtoviewangle[x1])>>ANGLETOFINESHIFT);
        draw.ds_xfrac = renderer.viewx + FixedPoint.mul(finecosine(angle), length);
        draw.ds_yfrac = -renderer.viewy - FixedPoint.mul(finesine(angle), length);

        if (renderer.fixedcolormap!=null) {
            draw.ds_colormap = renderer.fixedcolormap;
        } else {
            int index = distance >> LIGHTZSHIFT;

            if (index >= MAXLIGHTZ ) {
                index = MAXLIGHTZ-1;
            }

            draw.ds_colormap = planezlight[index];
        }

        draw.ds_y = y;
        draw.ds_x1 = x1;
        draw.ds_x2 = x2;

        // high or low detail
        Game.getInstance().renderer.draw.spanfunc();	
    }


    //
    // R_ClearPlanes
    // At begining of frame.
    //
    void R_ClearPlanes () {
        int	angle;

        // opening / clipping determination
        for (int i=0 ; i<Game.getInstance().renderer.draw.viewwidth ; i++) {
            floorclip[i] = Game.getInstance().renderer.draw.viewheight;
            ceilingclip[i] = -1;
        }

        //lastvisplane = visplanes[0];
        visplanes.clear();
        
        //lastopening = openings;
        lastopening = 0;
        
        // texture calculation
        //memset (cachedheight, 0, sizeof(cachedheight));
        Arrays.fill(cachedheight, 0);

        // left to right mapping
        angle = (int)((Game.getInstance().renderer.viewangle-ANG90)>>ANGLETOFINESHIFT);

        // scale will be unit scale at SCREENWIDTH/2 distance
        basexscale = FixedPoint.div (finecosine(angle),Game.getInstance().renderer.centerxfrac);
        baseyscale = -FixedPoint.div (finesine(angle),Game.getInstance().renderer.centerxfrac);
    }




    //
    // R_FindPlane
    //
    public Visplane R_FindPlane ( int _height, int picnum, int _lightlevel ) {
        Visplane	check = null;
        int height = _height;
        int lightlevel = _lightlevel;

        if (picnum == Game.getInstance().renderer.skyflatnum) {
            height = 0;			// all skys map together
            lightlevel = 0;
        }

//        for (check=visplanes; check<lastvisplane; check++) {
//            if (height == check.height
//                && picnum == check.picnum
//                && lightlevel == check.lightlevel)
//            {
//                break;
//            }
//        }

        for (Visplane visplane : visplanes) {
            if (height == visplane.height
                && picnum == visplane.picnum
                && lightlevel == visplane.lightlevel)
            {
                check = visplane;
                break;
            }            
        }


//        if (check < lastvisplane) {
//            return check;
//        }
        if ( check != null ) {
            return check;
        }

//        if (lastvisplane - visplanes == MAXVISPLANES) {
//            SystemInterface.I_Error ("R_FindPlane: no more visplanes");
//        }
        if ( visplanes.size() >= MAXVISPLANES ) {
            SystemInterface.I_Error ("R_FindPlane: no more visplanes");
        }

        
        //lastvisplane++;
        check = new Visplane();
        check.height = height;
        check.picnum = picnum;
        check.lightlevel = lightlevel;
        check.minx = SCREENWIDTH;
        check.maxx = -1;
        Arrays.fill(check.top, (byte)0xff);
        
        visplanes.add(check);

        //memset (check.top,0xff,sizeof(check.top));

        return check;
    }


    //
    // R_CheckPlane
    //
    Visplane R_CheckPlane(Visplane pl, int start, int stop) {
        int intrl;
        int intrh;
        int unionl;
        int unionh;
        int x;

        if (start < pl.minx) {
            intrl = pl.minx;
            unionl = start;
        } else {
            unionl = pl.minx;
            intrl = start;
        }

        if (stop > pl.maxx) {
            intrh = pl.maxx;
            unionh = stop;
        } else {
            unionh = pl.maxx;
            intrh = stop;
        }

        for (x = intrl; x <= intrh; x++) {
            if (pl.top[x] != 0xff) {
                break;
            }
        }

        if (x > intrh) {
            pl.minx = unionl;
            pl.maxx = unionh;

            // use the same one
            return pl;
        }

        Visplane vp = new Visplane();
        // make a new visplane
        vp.height = pl.height;
        vp.picnum = pl.picnum;
        vp.lightlevel = pl.lightlevel;

        //pl = vp;
        visplanes.add(vp);
        
//        pl.minx = start;
//        pl.maxx = stop;
        vp.minx = start;
        vp.maxx = stop;

        //memset (pl.top,0xff,sizeof(pl.top));
        Arrays.fill(vp.top, (byte)0xff);

        //return pl;
        return vp;
    }


    /**
     * Make the spans
     * 
     * @param x
     * @param _t1
     * @param _b1
     * @param _t2
     * @param _b2 
     */
    void R_MakeSpans(int x, int _t1, int _b1, int _t2, int _b2) {
        int t1 = _t1;
        int b1 = _b1;
        int t2 = _t2;
        int b2 = _b2;
        
        while (t1 < t2 && t1 <= b1) {
            R_MapPlane(t1, spanstart[t1], x - 1);
            t1++;
        }
        while (b1 > b2 && b1 >= t1) {
            R_MapPlane(b1, spanstart[b1], x - 1);
            b1--;
        }

        while (t2 < t1 && t2 <= b2) {
            spanstart[t2] = x;
            t2++;
        }
        while (b2 > b1 && b2 >= t2) {
            spanstart[b2] = x;
            b2--;
        }
    }



    //
    // R_DrawPlanes
    // At the end of each frame.
    //
    void R_DrawPlanes() {
        //Visplane pl;
        int light;
        int x;
        int stop;
        int angle;
        
//    #ifdef RANGECHECK
//        if (ds_p - drawsegs > MAXDRAWSEGS)
//            I_Error ("R_DrawPlanes: drawsegs overflow (%i)",
//                     ds_p - drawsegs);
//
//        if (lastvisplane - visplanes > MAXVISPLANES)
//            I_Error ("R_DrawPlanes: visplane overflow (%i)",
//                     lastvisplane - visplanes);
//
//        if (lastopening - openings > MAXOPENINGS)
//            I_Error ("R_DrawPlanes: opening overflow (%i)",
//                     lastopening - openings);
//    #endif

        logger.log(Level.CONFIG, "Plane.R_DrawPlanes()\n");
        
        //Renderer renderer = Game.getInstance().renderer;
        Things  things = Game.getInstance().things;
        Draw draw = renderer.draw;
        
        //for (pl = visplanes ; pl < lastvisplane ; pl++) {
        for (Visplane pl : visplanes) {
            if (pl.minx > pl.maxx) {
                continue;
            }

            

            // sky flat
            if (pl.picnum == renderer.skyflatnum) {
                draw.dc_iscale = things.pspriteiscale>>(renderer.detailshift?1:0);

                // Sky is allways drawn full bright,
                //  i.e. colormaps[0] is used.
                // Because of this hack, sky is not affected
                //  by INVUL inverse mapping.
                draw.dc_colormap = renderer.data.colormaps[0];
                draw.dc_texturemid = renderer.skytexturemid;
                for (x=pl.minx ; x <= pl.maxx ; x++) {
                    draw.dc_yl = pl.top[x];
                    draw.dc_yh = pl.bottom[x];

                    if (draw.dc_yl <= draw.dc_yh) {
                        angle = (int)((renderer.viewangle + renderer.xtoviewangle[x])>>ANGLETOSKYSHIFT);
                        draw.dc_x = x;
                        //draw.dc_source = renderer.data.R_GetColumn(renderer.skytexture, angle);
                        // porbably more like draw.dc_source = renderer.skytexture.getColumn(angle);   ?
                        draw.dc_source = renderer.skytexture.getColumn(angle);
                        Game.getInstance().renderer.colfunc.doColFunc(Game.getInstance());
                    }
                }
                continue;
            }

            // regular flat
            // something like wad.getFlat( num ); ?
//            draw.ds_source = W_CacheLumpNum(firstflat +
//                                       flattranslation[pl.picnum],
//                                       PU_STATIC);
            //draw.ds_source = Game.getInstance().wad.getFlats().flats.get(flattranslation[pl.picnum]).pixels;
            draw.ds_source = Game.getInstance().wad.getFlats().flats.get(pl.picnum).pixels;

            planeheight = Math.abs(pl.height-renderer.viewz);
            light = (pl.lightlevel >> LIGHTSEGSHIFT)+renderer.extralight;

            if (light >= LIGHTLEVELS) {
                light = LIGHTLEVELS-1;
            }

            if (light < 0) {
                light = 0;
            }

            planezlight = renderer.zlight[light];

            // Debug
            if ( pl.minx ==0 ) {
                int i=0; // breakpoint here.
                pl.minx = 1;
            }
            pl.top[pl.maxx+1] = (byte) 0xff;
            pl.top[pl.minx-1] = (byte) 0xff;

            stop = pl.maxx + 1;

            for (x=pl.minx ; x<= stop ; x++) {
                R_MakeSpans(x,pl.top[x-1],
                            pl.bottom[x-1],
                            pl.top[x],
                            pl.bottom[x]);
            }

            //Z_ChangeTag (ds_source, PU_CACHE);
        }
    }

}
