/*
 * Map Line Def - Redundant class for Line
 */
package thump.game.maplevel;

import thump.wad.map.Line;
import thump.wad.lump.MapLump;

/**
 *
 * @author mark
 */
public class MapLineDef extends Line {
    
    public MapLineDef(MapLump map, short startV, short endV, short flags, short special, short sector, short right, short left) {
        super(map, startV, endV, flags, special, sector, right, left);
    }
    
}
