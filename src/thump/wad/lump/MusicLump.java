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

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.logging.Logger;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import static javax.sound.midi.Sequence.PPQ;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;
import thump.sound.midi.EventFactory;
import thump.sound.midi.MidiEventWrapper;

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
    public final Sequence sequence;

    public MusicLump(FileChannel fc, String name, int filepos, int size) throws IOException, InvalidMidiDataException {
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

        sequence = new Sequence(PPQ, 35, 1);   // Ticks per second = 140.  Midi uses beats per quarter note so 140/4=35
        Track track = sequence.getTracks()[0];

        // Turn on General MIDI
        byte[] b = {(byte) 0xF0, 0x7E, 0x7F, 0x09, 0x01, (byte) 0xF7};
        SysexMessage sm = new SysexMessage();
        sm.setMessage(b, 6);
        MidiEvent me = new MidiEvent(sm, 0);
        track.add(me);

//        // Set tempo
//        MetaMessage mt = new MetaMessage();
//        byte[] bt = {0x02, (byte) 0x00, 0x00};
//        mt.setMessage(0x51, bt, 3);
//        me = new MidiEvent(mt, (long) 0);
//        track.add(me);

        // Set track name
        MetaMessage mt = new MetaMessage();
        mt.setMessage(0x03, name.getBytes(), name.length());
        me = new MidiEvent(mt, 0);
        track.add(me);

        bb.position(mStart);
        int ticks = 0;

        // Track channel volume.
        int channelVolume[] = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        while (bb.hasRemaining()) {
            MidiEventWrapper ew = EventFactory.createEvent(bb, ticks, channelVolume);
//            logger.log(Level.CONFIG, "    tick:{0} event: {1}  ch: {2}",
//                    new Object[]{ew.event.getTick(), ew.event.getMessage().getStatus(), ew.channel});
            ticks += ew.delay;
            track.add(ew.event);
        }

        try {
            //****  write the MIDI sequence to a MIDI file  ****
            File f = new File("/Users/mark/Desktop/MIDI/" + name.substring(0, 5) + ".mid");
            MidiSystem.write(sequence, 1, f);
        } //try
        catch (Exception e) {
            System.out.println("Exception caught " + e.toString());
        } //catch

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
