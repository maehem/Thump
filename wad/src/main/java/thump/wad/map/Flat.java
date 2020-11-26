/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.wad.map;

import java.nio.ByteBuffer;

/**
 *
 * @author mark
 */
public class Flat {

    public final String name;
    public final byte[] pixels;
    
    public Flat(String name, ByteBuffer bb) {
        this.name = name;

        bb.position(0);
        pixels = new byte[bb.limit()];
        bb.get(pixels);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Flat:\n");
        
            for ( int i=0; i< pixels.length; i++ ) {
                String padding = "00";
                String result = padding + Integer.toHexString(pixels[i]&0x000000FF);
                result = result.substring(result.length() - 2, result.length());  // take the right-most 64 digits
                sb.append(result);
            if ( (i+1)%64 == 0) {
                sb.append("\n");
            }
        }
        sb.append("\n");
        
        
        return sb.toString();
    }
    
//    public Image getImage() {
//        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
//        for ( int y = 0; y< 64; y++) {
//            for (int x=0; x<64; x++) {
//                int pp = pixels[x*y]&0xFF;
//                img.setRGB(x,y, 0xff<<24 | pp<<16 | pp<<8 | pp );
//            }
//        }
//        
//        return img;
//    }
//    
//    public Image getColorImage(Wad wad) {
//        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
//        Graphics g = img.getGraphics();
//        for ( int y = 0; y< 64; y++) {
//            for (int x=0; x<64; x++) {
//                int pp = pixels[x*y]&0xFF;
//
//                Color[] palette = wad.getPlayPalLump().paletteList.get(0);
//                Color c = palette[pp];
//                int cc = 0xFF<<24 | (c.getRed()&0xFF)<<16 | (c.getGreen()&0xFF)<<8 | c.getBlue()&0xFF;
//                img.setRGB(x, y, cc);
//            }
//        }
//
//        return img;
//    }
//    
    
}
