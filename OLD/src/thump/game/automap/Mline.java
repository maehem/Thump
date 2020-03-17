/*
 * mline_t
 */
package thump.game.automap;

/**
 *
 * @author mark
 */
public class Mline {

    public Mline( int xa, int ya, int xb, int yb ) {
        a = new Mpoint(xa, ya);
        b = new Mpoint(xb, xb);
    }
    
    Mpoint a, b;
}
