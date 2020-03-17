/*
 * PIT Check Thing Function
 */
package thump.play.PITfuncs;

import thump.game.Game;
import thump.maplevel.MapObject;
import thump.play.PITfunc;
import thump.render.Line;

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
