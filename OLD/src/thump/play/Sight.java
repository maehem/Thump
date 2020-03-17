/*
 *   LineOfSight/Visibility checks, uses REJECT Lookup Table.
 *
 */
package thump.play;

import thump.game.Game;
import thump.global.FixedPoint;
import static thump.global.FixedPoint.FRACBITS;
import thump.maplevel.MapObject;
import thump.render.Line;
import static thump.render.Line.ML_TWOSIDED;
import thump.render.Node;
import static thump.render.Node.NF_SUBSECTOR;
import thump.render.Sector;
import thump.render.Seg;
import thump.render.SubSector;
import thump.render.Vertex;

/**
 *
 * @author mark
 */
public class Sight {

    private final Game game;

    public Sight(Game game) {
        this.game = game;
    }
    
    //
    // P_CheckSight
    //
    public int		sightzstart;		// eye z of looker
    public int		topslope;
    public int		bottomslope;		// slopes to top and bottom of target

    public DivLine	strace;			// from t1 to t2
    public int		t2x;
    public int		t2y;

    public int		sightcounts[] = new int[2];


    //
    // P_DivlineSide
    // Returns side 0 (front), 1 (back), or 2 (on).
    //
    public static int P_DivlineSide(int x, int y, DivLine node) {
        int dx;
        int dy;
        int left;
        int right;

        if (0==node.dx) {
            if (x == node.x) {
                return 2;
            }

            if (x <= node.x) {
                return node.dy > 0 ? 1 : 0;
            }

            return node.dy < 0 ? 1 : 0;
        }

        if (0==node.dy) {
            if (x == node.y) {
                return 2;
            }

            if (y <= node.y) {
                return node.dx < 0 ? 1 : 0;
            }

            return node.dx > 0 ? 1 : 0;
        }

        dx = (x - node.x);
        dy = (y - node.y);

        left = (node.dy >> FRACBITS) * (dx >> FRACBITS);
        right = (dy >> FRACBITS) * (node.dx >> FRACBITS);

        if (right < left) {
            return 0;	// front side
        }
        if (left == right) {
            return 2;
        }
        return 1;		// back side
    }


    //
    // P_InterceptVector2
    // Returns the fractional intercept point
    // along the first divline.
    // This is only called by the addthings and addlines traversers.
    //
    public static int P_InterceptVector2(DivLine v2, DivLine v1) {
        int frac;
        int num;
        int den;

        den = FixedPoint.mul(v1.dy >> 8, v2.dx) - FixedPoint.mul(v1.dx >> 8, v2.dy);

        if (den == 0) {
            return 0;
            //	SystemInterface.I_Error ("P_InterceptVector: parallel");
        }

        num = FixedPoint.mul((v1.x - v2.x) >> 8, v1.dy)
                + FixedPoint.mul((v2.y - v1.y) >> 8, v1.dx);
        frac = FixedPoint.div(num, den);

        return frac;
    }

    //
    // P_CrossSubsector
    // Returns true
    //  if strace crosses the given subsector successfully.
    //
    public boolean P_CrossSubsector(int num) {
        Seg         seg;
        Line        line;
        int         s1;
        int         s2;
        int         count;
        SubSector   sub;
        Sector      front;
        Sector      back;
        int         opentop;
        int         openbottom;
        DivLine     divl = new DivLine();
        Vertex      v1;
        Vertex      v2;
        int         frac;
        int         slope;

//    #ifdef RANGECHECK
//        if (num>=numsubsectors)
//            SystemInterface.I_Error ("P_CrossSubsector: ss %i with numss = %i",
//                     num,
//                     numsubsectors);
//    #endif
        sub = game.playerSetup.subsectors[num];

        // check lines
        count = sub.numlines;
        int index = sub.firstline;
        //seg = game.playerSetup.segs[index];

        for (; count>0; index++, count--) {
            seg = game.playerSetup.segs[index];
            
            line = seg.linedef;

            // allready checked other side?
            if (line.validcount == game.renderer.validcount) {
                continue;
            }

            line.validcount = game.renderer.validcount;

            v1 = line.v1;
            v2 = line.v2;
            s1 = P_DivlineSide(v1.x, v1.y, strace);
            s2 = P_DivlineSide(v2.x, v2.y, strace);

            // line isn't crossed?
            if (s1 == s2) {
                continue;
            }

            divl.x = v1.x;
            divl.y = v1.y;
            divl.dx = v2.x - v1.x;
            divl.dy = v2.y - v1.y;
            s1 = P_DivlineSide(strace.x, strace.y, divl);
            s2 = P_DivlineSide(t2x, t2y, divl);

            // line isn't crossed?
            if (s1 == s2) {
                continue;
            }

            // stop because it is not two sided anyway
            // might do this after updating validcount?
            if (0 == (line.flags & ML_TWOSIDED)) {
                return false;
            }

            // crosses a two sided line
            front = seg.frontsector;
            back = seg.backsector;

            // no wall to block sight with?
            if (   front.floorheight == back.floorheight
                && front.ceilingheight == back.ceilingheight) {
                continue;
            }

            // possible occluder
            // because of ceiling height differences
            if (front.ceilingheight < back.ceilingheight) {
                opentop = front.ceilingheight;
            } else {
                opentop = back.ceilingheight;
            }

            // because of ceiling height differences
            if (front.floorheight > back.floorheight) {
                openbottom = front.floorheight;
            } else {
                openbottom = back.floorheight;
            }

            // quick test for totally closed doors
            if (openbottom >= opentop) {
                return false;		// stop
            }
            frac = P_InterceptVector2( strace,  divl);

            if (front.floorheight != back.floorheight) {
                slope = FixedPoint.div(openbottom - sightzstart, frac);
                if (slope > bottomslope) {
                    bottomslope = slope;
                }
            }

            if (front.ceilingheight != back.ceilingheight) {
                slope = FixedPoint.div(opentop - sightzstart, frac);
                if (slope < topslope) {
                    topslope = slope;
                }
            }

            if (topslope <= bottomslope) {
                return false;		// stop				
            }
        }
        // passed the subsector ok
        return true;
    }



    //
    // P_CrossBSPNode
    // Returns true
    //  if strace crosses the given node successfully.
    //
    public boolean P_CrossBSPNode(int bspnum) {
        Node bsp;
        int side;

        if ((bspnum & NF_SUBSECTOR) > 0) {
            if (bspnum == -1) {
                return P_CrossSubsector(0);
            } else {
                return P_CrossSubsector(bspnum & (~NF_SUBSECTOR));
            }
        }

        bsp = game.playerSetup.nodes[bspnum];

        // decide which side the start point is on
        side = Sight.P_DivlineSide(strace.x, strace.y, (DivLine) bsp);
        if (side == 2) {
            side = 0;	// an "on" should cross both sides
        }
        // cross the starting side
        if (!P_CrossBSPNode(bsp.children[side])) {
            return false;
        }

        // the partition plane is crossed here
        if (side == Sight.P_DivlineSide(t2x, t2y, (DivLine) bsp)) {
            // the line doesn't touch the other side
            return true;
        }

        // cross the ending side		
        return P_CrossBSPNode(bsp.children[side ^ 1]);
    }


    //
    // P_CheckSight
    // Returns true
    //  if a straight line between t1 and t2 is unobstructed.
    // Uses REJECT.
    //
    public boolean P_CheckSight(MapObject t1, MapObject t2) {
        int		s1;
        int		s2;
        int		pnum;
        int		bytenum;
        int		bitnum;

        // First check for trivial rejection.

        // Determine subsector entries in REJECT table.
        s1 = (game.playerSetup.getSecNum(t1.subsector.sector)); // - game.playerSetup.sectors);
        s2 = (game.playerSetup.getSecNum(t2.subsector.sector)); // - game.playerSetup.sectors);
        pnum = s1*game.playerSetup.sectors.length + s2;
        bytenum = pnum>>3;
        bitnum = 1 << (pnum&7);

        // Check in REJECT table.
        if ((game.playerSetup.rejectmatrix[bytenum]&bitnum)>0) {
            sightcounts[0]++;

            // can't possibly be connected
            return false;	
        }

        // An unobstructed LOS is possible.
        // Now look from eyes of t1 to any part of t2.
        sightcounts[1]++;

        game.renderer.validcount++;

        sightzstart = t1.z + t1.height - (t1.height>>2);
        topslope = (t2.z+t2.height) - sightzstart;
        bottomslope = (t2.z) - sightzstart;

        strace.x = t1.x;
        strace.y = t1.y;
        t2x = t2.x;
        t2y = t2.y;
        strace.dx = t2.x - t1.x;
        strace.dy = t2.y - t1.y;

        // the head node is the last node output
        return P_CrossBSPNode (game.playerSetup.nodes.length-1);	
    }

}
