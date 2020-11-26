/*
 * Info for SFX sounds
 */
package thump.game.sound.sfx;

/**
 *
 * @author mark
 */
public class SfxInfo {

    public SfxInfo(String name, boolean singularity, int priority, Sounds.SfxEnum link, int pitch, int volume) {
        this.name = name;
        this.singularity = singularity;
        this.priority = priority;
        this.link = link;
        this.vsp.pitch = pitch;
        this.vsp.vol = volume;
    }
    
    public String	name;           // up to 6-character name
    public boolean	singularity;    // Sfx singularity (only one at a time)
    public int		priority;       // Sfx priority

    public Sounds.SfxEnum	link;           // referenced sound if a link
    public final VSP         vsp = new VSP();
    
    //Object	pitch = null;   // pitch if a link

    //Object	volume = null;  // volume if a link

    public byte[]	data = null;    // sound data

    // this is checked every second to see if sound
    // can be thrown out (if 0, then decrement, if -1,
    // then throw out, if > 0, then it is in use)
    public  int		usefulness = -1;

//    public int		lumpnum;        // lump number of sfx
}
