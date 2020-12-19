/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.render;

import java.text.MessageFormat;
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

    @Override
    public String toString() {
        return MessageFormat.format( 
                "        x1:{0}  x2:{1}\n" + 
                "        gx:{2}  gy:{3}  gz:{4}  gzt:{5}\n" +
                "        startfrac:{6}   scale:{7}  xiscale:{8}  texturemid:{9}  patch:{10}",
            new Object[]{x1, x2, gx, gy, gz, gzt, startfrac, scale, xiscale, texturemid, patch }
        );
    }
    
    
    
}
