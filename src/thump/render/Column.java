/*
 * Column is a list of Posts
 *
 */
package thump.render;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import static thump.global.Defines.SCREENHEIGHT;
import static thump.global.Defines.SCREENWIDTH;

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nColumn: ");
        //sb.append(posts.size()).append("\n");
        
        int rowCount = 0;
        for ( int p=0; p<posts.size(); p++ ) {
            Post post = posts.get(p);
            int i=0;
            for ( i=0; i<post.rowStart-rowCount; i++ ) {
                sb.append("  ");
            }
            rowCount += post.pixels.length + i;
            sb.append(post.toString());
            //sb.append("  ");
        };
        
        //sb.append("\n        ");
        
        return sb.toString(); //To change body of generated methods, choose Tools | Templates.
    }
    
    public int[] getValsAlpha() {
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
                vals[j+p.rowStart] = (0xFF<<24) | ((pixel&0xff)<<16) | ((pixel&0xff)<<8) | (pixel&0xff);
            }
        }
        
        return vals;
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

    Image getColumnImage(byte[] dc_colormap) {
        BufferedImage img = new BufferedImage(1, height, BufferedImage.TYPE_4BYTE_ABGR);
        //Graphics g = img.getGraphics();
        int[] vals = getRawVals();
        
        for ( int i=0; i<vals.length; i++ ) {
            img.setRGB(0, i, dc_colormap[vals[i]]);
        //g.setColor(new Color(dc_colormap[colVals[(frac>>FRACBITS)&127]]));
        }
        
        return img;
    }
    
    public void draw( Screen screen, int x, int dy ) {
        //int		frac;
        //int		fracstep;
        int count = height-1; 

        // Zero length, column does not exceed a pixel.
        if (count < 0) {
            return;
        } 
        int y = 0; //ylookup[dc_yl];
        //int x = columnofs[dc_x];  


        int [] vals = getRawVals();
        //fracstep = 1; //dc_iscale; 
        //frac = dc_texturemid + (dc_yl-renderer.centery)*fracstep; 
                        
        do {
            if (y>=SCREENHEIGHT) {
                return;
            }
            try {
                if (vals[y]>=0) { // Transparency is -1 so don't draw for negative value.
                    screen.area[dy*SCREENWIDTH+x] = vals[y];
                }
            y++;
            dy++;
            //frac += fracstep;
            } catch (ArrayIndexOutOfBoundsException ex ) {
                // chicken!
                int i=0;
            }
            count--;
        } while (count>0); 
    }
}