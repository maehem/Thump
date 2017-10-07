/*
    The data sampled per tick (single player)
    and transmitted to other peers (multiplayer).
    Mainly movements/button commands per game tick,
    plus a checksum for internal state consistency.
 */
package thump.game;

/**
 *
 * @author mark
 */
public class TickCommand {
    public int     forwardmove=0;    // *2048 for move
    public int     sidemove=0;       // *2048 for move
    public short    angleturn=0;      // <<16 for angle delta
    public short    consistancy=0;    // checks for net game
    public byte     chatchar=0;
    public byte     buttons=0;
    
    // Not even used!
//    public int getChecksum(TickCommand cmd) {
//        int		i;
//        int		sum = 0; 
//
//        for (i=0 ; i< sizeof(*cmd)/4 - 1 ; i++) 
//            sum += ((int *)cmd)[i]; 
//
//        return sum;         
//    }
}
