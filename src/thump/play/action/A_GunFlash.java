/*
 * Gun Flash Player Action
 */
package thump.play.action;

import thump.game.Player;
import thump.maplevel.MapObject;
import thump.play.PSprite;

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
