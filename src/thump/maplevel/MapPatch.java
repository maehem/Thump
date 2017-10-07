/*
 * Texture Patch
 * A single patch from a texture definition,
 * basically a rectangular area within
 * the texture rectangle.

Offset	Length	Name	Content
    0x00	2	originx	A short int defining the horizontal offset of the patchNum relative to the upper-left of the texture
    0x02	2	originy	A short int defining the vertical offset of the patchNum relative to the upper-left of the texture
    0x04	2	patchNum	A short int defining the patchNum number (as listed in PNAMES) to draw
    0x06	2	stepdir	A short int defining ?
    0x08	2	colormap	A short int defining ?
*/
package thump.maplevel;

import java.awt.Image;
import thump.global.Defines;
import thump.render.Patch;
import thump.wad.Wad;

/**
 *
 * @author mark
 */
public class MapPatch {
    public final int originx;
    public final int originy;
    public final int stepdir;
    public final int colormap;
    public final int patchNum;
    private Patch patch = null; // Use getPatch();
    private final Wad wad;

    public MapPatch(Wad wad, short originX, short originY, short patchNum, short stepDir, short colorMap) {
        this.wad = wad;
        this.originx = originX;
        this.originy = originY;
        this.patchNum = patchNum;
        this.stepdir = stepDir;
        this.colormap = colorMap;
    }

    public Patch getPatch() {
        if ( patch == null ) {
            String pName = wad.getPNames().patchNames[patchNum];
            patch = wad.getPatchByName(pName.trim());
            if (patch == null ) {
                Defines.logger.warning("Patch not found for \"" + pName + "\"\n");
            }
        }
        
        return patch;
    }
    
    
    public Image getImage() {       
        return getPatch().getColorImage(0/*wad.getPlayPalLump().paletteList.get(0)*/); // Might only want BW image and apply colormap later?
    }
    
    
}
