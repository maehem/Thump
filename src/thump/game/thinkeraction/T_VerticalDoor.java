/*
 * Do Vertical Door Action
 */
package thump.game.thinkeraction;

import thump.game.ThinkerAction;
import thump.play.VDoor;

/**
 *
 * @author mark
 */
public class T_VerticalDoor implements ThinkerAction{

    @Override
    public void doAction(Object thing) {
        VDoor.T_VerticalDoor((VDoor) thing);
    }
    
}
