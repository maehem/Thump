/*
 * MapSegs are the same as regular Segs.
 */
package thump.maplevel;

import thump.render.Seg;
import thump.wad.lump.MapLump;

/**
 *
 * @author mark
 */
public class MapSeg extends Seg {
    
    public MapSeg(MapLump map, short start, short end, short angle, short lineDef, short direction, short offset) {
        super(map ,start, end, angle, lineDef, direction, offset);
    }
    
}
