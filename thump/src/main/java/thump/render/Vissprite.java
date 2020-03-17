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
    Vissprite	prev;
    Vissprite	next;
    
    int         x1;
    int         x2;

    // for line side calculation
    int		gx;
    int		gy;		

    // global bottom / top for silhouette clipping
    int         gz;
    int         gzt;

    // horizontal position of x1
    int         startfrac;
    
    int         scale;
    
    // negative if flipped
    int         xiscale;	

    int         texturemid;
    int         patch;

    // for color translation and shadow draw,
    //  maxbright frames as well
    byte	colormap[] = new byte[NUMCOLORMAPS];
   
    int         mobjflags;
    
}
