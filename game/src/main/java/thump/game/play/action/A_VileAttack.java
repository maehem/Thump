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
public class A_VileAttack implements Action {

    public A_VileAttack() {
    }

    @Override
    public void doAction(MapObject actor) {
//        MapObject fire;
//        int an;
//
//        if (null==actor.target) {
//            return;
//        }
//
//        A_FaceTarget(actor);
//
//        if (!game.sight.P_CheckSight(actor, actor.target)) {
//            return;
//        }
//
//        game.sound.S_StartSound(actor, sfx_barexp);
//        Interaction.P_DamageMobj(actor.target, actor, actor, 20);
//        actor.target.momz = 1000 * FRACUNIT / actor.target.info.mass;
//
//        an = actor.angle >> ANGLETOFINESHIFT;
//
//        fire = actor.tracer;
//
//        if (null==fire) {
//            return;
//        }
//
//        // move the fire between the vile and the player
//        fire.x = actor.target.x - FixedPoint.mul(24 * FRACUNIT, finecosine(an));
//        fire.y = actor.target.y - FixedPoint.mul(24 * FRACUNIT, finesine(an));
//        game.map.P_RadiusAttack(fire, actor, 70);
    }

    @Override
    public void doAction(Player player, PSprite psp) {}
    
}
