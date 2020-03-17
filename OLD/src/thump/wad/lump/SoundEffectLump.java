/*
       DS* entries in the directory refer to lumps that are sound data for
       systems using soundcards.
         This data is in a RAW format for 8-bit 11 KHz mono sound - first is
       an 8-byte header composed of 4 unsigned short integers:

       (1) 3           (means what?)
       (2) 11025       (the sample rate, samples per second)
       (3) N           (the number of samples)
       (4) 0

         Each sample is a single byte, since they are 8-bit samples. The
       maximum number of samples is 65535, so at 11 KHz, a little less than
       6 seconds is the longest possible sound effect.
 */
package thump.wad.lump;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author mark
 */
public class SoundEffectLump extends Lump {
    public final int length;
    public final int sampleRate;
    public final byte  data[];
    
    public SoundEffectLump(FileChannel fc, String name, int filepos, int size) throws IOException {
        super(name, filepos, size);
        
        fc.position(filepos);
        ByteBuffer bb = ByteBuffer.allocate(size);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        fc.read(bb);
        bb.position(2);  // Skip first short
        
        sampleRate = bb.getShort();
        length = bb.getShort()&0xFFFF;
        bb.getShort(); // Skip a byte.
        data = new byte[length];
        bb.get(data);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("PC Sound:     sampleRate: " + sampleRate + "   length:" + length + "\n    ");
        
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
    
    
    public void playSound() {
        try {
            AudioFormat af = new AudioFormat(sampleRate, 8, 1, false, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, af);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);

            line.open(af, 4096);
            line.start();
            line.write(data, 0, data.length);

            line.drain();
            line.stop();
            line.close();
        } catch (LineUnavailableException ex) {
            Logger.getLogger(SoundEffectLump.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
