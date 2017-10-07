/*
 * Wrapper for R_DrawFuzzColumn
 */
package thump.render.colfuncs;

import thump.game.Game;

/**
 *
 * @author mark
 */
public class ColFunc_DrawFuzzColumn implements ColumnFunction {

    @Override
    public void doColFunc(Game game) {
        game.renderer.draw.R_DrawFuzzColumn();
    }
    
}
