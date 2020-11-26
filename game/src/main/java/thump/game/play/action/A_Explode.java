/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.game.play.action;

import thump.game.Player;
import thump.game.maplevel.MapObject;
import thump.game.play.PSprite;

/**
 *
 * @author mark
 */
public class A_Explode implements Action {

    public A_Explode() {
    }

    @Override
    public void doAction(MapObject mo) {
//        game.map.P_RadiusAttack(mo, mo.target, 128);
    }

    @Override
    public void doAction(Player player, PSprite psp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
