/*
 * Vertical Lift Door
 */
package thump.play;

import thump.game.Game;
import thump.game.Player;
import thump.game.Thinker;
import thump.game.ThinkerAction;
import thump.game.thinkeraction.T_VerticalDoor;
import static thump.global.Defines.Card.*;
import static thump.global.FixedPoint.FRACUNIT;
import thump.maplevel.MapObject;
import thump.play.SpecialEffects.Result;
import static thump.play.SpecialEffects.Result.crushed;
import static thump.play.SpecialEffects.Result.pastdest;
import static thump.play.VDoor.Type.*;
import thump.render.Line;
import thump.render.Sector;
import static thump.sound.sfx.Sounds.SfxEnum.*;

/**
 *
 * @author mark
 */
public class VDoor implements Thinker {
    public static final int VDOORSPEED  = FRACUNIT * 2;
    public static final int VDOORWAIT   = 150;

    public static enum Type {
        normal,
        close30ThenOpen,
        close,
        open,
        raiseIn5Mins,
        blazeRaise,
        blazeOpen,
        blazeClose
    }

    //Thinker thinker;
    Type    type;
    Sector  sector;
    int     topheight;
    int     speed;

    // 1 = up, 0 = waiting at top, -1 = down
    int     direction;

    // tics to wait at the top
    int     topwait;
    // (keep in case a door going down is reset)
    // when it reaches 0, start going down
    int     topcountdown;
    
    
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
    // VERTICAL DOORS
    //

    //
    // T_VerticalDoor
    //
    public static void T_VerticalDoor (VDoor door) {
        Result	res;

        switch(door.direction) {
          case 0:
            // WAITING
            door.topcountdown--;
            if (0==door.topcountdown) {
                switch(door.type)
                {
                  case blazeRaise:
                    door.direction = -1; // time to go back down
                    Game.getInstance().sound.S_StartSound(door.sector.soundorg,
                                 sfx_bdcls);
                    break;

                  case normal:
                    door.direction = -1; // time to go back down
                    Game.getInstance().sound.S_StartSound(door.sector.soundorg,
                                 sfx_dorcls);
                    break;

                  case close30ThenOpen:
                    door.direction = 1;
                    Game.getInstance().sound.S_StartSound(door.sector.soundorg,
                                 sfx_doropn);
                    break;

                  default:
                    break;
                }
            }
            break;

          case 2:
            //  INITIAL WAIT
            door.topcountdown--;
            if (door.topcountdown==0) {
                switch(door.type) {
                  case raiseIn5Mins:
                    door.direction = 1;
                    door.type = normal;
                    Game.getInstance().sound.S_StartSound(door.sector.soundorg,
                                 sfx_doropn);
                    break;

                  default:
                    break;
                }
            }
            break;

          case -1:
            // DOWN
            res = Floor.T_MovePlane(door.sector,
                              door.speed,
                              door.sector.floorheight,
                              false,1,door.direction);
            if (res == pastdest) {
                switch(door.type) {
                  case blazeRaise:
                  case blazeClose:
                    door.sector.specialdata = null;
                    Tick.P_RemoveThinker (door);  // unlink and free
                    Game.getInstance().sound.S_StartSound(door.sector.soundorg,
                                 sfx_bdcls);
                    break;

                  case normal:
                  case close:
                    door.sector.specialdata = null;
                    Tick.P_RemoveThinker (door);  // unlink and free
                    break;

                  case close30ThenOpen:
                    door.direction = 0;
                    door.topcountdown = 35*30;
                    break;

                  default:
                    break;
                }
            }
            else if (res == crushed) {
                switch(door.type)
                {
                  case blazeClose:
                  case close:		// DO NOT GO BACK UP!
                    break;

                  default:
                    door.direction = 1;
                    Game.getInstance().sound.S_StartSound(door.sector.soundorg,
                                 sfx_doropn);
                    break;
                }
            }
            break;

          case 1:
            // UP
            res = Floor.T_MovePlane(door.sector,
                              door.speed,
                              door.topheight,
                              false,1,door.direction);

            if (res == pastdest) {
                switch(door.type)
                {
                  case blazeRaise:
                  case normal:
                    door.direction = 0; // wait at top
                    door.topcountdown = door.topwait;
                    break;

                  case close30ThenOpen:
                  case blazeOpen:
                  case open:
                    door.sector.specialdata = null;
                    Tick.P_RemoveThinker (door);  // unlink and free
                    break;

                  default:
                    break;
                }
            }
            break;
        }
    }


    //
    // EV_DoLockedDoor
    // Move a locked door up/down
    //

    public static int EV_DoLockedDoor
    ( Line	line,
      VDoor.Type	type,
      MapObject	thing )
    {
        Player p = thing.player;

        if (p==null) {
            return 0;
        }

        switch (line.special) {
            case 99:	// Blue Lock
            case 133:
                if (!p.cards[it_bluecard.ordinal()] && !p.cards[it_blueskull.ordinal()]) {
                    p.message = Game.getMessage("PD_BLUEO");
                    Game.getInstance().sound.S_StartSound(null, sfx_oof);
                    return 0;
                }
                break;

            case 134: // Red Lock
            case 135:
                if (!p.cards[it_redcard.ordinal()] && !p.cards[it_redskull.ordinal()]) {
                    p.message = Game.getMessage("PD_REDO");
                    Game.getInstance().sound.S_StartSound(null, sfx_oof);
                    return 0;
                }
                break;

            case 136:	// Yellow Lock
            case 137:
                if (!p.cards[it_yellowcard.ordinal()]
                        && !p.cards[it_yellowskull.ordinal()]) {
                    p.message = Game.getMessage("PD_YELLOWO");
                    Game.getInstance().sound.S_StartSound(null, sfx_oof);
                    return 0;
                }
                break;
        }

        return EV_DoDoor(line,type);
    }


    public static int EV_DoDoor(Line line, VDoor.Type type) {
        int		secnum,rtn;
        Sector	sec;
        VDoor	door;

        secnum = -1;
        rtn = 0;

        while ((secnum = SpecialEffects.P_FindSectorFromLineTag(line,secnum)) >= 0)
        {
            sec = Game.getInstance().playerSetup.sectors[secnum];
            if (null!=sec.specialdata) {
                continue;
            }


            // new door thinker
            rtn = 1;
            //door = Z_Malloc (sizeof(*door), PU_LEVSPEC, 0);
            door = new VDoor();
            Tick.P_AddThinker (door);
            sec.specialdata = door;

            door.setFunction( new T_VerticalDoor() );
            door.sector = sec;
            door.type = type;
            door.topwait = VDOORWAIT;
            door.speed = VDOORSPEED;

            switch(type) {
              case blazeClose:
                door.topheight = SpecialEffects.P_FindLowestCeilingSurrounding(sec);
                door.topheight -= 4*FRACUNIT;
                door.direction = -1;
                door.speed = VDOORSPEED * 4;
                Game.getInstance().sound.S_StartSound(door.sector.soundorg,
                             sfx_bdcls);
                break;

              case close:
                door.topheight = SpecialEffects.P_FindLowestCeilingSurrounding(sec);
                door.topheight -= 4*FRACUNIT;
                door.direction = -1;
                Game.getInstance().sound.S_StartSound(door.sector.soundorg,
                             sfx_dorcls);
                break;

              case close30ThenOpen:
                door.topheight = sec.ceilingheight;
                door.direction = -1;
                Game.getInstance().sound.S_StartSound(door.sector.soundorg,
                             sfx_dorcls);
                break;

              case blazeRaise:
              case blazeOpen:
                door.direction = 1;
                door.topheight = SpecialEffects.P_FindLowestCeilingSurrounding(sec);
                door.topheight -= 4*FRACUNIT;
                door.speed = VDOORSPEED * 4;
                if (door.topheight != sec.ceilingheight) {
                    Game.getInstance().sound.S_StartSound(door.sector.soundorg,
                            sfx_bdopn);
                }
                break;

              case normal:
              case open:
                door.direction = 1;
                door.topheight = SpecialEffects.P_FindLowestCeilingSurrounding(sec);
                door.topheight -= 4*FRACUNIT;
                if (door.topheight != sec.ceilingheight) {
                    Game.getInstance().sound.S_StartSound(door.sector.soundorg,
                            sfx_doropn);
                }
                break;

              default:
                break;
            }

        }
        return rtn;
    }


    //
    // EV_VerticalDoor : open a door manually, no tag value
    //
    public static void EV_VerticalDoor(Line line, MapObject thing) {
        int		secnum;
        Sector	sec;
        //VDoor	door;
        int		side;

        side = 0;	// only front sides can be used

        //	Check for locks
        Player player = thing.player;

        switch(line.special) {
          case 26: // Blue Lock
          case 32:
            if ( null==player ) {
                return;
            }

            if (!player.cards[it_bluecard.ordinal()] && !player.cards[it_blueskull.ordinal()])
            {
                player.message = Game.getMessage("PD_BLUEK");
                Game.getInstance().sound.S_StartSound(null,sfx_oof);
                return;
            }
            break;

          case 27: // Yellow Lock
          case 34:
            if ( null==player ) {
                return;
            }

            if (!player.cards[it_yellowcard.ordinal()] &&
                !player.cards[it_yellowskull.ordinal()])
            {
                player.message = Game.getMessage("PD_YELLOWK");
                Game.getInstance().sound.S_StartSound(null,sfx_oof);
                return;
            }
            break;

          case 28: // Red Lock
          case 33:
            if ( null==player ) {
                return;
            }

            if (!player.cards[it_redcard.ordinal()] && !player.cards[it_redskull.ordinal()]) {
                player.message = Game.getMessage("PD_REDK");
                Game.getInstance().sound.S_StartSound(null,sfx_oof);
                return;
            }
            break;
        }

        // if the sector has an active thinker, use it
        sec = Game.getInstance().playerSetup.sides[ line.sidenum[side^1]].getSector(Game.getInstance().playerSetup.map);
        //secnum = sec-sectors;

        if (sec.specialdata!=null) {
            VDoor door = (VDoor) sec.specialdata;
            switch(line.special)
            {
              case	1: // ONLY FOR "RAISE" DOORS, NOT "OPEN"s
              case	26:
              case	27:
              case	28:
              case	117:
                if (door.direction == -1) {
                    door.direction = 1;	// go back up
                } else {
                    if (null==thing.player) {
                        return;		// JDC: bad guys never close doors
                    }

                    door.direction = -1;	// start going down immediately
                }
                return;
            }
        }

        // for proper sound
        switch(line.special) {
          case 117:	// BLAZING DOOR RAISE
          case 118:	// BLAZING DOOR OPEN
            Game.getInstance().sound.S_StartSound(sec.soundorg,sfx_bdopn);
            break;

          case 1:	// NORMAL DOOR SOUND
          case 31:
            Game.getInstance().sound.S_StartSound(sec.soundorg,sfx_doropn);
            break;

          default:	// LOCKED DOOR SOUND
            Game.getInstance().sound.S_StartSound(sec.soundorg,sfx_doropn);
            break;
        }


        // new door thinker
        //door = Z_Malloc (sizeof(*door), PU_LEVSPEC, 0);
        VDoor door = new VDoor();
        Tick.P_AddThinker (door);
        sec.specialdata = door;
        door.setFunction( new T_VerticalDoor());
        door.sector = sec;
        door.direction = 1;
        door.speed = VDOORSPEED;
        door.topwait = VDOORWAIT;

        switch(line.special)
        {
          case 1:
          case 26:
          case 27:
          case 28:
            door.type = normal;
            break;

          case 31:
          case 32:
          case 33:
          case 34:
            door.type = open;
            line.special = 0;
            break;

          case 117:	// blazing door raise
            door.type = blazeRaise;
            door.speed = VDOORSPEED*4;
            break;
          case 118:	// blazing door open
            door.type = blazeOpen;
            line.special = 0;
            door.speed = VDOORSPEED*4;
            break;
        }

        // find the top and bottom of the movement range
        door.topheight = SpecialEffects.P_FindLowestCeilingSurrounding(sec);
        door.topheight -= 4*FRACUNIT;
    }


    //
    // Spawn a door that closes after 30 seconds
    //
    public static void P_SpawnDoorCloseIn30 (Sector sec) {
        //door = Z_Malloc ( sizeof(*door), PU_LEVSPEC, 0);
        VDoor door = new VDoor();
        Tick.P_AddThinker (door);

        sec.specialdata = door;
        sec.special = 0;

        door.setFunction( new T_VerticalDoor() );
        door.sector = sec;
        door.direction = 0;
        door.type = normal;
        door.speed = VDOORSPEED;
        door.topcountdown = 30 * 35;
    }

    //
    // Spawn a door that opens after 5 minutes
    //
    public static void P_SpawnDoorRaiseIn5Mins(Sector sec, int secnum) {
        VDoor	door = new VDoor();

        //door = Z_Malloc ( sizeof(*door), PU_LEVSPEC, 0);

        Tick.P_AddThinker (door);

        sec.specialdata = door;
        sec.special = 0;

        door.setFunction( new T_VerticalDoor() );
        door.sector = sec;
        door.direction = 2;
        door.type = raiseIn5Mins;
        door.speed = VDOORSPEED;
        door.topheight = SpecialEffects.P_FindLowestCeilingSurrounding(sec);
        door.topheight -= 4*FRACUNIT;
        door.topwait = VDOORWAIT;
        door.topcountdown = 5 * 60 * 35;
    }

    
}
