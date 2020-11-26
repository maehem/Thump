/*
 * Vertex - Just a plain old x and y.
 */
package thump.wad.map;

import static thump.base.FixedPoint.FRACBITS;

/**
 *
 * @author mark
 */
public class Vertex {
    public int x;
    public int y;

    public Vertex(int x, int y) {
        this.x = x<<FRACBITS;
        this.y = y<<FRACBITS;
    }
    
    @Override
    public String toString() {
        return  "vertex: x:" + x+ "  y:" + y;
    }
    
//    public Vertex getFracked() {
//        return new Vertex(x<<FixedPoint.FRACBITS, y<<FixedPoint.FRACBITS);
//    }
    
}
