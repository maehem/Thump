/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.wad.lump;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import thump.render.Line;
import thump.render.Vertex;

/**
 *
 * @author mark
 */
public class LineDefsLump extends Lump {

    public final ArrayList<Line> lineDefList = new ArrayList<>();
    private final MapLump parent;
    
    private int xMin = 0;
    private int xMax = 0;
    private int yMin = 0;
    private int yMax = 0;
    
    private boolean extentsKnown = false;

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

    private void computeExtents() {
        Vertex[] vList = parent.getVertexes().getVertexList();

        Iterator<Line> lineList = getLineDefList();
        while( lineList.hasNext() ) {
            Line line = lineList.next();
            Vertex s = vList[line.getStartV()];
            Vertex e = vList[line.getEndV()];
            if ( s.x < 0 && s.x < xMin ) {
                xMin = s.x;
            } else if ( s.x > 0 && s.x > xMax ) {
                xMax = s.x;
            }
            if ( s.y < 0 && s.y < yMin ) {
                yMin = s.y;
            } else if ( s.y > 0 && s.y > yMax ) {
                yMax = s.y;
            }
            if ( e.x < 0 && e.x < xMin ) {
                xMin = e.x;
            } else if ( e.x > 0 && e.x > xMax ) {
                xMax = e.x;
            }
            if ( e.y < 0 && e.y < yMin ) {
                yMin = e.y;
            } else if ( e.y > 0 && e.y > yMax ) {
                yMax = e.y;
            }
        }
    }
    
    public Image getImage(int zoom) {
        if ( !extentsKnown ) {
            computeExtents();  // Just need to do this once
        }
        
        int xOff = -xMin + 20;
        int yOff = -yMin + 20;
        
        BufferedImage img = new BufferedImage((xMax-xMin)+40, (yMax-yMin)+40, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) img.getGraphics();
        
        g.setColor(Color.black);
        g.fillRect(0, 0, img.getWidth()-1, img.getHeight()-1);
                
        Vertex[] vList = parent.getVertexes().getVertexList();
        
        g.setStroke(new BasicStroke(3.0f));
        g.setColor(Color.yellow);
        Iterator<Line> lineList = getLineDefList();
        while( lineList.hasNext() ) {
            Line line = lineList.next();
            Vertex s = vList[line.getStartV()];
            Vertex e = vList[line.getEndV()];
            
            g.drawLine(
                    (s.x + xOff), 
                    (s.y + yOff),
                    (e.x + xOff), 
                    (e.y + yOff)
            );
        }
        
        g.setColor(Color.blue);
        g.drawRect(0, 0, img.getWidth()-1, img.getHeight()-1);
        
        return img.getScaledInstance(800, -1, Image.SCALE_SMOOTH);
    }


    void initVertexes() {
        lineDefList.forEach((line) -> {
            line.initLine(parent);
            //line.v1 = vertexList.get(line.startV);
            //line.v2 = vertexList.get(line.endV);
        });
        
 
    }

    public Line[] toArray() {
        return lineDefList.toArray(new Line[lineDefList.size()]);
    }

}
