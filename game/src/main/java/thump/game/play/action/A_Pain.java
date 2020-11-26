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
public class A_Pain implements Action {

    public A_Pain() {
    }

    @Override
    public void doAction(MapObject mo) {
//        if (actor.info.painsound != null) {
//            game.sound.S_StartSound(actor, actor.info.painsound);
//        }
    }

    @Override
    public void doAction(Player player, PSprite psp) {}
    
}
