/*
        DP* entries in the directory refer to lumps that are sound data for
      systems using the PC speaker.
        It's a quick and simple format. First is a <short> that's always 0,
      then a <short> that's the number of bytes of sound data, then follow
      that many bytes worth of sound data. That is, the lump's bytes will be
      0, 0, N, 0, then N bytes of data. The DP* lumps range in size from around
      10 bytes to around 150 bytes, and the data seem to range from 0 to 96
      (0x00 to 0x60). The numbers obviously indicate frequency, but beyond
      that I don't know the exact correlation in Hz, nor the time duration
      of each byte worth of data. Feel free to figure this out and tell me.
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
public class PCSoundLump extends Lump {

    public final short length;
    public final byte  data[];
    
    public PCSoundLump(FileChannel fc, String name, int filepos, int size) throws IOException {
        super(name, filepos, size);
        fc.position(filepos);
        ByteBuffer bb = ByteBuffer.allocate(size);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        fc.read(bb);
        bb.position(2);  // Skip first short
        
        length = bb.getShort();
        data = new byte[length];
        bb.get(data);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("PC Sound:     length:" + length + "\n    ");
        
            for ( int i=0; i< data.length; i++ ) {
                String padding = "00";
                String result = padding + Integer.toHexString(data[i]&0xFF);
                result = result.substring(result.length() - 2, result.length());
                sb.append(result);
            if ( (i+1)%32 == 0) {
                sb.append("\n    ");
            }
        }
        sb.append("\n");
        
        
        return sb.toString();
    }
    
}
