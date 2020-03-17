/*
     A_WeaponReady
     The player can fire the weapon
     or change to another weapon at this time.
     Follows after getting weapon up,
     or after previous attack/fire sequence.
 */
package thump.play.action;

import static thump.game.Event.BT_ATTACK;
import thump.game.Game;
import thump.game.Player;
import static thump.global.Defines.WeaponType.wp_bfg;
import static thump.global.Defines.WeaponType.wp_chainsaw;
import static thump.global.Defines.WeaponType.wp_missile;
import static thump.global.Defines.WeaponType.wp_nochange;
import static thump.global.Defines.weaponinfo;
import thump.global.FixedPoint;
import static thump.global.FixedPoint.FRACUNIT;
import thump.global.State;
import static thump.global.State.StateNum.S_PLAY_ATK1;
import static thump.global.State.StateNum.S_PLAY_ATK2;
import static thump.global.State.StateNum.S_SAW;
import static thump.global.Tables.FINEANGLES;
import static thump.global.Tables.FINEMASK;
import static thump.global.Tables.finecosine;
import static thump.global.Tables.finesine;
import thump.global.ThingStateLUT;
import thump.maplevel.MapObject;
import thump.play.PSprite;
import static thump.play.PSprite.WEAPONTOP;

/**
 *
 * @author mark
 */
public class A_WeaponReady implements Action {

    public A_WeaponReady() {
    }

    @Override
    public void doAction(MapObject mo) {}

    @Override
    public void doAction(Player player, PSprite psp) {
        State.StateNum newstate;
        int angle;

        State[] states = ThingStateLUT.states;
        
        // get out of attack state
        if (player.mo.state == states[S_PLAY_ATK1.ordinal()]
                || player.mo.state == states[S_PLAY_ATK2.ordinal()]) {
//            P_SetMobjState(player.mo, S_PLAY);
        }

        if (player.readyweapon == wp_chainsaw
                && psp.state == states[S_SAW.ordinal()]) {
//            S_StartSound(player.mo, sfx_sawidl);
        }

        // check for change
        //  if player is dead, put the weapon away
        if (player.pendingweapon != wp_nochange || player.health==0) {
            // change weapon
            //  (pending weapon should allready be validated)
            newstate = weaponinfo[player.readyweapon.ordinal()].downstate;
            PSprite.P_SetPsprite(player, PSprite.Num.ps_weapon.ordinal(), newstate);
            return;
        }

        // check for fire
        //  the missile launcher and bfg do not auto fire
        if ((player.cmd.buttons & BT_ATTACK)>0) {
            if (!player.attackdown
                    || (player.readyweapon != wp_missile
                    && player.readyweapon != wp_bfg)) {
                player.attackdown = true;
                PSprite.P_FireWeapon(player);
                return;
            }
        } else {
            player.attackdown = false;
        }

        // bob the weapon based on movement speed
        angle = (128 * Game.getInstance().leveltime) & FINEMASK;
        psp.sx = FRACUNIT + FixedPoint.mul(player.bob, finecosine(angle));
        angle &= FINEANGLES / 2 - 1;
        psp.sy = WEAPONTOP + FixedPoint.mul(player.bob, finesine(angle));
    }
    
}
