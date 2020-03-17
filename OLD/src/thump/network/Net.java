/*
      Network play related stuff.
      There is a data struct that stores network
      doomcom related stuff, and another
      one that defines the actual packets to
      be transmitted.

 */
package thump.network;

import java.util.logging.Level;
import static thump.game.Event.BT_SPECIAL;
import thump.game.Game;
import thump.game.Stats;
import thump.game.TickCommand;
import thump.global.Defines;
import static thump.global.Defines.MAXPLAYERS;
import thump.global.SystemInterface;
import thump.global.VideoInterface;
import thump.menu.MenuManager;

/**
 *
 * @author mark
 */
public class Net {

    public static final long DOOMCOM_ID = 0x12345678l;

    // Max computers/players in a game.
    public static final int MAXNETNODES = 8;

    // Networking and tick handling related.
    public static final int BACKUPTICS = 12;

    public enum Command {
        CMD_SEND(1),
        CMD_GET(2);

        private final int value;

        private Command(int value ) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    
    public static final int NCMD_EXIT       = 0x80000000;
    public static final int NCMD_RETRANSMIT = 0x40000000;
    public static final int NCMD_SETUP      = 0x20000000;
    public static final int NCMD_KILL       = 0x10000000;	// kill game
    public static final int NCMD_CHECKSUM   = 0x0fffffff;

 
    public Communication   doomcom;
    DataPacket      netbuffer;		// points inside doomcom

    //
    // NETWORKING
    //
    // gametic is the tic about to (or currently being) run
    // maketic is the tick that hasn't had control made for it yet
    // nettics[] has the maketics for all players 
    //
    // a gametic cannot be run until nettics[] > gametic for all players
    //
    public static final int	RESENDCOUNT=	10;
    public static final int	PL_DRONE=	0x80;	// bit flag in doomdata.player

    public TickCommand	localcmds[] = new TickCommand[BACKUPTICS];

    public TickCommand        netcmds[][] = new TickCommand[MAXPLAYERS][BACKUPTICS];
    
    int     nettics[]       = new int[MAXNETNODES];
    boolean nodeingame[]    = new boolean[MAXNETNODES];	// set false as nodes leave game
    boolean remoteresend[]  = new boolean[MAXNETNODES];	// set when local needs tics
    int     resendto[]      = new int[MAXNETNODES];     // set when remote needs tics
    int     resendcount[]   = new int[MAXNETNODES];

    int     nodeforplayer[] = new int[MAXPLAYERS];

    public int     maketic;
    public int     lastnettic;
    public int     skiptics;
    public int     ticdup=1;
    public int     maxsend;	// BACKUPTICS/(2*ticdup)-1



    boolean     reboundpacket;
    DataPacket  reboundstore;
    
    NetInterface netInterface = new NetInterface();

    public Net() {
        for (int i=0; i<localcmds.length; i++ ) {
            localcmds[i]=new TickCommand();
        }

    }




    /*
    //
    //
    //
    int NetbufferSize (void)
    {
//        return (int)&(((doomdata_t *)0).cmds[netbuffer.numtics]); 
    }

    //
    // Checksum 
    //
    unsigned NetbufferChecksum (void)
    {
        unsigned		c;
        int		i,l;

        c = 0x1234567;

        // FIXME -endianess?
    #ifdef NORMALUNIX
        return 0;			// byte order problems
    #endif

        l = (NetbufferSize () - (int)&(((doomdata_t *)0).retransmitfrom))/4;
        for (i=0 ; i<l ; i++)
            c += ((unsigned *)&netbuffer.retransmitfrom)[i] * (i+1);

        return c & NCMD_CHECKSUM;
    }

    //
    //
    //
    int ExpandTics (int low)
    {
        int	delta;

        delta = low - (maketic&0xff);

        if (delta >= -64 && delta <= 64)
            return (maketic&~0xff) + low;
        if (delta > 64)
            return (maketic&~0xff) - 256 + low;
        if (delta < -64)
            return (maketic&~0xff) + 256 + low;

        I_Error ("ExpandTics: strange value %i at maketic %i",low,maketic);
        return 0;
    }



    //
    // HSendPacket
    //
    void
    HSendPacket
     (int	node,
      int	flags )
    {
        netbuffer.checksum = NetbufferChecksum () | flags;

        if (!node)
        {
            reboundstore = *netbuffer;
            reboundpacket = true;
            return;
        }

        if (demoplayback)
            return;

        if (!netgame)
            I_Error ("Tried to transmit to another node");

        doomcom.command = CMD_SEND;
        doomcom.remotenode = node;
        doomcom.datalength = NetbufferSize ();

        if (debugfile)
        {
            int		i;
            int		realretrans;
            if (netbuffer.checksum & NCMD_RETRANSMIT)
                realretrans = ExpandTics (netbuffer.retransmitfrom);
            else
                realretrans = -1;

            fprintf (debugfile,"send (%i + %i, R %i) [%i] ",
                     ExpandTics(netbuffer.starttic),
                     netbuffer.numtics, realretrans, doomcom.datalength);

            for (i=0 ; i<doomcom.datalength ; i++)
                fprintf (debugfile,"%i ",((byte *)netbuffer)[i]);

            fprintf (debugfile,"\n");
        }

        I_NetCmd ();
    }

    //
    // HGetPacket
    // Returns false if no packet is waiting
    //
    boolean HGetPacket (void)
    {	
        if (reboundpacket)
        {
            *netbuffer = reboundstore;
            doomcom.remotenode = 0;
            reboundpacket = false;
            return true;
        }

        if (!netgame)
            return false;

        if (demoplayback)
            return false;

        doomcom.command = CMD_GET;
        I_NetCmd ();

        if (doomcom.remotenode == -1)
            return false;

        if (doomcom.datalength != NetbufferSize ())
        {
            if (debugfile)
                fprintf (debugfile,"bad packet length %i\n",doomcom.datalength);
            return false;
        }

        if (NetbufferChecksum () != (netbuffer.checksum&NCMD_CHECKSUM) )
        {
            if (debugfile)
                fprintf (debugfile,"bad packet checksum\n");
            return false;
        }

        if (debugfile)
        {
            int		realretrans;
            int	i;

            if (netbuffer.checksum & NCMD_SETUP)
                fprintf (debugfile,"setup packet\n");
            else
            {
                if (netbuffer.checksum & NCMD_RETRANSMIT)
                    realretrans = ExpandTics (netbuffer.retransmitfrom);
                else
                    realretrans = -1;

                fprintf (debugfile,"get %i = (%i + %i, R %i)[%i] ",
                         doomcom.remotenode,
                         ExpandTics(netbuffer.starttic),
                         netbuffer.numtics, realretrans, doomcom.datalength);

                for (i=0 ; i<doomcom.datalength ; i++)
                    fprintf (debugfile,"%i ",((byte *)netbuffer)[i]);
                fprintf (debugfile,"\n");
            }
        }
        return true;	
    }


    //
    // GetPackets
    //
    char    exitmsg[80];

    void GetPackets (void)
    {
        int		netconsole;
        int		netnode;
        ticcmd_t	*src, *dest;
        int		realend;
        int		realstart;

        while ( HGetPacket() )
        {
            if (netbuffer.checksum & NCMD_SETUP)
                continue;		// extra setup packet

            netconsole = netbuffer.player & ~PL_DRONE;
            netnode = doomcom.remotenode;

            // to save bytes, only the low byte of tic numbers are sent
            // Figure out what the rest of the bytes are
            realstart = ExpandTics (netbuffer.starttic);		
            realend = (realstart+netbuffer.numtics);

            // check for exiting the game
            if (netbuffer.checksum & NCMD_EXIT)
            {
                if (!nodeingame[netnode])
                    continue;
                nodeingame[netnode] = false;
                playeringame[netconsole] = false;
                strcpy (exitmsg, "Player 1 left the game");
                exitmsg[7] += netconsole;
                players[consoleplayer].message = exitmsg;
                if (demorecording)
                    game.G_CheckDemoStatus ();
                continue;
            }

            // check for a remote game kill
            if (netbuffer.checksum & NCMD_KILL)
                I_Error ("Killed by network driver");

            nodeforplayer[netconsole] = netnode;

            // check for retransmit request
            if ( resendcount[netnode] <= 0 
                 && (netbuffer.checksum & NCMD_RETRANSMIT) )
            {
                resendto[netnode] = ExpandTics(netbuffer.retransmitfrom);
                if (debugfile)
                    fprintf (debugfile,"retransmit from %i\n", resendto[netnode]);
                resendcount[netnode] = RESENDCOUNT;
            }
            else
                resendcount[netnode]--;

            // check for out of order / duplicated packet		
            if (realend == nettics[netnode])
                continue;

            if (realend < nettics[netnode])
            {
                if (debugfile)
                    fprintf (debugfile,
                             "out of order packet (%i + %i)\n" ,
                             realstart,netbuffer.numtics);
                continue;
            }

            // check for a missed packet
            if (realstart > nettics[netnode])
            {
                // stop processing until the other system resends the missed tics
                if (debugfile)
                    fprintf (debugfile,
                             "missed tics from %i (%i - %i)\n",
                             netnode, realstart, nettics[netnode]);
                remoteresend[netnode] = true;
                continue;
            }

            // update command store from the packet
            {
                int		start;

                remoteresend[netnode] = false;

                start = nettics[netnode] - realstart;		
                src = &netbuffer.cmds[start];

                while (nettics[netnode] < realend)
                {
                    dest = &netcmds[netconsole][nettics[netnode]%BACKUPTICS];
                    nettics[netnode]++;
                    *dest = *src;
                    src++;
                }
            }
        }
    }


*/
    //
    // NetUpdate
    // Builds ticcmds for console player,
    // sends out a packet
    //
    long      gametime;
    
    public void NetUpdate () {
        long    nowtime;
        long    newtics;
        int     i, j;
        int     realstart;
        int     gameticdiv;

        Game game = Game.getInstance();
        
        // check time
        nowtime = SystemInterface.getInstance().I_GetTime ()/ticdup;
        newtics = nowtime - gametime;
        gametime = nowtime;

        if (newtics > 0) { 	// nothing new to update
            //goto listen; 

            if (skiptics <= newtics) {
                newtics -= skiptics;
                skiptics = 0;
            } else {
                skiptics -= newtics;
                newtics = 0;
            }

            netbuffer.player = (byte) game.consoleplayer;

            // build new ticcmds for console player
            gameticdiv = game.gametic/ticdup;
            for (i=0 ; i<newtics ; i++) {
                VideoInterface.getInstance().I_StartTic ();
                //Defines.logger.config("NetUpdate do D_ProcessEvents\n");

                game.doomMain.D_ProcessEvents ();
                if (maketic - gameticdiv >= BACKUPTICS/2-1) {
                    break;          // can't hold any more
                }

                //printf ("mk:%i ",maketic);
                game.G_BuildTiccmd (localcmds,maketic%BACKUPTICS);
                maketic++;
            }


            if (Game.getInstance().doomMain.singletics) {
                return;         // singletic update is syncronous
            }

            // send the packet to the other nodes
            for (i=0 ; i<doomcom.numnodes ; i++) {
                if (nodeingame[i])
                {
                    netbuffer.starttic = (byte) resendto[i];
                    realstart = resendto[i];
                    
                    netbuffer.numtics = (byte) (maketic - realstart);
                    if (netbuffer.numtics > BACKUPTICS) {
                        SystemInterface.I_Error ("NetUpdate: netbuffer.numtics > BACKUPTICS");
                    }

                    resendto[i] = maketic - doomcom.extratics;

                    for (j=0 ; j< netbuffer.numtics ; j++) {
                        netbuffer.cmds[j] =
                                localcmds[(realstart+j)%BACKUPTICS];
                    }

                    if (remoteresend[i])
                    {
                        netbuffer.retransmitfrom = (byte) nettics[i];
//TODO                        HSendPacket (i, NCMD_RETRANSMIT);
                    }
                    else
                    {
                        netbuffer.retransmitfrom = 0;
//TODO                        HSendPacket (i, 0);
                    }
                }

                // listen for other packets
            }
        }
      //listen:
//TODO        GetPackets ();
    }

/*

    //
    // CheckAbort
    //
    void CheckAbort (void)
    {
        event_t *ev;
        int		stoptic;

        stoptic = I_GetTime () + 2; 
        while (I_GetTime() < stoptic) 
            I_StartTic (); 

        I_StartTic ();
        for ( ; eventtail != eventhead 
                  ; eventtail = (++eventtail)&(MAXEVENTS-1) ) 
        { 
            ev = &events[eventtail]; 
            if (ev.type == ev_keydown && ev.data1 == KEY_ESCAPE)
                I_Error ("Network game synchronization aborted.");
        } 
    }

*/

    //
    // D_ArbitrateNetStart
    //
    void D_ArbitrateNetStart () {
//        int		i;
//        boolean	gotinfo[MAXNETNODES];
//
//        autostart = true;
//        memset (gotinfo,0,sizeof(gotinfo));
//
//        if (doomcom.consoleplayer)
//        {
//            // listen for setup info from key player
//            printf ("listening for network start info...\n");
//            while (1)
//            {
//                CheckAbort ();
//                if (!HGetPacket ())
//                    continue;
//                if (netbuffer.checksum & NCMD_SETUP)
//                {
//                    if (netbuffer.player != VERSION)
//                        I_Error ("Different DOOM versions cannot play a net game!");
//                    startskill = netbuffer.retransmitfrom & 15;
//                    deathmatch = (netbuffer.retransmitfrom & 0xc0) >> 6;
//                    nomonsters = (netbuffer.retransmitfrom & 0x20) > 0;
//                    respawnparm = (netbuffer.retransmitfrom & 0x10) > 0;
//                    startmap = netbuffer.starttic & 0x3f;
//                    startepisode = netbuffer.starttic >> 6;
//                    return;
//                }
//            }
//        }
//        else
//        {
//            // key player, send the setup info
//            printf ("sending network start info...\n");
//            do
//            {
//                CheckAbort ();
//                for (i=0 ; i<doomcom.numnodes ; i++)
//                {
//                    netbuffer.retransmitfrom = startskill;
//                    if (deathmatch)
//                        netbuffer.retransmitfrom |= (deathmatch<<6);
//                    if (nomonsters)
//                        netbuffer.retransmitfrom |= 0x20;
//                    if (respawnparm)
//                        netbuffer.retransmitfrom |= 0x10;
//                    netbuffer.starttic = startepisode * 64 + startmap;
//                    netbuffer.player = VERSION;
//                    netbuffer.numtics = 0;
//                    HSendPacket (i, NCMD_SETUP);
//                }
//
//    #if 1
//                for(i = 10 ; i  &&  HGetPacket(); --i)
//                {
//                    if((netbuffer.player&0x7f) < MAXNETNODES)
//                        gotinfo[netbuffer.player&0x7f] = true;
//                }
//    #else
//                while (HGetPacket ())
//                {
//                    gotinfo[netbuffer.player&0x7f] = true;
//                }
//    #endif
//
//                for (i=1 ; i<doomcom.numnodes ; i++)
//                    if (!gotinfo[i])
//                        break;
//            } while (i < doomcom.numnodes);
//        }
    }
    
    //
    // D_CheckNetGame
    // Works out player numbers among the net participants
    //
    //extern	int			viewangleoffset;

    public void D_CheckNetGame () {
        int             i;

        for (i=0 ; i<MAXNETNODES ; i++)
        {
            nodeingame[i] = false;
            nettics[i] = 0;
            remoteresend[i] = false;	// set when local needs tics
            resendto[i] = 0;		// which tic to start sending
        }

        // I_InitNetwork sets doomcom and netgame
        netInterface.I_InitNetwork ();
        if (doomcom.id != DOOMCOM_ID) {
            SystemInterface.I_Error ("Doomcom buffer invalid!");
        }

        Game game = Game.getInstance();
        Stats stats = Stats.getInstance();
        
        netbuffer = doomcom.data;
        game.consoleplayer = doomcom.consoleplayer;
        game.displayplayer = doomcom.consoleplayer;
        
        if (game.netgame) {
            D_ArbitrateNetStart ();
        }

        Defines.logger.log(Level.CONFIG, "startskill {0}  deathmatch: {1}  startmap: {2}  startepisode: {3}\n",
                new Object[]{game.doomMain.startskill, game.deathmatch, game.doomMain.startmap, game.doomMain.startepisode} );

        // read values out of doomcom
        ticdup = doomcom.ticdup;
        maxsend = BACKUPTICS/(2*ticdup)-1;
        if (maxsend<1) {
            maxsend = 1;
        }

        for (i=0 ; i<doomcom.numplayers ; i++) {
            game.playeringame[i] = true;
        }
        for (i=0 ; i<doomcom.numnodes ; i++) {
            nodeingame[i] = true;
        }

        Defines.logger.log(Level.CONFIG, "player {0} of {1} ({2} nodes)\n",
                new Object[] {game.consoleplayer+1, doomcom.numplayers, doomcom.numnodes}
        );

    }

    //
    // D_QuitNetGame
    // Called before quitting to leave a net game
    // without hanging the other players
    //
    public void D_QuitNetGame() {
/*
        int             i, j;

        if (debugfile)
            fclose (debugfile);

        if (!netgame || !usergame || consoleplayer == -1 || demoplayback)
            return;

        // send a bunch of packets for security
        netbuffer.player = consoleplayer;
        netbuffer.numtics = 0;
        for (i=0 ; i<4 ; i++)
        {
            for (j=1 ; j<doomcom.numnodes ; j++)
                if (nodeingame[j])
                    HSendPacket (j, NCMD_EXIT);
            I_WaitVBL (1);
        }
*/
    }


    
    //
    // TryRunTics
    //
    int	frametics[] = new int[4];
    int	frameon;
    boolean	frameskip[] = new boolean[4];
    int	oldnettics;
    long	oldentertics;

    //extern	boolean	advancedemo;

    public void TryRunTics () {
        int		i;
        int		lowtic;
        long		entertic;
        long		realtics;
        int		availabletics;
        int		counts;
        int		numplaying;

        // get real tics		
        entertic = SystemInterface.getInstance().I_GetTime ()/ticdup;
        realtics = entertic - oldentertics;
        oldentertics = entertic;

        // get available tics
        NetUpdate ();

        lowtic = Integer.MAX_VALUE;
        numplaying = 0;
        for (i=0 ; i<doomcom.numnodes ; i++)
        {
            if (nodeingame[i])
            {
                numplaying++;
                if (nettics[i] < lowtic) {
                    lowtic = nettics[i];
                }
            }
        }
        availabletics = lowtic - Game.getInstance().gametic/ticdup;

        // decide how many tics to run
        if (realtics < availabletics-1) {
            counts = (int) (realtics+1);
        } else if (realtics < availabletics) {
            counts = (int) realtics;
        } else {
            counts = availabletics;
        }

        if (counts < 1) {
            counts = 1;
        }

        frameon++;

//        if (debugfile)
//            fprintf (debugfile,
//                     "=======real: %i  avail: %i  game: %i\n",
//                     realtics, availabletics,counts);

        if (!Game.getInstance().demoplayback)
        {	
            // ideally nettics[0] should be 1 - 3 tics above lowtic
            // if we are consistantly slower, speed up time
            for (i=0 ; i<MAXPLAYERS ; i++) {
                if (Game.getInstance().playeringame[i]) {
                    break;
                }
            }
            if (Game.getInstance().consoleplayer == i) {
                // the key player does not adapt
            } else {
                if (nettics[0] <= nettics[nodeforplayer[i]]) {
                    gametime--;
                    // printf ("-");
                }
                frameskip[frameon&3] = (oldnettics > nettics[nodeforplayer[i]]);
                oldnettics = nettics[0];
                if (frameskip[0] && frameskip[1] && frameskip[2] && frameskip[3]) {
                    skiptics = 1;
                    // printf ("+");
                }
            }
        }// demoplayback

        // wait for new tics if needed
        while (lowtic < Game.getInstance().gametic/ticdup + counts)	
        {
            NetUpdate ();   
            lowtic = Integer.MAX_VALUE;

            for (i=0 ; i<doomcom.numnodes ; i++) {
                if (nodeingame[i] && nettics[i] < lowtic) {
                    lowtic = nettics[i];
                }
            }

            if (lowtic < Game.getInstance().gametic/ticdup) {
                SystemInterface.I_Error ("TryRunTics: lowtic < gametic");
            }

            // don't stay in here forever -- give the menu a chance to work
            if (SystemInterface.getInstance().I_GetTime ()/ticdup - entertic >= 20)
            {
                MenuManager.getInstance().M_Ticker ();
                return;
            } 
        }

        // run the count * ticdup dics
        while (counts>0) {
            counts--;
            for (i=0 ; i<ticdup ; i++) {
                if (Game.getInstance().gametic/ticdup > lowtic) {
                    SystemInterface.I_Error ("gametic>lowtic");
                }
                if (Game.getInstance().doomMain.advancedemo) {
                    Game.getInstance().doomMain.D_DoAdvanceDemo ();
                }
                MenuManager.getInstance().M_Ticker();
                Game.getInstance().G_Ticker();
                Game.getInstance().gametic++;

                // modify command for duplicated tics
                if (i != ticdup-1)
                {
                    TickCommand cmd;
                    int         buf;
                    int         j;

                    buf = (Game.getInstance().gametic/ticdup)%BACKUPTICS; 
                    for (j=0 ; j<MAXPLAYERS ; j++) {
                        cmd = netcmds[j][buf];
                        cmd.chatchar = 0;
                        if ((cmd.buttons & BT_SPECIAL)>0) {
                            cmd.buttons = 0;
                        }
                    }
                }
            }
            NetUpdate ();	// check for new console commands
        }
    }
    

}
