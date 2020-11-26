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
public class A_Fall implements Action {

    public A_Fall() {
    }

    @Override
    public void doAction(MapObject actor) {
//        // actor is on ground, it can be walked over
//        actor.flags &= ~MF_SOLID.getValue();
//
//        // So change this if corpse objects
//        // are meant to be obstacles.
    }

    @Override
    public void doAction(Player player, PSprite psp) {}
    
}
