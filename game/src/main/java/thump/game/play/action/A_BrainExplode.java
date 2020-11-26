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
public class A_BrainExplode implements Action {

    public A_BrainExplode() {
    }

    @Override
    public void doAction(MapObject mo) {
//        int x;
//        int y;
//        int z;
//        MapObject th;
//
//        x = mo.x + (Random.getInstance().P_Random() - Random.getInstance().P_Random()) * 2048;
//        y = mo.y;
//        z = 128 + Random.getInstance().P_Random() * 2 * FRACUNIT;
//        th = MObject.P_SpawnMobj(x, y, z, MT_ROCKET);
//        th.momz = Random.getInstance().P_Random() * 512;
//
//        MObject.P_SetMobjState(th, S_BRAINEXPLODE1);
//
//        th.tics -= Random.getInstance().P_Random() & 7;
//        if (th.tics < 1) {
//            th.tics = 1;
//        }
    }

    @Override
    public void doAction(Player player, PSprite psp) {}
    
}
