/*
 * Holder of game values that will be stored for later games.
 * Such as screen size, sound volume and key mappings.
 */
package thump.game.menu;

/**
 *
 * @author mark
 */
public class Default {
    public final String	name;
    public int          location;    // change the name of this.
    public final int	defaultvalue;
    int             scantranslate;		// PC scan code hack
    int             untranslated;		// lousy hack    

    public Default(String name, int defaultvalue) {
        this.name = name;
        this.location = defaultvalue;
        this.defaultvalue = defaultvalue;
    }

    public int getValue() {
        return location;
    }
    
    public void setValue(int newVal ) {
        this.location = newVal;
    }
    

}
