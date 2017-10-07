/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.wad.lump;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import thump.render.Sector;

/**
 *
 * @author mark
 */
public class SectorsLump extends Lump {
    public final ArrayList<Sector> sectorList = new ArrayList<>();    
    
    public SectorsLump(FileChannel fc, String name, int filepos, int size) throws IOException {
        super(name, filepos, size);

        fc.position(filepos);
        ByteBuffer bb = ByteBuffer.allocate(size);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        fc.read(bb);
        bb.position(0);
                
        for (int i = 0; i < size / 26; i++) {
            byte[] fT = new byte[8];
            byte[] cT = new byte[8];
            
            short floor = bb.getShort();
            short ceil = bb.getShort();
            bb.get(fT);
            bb.get(cT);
            short light = bb.getShort();
            short type = bb.getShort();
            short tag = bb.getShort();
            sectorList.add(new Sector(
                    floor,ceil, 
                    new String(fT, "ASCII"),
                    new String(cT, "ASCII"),
                    light, type, tag 
            ));
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Sectors:\n");
        
        Iterator<Sector> it = sectorList.iterator();
        int index=0;
        while (it.hasNext()) {
            sb.append("    ").append(index).append("::").append(it.next().toString()).append("\n");
            index++;
        }
        
        return sb.toString();
    }
    
    public Sector[] toArray() {
        return sectorList.toArray(new Sector[sectorList.size()]);
    }

    void init() {
        // Nothing to do here.
    }
}
