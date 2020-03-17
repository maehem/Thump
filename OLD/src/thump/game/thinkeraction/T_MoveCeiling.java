/*
 * Move Ceiling Thinker Action
 */
package thump.game.thinkeraction;

import thump.game.ThinkerAction;
import thump.play.Ceiling;

/**
 *
 * @author mark
 */
public class T_MoveCeiling implements ThinkerAction{

    @Override
    public void doAction(Object thing) {
        Ceiling.TA_MoveCeiling((Ceiling) thing);
    }
    
}
