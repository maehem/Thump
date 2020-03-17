/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.global;

import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import thump.game.WeaponInfo;
import static thump.global.Defines.AmmoType.*;
import static thump.global.State.StateNum.*;

/**
 *
 * @author mark
 */
public class Defines {

    public static final Logger logger = Logger.getGlobal();
        
    /*
     *  DOOM Types
     */
//    public static final int MAXCHAR     = ((char) 0x7f);
//    public static final int MAXSHORT    = ((short) 0x7fff);

    // Max pos 32-bit int.
//    public static final int MAXINT      = 0x7fffffff;
//    public static final int MAXLONG     = ((long) 0x7fffffff);
//    public static final int MINCHAR     = ((char) 0x80);
//    public static final int MINSHORT    = ((short) 0x8000);

    // Max negative 32-bit integer.
//    public static final int MININT      = ((int) 0x80000000);
//    public static final int MINLONG     = ((long) 0x80000000);

    //
    // File locations and names, relative to current position.
    // Path names are OS-sensitive.
    //
    public static final String SAVEGAMENAME="doomsav";
    public static final String DEVMAPS = "devmaps";
    public static final String DEVDATA = "devdata";

    //
    // Global parameters/defines.
    //
    // DOOM version
    public static final int VERSION =  110;


    // Game mode handling - identify IWAD version
    //  to handle IWAD dependend animations etc.
    public enum GameMode {
      SHAREWARE,	// DOOM 1 shareware, E1, M9
      REGISTERED,	// DOOM 1 registered, E3, M27
      COMMERCIAL,	// DOOM 2 retail, E1 M34
      // DOOM 2 german edition not handled
      RETAIL,	// DOOM 1 retail, E4, M36
      INDETERMINED	// Well, no IWAD found. 
    }


    // Mission packs - might be useful for TC stuff?
    public enum GameMission {
      doom,		// DOOM 1
      doom2,	// DOOM 2
      pack_tnt,	// TNT mission pack
      pack_plut,	// Plutonia pack
      none
    }


    // Identify language to use, software localization.
    public enum Language {
      english,
      french,
      german,
      unknown
    }


    // If rangecheck is undefined,
    // most parameter validation debugging code will not be compiled
    public final static boolean RANGECHECK = true;

    // Do or do not use external soundserver.
    // The sndserver binary to be run separately
    //  has been introduced by Dave Taylor.
    // The integrated sound support is experimental,
    //  and unfinished. Default is synchronous.
    // Experimental asynchronous timer based is
    //  handled by SNDINTR. 
    public final static boolean SNDSERV   = false;
    //#define SNDINTR  1


    // This one switches between MIT SHM (no proper mouse)
    // and XFree86 DGA (mickey sampling). The original
    // linuxdoom used SHM, which is default.
    //#define X11_DGA		1


    //
    // For resize of screen, at start of game.
    // It will not work dynamically, see visplanes.
    //
    public final static int	BASE_WIDTH	=	320;

    // It is educational but futile to change this
    //  scaling e.g. to 2. Drawing of status bar,
    //  menues etc. is tied to the scale implied
    //  by the graphics.
    public final static int	SCREEN_MUL	=	1;
    public final static double INV_ASPECT_RATIO	= 0.625; // 0.75, ideally

    // Defines suck. C sucks.
    // C++ might sucks for OOP, but it sure is a better C.
    // So there.
    public final static int	SCREENWIDTH  = 320;
    //SCREEN_MUL*BASE_WIDTH //320
    public final static int	SCREENHEIGHT = 200;
    //(int)(SCREEN_MUL*BASE_WIDTH*INV_ASPECT_RATIO) //200




    // The maximum number of players, multiplayer/networking.
    public final static int	MAXPLAYERS	= 4;

    // State updates, number of tics / second.
    public final static int	TICRATE		= 35;

    // The current state of the game: whether we are
    // playing, gazing at the intermission screen,
    // the game final animation, or a demo. 
    public enum GameState {
        GS_NONE,
        GS_LEVEL,
        GS_INTERMISSION,
        GS_FINALE,
        GS_DEMOSCREEN
    }

    //
    // Difficulty/skill settings/filters.
    //

    // Skill flags.
    public final static int	MTF_EASY		=1;
    public final static int	MTF_NORMAL		=2;
    public final static int	MTF_HARD		=4;

    // Deaf monsters/do not react to sound.
    public final static int	MTF_AMBUSH		=8;

    public enum Skill {
        sk_test(0),
        sk_baby(1),
        sk_easy(2),
        sk_medium(3),
        sk_hard(4),
        sk_nightmare(5);
        
        private final int value;
        
        private Skill(int value) {
                this.value = value;
        }
        
        public int getValue() {
            return value;
        }
        
        public static Skill skill(int val) {
            switch (val) {
                case 1: return sk_baby;
                case 2: return sk_easy;
                case 3: return sk_medium;
                case 4: return sk_hard;
                case 5: return sk_nightmare;
                default: return sk_test;
            }
        }
    }

    //
    // Key cards.
    //
    public enum Card {
        it_bluecard,
        it_yellowcard,
        it_redcard,
        it_blueskull,
        it_yellowskull,
        it_redskull,
    }

    //  The defined weapons,
    //  including a marker indicating
    //  user has not changed weapon.
    public enum WeaponType {
        wp_fist,
        wp_pistol,
        wp_shotgun,
        wp_chaingun,
        wp_missile,
        wp_plasma,
        wp_bfg,
        wp_chainsaw,
        wp_supershotgun,


        
        wp_nochange   // No pending weapon change.
    }

    // Ammunition types defined.
    public enum AmmoType {
        am_clip,	// Pistol / chaingun ammo.
        am_shell,	// Shotgun / double barreled shotgun.
        am_cell,	// Plasma rifle, BFG.
        am_misl,	// Missile launcher.
        am_noammo	// Unlimited for chainsaw / fist.	
    }


    public static final WeaponInfo	weaponinfo[] =
    {   
        // fist
        new WeaponInfo(
            am_noammo,
            S_PUNCHUP,
            S_PUNCHDOWN,
            S_PUNCH,
            S_PUNCH1,
            S_NULL
        ),

        // pistol
        new WeaponInfo(
            am_clip,
            S_PISTOLUP,
            S_PISTOLDOWN,
            S_PISTOL,
            S_PISTOL1,
            S_PISTOLFLASH
        ),

        // shotgun
        new WeaponInfo(
            am_shell,
            S_SGUNUP,
            S_SGUNDOWN,
            S_SGUN,
            S_SGUN1,
            S_SGUNFLASH1
        ),

        // chaingun
        new WeaponInfo(
            am_clip,
            S_CHAINUP,
            S_CHAINDOWN,
            S_CHAIN,
            S_CHAIN1,
            S_CHAINFLASH1
        ),

        // missile launcher
        new WeaponInfo(
            am_misl,
            S_MISSILEUP,
            S_MISSILEDOWN,
            S_MISSILE,
            S_MISSILE1,
            S_MISSILEFLASH1
        ),

        // plasma rifle
        new WeaponInfo(
            am_cell,
            S_PLASMAUP,
            S_PLASMADOWN,
            S_PLASMA,
            S_PLASMA1,
            S_PLASMAFLASH1
        ),

        // bfg 9000
        new WeaponInfo(
            am_cell,
            S_BFGUP,
            S_BFGDOWN,
            S_BFG,
            S_BFG1,
            S_BFGFLASH1
        ),

        // chainsaw
        new WeaponInfo(
            am_noammo,
            S_SAWUP,
            S_SAWDOWN,
            S_SAW,
            S_SAW1,
            S_NULL
        ),

        // super shotgun
        new WeaponInfo(
            am_shell,
            S_DSGUNUP,
            S_DSGUNDOWN,
            S_DSGUN,
            S_DSGUN1,
            S_DSGUNFLASH1
        )	
    };

    // Power up artifacts.
    public enum PowerType {
        pw_invulnerability,
        pw_strength,
        pw_invisibility,
        pw_ironfeet,
        pw_allmap,
        pw_infrared,
    }

    //  Power up durations,
    //  how many seconds till expiration,
    //  assuming TICRATE is 35 ticks/second.
    //
    public enum PowerDuration {
        INVULNTICS(30*TICRATE),
        INVISTICS(60*TICRATE),
        INFRATICS(120*TICRATE),
        IRONTICS(60*TICRATE);
        
        private final int value;

        private PowerDuration(int value) {
                this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }

    // DOOM keyboard definition.
    // This is the stuff configured by Setup.Exe.
    // Most key data are simple ascii (uppercased).
    //
    public final static char KEY_RIGHTARROW     = KeyEvent.VK_RIGHT; // =0xae;  //174
    public final static char KEY_LEFTARROW      = KeyEvent.VK_LEFT; // =0xac;  //172
    public final static char KEY_UPARROW        = KeyEvent.VK_UP; // =0xad;  //173
    public final static char KEY_DOWNARROW      = KeyEvent.VK_DOWN; // =0xaf;  //175
    public final static char KEY_ESCAPE         = KeyEvent.VK_ESCAPE;
    public final static char KEY_ENTER          = KeyEvent.VK_ENTER;
    public final static char KEY_TAB            = KeyEvent.VK_TAB;
    public final static char KEY_F1		= KeyEvent.VK_F1;
    public final static char KEY_F2		= KeyEvent.VK_F2;
    public final static char KEY_F3		= KeyEvent.VK_F3;
    public final static char KEY_F4		= KeyEvent.VK_F4;
    public final static char KEY_F5		= KeyEvent.VK_F5;
    public final static char KEY_F6		= KeyEvent.VK_F6;
    public final static char KEY_F7		= KeyEvent.VK_F7;
    public final static char KEY_F8		= KeyEvent.VK_F8;
    public final static char KEY_F9		= KeyEvent.VK_F9;
    public final static char KEY_F10		= KeyEvent.VK_F10;
    public final static char KEY_F11		= KeyEvent.VK_F11;
    public final static char KEY_F12		= KeyEvent.VK_F12;


    public final static char KEY_BACKSPACE	=KeyEvent.VK_BACK_SPACE;
    public final static char KEY_PAUSE          =KeyEvent.VK_PAUSE;

    public final static char KEY_EQUALS         =KeyEvent.VK_EQUALS;
    public final static char KEY_MINUS          =KeyEvent.VK_MINUS;

    public final static char KEY_RSHIFT         =KeyEvent.VK_SHIFT;
    public final static char KEY_RCTRL          =KeyEvent.VK_CONTROL;
    public final static char KEY_RALT           =KeyEvent.VK_ALT;

    public final static char KEY_LALT           =KeyEvent.VK_META;  // Apple Key


}
