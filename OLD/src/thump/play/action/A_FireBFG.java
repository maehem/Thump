/*
 * Fire BFG Weapon Action
 */
package thump.play.action;

import thump.game.Player;
import thump.maplevel.MapObject;
import thump.play.PSprite;

/**
 *
 * @author mark
 */
public class A_FireBFG implements Action {

    @Override
    public void doAction(MapObject mo) {}

    @Override
    public void doAction(Player player, PSprite psp) {
    //    player.ammo[weaponinfo[player.readyweapon].ammo] -= BFGCELLS;
    //    P_SpawnPlayerMissile (player.mo, MT_BFG);
    }
    
}
