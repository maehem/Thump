/*
 * Interface for PIT functions.  Used by Map
 */
package thump.game.play.PITfuncs;

import thump.game.maplevel.MapObject;
import thump.wad.map.Line;

/**
 *
 * @author mark
 */
public interface PITfunc {
    
    public boolean doFunc( MapObject mo);
    public boolean doFunc( Line line);
}
