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
public class A_BrainSpit implements Action {
    private boolean easy = false;

    public A_BrainSpit() {
    }

    @Override
    public void doAction(MapObject mo) {
//        MapObject targ;
//        MapObject newmobj;
//
//        easy = !easy;
//        if (game.gameskill.getValue() <= sk_easy.getValue() && (!easy)) {
//            return;
//        }
//
//        // shoot a cube at current target
//        targ = braintargets[braintargeton];
//        braintargeton = (braintargeton + 1) % numbraintargets;
//
//        // spawn brain missile
//        newmobj = MObject.P_SpawnMissile(mo, targ, MT_SPAWNSHOT);
//        newmobj.target = targ;
//        newmobj.reactiontime
//                = (int) (((targ.y - mo.y) / newmobj.momy) / newmobj.state.tics);
//
//        game.sound.S_StartSound(null, sfx_bospit);
    }

    @Override
    public void doAction(Player player, PSprite psp) {}
    
}
