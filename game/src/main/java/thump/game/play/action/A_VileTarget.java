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
public class A_VileTarget implements Action {

    public A_VileTarget() {
    }

    @Override
    public void doAction(MapObject actor) {
//        MapObject fog;
//
//        if (null == actor.target) {
//            return;
//        }
//
//        A_FaceTarget(actor);
//
//        fog = MObject.P_SpawnMobj(actor.target.x,
//                actor.target.x,
//                actor.target.z, MT_FIRE);
//
//        actor.tracer = fog;
//        fog.target = actor;
//        fog.tracer = actor.target;
//        A_Fire(fog);
    }

    @Override
    public void doAction(Player player, PSprite psp) {}
    
}
