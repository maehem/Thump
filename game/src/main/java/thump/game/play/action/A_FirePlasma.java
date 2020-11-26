/*
 * Fire Plasma Weapon Action
 */
package thump.game.play.action;

import thump.game.maplevel.MapObject;
import thump.game.Player;
import thump.game.play.PSprite;

/**
 *
 * @author mark
 */
public class A_FirePlasma implements Action {

    @Override
    public void doAction(MapObject mo) {}

    @Override
    public void doAction(Player player, PSprite psp) {
//        player.ammo[weaponinfo[player.readyweapon.ordinal()].ammo.ordinal()]--;
//    
//        PSprite.P_SetPsprite (player,
//    		  ps_flash.ordinal(),
//                  StateNum.values()[
//                    weaponinfo[player.readyweapon.ordinal()].flashstate.ordinal()+(Random.getInstance().P_Random ()&1)
//                  ] );
//    
//        MObject.P_SpawnPlayerMissile (player.mo, MT_PLASMA);
    }
    
}
