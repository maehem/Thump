/*
 * Slide Traverse function handler
 */
package thump.play.PTRfuncs;

import thump.game.Game;
import thump.play.Intercept;
import thump.play.PTRfunc;

/**
 *
 * @author mark
 */
public class PTR_SlideTraverse implements PTRfunc {

    @Override
    public boolean doFunc(Intercept in) {
        return Game.getInstance().map.PTR_SlideTraverse(in);
    }
    
}
