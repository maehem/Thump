/*

http://doomwiki.org/wiki/ENDOOM

 */
package thump.wad.lump;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 *
 * @author mark
 */
public class EndDoomLump extends Lump {

    //  25 lines of 80 characters each plus color info.
    public final byte[] text = new byte[80 * 25];
    public final int[] tColor = new int[80 * 25];

    public EndDoomLump(FileChannel fc, String name, int filepos, int size) throws IOException {
        super(name, filepos, size);
        fc.position(filepos);
        ByteBuffer bb = ByteBuffer.allocate(80 * 25 * 2);
        fc.read(bb);
        bb.position(0);

        for (int p = 0; p < 80 * 25; p++) {
            text[p] = (byte) (bb.get() & 0x000000FF);
            tColor[p] = bb.get() & 0x000000FF;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (int i=0; i<80*25; i++) {
            sb.append(new String(new byte[]{text[i]}));
            if ( i%80 == 0 ) {
                sb.append("\n");
            }
        }
        
        return sb.toString();
//        try {
//            return new String(text, "UTF-8");
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(EndDoomLump.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return "oops!";
    }

    public Image getAsImage(int width, int height) {
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
        
        byte[] bytes = text;
        for (int i = 0; i<80*25; i++) {
            // TODO:  Set text color and BG.

            int x = col * colWidth;
            int y = row * rowHeight;
            
            g.setColor(getColor((tColor[i]&0x70)>>4)); // Background colors (bits 6..4)
            g.fillRect(x, y/*-rowHeight*/, colWidth, rowHeight);
            
            g.setColor(getColor(tColor[i]&0xF)); // Foreground colors (bits 3..0 )
            
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

    private Color getColor(int i) {
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
}
