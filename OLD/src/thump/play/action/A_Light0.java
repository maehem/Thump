/*
 * Player Extra Light 0 Action
 */
package thump.play.action;

import thump.game.Player;
import thump.maplevel.MapObject;
import thump.play.PSprite;

/**
 *
 * @author mark
 */
public class A_Light0 implements Action {

    @Override
    public void doAction(MapObject mo) {}

    @Override
    public void doAction(Player player, PSprite psp) {
        player.extralight = 0;
    }
    
}
