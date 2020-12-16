/*
 * Bsp function
 */
package thump.render;

import java.util.ArrayList;
import java.util.logging.Level;
import thump.base.BoundingBox;
import static thump.base.Defines.logger;
import static thump.base.Tables.ANG180;
import static thump.base.Tables.ANG90;
import static thump.base.Tables.ANGLETOFINESHIFT;
import static thump.base.Tables.ang2deg;
import static thump.render.DrawSeg.MAXDRAWSEGS;
import thump.wad.Wad;
import thump.wad.map.Line;
import thump.wad.map.Node;
import static thump.wad.map.Node.NF_SUBSECTOR;
import thump.wad.map.Sector;
import thump.wad.map.Seg;
import thump.wad.map.Side;
import thump.wad.map.SubSector;

/**
 *
 * @author mark
 */
public class Bsp {
    public static int MAXSEGS	=	32;
    
    public Seg		curline;
    public Side		sidedef;
    public Line		linedef;
    public Sector	frontsector;
    public Sector	backsector;

    public DrawSeg	drawsegs[] = new DrawSeg[MAXDRAWSEGS];
    public int          ds_p;
    
    public int	newend=0;  // newend is one past the last valid seg

    public ClipRange	solidsegs[] = new ClipRange[MAXSEGS];
    
    private final Renderer renderer;
    
    /**
     * Create a BSP object.
     * 
     * @param renderer for the game
     */
    public Bsp(Renderer renderer) {
        this.renderer = renderer;
        
        for (int i=0; i<solidsegs.length; i++ ) {
            solidsegs[i] = new ClipRange();
        }
        for (int i=0; i<drawsegs.length; i++) {
            drawsegs[i] = new DrawSeg();
        }
    }
    
    //
    // R_ClearDrawSegs
    //
    public void R_ClearDrawSegs () {
        ds_p = 0;
    }

    //
    // R_ClipSolidWallSegment
    // Does handle solid walls,
    //  e.g. single sided LineDefs (middle texture)
    //  that entirely block the view.
    // 
    void R_ClipSolidWallSegment( int first, int last, Wad wad ) {
//    cliprange_t*	next;
//    cliprange_t*	start;

        logger.log(Level.CONFIG, "Bsp.R_ClipSolidWallSegment( first:{0}, last:{1})", new Object[]{first, last});
        int	next=0;  // next index
        //int nextIndex = 0;
        int	start = 0; // current start index
        //int startIndex = 0;

        // Find the first range that touches the range
        //  (adjacent pixels are touching).
//    start = solidsegs;
        //start = 0;
        
        
//    while (start->last < first-1)
//	start++;
        while (solidsegs[start].last < first-1) {
            start++;
        }

//      if (first < start->first) {
        if (first < solidsegs[start].first) {
//	    if (last < start->first-1) {
            if (last < solidsegs[start].first-1) {
                // Post is entirely visible (above start), so insert a new clippost.
                logger.log(Level.CONFIG, "    Post is entirely visible (above start). Insert a new clip post.");
//	        R_StoreWallRange (first, last);
                logger.log(Level.CONFIG, "    R_StoreWallRange(start:{0},stop:{1})", new Object[]{first, last});
                Segs.getInstance().R_StoreWallRange (
                        first, last,
                        renderer, wad);
//	    next = newend;
                next = newend;
//	    newend++;
                newend++;
                //newend = solidsegs[newend];

//	    while (next != start)  {
//		*next = *(next-1);
//		next--;
//	    }
                while (next != start) {
                    //*next = *(next-1);
                    // Set value of next to next-1.
                    solidsegs[next].first = solidsegs[next-1].first;
                    solidsegs[next].last = solidsegs[next-1].last; // Is this right?  Added as an experiment.
                    //next--;
                    next--;
                }
//	    next->first = first;
//	    next->last = last;
//	    return;
                solidsegs[next].first = first;
                solidsegs[next].last = last;
                return;
            }

            // There is a fragment above *start.
            logger.log(Level.CONFIG, "    There is a fragment above start.");
//	R_StoreWallRange (first, start->first - 1);
            Segs.getInstance().R_StoreWallRange (
                    first, solidsegs[start].first - 1,
                    renderer, wad);
            // Now adjust the clip size.
//	start->first = first;
            logger.log(Level.CONFIG, "    solidsegs[start].first = first  =  {0}", first);
            solidsegs[start].first = first;	
        }

        // Bottom contained in start?
//    if (last <= start->last)
//	return;			
        if (last <= solidsegs[start].last) {
            logger.log(Level.CONFIG, "    Bottom contained in start.");
            return;
        }			

        next = start;
//    next = start;
        
        boolean crunch = false;
        
        //while (last >= (next+1).first-1) {
        while (last >= solidsegs[next+1].first-1) {
            // There is a fragment between two posts.
            logger.log(Level.CONFIG, "    There is a fragment between two posts.");
//	R_StoreWallRange (next->last + 1, (next+1)->first - 1);
            Segs.getInstance().R_StoreWallRange (
                    solidsegs[next].last + 1, solidsegs[next+1].first - 1,
                    renderer, wad);            
//	next++;
            next++;

//	    if (last <= next->last) {
            if (last <= solidsegs[next].last) {
                // Bottom is contained in next. Adjust the clip size.
                logger.log(Level.CONFIG, "    Bottom is contained in next. Adjust clip size.");
//	    start->last = next->last;	
                solidsegs[start].last = solidsegs[next].last;	
//          goto crunch;
                crunch = true;
                break;
            }
        }

        if ( !crunch ) { 
            // There is a fragment after *next.
            logger.log(Level.CONFIG, "    There is a fragment after next.");
//    R_StoreWallRange (next->last + 1, last);
            Segs.getInstance().R_StoreWallRange (
                    solidsegs[next].last + 1, last,
                    renderer, wad);
            // Adjust the clip size.
//    start->last = last; // Adjust the clip size.
            solidsegs[start].last = last;
        }

        // Remove start+1 to next from the clip list,
        // because start now covers their area.
      //crunch:
        if (next == start) {
            // Post just extended past the bottom of one post.
            logger.log(Level.CONFIG, "    Post just extended past the bottom of one post.");
            return;
        }

//    while (next++ != newend)
//    {
//	// Remove a post.
//	*++start = *next;
//    }
        while (next++ != newend) {
            // Remove a post.
            //*++start = *next;
            logger.log(Level.CONFIG, "    Remove a post.");

            start++;
            solidsegs[start].first = solidsegs[next].first;
        }

//    newend = start+1;
        newend = start+1;
    }

//R_ClipSolidWallSegment
//( int			first,
//  int			last )
//{
//
//    // Find the first range that touches the range
//    //  (adjacent pixels are touching).
//    start = solidsegs;
//    while (start->last < first-1)
//	start++;
//
//    if (first < start->first) {
//	if (last < start->first-1) {
//	    // Post is entirely visible (above start),
//	    //  so insert a new clippost.
//	    R_StoreWallRange (first, last);
//	    next = newend;
//	    newend++;
//	    
//	    while (next != start)  {
//		*next = *(next-1);
//		next--;
//	    }
//	    next->first = first;
//	    next->last = last;
//	    return;
//	}
//		
//	// There is a fragment above *start.
//	R_StoreWallRange (first, start->first - 1);
//	// Now adjust the clip size.
//	start->first = first;	
//    }
//
//    // Bottom contained in start?
//    if (last <= start->last)
//	return;			
//		
//    next = start;
//    while (last >= (next+1)->first-1) {
//	// There is a fragment between two posts.
//	R_StoreWallRange (next->last + 1, (next+1)->first - 1);
//	next++;
//	
//	if (last <= next->last) {
//	    // Bottom is contained in next. Adjust the clip size.
//	    start->last = next->last;	
//	    goto crunch;
//	}
//    }
//	
//    // There is a fragment after *next.
//    R_StoreWallRange (next->last + 1, last);
//    start->last = last; // Adjust the clip size.
//	
//    // Remove start+1 to next from the clip list, because start now covers their area.
//  crunch:
//    if (next == start)
//    {
//	// Post just extended past the bottom of one post.
//	return;
//    }
//}

    /**
     * Clips the given range of columns, but does not include it 
     * in the clip list.
     * Does handle windows, e.g. LineDefs with upper and lower texture.
     * 
     * @param first in clip range
     * @param last in clip range
     * @param wad 
     */
    void R_ClipPassWallSegment( int first, int last, Wad wad ) {
        logger.log(Level.CONFIG, "bsp.R_ClipPassWallSegment( first:{0}, last:{1} )", new Object[]{first, last});
        
        // Find the first range that touches the range
        //  (adjacent pixels are touching).
        int start = 0;
        while (solidsegs[start].last < first-1) {
            start++;
        }

        if (first < solidsegs[start].first) {
            if (last < solidsegs[start].first-1) {
                // Post is entirely visible (above start).
                Segs.getInstance().R_StoreWallRange (
                        first, last, 
                        renderer, wad);
                return;
            }

            logger.log(Level.CONFIG, "R_StoreWallRange( first:{0}, last:{1} )", new Object[]{first, solidsegs[start].first-1});
            // There is a fragment above *start.
            Segs.getInstance().R_StoreWallRange (first, solidsegs[start].first - 1, renderer, wad);
        }

        // Bottom contained in start?
        if (last <= solidsegs[start].last) {
            return;
        }			

        while (last >= solidsegs[start+1].first-1) {
            // There is a fragment between two posts.
            Segs.getInstance().R_StoreWallRange (
                    solidsegs[start].last + 1, 
                    solidsegs[start+1].first - 1,
                    renderer,wad);
            start++;

            if (last <= solidsegs[start].last) {
                return;
            }
        }

        // There is a fragment after *next.
        Segs.getInstance().R_StoreWallRange (
                solidsegs[start].last + 1, last, 
                renderer, wad);
    }

    /**
     * 
     * @param viewwidth 
     */
    void R_ClearClipSegs (int viewwidth) {
        logger.log(Level.CONFIG, "R_ClearClipSegs()");
        solidsegs[0].first = Integer.MIN_VALUE; //solidsegs[0].first = -0x7fffffff;
        solidsegs[0].last = -1;
        solidsegs[1].first = viewwidth;
        solidsegs[1].last = Integer.MAX_VALUE; //solidsegs[1].last = 0x7fffffff;
        
        newend = 2; //newend = solidsegs+2;
    }

    //
    // R_AddLine
    // Clips the given segment
    // and adds any visible pieces to the line list.
    //
    void R_AddLine(Seg line, Wad wad) {
        int  x1;
        int  x2;
        long angle1;
        long angle2;
        long span;
        long tspan;

        curline = line;
        Renderer r = renderer;
        // TODO OPTIMIZE: quickly reject orthogonal back sides.
        angle1 = r.R_PointToAngle (line.v1.x, line.v1.y)&0xFFFFFFFFL;
        angle2 = r.R_PointToAngle (line.v2.x, line.v2.y)&0xFFFFFFFFL;

        logger.log(Level.CONFIG, 
                "Bsp.R_Addline(): {0}", line.toString());
        
        // Clip to view edges.
        // OPTIMIZE: make constant out of 2*clipangle (FIELDOFVIEW).
        //span = (angle1 - angle2)&0xFFFFL;
        span = (angle1 - angle2)&0xFFFFFFFFL;
        //span = Math.abs(angle1-angle2)&0xFFFFFFFF;
        logger.log(Level.CONFIG, 
                "     span=0x{0} = {1} = angle1-angle2 [0x{2}  {3}]-[0x{4}  {5}]", 
                new Object[]{
                    Long.toHexString(span),   ang2deg(span),
                    Long.toHexString(angle1), ang2deg(angle1), 
                    Long.toHexString(angle2), ang2deg(angle2)
                });
        
        if (span >= ANG180) {  // Back side? i.e. backface culling?
            logger.log(Level.CONFIG, 
                    "    span[{0}] > ANG180[{1}] ...  returning.",
                    new Object[]{Long.toHexString(span), Long.toHexString(ANG180)}
            );
            return;
        }		

        Segs.getInstance().rw_angle1 = angle1; // Global angle needed by segcalc.
        logger.log(Level.CONFIG, "    rw_angle set to: ", Long.toHexString(angle1) );

        angle1 = (angle1-r.viewangle)&0xFFFFFFFFL;
        logger.log(Level.CONFIG, "    angle1 set to: ", Long.toHexString(angle1));
        angle2 = (angle2-r.viewangle)&0xFFFFFFFFL;
        logger.log(Level.CONFIG, "    angle2 set to: ", Long.toHexString(angle2));
        
//        logger.log(Level.CONFIG, 
//                "    r.viewangle [{0}] subtracted from angle1 & angle2 now [0x{1}] [0x{2}]", 
//                new Object[]{Long.toHexString(r.viewangle), Long.toHexString(angle1), Long.toHexString(angle2)});

        long clipangle = r.clipangle;        

        tspan = (angle1 + clipangle)&0xFFFFFFFFL;
        logger.log(Level.CONFIG, 
                "    r.viewAngle:0x{0} r.clipangle:0x{1}  span:0x{2}  tspan:0x{3}", 
                new Object[]{ 
                    Long.toHexString(r.viewangle), 
                    Long.toHexString(clipangle), 
                    Long.toHexString(span),
                    Long.toHexString(tspan)
                });

        if (tspan > 2*clipangle) {
            tspan -= 2*clipangle;

            // Totally off the left edge?
            if (tspan >= span) {
                logger.log(Level.CONFIG, 
                        "     angle1: Off the left edge?  tspan=0x{0} >= span=0x{1}   returning...", 
                        new Object[]{Long.toHexString(tspan), Long.toHexString(span)}
                );
                return;
            }

            angle1 = clipangle;
            logger.log(Level.CONFIG, "    angle1 set to: ", Long.toHexString(angle1));
            
        }
    
        tspan = (clipangle - angle2)&0xFFFFFFFFL;  //tspan = (clipangle - angle2)&0xFFFFL;

        if (tspan > 2*clipangle) {
            tspan -= 2*clipangle;

            // Totally off the left edge?
            if (tspan >= span) {
                logger.log(Level.CONFIG, 
                        "     angle2: Off the left edge?  tspan={0} >= span={1}  returning...",
                        new Object[]{Long.toHexString(tspan), Long.toHexString(span)}
                );
                return;
            }
            
            angle2 = -clipangle;
            logger.log(Level.CONFIG, "    angle2 set to: ", Long.toHexString(angle2));
        }

        // The seg is in the view range, but not necessarily visible.
        angle1 = (((angle1+ANG90)&0xFFFFFFFFL)>>ANGLETOFINESHIFT);
        angle2 = (((angle2+ANG90)&0xFFFFFFFFL)>>ANGLETOFINESHIFT);
        logger.log( Level.CONFIG, 
                "    shisft down:  angle1:0x{0}   angle2:0x{1}", 
                new Object[]{Long.toHexString(angle1), Long.toHexString(angle2) });
        x1 = (int)(r.viewangletox[(int)angle1]);
        x2 = (int)(r.viewangletox[(int)angle2]);
        logger.log(Level.CONFIG, 
                "    x1: {0}    x2: {1}", 
                new Object[]{Integer.toHexString(x1), Integer.toHexString(x2)});

        // Does not cross a pixel?
        if (x1 == x2) {
            logger.log(Level.CONFIG, "   x1 == x2 ,  {0} == {1}   returning...", new Object[]{ x1, x2 });
            return;
        }				

        backsector = line.backsector;

        // Single sided line?
        if (backsector == null) {
            logger.log(Level.CONFIG, "    single sided line.");
            R_ClipSolidWallSegment (x1, x2-1, wad);
            return;
        }

        // Closed door.
        if (backsector.ceilingheight <= frontsector.floorheight
            || backsector.floorheight >= frontsector.ceilingheight) {
            logger.log(Level.CONFIG, "    closed door.");
            R_ClipSolidWallSegment (x1, x2-1, wad);
            return;
        }

        logger.log(Level.CONFIG, "    FrontSector: {0}", frontsector.toString());
        logger.log(Level.CONFIG, "     Backsector: {0}", backsector.toString());
        
        // Window.
        if (backsector.ceilingheight != frontsector.ceilingheight
            || backsector.floorheight != frontsector.floorheight) {
            logger.log(Level.CONFIG, 
                    "    window:  x1:{0}   x2:{1} ==> x2-1:{2}", 
                    new Object[]{
                        Integer.toHexString(x1),
                        Integer.toHexString(x2),
                        Integer.toHexString(x2-1)
                    });
            R_ClipPassWallSegment (x1, x2-1, wad);
            return;
        }	

        // Not needed since it returns either way.
//        // Reject empty lines used for triggers
//        //  and special events.
//        // Identical floor and ceiling on both sides,
//        // identical light levels on both sides,
//        // and no middle texture.
        if (backsector.ceilingpic.equals(frontsector.ceilingpic)
            && backsector.floorpic.equals(frontsector.floorpic)
            && backsector.lightlevel == frontsector.lightlevel
            && curline.sidedef.midtexture == null)
        {
            return;
        }


//      clippass:
        logger.log(Level.CONFIG, 
                    "  clippass:  x1:{0}   x2:{1} ==> x2-1:{2}", 
                    new Object[]{
                        Integer.toHexString(x1),
                        Integer.toHexString(x2),
                        Integer.toHexString(x2-1)
                    });
        R_ClipPassWallSegment (x1, x2-1, wad);	
//        return;
//
//      clipsolid:
//        R_ClipSolidWallSegment (x1, x2-1);
    }

    public int	checkcoord[][] = {
        {3,0,2,1},
        {3,0,2,0},
        {3,1,2,0},
        {0,0,0,0},
        {2,0,2,1},
        {0,0,0,0},
        {3,1,3,0},
        {0,0,0,0},
        {2,0,3,1},
        {2,1,3,1},
        {2,1,3,0}
    };

    /**
     * Checks BSP node/subtree bounding box.
     * 
     * @param bspcoord bounding box coordinates
     * @return true if some part of the bbox might be visible.
     */
    boolean R_CheckBBox (BoundingBox	bspcoord) {
        int boxx;
        int boxy;
        int boxpos;

        int x1;
        int y1;
        int x2;
        int y2;

        long angle1;
        long angle2;
        long span;
        long tspan;

        int start;

        int sx1;
        int sx2;

        logger.log(Level.CONFIG, "Bsp.R_CheckBBox() bspcoords: {0}", new Object[]{bspcoord.toString()});
        
        Renderer r = renderer;
        // Find the corners of the box
        // that define the edges from current viewpoint.
        //if (Game.getInstance().renderer.viewx <= bspcoord[BOXLEFT]) {
        if (r.viewx <= bspcoord.left) {
            boxx = 0;
        } else if (r.viewx < bspcoord.right) {
            boxx = 1;
        } else {
            boxx = 2;
        }

        if (r.viewy >= bspcoord.top) {
            boxy = 0;
        } else if (r.viewy > bspcoord.bottom) {
            boxy = 1;
        } else {
            boxy = 2;
        }

        boxpos = (boxy<<2)+boxx;
        if (boxpos == 5) {
            logger.log(Level.CONFIG, "    (boxy<<2)+boxx == 5     return true." );
            return true;
        }

        int[] bsparray = bspcoord.toArray();
        x1 = bsparray[checkcoord[boxpos][0]];
        y1 = bsparray[checkcoord[boxpos][1]];
        x2 = bsparray[checkcoord[boxpos][2]];
        y2 = bsparray[checkcoord[boxpos][3]];

        // check clip list for an open space
        long rp1 = r.R_PointToAngle(x1, y1);//&0xFFFFL;
        long rp2 = r.R_PointToAngle(x2, y2);//&0xFFFFL;
        long vAng = r.viewangle&0xFFFFFFFFL;
        
        logger.log(Level.CONFIG, "    viewangle:{0}", Long.toHexString(r.viewangle));
        logger.log(Level.CONFIG, "    rp1: {0}   rp2:{1}", new Object[]{Long.toHexString(rp1), Long.toHexString(rp2)});
        angle1 = (rp1 - r.viewangle)&0xFFFFFFFFL;
        angle2 = (rp2 - r.viewangle)&0xFFFFFFFFL;
        
        logger.log(Level.CONFIG, "R_CheckBBox: angle1 = {0} -  {1} = {2}", new Object[]{Long.toHexString(rp1), Long.toHexString(vAng), Long.toHexString(angle1) } );
        logger.log(Level.CONFIG, "R_CheckBBox: angle2 = {0} -  {1} = {2}", new Object[]{Long.toHexString(rp2), Long.toHexString(vAng), Long.toHexString(angle2) } );

        long saveAngle1 = angle1; // Debug
        span = Math.abs(angle1 - angle2)&0xFFFFFFFFL;
        logger.log(Level.CONFIG, "R_CheckBBox: span = {0}", new Object[]{ Long.toHexString(span) } );

        // Sitting on a line?
        if (span >= ANG180) {
            logger.log(Level.CONFIG, "             span > ANG180");
            return true;
        }

        long clipangle = r.clipangle;
        logger.log(Level.CONFIG, "R_CheckBBox: clipangle = {0} = {1}deg", new Object[]{ Long.toHexString(clipangle), ang2deg(clipangle) } );
        
        tspan = (angle1 + clipangle);
        if (tspan > 2*clipangle) {
            tspan -= 2*clipangle;

            // Totally off the left edge?
            if (tspan >= span) {
                logger.log(Level.CONFIG, 
                        "    ang1: Off the edge:  tspan >= span : {0} >= {1}    return false", 
                        new Object[]{Long.toHexString(tspan), Long.toHexString(span)}
                );
                return false;
            }	

            angle1 = clipangle;
        }
        /*

    tspan = clipangle - angle2;
    if (tspan > 2*clipangle)
    {
	tspan -= 2*clipangle;

	// Totally off the left edge?
	if (tspan >= span)
	    return false;
	
	angle2 = -clipangle;
    }
        
        
        
        */
        tspan = clipangle - angle2;
        if (tspan > 2*clipangle) {
            tspan -= 2*clipangle;

            // Totally off the left edge?
            if (tspan >= span) {
                logger.log(Level.CONFIG, 
                        "    ang2: Off the edge:  tspan >= span : {0} >= {1}    return false", 
                        new Object[]{Long.toHexString(tspan), Long.toHexString(span)}
                );
                return false;
            }

            logger.log(Level.CONFIG, "    angle2 = -clipangle    angle2 = {0}", new Object[]{Long.toHexString(-clipangle)});
            angle2 = -clipangle;
        }

        // Find the first clippost that touches the source post
        //  (adjacent pixels are touching).
        angle1 = (((angle1+ANG90)&0xFFFFFFFFL)>>ANGLETOFINESHIFT)&0xFFFL;
        angle2 = (((angle2+ANG90)&0xFFFFFFFFL)>>ANGLETOFINESHIFT)&0xFFFL;
        try {
            sx1 = (int)(r.viewangletox[(int)angle1]);
        } catch ( ArrayIndexOutOfBoundsException ex ) {
            //sx1 = (int)(r.viewangletox[r.viewangletox.length-1]);
            logger.log(Level.WARNING, "R_CheckBBox: oops!  angle1 = [{0}], max angle is: {1}", 
                    new Object[]{Long.toHexString(angle1), Long.toHexString(r.viewangletox.length)} );
            throw ex;
        }
        try {
            sx2 = (int)(r.viewangletox[(int)angle2]);
        } catch ( ArrayIndexOutOfBoundsException ex ) {
            //sx2 = (int)(r.viewangletox[r.viewangletox.length-1]);
            logger.log(Level.WARNING, "R_CheckBBox: oops!  angle2 = [{0}], max angle is: {1}", 
                    new Object[]{ Long.toHexString(angle2), Long.toHexString(r.viewangletox.length)} );
            throw ex;
        }

        logger.log(Level.CONFIG, "    sx1 = {0}  sx2 = {1}", new Object[]{sx1, sx2});
        // Does not cross a pixel.
        if (sx1 == sx2) {
            logger.log(Level.CONFIG, "      they are equal, returing...");
            return false;
        }			
        sx2--;

        start = 0;
        try {
        while (solidsegs[start].last < sx2) {
            start++;
        }
        } catch ( ArrayIndexOutOfBoundsException e) {
            int ii = 0;  // debug stop point
            throw e;
        }

        return !(sx1 >= solidsegs[start].first && sx2 <= solidsegs[start].last);
    }



    //
    // R_Subsector
    // Determine floor/ceiling planes.
    // Add sprites of things in sector.
    // Draw one or more line segments.
    //
    //void R_Subsector (int num) {
    void R_Subsector (SubSector sub, Wad wad, Seg[] segs) {
        int		count;
        //SubSector	sub;

        //Renderer renderer = Game.getInstance().renderer;
        //Wad wad = Game.getInstance().wad;
        
        renderer.sscount++;
        //sub = Game.getInstance().playerSetup.subsectors[num];
        frontsector = sub.sector;
        count = sub.numlines;
        int lineNum = sub.firstline;
        
        //logger.log(Level.CONFIG, "Bsp.R_Subsector:  0x{0}  count: {1}\n", new Object[]{Integer.toHexString(num), sub.numlines } );
        logger.log(Level.CONFIG, "Bsp.R_Subsector:  count: {0}", new Object[]{sub.numlines } );

        //line = Game.getInstance().playerSetup.segs[lineNum];

        if (frontsector.floorheight < renderer.viewz) {
            renderer.plane.floorplane = renderer.plane.R_FindPlane (
                    frontsector.floorheight,
                    frontsector.getFloorPic(wad),
                    frontsector.lightlevel);
            logger.log(Level.CONFIG, "    set floor plane picnum: {0}", renderer.plane.floorplane.picnum);
        } else {
            renderer.plane.floorplane = null;
        }

        if (   frontsector.ceilingheight > renderer.viewz 
            || frontsector.getCeilingPic(wad) == renderer.skyflatnum)
        {
            renderer.plane.ceilingplane = renderer.plane.R_FindPlane (
                    frontsector.ceilingheight,
                    frontsector.getCeilingPic(wad),
                    frontsector.lightlevel);
            logger.log(Level.CONFIG, "    set ceiling plane picnum: {0}", renderer.plane.ceilingplane.picnum);
        } else {
            renderer.plane.ceilingplane = null;
        }

        renderer.things.R_AddSprites (frontsector, renderer);	

        while (count>0) {
            count--;
            R_AddLine (segs[lineNum], wad);
            lineNum++;
        }
    }



    /**
     *  Renders all sub-sectors below a given node, traversing sub-tree recursively.Just call with BSP root.
     * 
     * @param bspnum 
     * @param nodes 
     * @param wad 
     * @param subsectors 
     * @param segs 
     */
    //public void R_RenderBSPNode(int bspnum) {
    //public void R_RenderBSPNode(int bspnum, Node[] nodes, Wad wad, SubSector[]	subsectors, Seg[] segs) {
    public void R_RenderBSPNode(int bspnum, 
            ArrayList<Node> nodes, 
            Wad wad, 
            SubSector[]	subsectors, 
            //ArrayList<SubSector> subsectors, 
            Seg[] segs ) {
        
        logger.log(Level.CONFIG,     "BSP Render: bspnum:0x{0}", Integer.toHexString(bspnum));
        // Found a subsector?
        if ((bspnum & NF_SUBSECTOR) > 0) {
            logger.log(Level.CONFIG, "                     ^--- is a Subsector");
            if (bspnum == -1 || bspnum == 0xFFFF) {  //  0xFFFF
                logger.config("Render Subsector:  -1");
                //R_Subsector(0);
                //R_Subsector(Game.getInstance().playerSetup.subsectors[0], wad);
                R_Subsector(subsectors[0], wad, segs);
                //R_Subsector(subsectors.get(0), wad, segs);
            } else {
                logger.log(Level.CONFIG, "Render Subsector:  0x{0}", Integer.toHexString(bspnum & (~NF_SUBSECTOR)));
                //R_Subsector(bspnum & (~NF_SUBSECTOR));
                //R_Subsector(Game.getInstance().playerSetup.subsectors[bspnum & (~NF_SUBSECTOR)], wad);
                R_Subsector(subsectors[bspnum & (~NF_SUBSECTOR)], wad, segs);
                //R_Subsector(subsectors.get(bspnum & (~NF_SUBSECTOR)), wad, segs);
            }
            return;
        }

        //Node bspNode = nodes[bspnum];
        Node bspNode = nodes.get(bspnum);
        Renderer r = renderer;
        
        // Decide which side the view point is on.
        int side = bspNode.R_PointOnSide(r.viewx, r.viewy)?1:0;

        // Recursively divide front space.
        //R_RenderBSPNode(bspNode.children[side], nodes, wad);
        R_RenderBSPNode(bspNode.children[side], nodes, wad, subsectors, segs);

        // Possibly divide back space.
        if (R_CheckBBox(bspNode.bbox[side ^ 1])) {
            //R_RenderBSPNode(bspNode.children[side ^ 1], nodes, wad);
            R_RenderBSPNode(bspNode.children[side ^ 1], nodes, wad, subsectors, segs);
        }
    }

}
