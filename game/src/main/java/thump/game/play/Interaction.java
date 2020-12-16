/*
 * Interaction/Collision
 */
package thump.game.play;

import java.util.Arrays;
import java.util.Objects;
import thump.base.FixedPoint;
import static thump.base.FixedPoint.FRACUNIT;
import static thump.base.Tables.ANG180;
import static thump.base.Tables.ANGLETOFINESHIFT;
import static thump.base.Tables.finecosine;
import static thump.base.Tables.finesine;
import thump.game.Defines;
import thump.game.Defines.AmmoType;
import static thump.game.Defines.AmmoType.*;
import thump.game.Defines.Card;
import static thump.game.Defines.Card.*;
import static thump.game.Defines.PowerDuration.*;
import thump.game.Defines.PowerType;
import static thump.game.Defines.PowerType.*;
import static thump.game.Defines.Skill.sk_baby;
import static thump.game.Defines.Skill.sk_nightmare;
import thump.game.Defines.WeaponType;
import static thump.game.Defines.WeaponType.*;
import static thump.game.Defines.weaponinfo;
import thump.game.Game;
import thump.game.MobJInfo;
import static thump.game.MobJInfo.Type.*;
import thump.game.Player;
import static thump.game.Player.Cheat.CF_GODMODE;
import thump.game.Player.PlayerState;
import static thump.game.State.StateNum.S_NULL;
import static thump.game.ThingStateLUT.states;
import thump.game.maplevel.MapObject;
import static thump.game.maplevel.MapObject.ONFLOORZ;
import static thump.game.play.Local.BASETHRESHOLD;
import static thump.game.play.Local.MAXHEALTH;
import thump.game.sound.sfx.Sounds;
import static thump.game.sound.sfx.Sounds.SfxEnum.sfx_getpow;
import static thump.game.sound.sfx.Sounds.SfxEnum.sfx_itemup;
import static thump.game.sound.sfx.Sounds.SfxEnum.sfx_wpnup;
import static thump.wad.map.Degenmobj.MobileObjectFlag.*;


/**
 *
 * @author mark
 */
public class Interaction {

    public static final int  BONUSADD	= 6;

    // a weapon is found with two clip loads,
    // a big item has five clip loads
    public static final int	maxammo[] = {200, 50, 300, 50};
    public static final int    clipammo[] = {10, 4, 20, 1};



    //
    // GET STUFF
    //

    //
    // P_GiveAmmo
    // Num is the number of clip loads,
    // not the individual count (0= 1/2 clip).
    // Returns false if the ammo can't be picked up at all
    //

    @SuppressWarnings("fallthrough")
    static boolean P_GiveAmmo(
            Player player,
            AmmoType ammo,
            int nnum) {
        
        int oldammo;
        int num = nnum;

        if (ammo == am_noammo) {
            return false;
        }

//        if (ammo < 0 || ammo > maxammo.length)
//            I_Error ("P_GiveAmmo: bad type %i", ammo);

        if ( Objects.equals(player.ammo[ammo.ordinal()], player.maxammo[ammo.ordinal()])  ) {
            return false;
        }

        if (num>0) {
            num *= clipammo[ammo.ordinal()];
        } else {
            num = clipammo[ammo.ordinal()]/2;
        }

        if (Game.getInstance().gameskill == sk_baby
            || Game.getInstance().gameskill == sk_nightmare)
        {
            // give double ammo in trainer mode,
            // you'll need in nightmare
            num <<= 1;
        }


        oldammo = player.ammo[ammo.ordinal()];
        player.ammo[ammo.ordinal()] += num;

        if (player.ammo[ammo.ordinal()] > player.maxammo[ammo.ordinal()]) {
            player.ammo[ammo.ordinal()] = player.maxammo[ammo.ordinal()];
        }

        // If non zero ammo, 
        // don't change up weapons,
        // player was lower on purpose.
        if (oldammo>0) {
            return true;
        }	

        // We were down to zero,
        // so select a new weapon.
        // Preferences are not user selectable.
        switch (ammo)
        {
          case am_clip:
            if (player.readyweapon == wp_fist)
            {
                if (player.weaponowned[wp_chaingun.ordinal()]) {
                    player.pendingweapon = wp_chaingun;
                } else {
                    player.pendingweapon = wp_pistol;
                }
            }
            break;

          case am_shell:
            if (player.readyweapon == wp_fist
                || player.readyweapon == wp_pistol)
            {
                if (player.weaponowned[wp_shotgun.ordinal()]) {
                    player.pendingweapon = wp_shotgun;
                }
            }
            break;

          case am_cell:
            if (player.readyweapon == wp_fist
                || player.readyweapon == wp_pistol)
            {
                if (player.weaponowned[wp_plasma.ordinal()]) {
                    player.pendingweapon = wp_plasma;
                }
            }
            break;

          case am_misl:
            if (player.readyweapon == wp_fist)
            {
                if (player.weaponowned[wp_missile.ordinal()]) {
                    player.pendingweapon = wp_missile;
                }
            }
          default:
            break;
        }

        return true;
    }

    //
    // P_GiveWeapon
    // The weapon name may have a MF_DROPPED flag ored in.
    //
    public static final boolean P_GiveWeapon(
            Player player,
            WeaponType weapon,
            boolean dropped) {
        boolean	gaveammo;
        boolean	gaveweapon;

        if (Game.getInstance().netgame
            && (Game.getInstance().deathmatch!=2)
             && !dropped )
        {
            // leave placed weapons forever on net games
            if (player.weaponowned[weapon.ordinal()]) {
                return false;
            }

            player.bonuscount += BONUSADD;
            player.weaponowned[weapon.ordinal()] = true;

            if (Game.getInstance().deathmatch>0) {
                P_GiveAmmo (player, weaponinfo[weapon.ordinal()].ammo, 5);
            } else {
                P_GiveAmmo (player, weaponinfo[weapon.ordinal()].ammo, 2);
            }
            player.pendingweapon = weapon;

            if (player == Game.getInstance().players[Game.getInstance().consoleplayer]) {
//                S_StartSound (NULL, sfx_wpnup);
            }
            return false;
        }

        if (weaponinfo[weapon.ordinal()].ammo != am_noammo) {
            // give one clip with a dropped weapon,
            // two clips with a found weapon
            if (dropped) {
                gaveammo = P_GiveAmmo (player, weaponinfo[weapon.ordinal()].ammo, 1);
            } else {
                gaveammo = P_GiveAmmo (player, weaponinfo[weapon.ordinal()].ammo, 2);
            }
        } else {
            gaveammo = false;
        }

        if (player.weaponowned[weapon.ordinal()]) {
            gaveweapon = false;
        } else
        {
            gaveweapon = true;
            player.weaponowned[weapon.ordinal()] = true;
            player.pendingweapon = weapon;
        }

        return (gaveweapon || gaveammo);
    }



    //
    // P_GiveBody
    // Returns false if the body isn't needed at all
    //
    public static final boolean P_GiveBody(
            Player player,
            int num) 
    {
        if (player.health >= MAXHEALTH) {
            return false;
        }

        player.health += num;
        if (player.health > MAXHEALTH) {
            player.health = MAXHEALTH;
        }
        player.mo.health = player.health;

        return true;
    }



    //
    // P_GiveArmor
    // Returns false if the armor is worse
    // than the current armor.
    //
    public static final boolean P_GiveArmor(
            Player player,
            int armortype) 
    {
        int hits;

        hits = armortype*100;
        if (player.armorpoints >= hits) {
            return false;	// don't pick up
        }

        player.armortype = armortype;
        player.armorpoints = hits;

        return true;
    }



    //
    // P_GiveCard
    //
    public static void P_GiveCard(Player player,  Card card) {
        if (player.cards[card.ordinal()]) {
            return;
        }

        player.bonuscount = BONUSADD;
        player.cards[card.ordinal()] = true;
    }

    //
    // P_GivePower
    //
    public static final boolean P_GivePower(
            Player  player,
            PowerType     power) 
    {
        
        if (power == pw_invulnerability) {
            player.powers[power.ordinal()] = INVULNTICS.getValue();
            return true;
        }

        if (power == pw_invisibility)
        {
            player.powers[power.ordinal()] = INVISTICS.getValue();
            player.mo.flags |= MF_SHADOW.getValue();
            return true;
        }

        if (power == pw_infrared)
        {
            player.powers[power.ordinal()] = INFRATICS.getValue();
            return true;
        }

        if (power == pw_ironfeet)
        {
            player.powers[power.ordinal()] = IRONTICS.getValue();
            return true;
        }

        if (power == pw_strength)
        {
            P_GiveBody (player, 100);
            player.powers[power.ordinal()] = 1;
            return true;
        }

        if (player.powers[power.ordinal()]>0) {
            return false;	// already got it
        }

        player.powers[power.ordinal()] = 1;
        return true;
    }




    //
    // P_TouchSpecialThing
    //
    public static void
    P_TouchSpecialThing
    ( MapObject	special,
      MapObject	toucher )
    {
        Player	player;
        int     i;
        int     delta;
        Sounds.SfxEnum     sound;

        delta = special.z - toucher.z;

        if (delta > toucher.height
            || delta < -8*FRACUNIT) {
            // out of reach
            return;
        }

        sound = sfx_itemup;	
        player = toucher.player;

        // Dead thing touching.
        // Can happen with a sliding player corpse.
        if (toucher.health <= 0) {
            return;
        }

        Game game = Game.getInstance();
        
        // Identify by sprite.
        switch (special.sprite) {
            // armor
            case SPR_ARM1:
                if (!P_GiveArmor(player, 1)) {
                    return;
                }
                player.message = ("GOTARMOR");
                break;

            case SPR_ARM2:
                if (!P_GiveArmor(player, 2)) {
                    return;
                }
                player.message = Game.getMessage("GOTMEGA");
                break;

            // bonus items
            case SPR_BON1:
                player.health++;		// can go over 100%
                if (player.health > 200) {
                    player.health = 200;
                }
                player.mo.health = player.health;
                player.message = Game.getMessage("GOTHTHBONUS");
                break;

            case SPR_BON2:
                player.armorpoints++;		// can go over 100%
                if (player.armorpoints > 200) {
                    player.armorpoints = 200;
                }
                if (player.armortype == 0) {
                    player.armortype = 1;
                }
                player.message = Game.getMessage("GOTARMBONUS");
                break;

            case SPR_SOUL:
                player.health += 100;
                if (player.health > 200) {
                    player.health = 200;
                }
                player.mo.health = player.health;
                player.message = Game.getMessage("GOTSUPER");
                sound = sfx_getpow;
                break;

            case SPR_MEGA:
                if (game.gameMode != Defines.GameMode.COMMERCIAL) {
                    return;
                }
                player.health = 200;
                player.mo.health = player.health;
                P_GiveArmor(player, 2);
                player.message = Game.getMessage("GOTMSPHERE");
                sound = sfx_getpow;
                break;

            // cards
            // leave cards for everyone
            case SPR_BKEY:
                if (!player.cards[it_bluecard.ordinal()]) {
                    player.message = Game.getMessage("GOTBLUECARD");
                }
                P_GiveCard(player, it_bluecard);
                if (!game.netgame) {
                    break;
                }
                return;

            case SPR_YKEY:
                if (!player.cards[it_yellowcard.ordinal()]) {
                    player.message = Game.getMessage("GOTYELWCARD");
                }
                P_GiveCard(player, it_yellowcard);
                if (!game.netgame) {
                    break;
                }
                return;

            case SPR_RKEY:
                if (!player.cards[it_redcard.ordinal()]) {
                    player.message = Game.getMessage("GOTREDCARD");
                }
                P_GiveCard(player, it_redcard);
                if (!game.netgame) {
                    break;
                }
                return;

            case SPR_BSKU:
                if (!player.cards[it_blueskull.ordinal()]) {
                    player.message = Game.getMessage("GOTBLUESKUL");
                }
                P_GiveCard(player, it_blueskull);
                if (!game.netgame) {
                    break;
                }
                return;

            case SPR_YSKU:
                if (!player.cards[it_yellowskull.ordinal()]) {
                    player.message = Game.getMessage("GOTYELWSKUL");
                }
                P_GiveCard(player, it_yellowskull);
                if (!game.netgame) {
                    break;
                }
                return;

            case SPR_RSKU:
                if (!player.cards[it_redskull.ordinal()]) {
                    player.message = Game.getMessage("GOTREDSKULL");
                }
                P_GiveCard(player, it_redskull);
                if (!game.netgame) {
                    break;
                }
                return;

            // medikits, heals
            case SPR_STIM:
                if (!P_GiveBody(player, 10)) {
                    return;
                }
                player.message = Game.getMessage("GOTSTIM");
                break;

            case SPR_MEDI:
                if (!P_GiveBody(player, 25)) {
                    return;
                }

                if (player.health < 25) {
                    player.message = Game.getMessage("GOTMEDINEED");
                } else {
                    player.message = Game.getMessage("GOTMEDIKIT");
                }
                break;

            // power ups
            case SPR_PINV:
                if (!P_GivePower(player, pw_invulnerability)) {
                    return;
                }
                player.message = Game.getMessage("GOTINVUL");
                sound = sfx_getpow;
                break;

            case SPR_PSTR:
                if (!P_GivePower(player, pw_strength)) {
                    return;
                }
                player.message = Game.getMessage("GOTBERSERK");
                if (player.readyweapon != wp_fist) {
                    player.pendingweapon = wp_fist;
                }
                sound = sfx_getpow;
                break;

            case SPR_PINS:
                if (!P_GivePower(player, pw_invisibility)) {
                    return;
                }
                player.message = Game.getMessage("GOTINVIS");
                sound = sfx_getpow;
                break;

            case SPR_SUIT:
                if (!P_GivePower(player, pw_ironfeet)) {
                    return;
                }
                player.message = Game.getMessage("GOTSUIT");
                sound = sfx_getpow;
                break;

            case SPR_PMAP:
                if (!P_GivePower(player, pw_allmap)) {
                    return;
                }
                player.message = Game.getMessage("GOTMAP");
                sound = sfx_getpow;
                break;

            case SPR_PVIS:
                if (!P_GivePower(player, pw_infrared)) {
                    return;
                }
                player.message = Game.getMessage("GOTVISOR");
                sound = sfx_getpow;
                break;

            // ammo
            case SPR_CLIP:
                if ((special.flags & MF_DROPPED.getValue()) > 0) {
                    if (!P_GiveAmmo(player, am_clip, 0)) {
                        return;
                    }
                } else if (!P_GiveAmmo(player, am_clip, 1)) {
                    return;
                }
                player.message = Game.getMessage("GOTCLIP");
                break;

            case SPR_AMMO:
                if (!P_GiveAmmo(player, am_clip, 5)) {
                    return;
                }
                player.message = Game.getMessage("GOTCLIPBOX");
                break;

            case SPR_ROCK:
                if (!P_GiveAmmo(player, am_misl, 1)) {
                    return;
                }
                player.message = Game.getMessage("GOTROCKET");
                break;

            case SPR_BROK:
                if (!P_GiveAmmo(player, am_misl, 5)) {
                    return;
                }
                player.message = Game.getMessage("GOTROCKBOX");
                break;

            case SPR_CELL:
                if (!P_GiveAmmo(player, am_cell, 1)) {
                    return;
                }
                player.message = Game.getMessage("GOTCELL");
                break;

            case SPR_CELP:
                if (!P_GiveAmmo(player, am_cell, 5)) {
                    return;
                }
                player.message = Game.getMessage("GOTCELLBOX");
                break;

            case SPR_SHEL:
                if (!P_GiveAmmo(player, am_shell, 1)) {
                    return;
                }
                player.message = Game.getMessage("GOTSHELLS");
                break;

            case SPR_SBOX:
                if (!P_GiveAmmo(player, am_shell, 5)) {
                    return;
                }
                player.message = Game.getMessage("GOTSHELLBOX");
                break;

            case SPR_BPAK:
                if (!player.backpack) {
                    for (i = 0; i < maxammo.length; i++) {
                        player.maxammo[i] *= 2;
                    }
                    player.backpack = true;
                }
                for (AmmoType t : AmmoType.values()) {
                    P_GiveAmmo(player, t, 1);
                }
                player.message = Game.getMessage("GOTBACKPACK");
                break;

            // weapons
            case SPR_BFUG:
                if (!P_GiveWeapon(player, wp_bfg, false)) {
                    return;
                }
                player.message = Game.getMessage("GOTBFG9000");
                sound = sfx_wpnup;
                break;

            case SPR_MGUN:
                if (!P_GiveWeapon(player, wp_chaingun, (special.flags & MF_DROPPED.getValue()) > 0)) {
                    return;
                }
                player.message = Game.getMessage("GOTCHAINGUN");
                sound = sfx_wpnup;
                break;

            case SPR_CSAW:
                if (!P_GiveWeapon(player, wp_chainsaw, false)) {
                    return;
                }
                player.message = Game.getMessage("GOTCHAINSAW");
                sound = sfx_wpnup;
                break;

            case SPR_LAUN:
                if (!P_GiveWeapon(player, wp_missile, false)) {
                    return;
                }
                player.message = Game.getMessage("GOTLAUNCHER");
                sound = sfx_wpnup;
                break;

            case SPR_PLAS:
                if (!P_GiveWeapon(player, wp_plasma, false)) {
                    return;
                }
                player.message = Game.getMessage("GOTPLASMA");
                sound = sfx_wpnup;
                break;

            case SPR_SHOT:
                if (!P_GiveWeapon(player, wp_shotgun, (special.flags & MF_DROPPED.getValue()) > 0)) {
                    return;
                }
                player.message = Game.getMessage("GOTSHOTGUN");
                sound = sfx_wpnup;
                break;

            case SPR_SGN2:
                if (!P_GiveWeapon(player, wp_supershotgun, (special.flags & MF_DROPPED.getValue()) > 0)) {
                    return;
                }
                player.message = Game.getMessage("GOTSHOTGUN2");
                sound = sfx_wpnup;
                break;

            default:
                //SystemInterface.I_Error("P_SpecialThing: Unknown gettable thing");
                thump.base.Defines.logger.severe("P_SpecialThing: Unknown gettable thing");
        }

        if ((special.flags & MF_COUNTITEM.getValue())>0 ) {
            player.itemcount++;
        }
        
        Game.getInstance().movingObject.P_RemoveMobj (special);
        player.bonuscount += BONUSADD;
        if (player == game.players[Game.getInstance().consoleplayer]) {
            game.sound.S_StartSound (null, sound);
        }
    }



    /**
     * KillMobj
     * 
     * @param source
     * @param target 
     */
    public static void P_KillMobj(
            MapObject	source,
            MapObject	target )
    {
        MobJInfo.Type	item;
        MapObject	mo;
        Player [] players = Game.getInstance().players;
        
        target.flags &= ~(MF_SHOOTABLE.getValue()|MF_FLOAT.getValue()|MF_SKULLFLY.getValue());

        if (target.type != MT_SKULL) {
            target.flags &= ~MF_NOGRAVITY.getValue();
        }

        target.flags |= MF_CORPSE.getValue()|MF_DROPOFF.getValue();
        target.height >>= 2;

        if (source!=null && source.player!=null) {
            // count for intermission
            if ((target.flags & MF_COUNTKILL.getValue())>0) {
                source.player.killcount++;
            }	

            if (target.player!=null) {
                source.player.frags[Arrays.asList(players).indexOf(target.player)]++;
                //source.player.frags[target.player-players]++;
            }
        } else if (Game.getInstance().netgame && (target.flags & MF_COUNTKILL.getValue())>0 ) {
            // count all monster deaths,
            // even those caused by other monsters
            players[0].killcount++;
        }

        if (target.player !=  null) {
            // count environment kills against you
            if (source==null) {
                target.player.frags[Arrays.asList(players).indexOf(target.player)]++;
            }

            target.flags &= ~MF_SOLID.getValue();
            target.player.playerstate = PlayerState.PST_DEAD;
            PSprite.P_DropWeapon (target.player);

            if (target.player == players[Game.getInstance().consoleplayer]
                && Game.getInstance().autoMap.automapactive)
            {
                // don't die in auto map,
                // switch view prior to dying
                Game.getInstance().autoMap.AM_Stop ();
            }

        }

        if (target.health < -target.info.spawnhealth 
            && target.info.xdeathstate.ordinal()>0) {
            MObject.P_SetMobjState (target, target.info.xdeathstate);
        } else {
            MObject.P_SetMobjState (target, target.info.deathstate);
        }
        
        target.tics -= Random.getInstance().P_Random()&3;

        if (target.tics < 1) {
            target.tics = 1;
        }

//	I_StartSound (&actor.r, actor.info.deathsound);


        // Drop stuff.
        // This determines the kind of object spawned
        // during the death frame of a thing.
        switch (target.type)
        {
          case MT_WOLFSS:
          case MT_POSSESSED:
            item = MT_CLIP;
            break;

          case MT_SHOTGUY:
            item = MT_SHOTGUN;
            break;

          case MT_CHAINGUY:
            item = MT_CHAINGUN;
            break;

          default:
            return;
        }

        mo = MObject.P_SpawnMobj (target.x,target.y,ONFLOORZ, item);
        mo.flags |= MF_DROPPED.getValue();	// special versions of items
    }




    //
    // P_DamageMobj
    // Damages both enemies and players
    // "inflictor" is the thing that caused the damage
    //  creature or missile, can be NULL (slime, etc)
    // "source" is the thing to target after taking damage
    //  creature or NULL
    // Source and inflictor are the same for melee attacks.
    // Source can be NULL for slime, barrel explosions
    // and other environmental stuff.
    //
    public static void P_DamageMobj ( 
            MapObject	target,
            MapObject	inflictor,
            MapObject	source,
            int 	_damage )
    {
        int     damage = _damage;
        long	ang;
        int		saved;
        Player	player;
        int	thrust;
        int	temp;

        if ( (target.flags & MF_SHOOTABLE.getValue())==0 ) {
            return;	// shouldn't happen...
        }

        if (target.health <= 0) {
            return;
        }

        if ( (target.flags & MF_SKULLFLY.getValue())>0 ) {
            target.momx = 0;
            target.momy = 0;
            target.momz = 0;
        }

        player = target.player;
        if (player!=null && Game.getInstance().gameskill == sk_baby) {
            damage >>= 1; 	// take half damage in trainer mode
        }


        // Some close combat weapons should not
        // inflict thrust and push the victim out of reach,
        // thus kick away unless using the chainsaw.
        if (inflictor!=null
            && (target.flags & MF_NOCLIP.getValue())==0
            && (source==null
                || source.player==null
                || source.player.readyweapon != wp_chainsaw))
        {
            ang = Game.getInstance().renderer.R_PointToAngle2 ( inflictor.x,
                                    inflictor.y,
                                    target.x,
                                    target.y);

            thrust = damage*(FRACUNIT>>3)*100/target.info.mass;

            // make fall forwards sometimes
            if ( damage < 40
                 && damage > target.health
                 && target.z - inflictor.z > 64*FRACUNIT
                 && (Random.getInstance().P_Random ()&1)>0 )
            {
                ang += ANG180;
                thrust *= 4;
            }

            ang >>= ANGLETOFINESHIFT;
            target.momx += FixedPoint.mul (thrust, finecosine(ang));
            target.momy += FixedPoint.mul (thrust, finesine(ang));
        }

        // player specific
        if (player!=null) {
            // end of game hell hack
            if (target.subsector.mapSector.sector.special == 11
                && damage >= target.health)
            {
                damage = target.health - 1;
            }


            // Below certain threshold,
            // ignore damage in GOD mode, or with INVUL power.
            if ( damage < 1000
                 && ( (player.cheats&CF_GODMODE.getValue())>0
                      || player.powers[pw_invulnerability.ordinal()]>0 ) )
            {
                return;
            }

            if (player.armortype>0) {
                if (player.armortype == 1) {
                    saved = damage/3;
                } else {
                    saved = damage/2;
                }

                if (player.armorpoints <= saved) {
                    // armor is used up
                    saved = player.armorpoints;
                    player.armortype = 0;
                }
                player.armorpoints -= saved;
                damage -= saved;
            }
            player.health -= damage; 	// mirror mobj health here for Dave
            if (player.health < 0) {
                player.health = 0;
            }

            player.attacker = source;
            player.damagecount += damage;	// add damage after armor / invuln

            if (player.damagecount > 100) {
                player.damagecount = 100;	// teleport stomp does 10k points...
            }

            temp = damage < 100 ? damage : 100;

            // Unused/blank method I_Tactile
//            if (player == Game.getInstance().players[Game.getInstance().consoleplayer]) {
//                I_Tactile (40,10,40+temp*2);
//            }
        }

        // do the damage	
        target.health -= damage;	
        if (target.health <= 0)
        {
            P_KillMobj (source, target);
            return;
        }

        if ( (Random.getInstance().P_Random () < target.info.painchance)
             && (target.flags&MF_SKULLFLY.getValue())==0 )
        {
            target.flags |= MF_JUSTHIT.getValue();	// fight back!

            MObject.P_SetMobjState (target, target.info.painstate);
        }

        target.reactiontime = 0;		// we're awake now...	

        if ( (target.threshold==0 || target.type == MT_VILE)
             && source!=null && source != target
             && source.type != MT_VILE)
        {
            // if not intent on another player,
            // chase after this one
            target.target = source;
            target.threshold = BASETHRESHOLD;
            if (target.state == states[target.info.spawnstate.ordinal()]
                && target.info.seestate != S_NULL) {
                MObject.P_SetMobjState (target, target.info.seestate);
            }
        }

    }

}
