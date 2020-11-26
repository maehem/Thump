/*
 * Light Flash Effect
 */
package thump.game.play;

import thump.wad.map.Sector;
import thump.wad.map.Thinker;
import thump.wad.map.ThinkerAction;


/**
 *
 * @author mark
 */
public class LightFlash implements Thinker {
    //Thinker	thinker;
    Sector	sector;
    int		count;
    int		maxlight;
    int		minlight;
    int		maxtime;
    int		mintime;

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
