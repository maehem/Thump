/*
 *  Movement, collision handling.
 *  Shooting and aiming.
 */
package thump.play;

import thump.game.Game;
import thump.game.Player;
import thump.game.PlayerSetup;
import thump.global.FixedPoint;
import static thump.global.FixedPoint.FRACBITS;
import static thump.global.FixedPoint.FRACUNIT;
import static thump.global.MobJInfo.Type.MT_BLOOD;
import static thump.global.MobJInfo.Type.MT_BRUISER;
import static thump.global.MobJInfo.Type.MT_CYBORG;
import static thump.global.MobJInfo.Type.MT_KNIGHT;
import static thump.global.MobJInfo.Type.MT_PLAYER;
import static thump.global.MobJInfo.Type.MT_SPIDER;
import thump.global.Random;
import static thump.global.State.StateNum.S_GIBS;
import thump.global.SystemInterface;
import static thump.global.Tables.ANG180;
import static thump.global.Tables.ANGLETOFINESHIFT;
import static thump.global.Tables.finecosine;
import static thump.global.Tables.finesine;
import thump.maplevel.MapObject;
import static thump.maplevel.MapObject.MobileObjectFlag.MF_DROPOFF;
import static thump.maplevel.MapObject.MobileObjectFlag.MF_DROPPED;
import static thump.maplevel.MapObject.MobileObjectFlag.MF_FLOAT;
import static thump.maplevel.MapObject.MobileObjectFlag.MF_MISSILE;
import static thump.maplevel.MapObject.MobileObjectFlag.MF_NOBLOOD;
import static thump.maplevel.MapObject.MobileObjectFlag.MF_NOCLIP;
import static thump.maplevel.MapObject.MobileObjectFlag.MF_PICKUP;
import static thump.maplevel.MapObject.MobileObjectFlag.MF_SHOOTABLE;
import static thump.maplevel.MapObject.MobileObjectFlag.MF_SKULLFLY;
import static thump.maplevel.MapObject.MobileObjectFlag.MF_SOLID;
import static thump.maplevel.MapObject.MobileObjectFlag.MF_SPECIAL;
import static thump.maplevel.MapObject.MobileObjectFlag.MF_TELEPORT;
import static thump.play.Local.MAPBLOCKSHIFT;
import static thump.play.Local.MAXRADIUS;
import static thump.play.Local.USERANGE;
import static thump.play.MapUtil.PT_ADDLINES;
import static thump.play.MapUtil.PT_ADDTHINGS;
import thump.play.PITfuncs.PIT_ChangeSector;
import thump.play.PITfuncs.PIT_CheckLine;
import thump.play.PITfuncs.PIT_CheckThing;
import thump.play.PITfuncs.PIT_RadiusAttack;
import thump.play.PITfuncs.PIT_StompThing;
import thump.play.PTRfuncs.PTR_AimTraverse;
import thump.play.PTRfuncs.PTR_ShootTraverse;
import thump.play.PTRfuncs.PTR_SlideTraverse;
import thump.play.PTRfuncs.PTR_UseTraverse;
import thump.render.BoundingBox;
import static thump.render.Defines.Slopetype.ST_HORIZONTAL;
import static thump.render.Defines.Slopetype.ST_VERTICAL;
import thump.render.Line;
import static thump.render.Line.*;
import thump.render.Sector;
import thump.render.SubSector;
import static thump.sound.sfx.Sounds.SfxEnum.sfx_noway;

/**
 *
 * @author mark
 */
public class Map {

    private final Game game;

    public Map(Game game) {
        this.game = game;
        util = new MapUtil(game);
    }

    

    BoundingBox		tmbbox = new BoundingBox(0, 0, 0, 0);
    MapObject		tmthing;
    int		tmflags;
    int		tmx;
    int		tmy;


    // If "floatok" true, move would be ok
    // if within "tmfloorz - tmceilingz".
    public boolean	floatok;

    public int		tmfloorz;
    int		tmceilingz;
    int		tmdropoffz;

    // keep track of the line that lowers the ceiling,
    // so missiles don't explode against sky hack walls
    public Line	ceilingline;

    // keep track of special lines as they are hit,
    // but don't process them until the move is proven valid
    public static final int MAXSPECIALCROSS = 8;

    public Line	spechit[] = new Line[MAXSPECIALCROSS];
    public int		numspechit;

    public MapUtil util;

    //
    // TELEPORT MOVE
    // 

    //
    // PIT_StompThing
    //
    public boolean PIT_StompThing (MapObject thing) {
        int	blockdist;

        if ((thing.flags & MF_SHOOTABLE.getValue())==0 ) {
            return true;
        }

        blockdist = thing.radius + tmthing.radius;

        if ( Math.abs(thing.x - tmx) >= blockdist
             || Math.abs(thing.y - tmy) >= blockdist )
        {
            // didn't hit it
            return true;
        }

        // don't clip against self
        if (thing == tmthing) {
            return true;
        }

        // monsters don't stomp things except on boss level
        if ( tmthing.player==null && game.gamemap != 30) {
            return false;
        }	

        Interaction.P_DamageMobj (thing, tmthing, tmthing, 10000);

        return true;
    }


    //
    // P_TeleportMove
    //
    public boolean P_TeleportMove(
            MapObject thing,
            int x,
            int y) {
        int			xl;
        int			xh;
        int			yl;
        int			yh;
        int			bx;
        int			by;

        SubSector	newsubsec;

        // kill anything occupying the position
        tmthing = thing;
        tmflags = thing.flags;

        tmx = x;
        tmy = y;

        tmbbox.top = y + tmthing.radius;
        tmbbox.bottom = y - tmthing.radius;
        tmbbox.right = x + tmthing.radius;
        tmbbox.left = x - tmthing.radius;

        newsubsec = game.renderer.R_PointInSubsector (x,y);
        ceilingline = null;

        // The base floor/ceiling is from the subsector
        // that contains the point.
        // Any contacted lines the step closer together
        // will adjust them.
        tmfloorz = newsubsec.sector.floorheight;
        tmdropoffz = newsubsec.sector.floorheight;
        tmceilingz = newsubsec.sector.ceilingheight;

        game.renderer.validcount++;
        numspechit = 0;

        PlayerSetup ps = Game.getInstance().playerSetup;
        // stomp on any things contacted
        xl = (tmbbox.left - ps.bmaporgx - MAXRADIUS)>>MAPBLOCKSHIFT;
        xh = (tmbbox.right - ps.bmaporgx + MAXRADIUS)>>MAPBLOCKSHIFT;
        yl = (tmbbox.bottom - ps.bmaporgy - MAXRADIUS)>>MAPBLOCKSHIFT;
        yh = (tmbbox.top - ps.bmaporgy + MAXRADIUS)>>MAPBLOCKSHIFT;

        for (bx=xl ; bx<=xh ; bx++) {
            for (by=yl ; by<=yh ; by++) {
                if (!util.P_BlockThingsIterator(bx,by,new PIT_StompThing())) {
                    return false;
                }
            }
        }

        // the move is ok,
        // so link the thing into its new position
        util.P_UnsetThingPosition (thing);

        thing.floorz = tmfloorz;
        thing.ceilingz = tmceilingz;	
        thing.x = x;
        thing.y = y;

        util.P_SetThingPosition (thing);

        return true;
    }


    //
    // MOVEMENT ITERATOR FUNCTIONS
    //


    //
    // PIT_CheckLine
    // Adjusts tmfloorz and tmceilingz as lines are contacted
    //
    public boolean PIT_CheckLine (Line ld) {
        if (   tmbbox.right  <= ld.bbox.left
            || tmbbox.left   >= ld.bbox.right
            || tmbbox.top    <= ld.bbox.bottom
            || tmbbox.bottom >= ld.bbox.top   ) {
            return true;
        }

        if (util.P_BoxOnLineSide (tmbbox, ld) != -1) {
            return true;
        }

        // A line has been hit

        // The moving thing's destination position will cross
        // the given line.
        // If this should not be allowed, return false.
        // If the line is special, keep track of it
        // to process later if the move is proven ok.
        // NOTE: specials are NOT sorted by order,
        // so two special lines that are only 8 pixels apart
        // could be crossed in either order.

        if (ld.backsector == null) {
            return false;		// one sided line
        }

        if ((tmthing.flags & MF_MISSILE.getValue())==0 )
        {
            if ( (ld.flags & ML_BLOCKING)>0 ) {
                return false;	// explicitly blocking everything
            }

            if ( tmthing.player==null && (ld.flags & ML_BLOCKMONSTERS)>0 ) {
                return false;	// block monsters only
            }
        }

        // set openrange, opentop, openbottom
        util.P_LineOpening (ld);	

        // adjust floor / ceiling heights
        if (util.opentop < tmceilingz)
        {
            tmceilingz = util.opentop;    // p_sight
            ceilingline = ld;
        }

        if (util.openbottom > tmfloorz) {
            tmfloorz = util.openbottom;
        }	

        if (util.lowfloor < tmdropoffz) {
            tmdropoffz = util.lowfloor;
        }

        // if contacted a special line, add it to the list
        if (ld.special>0) {
            spechit[numspechit] = ld;
            numspechit++;
        }

        return true;
    }

    //
    // PIT_CheckThing
    //
    public boolean PIT_CheckThing (MapObject thing) {
        int     blockdist;
        boolean solid;
        int     damage;

        if ((thing.flags & (MF_SOLID.getValue()|MF_SPECIAL.getValue()|MF_SHOOTABLE.getValue()) )==0) {
            return true;
        }

        blockdist = thing.radius + tmthing.radius;

        if (    Math.abs(thing.x - tmx) >= blockdist
             || Math.abs(thing.y - tmy) >= blockdist )
        {
            // didn't hit it
            return true;	
        }

        // don't clip against self
        if (thing == tmthing) {
            return true;
        }

        // check for skulls slamming into things
        if ((tmthing.flags & MF_SKULLFLY.getValue())>0) {
            damage = ((Random.getInstance().P_Random()%8)+1)*tmthing.info.damage;

            Interaction.P_DamageMobj (thing, tmthing, tmthing, damage);

            tmthing.flags &= ~MF_SKULLFLY.getValue();
            tmthing.momx = 0;
            tmthing.momy = 0;
            tmthing.momz = 0;

            MObject.P_SetMobjState (tmthing, tmthing.info.spawnstate);

            return false;		// stop moving
        }


        // missiles can hit other things
        if ((tmthing.flags & MF_MISSILE.getValue())>0) {
            // see if it went over / under
            if (tmthing.z > thing.z + thing.height) {
                return true;		// overhead
            }
            if (tmthing.z+tmthing.height < thing.z) {
                return true;		// underneath
            }

            if (tmthing.target!=null && (
                tmthing.target.type == thing.type || 
                (tmthing.target.type == MT_KNIGHT && thing.type == MT_BRUISER)||
                (tmthing.target.type == MT_BRUISER && thing.type == MT_KNIGHT) ) )
            {
                // Don't hit same species as originator.
                if (thing == tmthing.target) {
                    return true;
                }

                if (thing.type != MT_PLAYER) {
                    // Explode, but do no damage.
                    // Let players missile other players.
                    return false;
                }
            }

            if ((thing.flags & MF_SHOOTABLE.getValue())==0 ) {
                // didn't do any damage
                return ((thing.flags & MF_SOLID.getValue())==0);	
            }

            // damage / explode
            damage = ((Random.getInstance().P_Random()%8)+1)*tmthing.info.damage;
            Interaction.P_DamageMobj (thing, tmthing, tmthing.target, damage);

            // don't traverse any more
            return false;				
        }

        // check for special pickup
        if ((thing.flags & MF_SPECIAL.getValue())>0) {
            solid = (thing.flags&MF_SOLID.getValue())>0;
            if ((tmflags&MF_PICKUP.getValue())>0) {
                // can remove thing
                Interaction.P_TouchSpecialThing (thing, tmthing);
            }
            return !solid;
        }

        return (thing.flags & MF_SOLID.getValue())==0;
    }


    //
    // MOVEMENT CLIPPING
    //

    //
    // P_CheckPosition
    // This is purely informative, nothing is modified
    // (except things picked up).
    // 
    // in:
    //  a MapObject (can be valid or invalid)
    //  a position to be checked
    //   (doesn't need to be related to the MapObject.x,y)
    //
    // during:
    //  special things are touched if MF_PICKUP
    //  early out on solid lines?
    //
    // out:
    //  newsubsec
    //  floorz
    //  ceilingz
    //  tmdropoffz
    //   the lowest point contacted
    //   (monsters won't move to a dropoff)
    //  speciallines[]
    //  numspeciallines
    //
    public boolean P_CheckPosition(
            MapObject	thing,
            int         x,
            int         y ) {
        int			xl;
        int			xh;
        int			yl;
        int			yh;
        int			bx;
        int			by;
        SubSector	newsubsec;

        tmthing = thing;
        tmflags = thing.flags;

        tmx = x;
        tmy = y;

        tmbbox.top = y + tmthing.radius;
        tmbbox.bottom = y - tmthing.radius;
        tmbbox.right = x + tmthing.radius;
        tmbbox.left = x - tmthing.radius;

        newsubsec = game.renderer.R_PointInSubsector (x,y);
        ceilingline = null;

        // The base floor / ceiling is from the subsector
        // that contains the point.
        // Any contacted lines the step closer together
        // will adjust them.
        tmfloorz =  newsubsec.sector.floorheight;
        tmdropoffz = newsubsec.sector.floorheight;
        tmceilingz = newsubsec.sector.ceilingheight;

        game.renderer.validcount++;
        numspechit = 0;

        if ( (tmflags & MF_NOCLIP.getValue())>0 ) {
            return true;
        }

        PlayerSetup ps = Game.getInstance().playerSetup;
        
        // Check things first, possibly picking things up.
        // The bounding box is extended by MAXRADIUS
        // because MapObjects are grouped into mapblocks
        // based on their origin point, and can overlap
        // into adjacent blocks by up to MAXRADIUS units.
        xl = (tmbbox.left - ps.bmaporgx - MAXRADIUS)>>MAPBLOCKSHIFT;
        xh = (tmbbox.right - ps.bmaporgx + MAXRADIUS)>>MAPBLOCKSHIFT;
        yl = (tmbbox.bottom - ps.bmaporgy - MAXRADIUS)>>MAPBLOCKSHIFT;
        yh = (tmbbox.top - ps.bmaporgy + MAXRADIUS)>>MAPBLOCKSHIFT;

        for (bx=xl ; bx<=xh ; bx++) {
            for (by=yl ; by<=yh ; by++) {
                if (!util.P_BlockThingsIterator(bx,by,new PIT_CheckThing())) {
                    return false;
                }
            }
        }

        // check lines
        xl = (tmbbox.left - ps.bmaporgx)>>MAPBLOCKSHIFT;
        xh = (tmbbox.right - ps.bmaporgx)>>MAPBLOCKSHIFT;
        yl = (tmbbox.bottom - ps.bmaporgy)>>MAPBLOCKSHIFT;
        yh = (tmbbox.top - ps.bmaporgy)>>MAPBLOCKSHIFT;

        for (bx=xl ; bx<=xh ; bx++) {
            for (by=yl ; by<=yh ; by++) {
                if (!util.P_BlockLinesIterator (bx,by,new PIT_CheckLine())) {
                    return false;
                }
            }
        }

        return true;
    }


    //
    // P_TryMove
    // Attempt to move to a new position,
    // crossing special lines unless MF_TELEPORT is set.
    //
    public boolean P_TryMove ( 
            MapObject	thing,
            int         x,
            int         y       ) {
        int	oldx;
        int	oldy;
        boolean	side;
        boolean	oldside;
        Line	ld;

        floatok = false;
        if (!P_CheckPosition (thing, x, y)) {
            return false;		// solid wall or thing
        }

        if ( ((thing.flags & MF_NOCLIP.getValue())==0) ) {
            if (tmceilingz - tmfloorz < thing.height) {
                return false;	// doesn't fit
            }

            floatok = true;

            if ( (thing.flags&MF_TELEPORT.getValue())==0 
                 &&tmceilingz - thing.z < thing.height) {
                return false;	// mobj must lower itself to fit
            }

            if ( (thing.flags&MF_TELEPORT.getValue())==0
                 && tmfloorz - thing.z > 24*FRACUNIT ) {
                return false;	// too big a step up
            }

            if ( (thing.flags&(MF_DROPOFF.getValue()|MF_FLOAT.getValue()))==0
                 && tmfloorz - tmdropoffz > 24*FRACUNIT ) {
                return false;	// don't stand over a dropoff
            }
        }

        // the move is ok,
        // so link the thing into its new position
        util.P_UnsetThingPosition (thing);

        oldx = thing.x;
        oldy = thing.y;
        thing.floorz = tmfloorz;
        thing.ceilingz = tmceilingz;	
        thing.x = x;
        thing.y = y;

        util.P_SetThingPosition (thing);

        // if any special lines were hit, do the effect
        if ( (thing.flags&(MF_TELEPORT.getValue()|MF_NOCLIP.getValue()))==0 )
        {
            while (numspechit>0){
                // see if the line was crossed
                ld = spechit[numspechit];
                side = util.P_PointOnLineSide (thing.x, thing.y, ld);
                oldside = util.P_PointOnLineSide (oldx, oldy, ld);
                if (side != oldside) {
                    if (ld.special>0) {
                        SpecialEffects.P_CrossSpecialLine (
                                Game.getInstance().playerSetup.getLineNum(ld), 
                                oldside?1:0, thing);
                    }
                }
                numspechit--;
            }
        }

        return true;
    }


    //
    // P_ThingHeightClip
    // Takes a valid thing and adjusts the thing.floorz,
    // thing.ceilingz, and possibly thing.z.
    // This is called for all nearby monsters
    // whenever a sector changes height.
    // If the thing doesn't fit,
    // the z will be set to the lowest value
    // and false will be returned.
    //
    public boolean P_ThingHeightClip (MapObject thing) {
        boolean		onfloor;

        onfloor = (thing.z == thing.floorz);

        P_CheckPosition (thing, thing.x, thing.y);	
        // what about stranding a monster partially off an edge?

        thing.floorz = tmfloorz;
        thing.ceilingz = tmceilingz;

        if (onfloor)
        {
            // walking monsters rise and fall with the floor
            thing.z = thing.floorz;
        }
        else
        {
            // don't adjust a floating monster unless forced to
            if (thing.z+thing.height > thing.ceilingz) {
                thing.z = thing.ceilingz - thing.height;
            }
        }

        return thing.ceilingz - thing.floorz >= thing.height;
    }



    //
    // SLIDE MOVE
    // Allows the player to slide along any angled walls.
    //
    int		bestslidefrac;
    int		secondslidefrac;

    Line	bestslideline;
    Line	secondslideline;

    MapObject	slidemo;

    int		tmxmove;
    int		tmymove;



    //
    // P_HitSlideLine
    // Adjusts the xmove / ymove
    // so that the next move will slide along the wall.
    //
    public void P_HitSlideLine(Line ld) {
        boolean side;

        int lineangle;
        int moveangle;
        int deltaangle;

        int movelen;
        int newlen;

        if (ld.slopetype == ST_HORIZONTAL) {
            tmymove = 0;
            return;
        }

        if (ld.slopetype == ST_VERTICAL) {
            tmxmove = 0;
            return;
        }

        side = util.P_PointOnLineSide(slidemo.x, slidemo.y, ld);

        lineangle = game.renderer.R_PointToAngle2(0, 0, ld.dx, ld.dy);

        if (side) {
            lineangle += ANG180;
        }

        moveangle = game.renderer.R_PointToAngle2(0, 0, tmxmove, tmymove);
        deltaangle = moveangle - lineangle;

        if (deltaangle > ANG180) {
            deltaangle += ANG180;
            //	I_Error ("SlideLine: ang>ANG180");
        }

        lineangle >>= ANGLETOFINESHIFT;
        deltaangle >>= ANGLETOFINESHIFT;

        movelen = util.P_AproxDistance(tmxmove, tmymove);
        newlen = FixedPoint.mul(movelen, finecosine(deltaangle));

        tmxmove = FixedPoint.mul(newlen, finecosine(lineangle));
        tmymove = FixedPoint.mul(newlen, finesine(lineangle));
    }

    //
    // PTR_SlideTraverse
    //
    public boolean PTR_SlideTraverse (Intercept in) {
        Line	li;

        if (!in.isaline) {
            SystemInterface.I_Error ("PTR_SlideTraverse: not a line?");
        }

        li = (Line) in.lineThing;

        if ( (li.flags & ML_TWOSIDED)==0 ) {
            if (util.P_PointOnLineSide (slidemo.x, slidemo.y, li))
            {
                // don't hit the back side
                return true;		
            }
            isblocking(in, li);
            return false;
        }

        // set openrange, opentop, openbottom
        util.P_LineOpening (li);

        if (util.openrange < slidemo.height) {
            isblocking(in, li);
            return false;
		// doesn't fit
        }

        if (util.opentop - slidemo.z < slidemo.height) {
            isblocking(in, li);
            return false;
		// mobj is too high
        }

        if (util.openbottom - slidemo.z > 24*FRACUNIT ) {
            isblocking(in, li);
            return false;
		// too big a step up
        }
        
        // this line doesn't block movement
        return true;		

//      isblocking:		
//        if (in.frac < bestslidefrac)
//        {
//            secondslidefrac = bestslidefrac;
//            secondslideline = bestslideline;
//            bestslidefrac = in.frac;
//            bestslideline = li;
//        }
//
//        return false;	// stop
    
    
    }

   // the line does block movement,
   // see if it is closer than best so far
   private void isblocking(Intercept in, Line li) {
        if (in.frac < bestslidefrac) {
            secondslidefrac = bestslidefrac;
            secondslideline = bestslideline;
            bestslidefrac = in.frac;
            bestslideline = li;
        }
        
    }



    //
    // P_SlideMove
    // The momx / momy move is bad, so try to slide
    // along a wall.
    // Find the first line hit, move flush to it,
    // and slide along it
    //
    // This is a kludgy mess.
    //
    public void P_SlideMove (MapObject mo) {
        int		leadx;
        int		leady;
        int		trailx;
        int		traily;
        int		newx;
        int		newy;
        int		hitcount;

        slidemo = mo;
        hitcount = 0;

      //retry:
        do {
            hitcount++;
            if (hitcount == 3) {
                //goto stairstep;		// don't loop forever
                if (!P_TryMove(mo, mo.x, mo.y + mo.momy)) {
                    P_TryMove(mo, mo.x + mo.momx, mo.y);
                }
                return;
            }

            // util.trace along the three leading corners
            if (mo.momx > 0) {
                leadx = mo.x + mo.radius;
                trailx = mo.x - mo.radius;
            } else {
                leadx = mo.x - mo.radius;
                trailx = mo.x + mo.radius;
            }

            if (mo.momy > 0) {
                leady = mo.y + mo.radius;
                traily = mo.y - mo.radius;
            } else {
                leady = mo.y - mo.radius;
                traily = mo.y + mo.radius;
            }

            bestslidefrac = FRACUNIT + 1;

            util.P_PathTraverse(leadx, leady, leadx + mo.momx, leady + mo.momy,
                    PT_ADDLINES, new PTR_SlideTraverse());
            util.P_PathTraverse(trailx, leady, trailx + mo.momx, leady + mo.momy,
                    PT_ADDLINES, new PTR_SlideTraverse());
            util.P_PathTraverse(leadx, traily, leadx + mo.momx, traily + mo.momy,
                    PT_ADDLINES, new PTR_SlideTraverse());

            // move up to the wall
            if (bestslidefrac == FRACUNIT + 1) {
                // the move most have hit the middle, so stairstep
                //stairstep:
                if (!P_TryMove(mo, mo.x, mo.y + mo.momy)) {
                    P_TryMove(mo, mo.x + mo.momx, mo.y);
                }
                return;
            }

            // fudge a bit to make sure it doesn't hit
            bestslidefrac -= 0x800;
            if (bestslidefrac > 0) {
                newx = FixedPoint.mul(mo.momx, bestslidefrac);
                newy = FixedPoint.mul(mo.momy, bestslidefrac);

                if (!P_TryMove(mo, mo.x + newx, mo.y + newy)) {
                    //goto stairstep;
                    if (!P_TryMove(mo, mo.x, mo.y + mo.momy)) {
                        P_TryMove(mo, mo.x + mo.momx, mo.y);
                    }
                    return;
                }
            }

            // Now continue along the wall.
            // First calculate remainder.
            bestslidefrac = FRACUNIT - (bestslidefrac + 0x800);

            if (bestslidefrac > FRACUNIT) {
                bestslidefrac = FRACUNIT;
            }

            if (bestslidefrac <= 0) {
                return;
            }

            tmxmove = FixedPoint.mul(mo.momx, bestslidefrac);
            tmymove = FixedPoint.mul(mo.momy, bestslidefrac);

            P_HitSlideLine(bestslideline);	// clip the moves

            mo.momx = tmxmove;
            mo.momy = tmymove;

        } while (!P_TryMove(mo, mo.x + tmxmove, mo.y + tmymove));
    }

    //
    // P_LineAttack
    //
    MapObject		linetarget;	// who got hit (or NULL)
    MapObject		shootthing;

    // Height if not aiming up or down
    // ???: use slope for monsters?
    int		shootz;	

    int		la_damage;
    int		attackrange;

    int		aimslope;

    // slopes to top and bottom of target
    //extern int	topslope;
    //extern int	bottomslope;	


    //
    // PTR_AimTraverse
    // Sets linetaget and aimslope when a target is aimed at.
    //
    public boolean PTR_AimTraverse(Intercept in) {
        Line li;
        MapObject th;
        int slope;
        int thingtopslope;
        int thingbottomslope;
        int dist;

        if (in.isaline) {
            li = (Line) in.lineThing;

            if ((li.flags & ML_TWOSIDED) == 0) {
                return false;		// stop
            }

            // Crosses a two sided line.
            // A two sided line will restrict
            // the possible target ranges.
            util.P_LineOpening(li);

            if (util.openbottom >= util.opentop) {
                return false;		// stop
            }
            dist = FixedPoint.mul(attackrange, in.frac);

            if (li.frontsector.floorheight != li.backsector.floorheight) {
                slope = FixedPoint.div(util.openbottom - shootz, dist);
                if (slope > game.sight.bottomslope) {
                    game.sight.bottomslope = slope;
                }
            }

            if (li.frontsector.ceilingheight != li.backsector.ceilingheight) {
                slope = FixedPoint.div(util.opentop - shootz, dist);
                if (slope < game.sight.topslope) {
                    game.sight.topslope = slope;
                }
            }

            if (game.sight.topslope <= game.sight.bottomslope) {
                return false;		// stop
            }

            return true;			// shot continues
        }

        // shoot a thing
        th = (MapObject) in.lineThing;
        if (th == shootthing) {
            return true;			// can't shoot self
        }

        if ((th.flags & MF_SHOOTABLE.getValue()) == 0) {
            return true;			// corpse or something
        }

        // check angles to see if the thing can be aimed at
        dist = FixedPoint.mul(attackrange, in.frac);
        thingtopslope = FixedPoint.div(th.z + th.height - shootz, dist);

        if (thingtopslope < game.sight.bottomslope) {
            return true;			// shot over the thing
        }

        thingbottomslope = FixedPoint.div(th.z - shootz, dist);

        if (thingbottomslope > game.sight.topslope) {
            return true;			// shot under the thing
        }

        // this thing can be hit!
        if (thingtopslope > game.sight.topslope) {
            thingtopslope = game.sight.topslope;
        }

        if (thingbottomslope < game.sight.bottomslope) {
            thingbottomslope = game.sight.bottomslope;
        }

        aimslope = (thingtopslope + thingbottomslope) / 2;
        linetarget = th;

        return false;			// don't go any farther
    }


    //
    // PTR_ShootTraverse
    //
    public boolean PTR_ShootTraverse (Intercept in) {
        int		x;
        int		y;
        int		z;
        int		frac;

        Line		li;

        MapObject		th;

        int		slope;
        int		dist;
        int		thingtopslope;
        int		thingbottomslope;

        if (in.isaline) {
            li = (Line) in.lineThing;

            if (li.special>0) {
                SpecialEffects.P_ShootSpecialLine (shootthing, li);
            }

            if ( (li.flags & ML_TWOSIDED)!=0 ) {
                // crosses a two sided line
                util.P_LineOpening (li);

                dist = FixedPoint.mul (attackrange, in.frac);

                if (li.frontsector.floorheight != li.backsector.floorheight) {
                    slope = FixedPoint.div (util.openbottom - shootz , dist);
                    if (slope > aimslope) {
                        //goto hitline;
                    } else 
                    if (li.frontsector.ceilingheight != li.backsector.ceilingheight) {
                        slope = FixedPoint.div (util.opentop - shootz , dist);
                        if (slope >= aimslope) {
                            return true;      // shot continues
                        }
                    }
                }
            }
            
            // position a bit closer
            frac = in.frac - FixedPoint.div (4*FRACUNIT,attackrange);
            x = util.trace.x + FixedPoint.mul (util.trace.dx, frac);
            y = util.trace.y + FixedPoint.mul (util.trace.dy, frac);
            z = shootz + FixedPoint.mul (aimslope, FixedPoint.mul(frac, attackrange));

            if (li.frontsector.getCeilingPic() == game.renderer.skyflatnum)
            {
                // don't shoot the sky!
                if (z > li.frontsector.ceilingheight) {
                    return false;
                }

                // it's a sky hack wall
                if	(li.backsector!=null && li.backsector.getCeilingPic() == game.renderer.skyflatnum) {
                    return false;
                }		
            }

            // Spawn bullet puffs.
            MObject.P_SpawnPuff (x,y,z);

            // don't go any farther
            return false;	
        }

        // shoot a thing
        th = (MapObject) in.lineThing;
        if (th == shootthing) {
            return true;		// can't shoot self
        }

        if ((th.flags&MF_SHOOTABLE.getValue())==0) {
            return true;		// corpse or something
        }

        // check angles to see if the thing can be aimed at
        dist = FixedPoint.mul (attackrange, in.frac);
        thingtopslope = FixedPoint.div (th.z+th.height - shootz , dist);

        if (thingtopslope < aimslope) {
            return true;		// shot over the thing
        }

        thingbottomslope = FixedPoint.div (th.z - shootz, dist);

        if (thingbottomslope > aimslope) {
            return true;		// shot under the thing
        }


        // hit thing
        // position a bit closer
        frac = in.frac - FixedPoint.div (10*FRACUNIT,attackrange);

        x = util.trace.x + FixedPoint.mul (util.trace.dx, frac);
        y = util.trace.y + FixedPoint.mul (util.trace.dy, frac);
        z = shootz + FixedPoint.mul (aimslope, FixedPoint.mul(frac, attackrange));

        // Spawn bullet puffs or blod spots,
        // depending on target type.
        if ((th.flags & MF_NOBLOOD.getValue())>0) {
            MObject.P_SpawnPuff (x,y,z);
        } else {
            MObject.P_SpawnBlood (x,y,z, la_damage);
        }

        if (la_damage>0) {
            Interaction.P_DamageMobj (th, shootthing, shootthing, la_damage);
        }

        // don't go any farther
        return false;

    }


    //
    // P_AimLineAttack
    //
    public int P_AimLineAttack ( 
            MapObject	t1,
            int         _angle,
            int         distance ) {
        int	x2;
        int	y2;

        int angle = _angle;
        angle >>= ANGLETOFINESHIFT;
        shootthing = t1;

        x2 = t1.x + (distance>>FRACBITS)*finecosine(angle);
        y2 = t1.y + (distance>>FRACBITS)*finesine(angle);
        shootz = t1.z + (t1.height>>1) + 8*FRACUNIT;

        // can't shoot outside view angles
        game.sight.topslope = 100*FRACUNIT/160;	
        game.sight.bottomslope = -100*FRACUNIT/160;

        attackrange = distance;
        linetarget = null;

        util.P_PathTraverse ( t1.x, t1.y,
                         x2, y2,
                         PT_ADDLINES|PT_ADDTHINGS,
                         new PTR_AimTraverse() );

        if (linetarget!=null) {
            return aimslope;
        }

        return 0;
    }


    //
    // P_LineAttack
    // If damage == 0, it is just a test util.trace
    // that will leave linetarget set.
    //
    public void P_LineAttack
    ( MapObject	t1,
      int	aangle,
      int	distance,
      int	slope,
      int		damage )
    {
        int	x2;
        int	y2;

        int angle = aangle;
        angle >>= ANGLETOFINESHIFT;
        shootthing = t1;
        la_damage = damage;
        x2 = t1.x + (distance>>FRACBITS)*finecosine(angle);
        y2 = t1.y + (distance>>FRACBITS)*finesine(angle);
        shootz = t1.z + (t1.height>>1) + 8*FRACUNIT;
        attackrange = distance;
        aimslope = slope;

        util.P_PathTraverse ( t1.x, t1.y,
                         x2, y2,
                         PT_ADDLINES|PT_ADDTHINGS,
                         new PTR_ShootTraverse() );
    }



    //
    // USE LINES
    //
    public MapObject		usething;

    public boolean PTR_UseTraverse (Intercept in) {
        int		side;

        if (0==((Line)in.lineThing).special) {
            util.P_LineOpening ((Line) in.lineThing);
            if (util.openrange <= 0) {
                game.sound.S_StartSound (usething, sfx_noway);

                // can't use through a wall
                return false;	
            }
            // not a special line, but keep checking
            return true ;		
        }

        side = 0;
        if (util.P_PointOnLineSide (usething.x, usething.y, (Line) in.lineThing)) {
            side = 1;
        }

        //	return false;		// don't use back side

        game.playerSetup.svitch.P_UseSpecialLine (usething, (Line) in.lineThing, side);

        // can't use for than one special line in a row
        return false;
    }


    //
    // P_UseLines
    // Looks for special lines in front of the player to activate.
    //
    public void P_UseLines (Player player) {
        int	angle;
        int	x1;
        int	y1;
        int	x2;
        int	y2;

        usething = player.mo;

        angle = player.mo.angle >> ANGLETOFINESHIFT;

        x1 = player.mo.x;
        y1 = player.mo.y;
        x2 = x1 + (USERANGE>>FRACBITS)*finecosine(angle);
        y2 = y1 + (USERANGE>>FRACBITS)*finesine(angle);

        util.P_PathTraverse ( x1, y1, x2, y2, PT_ADDLINES, new PTR_UseTraverse() );
    }


    //
    // RADIUS ATTACK
    //
    private MapObject		bombsource;
    private MapObject		bombspot;
    private int		bombdamage;


    //
    // PIT_RadiusAttack
    // "bombsource" is the creature
    // that caused the explosion at "bombspot".
    //
    public boolean PIT_RadiusAttack (MapObject thing) {
        int	dx;
        int	dy;
        int	dist;

        if ((thing.flags & MF_SHOOTABLE.getValue())==0 ) {
            return true;
        }

        // Boss spider and cyborg
        // take no damage from concussion.
        if (   thing.type == MT_CYBORG
            || thing.type == MT_SPIDER) {
            return true;
        }	

        dx = Math.abs(thing.x - bombspot.x);
        dy = Math.abs(thing.y - bombspot.y);

        dist = dx>dy ? dx : dy;
        dist = (dist - thing.radius) >> FRACBITS;

        if (dist < 0) {
            dist = 0;
        }

        if (dist >= bombdamage) {
            return true;	// out of range
        }

        if ( game.sight.P_CheckSight (thing, bombspot) )
        {
            // must be in direct path
            Interaction.P_DamageMobj (thing, bombspot, bombsource, bombdamage - dist);
        }

        return true;
    }


    //
    // P_RadiusAttack
    // Source is the creature that caused the explosion at spot.
    //
    public void P_RadiusAttack(
            MapObject     spot,
            MapObject     source,
            int         damage  ) {
        int x;
        int y;

        int xl;
        int xh;
        int yl;
        int yh;

        int dist;

        PlayerSetup ps = game.playerSetup;

        dist = (damage+MAXRADIUS)<<FRACBITS;
        yh = (spot.y + dist - ps.bmaporgy)>>MAPBLOCKSHIFT;
        yl = (spot.y - dist - ps.bmaporgy)>>MAPBLOCKSHIFT;
        xh = (spot.x + dist - ps.bmaporgx)>>MAPBLOCKSHIFT;
        xl = (spot.x - dist - ps.bmaporgx)>>MAPBLOCKSHIFT;
        bombspot = spot;
        bombsource = source;
        bombdamage = damage;

        for (y=yl ; y<=yh ; y++) {
            for (x=xl ; x<=xh ; x++) {
                util.P_BlockThingsIterator (x, y, new PIT_RadiusAttack() );
            }
        }
    }



    //
    // SECTOR HEIGHT CHANGING
    // After modifying a sectors floor or ceiling height,
    // call this routine to adjust the positions
    // of all things that touch the sector.
    //
    // If anything doesn't fit anymore, true will be returned.
    // If crunch is true, they will take damage
    //  as they are being crushed.
    // If Crunch is false, you should set the sector height back
    //  the way it was and call P_ChangeSector again
    //  to undo the changes.
    //
    private boolean		crushchange;
    private boolean		nofit;


    //
    // PIT_ChangeSector
    //
    public boolean PIT_ChangeSector (MapObject	thing) {
        MapObject	mo;

        if (P_ThingHeightClip (thing))
        {
            // keep checking
            return true;
        }


        // crunch bodies to giblets
        if (thing.health <= 0)
        {
            MObject.P_SetMobjState (thing, S_GIBS);

            thing.flags &= ~MF_SOLID.getValue();
            thing.height = 0;
            thing.radius = 0;

            // keep checking
            return true;		
        }

        // crunch dropped items
        if ((thing.flags & MF_DROPPED.getValue())>0)
        {
            game.movingObject.P_RemoveMobj (thing);

            // keep checking
            return true;		
        }

        if ( (thing.flags & MF_SHOOTABLE.getValue())==0 ){
            // assume it is bloody gibs or something
            return true;			
        }

        nofit = true;

        if (crushchange && 0==(Game.getInstance().leveltime&3) )
        {
            Interaction.P_DamageMobj(thing,null,null,10);

            // spray blood in a random direction
            mo = MObject.P_SpawnMobj (thing.x,
                              thing.y,
                              thing.z + thing.height/2, MT_BLOOD);

            Random rand = Random.getInstance();
            
            mo.momx = (rand.P_Random() - rand.P_Random ())<<12;
            mo.momy = (rand.P_Random() - rand.P_Random ())<<12;
        }

        // keep checking (crush other things)	
        return true;	
    }



    //
    // P_ChangeSector
    //
    public boolean P_ChangeSector(
            Sector sector,
            boolean crunch) {
        int x;
        int y;

        nofit = false;
        crushchange = crunch;

        // re-check heights for all things near the moving sector
        for (x=sector.blockbox.left ; x<= sector.blockbox.right ; x++) {
            for (y=sector.blockbox.bottom;y<= sector.blockbox.top ; y++) {
                util.P_BlockThingsIterator (x, y, new PIT_ChangeSector());
            }
        }


        return nofit;
    }

}
