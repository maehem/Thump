/*
     A_BFGSpray
     Spawn a BFG explosion on every monster in view
 */
package thump.play.action;

import thump.game.Player;
import thump.maplevel.MapObject;
import thump.play.PSprite;

/**
 *
 * @author mark
 */
public class A_BFGSpray implements Action {

    @Override
    public void doAction(MapObject mo) {
    //    int			i;
    //    int			j;
    //    int			damage;
    //    angle_t		an;
    //	
    //    // offset angles from its attack angle
    //    for (i=0 ; i<40 ; i++)
    //    {
    //	an = mo.angle - ANG90/2 + ANG90/40*i;
    //
    //	// mo.target is the originator (player)
    //	//  of the missile
    //	P_AimLineAttack (mo.target, an, 16*64*FRACUNIT);
    //
    //	if (!linetarget)
    //	    continue;
    //
    //	P_SpawnMobj (linetarget.x,
    //		     linetarget.y,
    //		     linetarget.z + (linetarget.height>>2),
    //		     MT_EXTRABFG);
    //	
    //	damage = 0;
    //	for (j=0;j<15;j++)
    //	    damage += (P_Random()&7) + 1;
    //
    //	P_DamageMobj (linetarget, mo.target,mo.target, damage);
    //    }
    }

    @Override
    public void doAction(Player player, PSprite psp) {}
    
}
