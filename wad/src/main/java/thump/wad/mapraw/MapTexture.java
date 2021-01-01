/*
        MapTexture

        Offset	Length	Name	Content
        0x00	8	name	An ASCII string defining the name of the map texture. Only the characters A-Z (uppercase), 0-9, and [ ] - _ should be used in lump names. When a string is less than 8 bytes long, it should be null-padded to the eighth byte.
        0x08	4	masked	A boolean (0=false, 1=true) defining ?
        0x0C	2	width	A short integer defining the total width of the map texture.
        0x0E	2	height	A short integer defining the total height of the map texture.
        0x10	4	columndirectory	Obsolete, ignored by all DOOM versions
        0x14	2	patchcount	the number of map patches that make up this map texture
        0x16	10 * patchcount	patches[ ]	array with the map patchNum structures for this texture. (see next table) 
*/
package thump.wad.mapraw;

import java.nio.ByteBuffer;
import thump.wad.Wad;

/**
 *
 * @author mark
 */
public class MapTexture {
    public final String name;
    public final boolean masked;
    public final short width;
    public final short height;
    public final short patchcount;

    public final MapPatch[] patches;
    
    //private Patch fullPatch = null;   // The fully mapped Patch
    private PatchData fullPatch = null;
    
//    BufferedImage img = null;
//    BufferedImage rawMap = null;

    public MapTexture(/*Wad wad,*/ ByteBuffer bb) {
        
        byte[] bytes = new byte[8];
        bb.get(bytes);
        int i;
        for (i = 0; i < bytes.length && bytes[i] != 0; i++) { }
        this.name = new String(bytes, 0 , i);
        
        this.masked = bb.getInt() != 0;  //
        this.width = bb.getShort();
        this.height = bb.getShort();
        
        bb.getInt(); // Ignore these four bytes.
        
        this.patchcount = bb.getShort();
        
        this.patches = new MapPatch[patchcount];
        for( i=0; i< patchcount; i++ ) {
            patches[i] = new MapPatch(bb);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name + "   mask:"+ masked + "   w:" + width + "   h:" + height + "   pCount:" + patchcount + "\n");
        for ( MapPatch mp : patches ) {
            sb.append("\t").append(mp.toString()).append("\n");    
        }
        return sb.toString();        
    }

//    public Image getImage() {
//        if ( img == null ) {
//            img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//
//            Graphics g = img.getGraphics();
//
//            for ( MapPatch p: patches ) {
//                g.drawImage(p.getImage(), p.originx, p.originy, null);
//            }
//        }
//        
//        return img;
//    }
//
    
    
//    public Patch getPatch( ArrayList<Color[]> paletteList) {
////        if ( fullPatch ==  null ) {
////            ByteBuffer bb = ByteBuffer.allocate(width*height+8);
////            bb.order(ByteOrder.LITTLE_ENDIAN);
////            bb.put( new byte[]{ (byte) (width&0xFF),  (byte)((width>>8)&0xff)  } );
////            bb.put( new byte[]{ (byte) (height&0xFF), (byte)((height>>8)&0xff) } );
////            bb.put((byte)0);
////            bb.put((byte)0);
////            bb.put((byte)0);
////            bb.put((byte)0);
////            //byte bp[][] = new byte[width][height];
////            for ( MapPatch p: patches ) {
////                Patch fp = p.getPatch();
////                for ( int x=0; x<fp.width; x++) {
////                    Column c = fp.pixelData[x];
////                    for ( int y=0; y<c.height; y++) {
////                        bb.put((x+p.originx) * (y+p.originy) + 8 , (byte) c.getRawVals()[y]);
////                    }
////                }
////            }
////            
////            
////            // Seems to be lacking the offset table before the data!
////            
////            
////            //fullPatch = new Patch(name, bb, null);
////            fullpatch = Patch.merge(name, width, height, patches);
////            
////        }
//        if (fullPatch == null) {
//            fullPatch = Patch.merge(name, width, height, patches, paletteList);
//        }
//        return fullPatch;
//    }
    
//    public Column getColumn(int colnum, ArrayList<Color[]> paletteList ) {
//        if ( colnum == 139 ) {
//            int i=0;  // debug breakpoint
//        }
//        try {
//            return getPatch(paletteList).pixelData[colnum];         
//        } catch (Exception e) {
//            int size = getPatch(paletteList).pixelData.length;
//            
//            return getPatch(paletteList).pixelData[size-1];         
//        }
//    }
    
    
    public PatchData getPatch() {
//        if ( fullPatch ==  null ) {
//            ByteBuffer bb = ByteBuffer.allocate(width*height+8);
//            bb.order(ByteOrder.LITTLE_ENDIAN);
//            bb.put( new byte[]{ (byte) (width&0xFF),  (byte)((width>>8)&0xff)  } );
//            bb.put( new byte[]{ (byte) (height&0xFF), (byte)((height>>8)&0xff) } );
//            bb.put((byte)0);
//            bb.put((byte)0);
//            bb.put((byte)0);
//            bb.put((byte)0);
//            //byte bp[][] = new byte[width][height];
//            for ( MapPatch p: patches ) {
//                Patch fp = p.getPatch();
//                for ( int x=0; x<fp.width; x++) {
//                    Column c = fp.pixelData[x];
//                    for ( int y=0; y<c.height; y++) {
//                        bb.put((x+p.originx) * (y+p.originy) + 8 , (byte) c.getRawVals()[y]);
//                    }
//                }
//            }
//            
//            
//            // Seems to be lacking the offset table before the data!
//            
//            
//            //fullPatch = new Patch(name, bb, null);
//            fullpatch = Patch.merge(name, width, height, patches);
//            
//        }
//        if (fullPatch == null) {
//            fullPatch = PatchesLump.merge(name, width, height, patches);
//        }
        return fullPatch;
    }
    
    public Column getColumn(int colnum ) {
        return fullPatch.pixelData[colnum%fullPatch.pixelData.length];
    }
    
    public void merge(Wad wad) {
        fullPatch = new PatchData("MERGED", width, height);

        for (MapPatch p : patches) {
            //PatchData pp = p.getPatch();
            PatchData pd = wad.patchesLump.getPatch(p.getPatchNum());
            for (int x = 0; x < pd.width; x++) {
                try {
                    Column mc = fullPatch.pixelData[p.getX() + x /*+ pd.leftOffset*/];// Not sure what offsets do. They don't render right.
                    int[] rawVals = pd.pixelData[x].getRawVals();
                    for (int y = 0; y < pd.height; y++) {
                        try {
                            mc.posts.get(0).pixels[p.getY() + y /*+ pd.topOffset*/] = (byte) (rawVals[y] & 0xff);
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            //break;
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                    //break;
                }
            }
        }

    }
    
}
