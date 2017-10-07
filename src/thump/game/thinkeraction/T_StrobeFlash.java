/*
 * Strobe Flash Action
 */
package thump.game.thinkeraction;

import thump.game.ThinkerAction;
import thump.play.Lights;
import thump.play.Strobe;

/**
 *
 * @author mark
 */
public class T_StrobeFlash implements ThinkerAction {

    @Override
    public void doAction(Object thing) {
        Lights.T_StrobeFlash((Strobe) thing);
    }
    
}
