/*
 * Game - Most modules managed from here.
 */
package thump.game;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import static thump.base.Defines.logger;
import static thump.game.Event.BTS_PAUSE;
import static thump.game.Event.BTS_SAVEGAME;
import static thump.game.Event.BTS_SAVEMASK;
import static thump.game.Event.BTS_SAVESHIFT;
import static thump.game.Event.BT_ATTACK;
import static thump.game.Event.BT_CHANGE;
import static thump.game.Event.BT_SPECIAL;
import static thump.game.Event.BT_SPECIALMASK;
import static thump.game.Event.BT_USE;
import static thump.game.Event.BT_WEAPONSHIFT;
import static thump.game.Event.EventType.ev_joystick;
import static thump.game.Event.EventType.ev_keydown;
import static thump.game.Event.EventType.ev_mouse;
import thump.game.Event.GameAction;
import static thump.game.Event.GameAction.ga_completed;
import static thump.game.Event.GameAction.ga_loadgame;
import static thump.game.Event.GameAction.ga_loadlevel;
import static thump.game.Event.GameAction.ga_nothing;
import static thump.game.Event.GameAction.ga_playdemo;
import static thump.game.Event.GameAction.ga_savegame;
import static thump.game.Event.GameAction.ga_screenshot;
import static thump.game.Event.GameAction.ga_victory;
import static thump.game.Event.GameAction.ga_worlddone;
import static thump.game.Player.PlayerState.PST_DEAD;
import static thump.game.Player.PlayerState.PST_LIVE;
import static thump.game.Player.PlayerState.PST_REBORN;
import thump.game.automap.AutoMap;
import thump.game.intermission.Intermission;
import static thump.game.Defines.AmmoType.am_clip;
import thump.game.Defines.GameMission;
import static thump.game.Defines.GameMission.pack_plut;
import static thump.game.Defines.GameMission.pack_tnt;
import thump.game.Defines.GameMode;
import static thump.game.Defines.GameState.*;
import static thump.game.Defines.KEY_F12;
import static thump.game.Defines.KEY_PAUSE;
import static thump.game.Defines.MAXPLAYERS;
import thump.game.Defines.Skill;
import static thump.game.Defines.Skill.sk_nightmare;
import thump.game.Defines.WeaponType;
import static thump.game.Defines.WeaponType.wp_fist;
import static thump.game.Defines.WeaponType.wp_pistol;
import static thump.base.FixedPoint.FRACBITS;
import static thump.base.FixedPoint.FRACUNIT;
import static thump.game.MobJInfo.Type.*;
import thump.game.play.Random;
import static thump.game.State.StateNum.S_SARG_PAIN2;
import static thump.game.State.StateNum.S_SARG_RUN1;
import static thump.base.Tables.ANG45;
import static thump.base.Tables.ANGLETOFINESHIFT;
import static thump.base.Tables.finecosine;
import static thump.base.Tables.finesine;
import static thump.game.ThingStateLUT.mobjinfo;
import static thump.game.ThingStateLUT.states;
import thump.game.maplevel.MapObject;
import thump.game.play.MObject;
import thump.game.headup.Stuff;
import thump.game.maplevel.MapNode;
import thump.game.maplevel.MapSubSector;
import thump.game.network.Net;
import static thump.game.network.Net.BACKUPTICS;
import static thump.game.play.Interaction.maxammo;
import static thump.game.play.Local.MAXHEALTH;
import thump.game.play.Map;
import thump.game.play.Sight;
import thump.game.play.Stats;
import thump.game.play.Tick;
import thump.game.sound.Sound;
import static thump.game.sound.sfx.Sounds.SfxEnum.sfx_telept;
import thump.render.Renderer;
import static thump.render.Renderer.SKYFLATNAME;
import thump.game.menu.MenuManager;
import thump.game.menu.MenuMisc;
import thump.game.play.PlayerView;
import thump.game.status.Status;
import thump.system.SystemInterface;
import thump.system.VideoInterface;
import thump.wad.Wad;
import static thump.wad.map.Degenmobj.MobileObjectFlag.MF_SHADOW;
import thump.wad.map.Thinker;
import thump.wad.mapraw.MapThing;

/**
 *
 * @author mark
 */
public class Game {
    private static Game instance = null;
    PlayerView playerView;
    
    // use getInstance()
    private Game() {
        players[0] = new Player();
        //things = new Things();
        renderer.setThings(things.rThings);
    }
    
    public static Game getInstance() {
        if (instance == null ) {
            instance = new Game();
        }
        
        return instance;
    }
    
    // Command Line Args
    public  List<String> args; // Set by DoomMain
    
    public boolean isParam(String param) {
        return args.contains(param);
    }

    // -----------------------------------------------------
    // Game Mode - identify IWAD as shareware, retail etc.
    //
    public GameMode	gameMode = GameMode.INDETERMINED;
    public GameMission	gamemission = GameMission.doom;

    // Set if homebrew PWAD stuff has been added.
    public  boolean	modifiedgame = false;

    public DoomMain     doomMain;
    public  int         leveltime;


    // Both the head and tail of the thinker list.
    public Thinker	thinkercap;

    /*********************  Language *******************************/
    public  static String          country  = "US";
    public  static String          language = "en";
    public  static Locale          currentLocale = new Locale(language, country);
    public  static ResourceBundle  messages = ResourceBundle.getBundle("MessagesBundle", currentLocale);

    /*********************  Game Vars *******************************/
    public Event.GameAction     gameaction; 
    public Defines.GameState    gamestate=GS_DEMOSCREEN; 
    public Defines.Skill        gameskill;
    
    public boolean          respawnmonsters;
    public int              gameepisode = 1; 
    public int              gamemap = 1; 

    public boolean          paused; 
    public boolean          sendpause;          // send a pause event next tic 
    public boolean          sendsave;           // send a save event next tic 
    public boolean          usergame;           // ok to save / end game 

    public boolean          timingdemo;         // if true, exit with report on completion 
    public boolean          nodrawers;          // for comparative timing purposes 
    public boolean          noblit;             // for comparative timing purposes 
    public int              starttime;          // for comparative timing purposes  	 
    
    public boolean          viewactive; 

    public int              deathmatch;         // only if started as net death 
    public boolean          netgame;            // only true if packets are broadcast 
    public boolean          playeringame[] = new boolean[Defines.MAXPLAYERS]; 
    public Player           players[] = new Player[Defines.MAXPLAYERS]; 

    public int              consoleplayer=0;      // player taking events and displaying 
    public int              displayplayer;      // view being displayed 
    public int              gametic=1; 
    public int              levelstarttic;      // gametic at level start
    
    public int              totalkills, totalitems, totalsecret;    // for intermission 

    public char             demoname[] = new char[32]; 
    public boolean          demorecording; 
    public boolean          demoplayback; 
    public boolean          netdemo; 
    public byte             demobuffer[];
    public byte             demo_p[];
    public byte             demoend[]; 
    public boolean          singledemo;         // quit after playing a demo from cmdline 

    public boolean          precache = true;    // if true, load all graphics at start 

    public WbStart          wminfo = new WbStart();          	// parms for world map / intermission 

    public int consistancy[][] = new int[Defines.MAXPLAYERS][BACKUPTICS]; 

    public byte savebuffer[];


    public  boolean	nomonsters;	// checkparm of -nomonsters
    public  boolean	respawnparm;	// checkparm of -respawn
    public  boolean	fastparm;	// checkparm of -fast

    //public  boolean	devparm;	// DEBUG: launched with -devparm
    // 
    // controls (have defaults) 
    // 
    public int          key_right;
    public int		key_left;

    public int		key_up;
    public int		key_down; 
    public int          key_strafeleft;
    public int		key_straferight; 
    public int          key_fire;
    public int		key_use;
    public int		key_strafe;
    public int		key_speed; 

    public int          mousebfire; 
    public int          mousebstrafe; 
    public int          mousebforward; 

    public int          joybfire; 
    public int          joybstrafe; 
    public int          joybuse; 
    public int          joybspeed; 

    public  int         mouseSensitivity;



    public final int TURBOTHRESHOLD = 0x32;

    public int	forwardmove[] = {0x19, 0x32}; 
    public int	sidemove[] = {0x18, 0x28}; 
    public int	angleturn[] = {640, 1280, 320};	// + slow turn 

    public final int MAXPLMOVE = forwardmove[1];
    
    public final int SLOWTURNTICS = 6; 

    public final int NUMKEYS = 256; 

    public boolean gamekeydown[] = new boolean[NUMKEYS]; 
    public int     turnheld;				// for accelerative turning 

    public boolean mousearray[] = new boolean[4]; 
    //boolean	   mousebuttons = mousearray[1];		// allow [-1]

    // mouse values are used once 
    public int          mousex;
    public int		mousey;         

    public int          dclicktime;
    public boolean	dclickstate;
    public int		dclicks; 
    public int          dclicktime2;
    public int		dclickstate2;
    public int		dclicks2;

    // joystick values are repeated 
    public int          joyxmove;
    public int		joyymove;
    public boolean      joyarray[] = new boolean[5]; 
    //public boolean	joybuttons = joyarray[1];		// allow [-1] 

    public int		savegameslot; 
    public String	savedescription=null; 


    public final int    BODYQUESIZE = 32;

    public final MapObject	bodyque[] = new MapObject[BODYQUESIZE]; 
    public int		bodyqueslot; 

    // TODO change Object to StatisticsDriver when that is ready.
    public WbStart	statcopy;				// for statistics driver
 
    public final Stuff          headUp = new Stuff(this);
    public final PlayerSetup    playerSetup = new PlayerSetup();
    public final Renderer       renderer = new Renderer();
    public final Things         things = new Things(this,renderer);
    
    
    //public final MenuMisc       misc = new MenuMisc();
    public final VideoInterface videoInterface = VideoInterface.getInstance();
    //public Communication  doomcom;
    public final MObject        movingObject = new MObject();
    public final Map            map = new Map(this);
    public final Net            net = new Net();
    public final Sound          sound = new Sound();
    public Wad                  wad = null;
    public final MenuMisc       mainMenu = new MenuMisc();
    public final AutoMap        autoMap = new AutoMap(this);
    public final Status         statusBar = new Status(this);
    public final Intermission   intermission = new Intermission(this);
    public final Finale         finale = new Finale(this);
    public final Enemy          enemy = new Enemy(this);
    public final Sight          sight = new Sight(this);
    //public final Things         things = new Things(this);  // moved to Renderer
    
    

// Moved to TickCommand Object.  But not even used anywhere!
//    int G_CmdChecksum (TickCommand cmd) 
//    { 
//        int		i;
//        int		sum = 0; 
//
//        for (i=0 ; i< sizeof(*cmd)/4 - 1 ; i++) 
//            sum += ((int *)cmd)[i]; 
//
//        return sum; 
//    } 

    public void G_BuildTiccmd(TickCommand[] localcmds, int i) {
        if ( localcmds[i] == null ) {
            localcmds[i] = SystemInterface.getInstance().I_BaseTiccmd (); 
        }
        
        G_BuildTiccmd(localcmds[i]);
    }

    private int getKey(String key) {
        Object p = mainMenu.properties.get(key);
        try {
            return Integer.valueOf((String)p);
        } catch ( ClassCastException ex ) {
            return (Integer)p;
        }
    }
    
    //
    // G_BuildTiccmd
    // Builds a ticcmd from all of the available inputs
    // or reads it from the demo buffer. 
    // If recording a demo, write it out 
    // 
    private void G_BuildTiccmd (TickCommand cmd) { 
        int		i; 
        boolean	strafe;
        boolean	bstrafe; 
        int		speed;
        int		tspeed; 
        int		forward;
        int		side;

        logger.finest("Build Tick Command.");
        //TickCommand	base;
        //base = SystemInterface.getInstance().I_BaseTiccmd ();		// empty, or external driver
        
        cmd.reset(); //memcpy (cmd,base,sizeof(*cmd));

        cmd.consistancy = (short) consistancy[consoleplayer][net.maketic%BACKUPTICS]; 

//        strafe = gamekeydown[key_strafe] || mousebuttons[mousebstrafe] 
//            || joybuttons[joybstrafe];
        strafe = gamekeydown[getKey("key_strafe")] || mousearray[1+mousebstrafe] 
            || joyarray[1+joybstrafe]; 
        speed = gamekeydown[key_speed] || joyarray[1+joybspeed]?1:0;

        forward = 0;
        side = 0;

        // use two stage accelerative turning
        // on the keyboard and joystick
        if (joyxmove < 0
            || joyxmove > 0  
            || gamekeydown[key_right]
            || gamekeydown[key_left]) {
            turnheld += net.ticdup;
        } else {
            turnheld = 0;
        } 

        if (turnheld < SLOWTURNTICS) { 
            tspeed = 2;             // slow turn 
        } else {
            tspeed = speed;
        }

        
        // let movement keys cancel each other out
        if (strafe) { 
            if (gamekeydown[getKey("key_right")]) {
                // fprintf(stderr, "strafe right\n");
                logger.config("Strafe Right");
                side += sidemove[speed]; 
            }
            if (gamekeydown[getKey("key_left")]) {
                //	fprintf(stderr, "strafe left\n");
                logger.config("Strafe Left");
                side -= sidemove[speed]; 
            }
            if (joyxmove > 0) {
                side += sidemove[speed];
            } 
            if (joyxmove < 0) {
                side -= sidemove[speed];
            } 

        } else { 
            if (gamekeydown[getKey("key_right")]) {
                logger.config("Turn Right");
                cmd.angleturn -= angleturn[tspeed];
            } 
            if (gamekeydown[getKey("key_left")]) {
                logger.config("Turn Left");
                cmd.angleturn += angleturn[tspeed];
            } 
            if (joyxmove > 0) {
                cmd.angleturn -= angleturn[tspeed];
            } 
            if (joyxmove < 0) {
                cmd.angleturn += angleturn[tspeed];
            } 
        } 

        if (gamekeydown[getKey("key_up")]) {
            // fprintf(stderr, "up\n");
            logger.config("Move Forward");
            forward += forwardmove[speed]; 
        }
        if (gamekeydown[getKey("key_down")]) {
            // fprintf(stderr, "down\n");
            logger.config("Move Backward");
            forward -= forwardmove[speed]; 
        }
        if (joyymove < 0) {
            forward += forwardmove[speed];
        } 
        if (joyymove > 0) {
            forward -= forwardmove[speed];
        } 
        if (gamekeydown[getKey("key_straferight")]) {
            logger.config("Strafe Right");
            side += sidemove[speed];
        } 
        if (gamekeydown[getKey("key_strafeleft")]) {
            logger.config("Strafe Left");
            side -= sidemove[speed];
        }

        // buttons
        cmd.chatchar = (byte) Game.getInstance().headUp.HU_dequeueChatChar(); 

        if (gamekeydown[getKey("key_fire")] || mousearray[1+mousebfire] 
            || joyarray[1+joybfire]) {
            logger.config("Attack");
            cmd.buttons |= BT_ATTACK;
        } 

        if (gamekeydown[getKey("key_use")] || joyarray[1+joybuse] ) { 
            logger.config("Use");
            cmd.buttons |= BT_USE;
            // clear double clicks if hit use button 
            dclicks = 0;                   
        } 

        // chainsaw overrides 
        for (i=0 ; i<WeaponType.values().length-2 ; i++) {
            if (gamekeydown['1'+i]) {
                logger.log(Level.CONFIG, "Weapon Change {0}", gamekeydown['1'+i]);
                cmd.buttons |= BT_CHANGE;
                cmd.buttons |= i<<BT_WEAPONSHIFT;
                break;
            }
        }

        // mouse
        if (mousearray[1+mousebforward]) {
            forward += forwardmove[speed];
        }

        // forward double click
        if (mousearray[1+mousebforward] != dclickstate && dclicktime>1 ) { 
            dclickstate = mousearray[1+mousebforward]; 
            if (dclickstate) {
                dclicks++;
            } 
            if (dclicks == 2) { 
                cmd.buttons |= BT_USE; 
                dclicks = 0; 
            } else {
                dclicktime = 0;
            } 
        } else { 
            dclicktime += net.ticdup; 
            if (dclicktime > 20) 
            { 
                dclicks = 0; 
                dclickstate = false; 
            } 
        }

        // strafe double click
        bstrafe = mousearray[1+mousebstrafe] || joyarray[1+joybstrafe]; 
        if (bstrafe != dclickstate2>0 && dclicktime2 > 1 ) { 
            dclickstate2 = bstrafe?1:0; 
            if (dclickstate2>0) {
                dclicks2++;
            } 
            if (dclicks2 == 2) { 
                cmd.buttons |= BT_USE; 
                dclicks2 = 0; 
            }else {
                dclicktime2 = 0;
            } 
        } else { 
            dclicktime2 += net.ticdup; 
            if (dclicktime2 > 20) 
            { 
                dclicks2 = 0; 
                dclickstate2 = 0; 
            } 
        } 

        forward += mousey; 
        if (strafe) {
            side += mousex*2;
        } else {
            cmd.angleturn -= mousex*0x8;
        } 

        mousex = 0;
        mousey = 0; 

        if (forward > MAXPLMOVE) {
            forward = MAXPLMOVE;
        } else if (forward < -MAXPLMOVE) {
            forward = -MAXPLMOVE;
        } 
        if (side > MAXPLMOVE) {
            side = MAXPLMOVE;
        } else if (side < -MAXPLMOVE) {
            side = -MAXPLMOVE;
        } 

        cmd.forwardmove += forward; 
        cmd.sidemove += side;

        // special buttons
        if (sendpause)  { 
            sendpause = false; 
            cmd.buttons = (byte) (BT_SPECIAL | BTS_PAUSE); 
        } 

        if (sendsave) { 
            sendsave = false; 
            cmd.buttons = (byte) (BT_SPECIAL | BTS_SAVEGAME | (savegameslot<<BTS_SAVESHIFT)); 
        } 
    } 

    //
    // G_DoLoadLevel 
    //
    //extern  gamestate_t     wipegamestate; 

    void G_DoLoadLevel () { 
        int             i; 

        // Set the sky map.
        // First thing, we have a dummy sky texture name,
        //  a flat. The data is in the WAD only because
        //  we look for an actual index, instead of simply
        //  setting one.
        //skyflatnum = R_FlatNumForName ( SKYFLATNAME );
        renderer.skytexture = wad.getMapTextureByName(SKYFLATNAME);

        // DOOM determines the sky texture to be used
        // depending on the current episode, and the game version.
        if ( (gameMode == GameMode.COMMERCIAL)
             || ( gamemission == pack_tnt )
             || ( gamemission == pack_plut ) )
        {
            renderer.skytexture = wad.getMapTextureByName("SKY3");
            if (gamemap < 12) {
                renderer.skytexture = wad.getMapTextureByName("SKY1");
            } else if (gamemap < 21) {
                renderer.skytexture = wad.getMapTextureByName("SKY2");
            }
        }

        levelstarttic = gametic;        // for time calculation

        if (doomMain.wipegamestate == GS_LEVEL) { 
            doomMain.wipegamestate = null;             // force a wipe 
        }

        gamestate = GS_LEVEL; 

        for (i=0 ; i<MAXPLAYERS ; i++)  { 
            if (playeringame[i] && players[i].playerstate == PST_DEAD) {
                players[i].playerstate = PST_REBORN;
            } 
            //memset (players[i].frags,0,sizeof(players[i].frags));
            if ( players[i] != null ) {
                Arrays.fill(players[i].frags, 0);
            }
        } 

        playerSetup.P_SetupLevel (gameepisode, gamemap, 0, gameskill);    
        displayplayer = consoleplayer;		// view the guy you are playing    
        starttime = (int) SystemInterface.getInstance().I_GetTime (); 
        gameaction = ga_nothing; 
        //Z_CheckHeap ();

        // clear cmd building stuff
        //memset (gamekeydown, 0, sizeof(gamekeydown));
        Arrays.fill(gamekeydown, false);
        joyxmove = 0;
        joyymove = 0; 
        mousex = 0;
        mousey = 0; 
        sendpause = false;
        sendsave = false;
        paused = false; 
        //memset (mousebuttons, 0, sizeof(mousebuttons));
        Arrays.fill(mousearray, false);
        //memset (joybuttons, 0, sizeof(joybuttons));
        Arrays.fill(joyarray, false);
    } 

    //
    // G_Responder  
    // Get info needed to make ticcmd_ts for the players.
    // 
    boolean G_Responder (Event ev) { 
        // allow spy mode changes even during the demo
        if (gamestate == GS_LEVEL 
                && ev.type == ev_keydown 
                && ev.data1 == KEY_F12 
                && (singledemo || deathmatch==0) )
        {
            // spy mode 
            do { 
                displayplayer++; 
                if (displayplayer == MAXPLAYERS) {
                    displayplayer = 0;
                } 
            } while (!playeringame[displayplayer] && displayplayer != consoleplayer); 
            return true; 
        }

        // any other key pops up menu if in demos
        if (gameaction == ga_nothing && !singledemo && 
            (demoplayback || gamestate == GS_DEMOSCREEN) 
            ) 
        { 
            if (ev.type == ev_keydown ||  
                (ev.type == ev_mouse && ev.data1>0) || 
                (ev.type == ev_joystick && ev.data1>0) ) 
            { 
                MenuManager.getInstance().M_StartControlPanel (); 
                return true; 
            } 
            return false; 
        } 

        if (gamestate == GS_LEVEL) { 
//    #if 0 
//            if (devparm && ev.type == ev_keydown && ev.data1 == ';') 
//            { 
//                G_DeathMatchSpawnPlayer (0); 
//                return true; 
//            } 
//    #endif 
            if (headUp.HU_Responder (ev)) { 
                return true;	// chat ate the event 
            }
            if (statusBar.ST_Responder (ev)) { 
                return true;	// status window ate it 
            }
            if (autoMap.AM_Responder (ev)) { 
                return true;	// automap ate it 
            }
        } 

        if (gamestate == GS_FINALE && finale.F_Responder (ev)) {   // Finale.c
            return true;	// finale ate the event 
        } 

        logger.log(Level.CONFIG, "Game Event: {0} {1}", new Object[]{ev.type, ev.data1} );
        switch (ev.type) { 
          case ev_keydown: 
            if (ev.data1 == KEY_PAUSE) { 
                sendpause = true; 
                return true; 
            } 
            if (ev.data1 <NUMKEYS) {
                gamekeydown[ev.data1] = true;
            } 
            return true;    // eat key down events 

          case ev_keyup: 
            if (ev.data1 <NUMKEYS) {
                gamekeydown[ev.data1] = false;
            } 
            return false;   // always let key up events filter down 

          case ev_mouse: 
            mousearray[1] = (ev.data1 & 1)>0; 
            mousearray[2] = (ev.data1 & 2)>0; 
            mousearray[3] = (ev.data1 & 4)>0; 
            mousex = ev.data2*(mouseSensitivity+5)/10; 
            mousey = ev.data3*(mouseSensitivity+5)/10; 
            return true;    // eat events

          case ev_joystick: 
            joyarray[1+0] = (ev.data1 & 1)>0; 
            joyarray[2] = (ev.data1 & 2)>0; 
            joyarray[3] = (ev.data1 & 4)>0; 
            joyarray[4] = (ev.data1 & 8)>0; 
            joyxmove = ev.data2; 
            joyymove = ev.data3; 
            return true;    // eat events 

          default: 
            break; 
        } 
        logger.log(Level.CONFIG, "Nobody ate the event!{0}", ev.type);
        return false; 
    } 


    /**
     * Game Ticker -- G_Ticker
     */
    public void G_Ticker () { 
        int		i;
        int		buf; 
        TickCommand	cmd;

        //Player[] players = Stats.getInstance().players;
        logger.finer("Game.G_Ticker()");

        // do player reborns if needed
        for (i=0 ; i<MAXPLAYERS ; i++) {
            if (playeringame[i] && players[i].playerstate == PST_REBORN) {
                G_DoReborn (i);
            }
        }
        //Defines.logger.config("G_Ticker 1\n");

        // do things to change the game state
        while (gameaction != GameAction.ga_nothing) { 
            switch (gameaction) { 
              case ga_loadlevel: 
        logger.config("G_Ticker ga_loadlevel");
                G_DoLoadLevel (); 
                break; 
              case ga_newgame: 
        logger.config("G_Ticker ga_newgame");
                G_DoNewGame (); 
                break; 
              case ga_loadgame: 
        logger.config("G_Ticker ga_loadgame");
//                G_DoLoadGame (); 
                break; 
              case ga_savegame: 
        logger.config("G_Ticker ga_savegame");
//                G_DoSaveGame (); 
                break; 
              case ga_playdemo: 
        logger.config("G_Ticker ga_playdemo");
                G_DoPlayDemo (); 
                break; 
              case ga_completed: 
        logger.config("G_Ticker ga_completed");
                G_DoCompleted (); 
                break; 
              case ga_victory: 
        logger.config("G_Ticker ga_victory");
//                F_StartFinale (); 
                break; 
              case ga_worlddone: 
        logger.config("G_Ticker ga_worlddone");
                G_DoWorldDone (); 
                break; 
              case ga_screenshot: 
        logger.config("G_Ticker ge screenshot");
                MenuMisc.screenShot(); 
                gameaction = GameAction.ga_nothing; 
                break; 
              case ga_nothing: 
                break; 
            } 
        }
        //Defines.logger.config("G_Ticker 2\n");

        // get commands, check consistancy,
        // and build new consistancy check
        buf = (gametic/gametic%net.doomcom.ticdup)%BACKUPTICS; 

        for (i=0 ; i<MAXPLAYERS ; i++) {
            if (playeringame[i]) { 
                if ( net.netcmds[i][buf] == null ) {
                    players[i].cmd = new TickCommand();
                } else {
                    players[i].cmd = net.netcmds[i][buf];  // should be copied?
                }
                cmd = players[i].cmd; 

                //memcpy (cmd, netcmds[i][buf], sizeof(ticcmd_t)); 

                if (demoplayback) {
//                    G_ReadDemoTiccmd (cmd);
                } 
                if (demorecording) {
//                    G_WriteDemoTiccmd (cmd);
                }

                // check for turbo cheats
                if (cmd.forwardmove > TURBOTHRESHOLD 
                    && (gametic&31)==0 && ((gametic>>5)&3) == i )
                {
                    //static char turbomessage[80];
                    //extern char *player_names[4];
                    //sprintf (turbomessage, "%s is turbo!",player_names[i]);
                    //players[consoleplayer].message = turbomessage;
                    players[consoleplayer].message = Game.getInstance().headUp.player_names[i] + " is turbo!";
                }

                if (netgame && !netdemo && (gametic%net.doomcom.ticdup)==0 ) 
                { 
                    if (gametic > BACKUPTICS 
                        && consistancy[i][buf] != cmd.consistancy) 
                    { 
                        //SystemInterface.I_Error ("consistency failure ({0} should be {1})",
                        //         new Object[]{cmd.consistancy, consistancy[i][buf]}); 
                        logger.log(
                                Level.SEVERE,
                                "consistency failure ({0} should be {1})",
                                 new Object[]{cmd.consistancy, consistancy[i][buf]});
                    } 
                    if (players[i].mo != null) {
                        consistancy[i][buf] = players[i].mo.x;
                    } else {
                        consistancy[i][buf] = Random.getInstance().rndindex;
                    } 
                } 
            }
        }
        //Defines.logger.config("G_Ticker 3\n");

        // check for special buttons
        for (i=0 ; i<MAXPLAYERS ; i++) {
            if (playeringame[i]) 
            { 
                if ((players[i].cmd.buttons & BT_SPECIAL)>0) { 
                    switch (players[i].cmd.buttons & BT_SPECIALMASK) { 
                      case BTS_PAUSE: 
                        paused = !paused; 
                        if (paused) {
//                            S_PauseSound ();
                        } else {
//                            S_ResumeSound ();
                        } 
                        break; 

                      case BTS_SAVEGAME: 
                        if (savedescription==null) {
                            savedescription = "NET GAME";
                    }
                            //strcpy (savedescription, "NET GAME"); 
                        savegameslot =  
                            (players[i].cmd.buttons & BTS_SAVEMASK)>>BTS_SAVESHIFT; 
                        gameaction = ga_savegame; 
                        break; 
                    } 
                } 
            }
        }
        //Defines.logger.config("G_Ticker 4\n");

        // do main actions
        switch (gamestate) { 
          case GS_LEVEL: 
            Tick.P_Ticker (); 
            statusBar.ST_Ticker (); 
            autoMap.AM_Ticker (); 
            headUp.HU_Ticker ();            
            break; 

          case GS_INTERMISSION: 
//TODO            intermission.WI_Ticker (); 
            break; 

          case GS_FINALE: 
//TODO            F_Ticker (); 
            break; 

          case GS_DEMOSCREEN: 
            doomMain.D_PageTicker (); 
            break; 
        }        
    } 

    //
    // PLAYER STRUCTURE FUNCTIONS
    // also see P_SpawnPlayer in P_Things
    //

    //
    // G_InitPlayer 
    // Called at the start.
    // Called by the game initialization functions.
    //
    void G_InitPlayer(int player) {
        Player p;

        // set up the saved info         
        p = players[player];

        // clear everything else to defaults 
        G_PlayerReborn(player);
    }

    //
    // G_PlayerFinishLevel
    // Can when a player completes a level.
    //
    void G_PlayerFinishLevel(int player) {
        Player p;

        p = players[player];

        Arrays.fill(p.powers, 0);
        Arrays.fill(p.cards, false);
        p.mo.flags &= ~MF_SHADOW.getValue();	// cancel invisibility 
        p.extralight = 0;			// cancel gun flashes 
        p.fixedcolormap = 0;                    // cancel ir gogles 
        p.damagecount = 0;			// no palette changes 
        p.bonuscount = 0;
    }

    // TODO Move this to Player
    //
    // G_PlayerReborn
    // Called after a player dies 
    // almost everything is cleared and initialized 
    //
    public void G_PlayerReborn(int player) {
        Player p;
        int i;
        int frags[] = new int[MAXPLAYERS];
        int killcount;
        int itemcount;
        int secretcount;

        //Player[] players = Stats.getInstance().players;
        p = players[player];
        System.arraycopy(p.frags, 0, frags, 0, frags.length);
        //memcpy (frags,players[player].frags,frags.length); 

        killcount = p.killcount;
        itemcount = p.itemcount;
        secretcount = p.secretcount;

        p.clearSettings();
        //memset (p, 0, sizeof(*p)); 

        System.arraycopy(frags, 0, p.frags, 0, p.frags.length);
        //memcpy (players[player].frags, frags, sizeof(players[player].frags));

        p.killcount = killcount;
        p.itemcount = itemcount;
        p.secretcount = secretcount;

        p.usedown = true;
        p.attackdown = true;	// don't do anything immediately 
        p.playerstate = PST_LIVE;
        p.health = MAXHEALTH;
        p.readyweapon = wp_pistol;
        p.pendingweapon = wp_pistol;
        p.weaponowned[wp_fist.ordinal()] = true;
        p.weaponowned[wp_pistol.ordinal()] = true;
        p.ammo[am_clip.ordinal()] = 50;

        for (i = 0; i < maxammo.length; i++) {
            p.maxammo[i] = maxammo[i];
        }

    }

    //
    // G_CheckSpot  
    // Returns false if the player cannot be respawned
    // at the given mapthing_t spot  
    // because something is occupying it 
    //
    //void P_SpawnPlayer (MapThing mthing); 
    boolean G_CheckSpot( int playernum, MapThing mthing ) { 
        int		x;
        int		y; 
        MapSubSector	ss; 
        long		an; 
        MapObject	mo; 
        int		i;

        if (players[playernum].mo!=null) {
            // first spawn of level, before corpses
            for (i=0 ; i<playernum ; i++) {
                if (players[i].mo.x == mthing.x << FRACBITS
                        && players[i].mo.y == mthing.y << FRACBITS) {
                    return false;
                }
            }	
            return true;
        }

        x = mthing.x << FRACBITS; 
        y = mthing.y << FRACBITS; 

        if (!map.P_CheckPosition (players[playernum].mo, x, y) ) {
            return false;
        } 

        // flush an old corpse if needed 
        if (bodyqueslot >= BODYQUESIZE) {
            Game.getInstance().movingObject.P_RemoveMobj (bodyque[bodyqueslot%BODYQUESIZE]);
        } 
        bodyque[bodyqueslot%BODYQUESIZE] = players[playernum].mo; 
        bodyqueslot++; 

        // spawn a teleport fog 
        ss = MapNode.R_PointInSubsector(x,y); 
        an = ( ANG45 * (mthing.angle/45) ) >> ANGLETOFINESHIFT; 

        mo = MObject.P_SpawnMobj (x+20*finecosine(an), y+20*finesine(an) 
                          , ss.mapSector.sector.floorheight 
                          , MT_TFOG); 

        if (players[consoleplayer].viewz != 1) { 
            sound.S_StartSound (mo, sfx_telept);	// don't start sound on first frame 
        }

        return true; 
    } 

    //
    // G_DeathMatchSpawnPlayer 
    // Spawns a player at one of the random death match spots 
    // called at level load and each death 
    //
    void G_DeathMatchSpawnPlayer (int playernum) 
    { 
        int             i,j; 
        //int				selections; 

        Stats stats = Stats.getInstance();
        
        //selections = Stats.getInstance().deathmatch_p - Stats.getInstance().deathmatchstarts;
        int selections = Arrays.asList(playerSetup.deathmatchstarts).indexOf(playerSetup.deathmatch_p);
        
        if (selections < 4) {
            //SystemInterface.I_Error ("Only {0} deathmatch spots, 4 required", new Object[]{selections});
            thump.base.Defines.logger.log(Level.SEVERE, "Only {0} deathmatch spots, 4 required", new Object[]{selections});
        } 

        for (j=0 ; j<20 ; j++) 
        { 
            i = Random.getInstance().P_Random() % selections; 
            if (G_CheckSpot (playernum, playerSetup.deathmatchstarts[i]) ) 
            { 
                playerSetup.deathmatchstarts[i].type = (short) (playernum+1); 
                MObject.P_SpawnPlayer (playerSetup.deathmatchstarts[i]); 
                return; 
            } 
        } 

        // no good spot, so the player will probably get stuck 
        MObject.P_SpawnPlayer (playerSetup.playerstarts[playernum]); 
    } 

    //
    // G_DoReborn 
    // 
    void G_DoReborn(int playernum) {
        int i;

        if (!netgame) {
            // reload the level from scratch
            gameaction = ga_loadlevel;
        } else {
            // respawn at the start

            // first dissasociate the corpse 
            players[playernum].mo.player = null;

            // spawn at random spot if in death match 
            if (deathmatch > 0) {
                G_DeathMatchSpawnPlayer(playernum);
                return;
            }

            if (G_CheckSpot(playernum, playerSetup.playerstarts[playernum])) {
                MObject.P_SpawnPlayer(playerSetup.playerstarts[playernum]);
                return;
            }

            // try to spawn at one of the other players spots 
            for (i = 0; i < MAXPLAYERS; i++) {
                if (G_CheckSpot(playernum, playerSetup.playerstarts[i])) {
                    playerSetup.playerstarts[i].type = (short) (playernum + 1);	// fake as other player 
                    MObject.P_SpawnPlayer(playerSetup.playerstarts[i]);
                    playerSetup.playerstarts[i].type = (short) (i + 1);		// restore 
                    return;
                }
                // he's going to be inside something.  Too bad.
            }
            MObject.P_SpawnPlayer(playerSetup.playerstarts[playernum]);
        }
    }

    void G_ScreenShot() {
        gameaction = ga_screenshot;
    }


    // DOOM Par Times
    public static final int pars[][] = 
    { 
        {0}, 
        {0,30,75,120,90,165,180,180,30,165}, 
        {0,90,90,90,120,90,360,240,30,170}, 
        {0,90,45,90,150,90,90,165,30,135} 
    }; 

    // DOOM II Par Times
    public static final int cpars[] =
    {
        30,90,120,120,90,150,120,120,270,90,        //  1-10
        210,150,150,150,210,150,420,150,210,150,    // 11-20
        240,150,180,150,150,300,330,420,300,180,    // 21-30
        120,30                                      // 31-32
    };


    //
    // G_DoCompleted 
    //
    public boolean		secretexit; 
    //extern char*	pagename; 

    public void G_ExitLevel (){ 
        secretexit = false; 
        gameaction = ga_completed; 
    } 

    // Here's for the german edition.
    public void G_SecretExitLevel() { 
        // IF NO WOLF3D LEVELS, NO SECRET EXIT!
        if ( (gameMode == Defines.GameMode.COMMERCIAL)
                && (wad.findByName("MAP31")==null)) {
            secretexit = false;
        } else {
            secretexit = true;
        } 
        gameaction = ga_completed; 
    } 


    public void G_DoCompleted() { 
        
        gameaction = ga_nothing; 

        for (int i=0 ; i<MAXPLAYERS ; i++) {
            if (playeringame[i]) { 
                G_PlayerFinishLevel (i);        // take away cards and stuff 
            }
        }

        if (autoMap.automapactive) {
            autoMap.AM_Stop ();
        } 

        if ( gameMode != Defines.GameMode.COMMERCIAL) {
            switch(gamemap) {
                case 8:
                    gameaction = ga_victory;
                    return;
                case 9:
                    for (int i=0 ; i<MAXPLAYERS ; i++) {
                        players[i].didsecret = true;
                    }
                    break;
            }
        }


        wminfo.didsecret = players[consoleplayer].didsecret; 
        wminfo.epsd = gameepisode -1; 
        wminfo.last = gamemap -1;

        // wminfo.next is 0 biased, unlike gamemap
        if ( gameMode == Defines.GameMode.COMMERCIAL)
        {
            if (secretexit) {
                switch(gamemap) {
                    case 15: wminfo.next = 30; break;
                    case 31: wminfo.next = 31; break;
                }
            } else {
                switch(gamemap) {
                    case 31:
                    case 32: wminfo.next = 15; break;
                    default: wminfo.next = gamemap;
                }
            }
        } else {
            if (secretexit) { 
                wminfo.next = 8; 	// go to secret level 
            } else if (gamemap == 9) {
                // returning from secret level 
                switch (gameepisode) 
                { 
                  case 1: 
                    wminfo.next = 3; 
                    break; 
                  case 2: 
                    wminfo.next = 5; 
                    break; 
                  case 3: 
                    wminfo.next = 6; 
                    break; 
                  case 4:
                    wminfo.next = 2;
                    break;
                }                
            } else { 
                wminfo.next = gamemap;          // go to next level 
            }
        }

        wminfo.maxkills = totalkills; 
        wminfo.maxitems = totalitems; 
        wminfo.maxsecret = totalsecret; 
        wminfo.maxfrags = 0; 
        if ( gameMode == Defines.GameMode.COMMERCIAL ) {
            wminfo.partime = 35*cpars[gamemap-1];
        } else {
            wminfo.partime = 35*pars[gameepisode][gamemap];
        } 
        wminfo.pnum = consoleplayer; 

        for (int i=0 ; i<MAXPLAYERS ; i++) { 
            wminfo.plyr[i].in = playeringame[i]; 
            wminfo.plyr[i].skills = players[i].killcount; 
            wminfo.plyr[i].sitems = players[i].itemcount; 
            wminfo.plyr[i].ssecret = players[i].secretcount; 
            wminfo.plyr[i].stime = Game.getInstance().leveltime;
            
            wminfo.plyr[i].frags = Arrays.copyOf(players[i].frags, players[i].frags.length);
//            for ( int j=0; i<wminfo.plyr[j].frags.length; j++) {
//                wminfo.plyr[i].frags[j] = players[i].frags[j];
//            }
                //            memcpy (wminfo.plyr[i].frags, players[i].frags 
                //                    , sizeof(wminfo.plyr[i].frags)); 
        } 

        gamestate = GS_INTERMISSION; 
        viewactive = false; 
        autoMap.automapactive = false; 

        if (statcopy!=null) {
            statcopy = wminfo.copy();
            //memcpy (statcopy, &wminfo, sizeof(wminfo));
        }

        intermission.WI_Start (wminfo); 
    } 

    /**
     * World Done
     */
    @SuppressWarnings("fallthrough")
    public void G_WorldDone () { 
        gameaction = ga_worlddone; 

        if (secretexit) {
            players[consoleplayer].didsecret = true;
        } 

        if ( gameMode == Defines.GameMode.COMMERCIAL ) {
            switch (gamemap) {
              case 15:
              case 31:
                if (!secretexit) {
                    break;
                }
              case 6:
              case 11:
              case 20:
              case 30:
//                finale.F_StartFinale ();
                break;
            }
        }
    } 


    void G_DoWorldDone () {        
        gamestate = GS_LEVEL; 
        gamemap = wminfo.next+1; 
        G_DoLoadLevel (); 
        gameaction = ga_nothing; 
        viewactive = true; 
    } 


    //
    // G_InitFromSavegame
    // Can be called by the startup code or the menu task. 
    //
    //extern boolean setsizeneeded;
    //void R_ExecuteSetViewSize (void);

    //char	savename[256];
    String savename;

    void G_LoadGame (String name) { 
        //strcpy (savename, name);
        savename = name;
        gameaction = ga_loadgame; 
    } 

    private static final int VERSIONSIZE	= 16;

// TODO    
    void G_DoLoadGame() { 
//        int		length; 
//        int		i; 
//        int		a,b,c; 
//        //char            vcheck[VERSIONSIZE]; 
//        String vcheck;
//
//        gameaction = ga_nothing; 
//
//        length = M_ReadFile (savename, savebuffer); 
//        save_p = savebuffer + SAVESTRINGSIZE;
//
//        // skip the description field 
//        memset (vcheck,0,sizeof(vcheck)); 
//        sprintf (vcheck,"version %i",VERSION); 
//        if (strcmp (save_p, vcheck)) 
//            return;				// bad version 
//        save_p += VERSIONSIZE; 
//
//        gameskill = *save_p++; 
//        gameepisode = *save_p++; 
//        gamemap = *save_p++; 
//        for (i=0 ; i<MAXPLAYERS ; i++) 
//            playeringame[i] = *save_p++; 
//
//        // load a base level 
//        G_InitNew (gameskill, gameepisode, gamemap); 
//
//        // get the times 
//        a = *save_p++; 
//        b = *save_p++; 
//        c = *save_p++; 
//        leveltime = (a<<16) + (b<<8) + c; 
//
//        // dearchive all the modifications
//        P_UnArchivePlayers (); 
//        P_UnArchiveWorld (); 
//        P_UnArchiveThinkers (); 
//        P_UnArchiveSpecials (); 
//
//        if (*save_p != 0x1d) 
//            I_Error ("Bad savegame");
//
//        // done 
//        Z_Free (savebuffer); 
//
//        if (setsizeneeded)
//            R_ExecuteSetViewSize ();
//
//        // draw the pattern into the back screen
//        R_FillBackScreen ();   
    } 


    //
    // G_SaveGame
    // Called by the menu task.
    // Description is a 24 byte text string 
    //
    void
    G_SaveGame
    ( int	slot,
      String	description ) 
    { 
        savegameslot = slot; 
        //strcpy (savedescription, description);
        savedescription = description;
        sendsave = true; 
    } 

    // TODO
    void G_DoSaveGame() { 
//        char	name[100]; 
//        char	name2[VERSIONSIZE]; 
//        char*	description; 
//        int		length; 
//        int		i; 
//
//        if (M_CheckParm("-cdrom"))
//            sprintf(name,"c:\\doomdata\\"SAVEGAMENAME"%d.dsg",savegameslot);
//        else
//            sprintf (name,SAVEGAMENAME"%d.dsg",savegameslot); 
//        description = savedescription; 
//
//        save_p = savebuffer = screens[1]+0x4000; 
//
//        memcpy (save_p, description, SAVESTRINGSIZE); 
//        save_p += SAVESTRINGSIZE; 
//        memset (name2,0,sizeof(name2)); 
//        sprintf (name2,"version %i",VERSION); 
//        memcpy (save_p, name2, VERSIONSIZE); 
//        save_p += VERSIONSIZE; 
//
//        *save_p++ = gameskill; 
//        *save_p++ = gameepisode; 
//        *save_p++ = gamemap; 
//        for (i=0 ; i<MAXPLAYERS ; i++) 
//            *save_p++ = playeringame[i]; 
//        *save_p++ = leveltime>>16; 
//        *save_p++ = leveltime>>8; 
//        *save_p++ = leveltime; 
//
//        P_ArchivePlayers (); 
//        P_ArchiveWorld (); 
//        P_ArchiveThinkers (); 
//        P_ArchiveSpecials (); 
//
//        *save_p++ = 0x1d;		// consistancy marker 
//
//        length = save_p - savebuffer; 
//        if (length > SAVEGAMESIZE) 
//            I_Error ("Savegame buffer overrun"); 
//        M_WriteFile (name, savebuffer, length); 
//        gameaction = ga_nothing; 
//        savedescription[0] = 0;		 
//
//        players[consoleplayer].message = GGSAVED; 
//
//        // draw the pattern into the back screen
//        R_FillBackScreen ();	
    } 


    //
    // G_InitNew
    // Can be called by the startup code or the menu task,
    // consoleplayer, displayplayer, playeringame[] should be set. 
    //
    Skill   d_skill; 
    int     d_episode; 
    int     d_map; 

    public void G_DeferedInitNew( 
            Skill	skill,
            int		episode,
            int		map) { 
        d_skill = skill; 
        d_episode = episode; 
        d_map = map; 
        gameaction = GameAction.ga_newgame; 
    } 

    public void G_DoNewGame() {
        demoplayback = false; 
        netdemo = false;
        netgame = false;
        deathmatch = 0;
        playeringame[1] = false;
        playeringame[2] = false;
        playeringame[3] = false;
        respawnparm = false;
        fastparm = false;
        nomonsters = false;
        consoleplayer = 0;
        G_InitNew (d_skill, d_episode, d_map); 
        gameaction = ga_nothing; 
    } 

    public void G_InitNew(
            Skill   _skill,
            int     _episode,
            int     _map     ) {
        
        Skill skill = _skill;
        int episode = _episode;
        int map = _map;
        
        int i; 

        if (paused) { 
            paused = false; 
            sound.S_ResumeSound (); 
        } 


        if (skill.getValue() > sk_nightmare.getValue()) {
            skill = sk_nightmare;
        }


        // This was quite messy with SPECIAL and commented parts.
        // Supposedly hacks to make the latest edition work.
        // It might not work properly.
        if (episode < 1) {
            episode = 1;
        } 

        if ( null != gameMode ) {
            switch (gameMode) {
                case RETAIL:
                    if (episode > 4) {
                        episode = 4;
                    }
                    break;
                case SHAREWARE:
                    if (episode > 1) {
                        episode = 1;	// only start episode 1 on shareware
                    }     break;
                default:
                    if (episode > 3) {
                        episode = 3;
                    }     break;
            }
        }



        if (map < 1) {
            map = 1;
        }

        if ( (map > 9)
             && ( gameMode != Defines.GameMode.COMMERCIAL) ) {
            map = 9;
        } 

        Random.getInstance().M_ClearRandom (); 

        respawnmonsters = skill == sk_nightmare || respawnparm;

        if (fastparm || (skill == sk_nightmare && gameskill != sk_nightmare) ) { 
            for (i=S_SARG_RUN1.ordinal() ; i<=S_SARG_PAIN2.ordinal() ; i++) {
                states[i].tics >>= 1;
            } 
            mobjinfo[MT_BRUISERSHOT.ordinal()].speed = 20*FRACUNIT; 
            mobjinfo[MT_HEADSHOT.ordinal()].speed = 20*FRACUNIT; 
            mobjinfo[MT_TROOPSHOT.ordinal()].speed = 20*FRACUNIT; 
        }  else if (skill != sk_nightmare && gameskill == sk_nightmare)  { 
            for (i=S_SARG_RUN1.ordinal() ; i<=S_SARG_PAIN2.ordinal() ; i++) {
                states[i].tics <<= 1;
            } 
            mobjinfo[MT_BRUISERSHOT.ordinal()].speed = 15*FRACUNIT; 
            mobjinfo[MT_HEADSHOT.ordinal()].speed = 10*FRACUNIT; 
            mobjinfo[MT_TROOPSHOT.ordinal()].speed = 10*FRACUNIT; 
        } 


        // force players to be initialized upon first level load         
        for (i=0 ; i<MAXPLAYERS ; i++) {
            if ( players[i] != null ) {
                players[i].playerstate = PST_REBORN;
            }
        } 

        usergame = true;                // will be set false if a demo 
        paused = false; 
        demoplayback = false; 
        autoMap.automapactive = false; 
        viewactive = true; 
        gameepisode = episode; 
        gamemap = map; 
        gameskill = skill; 

        viewactive = true;

        //Wad wad = Stats.getInstance().wad;
        
        // set the sky map for the episode
        if ( gameMode == Defines.GameMode.COMMERCIAL) {
            renderer.skytexture = wad.getMapTextureByName("SKY3");
            if (gamemap < 12) {
                renderer.skytexture = wad.getMapTextureByName ("SKY1");
            } else
                if (gamemap < 21) {
                    renderer.skytexture = wad.getMapTextureByName ("SKY2");
            }
        } else {
            switch (episode) {
                case 1:
                    renderer.skytexture = wad.getMapTextureByName ("SKY1");
                    break;
                case 2:
                    renderer.skytexture = wad.getMapTextureByName ("SKY2");
                    break;
                case 3:
                    renderer.skytexture = wad.getMapTextureByName ("SKY3");
                    break;
                case 4:	// Special Edition sky
                    renderer.skytexture = wad.getMapTextureByName ("SKY4");
                    break;
            }
        } 
        

        G_DoLoadLevel (); 
    } 

/*  // Needs Rework!!!!

    //
    // DEMO RECORDING 
    // 
    public static final int DEMOMARKER = 0x80;


    void G_ReadDemoTiccmd (TickCommand cmd) 
    { 
        if (demo_p == DEMOMARKER) {
            // end of demo data stream 
            G_CheckDemoStatus (); 
            return; 
        } 
        cmd.forwardmove = ((signed char)*demo_p++); 
        cmd.sidemove = ((signed char)*demo_p++); 
        cmd.angleturn = ((int char)*demo_p++)<<8; 
        cmd.buttons = (int char)*demo_p++; 
    } 



    void G_WriteDemoTiccmd (TickCommand cmd) 
    { 
        if (gamekeydown['q'])           // press q to end demo recording 
            G_CheckDemoStatus (); 
        *demo_p++ = cmd.forwardmove; 
        *demo_p++ = cmd.sidemove; 
        *demo_p++ = (cmd.angleturn+128)>>8; 
        *demo_p++ = cmd.buttons; 
        demo_p -= 4; 
        if (demo_p > demoend - 16)
        {
            // no more space 
            G_CheckDemoStatus (); 
            return; 
        } 

        G_ReadDemoTiccmd (cmd);         // make SURE it is exactly the same 
    } 



    //
    // G_RecordDemo 
    // 
    void G_RecordDemo (char* name) 
    { 
        int             i; 
        int				maxsize;

        usergame = false; 
        strcpy (demoname, name); 
        strcat (demoname, ".lmp"); 
        maxsize = 0x20000;
        i = M_CheckParm ("-maxdemo");
        if (i && i<myargc-1)
            maxsize = atoi(myargv[i+1])*1024;
        demobuffer = Z_Malloc (maxsize,PU_STATIC,NULL); 
        demoend = demobuffer + maxsize;

        demorecording = true; 
    } 


    void G_BeginRecording (void) 
    { 
        int             i; 

        demo_p = demobuffer;

        *demo_p++ = VERSION;
        *demo_p++ = gameskill; 
        *demo_p++ = gameepisode; 
        *demo_p++ = gamemap; 
        *demo_p++ = deathmatch; 
        *demo_p++ = respawnparm;
        *demo_p++ = fastparm;
        *demo_p++ = nomonsters;
        *demo_p++ = consoleplayer;

        for (i=0 ; i<MAXPLAYERS ; i++) 
            *demo_p++ = playeringame[i]; 		 
    } 

*/
    //
    // G_PlayDemo 
    //

    String	defdemoname; 

    void G_DeferedPlayDemo (String name) { 
        defdemoname = name; 
        gameaction = GameAction.ga_playdemo; 
    } 

    void G_DoPlayDemo ()  { 
        Skill skill; 
        int             i, episode, map; 

        gameaction = ga_nothing; 
//        demobuffer = demo_p = W_CacheLumpName (defdemoname, PU_STATIC); 
//        if ( *demo_p++ != VERSION) {
//          fprintf( stderr, "Demo is from a different game version!\n");
//          gameaction = ga_nothing;
//          return;
//        }
//
//        skill = *demo_p++; 
//        episode = *demo_p++; 
//        map = *demo_p++; 
//        deathmatch = *demo_p++;
//        respawnparm = *demo_p++;
//        fastparm = *demo_p++;
//        nomonsters = *demo_p++;
//        consoleplayer = *demo_p++;
//
//        for (i=0 ; i<MAXPLAYERS ; i++) 
//            playeringame[i] = *demo_p++; 
//        if (playeringame[1]) 
//        { 
//            netgame = true; 
//            netdemo = true; 
//        }
//
        // don't spend a lot of time in loadlevel 
        precache = false;
//        G_InitNew (skill, episode, map); 
        precache = true; 

        usergame = false; 
        demoplayback = true; 
    }

    //
    // G_TimeDemo 
    //
    void G_TimeDemo (String name) { 	 
        nodrawers = isParam("-nodraw");
        noblit = isParam ("-noblit"); 
        timingdemo = true; 
        doomMain.singletics = true; 

        defdemoname = name; 
        gameaction = ga_playdemo; 
    } 

     /**
     * Called after a death or level completion to allow demos to be cleaned up 
     * Returns true if a new demo loop action will take place 
     * 
     * @return 
     */
    public boolean G_CheckDemoStatus ()  { 
        long             endtime; 

        if (timingdemo) { 
            endtime = SystemInterface.getInstance().I_GetTime (); 
            //SystemInterface.I_Error ("timed {0} gametics in {1} realtics",
            //        new Object[]{gametic, endtime-starttime});
            thump.base.Defines.logger.log(Level.SEVERE, 
                    "timed {0} gametics in {1} realtics",
                    new Object[]{gametic, endtime-starttime});
        } 

        if (demoplayback) 
        { 
            if (singledemo) {
                SystemInterface.getInstance().I_Quit ();
            } 

            //Z_ChangeTag (demobuffer, PU_CACHE); // Don't need.
            demoplayback = false; 
            netdemo = false;
            netgame = false;
            deathmatch = 0;
            playeringame[1] = false;
            playeringame[2] = false;
            playeringame[3] = false;
            respawnparm = false;
            fastparm = false;
            nomonsters = false;
            consoleplayer = 0;
            doomMain.D_AdvanceDemo (); 
            return true; 
        } 

        if (demorecording) { 
//            *demo_p++ = DEMOMARKER; 
//            M_WriteFile (demoname, demobuffer, demo_p - demobuffer); 
//            Z_Free (demobuffer); 
//            demorecording = false; 
//            I_Error ("Demo %s recorded",demoname); 
        } 

        return false; 
    } 

    public void setDoomMain(DoomMain dm ) {
        this.doomMain = dm;
    }

    /**
     * Get localized message for requested @tag.
     * Messages are stored in thump.i18n.MessageBundle.properties
     * 
     * @param tag bundle tag name to retrieve
     * @return
     */
    public static String getMessage(String tag) {
        return messages.getString(tag);
    }

}
