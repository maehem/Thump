/*
 * Event
 */
package thump.game;

/**
 *
 * @author mark
 */
public class Event {
    public static final int MAXEVENTS = 64;

    public Event(int data1, int data2, int data3 ) {
        this.data1 = data1;
        this.data2 = data2;
        this.data3 = data3;
    }
    
    public Event(EventType type, int data1 ) {
        this.type = type;
        this.data1 = data1;
        
    }
    
    public EventType	type;
    public int		data1;		// keys / mouse/joystick buttons
    public int		data2;		// mouse/joystick x move
    public int		data3;		// mouse/joystick y move

    public enum EventType {
        ev_keydown,
        ev_keyup,
        ev_mouse,
        ev_joystick
    }

    public enum GameAction {
        ga_nothing,
        ga_loadlevel,
        ga_newgame,
        ga_loadgame,
        ga_savegame,
        ga_playdemo,
        ga_completed,
        ga_victory,
        ga_worlddone,
        ga_screenshot
    }


    //
    // Button/action code definitions.
    //
    public static final int BT_ATTACK       = 1;            // Press "Fire".
    public static final int BT_USE          = 2;            // Use button, to open doors, activate switches.
    public static final int BT_SPECIAL      = 128;          // Flag: game events, not really buttons.
    public static final int BT_SPECIALMASK  = 3;
    public static final int BT_CHANGE       = 4;            // Flag, weapon change pending. If true, the next 3 bits hold weapon num.
    public static final int BT_WEAPONMASK   = 8 + 16 + 32;  // The 3bit weapon mask and shift, convenience.
    public static final int BT_WEAPONSHIFT  = 3;
    public static final int BTS_PAUSE       = 1;            // Pause the game.
    public static final int BTS_SAVEGAME    = 2;            // Save the game at each console.
    public static final int BTS_SAVEMASK    = 4 + 8 + 16;   // Savegame slot numbers
    public static final int BTS_SAVESHIFT   = 2;            //  occupy the second byte of buttons.    


 
//    public enum ButtonCode {
//        // Press "Fire".
//        BT_ATTACK(1;
//        // Use button, to open doors, activate switches.
//        BT_USE(2),
//
//        // Flag: game events, not really buttons.
//        BT_SPECIAL(128),
//        BT_SPECIALMASK(3),
//
//        // Flag, weapon change pending.
//        // If true, the next 3 bits hold weapon num.
//        BT_CHANGE(4),
//        // The 3bit weapon mask and shift, convenience.
//        BT_WEAPONMASK(8+16+32),
//        BT_WEAPONSHIFT(3),
//
//        // Pause the game.
//        BTS_PAUSE(1),
//        // Save the game at each console.
//        BTS_SAVEGAME(2),
//
//        // Savegame slot numbers
//        //  occupy the second byte of buttons.    
//        BTS_SAVEMASK(4+8+16),
//        BTS_SAVESHIFT(2);
//
//        private final int value;
//        
//        private ButtonCode(int value) {
//            this.value = value;
//        }
//        
//        public int getValue() {
//            return value;
//        }
//    }

    
}
