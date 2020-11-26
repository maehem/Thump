/*
 * Map Side Def.   Redundant class of Side
 */
package thump.game.maplevel;

import thump.wad.map.Side;

/**
 *
 * @author mark
 */
public class MapSideDef extends Side {
        
  // Front sector, towards viewer.
    public MapSector mapSector;

    public MapSideDef(int textureoffset, int rowoffset, 
            String toptexture, String bottomtexture, String midtexture, 
            int sector) {
        super(textureoffset, rowoffset, toptexture, bottomtexture, midtexture, sector);
    }
    
    public MapSideDef(Side s) {
        super(s);
        this.mapSector = (MapSector) sector;
    }
  
//    public Sector getSector(MapLump map) {
//        if (sector == null ) {
//            sector = map.getSectorsLump().sectorList.get(sectorNum);
//        }
//        return sector;
//    }

}
