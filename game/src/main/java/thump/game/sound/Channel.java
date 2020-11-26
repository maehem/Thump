/*
 * Sound Channel
 */
package thump.game.sound;

import thump.game.maplevel.MapObject;
import thump.game.sound.sfx.SfxInfo;

/**
 *
 * @author mark
 */
public class Channel {
    public SfxInfo      sfxinfo;    // sound information (if null, channel avail.)
    public MapObject    origin;     // origin of sound
    public int          handle;     // handle of the sound being played
}
