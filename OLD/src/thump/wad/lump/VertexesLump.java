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
import thump.render.Vertex;

/**
 *
 * @author mark
 */
public class VertexesLump extends Lump {
    private final ArrayList<Vertex> vertexList = new ArrayList<>();    
    private final MapLump parent;
    
    public VertexesLump(MapLump parent, FileChannel fc, String name, int filepos, int size) throws IOException {
        super(name, filepos, size);
        this.parent = parent;
        
        fc.position(filepos);
        ByteBuffer bb = ByteBuffer.allocate(size);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        fc.read(bb);
        bb.position(0);
        
        for (int i = 0; i < size / 4; i++) {
            short x = bb.getShort();
            short y = bb.getShort();
            vertexList.add(new Vertex( x,y ));
        }
        
        parent.setVertexes(this);
        
        //parent.getLineDefs().initVertexes(vertexList);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Vertexes:\n");
        
        Iterator<Vertex> it = vertexList.iterator();
        int index=0;
        while (it.hasNext()) {
            Vertex v = it.next();
            sb.append("    ").append(index).append("::")
                    .append(" x: ")
                    .append(Integer.toHexString(v.x))
                    .append(" y: ")
                    .append(Integer.toHexString(v.y))
                    .append("\n");
            index++;
        }
        
        return sb.toString();
    }
    
    public Vertex[] getVertexList() {
        return vertexList.toArray(new Vertex[]{});
    }
    
//    /**
//     * Gets FRACBITS shifted version of the Vertex List.
//     * 
//     * @return FRACBIT shifted vertex list.
//     */
//    public Vertex[] getFrackedVertexList() {
//        Vertex[] uf = getVertexList();
//        Vertex[] fracked = new Vertex[uf.length];
//        
//        for ( int i=0; i<uf.length; i++ ) {
//            fracked[i]=uf[i].getFracked();
//        }
//        
//        return fracked;
//    }

    void init() {
        // Nothing to do here.
    }
}
