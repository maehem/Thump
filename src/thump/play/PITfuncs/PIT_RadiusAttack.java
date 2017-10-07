/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.play.PITfuncs;

import thump.game.Game;
import thump.maplevel.MapObject;
import thump.play.PITfunc;
import thump.render.Line;

/**
 *
 * @author mark
 */
public class PIT_RadiusAttack implements PITfunc {

    @Override
    public boolean doFunc(MapObject mo) {
        return Game.getInstance().map.PIT_RadiusAttack(mo);
    }

    @Override
    public boolean doFunc(Line line) {
        throw new UnsupportedOperationException("PIT_RadiusAttack: Wrong function called!"); //To change body of generated methods, choose Tools | Templates.
    }
    
}
