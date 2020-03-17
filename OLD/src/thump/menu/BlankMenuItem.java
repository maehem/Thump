/*
 * Blank Menu Item.  Some screens have gaps between items.
 */
package thump.menu;

/**
 *
 * @author mark
 */
public class BlankMenuItem extends MenuItem {
    
    public BlankMenuItem(Enum<?> key) {
        super(key, -1, null, null,' ');
    }
    
}
