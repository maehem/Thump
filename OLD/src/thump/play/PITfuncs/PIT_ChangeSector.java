/*
 * PIT Change Sector function handler
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
public class PIT_ChangeSector implements PITfunc {

    @Override
    public boolean doFunc(MapObject mo) {
        return Game.getInstance().map.PIT_ChangeSector(mo);
    }

    @Override
    public boolean doFunc(Line line) {
        throw new UnsupportedOperationException("PIT_ChangeSector: Wrong function called!\n"); //To change body of generated methods, choose Tools | Templates.
    }
    
}
