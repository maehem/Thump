/*
 * Mobj Thinker Action
 */
package thump.game.thinkeraction;

import thump.game.ThinkerAction;
import thump.maplevel.MapObject;
import thump.play.MObject;

/**
 *
 * @author mark
 */
public class T_MobjThinker implements ThinkerAction{

    @Override
    public void doAction(Object thing) {
        MObject.P_MobjThinker((MapObject) thing);
    }
    
}
