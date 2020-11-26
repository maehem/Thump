/*
    See this:  http://www.shikadi.net/moddingwiki/MUS_Format

    <song>          := "MUS"
                       <byte:26>
                       <music_length>       ;<ushort>
                       <music_start>        ;<ushort>
                       <primary_channels>   ;<ushort>
                       <secondary_channels> ;<ushort>
                       <num_instr_patches>  ;<ushort>
                       <ushort:0>
                       <instr_patches>
                       <music data>
    <instr_patches> := <instr_patch> [num_instr_patches]
    <instr_patch>   := <ushort>             ;Drum instrument #s 28 less than in DMXGUS

    <music data>    := ???
 */
package thump.wad.lump;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.logging.Logger;

/**
 *
 * @author mark
 */
public class MusicLump extends Lump {

    private static final Logger logger = Logger.getGlobal();

    public final int mLength;
    public final int mStart;
    public final int priChannels;
    public final int secChannels;
    public final int instCount;
    public final int[] instrument;
    public final byte[] mData;
    //public final Sequence sequence;

    public MusicLump(FileChannel fc, String name, int filepos, int size) throws IOException {
        super(name, filepos, size);
        fc.position(filepos);
        ByteBuffer bb = ByteBuffer.allocate(size);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        fc.read(bb);
        bb.position(4);  // Skip the MUS + SUB(byte)

        mLength = bb.getShort() & 0x0000FFFF;
        mStart = bb.getShort() & 0x0000FFFF;
        priChannels = bb.getShort() & 0x0000FFFF;
        secChannels = bb.getShort() & 0x0000FFFF;
        instCount = bb.getShort() & 0x0000FFFF;
        bb.getShort();  // Skip this 0 short.

        instrument = new int[instCount];

        for (int i = 0; i < instrument.length; i++) {
            instrument[i] = bb.getShort() & 0x0000FFFF;
        }

        bb.position(mStart);
        mData = new byte[mLength];  // Rest of it should be music                
        bb.get(mData);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Music:\n    ");

        sb.append("l:").append(mLength).append("   s:").append(mStart).
                append("    pChan:").append(priChannels).
                append("    sChan:").append(secChannels).
                append("  instCnt:").append(instCount);
        sb.append("\n");
        sb.append("  ");
        for (int i = 0; i < instrument.length; i++) {
            String padding = "00";
            String result = padding + Integer.toHexString(instrument[i] & 0xFF);
            result = result.substring(result.length() - 2, result.length());  // take the right-most 64 digits
            sb.append(result);
            sb.append("  ");

            if ((i + 1) % 16 == 0) {
                sb.append("\n");
            }
        }
        sb.append("\n");

//        Track track = sequence.getTracks()[0];
//        for ( int i=0; i< track.size(); i++ ) {
//            MidiEvent me = track.get(i);
//            sb  .append("   MidiEvent @:").append(me.getTick())
//                .append("   Message: ").append(me.getMessage().toString())
//                .append("\n");
//        }
//        sb.append("    ");
//        for (int i=0; i< mData.length; i++ ) {
//            String padding = "00";
//            String result = padding + Integer.toHexString(mData[i]&0xFF);
//            result = result.substring(result.length() - 2, result.length());  // take the right-most 64 digits
//            sb.append(result);
//            sb.append(" ");
//            
//            if ( (i+1)%64 == 0) {
//                sb.append("\n    ");
//            }
//        }
//        sb.append("\n");
        return sb.toString();
    }

}
