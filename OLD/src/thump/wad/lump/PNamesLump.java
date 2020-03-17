/*
   Offset   Length              Name            Content
   0x00     4                   nummappatches	An integer holding a the number of following patches.
   0x04     8 * nummappatches	name_p[ ]	

Eight-character ASCII strings defining the 
lump names of the patches. Only the characters A-Z (uppercase), 
0-9, and [ ] - _ should be used in lump names. When a string 
is less than 8 bytes long, it should be null-padded to the 
eighth byte. 

*/
package thump.wad.lump;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

/**
 *
 * @author mark
 */
public class PNamesLump extends Lump {
    
    public final String[] patchNames;
    
    public PNamesLump(FileChannel fc, String name, int filepos, int size) throws IOException {
        super(name, filepos, size);
        fc.position(filepos);
        ByteBuffer bb = ByteBuffer.allocate(size);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        fc.read(bb);
        bb.position(0);

        int numPatches = bb.getInt();
        patchNames = new String[numPatches];
                
        for (int i = 0; i < patchNames.length; i++) {
            byte b[] = new byte[8];
            bb.get(b);
            patchNames[i] = new String(b);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("PNAMES:\n    ");
        for ( int i=0; i< patchNames.length; i++ ) {
            sb.append(patchNames[i]);
            sb.append("  ");
            
            if ( (i+1)%8 == 0 ) {
                sb.append("\n    ");
            }
        }
        return sb.toString();
    }
    
}
