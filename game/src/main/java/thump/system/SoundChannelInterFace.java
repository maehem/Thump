/*
 * Java specific channel for sound management.
 */
package thump.system;

import javax.sound.sampled.SourceDataLine;
import thump.game.sound.sfx.Sounds.SfxEnum;

/**
 *
 * @author mark
 */
public class SoundChannelInterFace /* extends SourceDataLine ??? */ {
    // SFX id of the playing sound effect.
    // Used to catch duplicates (like chainsaw).
    public SfxEnum	channelids = SfxEnum.sfx_None;			
    
    public SourceDataLine line;
    
    public byte[] data = null;
    
    private int step = 0;  // Byte in data we are at.
    
    // Time/gametic that the channel started playing,
    //  used to determine oldest, which automatically
    //  has lowest priority.
    // In case number of active sounds exceeds
    //  available channels.
    public int	channelstart = -1;
    
    // Gametic at which this sound would stop
    public int	channelsend = -1;
    
    
    // The sound in channel handles,
    //  determined on registration,
    //  might be used to unregister/stop/modify,
    //  currently unused.
    //public int channelhandles = -1;
    
    // The channel step amount...
    public int	channelstep;
    // ... and a 0.16 bit remainder of last step.
    public int	channelstepremainder;
    
    public void start( long tic ) {
        this.channelstart = (int) tic;
        this.channelsend = this.channelstart + data.length;
        step = 0;
        line.start();
    }
    
    public void stop() {
        line.stop();
    }
    
    public void clear() {
        stop();

        channelstart = -1;
        channelsend  = -1;
        
        data = null;
        step = 0;
        channelids = SfxEnum.sfx_None;
    }
    
    public void setData(byte[] b) {
        this.data = b;
        // Set pointer to end of raw data.
        //this.channelsend = b.length;
    }
    
    public boolean isPlaying( long now ) {
        if ( channelstart < 0 ) {
            return false;
        }
        
        return now<channelsend;
    }
    
    public void play() {
        if (data == null ) {
            return;
        }
        
        if ( step > data.length-1) {
            clear();
        } else {
            int len;
            len = line.available();
            if ( len > data.length-step ) {
                len = data.length-step;
            }
            line.write(data, step, len );
            step+=len;
        }
    }
}
