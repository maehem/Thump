/*
 * Main Menu
 */
package thump.menu;

import static thump.menu.MainMenu.Items.*;

/**
 *
 * @author mark
 */
public class MainMenu extends Menu implements MenuAction {

    private final MenuItem readThis; // Removed in some cases by menu manager.
    
    // TODO try making this private after code all works.
    protected static enum Items {
        newgame,  options, loadgame, savegame, readthis, quitdoom
    };

    public MainMenu(Menu prevMenu, int x, int y) {
        super(prevMenu, x, y);
        super.lastOn = newgame.ordinal();
        
        readThis = new MenuItem(readthis, 1, "M_RDTHIS", this, 'r');

        menuItems.add(new MenuItem(newgame,  1, "M_NGAME",  this, 'n'));
        menuItems.add(new MenuItem(options,  1, "M_OPTION", this, 'o'));
        menuItems.add(new MenuItem(loadgame, 1, "M_LOADG",  this, 'l'));
        menuItems.add(new MenuItem(savegame, 1, "M_SAVEG",  this, 's'));
        menuItems.add(readThis);   // Removed at Init for Commerical GameMode.
        menuItems.add(new MenuItem(quitdoom, 1, "M_QUITG",  this, 'q'));
    }

    @Override
    public void itemSelected(MenuItem item, int choice) {
        MenuManager menuManager = MenuManager.getInstance();
        
        switch ( (Items)item.key ) {
            case newgame:
                menuManager.M_NewGame(choice);
                break;
            case options:
                menuManager.M_Options(choice);
                break;
            case loadgame:
                menuManager.M_LoadGame(choice);
                break;
            case savegame:
                menuManager.M_SaveGame(choice);
                break;
            case readthis:
                menuManager.M_ReadThis(choice);
                break;
            case quitdoom:
                menuManager.M_QuitDOOM(choice);
                break;
            default:
                throw new AssertionError(((Items)item.key).name());
            
        }
        
    }

    @Override
    public void draw() {
        MenuManager.getInstance().M_DrawMainMenu();
    }
        
    public void quitDoomAction(int choice) {
//        // We pick index 0 which is language sensitive,
//        //  or one at random, between 1 and maximum number.
//        if (language != english )
//        sprintf(endstring,"%s\n\n"DOSY, endmsg[0] );
//        else
//        sprintf(endstring,"%s\n\n"DOSY, endmsg[ (gametic%(NUM_QUITMESSAGES-2))+1 ]);
//
//        M_StartMessage(endstring,M_QuitResponse,true);
    }
    
    void removeReadThis() {
        menuItems.remove(readThis);
    }
}
