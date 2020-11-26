/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.game.play;

/**
 *
 * @author mark
 */
public class Stats {
    private static Stats instance = null;
    private Stats() {
    }  // use getInstance()
    
    public static Stats getInstance() {
        if (instance == null ) {
            instance = new Stats();
        }
        
        return instance;
    }
    
//    public  final Things things;
//    
//    // Command Line Args
//    public  List<String> args; // Set by DoomMain
//    
//    public boolean isParam(String param) {
//        return args.contains(param);
//    }
//

    /*************************** FLAGS ************************************/
    // 
    // Command line parameters.
    //
    //public  boolean	nomonsters;	// checkparm of -nomonsters
    //public  boolean	respawnparm;	// checkparm of -respawn
    //public  boolean	fastparm;	// checkparm of -fast

    //public  boolean	devparm;	// DEBUG: launched with -devparm



    // -----------------------------------------------------
    // Game Mode - identify IWAD as shareware, retail etc.
    //
    //public GameMode	gameMode;
    //public GameMission	gamemission;

    // Set if homebrew PWAD stuff has been added.
    //public  boolean	modifiedgame;


    // -------------------------------------------
    // Selected skill type, map etc.
    //

    // Defaults for menu, methinks.
    //public  Skill       startskill = Skill.sk_medium;
    //public  int         startepisode = 1;
    //public  int		startmap = 1;

    //public  boolean     autostart;

    // Selected by user. 
    //public  Skill       gameskill;
    //public  int		gameepisode=1;
    //public  int		gamemap=1;

    // Nightmare mode flag, single player.
    //public  boolean     respawnmonsters;

    // Netgame? Only true if >1 player.
    //public  boolean	netgame;

    // Flag: true only if started as net deathmatch.
    // An enum might handle altdeath/cooperative better.
    //public  boolean	deathmatch;  // Defined In Game.java	
	
    // ---------------------------------------------
    // Internal parameters for sound rendering.
    // These have been taken from the DOS version,
    //  but are not (yet) supported with Linux
    //  (e.g. no sound volume adjustment with menu.

    // These are not used, but should be (menu).
    // From m_menu.c:
    //  Sound FX volume has default, 0 - 15
    //  Music volume has default, 0 - 15
    // These are multiplied by 8.
    //public int snd_SfxVolume;      // maximum volume for sound
    //public int snd_MusicVolume;    // maximum volume for music

    // Current music/sfx card - index useless
    //  w/o a reference LUT in a sound module.
    //public int snd_MusicDevice;
    //public int snd_SfxDevice;
    // Config file? Same disclaimer as above.
    //public int snd_DesiredMusicDevice;
    //public int snd_DesiredSfxDevice;


    // -------------------------
    // Status flags for refresh.
    //

    // Depending on view size - no status bar?
    // Note that there is no way to disable the
    //  status bar explicitely.
    //public  boolean statusbaractive;

    //public  boolean automapactive;  // In AutoMap mode?
    //public  boolean menuactive;     // Menu overlayed?
    //public  boolean paused;         // Game Pause?


    //public  boolean viewactive;

    //public  boolean nodrawers=false;
    //public  boolean noblit;

    //public	int viewwindowx;
    //public	int viewwindowy;
    //public	int viewheight;
    //public	int viewwidth;
    //public	int scaledviewwidth;

    // This one is related to the 3-screen display mode.
    // ANG90 = left side, ANG270 = right
    //public  int	viewangleoffset;

    // Player taking events, and displaying.
    //public  int	consoleplayer;	
    //public  int	displayplayer;

    // -------------------------------------
    // Scores, rating.
    // Statistics on a given map, for intermission.
    //
    //public  int         totalkills;
    //public  int         totalitems;
    //public  int         totalsecret;

    // Timer, for scores.
    //public  int         levelstarttic;	// gametic at level start
    //public  int         leveltime;	// tics in game play for par

    // --------------------------------------
    // DEMO playback/recording related stuff.
    // No demo, there is a human player in charge?
    // Disable save/end game?
    //public  boolean	usergame;

    //?
    //public  boolean	demoplayback;
    //public  boolean	demorecording;

    // Quit after playing a demo from cmdline.
    //public  boolean	singledemo;	

    //?
    //public  Defines.GameState     gamestate = GameState.GS_NONE;

    //-----------------------------
    // Internal parameters, fixed.
    // These are set by the engine, and not changed
    //  according to user inputs. Partly load from
    //  WAD, partly set at startup time.

    //public	int	gametic;


    // Bookkeeping on players - state.
    //public	Player	players[] = new Player[MAXPLAYERS];

    // Alive? Disconnected?
    //public  boolean	playeringame[] = new boolean[MAXPLAYERS];


    // Player spawn spots for deathmatch.
    //public final static int MAX_DM_STARTS = 10;
    //public  MapThing    deathmatchstarts[] = new MapThing[MAX_DM_STARTS];
    //public  MapThing	deathmatch_p;

    // Player spawn spots.
    //public  MapThing    playerstarts[] = new MapThing[MAXPLAYERS];

    // Intermission stats.
    // Parameters for world map / intermission.
    //public  WbStart     wminfo;	


    // LUT of ammunition limits for each kind.
    // This doubles with BackPack powerup item.
    //public  int         maxammo[] = new int[Defines.AmmoType.values().length];

    //-----------------------------------------
    // Internal parameters, used for engine.
    //

    // File handling stuff.
    //public  String          basedefault;
    //public  File            debugfile;

    // if true, load all graphics at level load
    //public  boolean         precache;

    // wipegamestate can be set to -1
    //  to force a wipe on the next draw
    //public  GameState       wipegamestate;

    //public  int             mouseSensitivity;
    //?
    // debug flag to cancel adaptiveness
    //public  boolean         singletics = true;	// Debug.  No net

    //public  int             bodyqueslot;

    // Needed to store the number of the dummy sky flat.
    // Used for rendering,
    //  as well as tracking projectiles etc.
    //public int              skyflatnum;

    // Netgame stuff (buffers and pointers, i.e. indices).

    // This is ???
    //public  Communication   doomcom;

    // This points inside doomcom.
    //public  DataPacket      netbuffer;	


    //public  TickCommand     localcmds[] = new TickCommand[BACKUPTICS];
    //public  int             rndindex;

    //public  int             maketic;
    //public  int             nettics[] = new int[MAXNETNODES];

    //public  TickCommand     netcmds[][] = new TickCommand[MAXPLAYERS][BACKUPTICS];
    //public  int             ticdup;
    
    
    
    //
    // EVENT HANDLING
    //
    // Events are asynchronous inputs generally generated by the game user.
    // Events can be discarded if no responder claims them
    //
    //Event   events[] = new Event[MAXEVENTS];
    //int     eventhead;
    //int     eventtail;

   
    // Make these all singletons?
//    public final Video video = new Video();
    //public Wad wad = null;
    //public final MenuMisc mainMenu = new MenuMisc();

    
}
