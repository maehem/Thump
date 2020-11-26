/*
 * Fire Pistol Weapon Action
 */
package thump.game.play.action;

import thump.game.Player;
import thump.game.maplevel.MapObject;
import thump.game.play.PSprite;

/**
 *
 * @author mark
 */
public class A_FirePistol implements Action {

    @Override
    public void doAction(MapObject mo) {}

    @Override
    public void doAction(Player player, PSprite psp) {
    //    S_StartSound (player.mo, sfx_pistol);
    //
    //    P_SetMobjState (player.mo, S_PLAY_ATK2);
    //    player.ammo[weaponinfo[player.readyweapon].ammo]--;
    //
    //    P_SetPsprite (player,
    //		  ps_flash,
    //		  weaponinfo[player.readyweapon].flashstate);
    //
    //    P_BulletSlope (player.mo);
    //    P_GunShot (player.mo, !player.refire);
    }
    
}
