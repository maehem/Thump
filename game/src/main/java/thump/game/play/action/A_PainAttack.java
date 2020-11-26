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
 * Spawn a lost soul and launch it at the target
 * 
 * @author mark
 */
public class A_PainAttack implements Action {

    public A_PainAttack() {
    }

    @Override
    public void doAction(MapObject actor) {
//        if (null == actor.target) {
//            return;
//        }
//
//        A_FaceTarget(actor);
//        A_PainShootSkull(actor, actor.angle);
    }

    @Override
    public void doAction(Player player, PSprite psp) {}
    
}
