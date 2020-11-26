/*
 * Thinker Glow Action
 */
package thump.game.thinkeraction;

import thump.game.play.Glow;
import thump.game.play.Lights;
import thump.wad.map.ThinkerAction;


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
