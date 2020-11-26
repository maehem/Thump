/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.render.colfuncs;

import thump.render.Draw;

/**
 *
 * @author mark
 */
public class ColFunc_DrawTranslatedColumn implements ColumnFunction {

    @Override
    public void doColFunc(Draw draw) {
        draw.R_DrawTranslatedColumn();
    }
    
}
