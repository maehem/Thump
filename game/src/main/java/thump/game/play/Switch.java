/*
 *	Switches, buttons. Two-state animation. Exits.
 */
package thump.game.play;

import static thump.base.Defines.logger;
import thump.game.Game;
import thump.game.PlayerSetup;
import thump.game.Defines;
import thump.game.maplevel.MapObject;
import static thump.game.play.Button.BWhere.BOTTOM;
import static thump.game.play.Button.BWhere.MIDDLE;
import static thump.game.play.Button.BWhere.TOP;
import static thump.game.play.Ceiling.Type.*;
import static thump.game.play.Floor.Type.*;
import static thump.game.play.Platform.Type.*;
import static thump.game.play.SpecialEffects.BUTTONTIME;
import static thump.game.play.SpecialEffects.MAXBUTTONS;
import static thump.game.play.SpecialEffects.MAXSWITCHES;
import static thump.game.play.SpecialEffects.Stair.*;
import static thump.game.play.VDoor.Type.blazeClose;
import static thump.game.play.VDoor.Type.blazeOpen;
import static thump.game.play.VDoor.Type.blazeRaise;
import static thump.game.play.VDoor.Type.close;
import static thump.game.play.VDoor.Type.normal;
import static thump.game.play.VDoor.Type.open;
import thump.game.sound.Sound;
import thump.game.sound.sfx.Sounds.SfxEnum;
import static thump.game.sound.sfx.Sounds.SfxEnum.sfx_swtchn;
import static thump.game.sound.sfx.Sounds.SfxEnum.sfx_swtchx;
import thump.wad.Wad;
import thump.wad.map.Line;
import static thump.wad.map.Line.ML_SECRET;

/**
 *
 * @author mark
 */
public class Switch {


    //
    // CHANGE THE TEXTURE OF A WALL SWITCH TO ITS OPPOSITE
    //
    SwitchList alphSwitchList[] =
    {
        // Doom shareware episode 1 switches
        new SwitchList("SW1BRCOM",	"SW2BRCOM",	1),
        new SwitchList("SW1BRN1",	"SW2BRN1",	1),
        new SwitchList("SW1BRN2",	"SW2BRN2",	1),
        new SwitchList("SW1BRNGN",	"SW2BRNGN",	1),
        new SwitchList("SW1BROWN",	"SW2BROWN",	1),
        new SwitchList("SW1COMM",	"SW2COMM",	1),
        new SwitchList("SW1COMP",	"SW2COMP",	1),
        new SwitchList("SW1DIRT",	"SW2DIRT",	1),
        new SwitchList("SW1EXIT",	"SW2EXIT",	1),
        new SwitchList("SW1GRAY",	"SW2GRAY",	1),
        new SwitchList("SW1GRAY1",	"SW2GRAY1",	1),
        new SwitchList("SW1METAL",	"SW2METAL",	1),
        new SwitchList("SW1PIPE",	"SW2PIPE",	1),
        new SwitchList("SW1SLAD",	"SW2SLAD",	1),
        new SwitchList("SW1STARG",	"SW2STARG",	1),
        new SwitchList("SW1STON1",	"SW2STON1",	1),
        new SwitchList("SW1STON2",	"SW2STON2",	1),
        new SwitchList("SW1STONE",	"SW2STONE",	1),
        new SwitchList("SW1STRTN",	"SW2STRTN",	1),

        // Doom registered episodes 2&3 switches
        new SwitchList("SW1BLUE",	"SW2BLUE",	2),
        new SwitchList("SW1CMT",	"SW2CMT",	2),
        new SwitchList("SW1GARG",	"SW2GARG",	2),
        new SwitchList("SW1GSTON",	"SW2GSTON",	2),
        new SwitchList("SW1HOT",	"SW2HOT",	2),
        new SwitchList("SW1LION",	"SW2LION",	2),
        new SwitchList("SW1SATYR",	"SW2SATYR",	2),
        new SwitchList("SW1SKIN",	"SW2SKIN",	2),
        new SwitchList("SW1VINE",	"SW2VINE",	2),
        new SwitchList("SW1WOOD",	"SW2WOOD",	2),

        // Doom II switches
        new SwitchList("SW1PANEL",	"SW2PANEL",	3),
        new SwitchList("SW1ROCK",	"SW2ROCK",	3),
        new SwitchList("SW1MET2",	"SW2MET2",	3),
        new SwitchList("SW1WDMET",	"SW2WDMET",	3),
        new SwitchList("SW1BRIK",	"SW2BRIK",	3),
        new SwitchList("SW1MOD1",	"SW2MOD1",	3),
        new SwitchList("SW1ZIM",	"SW2ZIM",	3),
        new SwitchList("SW1STON6",	"SW2STON6",	3),
        new SwitchList("SW1TEK",	"SW2TEK",	3),
        new SwitchList("SW1MARB",	"SW2MARB",	3),
        new SwitchList("SW1SKULL",	"SW2SKULL",	3)

        //new SwitchList("\0",		"\0",		0}
    };

    int     switchlist[] = new int[MAXSWITCHES * 2];
    int     numswitches;
    Button  buttonlist[] = new Button[MAXBUTTONS];

    //
    // P_InitSwitchList
    // Only called at game initialization.
    //
    public void P_InitSwitchList() {
        int i;
        int index;
        int episode;

        episode = 1;

        if (Game.getInstance().gameMode == Defines.GameMode.REGISTERED) {
            episode = 2;
        } else {
            if (Game.getInstance().gameMode == Defines.GameMode.COMMERCIAL) {
                episode = 3;
            }
        }

        //for (index = 0, i = 0; i < MAXSWITCHES; i++) {
        for (index = 0, i = 0; i < alphSwitchList.length; i++) {
            if (0==alphSwitchList[i].episode) {
                numswitches = index / 2;
                switchlist[index] = -1;
                break;
            }

            if (alphSwitchList[i].episode <= episode) {
//    #if 0	// UNUSED - debug?
//                int		value;
//
//                if (R_CheckTextureNumForName(alphSwitchList[i].name1) < 0)
//                {
//                    I_Error("Can't find switch texture '%s'!",
//                            alphSwitchList[i].name1);
//                    continue;
//                }
//
//                value = R_TextureNumForName(alphSwitchList[i].name1);
//    #endif
                switchlist[index] = Game.getInstance().wad.getTextureNum(alphSwitchList[i].name1);
                index++;
                switchlist[index] = Game.getInstance().wad.getTextureNum(alphSwitchList[i].name2);
                index++;
            }
        }
    }


    //
    // Start a button counting down till it turns off.
    //
    void P_StartButton(
            Line line,
            Button.BWhere w,
            int texture,
            int time) {
        int i;

        // See if button is already pressed
        for (i = 0; i < MAXBUTTONS; i++) {
            if (buttonlist[i].btimer > 0 && buttonlist[i].line == line) {
                return;
            }
        }

        for (i = 0; i < MAXBUTTONS; i++) {
            if (0==buttonlist[i].btimer) {
                buttonlist[i].line = line;
                buttonlist[i].where = w;
                buttonlist[i].btexture = texture;
                buttonlist[i].btimer = time;
                buttonlist[i].soundorg = (MapObject) line.frontsector.soundorg;
                return;
            }
        }

        logger.severe("P_StartButton: no button slots left!\n");
    }





    //
    // Function that changes wall texture.
    // Tell it if switch is ok to use again (1=yes, it's a button).
    //
    void P_ChangeSwitchTexture(Line line, int useAgain) {  // TODO change to boolean
        int     texTop;
        int     texMid;
        int     texBot;
        int     i;
        SfxEnum     sound;

        if (0==useAgain) {
            line.special = 0;
        }

        PlayerSetup ps = Game.getInstance().playerSetup;
        Sound gs = Game.getInstance().sound;
        Wad wad = Game.getInstance().wad;
        
//        texTop = ps.sides[line.sidenum[0]].getTopTextureNum();
//        texMid = ps.sides[line.sidenum[0]].getMidTextureNum();
//        texBot = ps.sides[line.sidenum[0]].getBottomTextureNum();
        texTop = ps.sides.get(line.sidenum[0]).getTopTextureNum(wad);
        texMid = ps.sides.get(line.sidenum[0]).getMidTextureNum(wad);
        texBot = ps.sides.get(line.sidenum[0]).getBottomTextureNum(wad);

        sound = sfx_swtchn;

        // EXIT SWITCH?
        if (line.special == 11) {
            sound = sfx_swtchx;
        }

        for (i = 0;i < numswitches*2;i++) {
            if (switchlist[i] == texTop) {
                gs.S_StartSound(buttonlist[0].soundorg,sound);
                //ps.sides[line.sidenum[0]].setTopTextureNum(switchlist[i^1]);
                ps.sides.get(line.sidenum[0]).setTopTextureNum(switchlist[i^1]);

                if (useAgain>0) {
                    P_StartButton(line,TOP,switchlist[i],BUTTONTIME);
                }

                return;
            } else {
                if (switchlist[i] == texMid) {
                    gs.S_StartSound(buttonlist[0].soundorg,sound);
                    //ps.sides[line.sidenum[0]].setMidTextureNum(switchlist[i^1]);
                    ps.sides.get(line.sidenum[0]).setMidTextureNum(switchlist[i^1]);

                    if (useAgain>0) {
                        P_StartButton(line, MIDDLE,switchlist[i],BUTTONTIME);
                    }

                    return;
                } else {
                    if (switchlist[i] == texBot) {
                        gs.S_StartSound(buttonlist[0].soundorg,sound);
                        //ps.sides[line.sidenum[0]].setBottomTextureNum(switchlist[i^1]);
                        ps.sides.get(line.sidenum[0]).setBottomTextureNum(switchlist[i^1]);

                        if (useAgain>0) {
                            P_StartButton(line, BOTTOM,switchlist[i],BUTTONTIME);
                        }

                        return;
                    }
                }
            }
        }
    }


    //
    // P_UseSpecialLine
    // Called when a thing uses a special line.
    // Only the front sides of lines are usable.
    //
    public boolean P_UseSpecialLine(MapObject thing, Line line, int side) {

        // Err...
        // Use the back sides of VERY SPECIAL lines...
        if (side>0) {
            switch (line.special) {
                case 124:
                    // Sliding door open&close
                    // UNUSED?
                    break;

                default:
                    return false;
            }
        }

        // Switches that other things can activate.
        if (null==thing.player) {
            // never open secret doors
            if ((line.flags & ML_SECRET)>0) {
                return false;
            }

            switch (line.special) {
                case 1: 	// MANUAL DOOR RAISE
                case 32:	// MANUAL BLUE
                case 33:	// MANUAL RED
                case 34:	// MANUAL YELLOW
                    break;

                default:
                    return false;
            }
        }

        PlayerSetup ps = Game.getInstance().playerSetup;
        // do something  
        switch (line.special) {
            // MANUALS
            case 1:		// Vertical Door
            case 26:		// Blue Door/Locked
            case 27:		// Yellow Door /Locked
            case 28:		// Red Door /Locked

            case 31:		// Manual door open
            case 32:		// Blue locked door open
            case 33:		// Red locked door open
            case 34:		// Yellow locked door open

            case 117:		// Blazing door raise
            case 118:		// Blazing door open
                VDoor.EV_VerticalDoor(line, thing);
                break;

            //UNUSED - Door Slide Open&Close
            // case 124:
            // EV_SlidingDoor (line, thing);
            // break;
            // SWITCHES
            case 7:
                // Build Stairs
                if (Floor.EV_BuildStairs(line, build8)>0) {
                    P_ChangeSwitchTexture(line, 0);
                }
                break;

            case 9:
                // Change Donut
                if (Game.getInstance().playerSetup.effects.EV_DoDonut(line)>0) {
                    P_ChangeSwitchTexture(line, 0);
                }
                break;

            case 11:
                // Exit level
                P_ChangeSwitchTexture(line, 0);
                Game.getInstance().G_ExitLevel();
                break;

            case 14:
                // Raise Floor 32 and change texture
                if (Platform.EV_DoPlat(line, raiseAndChange, 32)>0) {
                    P_ChangeSwitchTexture(line, 0);
                }
                break;

            case 15:
                // Raise Floor 24 and change texture
                if (Platform.EV_DoPlat(line, raiseAndChange, 24)>0) {
                    P_ChangeSwitchTexture(line, 0);
                }
                break;

            case 18:
                // Raise Floor to next highest floor
                if (Floor.EV_DoFloor(line, raiseFloorToNearest)>0) {
                    P_ChangeSwitchTexture(line, 0);
                }
                break;

            case 20:
                // Raise Plat next highest floor and change texture
                if (Platform.EV_DoPlat(line, raiseToNearestAndChange, 0)>0) {
                    P_ChangeSwitchTexture(line, 0);
                }
                break;

            case 21:
                // PlatDownWaitUpStay
                if (Platform.EV_DoPlat(line, downWaitUpStay, 0)>0) {
                    P_ChangeSwitchTexture(line, 0);
                }
                break;

            case 23:
                // Lower Floor to Lowest
                if (Floor.EV_DoFloor(line, lowerFloorToLowest)>0) {
                    P_ChangeSwitchTexture(line, 0);
                }
                break;

            case 29:
                // Raise Door
                if (VDoor.EV_DoDoor(line, normal)>0) {
                    P_ChangeSwitchTexture(line, 0);
                }
                break;

            case 41:
                // Lower Ceiling to Floor
                if (Ceiling.EV_DoCeiling(line, lowerToFloor)>0) {
                    P_ChangeSwitchTexture(line, 0);
                }
                break;

            case 71:
                // Turbo Lower Floor
                if (Floor.EV_DoFloor(line, turboLower)>0) {
                    P_ChangeSwitchTexture(line, 0);
                }
                break;

            case 49:
                // Ceiling Crush And Raise
                if (Ceiling.EV_DoCeiling(line, crushAndRaise)>0) {
                    P_ChangeSwitchTexture(line, 0);
                }
                break;

            case 50:
                // Close Door
                if (VDoor.EV_DoDoor(line, close)>0) {
                    P_ChangeSwitchTexture(line, 0);
                }
                break;

            case 51:
                // Secret EXIT
                P_ChangeSwitchTexture(line, 0);
                Game.getInstance().G_SecretExitLevel();
                break;

            case 55:
                // Raise Floor Crush
                if (Floor.EV_DoFloor(line, raiseFloorCrush)>0) {
                    P_ChangeSwitchTexture(line, 0);
                }
                break;

            case 101:
                // Raise Floor
                if (Floor.EV_DoFloor(line, raiseFloor)>0) {
                    P_ChangeSwitchTexture(line, 0);
                }
                break;

            case 102:
                // Lower Floor to Surrounding floor height
                if (Floor.EV_DoFloor(line, lowerFloor)>0) {
                    P_ChangeSwitchTexture(line, 0);
                }
                break;

            case 103:
                // Open Door
                if (VDoor.EV_DoDoor(line, open)>0) {
                    P_ChangeSwitchTexture(line, 0);
                }
                break;

            case 111:
                // Blazing Door Raise (faster than TURBO!)
                if (VDoor.EV_DoDoor(line, blazeRaise)>0) {
                    P_ChangeSwitchTexture(line, 0);
                }
                break;

            case 112:
                // Blazing Door Open (faster than TURBO!)
                if (VDoor.EV_DoDoor(line, blazeOpen)>0) {
                    P_ChangeSwitchTexture(line, 0);
                }
                break;

            case 113:
                // Blazing Door Close (faster than TURBO!)
                if (VDoor.EV_DoDoor(line, blazeClose)>0) {
                    P_ChangeSwitchTexture(line, 0);
                }
                break;

            case 122:
                // Blazing PlatDownWaitUpStay
                if (Platform.EV_DoPlat(line, blazeDWUS, 0)>0) {
                    P_ChangeSwitchTexture(line, 0);
                }
                break;

            case 127:
                // Build Stairs Turbo 16
                if (Floor.EV_BuildStairs(line, turbo16)>0) {
                    P_ChangeSwitchTexture(line, 0);
                }
                break;

            case 131:
                // Raise Floor Turbo
                if (Floor.EV_DoFloor(line, raiseFloorTurbo)>0) {
                    P_ChangeSwitchTexture(line, 0);
                }
                break;

            case 133:
            // BlzOpenDoor BLUE
            case 135:
            // BlzOpenDoor RED
            case 137:
                // BlzOpenDoor YELLOW
                if (VDoor.EV_DoLockedDoor(line, blazeOpen, thing)>0) {
                    P_ChangeSwitchTexture(line, 0);
                }
                break;

            case 140:
                // Raise Floor 512
                if (Floor.EV_DoFloor(line, raiseFloor512)>0) {
                    P_ChangeSwitchTexture(line, 0);
                }
                break;

            // BUTTONS
            case 42:
                // Close Door
                if (VDoor.EV_DoDoor(line, close)>0) {
                    P_ChangeSwitchTexture(line, 1);
                }
                break;

            case 43:
                // Lower Ceiling to Floor
                if (Ceiling.EV_DoCeiling(line, lowerToFloor)>0) {
                    P_ChangeSwitchTexture(line, 1);
                }
                break;

            case 45:
                // Lower Floor to Surrounding floor height
                if (Floor.EV_DoFloor(line, lowerFloor)>0) {
                    P_ChangeSwitchTexture(line, 1);
                }
                break;

            case 60:
                // Lower Floor to Lowest
                if (Floor.EV_DoFloor(line, lowerFloorToLowest)>0) {
                    P_ChangeSwitchTexture(line, 1);
                }
                break;

            case 61:
                // Open Door
                if (VDoor.EV_DoDoor(line, open)>0) {
                    P_ChangeSwitchTexture(line, 1);
                }
                break;

            case 62:
                // PlatDownWaitUpStay
                if (Platform.EV_DoPlat(line, downWaitUpStay, 1)>0) {
                    P_ChangeSwitchTexture(line, 1);
                }
                break;

            case 63:
                // Raise Door
                if (VDoor.EV_DoDoor(line, normal)>0) {
                    P_ChangeSwitchTexture(line, 1);
                }
                break;

            case 64:
                // Raise Floor to ceiling
                if (Floor.EV_DoFloor(line, raiseFloor)>0) {
                    P_ChangeSwitchTexture(line, 1);
                }
                break;

            case 66:
                // Raise Floor 24 and change texture
                if (Platform.EV_DoPlat(line, raiseAndChange, 24)>0) {
                    P_ChangeSwitchTexture(line, 1);
                }
                break;

            case 67:
                // Raise Floor 32 and change texture
                if (Platform.EV_DoPlat(line, raiseAndChange, 32)>0) {
                    P_ChangeSwitchTexture(line, 1);
                }
                break;

            case 65:
                // Raise Floor Crush
                if (Floor.EV_DoFloor(line, raiseFloorCrush)>0) {
                    P_ChangeSwitchTexture(line, 1);
                }
                break;

            case 68:
                // Raise Plat to next highest floor and change texture
                if (Platform.EV_DoPlat(line, raiseToNearestAndChange, 0)>0) {
                    P_ChangeSwitchTexture(line, 1);
                }
                break;

            case 69:
                // Raise Floor to next highest floor
                if (Floor.EV_DoFloor(line, raiseFloorToNearest)>0) {
                    P_ChangeSwitchTexture(line, 1);
                }
                break;

            case 70:
                // Turbo Lower Floor
                if (Floor.EV_DoFloor(line, turboLower)>0) {
                    P_ChangeSwitchTexture(line, 1);
                }
                break;

            case 114:
                // Blazing Door Raise (faster than TURBO!)
                if (VDoor.EV_DoDoor(line, blazeRaise)>0) {
                    P_ChangeSwitchTexture(line, 1);
                }
                break;

            case 115:
                // Blazing Door Open (faster than TURBO!)
                if (VDoor.EV_DoDoor(line, blazeOpen)>0) {
                    P_ChangeSwitchTexture(line, 1);
                }
                break;

            case 116:
                // Blazing Door Close (faster than TURBO!)
                if (VDoor.EV_DoDoor(line, blazeClose)>0) {
                    P_ChangeSwitchTexture(line, 1);
                }
                break;

            case 123:
                // Blazing PlatDownWaitUpStay
                if (Platform.EV_DoPlat(line, blazeDWUS, 0)>0) {
                    P_ChangeSwitchTexture(line, 1);
                }
                break;

            case 132:
                // Raise Floor Turbo
                if (Floor.EV_DoFloor(line, raiseFloorTurbo)>0) {
                    P_ChangeSwitchTexture(line, 1);
                }
                break;

            case 99:
            // BlzOpenDoor BLUE
            case 134:
            // BlzOpenDoor RED
            case 136:
                // BlzOpenDoor YELLOW
                if (VDoor.EV_DoLockedDoor(line, blazeOpen, thing)>0) {
                    P_ChangeSwitchTexture(line, 1);
                }
                break;

            case 138:
                // Light Turn On
                Lights.EV_LightTurnOn(line, 255);
                P_ChangeSwitchTexture(line, 1);
                break;

            case 139:
                // Light Turn Off
                Lights.EV_LightTurnOn(line, 35);
                P_ChangeSwitchTexture(line, 1);
                break;

        }

        return true;
    }

}
