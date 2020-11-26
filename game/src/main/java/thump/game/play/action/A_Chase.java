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
public class A_Chase implements Action {

    public A_Chase() {
    }

    @Override
    public void doAction(MapObject actor) {
//        int delta;
//
//        if (actor.reactiontime > 0) {
//            actor.reactiontime--;
//        }
//
//        // modify target threshold
//        if (actor.threshold > 0) {
//            if (null == actor.target
//                    || actor.target.health <= 0) {
//                actor.threshold = 0;
//            } else {
//                actor.threshold--;
//            }
//        }
//
//        // turn towards movement direction if not there yet
//        if (actor.movedir != DI_NODIR) {
//            actor.angle &= (7 << 29);
//            delta = actor.angle - (actor.movedir.ordinal() << 29);
//
//            if (delta > 0) {
//                actor.angle -= ANG90 / 2;
//            } else if (delta < 0) {
//                actor.angle += ANG90 / 2;
//            }
//        }
//
//        if (null == actor.target
//                || 0 == (actor.target.flags & MF_SHOOTABLE.getValue())) {
//            // look for a new target
//            if (P_LookForPlayers(actor, true)) {
//                return; 	// got a new target
//            }
//            MObject.P_SetMobjState(actor, actor.info.spawnstate);
//            return;
//        }
//
//        // do not attack twice in a row
//        if ((actor.flags & MF_JUSTATTACKED.getValue()) > 0) {
//            actor.flags &= ~MF_JUSTATTACKED.getValue();
//            if (game.gameskill != sk_nightmare && !game.fastparm) {
//                P_NewChaseDir(actor);
//            }
//            return;
//        }
//
//        // check for melee attack
//        if (actor.info.meleestate.ordinal()>0
//                && P_CheckMeleeRange(actor)) {
//            if (actor.info.attacksound != null) {
//                game.sound.S_StartSound(actor, actor.info.attacksound);
//            }
//
//            MObject.P_SetMobjState(actor, actor.info.meleestate);
//            return;
//        }
//
//        // check for missile attack
//        if (actor.info.missilestate != null) {
//            if (game.gameskill.getValue() < sk_nightmare.getValue()
//                    && !game.fastparm && actor.movecount > 0) {
//                //goto nomissile;
//            } else if (!P_CheckMissileRange(actor)) {
//                //goto nomissile;
//            } else {
//                MObject.P_SetMobjState(actor, actor.info.missilestate);
//                actor.flags |= MF_JUSTATTACKED.getValue();
//                return;
//            }
//        }
//
//        // ?
//        //nomissile:
//        // possibly choose another target
//        if (game.netgame
//                && 0 == actor.threshold
//                && !game.sight.P_CheckSight(actor, actor.target)) {
//            if (P_LookForPlayers(actor, true)) {
//                return;	// got a new target
//            }
//        }
//
//        actor.movecount--;
//        // chase towards player
//        if (actor.movecount < 0 || !P_Move(actor)) {
//            P_NewChaseDir(actor);
//        }
//
//        // make active sound
//        if (actor.info.activesound != null
//                && Random.getInstance().P_Random() < 3) {
//            game.sound.S_StartSound(actor, actor.info.activesound);
//        }
    }

    @Override
    public void doAction(Player player, PSprite psp) {}
    
}
