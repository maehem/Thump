/*
 * Doom WAD MUS events have standard Midi Events along with delay/ticks until
 * following event.
 */
package thump.wad.sound.music;

import javax.sound.midi.MidiEvent;

/**
 *
 * @author mark
 */
public class MidiEventWrapper {
    public final MidiEvent event;
    public final int channel;
    //public final int volume;
    public final long delay;

    public MidiEventWrapper(MidiEvent event, long delay, int channel) {
        this.event = event;
        this.delay = delay;
        this.channel = channel;
        //this.volume = volume;
    }
    
}
