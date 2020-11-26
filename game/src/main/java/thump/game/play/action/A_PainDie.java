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
public class A_PainDie implements Action {

    public A_PainDie() {
    }

    @Override
    public void doAction(MapObject actor) {
//        A_Fall(actor);
//        A_PainShootSkull(actor, actor.angle + ANG90);
//        A_PainShootSkull(actor, actor.angle + ANG180);
//        A_PainShootSkull(actor, actor.angle + ANG270);
    }

    @Override
    public void doAction(Player player, PSprite psp) {}
    
}
