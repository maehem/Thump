/*
        <picture>       := <header>
                           <pointers>           ;offsets to <column> starts
                           <pixel_data>
        <header>        := <width>              ;all are <short>
                           <height>
                           <left_offset>
                           <top_offset>
        <pointers>      := <pointer> [width]    ;<int>
        <pixel_data>    := <column> [width]

        <column>        := <post> [...]
                           <byte:255>           ;255 (0xff) ends the column

        <post>          := <rowstart>           ;<byte>
                           <num_pixels>         ;<byte>
                           <unused>             ;<byte>
                           <pixels>
                           <unused>             ;<byte>

        <pixels>        := <pixel> [num_pixels] ;<byte>
 */
package thump.render;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 *
 * @author mark
 */
public class Patch {
    public final String name;
    public final short width;
    public final short height;
    public final short leftOffset;
    public final short topOffset;
    public final int[] pointers;  // File specific. Could be thrown away after wad laoded.
    public final Column[] pixelData;
    public final ArrayList<BufferedImage> imageCache = new ArrayList<>(16);
    private final ArrayList<Color[]> paletteList;
    //public int[] data;

    public Patch( String name, ByteBuffer bb, ArrayList<Color[]> paletteList ) {
        this.name = name;
        this.paletteList = paletteList;
        
        width = bb.getShort();
        height = bb.getShort();
        leftOffset = bb.getShort();
        topOffset = bb.getShort();
        
        pointers = new int[width];
        for ( int i=0; i< width; i++ ) {
            pointers[i] = bb.getInt();
        }
        
        //int imageStart = bb.position();
        pixelData = new Column[width];
        for ( int i=0; i< width; i++ ) {
            bb.position(pointers[i]); // Seek to offset.
            pixelData[i] = new Column(bb, height);
        }
        
//        // Save a raster version of this patch
//        data = new int[width*height];
//        for ( int x=0; x<width; x++) {
//            int col[] = pixelData[x].getRawVals();
//            for (int y=0; y<height; y++ ) {
//                data[x+y*width] = col[y];
//            }
//        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Picture:\n");
        
        sb.append("  n:").append(name).append("  w:").append(width).append("  h:").
                append(height).append("  lOff:").append(leftOffset).append("  tOff:").
                append(topOffset).append("  cols:").append(pointers.length);
        //sb.append("\n");
        
//        for (Column col : pixelData) {
//            sb.append(col.toString());//.append("\n");
//        }
        return sb.toString(); //To change body of generated methods, choose Tools | Templates.
    }
    
//    public Image getImage() {
//        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//        int x=0;
//        for (Column col : pixelData) {
//            int[] vals = col.getValsAlpha();
//            for ( int y=0; y< vals.length; y++ ) {
//                img.setRGB(x, y, vals[y]);
//            }
//            x++;
//        }
//        
//        return img;
//    }
//    
    public Image getImage() {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        int x=0;
        for (Column col : pixelData) {
            int[] vals = col.getRawVals();
            for ( int y=0; y< vals.length; y++ ) {
                img.setRGB(x, y, 
                                (byte)vals[y]<<16|
                                (byte)vals[y]<<8 |
                                (byte)vals[y]&0xFF     );
            }
            x++;
        }
        
        return img;
    }
    
    public Image getColorImage(int paletteNum) {
        BufferedImage img;
        try {
            img = imageCache.get(paletteNum);
        } catch (IndexOutOfBoundsException ex ) {
            img = null;
        }
        
        if ( img != null ) {
            return img;
        } else {
            img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            int x=0;
            Color[] palette = paletteList.get(paletteNum);
            Graphics g = img.getGraphics();
            for (Column col : pixelData) {
                int[] vals = col.getRawVals();
                for ( int y=0; y< vals.length; y++ ) {
                    //Color[] palette = wad.getPlayPalLump().paletteList.get(0);
                    int val = vals[y];
                    int trans = 0xff;
                    if ( val == -1 ){
                        val = 0;
                        trans = 0x00;
                    }
                    Color c = palette[val];
                    int cc = trans<<24 | (c.getRed()&0xFF)<<16 | (c.getGreen()&0xFF)<<8 | c.getBlue()&0xFF;
                    img.setRGB(x, y, cc);
                }
                x++;
            }
            imageCache.add(paletteNum, img); // Cache it
        }
        
        return img;
    }
}
