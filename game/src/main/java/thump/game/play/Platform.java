/*
 * Platform Thing
 */
package thump.game.play;

import static thump.base.Defines.logger;
import static thump.base.FixedPoint.FRACUNIT;
import thump.game.Game;
import thump.game.PlayerSetup;
import thump.game.maplevel.MapSector;
import thump.game.thinkeraction.T_PlatRaise;
import static thump.game.play.Platform.Status.down;
import static thump.game.play.Platform.Status.in_stasis;
import static thump.game.play.Platform.Status.up;
import static thump.game.play.Platform.Status.waiting;
import static thump.game.play.Platform.Type.raiseAndChange;
import static thump.game.play.Platform.Type.raiseToNearestAndChange;
import static thump.game.play.SpecialEffects.Result.*;
import thump.wad.map.Line;
import thump.wad.map.Sector;
import thump.game.sound.Sound;
import static thump.game.sound.sfx.Sounds.SfxEnum.sfx_pstart;
import static thump.game.sound.sfx.Sounds.SfxEnum.sfx_pstop;
import static thump.game.sound.sfx.Sounds.SfxEnum.sfx_stnmov;
import thump.wad.map.Thinker;
import thump.wad.map.ThinkerAction;

/**
 *
 * @author mark
 */
public class Platform implements Thinker {
    
    public static final int PLATWAIT = 3;
    public static final int PLATSPEED = FRACUNIT;
    public static final int MAXPLATS = 30;

    //Thinker thinker;
    Sector  sector;
    int     speed;
    int     low;
    int     high;
    int     wait;
    int     count;
    Status  status;
    Status  oldstatus;
    boolean crush;
    int     tag;
    Type    type;

    public enum Status {
        up,
        down,
        waiting,
        in_stasis
    }

    public enum Type {
        perpetualRaise,
        downWaitUpStay,
        raiseAndChange,
        raiseToNearestAndChange,
        blazeDWUS
    }
    
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
    // Move a plat up and down
    //
    public static void T_PlatRaise(Platform plat) {
        SpecialEffects.Result res;

        Sound sound = Game.getInstance().sound;

        switch (plat.status) {
            case up:
                res = Floor.T_MovePlane(plat.sector,
                        plat.speed,
                        plat.high,
                        plat.crush, 0, 1);

                if (plat.type == raiseAndChange
                        || plat.type == raiseToNearestAndChange) {
                    if (0 == (Game.getInstance().leveltime & 7)) {
                        sound.S_StartSound(plat.sector.soundorg,
                                sfx_stnmov);
                    }
                }

                if (res == crushed && (!plat.crush)) {
                    plat.count = plat.wait;
                    plat.status = down;
                    sound.S_StartSound(plat.sector.soundorg,
                            sfx_pstart);
                } else if (res == pastdest) {
                    plat.count = plat.wait;
                    plat.status = waiting;
                    sound.S_StartSound(plat.sector.soundorg,
                            sfx_pstop);

                    switch (plat.type) {
                        case blazeDWUS:
                        case downWaitUpStay:
                            P_RemoveActivePlat(plat);
                            break;

                        case raiseAndChange:
                        case raiseToNearestAndChange:
                            P_RemoveActivePlat(plat);
                            break;

                        default:
                            break;
                    }
                }
                break;

            case down:
                res = Floor.T_MovePlane(plat.sector, plat.speed, plat.low, false, 0, -1);

                if (res == pastdest) {
                    plat.count = plat.wait;
                    plat.status = waiting;
                    sound.S_StartSound(plat.sector.soundorg, sfx_pstop);
                }
                break;

            case waiting:
                plat.count--;
                if (0 == plat.count) {
                    if (plat.sector.floorheight == plat.low) {
                        plat.status = up;
                    } else {
                        plat.status = down;
                    }
                    sound.S_StartSound(plat.sector.soundorg, sfx_pstart);
                }
                break;
            case in_stasis:
                break;
        }
    }


    //
    // Do Platforms
    //  "amount" is only used for SOME platforms.
    //
    public static int EV_DoPlat(Line line, Platform.Type type, int amount) {
        Platform plat;
        int secnum;
        int rtn;
        MapSector sec;

        secnum = -1;
        rtn = 0;

        //	Activate all <type> plats that are in_stasis
        switch (type) {
            case perpetualRaise:
                P_ActivateInStasis(line.tag);
                break;

            default:
                break;
        }

        PlayerSetup ps = Game.getInstance().playerSetup;
        Sound sound = Game.getInstance().sound;
        //SpecialEffects effects = Game.getInstance().playerSetup.effects;

        while ((secnum = SpecialEffects.P_FindSectorFromLineTag(line, secnum)) >= 0) {
            //sec = ps.sectors[secnum];
            sec = ps.sectors.get(secnum);

            if (sec.specialdata != null) {
                continue;
            }

            // Find lowest & highest floors around sector
            rtn = 1;
            //plat = Z_Malloc( sizeof(*plat), PU_LEVSPEC, 0);
            plat = new Platform();
            Tick.P_AddThinker(plat);

            plat.type = type;
            plat.sector = sec;
            plat.sector.specialdata = plat;
            plat.setFunction( new T_PlatRaise() );
            plat.crush = false;
            plat.tag = line.tag;

            switch (type) {
                case raiseToNearestAndChange:
                    plat.speed = PLATSPEED / 2;
                    //sec.setFloorPic( ps.sides[line.sidenum[0]].sector.getFloorPic() );
                    sec.setFloorPic( ps.sides.get(line.sidenum[0]).sector.getFloorPic(Game.getInstance().wad) );
                    plat.high = SpecialEffects.P_FindNextHighestFloor(sec, sec.floorheight);
                    plat.wait = 0;
                    plat.status = up;
                    // NO MORE DAMAGE, IF APPLICABLE
                    sec.special = 0;

                    sound.S_StartSound(sec.soundorg, sfx_stnmov);
                    break;

                case raiseAndChange:
                    plat.speed = PLATSPEED / 2;
                    //sec.setFloorPic( ps.sides[line.sidenum[0]].sector.getFloorPic() );
                    sec.setFloorPic( ps.sides.get(line.sidenum[0]).sector.getFloorPic(Game.getInstance().wad) );
                    plat.high = sec.floorheight + amount * FRACUNIT;
                    plat.wait = 0;
                    plat.status = up;

                    sound.S_StartSound(sec.soundorg, sfx_stnmov);
                    break;

                case downWaitUpStay:
                    plat.speed = PLATSPEED * 4;
                    plat.low = SpecialEffects.P_FindLowestFloorSurrounding(sec);

                    if (plat.low > sec.floorheight) {
                        plat.low = sec.floorheight;
                    }

                    plat.high = sec.floorheight;
                    plat.wait = 35 * PLATWAIT;
                    plat.status = down;
                    sound.S_StartSound(sec.soundorg, sfx_pstart);
                    break;

                case blazeDWUS:
                    plat.speed = PLATSPEED * 8;
                    plat.low = SpecialEffects.P_FindLowestFloorSurrounding(sec);

                    if (plat.low > sec.floorheight) {
                        plat.low = sec.floorheight;
                    }

                    plat.high = sec.floorheight;
                    plat.wait = 35 * PLATWAIT;
                    plat.status = down;
                    sound.S_StartSound(sec.soundorg, sfx_pstart);
                    break;

                case perpetualRaise:
                    plat.speed = PLATSPEED;
                    plat.low = SpecialEffects.P_FindLowestFloorSurrounding(sec);

                    if (plat.low > sec.floorheight) {
                        plat.low = sec.floorheight;
                    }

                    plat.high = SpecialEffects.P_FindHighestFloorSurrounding(sec);

                    if (plat.high < sec.floorheight) {
                        plat.high = sec.floorheight;
                    }

                    plat.wait = 35 * PLATWAIT;
                    plat.status = Status.values()[Random.getInstance().P_Random() & 1];

                    sound.S_StartSound(sec.soundorg, sfx_pstart);
                    break;
            }
            P_AddActivePlat(plat);
        }
        return rtn;
    }



    public static void P_ActivateInStasis(int tag) {
        Platform activeplats[] = Game.getInstance().playerSetup.effects.activeplats;

        for (int i = 0;i < MAXPLATS;i++) {
            if (activeplats[i]!=null
                    && (activeplats[i]).tag == tag
                    && (activeplats[i]).status == in_stasis)
            {
                (activeplats[i]).status = (activeplats[i]).oldstatus;
                (activeplats[i]).setFunction( new T_PlatRaise() );
            }
        }
    }

    public static void EV_StopPlat(Line line) {
        Platform activeplats[] = Game.getInstance().playerSetup.effects.activeplats;

        for (int j = 0;j < MAXPLATS;j++) {
            if (activeplats[j]!=null
                    && ((activeplats[j]).status != in_stasis)
                    && ((activeplats[j]).tag == line.tag))
            {
                (activeplats[j]).oldstatus = (activeplats[j]).status;
                (activeplats[j]).status = in_stasis;
                (activeplats[j]).setFunction(null);
            }
        }
    }

    public static void P_AddActivePlat(Platform plat) {
        Platform activeplats[] = Game.getInstance().playerSetup.effects.activeplats;

        for (int i = 0;i < MAXPLATS;i++) {
            if (activeplats[i] == null) {
                activeplats[i] = plat;
                return;
            }
        }
        logger.severe("P_AddActivePlat: no more plats!\n");
    }

    public static void P_RemoveActivePlat(Platform plat) {
        Platform activeplats[] = Game.getInstance().playerSetup.effects.activeplats;
        for (int i = 0;i < MAXPLATS;i++) {
            if (plat == activeplats[i]) {
                (activeplats[i]).sector.specialdata = null;
                Tick.P_RemoveThinker(activeplats[i]);
                activeplats[i] = null;

                return;
            }
        }
        logger.severe("P_RemoveActivePlat: can't find plat!\n");
    }

}
