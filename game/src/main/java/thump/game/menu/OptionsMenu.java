/*
 * Options Menu
 */
package thump.game.menu;

import static thump.game.menu.OptionsMenu.Items.*;

/**
 *
 * @author mark
 */
public class OptionsMenu extends Menu implements MenuAction {

    public static enum  Items {
        endgame,
        messages,
        detail,
        scrnsize,
        option_empty1,
        mousesens,
        option_empty2,
        soundvol,
    }
    
    public OptionsMenu(Menu prevMenu, int x, int y) {
        super(prevMenu, x, y);
        super.lastOn = endgame.ordinal();
        
        menuItems.add( new MenuItem(endgame,        1, "M_ENDGAM",  this,   'e'));
        menuItems.add( new MenuItem(messages,       1, "M_MESSG",   this,   'm'));
        menuItems.add( new MenuItem(detail,         1, "M_DETAIL",  this,   'g'));
        menuItems.add( new MenuItem(scrnsize,       2, "M_SCRNSZ",  this,   's'));
        menuItems.add( new BlankMenuItem(Items.option_empty1) );
        menuItems.add( new MenuItem(mousesens,      2, "M_MSENS",   this,   'm'));
        menuItems.add( new BlankMenuItem(Items.option_empty2) );
        menuItems.add( new MenuItem(soundvol,       1, "M_SVOL",    this,   's'));
    
    }

    @Override
    public void itemSelected(MenuItem item, int choice) {
        
        switch( (Items)item.key ) {
            case endgame:
                MenuManager.getInstance().M_EndGame(choice);
                break;
            case messages:
                MenuManager.getInstance().M_ChangeMessages(choice);
                break;
            case detail:
                MenuManager.getInstance().M_ChangeDetail(choice);
                break;
            case scrnsize:
                MenuManager.getInstance().M_SizeDisplay(choice);
                break;
            case mousesens:
                MenuManager.getInstance().M_ChangeSensitivity(choice);
                break;
            case soundvol:
                MenuManager.getInstance().M_Sound(choice);
                break;
                
            case option_empty2:
            case option_empty1:
            default:
                throw new AssertionError(((Items)item.key).name());
        
        }
    }

    @Override
    public void draw() {
        MenuManager.getInstance().M_DrawOptions();
    }
    
}