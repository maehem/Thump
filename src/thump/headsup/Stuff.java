/*
 * Head Up Stuff  --  Doom C - hu_stuff.c
 */
package thump.headsup;

import java.util.ResourceBundle;
import java.util.logging.Level;
import thump.game.Event;
import static thump.game.Event.EventType.ev_keydown;
import thump.game.Game;
import thump.game.Player;
import thump.global.Defines;
import static thump.global.Defines.KEY_ENTER;
import static thump.global.Defines.KEY_ESCAPE;
import static thump.global.Defines.KEY_LALT;
import static thump.global.Defines.KEY_RALT;
import static thump.global.Defines.KEY_RSHIFT;
import static thump.global.Defines.MAXPLAYERS;
import static thump.global.Defines.logger;
import static thump.headsup.HUlib.HU_MAXLINELENGTH;
import static thump.headsup.HUlib.HUlib_initSText;
import static thump.headsup.HUlib.HUlib_initTextLine;
import thump.menu.MenuManager;
import thump.render.Patch;
import thump.wad.lump.PictureLump;

/**
 *
 * @author mark
 */
public class Stuff {  // TODO rename to HeadUp
    //
    // Globally visible constants.
    //
    public static final byte HU_FONTSTART   = '!';	// the first font characters
    public static final byte HU_FONTEND     = '_';	// the last font characters

    // Calculate # of glyphs in font.
    public static final int HU_FONTSIZE    = (HU_FONTEND - HU_FONTSTART + 1);	

    public static final int  HU_BROADCAST    = 5;

    public static final char HU_MSGREFRESH  = Defines.KEY_ENTER;
    public static final int  HU_MSGX         = 0;
    public static final int  HU_MSGY         = 0;
    public static final int  HU_MSGWIDTH     = 64;	// in characters
    public static final int  HU_MSGHEIGHT    = 1;	// in lines

    public static final int  HU_MSGTIMEOUT   = (4*Defines.TICRATE);


    //
    // Locally used constants, shortcuts.
    //
    public String HU_TITLE ="";  // Set up in HU_Init
    public String HU_TITLE2="";
    public String HU_TITLEP="";
    public String HU_TITLET="";

    public static final int HU_TITLEHEIGHT = 1;
    public static final int HU_TITLEX = 0;
    public int HU_TITLEY; //= (167 - hu_font[0].height);

    public static final char HU_INPUTTOGGLE = 't';
    public static final int HU_INPUTX=HU_MSGX;
    public int HU_INPUTY; // =(HU_MSGY + HU_MSGHEIGHT*(hu_font[0].height) +1);
    public static final int HU_INPUTWIDTH=64;
    public static final int HU_INPUTHEIGHT=1;

    //private char	chat_char; // remove later.
    private Player	plr;
    public Patch	hu_font[] = new Patch[HU_FONTSIZE];
    public hu_textline	w_title = new hu_textline();
    public boolean	chat_on;
    public hu_itext	w_chat = new hu_itext();
    public boolean	always_off = false;
    public int		chat_dest[] = new int[MAXPLAYERS];
    public hu_itext     w_inputbuffer[] = new hu_itext[MAXPLAYERS];

    public boolean	message_on = false;
    public boolean	message_dontfuckwithme = false;
    public boolean	message_nottobefuckedwith = false;

    public hu_stext	w_message = new hu_stext();
    public int		message_counter = 0;

    ////extern int		showMessages;
    ////extern boolean		automapactive;

    public boolean	headsupactive = false;


    //private static Stuff instance = null;
    private final Game game;
    
    public Stuff( Game game ) {
        this.game = game;
}

//    public static Stuff getInstance() {
//        if ( instance == null ) {
//            instance = new Stuff();
//        }
//        return instance;
//    }
    
    

    public String	chat_macros[] =
    {
        Game.getMessage("HUSTR_CHATMACRO0"),
        Game.getMessage("HUSTR_CHATMACRO1"),
        Game.getMessage("HUSTR_CHATMACRO2"),
        Game.getMessage("HUSTR_CHATMACRO3"),
        Game.getMessage("HUSTR_CHATMACRO4"),
        Game.getMessage("HUSTR_CHATMACRO5"),
        Game.getMessage("HUSTR_CHATMACRO6"),
        Game.getMessage("HUSTR_CHATMACRO7"),
        Game.getMessage("HUSTR_CHATMACRO8"),
        Game.getMessage("HUSTR_CHATMACRO9")
    };


    public String	player_names[] =
    {
        Game.getMessage("HUSTR_PLRGREEN"),
        Game.getMessage("HUSTR_PLRINDIGO"),
        Game.getMessage("HUSTR_PLRBROWN"),
        Game.getMessage("HUSTR_PLRRED")
    };

    //
    // Builtin map names.
    // The actual names can be found in DStrings.h.
    //

    public String	mapnames[] =	// DOOM shareware/registered/retail (Ultimate) names.
    {
        Game.getMessage("HUSTR_E1M1"),
        Game.getMessage("HUSTR_E1M2"),
        Game.getMessage("HUSTR_E1M3"),
        Game.getMessage("HUSTR_E1M4"),
        Game.getMessage("HUSTR_E1M5"),
        Game.getMessage("HUSTR_E1M6"),
        Game.getMessage("HUSTR_E1M7"),
        Game.getMessage("HUSTR_E1M8"),
        Game.getMessage("HUSTR_E1M9"),

        Game.getMessage("HUSTR_E2M1"),
        Game.getMessage("HUSTR_E2M2"),
        Game.getMessage("HUSTR_E2M3"),
        Game.getMessage("HUSTR_E2M4"),
        Game.getMessage("HUSTR_E2M5"),
        Game.getMessage("HUSTR_E2M6"),
        Game.getMessage("HUSTR_E2M7"),
        Game.getMessage("HUSTR_E2M8"),
        Game.getMessage("HUSTR_E2M9"),

        Game.getMessage("HUSTR_E3M1"),
        Game.getMessage("HUSTR_E3M2"),
        Game.getMessage("HUSTR_E3M3"),
        Game.getMessage("HUSTR_E3M4"),
        Game.getMessage("HUSTR_E3M5"),
        Game.getMessage("HUSTR_E3M6"),
        Game.getMessage("HUSTR_E3M7"),
        Game.getMessage("HUSTR_E3M8"),
        Game.getMessage("HUSTR_E3M9"),

        Game.getMessage("HUSTR_E4M1"),
        Game.getMessage("HUSTR_E4M2"),
        Game.getMessage("HUSTR_E4M3"),
        Game.getMessage("HUSTR_E4M4"),
        Game.getMessage("HUSTR_E4M5"),
        Game.getMessage("HUSTR_E4M6"),
        Game.getMessage("HUSTR_E4M7"),
        Game.getMessage("HUSTR_E4M8"),
        Game.getMessage("HUSTR_E4M9"),

        "NEWLEVEL",
        "NEWLEVEL",
        "NEWLEVEL",
        "NEWLEVEL",
        "NEWLEVEL",
        "NEWLEVEL",
        "NEWLEVEL",
        "NEWLEVEL",
        "NEWLEVEL"
    };

    public String	mapnames2[] =	// DOOM 2 map names.
    {
        Game.getMessage("HUSTR_1"),
        Game.getMessage("HUSTR_2"),
        Game.getMessage("HUSTR_3"),
        Game.getMessage("HUSTR_4"),
        Game.getMessage("HUSTR_5"),
        Game.getMessage("HUSTR_6"),
        Game.getMessage("HUSTR_7"),
        Game.getMessage("HUSTR_8"),
        Game.getMessage("HUSTR_9"),
        Game.getMessage("HUSTR_10"),
        Game.getMessage("HUSTR_11"),

        Game.getMessage("HUSTR_12"),
        Game.getMessage("HUSTR_13"),
        Game.getMessage("HUSTR_14"),
        Game.getMessage("HUSTR_15"),
        Game.getMessage("HUSTR_16"),
        Game.getMessage("HUSTR_17"),
        Game.getMessage("HUSTR_18"),
        Game.getMessage("HUSTR_19"),
        Game.getMessage("HUSTR_20"),

        Game.getMessage("HUSTR_21"),
        Game.getMessage("HUSTR_22"),
        Game.getMessage("HUSTR_23"),
        Game.getMessage("HUSTR_24"),
        Game.getMessage("HUSTR_25"),
        Game.getMessage("HUSTR_26"),
        Game.getMessage("HUSTR_27"),
        Game.getMessage("HUSTR_28"),
        Game.getMessage("HUSTR_29"),
        Game.getMessage("HUSTR_30"),
        Game.getMessage("HUSTR_31"),
        Game.getMessage("HUSTR_32")
    };

    public String	mapnamesp[] =	// Plutonia WAD map names.
    {
        Game.getMessage("PHUSTR_1"),
        Game.getMessage("PHUSTR_2"),
        Game.getMessage("PHUSTR_3"),
        Game.getMessage("PHUSTR_4"),
        Game.getMessage("PHUSTR_5"),
        Game.getMessage("PHUSTR_6"),
        Game.getMessage("PHUSTR_7"),
        Game.getMessage("PHUSTR_8"),
        Game.getMessage("PHUSTR_9"),
        Game.getMessage("PHUSTR_10"),
        Game.getMessage("PHUSTR_11"),

        Game.getMessage("PHUSTR_12"),
        Game.getMessage("PHUSTR_13"),
        Game.getMessage("PHUSTR_14"),
        Game.getMessage("PHUSTR_15"),
        Game.getMessage("PHUSTR_16"),
        Game.getMessage("PHUSTR_17"),
        Game.getMessage("PHUSTR_18"),
        Game.getMessage("PHUSTR_19"),
        Game.getMessage("PHUSTR_20"),

        Game.getMessage("PHUSTR_21"),
        Game.getMessage("PHUSTR_22"),
        Game.getMessage("PHUSTR_23"),
        Game.getMessage("PHUSTR_24"),
        Game.getMessage("PHUSTR_25"),
        Game.getMessage("PHUSTR_26"),
        Game.getMessage("PHUSTR_27"),
        Game.getMessage("PHUSTR_28"),
        Game.getMessage("PHUSTR_29"),
        Game.getMessage("PHUSTR_30"),
        Game.getMessage("PHUSTR_31"),
        Game.getMessage("PHUSTR_32")
    };

    public String mapnamest[] =	// TNT WAD map names.
    {
        Game.getMessage("THUSTR_1"),
        Game.getMessage("THUSTR_2"),
        Game.getMessage("THUSTR_3"),
        Game.getMessage("THUSTR_4"),
        Game.getMessage("THUSTR_5"),
        Game.getMessage("THUSTR_6"),
        Game.getMessage("THUSTR_7"),
        Game.getMessage("THUSTR_8"),
        Game.getMessage("THUSTR_9"),
        Game.getMessage("THUSTR_10"),
        Game.getMessage("THUSTR_11"),

        Game.getMessage("THUSTR_12"),
        Game.getMessage("THUSTR_13"),
        Game.getMessage("THUSTR_14"),
        Game.getMessage("THUSTR_15"),
        Game.getMessage("THUSTR_16"),
        Game.getMessage("THUSTR_17"),
        Game.getMessage("THUSTR_18"),
        Game.getMessage("THUSTR_19"),
        Game.getMessage("THUSTR_20"),

        Game.getMessage("THUSTR_21"),
        Game.getMessage("THUSTR_22"),
        Game.getMessage("THUSTR_23"),
        Game.getMessage("THUSTR_24"),
        Game.getMessage("THUSTR_25"),
        Game.getMessage("THUSTR_26"),
        Game.getMessage("THUSTR_27"),
        Game.getMessage("THUSTR_28"),
        Game.getMessage("THUSTR_29"),
        Game.getMessage("THUSTR_30"),
        Game.getMessage("THUSTR_31"),
        Game.getMessage("THUSTR_32")
    };


    public int[]	shiftxform;

    public final int french_shiftxform[] =
    {
        0,
        1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
        11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
        21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
        31,
        ' ', '!', '"', '#', '$', '%', '&',
        '"', // shift-'
        '(', ')', '*', '+',
        '?', // shift-,
        '_', // shift--
        '>', // shift-.
        '?', // shift-/
        '0', // shift-0
        '1', // shift-1
        '2', // shift-2
        '3', // shift-3
        '4', // shift-4
        '5', // shift-5
        '6', // shift-6
        '7', // shift-7
        '8', // shift-8
        '9', // shift-9
        '/',
        '.', // shift-;
        '<',
        '+', // shift-=
        '>', '?', '@',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
        'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        '[', // shift-[
        '!', // shift-backslash - OH MY GOD DOES WATCOM SUCK
        ']', // shift-]
        '"', '_',
        '\'', // shift-`
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
        'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        '{', '|', '}', '~', 127

    };

    public final int english_shiftxform[] =
    {

        0,
        1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
        11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
        21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
        31,
        ' ', '!', '"', '#', '$', '%', '&',
        '"', // shift-'
        '(', ')', '*', '+',
        '<', // shift-,
        '_', // shift--
        '>', // shift-.
        '?', // shift-/
        ')', // shift-0
        '!', // shift-1
        '@', // shift-2
        '#', // shift-3
        '$', // shift-4
        '%', // shift-5
        '^', // shift-6
        '&', // shift-7
        '*', // shift-8
        '(', // shift-9
        ':',
        ':', // shift-;
        '<',
        '+', // shift-=
        '>', '?', '@',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
        'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        '[', // shift-[
        '!', // shift-backslash - OH MY GOD DOES WATCOM SUCK
        ']', // shift-]
        '"', '_',
        '\'', // shift-`
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
        'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        '{', '|', '}', '~', 127
    };

    public int frenchKeyMap[]=
    {
        0,
        1,2,3,4,5,6,7,8,9,10,
        11,12,13,14,15,16,17,18,19,20,
        21,22,23,24,25,26,27,28,29,30,
        31,
        ' ','!','"','#','$','%','&','%','(',')','*','+',';','-',':','!',
        '0','1','2','3','4','5','6','7','8','9',':','M','<','=','>','?',
        '@','Q','B','C','D','E','F','G','H','I','J','K','L',',','N','O',
        'P','A','R','S','T','U','V','Z','X','Y','W','^','\\','$','^','_',
        '@','Q','B','C','D','E','F','G','H','I','J','K','L',',','N','O',
        'P','A','R','S','T','U','V','Z','X','Y','W','^','\\','$','^',127
    };

    public int ForeignTranslation(int ch) {
        return ch < 128 ? frenchKeyMap[ch] : ch;
    }

    public void HU_Init() {

        //char	buffer[] = new char[9];

        if ( game.language.equals("fr")) {
            //if (french)
            shiftxform = french_shiftxform;
        } else {
            shiftxform = english_shiftxform;
        }

        // load the heads-up font
        int j = HU_FONTSTART;
        for (int i=0;i<HU_FONTSIZE;i++) {
            String patchNum = "00" + j; // Pad the short values
            String patch = "STCFN" + patchNum.substring(patchNum.length()-3);
            hu_font[i] = ((PictureLump)game.wad.findByName(patch)).pic;
            
            if ( hu_font[i] == null ) {
                Defines.logger.log(Level.SEVERE, "HU_Init: couldn''t find font for: {0}\n", patch);
            }
            j++;
            //	sprintf(buffer, "STCFN%.3d", j++);
            //	hu_font[i] = (patch_t *) W_CacheLumpName(buffer, PU_STATIC);
        }
        int ep = game.gameepisode-1;
        int gamemap = game.gamemap-1;
        int mapindex = ep*9+gamemap;
        HU_TITLE =(mapnames[mapindex]);
        HU_TITLE2=(mapnames2[game.gamemap-1]);
        HU_TITLEP=(mapnamesp[game.gamemap-1]);
        HU_TITLET=(mapnamest[game.gamemap-1]);
        HU_TITLEY = (167 - hu_font[0].height);
        HU_INPUTY=(HU_MSGY + HU_MSGHEIGHT*(hu_font[0].height) +1);
    }

    public void HU_Stop()
    {
        headsupactive = false;
    }

    public void HU_Start(){

        String	s;
        //Stats stats = Stats.getInstance();

        if (headsupactive) {
            HU_Stop();
        }

        plr = game.players[game.consoleplayer];
        message_on = false;
        message_dontfuckwithme = false;
        message_nottobefuckedwith = false;
        chat_on = false;

        // create the message widget
        HUlib_initSText(w_message,
                        HU_MSGX, HU_MSGY, HU_MSGHEIGHT,
                        hu_font,
                        HU_FONTSTART, message_on);

        // create the map title widget
        HUlib_initTextLine(w_title,
                           HU_TITLEX, HU_TITLEY,
                           hu_font,
                           HU_FONTSTART);

        switch ( game.gameMode ) {
          case SHAREWARE:
          case REGISTERED:
          case RETAIL:
            s = HU_TITLE;
            break;

            // FIXME
            //      case pack_plut:
            //	s = HU_TITLEP;
            //	break;
            //      case pack_tnt:
            //	s = HU_TITLET;
            //	break;



          case COMMERCIAL:
          default:
             s = HU_TITLE2;
             break;
        }

    //    while (*s)
    //	HUlib_addCharToTextLine(&w_title, *(s++));
    //
    //    // create the chat widget
    //    HUlib_initIText(&w_chat,
    //		    HU_INPUTX, HU_INPUTY,
    //		    hu_font,
    //		    HU_FONTSTART, &chat_on);
    //
    //    // create the inputbuffer widgets
    //    for (int i=0 ; i<MAXPLAYERS ; i++)
    //	HUlib_initIText(&w_inputbuffer[i], 0, 0, 0, 0, &always_off);
    //
        headsupactive = true;

    }


    public void HU_Drawer() {

        logger.log(Level.CONFIG, "HU_Drawer()\n");
        
        HUlib.HUlib_drawSText(w_message);
        HUlib.HUlib_drawIText(w_chat);
        if (game.autoMap.automapactive) {
            HUlib.HUlib_drawTextLine(w_title, false);
        }

    }

    public void HU_Erase() {

        logger.log(Level.CONFIG, "HU_Erase()\n");

        HUlib.HUlib_eraseSText(w_message);
        HUlib.HUlib_eraseIText(w_chat);
        HUlib.HUlib_eraseTextLine(w_title);

    }

    public void HU_Ticker() {

        int i;
        boolean rc;
        int c;

        // tick down message counter if message is up
        if (message_counter>0) {
            message_counter--;
            if (message_counter==0) {
                message_on = false;
                message_nottobefuckedwith = false;
            }
        }

        if (MenuManager.getInstance().showMessages || message_dontfuckwithme) {

            // display message if necessary
            if ( (plr.message!=null && message_nottobefuckedwith)
                  || ( plr.message!=null && message_dontfuckwithme)
            ) {
                HUlib.HUlib_addMessageToSText(w_message, null, plr.message.getBytes());
                plr.message = null;
                message_on = true;
                message_counter = HU_MSGTIMEOUT;
                message_nottobefuckedwith = message_dontfuckwithme;
                message_dontfuckwithme = false;
            }

        } // else message_on = false;

        Game game = Game.getInstance();
        // check for incoming chat characters
        if (game.netgame) {
            for (i=0 ; i<MAXPLAYERS; i++) {
                if (!game.playeringame[i]) {
                    continue;
                }
                if (i != game.consoleplayer && (game.players[i].cmd.chatchar)>0) {
                    c = game.players[i].cmd.chatchar;
                    if (c <= HU_BROADCAST) {
                        chat_dest[i] = c;
                    } else {
                        if (c >= 'a' && c <= 'z') {
                            c = shiftxform[c];
                        }
                        rc = HUlib.HUlib_keyInIText(w_inputbuffer[i], c);
                        if (rc && c == KEY_ENTER) {
                            if (w_inputbuffer[i].l.len>0
                                && (   chat_dest[i] == (game.consoleplayer+1) 
                                    || chat_dest[i] == HU_BROADCAST           )
                                ) {
                                HUlib.HUlib_addMessageToSText(w_message,
                                                        player_names[i].getBytes(),
                                                        w_inputbuffer[i].l.l);

                                message_nottobefuckedwith = true;
                                message_on = true;
                                message_counter = HU_MSGTIMEOUT;
                                if ( game.gameMode == Defines.GameMode.COMMERCIAL ) {
//                                  S_StartSound(0, sfx_radio);
                                } else {
//                                  S_StartSound(0, sfx_tink);
                                }
                            }
                            HUlib.HUlib_resetIText(w_inputbuffer[i]);
                        }
                    }
                    game.players[i].cmd.chatchar = 0;
                }
            }
        }

    }

    public static final int QUEUESIZE = 128;

    int	chatchars[] = new int[QUEUESIZE];
    static int	head = 0;
    static int	tail = 0;


    public void HU_queueChatChar(int c) {
        if (((head + 1) & (QUEUESIZE-1)) == tail)
        {
            plr.message = Game.getMessage("HUSTR_MSGU");
        }
        else
        {
            chatchars[head] = c;
            head = (head + 1) & (QUEUESIZE-1);
        }
    }

   public int HU_dequeueChatChar() {
        int c;

        if (head != tail) {
            c = chatchars[tail];
            tail = (tail + 1) & (QUEUESIZE-1);
        } else {
            c = 0;
        }

        return c;
    }

    byte	lastmessage[] = new byte[HU_MAXLINELENGTH+1];
    boolean	shiftdown = false;
    boolean	altdown = false;
    String	destination_keys[]=
        {
            Game.getMessage("HUSTR_KEYGREEN"),
            Game.getMessage("HUSTR_KEYINDIGO"),
            Game.getMessage("HUSTR_KEYBROWN"),
            Game.getMessage("HUSTR_KEYRED")
        };

    int		num_nobrainers = 0;

    public boolean HU_Responder(Event ev) {

        char[]		macromessage;
        boolean		eatkey = false;
        int             c;
        int		i;
        int		numplayers;

        ResourceBundle messages = game.messages;
        Game game = Game.getInstance();
        
        numplayers = 0;
        for (i=0 ; i<MAXPLAYERS ; i++) {
            if (game.playeringame[i]) {
                numplayers++;
            }
        }

        if (ev.data1 == KEY_RSHIFT){
            shiftdown = ev.type == ev_keydown;
            return false;
        } else if (ev.data1 == KEY_RALT || ev.data1 == KEY_LALT) {
            altdown = ev.type == ev_keydown;
            return false;
        }

        if (ev.type != ev_keydown) {
            return false;
        }

        if (!chat_on) {
            if (ev.data1 == HU_MSGREFRESH) {
                message_on = true;
                message_counter = HU_MSGTIMEOUT;
                eatkey = true;
            } else if (game.netgame && ev.data1 == HU_INPUTTOGGLE) {
                eatkey = true;
                chat_on = true;
                HUlib.HUlib_resetIText(w_chat);
                game.headUp.HU_queueChatChar((char)HU_BROADCAST);
            } else if (game.netgame && numplayers > 2) {
                for (i=0; i<MAXPLAYERS ; i++) {
                    if (ev.data1 == destination_keys[i].charAt(0)) {
                        if (game.playeringame[i] && i!=game.consoleplayer) {
                            eatkey = true;
                            chat_on = true;
                            HUlib.HUlib_resetIText(w_chat);
                            HU_queueChatChar((char) (i+1));
                            break;
                        } else if (i == Game.getInstance().consoleplayer) {
                            num_nobrainers++;
                            if (num_nobrainers < 3) {
                                plr.message = messages.getString("HUSTR_TALKTOSELF1");
                            } else if (num_nobrainers < 6) {
                                plr.message = messages.getString("HUSTR_TALKTOSELF2");
                            } else if (num_nobrainers < 9) {
                                plr.message = messages.getString("HUSTR_TALKTOSELF3");
                            } else if (num_nobrainers < 32) {
                                plr.message = messages.getString("HUSTR_TALKTOSELF4");
                            } else {
                                plr.message = messages.getString("HUSTR_TALKTOSELF5");
                            }
                        }
                    }
                }
            }
        } else {
            c = (char) ev.data1;
            // send a macro
            if (altdown) {
                c -= '0';
                if (c > 9) {
                    return false;
                }
                // fprintf(stderr, "got here\n");
                macromessage = chat_macros[c].toCharArray();

                // kill last message with a '\n'
                game.headUp.HU_queueChatChar(KEY_ENTER); // DEBUG!!!

                // send the macro message
                int ic=0;
                while (ic < macromessage.length) {
                    game.headUp.HU_queueChatChar(macromessage[ic]);
                    ic++;
                }
                game.headUp.HU_queueChatChar(KEY_ENTER);

                // leave chat mode and notify that it was sent
                chat_on = false;
                lastmessage = chat_macros[c].getBytes();
                //strcpy(lastmessage, chat_macros[c]);
                plr.message = new String(lastmessage);
                eatkey = true;
            } else {
                if (Game.language.equals("fr")) {
                    c = ForeignTranslation(c);
                }
                if (shiftdown || (c >= 'a' && c <= 'z')) {
                    c = shiftxform[c];
                }
                eatkey = HUlib.HUlib_keyInIText(w_chat, c);
                if (eatkey) {
                    // static unsigned char buf[20]; // DEBUG
                    HU_queueChatChar(c);

                    // sprintf(buf, "KEY: %d => %d", ev.data1, c);
                    //      plr.message = buf;
                }
                if (c == KEY_ENTER) {
                    chat_on = false;
                    if (w_chat.l.len>0) {
                        lastmessage = w_chat.l.l;
                        //strcpy(lastmessage, w_chat.l.l);
                        plr.message = new String(lastmessage);
                    }
                } else if (c == KEY_ESCAPE) {
                    chat_on = false;
                }
            }
        }

        return eatkey;

    }

}
