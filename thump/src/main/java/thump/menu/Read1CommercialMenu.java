/*
 * Read This Menu #1
 */
package thump.menu;

/**
 *
 * @author mark
 */
class Read1CommercialMenu extends Read1Menu {

//            read1Menu.routine = M_DrawReadThis1;
//            read1Menu.x = 330;
//            read1Menu.y = 165;
//            ReadMenu1[0].routine = M_FinishReadThis;

    static enum Items {
        rdthsempty1,
    }
    
    public Read1CommercialMenu(Menu prevMenu, int x, int y) {
        super(prevMenu, x, y);
        super.lastOn = Items.rdthsempty1.ordinal();
        
        menuItems.add( new MenuItem(Items.rdthsempty1, 1, "",  this,   ' '));
    }

    @Override
    public void draw() {
//        M_DrawReadThis1;
        MenuManager.getInstance().M_DrawReadThis1();
    }
    
    @Override
    public void itemSelected(MenuItem item, int choice) {
        MenuManager.getInstance().M_FinishReadThis(choice);
//       M_FinishReadThis; 
    }
}
