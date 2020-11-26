/*
        Offset	Size (bytes)	Description
        0	2	x offset/textureoffset
        2	2	y offset/rowoffset
        4	8	Name of upper texture
        12	8	Name of lower texture
        20	8	Name of middle texture
        28	2	Sector number this sidedef 'faces'
 */
package thump.wad.map;

//import thump.game.Game;
import static thump.base.FixedPoint.FRACBITS;
import thump.wad.Wad;
import thump.wad.lump.MapLump;

/**
 *
 * @author mark
 */
public class Side {
    public int   textureoffset;
    public final int   rowoffset;
    private final String  toptexture;
    private final String  bottomtexture;
    public String  midtexture;
    public final int   sectorNum;
    private int topTextureNum = -1;
    private int midTextureNum = -1;
    private int bottomTextureNum = -1;
    
    public Sector sector = null;

    public Side(int xOff, int yOff, String upperTexture, String lowerTexture, String middleTexture, int sectorNum) {
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
        this.sectorNum = sectorNum;
    }

    public Side(Side s) {
        this.textureoffset = s.textureoffset;
        this.rowoffset = s.rowoffset;
        this.toptexture = s.toptexture;
        this.bottomtexture = s.bottomtexture;
        this.midtexture = s.midtexture;
        this.sectorNum = s.sectorNum;
    }
    
    @Override
    public String toString() {
        return  "sideDef: x:" + textureoffset + "  y:" + rowoffset +
                "  upper:" + toptexture  + 
                "  lower:" + bottomtexture + "  mid:" + midtexture + 
                "  sector:"+ sectorNum
                ;
    }
    
    public int getTopTextureNum(Wad wad) {
        if ( topTextureNum == -1 && toptexture != null ) {
            //topTextureNum = Game.getInstance().wad.getTextureNum(toptexture);
            topTextureNum = wad.getTextureNum(toptexture);
        }
        return topTextureNum;
    }

    public int getMidTextureNum(Wad wad) {
        if ( midTextureNum == -1 && midtexture != null) {
            //midTextureNum = Game.getInstance().wad.getTextureNum(midtexture);
            midTextureNum = wad.getTextureNum(midtexture);
        }
        return midTextureNum;
    }

    public int getBottomTextureNum(Wad wad) {
        if ( bottomTextureNum == -1 && bottomtexture != null ) {
            //bottomTextureNum =  Game.getInstance().wad.getTextureNum(bottomtexture);
            bottomTextureNum = wad.getTextureNum(bottomtexture);
        }
        return bottomTextureNum;
    }

    public void setTopTextureNum(int num) {
        topTextureNum = num;
        //toptexture = Game.getInstance().wad.getTextures().get(num).name;
    }

    public void setMidTextureNum(int num) {
        midTextureNum = num;
        //midtexture = Game.getInstance().wad.getTextures().get(num).name;
    }

    public void setBottomTextureNum(int num) {
        bottomTextureNum = num;
        //bottomtexture = Game.getInstance().wad.getTextures().get(num).name;
    }
    
    public Sector getSector(MapLump map) {
        if (sector == null ) {
            sector = map.getSectorsLump().sectorList.get(sectorNum);
        }
        return sector;
    }
}
