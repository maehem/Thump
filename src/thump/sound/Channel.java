/*
 * Sound Channel
 */
package thump.sound;

import thump.maplevel.MapObject;
import thump.sound.sfx.SfxInfo;

/**
 *
 * @author mark
 */
public class Channel {
    public SfxInfo      sfxinfo;    // sound information (if null, channel avail.)
    public MapObject    origin;     // origin of sound
    public int          handle;     // handle of the sound being played
}
