/*
 * PIT Check Thing Function
 */
package thump.game.play.PITfuncs;

import thump.game.Game;
import thump.game.maplevel.MapObject;
import thump.wad.map.Line;

/**
 *
 * @author mark
 */
public class PIT_CheckThing implements PITfunc {

    @Override
    public boolean doFunc(MapObject thing) {
        return Game.getInstance().map.PIT_CheckThing(thing);
    }

    @Override
    public boolean doFunc(Line line) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
