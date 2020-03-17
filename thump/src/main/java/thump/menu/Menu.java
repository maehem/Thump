/*
 * Menu Template
 */
package thump.menu;

import java.util.ArrayList;

/**
 *
 * @author mark
 */
//typedef struct menu_s {
public abstract class Menu {
    ArrayList<MenuItem> menuItems = new ArrayList<>();

    Menu prevMenu;          // previous menu
    //Object drawRoutine;     // draw routine
    int x;
    int y;                  // x,y of menu
    int lastOn;	    // last item user was on in menu

    public Menu(Menu prevMenu, int x, int y) {
        this.prevMenu = prevMenu;
        this.x = x;
        this.y = y;
        
        lastOn = 0;
    }

    //public abstract void putItem( Enum<?> e, MenuItem item );
    
    public abstract void draw();

    public void setLastOn(int e) {
        this.lastOn = e;
    }
    
    public int numitems() {
        return menuItems.size();
    }

}