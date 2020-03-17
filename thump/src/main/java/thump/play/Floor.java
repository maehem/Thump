/*
 * Moving Floor
 */
package thump.play;

import thump.game.Game;
import thump.game.PlayerSetup;
import thump.game.Thinker;
import thump.game.ThinkerAction;
import thump.game.thinkeraction.T_MoveFloor;
import static thump.global.FixedPoint.FRACUNIT;
import static thump.play.Floor.Type.raiseFloorCrush;
import thump.play.SpecialEffects.Result;
import static thump.play.SpecialEffects.Result.*;
import thump.render.Line;
import static thump.render.Line.ML_TWOSIDED;
import thump.render.Sector;
import thump.render.Side;
import static thump.sound.sfx.Sounds.SfxEnum.sfx_pstop;
import static thump.sound.sfx.Sounds.SfxEnum.sfx_stnmov;

/**
 *
 * @author mark
 */
public class Floor implements Thinker {
    public static final int FLOORSPEED = FRACUNIT;
    
    public enum Type {
        // lower floor to highest surrounding floor
        lowerFloor,

        // lower floor to lowest surrounding floor
        lowerFloorToLowest,

        // lower floor to highest surrounding floor VERY FAST
        turboLower,

        // raise floor to lowest surrounding CEILING
        raiseFloor,

        // raise floor to next highest surrounding floor
        raiseFloorToNearest,

        // raise floor to shortest height texture around it
        raiseToTexture,

        // lower floor to lowest surrounding floor
        //  and change floorpic
        lowerAndChange,

        raiseFloor24,
        raiseFloor24AndChange,
        raiseFloorCrush,

         // raise to next highest floor, turbo-speed
        raiseFloorTurbo,       
        donutRaise,
        raiseFloor512
    }    

    //public Thinker thinker;
    public Type    type;
    public boolean crush;
    public Sector  sector;
    public int     direction;
    public int     newspecial;
    public int     texture;
    public int     floordestheight;
    public int     speed;

    private Thinker prevThinker;
    private Thinker nextThinker;
    private ThinkerAction function;

    @Override
    public void setPrevThinker(Thinker thinker) {
        this.prevThinker = thinker;
    }

    @Override
    public Thinker getPrevThinker() {
        return prevThinker;
    }

    @Override
    public void setNextThinker(Thinker thinker) {
        this.nextThinker = thinker;
    }

    @Override
    public Thinker getNextThinker() {
        return nextThinker;
    }

    @Override
    public void setFunction(ThinkerAction function) {
        this.function = function;
    }

    @Override
    public ThinkerAction getFunction() {
        return function;
    }
            //
    // Move a plane (floor or ceiling) and check for crushing
    //
    public static Result T_MovePlane(Sector sector,
            int speed,
            int dest,
            boolean crush,
            int floorOrCeiling,
            int direction) {
        boolean flag;
        int lastpos;

        Map map = Game.getInstance().map;
        
        switch (floorOrCeiling) {
            case 0:
                // FLOOR
                switch (direction) {
                    case -1:
                        // DOWN
                        if (sector.floorheight - speed < dest) {
                            lastpos = sector.floorheight;
                            sector.floorheight = dest;
                            flag = map.P_ChangeSector(sector, crush);
                            if (flag == true) {
                                sector.floorheight = lastpos;
                                map.P_ChangeSector(sector, crush);
                                //return crushed;
                            }
                            return pastdest;
                        } else {
                            lastpos = sector.floorheight;
                            sector.floorheight -= speed;
                            flag = map.P_ChangeSector(sector, crush);
                            if (flag == true) {
                                sector.floorheight = lastpos;
                                map.P_ChangeSector(sector, crush);
                                return crushed;
                            }
                        }
                        break;

                    case 1:
                        // UP
                        if (sector.floorheight + speed > dest) {
                            lastpos = sector.floorheight;
                            sector.floorheight = dest;
                            flag = map.P_ChangeSector(sector, crush);
                            if (flag == true) {
                                sector.floorheight = lastpos;
                                map.P_ChangeSector(sector, crush);
                                //return crushed;
                            }
                            return pastdest;
                        } else {
                            // COULD GET CRUSHED
                            lastpos = sector.floorheight;
                            sector.floorheight += speed;
                            flag = map.P_ChangeSector(sector, crush);
                            if (flag == true) {
                                if (crush == true) {
                                    return crushed;
                                }
                                sector.floorheight = lastpos;
                                map.P_ChangeSector(sector, crush);
                                return crushed;
                            }
                        }
                        break;
                }
                break;

            case 1:
                // CEILING
                switch (direction) {
                    case -1:
                        // DOWN
                        if (sector.ceilingheight - speed < dest) {
                            lastpos = sector.ceilingheight;
                            sector.ceilingheight = dest;
                            flag = map.P_ChangeSector(sector, crush);

                            if (flag == true) {
                                sector.ceilingheight = lastpos;
                                map.P_ChangeSector(sector, crush);
                                //return crushed;
                            }
                            return pastdest;
                        } else {
                            // COULD GET CRUSHED
                            lastpos = sector.ceilingheight;
                            sector.ceilingheight -= speed;
                            flag = map.P_ChangeSector(sector, crush);

                            if (flag == true) {
                                if (crush == true) {
                                    return crushed;
                                }
                                sector.ceilingheight = lastpos;
                                map.P_ChangeSector(sector, crush);
                                return crushed;
                            }
                        }
                        break;

                    case 1:
                        // UP
                        if (sector.ceilingheight + speed > dest) {
                            lastpos = sector.ceilingheight;
                            sector.ceilingheight = dest;
                            flag = map.P_ChangeSector(sector, crush);
                            if (flag == true) {
                                sector.ceilingheight = lastpos;
                                map.P_ChangeSector(sector, crush);
                                //return crushed;
                            }
                            return pastdest;
                        } else {
                            lastpos = sector.ceilingheight;
                            sector.ceilingheight += speed;
                            flag = map.P_ChangeSector(sector, crush);
                            // UNUSED
//    #if 0
//                    if (flag == true)
//                    {
//                        sector.ceilingheight = lastpos;
//                        P_ChangeSector(sector,crush);
//                        return crushed;
//                    }
//    #endif
                        }
                        break;
                }
                break;

        }
        return ok;
    }


    //
    // MOVE A FLOOR TO IT'S DESTINATION (UP OR DOWN)
    //
    public static void T_MoveFloor(Floor floor) {

        Result res = T_MovePlane(floor.sector,
                floor.speed,
                floor.floordestheight,
                floor.crush, 0, floor.direction);

        if (0 == (Game.getInstance().leveltime & 7)) {
            Game.getInstance().sound.S_StartSound(
                    floor.sector.soundorg,
                    sfx_stnmov);
        }

        if (res == pastdest) {
            floor.sector.specialdata = null;

            if (floor.direction == 1) {
                switch (floor.type) {
                    case donutRaise:
                        floor.sector.special = floor.newspecial;
                        floor.sector.setFloorPic(floor.texture);
                        break;
                    default:
                        break;
                }
            } else if (floor.direction == -1) {
                switch (floor.type) {
                    case lowerAndChange:
                        floor.sector.special = floor.newspecial;
                        floor.sector.setFloorPic(floor.texture);
                        break;
                    default:
                        break;
                }
            }
            Tick.P_RemoveThinker(floor);

            Game.getInstance().sound.S_StartSound(
                    floor.sector.soundorg,
                    sfx_pstop);
        }

    }

    //
    // HANDLE FLOOR TYPES
    //
    @SuppressWarnings("fallthrough")
    public static int EV_DoFloor(Line line, Floor.Type floortype) {
        int secnum;
        int rtn;
        int i;
        Sector sec;
        Floor floor;

        secnum = -1;
        rtn = 0;
        SpecialEffects effects = Game.getInstance().playerSetup.effects;
    
        while ((secnum = SpecialEffects.P_FindSectorFromLineTag(line, secnum)) >= 0) {
            sec = Game.getInstance().playerSetup.sectors[secnum];

            // ALREADY MOVING?  IF SO, KEEP GOING...
            if (sec.specialdata!=null) {
                continue;
            }

            // new floor thinker
            rtn = 1;
            //floor = Z_Malloc(sizeof( * floor), PU_LEVSPEC, 0);
            floor = new Floor();
            Tick.P_AddThinker(floor);
            sec.specialdata = floor;
            //floor.thinker.function.acp1 = (actionf_p1) T_MoveFloor;
            floor.setFunction(new T_MoveFloor());
            floor.type = floortype;
            floor.crush = false;

            switch (floortype) {
                case lowerFloor:
                    floor.direction = -1;
                    floor.sector = sec;
                    floor.speed = FLOORSPEED;
                    floor.floordestheight
                            = SpecialEffects.P_FindHighestFloorSurrounding(sec);
                    break;

                case lowerFloorToLowest:
                    floor.direction = -1;
                    floor.sector = sec;
                    floor.speed = FLOORSPEED;
                    floor.floordestheight
                            = SpecialEffects.P_FindLowestFloorSurrounding(sec);
                    break;

                case turboLower:
                    floor.direction = -1;
                    floor.sector = sec;
                    floor.speed = FLOORSPEED * 4;
                    floor.floordestheight
                            = SpecialEffects.P_FindHighestFloorSurrounding(sec);
                    if (floor.floordestheight != sec.floorheight) {
                        floor.floordestheight += 8 * FRACUNIT;
                    }
                    break;

                case raiseFloorCrush:
                    floor.crush = true;
                case raiseFloor:
                    floor.direction = 1;
                    floor.sector = sec;
                    floor.speed = FLOORSPEED;
                    floor.floordestheight
                            = SpecialEffects.P_FindLowestCeilingSurrounding(sec);
                    if (floor.floordestheight > sec.ceilingheight) {
                        floor.floordestheight = sec.ceilingheight;
                    }
                    floor.floordestheight -= (8 * FRACUNIT)
                            * (floortype==raiseFloorCrush?1:0);
                    break;

                case raiseFloorTurbo:
                    floor.direction = 1;
                    floor.sector = sec;
                    floor.speed = FLOORSPEED * 4;
                    floor.floordestheight
                            = SpecialEffects.P_FindNextHighestFloor(sec, sec.floorheight);
                    break;

                case raiseFloorToNearest:
                    floor.direction = 1;
                    floor.sector = sec;
                    floor.speed = FLOORSPEED;
                    floor.floordestheight
                            = SpecialEffects.P_FindNextHighestFloor(sec, sec.floorheight);
                    break;

                case raiseFloor24:
                    floor.direction = 1;
                    floor.sector = sec;
                    floor.speed = FLOORSPEED;
                    floor.floordestheight = floor.sector.floorheight
                            + 24 * FRACUNIT;
                    break;
                case raiseFloor512:
                    floor.direction = 1;
                    floor.sector = sec;
                    floor.speed = FLOORSPEED;
                    floor.floordestheight = floor.sector.floorheight
                            + 512 * FRACUNIT;
                    break;

                case raiseFloor24AndChange:
                    floor.direction = 1;
                    floor.sector = sec;
                    floor.speed = FLOORSPEED;
                    floor.floordestheight = floor.sector.floorheight
                            + 24 * FRACUNIT;
     // shit
                    sec.setFloorPic( line.frontsector.getFloorPic() );
                    sec.special = line.frontsector.special;
                    break;

                case raiseToTexture: {
                    int minsize = Integer.MAX_VALUE;
                    Side side;

                    floor.direction = 1;
                    floor.sector = sec;
                    floor.speed = FLOORSPEED;
                    for (i = 0; i < sec.linecount; i++) {
                        if (SpecialEffects.twoSided(secnum, i)>0) {
                            side = SpecialEffects.getSide(secnum, i, 0);
                            int texHeight = Game.getInstance().wad.getTextures().get(side.getBottomTextureNum()).height;

                            if (side.getBottomTextureNum() >= 0) {
                                if (texHeight < minsize) {
                                    minsize = texHeight;
                                }
                            }
                            side = SpecialEffects.getSide(secnum, i, 1);
                            texHeight = Game.getInstance().wad.getTextures().get(side.getBottomTextureNum()).height;
                            if (side.getBottomTextureNum() >= 0) {
                                if (texHeight < minsize) {
                                    minsize = texHeight;
                                }
                            }
                        }
                    }
                    floor.floordestheight = floor.sector.floorheight + minsize;
                }
                break;

                case lowerAndChange:
                    floor.direction = -1;
                    floor.sector = sec;
                    floor.speed = FLOORSPEED;
                    floor.floordestheight
                            = SpecialEffects.P_FindLowestFloorSurrounding(sec);
                    floor.texture = sec.getFloorPic();

                    PlayerSetup ps = Game.getInstance().playerSetup;
                    
                    for (i = 0; i < sec.linecount; i++) {
                        if (SpecialEffects.twoSided(secnum, i)>0) {
                            if (ps.getSecNum(SpecialEffects.getSide(secnum, i, 0).sector) == secnum) {
                                sec = Game.getInstance().playerSetup.effects.getSector(secnum, i, 1);

                                if (sec.floorheight == floor.floordestheight) {
                                    floor.texture = sec.getFloorPic();
                                    floor.newspecial = sec.special;
                                    break;
                                }
                            } else {
                                sec = Game.getInstance().playerSetup.effects.getSector(secnum, i, 0);

                                if (sec.floorheight == floor.floordestheight) {
                                    floor.texture = sec.getFloorPic();
                                    floor.newspecial = sec.special;
                                    break;
                                }
                            }
                        }
                    }
                default:
                    break;
            }
        }
        return rtn;
    }


    //
    // BUILD A STAIRCASE!
    //
    public static int EV_BuildStairs(Line line, SpecialEffects.Stair type) {
        int			secnum;
        int			height;
        int			i;
        int			newsecnum;
        int			texture;
        boolean			ok;
        int			rtn;

        Sector		sec;
        Sector		tsec;

        Floor	floor;

        int		stairsize=0;
        int		speed=0;

        secnum = -1;
        rtn = 0;
        while ((secnum = SpecialEffects.P_FindSectorFromLineTag(line,secnum)) >= 0) {
            sec = Game.getInstance().playerSetup.sectors[secnum];

            // ALREADY MOVING?  IF SO, KEEP GOING...
            if (null==sec.specialdata) {
                continue;
            }

            // new floor thinker
            rtn = 1;
            //floor = Z_Malloc (sizeof(*floor), PU_LEVSPEC, 0);
            floor = new Floor();
            Tick.P_AddThinker (floor);
            sec.specialdata = floor;
            floor.setFunction( new T_MoveFloor());
            floor.direction = 1;
            floor.sector = sec;
            switch(type) {
              case build8:
                speed = FLOORSPEED/4;
                stairsize = 8*FRACUNIT;
                break;
              case turbo16:
                speed = FLOORSPEED*4;
                stairsize = 16*FRACUNIT;
                break;
            }
            floor.speed = speed;
            height = sec.floorheight + stairsize;
            floor.floordestheight = height;

            texture = sec.getFloorPic();

            // Find next sector to raise
            // 1.	Find 2-sided line with same sector side[0]
            // 2.	Other side is the next sector to raise
            do {
                ok = false;
                for (i = 0;i < sec.linecount;i++) {
                    if ( 0==((sec.lines[i]).flags & ML_TWOSIDED) ) {
                        continue;
                    }

                    tsec = (sec.lines[i]).frontsector;
                    newsecnum = Game.getInstance().playerSetup.getSecNum(tsec);
                    //newsecnum = tsec-sectors;
                    

                    if (secnum != newsecnum) {
                        continue;
                    }
//                    if ( sec != tsec ) {
//                        continue;
//                    }

                    tsec = (sec.lines[i]).backsector;
                    //newsecnum = tsec - sectors;

                    if (tsec.getFloorPic() != texture) {
                        continue;
                    }

                    height += stairsize;

                    if (tsec.specialdata!=null) {
                        continue;
                    }

                    sec = tsec;
                    secnum = newsecnum;
                    
                    //floor = Z_Malloc (sizeof(*floor), PU_LEVSPEC, 0);
                    floor = new Floor();
                    
                    Tick.P_AddThinker (floor);

                    sec.specialdata = floor;
                    floor.setFunction( new T_MoveFloor());
                    floor.direction = 1;
                    floor.sector = sec;
                    floor.speed = speed;
                    floor.floordestheight = height;
                    ok = true;
                    break;
                }
            } while(ok);
        }
        return rtn;
    }


}
