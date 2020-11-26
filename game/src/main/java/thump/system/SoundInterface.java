/*
 * Sound Interface - Java.sound and Java.midi interface
 */
package thump.system;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import thump.game.Game;
import thump.base.Defines;
import thump.game.sound.sfx.Sounds;
import static thump.game.sound.sfx.Sounds.S_sfx;
import thump.game.sound.sfx.Sounds.SfxEnum;
import static thump.game.sound.sfx.Sounds.SfxEnum.*;
import thump.game.sound.sfx.VSP;

/**
 *
 * @author mark
 */
public class SoundInterface {
    public final static int NUM_CHANNELS = 8;
    
    // MIDI Synth and Sequencer
    Sequencer sequencer = null;
    Synthesizer synthesizer = null;
    
        
    //private ArrayList<Sequence> songs = new ArrayList<>();
    private int musicVolume = 127;
    private float sampleRate = 11025;
    
    private byte[] currentSoundData;
    
    // Need a Java Line per channel.
    
    
    // SFX id of the playing sound effect.
    // Used to catch duplicates (like chainsaw).
    //SfxEnum	channelids[] = new SfxEnum[NUM_CHANNELS];			
    //byte	channels[][] = new byte[NUM_CHANNELS][];
    SoundChannelInterFace channels[] = new SoundChannelInterFace[NUM_CHANNELS];
    
    // Time/gametic that the channel started playing,
    //  used to determine oldest, which automatically
    //  has lowest priority.
    // In case number of active sounds exceeds
    //  available channels.
    //int	channelstart[] = new int[NUM_CHANNELS];
    
    //int	channelsend[] = new int[NUM_CHANNELS];
    
    // The actual lengths of all sound effects.
    int lengths[] = new int[SfxEnum.values().length];
    
    // The sound in channel handles,
    //  determined on registration,
    //  might be used to unregister/stop/modify,
    //  currently unused.
    //int channelhandles[] = new int[NUM_CHANNELS];
    // Volume lookups.
    int vol_lookup[] = new int[128 * 256];


    
    //
    //  SFX I/O
    //

    // Init at program start...
    public void I_InitSound() {
        try {
            for ( int i=0; i<NUM_CHANNELS; i++ ) {
                channels[i] = new SoundChannelInterFace();
                SourceDataLine line; // AudioSystem
                AudioFormat af = new AudioFormat(sampleRate, 8, 1, false, false);
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, af);
                line = (SourceDataLine) AudioSystem.getLine(info);

                line.open(af, 65535);
                line.start();
                channels[i].line = line;
            }
    
        } catch (LineUnavailableException ex) {
            Logger.getLogger(SoundInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    // ... update sound buffer and audio device at runtime...
    public void I_UpdateSound() {
        // Mix current sound data.
        // Data, from raw sound, for right and left.
        byte[]	sample;
        int		dl;
        int		dr;

        // Pointers in global mixbuffer, left, right, end.
        int		leftoutIx;
        int		rightoutIx;
        int		leftendIx;
        // Step in mixbuffer, left and right, thus two.
        int				step;

        // Mixing channel index.
        //int				chan;

//        // Left and right channel
//        //  are in global mixbuffer, alternating.
//        leftoutIx = mixbuffer;
//        rightoutIx = mixbuffer+1;
//        step = 2;
//
//        // Determine end, for left channel only
//        //  (right channel is implicit).
//        leftend = mixbuffer + SAMPLECOUNT*step;
//
//        // Mix sounds into the mixing buffer.
//        // Loop over step*SAMPLECOUNT,
//        //  that is 512 values for two channels.
//        while (leftoutIx != leftendIx)
//        {
//        // Reset left/right value. 
//        dl = 0;
//        dr = 0;

        for ( int chan = 0; chan < NUM_CHANNELS; chan++ ) {
            // Check channel, if active.
            //if (channels[ chan ]!=null) {
                SoundChannelInterFace channel = channels[chan];
                
                // Get the raw data from the channel. 
                //sample = channels[ chan ];
                // Add left and right part
                //  for this channel (sound)
                //  to the current data.
                // Adjust volume accordingly.
//   TODO             dl += channelleftvol_lookup[ chan ][sample];
//                dr += channelrightvol_lookup[ chan ][sample];
                
                // Increment index ???
                //channel.channelstepremainder += channel.channelstep;
                // MSB is next sample???
                //???  channels[ chan ] += channelstepremainder[ chan ] >> 16;
                // Limit to LSB???
                //channel.channelstepremainder &= 65536-1;
                
                // Check whether we are done.
                //if (channels[ chan ] >= channel.channelsend)
                if ( !channel.isPlaying(Game.getInstance().gametic) ) {
                    channel.clear(); // Resets the channel.
                }
                
                // Play n tics worth of bytes
                channel.play();
                
                
            //}
        }

        // Clamp to range. Left hardware channel.
        // Has been char instead of short.
        // if (dl > 127) *leftout = 127;
        // else if (dl < -128) *leftout = -128;
        // else *leftout = dl;

//        if (dl > 0x7fff)
//            leftout = 0x7fff;
//        else if (dl < -0x8000)
//            leftout = -0x8000;
//        else
//            leftout = dl;
//
//        // Same for right hardware channel.
//        if (dr > 0x7fff)
//            rightout = 0x7fff;
//        else if (dr < -0x8000)
//            rightout = -0x8000;
//        else
//            rightout = dr;
//
//        // Increment current pointers in mixbuffer.
//        leftout += step;
//        rightout += step;
//        }
        
    }
    
    public void I_SubmitSound(){
        //line.write(currentSoundData, 0, currentSoundData.length-1);        
    }

    // ... shut down and relase at program termination.
    public void I_ShutdownSound(){
        for (SoundChannelInterFace channel : channels) {
            //channel.line.drain();
            channel.line.stop();
            channel.line.close();
        }
    }

    // Initialize channels?
    public void I_SetChannels() {
        // Generates volume lookup tables
        //  which also turn the unsigned samples
        //  into signed samples.
        for (int i=0 ; i<128 ; i++) {
            for (int j=0 ; j<256 ; j++) {
                vol_lookup[i*256+j] = (i*(j-128)*256)/127;
            }
        }
        
    }

//    void I_SetSfxVolume(int volume) {
//        // Identical to DOS.
//        // Basically, this should propagate
//        //  the menu/config file setting
//        //  to the state variable used in
//        //  the mixing.
//        snd_SfxVolume = volume;
//    }

    // Not used in Java.  We reference the WAD object directly.
//    // Get raw data lump index for sound descriptor.
//    public int I_GetSfxLumpNum (SfxInfo sfxinfo ) {
//        
//        return 0;
//    }


    // Starts a sound in a particular sound channel.
    public int I_StartSound(
            Sounds.SfxEnum id,
            VSP vsp,
            int priority) {
        
        return addsfx( id, vsp.vol, 0/*steptable[pitch]*/, vsp.sep );
        //return 0;
    }


    // Stops a sound channel.
    public void I_StopSound(int channel) {
        channels[channel].stop();
    }

    // Called by S_*() functions
    //  to see if a channel is still playing.
    // Returns 0 if no longer playing, 1 if playing.
    public boolean I_SoundIsPlaying(int channel) {
        // Ouch.
        return Game.getInstance().gametic < channels[channel].channelsend;
    }

    // UNUSED
    // Updates the volume, separation,
    //  and pitch of a sound channel.
//    public void I_UpdateSoundParams(
//            int handle,
//            VSP vsp  ) {
//
//    }


    
    
    
    //
    //  MUSIC I/O
    //
    public void I_InitMusic() {
        try {
            sequencer = MidiSystem.getSequencer();
            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            sequencer.getTransmitter().setReceiver(synthesizer.getReceiver());
            sequencer.open();
            sequencer.stop();
        } catch (MidiUnavailableException ex) {
            Defines.logger.log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void I_ShutdownMusic() {
            if ( sequencer != null ) {
                sequencer.stop();
                sequencer.close();
            }
            if ( synthesizer != null && synthesizer.isOpen() ) {
                synthesizer.close();
            }
        
    }
    
    // Volume.
    public void I_SetMusicVolume(int volume) {
        musicVolume = volume;
        MidiChannel[] channels = synthesizer.getChannels();

        for (MidiChannel channel : channels) {
            channel.controlChange(7, musicVolume);
        }
    }
    
    // PAUSE game handling.
    public void I_PauseSong(Sequence handle) {        
        sequencer.stop();
    }
    
    public void I_ResumeSong(Sequence handle) {
        sequencer.start();
    }
    
    // Registers a song handle to song data.
    public Sequence I_RegisterSong(Sequence seq) {
        return seq;   // Java does not need this. But we return handle for Sound.java to use.
    }
    
    // Called by anything that wishes to start music.
    //  plays a song, and when the song is done,
    //  starts playing it again in an endless loop.
    // Horrible thing to do, considering.
    public void I_PlaySong(
            Sequence handle,
            boolean looping) {
        try {
            sequencer.setSequence(handle);
            I_SetMusicVolume(musicVolume);
            
            if ( looping ) {
                sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            } else {
                sequencer.setLoopCount(0);
            }
            sequencer.setTickPosition(0);
            sequencer.start();
            
        } catch (InvalidMidiDataException ex) {
            Logger.getLogger(SoundInterface.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    // Stops a song over 3 seconds.
    public void I_StopSong(Sequence handle) {
        sequencer.stop();
    }
    
    // See above (register), then think backwards
    public void I_UnRegisterSong(Sequence handle) {
        // Nothing to do.
    }

    
    
    int handlenums = 0;
    //
    // This function adds a sound to the
    //  list of currently active sounds,
    //  which is maintained as a given number
    //  (eight, usually) of internal channels.
    // Returns a handle.
    //
    int addsfx(
            SfxEnum	sfxid,
            int volume,
            int step,
            int seperation) {

        int i;
        int rc = -1;

        int oldest = Game.getInstance().gametic;
        int oldestnum = 0;
        int slot;

        int rightvol;
        int leftvol;

        // Chainsaw troubles.
        // Play these sound effects only one at a time.
        if ( sfxid == sfx_sawup
             || sfxid == sfx_sawidl
             || sfxid == sfx_sawful
             || sfxid == sfx_sawhit
             || sfxid == sfx_stnmov
             || sfxid == sfx_pistol	 ) {
            // Loop all channels, check.
            for (i=0 ; i<NUM_CHANNELS ; i++) {
                // Active, and using the same SFX?
                if (   (channels[i].channelids == sfxid)
                    && (channels[i].isPlaying(Game.getInstance().gametic)) 
                ) {
                    // Reset.
                    ///channels[i].stop();
                    channels[i].clear();
                    // We are sure that iff, there will only be one.
                    break;
                }
            }
        }

        // Loop all channels to find oldest SFX.
        for (i=0; (i<NUM_CHANNELS) && (channels[i]!=null); i++) {
            if (channels[i].channelstart < oldest) {
                oldestnum = i;
                oldest = channels[i].channelstart;
            }
        }

        // Tales from the cryptic.
        // If we found a channel, fine.
        // If not, we simply overwrite the first one, 0.
        // Probably only happens at startup.
        if (i == NUM_CHANNELS) {
            slot = oldestnum;
        } else {
            slot = i;
        }

        // Okay, in the less recent channel,
        //  we will handle the new SFX.
        // Set pointer to raw data.
        
        //TODO  add setData(byte[] b) method.
        channels[slot].setData(S_sfx[sfxid.ordinal()].data);
        //channels[slot].data = S_sfx[sfxid.ordinal()].data;
        // Set pointer to end of raw data.
        //channels[slot].channelsend = channels[slot].data.length;

        // Reset current handle number, limited to 0..100.
        //if (handlenums==100) {
        //    handlenums = 0;
        //}
        // Old code, not sure how this would have worked?
        //if (!handlenums)
        //    handlenums = 100;

        // Assign current handle number.
        // Preserved so sounds could be stopped (unused).
        //channels[slot].channelhandles = handlenums;
        //rc = handlenums;
        //handlenums++;

        // Set stepping???
        // Kinda getting the impression this is never used.
        //channelstep[slot] = step;
        // ???
        //channelstepremainder[slot] = 0;
        // Should be gametic, I presume.
        channels[slot].start(Game.getInstance().gametic);
        //channels[slot].channelstart = Game.getInstance().gametic;

//        // Separation, that is, orientation/stereo.
//        //  range is: 1 - 256
//        seperation += 1;
//
//        // Per left/right channel.
//        //  x^2 seperation,
//        //  adjust volume properly.
//        leftvol =
//            volume - ((volume*seperation*seperation) >> 16); ///(256*256);
//        seperation = seperation - 257;
//        rightvol =
//            volume - ((volume*seperation*seperation) >> 16);	
//
//        // Sanity check, clamp volume.
//        if (rightvol < 0 || rightvol > 127)
//            I_Error("rightvol out of bounds");
//
//        if (leftvol < 0 || leftvol > 127)
//            I_Error("leftvol out of bounds");
//
//        // Get the proper lookup table piece
//        //  for this volume level???
//        channelleftvol_lookup[slot] = vol_lookup[leftvol*256];
//        channelrightvol_lookup[slot] = vol_lookup[rightvol*256];

        // Preserve sound SFX id,
        //  e.g. for avoiding duplicates of chainsaw.
        channels[slot].channelids = sfxid;

        // You tell me.
        //return rc;
        return slot;
    }

}
