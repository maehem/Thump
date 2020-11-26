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
public class A_PlayerScream implements Action {

    public A_PlayerScream() {
    }

    @Override
    public void doAction(MapObject mo) {
//        // Default death sound.
//        SfxEnum sound = sfx_pldeth;
//
//        if ((game.gameMode == GameMode.COMMERCIAL)
//                && (mo.health < -50)) {
//            // IF THE PLAYER DIES
//            // LESS THAN -50% WITHOUT GIBBING
//            sound = sfx_pdiehi;
//        }
//
//        game.sound.S_StartSound(mo, sound);
    }

    @Override
    public void doAction(Player player, PSprite psp) {}
    
}
