/*
    line_s

Offset	Size (bytes)	Description
0	2	Start Vertex
2	2	End Vertex
4	2	Flags
6	2	Special Type
8	2	Sector Tag
10	2	Right Sidedef 1
12	2	Left Sidedef 1

*/
package thump.wad.map;

import java.util.ArrayList;
import thump.base.BoundingBox;
import thump.base.FixedPoint;
import thump.wad.lump.MapLump;

/**
 *
 * @author mark
 */
public class Line {  // line_s
    //
    // Move clipping aid for LineDefs.
    //
    public enum Slopetype {
        ST_HORIZONTAL,
        ST_VERTICAL,
        ST_POSITIVE,
        ST_NEGATIVE
    }
    
    public  short startV;
    public  short endV;
    public  short flags;
    public int special;
    public short tag;
    public  short sidenum[] = new short[2];
//    private final short right;
//    private final short left;
    // Neat. Another bounding box, for the extent
    //  of the LineDef.
    public BoundingBox	bbox = new BoundingBox(0, 0, 0, 0);

    // To aid move clipping.
    public Slopetype	slopetype = null;

    // Front and back sector.
    // Note: redundant? Can be retrieved from SideDefs.
    public Sector	frontsector;
    public Sector	backsector;

    // if == validcount, already checked
    public int		validcount;

    // thinker_t for reversable actions
    public Thinker	specialdata = null;
    
    public Vertex v1 = null;  // These get set once the Vertex Lump is loaded.
    public Vertex v2 = null;
    
    public int dx;
    public int dy;

    public Line() { }
    
    public Line(MapLump map, short startV, short endV, short flags, short special, short tag, short right, short left) {
        this.startV = startV;
        this.endV = endV;
        this.flags = flags;
        this.special = special;
        this.tag = tag;
        this.sidenum[0]=right;
        this.sidenum[1]=left;
    }
    
    //
    // LineDef attributes.
    //
    // Solid, is an obstacle.
    public static final int ML_BLOCKING = 1;

    // Blocks monsters only.
    public static final int ML_BLOCKMONSTERS = 2;

    // Backside will not be present at all
    //  if not two sided.
    public static final int ML_TWOSIDED = 4;

    // If a texture is pegged, the texture will have
    // the end exposed to air held constant at the
    // top or bottom of the texture (stairs or pulled
    // down things) and will move with a height change
    // of one of the neighbor sectors.
    // Unpegged textures allways have the first row of
    // the texture at the top pixel of the line for both
    // top and bottom textures (use next to windows).
    // upper texture unpegged
    public static final int ML_DONTPEGTOP = 8;

    // lower texture unpegged
    public static final int ML_DONTPEGBOTTOM = 16;

    // In AutoMap: don't map as two sided: IT'S A SECRET!
    public static final int ML_SECRET = 32;

    // Sound rendering: don't let sound cross two of these.
    public static final int ML_SOUNDBLOCK = 64;

    // Don't draw on the automap at all.
    public static final int ML_DONTDRAW = 128;

    // Set if already seen, thus drawn in automap.
    public static final int ML_MAPPED = 256;

    
    /**
     * @return the v1
     */
    public short getStartV() {
        return startV;
    }

    /**
     * @return the v2
     */
    public short getEndV() {
        return endV;
    }

    /**
     * @return the flags
     */
    public short getFlags() {
        return flags;
    }

    /**
     * @return the special
     */
    public int getSpecial() {
        return special;
    }

    /**
     * @return the tag
     */
    public short getSector() {
        return tag;
    }

    public int getSideCount() {
        int sCount = 0;
        if ( sidenum[0] > 0 ) {
            sCount ++;           
        }
        if ( sidenum[1] > 0 ) {
            sCount ++;           
        }
        
        return sCount;
    }
    
    @Override
    public String toString() {
        return  "lineDef: s:" + getStartV()+ "  e:" + getEndV()+
                "  flags:" + String.format("0x%02X", getFlags() ) + 
                "  spec:" + getSpecial()+ "  sect:" + getSector() + 
                "  right:" +sidenum[0] + "    left:" + sidenum[1]
                ;
    }

    public void initLine(MapLump map) {
        Vertex[] vList = map.getVertexes().getVertexList();
        this.v1 = vList[startV];
        this.v2 = vList[endV];
        
        this.dx = v2.x - v1.x;
        this.dy = v2.y - v1.y;
        
        if (dx == 0) {
            slopetype = Line.Slopetype.ST_VERTICAL;
        } else if (dy == 0) {
            slopetype = Line.Slopetype.ST_HORIZONTAL;
        } else if (FixedPoint.div(dy, dx) > 0) {
            slopetype = Line.Slopetype.ST_POSITIVE;
        } else {
            slopetype = Line.Slopetype.ST_NEGATIVE;
        }
        
        if (v1.x < v2.x) {
            bbox.left = v1.x;
            bbox.right = v2.x;
        } else {
            bbox.left = v2.x;
            bbox.right = v1.x;
        }

        if (v1.y < v2.y) {
            bbox.bottom = v1.y;
            bbox.top = v2.y;
        } else {
            bbox.bottom = v2.y;
            bbox.top = v1.y;
        }

        ArrayList<Side> sides = map.getSideDefs().sideDefList;
        
        if (sidenum[0] != -1) {
            frontsector = sides.get(sidenum[0]).getSector(map);
        } else {
            frontsector = null;
        }

        if (sidenum[1] != -1) {
            backsector = sides.get(sidenum[1]).getSector(map);
        } else {
            backsector = null;
        }
            
    }
   
}
