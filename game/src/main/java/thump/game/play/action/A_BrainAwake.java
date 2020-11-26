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
public class A_BrainAwake implements Action {

    public A_BrainAwake() {
    }

    @Override
    public void doAction(MapObject mo) {
//        Thinker thinker;
//        MapObject m;
//        MapObject braintargets[] = new MapObject[32];  // Is this used somewhere globaly?
//        int numbraintargets;
//        int braintargeton;
//
//        // find all the target spots
//        numbraintargets = 0;
//        braintargeton = 0;
//
//        //thinker = game.thinkercap.getNextThinker();
//        for (thinker = game.thinkercap.getNextThinker();
//                thinker !=  game.thinkercap;
//                thinker = thinker.getNextThinker()) {
//            if (!(thinker.getFunction() instanceof T_MobjThinker)) {
//                continue;	// not a mobj
//            }
//            m = (MapObject) thinker;
//
//            if (m.type == MT_BOSSTARGET) {
//                braintargets[numbraintargets] = m;
//                numbraintargets++;
//            }
//        }
//
//        game.sound.S_StartSound(null, sfx_bossit);
    }

    @Override
    public void doAction(Player player, PSprite psp) {}
    
}
