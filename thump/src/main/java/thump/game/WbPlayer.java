/*

     INTERMISSION
     Structure passed e.g. to WI_Start(wb)

 */
package thump.game;

import java.util.Arrays;

/**
 *
 * @author mark
 */
public class WbPlayer {
    public boolean	in;	// whether the player is in game
    
    // Player stats, kills, collected items etc.
    public int		skills;
    public int		sitems;
    public int		ssecret;
    public int		stime; 
    public int		frags[] = new int[4];
    
    public int		score;	// current score on entry, modified on return

    WbPlayer copy() {
        WbPlayer copy = new WbPlayer();
        copy.in = in;
        copy.skills = skills;
        copy.sitems = sitems;
        copy.ssecret = ssecret;
        copy.stime = stime;
        copy.frags = Arrays.copyOf(frags, frags.length);
        copy.score = score;
        
        return copy;
    }
}
