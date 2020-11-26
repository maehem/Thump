/*
 *  Implements special effects:
 *  Texture animation, height or lighting changes
 *  according to adjacent sectors, respective
 *  utility functions, etc.
 *
 */
package thump.game.play;

import java.util.logging.Level;
import static thump.base.Defines.logger;
import static thump.base.FixedPoint.FRACUNIT;
import static thump.game.Defines.PowerType.pw_ironfeet;
import thump.game.Game;
import static thump.game.MobJInfo.Type.MT_BFG;
import static thump.game.MobJInfo.Type.MT_BRUISERSHOT;
import static thump.game.MobJInfo.Type.MT_HEADSHOT;
import static thump.game.MobJInfo.Type.MT_PLASMA;
import static thump.game.MobJInfo.Type.MT_ROCKET;
import static thump.game.MobJInfo.Type.MT_TROOPSHOT;
import thump.game.Player;
import static thump.game.Player.Cheat.CF_GODMODE;
import thump.game.PlayerSetup;
import thump.game.maplevel.MapObject;
import thump.game.maplevel.MapSideDef;
import thump.game.thinkeraction.T_MoveFloor;
import static thump.game.play.Ceiling.MAXCEILINGS;
import static thump.game.play.Ceiling.Type.*;
import static thump.game.play.Floor.Type.*;
import static thump.game.play.SpecialEffects.Stair.build8;
import static thump.game.play.SpecialEffects.Stair.turbo16;
import static thump.game.play.VDoor.Type.*;
import static thump.game.sound.sfx.Sounds.SfxEnum.sfx_swtchn;
import static thump.game.play.Floor.FLOORSPEED;
import static thump.game.play.Platform.MAXPLATS;
import static thump.game.play.Platform.Type.*;
import thump.wad.Wad;
import thump.wad.map.Line;
import static thump.wad.map.Line.ML_TWOSIDED;
import thump.wad.map.Sector;

/**
 *
 * @author mark
 */
public class SpecialEffects {  //  p_spec
    //      Define values for map objects

    public static final int MO_TELEPORTMAN = 14;

    public static final int GLOWSPEED = 8;
    public static final int STROBEBRIGHT = 5;
    public static final int FASTDARK = 15;
    public static final int SLOWDARK = 35;

    public static final int MAXANIMS = 32;
    
    // max # of wall switches in a level
    public static final int MAXSWITCHES = 50;

    // 4 players, 4 buttons each at once, max.
    public static final int MAXBUTTONS = 16;

    // 1 second, in ticks. 
    public static final int BUTTONTIME = 35;

    //
    // P_InitPicAnims
    //
    // Floor/ceiling animation sequences,
    //  defined by first and last frame,
    //  i.e. the flat (64x64 tile) name to
    //  be used.
    // The full animation sequence is given
    //  using all the flats between the start
    //  and end entry, in the order found in
    //  the WAD file.
    //
    AnimDef animdefs[]
            = {
                new AnimDef(false, "NUKAGE3", "NUKAGE1", 8),
                new AnimDef(false, "FWATER4", "FWATER1", 8),
                new AnimDef(false, "SWATER4", "SWATER1", 8),
                new AnimDef(false, "LAVA4", "LAVA1", 8),
                new AnimDef(false, "BLOOD3", "BLOOD1", 8),
                // DOOM II flat animations.
                new AnimDef(false, "RROCK08", "RROCK05", 8),
                new AnimDef(false, "SLIME04", "SLIME01", 8),
                new AnimDef(false, "SLIME08", "SLIME05", 8),
                new AnimDef(false, "SLIME12", "SLIME09", 8),
                new AnimDef(true, "BLODGR4", "BLODGR1", 8),
                new AnimDef(true, "SLADRIP3", "SLADRIP1", 8),
                new AnimDef(true, "BLODRIP4", "BLODRIP1", 8),
                new AnimDef(true, "FIREWALL", "FIREWALA", 8),
                new AnimDef(true, "GSTFONT3", "GSTFONT1", 8),
                new AnimDef(true, "FIRELAVA", "FIRELAV3", 8),
                new AnimDef(true, "FIREMAG3", "FIREMAG1", 8),
                new AnimDef(true, "FIREBLU2", "FIREBLU1", 8),
                new AnimDef(true, "ROCKRED3", "ROCKRED1", 8),
                new AnimDef(true, "BFALL4", "BFALL1", 8),
                new AnimDef(true, "SFALL4", "SFALL1", 8),
                new AnimDef(true, "WFALL4", "WFALL1", 8),
                new AnimDef(true, "DBRAIN4", "DBRAIN1", 8)
            };

    Anim anims[] = new Anim[MAXANIMS];
    private int lastanim = 0;
    
    public final Ceiling activeceilings[] = new Ceiling[MAXCEILINGS];
    public final Platform   activeplats[] = new Platform[MAXPLATS];
    
    private final PlayerSetup playerSetup;

    public SpecialEffects(PlayerSetup playerSetup) {
        this.playerSetup = playerSetup;
    }

    
    public enum Result {
        ok,
        crushed,
        pastdest
    }
    
    public enum Stair{
        build8,	// slowly build by 8
        turbo16	// quickly build by 16   
    }
    
    //
    //      Animating line specials
    //
    public static final int MAXLINEANIMS = 64;

    //	Init animation
    public void P_InitPicAnims() {
        Wad wad = Game.getInstance().wad;
        lastanim = 0;
        //for (int i=0 ; animdefs[i].istexture != -1 ; i++) {
        for (AnimDef animdef : animdefs) {
            anims[lastanim] = new Anim();
            if (animdef.istexture) {
                // different episode ?
                if (wad.R_CheckTextureNumForName(animdef.startname) == -1) {
                    continue;
                }
                anims[lastanim].picnum = wad.R_TextureNumForName(animdef.endname);
                anims[lastanim].basepic = wad.R_TextureNumForName(animdef.startname);
            } else {
                if (wad.W_CheckNumForName(animdef.startname) == -1) {
                    continue;
                }
                anims[lastanim].picnum  = wad.getFlats().getNumForName(animdef.endname);
                anims[lastanim].basepic = wad.getFlats().getNumForName(animdef.startname);
            }
            anims[lastanim].istexture = animdef.istexture;
            anims[lastanim].numpics = anims[lastanim].picnum - anims[lastanim].basepic + 1;
            if (anims[lastanim].numpics < 2) {
                logger.log(Level.SEVERE, "P_InitPicAnims: bad cycle from {0} to {1}\n", new Object[]{animdef.startname, animdef.endname});
            }
            anims[lastanim].speed = animdef.speed;
            lastanim++;
        }

    }

    //
    // UTILITIES
    //
    //
    // getSide()
    // Will return a side_t*
    //  given the number of the current sector,
    //  the line number, and the side (0/1) that you want.
    //
    //public static Side getSide(int currentSector, int line, int side) {
    public static MapSideDef getSide(int currentSector, int line, int side) {
        PlayerSetup ps = Game.getInstance().playerSetup;

        //return ps.sides[(ps.sectors[currentSector].lines[line]).sidenum[side]];
        return ps.sides.get(
                (ps.sectors.get(currentSector).lines[line]).sidenum[side]
        );
    }

    //
    // getSector()
    // Will return a Sector
    //  given the number of the current sector,
    //  the line number and the side (0/1) that you want.
    //
    Sector getSector(int currentSector, int line, int side) {
        PlayerSetup ps = Game.getInstance().playerSetup;

        return getSide(currentSector, line, side).getSector(ps.map);
    }

    //
    // twoSided()
    // Given the sector number and the line number,
    //  it will tell you whether the line is two-sided or not.
    //
    public static int twoSided(int sector, int line) {  // TODO make this a boolean.
        PlayerSetup ps = Game.getInstance().playerSetup;

        //return (ps.sectors[sector].lines[line]).flags & ML_TWOSIDED;
        return (ps.sectors.get(sector).lines[line]).flags & ML_TWOSIDED;
    }

    //
    // getNextSector()
    // Return Sector of sector next to current.
    // NULL if not two-sided line
    //
    public static Sector getNextSector(Line line, Sector sec) {
        if ((line.flags & ML_TWOSIDED) == 0) {
            return null;
        }

        if (line.frontsector == sec) {
            return line.backsector;
        }

        return line.frontsector;
    }

    //
    // P_FindLowestFloorSurrounding()
    // FIND LOWEST FLOOR HEIGHT IN SURROUNDING SECTORS
    //
    public static int P_FindLowestFloorSurrounding(Sector sec) {
        int i;
        Line check;
        Sector other;
        int floor = sec.floorheight;

        for (i = 0; i < sec.linecount; i++) {
            check = sec.lines[i];
            other = getNextSector(check, sec);

            if (other == null) {
                continue;
            }

            if (other.floorheight < floor) {
                floor = other.floorheight;
            }
        }
        return floor;
    }

    //
    // P_FindHighestFloorSurrounding()
    // FIND HIGHEST FLOOR HEIGHT IN SURROUNDING SECTORS
    //
    public static int P_FindHighestFloorSurrounding(Sector sec) {
        Line check;
        Sector other;
        int floor = -500 * FRACUNIT;

        for (int i = 0; i < sec.linecount; i++) {
            check = sec.lines[i];
            other = getNextSector(check, sec);

            if (other == null) {
                continue;
            }

            if (other.floorheight > floor) {
                floor = other.floorheight;
            }
        }
        return floor;
    }

    //
    // P_FindNextHighestFloor
    // FIND NEXT HIGHEST FLOOR IN SURROUNDING SECTORS
    // Note: this should be doable w/o a fixed array.
    // 20 adjoining sectors max!
    private static final int MAX_ADJOINING_SECTORS = 20;

    public static int P_FindNextHighestFloor(Sector sec, int currentheight) {
        int i;
        int h;
        int min;
        Line check;
        Sector other;
        int height = currentheight;

        int heightlist[] = new int[MAX_ADJOINING_SECTORS];

        for (i = 0, h = 0; i < sec.linecount; i++) {
            check = sec.lines[i];
            other = getNextSector(check, sec);

            if (other == null) {
                continue;
            }

            if (other.floorheight > height) {
                heightlist[h++] = other.floorheight;
            }

            // Check for overflow. Exit.
            if (h >= MAX_ADJOINING_SECTORS) {
                logger.severe( "Sector with more than 20 adjoining sectors\n");
                break;
            }
        }

        // Find lowest height in list
        if (h == 0) {
            return currentheight;
        }

        min = heightlist[0];

        // Range checking? 
        for (i = 1; i < h; i++) {
            if (heightlist[i] < min) {
                min = heightlist[i];
            }
        }

        return min;
    }

    //
    // FIND LOWEST CEILING IN THE SURROUNDING SECTORS
    //
    public static int P_FindLowestCeilingSurrounding(Sector sec) {
        int i;
        Line check;
        Sector other;
        int height = Integer.MAX_VALUE;

        for (i = 0; i < sec.linecount; i++) {
            check = sec.lines[i];
            other = getNextSector(check, sec);

            if (other == null) {
                continue;
            }

            if (other.ceilingheight < height) {
                height = other.ceilingheight;
            }
        }
        return height;
    }

    //
    // FIND HIGHEST CEILING IN THE SURROUNDING SECTORS
    //
    public static int P_FindHighestCeilingSurrounding(Sector sec) {
        int i;
        Line check;
        Sector other;
        int height = 0;

        for (i = 0; i < sec.linecount; i++) {
            check = sec.lines[i];
            other = getNextSector(check, sec);

            if (other == null) {
                continue;
            }

            if (other.ceilingheight > height) {
                height = other.ceilingheight;
            }
        }
        return height;
    }

    //
    // RETURN NEXT SECTOR # THAT LINE TAG REFERS TO
    //
    public static int P_FindSectorFromLineTag(Line line, int start) {

        PlayerSetup ps = Game.getInstance().playerSetup;

        //for (int i = start + 1; i < ps.sectors.length; i++) {
        for (int i = start + 1; i < ps.sectors.size(); i++) {
            //if (ps.sectors[i].tag == line.tag) {
            if (ps.sectors.get(i).tag == line.tag) {
                 return i;
            }
        }

        return -1;
    }

    //
    // Find minimum light from an adjacent sector
    //
    public static int P_FindMinSurroundingLight(Sector sector, int max) {
        Line line;
        Sector check;

        int min = max;
        for (int i = 0; i < sector.linecount; i++) {
            line = sector.lines[i];
            check = getNextSector(line, sector);

            if (check == null) {
                continue;
            }

            if (check.lightlevel < min) {
                min = check.lightlevel;
            }
        }
        return min;
    }

    //
    // EVENTS
    // Events are operations triggered by using, crossing,
    // or shooting special lines, or by timed thinkers.
    //
    //
    // P_CrossSpecialLine - TRIGGER
    // Called every time a thing origin is about
    //  to cross a line with a non 0 special.
    //
    public static void P_CrossSpecialLine(int linenum, int side, MapObject thing) {
        Line line;
        boolean ok;

        line = Game.getInstance().playerSetup.lines[linenum];

        //	Triggers that other things can activate
        if (thing.player == null) {
            // Things that should NOT trigger specials...
            switch (thing.type) {
                case MT_ROCKET:
                case MT_PLASMA:
                case MT_BFG:
                case MT_TROOPSHOT:
                case MT_HEADSHOT:
                case MT_BRUISERSHOT:
                    return;

                default:
                    break;
            }

            ok = false;
            switch (line.special) {
                case 39:	// TELEPORT TRIGGER
                case 97:	// TELEPORT RETRIGGER
                case 125:	// TELEPORT MONSTERONLY TRIGGER
                case 126:	// TELEPORT MONSTERONLY RETRIGGER
                case 4:	// RAISE DOOR
                case 10:	// PLAT DOWN-WAIT-UP-STAY TRIGGER
                case 88:	// PLAT DOWN-WAIT-UP-STAY RETRIGGER
                    ok = true;
                    break;
            }
            if (!ok) {
                return;
            }
        }

        // Note: could use some const's here.
        switch (line.special) {
            // TRIGGERS.
            // All from here to RETRIGGERS.
            case 2:
                // Open Door
                VDoor.EV_DoDoor(line, open);
                line.special = 0;
                break;

            case 3:
                // Close Door
                VDoor.EV_DoDoor(line, close);
                line.special = 0;
                break;

            case 4:
                // Raise Door
                VDoor.EV_DoDoor(line, normal);
                line.special = 0;
                break;

            case 5:
                // Raise Floor
                Floor.EV_DoFloor(line, raiseFloor);
                line.special = 0;
                break;

            case 6:
                // Fast Ceiling Crush & Raise
                Ceiling.EV_DoCeiling(line, fastCrushAndRaise);
                line.special = 0;
                break;

            case 8:
                // Build Stairs
                Floor.EV_BuildStairs(line, build8);
                line.special = 0;
                break;

            case 10:
                // PlatDownWaitUp
                Platform.EV_DoPlat(line, downWaitUpStay, 0);
                line.special = 0;
                break;

            case 12:
                // Light Turn On - brightest near
                Lights.EV_LightTurnOn(line, 0);
                line.special = 0;
                break;

            case 13:
                // Light Turn On 255
                Lights.EV_LightTurnOn(line, 255);
                line.special = 0;
                break;

            case 16:
                // Close Door 30
                VDoor.EV_DoDoor(line, close30ThenOpen);
                line.special = 0;
                break;

            case 17:
                // Start Light Strobing
                Lights.EV_StartLightStrobing(line);
                line.special = 0;
                break;

            case 19:
                // Lower Floor
                Floor.EV_DoFloor(line, lowerFloor);
                line.special = 0;
                break;

            case 22:
                // Raise floor to nearest height and change texture
                Platform.EV_DoPlat(line, raiseToNearestAndChange, 0);
                line.special = 0;
                break;

            case 25:
                // Ceiling Crush and Raise
                Ceiling.EV_DoCeiling(line, crushAndRaise);
                line.special = 0;
                break;

            case 30:
                // Raise floor to shortest texture height
                //  on either side of lines.
                Floor.EV_DoFloor(line, raiseToTexture);
                line.special = 0;
                break;

            case 35:
                // Lights Very Dark
                Lights.EV_LightTurnOn(line, 35);
                line.special = 0;
                break;

            case 36:
                // Lower Floor (TURBO)
                Floor.EV_DoFloor(line, turboLower);
                line.special = 0;
                break;

            case 37:
                // LowerAndChange
                Floor.EV_DoFloor(line, lowerAndChange);
                line.special = 0;
                break;

            case 38:
                // Lower Floor To Lowest
                Floor.EV_DoFloor(line, lowerFloorToLowest);
                line.special = 0;
                break;

            case 39:
                // TELEPORT!
                Teleport.EV_Teleport(line, side, thing);
                line.special = 0;
                break;

            case 40:
                // RaiseCeilingLowerFloor
                Ceiling.EV_DoCeiling(line, raiseToHighest);
                Floor.EV_DoFloor(line, lowerFloorToLowest);
                line.special = 0;
                break;

            case 44:
                // Ceiling Crush
                Ceiling.EV_DoCeiling(line, lowerAndCrush);
                line.special = 0;
                break;

            case 52:
                // EXIT!
                Game.getInstance().G_ExitLevel();
                break;

            case 53:
                // Perpetual Platform Raise
                Platform.EV_DoPlat(line, perpetualRaise, 0);
                line.special = 0;
                break;

            case 54:
                // Platform Stop
                Platform.EV_StopPlat(line);
                line.special = 0;
                break;

            case 56:
                // Raise Floor Crush
                Floor.EV_DoFloor(line, raiseFloorCrush);
                line.special = 0;
                break;

            case 57:
                // Ceiling Crush Stop
                Ceiling.EV_CeilingCrushStop(line);
                line.special = 0;
                break;

            case 58:
                // Raise Floor 24
                Floor.EV_DoFloor(line, raiseFloor24);
                line.special = 0;
                break;

            case 59:
                // Raise Floor 24 And Change
                Floor.EV_DoFloor(line, raiseFloor24AndChange);
                line.special = 0;
                break;

            case 104:
                // Turn lights off in sector(tag)
                Lights.EV_TurnTagLightsOff(line);
                line.special = 0;
                break;

            case 108:
                // Blazing Door Raise (faster than TURBO!)
                VDoor.EV_DoDoor(line, blazeRaise);
                line.special = 0;
                break;

            case 109:
                // Blazing Door Open (faster than TURBO!)
                VDoor.EV_DoDoor(line, blazeOpen);
                line.special = 0;
                break;

            case 100:
                // Build Stairs Turbo 16
                Floor.EV_BuildStairs(line, turbo16);
                line.special = 0;
                break;

            case 110:
                // Blazing Door Close (faster than TURBO!)
                VDoor.EV_DoDoor(line, blazeClose);
                line.special = 0;
                break;

            case 119:
                // Raise floor to nearest surr. floor
                Floor.EV_DoFloor(line, raiseFloorToNearest);
                line.special = 0;
                break;

            case 121:
                // Blazing PlatDownWaitUpStay
                Platform.EV_DoPlat(line, blazeDWUS, 0);
                line.special = 0;
                break;

            case 124:
                // Secret EXIT
                Game.getInstance().G_SecretExitLevel();
                break;

            case 125:
                // TELEPORT MonsterONLY
                if (thing.player == null) {
                    Teleport.EV_Teleport(line, side, thing);
                    line.special = 0;
                }
                break;

            case 130:
                // Raise Floor Turbo
                Floor.EV_DoFloor(line, raiseFloorTurbo);
                line.special = 0;
                break;

            case 141:
                // Silent Ceiling Crush & Raise
                Ceiling.EV_DoCeiling(line, silentCrushAndRaise);
                line.special = 0;
                break;

            // RETRIGGERS.  All from here till end.
            case 72:
                // Ceiling Crush
                Ceiling.EV_DoCeiling(line, lowerAndCrush);
                break;

            case 73:
                // Ceiling Crush and Raise
                Ceiling.EV_DoCeiling(line, crushAndRaise);
                break;

            case 74:
                // Ceiling Crush Stop
                Ceiling.EV_CeilingCrushStop(line);
                break;

            case 75:
                // Close Door
                VDoor.EV_DoDoor(line, close);
                break;

            case 76:
                // Close Door 30
                VDoor.EV_DoDoor(line, close30ThenOpen);
                break;

            case 77:
                // Fast Ceiling Crush & Raise
                Ceiling.EV_DoCeiling(line, fastCrushAndRaise);
                break;

            case 79:
                // Lights Very Dark
                Lights.EV_LightTurnOn(line, 35);
                break;

            case 80:
                // Light Turn On - brightest near
                Lights.EV_LightTurnOn(line, 0);
                break;

            case 81:
                // Light Turn On 255
                Lights.EV_LightTurnOn(line, 255);
                break;

            case 82:
                // Lower Floor To Lowest
                Floor.EV_DoFloor(line, lowerFloorToLowest);
                break;

            case 83:
                // Lower Floor
                Floor.EV_DoFloor(line, lowerFloor);
                break;

            case 84:
                // LowerAndChange
                Floor.EV_DoFloor(line, lowerAndChange);
                break;

            case 86:
                // Open Door
                VDoor.EV_DoDoor(line, open);
                break;

            case 87:
                // Perpetual Platform Raise
                Platform.EV_DoPlat(line, perpetualRaise, 0);
                break;

            case 88:
                // PlatDownWaitUp
                Platform.EV_DoPlat(line, downWaitUpStay, 0);
                break;

            case 89:
                // Platform Stop
                Platform.EV_StopPlat(line);
                break;

            case 90:
                // Raise Door
                VDoor.EV_DoDoor(line, normal);
                break;

            case 91:
                // Raise Floor
                Floor.EV_DoFloor(line, raiseFloor);
                break;

            case 92:
                // Raise Floor 24
                Floor.EV_DoFloor(line, raiseFloor24);
                break;

            case 93:
                // Raise Floor 24 And Change
                Floor.EV_DoFloor(line, raiseFloor24AndChange);
                break;

            case 94:
                // Raise Floor Crush
                Floor.EV_DoFloor(line, raiseFloorCrush);
                break;

            case 95:
                // Raise floor to nearest height
                // and change texture.
                Platform.EV_DoPlat(line, raiseToNearestAndChange, 0);
                break;

            case 96:
                // Raise floor to shortest texture height
                // on either side of lines.
                Floor.EV_DoFloor(line, raiseToTexture);
                break;

            case 97:
                // TELEPORT!
                Teleport.EV_Teleport(line, side, thing);
                break;

            case 98:
                // Lower Floor (TURBO)
                Floor.EV_DoFloor(line, turboLower);
                break;

            case 105:
                // Blazing Door Raise (faster than TURBO!)
                VDoor.EV_DoDoor(line, blazeRaise);
                break;

            case 106:
                // Blazing Door Open (faster than TURBO!)
                VDoor.EV_DoDoor(line, blazeOpen);
                break;

            case 107:
                // Blazing Door Close (faster than TURBO!)
                VDoor.EV_DoDoor(line, blazeClose);
                break;

            case 120:
                // Blazing PlatDownWaitUpStay.
                Platform.EV_DoPlat(line, blazeDWUS, 0);
                break;

            case 126:
                // TELEPORT MonsterONLY.
                if (thing.player == null) {
                    Teleport.EV_Teleport(line, side, thing);
                }
                break;

            case 128:
                // Raise To Nearest Floor
                Floor.EV_DoFloor(line, raiseFloorToNearest);
                break;

            case 129:
                // Raise Floor Turbo
                Floor.EV_DoFloor(line, raiseFloorTurbo);
                break;
        }
    }

    //
    // P_ShootSpecialLine - IMPACT SPECIALS
    // Called when a thing shoots a special line.
    //
    public static void P_ShootSpecialLine(MapObject thing, Line line) {
        boolean ok;

        //	Impacts that other things can activate.
        if (thing.player == null) {
            ok = false;
            switch (line.special) {
                case 46:
                    // OPEN DOOR IMPACT
                    ok = true;
                    break;
            }
            if (!ok) {
                return;
            }
        }

        switch (line.special) {
            case 24:
                // RAISE FLOOR
                Floor.EV_DoFloor(line, raiseFloor);
                Game.getInstance().playerSetup.svitch.P_ChangeSwitchTexture(line, 0);
                break;

            case 46:
                // OPEN DOOR
                VDoor.EV_DoDoor(line, open);
                Game.getInstance().playerSetup.svitch.P_ChangeSwitchTexture(line, 1);
                break;

            case 47:
                // RAISE FLOOR NEAR AND CHANGE
                Platform.EV_DoPlat(line, raiseToNearestAndChange, 0);
                Game.getInstance().playerSetup.svitch.P_ChangeSwitchTexture(line, 0);
                break;
        }
    }

    //
    // P_PlayerInSpecialSector
    // Called every tic frame
    //  that the player origin is in a special sector
    //
    public static void P_PlayerInSpecialSector(Player player) {
        Sector sector;

        sector = player.mo.subsector.sector;

        // Falling, not all the way down yet?
        if (player.mo.z != sector.floorheight) {
            return;
        }

        int leveltime = Game.getInstance().leveltime;

        // Has hitten ground.
        switch (sector.special) {
            case 5:
                // HELLSLIME DAMAGE
                if (0 == player.powers[pw_ironfeet.ordinal()]) {
                    if (0 == (leveltime & 0x1f)) {
                        Interaction.P_DamageMobj(player.mo, null, null, 10);
                    }
                }
                break;

            case 7:
                // NUKAGE DAMAGE
                if (0 == player.powers[pw_ironfeet.ordinal()]) {
                    if (0 == (leveltime & 0x1f)) {
                        Interaction.P_DamageMobj(player.mo, null, null, 5);
                    }
                }
                break;

            case 16:
            // SUPER HELLSLIME DAMAGE
            case 4:
                // STROBE HURT
                if (0 == player.powers[pw_ironfeet.ordinal()]
                        || (Random.getInstance().P_Random() < 5)) {
                    if (0 == (leveltime & 0x1f)) {
                        Interaction.P_DamageMobj(player.mo, null, null, 20);
                    }
                }
                break;

            case 9:
                // SECRET SECTOR
                player.secretcount++;
                sector.special = 0;
                break;

            case 11:
                // EXIT SUPER DAMAGE! (for E1M8 finale)
                player.cheats &= ~CF_GODMODE.getValue();

                if (0 == (leveltime & 0x1f)) {
                    Interaction.P_DamageMobj(player.mo, null, null, 20);
                }

                if (player.health <= 10) {
                    Game.getInstance().G_ExitLevel();
                }
                break;

            default:
                logger.log(Level.SEVERE, "P_PlayerInSpecialSector: unknown special {0}\n",
                        new Object[]{sector.special}
                );
                break;
        };
    }

    //
    // P_UpdateSpecials
    // Animate planes, scroll walls, etc.
    //
    boolean levelTimer;
    int levelTimeCount;

    public void P_UpdateSpecials() {
        //Anim	anim;
        int pic;
        int i;
        Line line;

        PlayerSetup ps = Game.getInstance().playerSetup;

        //	LEVEL TIMER
        if (levelTimer == true) {
            levelTimeCount--;
            if (0 == levelTimeCount) {
                Game.getInstance().G_ExitLevel();
            }
        }

        //	ANIMATE FLATS AND TEXTURES GLOBALLY
        //for (anim = anims ; anim < lastanim ; anim++)
        for (Anim anim : anims) {
            if ( anim == null ) {
                continue;
            }
            for (i = anim.basepic; i < anim.basepic + anim.numpics; i++) {
                pic = anim.basepic + ((Game.getInstance().leveltime / anim.speed + i) % anim.numpics);
                if (anim.istexture) {
                    Game.getInstance().renderer.data.texturetranslation[i] = pic;   //  This shold be the Wad's texture index.
                } else {
                    Game.getInstance().renderer.data.flattranslation[i] = pic;
                }
            }
        }

        //	ANIMATE LINE SPECIALS
        for (i = 0; i < numlinespecials; i++) {
            line = linespeciallist[i];
            switch (line.special) {
                case 48:
                    // EFFECT FIRSTCOL SCROLL +
                    //ps.sides[line.sidenum[0]].textureoffset += FRACUNIT;
                    ps.sides.get(line.sidenum[0]).textureoffset += FRACUNIT;
                    break;
            }
        }

        Button buttonlist[] = Game.getInstance().playerSetup.svitch.buttonlist;
        //	DO BUTTONS
        for (i = 0; i < MAXBUTTONS; i++) {
            if (buttonlist[i].btimer>0) {
                buttonlist[i].btimer--;
                if (0==buttonlist[i].btimer) {
                    switch (buttonlist[i].where) {
                        case TOP:
                            //ps.sides[buttonlist[i].line.sidenum[0]]
                            ps.sides.get(buttonlist[i].line.sidenum[0])
                                    .setTopTextureNum(buttonlist[i].btexture);
                            break;

                        case MIDDLE:
                            //ps.sides[buttonlist[i].line.sidenum[0]]
                            ps.sides.get(buttonlist[i].line.sidenum[0])
                                    .setMidTextureNum(buttonlist[i].btexture);
                            break;

                        case BOTTOM:
                            //ps.sides[buttonlist[i].line.sidenum[0]]
                            ps.sides.get(buttonlist[i].line.sidenum[0])
                                    .setBottomTextureNum(buttonlist[i].btexture);
                            break;
                    }
                    Game.getInstance().sound.S_StartSound(buttonlist[i].soundorg, sfx_swtchn);
                    //memset( & buttonlist[i], 0, sizeof(button_t));
                    buttonlist[i].reset();
                }
            }
        }

    }

    //
    // Special Stuff that can not be categorized
    //
    int EV_DoDonut(Line line) {
        Sector s1;
        Sector s2;
        Sector s3;
        int secnum;
        int rtn;
        int i;
        Floor floor;

        PlayerSetup ps = Game.getInstance().playerSetup;

        secnum = -1;
        rtn = 0;
        while ((secnum = P_FindSectorFromLineTag(line, secnum)) >= 0) {
            //s1 = ps.sectors[secnum];
            s1 = ps.sectors.get(secnum);

            // ALREADY MOVING?  IF SO, KEEP GOING...
            if (s1.specialdata!=null ) {
                continue;
            }

            rtn = 1;
            s2 = getNextSector(s1.lines[0], s1);
            for (i = 0; i < s2.linecount; i++) {
                if ((0 == (s2.lines[i].flags & ML_TWOSIDED))
                        || (s2.lines[i].backsector == s1)) {
                    continue;
                }
                s3 = s2.lines[i].backsector;

                //	Spawn rising slime
                //floor = Z_Malloc (sizeof(*floor), PU_LEVSPEC, 0);
                floor = new Floor();
                Tick.P_AddThinker(floor);
                s2.specialdata = floor;
                floor.setFunction( new T_MoveFloor() );
                floor.type = donutRaise;
                floor.crush = false;
                floor.direction = 1;
                floor.sector = s2;
                floor.speed = FLOORSPEED / 2;
                floor.texture = s3.getFloorPic(Game.getInstance().wad);
                floor.newspecial = 0;
                floor.floordestheight = s3.floorheight;

                //	Spawn lowering donut-hole
                //floor = Z_Malloc (sizeof(*floor), PU_LEVSPEC, 0);
                floor = new Floor();
                Tick.P_AddThinker(floor);
                s1.specialdata = floor;
                floor.setFunction(new T_MoveFloor());
                floor.type = lowerFloor;
                floor.crush = false;
                floor.direction = -1;
                floor.sector = s1;
                floor.speed = FLOORSPEED / 2;
                floor.floordestheight = s3.floorheight;
                break;
            }
        }
        return rtn;
    }

    //
    // SPECIAL SPAWNING
    //
    //
    // P_SpawnSpecials
    // After the map has been loaded, scan for specials
    //  that spawn thinkers
    //
    short numlinespecials;
    Line linespeciallist[] = new Line[MAXLINEANIMS];

    // Parses command line parameters.
    public void P_SpawnSpecials() {
        //Sector	sector;
        int i;
        int episode;

        PlayerSetup ps = Game.getInstance().playerSetup;

        episode = 1;
        if (Game.getInstance().wad.W_CheckNumForName("TEXTURE2") >= 0) {
            episode = 2;
        }

        // See if -TIMER needs to be used.
        levelTimer = false;

        if (Game.getInstance().isParam("-avg") && Game.getInstance().deathmatch > 0) {
            levelTimer = true;
            levelTimeCount = 20 * 60 * 35;
        }

        i = Game.getInstance().args.indexOf("-timer");
        if (i >= 0 && Game.getInstance().deathmatch > 0) {
            int time;
            time = Integer.valueOf(Game.getInstance().args.get(i + 1)) * 60 * 35;
            levelTimer = true;
            levelTimeCount = time;
        }

        //	Init special SECTORs.
        //sector = ps.sectors;
        //for (i=0 ; i<numsectors ; i++, sector++) {
        i = 0;
        for (Sector sector : ps.sectors) {
            if (0 == sector.special) {
                continue;  // just make this a case?
            }

            switch (sector.special) {
                case 1:
                    // FLICKERING LIGHTS
                    Lights.P_SpawnLightFlash(sector);
                    break;

                case 2:
                    // STROBE FAST
                    Lights.P_SpawnStrobeFlash(sector, FASTDARK, 0);
                    break;

                case 3:
                    // STROBE SLOW
                    Lights.P_SpawnStrobeFlash(sector, SLOWDARK, 0);
                    break;

                case 4:
                    // STROBE FAST/DEATH SLIME
                    Lights.P_SpawnStrobeFlash(sector, FASTDARK, 0);
                    sector.special = 4;
                    break;

                case 8:
                    // GLOWING LIGHT
                    Lights.P_SpawnGlowingLight(sector);
                    break;
                case 9:
                    // SECRET SECTOR
                    Game.getInstance().totalsecret++;
                    break;

                case 10:
                    // DOOR CLOSE IN 30 SECONDS
                    VDoor.P_SpawnDoorCloseIn30(sector);
                    break;

                case 12:
                    // SYNC STROBE SLOW
                    Lights.P_SpawnStrobeFlash(sector, SLOWDARK, 1);
                    break;

                case 13:
                    // SYNC STROBE FAST
                    Lights.P_SpawnStrobeFlash(sector, FASTDARK, 1);
                    break;

                case 14:
                    // DOOR RAISE IN 5 MINUTES
                    VDoor.P_SpawnDoorRaiseIn5Mins(sector, i);
                    break;

                case 17:
                    Lights.P_SpawnFireFlicker(sector);
                    break;
            }

            i++;
        }

        //	Init line EFFECTs
        numlinespecials = 0;
        //for (i = 0;i < numlines; i++) {
        for (Line line : ps.lines) {
            switch (line.special) {  // Ummmm......   really?
                case 48:
                    // EFFECT FIRSTCOL SCROLL+
                    linespeciallist[numlinespecials] = line;
                    numlinespecials++;
                    break;
            }
        }

        //	Init other misc stuff
        for (i = 0; i < MAXCEILINGS; i++) {
            activeceilings[i] = null;
        }

        for (i = 0; i < MAXPLATS; i++) {
            activeplats[i] = null;
        }

        // TODO move this to switch as a method.
        Button buttonlist[] = Game.getInstance().playerSetup.svitch.buttonlist;
        for (i = 0; i < MAXBUTTONS; i++) {
            buttonlist[i] = new Button();
            //memset(buttonlist[i], 0, sizeof(button_t));
        }

        // UNUSED: no horizonal sliders.
        //	P_InitSlidingDoorFrames();
    }

}
