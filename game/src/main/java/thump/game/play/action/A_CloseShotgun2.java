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
public class A_CloseShotgun2 implements Action {

    public A_CloseShotgun2() {
    }

    @Override
    public void doAction(MapObject mo) {}

    @Override
    public void doAction(Player player, PSprite psp) {
//        game.sound.S_StartSound(player.mo, sfx_dbcls);
//        //PSprite.A_ReFire(player, psp);
//        new A_ReFire().doAction(player, psp);
    }
    
}
