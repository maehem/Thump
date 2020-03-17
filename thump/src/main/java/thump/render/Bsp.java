/*
 * Bsp function
 */
package thump.render;

import java.util.logging.Level;
import thump.game.Game;
import static thump.global.Defines.logger;
import static thump.global.Tables.ANG180;
import static thump.global.Tables.ANG90;
import static thump.global.Tables.ANGLETOFINESHIFT;
import static thump.render.Defines.MAXDRAWSEGS;
import static thump.render.Node.NF_SUBSECTOR;

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
    
    // newend is one past the last valid seg
    public int	newend=0;

    public ClipRange	solidsegs[] = new ClipRange[MAXSEGS];
    
    //private static Bsp instance = null;
    private final Renderer renderer;
    
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
    void R_ClipSolidWallSegment( int first, int last ) {
        int	next=0;
        //int nextIndex = 0;
        int	start;
        //int startIndex = 0;

        // Find the first range that touches the range
        //  (adjacent pixels are touching).
        start = 0;
        //start = 0;
        
        logger.log(Level.CONFIG, "Bsp.R_ClipSolidWallSegment( first: {0}, last: {1})\n", new Object[]{first, last});
        
        while (solidsegs[start].last < first-1) {
            start++;
        }

        if (first < solidsegs[start].first) {
            if (last < solidsegs[start].first-1) {
                // Post is entirely visible (above start),
                //  so insert a new clippost.
                Segs.getInstance().R_StoreWallRange (first, last);
                next = newend;
                newend++;
                //newend = solidsegs[newend];

                while (next != start) {
                    //*next = *(next-1);
                    solidsegs[next] = solidsegs[next-1];
                    //next--;
                    next--;
                }
                solidsegs[next].first = first;
                solidsegs[next].last = last;
                return;
            }

            // There is a fragment above *start.
            Segs.getInstance().R_StoreWallRange (first, solidsegs[start].first - 1);
            // Now adjust the clip size.
            solidsegs[start].first = first;	
        }

        // Bottom contained in start?
        if (last <= solidsegs[start].last) {
            return;
        }			

        next = start;
        
        boolean crunch = false;
        
        //while (last >= (next+1).first-1) {
        while (last >= solidsegs[next+1].first-1) {
            // There is a fragment between two posts.
            Segs.getInstance().R_StoreWallRange (solidsegs[next].last + 1, solidsegs[next+1].first - 1);            
            next++;

            if (last <= solidsegs[next].last) {
                // Bottom is contained in next.
                // Adjust the clip size.
                solidsegs[start].last = solidsegs[next].last;	
                //goto crunch;
                crunch = true;
                break;
            }
        }

        if ( !crunch ) { 
            // There is a fragment after *next.
            Segs.getInstance().R_StoreWallRange (solidsegs[next].last + 1, last);
            // Adjust the clip size.
            solidsegs[start].last = last;
        }

        // Remove start+1 to next from the clip list,
        // because start now covers their area.
      //crunch:
        if (next == start) {
            // Post just extended past the bottom of one post.
            return;
        }


        while (next != newend) {
            // Remove a post.
            //*++start = *next;
            start++;
            solidsegs[start] = solidsegs[next];
        }

        //newend = start+1;
        newend = start+1;
    }


    //
    // R_ClipPassWallSegment
    // Clips the given range of columns,
    //  but does not includes it in the clip list.
    // Does handle windows,
    //  e.g. LineDefs with upper and lower texture.
    //
    void R_ClipPassWallSegment( int first, int last ) {
        int	start;

        logger.log(Level.CONFIG, "bsp.R_ClipPassWallSegment( {0}, {1} )\n", new Object[]{first, last});
        
        // Find the first range that touches the range
        //  (adjacent pixels are touching).
        start = 0;
        while (solidsegs[start].last < first-1) {
            start++;
        }

        if (first < solidsegs[start].first) {
            if (last < solidsegs[start].first-1) {
                // Post is entirely visible (above start).
                Segs.getInstance().R_StoreWallRange (first, last);
                return;
            }

            logger.log(Level.CONFIG, "R_StoreWallRange( {0}, {1} )\n", new Object[]{first, solidsegs[start].first-1});
            // There is a fragment above *start.
            Segs.getInstance().R_StoreWallRange (first, solidsegs[start].first - 1);
        }

        // Bottom contained in start?
        if (last <= solidsegs[start].last) {
            return;
        }			

        while (last >= solidsegs[start+1].first-1) {
            // There is a fragment between two posts.
            Segs.getInstance().R_StoreWallRange (solidsegs[start].last + 1, solidsegs[start+1].first - 1);
            start++;

            if (last <= solidsegs[start].last) {
                return;
            }
        }

        // There is a fragment after *next.
        Segs.getInstance().R_StoreWallRange (solidsegs[start].last + 1, last);
    }



    //
    // R_ClearClipSegs
    //
    void R_ClearClipSegs () {
        //solidsegs[0].first = -0x7fffffff; 
        solidsegs[0].first = Integer.MIN_VALUE;
        solidsegs[0].last = -1;
        solidsegs[1].first = Game.getInstance().renderer.draw.viewwidth;
        //solidsegs[1].last = 0x7fffffff;
        solidsegs[1].last = Integer.MAX_VALUE;
        //newend = solidsegs+2;
        newend = 2;
    }


    //
    // R_AddLine
    // Clips the given segment
    // and adds any visible pieces to the line list.
    //
    void R_AddLine (Seg line) {
        int			x1;
        int			x2;
        long		angle1;
        long		angle2;
        long		span;
        long		tspan;

        curline = line;
        Renderer r = Game.getInstance().renderer;
        // OPTIMIZE: quickly reject orthogonal back sides.
        angle1 = r.R_PointToAngle (line.v1.x, line.v1.y);
        angle2 = r.R_PointToAngle (line.v2.x, line.v2.y);

        // Clip to view edges.
        // OPTIMIZE: make constant out of 2*clipangle (FIELDOFVIEW).
        span = (angle1 - angle2)&0xFFFFL;
        
        logger.log(Level.CONFIG, "Bsp.R_Addline() seg: {0}\n", line.toString());
        // Back side? I.e. backface culling?
        if (span >= ANG180) {
            return;
        }		

        // Global angle needed by segcalc.
        Segs.getInstance().rw_angle1 = angle1;
        angle1 -= r.viewangle;
        angle2 -= r.viewangle;

        long clipangle = r.clipangle;
        logger.log(Level.CONFIG, "Bsp.R_AddLine():  ViewAngle:  {0}     Clipangle:  {1}\n", new Object[]{ Long.toHexString(r.viewangle), Long.toHexString(clipangle) });
        
        tspan = angle1 + clipangle;
        if (tspan > 2*clipangle) {
            tspan -= 2*clipangle;

            // Totally off the left edge?
            if (tspan >= span) {
                logger.log(Level.CONFIG, "     Angle 1: Off the left edge?  tspan={0}    span={1}   returning....\n", new Object[]{Long.toHexString(tspan), Long.toHexString(span)});
                return;
            }

            angle1 = clipangle;
        }
    
        tspan = (clipangle - angle2)&0xFFFFL;
        if (tspan > 2*clipangle) {
            tspan -= 2*clipangle;

            // Totally off the left edge?
            if (tspan >= span) {
                logger.log(Level.CONFIG, "     Angle2: Off the left edge?  tspan={0}    span={1}   returning....\n", new Object[]{tspan, span});
                return;
            }
            
            angle2 = -clipangle;
        }

        // The seg is in the view range,
        // but not necessarily visible.
        angle1 = (((angle1+ANG90)&0xFFFFFFFL)>>ANGLETOFINESHIFT);
        angle2 = (((angle2+ANG90)&0xFFFFFFFL)>>ANGLETOFINESHIFT);
        logger.log( Level.CONFIG, "Bsp.R_AddLine():  Angle1: {0}   Angle2: {1}\n", new Object[]{Long.toHexString(angle1), Long.toHexString(angle2) });
        x1 = (int)(Game.getInstance().renderer.viewangletox[(int)angle1]);
        x2 = (int)(Game.getInstance().renderer.viewangletox[(int)angle2]);

        // Does not cross a pixel?
        if (x1 == x2) {
            logger.log(Level.CONFIG, "Bsp.R_AddLine():  x1 == x2 ,  {0} == {1}   returning...\n", new Object[]{ x1, x2 });
//MJK            return;
        }				

        backsector = line.backsector;

        // Single sided line?
        if (backsector == null) {
            R_ClipSolidWallSegment (x1, x2-1);
            return;
        }

        // Closed door.
        if (backsector.ceilingheight <= frontsector.floorheight
            || backsector.floorheight >= frontsector.ceilingheight) {
            R_ClipSolidWallSegment (x1, x2-1);
            return;
        }

        logger.log(Level.CONFIG, "FrontSector: {0}\n", frontsector.toString());
        logger.log(Level.CONFIG, " Backsector: {0}\n", backsector.toString());
        
        // Window.
        if (backsector.ceilingheight != frontsector.ceilingheight
            || backsector.floorheight != frontsector.floorheight) {
            R_ClipPassWallSegment (x1, x2-1);
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
        R_ClipPassWallSegment (x1, x2-1);	
//        return;
//
//      clipsolid:
//        R_ClipSolidWallSegment (x1, x2-1);
    }


    //
    // R_CheckBBox
    // Checks BSP node/subtree bounding box.
    // Returns true
    //  if some part of the bbox might be visible.
    //
    public int	checkcoord[][] =
    {
        {3,0,2,1},
        {3,0,2,0},
        {3,1,2,0},
        {0},
        {2,0,2,1},
        {0,0,0,0},
        {3,1,3,0},
        {0},
        {2,0,3,1},
        {2,1,3,1},
        {2,1,3,0}
    };


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

        logger.log(Level.CONFIG, "Bsp.R_CheckBBox() {0}\n", new Object[]{bspcoord.toString()});
        
        Renderer r = Game.getInstance().renderer;
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
        long vAng = r.viewangle;
        
        angle1 = (rp1 - r.viewangle)&0xFFFFFFFFL;
        angle2 = (rp2 - r.viewangle)&0xFFFFFFFFL;
        
        logger.log(Level.CONFIG, "angle1 = {0} -  {1} = {2}\n", new Object[]{Long.toHexString(rp1), Long.toHexString(vAng), Long.toHexString(angle1) } );
        logger.log(Level.CONFIG, "angle2 = {0} -  {1} = {2}\n", new Object[]{Long.toHexString(rp2), Long.toHexString(vAng), Long.toHexString(angle2) } );

        long saveAngle1 = angle1; // Debug
        span = Math.abs(angle1 - angle2);
        logger.log(Level.CONFIG, "span = {0}\n", new Object[]{ Long.toHexString(span) } );

        // Sitting on a line?
        if (span >= ANG180) {
            return true;
        }

        long clipangle = r.clipangle;
        logger.log(Level.CONFIG, "clipangle = {0}\n", new Object[]{ Long.toHexString(clipangle) } );
        
        tspan = (angle1 + clipangle);
        if (tspan > 2*clipangle) {
            tspan -= 2*clipangle;

            // Totally off the left edge?
            if (tspan >= span) {
                return false;
            }	

            angle1 = clipangle;
        }
        
        tspan = clipangle - angle2;
        if (tspan > 2*clipangle) {
            tspan -= 2*clipangle;

            // Totally off the left edge?
            if (tspan >= span) {
                return false;
            }

            angle2 = -clipangle;
        }

        int itsHere = 0;
        // Find the first clippost
        //  that touches the source post
        //  (adjacent pixels are touching).
        angle1 = ((angle1+ANG90)&0xFFFFFFFF)>>ANGLETOFINESHIFT;
        angle2 = ((angle2+ANG90)&0xFFFFFFFF)>>ANGLETOFINESHIFT;
        try {
            sx1 = (int)(r.viewangletox[(int)angle1]);
        } catch ( ArrayIndexOutOfBoundsException ex ) {
            sx1 = (int)(r.viewangletox[r.viewangletox.length-1]);
            logger.log(Level.WARNING, "oops!  angle1 = {0} [{1}], max angle is: {2}\n", 
                    new Object[]{angle1, Long.toHexString(angle1), r.viewangletox.length} );
        }
        try {
            sx2 = (int)(r.viewangletox[(int)angle2]);
        } catch ( ArrayIndexOutOfBoundsException ex ) {
            sx2 = (int)(r.viewangletox[r.viewangletox.length-1]);
            logger.log(Level.WARNING, "oops!  angle2 = {0} [{1}], max angle is: {2}\n", 
                    new Object[]{ angle2, Long.toHexString(angle2), r.viewangletox.length} );
        }

        // Does not cross a pixel.
        if (sx1 == sx2) {
            return false;
        }			
        sx2--;

        start = 0;
        while (solidsegs[start].last < sx2) {
            start++;
        }

        return !(sx1 >= solidsegs[start].first && sx2 <= solidsegs[start].last);
    }



    //
    // R_Subsector
    // Determine floor/ceiling planes.
    // Add sprites of things in sector.
    // Draw one or more line segments.
    //
    void R_Subsector (int num) {
        int		count;
        SubSector	sub;

        //Renderer renderer = Game.getInstance().renderer;
        //Wad wad = Game.getInstance().wad;
        
        renderer.sscount++;
        sub = Game.getInstance().playerSetup.subsectors[num];
        frontsector = sub.sector;
        count = sub.numlines;
        int lineNum = sub.firstline;
        
        logger.log(Level.CONFIG, "Bsp.R_Subsector:  0x{0}  count: {1}\n", new Object[]{Integer.toHexString(num), sub.numlines } );

        //line = Game.getInstance().playerSetup.segs[lineNum];

        if (frontsector.floorheight < renderer.viewz) {
            renderer.plane.floorplane = renderer.plane.R_FindPlane (
                    frontsector.floorheight,
                    frontsector.getFloorPic(),
                    frontsector.lightlevel);
        } else {
            renderer.plane.floorplane = null;
        }

        if (frontsector.ceilingheight > renderer.viewz 
            || frontsector.getCeilingPic() == renderer.skyflatnum)
        {
            renderer.plane.ceilingplane = renderer.plane.R_FindPlane (
                    frontsector.ceilingheight,
                    frontsector.getCeilingPic(),
                    frontsector.lightlevel);
        } else {
            renderer.plane.ceilingplane = null;
        }

        Game.getInstance().things.R_AddSprites (frontsector);	

        while (count>0) {
            count--;
            R_AddLine (Game.getInstance().playerSetup.segs[lineNum]);
            lineNum++;
        }
    }



    /**
     *  Renders all subsectors below a given node, traversing subtree recursively.
     *  Just call with BSP root.
     * 
     * @param bspnum 
     */
    void R_RenderBSPNode(int bspnum) {
        logger.log(Level.CONFIG, "BSP Render BSP Node: {0}\n", Integer.toHexString(bspnum));
        // Found a subsector?
        if ((bspnum & NF_SUBSECTOR) > 0) {
            logger.log(Level.CONFIG, "\t\tis a Subsector\n");
            if (bspnum == -1 || bspnum == 0xFFFF) {  //  0xFFFF
                R_Subsector(0);
            } else {
                R_Subsector(bspnum & (~NF_SUBSECTOR));
            }
            return;
        }

        Node bsp = Game.getInstance().playerSetup.nodes[bspnum];
        Renderer r = Game.getInstance().renderer;
        
        // Decide which side the view point is on.
        int side = r.R_PointOnSide(r.viewx, r.viewy, bsp)?1:0;

        // Recursively divide front space.
        R_RenderBSPNode(bsp.children[side]);

        // Possibly divide back space.
        if (R_CheckBBox(bsp.bbox[side ^ 1])) {
            R_RenderBSPNode(bsp.children[side ^ 1]);
        }
    }



}
