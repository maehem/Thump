/*
 * Button
 */
package thump.play;

import thump.maplevel.MapObject;
import thump.render.Line;

/**
 *
 * @author mark
 */
public class Button {
    public enum BWhere {
        TOP,
        MIDDLE,
        BOTTOM
    };
    
    Line	line;
    BWhere	where;
    int		btexture;
    int		btimer;
    MapObject	soundorg;
    
    /**
     * Clear Settings
     */
    public void reset() {
        line = null;
        where = null;
        btexture = 0;
        btimer = 0;
        soundorg = null;
    }
}
