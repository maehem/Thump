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
import thump.wad.map.Side;

/**
 *
 * @author mark
 */
public class SideDefsLump extends Lump {
    public final ArrayList<Side> sideDefList = new ArrayList<>();    
    
    public SideDefsLump(FileChannel fc, String name, int filepos, int size) throws IOException {
        super(name, filepos, size);
        
        fc.position(filepos);
        ByteBuffer bb = ByteBuffer.allocate(size);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        fc.read(bb);
        bb.position(0);
        
        for (int i = 0; i < size / 30; i++) {
            byte[] uName = new byte[8];
            byte[] lName = new byte[8];
            byte[] mName = new byte[8];
            short x = bb.getShort();
            short y = bb.getShort();
            bb.get(uName);
            bb.get(lName);
            bb.get(mName);
            short sec = bb.getShort();
            sideDefList.add(new Side(
                    x,y, 
                    new String(uName, "ASCII"),
                    new String(lName, "ASCII"),
                    new String(mName, "ASCII"),
                    sec 
            ));
        }

    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SideDefs:\n");
        
        Iterator<Side> it = sideDefList.iterator();
        int index=0;
        while (it.hasNext()) {
            sb.append("    ").append(index).append("::").append(it.next().toString()).append("\n");
            index++;
        }
        
        return sb.toString();
    }

    void init() {
    }

    public Side[] toArray() {
        return sideDefList.toArray(new Side[sideDefList.size()]);
    }
    
}
