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
import thump.wad.map.Seg;

/**
 *
 * @author mark
 */
public class SegsLump extends Lump {
    private final ArrayList<Seg> segList = new ArrayList<>();    
    
    public SegsLump(MapLump map, FileChannel fc, String name, int filepos, int size) throws IOException {
        super(name, filepos, size);
        fc.position(filepos);
        ByteBuffer bb = ByteBuffer.allocate(size);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        fc.read(bb);
        bb.position(0);
        
        for (int i = 0; i < size / 12; i++) {
            segList.add(new Seg(map, 
                    bb.getShort(), bb.getShort(),
                    bb.getShort()&0xFFFF, bb.getShort(),
                    bb.getShort(), bb.getShort()
            ));
        }
        int i=0;  // for breakpoint debug
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Segs:\n");
        
        Iterator<Seg> it = segList.iterator();
        int index=0;
        while (it.hasNext()) {
            sb.append("    ").append(index).append("::").append(it.next().toString()).append("\n");
            index++;
        }
        
        return sb.toString();
    }
    
//    public Seg[] getFrackedSegList() {
//        
//        Seg[] segs = new Seg[segList.size()];
//        int i=0;
//        Iterator<Seg> it = segList.iterator();
//        while (it.hasNext() ) {
//            segs[i] = it.next().getFracked();
//            i++;
//        }
//        
//        return segs;
//    }

    void init() {
        Iterator<Seg> it = segList.iterator();
        while (it.hasNext() ) {
            it.next().init();
        }
    }

    public Seg[] toArray() {
        return segList.toArray(new Seg[segList.size()]);
    }
}
