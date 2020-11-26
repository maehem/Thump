/*
 * Mobj Thinker Action
 */
package thump.game.thinkeraction;

import thump.game.maplevel.MapObject;
import thump.game.play.MObject;
import thump.wad.map.ThinkerAction;


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
