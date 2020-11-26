/*

<PLAYPAL>       := <palette> [14]
<palette>       := {<red> <green> <blue>} [256]
<red>           := <byte>
<green>         := <byte>
<blue>          := <byte>


 */
package thump.wad.lump;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author mark
 */
public class PlaypalLump extends Lump {

    //public final ArrayList<Color[]> paletteList = new ArrayList<>();
    public final ArrayList<int[]> paletteList = new ArrayList<>();
    public final byte[] rawPalette = new byte[3*256]; // RGB interleaved for IndexColorMap 

    public PlaypalLump(FileChannel fc, String name, int filePos, int lumpSize) throws IOException {
        super(name, filePos, lumpSize);

        fc.position(filepos);
        ByteBuffer bb = ByteBuffer.allocate(3*256);
        for (int p = 0; p < 14; p++) {
            //Color[] palette = new Color[256];
            int[] palette = new int[256];
            bb.clear();
            fc.read(bb);
            bb.position(0);
            for (int i = 0; i < 256; i++) {
                int r = bb.get()&0x000000FF;
                int g = bb.get()&0x000000FF;
                int b = bb.get()&0x000000FF;
                rawPalette[3*i]  =(byte)((byte)r&0xFF);
                rawPalette[3*i+1]=(byte)((byte)g&0xFF);
                rawPalette[3*i+2]=(byte)((byte)b&0xFF);
                //palette[i] = new Color(bb.get()&0x000000FF, bb.get()&0x000000FF, bb.get()&0x000000FF);
                //palette[i] = new Color(r,g,b);
                palette[i] 
                        = 0xFF000000 // alpha
                        | r<<16 
                        | g<<8 
                        | b;
            }
            //int x=0;
            paletteList.add(palette);
        }
    }
    
//    public static Image getImageFor( Color[] c ) {
//        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
//        
//        Graphics g = img.getGraphics();
//        
//        for (int x=0; x< 16; x++ ) {
//            for ( int y=0; y< 16; y++ ) {
//                g.setColor(c[x*y]);
//                g.fillRect(x*4, y*4, 4, 4);
//            }
//        }
//        
//        return img;
//    }
//    
//    public Image getPreviewImage() {
//        BufferedImage img = new BufferedImage(640, 640, BufferedImage.TYPE_INT_ARGB);
//        Graphics g = img.getGraphics();
//        int MARGIN = 4;
//        // For each palette draw the image spaced out in a grid.
//        Iterator<Color[]> palettes = getPalettes();
//        for (int y=0; y< 4; y++ ) {
//            for ( int x=0; x< 4; x++ ) {
//                if (palettes.hasNext()) {
//                    g.drawImage(getImageFor(palettes.next()), x*68+MARGIN, y*68+MARGIN, null);
//                }
//            }
//        }
//        
//        return img;       
//    }
//    
    public byte[] getAsBytes(int i) {
        //Color[] rgb = paletteList.get(i);
        int[] rgb = paletteList.get(i);
        byte[] bytes = new byte[rgb.length*3];
        
        for ( int j=0; j<rgb.length; j++ ) {
            //bytes[j*3+0]=(byte) rgb[j].getRed();
            bytes[j*3+0]=(byte)((rgb[j]>>16)&0xFF);
            //bytes[j*3+1]=(byte) rgb[j].getGreen();
            bytes[j*3+1]=(byte)((rgb[j]>>8)&0xFF);
            //bytes[j*3+2]=(byte) rgb[j].getBlue();
            bytes[j*3+2]=(byte)((rgb[j])&0xFF);
        }
        return bytes;
    }
    
//    public Iterator<Color[]> getPalettes() {
//        return paletteList.iterator();
//    }
    
    public Iterator<int[]> getPalettes() {
        return paletteList.iterator();
    }
}
