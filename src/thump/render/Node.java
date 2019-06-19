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
package thump.render;

import static thump.global.FixedPoint.FRACBITS;
import thump.play.DivLine;

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
    
    @Override
    public String toString() {
        return  "node: x:" + Integer.toHexString(x) + "  y:" + Integer.toHexString(y) +
                "    xc:" + dx + "    yc:" + dy +
                "    bbr:" + bbox[0].toString() + "    bbL:" + bbox[1].toString() +
                "    cr:" + children[0] + "    cl:" + children[1]
                ;
    }
    
}
