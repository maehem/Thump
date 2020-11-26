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
import static thump.base.FixedPoint.FRACBITS;
import thump.base.BoundingBox;
import thump.wad.map.Node;

/**
 *
 * @author mark
 */
public class NodesLump extends Lump {
    
    public final ArrayList<Node> nodeList = new ArrayList<>();    

    public NodesLump(FileChannel fc, String name, int filepos, int size) throws IOException {
        super(name, filepos, size);
        fc.position(filepos);
        ByteBuffer bb = ByteBuffer.allocate(size);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        fc.read(bb);
        bb.position(0);
        
        for (int i = 0; i < size / 28; i++) {
            short x = bb.getShort();
            short y = bb.getShort();
            short xc = bb.getShort();
            short yc = bb.getShort();
            BoundingBox br = new BoundingBox(bb.getShort()<<FRACBITS, bb.getShort()<<FRACBITS, bb.getShort()<<FRACBITS, bb.getShort()<<FRACBITS);
            BoundingBox bl = new BoundingBox(bb.getShort()<<FRACBITS, bb.getShort()<<FRACBITS, bb.getShort()<<FRACBITS, bb.getShort()<<FRACBITS);
            short cr = bb.getShort();
            short cl = bb.getShort();
            
            nodeList.add(new Node( x,y,xc,yc,br,bl,cr&0xffff,cl&0xffff));
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Nodes:\n");
        
        Iterator<Node> it = nodeList.iterator();
        int index=0;
        while (it.hasNext()) {
            sb.append("    ").append(index).append("::").append(it.next().toString()).append("\n");
            index++;
        }
        
        return sb.toString();
    }
    
    public Node[] toArray() {
        return nodeList.toArray(new Node[nodeList.size()]);
    }

    void init() {
        // Nothing do to here.
    }
}
