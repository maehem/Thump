/*
 * Source Animation Definition
 */
package thump.game.play;

/**
 *
 * @author mark
 */
public class AnimDef {
    boolean	istexture;	// if false, it is a flat
    String	endname;
    String	startname;
    int		speed;    

    AnimDef(boolean isTexture, String endgame, String startgame, int speed) {
        this.istexture = isTexture;
        this.endname = endgame;
        this.startname = startgame;
        this.speed = speed;
    }
}
