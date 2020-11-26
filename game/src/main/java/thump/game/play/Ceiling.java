/*
 * Moving Ceiling
 */
package thump.game.play;

import static thump.base.FixedPoint.FRACUNIT;
import thump.game.Game;
import static thump.game.play.Ceiling.Type.lowerToFloor;
import thump.game.thinkeraction.T_MoveCeiling;
import static thump.game.play.SpecialEffects.Result.crushed;
import static thump.game.play.SpecialEffects.Result.pastdest;
import static thump.game.sound.sfx.Sounds.SfxEnum.sfx_pstop;
import static thump.game.sound.sfx.Sounds.SfxEnum.sfx_stnmov;
import thump.wad.map.Line;
import thump.wad.map.Sector;
import thump.wad.map.Thinker;
import thump.wad.map.ThinkerAction;

/**
 *
 * @author mark
 */
public class Ceiling implements Thinker {
    
    public static final int CEILSPEED   = FRACUNIT;
    public static final int CEILWAIT    = 150;
    public static final int MAXCEILINGS = 30;

    public enum Type {
        lowerToFloor,
        raiseToHighest,
        lowerAndCrush,
        crushAndRaise,
        fastCrushAndRaise,
        silentCrushAndRaise
    }

    //Thinker thinker;
    Type    type;
    Sector  sector;
    int     bottomheight;
    int     topheight;
    int     speed;
    boolean crush;

    // 1 = up, 0 = waiting, -1 = down
    int     direction;

    // ID
    int     tag;
    int     olddirection;

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
    // TA_MoveCeiling
    //

    public static void TA_MoveCeiling (Ceiling ceiling) {
        SpecialEffects.Result	res;

        switch(ceiling.direction) {
          case 0:
            // IN STASIS
            break;
          case 1:
            // UP
            res = Floor.T_MovePlane(ceiling.sector,
                              ceiling.speed,
                              ceiling.topheight,
                              false,1,ceiling.direction);

            if (0==(Game.getInstance().leveltime&7)) {
                switch(ceiling.type)
                {
                  case silentCrushAndRaise:
                    break;
                  default:
                    Game.getInstance().sound.S_StartSound(ceiling.sector.soundorg,
                                 sfx_stnmov);
                    // ?
                    break;
                }
            }

            if (res == pastdest)
            {
                switch(ceiling.type)
                {
                  case raiseToHighest:
                    P_RemoveActiveCeiling(ceiling);
                    break;

                  case silentCrushAndRaise:
                    Game.getInstance().sound.S_StartSound(ceiling.sector.soundorg,
                                 sfx_pstop);
                  case fastCrushAndRaise:
                  case crushAndRaise:
                    ceiling.direction = -1;
                    break;

                  default:
                    break;
                }

            }
            break;

          case -1:
            // DOWN
            res = Floor.T_MovePlane(ceiling.sector,
                              ceiling.speed,
                              ceiling.bottomheight,
                              ceiling.crush,1,ceiling.direction);

            if (0==(Game.getInstance().leveltime&7)) {
                switch(ceiling.type)
                {
                  case silentCrushAndRaise: break;
                  default:
                    Game.getInstance().sound.S_StartSound(ceiling.sector.soundorg,
                                 sfx_stnmov);
                }
            }

            if (res == pastdest) {
                switch(ceiling.type) {
                  case silentCrushAndRaise:
                    Game.getInstance().sound.S_StartSound(ceiling.sector.soundorg,
                                 sfx_pstop);
                  case crushAndRaise:
                    ceiling.speed = CEILSPEED;
                  case fastCrushAndRaise:
                    ceiling.direction = 1;
                    break;

                  case lowerAndCrush:
                  case lowerToFloor:
                    P_RemoveActiveCeiling(ceiling);
                    break;

                  default:
                    break;
                }
            }  else {  // ( res != pastdest ) 
                if (res == crushed) {
                    switch(ceiling.type)  {
                      case silentCrushAndRaise:
                      case crushAndRaise:
                      case lowerAndCrush:
                        ceiling.speed = CEILSPEED / 8;
                        break;

                      default:
                        break;
                    }
                }
            }
            break;
        }
    }


    //
    // EV_DoCeiling
    // Move a ceiling up/down and all around!
    //
    public static int EV_DoCeiling(Line line, Ceiling.Type type) {
        int		secnum;
        int		rtn;
        Sector	sec;
        Ceiling	ceiling;

        secnum = -1;
        rtn = 0;

        //	Reactivate in-stasis ceilings...for certain types.
        switch(type) {
          case fastCrushAndRaise:
          case silentCrushAndRaise:
          case crushAndRaise:
            P_ActivateInStasisCeiling(line);
            break;
          default:
            break;
        }

        while ((secnum = SpecialEffects.P_FindSectorFromLineTag(line,secnum)) >= 0) {
            //sec = Game.getInstance().playerSetup.sectors[secnum];
            sec = Game.getInstance().playerSetup.sectors.get(secnum);
            if (sec.specialdata!=null) {
                continue;
            }

            // new door thinker
            rtn = 1;
            //ceiling = Z_Malloc (sizeof(*ceiling), PU_LEVSPEC, 0);
            ceiling = new Ceiling();
            Tick.P_AddThinker (ceiling);
            sec.specialdata = ceiling;
            ceiling.setFunction(new T_MoveCeiling());
            ceiling.sector = sec;
            ceiling.crush = false;

            switch(type) {
              case fastCrushAndRaise:
                ceiling.crush = true;
                ceiling.topheight = sec.ceilingheight;
                ceiling.bottomheight = sec.floorheight + (8*FRACUNIT);
                ceiling.direction = -1;
                ceiling.speed = CEILSPEED * 2;
                break;

              case silentCrushAndRaise:
              case crushAndRaise:
                ceiling.crush = true;
                ceiling.topheight = sec.ceilingheight;
              case lowerAndCrush:
              case lowerToFloor:
                ceiling.bottomheight = sec.floorheight;
                if (type != lowerToFloor)
                    ceiling.bottomheight += 8*FRACUNIT;
                ceiling.direction = -1;
                ceiling.speed = CEILSPEED;
                break;

              case raiseToHighest:
                ceiling.topheight = SpecialEffects.P_FindHighestCeilingSurrounding(sec);
                ceiling.direction = 1;
                ceiling.speed = CEILSPEED;
                break;
            }

            ceiling.tag = sec.tag;
            ceiling.type = type;
            P_AddActiveCeiling(ceiling);
        }
        return rtn;
    }


    //
    // Add an active ceiling
    //
    public static void P_AddActiveCeiling(Ceiling c) {

        Ceiling[] activeceilings = Game.getInstance().playerSetup.effects.activeceilings;

        for (int i = 0; i < MAXCEILINGS;i++) {
            if (activeceilings[i] == null) {
                activeceilings[i] = c;
                return;
            }
        }
    }



    //
    // Remove a ceiling's thinker
    //
    public static void P_RemoveActiveCeiling(Ceiling c) {
        Ceiling[] activeceilings = Game.getInstance().playerSetup.effects.activeceilings;

        for (int i = 0;i < MAXCEILINGS;i++) {
            if (activeceilings[i] == c) {
                activeceilings[i].sector.specialdata = null;
                Tick.P_RemoveThinker (activeceilings[i]);
                activeceilings[i] = null;
                break;
            }
        }
    }



    //
    // Restart a ceiling that's in-stasis
    //
    public static void P_ActivateInStasisCeiling(Line line) {
        Ceiling[] activeceilings = Game.getInstance().playerSetup.effects.activeceilings;

        for (int i = 0;i < MAXCEILINGS;i++)
        {
            if (activeceilings[i] != null
                && (activeceilings[i].tag == line.tag)
                && (activeceilings[i].direction == 0))
            {
                activeceilings[i].direction = activeceilings[i].olddirection;
                activeceilings[i].setFunction(new T_MoveCeiling());
            }
        }
    }



    //
    // EV_CeilingCrushStop
    // Stop a ceiling from crushing!
    //
    public static int EV_CeilingCrushStop(Line line) {
        Ceiling[] activeceilings = Game.getInstance().playerSetup.effects.activeceilings;
        int rtn = 0;

        for (int i = 0;i < MAXCEILINGS;i++) {
            if (activeceilings[i] != null
                && (activeceilings[i].tag == line.tag)
                && (activeceilings[i].direction != 0))
            {
                activeceilings[i].olddirection = activeceilings[i].direction;
                activeceilings[i].setFunction( null);
                activeceilings[i].direction = 0;		// in-stasis
                rtn = 1;
            }
        }


        return rtn;
    }
}
