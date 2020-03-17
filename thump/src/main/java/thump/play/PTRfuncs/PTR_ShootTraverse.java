/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.play.PTRfuncs;

import thump.game.Game;
import thump.play.Intercept;
import thump.play.PTRfunc;

/**
 *
 * @author mark
 */
public class PTR_ShootTraverse implements PTRfunc {

    @Override
    public boolean doFunc(Intercept in) {
        return Game.getInstance().map.PTR_ShootTraverse(in);
    }
    
}
