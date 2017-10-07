/*
 * New Game Menu
 */
package thump.menu;

import static thump.menu.NewGameMenu.Items.*;

/**
 *
 * @author mark
 */
public class NewGameMenu extends Menu implements MenuAction{

    public enum  Items {
        killthings,
        toorough,
        hurtme,
        violence,
        nightmare
    }
    
    //EnumMap<Items, MenuItem> menuItems = new EnumMap<>(Items.class);

    public NewGameMenu(Menu prevMenu, int x, int y) {
        super(prevMenu, x, y);
        super.setLastOn(hurtme.ordinal());
        
        menuItems.add( new MenuItem(killthings, 1, "M_JKILL", this, 'i'));
        menuItems.add( new MenuItem(toorough,   1, "M_ROUGH", this, 'h'));
        menuItems.add( new MenuItem(hurtme,     1, "M_HURT",  this, 'h'));
        menuItems.add( new MenuItem(violence,   1, "M_ULTRA", this, 'u'));
        menuItems.add( new MenuItem(nightmare,  1, "M_NMARE", this, 'n'));
        
    }

//    @Override
//    public final void putItem(Enum<?> e, MenuItem item) {
//        menuItems.put((Items) e,  item);
//    }

     @Override
    public void draw() {
        MenuManager.getInstance().M_DrawNewGame();
    }
    
    @Override
    public void itemSelected(MenuItem menuItem, int choice) {
        MenuManager.getInstance().M_ChooseSkill(choice);
    }

}
