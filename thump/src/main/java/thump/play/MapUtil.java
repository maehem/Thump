/*
 * Movement/collision utility functions,
 * as used by function in p_map.c. 
 * BLOCKMAP Iterator functions,
 * and some PIT_* functions to use for iteration.
 */
package thump.play;

import thump.game.Game;
import thump.game.PlayerSetup;
import thump.global.FixedPoint;
import static thump.global.FixedPoint.FRACBITS;
import static thump.global.FixedPoint.FRACUNIT;
import thump.maplevel.MapObject;
import static thump.maplevel.MapObject.MobileObjectFlag.MF_NOBLOCKMAP;
import static thump.maplevel.MapObject.MobileObjectFlag.MF_NOSECTOR;
import static thump.play.Intercept.MAXINTERCEPTS;
import static thump.play.Local.MAPBLOCKSHIFT;
import static thump.play.Local.MAPBLOCKSIZE;
import static thump.play.Local.MAPBTOFRAC;
import thump.play.PITfuncs.PIT_AddLineIntercepts;
import thump.play.PITfuncs.PIT_AddThingIntercepts;
import thump.render.BoundingBox;
import thump.render.Line;
import thump.render.Sector;
import thump.render.SubSector;

/**
 *
 * @author mark
 */
public class MapUtil {

    private final Game game;

    public MapUtil(Game game) {
        this.game = game;
    }
    
    public static final int PT_ADDLINES     = 1;
    public static final int PT_ADDTHINGS    = 2;
    public static final int PT_EARLYOUT     = 4;

    //
    // P_AproxDistance
    // Gives an estimation of distance (not exact)
    //
    public int P_AproxDistance( int	_dx, int	_dy ) {
        int dx = Math.abs(_dx);
        int dy = Math.abs(_dy);
        if (dx < dy) {
            return dx+dy-(dx>>1);
        }
        return dx+dy-(dy>>1);
    }


    //
    // P_PointOnLineSide
    // Returns 0 or 1
    //
    public boolean P_PointOnLineSide(int x, int y, Line line) {
        int dx;
        int dy;
        int left;
        int right;

        if (0==line.dx) {
            if (x <= line.v1.x) {
                return line.dy> 0;
            }

            return line.dy < 0;
        }
        
        if (0==line.dy) {
            if (y <= line.v1.y) {
                return line.dx < 0;
            }

            return line.dx > 0;
        }

        dx = (x - line.v1.x);
        dy = (y - line.v1.y);

        left = FixedPoint.mul(line.dy >> FRACBITS, dx);
        right = FixedPoint.mul(dy, line.dx >> FRACBITS);
        // back side
        
        return right >= left;			
    }



    //
    // P_BoxOnLineSide
    // Considers the line to be infinite
    // Returns side 0 or 1, -1 if box crosses the line.
    //
    public int P_BoxOnLineSide(BoundingBox tmbox, Line ld) {
        boolean p1=false;
        boolean p2=false;

        switch (ld.slopetype) {
            case ST_HORIZONTAL:
                p1 = tmbox.top > ld.v1.y;
                p2 = tmbox.bottom > ld.v1.y;
                if (ld.dx < 0) {
                    p1 = !p1;
                    p2 = !p2;
                }
                break;

            case ST_VERTICAL:
                p1 = tmbox.right < ld.v1.x;
                p2 = tmbox.left < ld.v1.x;
                if (ld.dy < 0) {
                    p1 = !p1;
                    p2 = !p2;
                }
                break;

            case ST_POSITIVE:
                p1 = P_PointOnLineSide(tmbox.left, tmbox.top, ld);
                p2 = P_PointOnLineSide(tmbox.right, tmbox.bottom, ld);
                break;

            case ST_NEGATIVE:
                p1 = P_PointOnLineSide(tmbox.right, tmbox.top, ld);
                p2 = P_PointOnLineSide(tmbox.left, tmbox.bottom, ld);
                break;
        }

        if (p1 == p2) {
            return p1?1:0;
        }
        return -1;
    }


    //
    // P_PointOnDivlineSide
    // Returns 0 or 1.
    //
    public boolean P_PointOnDivlineSide(int x, int y, DivLine line) {
        int dx;
        int dy;
        int left;
        int right;

        if (0==line.dx) {
            if (x <= line.x) {
                return line.dy > 0;
            }

            return line.dy < 0;
        }
        if (0==line.dy) {
            if (y <= line.y) {
                return line.dx < 0;
            }

            return line.dx > 0;
        }

        dx = (x - line.x);
        dy = (y - line.y);

        // try to quickly decide by looking at sign bits
        if (((line.dy ^ line.dx ^ dx ^ dy) & 0x80000000)>0) {
            // (left is negative)
            
            return ((line.dy ^ dx) & 0x80000000)>0;
        }

        left = FixedPoint.mul(line.dy >> 8, dx >> 8);
        right = FixedPoint.mul(dy >> 8, line.dx >> 8);
        
        return right >= left;			
    }



    //
    // P_MakeDivline
    //
    public void P_MakeDivline(Line li, DivLine dl) {
        dl.x = li.v1.x;
        dl.y = li.v1.y;
        dl.dx = li.dx;
        dl.dy = li.dy;
    }



    //
    // P_InterceptVector
    // Returns the fractional intercept point
    // along the first divline.
    // This is only called by the addthings
    // and addlines traversers.
    //
    public int P_InterceptVector( DivLine	v2, DivLine	v1 ) {
//    #if 1
        int	frac;
        int	num;
        int	den;

        den = FixedPoint.mul (v1.dy>>8,v2.dx) - FixedPoint.mul(v1.dx>>8,v2.dy);

        if (den == 0) {
            return 0;
            //	I_Error ("P_InterceptVector: parallel");
        }

        num =
             FixedPoint.mul ( (v1.x - v2.x)>>8 ,v1.dy )
            +FixedPoint.mul ( (v2.y - v1.y)>>8, v1.dx );

        frac = FixedPoint.div (num , den);

        return frac;
//    #else	// UNUSED, float debug.
//        float	frac;
//        float	num;
//        float	den;
//        float	v1x;
//        float	v1y;
//        float	v1dx;
//        float	v1dy;
//        float	v2x;
//        float	v2y;
//        float	v2dx;
//        float	v2dy;
//
//        v1x = (float)v1.x/FRACUNIT;
//        v1y = (float)v1.y/FRACUNIT;
//        v1dx = (float)v1.dx/FRACUNIT;
//        v1dy = (float)v1.dy/FRACUNIT;
//        v2x = (float)v2.x/FRACUNIT;
//        v2y = (float)v2.y/FRACUNIT;
//        v2dx = (float)v2.dx/FRACUNIT;
//        v2dy = (float)v2.dy/FRACUNIT;
//
//        den = v1dy*v2dx - v1dx*v2dy;
//
//        if (den == 0)
//            return 0;	// parallel
//
//        num = (v1x - v2x)*v1dy + (v2y - v1y)*v1dx;
//        frac = num / den;
//
//        return frac*FRACUNIT;
//    #endif
    }


    //
    // P_LineOpening
    // Sets opentop and openbottom to the window
    // through a two sided line.
    // OPTIMIZE: keep this precalculated
    //
    public int opentop;
    public int openbottom;
    public int openrange;
    public int lowfloor;


    public void P_LineOpening(Line linedef) {
        Sector front;
        Sector back;

        if (linedef.sidenum[1] == -1) {
            // single sided line
            openrange = 0;
            return;
        }

        front = linedef.frontsector;
        back = linedef.backsector;

        if (front.ceilingheight < back.ceilingheight) {
            opentop = front.ceilingheight;
        } else {
            opentop = back.ceilingheight;
        }

        if (front.floorheight > back.floorheight) {
            openbottom = front.floorheight;
            lowfloor = back.floorheight;
        } else {
            openbottom = back.floorheight;
            lowfloor = front.floorheight;
        }

        openrange = opentop - openbottom;
    }


    //
    // THING POSITION SETTING
    //


    //
    // P_UnsetThingPosition
    // Unlinks a thing from block map and sectors.
    // On each position change, BLOCKMAP and other
    // lookups maintaining lists ot things inside
    // these structures need to be updated.
    //
    public void P_UnsetThingPosition(MapObject thing) {
        int blockx;
        int blocky;
        
        PlayerSetup ps = Game.getInstance().playerSetup;

        if (0 == (thing.flags & MF_NOSECTOR.getValue())) {
            // inert things don't need to be in blockmap?
            // unlink from subsector
            if (thing.snext != null) {
                thing.snext.sprev = thing.sprev;
            }

            if (thing.sprev != null) {
                thing.sprev.snext = thing.snext;
            } else {
                thing.subsector.sector.thinglist = thing.snext;
            }
        }

        if (0 == (thing.flags & MF_NOBLOCKMAP.getValue())) {
            // inert things don't need to be in blockmap
            // unlink from block map
            if (thing.bnext != null) {
                thing.bnext.bprev = thing.bprev;
            }

            if (thing.bprev != null) {
                thing.bprev.bnext = thing.bnext;
            } else {
                blockx = (thing.x - ps.bmaporgx) >> MAPBLOCKSHIFT;
                blocky = (thing.y - ps.bmaporgy) >> MAPBLOCKSHIFT;

                if (blockx >= 0 && blockx < ps.bmapwidth
                        && blocky >= 0 && blocky < ps.bmapheight) {
                    ps.blocklinks[blocky * ps.bmapwidth + blockx] = thing.bnext;
                }
            }
        }
    }


    //
    // P_SetThingPosition
    // Links a thing into both a block and a subsector
    // based on it's x y.
    // Sets thing.subsector properly
    //
    public void P_SetThingPosition(MapObject thing) {
        SubSector ss;
        Sector sec;
        int blockx;
        int blocky;
        MapObject link;

        PlayerSetup ps = Game.getInstance().playerSetup;

        // link into subsector
        ss = Game.getInstance().renderer.R_PointInSubsector(thing.x, thing.y);
        thing.subsector = ss;

        if (0 == (thing.flags & MF_NOSECTOR.getValue())) {
            // invisible things don't go into the sector links
            sec = ss.sector;

            thing.sprev = null;
            thing.snext = sec.thinglist;

            if (sec.thinglist!=null) {
                sec.thinglist.sprev = thing;
            }

            sec.thinglist = thing;
        }

        // link into blockmap
        if (0==(thing.flags & MF_NOBLOCKMAP.getValue())) {
            // inert things don't need to be in blockmap		
            blockx = (thing.x - ps.bmaporgx) >> MAPBLOCKSHIFT;
            blocky = (thing.y - ps.bmaporgy) >> MAPBLOCKSHIFT;

            if (blockx >= 0
                    && blockx < ps.bmapwidth
                    && blocky >= 0
                    && blocky < ps.bmapheight) {
                link =  ps.blocklinks[blocky * ps.bmapwidth + blockx];
                thing.bprev = null;
                thing.bnext =  link;
                if ( link!=null ) {
                    link.bprev = thing;
                }

                //link = thing;
                ps.blocklinks[blocky * ps.bmapwidth + blockx] = thing;
            } else {
                // thing is off the map
                thing.bnext = null;
                thing.bprev = null;
            }
        }
    }



    //
    // BLOCK MAP ITERATORS
    // For each line/thing in the given mapblock,
    // call the passed PIT_* function.
    // If the function returns false,
    // exit with false without checking anything else.
    //


    //
    // P_BlockLinesIterator
    // The validcount flags are used to avoid checking lines
    // that are marked in multiple mapblocks,
    // so increment validcount before the first call
    // to P_BlockLinesIterator, then make one or more calls
    // to it.
    //
    public boolean P_BlockLinesIterator(int x, int y, PITfunc func) { //boolean(*func)(Line) 
        int offset;
        //int list;
        Line ld;
        PlayerSetup ps = Game.getInstance().playerSetup;

        if (x < 0
                || y < 0
                || x >= ps.bmapwidth
                || y >= ps.bmapheight) {
            return true;
        }

        //offset = y * ps.bmapwidth + x;
        offset = y + x;

        offset = ps.bmapOffsetList[offset];  // Needs rework based on original source.
        // This wants to be blockmaplump.offsetList[offset];
        Short[] list = ps.bmapList[offset];
        
        //for (list = ps.blockmaplump[offset]; list != -1; offset++) {
        for ( int i=0; i<list.length-1; i++) {
            ld = ps.lines[list[i]];

            if (ld.validcount == Game.getInstance().renderer.validcount) {
                continue; 	// line has already been checked
            }

            ld.validcount = Game.getInstance().renderer.validcount;

            if (!func.doFunc(ld)) {  // PIT_ funcs
                return false;
            }
        }
        return true;	// everything was checked
    }


    //
    // P_BlockThingsIterator
    //
    public boolean P_BlockThingsIterator(int x, int y, PITfunc func) {//boolean(*func)(MapObject)

        MapObject		mobj;
        PlayerSetup ps = Game.getInstance().playerSetup;

        if ( x<0
             || y<0
             || x>=ps.bmapwidth
             || y>=ps.bmapheight)
        {
            return true;
        }


        for (mobj = game.playerSetup.blocklinks[y*ps.bmapwidth+x] ; mobj!=null ; mobj = mobj.bnext) {
            if (!func.doFunc(mobj) ) {
                return false;
            }
        }
        return true;
    }



    //
    // INTERCEPT ROUTINES
    //
    // TODO: Make this an arrayList?sx
    public Intercept	intercepts[] = new Intercept[MAXINTERCEPTS];
    public int	intercept_p;

    public DivLine 	trace = new DivLine();
    public boolean 	earlyout;
    public int		ptflags;


    //
    // PIT_AddLineIntercepts.
    // Looks for lines in the given block
    // that intercept the given trace
    // to add to the intercepts list.
    //
    // A line is crossed if its endpoints
    // are on opposite sides of the trace.
    // Returns true if earlyout and a solid line hit.
    //
    public boolean PIT_AddLineIntercepts(Line ld) {
        boolean s1;
        boolean s2;
        int frac;
        DivLine dl = new DivLine();

        // avoid precision problems with two routines
        if (trace.dx > FRACUNIT * 16
                || trace.dy > FRACUNIT * 16
                || trace.dx < -FRACUNIT * 16
                || trace.dy < -FRACUNIT * 16) {
            s1 = P_PointOnDivlineSide(ld.v1.x, ld.v1.y, trace);
            s2 = P_PointOnDivlineSide(ld.v2.x, ld.v2.y, trace);
        } else {
            s1 = P_PointOnLineSide(trace.x, trace.y, ld);
            s2 = P_PointOnLineSide(trace.x + trace.dx, trace.y + trace.dy, ld);
        }

        if (s1 == s2) {
            return true;	// line isn't crossed
        }

        // hit the line
        P_MakeDivline(ld, dl);
        frac = P_InterceptVector(trace, dl);

        if (frac < 0) {
            return true;	// behind source
        }

        // try to early out the check
        if (earlyout
                && frac < FRACUNIT
                && null == ld.backsector) {
            return false;	// stop checking
        }

        intercepts[intercept_p] = new Intercept(frac, true, ld);
//        intercepts[intercept_p].frac = frac;
//        intercepts[intercept_p].isaline = true;
//        intercepts[intercept_p].lineThing = ld;
        intercept_p++;

        return true;	// continue
    }



    //
    // PIT_AddThingIntercepts
    //
    public boolean PIT_AddThingIntercepts(MapObject thing) {
        int x1;
        int y1;
        int x2;
        int y2;

        boolean s1;
        boolean s2;

        boolean tracepositive;

        DivLine dl = new DivLine();

        int frac;

        tracepositive = (trace.dx ^ trace.dy) > 0;

        // check a corner to corner crossection for hit
        if (tracepositive) {
            x1 = thing.x - thing.radius;
            y1 = thing.y + thing.radius;

            x2 = thing.x + thing.radius;
            y2 = thing.y - thing.radius;
        } else {
            x1 = thing.x - thing.radius;
            y1 = thing.y - thing.radius;

            x2 = thing.x + thing.radius;
            y2 = thing.y + thing.radius;
        }

        s1 = P_PointOnDivlineSide(x1, y1, trace);
        s2 = P_PointOnDivlineSide(x2, y2, trace);

        if (s1 == s2) {
            return true;		// line isn't crossed
        }

        dl.x = x1;
        dl.y = y1;
        dl.dx = x2 - x1;
        dl.dy = y2 - y1;

        frac = P_InterceptVector(trace, dl);

        if (frac < 0) {
            return true;		// behind source
        }
        intercepts[intercept_p] = new Intercept(frac, false, thing);
//        intercepts[intercept_p].frac = frac;
//        intercepts[intercept_p].isaline = false;
//        intercepts[intercept_p].lineThing = thing;
        intercept_p++;

        return true;		// keep going
    }

    //
    // P_TraverseIntercepts
    // Returns true if the traverser function returns true
    // for all lines.
    // 
    boolean
            P_TraverseIntercepts(PTRfunc func,
                    int maxfrac) {
        int count;
        int dist;
        Intercept scan;
        Intercept in;

        //count = intercept_p - intercepts;
        count = intercept_p;

        in = null;			// shut up compiler warning

        while (count > 0) {
            count--;
            dist = Integer.MAX_VALUE;
            //for (scan = intercepts ; scan<intercept_p ; scan++) {
            for (int i = 0; i < intercept_p; i++) {
                scan = intercepts[i];
                if (scan.frac < dist) {
                    dist = scan.frac;
                    in = scan;
                }
            }

            if (dist > maxfrac) {
                return true;	// checked everything in range		
            }

//    #if 0  // UNUSED
//        {
//            // don't check these yet, there may be others inserted
//            in = scan = intercepts;
//            for ( scan = intercepts ; scan<intercept_p ; scan++)
//                if (scan.frac > maxfrac)
//                    *in++ = *scan;
//            intercept_p = in;
//            return false;
//        }
//    #endif
            if (!func.doFunc(in)) {
                return false;	// don't bother going farther
            }
            if (in != null) {
                in.frac = Integer.MAX_VALUE;
            }
        }

        return true;		// everything was traversed
    }




    //
    // P_PathTraverse
    // Traces a line from x1,y1 to x2,y2,
    // calling the traverser function for each.
    // Returns true if the traverser function returns true
    // for all lines.
    //
    public boolean P_PathTraverse(
            int _x1,
            int _y1,
            int _x2,
            int _y2,
            int flags,
            PTRfunc func)    {
        int x1 = _x1;
        int y1 = _y1;
        int x2 = _x2;
        int y2 = _y2;

        int xt1;
        int yt1;
        int xt2;
        int yt2;

        int xstep;
        int ystep;

        int partial;

        int xintercept;
        int yintercept;

        int mapx;
        int mapy;

        int mapxstep;
        int mapystep;

        int count;
        
        int bmaporgx = game.playerSetup.bmaporgx;
        int bmaporgy = game.playerSetup.bmaporgy;

        earlyout = (flags & PT_EARLYOUT) > 0;

        game.renderer.validcount++;
        //intercept_p = intercepts;
        intercept_p = 0;
        intercepts = new Intercept[MAXINTERCEPTS];

        if (((x1 - bmaporgx) & (MAPBLOCKSIZE - 1)) == 0) {
            x1 += FRACUNIT;	// don't side exactly on a line
        }
        if (((y1 - bmaporgy) & (MAPBLOCKSIZE - 1)) == 0) {
            y1 += FRACUNIT;	// don't side exactly on a line
        }
        trace.x = x1;
        trace.y = y1;
        trace.dx = x2 - x1;
        trace.dy = y2 - y1;

        x1 -= bmaporgx;
        y1 -= bmaporgy;
        xt1 = x1>>MAPBLOCKSHIFT;
        yt1 = y1>>MAPBLOCKSHIFT;

        x2 -= bmaporgx;
        y2 -= bmaporgy;
        xt2 = x2>>MAPBLOCKSHIFT;
        yt2 = y2>>MAPBLOCKSHIFT;

        if (xt2 > xt1) {
            mapxstep = 1;
            partial = FRACUNIT - ((x1 >> MAPBTOFRAC) & (FRACUNIT - 1));
            ystep = FixedPoint.div(y2 - y1, Math.abs(x2 - x1));
        } else if (xt2 < xt1) {
            mapxstep = -1;
            partial = (x1 >> MAPBTOFRAC) & (FRACUNIT - 1);
            ystep = FixedPoint.div(y2 - y1, Math.abs(x2 - x1));
        } else {
            mapxstep = 0;
            partial = FRACUNIT;
            ystep = 256 * FRACUNIT;
        }

        yintercept = (y1>>MAPBTOFRAC) + FixedPoint.mul (partial, ystep);


        if (yt2 > yt1) {
            mapystep = 1;
            partial = FRACUNIT - ((y1 >> MAPBTOFRAC) & (FRACUNIT - 1));
            xstep = FixedPoint.div(x2 - x1, Math.abs(y2 - y1));
        } else if (yt2 < yt1) {
            mapystep = -1;
            partial = (y1 >> MAPBTOFRAC) & (FRACUNIT - 1);
            xstep = FixedPoint.div(x2 - x1, Math.abs(y2 - y1));
        } else {
            mapystep = 0;
            partial = FRACUNIT;
            xstep = 256 * FRACUNIT;
        }
        xintercept = (x1>>MAPBTOFRAC) + FixedPoint.mul (partial, xstep);

        // Step through map blocks.
        // Count is present to prevent a round off error
        // from skipping the break.
        mapx = xt1;
        mapy = yt1;

        for (count = 0; count < 64; count++) {
            if ((flags & PT_ADDLINES) > 0) {
                if (!P_BlockLinesIterator(mapx, mapy, new PIT_AddLineIntercepts(this))) {
                    return false;	// early out
                }
            }

            if ((flags & PT_ADDTHINGS) > 0) {
                if (!P_BlockThingsIterator(mapx, mapy, new PIT_AddThingIntercepts(this))) {
                    return false;	// early out
                }
            }

            if (mapx == xt2
                    && mapy == yt2) {
                break;
            }

            if ((yintercept >> FRACBITS) == mapy) {
                yintercept += ystep;
                mapx += mapxstep;
            } else if ((xintercept >> FRACBITS) == mapx) {
                xintercept += xstep;
                mapy += mapystep;
            }

        }
        // go through the sorted list
        return P_TraverseIntercepts ( func, FRACUNIT );
    }



 
}
