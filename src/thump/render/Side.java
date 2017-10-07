/*
        Offset	Size (bytes)	Description
        0	2	x offset/textureoffset
        2	2	y offset/rowoffset
        4	8	Name of upper texture
        12	8	Name of lower texture
        20	8	Name of middle texture
        28	2	Sector number this sidedef 'faces'
 */
package thump.render;

import thump.game.Game;
import static thump.global.FixedPoint.FRACBITS;
import thump.wad.lump.MapLump;

/**
 *
 * @author mark
 */
public class Side {
    public int   textureoffset;
    public final int   rowoffset;
    public String  toptexture;
    public String  bottomtexture;
    public String  midtexture;
    public final int   sectorNum;
    public int topTextureNum = -1;
    public int midTextureNum = -1;
    public int bottomTextureNum = -1;
    
    public Sector sector = null;

    public Side(int xOff, int yOff, String upperTexture, String lowerTexture, String middleTexture, int sector) {
        this.textureoffset = xOff<<FRACBITS;
        this.rowoffset = yOff<<FRACBITS;
        if (upperTexture.startsWith("-") ) {
            this.toptexture = null;
        } else {
            this.toptexture = upperTexture;
        }
        if (lowerTexture.startsWith("-")) {
            this.bottomtexture = null;
        } else {
            this.bottomtexture = lowerTexture;
        }
        if (middleTexture.startsWith("-")) {
            this.midtexture = null;
        } else {
            this.midtexture = middleTexture;
        }
        this.sectorNum = sector;
    }

    @Override
    public String toString() {
        return  "sideDef: x:" + textureoffset + "  y:" + rowoffset +
                "  upper:" + toptexture  + 
                "  lower:" + bottomtexture + "  mid:" + midtexture + 
                "  sector:"+ sectorNum
                ;
    }
    
    public int getTopTexture() {
        if ( topTextureNum == -1) {
            topTextureNum = Game.getInstance().wad.getTextureNum(toptexture);
        }
        return topTextureNum;
    }

    public int getMidTexture() {
        if ( midTextureNum == -1 ) {
            midTextureNum = Game.getInstance().wad.getTextureNum(midtexture);
        }
        
        return midTextureNum;
    }

    public int getBottomTexture() {
        if ( bottomTextureNum == -1 ) {
            bottomTextureNum =  Game.getInstance().wad.getTextureNum(bottomtexture);
        }
        return bottomTextureNum;
    }

    public Sector getSector(MapLump map) {
        if (sector == null ) {
            sector = map.getSectorsLump().sectorList.get(sectorNum);
        }
        return sector;
    }
}
