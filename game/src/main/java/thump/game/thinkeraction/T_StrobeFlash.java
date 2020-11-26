/*
 * Strobe Flash Action
 */
package thump.game.thinkeraction;

import thump.game.play.Lights;
import thump.game.play.Strobe;
import thump.wad.map.ThinkerAction;

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
