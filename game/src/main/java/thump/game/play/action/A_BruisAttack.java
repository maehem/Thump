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
public class A_BruisAttack implements Action {

    public A_BruisAttack() {
    }

    @Override
    public void doAction(MapObject actor) {
//        int damage;
//
//        if (null == actor.target) {
//            return;
//        }
//
//        if (P_CheckMeleeRange(actor)) {
//            game.sound.S_StartSound(actor, sfx_claw);
//            damage = (Random.getInstance().P_Random() % 8 + 1) * 10;
//            Interaction.P_DamageMobj(actor.target, actor, actor, damage);
//            return;
//        }
//
//        // launch a missile
//        MObject.P_SpawnMissile(actor, actor.target, MT_BRUISERSHOT);
    }

    @Override
    public void doAction(Player player, PSprite psp) {}
    
}
