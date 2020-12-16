/*
 * MapSegs are the same as regular Segs.
 */
package thump.game.maplevel;

import thump.wad.map.Line;
import thump.wad.map.Seg;

/**
 *  
 * @author mark
 */
public class MapSeg /*extends Seg*/ {
    public MapSector mapFrontSector;
    public MapSector mapBackSector = null;
    
    public MapSideDef mapSideDef;
    public Line mapLineDef;
    public final Seg seg;
    
//    public MapSeg(MapLump map, short start, short end, short angle, short lineDef, short direction, short offset) {
//        super(map ,start, end, angle, lineDef, direction, offset);
//    }
    
    public MapSeg( Seg seg ) {
        this.seg = seg;
        //super( seg );
        //this.mapLineDef = 
        //this.mapSideDef =
        //this.mapFrontSector =
        //this.mapBackSector =
    }
}
