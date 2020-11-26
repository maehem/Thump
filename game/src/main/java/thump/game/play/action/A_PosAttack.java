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
public class A_PosAttack implements Action {

    public A_PosAttack() {
    }

    @Override
    public void doAction(MapObject actor) {
//        int angle;
//        int damage;
//        int slope;
//
//        if (null == actor.target) {
//            return;
//        }
//
//        Random rand = Random.getInstance();
//        A_FaceTarget(actor);
//        angle = actor.angle;
//        slope = game.map.P_AimLineAttack(actor, angle, MISSILERANGE);
//
//        game.sound.S_StartSound(actor, sfx_pistol);
//        angle += (rand.P_Random() - rand.P_Random()) << 20;
//        damage = ((rand.P_Random() % 5) + 1) * 3;
//        game.map.P_LineAttack(actor, angle, MISSILERANGE, slope, damage);
    }

    @Override
    public void doAction(Player player, PSprite psp) {}
    
}
