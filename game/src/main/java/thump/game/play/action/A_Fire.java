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
 * Keep fire in front of player unless out of sight
 * 
 * @author mark
 */
public class A_Fire implements Action {

    public A_Fire() {
    }

    @Override
    public void doAction(MapObject actor) {
//        MapObject dest;
//        int an;
//
//        dest = actor.tracer;
//        if (null == dest) {
//            return;
//        }
//
//        // don't move it if the vile lost sight
//        if (!game.sight.P_CheckSight(actor.target, dest)) {
//            return;
//        }
//
//        an = dest.angle >> ANGLETOFINESHIFT;
//
//        game.map.util.P_UnsetThingPosition(actor);
//        actor.x = dest.x + FixedPoint.mul(24 * FRACUNIT, finecosine(an));
//        actor.y = dest.y + FixedPoint.mul(24 * FRACUNIT, finesine(an));
//        actor.z = dest.z;
//        game.map.util.P_SetThingPosition(actor);
    }

    @Override
    public void doAction(Player player, PSprite psp) {}
    
}
