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
import java.util.logging.Level;
import static thump.global.Defines.logger;
import static thump.global.FixedPoint.FRACBITS;
import thump.maplevel.MapThing;
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
    
    public Image getImage(int imgWidth ) {
        if ( !extentsKnown ) {
            computeExtents();  // Just need to do this once
        }
        
        int xOff = -xMin>>FRACBITS;
        int yOff = -yMin>>FRACBITS;
        
        int width = (xMax-xMin)>>FRACBITS;
        int height = (yMax-yMin)>>FRACBITS;
        
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        logger.log(Level.CONFIG, "Image: {0} x {1}  xMin: {2}  yMin: {3}  xMax: {4}  yMax: {5} OffsetX: {6}  OffsetY: {7}\n", 
                new Object[]{width, height, 
                    xMin>>FRACBITS, yMin>>FRACBITS, 
                    xMax>>FRACBITS, yMax>>FRACBITS, 
                    xOff, yOff});
        
        // Draw from minX and minY points.
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.translate(xOff, yOff);
        
        // Fill the background with black.
        g.setColor(Color.black);
        g.fillRect(0, 0, img.getWidth()-1, img.getHeight()-1);
                
        Vertex[] vList = parent.getVertexes().getVertexList();
        
        Iterator<Line> lineList = getLineDefList();
        while( lineList.hasNext() ) {
            Line line = lineList.next();

            Vertex s = vList[line.getStartV()];
            Vertex e = vList[line.getEndV()];
            
            // Round it down.
            int sX = s.x>>FRACBITS;
            int sY = s.y>>FRACBITS;
            
            int eX = e.x>>FRACBITS;
            int eY = e.y>>FRACBITS;

            if ( line.getSideCount() == 2 ) {
                g.setColor(Color.GRAY);
            } else {
                g.setColor(Color.WHITE);
            }
            g.setStroke(new BasicStroke(4.0f/line.getSideCount()));

            g.drawLine( sX, sY, eX, eY );
            
            // Draw side-0 indicator at linedef half-point.
            if ( line.sidenum[0] > 0 ) {
                g.setColor(new Color(255,100,100));
                g.setStroke(new BasicStroke(1.0f));

                // Draw tick perpendicular at half point. 
                int mX = (sX + eX)/2;
                int mY = (sY + eY)/2;
                //double pAngle = -angle(s.x, s.y, e.x, e.y);
                double pAngle = -angle(sX, sY, eX, eY);
                
                int pX = (int)(mX + Math.sin(pAngle) * 10);
                int pY = (int)(mY + Math.cos(pAngle) * 10);
                
                g.drawLine( mX, mY, pX, pY );
            }
            
            // Draw side-1 indicator at linedef half-point.
            if ( line.sidenum[1] > 0 ) {
                g.setColor(new Color(100,100,255));
                g.setStroke(new BasicStroke(1.0f));

                // Draw tick perpendicular at half point. 
                int mX = (sX + eX)/2;
                int mY = (sY + eY)/2;
                //double pAngle = -angle(s.x, s.y, e.x, e.y);
                double pAngle = -angle(sX, sY, eX, eY);
                
                int pX = (int)(mX + Math.sin(pAngle) * -10);
                int pY = (int)(mY + Math.cos(pAngle) * -10);
                
                g.drawLine( mX, mY, pX, pY );
            }
            
        }
        
        // Draw vertex start points as red boxes.
        int boxSize = 6;
        g.setColor(Color.red);
        
        for ( Vertex v : vList ) {
            g.fillRect( 
                    (v.x>>FRACBITS) /* + xOff */ - (boxSize/2),// + margin, 
                    (v.y>>FRACBITS) /* + yOff */ - (boxSize/2),// + margin , 
                    boxSize, boxSize);
        }
                
        // Draw Things
        MapThing[] things = parent.getThings().toArray();
        for (MapThing t : things) {
            if ( t.type == 1 ) {
                g.setColor(Color.GREEN);  // Player 1
            } else {
                g.setColor(Color.ORANGE);
            }
            g.fillRect( t.x, t.y, 12, 12);
        }
        
        
        // Draw a cross hair at Map 0,0
        g.setColor(Color.CYAN);
        g.setStroke(new BasicStroke(3.0f));
        g.drawLine(  0, -10,  0, 10 );
        g.drawLine(-10,   0, 10,  0 );
        
        return img.getScaledInstance(imgWidth, -1, Image.SCALE_AREA_AVERAGING);
    }

    private static double angle(double x1, double y1, double x2, double y2) {
        double xdiff = x1 - x2;
        double ydiff = y1 - y2;
        
        return  Math.atan2(ydiff, xdiff);
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
