/*
 * Punch Weapon Action
 */
package thump.play.action;

import thump.game.Player;
import thump.maplevel.MapObject;
import thump.play.PSprite;

/**
 *
 * @author mark
 */
public class A_Punch implements Action {

    public A_Punch() {
    }

    @Override
    public void doAction(MapObject mo) {}

    @Override
    public void doAction(Player player, PSprite psp) {
    //    angle_t	angle;
    //    int		damage;
    //    int		slope;
    //	
    //    damage = (P_Random ()%10+1)<<1;
    //
    //    if (player.powers[pw_strength])	
    //	damage *= 10;
    //
    //    angle = player.mo.angle;
    //    angle += (P_Random()-P_Random())<<18;
    //    slope = P_AimLineAttack (player.mo, angle, MELEERANGE);
    //    P_LineAttack (player.mo, angle, MELEERANGE, slope, damage);
    //
    //    // turn to face target
    //    if (linetarget)
    //    {
    //	S_StartSound (player.mo, sfx_punch);
    //	player.mo.angle = R_PointToAngle2 (player.mo.x,
    //					     player.mo.y,
    //					     linetarget.x,
    //					     linetarget.y);
    //    }
    }
    
}
