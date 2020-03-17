/*
 * Thinker Glow Action
 */
package thump.game.thinkeraction;

import thump.game.ThinkerAction;
import thump.play.Glow;
import thump.play.Lights;

/**
 *
 * @author mark
 */
public class T_Glow implements ThinkerAction {

    @Override
    public void doAction(Object thing) {
        Lights.T_Glow((Glow) thing);
    }
    
}
