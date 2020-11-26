/*
 * Fire Shotgun 2 Weapon Action
 */
package thump.game.play.action;

import thump.game.Player;
import thump.game.maplevel.MapObject;
import thump.game.play.PSprite;

/**
 *
 * @author mark
 */
public class A_FireShotgun2 implements Action {

    @Override
    public void doAction(MapObject mo) {}

    @Override
    public void doAction(Player player, PSprite psp) {
    //    int		i;
    //    angle_t	angle;
    //    int		damage;
    //		
    //	
    //    S_StartSound (player.mo, sfx_dshtgn);
    //    P_SetMobjState (player.mo, S_PLAY_ATK2);
    //
    //    player.ammo[weaponinfo[player.readyweapon].ammo]-=2;
    //
    //    P_SetPsprite (player,
    //		  ps_flash,
    //		  weaponinfo[player.readyweapon].flashstate);
    //
    //    P_BulletSlope (player.mo);
    //	
    //    for (i=0 ; i<20 ; i++)
    //    {
    //	damage = 5*(P_Random ()%3+1);
    //	angle = player.mo.angle;
    //	angle += (P_Random()-P_Random())<<19;
    //	P_LineAttack (player.mo,
    //		      angle,
    //		      MISSILERANGE,
    //		      bulletslope + ((P_Random()-P_Random())<<5), damage);
    //    }
    }
    
}
