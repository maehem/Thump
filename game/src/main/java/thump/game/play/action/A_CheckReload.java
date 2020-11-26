/*
 * Player Check Reload Action
 */
package thump.game.play.action;

import thump.game.Player;
import thump.game.maplevel.MapObject;
import thump.game.play.PSprite;
import static thump.game.play.PSprite.P_CheckAmmo;

/**
 *
 * @author mark
 */
public class A_CheckReload implements Action {

    public A_CheckReload() {
    }

    @Override
    public void doAction(MapObject mo) {}

    @Override
    public void doAction(Player player, PSprite psp) {
        P_CheckAmmo (player);
    }
    
}
