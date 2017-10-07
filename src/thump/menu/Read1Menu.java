/*
 * Read This Menu #1
 */
package thump.menu;

/**
 *
 * @author mark
 */
class Read1Menu extends Menu implements MenuAction {

    static enum Items {
        rdthsempty1,
    }
    
    public Read1Menu(Menu prevMenu, int x, int y) {
        super(prevMenu, x, y);
        super.lastOn = Items.rdthsempty1.ordinal();
        
        menuItems.add( new MenuItem(Items.rdthsempty1, 1, "",  this,   ' '));
    }

    @Override
    public void draw() {
        MenuManager.getInstance().M_DrawReadThis1();
    }
    
    @Override
    public void itemSelected(MenuItem item, int choice) {
//        choice = 0;
//        M_SetupNextMenu(&ReadDef2);
        MenuManager.getInstance().M_ReadThis(choice);
    }
}
