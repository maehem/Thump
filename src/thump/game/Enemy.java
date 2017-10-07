/*
 *   Enemy thinking, AI.
 *   Action Pointer Functions that are associated with states/frames.
 *
 */
package thump.game;

import static thump.game.Enemy.DirType.*;
import thump.game.thinkeraction.T_MobjThinker;
import thump.global.Defines.GameMode;
import static thump.global.Defines.Skill.sk_easy;
import static thump.global.FixedPoint.FRACUNIT;
import thump.global.MobJInfo;
import static thump.global.MobJInfo.Type.*;
import thump.global.Random;
import static thump.global.State.StateNum.S_BRAINEXPLODE1;
import static thump.global.State.StateNum.S_NULL;
import thump.global.SystemInterface;
import static thump.global.Tables.ANG270;
import static thump.global.Tables.ANG90;
import static thump.global.ThingStateLUT.mobjinfo;
import thump.maplevel.MapObject;
import static thump.maplevel.MapObject.MobileObjectFlag.*;
import static thump.play.Local.FLOATSPEED;
import static thump.play.Local.MELEERANGE;
import thump.play.MObject;
import thump.render.Line;
import static thump.render.Line.ML_SOUNDBLOCK;
import static thump.render.Line.ML_TWOSIDED;
import thump.render.Sector;
import thump.render.Side;
import thump.sound.sfx.Sounds.SfxEnum;
import static thump.sound.sfx.Sounds.SfxEnum.*;

/**
 *
 * @author mark
 */
public class Enemy {

    private final Game game;

    public Enemy( Game game ) {
        this.game = game;
    }

    

    public enum DirType
    {
        DI_EAST,
        DI_NORTHEAST,
        DI_NORTH,
        DI_NORTHWEST,
        DI_WEST,
        DI_SOUTHWEST,
        DI_SOUTH,
        DI_SOUTHEAST,
        DI_NODIR,

    };


    //
    // P_NewChaseDir related LUT.
    //
    private final DirType opposite[] =
    {
      DI_WEST, DI_SOUTHWEST, DI_SOUTH, DI_SOUTHEAST,
      DI_EAST, DI_NORTHEAST, DI_NORTH, DI_NORTHWEST, DI_NODIR
    };

    private final DirType diags[] =
    {
        DI_NORTHWEST, DI_NORTHEAST, DI_SOUTHWEST, DI_SOUTHEAST
    };


    //void A_Fall (MapObject actor);


    //
    // ENEMY THINKING
    // Enemies are allways spawned
    // with targetplayer = -1, threshold = 0
    // Most monsters are spawned unaware of all players,
    // but some can be made preaware
    //


    //
    // Called by P_NoiseAlert.
    // Recursively traverse adjacent sectors,
    // sound blocking lines cut off traversal.
    //

    private MapObject		soundtarget;

    private void P_RecursiveSound( Sector sec, int soundblocks ) {
        int     i;
        Line	check;
        Sector	other;

        // wake up all monsters in this sector
        if (sec.validcount == game.renderer.validcount
            && sec.soundtraversed <= soundblocks+1) {
            return;		// already flooded
        }

        sec.validcount = game.renderer.validcount;
        sec.soundtraversed = soundblocks+1;
        sec.soundtarget = soundtarget;

        for (i=0 ;i<sec.linecount ; i++) {
            check = sec.lines[i];
            if (0==(check.flags & ML_TWOSIDED) ) {
                continue;
            }

            Game.getInstance().map.util.P_LineOpening (check);

            if (game.map.util.openrange <= 0) {
                continue;	// closed door
            }

            Side sides[] = Game.getInstance().playerSetup.sides;
            
            if ( sides[ check.sidenum[0] ].sector == sec) {
                other = sides[ check.sidenum[1] ] .sector;
            } else {
                other = sides[ check.sidenum[0] ].sector;
            }

            if ((check.flags & ML_SOUNDBLOCK)>0) {
                if (0==soundblocks) {
                    P_RecursiveSound (other, 1);
                }
            } else {
                P_RecursiveSound (other, soundblocks);
            }
        }
    }



    //
    // P_NoiseAlert
    // If a monster yells at a player,
    // it will alert other monsters to the player.
    //
    public void P_NoiseAlert(MapObject target, MapObject emmiter) {
        soundtarget = target;
        Game.getInstance().renderer.validcount++;
        P_RecursiveSound(emmiter.subsector.sector, 0);
    }



    //
    // P_CheckMeleeRange
    //
    boolean P_CheckMeleeRange(MapObject actor) {
        MapObject pl;
        int dist;

        if (null == actor.target) {
            return false;
        }

        pl = actor.target;
        dist = game.map.util.P_AproxDistance(pl.x - actor.x, pl.y - actor.y);

        if (dist >= MELEERANGE - 20 * FRACUNIT + pl.info.radius) {
            return false;
        }

        return game.sight.P_CheckSight(actor, actor.target);
    }

    //
    // P_CheckMissileRange
    //
    boolean P_CheckMissileRange(MapObject actor) {
        int dist;

        if (!game.sight.P_CheckSight(actor, actor.target)) {
            return false;
        }

        if ((actor.flags & MF_JUSTHIT.getValue())>0) {
            // the target just hit the enemy,
            // so fight back!
            actor.flags &= ~MF_JUSTHIT.getValue();
            return true;
        }

        if (actor.reactiontime>0) {
            return false;	// do not attack yet
        }

        // OPTIMIZE: get this from a global checksight
        dist = game.map.util.P_AproxDistance(actor.x - actor.target.x,
                actor.y - actor.target.y) - 64 * FRACUNIT;

        if (actor.info.meleestate == S_NULL) {
            dist -= 128 * FRACUNIT;	// no melee attack, so fire more
        }

        dist >>= 16;

        if (actor.type == MT_VILE) {
            if (dist > 14 * 64) {
                return false;	// too far away
            }
        }

        if (actor.type == MT_UNDEAD) {
            if (dist < 196) {
                return false;	// close for fist attack
            }
            dist >>= 1;
        }

        if (actor.type == MT_CYBORG
                || actor.type == MT_SPIDER
                || actor.type == MT_SKULL) {
            dist >>= 1;
        }

        if (dist > 200) {
            dist = 200;
        }

        if (actor.type == MT_CYBORG && dist > 160) {
            dist = 160;
        }

        return Random.getInstance().P_Random() >= dist;
    }


    //
    // P_Move
    // Move in the current direction,
    // returns false if the move is blocked.
    //
    private int	xspeed[] = {FRACUNIT,47000,0,-47000,-FRACUNIT,-47000,0,47000};
    private int yspeed[] = {0,47000,FRACUNIT,47000,0,-47000,-FRACUNIT,-47000};

    public static final int MAXSPECIALCROSS	=8;

    boolean P_Move (MapObject	actor) {
        int	tryx;
        int	tryy;

        Line	ld;

        // warning: 'catch', 'throw', and 'try'
        // are all C++ reserved words
        boolean	try_ok;
        boolean	good;

        if (actor.movedir == DI_NODIR) {
            return false;
        }

        if (actor.movedir.ordinal() >= 8) {
            SystemInterface.I_Error ("Weird actor.movedir!");
        }

        tryx = actor.x + actor.info.speed*xspeed[actor.movedir.ordinal()];
        tryy = actor.y + actor.info.speed*yspeed[actor.movedir.ordinal()];

        try_ok = game.map.P_TryMove (actor, tryx, tryy);

        if (!try_ok)
        {
            // open any specials
            if ((actor.flags & MF_FLOAT.getValue())>0 && game.map.floatok) {
                // must adjust height
                if (actor.z < game.map.tmfloorz) {
                    actor.z += FLOATSPEED;
                } else {
                    actor.z -= FLOATSPEED;
                }

                actor.flags |= MF_INFLOAT.getValue();
                return true;
            }

            if (0==game.map.numspechit) {
                return false;
            }

            actor.movedir = DI_NODIR;
            good = false;
            while (game.map.numspechit>0) {
                game.map.numspechit--;
                ld = game.map.spechit[game.map.numspechit];
                // if the special is not a door
                // that can be opened,
                // return false
                if (game.playerSetup.svitch.P_UseSpecialLine (actor, ld,0)) {
                    good = true;
                }
            }
            return good;
        } else {
            actor.flags &= ~MF_INFLOAT.getValue();
        }


        if (0==(actor.flags & MF_FLOAT.getValue()) ) {
            actor.z = actor.floorz;
        }
        return true; 
    }


    //
    // TryWalk
    // Attempts to move actor on
    // in its current (ob.moveangle) direction.
    // If blocked by either a wall or an actor
    // returns FALSE
    // If move is either clear or blocked only by a door,
    // returns TRUE and sets...
    // If a door is in the way,
    // an OpenDoor call is made to start it opening.
    //
    boolean P_TryWalk(MapObject actor) {
        if (!P_Move(actor)) {
            return false;
        }

        actor.movecount = Random.getInstance().P_Random() & 15;
        return true;
    }




    void P_NewChaseDir(MapObject actor) {
        int deltax;
        int deltay;

        DirType d[] = new DirType[3];

        DirType tdir;
        DirType olddir;

        DirType turnaround;

        if (null == actor.target) {
            SystemInterface.I_Error("P_NewChaseDir: called with no target");
        }

        olddir = actor.movedir;
        turnaround = opposite[olddir.ordinal()];

        deltax = actor.target.x - actor.x;
        deltay = actor.target.y - actor.y;

        if (deltax > 10 * FRACUNIT) {
            d[1] = DI_EAST;
        } else if (deltax < -10 * FRACUNIT) {
            d[1] = DI_WEST;
        } else {
            d[1] = DI_NODIR;
        }

        if (deltay < -10 * FRACUNIT) {
            d[2] = DI_SOUTH;
        } else if (deltay > 10 * FRACUNIT) {
            d[2] = DI_NORTH;
        } else {
            d[2] = DI_NODIR;
        }

        // try direct route
        if (d[1] != DI_NODIR && d[2] != DI_NODIR) {
            actor.movedir = diags[((deltay < 0?1:0) << 1) + (deltax > 0?1:0)];
            if ((actor.movedir != turnaround ) && P_TryWalk(actor)) {
                return;
            }
        }

        // try other directions
        if (Random.getInstance().P_Random() > 200
                || Math.abs(deltay) > Math.abs(deltax)) {
            tdir = d[1];
            d[1] = d[2];
            d[2] = tdir;
        }

        if (d[1] == turnaround) {
            d[1] = DI_NODIR;
        }
        if (d[2] == turnaround) {
            d[2] = DI_NODIR;
        }

        if (d[1] != DI_NODIR) {
            actor.movedir = d[1];
            if (P_TryWalk(actor)) {
                // either moved forward or attacked
                return;
            }
        }

        if (d[2] != DI_NODIR) {
            actor.movedir = d[2];

            if (P_TryWalk(actor)) {
                return;
            }
        }

        // there is no direct path to the player,
        // so pick another direction.
        if (olddir != DI_NODIR) {
            actor.movedir = olddir;

            if (P_TryWalk(actor)) {
                return;
            }
        }

        // randomly determine direction of search
        if ((Random.getInstance().P_Random() & 1)>0) {
            for (   tdir = DI_EAST;
                    tdir.ordinal() <= DI_SOUTHEAST.ordinal();
                    tdir=DirType.values()[tdir.ordinal()+1]) {
                
                if (tdir != turnaround) {
                    actor.movedir = tdir;

                    if (P_TryWalk(actor)) {
                        return;
                    }
                }
            }
        } else {
            for (   tdir = DI_SOUTHEAST;
                    tdir.ordinal() != (DI_EAST.ordinal() - 1);
                    tdir=DirType.values()[tdir.ordinal()-1]) {
                
                if (tdir != turnaround) {
                    actor.movedir = tdir;

                    if (P_TryWalk(actor)) {
                        return;
                    }
                }
            }
        }

        if (turnaround != DI_NODIR) {
            actor.movedir = turnaround;
            if (P_TryWalk(actor)) {
                return;
            }
        }

        actor.movedir = DI_NODIR;	// can not move
    }


    //
    // P_LookForPlayers
    // If allaround is false, only look 180 degrees in front.
    // Returns true if a player is targeted.
    //
    boolean P_LookForPlayers(MapObject actor, boolean allaround) {
        int c;
        int stop;
        Player player;
        //Sector sector;
        int an;
        int dist;

        //sector = actor.subsector.sector;

        c = 0;
        stop = (actor.lastlook - 1) & 3;

        for (;; actor.lastlook = (actor.lastlook + 1) & 3) {
            if (!game.playeringame[actor.lastlook]) {
                continue;
            }

            if (c == 2 || actor.lastlook == stop) {
                // done looking
                return false;
            }
            c++;

            player = game.players[actor.lastlook];

            if (player.health <= 0) {
                continue;		// dead
            }
            if (!game.sight.P_CheckSight(actor, player.mo)) {
                continue;		// out of sight
            }
            if (!allaround) {
                an = game.renderer.R_PointToAngle2(actor.x,
                        actor.y,
                        player.mo.x,
                        player.mo.y)
                        - actor.angle;

                if (an > ANG90 && an < ANG270) {
                    dist = game.map.util.P_AproxDistance(player.mo.x - actor.x,
                            player.mo.y - actor.y);
                    // if real close, react anyway
                    if (dist > MELEERANGE) {
                        continue;	// behind back
                    }
                }
            }

            actor.target = player.mo;
            return true;
        }

        //return false;
    }

/*  Moved to player.action as Objects.
    //
    // A_KeenDie
    // DOOM II special, map 32.
    // Uses special tag 666.
    //
    void A_KeenDie (MapObject mo) {
        Thinker	th;
        MapObject	mo2;
        Line	junk = new Line();

        A_Fall (mo);

        // scan the remaining thinkers
        // to see if all Keens are dead
        for (th = game.thinkercap.getNextThinker() ; th != game.thinkercap ; th=th.getNextThinker()) {
            if (!(th.getFunction() instanceof T_MobjThinker)) {
                continue;
            }

            mo2 = (MapObject)th;
            if (mo2 != mo
                && mo2.type == mo.type
                && mo2.health > 0)
            {
                // other Keen not dead
                return;		
            }
        }

        junk.tag = 666;
        VDoor.EV_DoDoor(junk,open);
    }

    //
    // ACTION ROUTINES
    //
    //
    // A_Look
    // Stay in state until a player is sighted.
    //
    void A_Look(MapObject actor) {
        MapObject targ;

        actor.threshold = 0;	// any shot will wake up
        targ = actor.subsector.sector.soundtarget;

        boolean seeyou = false;

        if (targ != null && (targ.flags & MF_SHOOTABLE.getValue()) > 0) {
            actor.target = targ;

            if ((actor.flags & MF_AMBUSH.getValue()) > 0) {
                if (game.sight.P_CheckSight(actor, actor.target)) {
                    //goto seeyou;
                    seeyou = true;
                }
            } else {
                //goto seeyou;
                seeyou = true;
            }
        }

        if (!seeyou && !P_LookForPlayers(actor, false)) {
            return;
        }

        // go into chase state
        //seeyou:
        if (actor.info.seesound != Sounds.SfxEnum.sfx_None) {
            SfxEnum sound;

            switch (actor.info.seesound) {
                case sfx_posit1:
                case sfx_posit2:
                case sfx_posit3:
                    sound = SfxEnum.values()[sfx_posit1.ordinal() + Random.getInstance().P_Random() % 3];
                    break;

                case sfx_bgsit1:
                case sfx_bgsit2:
                    sound = SfxEnum.values()[sfx_bgsit1.ordinal() + Random.getInstance().P_Random() % 2];
                    break;

                default:
                    sound = actor.info.seesound;
                    break;
            }

            if (actor.type == MT_SPIDER || actor.type == MT_CYBORG) {
                // full volume
                game.sound.S_StartSound(null, sound);
            } else {
                game.sound.S_StartSound(actor, sound);
            }
        }

        MObject.P_SetMobjState(actor, actor.info.seestate);
    }


    //
    // A_Chase
    // Actor has a melee attack,
    // so it tries to close as fast as possible
    //
    void A_Chase(MapObject actor) {
        int delta;

        if (actor.reactiontime > 0) {
            actor.reactiontime--;
        }

        // modify target threshold
        if (actor.threshold > 0) {
            if (null == actor.target
                    || actor.target.health <= 0) {
                actor.threshold = 0;
            } else {
                actor.threshold--;
            }
        }

        // turn towards movement direction if not there yet
        if (actor.movedir != DI_NODIR) {
            actor.angle &= (7 << 29);
            delta = actor.angle - (actor.movedir.ordinal() << 29);

            if (delta > 0) {
                actor.angle -= ANG90 / 2;
            } else if (delta < 0) {
                actor.angle += ANG90 / 2;
            }
        }

        if (null == actor.target
                || 0 == (actor.target.flags & MF_SHOOTABLE.getValue())) {
            // look for a new target
            if (P_LookForPlayers(actor, true)) {
                return; 	// got a new target
            }
            MObject.P_SetMobjState(actor, actor.info.spawnstate);
            return;
        }

        // do not attack twice in a row
        if ((actor.flags & MF_JUSTATTACKED.getValue()) > 0) {
            actor.flags &= ~MF_JUSTATTACKED.getValue();
            if (game.gameskill != sk_nightmare && !game.fastparm) {
                P_NewChaseDir(actor);
            }
            return;
        }

        // check for melee attack
        if (actor.info.meleestate.ordinal()>0
                && P_CheckMeleeRange(actor)) {
            if (actor.info.attacksound != null) {
                game.sound.S_StartSound(actor, actor.info.attacksound);
            }

            MObject.P_SetMobjState(actor, actor.info.meleestate);
            return;
        }

        // check for missile attack
        if (actor.info.missilestate != null) {
            if (game.gameskill.getValue() < sk_nightmare.getValue()
                    && !game.fastparm && actor.movecount > 0) {
                //goto nomissile;
            } else if (!P_CheckMissileRange(actor)) {
                //goto nomissile;
            } else {
                MObject.P_SetMobjState(actor, actor.info.missilestate);
                actor.flags |= MF_JUSTATTACKED.getValue();
                return;
            }
        }

        // ?
        //nomissile:
        // possibly choose another target
        if (game.netgame
                && 0 == actor.threshold
                && !game.sight.P_CheckSight(actor, actor.target)) {
            if (P_LookForPlayers(actor, true)) {
                return;	// got a new target
            }
        }

        actor.movecount--;
        // chase towards player
        if (actor.movecount < 0 || !P_Move(actor)) {
            P_NewChaseDir(actor);
        }

        // make active sound
        if (actor.info.activesound != null
                && Random.getInstance().P_Random() < 3) {
            game.sound.S_StartSound(actor, actor.info.activesound);
        }
    }


    //
    // A_FaceTarget
    //
    void A_FaceTarget(MapObject actor) {
        if (null == actor.target) {
            return;
        }

        actor.flags &= ~MF_AMBUSH.getValue();

        actor.angle = game.renderer.R_PointToAngle2(actor.x,
                actor.y,
                actor.target.x,
                actor.target.y);

        if ((actor.target.flags & MF_SHADOW.getValue()) > 0) {
            actor.angle += (Random.getInstance().P_Random() - Random.getInstance().P_Random()) << 21;
        }
    }


    //
    // A_PosAttack
    //
    void A_PosAttack(MapObject actor) {
        int angle;
        int damage;
        int slope;

        if (null == actor.target) {
            return;
        }

        Random rand = Random.getInstance();
        A_FaceTarget(actor);
        angle = actor.angle;
        slope = game.map.P_AimLineAttack(actor, angle, MISSILERANGE);

        game.sound.S_StartSound(actor, sfx_pistol);
        angle += (rand.P_Random() - rand.P_Random()) << 20;
        damage = ((rand.P_Random() % 5) + 1) * 3;
        game.map.P_LineAttack(actor, angle, MISSILERANGE, slope, damage);
    }

    void A_SPosAttack(MapObject actor) {
        int i;
        int angle;
        int bangle;
        int damage;
        int slope;

        if (null == actor.target) {
            return;
        }

        Random rand = Random.getInstance();
        game.sound.S_StartSound(actor, sfx_shotgn);
        A_FaceTarget(actor);
        bangle = actor.angle;
        slope = game.map.P_AimLineAttack(actor, bangle, MISSILERANGE);

        for (i = 0; i < 3; i++) {
            angle = bangle + ((rand.P_Random() - rand.P_Random()) << 20);
            damage = ((rand.P_Random() % 5) + 1) * 3;
            game.map.P_LineAttack(actor, angle, MISSILERANGE, slope, damage);
        }
    }

    void A_CPosAttack(MapObject actor) {
        int angle;
        int bangle;
        int damage;
        int slope;

        if (null == actor.target) {
            return;
        }

        Random rand = Random.getInstance();

        game.sound.S_StartSound(actor, sfx_shotgn);
        A_FaceTarget(actor);
        bangle = actor.angle;
        slope = game.map.P_AimLineAttack(actor, bangle, MISSILERANGE);

        angle = bangle + ((rand.P_Random() - rand.P_Random()) << 20);
        damage = ((rand.P_Random() % 5) + 1) * 3;
        game.map.P_LineAttack(actor, angle, MISSILERANGE, slope, damage);
    }

    void A_CPosRefire(MapObject actor) {
        // keep firing unless target got out of sight
        A_FaceTarget(actor);

        if (Random.getInstance().P_Random() < 40) {
            return;
        }

        if (null == actor.target
                || actor.target.health <= 0
                || !game.sight.P_CheckSight(actor, actor.target)) {
            MObject.P_SetMobjState(actor, actor.info.seestate);
        }
    }


    void A_SpidRefire(MapObject actor) {
        // keep firing unless target got out of sight
        A_FaceTarget(actor);

        if (Random.getInstance().P_Random() < 10) {
            return;
        }

        if (null == actor.target
                || actor.target.health <= 0
                || !game.sight.P_CheckSight(actor, actor.target)) {
            MObject.P_SetMobjState(actor, actor.info.seestate);
        }
    }

    void A_BspiAttack(MapObject actor) {
        if (null == actor.target) {
            return;
        }

        A_FaceTarget(actor);

        // launch a missile
        MObject.P_SpawnMissile(actor, actor.target, MT_ARACHPLAZ);
    }


    //
    // A_TroopAttack
    //
    void A_TroopAttack(MapObject actor) {
        int damage;

        if (null == actor.target) {
            return;
        }

        A_FaceTarget(actor);
        if (P_CheckMeleeRange(actor)) {
            game.sound.S_StartSound(actor, sfx_claw);
            damage = (Random.getInstance().P_Random() % 8 + 1) * 3;
            Interaction.P_DamageMobj(actor.target, actor, actor, damage);
            return;
        }

        // launch a missile
        MObject.P_SpawnMissile(actor, actor.target, MT_TROOPSHOT);
    }


    void A_SargAttack(MapObject actor) {
        int damage;

        if (null == actor.target) {
            return;
        }

        A_FaceTarget(actor);
        if (P_CheckMeleeRange(actor)) {
            damage = ((Random.getInstance().P_Random() % 10) + 1) * 4;
            Interaction.P_DamageMobj(actor.target, actor, actor, damage);
        }
    }

    void A_HeadAttack(MapObject actor)    {
        int		damage;

        if (null==actor.target) {
            return;
        }

        A_FaceTarget (actor);
        if (P_CheckMeleeRange (actor))
        {
            damage = (Random.getInstance().P_Random()%6+1)*10;
            Interaction.P_DamageMobj (actor.target, actor, actor, damage);
            return;
        }

        // launch a missile
        MObject.P_SpawnMissile (actor, actor.target, MT_HEADSHOT);
    }

    void A_CyberAttack (MapObject actor)
    {	
        if (null==actor.target) {
            return;
        }

        A_FaceTarget (actor);
        MObject.P_SpawnMissile (actor, actor.target, MT_ROCKET);
    }


    void A_BruisAttack(MapObject actor) {
        int damage;

        if (null == actor.target) {
            return;
        }

        if (P_CheckMeleeRange(actor)) {
            game.sound.S_StartSound(actor, sfx_claw);
            damage = (Random.getInstance().P_Random() % 8 + 1) * 10;
            Interaction.P_DamageMobj(actor.target, actor, actor, damage);
            return;
        }

        // launch a missile
        MObject.P_SpawnMissile(actor, actor.target, MT_BRUISERSHOT);
    }

    //
    // A_SkelMissile
    //
    void A_SkelMissile(MapObject actor) {
        MapObject mo;

        if (null == actor.target) {
            return;
        }

        A_FaceTarget(actor);
        actor.z += 16 * FRACUNIT;	// so missile spawns higher
        mo = MObject.P_SpawnMissile(actor, actor.target, MT_TRACER);
        actor.z -= 16 * FRACUNIT;	// back to normal

        mo.x += mo.momx;
        mo.y += mo.momy;
        mo.tracer = actor.target;
    }

    public static final int TRACEANGLE = 0xc000000;

    void A_Tracer(MapObject actor) {
        int exact;
        int dist;
        int slope;
        MapObject dest;
        MapObject th;

        if ((game.gametic & 3) > 0) {
            return;
        }

        // spawn a puff of smoke behind the rocket		
        MObject.P_SpawnPuff(actor.x, actor.y, actor.z);

        th = MObject.P_SpawnMobj(actor.x - actor.momx,
                actor.y - actor.momy,
                actor.z, MT_SMOKE);

        th.momz = FRACUNIT;
        th.tics -= Random.getInstance().P_Random() & 3;
        if (th.tics < 1) {
            th.tics = 1;
        }

        // adjust direction
        dest = actor.tracer;

        if (null == dest || dest.health <= 0) {
            return;
        }

        // change angle	
        exact = game.renderer.R_PointToAngle2(actor.x,
                actor.y,
                dest.x,
                dest.y);

        if (exact != actor.angle) {
            if (exact - actor.angle > 0x80000000) {
                actor.angle -= TRACEANGLE;
                if (exact - actor.angle < 0x80000000) {
                    actor.angle = exact;
                }
            } else {
                actor.angle += TRACEANGLE;
                if (exact - actor.angle > 0x80000000) {
                    actor.angle = exact;
                }
            }
        }

        exact = actor.angle >> ANGLETOFINESHIFT;
        actor.momx = FixedPoint.mul(actor.info.speed, finecosine(exact));
        actor.momy = FixedPoint.mul(actor.info.speed, finesine(exact));

        // change slope
        dist = game.map.util.P_AproxDistance(dest.x - actor.x,
                dest.y - actor.y);

        dist /= actor.info.speed;

        if (dist < 1) {
            dist = 1;
        }
        slope = (dest.z + 40 * FRACUNIT - actor.z) / dist;

        if (slope < actor.momz) {
            actor.momz -= FRACUNIT / 8;
        } else {
            actor.momz += FRACUNIT / 8;
        }
    }


    public void A_SkelWhoosh(MapObject actor) {
        if (null==actor.target) {
            return;
        }
        A_FaceTarget(actor);
        game.sound.S_StartSound(actor, sfx_skeswg);
    }

    public void A_SkelFist(MapObject actor) {
        int damage;

        if (null==actor.target) {
            return;
        }

        A_FaceTarget(actor);

        if (P_CheckMeleeRange(actor)) {
            damage = ((Random.getInstance().P_Random() % 10) + 1) * 6;
            game.sound.S_StartSound(actor, sfx_skepch);
            Interaction.P_DamageMobj(actor.target, actor, actor, damage);
        }
    }
*/

    //
    // PIT_VileCheck
    // Detect a corpse that could be raised.
    //
    MapObject		corpsehit;
    MapObject		vileobj;
    int		viletryx;
    int		viletryy;

    public boolean PIT_VileCheck(MapObject thing) {
        int maxdist;
        boolean check;

        if (0 == (thing.flags & MF_CORPSE.getValue())) {
            return true;	// not a monster
        }
        if (thing.tics != -1) {
            return true;	// not lying still yet
        }
        if (thing.info.raisestate == S_NULL) {
            return true;	// monster doesn't have a raise state
        }
        maxdist = thing.info.radius + mobjinfo[MT_VILE.ordinal()].radius;

        if (Math.abs(thing.x - viletryx) > maxdist
                || Math.abs(thing.y - viletryy) > maxdist) {
            return true;		// not actually touching
        }
        corpsehit = thing;
        corpsehit.momx = 0;
        corpsehit.momy = 0;
        corpsehit.height <<= 2;
        check = game.map.P_CheckPosition(corpsehit, corpsehit.x, corpsehit.y);
        corpsehit.height >>= 2;
        // got one, so stop checking
        
        return !check;	// got one, stop checking	
    }

/*
    //
    // A_VileChase
    // Check for ressurecting a body
    //
    void A_VileChase(MapObject actor) {
        int xl;
        int xh;
        int yl;
        int yh;

        int bx;
        int by;

        MobJInfo info;
        MapObject temp;

        if (actor.movedir != DI_NODIR) {
            // check for corpses to raise
            viletryx = actor.x + actor.info.speed * xspeed[actor.movedir.ordinal()];
            viletryy = actor.y + actor.info.speed * yspeed[actor.movedir.ordinal()];

            xl = (viletryx - game.playerSetup.bmaporgx - MAXRADIUS * 2) >> MAPBLOCKSHIFT;
            xh = (viletryx - game.playerSetup.bmaporgx + MAXRADIUS * 2) >> MAPBLOCKSHIFT;
            yl = (viletryy - game.playerSetup.bmaporgy - MAXRADIUS * 2) >> MAPBLOCKSHIFT;
            yh = (viletryy - game.playerSetup.bmaporgy + MAXRADIUS * 2) >> MAPBLOCKSHIFT;

            vileobj = actor;
            for (bx = xl; bx <= xh; bx++) {
                for (by = yl; by <= yh; by++) {
                    // Call PIT_VileCheck to check
                    // whether object is a corpse
                    // that canbe raised.
                    if (!game.map.util.P_BlockThingsIterator(bx, by, new PIT_VileCheck())) {
                        // got one!
                        temp = actor.target;
                        actor.target = corpsehit;
                        A_FaceTarget(actor);
                        actor.target = temp;

                        MObject.P_SetMobjState(actor, S_VILE_HEAL1);
                        game.sound.S_StartSound(corpsehit, sfx_slop);
                        info = corpsehit.info;

                        MObject.P_SetMobjState(corpsehit, info.raisestate);
                        corpsehit.height <<= 2;
                        corpsehit.flags = info.flags;
                        corpsehit.health = info.spawnhealth;
                        corpsehit.target = null;

                        return;
                    }
                }
            }
        }

        // Return to normal attack.
        A_Chase(actor);
    }

    //
    // A_VileStart
    //
    void A_VileStart(MapObject actor) {
        game.sound.S_StartSound(actor, sfx_vilatk);
    }


    //
    // A_Fire
    // Keep fire in front of player unless out of sight
    //
    //void A_Fire (MapObject actor);

    void A_StartFire(MapObject actor) {
        game.sound.S_StartSound(actor, sfx_flamst);
        A_Fire(actor);
    }

    void A_FireCrackle(MapObject actor) {
        game.sound.S_StartSound(actor, sfx_flame);
        A_Fire(actor);
    }

    void A_Fire(MapObject actor) {
        MapObject dest;
        int an;

        dest = actor.tracer;
        if (null == dest) {
            return;
        }

        // don't move it if the vile lost sight
        if (!game.sight.P_CheckSight(actor.target, dest)) {
            return;
        }

        an = dest.angle >> ANGLETOFINESHIFT;

        game.map.util.P_UnsetThingPosition(actor);
        actor.x = dest.x + FixedPoint.mul(24 * FRACUNIT, finecosine(an));
        actor.y = dest.y + FixedPoint.mul(24 * FRACUNIT, finesine(an));
        actor.z = dest.z;
        game.map.util.P_SetThingPosition(actor);
    }



    //
    // A_VileTarget
    // Spawn the hellfire
    //
    void A_VileTarget(MapObject actor) {
        MapObject fog;

        if (null == actor.target) {
            return;
        }

        A_FaceTarget(actor);

        fog = MObject.P_SpawnMobj(actor.target.x,
                actor.target.x,
                actor.target.z, MT_FIRE);

        actor.tracer = fog;
        fog.target = actor;
        fog.tracer = actor.target;
        A_Fire(fog);
    }



    //
    // A_VileAttack
    //
    void A_VileAttack(MapObject actor) {
        MapObject fire;
        int an;

        if (null==actor.target) {
            return;
        }

        A_FaceTarget(actor);

        if (!game.sight.P_CheckSight(actor, actor.target)) {
            return;
        }

        game.sound.S_StartSound(actor, sfx_barexp);
        Interaction.P_DamageMobj(actor.target, actor, actor, 20);
        actor.target.momz = 1000 * FRACUNIT / actor.target.info.mass;

        an = actor.angle >> ANGLETOFINESHIFT;

        fire = actor.tracer;

        if (null==fire) {
            return;
        }

        // move the fire between the vile and the player
        fire.x = actor.target.x - FixedPoint.mul(24 * FRACUNIT, finecosine(an));
        fire.y = actor.target.y - FixedPoint.mul(24 * FRACUNIT, finesine(an));
        game.map.P_RadiusAttack(fire, actor, 70);
    }


    //
    // Mancubus attack,
    // firing three missiles (bruisers)
    // in three different directions?
    // Doesn't look like it. 
    //
    public static final int FATSPREAD = (ANG90 / 8);

    void A_FatRaise(MapObject actor) {
        A_FaceTarget(actor);
        game.sound.S_StartSound(actor, sfx_manatk);
    }


    void A_FatAttack1(MapObject actor) {
        MapObject mo;
        int an;

        A_FaceTarget(actor);
        // Change direction  to ...
        actor.angle += FATSPREAD;
        MObject.P_SpawnMissile(actor, actor.target, MT_FATSHOT);

        mo = MObject.P_SpawnMissile(actor, actor.target, MT_FATSHOT);
        mo.angle += FATSPREAD;
        an = mo.angle >> ANGLETOFINESHIFT;
        mo.momx = FixedPoint.mul(mo.info.speed, finecosine(an));
        mo.momy = FixedPoint.mul(mo.info.speed, finesine(an));
    }

    void A_FatAttack2(MapObject actor) {
        MapObject mo;
        int an;

        A_FaceTarget(actor);
        // Now here choose opposite deviation.
        actor.angle -= FATSPREAD;
        MObject.P_SpawnMissile(actor, actor.target, MT_FATSHOT);

        mo = MObject.P_SpawnMissile(actor, actor.target, MT_FATSHOT);
        mo.angle -= FATSPREAD * 2;
        an = mo.angle >> ANGLETOFINESHIFT;
        mo.momx = FixedPoint.mul(mo.info.speed, finecosine(an));
        mo.momy = FixedPoint.mul(mo.info.speed, finesine(an));
    }

    void A_FatAttack3(MapObject actor) {
        MapObject mo;
        int an;

        A_FaceTarget(actor);

        mo = MObject.P_SpawnMissile(actor, actor.target, MT_FATSHOT);
        mo.angle -= FATSPREAD / 2;
        an = mo.angle >> ANGLETOFINESHIFT;
        mo.momx = FixedPoint.mul(mo.info.speed, finecosine(an));
        mo.momy = FixedPoint.mul(mo.info.speed, finesine(an));

        mo = MObject.P_SpawnMissile(actor, actor.target, MT_FATSHOT);
        mo.angle += FATSPREAD / 2;
        an = mo.angle >> ANGLETOFINESHIFT;
        mo.momx = FixedPoint.mul(mo.info.speed, finecosine(an));
        mo.momy = FixedPoint.mul(mo.info.speed, finesine(an));
    }

    //
    // SkullAttack
    // Fly at the player like a missile.
    //
    public static final int SKULLSPEED = (20 * FRACUNIT);

    void A_SkullAttack(MapObject actor) {
        MapObject dest;
        int an;
        int dist;

        if (null == actor.target) {
            return;
        }

        dest = actor.target;
        actor.flags |= MF_SKULLFLY.getValue();

        game.sound.S_StartSound(actor, actor.info.attacksound);
        A_FaceTarget(actor);
        an = actor.angle >> ANGLETOFINESHIFT;
        actor.momx = FixedPoint.mul(SKULLSPEED, finecosine(an));
        actor.momy = FixedPoint.mul(SKULLSPEED, finesine(an));
        dist = game.map.util.P_AproxDistance(dest.x - actor.x, dest.y - actor.y);
        dist /= SKULLSPEED;

        if (dist < 1) {
            dist = 1;
        }
        actor.momz = (dest.z + (dest.height >> 1) - actor.z) / dist;
    }


    //
    // A_PainShootSkull
    // Spawn a lost soul and launch it at the target
    //
    void A_PainShootSkull(MapObject actor, int angle) {
        int x;
        int y;
        int z;

        MapObject newmobj;
        int an;
        int prestep;
        int count;
        Thinker currentthinker;

        // count total number of skull currently on the level
        count = 0;

        currentthinker = game.thinkercap.getNextThinker();
        while (currentthinker != game.thinkercap) {
            if ((currentthinker.getFunction() instanceof T_MobjThinker)
                    && ((MapObject) currentthinker).type == MT_SKULL) {
                count++;
            }
            currentthinker = currentthinker.getNextThinker();
        }

        // if there are allready 20 skulls on the level,
        // don't spit another one
        if (count > 20) {
            return;
        }

        // okay, there's playe for another one
        an = angle >> ANGLETOFINESHIFT;

        prestep = 4 * FRACUNIT
                + 3 * (actor.info.radius + mobjinfo[MT_SKULL.ordinal()].radius) / 2;

        x = actor.x + FixedPoint.mul(prestep, finecosine(an));
        y = actor.y + FixedPoint.mul(prestep, finesine(an));
        z = actor.z + 8 * FRACUNIT;

        newmobj = MObject.P_SpawnMobj(x, y, z, MT_SKULL);

        // Check for movements.
        if (!game.map.P_TryMove(newmobj, newmobj.x, newmobj.y)) {
            // kill it immediately
            Interaction.P_DamageMobj(newmobj, actor, actor, 10000);
            return;
        }

        newmobj.target = actor.target;
        A_SkullAttack(newmobj);
    }

    //
    // A_PainAttack
    // Spawn a lost soul and launch it at the target
    // 
    void A_PainAttack(MapObject actor) {
        if (null == actor.target) {
            return;
        }

        A_FaceTarget(actor);
        A_PainShootSkull(actor, actor.angle);
    }

    void A_PainDie(MapObject actor) {
        A_Fall(actor);
        A_PainShootSkull(actor, actor.angle + ANG90);
        A_PainShootSkull(actor, actor.angle + ANG180);
        A_PainShootSkull(actor, actor.angle + ANG270);
    }

    void A_Scream(MapObject actor) {
        SfxEnum sound;

        switch (actor.info.deathsound) {
            case sfx_None:
                return;

            case sfx_podth1:
            case sfx_podth2:
            case sfx_podth3:
                sound = SfxEnum.values()[sfx_podth1.ordinal() + Random.getInstance().P_Random() % 3];
                break;

            case sfx_bgdth1:
            case sfx_bgdth2:
                sound = SfxEnum.values()[sfx_bgdth1.ordinal() + Random.getInstance().P_Random() % 2];
                break;

            default:
                sound = actor.info.deathsound;
                break;
        }

        // Check for bosses.
        if (actor.type == MT_SPIDER
                || actor.type == MT_CYBORG) {
            // full volume
            game.sound.S_StartSound(null, sound);
        } else {
            game.sound.S_StartSound(actor, sound);
        }
    }

    void A_XScream(MapObject actor) {
        game.sound.S_StartSound(actor, sfx_slop);
    }

    void A_Pain(MapObject actor) {
        if (actor.info.painsound != null) {
            game.sound.S_StartSound(actor, actor.info.painsound);
        }
    }

    void A_Fall(MapObject actor) {
        // actor is on ground, it can be walked over
        actor.flags &= ~MF_SOLID.getValue();

        // So change this if corpse objects
        // are meant to be obstacles.
    }

    //
    // A_Explode
    //
    void A_Explode(MapObject thingy) {
        game.map.P_RadiusAttack(thingy, thingy.target, 128);
    }

    //
    // A_BossDeath
    // Possibly trigger special effects
    // if on first boss level
    //
    void A_BossDeath(MapObject mo) {
        Thinker th;
        MapObject mo2;
        Line junk = new Line();
        int i;
        int gamemap = game.gamemap;
        GameMode gamemode = game.gameMode;

        if (gamemode == GameMode.COMMERCIAL) {
            if (gamemap != 7) {
                return;
            }

            if ((mo.type != MT_FATSO)
                    && (mo.type != MT_BABY)) {
                return;
            }
        } else {
            switch (game.gameepisode) {
                case 1:
                    if (gamemap != 8) {
                        return;
                    }

                    if (mo.type != MT_BRUISER) {
                        return;
                    }
                    break;

                case 2:
                    if (gamemap != 8) {
                        return;
                    }

                    if (mo.type != MT_CYBORG) {
                        return;
                    }
                    break;

                case 3:
                    if (gamemap != 8) {
                        return;
                    }

                    if (mo.type != MT_SPIDER) {
                        return;
                    }

                    break;

                case 4:
                    switch (gamemap) {
                        case 6:
                            if (mo.type != MT_CYBORG) {
                                return;
                            }
                            break;

                        case 8:
                            if (mo.type != MT_SPIDER) {
                                return;
                            }
                            break;

                        default:
                            return;
                    }
                    break;

                default:
                    if (gamemap != 8) {
                        return;
                    }
                    break;
            }

        }

        // make sure there is a player alive for victory
        for (i = 0; i < MAXPLAYERS; i++) {
            if (game.playeringame[i] && game.players[i].health > 0) {
                break;
            }
        }

        if (i == MAXPLAYERS) {
            return;	// no one left alive, so do not end game
        }
        // scan the remaining thinkers to see
        // if all bosses are dead
        for (th = game.thinkercap.getNextThinker(); th != game.thinkercap; th = th.getNextThinker()) {
            if (!(th.getFunction() instanceof T_MobjThinker)) {
                continue;
            }

            mo2 = (MapObject) th;
            if (mo2 != mo
                    && mo2.type == mo.type
                    && mo2.health > 0) {
                // other boss not dead
                return;
            }
        }

        // victory!
        if (gamemode == GameMode.COMMERCIAL) {
            if (gamemap == 7) {
                if (mo.type == MT_FATSO) {
                    junk.tag = 666;
                    Floor.EV_DoFloor(junk, lowerFloorToLowest);
                    return;
                }

                if (mo.type == MT_BABY) {
                    junk.tag = 667;
                    Floor.EV_DoFloor(junk, raiseToTexture);
                    return;
                }
            }
        } else {
            switch (game.gameepisode) {
                case 1:
                    junk.tag = 666;
                    Floor.EV_DoFloor(junk, lowerFloorToLowest);
                    return;

                case 4:
                    switch (gamemap) {
                        case 6:
                            junk.tag = 666;
                            VDoor.EV_DoDoor(junk, VDoor.Type.blazeOpen);
                            return;

                        case 8:
                            junk.tag = 666;
                            Floor.EV_DoFloor(junk, lowerFloorToLowest);
                            return;
                    }
            }
        }

        game.G_ExitLevel();
    }

    void A_Hoof(MapObject mo) {
        game.sound.S_StartSound(mo, sfx_hoof);
        A_Chase(mo);
    }

    void A_Metal(MapObject mo) {
        game.sound.S_StartSound(mo, sfx_metal);
        A_Chase(mo);
    }

    void A_BabyMetal(MapObject mo) {
        game.sound.S_StartSound(mo, sfx_bspwlk);
        A_Chase(mo);
    }

    void A_OpenShotgun2(Player player,
                    PSprite psp) {
        game.sound.S_StartSound(player.mo, sfx_dbopn);
    }

    void A_LoadShotgun2(Player player,
                    PSprite psp) {
        game.sound.S_StartSound(player.mo, sfx_dbload);
    }


    void A_CloseShotgun2(Player player,
                    PSprite psp) {
        game.sound.S_StartSound(player.mo, sfx_dbcls);
        //PSprite.A_ReFire(player, psp);
        new A_ReFire().doAction(player, psp);
    }
*/
    MapObject braintargets[] = new MapObject[32];
    int numbraintargets;
    int braintargeton;

    void A_BrainAwake(MapObject mo) {
        Thinker thinker;
        MapObject m;

        // find all the target spots
        numbraintargets = 0;
        braintargeton = 0;

        //thinker = game.thinkercap.getNextThinker();
        for (thinker = game.thinkercap.getNextThinker();
                thinker !=  game.thinkercap;
                thinker = thinker.getNextThinker()) {
            if (!(thinker.getFunction() instanceof T_MobjThinker)) {
                continue;	// not a mobj
            }
            m = (MapObject) thinker;

            if (m.type == MT_BOSSTARGET) {
                braintargets[numbraintargets] = m;
                numbraintargets++;
            }
        }

        game.sound.S_StartSound(null, sfx_bossit);
    }


    void A_BrainPain(MapObject mo) {
        game.sound.S_StartSound(null, sfx_bospn);
    }

    void A_BrainScream(MapObject mo) {
        int x;
        int y;
        int z;
        MapObject th;

        for (x = mo.x - 196 * FRACUNIT; x < mo.x + 320 * FRACUNIT; x += FRACUNIT * 8) {
            y = mo.y - 320 * FRACUNIT;
            z = 128 + Random.getInstance().P_Random() * 2 * FRACUNIT;
            th = MObject.P_SpawnMobj(x, y, z, MT_ROCKET);
            th.momz = Random.getInstance().P_Random() * 512;

            MObject.P_SetMobjState(th, S_BRAINEXPLODE1);

            th.tics -= Random.getInstance().P_Random() & 7;
            if (th.tics < 1) {
                th.tics = 1;
            }
        }

        game.sound.S_StartSound(null, sfx_bosdth);
    }


    void A_BrainExplode(MapObject mo) {
        int x;
        int y;
        int z;
        MapObject th;

        x = mo.x + (Random.getInstance().P_Random() - Random.getInstance().P_Random()) * 2048;
        y = mo.y;
        z = 128 + Random.getInstance().P_Random() * 2 * FRACUNIT;
        th = MObject.P_SpawnMobj(x, y, z, MT_ROCKET);
        th.momz = Random.getInstance().P_Random() * 512;

        MObject.P_SetMobjState(th, S_BRAINEXPLODE1);

        th.tics -= Random.getInstance().P_Random() & 7;
        if (th.tics < 1) {
            th.tics = 1;
        }
    }

    void A_BrainDie(MapObject mo) {
        game.G_ExitLevel();
    }

    private boolean easy = false;

    void A_BrainSpit(MapObject mo) {
        MapObject targ;
        MapObject newmobj;

        easy = !easy;
        if (game.gameskill.getValue() <= sk_easy.getValue() && (!easy)) {
            return;
        }

        // shoot a cube at current target
        targ = braintargets[braintargeton];
        braintargeton = (braintargeton + 1) % numbraintargets;

        // spawn brain missile
        newmobj = MObject.P_SpawnMissile(mo, targ, MT_SPAWNSHOT);
        newmobj.target = targ;
        newmobj.reactiontime
                = (int) (((targ.y - mo.y) / newmobj.momy) / newmobj.state.tics);

        game.sound.S_StartSound(null, sfx_bospit);
    }


    // travelling cube sound
    void A_SpawnSound(MapObject mo) {
        game.sound.S_StartSound(mo, sfx_boscub);
        A_SpawnFly(mo);
    }

    void A_SpawnFly(MapObject mo) {
        MapObject newmobj;
        MapObject fog;
        MapObject targ;
        int r;
        MobJInfo.Type type;

        --mo.reactiontime;
        if (mo.reactiontime>0) {
            return;	// still flying
        }
        targ = mo.target;

        // First spawn teleport fog.
        fog = MObject.P_SpawnMobj(targ.x, targ.y, targ.z, MT_SPAWNFIRE);
        game.sound.S_StartSound(fog, sfx_telept);

        // Randomly select monster to spawn.
        r = Random.getInstance().P_Random();

        // Probability distribution (kind of :),
        // decreasing likelihood.
        if (r < 50) {
            type = MT_TROOP;
        } else if (r < 90) {
            type = MT_SERGEANT;
        } else if (r < 120) {
            type = MT_SHADOWS;
        } else if (r < 130) {
            type = MT_PAIN;
        } else if (r < 160) {
            type = MT_HEAD;
        } else if (r < 162) {
            type = MT_VILE;
        } else if (r < 172) {
            type = MT_UNDEAD;
        } else if (r < 192) {
            type = MT_BABY;
        } else if (r < 222) {
            type = MT_FATSO;
        } else if (r < 246) {
            type = MT_KNIGHT;
        } else {
            type = MT_BRUISER;
        }

        newmobj = MObject.P_SpawnMobj(targ.x, targ.y, targ.z, type);
        if (P_LookForPlayers(newmobj, true)) {
            MObject.P_SetMobjState(newmobj, newmobj.info.seestate);
        }

        // telefrag anything in this spot
        game.map.P_TeleportMove(newmobj, newmobj.x, newmobj.y);

        // remove self (i.e., cube).
        game.movingObject.P_RemoveMobj(mo);
    }

    void A_PlayerScream(MapObject mo) {
        // Default death sound.
        SfxEnum sound = sfx_pldeth;

        if ((game.gameMode == GameMode.COMMERCIAL)
                && (mo.health < -50)) {
            // IF THE PLAYER DIES
            // LESS THAN -50% WITHOUT GIBBING
            sound = sfx_pdiehi;
        }

        game.sound.S_StartSound(mo, sound);
    }
}
