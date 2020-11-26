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
public class A_CyberAttack implements Action {

    public A_CyberAttack() {
    }

    @Override
    public void doAction(MapObject actor) {
//        if (null==actor.target) {
//            return;
//        }
//
//        A_FaceTarget (actor);
//        MObject.P_SpawnMissile (actor, actor.target, MT_ROCKET);
    }

    @Override
    public void doAction(Player player, PSprite psp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
