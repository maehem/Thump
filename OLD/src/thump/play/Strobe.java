/*
 * Strobe Effect
 */
package thump.play;

import thump.game.Thinker;
import thump.game.ThinkerAction;
import thump.render.Sector;

/**
 *
 * @author mark
 */
public class Strobe implements Thinker {
    //Thinker	thinker;
    Sector	sector;
    int		count;
    int		minlight;
    int		maxlight;
    int		darktime;
    int		brighttime;
    
    private Thinker prevThinker;
    private Thinker nextThinker;
    private ThinkerAction function;

    @Override
    public void setPrevThinker(Thinker thinker) {
        this.prevThinker = thinker;
    }

    @Override
    public Thinker getPrevThinker() {
        return prevThinker;
    }

    @Override
    public void setNextThinker(Thinker thinker) {
        this.nextThinker = thinker;
    }

    @Override
    public Thinker getNextThinker() {
        return nextThinker;
    }

    @Override
    public void setFunction(ThinkerAction function) {
        this.function = function;
    }

    @Override
    public ThinkerAction getFunction() {
        return function;
    }
        
}
