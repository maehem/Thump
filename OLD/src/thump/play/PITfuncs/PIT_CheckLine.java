/*
 * PIT Check Line Function
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
public class PIT_CheckLine implements PITfunc {

    @Override
    public boolean doFunc(MapObject mo) {
        throw new UnsupportedOperationException("PIT_CheckLine: Wrong function called!"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean doFunc(Line ld) {
        return Game.getInstance().map.PIT_CheckLine(ld);
    }
    
}
