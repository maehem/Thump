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
public class A_Tracer implements Action {
    public static final int TRACEANGLE = 0xc000000;

    public A_Tracer() {
    }

    @Override
    public void doAction(MapObject actor) {
//        int exact;
//        int dist;
//        int slope;
//        MapObject dest;
//        MapObject th;
//
//        if ((game.gametic & 3) > 0) {
//            return;
//        }
//
//        // spawn a puff of smoke behind the rocket		
//        MObject.P_SpawnPuff(actor.x, actor.y, actor.z);
//
//        th = MObject.P_SpawnMobj(actor.x - actor.momx,
//                actor.y - actor.momy,
//                actor.z, MT_SMOKE);
//
//        th.momz = FRACUNIT;
//        th.tics -= Random.getInstance().P_Random() & 3;
//        if (th.tics < 1) {
//            th.tics = 1;
//        }
//
//        // adjust direction
//        dest = actor.tracer;
//
//        if (null == dest || dest.health <= 0) {
//            return;
//        }
//
//        // change angle	
//        exact = game.renderer.R_PointToAngle2(actor.x,
//                actor.y,
//                dest.x,
//                dest.y);
//
//        if (exact != actor.angle) {
//            if (exact - actor.angle > 0x80000000) {
//                actor.angle -= TRACEANGLE;
//                if (exact - actor.angle < 0x80000000) {
//                    actor.angle = exact;
//                }
//            } else {
//                actor.angle += TRACEANGLE;
//                if (exact - actor.angle > 0x80000000) {
//                    actor.angle = exact;
//                }
//            }
//        }
//
//        exact = actor.angle >> ANGLETOFINESHIFT;
//        actor.momx = FixedPoint.mul(actor.info.speed, finecosine(exact));
//        actor.momy = FixedPoint.mul(actor.info.speed, finesine(exact));
//
//        // change slope
//        dist = game.map.util.P_AproxDistance(dest.x - actor.x,
//                dest.y - actor.y);
//
//        dist /= actor.info.speed;
//
//        if (dist < 1) {
//            dist = 1;
//        }
//        slope = (dest.z + 40 * FRACUNIT - actor.z) / dist;
//
//        if (slope < actor.momz) {
//            actor.momz -= FRACUNIT / 8;
//        } else {
//            actor.momz += FRACUNIT / 8;
//        }
    }

    @Override
    public void doAction(Player player, PSprite psp) {}
    
}
