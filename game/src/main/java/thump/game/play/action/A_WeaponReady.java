/*
     A_WeaponReady
     The player can fire the weapon
     or change to another weapon at this time.
     Follows after getting weapon up,
     or after previous attack/fire sequence.
 */
package thump.game.play.action;

import thump.base.FixedPoint;
import static thump.base.FixedPoint.FRACUNIT;
import static thump.base.Tables.FINEANGLES;
import static thump.base.Tables.FINEMASK;
import static thump.base.Tables.finecosine;
import static thump.base.Tables.finesine;
import static thump.game.Defines.WeaponType.*;
import static thump.game.Defines.weaponinfo;
import static thump.game.Event.BT_ATTACK;
import thump.game.Game;
import thump.game.Player;
import thump.game.maplevel.MapObject;
import thump.game.State;
import static thump.game.State.StateNum.*;
import thump.game.ThingStateLUT;
import thump.game.play.PSprite;
import static thump.game.play.PSprite.WEAPONTOP;

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
