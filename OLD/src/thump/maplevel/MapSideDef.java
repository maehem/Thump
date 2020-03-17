/*
 * Map Side Def.   Redundant class of Side
 */
package thump.maplevel;

import thump.render.Side;

/**
 *
 * @author mark
 */
public class MapSideDef extends Side {
        
  // Front sector, towards viewer.
    public int sector;

    public MapSideDef(int textureoffset, int rowoffset, 
            String toptexture, String bottomtexture, String midtexture, 
            int sector) {
        super(sector, sector, toptexture, toptexture, midtexture, sector);
    }
  
}
