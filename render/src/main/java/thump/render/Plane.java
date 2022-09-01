/*
 * Plane
 */
package thump.render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import static thump.base.Defines.SCREENHEIGHT;
import static thump.base.Defines.SCREENWIDTH;
import static thump.base.Defines.logger;
import thump.base.FixedPoint;
import static thump.base.Tables.ANG90;
import static thump.base.Tables.ANGLETOFINESHIFT;
import static thump.base.Tables.finecosine;
import static thump.base.Tables.finesine;
import static thump.render.Renderer.ANGLETOSKYSHIFT;
import static thump.render.Renderer.LIGHTLEVELS;
import static thump.render.Renderer.LIGHTSEGSHIFT;
import static thump.render.Renderer.LIGHTZSHIFT;
import static thump.render.Renderer.MAXLIGHTZ;
import static thump.render.Renderer.NUMCOLORMAPS;
import thump.wad.lump.FlatsLump;

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
    
        logger.log(Level.FINER, "    Plane.R_MapPlane( y:{0}, x1:{1}, x2:{2})", new Object[]{y,x1,x2});
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
        angle = ((renderer.viewangle + renderer.xtoviewangle[x1])>>ANGLETOFINESHIFT)&0xFFF;
        draw.ds_xfrac = renderer.viewx + FixedPoint.mul(finecosine(angle), length);
        draw.ds_yfrac = -renderer.viewy - FixedPoint.mul(finesine(angle), length);

        if (renderer.fixedcolormap!=null) {
            draw.ds_colormap = renderer.fixedcolormap;
        } else {
            int index = distance>>LIGHTZSHIFT;

            if (index >= MAXLIGHTZ ) {
                index = MAXLIGHTZ-1;
            }

            if ( index < 0 ) {
                int ii=0;  //debugger breakpoint
            }
            draw.ds_colormap = planezlight[index];
        }

        draw.ds_y = y;
        draw.ds_x1 = x1;
        draw.ds_x2 = x2;

        logger.log(Level.FINEST, 
                "    draw: ds_y:{0}   ds_x1:{1}   ds_x2:{2}   ds_xfrac:{3}    ds_yfrac:{4}",
                new Object[]{ draw.ds_y, draw.ds_x1, draw.ds_x2, draw.ds_xfrac, draw.ds_yfrac }
        );
        // high or low detail
        draw.spanfunc();	
    }


    //
    // R_ClearPlanes
    // At begining of frame.
    //
    void R_ClearPlanes () {
        int	angle;

        // opening / clipping determination
        for (int i=0 ; i<renderer.draw.viewwidth ; i++) {
            floorclip[i] = renderer.draw.viewheight;
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
        //angle = (int)(((renderer.viewangle-ANG90)&0xFFFFFFFFL)>>ANGLETOFINESHIFT);
        angle = ((renderer.viewangle-ANG90)>>ANGLETOFINESHIFT)&0xFFF;

        // scale will be unit scale at SCREENWIDTH/2 distance
        basexscale = FixedPoint.div (finecosine(angle),renderer.centerxfrac);
        baseyscale = -FixedPoint.div (finesine(angle),renderer.centerxfrac);
    }




    //
    // R_FindPlane
    //
    public Visplane R_FindPlane ( int _height, int picnum, int _lightlevel ) {
        Visplane	check = null;
        int height = _height;
        int lightlevel = _lightlevel;

        if (picnum == renderer.skyflatnum) {
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

        // See if we alreqady have that visplane in our list.
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
            //SystemInterface.I_Error ("R_FindPlane: no more visplanes");
            logger.severe("R_FindPlane: no more visplanes");
        }

        
        //lastvisplane++;
        check = new Visplane();
        check.height = height;
        check.picnum = picnum;
        check.lightlevel = lightlevel;
        check.minx = SCREENWIDTH;
        //check.maxx = -1;
        //check.maxx = Integer.MAX_VALUE;
        check.maxx = 0xFF;
        
        //memset (check.top,0xff,sizeof(check.top));
        Arrays.fill(check.top, 0xff);
        
        visplanes.add(check);

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

        logger.log(Level.FINE, "R_CheckPlane(start:{0},stop:{1})", new Object[]{start, stop});
        
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
            if ( pl.minx < 0 ) {
                logger.log( Level.CONFIG, "    pl.minx was set to a negative value.");
            }
            pl.maxx = unionh;
            if ( pl.maxx < 0 ) {
                logger.log(Level.CONFIG, "    pl.maxx was set to a negative value.");
            }

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
        pl.minx = start;
        if ( pl.minx < 0 ) {
            logger.log(Level.CONFIG, "    pl.minx was set to a negative value.");            
        }
//        pl.maxx = stop;
        pl.maxx = stop;
        if ( pl.maxx < 0 ) {
            logger.log(Level.CONFIG, "    pl.maxx was set to a negative value.");
        }

        vp.minx = start;
        if ( vp.minx < 0 ) {
            logger.log(Level.CONFIG, "    vp.minx was set to a negative value.");
        }
        vp.maxx = stop;
        if ( vp.maxx < 0 ) {
            logger.log(Level.CONFIG, "    vp.maxx was set to a negative value.");
        }

        //memset (pl.top,0xff,sizeof(pl.top));
        Arrays.fill(vp.top, 0xff);

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
        
        logger.log(Level.FINEST, 
                "R_MakeSpans(x:{0}, t1:{1}, b1:{2}, t2:{3}, b2:{4}",
                new Object[]{x,_t1,_b1,_t2,_b2}
        );
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
        //logger.log(Level.CONFIG, "    spanstart[]:  {0}", Arrays.toString(spanstart));
    }



    //
    // R_DrawPlanes
    // At the end of each frame.
    //
    public void R_DrawPlanes(FlatsLump flats, int pspriteiscale) {
        //Visplane pl;
        int light;
        int x=0;
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

        logger.log(Level.CONFIG, "Plane.R_DrawPlanes():  visplanes.size={0}", visplanes.size());
        
        Draw draw = renderer.draw;
        
        //for (pl = visplanes ; pl < lastvisplane ; pl++) {
        for (Visplane pl : visplanes) {
            logger.log(Level.CONFIG, "process visplane. pl.picnum = {0}", pl.picnum);
            if (pl.minx > pl.maxx) {
               logger.log(Level.CONFIG, "    visplane: minx > maxx [{0}]>[{1}]  ...Next visplane.", new Object[]{pl.minx, pl.maxx});
               continue;
            }

            // sky flat
            if (pl.picnum == renderer.skyflatnum) {
                logger.log(Level.CONFIG, "    Draw a skyflat.");
                //draw.dc_iscale = renderer.things.pspriteiscale>>(renderer.detailshift?1:0);
                draw.dc_iscale = pspriteiscale>>(renderer.detailshift?1:0);

                // Sky is allways drawn full bright,
                //  i.e. colormaps[0] is used.
                // Because of this hack, sky is not affected
                //  by INVUL inverse mapping.
                draw.dc_colormap = renderer.data.colormaps[0];
                draw.dc_texturemid = renderer.skytexturemid;
                for (x=pl.minx ; x <= pl.maxx ; x++) {
//                    draw.dc_yl = pl.top[x];
//                    draw.dc_yh = pl.bottom[x];
                    draw.dc_yl = pl.top[x+1]; /*&0xFF; */
                    draw.dc_yh = pl.bottom[x+1];/* &0xFF; */
                    logger.log(Level.CONFIG, "    pl.top/bottom[{0}]   draw.dc_yl={1}    draw.dc_yh={2}", new Object[]{x+1,draw.dc_yl, draw.dc_yh});
                    if (draw.dc_yl <= draw.dc_yh) {
                        // TODO:  See if xtoviewangle is similar to top[] and needs padding.
                        //angle = (int)((renderer.viewangle + renderer.xtoviewangle[x])>>ANGLETOSKYSHIFT);
                        angle = ((renderer.viewangle + renderer.xtoviewangle[x])>>ANGLETOSKYSHIFT)&0x3FF;
                        draw.dc_x = x;   //   x+1 ????
                        //draw.dc_source = renderer.data.R_GetColumn(renderer.skytexture, angle);
                        // porbably more like draw.dc_source = renderer.skytexture.getColumn(angle);   ?
                        draw.dc_source = renderer.skytexture.getColumn(angle);
                        renderer.colfunc.doColFunc(draw);
                    }
                }
                //continue;
            }

            // regular flat
            // something like wad.getFlat( num ); ?
//            draw.ds_source = W_CacheLumpNum(firstflat +
//                                       flattranslation[pl.picnum],
//                                       PU_STATIC);
            //draw.ds_source = Game.getInstance().wad.getFlats().flats.get(flattranslation[pl.picnum]).pixels;
            //draw.ds_source = Game.getInstance().wad.getFlats().flats.get(pl.picnum).pixels;
            draw.ds_source = flats.get(pl.picnum).pixels;

            planeheight = Math.abs(pl.height-renderer.viewz);
            light = (pl.lightlevel >> LIGHTSEGSHIFT)+renderer.extralight;

            if (light >= LIGHTLEVELS) {
                light = LIGHTLEVELS-1;
            }

            if (light < 0) {
                light = 0;
            }

            planezlight = renderer.zlight[light];
            
            // MJK:  minx and maxx come in at zero or max sometimes.  
            //if ( pl.minx <= 0 ) {
            //    pl.minx = 1;
            //}
            //if ( pl.maxx > pl.top.length-1 ) {
            //    pl.maxx = pl.top.length-1;
            //}
            
//            pl.top[pl.maxx+1] = (byte) 0xff;
//            pl.top[pl.minx-1] = (byte) 0xff;
            //pl.top[pl.maxx+2] = 0xff;
            //pl.top[pl.minx] = 0xff;
            pl.top[pl.maxx+2] = 0xff;
            pl.top[pl.minx] = 0xff;

            stop = pl.maxx + 1;

//            try {
            //for (x=pl.minx ; x<= stop ; x++) {
            for (x=pl.minx ; x < stop ; x++) {
//                R_MakeSpans(x,
//                        pl.top[x-1],
//                        pl.bottom[x-1],
//                        pl.top[x],
//                        pl.bottom[x]);
//            }
    if ( x == 250 ) {
        int i=0; // breakpoint
    }
                R_MakeSpans(x,
                        pl.top[x],
                        pl.bottom[x],
                        //pl.top[x-1],
                        //pl.bottom[x-1],
                        //pl.top[x+1],
                        //pl.bottom[x+1]);
                        pl.top[x+1],
                        pl.bottom[x+1]);
            }
//            } catch ( ArrayIndexOutOfBoundsException e) {
//                int i=0;  // Breakpoint.
//            }

            //Z_ChangeTag (ds_source, PU_CACHE);
        }
    }

}
