/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.render;

import thump.wad.map.Seg;

/**
 *
 * @author mark
 */
public class DrawSeg {
    public static final int MAXDRAWSEGS = 256;
    
    // TODO: Change to enum
    public static final int SIL_NONE = 0;
    public static final int SIL_BOTTOM = 1;
    public static final int SIL_TOP = 2;
    public static final int SIL_BOTH = 3;

    public Seg     curline;
    public int     x1;
    public int     x2;

    public int     scale1;
    public int     scale2;
    public int     scalestep;

    // 0=none, 1=bottom, 2=top, 3=both
    public int     silhouette = SIL_NONE; // SIL_ values.

    // do not clip sprites above this
    public int      bsilheight;

    // do not clip sprites below this
    public int      tsilheight;
    
    // Pointers to lists for sprite clipping,
    //  all three adjusted so [x1] is first value.
    public int[]    sprtopclip=null;
    public int[]    sprbottomclip=null;
    public int      maskedtexturecol=-1;
    
}
