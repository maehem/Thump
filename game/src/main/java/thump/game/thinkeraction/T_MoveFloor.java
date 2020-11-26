/*
 * Floor Mover Action
 */
package thump.game.thinkeraction;

import thump.game.play.Floor;
import thump.wad.map.ThinkerAction;

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
