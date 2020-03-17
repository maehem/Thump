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
public class PatchesLump extends Lump {
    private final ArrayList<Patch> patches = new ArrayList<>();
    private final ArrayList<Color[]>paletteList;

    public PatchesLump(FileChannel fc, String name, int filepos, int size, ArrayList<Color[]> paletteList) {
        super(name, filepos, size);
        this.paletteList = paletteList;
    }
    
    public Patch addPatch(FileChannel fc, String name, int filePos, int lumpSize) throws IOException {
        fc.position(filePos);
        // Load up BB
        ByteBuffer bb = ByteBuffer.allocate(lumpSize);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        fc.read(bb);
        bb.position(0);

        Patch pic = new Patch(name, bb, paletteList);
        patches.add(pic);
    
        return pic;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Patches:\n");
        
        for ( int i=0; i<patches.size(); i++) {
            sb.append(patches.get(i).name).append("  ");
            if ( (i+1)% 10 == 0 ) {
                sb.append("\n");
            }
        }
        return sb.toString(); //To change body of generated methods, choose Tools | Templates.
    }
    
    public ArrayList<Patch> getPatches() {
        return patches;
    }

    public Patch getPatch(short patchNum) {
        return patches.get(patchNum);
    }
    
    public Patch getPatchByName( String pName ) {
        ListIterator<Patch> pics = getPatches().listIterator();
        
        while ( pics.hasNext() ) {
            Patch p = pics.next();
            if (p.name.equals(pName) ) {
                return p;
            }
        }
        
        return null;
    }
    
}
