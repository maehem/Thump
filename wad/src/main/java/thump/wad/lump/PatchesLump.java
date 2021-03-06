/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.wad.lump;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.logging.Level;
import static thump.base.Defines.logger;
import thump.wad.Wad;
import thump.wad.mapraw.Column;
import thump.wad.mapraw.MapPatch;
import thump.wad.mapraw.PatchData;
//import thump.wad.map.Patch;

/**
 *
 * @author mark
 */
public class PatchesLump extends Lump {
    public final ArrayList<PatchData> lumpPatches = new ArrayList<>(); // Todo, eliminate this after sort.
    private final ArrayList<PatchData> patches = new ArrayList<>();
    //private final ArrayList<Color[]>paletteList;

    public PatchesLump(FileChannel fc, String name, int filepos, int size /*, ArrayList<Color[]> paletteList*/) {
        super(name, filepos, size);
        //this.paletteList = paletteList;
    }
    
    public PatchData addPatch(FileChannel fc, String name, int filePos, int lumpSize) throws IOException {
        fc.position(filePos);
        // Load up BB
        ByteBuffer bb = ByteBuffer.allocate(lumpSize);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        fc.read(bb);
        bb.position(0);

        PatchData pic = new PatchData(name,  bb /*, paletteList*/);
        lumpPatches.add(pic);
    
        return pic;
    }
    
    /**
     * Generate patches in order of pNames 
     * @param lump
     */
    public void sortPatches(PNamesLump lump) {
        for ( String pName: lump.patchNames ) {
            patches.add(getNamedPatch(pName));
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Patches:\n");
        
        for ( int i=0; i<patches.size(); i++) {
            //sb.append(lumpPatches.get(i).name).append("  ");*/
            sb.append(i).append(": ");
            sb.append(patches.get(i).toString());
            sb.append("\n");
//            if ( (i+1)% 10 == 0 ) {
//                sb.append("\n");
//            }
        }
        return sb.toString();
    }
    
    public ArrayList<PatchData> getPatches() {
        return patches;
    }

    public PatchData getPatch(int patchNum) {
        return patches.get(patchNum);
    }
    
    private  PatchData getNamedPatch( String name ) {
        for ( PatchData pd: lumpPatches ) {
            if ( pd.name.equals(name.toUpperCase())) { return pd; }

            if ( name.equals("w94_1")) {
                int i=0; // breakpoint
            }
        }
        
        logger.log(Level.WARNING, "PatchesLump.getNamedPatch():  Failed to find a patch called {0}", name);
        return new PatchData(name, 10, 10);
    }

//    public PatchData getPatchByName( String pName ) {
//        ListIterator<PatchData> pics = getPatches().listIterator();
//        
//        while ( pics.hasNext() ) {
//            PatchData p = pics.next();
//            if (p.name.equals(pName) ) {
//                return p;
//            }
//        }
//        
//        return null;
//    }
    
    //TODO:  make non-static?
    public static PatchData merge(Wad wad, short width, short height, MapPatch[] patchList) {
        //PatchData mp = new PatchData(name, width, height);
        PatchData mp = new PatchData("MERGED", width, height);

        for (MapPatch p : patchList) {
            //PatchData pp = p.getPatch();
            PatchData pp = wad.patchesLump.getPatch(p.getPatchNum());
            for (int x = 0; x < pp.width; x++) {
                try {
                    Column mc = mp.pixelData[x + pp.leftOffset];
                    int[] rawVals = pp.pixelData[x].getRawVals();
                    for (int y = 0; y < pp.height; y++) {
                        try {
                            mc.posts.get(0).pixels[y + pp.topOffset] = (byte) (rawVals[y] & 0xff);
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            break;
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                    break;
                }

            }
        }

        return mp;
    }

}
