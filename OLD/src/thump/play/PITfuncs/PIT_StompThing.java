/*
 * PIT Stomp Thing Function
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
public class PIT_StompThing implements PITfunc {

    @Override
    public boolean doFunc(MapObject thing) {
        return Game.getInstance().map.PIT_StompThing(thing);
    }

    @Override
    public boolean doFunc(Line line) {
        throw new UnsupportedOperationException("PIT_StompThing: Wrong function called!"); //To change body of generated methods, choose Tools | Templates.
    }
    
}
