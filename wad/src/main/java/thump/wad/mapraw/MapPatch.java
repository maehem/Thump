/*
 Texture MapPatch
 A single patchNum from a texture definition,
 basically a rectangular area within
 the texture rectangle.

    Offset Length Name     Content
    0x00   2      originx  A short int defining the horizontal offset of the patchNum relative to the upper-left of the texture
    0x02   2      originy  A short int defining the vertical offset of the patchNum relative to the upper-left of the texture
    0x04   2      patchNum A short int defining the patchNum number (as listed in PNAMES) to draw
    0x06   2      stepdir  A short int defining ?
    0x08   2      colormap A short int defining ?

*/
package thump.wad.mapraw;

import java.nio.ByteBuffer;

/**
 *
 * @author mark
 */
public class MapPatch {

    private final int x;
    private final int y;
    private final int patchNum;
    private final int setDir;    // Unused
    private final int colorMap;  // Unused

    public MapPatch(ByteBuffer bb) {
        x = bb.getShort();
        y = bb.getShort();
        patchNum = bb.getShort();
        setDir = bb.getShort();
        colorMap = bb.getShort();
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getPatchNum() {
        return patchNum;
    }

    @Override
    public String toString() {
        return "x,y: " + x + "," + y + " n: "+ patchNum;
    } 
    
}