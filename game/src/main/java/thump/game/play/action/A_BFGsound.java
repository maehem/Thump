/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.game.play.action;

import thump.game.Game;
import thump.game.Player;
import thump.game.maplevel.MapObject;
import thump.game.play.PSprite;
import static thump.game.sound.sfx.Sounds.SfxEnum.sfx_bfg;

/**
 *
 * @author mark
 */
public class A_BFGsound implements Action {

    @Override
    public void doAction(MapObject mo) {}

    @Override
    public void doAction(Player player, PSprite psp) {
        Game.getInstance().sound.S_StartSound (player.mo, sfx_bfg);
    }
    
}
