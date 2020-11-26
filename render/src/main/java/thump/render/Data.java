/*
 * Render Data  -  r_data
 */
package thump.render;

import java.util.ArrayList;
import static thump.base.Defines.logger;
import static thump.base.FixedPoint.FRACBITS;
import thump.wad.Wad;
import thump.wad.mapraw.MapPatch;
import thump.wad.mapraw.MapTexture;
import thump.wad.mapraw.PatchData;

/**
 *
 * @author mark
 */
public class Data {

    int         firstflat;
    int         lastflat;
    int         numflats;

    int         firstpatch;
    int         lastpatch;
    int         numpatches;

    public int         firstspritelump;
    public int         lastspritelump;
    public int         numspritelumps;

    //int         numtextures;
//    MapTexture[]   textures;
//
//    int[]      texturewidthmask;
//// needed for texture pegging
    public int[]         textureheight;
//    int[]      texturecompositesize;
//    int[][]      texturecolumnlump;
//    int[][]   texturecolumnofs;
//    byte[][]    texturecomposite;

// for global animation
    public int[]      flattranslation;
    public int[]      texturetranslation;

// needed for pre rendering
    public int[]         spritewidth;
    public int[]         spriteoffset;
    public int[]         spritetopoffset;

    public byte[][]	colormaps;   //TODO   Should be a LightTable Object?
    
    public  final Renderer renderer;

    public Data(Renderer renderer) {
        this.renderer = renderer;
    }
    
    
    
//
// MAPTEXTURE_T CACHING
// When a texture is first needed,
//  it counts the number of composite columns
//  required in the texture and allocates space
//  for a column directory and any new columns.
// The directory will simply point inside other patches
//  if there is only one col in a given column,
//  but any columns with multiple patches
//  will have new column_ts generated.
//


/*  NOT USED!
    //
    // R_DrawColumnInCache
    // Clip and draw a column
    //  from a col into a cached post.
    //
    void R_DrawColumnInCache(
            Column col,
            byte[] cache,
            int originy,
            int cacheheight) {
        int		count;
        int		position;
        byte[]	source;
        //byte[]	dest;
        int destIndex = 3;

        //dest = (byte *)cache + 3;

        
        while (col.topdelta != 0xff)
        {
            source = (byte *)            count = col.length;
            position = originy + col.topdelta;

            if (position < 0)
            {
                count += position;
                position = 0;
            }

            if (position + count > cacheheight)
                count = cacheheight - position;

            if (count > 0)
                memcpy (cache + position, source, count);

            col = (Column)(  (byte *)); 
        }
    }
*/

/*     NOT USED
    //
    // R_GenerateComposite
    // Using the texture definition,
    //  the composite texture is created from the patches,
    //  and each column is cached.
    //
    void R_GenerateComposite (int texnum)
    {
        byte[]		block;
        MapTexture		texture;
        MapPatch		patch;	
        Patch		realpatch;
        int			x;
        int			x1;
        int			x2;
        int			i;
        Column		patchcol;
        int[]		collump;
        int[]	colofs;

        texture = textures[texnum];

        block = Z_Malloc (texturecompositesize[texnum],
                          PU_STATIC, 
                          texturecomposite[texnum]);	

        collump = texturecolumnlump[texnum];
        colofs = texturecolumnofs[texnum];

        // Composite the columns together.
        patch = texture.patches;

        for (i=0 , patch = texture.patches;
             i<texture.patchcount;
             i++, patch++)
        {
            realpatch = W_CacheLumpNum (patch.patch, PU_CACHE);
            x1 = patch.originx;
            x2 = x1 + realpatch.width;

            if (x1<0) {
                x = 0;
            } else {
                x = x1;
            }

            if (x2 > texture.width) {
                x2 = texture.width;
            }

            for ( ; x<x2 ; x++) {
                // Column does not have multiple patches?
                if (collump[x] >= 0) {
                    continue;
                }

                patchcol = (Column)((byte *)realpatch
                                        + LONG(realpatch.columnofs[x-x1]));
                R_DrawColumnInCache (patchcol,
                                     block + colofs[x],
                                     patch.originy,
                                     texture.height);
            }

        }

        // Now that the texture has been built in column cache,
        //  it is purgable from zone memory.
        Z_ChangeTag (block, PU_CACHE);
    }

  */   //  NOT USED!
    
    

    //
    // R_GenerateLookup
    //
    void R_GenerateLookup (int texnum)
    {
//        MapTexture		texture;
        byte[]		patchcount;	// patchcount[texture.width]
        MapPatch		patch;
        int     patchNum = 0;
        //Patch		realpatch;
        int			x;
        int			x1;
        int			x2;
        int			i;
//        int[]		collump;
//        int[]	colofs;

//        texture = textures[texnum];

        // Composited texture not created yet.
//        texturecomposite[texnum] = null;

//        texturecompositesize[texnum] = 0;
//        collump = texturecolumnlump[texnum];
//        colofs = texturecolumnofs[texnum];

//        // Now count the number of columns
//        //  that are covered by more than one patch.
//        // Fill in the lump / offset, so columns
//        //  with only a single patch are all done.
//        //patchcount = (byte *)alloca (texture.width);
//        patchcount = new byte[texture.width];
//        //memset (patchcount, 0, texture.width);
//        Arrays.fill(patchcount, (byte)0);
//        
//        //patch = texture.patch[patchNum];
//
//        for (i=0 , patch = texture.patch[patchNum];
//             i<texture.patchcount;
//             i++, patchNum++)
//        {
//            //realpatch = W_CacheLumpNum (patch.patch, PU_CACHE);
//            realpatch = patch.getPatch();
//            
//            x1 = patch.originx;
//            x2 = x1 + realpatch.width;
//
//            if (x1 < 0) {
//                x = 0;
//            } else {
//                x = x1;
//            }
//
//            if (x2 > texture.width) {
//                x2 = texture.width;
//            }
//            for ( ; x<x2 ; x++) {
//                patchcount[x]++;
//                collump[x] = patch.patchNum;
//                //colofs[x] = LONG(realpatch.columnofs[x-x1])+3;
//                colofs[x] = realpatch.pointers[x-x1]+3;
//            }
//        }
//
//        for (x=0 ; x<texture.width ; x++) {
//            if (0==patchcount[x]) {
//                SystemInterface.I_Error("R_GenerateLookup: column without a patch ({0})\n",
//                        new Object[]{texture.name});
//                return;
//            }
//
//            if (patchcount[x] > 1) {
//                // Use the cached block.
//                collump[x] = -1;	
//                colofs[x] = texturecompositesize[texnum];
//
//                if (texturecompositesize[texnum] > 0x10000-texture.height) {
//                    SystemInterface.I_Error (
//                            "R_GenerateLookup: texture {0} is >64k",
//                             new Object[]{texnum});
//                }
//
//                texturecompositesize[texnum] += texture.height;
//            }
//        }	
    }



////  Not used!   Replaced by texture's getColumn() method.
//    //
//    // R_GetColumn
//    //
//    public Column R_GetColumn(
//        int		tex,
//        int		_col )
//    {
//        int		lump;
//        int		ofs;
//
//        int col = _col;
//        col &= texturewidthmask[tex];
//        lump = texturecolumnlump[tex][col];
//        ofs = texturecolumnofs[tex][col];
//
//        if (lump > 0) {
//            return (byte *)W_CacheLumpNum(lump,PU_CACHE)+ofs;
//        }
//
//        if (!texturecomposite[tex]) {
//            R_GenerateComposite (tex);
//        }
//
//        return texturecomposite[tex] + ofs;
//    }
//


    // Done in WAD Loader
    //
    // R_InitTextures
    // Initializes the texture list
    //  with the textures from the world map.
    //
    void R_InitTextures (Wad wad) {
        MapTexture mtexture;
        MapTexture texture;
        MapPatch mpatch;
        MapPatch patch;

        int i;
        int j;

        int[] maptex;
        int[] maptex2;
        int[] maptex1;

        String name;
        String names;
        String name_p;

        int[] patchlookup = null;

        int totalwidth;
        int nummappatches;
        int offset;
        int maxoff;
        int maxoff2;
        int numtextures1;
        int numtextures2;

        int[] directory;

        int temp1;
        int temp2;
        int temp3;

        // Load the patch names from pnames.lmp.
        //name[8] = 0;
//        names = W_CacheLumpName("PNAMES", PU_STATIC);
//        nummappatches = LONG( * ((int *)names
//        ) );
//        name_p = names + 4;
//        patchlookup = alloca(nummappatches * sizeof( * patchlookup));
//
//        for (i = 0; i < nummappatches; i++) {
//            strncpy(name, name_p + i * 8, 8);
//            patchlookup[i] = W_CheckNumForName(name);
//        }
//        Z_Free(names);

        ArrayList<PatchData> patches = wad.patchesLump.getPatches();
        patchlookup = new int[patches.size()];
        for ( i=0; i<patches.size(); i++ ) { /// GRRR
            patchlookup[i] = i;
        }

        // Load the map texture definitions from textures.lmp.
        // The data is contained in one or two lumps,
        //  TEXTURE1 for shareware, plus TEXTURE2 for commercial.
//        maptex = maptex1 = W_CacheLumpName("TEXTURE1", PU_STATIC);
//        numtextures1 = LONG( * maptex);
//        maxoff = W_LumpLength(W_GetNumForName("TEXTURE1"));
//        directory = maptex + 1;
//
//        if (W_CheckNumForName("TEXTURE2") != -1) {
//            maptex2 = W_CacheLumpName("TEXTURE2", PU_STATIC);
//            numtextures2 = LONG( * maptex2);
//            maxoff2 = W_LumpLength(W_GetNumForName("TEXTURE2"));
//        } else {
//            maptex2 = NULL;
//            numtextures2 = 0;
//            maxoff2 = 0;
//        }
//        numtextures = numtextures1 + numtextures2;

        //textures = Z_Malloc (numtextures*4, PU_STATIC, 0);
//        textures = new int[numtextures * 4];
//        //texturecolumnlump = Z_Malloc (numtextures*4, PU_STATIC, 0);
//        texturecolumnlump = new int[numtextures * 4];
//        //texturecolumnofs = Z_Malloc (numtextures*4, PU_STATIC, 0);
//        texturecolumnofs = new int[numtextures * 4];
//        //texturecomposite = Z_Malloc (numtextures*4, PU_STATIC, 0);
//        texturecomposite = new int[numtextures * 4];
//        //texturecompositesize = Z_Malloc (numtextures*4, PU_STATIC, 0);
//        texturecompositesize = new int[numtextures * 4];
//        //texturewidthmask = Z_Malloc (numtextures*4, PU_STATIC, 0);
//        texturewidthmask = new int[numtextures * 4];
//        //textureheight = Z_Malloc (numtextures*4, PU_STATIC, 0);
        textureheight = new int[wad.getTextures().size() * 4];

//        totalwidth = 0;

//        //	Really complex printing shit...
//        temp1 = W_GetNumForName("S_START");  // P_???????
//        temp2 = W_GetNumForName("S_END") - 1;
//        temp3 = ((temp2 - temp1 + 63) / 64) + ((numtextures + 63) / 64);
//        printf("[");
//        for (i = 0; i < temp3; i++) {
//            printf(" ");
//        }
//        printf("         ]");
//        for (i = 0; i < temp3; i++) {
//            printf("\x8");
//        }
//        printf("\x8\x8\x8\x8\x8\x8\x8\x8\x8\x8");

//        for (i = 0; i < numtextures; i++, directory++) {
//            if (!(i & 63)) {
//                printf(".");
//            }
//
//            if (i == numtextures1) {
//                // Start looking in second texture file.
//                maptex = maptex2;
//                maxoff = maxoff2;
//                directory = maptex + 1;
//            }
//
//            offset = LONG( * directory);
//
//            if (offset > maxoff) {
//                I_Error("R_InitTextures: bad texture directory");
//            }
//
//            mtexture = (mapTexture *) ( (byte *)maptex + offset
//            );
//    
//    	texture = textures[i]
//                    = Z_Malloc(sizeof(Texture)
//                            + sizeof(MapPatch) * (SHORT(mtexture.patchcount) - 1),
//                            PU_STATIC, 0);
//
//            texture.width = SHORT(mtexture.width);
//            texture.height = SHORT(mtexture.height);
//            texture.patchcount = SHORT(mtexture.patchcount);
//
//            memcpy(texture.name, mtexture.name, sizeof(texture.name));
//            mpatch =  & mtexture.patches[0];
//            patch =  & texture.patches[0];
//
//            for (j = 0; j < texture.patchcount; j++, mpatch++, patch++) {
//                patch.originx = SHORT(mpatch.originx);
//                patch.originy = SHORT(mpatch.originy);
//                patch.patch = patchlookup[SHORT(mpatch.patch)];
//                if (patch.patch == -1) {
//                    I_Error("R_InitTextures: Missing patch in texture %s",
//                            texture.name);
//                }
//            }
//            texturecolumnlump[i] = Z_Malloc(texture.width * 2, PU_STATIC, 0);
//            texturecolumnofs[i] = Z_Malloc(texture.width * 2, PU_STATIC, 0);
//
//            j = 1;
//            while (j * 2 <= texture.width) {
//                j <<= 1;
//            }
//
//            texturewidthmask[i] = j - 1;
//            textureheight[i] = texture.height << FRACBITS;
//
//            totalwidth += texture.width;
//        }

//        Z_Free(maptex1);
//        if (maptex2) {
//            Z_Free(maptex2);
//        }

        // Lookup is not used anywhere
//        // Precalculate whatever possible.	
//        for (i = 0; i < numtextures; i++) {
//            R_GenerateLookup(i);
//        }

        // Create translation table for global animation.
        //texturetranslation = Z_Malloc((numtextures + 1) * 4, PU_STATIC, 0);
        texturetranslation = new int[wad.getTextures().size()];

        for (i = 0; i < wad.getTextures().size(); i++) {
            texturetranslation[i] = i;
        }
    }


    // Done in WAD Loader
    //
    // R_InitFlats
    //
    void R_InitFlats(Wad wad) {    	
        //firstflat = W_GetNumForName ("F_START") + 1;
        firstflat = 0;
        //lastflat = W_GetNumForName ("F_END") - 1;
        lastflat = wad.getFlats().flats.size()-1;
        
        //numflats = lastflat - firstflat + 1;
        numflats = wad.getFlats().flats.size();
    	
        // Create translation table for global animation.
        //flattranslation = Z_Malloc ((numflats+1)*4, PU_STATIC, 0);
        flattranslation = new int[numflats+1];  //TODO:  Why the plus one?
        
        for (int i=0 ; i<numflats ; i++) {
            flattranslation[i] = i;
        }
    }

    //
    // R_InitSpriteLumps
    // Finds the width and hoffset of all sprites in the wad,
    //  so the sprite does not need to be cached completely
    //  just for having the header info ready during rendering.
    //
    void R_InitSpriteLumps (Wad wad) {        
        int		i;
        PatchData         patch;
    //	
    //    firstspritelump = W_GetNumForName ("S_START") + 1;
        firstspritelump = 0;
    //    lastspritelump = W_GetNumForName ("S_END") - 1;
        lastspritelump = wad.spritesLump.sprites.size()-1;
    //    
    //    numspritelumps = lastspritelump - firstspritelump + 1;
        numspritelumps = wad.spritesLump.sprites.size();
        
    //    spritewidth = Z_Malloc (numspritelumps*4, PU_STATIC, 0);
        spritewidth = new int[numspritelumps];
        
    //    spriteoffset = Z_Malloc (numspritelumps*4, PU_STATIC, 0);
        spriteoffset = new int[numspritelumps];
        
    //    spritetopoffset = Z_Malloc (numspritelumps*4, PU_STATIC, 0);
        spritetopoffset = new int[numspritelumps];
        
    	
        for (i = 0; i < numspritelumps; i++) {
            if (0==(i & 63)) {
                //printf(".");
                logger.config(".");
            }

            //patch = W_CacheLumpNum(firstspritelump + i, PU_CACHE);
            patch = wad.spritesLump.sprites.get(i);
            
            spritewidth[i] = patch.width << FRACBITS;
            spriteoffset[i] = patch.leftOffset << FRACBITS;
            spritetopoffset[i] = patch.topOffset << FRACBITS;
        }
    }



    //
    // R_InitColormaps
    //
    void R_InitColormaps (Wad wad)
    {
    //    int	lump, length;
    //    
    //    // Load in the light tables, 
    //    //  256 byte align tables.
    //    lump = W_GetNumForName("COLORMAP"); 
    //    length = W_LumpLength (lump) + 255; 
    //    colormaps = Z_Malloc (length, PU_STATIC, 0); 
    //    colormaps = (byte *)( ((int)colormaps + 255)&~0xff); 
    //    W_ReadLump (lump,colormaps); 
        colormaps = wad.getColorMaps();
    }


    //
    // R_InitData
    // Locates all the lumps
    //  that will be used by all views
    // Must be called after W_Init.
    //
    void R_InitData ( Wad wad) {

        logger.config("InitTextures\n");
        R_InitTextures (wad);

        logger.config("InitFlats\n");
        R_InitFlats (wad);

        logger.config("InitSprites\n");
        R_InitSpriteLumps (wad);

        logger.config("InitColormaps\n");
        R_InitColormaps (wad);
    }

    /*

    //
    // R_FlatNumForName
    // Retrieval, get a flat number for a flat name.
    //
    int R_FlatNumForName (String name)
    {
        int		i;
        String	namet;

        i = W_CheckNumForName (name);

        if (i == -1)
        {
            namet[8] = 0;
            memcpy (namet, name,8);
            I_Error ("R_FlatNumForName: %s not found",namet);
        }
        return i - firstflat;
    }



// Moved to Wad.java
    //
    // R_CheckTextureNumForName
    // Check whether texture is available.
    // Filter out NoTexture indicator.
    //
    int	R_CheckTextureNumForName (String name)
    {
        int		i;

        // "NoTexture" marker.
        if (name[0] == '-')		
            return 0;

        for (i=0 ; i<numtextures ; i++)
            if (!strncasecmp (textures[i].name, name, 8) )
                return i;

        return -1;
    }


// Moved to Wad.java
    //
    // R_TextureNumForName
    // Calls R_CheckTextureNumForName,
    //  aborts with error message.
    //
    int	R_TextureNumForName (String name)
    {
        int		i;

        i = R_CheckTextureNumForName (name);

        if (i==-1)
        {
            I_Error ("R_TextureNumForName: %s not found",
                     name);
        }
        return i;
    }




    //
    // R_PrecacheLevel
    // Preloads all relevant graphics for the level.
    //
    int		flatmemory;
    int		texturememory;
    int		spritememory;

    void R_PrecacheLevel ()
    {
        String		flatpresent;
        String		texturepresent;
        String		spritepresent;

        int			i;
        int			j;
        int			k;
        int			lump;

        MapTexture		texture;
        thinker_t*		th;
        spriteframe_t*	sf;

        if (demoplayback)
            return;

        // Precache flats.
        flatpresent = alloca(numflats);
        memset (flatpresent,0,numflats);	

        for (i=0 ; i<numsectors ; i++)
        {
            flatpresent[sectors[i].floorpic] = 1;
            flatpresent[sectors[i].ceilingpic] = 1;
        }

        flatmemory = 0;

        for (i=0 ; i<numflats ; i++)
        {
            if (flatpresent[i])
            {
                lump = firstflat + i;
                flatmemory += lumpinfo[lump].size;
                W_CacheLumpNum(lump, PU_CACHE);
            }
        }

        // Precache textures.
        texturepresent = alloca(numtextures);
        memset (texturepresent,0, numtextures);

        for (i=0 ; i<numsides ; i++)
        {
            texturepresent[sides[i].toptexture] = 1;
            texturepresent[sides[i].midtexture] = 1;
            texturepresent[sides[i].bottomtexture] = 1;
        }

        // Sky texture is always present.
        // Note that F_SKY1 is the name used to
        //  indicate a sky floor/ceiling as a flat,
        //  while the sky texture is stored like
        //  a wall texture, with an episode dependend
        //  name.
        texturepresent[skytexture] = 1;

        texturememory = 0;
        for (i=0 ; i<numtextures ; i++)
        {
            if (!texturepresent[i])
                continue;

            texture = textures[i];

            for (j=0 ; j<texture.patchcount ; j++)
            {
                lump = texture.patches[j].patch;
                texturememory += lumpinfo[lump].size;
                W_CacheLumpNum(lump , PU_CACHE);
            }
        }

        // Precache sprites.
        spritepresent = alloca(numsprites);
        memset (spritepresent,0, numsprites);

        for (th = thinkercap.next ; th != &thinkercap ; th=th.next)
        {
            if (th.function.acp1 == (actionf_p1)P_MobjThinker)
                spritepresent[((mobj_t *)th).sprite] = 1;
        }

        spritememory = 0;
        for (i=0 ; i<numsprites ; i++)
        {
            if (!spritepresent[i])
                continue;

            for (j=0 ; j<sprites[i].numframes ; j++)
            {
                sf = &sprites[i].spriteframes[j];
                for (k=0 ; k<8 ; k++)
                {
                    lump = firstspritelump + sf.lump[k];
                    spritememory += lumpinfo[lump].size;
                    W_CacheLumpNum(lump , PU_CACHE);
                }
            }
        }
    }




*/
    
}
