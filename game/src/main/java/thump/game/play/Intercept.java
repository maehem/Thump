/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.game.play;

/**
 *
 * @author mark
 */
public class Intercept {
    public static final int MAXINTERCEPTS = 128;

    public Intercept(int frac, boolean isaline, Object lineThing) {
        this.frac = frac;
        this.isaline = isaline;
        this.lineThing = lineThing;
    }
    
    

    public int     frac;		// along trace line
    public boolean isaline;    // Object type stored in lineThing
    //    union {
    //	MapObject	thing;
    //	Line        line;
    //    }			d;
    public Object lineThing;  // isaline tells what type of object we stored here.
}
