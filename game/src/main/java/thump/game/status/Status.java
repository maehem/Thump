/*
 * Status Bar
 */
package thump.game.status;

import java.util.logging.Level;
import static thump.base.Defines.SCREENHEIGHT;
import static thump.base.Defines.SCREENWIDTH;
import static thump.base.Defines.SCREEN_MUL;
import static thump.base.Defines.logger;
import static thump.base.Tables.ANG180;
import static thump.base.Tables.ANG45;
import thump.game.Defines;
import static thump.game.Defines.AmmoType.am_noammo;
import static thump.game.Defines.GameMode.COMMERCIAL;
import static thump.game.Defines.GameMode.REGISTERED;
import static thump.game.Defines.GameMode.RETAIL;
import static thump.game.Defines.GameMode.SHAREWARE;
import static thump.game.Defines.KEY_ENTER;
import static thump.game.Defines.MAXPLAYERS;
import thump.game.Defines.PowerType;
import static thump.game.Defines.PowerType.pw_invulnerability;
import static thump.game.Defines.PowerType.pw_ironfeet;
import static thump.game.Defines.PowerType.pw_strength;
import static thump.game.Defines.TICRATE;
import static thump.game.Defines.WeaponType.wp_chainsaw;
import static thump.game.Defines.weaponinfo;
import thump.game.Event;
import thump.game.Cheat;
import static thump.game.Event.EventType.*;
import thump.game.Game;
import thump.game.Player;
import static thump.game.Player.Cheat.*;
import thump.game.maplevel.MapObject;
import thump.game.play.Interaction;
import thump.game.play.Random;
import thump.wad.mapraw.PatchData;
import thump.system.VideoInterface;
import static thump.game.automap.AutoMap.*;
import static thump.game.status.Status.ChatStateNum.StartChatState;
import static thump.game.status.Status.StateNum.AutomapState;
import static thump.game.status.Status.StateNum.FirstPersonState;
import thump.game.sound.sfx.Sounds;
import static thump.game.sound.sfx.Sounds.MusicEnum.mus_e1m1;
import static thump.game.sound.sfx.Sounds.MusicEnum.mus_runnin;
import thump.wad.Wad;
import thump.wad.lump.PictureLump;

/**
 *
 * @author mark
 */
public class Status {

    private final Game game;

    public Status(Game game) {
        this.game = game;
    }
    
    public static final int BG = 4;
    public static final int FG = 0;
    
    // Size of statusbar.
    // Now sensitive for scaling.
    public static final int ST_HEIGHT   = 32 * SCREEN_MUL;
    public static final int ST_WIDTH    = SCREENWIDTH;
    public static final int ST_Y        = (SCREENHEIGHT - ST_HEIGHT);
    
    // Palette indices.
    // For damage/bonus red-/gold-shifts
    private static final int STARTREDPALS	=	1;
    private static final int STARTBONUSPALS	=	9;
    private static final int NUMREDPALS		=	8;
    private static final int NUMBONUSPALS	=	4;
    // Radiation suit, green shift.
    private static final int RADIATIONPAL	=	13;

    // N/256*100% probability
    //  that the normal face state will change
    private static final int ST_FACEPROBABILITY	=	96;

    // For Responder
    private static final int ST_TOGGLECHAT	=	KEY_ENTER;

    // Location of status bar
    private static final int ST_X	=			0;
    private static final int ST_X2	=			104;

    private static final int ST_FX  	=		143;
    private static final int ST_FY  	=		169;

    // Should be set to patch width
    //  for tall numbers later on
    private static int      ST_TALLNUMWIDTH = 0;

    // Number of status faces.
    private static final int ST_NUMPAINFACES	=	5;
    private static final int ST_NUMSTRAIGHTFACES=	3;
    private static final int ST_NUMTURNFACES	=	2;
    private static final int ST_NUMSPECIALFACES	=	3;

    private static final int ST_FACESTRIDE =
              (ST_NUMSTRAIGHTFACES+ST_NUMTURNFACES+ST_NUMSPECIALFACES);

    private static final int ST_NUMEXTRAFACES	=	2;

    private static final int ST_NUMFACES =
              (ST_FACESTRIDE*ST_NUMPAINFACES+ST_NUMEXTRAFACES);

    private static final int ST_TURNOFFSET	=	(ST_NUMSTRAIGHTFACES);
    private static final int ST_OUCHOFFSET	=	(ST_TURNOFFSET + ST_NUMTURNFACES);
    private static final int ST_EVILGRINOFFSET	=	(ST_OUCHOFFSET + 1);
    private static final int ST_RAMPAGEOFFSET	=	(ST_EVILGRINOFFSET + 1);
    private static final int ST_GODFACE		=	(ST_NUMPAINFACES*ST_FACESTRIDE);
    private static final int ST_DEADFACE	=		(ST_GODFACE+1);

    private static final int ST_FACESX		=	143;
    private static final int ST_FACESY		=	168;

    private static final int ST_EVILGRINCOUNT	=	(2*TICRATE);
    private static final int ST_STRAIGHTFACECOUNT=	(TICRATE/2);
    private static final int ST_TURNCOUNT	=	(1*TICRATE);
    private static final int ST_OUCHCOUNT	=	(1*TICRATE);
    private static final int ST_RAMPAGEDELAY	=	(2*TICRATE);

    private static final int ST_MUCHPAIN	=		20;


    // Location and size of statistics,
    //  justified according to widget type.
    // Problem is, within which space? STbar? Screen?
    // Note: this could be read in by a lump.
    //       Problem is, is the stuff rendered
    //       into a buffer,
    //       or into the frame buffer?

    // AMMO number pos.
    private static final int ST_AMMOWIDTH	=	3;	
    private static final int ST_AMMOX		=	44;
    private static final int ST_AMMOY		=	171;

    // HEALTH number pos.
    private static final int ST_HEALTHWIDTH	=	3;	
    private static final int ST_HEALTHX		=	90;
    private static final int ST_HEALTHY		=	171;

    // Weapon pos.
    private static final int ST_ARMSX		=	111;
    private static final int ST_ARMSY		=	172;
    private static final int ST_ARMSBGX		=	104;
    private static final int ST_ARMSBGY		=	168;
    private static final int ST_ARMSXSPACE	=	12;
    private static final int ST_ARMSYSPACE	=	10;

    // Frags pos.
    private static final int ST_FRAGSX		=	138;
    private static final int ST_FRAGSY		=	171	;
    private static final int ST_FRAGSWIDTH	=	2;

    // ARMOR number pos.
    private static final int ST_ARMORWIDTH	=	3;
    private static final int ST_ARMORX		=	221;
    private static final int ST_ARMORY		=	171;

    // Key icon positions.
    private static final int ST_KEY0WIDTH	=	8;
    private static final int ST_KEY0HEIGHT	=	5;
    private static final int ST_KEY0X		=	239;
    private static final int ST_KEY0Y		=	171;
    private static final int ST_KEY1WIDTH	=	ST_KEY0WIDTH;
    private static final int ST_KEY1X		=	239;
    private static final int ST_KEY1Y		=	181;
    private static final int ST_KEY2WIDTH	=	ST_KEY0WIDTH;
    private static final int ST_KEY2X		=	239;
    private static final int ST_KEY2Y		=	191;

    // Ammunition counter.
    private static final int ST_AMMO0WIDTH	=	3;
    private static final int ST_AMMO0HEIGHT	=	6;
    private static final int ST_AMMO0X		=	288;
    private static final int ST_AMMO0Y		=	173;
    private static final int ST_AMMO1WIDTH	=	ST_AMMO0WIDTH;
    private static final int ST_AMMO1X		=	288;
    private static final int ST_AMMO1Y		=	179;
    private static final int ST_AMMO2WIDTH	=	ST_AMMO0WIDTH;
    private static final int ST_AMMO2X		=	288;
    private static final int ST_AMMO2Y		=	191;
    private static final int ST_AMMO3WIDTH	=	ST_AMMO0WIDTH;
    private static final int ST_AMMO3X		=	288;
    private static final int ST_AMMO3Y		=	185;

    // Indicate maximum ammunition.
    // Only needed because backpack exists.
    private static final int ST_MAXAMMO0WIDTH	=	3;
    private static final int ST_MAXAMMO0HEIGHT	=	5;
    private static final int ST_MAXAMMO0X	=	314;
    private static final int ST_MAXAMMO0Y	=	173;
    private static final int ST_MAXAMMO1WIDTH	=	ST_MAXAMMO0WIDTH;
    private static final int ST_MAXAMMO1X	=	314;
    private static final int ST_MAXAMMO1Y	=	179;
    private static final int ST_MAXAMMO2WIDTH	=	ST_MAXAMMO0WIDTH;
    private static final int ST_MAXAMMO2X	=	314;
    private static final int ST_MAXAMMO2Y	=	191;
    private static final int ST_MAXAMMO3WIDTH	=	ST_MAXAMMO0WIDTH;
    private static final int ST_MAXAMMO3X	=	314;
    private static final int ST_MAXAMMO3Y	=	185;

    // pistol
    private static final int ST_WEAPON0X    = 110;
    private static final int ST_WEAPON0Y    = 172;

    // shotgun
    private static final int ST_WEAPON1X    = 122;
    private static final int ST_WEAPON1Y    = 172;

    // chain gun
    private static final int ST_WEAPON2X    = 134;
    private static final int ST_WEAPON2Y    = 172;

    // missile launcher
    private static final int ST_WEAPON3X    = 110;
    private static final int ST_WEAPON3Y    = 181;

    // plasma gun
    private static final int ST_WEAPON4X    = 122;
    private static final int ST_WEAPON4Y    = 181;

    // bfg
    private static final int ST_WEAPON5X    = 134;
    private static final int ST_WEAPON5Y    = 181;

    // WPNS title
    private static final int ST_WPNSX       = 109;
    private static final int ST_WPNSY       = 191;

    // DETH title
    private static final int ST_DETHX       = 109;
    private static final int ST_DETHY       = 191;

    //Incoming messages window location
    //UNUSED
    // private static final int ST_MSGTEXTX	=   (viewwindowx);
    // private static final int ST_MSGTEXTY	=   (viewwindowy+viewheight-18);
    private static final int ST_MSGTEXTX    = 0;
    private static final int ST_MSGTEXTY    = 0;
    // Dimensions given in characters.
    private static final int ST_MSGWIDTH    = 52;
    // Or shall I say, in lines?
    private static final int ST_MSGHEIGHT   = 1;

    private static final int ST_OUTTEXTX    = 0;
    private static final int ST_OUTTEXTY    = 6;

    // Width, in characters again.
    private static final int ST_OUTWIDTH    = 52;
    // Height, in lines. 
    private static final int ST_OUTHEIGHT   = 1;

    // NOT USED?
//    private              int ST_MAPWIDTH	=
//        (strlen(Game.getInstance().headUp.mapnames[(Game.getInstance().gameepisode-1)*9+(gamemap-1)]));

    //NOT USED?
//    private static final int ST_MAPTITLEX =
//        (SCREENWIDTH - ST_MAPWIDTH * ST_CHATFONTWIDTH);

    private static final int ST_MAPTITLEY   = 0;
    private static final int ST_MAPHEIGHT   = 1;

// States for status bar code.
    public enum StateNum {
        AutomapState,
        FirstPersonState
    }

    // States for the chat code.
    public enum ChatStateNum {
        StartChatState,
        WaitDestState,
        GetChatState
    }

    Player  plyr;     // main player in game
    boolean st_firsttime;    // ST_Start() has just been called
    int     veryfirsttime = 1;    // used to execute ST_Init() only once
    int     lu_palette;    // lump number for PLAYPAL
    int     st_clock;    // used for timing
    int     st_msgcounter = 0;    // used for making messages go away
    static  ChatStateNum st_chatstate;    // used when in chat 
    static  StateNum st_gamestate;    // whether in automap or first-person
    boolean st_statusbaron;    // whether left-side main status bar is active
    boolean st_chat;    // whether status bar chat is active
    boolean st_oldchat;    // value of st_chat before message popped up
    boolean st_cursoron;    // whether chat window has the cursor on
    boolean st_notdeathmatch;     // !deathmatch
    boolean st_armson;    // !deathmatch && st_statusbaron
    boolean st_fragson;     // !deathmatch
    PatchData   sbar;    // main bar left
    PatchData   tallnum[]       = new PatchData[10];    // 0-9, tall numbers
    PatchData   tallpercent;    // tall % sign
    PatchData   shortnum[]      = new PatchData[10];    // 0-9, short, yellow (,different!) numbers
    PatchData   keys[]          = new PatchData[Defines.Card.values().length];     // 3 key-cards, 3 skulls
    PatchData   faces[]         = new PatchData[ST_NUMFACES];    // face status patches
    PatchData   faceback;    // face background
    PatchData   armsbg;     // main bar right
    PatchData   arms[][] = new PatchData[6][2];     // weapon ownership patches


    // ready-weapon widget
    static NumberWidget	w_ready;

     // in deathmatch only, summary of frags stats
    static NumberWidget	w_frags;

    // health widget
    static PercentWidget	w_health;

    // arms background
    static BinaryIconWidget	w_armsbg; 

    // weapon ownership widgets
    static MultipleIconWidgetBool	w_arms[] = new MultipleIconWidgetBool[6];

    // face status widget
    static MultipleIconWidget	w_faces; 

    // keycard widgets
    static MultipleIconWidget	w_keyboxes[] = new MultipleIconWidget[3];

    // armor widget
    static PercentWidget	w_armor;

    // ammo widgets
    static NumberWidget         w_ammo[] = new NumberWidget[4];

    // max ammo widgets
    static NumberWidget         w_maxammo[] = new NumberWidget[4]; 



     // number of frags so far in deathmatch
    int	st_fragscount;

    // used to use appopriately pained face
    int	st_oldhealth = -1;

    // used for evil grin
    boolean	oldweaponsowned[] = new boolean[Defines.WeaponType.values().length]; 

     // count until face changes
    int	st_facecount = 0;

    // current face index, used by w_faces
    int	st_faceindex = 0;

    // holds key-type for each key box on bar
    int	keyboxes[] = new int[3]; 

    // a random number per tick
    int	st_randomnumber;  



    // Massive bunches of cheat shit
    //  to keep it from being easy to figure them out.
    // Yeah, right...
    int	cheat_mus_seq[] =
    {
        0xb2, 0x26, 0xb6, 0xae, 0xea, 0x01, 0x00, 0x00, 0xff
    };

    int	cheat_choppers_seq[] =
    {
        0xb2, 0x26, 0xe2, 0x32, 0xf6, 0x2a, 0x2a, 0xa6, 0x6a, 0xea, 0xff // id...
    };

    int	cheat_god_seq[] =
    {
        0xb2, 0x26, 0x26, 0xaa, 0x26, 0xff  // iddqd
    };

    int	cheat_ammo_seq[] =
    {
        0xb2, 0x26, 0xf2, 0x66, 0xa2, 0xff	// idkfa
    };

    int	cheat_ammonokey_seq[] =
    {
        0xb2, 0x26, 0x66, 0xa2, 0xff	// idfa
    };


    // Smashing Pumpkins Into Samml Piles Of Putried Debris. 
    int	cheat_noclip_seq[] =
    {
        0xb2, 0x26, 0xea, 0x2a, 0xb2,	// idspispopd
        0xea, 0x2a, 0xf6, 0x2a, 0x26, 0xff
    };

    //
    int	cheat_commercial_noclip_seq[] =
    {
        0xb2, 0x26, 0xe2, 0x36, 0xb2, 0x2a, 0xff	// idclip
    }; 



    int	cheat_powerup_seq[][] =
    {
        { 0xb2, 0x26, 0x62, 0xa6, 0x32, 0xf6, 0x36, 0x26, 0x6e,       0xff }, 	// beholdv
        { 0xb2, 0x26, 0x62, 0xa6, 0x32, 0xf6, 0x36, 0x26, 0xea, 0xff }, 	// beholds
        { 0xb2, 0x26, 0x62, 0xa6, 0x32, 0xf6, 0x36, 0x26, 0xb2, 0xff }, 	// beholdi
        { 0xb2, 0x26, 0x62, 0xa6, 0x32, 0xf6, 0x36, 0x26, 0x6a,       0xff }, 	// beholdr
        { 0xb2, 0x26, 0x62, 0xa6, 0x32, 0xf6, 0x36, 0x26, 0xa2, 0xff }, 	// beholda
        { 0xb2, 0x26, 0x62, 0xa6, 0x32, 0xf6, 0x36, 0x26, 0x36,       0xff }, 	// beholdl
        { 0xb2, 0x26, 0x62, 0xa6, 0x32, 0xf6, 0x36, 0x26, 0xff }		// behold
    };


    int	cheat_clev_seq[] =
    {
        0xb2, 0x26,  0xe2, 0x36, 0xa6, 0x6e, 1, 0, 0, 0xff	// idclev
    };


    // my position cheat
    int	cheat_mypos_seq[] =
    {
        0xb2, 0x26, 0xb6, 0xba, 0x2a, 0xf6, 0xea, 0xff	// idmypos
    }; 


    // Now what?
    Cheat cheat_mus               = new Cheat(cheat_mus_seq);
    Cheat cheat_god               = new Cheat(cheat_god_seq);
    Cheat cheat_ammo              = new Cheat(cheat_ammo_seq);
    Cheat cheat_ammonokey         = new Cheat(cheat_ammonokey_seq);
    Cheat cheat_noclip            = new Cheat(cheat_noclip_seq);
    Cheat cheat_commercial_noclip = new Cheat(cheat_commercial_noclip_seq);

    Cheat	cheat_powerup[] = {
        new Cheat( cheat_powerup_seq[0] ),
        new Cheat( cheat_powerup_seq[1] ),
        new Cheat( cheat_powerup_seq[2] ),
        new Cheat( cheat_powerup_seq[3] ),
        new Cheat( cheat_powerup_seq[4] ),
        new Cheat( cheat_powerup_seq[5] ),
        new Cheat( cheat_powerup_seq[6] )
    };

    Cheat	cheat_choppers = new Cheat( cheat_choppers_seq );
    Cheat	cheat_clev = new Cheat(  cheat_clev_seq );
    Cheat	cheat_mypos = new Cheat(  cheat_mypos_seq );

    private int st_palette = 0;

    private boolean	st_stopped = true;


    // 
    //extern char*	mapnames[];


    //
    // STATUS BAR CODE
    //
    //void ST_Stop(void);

    void ST_refreshBackground() {

        if (st_statusbaron) {
            //Defines.logger.config("ST_refreshBackground\n");
            Game.getInstance().renderer.video.drawPatch(ST_X, 0, BG, sbar);

            if (Game.getInstance().netgame) {
                Game.getInstance().renderer.video.drawPatch(ST_FX, 0, BG, faceback);
            }

            Game.getInstance().renderer.video.copyRect(ST_X, 0, BG, ST_WIDTH, ST_HEIGHT, ST_X, ST_Y, FG);
        }
    }

    // Respond to keyboard input events,
    //  intercept cheats.
    public boolean ST_Responder (Event ev) {
      int		i;

        // Filter automap on/off.
        if (ev.type == ev_keyup
                && ((ev.data1 & 0xffff0000) == AM_MSGHEADER)) {
            switch (ev.data1) {
                case AM_MSGENTERED:
                    st_gamestate = AutomapState;
                    st_firsttime = true;
                    break;

                case AM_MSGEXITED:
                    //	fprintf(stderr, "AM exited\n");
                    st_gamestate = FirstPersonState;
                    break;
            }
        } else if (ev.type == ev_keydown) {  // if a user keypress...
        if (!Game.getInstance().netgame) {
                // b. - enabled for more debug fun.
                // if (gameskill != sk_nightmare) {

                // 'dqd' cheat for toggleable god mode
                if (cheat_god.cht_CheckCheat( ev.data1)) {
                    plyr.cheats ^= CF_GODMODE.getValue();
                    if ((plyr.cheats & CF_GODMODE.getValue())>0) {
                        if (plyr.mo!=null) {
                            plyr.mo.health = 100;
                        }

                        plyr.health = 100;
                        plyr.message = Game.getMessage("STSTR_DQDON");
                    } else {
                        plyr.message = Game.getMessage("STSTR_DQDOFF");
                    }
                } // 'fa' cheat for killer fucking arsenal
                else if (cheat_ammonokey.cht_CheckCheat(  ev.data1)) {
                    plyr.armorpoints = 200;
                    plyr.armortype = 2;

                    for (i = 0; i < Defines.WeaponType.values().length; i++) {
                        plyr.weaponowned[i] = true;
                    }

                    for (i = 0; i < Defines.AmmoType.values().length; i++) {
                        plyr.ammo[i] = plyr.maxammo[i];
                    }

                    plyr.message = Game.getMessage("STSTR_FAADDED");
                } // 'kfa' cheat for key full ammo
                else if (cheat_ammo.cht_CheckCheat( ev.data1)) {
                    plyr.armorpoints = 200;
                    plyr.armortype = 2;

                    for (i = 0; i < Defines.WeaponType.values().length; i++) {
                        plyr.weaponowned[i] = true;
                    }

                    for (i = 0; i < Defines.AmmoType.values().length; i++) {
                        plyr.ammo[i] = plyr.maxammo[i];
                    }

                    for (i = 0; i < Defines.Card.values().length; i++) {
                        plyr.cards[i] = true;
                    }

                    plyr.message = Game.getMessage("STSTR_KFAADDED");
                } // 'mus' cheat for changing music
                else if (cheat_mus.cht_CheckCheat(  ev.data1)) {

                    int buf[] = new int[3];
                    int musnum;

                    plyr.message = Game.getMessage("STSTR_MUS");
                    cheat_mus.cht_GetParam( buf);

                    if (game.gameMode == COMMERCIAL) {
                        musnum = mus_runnin.ordinal() + (buf[0] - '0') * 10 + buf[1] - '0' - 1;

                        if (((buf[0] - '0') * 10 + buf[1] - '0') > 35) {
                            plyr.message = Game.getMessage("STSTR_NOMUS");
                        } else {
                            game.sound.S_ChangeMusic(Sounds.MusicEnum.values()[musnum], true);
                        }
                    } else {
                        musnum = mus_e1m1.ordinal() + (buf[0] - '1') * 9 + (buf[1] - '1');

                        if (((buf[0] - '1') * 9 + buf[1] - '1') > 31) {
                            plyr.message = Game.getMessage("STSTR_NOMUS");
                        } else {
                            game.sound.S_ChangeMusic(Sounds.MusicEnum.values()[musnum], true);
                        }
                    }
                } // Simplified, accepting both "noclip" and "idspispopd".
                // no clipping mode cheat
                else if (    cheat_noclip.cht_CheckCheat( ev.data1)
                          || cheat_commercial_noclip.cht_CheckCheat( ev.data1)) {
                    plyr.cheats ^= CF_NOCLIP.getValue();

                    if ((plyr.cheats & CF_NOCLIP.getValue())>0) {
                        plyr.message = Game.getMessage("STSTR_NCON");
                    } else {
                        plyr.message = Game.getMessage("STSTR_NCOFF");
                    }
                }
                // 'behold?' power-up cheats
                for (i = 0; i < 6; i++) {
                    if (cheat_powerup[i].cht_CheckCheat( ev.data1)) {
                        if (plyr.powers[i]==0) {
                            Interaction.P_GivePower(plyr, PowerType.values()[i]);
                        } else if (i != pw_strength.ordinal()) {
                            plyr.powers[i] = 1;
                        } else {
                            plyr.powers[i] = 0;
                        }

                        plyr.message = Game.getMessage("STSTR_BEHOLDX");
                    }
                }

                // 'behold' power-up menu
                if (cheat_powerup[6].cht_CheckCheat( ev.data1)) {
                    plyr.message = Game.getMessage("STSTR_BEHOLD");
                } // 'choppers' invulnerability & chainsaw
                else if (cheat_choppers.cht_CheckCheat( ev.data1)) {
                    plyr.weaponowned[wp_chainsaw.ordinal()] = true;
                    plyr.powers[pw_invulnerability.ordinal()] = 1;
                    plyr.message = Game.getMessage("STSTR_CHOPPERS");
                } // 'mypos' for player position
                else if (cheat_mypos.cht_CheckCheat( ev.data1)) {
//                    static char buf[ST_MSGWIDTH ];
//                    sprintf(buf,  "ang=0x%x;x,y=(0x%x,0x%x)",
//                        players[consoleplayer].mo.angle,
//                        players[consoleplayer].mo.x,
//                        players[consoleplayer].mo.y);
//                    plyr.message  = buf;
                    MapObject mo = game.players[game.consoleplayer].mo;

                    plyr.message = 
                            "ang=0x"  + String.format("%08x", mo.angle) + ";"
                          + "x,y=(0x" + String.format("%08x", mo.x) + "," 
                          +      "0x" + String.format("%08x", mo.y) + ")";
                }
}

        // 'clev' change-level cheat
        if (cheat_clev.cht_CheckCheat( ev.data1)) {
          int		buf[]= new int[3];
          int		epsd;
          int		map;

          cheat_clev.cht_GetParam( buf);

          if (game.gameMode == COMMERCIAL) {
            epsd = 0;
            map = (buf[0] - '0')*10 + buf[1] - '0';
          } else {
            epsd = buf[0] - '0';
            map = buf[1] - '0';
          }

          // Catch invalid maps.
          if (epsd < 1) {
              return false;
          }

          if (map < 1) {
              return false;
          }

          // Ohmygod - this is not going to work.
          if ((game.gameMode == RETAIL)
              && ((epsd > 4) || (map > 9))) {
              return false;
          }

          if ((game.gameMode == REGISTERED)
              && ((epsd > 3) || (map > 9))) {
              return false;
          }

          if ((game.gameMode == SHAREWARE)
              && ((epsd > 1) || (map > 9))) {
              return false;
          }

          if ((game.gameMode == COMMERCIAL)
            && (( epsd > 1) || (map > 34))) {
              return false;
          }

          // So be it.
          plyr.message = Game.getMessage("STSTR_CLEV");
          game.G_DeferedInitNew(game.gameskill, epsd, map);
        }    
      }
      return false;
    }

    int ST_calcPainOffset() {
        int     health;
        int     lastcalc = 0;
        int     oldhealth = -1;

        health = plyr.health > 100 ? 100 : plyr.health;

        if (health != oldhealth) {
            lastcalc = ST_FACESTRIDE * (((100 - health) * ST_NUMPAINFACES) / 101);
            oldhealth = health;
        }

        return lastcalc;
    }

    /**
     * This is a not-very-pretty routine which handles the face states and their
     * timing. the precedence of expressions is: dead > evil grin > turned head
     * > straight ahead
     *
     */
    void ST_updateFaceWidget() {
        int		i;
        long	badguyangle;
        long	diffang;
        int	lastattackdown = -1;
        int	priority = 0;
        boolean	doevilgrin;

        if (priority < 10) {
            // dead
            if (plyr.health==0) {
                priority = 9;
                st_faceindex = ST_DEADFACE;
                st_facecount = 1;
            }
        }

        if (priority < 9) {
            if (plyr.bonuscount>0) {
                // picking up bonus
                doevilgrin = false;

                for (i=0;i<Defines.WeaponType.values().length;i++) {
                    if (oldweaponsowned[i] != plyr.weaponowned[i]) {
                        doevilgrin = true;
                        oldweaponsowned[i] = plyr.weaponowned[i];
                    }
                }
                if (doevilgrin) {
                    // evil grin if just picked up weapon
                    priority = 8;
                    st_facecount = ST_EVILGRINCOUNT;
                    st_faceindex = ST_calcPainOffset() + ST_EVILGRINOFFSET;
                }
            }
        }

        if (priority < 8) {
            if (plyr.damagecount>0
                && plyr.attacker!=null
                && plyr.attacker != plyr.mo)
            {
                // being attacked
                priority = 7;

                if (plyr.health - st_oldhealth > ST_MUCHPAIN) {
                    st_facecount = ST_TURNCOUNT;
                    st_faceindex = ST_calcPainOffset() + ST_OUCHOFFSET;
                } else {
                    badguyangle = game.renderer.R_PointToAngle2(plyr.mo.x,
                                                  plyr.mo.y,
                                                  plyr.attacker.x,
                                                  plyr.attacker.y);

                    boolean b;
                    if (badguyangle > plyr.mo.angle) {
                        // whether right or left
                        diffang = badguyangle - plyr.mo.angle;
                        b = diffang > ANG180; 
                    } else {
                        // whether left or right
                        diffang = plyr.mo.angle - badguyangle;
                        b = diffang <= ANG180; 
                    } // confusing, aint it?


                    st_facecount = ST_TURNCOUNT;
                    st_faceindex = ST_calcPainOffset();

                    if (diffang < ANG45) {
                        // head-on    
                        st_faceindex += ST_RAMPAGEOFFSET;
                    } else if (b) {
                        // turn face right
                        st_faceindex += ST_TURNOFFSET;
                    } else {
                        // turn face left
                        st_faceindex += ST_TURNOFFSET+1;
                    }
                }
            }
        }

        if (priority < 7) {
            // getting hurt because of your own damn stupidity
            if (plyr.damagecount>0) {
                if (plyr.health - st_oldhealth > ST_MUCHPAIN) {
                    priority = 7;
                    st_facecount = ST_TURNCOUNT;
                    st_faceindex = ST_calcPainOffset() + ST_OUCHOFFSET;
                } else {
                    priority = 6;
                    st_facecount = ST_TURNCOUNT;
                    st_faceindex = ST_calcPainOffset() + ST_RAMPAGEOFFSET;
                }
            }
        }

        if (priority < 6) {
            // rapid firing
            if (plyr.attackdown) {
                if (lastattackdown==-1) {
                    lastattackdown = ST_RAMPAGEDELAY;
                } else if (--lastattackdown==0) {
                    priority = 5;
                    st_faceindex = ST_calcPainOffset() + ST_RAMPAGEOFFSET;
                    st_facecount = 1;
                    lastattackdown = 1;
                }
            } else {
                lastattackdown = -1;
            }

        }

        if (priority < 5) {
            // invulnerability
            if ((plyr.cheats & CF_GODMODE.getValue())>0
                || plyr.powers[pw_invulnerability.ordinal()]>0)
            {
                priority = 4;

                st_faceindex = ST_GODFACE;
                st_facecount = 1;
            }
        }

        // look left or look right if the facecount has timed out
        if (0==st_facecount) {
            st_faceindex = ST_calcPainOffset() + (st_randomnumber % 3);
            st_facecount = ST_STRAIGHTFACECOUNT;
            priority = 0;
        }

        st_facecount--;

    }

    void ST_updateWidgets() {
        int	largeammo = 1994; // means "n/a"
        int		i;

        // must redirect the pointer if the ready weapon has changed.
        //  if (w_ready.data != plyr.readyweapon)
        //  {
        if (weaponinfo[plyr.readyweapon.ordinal()].ammo == am_noammo) {
            w_ready.num = largeammo;
        } else {
            w_ready.num = plyr.ammo[weaponinfo[plyr.readyweapon.ordinal()].ammo.ordinal()];
        }
        //{
        // int tic=0;
        // int dir=-1;
        // if (!(tic&15))
        //   plyr.ammo[weaponinfo[plyr.readyweapon].ammo]+=dir;
        // if (plyr.ammo[weaponinfo[plyr.readyweapon].ammo] == -100)
        //   dir = 1;
        // tic++;
        // }
        w_ready.data = plyr.readyweapon;

        // if (*w_ready.on)
        //  STlib_updateNum(&w_ready, true);
        // refresh weapon change
        //  }

        // update keycard multiple widgets
        for (i=0;i<3;i++) {
            keyboxes[i] = plyr.cards[i] ? i : -1;

            if (plyr.cards[i+3]) {
                keyboxes[i] = i+3;
            }
        }

        // refresh everything if this is him coming back to life
        ST_updateFaceWidget();

        // used by the w_armsbg widget
        st_notdeathmatch = !(game.deathmatch>0);

        // used by w_arms[] widgets
        st_armson = st_statusbaron && !(game.deathmatch>0); 

        // used by w_frags widget
        st_fragson = game.deathmatch>0 && st_statusbaron; 
        st_fragscount = 0;

        for (i=0 ; i<MAXPLAYERS ; i++)
        {
            if (i != game.consoleplayer) {
                st_fragscount += plyr.frags[i];
            } else {
                st_fragscount -= plyr.frags[i];
            }
        }

        // get rid of chat window if up because of message
        st_msgcounter--;
        if (st_msgcounter<=0) {
            st_chat = st_oldchat;
        }

    }

    public void ST_Ticker () {

        st_clock++;
        st_randomnumber = Random.getInstance().M_Random();
        ST_updateWidgets();
        st_oldhealth = plyr.health;

    }

    void ST_doPaletteStuff() {

        int		palette=0;
        byte[]           pal;
        int		cnt;
        int		bzc;

        cnt = plyr.damagecount;

        if (plyr.powers[pw_strength.ordinal()]>0) {
            // slowly fade the berzerk out
            bzc = 12 - (plyr.powers[pw_strength.ordinal()]>>6);

            if (bzc > cnt) {
                cnt = bzc;
            }
        }

        if (cnt>0) {
            palette = (cnt+7)>>3;

            if (palette >= NUMREDPALS) {
                palette = NUMREDPALS-1;
            }

            palette += STARTREDPALS;
        } else if (plyr.bonuscount>0) {
            palette = (plyr.bonuscount+7)>>3;

            if (palette >= NUMBONUSPALS) {
                palette = NUMBONUSPALS-1;
            }

            palette += STARTBONUSPALS;
        } else if ( plyr.powers[pw_ironfeet.ordinal()] > 4*32
                  || (plyr.powers[pw_ironfeet.ordinal()]&8)>0) 
        {
            palette = RADIATIONPAL;
        } else {
            palette = 0;
        }

        if (palette != st_palette) {
            st_palette = palette;
            logger.log(Level.CONFIG, "Do_Palette_Stuff: Set Palette {0}\n", palette);
            //pal = (byte *) W_CacheLumpNum (lu_palette, PU_CACHE)+palette*768;
            VideoInterface.getInstance().I_SetPalette (game.wad.paletteList.get(palette)); //TODO pass this via Video and not VideoInterface.
        }

    }

    void ST_drawWidgets(boolean refresh) {
        int		i;

        // used by w_arms[] widgets
        st_armson = st_statusbaron && 0==game.deathmatch;

        // used by w_frags widget
        st_fragson = game.deathmatch>0 && st_statusbaron; 

        w_ready.STlib_updateNum(refresh);
        //STlib_updateNum(&w_ready, refresh);

        for (i=0;i<4;i++) {
            //STlib_updateNum(&w_ammo[i], refresh);
            w_ammo[i].STlib_updateNum(refresh);
            //STlib_updateNum(&w_maxammo[i], refresh);
            w_maxammo[i].STlib_updateNum(refresh);
        }

        //STlib_updatePercent(&w_health, refresh);
        w_health.STlib_updatePercent(refresh);
        //STlib_updatePercent(&w_armor, refresh);
        w_armor.STlib_updatePercent(refresh);

        //STlib_updateBinIcon(&w_armsbg, refresh);
        w_armsbg.STlib_updateBinIcon(refresh);

        for (i=0;i<6;i++) {
            //STlib_updateMultIcon(&w_arms[i], refresh);
            w_arms[i].STlib_updateMultIcon(refresh);
        }

        //STlib_updateMultIcon(&w_faces, refresh);
        w_faces.STlib_updateMultIcon(refresh);

        for (i=0;i<3;i++) {
            //STlib_updateMultIcon(&w_keyboxes[i], refresh);
            w_keyboxes[i].STlib_updateMultIcon(refresh);
        }

        //STlib_updateNum(&w_frags, refresh);
        w_frags.STlib_updateNum(refresh);

    }

    void ST_doRefresh() {

        st_firsttime = false;

        // draw status bar background to off-screen buff
        ST_refreshBackground();

        // and refresh all widgets
        ST_drawWidgets(true);

    }

    void ST_diffDraw() {
        // update all widgets
        ST_drawWidgets(false);
    }

    public void ST_Drawer (boolean fullscreen, boolean refresh) {

        //Defines.logger.config("ST_Drawer()\n");
        st_statusbaron = (!fullscreen) || Game.getInstance().autoMap.automapactive;
        st_firsttime = st_firsttime || refresh;

        // Do red-/gold-shifts from damage/items
        ST_doPaletteStuff();

        // If just after ST_Start(), refresh all
        if (st_firsttime) {
            //Defines.logger.config("ST_Drawer(): Refresh/First Time\n");
            ST_doRefresh();
        } // Otherwise, update as little as possible
        else {
            //Defines.logger.config("ST_Drawer(): Diff Draw\n");
            ST_diffDraw();
        }

    }

    public void ST_loadGraphics() {

        int		i;
        int		j;
        int		facenum;

        //char	namebuf[9];
        
        Wad wad = Game.getInstance().wad;

        // Load the numbers, tall and short
        for (i=0;i<10;i++) {
            //sprintf(namebuf, "STTNUM%d", i);
            //tallnum[i] = (patch_t *) W_CacheLumpName(namebuf, PU_STATIC);
            //tallnum[i] = wad.getPatchByName("STTNUM" + i);
            tallnum[i] = ((PictureLump)wad.findByName("STTNUM" + i)).pic;

            //sprintf(namebuf, "STYSNUM%d", i);
            //shortnum[i] = (patch_t *) W_CacheLumpName(namebuf, PU_STATIC);
            shortnum[i] = ((PictureLump)wad.findByName("STYSNUM" + i)).pic;
        }

        ST_TALLNUMWIDTH = tallnum[0].width;
    
        // Load percent key.
        //Note: why not load STMINUS here, too?
        //tallpercent = (patch_t *) W_CacheLumpName("STTPRCNT", PU_STATIC);
        tallpercent = ((PictureLump)wad.findByName("STTPRCNT")).pic;

        // key cards
        for (i=0;i<Defines.Card.values().length;i++) {
            //sprintf(namebuf, "STKEYS%d", i);
            //keys[i] = (patch_t *) W_CacheLumpName(namebuf, PU_STATIC);
            keys[i] = ((PictureLump)wad.findByName("STKEYS" + i)).pic;
        }

        // arms background
        //armsbg = (patch_t *) W_CacheLumpName("STARMS", PU_STATIC);
        armsbg = ((PictureLump)wad.findByName("STARMS")).pic;

        // arms ownership widgets
        for (i=0;i<6;i++) {
            //sprintf(namebuf, "STGNUM%d", i+2);

            // gray #
            //arms[i][0] = (patch_t *) W_CacheLumpName(namebuf, PU_STATIC);
            arms[i][0] = ((PictureLump)wad.findByName("STGNUM" + (i+2))).pic;

            // yellow #
            arms[i][1] = shortnum[i+2]; 
        }

        // face backgrounds for different color players
        //sprintf(namebuf, "STFB%d", consoleplayer);
        //faceback = (patch_t *) W_CacheLumpName(namebuf, PU_STATIC);
        faceback = ((PictureLump)wad.findByName("STFB" + Game.getInstance().consoleplayer)).pic;

        // status bar background bits
        //sbar = (patch_t *) W_CacheLumpName("STBAR", PU_STATIC);
        sbar = ((PictureLump)wad.findByName("STBAR")).pic;
        
        // face states
        facenum = 0;
        for (i=0;i<ST_NUMPAINFACES;i++) {
            for (j=0;j<ST_NUMSTRAIGHTFACES;j++) {
                //sprintf(namebuf, "STFST%d%d", i, j);
                //faces[facenum++] = W_CacheLumpName(namebuf, PU_STATIC);
                faces[facenum] = ((PictureLump)wad.findByName("STFST" + i + "" + j)).pic;
                facenum++;
            }
            
            //sprintf(namebuf, "STFTR%d0", i);	// turn right
            //faces[facenum++] = W_CacheLumpName(namebuf, PU_STATIC);
            faces[facenum] = ((PictureLump)wad.findByName("STFTR" + i + "0")).pic;
            facenum++;

            //sprintf(namebuf, "STFTL%d0", i);	// turn left
            //faces[facenum++] = W_CacheLumpName(namebuf, PU_STATIC);
            faces[facenum] = ((PictureLump)wad.findByName("STFTL" + i + "0")).pic;
            facenum++;
            
            //sprintf(namebuf, "STFOUCH%d", i);	// ouch!
            //faces[facenum++] = W_CacheLumpName(namebuf, PU_STATIC);
            faces[facenum] = ((PictureLump)wad.findByName("STFOUCH" + i )).pic;
            facenum++;
            
            //sprintf(namebuf, "STFEVL%d", i);	// evil grin ;)
            //faces[facenum++] = W_CacheLumpName(namebuf, PU_STATIC);
            faces[facenum] = ((PictureLump)wad.findByName("STFEVL" + i )).pic;
            facenum++;
            
            //sprintf(namebuf, "STFKILL%d", i);	// pissed off
            //faces[facenum++] = W_CacheLumpName(namebuf, PU_STATIC);
            faces[facenum] = ((PictureLump)wad.findByName("STFKILL" + i )).pic;
            facenum++;
        }
        
        //faces[facenum++] = W_CacheLumpName("STFGOD0", PU_STATIC);
        faces[facenum] = ((PictureLump)wad.findByName("STFGOD0")).pic;
        facenum++;
        
        //faces[facenum++] = W_CacheLumpName("STFDEAD0", PU_STATIC);
        faces[facenum] = ((PictureLump)wad.findByName("STFDEAD0")).pic;
        facenum++;

    }

    public void ST_loadData() {
        //lu_palette = W_GetNumForName ("PLAYPAL");
        ST_loadGraphics();
    }

    public void ST_unloadGraphics() {
        int i;

        // unload the numbers, tall and short
        for (i=0;i<10;i++) {
            //Z_ChangeTag(tallnum[i], PU_CACHE);
            tallnum[i] = null;
            //Z_ChangeTag(shortnum[i], PU_CACHE);
            shortnum[i] = null;
        }
        // unload tall percent
        //Z_ChangeTag(tallpercent, PU_CACHE);
        tallpercent = null;

        // unload arms background
        //Z_ChangeTag(armsbg, PU_CACHE); 
        armsbg = null;

        // unload gray #'s
        for (i=0;i<6;i++) {
            //Z_ChangeTag(arms[i][0], PU_CACHE);
            arms[i][0] = null;
        }

        // unload the key cards
        for (i=0;i<keys.length;i++) {
            //Z_ChangeTag(keys[i], PU_CACHE);
            keys[i] = null;
            
        }

        //Z_ChangeTag(sbar, PU_CACHE);
        sbar = null;
        //Z_ChangeTag(faceback, PU_CACHE);
        faceback = null;

        for (i=0;i<faces.length;i++) {
            //Z_ChangeTag(faces[i], PU_CACHE);
            faces[i] = null;
        }
            
            // Note: nobody ain't seen no unloading
            //   of stminus yet. Dude.


    }

    public void ST_unloadData() {
        ST_unloadGraphics();
    }

    public void ST_initData() {

        int		i;

        st_firsttime = true;
        plyr = Game.getInstance().players[Game.getInstance().consoleplayer];

        st_clock = 0;
        st_chatstate = StartChatState;
        st_gamestate = FirstPersonState;

        st_statusbaron = true;
        st_oldchat = false;
        st_chat = false;
        st_cursoron = false;

        st_faceindex = 0;
        st_palette = -1;

        st_oldhealth = -1;

        for (i=0;i<Defines.WeaponType.values().length;i++) {
            oldweaponsowned[i] = plyr.weaponowned[i];
        }

        for (i=0;i<3;i++) {
            keyboxes[i] = -1;
            
            //STlib_init();  Haha  OOP for the win!
        }

    }

    public void ST_createWidgets() {

        int i;

        // ready weapon ammo
        w_ready = new NumberWidget(
                      ST_AMMOX,
                      ST_AMMOY,
                      tallnum,
                      plyr.ammo[weaponinfo[plyr.readyweapon.ordinal()].ammo.ordinal()],
                      st_statusbaron,
                      ST_AMMOWIDTH );

        // the last weapon type
        w_ready.data = plyr.readyweapon; 

        // health percentage
        w_health = new PercentWidget(
                          ST_HEALTHX,
                          ST_HEALTHY,
                          tallnum,
                          plyr.health,
                          st_statusbaron,
                          tallpercent );

        // arms background
        w_armsbg = new BinaryIconWidget(
                          ST_ARMSBGX,
                          ST_ARMSBGY,
                          armsbg,
                          st_notdeathmatch,
                          st_statusbaron);

        // weapons owned
        for(i=0;i<6;i++) {
            w_arms[i] = new MultipleIconWidgetBool(
                ST_ARMSX+(i%3)*ST_ARMSXSPACE,
                ST_ARMSY+(i/3)*ST_ARMSYSPACE,
                arms[i], plyr.weaponowned[i+1],
                st_armson);
        }

        // frags sum
        w_frags = new NumberWidget(
                ST_FRAGSX,
                ST_FRAGSY,
                tallnum,
                st_fragscount,
                st_fragson,
                ST_FRAGSWIDTH );

        // faces
        w_faces = new MultipleIconWidget(
                ST_FACESX,
                ST_FACESY,
                faces,
                st_faceindex,
                st_statusbaron  );

        // armor percentage - should be colored later
        w_armor = new PercentWidget(
                          ST_ARMORX,
                          ST_ARMORY,
                          tallnum,
                          plyr.armorpoints,
                          st_statusbaron, tallpercent);

        // keyboxes 0-2
        w_keyboxes[0] = new MultipleIconWidget(
                           ST_KEY0X,
                           ST_KEY0Y,
                           keys,
                           keyboxes[0],
                           st_statusbaron);

        w_keyboxes[1] = new MultipleIconWidget(
                           ST_KEY1X,
                           ST_KEY1Y,
                           keys,
                           keyboxes[1],
                           st_statusbaron);

        w_keyboxes[2] = new MultipleIconWidget(
                           ST_KEY2X,
                           ST_KEY2Y,
                           keys,
                           keyboxes[2],
                           st_statusbaron);

        // ammo count (all four kinds)
        w_ammo[0] = new NumberWidget(
                      ST_AMMO0X,
                      ST_AMMO0Y,
                      shortnum,
                      plyr.ammo[0],
                      st_statusbaron,
                      ST_AMMO0WIDTH  );

        w_ammo[1] = new NumberWidget(
                      ST_AMMO1X,
                      ST_AMMO1Y,
                      shortnum,
                      plyr.ammo[1],
                      st_statusbaron,
                      ST_AMMO1WIDTH   );

        w_ammo[2] = new NumberWidget(
                      ST_AMMO2X,
                      ST_AMMO2Y,
                      shortnum,
                      plyr.ammo[2],
                      st_statusbaron,
                      ST_AMMO2WIDTH  );

        w_ammo[3] = new NumberWidget(
                      ST_AMMO3X,
                      ST_AMMO3Y,
                      shortnum,
                      plyr.ammo[3],
                      st_statusbaron,
                      ST_AMMO3WIDTH);

        // max ammo count (all four kinds)
        w_maxammo[0] = new NumberWidget(
                      ST_MAXAMMO0X,
                      ST_MAXAMMO0Y,
                      shortnum,
                      plyr.maxammo[0],
                      st_statusbaron,
                      ST_MAXAMMO0WIDTH);

        w_maxammo[1] = new NumberWidget(
                      ST_MAXAMMO1X,
                      ST_MAXAMMO1Y,
                      shortnum,
                      plyr.maxammo[1],
                      st_statusbaron,
                      ST_MAXAMMO1WIDTH);

        w_maxammo[2] = new NumberWidget(
                      ST_MAXAMMO2X,
                      ST_MAXAMMO2Y,
                      shortnum,
                      plyr.maxammo[2],
                      st_statusbaron,
                      ST_MAXAMMO2WIDTH);

        w_maxammo[3] = new NumberWidget(
                      ST_MAXAMMO3X,
                      ST_MAXAMMO3Y,
                      shortnum,
                      plyr.maxammo[3],
                      st_statusbaron,
                      ST_MAXAMMO3WIDTH);

    }

    public void ST_Start () {

        if (!st_stopped) {
            ST_Stop();
        }

        ST_initData();
        ST_createWidgets();
        st_stopped = false;
    }

    public void ST_Stop () {
        if (st_stopped) {
            return;
        }

        //VideoInterface.getInstance().I_SetPalette (lu_palette);
        // Reset palette
        VideoInterface.getInstance().I_SetPalette (game.wad.paletteList.get(0)); //TODO pass this via Video and not VideoInterface.

        st_stopped = true;
    }

   public void ST_Init () {
       
        veryfirsttime = 0;
        ST_loadData();
        //screens[4] = (byte *) Z_Malloc(ST_WIDTH*ST_HEIGHT, PU_STATIC, 0);
    }

}
