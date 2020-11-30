/*

Field       Type        Size        Offset  Description
width       uint16_t	2           0       Width of graphic
height      uint16_t	2           2       Height of graphic
leftoffset  int16_t	2           4       Offset in pixels to the left of the origin
topoffset   int16_t	2           6       Offset in pixels below the origin
columnofs   uint32_t[]	4 * width   8       Array of column offsets relative to the beginning of the patch header

*/
package thump.wad.mapraw;

import java.nio.ByteBuffer;
import static thump.base.Defines.logger;

/**
 *
 * @author mark
 */
public class PatchData {
    public final String name;
    public final int width;
    public final int height;
    public final int leftOffset;
    public final int topOffset;
    //private int columnOffset[];
    public final Column[] pixelData;

    public PatchData( String name, ByteBuffer bb ) {
        this.name = name;
        bb.position(0);
        width = bb.getShort();
        height = bb.getShort();
        leftOffset = bb.getShort();
        topOffset = bb.getShort();
        
        int colOffset[] = new int[width];
        for (int i = 0; i < width; i++) {
            colOffset[i] = bb.getInt();
        }

        //int imageStart = bb.position();
        pixelData = new Column[width];
        for (int i = 0; i < width; i++) {
            bb.position(colOffset[i]); // Seek to offset.
            pixelData[i] = new Column(bb, height);
        }
    }
    
    /**
     * Create a transparent PatchData.
     * 
     * @param name
     * @param width
     * @param height 
     */
    public PatchData( String name, int width, int height ) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.leftOffset = 0;
        this.topOffset = 0;
        this.pixelData = new Column[width];
        
        for ( int i=0; i<width; i++ ) {
            pixelData[i] = new Column(height);
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Picture:\n");

        sb.     //append("  n:").
                append("  w:").append(width).
                append("  h:").append(height).
                append("  lOff:").append(leftOffset).
                append("  tOff:").append(topOffset).
                append("  cols:").append(width);

        return sb.toString();
    }

}
