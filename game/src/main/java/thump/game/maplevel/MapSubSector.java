/*
 * Map SubSector
 */
package thump.game.maplevel;

import thump.wad.map.SubSector;

/**
 *
 * @author mark
 */
public class MapSubSector extends SubSector {
    public MapSector mapSector;
    
    public MapSubSector(short count, short first) {
        super(count, first);
        mapSector = new MapSector(sector);
        
    }
    
    public MapSubSector( SubSector ss ) {
        super(ss);
    }
}
