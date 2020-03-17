/*
 * Saw Weapon Action
 */
package thump.play.action;

import thump.game.Player;
import thump.maplevel.MapObject;
import thump.play.PSprite;

/**
 *
 * @author mark
 */
public class A_Saw implements Action {

    @Override
    public void doAction(MapObject mo) {}

    @Override
    public void doAction(Player player, PSprite psp) {
    //    angle_t	angle;
    //    int		damage;
    //    int		slope;
    //
    //    damage = 2*(P_Random ()%10+1);
    //    angle = player.mo.angle;
    //    angle += (P_Random()-P_Random())<<18;
    //    
    //    // use meleerange + 1 se the puff doesn't skip the flash
    //    slope = P_AimLineAttack (player.mo, angle, MELEERANGE+1);
    //    P_LineAttack (player.mo, angle, MELEERANGE+1, slope, damage);
    //
    //    if (!linetarget)
    //    {
    //	S_StartSound (player.mo, sfx_sawful);
    //	return;
    //    }
    //    S_StartSound (player.mo, sfx_sawhit);
    //	
    //    // turn to face target
    //    angle = R_PointToAngle2 (player.mo.x, player.mo.y,
    //			     linetarget.x, linetarget.y);
    //    if (angle - player.mo.angle > ANG180)
    //    {
    //	if (angle - player.mo.angle < -ANG90/20)
    //	    player.mo.angle = angle + ANG90/21;
    //	else
    //	    player.mo.angle -= ANG90/20;
    //    }
    //    else
    //    {
    //	if (angle - player.mo.angle > ANG90/20)
    //	    player.mo.angle = angle - ANG90/21;
    //	else
    //	    player.mo.angle += ANG90/20;
    //    }
    //    player.mo.flags |= MF_JUSTATTACKED;
    }
    
}
