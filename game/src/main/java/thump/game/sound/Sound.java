/*
 * The not so system specific sound interface.
 */
package thump.game.sound;

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import static javax.sound.midi.Sequence.PPQ;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import thump.base.Defines;
import static thump.base.Defines.logger;
import thump.game.Defines.GameMode;
import thump.base.FixedPoint;
import static thump.base.FixedPoint.FRACBITS;
import thump.game.play.Random;
import static thump.base.Tables.ANGLETOFINESHIFT;
import static thump.base.Tables.finesine;
import thump.game.Game;
import thump.game.maplevel.MapObject;
import thump.game.Player;
import thump.game.sound.music.EventFactory;
import thump.game.sound.music.MidiEventWrapper;
import thump.game.sound.sfx.SfxInfo;
import thump.game.sound.sfx.Sounds;
import thump.game.sound.sfx.Sounds.MusicEnum;
import static thump.game.sound.sfx.Sounds.MusicEnum.*;
import static thump.game.sound.sfx.Sounds.S_sfx;
import static thump.game.sound.sfx.Sounds.SfxEnum.*;
import thump.game.sound.sfx.VSP;
import thump.system.SoundInterface;
import thump.wad.lump.MusicLump;
import thump.wad.lump.SoundEffectLump;
import thump.wad.sound.music.MusicInfo;

/**
 *
 * @author mark
 */
public class Sound {
    
    public final SoundInterface soundInterface = new SoundInterface();

    // Purpose?
    public static final byte snd_prefixen[]
    = { 'P', 'P', 'A', 'S', 'S', 'S', 'M', 'M', 'M', 'S', 'S', 'S' };

    public static final int S_MAX_VOLUME = 127;
    

    // when to clip out sounds
    // Does not fit the large outdoor areas.
    public static final int S_CLIPPING_DIST = (1200*0x10000);

    // Distance tp origin when sounds should be maxed out.
    // This should relate to movement clipping resolution
    // (see BLOCKMAP handling).
    // Originally: (200*0x10000).
    public static final int S_CLOSE_DIST    = (160 * 0x10000);

    public static final int S_ATTENUATOR    = ((S_CLIPPING_DIST - S_CLOSE_DIST) >> FRACBITS);

    // Adjustable by menu. Not used!
    //public static final int NORM_VOLUME     = snd_MaxVolume;

    public static final int NORM_PITCH      = 128;
    public static final int NORM_PRIORITY   = 64;
    public static final int NORM_SEP        = 128;

    public static final int S_PITCH_PERTURB = 1;
    public static final int S_STEREO_SWING  = (96 * 0x10000);

    // percent attenuation from front to back
    public static final int S_IFRACVOL      = 30;

    public static final int NA              = 0;
    public static final int S_NUMCHANNELS   = 8;


    // Current music/sfx card - index useless
    //  w/o a reference LUT in a sound module.
    //extern int snd_MusicDevice;
    //extern int snd_SfxDevice;
    // Config file? Same disclaimer as above.
    //extern int snd_DesiredMusicDevice;
    //extern int snd_DesiredSfxDevice;


    // following is set
    //  by the defaults code in M_misc:
    // number of channels available
    private int    numChannels;	

    // the set of channels available
    static Channel[]	channels;

    // These are not used, but should be (menu).
    // Maximum volume of a sound effect.
    // Internal default is max out of 0-15.
    int 		snd_SfxVolume = 8;

    // Maximum volume of music. Useless so far.
    int 		snd_MusicVolume = 8; 

    // whether songs are mus_paused
    boolean		mus_paused;	

    // music currently being played
    MusicInfo           mus_playing=null;

    static int		nextcleanup;

//    //
//    // Internals.
//    //
//    int
//    S_getChannel
//    ( void*		origin,
//      SfxInfo	sfxinfo );
//
//
//    int
//    S_AdjustSoundParams
//    ( MapObject	listener,
//      MapObject	source,
//      int*		vol,
//      int*		sep,
//      int*		pitch );
//
//    void S_StopChannel(int cnum);
//


    //
    // Initializes sound stuff, including volume
    // Sets channels, SFX and music volume,
    //  allocates channel buffer, sets S_sfx lookup.
    //
    public void S_Init(
            int sfxVolume,
            int musicVolume ) 
    {
        int i;

      Defines.logger.log(Level.CONFIG, "S_Init: default sfx volume {0}\n", sfxVolume);

      soundInterface.I_InitSound();
      soundInterface.I_InitMusic();
      
      // Whatever these did with DMX, these are rather dummies now.
      soundInterface.I_SetChannels();

      S_SetSfxVolume(sfxVolume);
      S_SetMusicVolume(musicVolume);

      // Allocating the internal channels for mixing
      // (the maximum numer of sounds rendered
      // simultaneously) within zone memory.
      numChannels = SoundInterface.NUM_CHANNELS;
      channels = new Channel[numChannels];

      // Free all channels for use
      for (i=0 ; i<numChannels ; i++) {
        channels[i] = new Channel();
        channels[i].sfxinfo = null;
      }

      // no sounds are playing, and they are not mus_paused
      mus_paused = false;

      // Note that sounds have not been cached (yet).
//      for (i=1 ; i<S_sfx.length ; i++) {
//          S_sfx[i].lumpnum = -1;
//          S_sfx[i].usefulness = -1;
//        }
    }


    //
    // Per level startup code.
    // Kills playing sounds at start of level,
    //  determines music if any, changes music.
    //
    public void S_Start() {
        int cnum;
        int mnum;

        // kill all playing sounds at start of level
        //  (trust me - a good idea)
        for (cnum = 0; cnum < numChannels; cnum++) {
            if (channels[cnum].sfxinfo != null) {
                S_StopChannel(cnum);
            }
        }

        // start new music for the level
        mus_paused = false;

        GameMode gamemode = Game.getInstance().gameMode;
        int gameepisode = Game.getInstance().gameepisode;
        int gamemap = Game.getInstance().gamemap;

        if (gamemode == GameMode.COMMERCIAL) {
            mnum = mus_runnin.ordinal() + gamemap - 1;
        } else {
            MusicEnum spmus[] = {
                        // Song - Who? - Where?
                        mus_e3m4, // American	e4m1
                        mus_e3m2, // Romero	e4m2
                        mus_e3m3, // Shawn	e4m3
                        mus_e1m5, // American	e4m4
                        mus_e2m7, // Tim 	e4m5
                        mus_e2m4, // Romero	e4m6
                        mus_e2m6, // J.Anderson	e4m7 CHIRON.WAD
                        mus_e2m5, // Shawn	e4m8
                        mus_e1m9  // Tim	e4m9
                    };

            if (gameepisode < 4) {
                mnum = mus_e1m1.ordinal() + (gameepisode - 1) * 9 + gamemap - 1;
            } else {
                mnum = spmus[gamemap - 1].ordinal();
            }
        }

        // HACK FOR COMMERCIAL
        //  if (commercial && mnum > mus_e3m9)	
        //      mnum -= mus_e3m9;
        S_ChangeMusic(MusicEnum.values()[mnum], true);

        nextcleanup = 15;
    }


    public void S_StartSoundAtVolume(
            Object          origin_p,
            Sounds.SfxEnum  sfx_id,
            int             _volume  ) {

        VSP vsp = new VSP();
        boolean rc;
        //int sep;
        //int pitch;
        int priority;
        SfxInfo sfx;
        int cnum;

        vsp.vol = _volume;
        
        MapObject origin = (MapObject) origin_p;

        // Debug.
        //  fprintf( stderr,
        //  	   "S_StartSoundAtVolume: playing sound %d (%s)\n",
        //  	   sfx_id, S_sfx[sfx_id].name );
        // check for bogus sound #
        if (sfx_id.ordinal() < 1 || sfx_id.ordinal() > S_sfx.length) {
            //SystemInterface.I_Error("Bad sfx #: {0}\n", new Object[]{ sfx_id } );
            logger.log(Level.SEVERE, "Bad sfx #: {0}\n", new Object[]{ sfx_id } );
        }

        sfx = S_sfx[sfx_id.ordinal()];

        // Initialize sound parameters
        if (sfx.link!=null) {
            vsp.pitch = sfx.vsp.pitch;
            priority = sfx.priority;
            vsp.vol += sfx.vsp.vol;

            if (vsp.vol < 1) {
                return;
            }

            if (vsp.vol > snd_SfxVolume) {
                vsp.vol = snd_SfxVolume;
            }
        } else {
            vsp.pitch = NORM_PITCH;
            priority = NORM_PRIORITY;
        }

        Player[] players = Game.getInstance().players;
        int consoleplayer = Game.getInstance().consoleplayer;
        
        // Check to see if it is audible,
        //  and if not, modify the params
        if (origin!=null && origin != players[consoleplayer].mo) {
            rc = S_AdjustSoundParams(
                    players[consoleplayer].mo,
                    origin,
                    vsp
                     //& volume,
                     //& sep,
                     //& pitch
            );

            if (origin.x == players[consoleplayer].mo.x
                    && origin.y == players[consoleplayer].mo.y) {
                vsp.sep = NORM_SEP;
            }

            if (!rc) {
                return;
            }
        } else {
            vsp.sep = NORM_SEP;
        }

        // hacks to vary the sfx pitches
        if (sfx_id.ordinal() >= sfx_sawup.ordinal()
                && sfx_id.ordinal() <= sfx_sawhit.ordinal()) {
            vsp.pitch += 8 - (Random.getInstance().M_Random() & 15);

            if (vsp.pitch < 0) {
                vsp.pitch = 0;
            } else if (vsp.pitch > 255) {
                vsp.pitch = 255;
            }
        } else if (sfx_id != sfx_itemup
                && sfx_id != sfx_tink) {
            vsp.pitch += 16 - (Random.getInstance().M_Random() & 31);

            if (vsp.pitch < 0) {
                vsp.pitch = 0;
            } else if (vsp.pitch > 255) {
                vsp.pitch = 255;
            }
        }

        // kill old sound
        S_StopSound(origin);

        // try to find a channel
        cnum = S_getChannel(origin, sfx);

        if (cnum < 0) {
            return;
        }

        //
        // This is supposed to handle the loading/caching.
        // For some odd reason, the caching is done nearly
        //  each time the sound is needed?
        //
        // get lumpnum if necessary
//        if (sfx.lumpnum < 0) {
//            sfx.lumpnum = soundInterface.I_GetSfxLumpNum(sfx); // Pass this to constructor.
//        }
        
        if ( sfx.data == null ) {
            Defines.logger.log(Level.CONFIG, "Sound: DS_{0}\n", sfx.name);
            SoundEffectLump lump 
                    = (SoundEffectLump) Game.getInstance().wad.findByName("DS" + sfx.name.toUpperCase());
            sfx.data = lump.data;
        }
//        #
//        ifndef SNDSRV // cache data if necessary
//        if (!sfx.data) {
//            fprintf(stderr,
//                    "S_StartSoundAtVolume: 16bit and not pre-cached - wtf?\n");
//
//            // DOS remains, 8bit handling
//            //sfx.data = (void *) W_CacheLumpNum(sfx.lumpnum, PU_MUSIC);
//            // fprintf( stderr,
//            //	     "S_StartSoundAtVolume: loading %d (lump %d) : 0x%x\n",
//            //       sfx_id, sfx.lumpnum, (int)sfx.data );
//        }
//        #endif 
        // increase the usefulness
        if (sfx.usefulness++ < 0) {
            sfx.usefulness = 1;
        }

        // Assigns the handle to one of the channels in the
        //  mix/output buffer.
        channels[cnum].handle = soundInterface.I_StartSound(
                sfx_id,
                //sfx.data,
                vsp,
                priority);
    }	



    public void S_StartSound (
        Object	origin,
            Sounds.SfxEnum     sfx_id  )
    {
//    #ifdef SAWDEBUG
//        // if (sfx_id == sfx_sawful)
//        // sfx_id = sfx_itemup;
//    #endif

        S_StartSoundAtVolume(origin, sfx_id, snd_SfxVolume);


        // UNUSED. We had problems, had we not?
//    #ifdef SAWDEBUG
//    {
//        int i;
//        int n;
//
//        static MapObject      last_saw_origins[10] = {1,1,1,1,1,1,1,1,1,1};
//        static int		first_saw=0;
//        static int		next_saw=0;
//
//        if (sfx_id == sfx_sawidl
//            || sfx_id == sfx_sawful
//            || sfx_id == sfx_sawhit)
//        {
//            for (i=first_saw;i!=next_saw;i=(i+1)%10)
//                if (last_saw_origins[i] != origin)
//                    fprintf(stderr, "old origin 0x%lx != "
//                            "origin 0x%lx for sfx %d\n",
//                            last_saw_origins[i],
//                            origin,
//                            sfx_id);
//
//            last_saw_origins[next_saw] = origin;
//            next_saw = (next_saw + 1) % 10;
//            if (next_saw == first_saw)
//                first_saw = (first_saw + 1) % 10;
//
//            for (n=i=0; i<numChannels ; i++)
//            {
//                if (channels[i].sfxinfo == S_sfx[sfx_sawidl]
//                    || channels[i].sfxinfo == S_sfx[sfx_sawful]
//                    || channels[i].sfxinfo == S_sfx[sfx_sawhit]) n++;
//            }
//
//            if (n>1)
//            {
//                for (i=0; i<numChannels ; i++)
//                {
//                    if (channels[i].sfxinfo == S_sfx[sfx_sawidl]
//                        || channels[i].sfxinfo == S_sfx[sfx_sawful]
//                        || channels[i].sfxinfo == S_sfx[sfx_sawhit])
//                    {
//                        fprintf(stderr,
//                                "chn: sfxinfo=0x%lx, origin=0x%lx, "
//                                "handle=%d\n",
//                                channels[i].sfxinfo,
//                                channels[i].origin,
//                                channels[i].handle);
//                    }
//                }
//                fprintf(stderr, "\n");
//            }
//        }
//    }
//    #endif

    }


    public void S_StopSound(Object origin){

        for (int cnum=0 ; cnum<numChannels ; cnum++) {
            if (channels[cnum].sfxinfo!=null && channels[cnum].origin == origin) {
                S_StopChannel(cnum);
                break;
            }
        }
    }


    //
    // Stop and resume music, during game PAUSE.
    //
    public void S_PauseSound() {
        if (mus_playing!=null && !mus_paused) {
            soundInterface.I_PauseSong(mus_playing.handle);
            mus_paused = true;
        }
    }

    public void S_ResumeSound() {
        if (mus_playing!=null && mus_paused) {
            soundInterface.I_ResumeSong(mus_playing.handle);
            mus_paused = false;
        }
    }


    //
    // Updates music & sounds
    //
    public void S_UpdateSounds(MapObject listener_p) {
        boolean		audible;
        int		cnum;
        VSP             vsp = new VSP();
        //int		volume;
        //int		sep;
        //int		pitch;
        SfxInfo	sfx;
        Channel	c;

        MapObject	listener = listener_p;

        // Clean up unused data.
        // This is currently not done for 16bit (sounds cached static).
        // DOS 8bit remains. 
    //    if (gametic > nextcleanup)
    //    {
    //	for (i=1 ; i<NUMSFX ; i++)
    //	{
    //	    if (S_sfx[i].usefulness < 1
    //		&& S_sfx[i].usefulness > -1)
    //	    {
    //		if (--S_sfx[i].usefulness == -1)
    //		{
    //		    Z_ChangeTag(S_sfx[i].data, PU_CACHE);
    //		    S_sfx[i].data = 0;
    //		}
    //	    }
    //	}
    //	nextcleanup = gametic + 15;
    //    }

        for (cnum=0 ; cnum<numChannels ; cnum++) {
            c = channels[cnum];
            sfx = c.sfxinfo;

            if ( c.sfxinfo!=null ) {
                if (soundInterface.I_SoundIsPlaying(c.handle)) {
                    // initialize parameters
                    vsp.vol = snd_SfxVolume;
                    vsp.pitch = NORM_PITCH;
                    vsp.sep = NORM_SEP;

                    if ( sfx.link != null ) {
                        vsp.pitch = sfx.vsp.pitch;
                        vsp.vol += sfx.vsp.vol;
                        if (vsp.vol < 1)
                        {
                            S_StopChannel(cnum);
                            continue;
                        }
                        else if (vsp.vol > snd_SfxVolume)
                        {
                            vsp.vol = snd_SfxVolume;
                        }
                    }

                    // check non-local sounds for distance clipping
                    //  or modify their params
                    if (c.origin!=null && listener_p != c.origin)
                    {
                        audible = S_AdjustSoundParams(
                                listener,
                                c.origin,
                                vsp );

                        if (!audible) {
                            S_StopChannel(cnum);
                        }
//                        else {
//                            soundInterface.I_UpdateSoundParams(c.handle, vsp);
//                        }
                    }
                } else {
                    // if channel is allocated but sound has stopped,
                    //  free it
                    S_StopChannel(cnum);
                }
            }
        }
        // kill music if it is a single-play && finished
        // if (	mus_playing
        //      && !I_QrySongPlaying(mus_playing.handle)
        //      && !mus_paused )
        // S_StopMusic();
    }

    public void S_SetMusicVolume(int volume) {
        if (volume < 0 || volume > 127) {
            //SystemInterface.I_Error("Attempt to set music volume at {0}",
            //        new Object[]{volume});
            logger.log(Level.SEVERE, "Attempt to set music volume at {0}",
                    new Object[]{volume});
        }    

        //soundInterface.I_SetMusicVolume(127);
        soundInterface.I_SetMusicVolume(volume);
        snd_MusicVolume = volume;
    }



    public void S_SetSfxVolume(int volume) {

        if (volume < 0 || volume > 127) {
            //SystemInterface.I_Error("Attempt to set sfx volume at {0}\n", new Object[]{volume});
            logger.log(Level.SEVERE, "Attempt to set sfx volume at {0}\n", new Object[]{volume});
        }

        snd_SfxVolume = volume;
    }

    //
    // Starts some music with the music id found in sounds.h.
    //
    public void S_StartMusic(MusicEnum m_id) {
        S_ChangeMusic(m_id, false);
    }

    public void S_ChangeMusic(
            MusicEnum     musicnum,
            boolean looping   ) {
            MusicInfo music = null;
            //String namebuf;

        if (    (musicnum.ordinal() <= mus_None.ordinal())
             || (musicnum.ordinal() >= MusicEnum.values().length) ){
            //SystemInterface.I_Error("Bad music number {0}\n", new Object[]{musicnum});
            logger.log(Level.SEVERE, "Bad music number {0}\n", new Object[]{musicnum});
        } else {
            music = Sounds.S_music[musicnum.ordinal()];
        }

        if (mus_playing == music) {
            return;
        }

        // shutdown old music
        S_StopMusic();

        // get lumpnum if neccessary
        if (music.data==null) {
            //sprintf(namebuf, "d_%s", music.name);
            MusicLump lump = (MusicLump) Game.getInstance().wad.findByName("D_" + music.name.toUpperCase());
            //music.lumpnum = W_GetNumForName("D_" + music.name );
                try {
                    // load & register it
                    //music.data = lump.sequence;
                    music.data = createMidiSequence(lump.name, lump.mData);
                } catch (InvalidMidiDataException ex) {
                    Logger.getLogger(Sound.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
        music.handle = soundInterface.I_RegisterSong(music.data);

        // play it
        soundInterface.I_PlaySong(music.handle, looping);

        mus_playing = music;
    }


    public void S_StopMusic() {
        if (mus_playing!=null) {
            if (mus_paused) {
                soundInterface.I_ResumeSong(mus_playing.handle);
            }

            soundInterface.I_StopSong(mus_playing.handle);
            //soundInterface.I_UnRegisterSong(mus_playing.handle);
            //Z_ChangeTag(mus_playing.data, PU_CACHE);

            mus_playing.handle = null;
            mus_playing = null;
        }
    }




    private void S_StopChannel(int cnum) {

        int		i;
        Channel	c = channels[cnum];

        if ( c.sfxinfo != null ) {
            // stop the sound playing
            if (soundInterface.I_SoundIsPlaying(c.handle)) {
//    #ifdef SAWDEBUG
//                if (c.sfxinfo == S_sfx[sfx_sawful])
//                    fprintf(stderr, "stopped\n");
//    #endif
                soundInterface.I_StopSound(c.handle);
            }

            // check to see
            //  if other channels are playing the sound
            for (i=0 ; i<numChannels ; i++)
            {
                if (cnum != i
                    && c.sfxinfo == channels[i].sfxinfo) {
                    break;
                }
            }

            // degrade usefulness of sound data
            c.sfxinfo.usefulness--;

            c.sfxinfo = null;
        }
    }

    //
    // Changes volume, stereo-separation, and pitch variables
    //  from the norm of a sound effect to be played.
    // If the sound is not audible, returns a 0.
    // Otherwise, modifies parameters and returns 1.
    //
    private boolean S_AdjustSoundParams(
            MapObject	listener,
            MapObject	source,
            VSP   vsp )
//      int*		vol,
//      int*		sep,
//      int*		pitch )
    {
        int	approx_dist;
        int	adx;
        int	ady;
        long	angle;

        // calculate the distance to sound origin
        //  and clip it if necessary
        adx = Math.abs(listener.x - source.x);
        ady = Math.abs(listener.y - source.y);

        // From _GG1_ p.428. Appox. eucledian distance fast.
        approx_dist = adx + ady - ((adx < ady ? adx : ady)>>1);

        if ( Game.getInstance().gamemap != 8 && approx_dist > S_CLIPPING_DIST ){
            return false;
        }

        // angle of source to listener
        angle = Game.getInstance().renderer.R_PointToAngle2(
                listener.x, listener.y,
                source.x,   source.y    );

        if (angle > listener.angle) {
            angle -= listener.angle;
        } else {
            angle += (0xffffffff - listener.angle);
        }

        angle >>= ANGLETOFINESHIFT;

        // stereo separation
        vsp.sep = 128 - (FixedPoint.mul(S_STEREO_SWING,finesine(angle))>>FRACBITS);

        // volume calculation
        if (approx_dist < S_CLOSE_DIST)
        {
            vsp.vol = snd_SfxVolume;
        }
        else if (Game.getInstance().gamemap == 8) {
            if (approx_dist > S_CLIPPING_DIST) {
                approx_dist = S_CLIPPING_DIST;
            }

            vsp.vol = 15+ ((snd_SfxVolume-15)
                        *((S_CLIPPING_DIST - approx_dist)>>FRACBITS))
                / S_ATTENUATOR;
        } else {
            // distance effect
            vsp.vol = (snd_SfxVolume
                    * ((S_CLIPPING_DIST - approx_dist)>>FRACBITS))
                / S_ATTENUATOR; 
        }

        return vsp.vol>0;
    }



    //
    // S_getChannel :
    //   If none available, return -1.  Otherwise channel #.
    //
    private int S_getChannel(
            MapObject  origin,
            SfxInfo sfxinfo) {
        
        // channel number to use
        int		cnum;

        Channel	c;

        // Find an open channel
        for (cnum=0 ; cnum<numChannels ; cnum++)
        {
            if (channels[cnum].sfxinfo==null) {
                break;
            } else if (origin!=null &&  channels[cnum].origin ==  origin) {
                S_StopChannel(cnum);
                break;
            }
        }

        // None available
        if (cnum == numChannels) {
            // Look for lower priority
            for (cnum=0 ; cnum<numChannels ; cnum++) {
                if (channels[cnum].sfxinfo.priority >= sfxinfo.priority) {
                    break;
                }
            }

            if (cnum == numChannels) {
                // FUCK!  No lower priority.  Sorry, Charlie.    
                return -1;
            } else {
                // Otherwise, kick out lower priority.
                S_StopChannel(cnum);
            }
        }

        c = channels[cnum];

        // channel is decided to be cnum.
        c.sfxinfo = sfxinfo;
        c.origin = origin;

        return cnum;
    }

    public void UpdateSound() {
        soundInterface.I_UpdateSound();
    }

    public static Sequence createMidiSequence( String name, byte data[] ) throws InvalidMidiDataException {

        final ByteBuffer bb = ByteBuffer.wrap(new byte[data.length]); 
        bb.put(data);
        bb.flip();
    
        Sequence sequence = new Sequence(PPQ, 35, 1);   // Ticks per second = 140.  Midi uses beats per quarter note so 140/4=35
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

        bb.position(0);
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

        /* I think this is debug
        try {
            //****  write the MIDI sequence to a MIDI file  ****
            File f = new File("/Users/mark/Desktop/MIDI/" + name.substring(0, 5) + ".mid");
            MidiSystem.write(sequence, 1, f);
        } //try
        catch (Exception e) {
            System.out.println("Exception caught " + e.toString());
        } //catch
        */

        return sequence;
    }

    public static void playSound(SoundEffectLump sl) {
        try {
            AudioFormat af = new AudioFormat(sl.sampleRate, 8, 1, false, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, af);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);

            line.open(af, 4096);
            line.start();
            line.write(sl.data, 0, sl.data.length);

            line.drain();
            line.stop();
            line.close();
        } catch (LineUnavailableException ex) {
            Logger.getLogger(SoundEffectLump.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
