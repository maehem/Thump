/*
 * Video/Image Functions
 */
package thump.render;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import thump.base.BoundingBox;
import thump.base.Defines;
import static thump.base.Defines.SCREENHEIGHT;
import static thump.base.Defines.SCREENWIDTH;
import thump.wad.Wad;
import thump.wad.lump.Lump;
import thump.wad.lump.PictureLump;
import thump.wad.mapraw.Column;
import thump.wad.mapraw.PatchData;

/**
 *
 * @author mark
 */
public class Video {

    public Video() {
        // TODO this will be just one screen image.
        for (int i = 0; i < 5; i++) {
            //screens[i] = base + i*SCREENWIDTH*SCREENHEIGHT;
            //screens[i] = new byte[SCREENWIDTH*SCREENHEIGHT];
            screenImage[i] = new BufferedImage(SCREENWIDTH, SCREENHEIGHT, BufferedImage.TYPE_INT_ARGB);
        }
    }

    static final Logger logger = Defines.logger;

    // TODO screenImage can be objects that represent images?
    // I think we only need one of these [0]
    public BufferedImage[] screenImage = new BufferedImage[5];

    //public     int[][]    screens = new int[5][];  // 5
    // We render all stuff to these Screen objects. They are pre-PlayPal
    // byte values.   Screen[0] is the main screen that read and colormapping
    // applied during game render loop.
    // Other Screens are temp buffers for holding or doing special screen effects.
    public Screen screens[] = new Screen[5];

    // Never seems to be used.  Only added to.
    //private     int	dirtybox[] = new int[4];  //4
    public final BoundingBox dirtybox = new BoundingBox(0, 0, 0, 0);

    //private     byte gammatable[5][256];
    public int usegamma;

    // Now where did these came from?
    public int gammatable[][]
            = {
                {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
                    17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                    33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48,
                    49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64,
                    65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80,
                    81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96,
                    97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112,
                    113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128,
                    128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143,
                    144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159,
                    160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175,
                    176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191,
                    192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207,
                    208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223,
                    224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239,
                    240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 254, 255},
                {2, 4, 5, 7, 8, 10, 11, 12, 14, 15, 16, 18, 19, 20, 21, 23, 24, 25, 26, 27, 29, 30, 31,
                    32, 33, 34, 36, 37, 38, 39, 40, 41, 42, 44, 45, 46, 47, 48, 49, 50, 51, 52, 54, 55,
                    56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 69, 70, 71, 72, 73, 74, 75, 76, 77,
                    78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98,
                    99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114,
                    115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 129,
                    130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145,
                    146, 147, 148, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160,
                    161, 162, 163, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175,
                    175, 176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 186, 187, 188, 189,
                    190, 191, 192, 193, 194, 195, 196, 196, 197, 198, 199, 200, 201, 202, 203, 204,
                    205, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 214, 215, 216, 217, 218,
                    219, 220, 221, 222, 222, 223, 224, 225, 226, 227, 228, 229, 230, 230, 231, 232,
                    233, 234, 235, 236, 237, 237, 238, 239, 240, 241, 242, 243, 244, 245, 245, 246,
                    247, 248, 249, 250, 251, 252, 252, 253, 254, 255},
                {4, 7, 9, 11, 13, 15, 17, 19, 21, 22, 24, 26, 27, 29, 30, 32, 33, 35, 36, 38, 39, 40, 42,
                    43, 45, 46, 47, 48, 50, 51, 52, 54, 55, 56, 57, 59, 60, 61, 62, 63, 65, 66, 67, 68, 69,
                    70, 72, 73, 74, 75, 76, 77, 78, 79, 80, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93,
                    94, 95, 96, 97, 98, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112,
                    113, 114, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128,
                    129, 130, 131, 132, 133, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144,
                    144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 153, 154, 155, 156, 157, 158, 159,
                    160, 160, 161, 162, 163, 164, 165, 166, 166, 167, 168, 169, 170, 171, 172, 172, 173,
                    174, 175, 176, 177, 178, 178, 179, 180, 181, 182, 183, 183, 184, 185, 186, 187, 188,
                    188, 189, 190, 191, 192, 193, 193, 194, 195, 196, 197, 197, 198, 199, 200, 201, 201,
                    202, 203, 204, 205, 206, 206, 207, 208, 209, 210, 210, 211, 212, 213, 213, 214, 215,
                    216, 217, 217, 218, 219, 220, 221, 221, 222, 223, 224, 224, 225, 226, 227, 228, 228,
                    229, 230, 231, 231, 232, 233, 234, 235, 235, 236, 237, 238, 238, 239, 240, 241, 241,
                    242, 243, 244, 244, 245, 246, 247, 247, 248, 249, 250, 251, 251, 252, 253, 254, 254,
                    255},
                {8, 12, 16, 19, 22, 24, 27, 29, 31, 34, 36, 38, 40, 41, 43, 45, 47, 49, 50, 52, 53, 55,
                    57, 58, 60, 61, 63, 64, 65, 67, 68, 70, 71, 72, 74, 75, 76, 77, 79, 80, 81, 82, 84, 85,
                    86, 87, 88, 90, 91, 92, 93, 94, 95, 96, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107,
                    108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124,
                    125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 135, 136, 137, 138, 139, 140,
                    141, 142, 143, 143, 144, 145, 146, 147, 148, 149, 150, 150, 151, 152, 153, 154, 155,
                    155, 156, 157, 158, 159, 160, 160, 161, 162, 163, 164, 165, 165, 166, 167, 168, 169,
                    169, 170, 171, 172, 173, 173, 174, 175, 176, 176, 177, 178, 179, 180, 180, 181, 182,
                    183, 183, 184, 185, 186, 186, 187, 188, 189, 189, 190, 191, 192, 192, 193, 194, 195,
                    195, 196, 197, 197, 198, 199, 200, 200, 201, 202, 202, 203, 204, 205, 205, 206, 207,
                    207, 208, 209, 210, 210, 211, 212, 212, 213, 214, 214, 215, 216, 216, 217, 218, 219,
                    219, 220, 221, 221, 222, 223, 223, 224, 225, 225, 226, 227, 227, 228, 229, 229, 230,
                    231, 231, 232, 233, 233, 234, 235, 235, 236, 237, 237, 238, 238, 239, 240, 240, 241,
                    242, 242, 243, 244, 244, 245, 246, 246, 247, 247, 248, 249, 249, 250, 251, 251, 252,
                    253, 253, 254, 254, 255},
                {16, 23, 28, 32, 36, 39, 42, 45, 48, 50, 53, 55, 57, 60, 62, 64, 66, 68, 69, 71, 73, 75, 76,
                    78, 80, 81, 83, 84, 86, 87, 89, 90, 92, 93, 94, 96, 97, 98, 100, 101, 102, 103, 105, 106,
                    107, 108, 109, 110, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124,
                    125, 126, 128, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141,
                    142, 143, 143, 144, 145, 146, 147, 148, 149, 150, 150, 151, 152, 153, 154, 155, 155,
                    156, 157, 158, 159, 159, 160, 161, 162, 163, 163, 164, 165, 166, 166, 167, 168, 169,
                    169, 170, 171, 172, 172, 173, 174, 175, 175, 176, 177, 177, 178, 179, 180, 180, 181,
                    182, 182, 183, 184, 184, 185, 186, 187, 187, 188, 189, 189, 190, 191, 191, 192, 193,
                    193, 194, 195, 195, 196, 196, 197, 198, 198, 199, 200, 200, 201, 202, 202, 203, 203,
                    204, 205, 205, 206, 207, 207, 208, 208, 209, 210, 210, 211, 211, 212, 213, 213, 214,
                    214, 215, 216, 216, 217, 217, 218, 219, 219, 220, 220, 221, 221, 222, 223, 223, 224,
                    224, 225, 225, 226, 227, 227, 228, 228, 229, 229, 230, 230, 231, 232, 232, 233, 233,
                    234, 234, 235, 235, 236, 236, 237, 237, 238, 239, 239, 240, 240, 241, 241, 242, 242,
                    243, 243, 244, 244, 245, 245, 246, 246, 247, 247, 248, 248, 249, 249, 250, 250, 251,
                    251, 252, 252, 253, 254, 254, 255, 255}
            };

    public Graphics getGraphics(int sNum) {
        return screenImage[sNum].getGraphics();
    }

    /**
     * markRect
     *
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void markRect(int x, int y, int width, int height) {
        //M_AddToBox(dirtybox, sx, sy);
        dirtybox.M_AddToBox(x, y);
        //M_AddToBox(dirtybox, sx + width - 1, sy + height - 1);
        dirtybox.M_AddToBox(x + width - 1, y + height - 1);
    }

    /**
     * copyRect
     *
     * @param srcx
     * @param srcy
     * @param srcscrn
     * @param width
     * @param height
     * @param destx
     * @param desty
     * @param destscrn
     */
    public void copyRect(int srcx,
            int srcy,
            int srcscrn,
            int width,
            int height,
            int destx,
            int desty,
            int destscrn) {
//        int	src;
//        int	dest; 

        if (srcx < 0
                || srcx + width > SCREENWIDTH
                || srcy < 0
                || srcy + height > SCREENHEIGHT
                || destx < 0 || destx + width > SCREENWIDTH
                || desty < 0
                || desty + height > SCREENHEIGHT
                || srcscrn > 4
                || destscrn > 4) {
            //I_Error ("Bad V_CopyRect");
            Defines.logger.severe("Bad Video.copyRect()");
        }
        markRect(destx, desty, width, height);

        int dx = destx;
        for (int sx = srcx; sx < srcx + width; sx++) {
            int dy = desty;
            for (int sy = srcy; sy < srcy + height; sy++) {
                try {
                    int pixel = screens[srcscrn].area[sx + (sy * SCREENWIDTH)];
                    if (pixel >= 0) {  // Transparency is -1 == pixel value.
                        screens[destscrn].area[dx + (dy * SCREENWIDTH)] = pixel;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                    //Ignore out of bounds if the dest image is off an edge.
                }
                dy++;
            }
            dx++;
        }

//        src = screenImage[srcscrn]+SCREENWIDTH*srcy+srcx; 
//        dest = screenImage[destscrn]+SCREENWIDTH*desty+destx; 
//
//        for ( ; height>0 ; height--) 
//        { 
////TODO            memcpy (dest, src, width); 
////            src += SCREENWIDTH; 
////            dest += SCREENWIDTH; 
//        } 
    }

    /**
     * Draws a column based masked pic to the screen.
     *
     * @param dx
     * @param dy
     * @param scrn
     * @param patch
     */
    public void drawPatch(
            int dx,
            int dy,
            int scrn,
            PatchData patch
    ) {
        //int		count;
        //int		col; 
        //Column	column; 
        //int         desttop = 0;
        //int         dest;
        //int         source; 
        //int		w; 

        int x = dx;
        int y = dy;

        y -= patch.topOffset;
        x -= patch.leftOffset;
        if (x < 0
                || x + patch.width > SCREENWIDTH
                || y < 0
                || y + patch.height > SCREENHEIGHT
                || scrn > 4) {
            logger.log(Level.WARNING, "Patch at {0},{1} exceeds LFB", new Object[]{x, y});
            // No I_Error abort - what is up with TNT.WAD?
            logger.log(Level.WARNING, "Video.drawPatch(): bad patch (ignored)");
            return;
        }

        if (scrn <= 0) {
            markRect(x, y, patch.width, patch.height);
        }

//        BufferedImage src = (BufferedImage) patch.getColorImage(0/*Stats.getInstance().wad.getPlayPalLump().paletteList.get(0)*/);
//        //BufferedImage dest = screenImage[scrn];
//        Graphics g = getGraphics(scrn);
//        //Graphics g = VideoInterface.getInstance().getStrategy().getDrawGraphics();
//        
//        g.drawImage(src, sx, sy, null);
//        g.dispose();
        int destX = x;
        Column columns[] = patch.pixelData;
        for (Column column : columns) {
            //column.draw(screens[scrn], destX, y);
            drawColumn(column, screens[scrn], x, dy);
            destX++;
        }

        /*
        col = 0;
        // desttop should be a screen object
        //  Need to solve this!!!!!!!
        //    desttop = screenImage[scrn]+sy*SCREENWIDTH+sx;
        w = patch.width;
        // sx is not used in the loop
        for ( ; col<w ; sx++, col++, desttop++) {
        column = patch.pixelData[col];
        // step through the posts in a column
        Iterator<Post> posts = column.posts.iterator();
        while(posts.hasNext()) { //while (column.topdelta != 0xff )
        Post post = posts.next();
        // Look at column.getValsAlpha()
        // For a hint to this.
        //	    source = (byte *)column + 3;
        dest = desttop + column.topdelta*SCREENWIDTH;
        //count = column.posts.size();
        // For each pixel in post draw it to dest col.
        count = post.pixels.length;
        while (count>0) {
        count--;
        //		*dest = *source++;    // Copy byte at source into dest
        dest += SCREENWIDTH;  // Next row
        }
        //	    column = (column_t *)(  (byte *)column + column.length + 4 );
        }
        }
         */
    }

//
// V_DrawPatchFlipped 
// Masks a column based masked pic to the screen.
// Flips horizontally, e.g. to mirror face.
//
    public void drawPatchFlipped(int dx,
            int dy,
            int scrn,
            PatchData patch) //patch_t*	patch ) 
    {

        int count;
        int col;
        Column column;
//    byte*	desttop;
//    byte*	dest;
//    byte*	source; 
//    int		w; 

        int x = dx;
        int y = dy;

        y -= patch.topOffset;
        x -= patch.leftOffset;
        if (x < 0
                || x + patch.width > SCREENWIDTH
                || y < 0
                || y + patch.height > SCREENHEIGHT
                || scrn > 4) {
//      fprintf( stderr, "PatchNot origin %d,%d exceeds LFB\n", sx,sy );
//      I_Error ("Bad V_DrawPatch in V_DrawPatchFlipped");
        }

        if (0 == scrn) {
            markRect(x, y, patch.width, patch.height);
        }

        Screen dest = screens[scrn];
        //int desttop = sy*SCREENWIDTH+sx; 

        int w = patch.width;

        //for( col=0 ; col<w ; col++) {
        for (col = w - 1; col != 0; col--) {
            //desttop++;
            //column = (column_t *)((byte *)patch + LONG(patch.columnofs[w-1-col])); 
            column = patch.pixelData[col];

            //column.draw(dest, x, dy);
            drawColumn(column, dest, x, dy);
            
            // step through the posts in a column 
//	while (column.topdelta != 0xff ) 
//	{ 
//	    source = (byte *)column + 3; 
//	    dest = desttop + column.topdelta*SCREENWIDTH; 
//	    count = column.length; 
//			 
//	    while (count--) 
//	    { 
//		*dest = *source++; 
//		dest += SCREENWIDTH; 
//	    } 
//	    column = (column_t *)(  (byte *)column + column.length 
//				    + 4 ); 
//            sx++;
//	} 
        }
    }

    public void drawPatchDirect(int x, int y, int scrn, String lumpName, Wad wad) {
        if (lumpName == null || "".equals(lumpName)) {
            return;
        }

        Lump lump = wad.findByName(lumpName);
        if (lump instanceof PictureLump) {
            drawPatchDirect(x, y, scrn, ((PictureLump) lump).pic, wad);
        }
    }

    //
    // V_DrawPatchDirect
    // Draws directly to the screen on the pc. 
    //
    public void drawPatchDirect(
            int x, int y, 
            int scrn,
            PatchData patchData,
            Wad wad) {
        //MapPatch patch) {
        //drawPatch (sx,sy,scrn, patch);
        /*
        int		count;
        int		col;
        column_t*	column; 
        byte*	desttop;
        byte*	dest;
        byte*	source;
        int		w;
        sy -= SHORT(patch.originy);
        sx -= SHORT(patch.originx);
        #ifdef RANGECHECK 
        if (sx<0
        ||sx+SHORT(patch.width) >SCREENWIDTH
        || sy<0
        || sy+SHORT(patch.height)>SCREENHEIGHT
        || (unsigned)scrn>4)
        {
        I_Error ("Bad V_DrawPatchDirect");
        }
        #endif 
         */
        //BufferedImage src = (BufferedImage) patchData.getColorImage(0/*Stats.getInstance().wad.getPlayPalLump().paletteList.get(0)*/);
        BufferedImage src = PatchData2Image(patchData, wad.getPlayPalLump().paletteList.get(0));
        //BufferedImage dest = screenImage[scrn];
        Graphics g = getGraphics(scrn);
        //Graphics g = VideoInterface.getInstance().getStrategy().getDrawGraphics();

        g.drawImage(src, x, y, null);
        g.dispose();


        /*
//	V_MarkRect (sx, sy, SHORT(patch.width), SHORT(patch.height)); 
        desttop = destscreen + sy*SCREENWIDTH/4 + (sx>>2); 

        w = SHORT(patch.width); 
        for ( col = 0 ; col<w ; col++) 
        { 
            outp (SC_INDEX+1,1<<(sx&3)); 
            column = (column_t *)((byte *)patch + LONG(patch.columnofs[col])); 

            // step through the posts in a column 

            while (column.topdelta != 0xff ) 
            { 
                source = (byte *)column + 3; 
                dest = desttop + column.topdelta*SCREENWIDTH/4; 
                count = column.length; 

                while (count--) 
                { 
                    *dest = *source++; 
                    dest += SCREENWIDTH/4; 
                } 
                column = (column_t *)(  (byte *)column + column.length 
                                        + 4 ); 
            } 
            if ( ((++sx)&3) == 0 ) 
                desttop++;	// go to next byte, not next plane 
        }*/
    }

    // V_DrawBlock
    // Draw a linear block of pixels into the view buffer.
    //
    public void drawBlock(int x,
            int y,
            int scrn,
            int width,
            int height,
            Screen src) {
        //BufferedImage dest;

        if (x < 0
                || x + width > SCREENWIDTH
                || y < 0
                || y + height > SCREENHEIGHT
                || scrn > 4) {
            //SystemInterface.I_Error("Bad Video_DrawBlock");
            logger.log(Level.SEVERE, "Bad Video_DrawBlock");
        }

        markRect(x, y, width, height);

        //BufferedImage dest = screenImage[scrn];
//TODO            dest = screenImage[scrn] + sy * SCREENWIDTH + sx;
        //Graphics g = dest.getGraphics();
        //Graphics g = VideoInterface.getInstance().getStrategy().getDrawGraphics();
//        Graphics g = getGraphics(scrn);
//        g.drawImage(src, sx, sy, null);
//        g.dispose();
        int sx = 0;
        for (int dx = x; dx < x + width; dx++) {
            sx++;
            int sy = 0;
            for (int dy = y; dy < y + height; dy++) {
                sy++;
                try {
                    screens[scrn].area[dx + dy * SCREENWIDTH] = src.area[sx + sy * SCREENWIDTH];
                } catch (ArrayIndexOutOfBoundsException ex) {
                    //Ignore out of bounds if the dest image is off an edge.
                }
            }
        }

//            int h = height;
//            while (h > 0) {
//                h--;
//    //TODO            memcpy(dest, src, width);
//    //TODO            src += width;
//    //TODO            dest += SCREENWIDTH;
//            }
    }

    // Only for screens[a] to screens[b] copy.
    private void memcpy(Screen destScrn, int dIx, Screen srcScrn, int sIx, int width) {
        int dx = dIx;
        for (int i = sIx; i < sIx + width; i++) {
            destScrn.area[dx] = srcScrn.area[i];
            dx++;
        }
    }

    //
    // V_GetBlock
    // Gets a linear block of pixels from the view buffer.
    //
    void getBlock(
            int x,
            int y,
            int scrn,
            int width,
            int height,
            Screen dest) {
        //Object	src; 

        if (x < 0
                || x + width > SCREENWIDTH
                || y < 0
                || y + height > SCREENHEIGHT
                || scrn > 4) {
            //SystemInterface.I_Error("Bad Video_GetBlock");
            logger.severe("Video::getBlock():  Bad getBlock");
        }

//        BufferedImage src = screenImage[scrn];
//        
//        Graphics g = dest.getGraphics();
//
//        g.drawImage(src, sx, sy, null);
//        g.dispose();
//TODO        src = screenImage[scrn] + sy*SCREENWIDTH+sx;
        int si = y * SCREENWIDTH + x;
        int di = 0;

        int h = height;
        while (h > 0) {
            h--;
            memcpy(dest, di, screens[scrn], si, width);
            si += SCREENWIDTH;
            di += width;
        }
    }

    //
    // V_Init
    // 
    public void init() {
        logger.log(Level.CONFIG, "Allocate screens.");

        // <nostalgia>stick these in low dos memory on PCs</nostalgia>
        //byte[] base = SystemInterface.I_AllocLow (SCREENWIDTH*SCREENHEIGHT*4);
        for (int i = 0; i < screens.length; i++) {
            screens[i] = new Screen();
        }
    }

//    public void clearScreen(int i) {
//        Graphics g = getGraphics(i);
//        //g.setColor(new Color(0, 0, 0, 255));
//        g.clearRect(0, 0, screenImage[i].getWidth(), screenImage[i].getHeight());
//        g.dispose();
//    }
//    public void clearScreenRegion(int screen, int sx, int sy, int w, int h) {
//        Graphics g = getGraphics(screen);
//        g.clearRect(sx, sy, w, h);
//        g.dispose();
//    }
//
    public void copyScreen(int s, int d) {
        memcpy(screens[d], 0, screens[s], 0, SCREENHEIGHT * SCREENWIDTH);
    }

    public void drawColumn( Column col, Screen screen, int x, int dy ) {
        //int		frac;
        //int		fracstep;
        int count = col.height-1; 

        // Zero length, column does not exceed a pixel.
        if (count < 0) {
            return;
        } 
        int y = 0; //ylookup[dc_yl];
        //int x = columnofs[dc_x];  


        int [] vals = col.getRawVals();
        //fracstep = 1; //dc_iscale; 
        //frac = dc_texturemid + (dc_yl-renderer.centery)*fracstep; 
                        
        do {
            if (y>=SCREENHEIGHT) {
                return;
            }
            try {
                if (vals[y]>=0) { // Transparency is -1 so don't draw for negative value.
                    screen.area[dy*SCREENWIDTH+x] = vals[y];
                }
            y++;
            dy++;
            //frac += fracstep;
            } catch (ArrayIndexOutOfBoundsException ex ) {
                // chicken!
                int i=0;
            }
            count--;
        } while (count>0); 
    }
    
    private static BufferedImage PatchData2Image(PatchData data, int[] palette) {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        for (int x=0; x<64; x++) {  // Column
            int[] col =  data.pixelData[x].getRawVals();
            for ( int y = 0; y< 64; y++) {
                //int pp = pixels[x*y]&0xFF;
                int pp = col[y]&0xFF;

                //Color[] palette = wad.getPlayPalLump().paletteList.get(0);
                int c = palette[pp];
                //int cc = 0xFF<<24 | (c.getRed()&0xFF)<<16 | (c.getGreen()&0xFF)<<8 | c.getBlue()&0xFF;
                img.setRGB(x, y, c );
            }
        }

        return img;
    }
        
}
