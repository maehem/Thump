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
public class A_FaceTarget implements Action {

    public A_FaceTarget() {
    }

    @Override
    public void doAction(MapObject actor) {
//        if (null == actor.target) {
//            return;
//        }
//
//        actor.flags &= ~MF_AMBUSH.getValue();
//
//        actor.angle = game.renderer.R_PointToAngle2(actor.x,
//                actor.y,
//                actor.target.x,
//                actor.target.y);
//
//        if ((actor.target.flags & MF_SHADOW.getValue()) > 0) {
//            actor.angle += (Random.getInstance().P_Random() - Random.getInstance().P_Random()) << 21;
//        }
    }

    @Override
    public void doAction(Player player, PSprite psp) {}
    
}
