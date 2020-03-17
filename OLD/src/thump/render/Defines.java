/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.render;

/**
 *
 * @author mark
 */
public class Defines {
// Silhouette, needed for clipping Segs (mainly)
// and sprites representing things.

    public static final int SIL_NONE = 0;
    public static final int SIL_BOTTOM = 1;
    public static final int SIL_TOP = 2;
    public static final int SIL_BOTH = 3;

    public static final int MAXDRAWSEGS = 256;

    //
    // Move clipping aid for LineDefs.
    //
    public enum Slopetype {
        ST_HORIZONTAL,
        ST_VERTICAL,
        ST_POSITIVE,
        ST_NEGATIVE
    }
}
