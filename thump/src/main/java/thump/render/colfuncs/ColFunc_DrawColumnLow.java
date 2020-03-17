/*
 * Wrapper for R_DrawColumnLow
 */
package thump.render.colfuncs;

import thump.game.Game;

/**
 *
 * @author mark
 */
public class ColFunc_DrawColumnLow implements ColumnFunction {

    @Override
    public void doColFunc(Game game) {
        game.renderer.draw.R_DrawColumnLow();
    }
    
}
