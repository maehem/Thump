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
public class A_SpawnFly implements Action {

    public A_SpawnFly() {
    }

    @Override
    public void doAction(MapObject mo) {
//        MapObject newmobj;
//        MapObject fog;
//        MapObject targ;
//        int r;
//        MobJInfo.Type type;
//
//        --mo.reactiontime;
//        if (mo.reactiontime>0) {
//            return;	// still flying
//        }
//        targ = mo.target;
//
//        // First spawn teleport fog.
//        fog = MObject.P_SpawnMobj(targ.x, targ.y, targ.z, MT_SPAWNFIRE);
//        game.sound.S_StartSound(fog, sfx_telept);
//
//        // Randomly select monster to spawn.
//        r = Random.getInstance().P_Random();
//
//        // Probability distribution (kind of :),
//        // decreasing likelihood.
//        if (r < 50) {
//            type = MT_TROOP;
//        } else if (r < 90) {
//            type = MT_SERGEANT;
//        } else if (r < 120) {
//            type = MT_SHADOWS;
//        } else if (r < 130) {
//            type = MT_PAIN;
//        } else if (r < 160) {
//            type = MT_HEAD;
//        } else if (r < 162) {
//            type = MT_VILE;
//        } else if (r < 172) {
//            type = MT_UNDEAD;
//        } else if (r < 192) {
//            type = MT_BABY;
//        } else if (r < 222) {
//            type = MT_FATSO;
//        } else if (r < 246) {
//            type = MT_KNIGHT;
//        } else {
//            type = MT_BRUISER;
//        }
//
//        newmobj = MObject.P_SpawnMobj(targ.x, targ.y, targ.z, type);
//        if (P_LookForPlayers(newmobj, true)) {
//            MObject.P_SetMobjState(newmobj, newmobj.info.seestate);
//        }
//
//        // telefrag anything in this spot
//        game.map.P_TeleportMove(newmobj, newmobj.x, newmobj.y);
//
//        // remove self (i.e., cube).
//        game.movingObject.P_RemoveMobj(mo);
    }

    @Override
    public void doAction(Player player, PSprite psp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
