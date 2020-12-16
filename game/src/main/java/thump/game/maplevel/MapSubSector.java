/*
 * Map SubSector
 */
package thump.game.maplevel;

import thump.wad.map.SubSector;

/**
 *
 * @author mark
 */
public class MapSubSector /*extends SubSector*/ {
    public final SubSector subsector;
    public MapSector mapSector;
    
//    public MapSubSector(short count, short first) {
//        super(count, first);
//        //mapSector = new MapSector(sector);        
//    }
    
    public MapSubSector( SubSector subsector ) {
        this.subsector = subsector;
//        super(ss);
//        mapSector = new MapSector(ss.sector);
    }
}
