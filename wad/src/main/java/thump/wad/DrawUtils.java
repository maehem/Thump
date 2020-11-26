package thump.wad;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import static thump.base.Defines.logger;
import static thump.base.FixedPoint.FRACBITS;
import thump.wad.lump.EndDoomLump;
import thump.wad.lump.LineDefsLump;
import thump.wad.lump.MapLump;
import thump.wad.lump.PlaypalLump;
import thump.wad.map.Flat;
import thump.wad.map.Line;
import thump.wad.map.Vertex;
import thump.wad.mapraw.Column;
import thump.wad.mapraw.MapTexture;
import thump.wad.mapraw.MapThing;
import thump.wad.mapraw.PatchData;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mark
 */
public class DrawUtils {
    
    /**
     * Draw the map and details into an image.
     * @return 
     */
    public static Image getMapImage(MapLump mapLump) {
        // Get a base image with lines and vertex drawn.
        //Image img = this.lineDefs.getImage(1200);
        Image img = DrawUtils.getLineDefImage(mapLump.getLineDefs(), 1200);
        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null)+(2*mapLump.margin), img.getHeight(null)+(2*mapLump.margin), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.setColor(Color.BLACK);
        bGr.fillRect(0, 0, bimage.getWidth(), bimage.getHeight());
        bGr.setStroke(new BasicStroke(5));
        bGr.drawRect(2, 2, bimage.getWidth()-2, bimage.getHeight()-2);
        bGr.setColor(Color.BLUE);
        bGr.drawImage(img, mapLump.margin, mapLump.margin, null);
        
        // Draw
        bGr.dispose();

        return bimage;        
    }

    public static Image getLineDefImage(LineDefsLump lineDefs, int imgWidth ) {
        if ( !lineDefs.extentsKnown ) {
            lineDefs.computeExtents();  // Just need to do this once
        }
        
        int xOff = -lineDefs.xMin>>FRACBITS;
        int yOff = -lineDefs.yMin>>FRACBITS;
        
        int width = (lineDefs.xMax-lineDefs.xMin)>>FRACBITS;
        int height = (lineDefs.yMax-lineDefs.yMin)>>FRACBITS;
        
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        logger.log(Level.CONFIG, "Image: {0} x {1}  xMin: {2}  yMin: {3}  xMax: {4}  yMax: {5} OffsetX: {6}  OffsetY: {7}\n", 
                new Object[]{width, height, 
                    lineDefs.xMin>>FRACBITS, lineDefs.yMin>>FRACBITS, 
                    lineDefs.xMax>>FRACBITS, lineDefs.yMax>>FRACBITS, 
                    xOff, yOff});
        
        // Draw from minX and minY points.
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.translate(xOff, yOff);
        
        // Fill the background with black.
        g.setColor(Color.black);
        g.fillRect(0, 0, img.getWidth()-1, img.getHeight()-1);
                
        Vertex[] vList = lineDefs.parent.getVertexes().getVertexList();
        
        Iterator<Line> lineList = lineDefs.getLineDefList();
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
        MapThing[] things = lineDefs.parent.getThings().toArray();
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
    
    public static Image getTextureImage(Wad wad, MapTexture mt) {
        //if ( img == null ) {
        
        BufferedImage    img = new BufferedImage(mt.width, mt.height, BufferedImage.TYPE_INT_ARGB);

        Graphics g = img.getGraphics();

//            for ( MapPatch p: mt.patches ) {
//                PatchData patch = wad.patchesLump.getPatch(p.getPatchNum());
//                //g.drawImage(patches.getImage(), p.getX(), p.getY(), null);
//                g.drawImage(getRawPatchImage(patch), p.getX(), p.getY(), null);
//            }
        //}
        PatchData patch = mt.getPatch();
        g.drawImage(getRawPatchImage(patch), 0, 0, null);
        
        return img;
    }

    public static Image getRawPatchImage(PatchData pd) {
        BufferedImage img = new BufferedImage(pd.width, pd.height, BufferedImage.TYPE_BYTE_GRAY);
        int x = 0;
        for (Column col : pd.pixelData) {
            int[] vals = col.getRawVals();
            for (int y = 0; y < vals.length; y++) {
                img.setRGB(x, y,
                        (byte) vals[y] << 16
                        | (byte) vals[y] << 8
                        | (byte) vals[y] & 0xFF);
            }
            x++;
        }

        return img;
    }
    
    public static Image getImage(Flat flat) {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        for ( int y = 0; y< 64; y++) {
            for (int x=0; x<64; x++) {
                int pp = flat.pixels[x*y]&0xFF;
                img.setRGB(x,y, 0xff<<24 | pp<<16 | pp<<8 | pp );
            }
        }
        
        return img;
    }
    
    public static Image getColorImage(Wad wad, Flat flat) {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        int[] palette = wad.getPlayPalLump().paletteList.get(0);
        for ( int y = 0; y< 64; y++) {
            for (int x=0; x<64; x++) {
                int pp = flat.pixels[x*y]&0xFF;

//                Color[] palette = wad.getPlayPalLump().paletteList.get(0);
//                Color c = palette[pp];
//                int cc = 0xFF<<24 | (c.getRed()&0xFF)<<16 | (c.getGreen()&0xFF)<<8 | c.getBlue()&0xFF;
//                img.setRGB(x, y, cc);

                //Color c = palette[pp];
                //int cc = 0xFF<<24 | (c.getRed()&0xFF)<<16 | (c.getGreen()&0xFF)<<8 | c.getBlue()&0xFF;
                img.setRGB(x, y, palette[pp]);
            }
        }

        return img;
    }
    
    public static Image getImageFor( int[] c ) {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        
        Graphics g = img.getGraphics();
        
        for (int x=0; x< 16; x++ ) {
            for ( int y=0; y< 16; y++ ) {
                g.setColor(new Color(c[x*y]));
                g.fillRect(x*4, y*4, 4, 4);
            }
        }
        
        return img;
    }
    
    public static Image getPreviewImage(PlaypalLump ppl) {
        BufferedImage img = new BufferedImage(640, 640, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        int MARGIN = 4;
        // For each palette draw the image spaced out in a grid.
        Iterator<int[]> palettes = ppl.getPalettes();
        for (int y=0; y< 4; y++ ) {
            for ( int x=0; x< 4; x++ ) {
                if (palettes.hasNext()) {
                    g.drawImage(getImageFor(palettes.next()), x*68+MARGIN, y*68+MARGIN, null);
                }
            }
        }
        
        return img;
    }
    
    /**
     * End Doom Lump Text screen render
     * @param edl
     * @param width
     * @param height
     * @return 
     */
    public static Image getAsImage(EndDoomLump edl, int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();

        float rowSpacing = height/34.0f;
        g.setFont(new Font("OCR A Std", Font.PLAIN, (int)rowSpacing));
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        //g.setFont(g.getFont().deriveFont(rowSpacing));
        
                
        int col = 0;
        int row = 0;
        
        int rowHeight = height/25;
        int colWidth = width/80;
        
        byte[] bytes = edl.text;
        for (int i = 0; i<80*25; i++) {
            // TODO:  Set text color and BG.

            int x = col * colWidth;
            int y = row * rowHeight;
            
            g.setColor(getColor((edl.tColor[i]&0x70)>>4)); // Background colors (bits 6..4)
            g.fillRect(x, y/*-rowHeight*/, colWidth, rowHeight);
            
            g.setColor(getColor(edl.tColor[i]&0xF)); // Foreground colors (bits 3..0 )
            
            // Look for Code page 737 (also known as CP 737) charcters and
            // attempt to draw them as they would have appeared on a VGA card.
            switch (bytes[i]&0xFF) {
                case 0xdf:
                    g.fillRect(x, y, colWidth, rowHeight); // Block
                    break;
                case 0xc4:
                    g.fillRect(x, y + (rowHeight/4), colWidth, rowHeight/8);  // Line
                    break;
                default:
                    g.drawBytes(bytes, i, 1, x, y+metrics.getAscent());
                    break;
            }
            //g.drawString("" + (col%10) , col*width/81, row*height/26);
            //g.drawBytes(new byte[]{(byte)(48)}, 0, 1, col*width/180, row*height/54);
            col++;
            if (col > 79) {
                col = 0;
                row++;
            }
        }

        g.setColor(Color.BLUE);
        g.drawRect(0, 0, width-1, height-1);

        return img;
    }

    private static Color getColor(int i) {
        switch (i) {
            case 0: return Color.BLACK;
            case 1: return Color.BLUE;
            case 2: return Color.GREEN;
            case 3: return Color.CYAN;
            case 4: return Color.RED;
            case 5: return Color.MAGENTA;
            case 6: return new Color( 139,69,19 );
            case 7: return Color.LIGHT_GRAY;
            case 8: return Color.DARK_GRAY;
            case 9: return Color.BLUE.brighter();
            case 10: return Color.GREEN.brighter();
            case 11: return Color.CYAN.brighter();
            case 12: return Color.RED.brighter();
            case 13: return Color.MAGENTA.brighter();
            case 14: return Color.YELLOW;
            default:
            case 15: return Color.WHITE;
        }
    }
    
    public static Image getColorImage(PatchData pd, ArrayList<int[]> paletteList, int paletteNum) {
        BufferedImage img = new BufferedImage(pd.width, pd.height, BufferedImage.TYPE_INT_ARGB);
            int x = 0;
            int[] palette = paletteList.get(paletteNum);
            Graphics g = img.getGraphics();
            for (Column col : pd.pixelData) {
                int[] vals = col.getRawVals();
                for (int y = 0; y < vals.length; y++) {
                    //Color[] palette = wad.getPlayPalLump().paletteList.get(0);
                    int val = vals[y];
                    int trans = 0xff;
                    if (val == -1) {
                        val = 0;
                        trans = 0x00;
                    }
                    int c = palette[val];
                    //int cc = trans << 24 | (c.getRed() & 0xFF) << 16 | (c.getGreen() & 0xFF) << 8 | c.getBlue() & 0xFF;
                    img.setRGB(x, y, c);
                }
                x++;
            }
        

        return img;
    }
}
