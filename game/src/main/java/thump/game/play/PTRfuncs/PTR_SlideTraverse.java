/*
 * Slide Traverse function handler
 */
package thump.game.play.PTRfuncs;

import thump.game.Game;
import thump.game.play.Intercept;

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
