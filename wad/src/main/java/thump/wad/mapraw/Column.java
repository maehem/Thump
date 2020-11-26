/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.wad.mapraw;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 *
 * @author mark
 */
public class Column {
    public final ArrayList<Post> posts = new ArrayList<>();
    public final int height;

    public Column(ByteBuffer bb, int height) {
        this.height = height;
        
        // Add posts until next byte is 255
        while ( bb.get(bb.position()) != -1 )  { 
            posts.add(new Post(bb) );
        }
    }
    
    public Column(int height) {
        this.height = height;
        posts.add(new Post(height));
    }
    
    public int[] getRawVals() {
        int vals[] = new int[height];
        
        for ( int i=0; i< vals.length; i++ ) {
            vals[i] = -1;  // -1 is transparent.
        }
        
        for ( int i=0; i< posts.size(); i++ ) {
            Post p = posts.get(i);
            if ( p.rowStart == 255 ) {  // Ignore posts with rowStart = 255
                continue;
            }
            for ( int j=0; j<p.pixels.length; j++) {
                byte pixel = p.pixels[j];
                vals[j+p.rowStart] = pixel&0xff;
            }
        }
        
        return vals;
    }

}
