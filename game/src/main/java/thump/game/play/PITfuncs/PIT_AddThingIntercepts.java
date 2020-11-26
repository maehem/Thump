/*
 * PIT Add Thing Intercepts Function
 */
package thump.game.play.PITfuncs;

import thump.game.maplevel.MapObject;
import thump.game.play.MapUtil;
import thump.wad.map.Line;



/**
 *
 * @author mark
 */
public class PIT_AddThingIntercepts implements PITfunc {

    private final MapUtil mapUtil;

    public PIT_AddThingIntercepts(MapUtil mapUtil) {
        this.mapUtil = mapUtil;
    }
    

    @Override
    public boolean doFunc(MapObject thing) {
        return mapUtil.PIT_AddThingIntercepts(thing);
    }

    @Override
    public boolean doFunc(Line line) {
        throw new UnsupportedOperationException("PIT_AddThingIntercepts: Wrong function called!"); //To change body of generated methods, choose Tools | Templates.
    }
    
}
