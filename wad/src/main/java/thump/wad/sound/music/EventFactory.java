/*
 * Interprets Doom MUS events into Java MIDI events.
 */
package thump.wad.sound.music;

import java.nio.ByteBuffer;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;

/**
 *
 * @author mark
 */
public class EventFactory {
    //private static final Logger logger = Logger.getGlobal();
    private final static int VELOCITY = 64;

    public static MidiEventWrapper createEvent(ByteBuffer bb, long ticks, int[] channelVolumes ) throws InvalidMidiDataException {
        byte b = bb.get();
        int channel = b & 0x00000F; //  Lower four bits of the byte.
        int t = (b&0x70) >> 4;     //  First three bits of the top nybble.
        MidiEventType type = MidiEventType.values()[t];
        boolean last = (b&0x80) != 0; // If this bit is set, it means bytes follow to establish 'ticks' until next note.
        long delay = 0;
        
        MidiEvent event = null;

        switch (type) {
            case RELEASE_NOTE:
                event = createReleaseNote(bb, channel, ticks );
                break;
            case PLAY_NOTE:
                event = createPlayNote(bb, channel, ticks, channelVolumes );
                break;
            case PITCH_BEND:
                event = createPitchBend(bb, channel, ticks);
                break;
            case SYSTEM_EVENT:
                event = createSystemEvent(bb, channel, ticks);
                break;
            case CONTROLLER:
                event = createControllerEvent(bb, channel, ticks);
                break;
            case END_OF_MEASURE:
                // Not used by Midi.
                event = null;
                break;
            case FINSH:
                event = createFinish(bb, channel, ticks);
                break;
            default:
                throw new InvalidMidiDataException("Unsupported MIDI Event Type: " + type.name());
        }

        while ( last ) {            
            int dval = bb.get()&0xFF;
            last = (dval&0x80) != 0; // bit 7 set if next byte adds to delay.
            delay = delay * 128 + (dval&0x7F);                                        
        }
                
        return new MidiEventWrapper(event, delay, channel);
    }

    private static MidiEvent createReleaseNote(ByteBuffer bb, int channel, long ticks) throws InvalidMidiDataException {
        // Get note data
        int note = bb.get()&0x7F;
        //logger.log(Level.CONFIG, "Create Release Note:   ch: {0}  note:{1}", new Object[]{channel,note});
        
        ShortMessage message = new ShortMessage(ShortMessage.NOTE_OFF, channel, note, VELOCITY);
        
        return new MidiEvent(message, ticks);
    }

    private static MidiEvent createPlayNote(ByteBuffer bb, int channel, long ticks, int[] lastVol ) throws InvalidMidiDataException {
        int b = bb.get()&0xFF;
        int vol = b&0x80; // If set, second byte is channel volume/velocity, else use previous volume.
        int note = b&0x7F;
        int velocity;
        
        if ( vol != 0 ) {
            velocity = bb.get()&0x7F;
            lastVol[channel] = velocity; // Update our tracker.
        } else {
            velocity = lastVol[channel];
        }
        
        //logger.log(Level.CONFIG, "Create Play Note:   ch: {0}  note:{1}  vel:{2}", new Object[]{channel,note,velocity});
        
        ShortMessage message = new ShortMessage(ShortMessage.NOTE_ON, channel, note, velocity);
        
        return new MidiEvent(message, ticks);
    }

    private static MidiEvent createPitchBend(ByteBuffer bb, int channel, long ticks) throws InvalidMidiDataException {
        int bend = (bb.get()&0xFF)<<6; // Shift bend six bits up.
        //logger.log(Level.CONFIG, "Create Pitch Bend:   ch: {0}  v:{1}", new Object[]{channel,bend});
        
        int bL = bend&0x7F;
        int bU = (bend>>7)&0x7F;
        ShortMessage message = new ShortMessage(ShortMessage.PITCH_BEND, channel, bL, bU); // 50% chance bL and bU are reversed.
        
        return new MidiEvent(message, ticks);
    }

    private static MidiEvent createSystemEvent(ByteBuffer bb, int channel, long ticks) throws InvalidMidiDataException {
        //    10    120 (78h)  All sounds off
        //    11    123 (7Bh)  All notes off
        //    12    126 (7Eh)  Mono
        //    13    127 (7Fh)  Poly
        //    14    121 (79h)  Reset all controllers
        int sysEv = bb.get()&0x7F;
        int midiCode = 0;
        int midiValue = 0;
        switch ( sysEv ) {
            case 10:
                midiCode = 120;
                midiValue = 0;
                break;
            case 11:
                midiCode = 123;
                midiValue = 0;
                break;
            case 12:
                midiCode = 126;
                midiValue = 15;  // Number of channels ????
                break;
            case 13:
                midiCode = 127;
                midiValue = 0;
                break;
            case 14:
                midiCode = 121;
                midiValue = 0;
                break;
            default:
                throw new InvalidMidiDataException("Unsupported MIDI System Event Type: " + sysEv);
        }
        //logger.log(Level.CONFIG, "Create System Event:   ch: {0}  code:{1}   val:{2}", new Object[]{channel,midiCode, midiValue});
               
        ShortMessage m = new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, midiCode, midiValue);
        
        return new MidiEvent(m, ticks);
    }
    
    private static MidiEvent createControllerEvent(ByteBuffer bb, int channel, long ticks) throws InvalidMidiDataException {
        int cNumber = bb.get()&0x7F;
        int cValue = bb.get()&0x7F;
        int midiCode = 0;
        int command = ShortMessage.CONTROL_CHANGE;
        // TODO ::  This could just be an array table.
        switch( cNumber ) {
            case 0: //0	N/A	Change instrument (MIDI event 0xC0)
                command = ShortMessage.PROGRAM_CHANGE;
                midiCode = cValue;  // Only one byte for this command. First byte(command) is the value.
                cValue = 0;
                break;
            case 1:// 0 or 32	Bank select: 0 by default
                midiCode = 0;
                break;
            case 2:// 1	Modulation (frequency vibrato depth)
                midiCode = 1;
                break;
            case 3:// 7	Volume: 0-silent, ~100-normal, 127-loud
                midiCode = 7;
                break;
            case 4:// 10	Pan (balance): 0-left, 64-center (default), 127-right
                midiCode = 10;
                break;
            case 5:// 11	Expression
                midiCode = 11;
                break;
            case 6:// 91	Reverb depth
                midiCode = 91;
                break;
            case 7:// 93	Chorus depth
                midiCode = 93;
                break;
            case 8:// 64	Sustain pedal (hold)
                midiCode = 64;
                break;
            case 9:// 67	Soft pedal
                midiCode = 67;
                break;
            default:
                throw new InvalidMidiDataException("Unsupported MIDI Controller Event Type: " + cNumber);
        }
               
        //logger.log(Level.CONFIG, "Create Controller Event:   ch: {0}  code:{1}   val:{2}", new Object[]{channel,midiCode, cValue});
        ShortMessage m = new ShortMessage(command, channel, midiCode, cValue);
        
        return new MidiEvent(m, ticks);
    }

    private static MidiEvent createFinish(ByteBuffer bb, int channel, long ticks) throws InvalidMidiDataException {
//****  set end of track (meta event)  ****
//		mt = new MetaMessage();
//        byte[] bet = {}; // empty array
//		mt.setMessage(0x2F,bet,0);
//		me = new MidiEvent(mt, (long)140);
//		t.add(me);
        
        //logger.log(Level.CONFIG, "Create Finish Event:   ch: {0}", channel);
        MetaMessage message = new MetaMessage(0x2F, new byte[]{}, 0);
        
        return new MidiEvent(message, ticks);
    }

}
