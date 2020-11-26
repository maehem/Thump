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
 *  Check for resurrecting a body
 * 
 * @author mark
 */
public class A_VileChase implements Action {

    public A_VileChase() {
    }

    @Override
    public void doAction(MapObject actor) {
//        int xl;
//        int xh;
//        int yl;
//        int yh;
//
//        int bx;
//        int by;
//
//        MobJInfo info;
//        MapObject temp;
//
//        if (actor.movedir != DI_NODIR) {
//            // check for corpses to raise
//            viletryx = actor.x + actor.info.speed * xspeed[actor.movedir.ordinal()];
//            viletryy = actor.y + actor.info.speed * yspeed[actor.movedir.ordinal()];
//
//            xl = (viletryx - game.playerSetup.bmaporgx - MAXRADIUS * 2) >> MAPBLOCKSHIFT;
//            xh = (viletryx - game.playerSetup.bmaporgx + MAXRADIUS * 2) >> MAPBLOCKSHIFT;
//            yl = (viletryy - game.playerSetup.bmaporgy - MAXRADIUS * 2) >> MAPBLOCKSHIFT;
//            yh = (viletryy - game.playerSetup.bmaporgy + MAXRADIUS * 2) >> MAPBLOCKSHIFT;
//
//            vileobj = actor;
//            for (bx = xl; bx <= xh; bx++) {
//                for (by = yl; by <= yh; by++) {
//                    // Call PIT_VileCheck to check
//                    // whether object is a corpse
//                    // that canbe raised.
//                    if (!game.map.util.P_BlockThingsIterator(bx, by, new PIT_VileCheck())) {
//                        // got one!
//                        temp = actor.target;
//                        actor.target = corpsehit;
//                        A_FaceTarget(actor);
//                        actor.target = temp;
//
//                        MObject.P_SetMobjState(actor, S_VILE_HEAL1);
//                        game.sound.S_StartSound(corpsehit, sfx_slop);
//                        info = corpsehit.info;
//
//                        MObject.P_SetMobjState(corpsehit, info.raisestate);
//                        corpsehit.height <<= 2;
//                        corpsehit.flags = info.flags;
//                        corpsehit.health = info.spawnhealth;
//                        corpsehit.target = null;
//
//                        return;
//                    }
//                }
//            }
//        }
//
//        // Return to normal attack.
//        A_Chase(actor);
    }

    @Override
    public void doAction(Player player, PSprite psp) {}
    
}
