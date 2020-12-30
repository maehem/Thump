/*
        Offset	Size (bytes)	Description
        0	2	Starting vertex number
        2	2	Ending vertex number
        4	2	Angle
        6	2	Linedef number
        8	2	Direction: 0 (same as linedef) or 1 (opposite of linedef)
        10	2	Offset: distance along linedef to start of seg
 */
package thump.wad.map;

import java.util.ArrayList;
import static thump.base.FixedPoint.FRACBITS;
import thump.wad.lump.MapLump;

/**
 *
 * @author mark
 */
public class Seg {
    private final MapLump map;
    public final int start;
    public final int end;
    public final int angle;
    public final int lineDefNum;
    public final int side; // How to use this?  // aka: side
    public final int offset;
    
    public Vertex v1;
    public Vertex v2;
    
    // Sector references.
    // Could be retrieved from linedef, too.
    // backsector is NULL for one sided lines
    public Sector frontsector;
    public Sector backsector = null;
    
    public Side sidedef;
    public Line linedef;

    public Seg(MapLump map, int start, int end, int angle, int lineDef, int side, int offset) {
        this.map = map;
        this.start = start;
        this.end = end;
        //this.angle = ((long)(angle&0xFFFFFFFL))<<FRACBITS;
        this.angle = (angle&0xFFFF)<<FRACBITS;
        this.lineDefNum = lineDef;
        this.side = side;
        this.offset = offset<<FRACBITS;
        
    }
    
    public Seg( Seg s ) {
        this( s.map, s.start, s.end, s.angle, s.lineDefNum, s.side, s.offset );
    }
    
    @Override
    public String toString() {
        return  "\n    seg=> start:" + start+ "  end:" + end +
                "\n    angle:" + Integer.toHexString(angle) + "    lineDef:" + lineDefNum +
                "    side:" + side + "   offset:" + offset
                ;
    }

//    public Seg getFracked() {
//        return new Seg(start, end, angle<<FixedPoint.FRACBITS, lineDefNum, side, offset<<FixedPoint.FRACBITS);
//    }


    public void init() {
        // TODO retrieve sectors from linedefs?
        Vertex[] vList = map.getVertexes().getVertexList();
        this.v1 = vList[start];
        this.v2 = vList[end];
        ArrayList<Side> sides = map.getSideDefs().sideDefList;
        this.linedef = map.getLineDefs().lineDefList.get(lineDefNum);
        this.sidedef = sides.get(linedef.sidenum[side]);
        this.frontsector = sidedef.getSector(map);
        if ( (this.linedef.flags & Line.ML_TWOSIDED)>0 ) {
            this.backsector = sides.get(linedef.sidenum[side^1]).getSector(map);
        } else {
            this.backsector = null;
        }

    }
}
