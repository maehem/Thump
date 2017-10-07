/*
 * Interface for PIT functions.  Used by Map
 */
package thump.play;

import thump.maplevel.MapObject;
import thump.render.Line;

/**
 *
 * @author mark
 */
public interface PITfunc {
    
    public boolean doFunc( MapObject mo);
    public boolean doFunc( Line line);
}
