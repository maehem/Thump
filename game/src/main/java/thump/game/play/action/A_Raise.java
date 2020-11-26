/*
 * A_Raise
 */
package thump.game.play.action;

import static thump.base.FixedPoint.FRACUNIT;
import thump.game.Player;
import thump.game.maplevel.MapObject;
import thump.game.play.PSprite;

/**
 *
 * @author mark
 */
public class A_Raise implements Action {
    public static final int RAISESPEED = FRACUNIT*6;

    @Override
    public void doAction(MapObject mo) {}

    @Override
    public void doAction(Player player, PSprite psp) {
    //    statenum_t	newstate;
    //	
    //    psp.sy -= RAISESPEED;
    //
    //    if (psp.sy > WEAPONTOP )
    //	return;
    //    
    //    psp.sy = WEAPONTOP;
    //    
    //    // The weapon has been raised all the way,
    //    //  so change to the ready pstate.
    //    newstate = weaponinfo[player.readyweapon].readystate;
    //
    //    P_SetPsprite (player, ps_weapon, newstate);
    }
    
}
