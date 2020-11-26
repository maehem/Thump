/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.game.play.action;

import thump.game.maplevel.MapObject;

/**
 * Spawn a lost soul and launch it at the target
 * 
 * @author mark
 */
public class A_PainShootSkull {
    
    public void doAction(MapObject actor, int angle) {
//        int x;
//        int y;
//        int z;
//
//        MapObject newmobj;
//        int an;
//        int prestep;
//        int count;
//        Thinker currentthinker;
//
//        // count total number of skull currently on the level
//        count = 0;
//
//        currentthinker = game.thinkercap.getNextThinker();
//        while (currentthinker != game.thinkercap) {
//            if ((currentthinker.getFunction() instanceof T_MobjThinker)
//                    && ((MapObject) currentthinker).type == MT_SKULL) {
//                count++;
//            }
//            currentthinker = currentthinker.getNextThinker();
//        }
//
//        // if there are allready 20 skulls on the level,
//        // don't spit another one
//        if (count > 20) {
//            return;
//        }
//
//        // okay, there's playe for another one
//        an = angle >> ANGLETOFINESHIFT;
//
//        prestep = 4 * FRACUNIT
//                + 3 * (actor.info.radius + mobjinfo[MT_SKULL.ordinal()].radius) / 2;
//
//        x = actor.x + FixedPoint.mul(prestep, finecosine(an));
//        y = actor.y + FixedPoint.mul(prestep, finesine(an));
//        z = actor.z + 8 * FRACUNIT;
//
//        newmobj = MObject.P_SpawnMobj(x, y, z, MT_SKULL);
//
//        // Check for movements.
//        if (!game.map.P_TryMove(newmobj, newmobj.x, newmobj.y)) {
//            // kill it immediately
//            Interaction.P_DamageMobj(newmobj, actor, actor, 10000);
//            return;
//        }
//
//        newmobj.target = actor.target;
//        A_SkullAttack(newmobj);
    }
    
}
