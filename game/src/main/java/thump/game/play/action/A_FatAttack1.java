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
public class A_FatAttack1 implements Action {

    public A_FatAttack1() {
    }

    @Override
    public void doAction(MapObject actor) {
//        MapObject mo;
//        int an;
//
//        A_FaceTarget(actor);
//        // Change direction  to ...
//        actor.angle += FATSPREAD;
//        MObject.P_SpawnMissile(actor, actor.target, MT_FATSHOT);
//
//        mo = MObject.P_SpawnMissile(actor, actor.target, MT_FATSHOT);
//        mo.angle += FATSPREAD;
//        an = mo.angle >> ANGLETOFINESHIFT;
//        mo.momx = FixedPoint.mul(mo.info.speed, finecosine(an));
//        mo.momy = FixedPoint.mul(mo.info.speed, finesine(an));
    }

    @Override
    public void doAction(Player player, PSprite psp) {}
    
}
