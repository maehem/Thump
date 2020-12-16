/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.wad;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import thump.base.Defines;
//import thump.global.SystemInterface;
import thump.wad.lump.ColorMapLump;
import thump.wad.lump.FlatsLump;
import thump.wad.lump.Lump;
import thump.wad.lump.MapLump;
import thump.wad.lump.PNamesLump;
import thump.wad.lump.PatchesLump;
import thump.wad.lump.PictureLump;
import thump.wad.lump.PlaypalLump;
import thump.wad.lump.SpritesLump;
import thump.wad.map.Flat;
import thump.wad.mapraw.MapTexture;
import thump.wad.mapraw.PatchData;

/**
 *
 * @author mark
 */
public class Wad {
    // Should be "IWAD" or "PWAD".
    public String   identification;
    public int      numlumps;
    public int      infotableofs;
    public final List<Lump> lumps = new ArrayList<>();
    
    public ArrayList<int[]> paletteList = null;
    
    private  ArrayList<MapTexture> textures = null;
    public SpritesLump spritesLump = null;  // Set by WadLoader
    public PatchesLump patchesLump = null;  // Set by WadLoader
    public FlatsLump   flatsLump = null;  // Set by WadLoader
    public PNamesLump pNamesLump = null; // Set by WadLoader
    //public PictureLump statusLump = null;
    
    public ArrayList<MapLump> mapLumps = new ArrayList<>();
    
    public PlaypalLump getPlayPalLump() {
        Iterator<Lump> iterator = lumps.iterator();
        
        while ( iterator.hasNext() ) {
            Lump lump = iterator.next();
            if (lump.name.startsWith("PLAYPAL")) {
                return (PlaypalLump) lump;
            }
        }
        return null;
    }
    
    public Lump findByName( String name ) {
        Iterator<Lump> iterator = lumps.iterator();
        
        while ( iterator.hasNext() ) {
            Lump lump = iterator.next();
            if (lump.name.startsWith(name.toUpperCase())) {
                return lump;
            }
        }
        return null;        
    }

    /** Finds normal picture/patches in top level lumps.
     *
     */
    PatchData findPicture(short patchNum) {
        // Look in PNAMES and get the name of the indexed Patch.
        PNamesLump pNames = (PNamesLump) this.findByName("PNAMES");
        
        // Get the named PictureLump
        PictureLump lump = (PictureLump) findByName(pNames.patchNames[patchNum]);
        
        return lump.pic;
    }

    
    public PNamesLump getPNames() {
        return pNamesLump;
//        Iterator<Lump> iterator = lumps.iterator();
//        
//        while ( iterator.hasNext() ) {
//            Lump lump = iterator.next();
//            if (lump instanceof PNamesLump) {
//                return (PNamesLump) lump;
//            }
//        }
//        return null;                
    }
    
    public PatchData getPatchByName(String pName) {
        return patchesLump.getPatch(pNamesLump.get(pName.toUpperCase()));
    }
    
    public Flat getFlatByName(String pName) {
        return flatsLump.getFlatByName(pName.toUpperCase());
    }
    
    public MapTexture getMapTextureByName( String name ) {
        //TextureLump lump = (TextureLump) findByName("TEXTURE1");
        for (MapTexture t : textures) {
            if ( t.name.matches(name) ) {
                return t;
            }
        }
        return null;        
    }
    
    public ArrayList<MapTexture> getTextures() {
        return textures;
    }
    
    public int getTextureNum(String name) {
        //TextureLump lump = (TextureLump) findByName("TEXTURE1");
        for (int i=0; i< textures.size(); i++) {
            if ( textures.get(i).name.equals(name.toUpperCase()) ) {
                return i;
            }
        }
        //return -1;
        return 0;        
    }
    
    public FlatsLump getFlats() {
        return (FlatsLump) findByName("F_START");
    }
    
    //
    // R_TextureNumForName
    // Calls R_CheckTextureNumForName,
    //  aborts with error message.
    //
    public int	R_TextureNumForName (String name) {

        int i = R_CheckTextureNumForName (name);

        if (i==-1) {
            //SystemInterface.I_Error (
            Defines.logger.log(Level.SEVERE,
                    "R_TextureNumForName: {0} not found\n",
                     new Object[]{name});
        }
        
        return i;
    }

    //
    // R_CheckTextureNumForName
    // Check whether texture is available.
    // Filter out NoTexture indicator.
    //
    public int	R_CheckTextureNumForName (String name) {
        
        // "NoTexture" marker.
        if (name.startsWith("-") ) {
            return 0;
        }

        return getTextureNum(name);
    }
    
    public void setTextures(ArrayList<MapTexture> texture) {
        this.textures = texture;
    }
    
    public byte[][] getColorMaps() {  //  TODO cache this.
        ColorMapLump lump = (ColorMapLump) findByName("COLORMAP");
        Iterator<byte[]> list = lump.mapList.iterator();
        byte[][] maps = new byte[lump.mapList.size()][256];
        int i=0;
        while(list.hasNext()) {
            byte[] next = list.next();
            Arrays.fill(maps[i], (byte)0);
            for( int ii=0; ii< next.length; ii++) {
                maps[i][ii] = next[ii];
            }
            i++;
        }
        
        return maps;
    }

    //
    // W_CheckNumForName
    // Returns -1 if name not found.
    //
    public int W_CheckNumForName (String name) {
//        union {
//            char	s[9];
//            int	x[2];
//
//        } name8;

//        int		v1;
//        int		v2;
//        LumpInfo	lump_p;
//
//        // make the name into two integers for easy compares
//        strncpy (name8.s,name,8);
//
//        // in case the name was a fill 8 chars
//        name8.s[8] = 0;
//
//        // case insensitive
//        strupr (name8.s);		
//
//        v1 = name8.x[0];
//        v2 = name8.x[1];
//
//
//        // scan backwards so patches lump files take precedence
//        lump_p = lumpinfo + numlumps;
//
//        while (lump_p-- != lumpinfo) {
//            if ( *(int *)lump_p->name == v1
//                 && *(int *)&lump_p->name[4] == v2)
//            {
//                return lump_p - lumpinfo;
//            }
//        }
        int lump_p = lumps.size()-1;
        while ( lump_p >= 0 ) {
            if ( lumps.get(lump_p).name.equals(name.toUpperCase()) ) {
                return lump_p;
            }
                
            lump_p--;
        }

//        // TFB. Not found.
        return -1;
    }


    //
    // W_GetNumForName
    // Calls W_CheckNumForName, but bombs out if not found.
    //
    public int W_GetNumForName (String name) {

        int i = W_CheckNumForName (name);

        if (i == -1) {
            //SystemInterface.I_Error (
            Defines.logger.log(Level.SEVERE, "W_GetNumForName: {0} not found!\n", new Object[]{name});
        }

        return i;
    }

    

}
