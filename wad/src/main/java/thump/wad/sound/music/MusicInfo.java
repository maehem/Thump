/*
 * MIDI Music Info
 */
package thump.wad.sound.music;

import javax.sound.midi.Sequence;

/**
 *
 * @author mark
 */
public class MusicInfo {

    public MusicInfo(String name, int lumpnum) {
        this.lumpnum = lumpnum;
        this.name = name;
    }
    
    // up to 6-character name
    public final String	name;

    // lump number of music
    public int		lumpnum;
    
    // music data
    public Sequence	data = null;

    // music handle once registered
    public Sequence handle = null;  // When not null sequence is registered.
}
