/*
 * Save Menu
 */
package thump.game.menu;

/**
 *
 * @author mark
 */
class SaveMenu extends Menu implements MenuAction {
    enum Items {
        load1,
        load2,
        load3,
        load4,
        load5,
        load6,
    };

    public SaveMenu(Menu prevMenu, int x, int y) {
        super(prevMenu, x, y);
        super.lastOn = Items.load1.ordinal();
        
        menuItems.add( new MenuItem(Items.load1, 1, "",  this,  '1'));
        menuItems.add( new MenuItem(Items.load2, 1, "",  this,  '2'));
        menuItems.add( new MenuItem(Items.load3, 1, "",  this,  '3'));
        menuItems.add( new MenuItem(Items.load4, 1, "",  this,  '4'));
        menuItems.add( new MenuItem(Items.load5, 1, "",  this,  '5'));
        menuItems.add( new MenuItem(Items.load6, 1, "",  this,  '6'));
    }

    @Override
    public void draw() {
        MenuManager.getInstance().M_DrawSave();
    }

    @Override
    public void itemSelected(MenuItem menuItem, int choice) {
        MenuManager.getInstance().M_SaveSelect(choice);
    }
    
}
