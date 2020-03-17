/*

    Network packet data.

 */
package thump.network;

import thump.game.TickCommand;
import static thump.network.Net.BACKUPTICS;

/**
 *
 * @author mark
 */
public class DataPacket {
    // High bit is retransmit request.
    int                 checksum;
    // Only valid if NCMD_RETRANSMIT.
    byte		retransmitfrom;
    
    byte		starttic;
    byte		player;
    byte		numtics;
    TickCommand		cmds[] = new TickCommand[BACKUPTICS];
    
}
