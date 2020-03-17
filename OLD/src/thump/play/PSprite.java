/*

    Overlay psprites are scaled shapes
    drawn directly on the view screen,
    coordinates are given for a 320*200 view screen.

 */
package thump.play;

import thump.game.Game;
import thump.game.Player;
import thump.global.Defines;
import static thump.global.Defines.AmmoType.*;
import thump.global.Defines.GameMode;
import static thump.global.Defines.WeaponType.*;
import static thump.global.Defines.weaponinfo;
import thump.global.FixedPoint;
import static thump.global.FixedPoint.FRACBITS;
import static thump.global.FixedPoint.FRACUNIT;
import thump.global.Random;
import thump.global.State;
import thump.global.State.StateNum;
import static thump.global.State.StateNum.S_NULL;
import static thump.global.State.StateNum.S_PLAY_ATK1;
import static thump.global.Tables.FINEANGLES;
import static thump.global.Tables.FINEMASK;
import static thump.global.Tables.finesine;
import thump.global.ThingStateLUT;
import thump.maplevel.MapObject;
import static thump.play.Local.MISSILERANGE;
import static thump.play.PSprite.Num.ps_flash;
import static thump.play.PSprite.Num.ps_weapon;
import static thump.sound.sfx.Sounds.SfxEnum.sfx_sawup;

/**
 *
 * @author mark
 */
public class PSprite {
//
// Frame flags:
// handles maximum brightness (torches, muzzle flare, light sources)
//
    public static final int FF_FULLBRIGHT = 0x8000;	// flag in thing.frame
    public static final int FF_FRAMEMASK  = 0x7fff;
    public static final int LOWERSPEED = FRACUNIT*6;
    public static final int RAISESPEED = FRACUNIT*6;

    public static final int WEAPONBOTTOM = 128*FRACUNIT;
    public static final int WEAPONTOP = 32*FRACUNIT;


// plasma cells for a bfg attack
    public static final int BFGCELLS = 40;	

        
    public enum Num {
        ps_weapon,
        ps_flash
    }

    public State state;	// a NULL pstate means not active
    public long	tics;
    public int	sx;
    public int	sy;
    
    public int  swingx;
    public int  swingy;
    
    // Sets a slope so a near miss is at aproximately
    // the height of the intended target
    public int		bulletslope;

    //
    // P_SetPsprite
    //
    public static void P_SetPsprite(
            Player player,
            int position,
            State.StateNum _stnum) {
        PSprite	psp;
        State	pstate;
        State.StateNum stnum = _stnum;
    	
        psp = player.psprites[position];
    	
        do{
            if (stnum == S_NULL) {
                // object removed itself
                psp.state = null;
                break;	
            }

            pstate = ThingStateLUT.states[stnum.ordinal()];
            psp.state = pstate;
            psp.tics = pstate.tics;	// could be 0

            if (pstate.misc1>0)
            {
                // coordinate set
                psp.sx = (int) (pstate.misc1 << FRACBITS);
                psp.sy = (int) (pstate.misc2 << FRACBITS);
            }

            // Call action routine.
            // Modified handling.
            if (pstate.action!=null)
            {
                pstate.action.doAction(player, psp);
                if (psp.state==null) {
                    break;
                }
            }

            stnum = psp.state.nextstate;

        } while (psp.tics==0);
        // an initial pstate of 0 could cycle through
    }


    //
    // P_CalcSwing
    //	
    public void P_CalcSwing (Player player) {
        int	swing;
        int		angle;
    	
        // OPTIMIZE: tablify this.
        // A LUT would allow for different modes,
        //  and add flexibility.
    
        swing = player.bob;
    
        angle = (FINEANGLES/70*Game.getInstance().leveltime)&FINEMASK;
        swingx = FixedPoint.mul ( swing, finesine(angle));
    
        angle = (FINEANGLES/70*Game.getInstance().leveltime+FINEANGLES/2)&FINEMASK;
        swingy = -FixedPoint.mul ( swingx, finesine(angle));
    }



    //
    // P_BringUpWeapon
    // Starts bringing the pending weapon up
    // from the bottom of the screen.
    // Uses player
    //
    public static void P_BringUpWeapon (Player player) {
        StateNum	newstate;
    	
        if (player.pendingweapon == wp_nochange) {
            player.pendingweapon = player.readyweapon;
        }
    		
        if (player.pendingweapon == wp_chainsaw) {
            Game.getInstance().sound.S_StartSound (player.mo, sfx_sawup);
        }
    		
        newstate = weaponinfo[player.pendingweapon.ordinal()].upstate;
    
        player.pendingweapon = wp_nochange;
        player.psprites[ps_weapon.ordinal()].sy = WEAPONBOTTOM;
    
        P_SetPsprite (player, ps_weapon.ordinal(), newstate);
    }

    //
    // P_CheckAmmo
    // Returns true if there is enough ammo to shoot.
    // If not, selects the next weapon to use.
    //
    public static boolean P_CheckAmmo(Player player) {
        Defines.AmmoType ammo;
        int count = 0;

        ammo = weaponinfo[player.readyweapon.ordinal()].ammo;

        if (null != player.readyweapon) { // Minimal amount for one shot varies.
            switch (player.readyweapon) {
                case wp_bfg:
                    count = BFGCELLS;
                    break;
                case wp_supershotgun:
                    count = 2;	// Double barrel.
                    break;
                default:
                    count = 1;	// Regular.
                    break;
            }
        }
        // Some do not need ammunition anyway.
        // Return if current ammunition sufficient.
        if (ammo == am_noammo || player.ammo[ammo.ordinal()] >= count) {
            return true;
        }

        GameMode gameMode = Game.getInstance().gameMode;

        // Out of ammo, pick a weapon to change to.
        // Preferences are set here.
        do {
            if (player.weaponowned[wp_plasma.ordinal()]
                    && player.ammo[am_cell.ordinal()] > 0
                    && (gameMode != GameMode.SHAREWARE)) {
                player.pendingweapon = wp_plasma;
            } else if (player.weaponowned[wp_supershotgun.ordinal()]
                    && player.ammo[am_shell.ordinal()] > 2
                    && (gameMode == GameMode.COMMERCIAL)) {
                player.pendingweapon = wp_supershotgun;
            } else if (player.weaponowned[wp_chaingun.ordinal()]
                    && player.ammo[am_clip.ordinal()] > 0) {
                player.pendingweapon = wp_chaingun;
            } else if (player.weaponowned[wp_shotgun.ordinal()]
                    && player.ammo[am_shell.ordinal()] > 0) {
                player.pendingweapon = wp_shotgun;
            } else if (player.ammo[am_clip.ordinal()] > 0) {
                player.pendingweapon = wp_pistol;
            } else if (player.weaponowned[wp_chainsaw.ordinal()]) {
                player.pendingweapon = wp_chainsaw;
            } else if (player.weaponowned[wp_missile.ordinal()]
                    && player.ammo[am_misl.ordinal()] > 0) {
                player.pendingweapon = wp_missile;
            } else if (player.weaponowned[wp_bfg.ordinal()]
                    && player.ammo[am_cell.ordinal()] > 40
                    && (gameMode != GameMode.SHAREWARE)) {
                player.pendingweapon = wp_bfg;
            } else {
                // If everything fails.
                player.pendingweapon = wp_fist;
            }

        } while (player.pendingweapon == wp_nochange);

        // Now set appropriate weapon overlay.
        P_SetPsprite(player,
                ps_weapon.ordinal(),
                weaponinfo[player.readyweapon.ordinal()].downstate);

        return false;
    }


    //
    // P_FireWeapon.
    //
    public static void P_FireWeapon (Player player) {
        State.StateNum	newstate;
    	
        if (!P_CheckAmmo (player)) {
            return;
        }
    	
        MObject.P_SetMobjState (player.mo, S_PLAY_ATK1);
        newstate = weaponinfo[player.readyweapon.ordinal()].atkstate;
        P_SetPsprite (player, ps_weapon.ordinal(), newstate);
        Game.getInstance().enemy.P_NoiseAlert (player.mo, player.mo);
    }



    //
    // P_DropWeapon
    // Player died, so put the weapon away.
    //
    public static void P_DropWeapon (Player player)
    {
        P_SetPsprite (player,
    		  ps_weapon.ordinal(),
    		  weaponinfo[player.readyweapon.ordinal()].downstate);
    }


/*     Actions are handled in their own play.action objects.
    //
    // A_WeaponReady
    // The player can fire the weapon
    // or change to another weapon at this time.
    // Follows after getting weapon up,
    // or after previous attack/fire sequence.
    //
    public void A_WeaponReady(Player player, PSprite psp) {
        StateNum newstate;
        int angle;

        // get out of attack pstate
        if (player.mo.state ==  states[S_PLAY_ATK1.ordinal()]
                || player.mo.state ==  states[S_PLAY_ATK2.ordinal()]) {
            MObject.P_SetMobjState(player.mo, S_PLAY);
        }

        if (player.readyweapon == wp_chainsaw
                && psp.state == states[S_SAW.ordinal()]) {
            Game.getInstance().sound.S_StartSound(player.mo, sfx_sawidl);
        }

        // check for change
        //  if player is dead, put the weapon away
        if (player.pendingweapon != wp_nochange || 0==player.health) {
            // change weapon
            //  (pending weapon should allready be validated)
            newstate = weaponinfo[player.readyweapon.ordinal()].downstate;
            P_SetPsprite(player, ps_weapon, newstate);
            return;
        }

        // check for fire
        //  the missile launcher and bfg do not auto fire
        if ((player.cmd.buttons & BT_ATTACK)>0) {
            if (!player.attackdown
                    || (player.readyweapon != wp_missile
                    && player.readyweapon != wp_bfg)) {
                player.attackdown = true;
                P_FireWeapon(player);
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

    //
    // A_ReFire
    // The player can re-fire the weapon
    // without lowering it entirely.
    //
    public static void A_ReFire(Player player, PSprite psp) {

        // check for fire
        //  (if a weaponchange is pending, let it go through instead)
        if ((player.cmd.buttons & BT_ATTACK) > 0
                && player.pendingweapon == wp_nochange
                && player.health > 0) {
            player.refire++;
            P_FireWeapon(player);
        } else {
            player.refire = 0;
            P_CheckAmmo(player);
        }
    }


    public void A_CheckReload( Player player, PSprite psp ) {
        P_CheckAmmo (player);
    //#if 0
    //    if (player.ammo[am_shell]<2)
    //	P_SetPsprite (player, ps_weapon, S_DSNR1);
    //#endif
    }

    //
    // A_Lower
    // Lowers current weapon,
    //  and changes weapon at bottom.
    //
    public void A_Lower( Player player, PSprite psp ) {	
        psp.sy += LOWERSPEED;

        // Is already down.
        if (psp.sy < WEAPONBOTTOM) {
            return;
        }

        // Player is dead.
        if (player.playerstate == PST_DEAD) {
            psp.sy = WEAPONBOTTOM;

            // don't bring weapon back up
            return;
        }

        // The old weapon has been lowered off the screen,
        // so change the weapon and start raising it
        if (0==player.health) {
            // Player is dead, so keep the weapon off screen.
            P_SetPsprite(player, ps_weapon, S_NULL);
            return;
        }

        player.readyweapon = player.pendingweapon;

        P_BringUpWeapon(player);
    }

    //
    // A_Raise
    //
    public void A_Raise( Player player, PSprite psp ) {
        StateNum	newstate;
    	
        psp.sy -= RAISESPEED;
    
        if (psp.sy > WEAPONTOP )
    	return;
        
        psp.sy = WEAPONTOP;
        
        // The weapon has been raised all the way,
        //  so change to the ready pstate.
        newstate = weaponinfo[player.readyweapon.ordinal()].readystate;
    
        P_SetPsprite (player, ps_weapon, newstate);
    }


    //
    // A_GunFlash
    //
    public void A_GunFlash( Player	player, PSprite psp ) {
        MObject.P_SetMobjState (player.mo, S_PLAY_ATK2);
        P_SetPsprite (player,ps_flash,weaponinfo[player.readyweapon.ordinal()].flashstate);
    }

    //
    // WEAPON ATTACKS
    //

    //
    // A_Punch
    //
    public void A_Punch(Player player, PSprite psp) {
        int angle;
        int damage;
        int slope;

        damage = (Random.getInstance().P_Random() % 10 + 1) << 1;

        if (player.powers[pw_strength.ordinal()]>0) {
            damage *= 10;
        }

        angle = player.mo.angle;
        angle += (Random.getInstance().P_Random() - Random.getInstance().P_Random()) << 18;
        slope = Game.getInstance().map.P_AimLineAttack(player.mo, angle, MELEERANGE);
        Game.getInstance().map.P_LineAttack(player.mo, angle, MELEERANGE, slope, damage);

        // turn to face target
        if (Game.getInstance().map.linetarget!=null) {
            Game.getInstance().sound.S_StartSound(player.mo, sfx_punch);
            player.mo.angle = Game.getInstance().renderer.R_PointToAngle2(player.mo.x,
                    player.mo.y,
                    Game.getInstance().map.linetarget.x,
                    Game.getInstance().map.linetarget.y);
        }
    }


    //
    // A_Saw
    //
    public void A_Saw(Player player, PSprite psp) {
        int angle;
        int damage;
        int slope;

        damage = 2 * (Random.getInstance().P_Random() % 10 + 1);
        angle = player.mo.angle;
        angle += (Random.getInstance().P_Random() - Random.getInstance().P_Random()) << 18;
        Map map = Game.getInstance().map;

        // use meleerange + 1 se the puff doesn't skip the flash
        slope = map.P_AimLineAttack(player.mo, angle, MELEERANGE + 1);
        map.P_LineAttack(player.mo, angle, MELEERANGE + 1, slope, damage);

        if (null == map.linetarget) {
            Game.getInstance().sound.S_StartSound(player.mo, sfx_sawful);
            return;
        }
        Game.getInstance().sound.S_StartSound(player.mo, sfx_sawhit);

        // turn to face target
        angle = Game.getInstance().renderer.R_PointToAngle2(player.mo.x, player.mo.y,
                map.linetarget.x, map.linetarget.y);
        if (angle - player.mo.angle > ANG180) {
            if (angle - player.mo.angle < -ANG90 / 20) {
                player.mo.angle = angle + ANG90 / 21;
            } else {
                player.mo.angle -= ANG90 / 20;
            }
        } else if (angle - player.mo.angle > ANG90 / 20) {
            player.mo.angle = angle - ANG90 / 21;
        } else {
            player.mo.angle += ANG90 / 20;
        }
        player.mo.flags |= MF_JUSTATTACKED.getValue();
    }


    //
    // A_FireMissile
    //
    public void A_FireMissile( Player player, PSprite psp ) {
        player.ammo[weaponinfo[player.readyweapon.ordinal()].ammo.ordinal()]--;
        MObject.P_SpawnPlayerMissile (player.mo, MT_ROCKET);
    }


    //
    // A_FireBFG
    //
    public void A_FireBFG( Player	player, PSprite	psp ) {
        player.ammo[weaponinfo[player.readyweapon.ordinal()].ammo.ordinal()] -= BFGCELLS;
        MObject.P_SpawnPlayerMissile (player.mo, MT_BFG);
    }


    //
    // A_FirePlasma
    //
    public void A_FirePlasma( Player player, PSprite psp ) {
        player.ammo[weaponinfo[player.readyweapon.ordinal()].ammo.ordinal()]--;
    
        P_SetPsprite (player,
    		  ps_flash,
    		  weaponinfo[player.readyweapon.ordinal()].flashstate.ordinal()+(Random.getInstance().P_Random ()&1) );
    
        MObject.P_SpawnPlayerMissile (player.mo, MT_PLASMA);
    }

    //
    // A_FirePistol
    //
    public void A_FirePistol( Player player, PSprite psp ) {
        Game.getInstance().sound.S_StartSound (player.mo, sfx_pistol);
    
        MObject.P_SetMobjState (player.mo, S_PLAY_ATK2);
        player.ammo[weaponinfo[player.readyweapon.ordinal()].ammo.ordinal()]--;
    
        P_SetPsprite (player,
    		  ps_flash,
    		  weaponinfo[player.readyweapon.ordinal()].flashstate);
    
        P_BulletSlope (player.mo);
        psp.P_GunShot (player.mo, (player.refire <= 0));
    }


    //
    // A_FireShotgun
    //
    void A_FireShotgun( Player player, PSprite psp ) {
        int		i;
    	
        Game.getInstance().sound.S_StartSound (player.mo, sfx_shotgn);
        MObject.P_SetMobjState (player.mo, S_PLAY_ATK2);
    
        player.ammo[weaponinfo[player.readyweapon.ordinal()].ammo.ordinal()]--;
    
        P_SetPsprite (player,
    		  ps_flash,
    		  weaponinfo[player.readyweapon.ordinal()].flashstate);
    
        P_BulletSlope (player.mo);
    	
        for (i=0 ; i<7 ; i++) {
            P_GunShot (player.mo, false);
        }
    }


    //
    // A_FireShotgun2
    //
    void A_FireShotgun2(Player player, PSprite psp) {
        int i;
        int angle;
        int damage;

        Game.getInstance().sound.S_StartSound(player.mo, sfx_dshtgn);
        MObject.P_SetMobjState(player.mo, S_PLAY_ATK2);

        player.ammo[weaponinfo[player.readyweapon.ordinal()].ammo.ordinal()] -= 2;

        P_SetPsprite(player,
                ps_flash,
                weaponinfo[player.readyweapon.ordinal()].flashstate);

        P_BulletSlope(player.mo);

        for (i = 0; i < 20; i++) {
            damage = 5 * (Random.getInstance().P_Random() % 3 + 1);
            angle = player.mo.angle;
            angle += (Random.getInstance().P_Random() - Random.getInstance().P_Random()) << 19;
            Game.getInstance().map.P_LineAttack(player.mo,
                    angle,
                    MISSILERANGE,
                    bulletslope + ((Random.getInstance().P_Random() - Random.getInstance().P_Random()) << 5), damage);
        }
    }


    //
    // A_FireCGun
    //
    void A_FireCGun( Player player, PSprite psp ) {
        Game.getInstance().sound.S_StartSound (player.mo, sfx_pistol);
    
        if (0==player.ammo[weaponinfo[player.readyweapon.ordinal()].ammo.ordinal()]) {
            return;
        }
    		
        MObject.P_SetMobjState (player.mo, S_PLAY_ATK2);
        player.ammo[weaponinfo[player.readyweapon.ordinal()].ammo.ordinal()]--;
    
        P_SetPsprite (player,
    		  ps_flash,
    		  weaponinfo[player.readyweapon.ordinal()].flashstate
    		  + psp.state
    		  - states[S_CHAIN1] );
    
        P_BulletSlope (player.mo);
    	
        P_GunShot (player.mo, player.refire==0);
    }


    //
    // ?
    //
    void A_Light0 (Player player, PSprite psp) {
        player.extralight = 0;
    }

    void A_Light1 (Player player, PSprite psp) {
        player.extralight = 1;
    }

    void A_Light2 (Player player, PSprite psp) {
        player.extralight = 2;
    }
    
    
    //
    // A_BFGsound
    //
    void A_BFGsound( Player	player, PSprite psp ){
        Game.getInstance().sound.S_StartSound (player.mo, sfx_bfg);
    }

    //
    // A_BFGSpray
    // Spawn a BFG explosion on every monster in view
    //
    void A_BFGSpray(MapObject mo) {
        int			i;
        int			j;
        int			damage;
        int		an;
    	
        // offset angles from its attack angle
        for (i=0 ; i<40 ; i++)
        {
    	an = mo.angle - ANG90/2 + ANG90/40*i;
        Map map = Game.getInstance().map;
    	// mo.target is the originator (player)
    	//  of the missile
    	map.P_AimLineAttack (mo.target, an, 16*64*FRACUNIT);
    
    	if (null==map.linetarget) {
            continue;
            }
    
    	MObject.P_SpawnMobj (map.linetarget.x,
    		     map.linetarget.y,
    		     map.linetarget.z + (map.linetarget.height>>2),
    		     MT_EXTRABFG);
    	
    	damage = 0;
    	for (j=0;j<15;j++) {
            damage += (Random.getInstance().P_Random()&7) + 1;
            }
    
    	Interaction.P_DamageMobj (map.linetarget, mo.target,mo.target, damage);
        }
    }

*/

    //
    // P_BulletSlope
    //
    public void P_BulletSlope (MapObject mo){
        long	an;
        
        // see which target is to be aimed at
        an = mo.angle;
        bulletslope = Game.getInstance().map.P_AimLineAttack (mo, an, 16*64*FRACUNIT);
    
        if (null==Game.getInstance().map.linetarget)
        {
    	an += 1<<26;
    	bulletslope = Game.getInstance().map.P_AimLineAttack (mo, an, 16*64*FRACUNIT);
    	if (null==Game.getInstance().map.linetarget)
    	{
    	    an -= 2<<26;
    	    bulletslope = Game.getInstance().map.P_AimLineAttack (mo, an, 16*64*FRACUNIT);
    	}
        }
    }


    //
    // P_GunShot
    //
    public void P_GunShot( MapObject	mo, boolean accurate ) {
        long	angle;
        int		damage;
    	
        damage = 5*(Random.getInstance().P_Random ()%3+1);
        angle = mo.angle;
    
        if (!accurate) {
            angle += (Random.getInstance().P_Random()-Random.getInstance().P_Random())<<18;
        }
    
        Game.getInstance().map.P_LineAttack (mo, angle, MISSILERANGE, bulletslope, damage);
    }



    //
    // P_SetupPsprites
    // Called at start of level for each player.
    //
    public static void P_SetupPsprites (Player player) {
    	
        // remove all psprites
        for (PSprite psprite : player.psprites) {
            psprite.state = null;
        }
    		
        // spawn the gun
        player.pendingweapon = player.readyweapon;
        P_BringUpWeapon (player);
    }


    //
    // P_MovePsprites
    // Called every tic by player thinking routine.
    //
    public static void P_MovePsprites(Player player) {
        int i=0;
        //PSprite psp;
        //State state;

        //psp = player.psprites[0];
        for (PSprite psp : player.psprites) {
        //for (i = 0; i < NUMPSPRITES; i++, psp++) {
            // a null pstate means not active
            if (psp.state!=null) {
                // drop tic count and possibly change pstate

                // a -1 tic count never changes
                if (psp.tics != -1) {
                    psp.tics--;
                    if (0==psp.tics) {
                        P_SetPsprite(player, i, psp.state.nextstate);
                    }
                }
            }
            i++;
        }

        player.psprites[ps_flash.ordinal()].sx = player.psprites[ps_weapon.ordinal()].sx;
        player.psprites[ps_flash.ordinal()].sy = player.psprites[ps_weapon.ordinal()].sy;
    }

}
