/*
 * Teleportation
 */
package thump.game.play;

import static thump.base.Tables.ANGLETOFINESHIFT;
import static thump.base.Tables.finecosine;
import static thump.base.Tables.finesine;
import thump.game.Game;
import static thump.game.MobJInfo.Type.MT_TELEPORTMAN;
import static thump.game.MobJInfo.Type.MT_TFOG;
import thump.game.maplevel.MapObject;
import static thump.game.play.MObject.P_SpawnMobj;
import static thump.game.sound.sfx.Sounds.SfxEnum.sfx_telept;
import thump.game.thinkeraction.T_MobjThinker;
import static thump.wad.map.Degenmobj.MobileObjectFlag.MF_MISSILE;
import thump.wad.map.Line;
import thump.wad.map.Sector;
import thump.wad.map.Thinker;

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
        //for (i = 0; i < game.playerSetup.sectors.length; i++) {
        for (i = 0; i < game.playerSetup.sectors.size(); i++) {
            //if (game.playerSetup.sectors[i].tag == tag) {
            if (game.playerSetup.sectors.get(i).sector.tag == tag) {
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

                    sector = m.subsector.mapSector.sector;
                    // wrong sector
                    //if (sector - sectors != i) {
                    //if ( sector != game.playerSetup.sectors[i] ) {
                    if ( sector != game.playerSetup.sectors.get(i).sector ) {
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
