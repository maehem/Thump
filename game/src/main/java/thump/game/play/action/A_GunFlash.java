/*
 * Gun Flash Player Action
 */
package thump.game.play.action;

import thump.game.Player;
import thump.game.maplevel.MapObject;
import thump.game.play.PSprite;

/**
 *
 * @author mark
 */
public class A_GunFlash implements Action {

    @Override
    public void doAction(MapObject mo) {}

    @Override
    public void doAction(Player player, PSprite psp) {
    //    P_SetMobjState (player.mo, S_PLAY_ATK2);
    //    P_SetPsprite (player,ps_flash,weaponinfo[player.readyweapon].flashstate);
    }
    
}
