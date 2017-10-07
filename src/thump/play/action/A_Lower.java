/*
     A_Lower
     Lowers current weapon,
      and changes weapon at bottom.
 */
package thump.play.action;

import thump.game.Player;
import thump.maplevel.MapObject;
import thump.play.PSprite;

/**
 *
 * @author mark
 */
public class A_Lower implements Action {

    @Override
    public void doAction(MapObject mo) {}

    @Override
    public void doAction(Player player, PSprite psp) {
    //    psp.sy += LOWERSPEED;
    //
    //    // Is already down.
    //    if (psp.sy < WEAPONBOTTOM )
    //	return;
    //
    //    // Player is dead.
    //    if (player.playerstate == PST_DEAD)
    //    {
    //	psp.sy = WEAPONBOTTOM;
    //
    //	// don't bring weapon back up
    //	return;		
    //    }
    //    
    //    // The old weapon has been lowered off the screen,
    //    // so change the weapon and start raising it
    //    if (!player.health)
    //    {
    //	// Player is dead, so keep the weapon off screen.
    //	P_SetPsprite (player,  ps_weapon, S_NULL);
    //	return;	
    //    }
    //	
    //    player.readyweapon = player.pendingweapon; 
    //
    //    P_BringUpWeapon (player);
    }
    
}
