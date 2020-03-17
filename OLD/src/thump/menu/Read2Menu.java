/*
 * Read This Page 2 Menu
 */
package thump.menu;

/**
 *
 * @author mark
 */
class Read2Menu extends Menu implements MenuAction {

    public static enum Items {
        rdthsempty2,
    }
    
    public Read2Menu(Menu prevMenu, int x, int y) {
        super(prevMenu, x, y);
        super.lastOn = Items.rdthsempty2.ordinal();
        
        menuItems.add( new MenuItem(Items.rdthsempty2, 1, "",  this,   ' '));
    }

    @Override
    public void draw() {
        MenuManager.getInstance().M_DrawReadThis2();
    }
    
    @Override
    public void itemSelected(MenuItem item, int choice) {
//        choice = 0;
//        M_SetupNextMenu(&MainDef);
        MenuManager.getInstance().M_ReadThis2(choice);
    }
}
