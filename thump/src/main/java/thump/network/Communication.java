/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.network;

/**
 *
 * @author mark
 */
public class Communication {
    public long		id;             // Supposed to be DOOMCOM_ID?
    
    public short	intnum;         // DOOM executes an int to execute commands.
	
    // Communication between DOOM and the driver.
    public short	command;        // Is CMD_SEND or CMD_GET.
    
    public short	remotenode;     // Is dest for send, set by get (-1 = no packet).
    public short	datalength;     // Number of bytes in doomdata to be sent

    // Info common to all nodes.
    // Console is allways node 0.
    public short	numnodes=0;
    public short	ticdup=0;         // Flag: 1 = no duplication, 2-5 = dup for slow nets.
    public short	extratics;      // Flag: 1 = send a backup tic in every packet.
    public short	deathmatch;     // Flag: 1 = deathmatch.
    public short	savegame;       // Flag: -1 = new game, 0-5 = load savegame
    public short	episode;	// 1-3
    public short	map;		// 1-9
    public short	skill;		// 1-5
    public short	consoleplayer;  // Info specific to this node.
    public short	numplayers;
    
    // These are related to the 3-display mode,
    //  in which two drones looking left and right
    //  were used to render two additional views
    //  on two additional computers.
    // Probably not operational anymore.
    // 1 = left, 0 = center, -1 = right
    public short	angleoffset;
    
    public short	drone;          // 1 = drone

    // The packet data to be sent.
    public DataPacket	data = new DataPacket();

    private static Communication instance = null;
    
    private Communication() {
    }
    
    public static Communication getInstance() {
        if ( instance == null ) {
            instance = new Communication();
        }
        
        return instance;
        
    }
    
        
}
