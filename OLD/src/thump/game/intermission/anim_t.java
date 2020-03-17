/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.game.intermission;

import thump.game.intermission.Intermission.animenum_t;
import thump.render.Patch;

/**
 *
 * @author mark
 */
public class anim_t {
        animenum_t	type;

        // period in tics between animations
        int		period;

        // number of animation frames
        int		nanims;

        // location of animation
        Point	loc;

        // ALWAYS: n/a,
        // RANDOM: period deviation (<256),
        // LEVEL: level
        int		data1;

        // ALWAYS: n/a,
        // RANDOM: random base period,
        // LEVEL: n/a
        int		data2; 

        // actual graphics for frames of animations
        Patch	p[] = new Patch[3]; 

        // following must be initialized to zero before use!

        // next value of bcnt (used in conjunction with period)
        int		nexttic;

        // last drawn animation frame
        int		lastdrawn;

        // next frame number to animate
        int		ctr;

        // used by RANDOM and LEVEL when animating
        int		state;  

    public anim_t(animenum_t type, int period, int nanims, int locX, int locY) {
        this.type = type;
        this.period = period;
        this.nanims = nanims;
        this.loc = new Point(locX, locY);
    }

    public anim_t(animenum_t type, int period, int nanims, int locX, int locY, int data1) {
        this(type, period, nanims, locX, locY);
        this.data1 = data1;
    }
        
    
}
