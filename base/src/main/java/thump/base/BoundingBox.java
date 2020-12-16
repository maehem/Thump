/*
 * Bounding Box
 */
package thump.base;

/**
 *
 * @author mark
 */
public class BoundingBox {
    public int left;
    public int top;
    public int right;
    public int bottom;

    public BoundingBox(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public BoundingBox() {
        this(0,0,0,0);
    }

    public void M_ClearBox() {
        top = Integer.MIN_VALUE;
        right = Integer.MIN_VALUE;
        bottom = Integer.MAX_VALUE;
        left = Integer.MAX_VALUE;

    //    box[BOXTOP] = box[BOXRIGHT] = MININT;
    //    box[BOXBOTTOM] = box[BOXLEFT] = MAXINT;
    }

    public void M_AddToBox(int x, int y) {
        if (x < left) {
            left = x;
        } else if (x > right) {
            right = x;
        }

        if (y < bottom) {
            bottom = y;
        } else if (y > top) {
            top = y;
        }

    //    if (x<box[BOXLEFT])
    //	box[BOXLEFT] = x;
    //    else if (x>box[BOXRIGHT])
    //	box[BOXRIGHT] = x;
    //    if (y<box[BOXBOTTOM])
    //	box[BOXBOTTOM] = y;
    //    else if (y>box[BOXTOP])
    //	box[BOXTOP] = y;
    }

    @Override
    public String toString() {
        return "l:" + left + " t:" + top + " r:" + right + " b:" + bottom;
    }    
    
    public int[] toArray() {
        return new int[]{ top, bottom,left,right };
    }
}
