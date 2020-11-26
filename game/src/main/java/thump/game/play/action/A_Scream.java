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
public class A_Scream implements Action {

    public A_Scream() {
    }

    @Override
    public void doAction(MapObject actor) {
//        SfxEnum sound;
//
//        switch (actor.info.deathsound) {
//            case sfx_None:
//                return;
//
//            case sfx_podth1:
//            case sfx_podth2:
//            case sfx_podth3:
//                sound = SfxEnum.values()[sfx_podth1.ordinal() + Random.getInstance().P_Random() % 3];
//                break;
//
//            case sfx_bgdth1:
//            case sfx_bgdth2:
//                sound = SfxEnum.values()[sfx_bgdth1.ordinal() + Random.getInstance().P_Random() % 2];
//                break;
//
//            default:
//                sound = actor.info.deathsound;
//                break;
//        }
//
//        // Check for bosses.
//        if (actor.type == MT_SPIDER
//                || actor.type == MT_CYBORG) {
//            // full volume
//            game.sound.S_StartSound(null, sound);
//        } else {
//            game.sound.S_StartSound(actor, sound);
//        }
    }

    @Override
    public void doAction(Player player, PSprite psp) {}
    
}
