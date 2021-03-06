/*
        <post>          := <rowstart>           ;<byte>
                           <num_pixels>         ;<byte>
                           <unused>             ;<byte>
                           <pixels>
                           <unused>             ;<byte>
        <pixels>        := <pixel> [num_pixels] ;<byte>
 */
package thump.render;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 *
 * @author mark
 */
public class Post {
    public final int rowStart;
    public final byte[] pixels;

    public Post(ByteBuffer bb) {
        //int sp = bb.position(); //Debug started at.
        rowStart = bb.get()&0xFF;
        pixels = new byte[bb.get()&0x000000ff];
        try {
            bb.get(); // skip byte
            bb.get(pixels);
            bb.get(); // skip byte
        } catch ( BufferUnderflowException e) {
            int i = 2; // Something to stop at.
        }
    }

    Post(short height) {
        pixels = new byte[height];
        rowStart = 0;
        
        for ( int i=0; i<height; i++) {
            pixels[i] = (byte) 0xFF;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
            //sb.append("\nPixels:  ");
            
            for ( int i=0; i< pixels.length; i++ ) {
                String padding = "00";
                String result = padding + Integer.toHexString(pixels[i]&0xFF);
                result = result.substring(result.length() - 2, result.length());  // take the right-most 64 digits
                sb.append(result);
//            if ( (i+1)%128 == 0) {
//                sb.append("\n");
//            }
        }
        //sb.append("\n");
        
        return sb.toString(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
