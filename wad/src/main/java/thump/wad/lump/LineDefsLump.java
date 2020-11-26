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
import thump.wad.map.Line;
import thump.wad.map.Vertex;

/**
 *
 * @author mark
 */
public class LineDefsLump extends Lump {

    public final ArrayList<Line> lineDefList = new ArrayList<>();
    public final MapLump parent;
    
    public int xMin = 0;
    public int xMax = 0;
    public int yMin = 0;
    public int yMax = 0;
    
    public boolean extentsKnown = false;

    public LineDefsLump(MapLump parent, FileChannel fc, String name, int filepos, int size) throws IOException {
        super(name, filepos, size);
        this.parent = parent;
        parent.setLineDefs(this);

        fc.position(filepos);
        ByteBuffer bb = ByteBuffer.allocate(size);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        fc.read(bb);
        bb.position(0);

        for (int i = 0; i < size / 14; i++) {
            lineDefList.add(new Line(parent,
                    bb.getShort(), bb.getShort(),
                    bb.getShort(), bb.getShort(),
                    bb.getShort(), bb.getShort(),
                    bb.getShort()
            ));
        }
    }

    /**
     * @return the lineDefList
     */
    public Iterator<Line> getLineDefList() {
        return lineDefList.iterator();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("LineDefs:\n");

        Iterator<Line> it = getLineDefList();
        int index = 0;
        while (it.hasNext()) {
            sb.append("    ").append(index).append("::").append(it.next().toString()).append("\n");
            index++;
        }

        return sb.toString();
    }

    public void computeExtents() {
        Vertex[] vList = parent.getVertexes().getVertexList();
        xMin = vList[0].x;
        yMin = vList[0].y;
        
        for ( Vertex v: vList ) {
            
            if ( v.x < xMin ) {
                xMin = v.x;
            } else if ( v.x > xMax ) {
                xMax = v.x;
            }
            if ( v.y < yMin ) {
                yMin = v.y;
            } else if ( v.y > yMax ) {
                yMax = v.y;
            }
            
        }
    }
    

    
    void initVertexes() {
        lineDefList.forEach((Line line) -> {
            line.initLine(parent);
        });
        
 
    }

    public Line[] toArray() {
        return lineDefList.toArray(new Line[lineDefList.size()]);
    }

}
