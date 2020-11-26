/*
 * PIT Add Line Intercepts Function
 */
package thump.game.play.PITfuncs;

import thump.game.maplevel.MapObject;
import thump.game.play.MapUtil;
import thump.wad.map.Line;

/**
 *
 * @author mark
 */
//
// PIT_AddLineIntercepts.
// Looks for lines in the given block
// that intercept the given mapUtil.trace
// to add to the intercepts list.
//
// A line is crossed if its endpoints
// are on opposite sides of the mapUtil.trace.
// Returns true if earlyout and a solid line hit.
//
public class PIT_AddLineIntercepts implements PITfunc {

    private final MapUtil mapUtil;

    public PIT_AddLineIntercepts(MapUtil mapUtil) {
        this.mapUtil = mapUtil;
    }

    @Override
    public boolean doFunc(MapObject mo) {
        throw new UnsupportedOperationException("PIT_LineIntercepts: Wrong function called!"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean doFunc(Line ld) {
        return mapUtil.PIT_AddLineIntercepts(ld);
    }

}
