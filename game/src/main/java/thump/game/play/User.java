/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.game.play;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import static thump.base.Defines.logger;
import static thump.game.Event.BT_CHANGE;
import static thump.game.Event.BT_SPECIAL;
import static thump.game.Event.BT_USE;
import static thump.game.Event.BT_WEAPONMASK;
import static thump.game.Event.BT_WEAPONSHIFT;
import thump.game.Game;
import thump.game.Player;
import static thump.game.Player.Cheat.*;
import static thump.game.Player.PlayerState.PST_DEAD;
import static thump.game.Player.PlayerState.PST_LIVE;
import static thump.game.Player.PlayerState.PST_REBORN;
import thump.game.TickCommand;
import static thump.game.Defines.PowerType.*;
import thump.game.Defines.WeaponType;
import static thump.game.Defines.WeaponType.*;
import thump.base.FixedPoint;
import static thump.base.FixedPoint.FRACUNIT;
import static thump.game.State.StateNum.S_PLAY;
import static thump.game.State.StateNum.S_PLAY_RUN1;
import static thump.base.Tables.ANG180;
import static thump.base.Tables.ANG90;
import static thump.base.Tables.ANGLETOFINESHIFT;
import static thump.base.Tables.FINEANGLES;
import static thump.base.Tables.FINEMASK;
import static thump.base.Tables.finecosine;
import static thump.base.Tables.finesine;
import static thump.game.Defines.GameMode.COMMERCIAL;
import static thump.game.Defines.GameMode.SHAREWARE;
import static thump.game.ThingStateLUT.states;
import static thump.game.play.Local.VIEWHEIGHT;
import static thump.wad.map.Degenmobj.MobileObjectFlag.MF_JUSTATTACKED;
import static thump.wad.map.Degenmobj.MobileObjectFlag.MF_NOCLIP;
import static thump.wad.map.Degenmobj.MobileObjectFlag.MF_SHADOW;

/**
 *
 * @author mark
 */
public class User {
    // Index of the special effects (INVUL inverse) map.
    public static final int INVERSECOLORMAP = 32;

    //
    // Movement.
    //
    public static final int MAXBOB = 0x100000; // 16 pixels of bob
	
    public boolean  onground = false;

    //
    // P_Thrust
    // Moves the given origin along a given angle.
    //
    void P_Thrust ( 
        Player	player,
        long	_angle,
        int	move 
    ) {
        long angle = _angle;
        angle >>= ANGLETOFINESHIFT;

        player.mo.momx += FixedPoint.mul(move,finecosine(angle)); 
        player.mo.momy += FixedPoint.mul(move,finesine(angle));
    }

    //
    // P_CalcHeight
    // Calculate the walking / running height adjustment
    //
    void P_CalcHeight (Player player) {
        int	angle;
        int	bob;

        // Regular movement bobbing
        // (needs to be calculated for gun swing
        // even if not on ground)
        // OPTIMIZE: tablify angle
        // Note: a LUT allows for effects
        //  like a ramp with low health.
        player.bob =
            FixedPoint.mul (player.mo.momx, player.mo.momx)
            + FixedPoint.mul (player.mo.momy,player.mo.momy);

        player.bob >>= 2;

        if (player.bob>MAXBOB) {
            player.bob = MAXBOB;
        }

        if ((player.cheats & CF_NOMOMENTUM.getValue())>0 || !onground) {
            player.viewz = player.mo.z + VIEWHEIGHT;

            if (player.viewz > player.mo.ceilingz-4*FRACUNIT) {
                player.viewz = player.mo.ceilingz-4*FRACUNIT;
            }

            player.viewz = player.mo.z + player.viewheight;
            return;
        }

        angle = (FINEANGLES/20*Game.getInstance().leveltime)&FINEMASK;
        bob = FixedPoint.mul ( player.bob/2, finesine(angle));


        // move viewheight
        if (player.playerstate == PST_LIVE) {
            player.viewheight += player.deltaviewheight;

            if (player.viewheight > VIEWHEIGHT)
            {
                player.viewheight = VIEWHEIGHT;
                player.deltaviewheight = 0;
            }

            if (player.viewheight < VIEWHEIGHT/2)
            {
                player.viewheight = VIEWHEIGHT/2;
                if (player.deltaviewheight <= 0) {
                    player.deltaviewheight = 1;
                }
            }

            if (player.deltaviewheight>0) {
                player.deltaviewheight += FRACUNIT/4;
                if (player.deltaviewheight==0) {
                    player.deltaviewheight = 1;
                }
            }
        }
        player.viewz = player.mo.z + player.viewheight + bob;

        if (player.viewz > player.mo.ceilingz-4*FRACUNIT) {
            player.viewz = player.mo.ceilingz-4*FRACUNIT;
        }
    }



    //
    // P_MovePlayer
    //
    void P_MovePlayer (Player player) {
        TickCommand		cmd;

        cmd = player.cmd;

        logger.log(Level.CONFIG, "    addjust player turn: {0}", cmd.angleturn);
        player.mo.angle += (cmd.angleturn<<16);
        //player.mo.angle &= 0xFFFFFFFFL;
        // Do not let the player control movement
        //  if not onground.
        onground = (player.mo.z <= player.mo.floorz);

        if (cmd.forwardmove!=0 && onground) {
            P_Thrust (player, player.mo.angle, cmd.forwardmove*2048);
            logger.log(Level.CONFIG, "    player move: {0}", cmd.forwardmove);
            
        }
        
        // DEBUG CODE   -- delete
        if (cmd.forwardmove!=0 && !onground) {
            P_Thrust (player, player.mo.angle, cmd.forwardmove*2048);
            logger.log(Level.CONFIG, "    player move: {0}", cmd.forwardmove);
            
        }
         

        if (cmd.sidemove!=0 && onground) {
            P_Thrust (player, player.mo.angle-ANG90, cmd.sidemove*2048);
        }

        if ( (cmd.forwardmove!=0 || cmd.sidemove!=0) 
             && player.mo.state == states[S_PLAY.ordinal()] )
        {
            MObject.P_SetMobjState (player.mo, S_PLAY_RUN1);
        }
    }	


    //
    // P_DeathThink
    // Fall on your face when dying.
    // Decrease POV height to floor height.
    //
    public static final int ANG5  = 	(ANG90/18);

    void P_DeathThink (Player player) {
        int		angle;
        int		delta;

        PSprite.P_MovePsprites (player);

        // fall to the ground
        if (player.viewheight > 6*FRACUNIT) {
            player.viewheight -= FRACUNIT;
        }

        if (player.viewheight < 6*FRACUNIT) {
            player.viewheight = 6*FRACUNIT;
        }

        player.deltaviewheight = 0;
        onground = (player.mo.z <= player.mo.floorz);
        P_CalcHeight (player);

        if (player.attacker != null && player.attacker != player.mo) {
            angle = Game.getInstance().renderer.R_PointToAngle2 (player.mo.x,
                                     player.mo.y,
                                     player.attacker.x,
                                     player.attacker.y);

            delta = angle - player.mo.angle;

            if (delta < ANG5 || delta > -ANG5) {
                // Looking at killer,
                //  so fade damage flash down.
                player.mo.angle = angle;

                if (player.damagecount>0) {
                    player.damagecount--;
                }
            }
            else if (delta < ANG180) {
                player.mo.angle += ANG5;
            } else {
                player.mo.angle -= ANG5;
            }
        } else if (player.damagecount>0) {
            player.damagecount--;
        }


        if ((player.cmd.buttons & BT_USE)>0) {
            player.playerstate = PST_REBORN;
        }
    }



    //
    // P_PlayerThink
    //
    void P_PlayerThink (Player player) {
        TickCommand		cmd;
        WeaponType	newweapon;

        // fixme: do this in the cheat code
        if ((player.cheats & CF_NOCLIP.getValue())>0) {
            player.mo.flags |= MF_NOCLIP.getValue();
        } else {
            player.mo.flags &= ~MF_NOCLIP.getValue();
        }

        // chain saw run forward
        cmd = player.cmd;
        if ((player.mo.flags & MF_JUSTATTACKED.getValue())>0) {
            cmd.angleturn = 0;
            cmd.forwardmove = 0xc800/512;
            cmd.sidemove = 0;
            player.mo.flags &= ~MF_JUSTATTACKED.getValue();
        }


        if (player.playerstate == PST_DEAD) {
            P_DeathThink (player);
            return;
        }

        // Move around.
        // Reactiontime is used to prevent movement
        //  for a bit after a teleport.
        if (player.mo.reactiontime>0) {
            player.mo.reactiontime--;
        } else {
            P_MovePlayer (player);
        }

        P_CalcHeight (player);

        if (player.mo.subsector.mapSector.sector.special>0) {
// p_spec            P_PlayerInSpecialSector (player);
        }

        // Check for weapon change.

        // A special event has no other buttons.
        if ((cmd.buttons & BT_SPECIAL)>0) {
            cmd.buttons = 0;
        }			

        if ((cmd.buttons & BT_CHANGE)>0) {
            // The actual changing of the weapon is done
            //  when the weapon psprite can do it
            //  (read: not in the middle of an attack).
            List<WeaponType> list = Arrays.asList(WeaponType.values());

            //newweapon = (cmd.buttons&BT_WEAPONMASK)>>BT_WEAPONSHIFT;
            newweapon = list.get((cmd.buttons&BT_WEAPONMASK)>>BT_WEAPONSHIFT);

            if (newweapon == wp_fist
                && player.weaponowned[wp_chainsaw.ordinal()]
                && !(player.readyweapon == wp_chainsaw
                     && player.powers[pw_strength.ordinal()]>0))
            {
                newweapon = wp_chainsaw;
            }

            if ( (Game.getInstance().gameMode == COMMERCIAL)
                && newweapon == wp_shotgun 
                && player.weaponowned[wp_supershotgun.ordinal()]
                && player.readyweapon != wp_supershotgun)
            {
                newweapon = wp_supershotgun;
            }


            if (player.weaponowned[newweapon.ordinal()]
                && newweapon != player.readyweapon) {
                // Do not go to plasma or BFG in shareware,
                //  even if cheated.
                if ((newweapon != wp_plasma
                     && newweapon != wp_bfg)
                    || (Game.getInstance().gameMode != SHAREWARE) )
                {
                    player.pendingweapon = newweapon;
                }
            }
        }

        // check for use
        if ((cmd.buttons & BT_USE)>0) {
            if (!player.usedown) {
                Game.getInstance().map.P_UseLines (player);
                player.usedown = true;
            }
        } else {
            player.usedown = false;
        }

        // cycle psprites
        PSprite.P_MovePsprites (player);

        // Counters, time dependend power ups.

        // Strength counts up to diminish fade.
        if (player.powers[pw_strength.ordinal()]>0) {
            player.powers[pw_strength.ordinal()]++;
        }	

        if (player.powers[pw_invulnerability.ordinal()]>0) {
            player.powers[pw_invulnerability.ordinal()]--;
        }

        if (player.powers[pw_invisibility.ordinal()]>0) {
            player.powers[pw_invisibility.ordinal()]--;
            if (player.powers[pw_invisibility.ordinal()]==0 ) {
                player.mo.flags &= ~MF_SHADOW.getValue();
            }
        }

        if (player.powers[pw_infrared.ordinal()]>0) {
            player.powers[pw_infrared.ordinal()]--;
        }

        if (player.powers[pw_ironfeet.ordinal()]>0) {
            player.powers[pw_ironfeet.ordinal()]--;
        }

        if (player.damagecount>0) {
            player.damagecount--;
        }

        if (player.bonuscount>0) {
            player.bonuscount--;
        }


        // Handling colormaps.
        if (player.powers[pw_invulnerability.ordinal()]>0) {
            if (player.powers[pw_invulnerability.ordinal()] > 4*32
                || (player.powers[pw_invulnerability.ordinal()]&8)>0 ) {
                player.fixedcolormap = INVERSECOLORMAP;
            } else {
                player.fixedcolormap = 0;
            }
        } else if (player.powers[pw_infrared.ordinal()]>0) {
            if (player.powers[pw_infrared.ordinal()] > 4*32
                || (player.powers[pw_infrared.ordinal()]&8)>0 ) {
                // almost full bright
                player.fixedcolormap = 1;
            }
            else {
                player.fixedcolormap = 0;
            }
        } else {
            player.fixedcolormap = 0;
        }
    }

}
