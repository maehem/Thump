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
public class A_BabyMetal implements Action {

    public A_BabyMetal() {
    }

    @Override
    public void doAction(MapObject mo) {
//        game.sound.S_StartSound(mo, sfx_bspwlk);
//        A_Chase(mo);
    }

    @Override
    public void doAction(Player player, PSprite psp) {}
    
}
