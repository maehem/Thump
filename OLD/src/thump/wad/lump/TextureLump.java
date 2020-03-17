/*
    Offset	Length          Name        Content
    0x00	4               numtextures	An integer holding a the number of map textures.
    0x04	4 * numtextures	offset[ ]	An array of integers containing the offsets to the map textures in this lump.
    offset[0] ...Flexible	mtexture[ ]	An array with the map texture structures. (see next table)
    offset[1] ...Flexible	mtexture[ ]	An array with the map texture structures. (see next table)
*/
package thump.wad.lump;

import java.awt.Image;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import thump.maplevel.MapTexture;
import thump.wad.Wad;

/**
 *
 * @author mark
 */
public class TextureLump extends Lump {
    //public int[] offset = new int[0];
    public final ArrayList<Integer> offset = new ArrayList<>();
    
    //public final MapTexture[] mTexture;
    public final ArrayList<MapTexture> texture = new ArrayList<>();
    
    public TextureLump(Wad wad, FileChannel fc, String name, int filepos, int size) throws IOException {
        super(name, filepos, size);

        ingest(wad, fc, filepos, size);
        
        wad.setTextures(texture);
    }
    
    public final void ingest(Wad wad, FileChannel fc, int filepos, int size) throws IOException {
        fc.position(filepos);
        ByteBuffer bb = ByteBuffer.allocate(size);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        fc.read(bb);
        bb.position(0);

        int numTextures = bb.getInt();
        
        for (int i = 0; i < numTextures; i++) {
            offset.add(bb.getInt());
        }
                
        for (int i = 0; i < numTextures; i++) {
            texture.add(new MapTexture(wad, bb));
        }
        
    }
    
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TextureX: \n");
        sb.append("    numTextures:").append(texture.size());
        sb.append("\n");
        for (int i = 0; i < offset.size(); i ++) {
            sb.append("    ");

            sb.append(String.format("%08X", offset.get(i)));
            
            if ( (i+1) % 8 == 0 ) {
                sb.append("\n");
            }
        }
        for (int i=0; i<texture.size(); i++) {
            sb.append("   ").append(i).append("::    ");
            sb.append(texture.get(i).toString());
            sb.append("\n");
        }
        return sb.toString(); //To change body of generated methods, choose Tools | Templates.
    }
    
    Image getImage(Wad wad, int i ) {
        return texture.get(i).getImage();
    }
}
