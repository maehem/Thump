/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.game.play.action;

import static thump.base.FixedPoint.FRACUNIT;
import thump.game.Player;
import thump.game.maplevel.MapObject;
import thump.game.play.PSprite;

/**
 * Fly at the player like a missile.
 * 
 * @author mark
 */
public class A_SkullAttack implements Action {
    public static final int SKULLSPEED = (20 * FRACUNIT);

    public A_SkullAttack() {
    }

    @Override
    public void doAction(MapObject actor) {
//        MapObject dest;
//        int an;
//        int dist;
//
//        if (null == actor.target) {
//            return;
//        }
//
//        dest = actor.target;
//        actor.flags |= MF_SKULLFLY.getValue();
//
//        game.sound.S_StartSound(actor, actor.info.attacksound);
//        A_FaceTarget(actor);
//        an = actor.angle >> ANGLETOFINESHIFT;
//        actor.momx = FixedPoint.mul(SKULLSPEED, finecosine(an));
//        actor.momy = FixedPoint.mul(SKULLSPEED, finesine(an));
//        dist = game.map.util.P_AproxDistance(dest.x - actor.x, dest.y - actor.y);
//        dist /= SKULLSPEED;
//
//        if (dist < 1) {
//            dist = 1;
//        }
//        actor.momz = (dest.z + (dest.height >> 1) - actor.z) / dist;
    }

    @Override
    public void doAction(Player player, PSprite psp) {}
    
}
