/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.wad.lump;

import java.awt.Color;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.ListIterator;
import thump.render.Patch;

/**
 *
 * @author mark
 */
public class SpritesLump extends Lump {
    public final ArrayList<Patch> sprites = new ArrayList<>();
    private final ArrayList<Color[]>paletteList;

    public SpritesLump(FileChannel fc, String name, int filepos, int size, ArrayList<Color[]> paletteList) {
        super(name, filepos, size);
        this.paletteList = paletteList;
    }
    
    public Patch addSprite(FileChannel fc, String name, int filePos, int lumpSize) throws IOException {
        fc.position(filePos);
        // Load up BB
        ByteBuffer bb = ByteBuffer.allocate(lumpSize);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        fc.read(bb);
        bb.position(0);

        Patch pic = new Patch(name, bb, paletteList);
        sprites.add(pic);
        return pic;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Sprites:\n");
        
        for ( int i=0; i<sprites.size(); i++) {
            sb.append(sprites.get(i).name).append("  ");
            if ( (i+1)% 10 == 0 ) {
                sb.append("\n");
            }
        }
        return sb.toString(); //To change body of generated methods, choose Tools | Templates.
    }
    
    public ListIterator<Patch> getSprites() {
        return sprites.listIterator();
    }

    public Patch getSprite(int num) {
        return sprites.get(num);
    }
    
}
