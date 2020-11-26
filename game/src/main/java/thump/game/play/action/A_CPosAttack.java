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
public class A_CPosAttack implements Action {

    public A_CPosAttack() {
    }

    @Override
    public void doAction(MapObject actor) {
//        int angle;
//        int bangle;
//        int damage;
//        int slope;
//
//        if (null == actor.target) {
//            return;
//        }
//
//        Random rand = Random.getInstance();
//
//        game.sound.S_StartSound(actor, sfx_shotgn);
//        A_FaceTarget(actor);
//        bangle = actor.angle;
//        slope = game.map.P_AimLineAttack(actor, bangle, MISSILERANGE);
//
//        angle = bangle + ((rand.P_Random() - rand.P_Random()) << 20);
//        damage = ((rand.P_Random() % 5) + 1) * 3;
//        game.map.P_LineAttack(actor, angle, MISSILERANGE, slope, damage);
    }

    @Override
    public void doAction(Player player, PSprite psp) {}
    
}
