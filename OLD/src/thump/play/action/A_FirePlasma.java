/*
 * Fire Plasma Weapon Action
 */
package thump.play.action;

import thump.game.Player;
import static thump.global.Defines.weaponinfo;
import static thump.global.MobJInfo.Type.MT_PLASMA;
import thump.global.Random;
import thump.global.State.StateNum;
import thump.maplevel.MapObject;
import thump.play.MObject;
import thump.play.PSprite;
import static thump.play.PSprite.Num.ps_flash;

/**
 *
 * @author mark
 */
public class A_FirePlasma implements Action {

    @Override
    public void doAction(MapObject mo) {}

    @Override
    public void doAction(Player player, PSprite psp) {
        player.ammo[weaponinfo[player.readyweapon.ordinal()].ammo.ordinal()]--;
    
        PSprite.P_SetPsprite (player,
    		  ps_flash.ordinal(),
                  StateNum.values()[
                    weaponinfo[player.readyweapon.ordinal()].flashstate.ordinal()+(Random.getInstance().P_Random ()&1)
                  ] );
    
        MObject.P_SpawnPlayerMissile (player.mo, MT_PLASMA);
    }
    
}
