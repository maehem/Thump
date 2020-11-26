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
public class A_SkelFist implements Action {

    public A_SkelFist() {
    }

    @Override
    public void doAction(MapObject actor) {
//        int damage;
//
//        if (null==actor.target) {
//            return;
//        }
//
//        A_FaceTarget(actor);
//
//        if (P_CheckMeleeRange(actor)) {
//            damage = ((Random.getInstance().P_Random() % 10) + 1) * 6;
//            game.sound.S_StartSound(actor, sfx_skepch);
//            Interaction.P_DamageMobj(actor.target, actor, actor, damage);
//        }
    }

    @Override
    public void doAction(Player player, PSprite psp) {}
    
}
