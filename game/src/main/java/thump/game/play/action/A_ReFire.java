/*
     A_ReFire
     The player can re-fire the weapon
     without lowering it entirely.
*/
package thump.game.play.action;

import thump.game.Player;
import thump.game.maplevel.MapObject;
import thump.game.play.PSprite;

/**
 *
 * @author mark
 */
public class A_ReFire implements Action {

    public A_ReFire() {
    }

    @Override
    public void doAction(MapObject mo) {}

    @Override
    public void doAction(Player player, PSprite psp) {
    //    
    //    // check for fire
    //    //  (if a weaponchange is pending, let it go through instead)
    //    if ( (player.cmd.buttons & BT_ATTACK) 
    //	 && player.pendingweapon == wp_nochange
    //	 && player.health)
    //    {
    //	player.refire++;
    //	P_FireWeapon (player);
    //    }
    //    else
    //    {
    //	player.refire = 0;
    //	P_CheckAmmo (player);
    //    }
    }
    
}
