/*
    BSP node.

        Offset	Size (bytes)	Description
        0	2	Partition line x coordinate
        2	2	Partition line y coordinate
        4	2	Change in x to end of partition line
        6	2	Change in y to end of partition line
        8	8	Right bounding box
        16	8	Left bounding box
        24	2	Right child
        26	2	Left child
 */
package thump.wad.map;

import thump.base.BoundingBox;
import thump.base.DivLine;
import thump.base.FixedPoint;
import static thump.base.FixedPoint.FRACBITS;
//import thump.play.DivLine;

/**
 *
 * @author mark
 */
public class Node extends DivLine {
    // Indicate a leaf.
    public static final int NF_SUBSECTOR=0x8000;
    
//    public final int x;
//    public final int y;
//    public final int dx;
//    public final int dy;
    public final BoundingBox bbox[] = new BoundingBox[2];
    public final int children[] = new int[2];

    public Node(short x, short y, short xc, short yc, BoundingBox bbRight, BoundingBox bbLeft, int cr, int cl) {
        this.x = x<<FRACBITS;
        this.y = y<<FRACBITS;
        this.dx = xc<<FRACBITS;
        this.dy = yc<<FRACBITS;
        this.bbox[0] = bbRight;
        this.bbox[1] = bbLeft ;
        this.children[0] = cr;  // TODO: Correct order?
        this.children[1] = cl;
    }
    
    /**
     * Copy Node for use in MapNode of game.
     * 
     * @param node 
     */
    public Node( Node node ) {
        this.x = node.x;
        this.y = node.y;
        this.dx = node.dx;
        this.dy = node.dy;
        this.bbox[0] = node.bbox[0];
        this.bbox[1] = node.bbox[1];
        this.children[0] = node.children[0];
        this.children[1] = node.children[1];        
    }
    
    /**
     * Traverse BSP (sub) tree, check point against partition plane.
     * 
     * @param sx
     * @param sy
     * @return side.  false=(front) or true=(back).
     */
    public boolean  R_PointOnSide( int sx, int sy ) {
//    public boolean  R_PointOnSide( int sx, int sy, Node node ) {
        int	_dx;
        int	_dy;
        int	left;
        int	right;

        if (dx == 0) {
            if (sx <= x) {
                return dy > 0;
            }

            return dy < 0;
        }
        if (dy == 0) {
            if (sy <= y) {
                return dx < 0;
            }

            return dx > 0;
        }

        _dx = (sx - x);
        _dy = (sy - y);

        // Try to quickly decide by looking at sign bits.
        if ( ((dy ^ dx ^ _dx ^ _dy)&0x80000000) > 0 ) {
            // (left is negative)
            
            return ((dy ^ _dx) & 0x80000000) > 0;
        }

        left =  FixedPoint.mul(dy>>FRACBITS , _dx );
        right = FixedPoint.mul(_dy , dx>>FRACBITS );
        // back side
			
        return right >= left;			
    }
    
    @Override
    public String toString() {
        return  "node: x:" + Integer.toHexString(x) + "  y:" + Integer.toHexString(y) +
                "    xc:" + dx + "    yc:" + dy +
                "    bbr:" + bbox[0].toString() + "    bbL:" + bbox[1].toString() +
                "    cr:" + children[0] + "    cl:" + children[1]
                ;
    }
    
}
