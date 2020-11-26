/*
 * Wrapper for R_DrawFuzzColumn
 */
package thump.render.colfuncs;

import thump.render.Draw;


/**
 *
 * @author mark
 */
public class ColFunc_DrawFuzzColumn implements ColumnFunction {

    @Override
    public void doColFunc(Draw draw) {
        draw.R_DrawFuzzColumn();
    }
    
}
