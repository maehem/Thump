/*
 * Floor Mover Action
 */
package thump.game.thinkeraction;

import thump.game.ThinkerAction;
import thump.play.Floor;

/**
 *
 * @author mark
 */
public class T_MoveFloor implements ThinkerAction {

    @Override
    public void doAction(Object floor) {
        Floor.T_MoveFloor((Floor) floor);
    }
    
}
