/*
 * fpoint_t
 */
package thump.game.automap;

import java.text.MessageFormat;

/**
 *
 * @author mark
 */
public class Fpoint {
    public int x, y;  
    
    Fpoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return new MessageFormat("x:{0}  y:{1}").format(new Object[]{x,y});
    }
    
    
}
