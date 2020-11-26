/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.game.play.action;

import thump.game.Player;
import thump.game.maplevel.MapObject;
import thump.game.play.PSprite;
import static thump.game.play.action.A_FatRaise.FATSPREAD;

/**
 *
 * @author mark
 */
public class A_FatAttack3 implements Action {

    public A_FatAttack3() {
    }

    @Override
    public void doAction(MapObject actor) {
        MapObject mo;
//        int an;
//
//        A_FaceTarget(actor);
//
//        mo = MObject.P_SpawnMissile(actor, actor.target, MT_FATSHOT);
//        mo.angle -= FATSPREAD / 2;
//        an = mo.angle >> ANGLETOFINESHIFT;
//        mo.momx = FixedPoint.mul(mo.info.speed, finecosine(an));
//        mo.momy = FixedPoint.mul(mo.info.speed, finesine(an));
//
//        mo = MObject.P_SpawnMissile(actor, actor.target, MT_FATSHOT);
//        mo.angle += FATSPREAD / 2;
//        an = mo.angle >> ANGLETOFINESHIFT;
//        mo.momx = FixedPoint.mul(mo.info.speed, finecosine(an));
//        mo.momy = FixedPoint.mul(mo.info.speed, finesine(an));
    }

    @Override
    public void doAction(Player player, PSprite psp) {}
    
}
