/*
 * Button
 */
package thump.game.play;

import thump.game.maplevel.MapObject;
import thump.wad.map.Line;


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
