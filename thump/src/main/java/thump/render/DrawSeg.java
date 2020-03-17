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
public class DrawSeg {
    public Seg     curline;
    public int     x1;
    public int     x2;

    public int     scale1;
    public int     scale2;
    public int     scalestep;

    // 0=none, 1=bottom, 2=top, 3=both
    public int     silhouette = 0; // SIL_ values.  Use enum?

    // do not clip sprites above this
    public int      bsilheight;

    // do not clip sprites below this
    public int      tsilheight;
    
    // Pointers to lists for sprite clipping,
    //  all three adjusted so [x1] is first value.
    int[]         sprtopclip=null;
    int[]         sprbottomclip=null;
    int         maskedtexturecol=-1;
    
}
