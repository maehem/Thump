/*
 * PIT Vile Check Function
 */
package thump.game.play.PITfuncs;

import thump.game.Game;
import thump.game.maplevel.MapObject;
import thump.wad.map.Line;

/**
 *
 * @author mark
 */
public class PIT_VileCheck implements PITfunc {

    @Override
    public boolean doFunc(MapObject mo) {
        return Game.getInstance().enemy.PIT_VileCheck(mo);
    }

    @Override
    public boolean doFunc(Line line) {
        throw new UnsupportedOperationException("PIT_VileCheck: Wrong function called!"); //To change body of generated methods, choose Tools | Templates.
    }
    
}
