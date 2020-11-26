/*
 * Wrapper for R_DrawColumnLow
 */
package thump.render.colfuncs;

import thump.render.Draw;


/**
 *
 * @author mark
 */
public class ColFunc_DrawColumnLow implements ColumnFunction {

    @Override
    public void doColFunc(Draw draw) {
        draw.R_DrawColumnLow();
    }
    
}
