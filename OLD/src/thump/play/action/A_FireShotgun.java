/*
 * Fire Shotgun Wepon Action
 */
package thump.play.action;

import thump.game.Player;
import thump.maplevel.MapObject;
import thump.play.PSprite;

/**
 *
 * @author mark
 */
public class A_FireShotgun implements Action {

    @Override
    public void doAction(MapObject mo) {}

    @Override
    public void doAction(Player player, PSprite psp) {
    //    int		i;
    //	
    //    S_StartSound (player.mo, sfx_shotgn);
    //    P_SetMobjState (player.mo, S_PLAY_ATK2);
    //
    //    player.ammo[weaponinfo[player.readyweapon].ammo]--;
    //
    //    P_SetPsprite (player,
    //		  ps_flash,
    //		  weaponinfo[player.readyweapon].flashstate);
    //
    //    P_BulletSlope (player.mo);
    //	
    //    for (i=0 ; i<7 ; i++)
    //	P_GunShot (player.mo, false);
    }
    
}
