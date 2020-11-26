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
public class A_CPosRefire implements Action {

    public A_CPosRefire() {
    }

    @Override
    public void doAction(MapObject actor) {
//        // keep firing unless target got out of sight
//        A_FaceTarget(actor);
//
//        if (Random.getInstance().P_Random() < 40) {
//            return;
//        }
//
//        if (null == actor.target
//                || actor.target.health <= 0
//                || !game.sight.P_CheckSight(actor, actor.target)) {
//            MObject.P_SetMobjState(actor, actor.info.seestate);
//        }
    }

    @Override
    public void doAction(Player player, PSprite psp) {}
    
}
