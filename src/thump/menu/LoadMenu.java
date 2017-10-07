/*
 * Load Menu
 */
package thump.menu;

import static thump.menu.LoadMenu.Items.load1;

/**
 *
 * @author mark
 */
class LoadMenu extends Menu implements MenuAction {
    enum Items {
        load1,
        load2,
        load3,
        load4,
        load5,
        load6,
    };

    public LoadMenu(Menu prevMenu, int x, int y) {
        super(prevMenu, x, y);
        super.lastOn = load1.ordinal();
        
        menuItems.add( new MenuItem(Items.load1, 1, null,  this,  '1'));
        menuItems.add( new MenuItem(Items.load2, 1, null,  this,  '2'));
        menuItems.add( new MenuItem(Items.load3, 1, null,  this,  '3'));
        menuItems.add( new MenuItem(Items.load4, 1, null,  this,  '4'));
        menuItems.add( new MenuItem(Items.load5, 1, null,  this,  '5'));
        menuItems.add( new MenuItem(Items.load6, 1, null,  this,  '6'));
    }

    @Override
    public void draw() {
        MenuManager.getInstance().M_DrawLoad();
    }

    @Override
    public void itemSelected(MenuItem menuItem, int choice) {
        MenuManager.getInstance().M_LoadSelect(choice);
    }
    
}
