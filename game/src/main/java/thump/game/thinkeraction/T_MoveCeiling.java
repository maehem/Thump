/*
 * Move Ceiling Thinker Action
 */
package thump.game.thinkeraction;

import thump.game.play.Ceiling;
import thump.wad.map.ThinkerAction;

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
