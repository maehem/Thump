
package thump.game.play;

import java.util.logging.Level;
import static thump.base.Defines.logger;
import thump.game.Game;
import thump.game.Player;
import static thump.game.Player.Cheat.CF_NOMOMENTUM;
import static thump.game.Player.PlayerState.PST_LIVE;
import static thump.game.Player.PlayerState.PST_REBORN;
import thump.game.PlayerSetup;
import static thump.game.State.StateNum.S_NULL;
import static thump.game.play.Local.MAXMOVE;
import thump.game.thinkeraction.T_MobjThinker;
import static thump.game.Defines.MAXPLAYERS;
import static thump.game.Defines.MTF_AMBUSH;
import thump.game.Defines.Skill;
import static thump.game.Defines.Skill.sk_nightmare;
import thump.base.FixedPoint;
import static thump.base.FixedPoint.FRACBITS;
import static thump.base.FixedPoint.FRACUNIT;
import thump.game.MobJInfo;
import static thump.game.MobJInfo.Type.*;
import thump.game.State;
import thump.game.State.StateNum;
import static thump.game.State.StateNum.*;
import static thump.base.Tables.ANG45;
import static thump.base.Tables.ANGLETOFINESHIFT;
import static thump.base.Tables.finecosine;
import static thump.base.Tables.finesine;
import thump.game.ThingStateLUT;
import static thump.game.ThingStateLUT.states;
import thump.game.maplevel.MapNode;
import thump.game.maplevel.MapObject;
import static thump.game.maplevel.MapObject.ONCEILINGZ;
import static thump.game.maplevel.MapObject.ONFLOORZ;
import static thump.game.play.Local.*;
import thump.game.sound.sfx.Sounds;
import thump.render.Renderer;
import static thump.game.sound.sfx.Sounds.SfxEnum.*;
import static thump.wad.map.Degenmobj.MobileObjectFlag.*;
import thump.wad.map.SubSector;
import thump.wad.mapraw.MapThing;

/**
 *
 * @author mark
 */
public class MObject {
    // Time interval for item respawning.
    public final static int ITEMQUESIZE = 128;
    
    public MapThing	itemrespawnque[] = new MapThing[ITEMQUESIZE];
    public int		itemrespawntime[] = new int[ITEMQUESIZE];
    public int		iquehead;
    public int		iquetail;


    public static boolean P_SetMobjState ( MapObject mobj, StateNum snum ) {
        State       state;
        StateNum    stateNum = snum;

        do {
            if (stateNum == S_NULL) {
                mobj.state = ThingStateLUT.states[S_NULL.ordinal()];
                Game.getInstance().movingObject.P_RemoveMobj (mobj);
                return false;
            }

            state = ThingStateLUT.states[stateNum.ordinal()];
            mobj.state = state;
            mobj.tics = state.tics;
            mobj.sprite = state.sprite;
            mobj.frame = state.frame;

            // Modified handling.
            // Call action functions when the stateNum is set
            if (state.action!=null) {
                state.action.doAction(mobj);
            }	

            stateNum = state.nextstate;
        } while (mobj.tics==0);

        return true;
    }


    //
    // P_ExplodeMissile  
    //
    public static void P_ExplodeMissile (MapObject mo) {
        mo.momx = 0;
        mo.momy = 0;
        mo.momz = 0;

        P_SetMobjState (mo, ThingStateLUT.mobjinfo[mo.type.ordinal()].deathstate);

        mo.tics -= Random.getInstance().P_Random()&3;

        if (mo.tics < 1) {
            mo.tics = 1;
        }

        mo.flags &= ~MF_MISSILE.getValue();

        if (mo.info.deathsound!=null) {
//            S_StartSound (mo, mo.info.deathsound);
        }
    }

    //
    // P_XYMovement  
    //
    private static final int STOPSPEED	= 0x1000;
    private static final int FRICTION   = 0xe800;

    public static void P_XYMovement (MapObject mo) { 	
        int 	ptryx;
        int	ptryy;
        Player	player;
        int	xmove;
        int	ymove;

        if (mo.momx==0 && mo.momy==0)
        {
            if ( (mo.flags & MF_SKULLFLY.getValue())>0 ) {
                // the skull slammed into something
                mo.flags &= ~MF_SKULLFLY.getValue();
                mo.momx = 0;
                mo.momy = 0;
                mo.momz = 0;

                P_SetMobjState (mo, mo.info.spawnstate);
            }
            return;
        }

        player = mo.player;

        if (mo.momx > MAXMOVE) {
            mo.momx = MAXMOVE;
        } else if (mo.momx < -MAXMOVE) {
            mo.momx = -MAXMOVE;
        }

        if (mo.momy > MAXMOVE) {
            mo.momy = MAXMOVE;
        } else if (mo.momy < -MAXMOVE) {
            mo.momy = -MAXMOVE;
        }

        xmove = mo.momx;
        ymove = mo.momy;

        do {
            if (xmove > MAXMOVE/2 || ymove > MAXMOVE/2) {
                ptryx = mo.x + xmove/2;
                ptryy = mo.y + ymove/2;
                xmove >>= 1;
                ymove >>= 1;
            } else {
                ptryx = mo.x + xmove;
                ptryy = mo.y + ymove;
                xmove = ymove = 0;
            }

            if (!Game.getInstance().map.P_TryMove (mo, ptryx, ptryy)) {
                // blocked move
                if (mo.player!=null) {	// try to slide along it
                    Game.getInstance().map.P_SlideMove (mo);
                } else if ((mo.flags & MF_MISSILE.getValue())>0) {
                    Map map = Game.getInstance().map;
                    // explode a missile
                    if (map.ceilingline != null &&   // p_map.c
                        map.ceilingline.backsector!= null &&
                            map.ceilingline.backsector.ceilingpic.equals(Renderer.SKYFLATNAME) )
                    {
                        // Hack to prevent missiles exploding
                        // against the sky.
                        // Does not handle sky floors.
                        Game.getInstance().movingObject.P_RemoveMobj (mo);
                        return;
                    }
                    P_ExplodeMissile (mo);
                } else {
                    mo.momx = 0;
                    mo.momy = 0;
                }
            }
        } while (xmove>0 || ymove>0);

        // slow down
        if (    player!=null 
                && (player.cheats & CF_NOMOMENTUM.getValue())>0 ) {
            // debug option for no sliding at all
            mo.momx = 0;
            mo.momy = 0;
            return;
        }

        if ((mo.flags & (MF_MISSILE.getValue() | MF_SKULLFLY.getValue()) )>0) {
            return; 	// no friction for missiles ever
        }

        if (mo.z > mo.floorz) {
            return;		// no friction when airborne
        }

        if ((mo.flags & MF_CORPSE.getValue())>0) {
            // do not stop sliding
            //  if halfway off a step with some momentum
            if (   mo.momx > FRACUNIT/4
                || mo.momx < -FRACUNIT/4
                || mo.momy > FRACUNIT/4
                || mo.momy < -FRACUNIT/4    )
            {
                if (mo.floorz != mo.subsector.mapSector.sector.floorheight) {
                    return;
                }
            }
        }

        if (mo.momx > -STOPSPEED
            && mo.momx < STOPSPEED
            && mo.momy > -STOPSPEED
            && mo.momy < STOPSPEED
            && ( player==null || 
                (player.cmd.forwardmove== 0 && player.cmd.sidemove == 0 ) 
               ) 
        ) {
            // if in a walking frame, stop moving
            if (    player!=null
                    && ( 
                    //    Arrays.binarySearch(states, player.mo.stateNum) - S_PLAY_RUN1.ordinal()
                    ThingStateLUT.indexOfState(player.mo.state) - S_PLAY_RUN1.ordinal()
                    //ThingStateLUT.indexOfState(player.mo.stateNum) - S_PLAY_RUN1.ordinal()
                    ) < 4  ) {  /// ????
                P_SetMobjState (player.mo, S_PLAY);
            }

            mo.momx = 0;
            mo.momy = 0;
        } else {
            mo.momx = FixedPoint.mul (mo.momx, FRICTION);
            mo.momy = FixedPoint.mul (mo.momy, FRICTION);
        }
    }

    //
    // P_ZMovement
    //
    static void P_ZMovement (MapObject mo) {
        int	dist;
        int	delta;

        // check for smooth step up
        if (mo.player!=null && mo.z < mo.floorz)
        {
            mo.player.viewheight -= mo.floorz-mo.z;

            mo.player.deltaviewheight
                = (VIEWHEIGHT - mo.player.viewheight)>>3;
        }

        // adjust height
        mo.z += mo.momz;

        if ( (mo.flags & MF_FLOAT.getValue()) > 0   &&   mo.target!=null ) {
            // float down towards target if too close
            if (    (mo.flags & MF_SKULLFLY.getValue()) == 0
                 && (mo.flags & MF_INFLOAT.getValue())  == 0 ) {
                dist = Game.getInstance().map.util.P_AproxDistance (mo.x - mo.target.x,
                                        mo.y - mo.target.y);

                delta =(mo.target.z + (mo.height>>1)) - mo.z;

                if (delta<0 && dist < -(delta*3) ) {
                    mo.z -= FLOATSPEED;
                } else if (delta>0 && dist < (delta*3) ) {
                    mo.z += FLOATSPEED;
                }			
            }

        }

        // clip movement
        if (mo.z <= mo.floorz) {
            // hit the floor

            // Note (id):
            //  somebody left this after the setting momz to 0,
            //  kinda useless there.
            if ((mo.flags & MF_SKULLFLY.getValue())>0) {
                // the skull slammed into something
                mo.momz = -mo.momz;
            }

            if (mo.momz < 0) {
                if (mo.player != null
                    && mo.momz < -GRAVITY*8) {
                    // Squat down.
                    // Decrease viewheight for a moment
                    // after hitting the ground (hard),
                    // and utter appropriate sound.
                    mo.player.deltaviewheight = mo.momz>>3;
                    Game.getInstance().sound.S_StartSound (mo, sfx_oof);
                }
                mo.momz = 0;
            }
            mo.z = mo.floorz;

            if ( ((mo.flags & MF_MISSILE.getValue())) > 0
                 && (mo.flags & MF_NOCLIP.getValue())==0 )
            {
                P_ExplodeMissile (mo);
                return;
            }
        } else if ((mo.flags & MF_NOGRAVITY.getValue())==0 ) {
            if (mo.momz == 0) {
                mo.momz = -GRAVITY*2;
            } else {
                mo.momz -= GRAVITY;
            }
        }

        if (mo.z + mo.height > mo.ceilingz) {
            // hit the ceiling
            if (mo.momz > 0) {
                mo.momz = 0;
            }
            
            {  // Awkward braces!!!!  WTF?
                mo.z = mo.ceilingz - mo.height;
            }

            if ((mo.flags & MF_SKULLFLY.getValue())>0) {   // the skull slammed into something
                mo.momz = -mo.momz;
            }

            if (    (mo.flags & MF_MISSILE.getValue())>0
                 && (mo.flags & MF_NOCLIP.getValue() )==0 )
            {
                P_ExplodeMissile (mo);
                /// return;  we are done anyway
            }
        }
    } 


    //
    // P_NightmareRespawn
    //
    void P_NightmareRespawn (MapObject mobj) {
        int		x;
        int		y;
        int		z; 
        SubSector	ss; 
        MapObject		mo;
        MapThing		mthing;

        x = mobj.spawnpoint.x << FRACBITS; 
        y = mobj.spawnpoint.y << FRACBITS; 

        // something is occupying it's position?
        if (!Game.getInstance().map.P_CheckPosition (mobj, x, y) ) { 
            return;	// no respwan
        }

        // spawn a teleport fog at old spot
        // because of removal of the body?
        mo = P_SpawnMobj (mobj.x,
                          mobj.y,
                          mobj.subsector.mapSector.sector.floorheight , MT_TFOG); 
        // initiate teleport sound
        Game.getInstance().sound.S_StartSound (mo, sfx_telept);

        // spawn a teleport fog at the new spot
        //ss = Game.getInstance().renderer.R_PointInSubsector (x,y); 
        ss = MapNode.R_PointInSubsector (x,y).subsector; 

        mo = P_SpawnMobj (x, y, ss.sector.floorheight , MT_TFOG); 

        Game.getInstance().sound.S_StartSound (mo, sfx_telept);

        // spawn the new monster
        mthing = mobj.spawnpoint;

        // spawn it
        if ((mobj.info.flags & MF_SPAWNCEILING.getValue())>0) {
            z = ONCEILINGZ;
        } else {
            z = ONFLOORZ;
        }

        // inherit attributes from deceased one
        mo = P_SpawnMobj (x,y,z, mobj.type);
        mo.spawnpoint = mobj.spawnpoint;	
        mo.angle = ANG45 * (mthing.angle/45);

        if ((mthing.options & MTF_AMBUSH)>0) {
            mo.flags |= MF_AMBUSH.getValue();
        }

        mo.reactiontime = 18;

        // remove the old monster,
        P_RemoveMobj (mobj);
    }


    //
    // P_MobjThinker
    //
    public static void P_MobjThinker (MapObject mobj) {
        // momentum movement
        if (   mobj.momx>0 
            || mobj.momy>0
            || (mobj.flags&MF_SKULLFLY.getValue()) > 0    )
        {
            P_XYMovement (mobj);

            // FIXME: decent NOP/NULL/Nil function pointer please.
            //if (mobj.thinker.function.acv == (actionf_v) (-1))
            if (mobj.getFunction() == null) {
                return;		// mobj was removed
            }
        }
        if (    (mobj.z != mobj.floorz)
             ||  mobj.momz > 0              ) {
            P_ZMovement (mobj);

            // FIXME: decent NOP/NULL/Nil function pointer please.
            //if (mobj.thinker.function.acv == (actionf_v) (-1))
            if ( mobj.getFunction() == null ) {
                return;		// mobj was removed
            }
        }


        // cycle through states,
        // calling action functions at transitions
        if (mobj.tics != -1) {
            mobj.tics--;

            // you can cycle through multiple states in a tic
            if ( mobj.tics==0 ) {
                if (!MObject.P_SetMobjState (mobj, mobj.state.nextstate) ) {
                    return;		// freed itself
                }
            }
        } else {
            // check for nightmare respawn
            if ((mobj.flags & MF_COUNTKILL.getValue()) == 0 ) {
                return;
            }

            if (!Game.getInstance().respawnmonsters) {
                return;
            }

            mobj.movecount++;

            if (mobj.movecount < 12*35) {
                return;
            }

            if ( (Game.getInstance().leveltime&0x1F) > 0 ) {
                return;
            }

            if (Random.getInstance().P_Random () > 4) {
                return;
            }

            Game.getInstance().movingObject.P_NightmareRespawn (mobj);
        }

    }


    //
    // P_SpawnMobj
    //
    public static MapObject P_SpawnMobj(
            int x,
            int y,
            int z,
            MobJInfo.Type type) {
        MapObject mobj = new MapObject();
        State state;
        MobJInfo info = ThingStateLUT.mobjinfo[type.ordinal()];

        //mobj = Z_Malloc (sizeof(*mobj), PU_LEVEL, NULL);
        //memset (mobj, 0, sizeof (*mobj));
        //info = type;
        mobj.type = type;
        mobj.info = info;
        mobj.x = x;
        mobj.y = y;
        mobj.radius = info.radius;
        mobj.height = info.height;
        mobj.flags = info.flags;
        mobj.health = info.spawnhealth;

        if (Game.getInstance().gameskill != sk_nightmare) {
            mobj.reactiontime = info.reactiontime;
        }

        mobj.lastlook = Random.getInstance().P_Random() % MAXPLAYERS;
        // do not set the stateNum with P_SetMobjState,
        // because action routines can not be called yet
        state = states[info.spawnstate.ordinal()];

        //mobj.stateNum = state;
        mobj.state = state;
        mobj.tics = state.tics;
        mobj.sprite = state.sprite;
        mobj.frame = state.frame;

        // set subsector and/or block links
        Game.getInstance().map.util.P_SetThingPosition(mobj);

        mobj.floorz = mobj.subsector.mapSector.sector.floorheight;
        mobj.ceilingz = mobj.subsector.mapSector.sector.ceilingheight;

        switch (z) {
            case ONFLOORZ:
                mobj.z = mobj.floorz;
                break;
            case ONCEILINGZ:
                mobj.z = mobj.ceilingz - mobj.info.height;
                break;
            default:
                mobj.z = z;
                break;
        }

        //mobj.thinker.function.acp1 = (actionf_p1)P_MobjThinker;
        mobj.setFunction( new T_MobjThinker());

        Tick.P_AddThinker(mobj);

        return mobj;
    }


    public void P_RemoveMobj (MapObject mobj) {
        if (   (mobj.flags & MF_SPECIAL.getValue())>0
            && (mobj.flags & MF_DROPPED.getValue())==0
            && (mobj.type != MT_INV)
            && (mobj.type != MT_INS))
        {
            itemrespawnque[iquehead] = mobj.spawnpoint;
            itemrespawntime[iquehead] = Game.getInstance().leveltime;
            iquehead = (iquehead+1)&(ITEMQUESIZE-1);

            // lose one off the end?
            if (iquehead == iquetail) {
                iquetail = (iquetail+1)&(ITEMQUESIZE-1);
            }
        }

        // unlink from sector and block lists
        Game.getInstance().map.util.P_UnsetThingPosition (mobj);

        // stop any playing sound
        Game.getInstance().sound.S_StopSound (mobj);

        // free block
        Tick.P_RemoveThinker (mobj);
    }




    //
    // P_RespawnSpecials
    //
    void P_RespawnSpecials () {
        int		x;
        int		y;
        int		z;

        SubSector	ss; 
        MapObject		mo;
        MapThing	mthing;

        int		i;

        // only respawn items in deathmatch
        if (Game.getInstance().deathmatch != 2) {
            return;
        }

        // nothing left to respawn?
        if (iquehead == iquetail) {
            return;
        }		

        // wait at least 30 seconds
        if (Game.getInstance().leveltime - itemrespawntime[iquetail] < 30*35) {
            return;
        }			

        mthing = itemrespawnque[iquetail];

        x = mthing.x << FRACBITS; 
        y = mthing.y << FRACBITS; 

        // spawn a teleport fog at the new spot
        ss = MapNode.R_PointInSubsector (x,y).subsector; 
        mo = P_SpawnMobj (x, y, ss.sector.floorheight , MT_IFOG); 
        Game.getInstance().sound.S_StartSound (mo, sfx_itmbk);

        // find which type to spawn
        for (i=0 ; i< ThingStateLUT.mobjinfo.length ; i++) {
            if (mthing.type == ThingStateLUT.mobjinfo[i].doomednum) {
                break;
            }
        }

        // spawn it
        if ((ThingStateLUT.mobjinfo[i].flags & MF_SPAWNCEILING.getValue())>0) {
            z = ONCEILINGZ;
        } else {
            z = ONFLOORZ;
        }

        mo = P_SpawnMobj (x,y,z, MobJInfo.Type.values()[i]);
        mo.spawnpoint = mthing;	
        mo.angle = ANG45 * (mthing.angle/45);

        // pull it from the que
        iquetail = (iquetail+1)&(ITEMQUESIZE-1);
    }




    //
    // P_SpawnPlayer
    // Called when a player is spawned on the level.
    // Most of the player structure stays unchanged
    //  between levels.
    //
    public static void P_SpawnPlayer (MapThing mthing) {
        Player		p;
        int		x;
        int		y;
        int		z;

        MapObject		mobj;

        int			i;

        // not playing?
        if (!Game.getInstance().playeringame[mthing.type-1]) {
            return;
        }					

        p = Game.getInstance().players[mthing.type-1];

        if (p.playerstate == PST_REBORN) {
            Game.getInstance().G_PlayerReborn (mthing.type-1);
        }

        x 		= mthing.x << FRACBITS;
        y 		= mthing.y << FRACBITS;
        z		= ONFLOORZ;
        mobj	= P_SpawnMobj (x,y,z, MT_PLAYER);

        // set color translations for player sprites
        if (mthing.type > 1) {
            mobj.flags |= (mthing.type-1)<<MF_TRANSSHIFT.getValue();
        }

        mobj.angle	= ANG45 * (mthing.angle/45);
        mobj.player = p;
        mobj.health = p.health;

        p.mo = mobj;
        p.playerstate = PST_LIVE;	
        p.refire = 0;
        p.message = null;
        p.damagecount = 0;
        p.bonuscount = 0;
        p.extralight = 0;
        p.fixedcolormap = 0;
        p.viewheight = VIEWHEIGHT;

        // setup gun psprite
        PSprite.P_SetupPsprites (p);

        // give all cards in death match mode
        if (Game.getInstance().deathmatch>0) {
            for (i=0 ; i<p.cards.length ; i++) {
                p.cards[i] = true;
            }
        }

        if (mthing.type-1 == Game.getInstance().consoleplayer) {
            // wake up the status bar
            Game.getInstance().statusBar.ST_Start ();
            // wake up the heads up text
            Game.getInstance().headUp.HU_Start ();		
        }
    }



    //
    // P_SpawnMapThing
    // The fields of the mapthing should
    // already be in host byte order.
    //
    public static void P_SpawnMapThing (MapThing mthing) {
        int			i;
        int			bit = 0;
        MapObject		mobj;
        int		x;
        int		y;
        int		z;

        MobJInfo[] mobjinfo = ThingStateLUT.mobjinfo;
        PlayerSetup ps = Game.getInstance().playerSetup;
        
        // count deathmatch start positions
        if (mthing.type == 11) {
            // TODO  Make DeathMatchStarts an ArrayList object.
            if (ps.deathmatch_p < ps.deathmatchstarts.length-1) {
                //memcpy (deathmatch_p, mthing, sizeof(*mthing));  LOLZ... good times
                ps.deathmatchstarts[ps.deathmatch_p] = mthing;
                ps.deathmatch_p++;
            }
            return;
        }

        // check for players specially
        if (mthing.type <= 4)
        {
            // save spots for respawning in network games
            ps.playerstarts[mthing.type-1] = mthing;
            if (Game.getInstance().deathmatch==0) {
                P_SpawnPlayer (mthing);
            }

            return;
        }

        // check for apropriate skill level
        if ( !Game.getInstance().netgame 
                && (mthing.options & 16)>0 ) {
            return;
        }

        Skill gameskill = Game.getInstance().gameskill;
        if (null != gameskill) {
            switch (gameskill) {
                case sk_baby:
                    bit = 1;
                    break;
                case sk_nightmare:
                    bit = 4;
                    break;
                default:
                    bit = 1<<(gameskill.getValue()-1);
                    break;
            }
        }

        if ((mthing.options & bit)==0 ) {
            return;
        }

        // find which type to spawn
        for (i=0 ; i< mobjinfo.length ; i++) {
            if (mthing.type == mobjinfo[i].doomednum) {
                break;
            }
        }

        if (i==mobjinfo.length) {
            logger.log(Level.SEVERE,
                    "P_SpawnMapThing: Unknown type {0} at ({1}, {2})",
                    new Object[]{ mthing.type, mthing.x, mthing.y}
            );
        }

        // don't spawn keycards and players in deathmatch
        if (Game.getInstance().deathmatch>0 
                && (mobjinfo[i].flags & MF_NOTDMATCH.getValue())>0
        ) {
            return;
        }

        // don't spawn any monsters if -nomonsters
        if (Game.getInstance().nomonsters
            && ( i == MT_SKULL.ordinal()
                 || (mobjinfo[i].flags & MF_COUNTKILL.getValue())>0) )
        {
            return;
        }

        // spawn it
        x = mthing.x << FRACBITS;
        y = mthing.y << FRACBITS;

        if ((mobjinfo[i].flags & MF_SPAWNCEILING.getValue())>0) {
            z = ONCEILINGZ;
        } else {
            z = ONFLOORZ;
        }

        mobj = P_SpawnMobj (x,y,z, MobJInfo.Type.values()[i]);  // Add type field into mobjinfo?
        mobj.spawnpoint = mthing;

        if (mobj.tics > 0) {
            mobj.tics = 1 + (Random.getInstance().P_Random () % mobj.tics);
        }
        if ((mobj.flags & MF_COUNTKILL.getValue())>0) {
            Game.getInstance().totalkills++;
        }
        if ((mobj.flags & MF_COUNTITEM.getValue())>0) {
            Game.getInstance().totalitems++;
        }

        mobj.angle = ANG45 * (mthing.angle/45);
        if ((mthing.options & MTF_AMBUSH)>0) {
            mobj.flags |= MF_AMBUSH.getValue();
        }
    }


    //
    // GAME SPAWN FUNCTIONS
    //


    //
    // P_SpawnPuff
    //
    //extern int attackrange;

    public static void P_SpawnPuff(
            int	x,
            int	y,
            int	z ) {
        MapObject	th;

        int zz = z;
        zz += ((Random.getInstance().P_Random()-Random.getInstance().P_Random())<<10);

        th = P_SpawnMobj (x,y,zz, MT_PUFF);
        th.momz = FRACUNIT;
        th.tics -= Random.getInstance().P_Random()&3;

        if (th.tics < 1) {
            th.tics = 1;
        }

        // don't make punches spark on the wall
        if (Game.getInstance().map.attackrange == MELEERANGE) {   //p_map.c
            P_SetMobjState (th, S_PUFF3);
        }
    }




    //
    // P_SpawnBlood
    // 
    public static void P_SpawnBlood(
            int x,
            int y,
            int z,
            int damage  ) {
        MapObject	th;

        int zz = z;
        zz += ((Random.getInstance().P_Random()-Random.getInstance().P_Random())<<10);
        th = P_SpawnMobj (x,y,zz, MT_BLOOD);
        th.momz = FRACUNIT*2;
        th.tics -= Random.getInstance().P_Random()&3;

        if (th.tics < 1) {
            th.tics = 1;
        }

        if (damage <= 12 && damage >= 9) {
            P_SetMobjState (th,S_BLOOD2);
        } else if (damage < 9) {
            P_SetMobjState (th,S_BLOOD3);
        }
    }



    //
    // P_CheckMissileSpawn
    // Moves the missile forward a bit
    //  and possibly explodes it right there.
    //
    static void P_CheckMissileSpawn (MapObject th) {
        th.tics -= Random.getInstance().P_Random()&3;
        if (th.tics < 1) {
            th.tics = 1;
        }

        // move a little forward so an angle can
        // be computed if it immediately explodes
        th.x += (th.momx>>1);
        th.y += (th.momy>>1);
        th.z += (th.momz>>1);

        if (!Game.getInstance().map.P_TryMove (th, th.x, th.y)) {
            P_ExplodeMissile (th);
        }
    }


    //
    // P_SpawnMissile
    //
    public static MapObject P_SpawnMissile(
            MapObject source,
            MapObject dest,
            MobJInfo.Type type) {
        MapObject	th;
        int	an;
        int		dist;

        th = P_SpawnMobj (source.x,
                          source.y,
                          source.z + 4*8*FRACUNIT, type);

        if (th.info.seesound!=Sounds.SfxEnum.sfx_None) {
//            S_StartSound (th, th.info.seesound);
        }

        th.target = source;	// where it came from
        an = Game.getInstance().renderer.R_PointToAngle2 (source.x, source.y, dest.x, dest.y);	

        // fuzzy player
        if ((dest.flags & MF_SHADOW.getValue())>0) {
            an += (Random.getInstance().P_Random()-Random.getInstance().P_Random())<<20;
        }	

        th.angle = an;
        an >>= ANGLETOFINESHIFT;
        th.momx = FixedPoint.mul (th.info.speed, finecosine(an));
        th.momy = FixedPoint.mul (th.info.speed, finesine(an));

        dist = Game.getInstance().map.util.P_AproxDistance (dest.x - source.x, dest.y - source.y);
        dist /= th.info.speed;

        if (dist < 1) {
            dist = 1;
        }

        th.momz = (dest.z - source.z) / dist;
        P_CheckMissileSpawn (th);

        return th;
    }


    //
    // P_SpawnPlayerMissile
    // Tries to aim at a nearby monster
    //
    public static void P_SpawnPlayerMissile(
            MapObject source,
            MobJInfo.Type type) {
        MapObject th;
        int an;

        int x;
        int y;
        int z;
        int slope;

        // see which target is to be aimed at
        Map map = Game.getInstance().map;

        an = source.angle;
        slope = map.P_AimLineAttack(source, an, 16 * 64 * FRACUNIT);

        if (null == map.linetarget) {
            an += 1 << 26;
            slope = map.P_AimLineAttack(source, an, 16 * 64 * FRACUNIT);

            if (null == map.linetarget) {
                an -= 2 << 26;
                slope = map.P_AimLineAttack(source, an, 16 * 64 * FRACUNIT);
            }

            if (null == map.linetarget) {
                an = source.angle;
                slope = 0;
            }
        }

        x = source.x;
        y = source.y;
        z = source.z + 4 * 8 * FRACUNIT;

        th = P_SpawnMobj(x, y, z, type);

        if (th.info.seesound != Sounds.SfxEnum.sfx_None) //            S_StartSound (th, th.info.seesound);
        {
            th.target = source;
        }
        th.angle = an;
        th.momx = FixedPoint.mul(th.info.speed,
                finecosine(an >> ANGLETOFINESHIFT));
        th.momy = FixedPoint.mul(th.info.speed,
                finesine(an >> ANGLETOFINESHIFT));
        th.momz = FixedPoint.mul(th.info.speed, slope);

        P_CheckMissileSpawn(th);
    }
 }
