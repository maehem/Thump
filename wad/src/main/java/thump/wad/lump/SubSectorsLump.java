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
import thump.wad.map.SubSector;

/**
 *
 * @author mark
 */
public class SubSectorsLump extends Lump {
    
    public final ArrayList<SubSector> ssectorList = new ArrayList<>();    
    
    public SubSectorsLump(FileChannel fc, String name, int filepos, int size) throws IOException {
        super(name, filepos, size);

        fc.position(filepos);
        ByteBuffer bb = ByteBuffer.allocate(size);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        fc.read(bb);
        bb.position(0);
        
        for (int i = 0; i < size / 4; i++) {
            short c = bb.getShort();
            short f = bb.getShort();
            ssectorList.add(new SubSector( c,f ));
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SubSectors:\n");
        
        Iterator<SubSector> it = ssectorList.iterator();
        int index=0;
        while (it.hasNext()) {
            sb.append("    ").append(index).append("::").append(it.next().toString()).append("\n");
            index++;
        }
        
        return sb.toString();
    }
    
    public SubSector[] toArray() {
        return ssectorList.toArray(new SubSector[ssectorList.size()]);
    }

    void init() {
        // Nothing to do here.
    }
}
