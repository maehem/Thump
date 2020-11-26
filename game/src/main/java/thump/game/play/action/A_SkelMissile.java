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
public class A_SkelMissile implements Action {

    public A_SkelMissile() {
    }

    @Override
    public void doAction(MapObject actor) {
//        MapObject mo;
//
//        if (null == actor.target) {
//            return;
//        }
//
//        A_FaceTarget(actor);
//        actor.z += 16 * FRACUNIT;	// so missile spawns higher
//        mo = MObject.P_SpawnMissile(actor, actor.target, MT_TRACER);
//        actor.z -= 16 * FRACUNIT;	// back to normal
//
//        mo.x += mo.momx;
//        mo.y += mo.momy;
//        mo.tracer = actor.target;
    }

    @Override
    public void doAction(Player player, PSprite psp) {}
    
}
