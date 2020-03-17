/*
 * Teleportation
 */
package thump.play;

import thump.game.Game;
import thump.game.Thinker;
import thump.game.thinkeraction.T_MobjThinker;
import static thump.global.MobJInfo.Type.MT_TELEPORTMAN;
import static thump.global.MobJInfo.Type.MT_TFOG;
import static thump.global.Tables.ANGLETOFINESHIFT;
import static thump.global.Tables.finecosine;
import static thump.global.Tables.finesine;
import thump.maplevel.MapObject;
import static thump.maplevel.MapObject.MobileObjectFlag.MF_MISSILE;
import static thump.play.MObject.P_SpawnMobj;
import thump.render.Line;
import thump.render.Sector;
import static thump.sound.sfx.Sounds.SfxEnum.sfx_telept;

/**
 *
 * @author mark
 */
public class Teleport {

    //
    // TELEPORTATION
    //
    public static int EV_Teleport(
            Line line,
            int side,
            MapObject thing) 
    {
        
        int i;
        int tag;
        MapObject m;
        MapObject fog;
        long an;
        Thinker thinker;
        Sector sector;
        int oldx;
        int oldy;
        int oldz;

        // don't teleport missiles
        if ((thing.flags & MF_MISSILE.getValue())>0) {
            return 0;
        }

        // Don't teleport if hit back of line,
        //  so you can get out of teleporter.
        if (side == 1) {
            return 0;
        }

        Game game = Game.getInstance();
        
        tag = line.tag;
        for (i = 0; i < game.playerSetup.sectors.length; i++) {
            if (game.playerSetup.sectors[i].tag == tag) {
                //thinker = game.thinkercap.next;
                for (thinker = game.thinkercap.getNextThinker();
                        thinker != game.thinkercap;
                        thinker = thinker.getNextThinker()) {
                    // not a mobj
                    if (!(thinker.getFunction() instanceof T_MobjThinker) ) {
                        continue;
                    }

                    m = (MapObject) thinker;

                    // not a teleportman
                    if (m.type != MT_TELEPORTMAN) {
                        continue;
                    }

                    sector = m.subsector.sector;
                    // wrong sector
                    //if (sector - sectors != i) {
                    if ( sector != game.playerSetup.sectors[i] ) {
                        continue;
                    }

                    oldx = thing.x;
                    oldy = thing.y;
                    oldz = thing.z;

                    if (!Game.getInstance().map.P_TeleportMove(thing, m.x, m.y)) {
                        return 0;
                    }

                    thing.z = thing.floorz;  //fixme: not needed?
                    if (thing.player!=null) {
                        thing.player.viewz = thing.z + thing.player.viewheight;
                    }

                    // spawn teleport fog at source and destination
                    fog = P_SpawnMobj(oldx, oldy, oldz, MT_TFOG);
                    game.sound.S_StartSound(fog, sfx_telept);
                    an = m.angle >> ANGLETOFINESHIFT;
                    fog = P_SpawnMobj(m.x + 20 * finecosine(an), m.y + 20 * finesine(an), thing.z, MT_TFOG);

                    // emit sound, where?
                    game.sound.S_StartSound(fog, sfx_telept);

                    // don't move for a bit
                    if (thing.player!=null) {
                        thing.reactiontime = 18;
                    }

                    thing.angle = m.angle;
                    thing.momx = 0;
                    thing.momy = 0;
                    thing.momz = 0;
                    return 1;
                }
            }
        }
        return 0;
    }


}
