/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.game.play.action;

import static thump.base.Tables.ANG90;
import thump.game.Player;
import thump.game.maplevel.MapObject;
import thump.game.play.PSprite;

/**
 *
 * Mancubus attack, firing three missiles (bruisers) in three different
 * directions? Doesn't look like it.
 *
 * @author mark
 */
public class A_FatRaise implements Action {
    public static final int FATSPREAD = (int) (ANG90 / 8);

    public A_FatRaise() {
    }

    @Override
    public void doAction(MapObject actor) {
//        A_FaceTarget(actor);
//        game.sound.S_StartSound(actor, sfx_manatk);
    }

    @Override
    public void doAction(Player player, PSprite psp) {}
    
}
