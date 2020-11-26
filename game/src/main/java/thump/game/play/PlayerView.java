/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.game.play;

import thump.render.RPlayerView;

/**
 *
 * @author mark
 */
public class PlayerView extends RPlayerView{
    
    // psprites[]
    public PSprite psprites[];
    
    // extralight
    
    // viewz
    
    // fixedcolormap
    

    public PlayerView(
            int x, int y, long angle, 
            int invisibility, 
            int lightlevel, 
            int extralight, 
            int viewz, 
            int fixedcolormap,
            PSprite[] psprites
    ) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        //this.powers = powers;
        this.invisibility = invisibility;
        this.lightlevel = lightlevel;
        this.extralight = extralight;
        this.viewz = viewz;
        this.fixedcolormap = fixedcolormap;
        this.psprites = psprites;
    }
    
}
