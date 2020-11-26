/*
 * Fire ChainGun Weapon Action
 */
package thump.game.play.action;

import thump.game.Player;
import thump.game.maplevel.MapObject;
import thump.game.play.PSprite;

/**
 *
 * @author mark
 */
public class A_FireCGun implements Action {

    public A_FireCGun() {
    }

    @Override
    public void doAction(MapObject mo) {}

    @Override
    public void doAction(Player player, PSprite psp) {
    //    S_StartSound (player.mo, sfx_pistol);
    //
    //    if (!player.ammo[weaponinfo[player.readyweapon].ammo])
    //	return;
    //		
    //    P_SetMobjState (player.mo, S_PLAY_ATK2);
    //    player.ammo[weaponinfo[player.readyweapon].ammo]--;
    //
    //    P_SetPsprite (player,
    //		  ps_flash,
    //		  weaponinfo[player.readyweapon].flashstate
    //		  + psp.pstate
    //		  - &states[S_CHAIN1] );
    //
    //    P_BulletSlope (player.mo);
    //	
    //    P_GunShot (player.mo, !player.refire);
    }
    
}
