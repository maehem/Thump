/*
 * MenuManager System
 */
package thump.menu;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;
import java.util.Properties;
import thump.game.Event;
import thump.game.Game;
import thump.global.Defines;
import thump.global.SystemInterface;
import static thump.headsup.Stuff.HU_FONTSIZE;
import static thump.headsup.Stuff.HU_FONTSTART;
import static thump.i18n.English.DETAILHI;
import static thump.i18n.English.DETAILLO;
import static thump.menu.NewGameMenu.Items.nightmare;
import static thump.menu.OptionsMenu.Items.detail;
import static thump.menu.OptionsMenu.Items.messages;
import static thump.menu.OptionsMenu.Items.mousesens;
import static thump.menu.OptionsMenu.Items.scrnsize;
import static thump.menu.SoundMenu.Items.music_vol;
import static thump.menu.SoundMenu.Items.sfx_vol;
import thump.sound.Sound;
import thump.sound.sfx.Sounds;
import static thump.sound.sfx.Sounds.SfxEnum.*;

/**
 *
 * @author mark
 */
public class MenuManager {
    
    private final Game game = Game.getInstance();
    //extern boolean		chat_on;		// in heads-up code
    
    private static MenuManager manager = null;
    private int itemOn;		// menu item skull is on
    private int skullAnimCounter;	// skull animation counter
    private int whichSkull;		// which skull to draw

    //
    // defaulted values
    //
    private int	mouseSensitivity;       // has default
    public boolean  showMessages;           // Show messages has default, 0 = off, 1 = on
    public boolean  detailLevel;            // Blocky mode,  has default, 0 = high, 1 = normal
    public  int	screenblocks;           // has default
    private int	screenSize = 1;             // temp for screenblocks (0-9)
    private int	quickSaveSlot;          // -1 = no quicksave slot picked!
    
          
     // 1 = message to be printed
    private boolean     messageToPrint = false;
    // ...and here is the message string!
    private String	messageString = "";		

    private int         messx;	    // message x & y		
    private int         messy;
    private boolean     messageLastMenuActive;

    private boolean     messageNeedsInput; // timed message = no input from user


    private MenuResponse messageRoutine;
    //void    (*messageRoutine)(int response);

    private final static int SAVESTRINGSIZE = 24;

    private  static final int SKULLXOFF = -32;
    private  static final int LINEHEIGHT = 16;


    private String gammamsg[] =
    {
        Game.getMessage("GAMMALVL0"),
        Game.getMessage("GAMMALVL1"),
        Game.getMessage("GAMMALVL2"),
        Game.getMessage("GAMMALVL3"),
        Game.getMessage("GAMMALVL4")
    };

    private boolean     saveStringEnter;// we are going to be entering a savegame string
    private int         saveSlot;	// which slot to save in
    private int         saveCharIndex;	// which char we're editing
    // old save description before edit
    private String      saveOldString;  // Truncate to 26 chars.

    public boolean     inhelpscreens;
    public boolean     menuactive;

    private String      savegamestrings[] = new String[10];
    private String      endstring;


    // graphic name of skulls
    // warning: initializer-string for array of chars is too long
    private final String skullName[] = {"M_SKULL1" ,"M_SKULL2"};
    
    /*
     *  Menu Definitions
    */
    private       Menu        currentMenu;    
    public final MainMenu    mainMenu    = new MainMenu( null, 97, 80/*64*/ );
    private final EpisodeMenu episodeMenu = new EpisodeMenu(mainMenu, 48, 63);
    private final NewGameMenu newGameMenu = new NewGameMenu(episodeMenu, 48, 63);
    private final OptionsMenu optionsMenu = new OptionsMenu(mainMenu, 60, 37);
    private       Read1Menu   read1Menu   = new Read1Menu(mainMenu, 280, 185 );  // Not final, it can change.
    private final Read1CommercialMenu   read1CommercialMenu   = new Read1CommercialMenu(mainMenu, 330, 165 );
    private final Read2Menu   read2Menu   = new Read2Menu(read1Menu, 330, 175 );
    private final SoundMenu   soundMenu   = new SoundMenu(optionsMenu,80,64);
    private final LoadMenu    loadMenu    = new LoadMenu(mainMenu, 80,54);
    private final SaveMenu    saveMenu    = new SaveMenu(mainMenu, 80,54);
    

    public  final MenuMisc menuMisc = new MenuMisc();
    private Sound sound = Game.getInstance().sound;
    
    
    public static MenuManager getInstance() {
        if ( manager == null ) {
            manager = new MenuManager();
        }
        
        return manager;
    }
    
    private int snd_MusicVolume = 8;
    private int snd_SfxVolume = 8;
    
    private MenuManager() {}

    public void setCurrentMenu( Menu m ) {
        this.currentMenu = m;
    }
    
    public Menu getCurrentMenu() {
        return currentMenu;
    }
    
    public void initDefaultProperties() {
        menuMisc.loadDefaults();
        Properties p = menuMisc.properties;
        
        mouseSensitivity    = Integer.valueOf(p.getProperty(MenuMisc.PROPK_MOUSE_SENS)); //mouseSensitivity;
        showMessages        = Boolean.valueOf(p.getProperty(MenuMisc.PROPK_SHOW_MSG)); //showMessages;
        detailLevel         = Boolean.valueOf(p.getProperty(MenuMisc.PROPK_DETAIL));//detailLevel;
        screenblocks        = Integer.valueOf(p.getProperty(MenuMisc.PROPK_SCREENBLK));//screenblocks;
        snd_SfxVolume       = Integer.valueOf(p.getProperty(MenuMisc.PROPK_SFX_VOL));
        snd_MusicVolume     = Integer.valueOf(p.getProperty(MenuMisc.PROPK_MUS_VOL));
        //sound channels  Sound
        //usegamma  Video
    
        
    }
    
    /*********************************************
     * Main Menu Callbacks
     *********************************************/
    
    /**
     * Load either New Game menu or Episode Menu(on COMMERCIAL version)
     * @param choice not used 
     */
    protected void M_NewGame(int choice) {
        if (Game.getInstance().netgame && !game.demoplayback) {
            MenuManager.getInstance().M_StartMessage(
                    Game.getMessage("NEWGAME"),
                    null,false);
            return;
        }

        if ( game.gameMode == Defines.GameMode.COMMERCIAL ) {
            M_SetupNextMenu(newGameMenu);
        } else {
            M_SetupNextMenu(episodeMenu);
        }
    }

    protected void M_Options(int choice) {
        M_SetupNextMenu( optionsMenu );
    }

    /**
     * Load Game Menu item action
     * 
     * @param choice 
     */
    protected void M_LoadGame(int choice) {
        if (Game.getInstance().netgame) {
            M_StartMessage(
                Game.getMessage("LOADNET"),
                null, false);
            return;
        }

        M_SetupNextMenu( loadMenu );
        M_ReadSaveStrings();
    }

    /**
     * Save Game Menu item action
     * 
     * @param choice
     */
    protected void M_SaveGame(int choice) {
        if (!Game.getInstance().usergame) {
            M_StartMessage(
                    Game.getMessage("SAVEDEAD"),
                    null, false);
            return;
        }

        if (game.gamestate != Defines.GameState.GS_LEVEL) {
            return;
        }

        M_SetupNextMenu( saveMenu );
        M_ReadSaveStrings();
    }

    protected void M_ReadThis(int choice) {
        M_SetupNextMenu( read1Menu );
    }

    protected void M_QuitDOOM(int choice) {
        M_StartMessage(
                endmsg[(game.gametic%(endmsg.length-1))]
              + "\n\n" + game.messages.getString("QUITMSG"), 
                new M_QuitResponse(),
                true);
    }

   /*************************************************
    *  New Game Menu Callbacks
    *************************************************/
    /**
     * Choose Skill Level
     * 
     * @param choice not used
     */
    protected void M_ChooseSkill(int choice) {
        if (choice == nightmare.ordinal()) {
            M_StartMessage(
                    Game.getMessage("NIGHTMARE"),
                    new M_VerifyNightmare(), true);
            return;
        }

        Game.getInstance().G_DeferedInitNew(Defines.Skill.values()[choice], epi + 1, 1);
        M_ClearMenus();
    }

    private class M_VerifyNightmare implements MenuResponse {

        @Override
        public void routine( byte choice) {
        
            if (choice != 'y') {
                return;
            }

//TODO            G_DeferedInitNew(nightmare, epi + 1, 1);
            M_ClearMenus();
        }
    }

    
   /*************************************************
    *  Episode Menu Callbacks
    *************************************************/
    void M_Episode(int choice) {
        if (game.gameMode == Defines.GameMode.SHAREWARE
                && choice > 0 ) {
            
            M_StartMessage(
                    Game.getMessage("SWSTRING"),
                    null, false );
            
            M_SetupNextMenu( read1Menu);
            return;
        }
        epi=choice;
        // Yet another hack...
        if (game.gameMode == Defines.GameMode.REGISTERED
                && choice > 2) {
            Defines.logger.severe( "M_Episode: 4th episode requires UltimateDOOM\n");
            epi=0; // No episode for you!!!
        }

        M_SetupNextMenu( newGameMenu );
    }

    
    
    /*************************************************
    *  Options Menu Callbacks
    *************************************************/
    /**
     * End Game Menu action
     * 
     * @param choice not used
     */
    public void M_EndGame(int choice) {
        if (!Game.getInstance().usergame) {
            sound.S_StartSound(null, sfx_oof);
            return;
        }

        if (Game.getInstance().netgame) {
            M_StartMessage(
                    Game.getMessage("NETEND"),
                    null, false);
            return;
        }

        M_StartMessage(
                Game.getMessage("ENDGAME"),
                new M_EndGameResponse(), true);
    }

    /**
     * Toggle messages on/off
     * 
     * @param choice not used
     */
    void M_ChangeMessages(int choice) {
        showMessages = !showMessages; // Toggle value

        if (showMessages) {
            game
                    .players[Game.getInstance().consoleplayer]
                    .message = Game.getMessage("MSGOFF");
        } else {
            game
                    .players[Game.getInstance().consoleplayer]
                    .message = Game.getMessage("MSGON");
        }

        game.headUp.message_dontfuckwithme = true;
    }

    /**
     * Change Detail - Not working currently
     * 
     * @param choice not used
     */
    void M_ChangeDetail(int choice) {
        detailLevel = !detailLevel;

        // FIXME - does not work. Remove anyway?
        Defines.logger.info("M_ChangeDetail: low detail mode not applicable.\n");

        
        Game.getInstance().renderer.R_SetViewSize (screenblocks, detailLevel);

        if (!detailLevel) {
            game.players[Game.getInstance().consoleplayer].message = DETAILHI;
        } else {
            game.players[Game.getInstance().consoleplayer].message = DETAILLO;
        }
        
    }

    /**
     * Increase/Decrease screen size in blocks
     * 
     * @param choice 0=decrease, 1=increase
     */
    void M_SizeDisplay(int choice) {
        switch (choice) {
            case 0:
                if (screenSize > 0) {
                    screenblocks--;
                    screenSize--;
                }
                break;
            case 1:
                if (screenSize < 8) {
                    screenblocks++;
                    screenSize++;
                }
                break;
        }

        Game.getInstance().renderer.R_SetViewSize(screenblocks, detailLevel);
    }

    /**
     * Change Mouse Sensitivity
     * 
     * @param choice 0=decrease,  1=increase
     */
    void M_ChangeSensitivity(int choice) {
        switch (choice) {
            case 0:
                if (mouseSensitivity>0) {
                    mouseSensitivity--;
                }
                break;
            case 1:
                if (mouseSensitivity < 9) {
                    mouseSensitivity++;
                }
                break;
        }
    }

    /**
     * Open Sound Settings Menu
     * 
     * @param choice not used
     */
    void M_Sound(int choice) {
        M_SetupNextMenu( soundMenu );
    }

    /***************************************************
     * Load Game Menu Page
     ***************************************************/

    /**
     * Load Select Menu action
     * 
     * @param choice is slot number to load (1-6)
     */
    void M_LoadSelect(int choice) {
        String name;   //char name[256];
//
//        if (M_CheckParm("-cdrom")) {
            String saveGameName = Game.getMessage("SAVEGAMENAME");
            name = "c:\\doomdata\\" + saveGameName + choice + ".dsg";
//            sprintf(name, "c:\\doomdata\\"SAVEGAMENAME"%d.dsg",choice);
//        } else
//            sprintf(name, SAVEGAMENAME"%d.dsg",choice);
//        G_LoadGame(name);
        M_ClearMenus();
    }

    /***************************************************
     * Save Game Menu Page
     ***************************************************/

    /**
     * Save Game Menu action
     * @param choice is the slot number (1-6) to save to
     */
    protected void M_SaveSelect(int choice) {
//        // we are going to be intercepting all chars
        saveStringEnter = true;

        saveSlot = choice;
        saveOldString = savegamestrings[choice];
//        strcpy(saveOldString, savegamestrings[choice]);

        // Null value OK for string array location.
//        if (!strcmp(savegamestrings[choice], EMPTYSTRING)) {
//            savegamestrings[choice][0] = 0;
//        }
        if ( savegamestrings[choice]== null ) {
            savegamestrings[choice] = "";
        }

        saveCharIndex = savegamestrings[choice].length();
//        saveCharIndex = strlen(savegamestrings[choice]);
    }

    /***************************************************
     * Read This Additional Menu Pages
     ***************************************************/
    /**
     * Page 2 of Read this menu
     * @param choice not used
     */
    protected void M_ReadThis2(int choice) {
        M_SetupNextMenu( read2Menu );
    }

    /**
     * Finish of Read this pages. Go back to MainMenu.
     * 
     * @param choice not used
     */
    void M_FinishReadThis(int choice) {
        M_SetupNextMenu( mainMenu );
    }


    /***************************************************
     * Sound Menu Pages
     ***************************************************/
    /**
     * Change SFX Volume
     * 
     * @param choice 0=decrease,  1=increase
     */
    void M_SfxVol(int choice) {
        switch (choice) {
            case 0:
                if (snd_SfxVolume>0) {
                    snd_SfxVolume--;
                }
                break;
            case 1:
                if (snd_SfxVolume < 15) {
                    snd_SfxVolume++;
                }
                break;
        }

        sound.S_SetSfxVolume(snd_SfxVolume * 8 );  // Menu tracks 0-15.  Sound wants 0-127
    }

    /**
     * Change Music Volume
     * 
     * @param choice  0=decrease,  1=increase
     */
    void M_MusicVol(int choice) {
        switch (choice) {
            case 0:
                if (snd_MusicVolume>0) {
                    snd_MusicVolume--;
                }
                break;
            case 1:
                if (snd_MusicVolume < 15) {
                    snd_MusicVolume++;
                }
                break;
        }

        sound.S_SetMusicVolume(snd_MusicVolume * 8);  // Menu tracks 0-15.  Sound wants 0-127
    }







    //
    // M_ReadSaveStrings
    //  read the strings from the savegame files
    //
    void M_ReadSaveStrings() {
//        int handle;
//        int count;
//        int i;
//        String name;
//	
//        for (i = 0;i < LoadMenu.Items.values().length;i++) {
//            if (M_CheckParm("-cdrom")) {
//                sprintf(name,"c:\\doomdata\\"SAVEGAMENAME"%d.dsg",i);
//            } else {
//                sprintf(name,SAVEGAMENAME"%d.dsg",i);
//            }
//            handle = open (name, O_RDONLY | 0, 0666);
//            if (handle == -1)
//            {
//                strcpy(&savegamestrings[i][0],EMPTYSTRING);
//                LoadMenu[i].status = 0;
//                continue;
//            }
//            count = read (handle, &savegamestrings[i], SAVESTRINGSIZE);
//            close (handle);
//            LoadMenu[i].status = 1;
//        }
    }

    /**
     * Draw the Loading Screen
     */
    void M_DrawLoad() {
        //Lump lump = game.wad.findByName("M_LOADG");
        game.video.drawPatchDirect(72, 28, 0, "M_LOADG");
        for (int i = 0; i < loadMenu.numitems(); i++) {
            M_DrawSaveLoadBorder(loadMenu.x, loadMenu.y + LINEHEIGHT * i);
            M_WriteText(loadMenu.x, loadMenu.y + LINEHEIGHT * i, savegamestrings[i]);
        }
    }

    //
    // Draw border for the savegame description
    //
    void M_DrawSaveLoadBorder(int x, int y) {
        //Wad wad = game.wad;
        //Video vid = Video.getInstance();

        game.video.drawPatchDirect(x-8, y+7, 0, "M_LSLEFT");        
        // video.drawPatchDirect(x - 8, y + 7, 0, W_CacheLumpName("M_LSLEFT", PU_CACHE));

        int xx = x;
        for (int i = 0; i < 24; i++) {
            game.video.drawPatchDirect(xx, y + 7, 0, "M_LSCNTR");        
            //video.drawPatchDirect(x, y + 7, 0, W_CacheLumpName("M_LSCNTR", PU_CACHE));
            xx += 8;
        }

        game.video.drawPatchDirect(x, y + 7, 0, "M_LSRGHT");        
        //video.drawPatchDirect(x, y + 7, 0, W_CacheLumpName("M_LSRGHT", PU_CACHE));
    }

    //
    //  M_SaveGame & Cie.
    //
    void M_DrawSave() {
        int i;

        game.video.drawPatchDirect(72, 28, 0, "M_SAVEG");
        for (i = 0; i < loadMenu.numitems(); i++) {
            M_DrawSaveLoadBorder(loadMenu.x, loadMenu.y + LINEHEIGHT * i);
            M_WriteText(loadMenu.x, loadMenu.y + LINEHEIGHT * i, savegamestrings[i]);
        }

        if (saveStringEnter) {
            i = M_StringWidth(savegamestrings[saveSlot]);
            M_WriteText(loadMenu.x + i, loadMenu.y + LINEHEIGHT * saveSlot, "_");
        }
    }

    //
    // M_Responder calls this when user is finished
    //
    void M_DoSave(int slot) {
//TODO        G_SaveGame(slot, savegamestrings[slot]);
        M_ClearMenus();

        // PICK QUICKSAVE SLOT YET?
        if (quickSaveSlot == -2) {
            quickSaveSlot = slot;
        }
    }

//
//      M_QuickSave
//
//    private String tempstring;

    private class M_QuickSaveResponse implements MenuResponse {

        @Override
        public void routine(byte ch) {
            if (ch == 'y') {
                M_DoSave(quickSaveSlot);
                game.sound.S_StartSound(null, sfx_swtchx);
            }
        }
        
    }
//    void M_QuickSaveResponse(int ch) {
////        if (ch == 'y') {
////            M_DoSave(quickSaveSlot);
////            sound.S_StartSound(null, sfx_swtchx);
////        }
//    }

    
    
    void M_QuickSave() {
        if (!game.usergame) {
            sound.S_StartSound(null, sfx_oof);
            return;
        }

        if (game.gamestate != Defines.GameState.GS_LEVEL) {
            return;
        }

        if (quickSaveSlot < 0) {
            M_StartControlPanel();
            M_ReadSaveStrings();
            M_SetupNextMenu( saveMenu );
            quickSaveSlot = -2;	// means to pick a slot now
            return;
        }
        
        //sprintf(tempstring, QSPROMPT, savegamestrings[quickSaveSlot]);
        Object[] messageArgs = new Object[]{
            "QSPROMPT",
            savegamestrings[quickSaveSlot],
            Game.getMessage("PRESSYN")
        };
        
        M_StartMessage(
                formatMessage(messageArgs, true), 
                new M_QuickSaveResponse(), true);

    }

    //
    // M_QuickLoad
    //
    private class M_QuickLoadResponse implements MenuResponse {

        @Override
        public void routine(byte ch) {
            if (ch == 'y') {
                M_LoadSelect(quickSaveSlot);
                sound.S_StartSound(null, sfx_swtchx);
            }
        }

    }
//            void M_QuickLoadResponse(int ch) {
//        //        if (ch == 'y') {
//        //            M_LoadSelect(quickSaveSlot);
//        //            sound.S_StartSound(null, sfx_swtchx);
//        //        }
//            }

    void M_QuickLoad() {
        
        if (game.netgame) {
            M_StartMessage(Game.getMessage("QLOADNET"), null, false);
            return;
        }

        if (quickSaveSlot < 0) {
            M_StartMessage(Game.getMessage("QSAVESPOT"), null, false);
            return;
        }
        
        
        //sprintf(tempstring, Game.getMessage("QLPROMPT"), savegamestrings[quickSaveSlot]);
        Object[] messageArgs = new Object[]{
            "QLPROMPT",
            savegamestrings[quickSaveSlot],
            Game.getMessage("PRESSYN")
        };
        
        M_StartMessage(
                formatMessage(messageArgs, true), 
                new M_QuickLoadResponse(), true);
    }

    /**
     * Format a localization message that contains bracketed "fill in" items
     * example  "It's been {0} days since I last played DOOM."
     * 
     * @param args to fill in.  [0] is always the message.
     * @param bundleTag if true, message is a bundle tag name instead of regular String.
     * @return 
     */
    private String formatMessage( Object[] args, boolean bundleTag ) {
        MessageFormat formatter = new MessageFormat("");
        formatter.setLocale(Game.currentLocale);

        if ( bundleTag ) {
            String message = Game.getMessage((String) args[0]);
            Object[] messageArguments = new Object[args.length];
        
            for ( int i=0; i<args.length; i++ ) {
                if (i==0) {
                    messageArguments[i] = message;
                } else {
                    messageArguments[i] = args[i];
                }
             }
            
            formatter.applyPattern(message);
            return formatter.format(messageArguments);
        } else {
            formatter.applyPattern((String) args[0]);
            return formatter.format(args);
        }
    }
    

    /**
     * Read This Menu 1
     */
    void M_DrawReadThis1() {
        inhelpscreens = true;
        switch (game.gameMode) {
            case COMMERCIAL:
                game.video.drawPatchDirect(0, 0, 0, "HELP");
                break;
            case SHAREWARE:
            case REGISTERED:
            case RETAIL:
                game.video.drawPatchDirect(0, 0, 0, "HELP1");
                break;
            default:
                break;
        }
    }

    /**
     * Read This Menu 2
     */
    void M_DrawReadThis2() {
        inhelpscreens = true;
        switch (game.gameMode) {
            case RETAIL:
            case COMMERCIAL:
                // This hack keeps us from having to change menus.
                game.video.drawPatchDirect(0, 0, 0, "CREDIT");
                break;
            case SHAREWARE:
            case REGISTERED:
                game.video.drawPatchDirect(0, 0, 0, "HELP2");
                break;
            default:
                break;
        }
    }

    //
    // Change Sfx & Music volumes
    //
    void M_DrawSound() {
        game.video.drawPatchDirect(60, 38, 0, "M_SVOL");

        M_DrawThermo(soundMenu.x, soundMenu.y + LINEHEIGHT * (sfx_vol.ordinal() + 1),
                16, snd_SfxVolume);

        M_DrawThermo(soundMenu.x, soundMenu.y + LINEHEIGHT * (music_vol.ordinal() + 1),
                16, snd_MusicVolume);
    }

    //
    // M_DrawMainMenu
    //
    void M_DrawMainMenu() {
        game.video.drawPatchDirect(94, 20/*2*/, 0, "M_DOOM");
    }

    //
    // M_NewGame
    //
    void M_DrawNewGame() {
        game.video.drawPatchDirect(96, 14, 0, "M_NEWG");
        game.video.drawPatchDirect(54, 38, 0, "M_SKILL");
    }

    
//
//      M_Episode
//
    private int epi;

    void M_DrawEpisode() {
        game.video.drawPatchDirect(54, 38, 0, "M_EPISOD");
    }

    //
    // M_Options
    //
    private final String detailNames[]= {"M_GDHIGH","M_GDLOW"};
    private final String msgNames[]   = {"M_MSGOFF","M_MSGON"};


    void M_DrawOptions() {
        game.video.drawPatchDirect(108, 15, 0, "M_OPTTTL");

        game.video.drawPatchDirect(optionsMenu.x + 175, optionsMenu.y + LINEHEIGHT * detail.ordinal(), 0,
                detailNames[detailLevel?1:0]);

        game.video.drawPatchDirect(optionsMenu.x + 120, optionsMenu.y + LINEHEIGHT * messages.ordinal(), 0,
                msgNames[showMessages?1:0]);

        M_DrawThermo(optionsMenu.x, optionsMenu.y + LINEHEIGHT * (mousesens.ordinal() + 1),
                10, mouseSensitivity);

        M_DrawThermo(optionsMenu.x, optionsMenu.y + LINEHEIGHT * (scrnsize.ordinal() + 1),
                9, screenSize);
    }

    /**
     * End Game Response
     */
    private class M_EndGameResponse implements MenuResponse {

        @Override
        public void routine(byte ch) {
            if (ch != 'y') {
                return;
            }
            currentMenu.lastOn = itemOn;
            M_ClearMenus();
            Game.getInstance().doomMain.D_StartTitle();
        }

    }
//        void M_EndGameResponse(int ch) {
//    //        if (ch != 'y') {
//    //            return;
//    //        }
//    //        currentMenu . lastOn = itemOn;
//    //        M_ClearMenus();
//    //        D_StartTitle();
//        }

    //
    // M_QuitDOOM
    //
    private final Sounds.SfxEnum quitsounds[] = {
        sfx_pldeth,
        sfx_dmpain,
        sfx_popain,
        sfx_slop,
        sfx_telept,
        sfx_posit1,
        sfx_posit3,
        sfx_sgtatk
    };

    private final Sounds.SfxEnum quitsounds2[] = {
        sfx_vilact,
        sfx_getpow,
        sfx_boscub,
        sfx_slop,
        sfx_skeswg,
        sfx_kntdth,
        sfx_bspact,
        sfx_sgtatk
    };

    // From dStrings.c
    public final static String endmsg[]= {
        // Default
        Game.getMessage("QUITMSG"),
        
        // DOOM1
        Game.getMessage("QMSG_D1_1"),
        Game.getMessage("QMSG_D1_2"),
        Game.getMessage("QMSG_D1_3"),
        Game.getMessage("QMSG_D1_4"),
        Game.getMessage("QMSG_D1_5"),
        Game.getMessage("QMSG_D1_6"),
        Game.getMessage("QMSG_D1_7"),

        // QuitDOOM II messages
        Game.getMessage("QMSG_D2_1"),
        Game.getMessage("QMSG_D2_2"),
        Game.getMessage("QMSG_D2_3"),
        Game.getMessage("QMSG_D2_4"),
        Game.getMessage("QMSG_D2_5"),
        Game.getMessage("QMSG_D2_6"),
        Game.getMessage("QMSG_D2_7"),

        // FinalDOOM?
        Game.getMessage("QMSG_DF_1"),
        Game.getMessage("QMSG_DF_2"),
        Game.getMessage("QMSG_DF_3"),
        Game.getMessage("QMSG_DF_4"),
        Game.getMessage("QMSG_DF_5"),
        Game.getMessage("QMSG_DF_6"),
        Game.getMessage("QMSG_DF_7"),

        // Internal debug. Different style, too.
        Game.getMessage("QMSG_DBG"),

};
    


    private class M_QuitResponse implements MenuResponse {

        @Override
        public void routine(byte ch) {
    
            if (ch != 'y' && ch != 'Y') {
                return;
            }
            if (!Game.getInstance().netgame) {
                if (game.gameMode == Defines.GameMode.COMMERCIAL) {
                    sound.S_StartSound(null, quitsounds2[(Game.getInstance().gametic >> 2) & 7]);
                } else {
                    sound.S_StartSound(null, quitsounds[(Game.getInstance().gametic >> 2) & 7]);
                }
                SystemInterface.getInstance().I_WaitVBL(105);
            }
            SystemInterface.getInstance().I_Quit();
        }
    }

    //
    //      MenuManager Functions
    //
    void M_DrawThermo(int _x,
                    int y,
                    int thermWidth,
                    int thermDot) {
        int i;
        int xx = _x;
        
        game.video.drawPatchDirect(xx, y, 0, "M_THERML");
        xx += 8;
        for (i = 0; i < thermWidth; i++) {
            game.video.drawPatchDirect(xx, y, 0, "M_THERMM");
            xx += 8;
        }
        game.video.drawPatchDirect(xx, y, 0, "M_THERMR");

        game.video.drawPatchDirect((_x + 8) + thermDot * 8, y,
                0, "M_THERMO");
    }

    void M_DrawEmptyCell(Menu	menu, int item ) {
        game.video.drawPatchDirect(
            menu.x - 10, 
            menu.y + item * LINEHEIGHT - 1, 0,
            "M_CELL1"       );
    }

    void M_DrawSelCell(Menu menu, int item ) {
        game.video.drawPatchDirect(
            menu.x - 10, 
            menu.y + item * LINEHEIGHT - 1, 0,
            "M_CELL2"    );
    }

    void M_StartMessage(String string, MenuResponse routine, boolean input ) {
        messageLastMenuActive = menuactive;
        messageToPrint = true;
        messageString = string;
        messageRoutine = routine;
        messageNeedsInput = input;
        menuactive = true;
    }

    void M_StopMessage() {
        menuactive = messageLastMenuActive;
        messageToPrint = false;
    }

    /**
     * Find string width from hu_font chars
     * 
     * @param string
     * @return 
     */
    int M_StringWidth(String string) {
        int i;
        int w = 0;
        int c;

        char[] chars = string.toCharArray();
        for (i = 0; i < chars.length; i++) {
            c = Character.toUpperCase(chars[i]) - HU_FONTSTART;
            if (c < 0 || c >= HU_FONTSIZE) {
                w += 4;
            } else {
                w += game.headUp.hu_font[c].width;
            }
        }
		
        return w;
    }

    /**
     * Find string height from hu_font chars
     * 
     * @param string
     * @return 
     */
    int M_StringHeight(String string) {
	int height = game.headUp.hu_font[0].height;
        
        int h = height;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == '\n') {
                h += height;
            }
        }

        return h;
    }

    //
    //      Write a string using the hu_font
    //
    void M_WriteText(
            int x,
            int y,
            String string  ) 
    {
        int w;
        //String	ch;
        //int start = 0;
        //int c;
        int cx;
        int cy;

        //ch = string;
        cx = x;
        cy = y;

        if ( string == null ) {
            return;
        }
        
        for ( int i=0; i<string.length(); i++ ) {
            int c = string.charAt(i);
            if ( c == '\n' ) {
                cx = x;
                cy += 12;
            } else {
                c = Character.toUpperCase(c)-HU_FONTSTART;
                if (c < 0 || c >= HU_FONTSIZE) {
                    cx += 4;
                } else {
                    w = game.headUp.hu_font[c].width;
                    //if (cx + w > SCREENWIDTH) {
                    //    break;
                    //}
                    game.video.drawPatchDirect(cx, cy, 0, game.headUp.hu_font[c]);
                    cx += w;
                }                
            }
        }
//        while (true) {
//            c =  ch.charAt(start);
//            start++;
//            
//            if (c==0) {
//                break;
//            }
//            if (c == '\n') {
//                cx = x;
//                cy += 12;
//                continue;
//            }
//
//            c = Character.toUpperCase(c) - HU_FONTSTART;
//            if (c < 0 || c >= HU_FONTSIZE) {
//                cx += 4;
//                continue;
//            }
//
//            w = Stuff.getInstance().hu_font[c].width;
//            if (cx + w > SCREENWIDTH) {
//                break;
//            }
//            video.drawPatchDirect(cx, cy, 0, Stuff.getInstance().hu_font[c]);
//            cx += w;
//        }
    }

//
// CONTROL PANEL
//
    private long    joywait = 0;
    private long    mousewait = 0;
    private int     mousey = 0;
    private int     lasty = 0;
    private int     mousex = 0;
    private int     lastx = 0;
    
    //
    // M_Responder
    //
    public boolean M_Responder(Event  ev) {
        byte             ch = -1;

        //ch = -1;
        //Defines.logger.config("M_Responder\n");

        long sysTime = SystemInterface.getInstance().I_GetTime();
        
        if (    ev.type == Event.EventType.ev_joystick &&
                joywait < sysTime
        ) {
            if (ev.data3 == -1) {
                ch = Defines.KEY_UPARROW;
                joywait = sysTime + 5;
            } else if (ev.data3 == 1) {
                ch = Defines.KEY_DOWNARROW;
                joywait = sysTime + 5;
            }

            if (ev.data2 == -1) {
                ch = Defines.KEY_LEFTARROW;
                joywait = sysTime + 2;
            } else if (ev.data2 == 1) {
                ch = Defines.KEY_RIGHTARROW;
                joywait = sysTime + 2;
            }

            if ((ev.data1&1) > 0) {
                ch = Defines.KEY_ENTER;
                joywait = sysTime + 5;
            }
            
            if ((ev.data1&2) > 0) {
                ch = Defines.KEY_BACKSPACE;
                joywait = sysTime + 5;
            }
        } else {
            if (  ev.type == Event.EventType.ev_mouse &&
                    mousewait < SystemInterface.getInstance().I_GetTime()
            ) {
                mousey += ev.data3;
                if (mousey < lasty-30) {
                    ch = Defines.KEY_DOWNARROW;
                    mousewait = sysTime + 5;
                    lasty -= 30;
                    mousey = lasty;
                } else if (mousey > lasty+30) {
                    ch = Defines.KEY_UPARROW;
                    mousewait = sysTime + 5;
                    lasty += 30;
                    mousey = lasty;
                }

                mousex += ev.data2;
                if (mousex < lastx-30) {
                    ch = Defines.KEY_LEFTARROW;
                    mousewait = sysTime + 5;
                    lastx -= 30;
                    mousex = lastx;
                } else if (mousex > lastx+30) {
                    ch = Defines.KEY_RIGHTARROW;
                    mousewait = sysTime + 5;
                    lastx += 30;
                    mousex = lastx;
                }

                if ((ev.data1&1)>0) {
                    ch = Defines.KEY_ENTER;
                    mousewait = sysTime + 15;
                }

                if ((ev.data1&2)>0) {
                    ch = Defines.KEY_BACKSPACE;
                    mousewait = sysTime + 15;
                }
            } else if (ev.type == Event.EventType.ev_keydown) {
                ch = (byte) ev.data1;
            }
        }

        if (ch == -1) {
            return false;
        }


        // Save Game string input
        if (saveStringEnter) {
            switch(ch) {
              case Defines.KEY_BACKSPACE:
                if (saveCharIndex > 0) {
                    saveCharIndex--;
                    if ( saveCharIndex == 0 ) {
                        savegamestrings[saveSlot]="";
                    }
                    String oldStr = savegamestrings[saveSlot];
                    savegamestrings[saveSlot]=oldStr.substring(0, oldStr.length()-1);
                    //savegamestrings[saveSlot][saveCharIndex] = 0;
                }
                break;

              case Defines.KEY_ESCAPE:
                saveStringEnter = false;
                savegamestrings[saveSlot] = saveOldString;
                //strcpy(&savegamestrings[saveSlot][0],saveOldString);
                break;

              case Defines.KEY_ENTER:
                saveStringEnter = false;
                if (savegamestrings[saveSlot].length() > 0) {
                    M_DoSave(saveSlot);
                }
                break;

              default:
                ch = (byte) Character.toUpperCase(ch);
                if (ch != 32) {
                    if (ch-HU_FONTSTART < 0 || ch-HU_FONTSTART >= HU_FONTSIZE) {
                        break;
                    }
                }
                if (ch >= 32 && ch <= 127 &&
                    saveCharIndex < SAVESTRINGSIZE-1 &&
                    M_StringWidth(savegamestrings[saveSlot]) <
                    (SAVESTRINGSIZE-2)*8)
                {
                    savegamestrings[saveSlot] += ch;
                    //savegamestrings[saveSlot][saveCharIndex++] = ch;
                    //savegamestrings[saveSlot][saveCharIndex] = 0;
                }
                break;
            }
            return true;
        }

        // Take care of any messages that need input
        if (messageToPrint) {
            if (messageNeedsInput == true &&
                !(ch == ' ' || ch == 'n' || ch == 'y' || ch == 'N' || ch == 'Y' || ch == Defines.KEY_ESCAPE)) {
                return false;
            }

            menuactive = messageLastMenuActive;
            messageToPrint = false;
            if (messageRoutine!=null) {
                messageRoutine.routine(ch);
            }

            menuactive = false;
            sound.S_StartSound(null,sfx_swtchx);
            return true;
        }

        if (game.doomMain.devparm && ch == Defines.KEY_F1) {
//TODO            G_ScreenShot ();
            return true;
        }
        
        // F-Keys
        if (!menuactive) {
            switch(ch) {
                case Defines.KEY_MINUS:         // Screen size down
                    if (game.autoMap.automapactive || game.headUp.chat_on) {
                        return false;
                    }
                    M_SizeDisplay(0);
                    sound.S_StartSound(null,sfx_stnmov);
                    return true;
                    
                case Defines.KEY_EQUALS:        // Screen size up
                    if (game.autoMap.automapactive || game.headUp.chat_on) {
                        return false;
                    }
                    M_SizeDisplay(1);
                    sound.S_StartSound(null,sfx_stnmov);
                    return true;
                    
                case Defines.KEY_F1:            // Help key
                    M_StartControlPanel ();
                    
                    if ( game.gameMode == Defines.GameMode.RETAIL ) {
                        currentMenu = read2Menu;
                    } else {
                        currentMenu = read1Menu;
                    }
                    
                    itemOn = 0;
                    sound.S_StartSound(null,sfx_swtchn);
                    return true;
                    
                case Defines.KEY_F2:            // Save
                    M_StartControlPanel();
                    sound.S_StartSound(null,sfx_swtchn);
                    M_SaveGame(0);
                    return true;
                    
                case Defines.KEY_F3:            // Load
                    M_StartControlPanel();
                    sound.S_StartSound(null,sfx_swtchn);
                    M_LoadGame(0);
                    return true;
                    
                case Defines.KEY_F4:            // Sound Volume
                    M_StartControlPanel ();
                    currentMenu = soundMenu;
                    itemOn = sfx_vol.ordinal();
                    sound.S_StartSound(null,sfx_swtchn);
                    return true;
                    
                case Defines.KEY_F5:            // Detail toggle
                    M_ChangeDetail(0);
                    sound.S_StartSound(null,sfx_swtchn);
                    return true;
                    
                case Defines.KEY_F6:            // Quicksave
                    sound.S_StartSound(null,sfx_swtchn);
                    M_QuickSave();
                    return true;
                    
                case Defines.KEY_F7:            // End game
                    sound.S_StartSound(null,sfx_swtchn);
                    M_EndGame(0);
                    return true;
                    
                case Defines.KEY_F8:            // Toggle messages
                    M_ChangeMessages(0);
                    sound.S_StartSound(null,sfx_swtchn);
                    return true;
                    
                case Defines.KEY_F9:            // Quickload
                    sound.S_StartSound(null,sfx_swtchn);
                    M_QuickLoad();
                    return true;
                    
                case Defines.KEY_F10:           // Quit DOOM
                    sound.S_StartSound(null,sfx_swtchn);
                    M_QuitDOOM(0);
                    return true;
                    
                case Defines.KEY_F11:           // gamma toggle
                    game.video.usegamma++;
                    if (game.video.usegamma > 4) {
                        game.video.usegamma = 0;
                    }
                    game.players[game.consoleplayer].message = gammamsg[game.video.usegamma];
//TODO                    I_SetPalette (W_CacheLumpName ("PLAYPAL",PU_CACHE));
                    return true;
                    
            }
        }


        // Pop-up menu?
        if (!menuactive) {
            if (ch == Defines.KEY_ESCAPE) {
                M_StartControlPanel ();
                sound.S_StartSound(null,sfx_swtchn);
                return true;
            }
            return false;
        }

        Defines.logger.config("do menu key\n");
        // Keys usable within menu
        switch (ch) {
          case Defines.KEY_DOWNARROW:
            do {
                if (itemOn+1 > currentMenu.numitems()-1) {
                    itemOn = 0;
                } else {
                    itemOn++;
                }
                sound.S_StartSound(null,sfx_pstop);
            } while(currentMenu.menuItems.get(itemOn).status==-1);
            return true;

          case Defines.KEY_UPARROW:
            do {
                if (itemOn==0) {
                    itemOn = currentMenu.numitems()-1;
                } else {
                    itemOn--;
                }
                sound.S_StartSound(null,sfx_pstop);
            } while(currentMenu.menuItems.get(itemOn).status==-1);
            
            return true;

          case Defines.KEY_LEFTARROW:
            if (currentMenu.menuItems.get(itemOn).action!= null &&
                currentMenu.menuItems.get(itemOn).status == 2) {
                sound.S_StartSound(null,sfx_stnmov);
                MenuItem currentItem = currentMenu.menuItems.get(itemOn);
                currentItem.action.itemSelected(currentItem, 0);
            }
            
            return true;

          case Defines.KEY_RIGHTARROW:
            if (currentMenu.menuItems.get(itemOn).action!= null &&
                currentMenu.menuItems.get(itemOn).status == 2) {
                sound.S_StartSound(null,sfx_stnmov);
                MenuItem currentItem = currentMenu.menuItems.get(itemOn);
                currentItem.action.itemSelected(currentItem, 1);
            }
            return true;

          case Defines.KEY_ENTER:
            if (currentMenu.menuItems.get(itemOn).action!= null &&
                currentMenu.menuItems.get(itemOn).status !=0 ) {
                currentMenu.lastOn = itemOn;
                if (currentMenu.menuItems.get(itemOn).status == 2) {
                    MenuItem currentItem = currentMenu.menuItems.get(itemOn);
                    currentItem.action.itemSelected(currentItem, 1);
                    sound.S_StartSound(null,sfx_stnmov);
                } else {
                    MenuItem currentItem = currentMenu.menuItems.get(itemOn);
                    currentItem.action.itemSelected(currentItem, itemOn);
                    //    currentMenu.menuItems.get(itemOn).routine(itemOn);
                    sound.S_StartSound(null,sfx_pistol);
                }
            }
            return true;

          case Defines.KEY_ESCAPE:
            currentMenu.lastOn = itemOn;
            M_ClearMenus ();
            sound.S_StartSound(null,sfx_swtchx);
            return true;

          case Defines.KEY_BACKSPACE:
            currentMenu.lastOn = itemOn;
            if (currentMenu.prevMenu != null) {
                currentMenu = currentMenu.prevMenu;
                itemOn = currentMenu.lastOn;
                sound.S_StartSound(null,sfx_swtchn);
            }
            return true;

          default:
            for (int i = itemOn+1;i < currentMenu.numitems();i++) {
                if (currentMenu.menuItems.get(i).alphaKey == ch) {
                    itemOn = i;
                     sound.S_StartSound(null,sfx_pstop);
                    return true;
                }
            }
            for (int i = 0;i <= itemOn;i++) {
                if (currentMenu.menuItems.get(i).alphaKey == ch) {
                    itemOn = i;
                    sound.S_StartSound(null,sfx_pstop);
                    return true;
                }
            }
            break;

        }

        return false;
}


    /**
     * Start the Control Panel
     */
    public void M_StartControlPanel ()
    {
        // intro might call this repeatedly
        if (menuactive) {
            return;
        }

        menuactive = true;
        currentMenu = mainMenu;        // JDC
        itemOn = currentMenu.lastOn;   // JDC
    }

    private int x;
    private int y;
//
// M_Drawer
// Called after the view has been rendered,
// but before it has been blitted.
//
public void M_Drawer () {
    int		i;
    int		max;
    String		string="";
    int			start;

    inhelpscreens = false;

    //Defines.logger.config("M_Drawer()\n");
    
    if ( menuactive ) {
        BufferedImage screen = game.video.screenImage[0];
        Graphics g = screen.getGraphics();
        g.setColor( new Color(120, 100, 0, 150) );
        g.fillRect(0, 0, screen.getWidth(), screen.getHeight());
        g.dispose();
    }
    
    // Horiz. & Vertically center string and print it.
    if (messageToPrint) {
	start = 0;
	y = 100 - M_StringHeight(messageString)/2;
	while(messageString.length() > start) {
            
            
	    for (i = 0;i < messageString.length()-start;i++) {
                if (messageString.charAt(start+i) == '\n') {
                    //memset(string,0,40);
                    //strncpy(string,messageString+start,i);
                    string = messageString.substring(start, start+i);
                    start += i+1;
                    break;
                }
            }
				
	    if (i == messageString.length()-start) {  // End of string reached
		//strcpy(string,messageString+start);
                string = messageString.substring(start);
		start += i;
	    }
				
	    x = 160 - M_StringWidth(string)/2;
	    M_WriteText(x,y,string);
	    y += game.headUp.hu_font[0].height;
	}
	return;
    }

    if (!menuactive) { 
        return;
    }

    
    //if (currentMenu.draw;
    currentMenu.draw();         // call Draw routine
    
    // DRAW MENU
    x = currentMenu.x;
    y = currentMenu.y;
    max = currentMenu.numitems();

    for (i=0;i<max;i++) {
	if (currentMenu.menuItems.get(i).name!=null) {
            game.video.drawPatchDirect (x,y,0,currentMenu.menuItems.get(i).name);
        }
	y += LINEHEIGHT;
    }

    //Video.getInstance().clearScreenRegion(0, x+SKULLXOFF, currentMenu.y-5, 32, LINEHEIGHT*currentMenu.numitems()+5 );
    // DRAW SKULL
    game.video.drawPatchDirect(
            x + SKULLXOFF,currentMenu.y - 5 + itemOn*LINEHEIGHT, 0,
            skullName[whichSkull]);

}


//
// M_ClearMenus
//
void M_ClearMenus ()
{
    menuactive = false;
    // if (!netgame && usergame && paused)
    //       sendpause = true;
}




//
// M_SetupNextMenu
//
void M_SetupNextMenu(Menu menudef)
{
    currentMenu = menudef;
    itemOn = currentMenu.lastOn;
}


//
// M_Ticker
//
public void M_Ticker ()
{
    skullAnimCounter--;
    if (skullAnimCounter <= 0) {
	whichSkull ^= 1;
	skullAnimCounter = 8;
    }
}


    //
    // M_Init
    //
    public void M_Init () {
        currentMenu = mainMenu;
        menuactive = false;
        itemOn = currentMenu.lastOn;
        whichSkull = 0;
        skullAnimCounter = 10;
        screenSize = screenblocks - 3;
        messageToPrint = false;
        messageString = null;
        messageLastMenuActive = menuactive;
        quickSaveSlot = -1;

        // Here we could catch other version dependencies,
        //  like HELP1/2, and four episodes.

        switch ( game.gameMode ) {
          case COMMERCIAL:
            // This is used because DOOM 2 had only one HELP
            //  page. I use CREDIT as second page now, but
            //  kept this hack for educational purposes.
            
            //
            //MainMenu[readthis] = MainMenu[quitdoom];
            //mainMenu.numitems--;
            mainMenu.removeReadThis();
            
            mainMenu.y += 8;
            newGameMenu.prevMenu = mainMenu;
            read1Menu = read1CommercialMenu;
            
            // Moved to Read1CommercialMenu
//            read1Menu.routine = M_DrawReadThis1;
//            read1Menu.x = 330;
//            read1Menu.y = 165;
//            ReadMenu1[0].routine = M_FinishReadThis;
            break;
          case SHAREWARE:
            // Episode 2 and 3 are handled,
            //  branching to an ad screen.
          case REGISTERED:
            // We need to remove the fourth episode.
            //EpiDef.numitems--;
            episodeMenu.removeFourth();
            break;
          case RETAIL:
            // We are fine.
          default:
            break;
        }

    }

    
}
