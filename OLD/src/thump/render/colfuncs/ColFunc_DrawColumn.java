/*
 * Wrapper for R_DrawColumn
 */
package thump.render.colfuncs;

import thump.game.Game;

/**
 *
 * @author mark
 */
public class ColFunc_DrawColumn implements ColumnFunction {

    @Override
    public void doColFunc(Game game) {
        game.renderer.draw.R_DrawColumn();
    }
    
}
