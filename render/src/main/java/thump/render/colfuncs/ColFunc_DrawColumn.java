/*
 * Wrapper for R_DrawColumn
 */
package thump.render.colfuncs;

import thump.render.Draw;
import thump.render.Screen;

/**
 *
 * @author mark
 */
public class ColFunc_DrawColumn implements ColumnFunction {

    private final Screen screen;

    public ColFunc_DrawColumn(Screen screen) {
        this.screen = screen;
    }

    
    @Override
    public void doColFunc(Draw draw) {
        draw.R_DrawColumn(screen);
    }
    
}
