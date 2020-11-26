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
public class A_KeenDie implements Action {

    public A_KeenDie() {
    }

    @Override
    public void doAction(MapObject mo) {
//        Thinker	th;
//        MapObject	mo2;
//        Line	junk = new Line();
//
//        A_Fall (mo);
//
//        // scan the remaining thinkers
//        // to see if all Keens are dead
//        for (th = game.thinkercap.getNextThinker() ; th != game.thinkercap ; th=th.getNextThinker()) {
//            if (!(th.getFunction() instanceof T_MobjThinker)) {
//                continue;
//            }
//
//            mo2 = (MapObject)th;
//            if (mo2 != mo
//                && mo2.type == mo.type
//                && mo2.health > 0)
//            {
//                // other Keen not dead
//                return;		
//            }
//        }
//
//        junk.tag = 666;
//        VDoor.EV_DoDoor(junk,open);
    }

    @Override
    public void doAction(Player player, PSprite psp) {}
    
}
