/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.render;

import static thump.render.Renderer.NUMCOLORMAPS;

/**
 *
 * @author mark
 */
public class Vissprite {
    // Doubly linked list.
    public Vissprite	prev;
    public Vissprite	next;
    
    public int         x1;
    public int         x2;

    // for line side calculation
    public int		gx;
    public int		gy;		

    // global bottom / top for silhouette clipping
    public int         gz;
    public int         gzt;

    // horizontal position of x1
    public int         startfrac;
    
    public int         scale;
    
    // negative if flipped
    public int         xiscale;	

    public int         texturemid;
    public int         patch;

    // for color translation and shadow draw,
    //  maxbright frames as well
    public byte	colormap[] = new byte[NUMCOLORMAPS];
   
    public int         mobjflags;
    
}
