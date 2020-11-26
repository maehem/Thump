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
public class A_BspiAttack implements Action {

    public A_BspiAttack() {
    }

    @Override
    public void doAction(MapObject actor) {
//        if (null == actor.target) {
//            return;
//        }
//
//        A_FaceTarget(actor);
//
//        // launch a missile
//        MObject.P_SpawnMissile(actor, actor.target, MT_ARACHPLAZ);
    }

    @Override
    public void doAction(Player player, PSprite psp) {}
    
}
