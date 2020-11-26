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
public class A_Look implements Action {

    public A_Look() {
    }

    @Override
    public void doAction(MapObject actor) {
//        MapObject targ;
//
//        actor.threshold = 0;	// any shot will wake up
//        targ = actor.subsector.sector.soundtarget;
//
//        boolean seeyou = false;
//
//        if (targ != null && (targ.flags & MF_SHOOTABLE.getValue()) > 0) {
//            actor.target = targ;
//
//            if ((actor.flags & MF_AMBUSH.getValue()) > 0) {
//                if (game.sight.P_CheckSight(actor, actor.target)) {
//                    //goto seeyou;
//                    seeyou = true;
//                }
//            } else {
//                //goto seeyou;
//                seeyou = true;
//            }
//        }
//
//        if (!seeyou && !P_LookForPlayers(actor, false)) {
//            return;
//        }
//
//        // go into chase state
//        //seeyou:
//        if (actor.info.seesound != Sounds.SfxEnum.sfx_None) {
//            SfxEnum sound;
//
//            switch (actor.info.seesound) {
//                case sfx_posit1:
//                case sfx_posit2:
//                case sfx_posit3:
//                    sound = SfxEnum.values()[sfx_posit1.ordinal() + Random.getInstance().P_Random() % 3];
//                    break;
//
//                case sfx_bgsit1:
//                case sfx_bgsit2:
//                    sound = SfxEnum.values()[sfx_bgsit1.ordinal() + Random.getInstance().P_Random() % 2];
//                    break;
//
//                default:
//                    sound = actor.info.seesound;
//                    break;
//            }
//
//            if (actor.type == MT_SPIDER || actor.type == MT_CYBORG) {
//                // full volume
//                game.sound.S_StartSound(null, sound);
//            } else {
//                game.sound.S_StartSound(actor, sound);
//            }
//        }
//
//        MObject.P_SetMobjState(actor, actor.info.seestate);
    }

    @Override
    public void doAction(Player player, PSprite psp) {}
    
}
