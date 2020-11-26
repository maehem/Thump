/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.game.play.action;

import thump.game.Player;
import thump.game.maplevel.MapObject;
import thump.game.play.PSprite;

/**
 *
 * @author mark
 */
public class A_BossDeath implements Action {

    public A_BossDeath() {
    }

    @Override
    public void doAction(MapObject mo) { // change to actor
//        Thinker th;
//        MapObject mo2;
//        Line junk = new Line();
//        int i;
//        int gamemap = game.gamemap;
//        GameMode gamemode = game.gameMode;
//
//        if (gamemode == GameMode.COMMERCIAL) {
//            if (gamemap != 7) {
//                return;
//            }
//
//            if ((mo.type != MT_FATSO)
//                    && (mo.type != MT_BABY)) {
//                return;
//            }
//        } else {
//            switch (game.gameepisode) {
//                case 1:
//                    if (gamemap != 8) {
//                        return;
//                    }
//
//                    if (mo.type != MT_BRUISER) {
//                        return;
//                    }
//                    break;
//
//                case 2:
//                    if (gamemap != 8) {
//                        return;
//                    }
//
//                    if (mo.type != MT_CYBORG) {
//                        return;
//                    }
//                    break;
//
//                case 3:
//                    if (gamemap != 8) {
//                        return;
//                    }
//
//                    if (mo.type != MT_SPIDER) {
//                        return;
//                    }
//
//                    break;
//
//                case 4:
//                    switch (gamemap) {
//                        case 6:
//                            if (mo.type != MT_CYBORG) {
//                                return;
//                            }
//                            break;
//
//                        case 8:
//                            if (mo.type != MT_SPIDER) {
//                                return;
//                            }
//                            break;
//
//                        default:
//                            return;
//                    }
//                    break;
//
//                default:
//                    if (gamemap != 8) {
//                        return;
//                    }
//                    break;
//            }
//
//        }
//
//        // make sure there is a player alive for victory
//        for (i = 0; i < MAXPLAYERS; i++) {
//            if (game.playeringame[i] && game.players[i].health > 0) {
//                break;
//            }
//        }
//
//        if (i == MAXPLAYERS) {
//            return;	// no one left alive, so do not end game
//        }
//        // scan the remaining thinkers to see
//        // if all bosses are dead
//        for (th = game.thinkercap.getNextThinker(); th != game.thinkercap; th = th.getNextThinker()) {
//            if (!(th.getFunction() instanceof T_MobjThinker)) {
//                continue;
//            }
//
//            mo2 = (MapObject) th;
//            if (mo2 != mo
//                    && mo2.type == mo.type
//                    && mo2.health > 0) {
//                // other boss not dead
//                return;
//            }
//        }
//
//        // victory!
//        if (gamemode == GameMode.COMMERCIAL) {
//            if (gamemap == 7) {
//                if (mo.type == MT_FATSO) {
//                    junk.tag = 666;
//                    Floor.EV_DoFloor(junk, lowerFloorToLowest);
//                    return;
//                }
//
//                if (mo.type == MT_BABY) {
//                    junk.tag = 667;
//                    Floor.EV_DoFloor(junk, raiseToTexture);
//                    return;
//                }
//            }
//        } else {
//            switch (game.gameepisode) {
//                case 1:
//                    junk.tag = 666;
//                    Floor.EV_DoFloor(junk, lowerFloorToLowest);
//                    return;
//
//                case 4:
//                    switch (gamemap) {
//                        case 6:
//                            junk.tag = 666;
//                            VDoor.EV_DoDoor(junk, VDoor.Type.blazeOpen);
//                            return;
//
//                        case 8:
//                            junk.tag = 666;
//                            Floor.EV_DoFloor(junk, lowerFloorToLowest);
//                            return;
//                    }
//            }
//        }
//
//        game.G_ExitLevel();
    }

    @Override
    public void doAction(Player player, PSprite psp) {}
    
}
