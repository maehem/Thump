/*
 * Drawing Functions
 */
package thump.render;

import java.util.logging.Level;
import static thump.base.Defines.SCREENHEIGHT;
import static thump.base.Defines.SCREENWIDTH;
import static thump.base.Defines.logger;
import static thump.base.FixedPoint.FRACBITS;
import thump.wad.Wad;
import thump.wad.lump.PictureLump;
import thump.wad.map.Flat;
import thump.wad.mapraw.Column;
import thump.wad.mapraw.PatchData;

/**
 *
 * @author mark
 */
public class Draw {
    
    // ?
    public static final int MAXWIDTH    = 1120;
    public static final int MAXHEIGHT   = 832;

    // status bar height at bottom of screen
    public static final int SBARHEIGHT  = 32;

    //
    // All drawing to the view buffer is accomplished in this file.
    // The other refresh files only know about ccordinates,
    //  not the architecture of the frame buffer.
    // Conveniently, the frame buffer is a linear one,
    //  and we need only the base address,
    //  and the total size == width*height*depth/8.,
    //

    public byte[]	viewimage; 
    public int		viewwidth;
    public int		scaledviewwidth;
    public int		viewheight;
    public int		viewwindowx;
    public int		viewwindowy; 
    public int          ylookup[] = new int[MAXHEIGHT]; 
    public int		columnofs[] = new int[MAXWIDTH]; 

    // Color tables for different players,
    //  translate a limited part to another
    //  (color ramps used for  suit colors).
    //
    byte	translations[][]= new byte[3][256];	

    // R_DrawColumn
    // Source is the top of the column to scale.
    //
    public byte[] dc_colormap;
    public int dc_x;
    public int dc_yl;
    public int dc_yh;
    
    public int dc_iscale;
    public int dc_texturemid;
    // fi

    // first pixel in a column (possibly virtual) 
    //byte[] dc_source;
    public Column dc_source;

    // just for profiling 
    int dccount;
    
    public final Renderer renderer;

    public Draw(Renderer renderer) {
        this.renderer = renderer;
    }

    
    /*
    //
    // A column is a vertical slice/span from a wall texture that,
    //  given the DOOM style restrictions on the view orientation,
    //  will always have constant z depth.
    // Thus a special case loop for very fast rendering can
    //  be used. It has also been used with Wolfenstein 3D.
    // 
    public void R_DrawColumn () 
    { 
        //byte[]		dest; 
        int		frac;
        int		fracstep;

        int count = dc_yh - dc_yl; 

        // Zero length, column does not exceed a pixel.
        if (count < 0) {
            return;
        } 

//    #ifdef RANGECHECK 
//        if ((unsigned)dc_x >= SCREENWIDTH
//            || dc_yl < 0
//            || dc_yh >= SCREENHEIGHT) 
//            I_Error ("R_DrawColumn: %i to %i at %i", dc_yl, dc_yh, dc_x); 
//    #endif 

        // Framebuffer destination address.
        // Use ylookup LUT to avoid multiply with ScreenWidth.
        // Use columnofs LUT for subwindows? 

        int y = ylookup[dc_yl];
        int x = columnofs[dc_x];  

        BufferedImage dest = Game.getInstance().video.screenImage[0];
        Graphics g = dest.getGraphics();
        // Determine scaling,
        //  which is the only mapping to be done.
        fracstep = dc_iscale; 
        frac = dc_texturemid + (dc_yl-renderer.centery)*fracstep; 

        // Inner loop that does the actual texture mapping,
        //  e.g. a DDA-lile scaling.
        // This is as fast as it gets.
        int[] colVals = dc_source.getRawVals();
        do {
            // Re-map color indices from wall texture column
            //  using a lighting/special effects LUT.
            
            
            //*dest = dc_colormap[dc_source[(frac>>FRACBITS)&127]];
            // Draw something at x, y?
            g.setColor(new Color(dc_colormap[colVals[(frac>>FRACBITS)&127]]));
            g.fillRect(x, y, 1, 1);
            y++; // Next row.

            //dest += SCREENWIDTH; 
            frac += fracstep;
        } while ((count--)>0); 
    } 
*/
    public void R_DrawColumn(Screen dest) {
        int		frac;
        int		fracstep;

        if (dc_x >= SCREENWIDTH || dc_yl < 0 || dc_yh >= SCREENHEIGHT ) {
            logger.log(Level.WARNING, "    R_DrawColumn: {0} to {1} at {2}", new Object[]{dc_yl, dc_yh, dc_x});
        }
        
        // Zero length, column does not exceed a pixel.
        int count = dc_yh - dc_yl; 
        if (count <= 0) {
            logger.log(Level.FINE, "    Short post. Nothing to draw.");
            return;
        } 
        int y = ylookup[dc_yl];
        int x = columnofs[dc_x];  

        int [] vals = dc_source.getRawVals();
        logger.log(Level.CONFIG, "    R_DrawColumn in buffer at:  x:{0}  yStart:{1} count:{2}", new Object[]{x,y/SCREENWIDTH, count});
        fracstep = dc_iscale; 
        frac = dc_texturemid + (dc_yl-renderer.centery)*fracstep; 
        
        do {
//            dest.area[x+y*SCREENWIDTH] = dc_colormap[vals[(frac>>FRACBITS)&127]];
            int index = (frac>>FRACBITS)&127;
            if ( index < vals.length ) {  // Ignore index outside of vals[] length.
                int val = vals[index];
                if ( val>=0 ) {  // val  -1  is clear
                    dest.area[x+y] = dc_colormap[val];
                }
            }
            //y++;
            y+=SCREENWIDTH;
            frac += fracstep;
        } while ((count--)>0);
        
    }

    /*


    // UNUSED.
    // Loop unrolled.
    #if 0
    void R_DrawColumn (void) 
    { 
        int			count; 
        byte[]		source;
        byte[]		dest;
        byte[]		colormap;

        unsigned		frac;
        unsigned		fracstep;
        unsigned		fracstep2;
        unsigned		fracstep3;
        unsigned		fracstep4;	 

        count = dc_yh - dc_yl + 1; 

        source = dc_source;
        colormap = dc_colormap;		 
        dest = ylookup[dc_yl] + columnofs[dc_x];  

        fracstep = dc_iscale<<9; 
        frac = (dc_texturemid + (dc_yl-centery)*dc_iscale)<<9; 

        fracstep2 = fracstep+fracstep;
        fracstep3 = fracstep2+fracstep;
        fracstep4 = fracstep3+fracstep;

        while (count >= 8) 
        { 
            dest[0] = colormap[source[frac>>25]]; 
            dest[SCREENWIDTH] = colormap[source[(frac+fracstep)>>25]]; 
            dest[SCREENWIDTH*2] = colormap[source[(frac+fracstep2)>>25]]; 
            dest[SCREENWIDTH*3] = colormap[source[(frac+fracstep3)>>25]];

            frac += fracstep4; 

            dest[SCREENWIDTH*4] = colormap[source[frac>>25]]; 
            dest[SCREENWIDTH*5] = colormap[source[(frac+fracstep)>>25]]; 
            dest[SCREENWIDTH*6] = colormap[source[(frac+fracstep2)>>25]]; 
            dest[SCREENWIDTH*7] = colormap[source[(frac+fracstep3)>>25]]; 

            frac += fracstep4; 
            dest += SCREENWIDTH*8; 
            count -= 8;
        } 

        while (count > 0)
        { 
            *dest = colormap[source[frac>>25]]; 
            dest += SCREENWIDTH; 
            frac += fracstep; 
            count--;
        } 
    }
    #endif

*/
    public void R_DrawColumnLow () { 
        int			count; 
        //byte[]		dest; 
        //byte[]		dest2;
        int		frac;
        int		fracstep;	 

        count = dc_yh - dc_yl; 

        // Zero length.
        if (count < 0) {
            return;
        } 

        logger.log(Level.CONFIG, "R_DrawColumnLow");
//    #ifdef RANGECHECK 
//        if ((unsigned)dc_x >= SCREENWIDTH
//            || dc_yl < 0
//            || dc_yh >= SCREENHEIGHT)
//        {
//
//            I_Error ("R_DrawColumn: %i to %i at %i", dc_yl, dc_yh, dc_x);
//        }
//        //	dccount++; 
//    #endif 
        // Blocky mode, need to multiply by 2.
        dc_x <<= 1;

        //dest = ylookup[dc_yl] + columnofs[dc_x];
        //dest2 = ylookup[dc_yl] + columnofs[dc_x+1];
        Screen dest = renderer.video.screens[0];

        int y = ylookup[dc_yl];
        int x = columnofs[dc_x];  
        int x2 = columnofs[dc_x+1];  

        //BufferedImage dest = Game.getInstance().video.screenImage[0];
        //Graphics g = dest.getGraphics();

        fracstep = dc_iscale; 
        frac = dc_texturemid + (dc_yl-renderer.centery)*fracstep;
        
        int[] colVals = dc_source.getRawVals();

        do {
            // Hack. Does not work corretly.
            dest.area[x+y] = dc_colormap[colVals[(frac>>FRACBITS)&127]];
            dest.area[x+1+y] = dc_colormap[colVals[(frac>>FRACBITS)&127]];
            
            y+=SCREENWIDTH;
            frac += fracstep; 

        } while ((count--)>0);
                        
    }

    //
    // Spectre/Invisibility.
    //
    public static final int FUZZTABLE		=50; 
    //public static final int FUZZOFF	= SCREENWIDTH;
    public static final int FUZZOFF	= 1;  // Effects value


    int	fuzzoffset[] =
    {
        FUZZOFF,-FUZZOFF,FUZZOFF,-FUZZOFF,FUZZOFF,FUZZOFF,-FUZZOFF,
        FUZZOFF,FUZZOFF,-FUZZOFF,FUZZOFF,FUZZOFF,FUZZOFF,-FUZZOFF,
        FUZZOFF,FUZZOFF,FUZZOFF,-FUZZOFF,-FUZZOFF,-FUZZOFF,-FUZZOFF,
        FUZZOFF,-FUZZOFF,-FUZZOFF,FUZZOFF,FUZZOFF,FUZZOFF,FUZZOFF,-FUZZOFF,
        FUZZOFF,-FUZZOFF,FUZZOFF,FUZZOFF,-FUZZOFF,-FUZZOFF,FUZZOFF,
        FUZZOFF,-FUZZOFF,-FUZZOFF,-FUZZOFF,-FUZZOFF,FUZZOFF,FUZZOFF,
        FUZZOFF,FUZZOFF,-FUZZOFF,FUZZOFF,FUZZOFF,-FUZZOFF,FUZZOFF 
    }; 

    int	fuzzpos = 0; 


    //
    // Framebuffer postprocessing.
    // Creates a fuzzy image by copying pixels
    //  from adjacent ones to left and right.
    // Used with an all black colormap, this
    //  could create the SHADOW effect,
    //  i.e. spectres and invisible players.
    //
    public void R_DrawFuzzColumn () 
    { 
        int			count; 
        //byte[]		dest; 
        int		frac;
        int		fracstep;	 

        logger.log(Level.CONFIG, "R_DrawFuzzColumn");
        // Adjust borders. Low... 
        if (dc_yl==0) {
            dc_yl = 1;
        }

        // .. and high.
        if (dc_yh == viewheight-1) {
            dc_yh = viewheight - 2;
        } 

        count = dc_yh - dc_yl; 

        // Zero length.
        if (count < 0) {
            return;
        } 


//    #ifdef RANGECHECK 
//        if ((unsigned)dc_x >= SCREENWIDTH
//            || dc_yl < 0 || dc_yh >= SCREENHEIGHT)
//        {
//            I_Error ("R_DrawFuzzColumn: %i to %i at %i",
//                     dc_yl, dc_yh, dc_x);
//        }
//    #endif
//

        // Keep till detailshift bug in blocky mode fixed,
        //  or blocky mode removed.
        // WATCOM code 
    //    if (detailshift)
    //    {
    //	if (dc_x & 1)
    //	{
    //	    outpw (GC_INDEX,GC_READMAP+(2<<8) ); 
    //	    outp (SC_INDEX+1,12); 
    //	}
    //	else
    //	{
    //	    outpw (GC_INDEX,GC_READMAP); 
    //	    outp (SC_INDEX+1,3); 
    //	}
    //	dest = destview + dc_yl*80 + (dc_x>>1); 
    //    }
    //    else
    //    {
    //	outpw (GC_INDEX,GC_READMAP+((dc_x&3)<<8) ); 
    //	outp (SC_INDEX+1,1<<(dc_x&3)); 
    //	dest = destview + dc_yl*80 + (dc_x>>2); 
    //    }


        // Does not work with blocky mode.
        //dest = ylookup[dc_yl] + columnofs[dc_x];
        //BufferedImage dest = Game.getInstance().video.screenImage[0];
        int y = ylookup[dc_yl];
        int x = columnofs[dc_x];
        Screen dest = renderer.video.screens[0];
        byte[][] colormaps = renderer.data.colormaps;

        // Looks familiar.
        fracstep = dc_iscale; 
        frac = dc_texturemid + (dc_yl-renderer.centery)*fracstep; 

        // Looks like an attempt at dithering, using the colormap #6 
        // (of 0-31, a bit brighter than average).
        do {
            // Lookup framebuffer, and retrieve
            //  a pixel that is either one column
            //  left or right of the current one.
            // Add index from colormap to index.
            //*dest = colormaps[6*256+dest[fuzzoffset[fuzzpos]]];
            //dest.area[x+y*SCREENWIDTH] = colormaps[6][dest.area[x*(y+fuzzoffset[fuzzpos])]];
            int destIdx = x+y;
            if ( destIdx >= SCREENWIDTH*SCREENHEIGHT ) {
                break;
            }
            int destFuzzIdx = destIdx + fuzzoffset[fuzzpos];  // Will be -1, 0 or +1;
            int fuzzVal = colormaps[6][ dest.area[destFuzzIdx]&0xFF ]&0xFF;
            dest.area[destIdx] = fuzzVal;
            
            //int name = dest.area[x+y+fuzzoffset[fuzzpos]];
            //dest.area[x+y] = colormaps[6][name&0xFF];
            
            //y++; // Next row.
            y+=SCREENWIDTH;

            fuzzpos++;
            // Clamp table lookup index.
            if (fuzzpos == FUZZTABLE) {
                fuzzpos = 0;
            }

            frac += fracstep; 
        } while ((count--)>0); 
    } 


    //
    // R_DrawTranslatedColumn
    // Used to draw player sprites
    //  with the green colorramp mapped to others.
    // Could be used with different translation
    //  tables, e.g. the lighter colored version
    //  of the BaronOfHell, the HellKnight, uses
    //  identical sprites, kinda brightened up.
    //
    public byte[]	dc_translation;
    public byte[]	translationtables;

    public void R_DrawTranslatedColumn() { 
        int		count; 
        //byte[]		dest;
        //int             destIdx = 0;
        int		frac;
        int		fracstep;	 

        count = dc_yh - dc_yl; 
        if (count < 0) {
            return;
        }
        logger.log(Level.CONFIG, "R_DrawTranslatedColumn");
//    #ifdef RANGECHECK 
//        if ((unsigned)dc_x >= SCREENWIDTH
//            || dc_yl < 0
//            || dc_yh >= SCREENHEIGHT)
//        {
//            I_Error ( "R_DrawColumn: %i to %i at %i",
//                      dc_yl, dc_yh, dc_x);
//        }
//
//    #endif 
        


        // WATCOM VGA specific.
        // Keep for fixing.
    //    if (detailshift)
    //    {
    //	if (dc_x & 1)
    //	    outp (SC_INDEX+1,12); 
    //	else
    //	    outp (SC_INDEX+1,3);
    //	
    //	dest = destview + dc_yl*80 + (dc_x>>1); 
    //    }
    //    else
    //    {
    //	outp (SC_INDEX+1,1<<(dc_x&3)); 
    //
    //	dest = destview + dc_yl*80 + (dc_x>>2); 
    //    }


        // FIXME. As above.
        //dest = ylookup[dc_yl] + columnofs[dc_x]; 
        int y = ylookup[dc_yl];
        int x = columnofs[dc_x];  

        Screen dest = renderer.video.screens[0];

        // Looks familiar.
        fracstep = dc_iscale; 
        frac = dc_texturemid + (dc_yl-renderer.centery)*fracstep; 

        int[] colVals = dc_source.getRawVals();

        // Here we do an additional index re-mapping.
        do {
            // Translation tables are used
            //  to map certain colorramps to other ones,
            //  used with PLAY sprites.
            // Thus the "green" ramp of the player 0 sprite
            //  is mapped to gray, red, black/indigo. 
            dest.area[x+y] = dc_colormap[dc_translation[colVals[frac>>FRACBITS]]];
            //dest += SCREENWIDTH;

            //frac += fracstep; 
            
            y+=SCREENWIDTH; // Next row.

            frac += fracstep;
            //count--;
        } while ((count--)>0); 
    } 




    //
    // R_InitTranslationTables
    // Creates the translation tables to map
    //  the green color ramp to gray, brown, red.
    // Assumes a given structure of the PLAYPAL.
    // Could be read from a lump instead.
    //
    public void R_InitTranslationTables() {

        //translationtables = Z_Malloc (256*3+255, PU_STATIC, 0);
        translationtables = new byte[256 * 3 + 255];
        //translationtables = (byte *)(( (int)translationtables + 255 )& ~255);

        // translate just the 16 green colors
        for (int i = 0; i < 256; i++) {
            if (i >= 0x70 && i <= 0x7f) {
                // map green ramp to gray, brown, red
                translationtables[i] = (byte) (0x60 + (i & 0xf));
                translationtables[i + 256] = (byte) (0x40 + (i & 0xf));
                translationtables[i + 512] = (byte) (0x20 + (i & 0xf));
            } else {
                // Keep all other colors as is.
                translationtables[i] = (byte) i;
                translationtables[i + 256] = (byte) i;
                translationtables[i + 512] = (byte) i;
            }
        }
    }



    //
    // R_DrawSpan 
    // With DOOM style restrictions on view orientation,
    //  the floors and ceilings consist of horizontal slices
    //  or spans with constant z depth.
    // However, rotation around the world z axis is possible,
    //  thus this mapping, while simpler and faster than
    //  perspective correct texture mapping, has to traverse
    //  the texture at an angle in all but a few cases.
    // In consequence, flats are not stored by column (like walls),
    //  and the inner loop has to step in texture space u and v.
    //
    int			ds_y; 
    int			ds_x1; 
    int			ds_x2;

    byte[]		ds_colormap; 

    int			ds_xfrac; 
    int			ds_yfrac; 
    int			ds_xstep; 
    int			ds_ystep;

    // start of a 64*64 tile image 
    int[]		ds_source;	// Should be an object!

    // just for profiling
    int			dscount;


    public void spanfunc() {
        if ( !renderer.detailshift ) {
            R_DrawSpan();
        } else {
            R_DrawSpanLow();
        }
    }
    
    //
    // Draws the actual span.
    public void R_DrawSpan ()  { 
        int		xfrac;
        int		yfrac; 
        //byte[]		dest; 
        int			spot; 

//    #ifdef RANGECHECK 
//        if (ds_x2 < ds_x1
//            || ds_x1<0
//            || ds_x2>=SCREENWIDTH  
//            || (unsigned)ds_y>SCREENHEIGHT)
//        {
//            I_Error( "R_DrawSpan: %i to %i at %i",
//                     ds_x1,ds_x2,ds_y);
//        }
//    //	dscount++; 
//    #endif 

        xfrac = ds_xfrac;
        yfrac = ds_yfrac;
        
        logger.log(Level.CONFIG, "Draw.R_DrawSpan( xfrac:{0},  yfrac:{1})", new Object[]{xfrac, yfrac});

        Screen dest = renderer.video.screens[0];
        //dest = ylookup[ds_y] + columnofs[ds_x1];
        if ( ds_x1 < 0 ) {
            int ii=0;  // debug breakpoint
        }
        int x = columnofs[ds_x1];
        int y = ylookup[ds_y];
        
        logger.log(Level.CONFIG, "start draw at y:{0}", y/SCREENWIDTH);

        // We do not check for zero spans here?
        int count = ds_x2 - ds_x1; 

        do {
            // Current texture index in u,v.
            spot = ((yfrac>>(16-6))&(63*64)) + ((xfrac>>16)&63);

            // Lookup pixel from flat texture tile,
            //  re-index using light/colormap.
            //*dest++ = ds_colormap[ds_source[spot]];
            //try{
                int ds_src = ds_source[spot];
                byte ds_cmap = ds_colormap[ds_src];
//                if (x+y*SCREENWIDTH > 64000 ) {
//                    int ddd=0;  // breakpoint;
//                }
            //dest.area[x+y*SCREENWIDTH] = ds_cmap;
            dest.area[x+y] = ds_cmap;

            //} catch (ArrayIndexOutOfBoundsException e) {
            //    int ddd=0;  // breakpoint
            //}
            x++;
            

            // Next step in u,v.
            xfrac += ds_xstep; 
            yfrac += ds_ystep;

            count--;
        } while (count>0); 
    } 


/*
    // UNUSED.
    // Loop unrolled by 4.
    #if 0
    void R_DrawSpan (void) 
    { 
        unsigned	position, step;

        byte[]	source;
        byte[]	colormap;
        byte[]	dest;

        unsigned	count;
        usingned	spot; 
        unsigned	value;
        unsigned	temp;
        unsigned	xtemp;
        unsigned	ytemp;

        position = ((ds_xfrac<<10)&0xffff0000) | ((ds_yfrac>>6)&0xffff);
        step = ((ds_xstep<<10)&0xffff0000) | ((ds_ystep>>6)&0xffff);

        source = ds_source;
        colormap = ds_colormap;
        dest = ylookup[ds_y] + columnofs[ds_x1];	 
        count = ds_x2 - ds_x1 + 1; 

        while (count >= 4) 
        { 
            ytemp = position>>4;
            ytemp = ytemp & 4032;
            xtemp = position>>26;
            spot = xtemp | ytemp;
            position += step;
            dest[0] = colormap[source[spot]]; 

            ytemp = position>>4;
            ytemp = ytemp & 4032;
            xtemp = position>>26;
            spot = xtemp | ytemp;
            position += step;
            dest[1] = colormap[source[spot]];

            ytemp = position>>4;
            ytemp = ytemp & 4032;
            xtemp = position>>26;
            spot = xtemp | ytemp;
            position += step;
            dest[2] = colormap[source[spot]];

            ytemp = position>>4;
            ytemp = ytemp & 4032;
            xtemp = position>>26;
            spot = xtemp | ytemp;
            position += step;
            dest[3] = colormap[source[spot]]; 

            count -= 4;
            dest += 4;
        } 
        while (count > 0) 
        { 
            ytemp = position>>4;
            ytemp = ytemp & 4032;
            xtemp = position>>26;
            spot = xtemp | ytemp;
            position += step;
            *dest++ = colormap[source[spot]]; 
            count--;
        } 
    } 
    #endif

*/
    //
    // Again..
    //
    public void R_DrawSpanLow () { 
        int		xfrac;
        int		yfrac; 
        //byte[]		dest; 
        //int			count;
        int			spot; 

//    #ifdef RANGECHECK 
//        if (ds_x2 < ds_x1
//            || ds_x1<0
//            || ds_x2>=SCREENWIDTH  
//            || (unsigned)ds_y>SCREENHEIGHT)
//        {
//            I_Error( "R_DrawSpan: %i to %i at %i",
//                     ds_x1,ds_x2,ds_y);
//        }
//    //	dscount++; 
//    #endif 

        xfrac = ds_xfrac; 
        yfrac = ds_yfrac; 

        // Blocky mode, need to multiply by 2.
        ds_x1 <<= 1;
        ds_x2 <<= 1;

        Screen dest = renderer.video.screens[0];
        //dest = ylookup[ds_y] + columnofs[ds_x1];
        int x = columnofs[ds_x1];
        int y = ylookup[ds_y];


        int count = ds_x2 - ds_x1; 
        do { 
            spot = ((yfrac>>(16-6))&(63*64)) + ((xfrac>>16)&63);
            // Lowres/blocky mode does it twice,
            //  while scale is adjusted appropriately.
            //*dest++ = ds_colormap[ds_source[spot]]; 
            dest.area[x+y*SCREENWIDTH] = ds_colormap[ds_source[spot]];
            x++;
            dest.area[x+y*SCREENWIDTH] = ds_colormap[ds_source[spot]];
            x++;
            //x+=2;

            xfrac += ds_xstep; 
            yfrac += ds_ystep; 

            count--;
        } while (count>0); 
    }

    
    
    //
    // R_InitBuffer 
    // Creates lookup tables that avoid
    //  multiplies and other hazzles
    //  for getting the framebuffer address
    //  of a pixel to draw.
    //
    void R_InitBuffer(
            int width,
            int height ) {
        int i; 

        // Handle resize,
        //  e.g. smaller view windows
        //  with border and/or status bar.
        viewwindowx = (SCREENWIDTH-width) >> 1; 

        // Column offset. For windows.
        for (i=0 ; i<width ; i++) {
            columnofs[i] = viewwindowx + i;
        }

        // Samw with base row offset.
        if (width == SCREENWIDTH) {
            viewwindowy = 0;
        } else {
            viewwindowy = (SCREENHEIGHT-SBARHEIGHT-height) >> 1;
        } 

        // Preclaculate all row offsets.
        for (i=0 ; i<height ; i++) {
            ylookup[i] = 
                //screens[0] +
                (i+viewwindowy)*SCREENWIDTH;
        } 
    } 
 

    //
    // R_FillBackScreen
    // Fills the back screen with a pattern
    //  for variable screen sizes
    // Also draws a beveled edge.
    //
    public void R_FillBackScreen(Wad wad, boolean isDoomII) {
        Flat src;
        //BufferedImage dest;
        int x;
        int y;
        PatchData patch;

        if (scaledviewwidth == 320) {
            return;
        }

        // DOOM border patch.
        String name1 = "FLOOR7_2";

        // DOOM II border patch.
        String name2 = "GRNROCK";

        String name;

        //if (Game.getInstance().gameMode == COMMERCIAL) {
        if (isDoomII) {
             name = name2;
        } else {
            name = name1;
        }

        Video video = renderer.video;
        //Wad wad = Game.getInstance().wad;
        src = wad.getFlatByName(name);
        Screen dest = video.screens[1];
        int destIndex = 0;


        for (y = 0; y < SCREENHEIGHT - SBARHEIGHT; y++) {
            for (x = 0; x < SCREENWIDTH / 64; x++) {
                for ( int i=0; i<64; i++ ) {
                    //memcpy(dest, src + ((y & 63) << 6), 64);
                    dest.area[destIndex]= src.pixels[(y&63)<<6]&0xFF;
                    destIndex++; // += 64;
                }
            }

            if ((SCREENWIDTH & 63)>0) {
                for (int i=0; i< (SCREENWIDTH&63); i++) {
                    //memcpy(dest, src + ((y & 63) << 6), SCREENWIDTH & 63);
                    dest.area[destIndex] = src.pixels[(y&63)<<6]&0xFF;
                    destIndex += (SCREENWIDTH & 63);
                }
            }
        }

        
        
        //patch = wad.getPatchByName("brdr_t");
        patch = ((PictureLump)wad.findByName("brdr_t")).pic;

        for (x = 0; x < scaledviewwidth; x += 8) {
            video.drawPatch(viewwindowx + x, viewwindowy - 8, 1, patch);
        }
        patch = ((PictureLump)wad.findByName("brdr_b")).pic;

        for (x = 0; x < scaledviewwidth; x += 8) {
            video.drawPatch(viewwindowx + x, viewwindowy + viewheight, 1, patch);
        }
        patch = ((PictureLump)wad.findByName("brdr_l")).pic;

        for (y = 0; y < viewheight; y += 8) {
            video.drawPatch(viewwindowx - 8, viewwindowy + y, 1, patch);
        }
        patch = ((PictureLump)wad.findByName("brdr_r")).pic;

        for (y = 0; y < viewheight; y += 8) {
            video.drawPatch(viewwindowx + scaledviewwidth, viewwindowy + y, 1, patch);
        }

        // Draw beveled edge. 
        video.drawPatch(viewwindowx - 8,
                viewwindowy - 8,
                1,
                ((PictureLump)wad.findByName("brdr_tl")).pic);

        video.drawPatch(viewwindowx + scaledviewwidth,
                viewwindowy - 8,
                1,
                ((PictureLump)wad.findByName("brdr_tr")).pic);

        video.drawPatch(viewwindowx - 8,
                viewwindowy + viewheight,
                1,
                ((PictureLump)wad.findByName("brdr_bl")).pic);

        video.drawPatch(viewwindowx + scaledviewwidth,
                viewwindowy + viewheight,
                1,
                ((PictureLump)wad.findByName("brdr_br")).pic);
    }


    //
    // Copy a screen buffer.
    //
    void
    R_VideoErase
    ( int	ofs,
      int		count ) 
    { 
      // LFB copy.
      // This might not be a good idea if memcpy
      //  is not optiomal, e.g. byte by byte on
      //  a 32bit CPU, as GNU GCC/Linux libc did
      //  at one point.
        //memcpy (screenImage[0]+ofs, screenImage[1]+ofs, count); 
        Screen a = renderer.video.screens[0];
        Screen b = renderer.video.screens[1];
        
        for ( int i=0; i<count; i++ ) {
            a.area[i+ofs] = b.area[i+ofs];
        }
    } 


    //
    // R_DrawViewBorder
    // Draws the border around the view
    //  for different size windows?
    //
    public void R_DrawViewBorder() {
        int top;
        int side;
        int ofs;
        int i;

        if (scaledviewwidth == SCREENWIDTH) {
            return;
        }

        top = ((SCREENHEIGHT - SBARHEIGHT) - viewheight) / 2;
        side = (SCREENWIDTH - scaledviewwidth) / 2;

        // copy top and one line of left side 
        R_VideoErase(0, top * SCREENWIDTH + side);

        // copy one line of right side and bottom 
        ofs = (viewheight + top) * SCREENWIDTH - side;
        R_VideoErase(ofs, top * SCREENWIDTH + side);

        // copy sides using wraparound 
        ofs = top * SCREENWIDTH + SCREENWIDTH - side;
        side <<= 1;

        for (i = 1; i < viewheight; i++) {
            R_VideoErase(ofs, side);
            ofs += SCREENWIDTH;
        }

        // ? 
        //V_MarkRect (0,0,SCREENWIDTH, SCREENHEIGHT-SBARHEIGHT); 
    }
    
}
