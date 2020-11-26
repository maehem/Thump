/*
 * Automap
 */
package thump.game.automap;

import java.util.Arrays;
import java.util.logging.Level;
import thump.base.Defines;
import static thump.base.Defines.SCREENHEIGHT;
import static thump.base.Defines.SCREENWIDTH;
import thump.base.FixedPoint;
import static thump.base.FixedPoint.FRACBITS;
import static thump.base.FixedPoint.FRACUNIT;
import static thump.base.Tables.ANGLETOFINESHIFT;
import static thump.base.Tables.finecosine;
import static thump.base.Tables.finesine;
import thump.game.Cheat;
import static thump.game.Defines.KEY_DOWNARROW;
import static thump.game.Defines.KEY_LEFTARROW;
import static thump.game.Defines.KEY_RIGHTARROW;
import static thump.game.Defines.KEY_TAB;
import static thump.game.Defines.KEY_UPARROW;
import static thump.game.Defines.MAXPLAYERS;
import static thump.game.Defines.PowerType.pw_allmap;
import static thump.game.Defines.PowerType.pw_invisibility;
import thump.game.Event;
import static thump.game.Event.EventType.ev_keydown;
import static thump.game.Event.EventType.ev_keyup;
import thump.game.Game;
import thump.game.Player;
import thump.game.PlayerSetup;
import thump.game.maplevel.MapSector;
import static thump.game.play.Local.MAPBLOCKUNITS;
import static thump.game.play.Local.PLAYERRADIUS;
import thump.render.Screen;
import thump.wad.map.Degenmobj;
import thump.wad.map.Line;
import static thump.wad.map.Line.ML_DONTDRAW;
import static thump.wad.map.Line.ML_MAPPED;
import static thump.wad.map.Line.ML_SECRET;
import thump.wad.mapraw.PatchData;

/**
 *
 * @author mark
 */
public class AutoMap {

    private final Game game;

    public AutoMap(Game game) {
        this.game = game;
        
        for ( int i=0; i<markpoints.length; i++ ) {
            markpoints[i]=new Mpoint();
        }
    }
    
    
    // Used by ST StatusBar stuff.
    public static final int AM_MSGHEADER = (('a' << 24) + ('m' << 16));
    public static final int AM_MSGENTERED = (AM_MSGHEADER | ('e' << 8));
    public static final int AM_MSGEXITED = (AM_MSGHEADER | ('x' << 8));

    // For use if I do walls with outsides/insides
    public static final int REDS        = (256 - 5 * 16);
    public static final int REDRANGE    = 16;
    public static final int BLUES       = (256 - 4 * 16 + 8);
    public static final int BLUERANGE   = 8;
    public static final int GREENS      = (7 * 16);
    public static final int GREENRANGE  = 16;
    public static final int GRAYS       = (6 * 16);
    public static final int GRAYSRANGE  = 16;
    public static final int BROWNS      = (4 * 16);
    public static final int BROWNRANGE  = 16;
    public static final int YELLOWS     = (256 - 32 + 7);
    public static final int YELLOWRANGE = 1;
    public static final int BLACK       = 0;
    public static final int WHITE       = (256 - 47);

    // Automap colors
    public static final int BACKGROUND      = BLACK;
    public static final int YOURCOLORS      = WHITE;
    public static final int YOURRANGE       = 0;
    public static final int WALLCOLORS      = REDS;
    public static final int WALLRANGE       = REDRANGE;
    public static final int TSWALLCOLORS    = GRAYS;
    public static final int TSWALLRANGE     = GRAYSRANGE;
    public static final int FDWALLCOLORS    = BROWNS;
    public static final int FDWALLRANGE     = BROWNRANGE;
    public static final int CDWALLCOLORS    = YELLOWS;
    public static final int CDWALLRANGE     = YELLOWRANGE;
    public static final int THINGCOLORS     = GREENS;
    public static final int THINGRANGE      = GREENRANGE;
    public static final int SECRETWALLCOLORS = WALLCOLORS;
    public static final int SECRETWALLRANGE = WALLRANGE;
    public static final int GRIDCOLORS      = (GRAYS + GRAYSRANGE / 2);
    public static final int GRIDRANGE       = 0;
    public static final int XHAIRCOLORS     = GRAYS;

    // drawing stuff
    public static final int FB              = 0;

    public static final int AM_PANDOWNKEY   = KEY_DOWNARROW;
    public static final int AM_PANUPKEY     = KEY_UPARROW;
    public static final int AM_PANRIGHTKEY  = KEY_RIGHTARROW;
    public static final int AM_PANLEFTKEY   = KEY_LEFTARROW;
    public static final int AM_ZOOMINKEY    = '=';
    public static final int AM_ZOOMOUTKEY   = '-';
    public static final int AM_STARTKEY     = KEY_TAB;
    public static final int AM_ENDKEY       = KEY_TAB;
    public static final int AM_GOBIGKEY     = '0';
    public static final int AM_FOLLOWKEY    = 'f';
    public static final int AM_GRIDKEY      = 'g';
    public static final int AM_MARKKEY      = 'm';
    public static final int AM_CLEARMARKKEY = 'c';

    public static final int AM_NUMMARKPOINTS = 10;

    // scale on entry
    public static final int INITSCALEMTOF= (int) (.2*FRACUNIT);
    // how much the automap moves window per tic in frame-buffer coordinates
    // moves 140 pixels in 1 second
    public static final int F_PANINC=	4;
    // how much zoom-in per tic
    // goes to 2x in 1 second
    public static final int M_ZOOMIN =       ((int) (1.02*FRACUNIT));
    // how much zoom-out per tic
    // pulls out to 0.5x in 1 second
    public static final int M_ZOOMOUT=       ((int) (FRACUNIT/1.02));

    // translates between frame-buffer and map distances
    //public static final int FTOM(x)= FixedPoint.mul(((x)<<16),scale_ftom)
    public int FTOM( int x ) {
        return FixedPoint.mul(((x)<<16),scale_ftom);
    }
    
    //public static final int MTOF(x)= (FixedPoint.mul((x),scale_mtof)>>16)
    public int MTOF( int x ) {
        return FixedPoint.mul((x),scale_mtof)>>16;
    }
    
    
    // translates between frame-buffer and map coordinates
    //public static final int CXMTOF(x)=  (f_x + MTOF((x)-m_x))
    public int CXMTOF(int x) {
        return (f_x + MTOF((x)-m_x));
    }
    
    //public static final int CYMTOF(y)=  (f_y + (f_h - MTOF((y)-m_y)))
    public int CYMTOF(int y) {
        return (f_y + (f_h - MTOF((y)-m_y)));
    }

    // the following is crap
    public static final int LINE_NEVERSEE= ML_DONTDRAW;

//    typedef struct
//    {
//        int x, y;
//    } fpoint_t;
//
//    typedef struct
//    {
//        fpoint_t a, b;
//    } Fline;
//
//    typedef struct
//    {
//        int		x,y;
//    } mpoint_t;
//
//    typedef struct
//    {
//        mpoint_t a, b;
//    } Mline;
//
//    typedef struct
//    {
//        int slp, islp;
//    } islope_t;



    //
    // The vector graphics for the automap.
    //  A line drawing of the player pointing right,
    //   starting from the middle.
    //
    public static final int R = ((8*PLAYERRADIUS)/7);
    
    private static final Mline player_arrow[] = new Mline[]{
        new Mline(-R + R / 8,     0,  R,          0),     // -----
        new Mline( R,             0,  R - R / 2,  R / 4), // ----.
        new Mline( R,             0,  R - R / 2, -R / 4),
        new Mline(-R + R / 8,     0, -R - R / 8,  R / 4), // >---.
        new Mline(-R + R / 8,     0, -R - R / 8, -R / 4),
        new Mline(-R + 3 * R / 8, 0, -R + R / 8,  R / 4), // >>--.
        new Mline(-R + 3 * R / 8, 0, -R + R / 8, -R / 4)
    };
    //#undef R
    
    public static final int NUMPLYRLINES = player_arrow.length;

    //public static final int R = ((8*PLAYERRADIUS)/7)
    private static final Mline cheat_player_arrow[] = new Mline[]{
        new Mline(-R + R / 8,      0,                R,               0    ), // -----
        new Mline(R,               0,                R - R / 2,       R / 6), // ----.
        new Mline(R,               0,                R - R / 2,      -R / 6),
        new Mline(-R + R / 8,      0,               -R - R / 8,       R / 6), // >----.
        new Mline(-R + R / 8,      0,               -R - R / 8,      -R / 6),
        new Mline(-R + 3 * R / 8,  0,               -R + R / 8,       R / 6), // >>----.
        new Mline(-R + 3 * R / 8,  0,               -R + R / 8,      -R / 6),
        new Mline(-R / 2,          0,               -R / 2,          -R / 6), // >>-d--.
        new Mline(-R / 2,         -R / 6,           -R / 2 + R / 6,  -R / 6),
        new Mline(-R / 2 + R / 6, -R / 6,           -R / 2 + R / 6,   R / 4),
        new Mline(-R / 6,          0,               -R / 6,          -R / 6), // >>-dd-.
        new Mline(-R / 6,         -R / 6,            0,              -R / 6),
        new Mline(0,              -R / 6,            0,               R / 4),
        new Mline(R / 6,           R / 4,            R / 6,          -R / 7), // >>-ddt.
        new Mline(R / 6,          -R / 7,            R / 6 + R / 32, -R / 7 - R / 32),
        new Mline(R / 6 + R / 32, -R / 7 - R / 32,   R / 6 + R / 10, -R / 7)
    };
    public static final int NUMCHEATPLYRLINES = cheat_player_arrow.length;

    public static final int F = (FRACUNIT);
    
    private static final Mline triangle_guy[] = {
        new Mline( (int)(-.867*F), (int)(-.5*F) ,  (int)(.867*F), (int)(-.5*F)),
        new Mline( (int)( .867*F), (int)(-.5*F) ,              0,           F ),
        new Mline(              0,           F  , (int)(-.867*F), (int)(-.5*F))
    };
    public static final int NUMTRIANGLEGUYLINES = triangle_guy.length;

    private static final Mline thintriangle_guy[] = {
        new Mline( (int)(-.5*F), (int)(-.7*F) ,           F ,           0  ),
        new Mline(           F,             0 , (int)(-.5*F), (int)( .7*F) ),
        new Mline( (int)(-.5*F), (int)(.7*F)  , (int)(-.5*F), (int)(-.7*R) )
    };
    public static final int NUMTHINTRIANGLEGUYLINES = thintriangle_guy.length;




    int 	cheating = 0;
    boolean 	grid = false;

    int 	leveljuststarted = 1; 	// kluge until AM_LevelInit() is called

    public boolean    	automapactive = false;
    int 	finit_width = SCREENWIDTH;
    int 	finit_height = SCREENHEIGHT - 32;

    // location of window on screen
    int 	f_x;
    int	f_y;

    // size of window on screen
    int 	f_w;
    int	f_h;

    int 	lightlev; 		// used for funky strobing effect
    //BufferedImage	fb; 			// pseudo-frame buffer
    Screen fb = null;
    int 	amclock;

    Mpoint      m_paninc = new Mpoint(); // how far the window pans each tic (map coords)
    int 	mtof_zoommul; // how far the window zooms in each tic (map coords)
    int 	ftom_zoommul; // how far the window zooms in each tic (fb coords)

    int 	m_x, m_y;   // LL x,y where the window is on the map (map coords)
    int 	m_x2, m_y2; // UR x,y where the window is on the map (map coords)

    //
    // width/height of window on map (map coords)
    //
    int 	m_w;
    int	m_h;

    // based on level size
    int 	min_x;
    int	min_y; 
    int 	max_x;
    int  max_y;

    int 	max_w; // max_x-min_x,
    int  max_h; // max_y-min_y

    // based on player size
    int 	min_w;
    int  min_h;


    int 	min_scale_mtof; // used to tell when to stop zooming out
    int 	max_scale_mtof; // used to tell when to stop zooming in

    // old stuff for recovery later
    int old_m_w, old_m_h;
    int old_m_x, old_m_y;

    // old location used by the Follower routine
     Mpoint f_oldloc = new Mpoint();

    // used by MTOF to scale from map-to-frame-buffer coords
    int scale_mtof = INITSCALEMTOF;
    // used by FTOM to scale from frame-buffer-to-map coords (=1/scale_mtof)
    int scale_ftom;

     Player plr; // the player represented by an arrow

     PatchData marknums[] = new PatchData[10]; // numbers used for marking by the automap
     Mpoint markpoints[] = new Mpoint[AM_NUMMARKPOINTS]; // where the points are
    int markpointnum = 0; // next point to be assigned

    boolean followplayer = true; // specifies whether to follow the player around

     int cheat_amap_seq[] = { 0xb2, 0x26, 0x26, 0x2e, 0xff };
     //cheatseq_t cheat_amap = { cheat_amap_seq, 0 };
     Cheat cheat_amap = new Cheat(cheat_amap_seq);
    

     boolean stopped = true;

    //extern boolean viewactive;
    //extern byte screenImage[][SCREENWIDTH*SCREENHEIGHT];



    // Calculates the slope and slope according to the x-axis of a line
    // segment in map coordinates (with the upright y-axis n' all) so
    // that it can be used with the brain-dead drawing stuff.


    void AM_getIslope(
            Mline ml,
            Islope is) {
        int dx, dy;

        dy = ml.a.y - ml.b.y;
        dx = ml.b.x - ml.a.x;
        if (dy==0) {
            is.islp = (dx<0?Integer.MIN_VALUE:Integer.MAX_VALUE);
        } else {
            is.islp = FixedPoint.div(dx, dy);
        }
        if (dx==0) {
            is.slp = (dy<0?Integer.MIN_VALUE:Integer.MAX_VALUE);
        } else {
            is.slp = FixedPoint.div(dy, dx);
        }
    }

    //
    //
    //
    void AM_activateNewScale() {
        m_x += m_w/2;
        m_y += m_h/2;
        m_w = FTOM(f_w);
        m_h = FTOM(f_h);
        m_x -= m_w/2;
        m_y -= m_h/2;
        m_x2 = m_x + m_w;
        m_y2 = m_y + m_h;
    }

    //
    //
    //
    void AM_saveScaleAndLoc() {
        old_m_x = m_x;
        old_m_y = m_y;
        old_m_w = m_w;
        old_m_h = m_h;
    }

    //
    //
    //
    void AM_restoreScaleAndLoc() {

        m_w = old_m_w;
        m_h = old_m_h;
        if (!followplayer) {
            m_x = old_m_x;
            m_y = old_m_y;
        } else {
            m_x = plr.mo.x - m_w/2;
            m_y = plr.mo.y - m_h/2;
        }
        m_x2 = m_x + m_w;
        m_y2 = m_y + m_h;

        // Change the scaling multipliers
        scale_mtof = FixedPoint.div(f_w<<FRACBITS, m_w);
        scale_ftom = FixedPoint.div(FRACUNIT, scale_mtof);
    }

    //
    // adds a marker at the current location
    //
    void AM_addMark()
    {
        markpoints[markpointnum].x = m_x + m_w/2;
        markpoints[markpointnum].y = m_y + m_h/2;
        markpointnum = (markpointnum + 1) % AM_NUMMARKPOINTS;

    }

    //
    // Determines bounding box of all vertices,
    // sets global variables controlling zoom range.
    //
    void AM_findMinMaxBoundaries()
    {
        int i;
        int a;
        int b;

        min_x = Integer.MAX_VALUE;
        min_y = Integer.MAX_VALUE;
        max_x = Integer.MIN_VALUE;
        max_y = Integer.MIN_VALUE;

        PlayerSetup ps = Game.getInstance().playerSetup;
        
        for (i=0;i<ps.vertexes.length;i++) {
            if (ps.vertexes[i].x < min_x) {
                min_x = ps.vertexes[i].x;
            } else if (ps.vertexes[i].x > max_x) {
                max_x = ps.vertexes[i].x;
            }

            if (ps.vertexes[i].y < min_y) {
                min_y = ps.vertexes[i].y;
            } else if (ps.vertexes[i].y > max_y) {
                max_y = ps.vertexes[i].y;
            }
        }

        max_w = max_x - min_x;
        max_h = max_y - min_y;

        min_w = 2*PLAYERRADIUS; // const? never changed?
        min_h = 2*PLAYERRADIUS;

        a = FixedPoint.div(f_w<<FRACBITS, max_w);
        b = FixedPoint.div(f_h<<FRACBITS, max_h);

        min_scale_mtof = a < b ? a : b;
        max_scale_mtof = FixedPoint.div(f_h<<FRACBITS, 2*PLAYERRADIUS);

    }


    //
    //
    //
    void AM_changeWindowLoc()
    {
        if (m_paninc.x!=0 || m_paninc.y!=0) {
            followplayer = false;
            f_oldloc.x = Integer.MAX_VALUE;
        }

        m_x += m_paninc.x;
        m_y += m_paninc.y;

        if (m_x + m_w/2 > max_x) {
            m_x = max_x - m_w/2;
        } else if (m_x + m_w/2 < min_x) {
            m_x = min_x - m_w/2;
        }

        if (m_y + m_h/2 > max_y) {
            m_y = max_y - m_h/2;
        } else if (m_y + m_h/2 < min_y) {
            m_y = min_y - m_h/2;
        }

        m_x2 = m_x + m_w;
        m_y2 = m_y + m_h;
    }


    Event st_notify = null;
    
    //
    //
    //
    void AM_initVariables()
    {
        st_notify = new Event( ev_keyup, AM_MSGENTERED );
        automapactive = true;
        //fb = game.video.screenImage[0];
        fb = game.renderer.video.screens[0];

        f_oldloc.x = Integer.MAX_VALUE;
        amclock = 0;
        lightlev = 0;

        m_paninc.x = 0;
        m_paninc.y = 0;
        
        ftom_zoommul = FRACUNIT;
        mtof_zoommul = FRACUNIT;

        m_w = FTOM(f_w);
        m_h = FTOM(f_h);

        // find player to center on initially
        int pnum = game.consoleplayer;
        if (!game.playeringame[pnum]) {
            for (pnum=0;pnum<MAXPLAYERS;pnum++) {
                if (game.playeringame[pnum]) {
                    break;
                }
            }
        }

        plr = game.players[pnum];
        m_x = plr.mo.x - m_w/2;
        m_y = plr.mo.y - m_h/2;
        AM_changeWindowLoc();

        // for saving & restoring
        old_m_x = m_x;
        old_m_y = m_y;
        old_m_w = m_w;
        old_m_h = m_h;

        // inform the status bar of the change
        game.statusBar.ST_Responder(st_notify);

    }

    //
    // 
    //
    void AM_loadPics()
    {
        //int i;
        //char namebuf[9];

        for (int i=0;i<10;i++) {
            //sprintf(namebuf, "AMMNUM%d", i);
            marknums[i] = game.wad.getPatchByName("AMMNUM" + i);
            //marknums[i] = W_CacheLumpName(namebuf, PU_STATIC);
        }

    }

    void AM_unloadPics() {
//        int i;
//
//        for (i=0;i<10;i++)
//            Z_ChangeTag(marknums[i], PU_CACHE);

    }

    void AM_clearMarks() {

        for (int i=0;i<AM_NUMMARKPOINTS;i++) {
            markpoints[i].x = -1; // means empty
        }
        markpointnum = 0;
    }

    //
    // should be called at the start of every level
    // right now, i figure it out myself
    //
    void AM_LevelInit()
    {
        leveljuststarted = 0;

        f_x = 0;
        f_y = 0;
        f_w = finit_width;
        f_h = finit_height;

        AM_clearMarks();

        AM_findMinMaxBoundaries();
        scale_mtof = FixedPoint.div(min_scale_mtof, (int) (0.7*FRACUNIT));
        if (scale_mtof > max_scale_mtof) {
            scale_mtof = min_scale_mtof;
        }
        scale_ftom = FixedPoint.div(FRACUNIT, scale_mtof);
    }


    //
    //
    //
    public void AM_Stop () {
        st_notify = new Event( ev_keyup, AM_MSGEXITED );

        AM_unloadPics();
        automapactive = false;
        game.statusBar.ST_Responder(st_notify);
        stopped = true;
    }

    //
    //
    //
    public void AM_Start () {
        int lastlevel = -1, lastepisode = -1;

        if (!stopped) AM_Stop();
        stopped = false;
        if (lastlevel != game.gamemap || lastepisode != game.gameepisode)
        {
            AM_LevelInit();
            lastlevel = game.gamemap;
            lastepisode = game.gameepisode;
        }
        AM_initVariables();
        AM_loadPics();
    }

    //
    // set the window scale to the maximum size
    //
    void AM_minOutWindowScale() {
        scale_mtof = min_scale_mtof;
        scale_ftom = FixedPoint.div(FRACUNIT, scale_mtof);
        AM_activateNewScale();
    }

    //
    // set the window scale to the minimum size
    //
    void AM_maxOutWindowScale() {
        scale_mtof = max_scale_mtof;
        scale_ftom = FixedPoint.div(FRACUNIT, scale_mtof);
        AM_activateNewScale();
    }


    //
    // Handle events (user inputs) in automap mode
    //
    public boolean AM_Responder( Event	ev ) {
        boolean rc;
        boolean cheatstate = false;
        boolean bigstate = false;
        //char buffer[20];

        rc = false;

        if (!automapactive) {
            if (ev.type == ev_keydown && ev.data1 == AM_STARTKEY) {
                AM_Start();
                game.viewactive = false;
                rc = true;
            }
        } else if (ev.type == ev_keydown) {

            rc = true;
            switch (ev.data1) {
                case AM_PANRIGHTKEY: // pan right
                    if (!followplayer) {
                        m_paninc.x = FTOM(F_PANINC);
                    } else {
                        rc = false;
                    }
                    break;
                case AM_PANLEFTKEY: // pan left
                    if (!followplayer) {
                        m_paninc.x = -FTOM(F_PANINC);
                    } else {
                        rc = false;
                    }
                    break;
                case AM_PANUPKEY: // pan up
                    if (!followplayer) {
                        m_paninc.y = FTOM(F_PANINC);
                    } else {
                        rc = false;
                    }
                    break;
                case AM_PANDOWNKEY: // pan down
                    if (!followplayer) {
                        m_paninc.y = -FTOM(F_PANINC);
                    } else {
                        rc = false;
                    }
                    break;
                case AM_ZOOMOUTKEY: // zoom out
                    mtof_zoommul = M_ZOOMOUT;
                    ftom_zoommul = M_ZOOMIN;
                    break;
                case AM_ZOOMINKEY: // zoom in
                    mtof_zoommul = M_ZOOMIN;
                    ftom_zoommul = M_ZOOMOUT;
                    break;
              case AM_ENDKEY:
                bigstate = false;
                game.viewactive = true;
                AM_Stop ();
                break;
              case AM_GOBIGKEY:
                bigstate = !bigstate;
                if (bigstate) {
                    AM_saveScaleAndLoc();
                    AM_minOutWindowScale();
                } else {
                    AM_restoreScaleAndLoc();
                }
                break;
              case AM_FOLLOWKEY:
                followplayer = !followplayer;
                f_oldloc.x = Integer.MAX_VALUE;
                plr.message = followplayer ? 
                        Game.messages.getString("AMSTR_FOLLOWON") 
                      : Game.messages.getString("AMSTR_FOLLOWOFF");
                break;
              case AM_GRIDKEY:
                grid = !grid;
                plr.message = grid ?
                        Game.messages.getString("AMSTR_GRIDON") 
                      : Game.messages.getString("AMSTR_GRIDOFF");
                break;
              case AM_MARKKEY:
                //sprintf(buffer, "%s %d", AMSTR_MARKEDSPOT, markpointnum);
                plr.message = Game.messages.getString("AMSTR_MARKEDSPOT") + markpointnum;
                AM_addMark();
                break;
              case AM_CLEARMARKKEY:
                AM_clearMarks();
                plr.message = Game.messages.getString("AMSTR_MARKSCLEARED");
                break;
              default:
                cheatstate=false;
                rc = false;
            }
            if (game.deathmatch==0 && cheat_amap.cht_CheckCheat( ev.data1)) {
                rc = false;
                cheating = (cheating+1) % 3;
            }
        } else if (ev.type == ev_keyup) {
            rc = false;
            switch (ev.data1) {
                case AM_PANRIGHTKEY:
                    if (!followplayer) {
                        m_paninc.x = 0;
                    }
                    break;
                case AM_PANLEFTKEY:
                    if (!followplayer) {
                        m_paninc.x = 0;
                    }
                    break;
                case AM_PANUPKEY:
                    if (!followplayer) {
                        m_paninc.y = 0;
                    }
                    break;
                case AM_PANDOWNKEY:
                    if (!followplayer) {
                        m_paninc.y = 0;
                    }
                    break;
                case AM_ZOOMOUTKEY:
                case AM_ZOOMINKEY:
                    mtof_zoommul = FRACUNIT;
                    ftom_zoommul = FRACUNIT;
                    break;
            }
        }

        return rc;

    }


    //
    // Zooming
    //
    void AM_changeWindowScale() {

        // Change the scaling multipliers
        scale_mtof = FixedPoint.mul(scale_mtof, mtof_zoommul);
        scale_ftom = FixedPoint.div(FRACUNIT, scale_mtof);

        if (scale_mtof < min_scale_mtof) {
            AM_minOutWindowScale();
        } else if (scale_mtof > max_scale_mtof) {
            AM_maxOutWindowScale();
        } else {
            AM_activateNewScale();
        }
    }


    //
    //
    //
    void AM_doFollowPlayer() {

        if (f_oldloc.x != plr.mo.x || f_oldloc.y != plr.mo.y) {
            m_x = FTOM(MTOF(plr.mo.x)) - m_w/2;
            m_y = FTOM(MTOF(plr.mo.y)) - m_h/2;
            m_x2 = m_x + m_w;
            m_y2 = m_y + m_h;
            f_oldloc.x = plr.mo.x;
            f_oldloc.y = plr.mo.y;

            //  m_x = FTOM(MTOF(plr.mo.x - m_w/2));
            //  m_y = FTOM(MTOF(plr.mo.y - m_h/2));
            //  m_x = plr.mo.x - m_w/2;
            //  m_y = plr.mo.y - m_h/2;

        }
    }

    int nexttic = 0;
    //
    //
    //
    void AM_updateLightLev()
    {
        //int litelevels[] = { 0, 3, 5, 6, 6, 7, 7, 7 };
        int litelevels[] = { 0, 4, 7, 10, 12, 14, 15, 15 };
        int litelevelscnt = 0;

        // Change light level
        if (amclock>nexttic) {
            lightlev = litelevels[litelevelscnt];
            litelevelscnt++;
            
            if (litelevelscnt == litelevels.length) {
                litelevelscnt = 0;
            }
            nexttic = amclock + 6 - (amclock % 6);
        }

    }


    //
    // Updates on Game Tick
    //
    public void AM_Ticker ()
    {

        if (!automapactive) {
            return;
        }

        amclock++;

        if (followplayer) {
            AM_doFollowPlayer();
        }

        // Change the zoom if necessary
        if (ftom_zoommul != FRACUNIT) {
            AM_changeWindowScale();
        }

        // Change x,y location
        if (m_paninc.x!=0 || m_paninc.y!=0) {
            AM_changeWindowLoc();
        }
            
            // Update light level
            // AM_updateLightLev();

    }


    //
    // Clear automap frame buffer.
    //
    void AM_clearFB(int color)
    {
//        Graphics g = fb.getGraphics();
//        g.setColor(new Color(color, color, color) ); // Hack
//        g.fillRect(0, 0, f_w, f_h);
        //memset(fb, color, f_w*f_h);
        Arrays.fill(fb.area, color);
    }


    private static final int LEFT   = 1;
    private static final int RIGHT  = 2;
    private static final int BOTTOM = 4;
    private static final int TOP    = 8;
        
    private int DOOUTCODE(int mx, int my) { 
            int oc = 0; 
            
            if (my < 0) {
                oc |= TOP;
            } else if (my >= f_h) {
                oc |= BOTTOM;
            } 
            
            if (mx < 0) {
                oc |= LEFT;
            } else if (mx >= f_w) {
                oc |= RIGHT;
            }  
            
            return oc;
    }

    // Automap clipping of lines.
    //
    // Based on Cohen-Sutherland clipping algorithm but with a slightly
    // faster reject and precalculated slopes.  If the speed is needed,
    // use a hash algorithm to handle  the common cases.
    //
    boolean AM_clipMline(
            Mline ml,
            Fline fl) {

        int outcode1 = 0;
        int outcode2 = 0;
        int outside;

        Fpoint tmp = new Fpoint(0,0);
        int dx;
        int dy;


        // do trivial rejects and outcodes
        if (ml.a.y > m_y2) {
            outcode1 = TOP;
        } else if (ml.a.y < m_y) {
            outcode1 = BOTTOM;
        }

        if (ml.b.y > m_y2) {
            outcode2 = TOP;
        } else if (ml.b.y < m_y) {
            outcode2 = BOTTOM;
        }

        if (outcode1 > 0 & outcode2 > 0) {
            return false; // trivially outside
        }
        if (ml.a.x < m_x) {
            outcode1 |= LEFT;
        } else if (ml.a.x > m_x2) {
            outcode1 |= RIGHT;
        }

        if (ml.b.x < m_x) {
            outcode2 |= LEFT;
        } else if (ml.b.x > m_x2) {
            outcode2 |= RIGHT;
        }

        if (outcode1 > 0 & outcode2 > 0) {
            return false; // trivially outside
        }
        // transform to frame-buffer coordinates.
        fl.a.x = CXMTOF(ml.a.x);
        fl.a.y = CYMTOF(ml.a.y);
        fl.b.x = CXMTOF(ml.b.x);
        fl.b.y = CYMTOF(ml.b.y);

        outcode1 = DOOUTCODE(fl.a.x, fl.a.y);
        outcode2 = DOOUTCODE(fl.b.x, fl.b.y);

        if (outcode1>0 & outcode2>0) {
            return false;
        }

        while (outcode1>0 | outcode2>0)
        {
            // may be partially inside box
            // find an outside point
            if (outcode1>0) {
                outside = outcode1;
            } else {
                outside = outcode2;
            }

            // clip to each side
            if ((outside & TOP)>0) {
                dy = fl.a.y - fl.b.y;
                dx = fl.b.x - fl.a.x;
                tmp.x = fl.a.x + (dx*(fl.a.y))/dy;
                tmp.y = 0;
            } else if ((outside & BOTTOM)>0) {
                dy = fl.a.y - fl.b.y;
                dx = fl.b.x - fl.a.x;
                tmp.x = fl.a.x + (dx*(fl.a.y-f_h))/dy;
                tmp.y = f_h-1;
            } else if ((outside & RIGHT)>0) {
                dy = fl.b.y - fl.a.y;
                dx = fl.b.x - fl.a.x;
                tmp.y = fl.a.y + (dy*(f_w-1 - fl.a.x))/dx;
                tmp.x = f_w-1;
            } else if ((outside & LEFT)>0) {
                dy = fl.b.y - fl.a.y;
                dx = fl.b.x - fl.a.x;
                tmp.y = fl.a.y + (dy*(-fl.a.x))/dx;
                tmp.x = 0;
            }

            if (outside == outcode1) {
                fl.a = tmp;
                outcode1 = DOOUTCODE(fl.a.x, fl.a.y);
            } else {
                fl.b = tmp;
                outcode2 = DOOUTCODE(fl.b.x, fl.b.y);
            }

            if ((outcode1 & outcode2) > 0) {
                return false; // trivially outside
            }
        }

        return true;
    }


    public void PUTDOT(int xx,int yy,int cc) {
        
        fb.area[(yy)*f_w+(xx)]=(cc);
        
//        Graphics g = fb.getGraphics();
//        g.setColor(game.wad.paletteList.get(0)[cc]);
//        g.fillRect(xx, yy, 1, 1);
                
    }

    //
    // Classic Bresenham w/ whatever optimizations needed for speed
    //
    void AM_drawFline ( 
    Fline	fl,
      int		color )
    {
         int x;
         int y;
         int dx;
         int dy;
         int sx;
         int sy;
         int ax;
         int ay;
         int d;

        int fuck = 0;

        // For debugging only
        if (      fl.a.x < 0 || fl.a.x >= f_w
               || fl.a.y < 0 || fl.a.y >= f_h
               || fl.b.x < 0 || fl.b.x >= f_w
               || fl.b.y < 0 || fl.b.y >= f_h)
        {
            Defines.logger.log(Level.FINE, "fuck {0}\n", fuck);
            fuck++;
            return;
        }


        dx = fl.b.x - fl.a.x;
        ax = 2 * (dx<0 ? -dx : dx);
        sx = dx<0 ? -1 : 1;

        dy = fl.b.y - fl.a.y;
        ay = 2 * (dy<0 ? -dy : dy);
        sy = dy<0 ? -1 : 1;

        x = fl.a.x;
        y = fl.a.y;

        if (ax > ay) {
            d = ay - ax / 2;
            while (true) {
                PUTDOT(x, y, color);
                if (x == fl.b.x) {
                    return;
                }
                if (d >= 0) {
                    y += sy;
                    d -= ax;
                }
                x += sx;
                d += ay;
            }
        } else {
            d = ax - ay / 2;
            while (true) {
                PUTDOT(x, y, color);
                if (y == fl.b.y) {
                    return;
                }
                if (d >= 0) {
                    x += sx;
                    d -= ay;
                }
                y += sy;
                d += ax;
            }
        }
    }


    //
    // Clip lines, draw visible part sof lines.
    //
    void AM_drawMline(
            Mline ml,
            int color) {
        
        Fline fl = new Fline();

        if (AM_clipMline(ml, fl)) {
            AM_drawFline(fl, color); // draws it on frame buffer using fb coords
        }
    }



    //
    // Draws flat (floor/ceiling tile) aligned grid lines.
    //
    void AM_drawGrid(int color) {
        int x, y;
        int start, end;
        Mline ml = new Mline(0,0,0,0);

        // Figure out start of vertical gridlines
        start = m_x;
        if (((start-game.playerSetup.bmaporgx)%(MAPBLOCKUNITS<<FRACBITS))>0) {
            start += (MAPBLOCKUNITS<<FRACBITS)
                    - ((start-game.playerSetup.bmaporgx)%(MAPBLOCKUNITS<<FRACBITS));
        }
        end = m_x + m_w;

        // draw vertical gridlines
        ml.a.y = m_y;
        ml.b.y = m_y+m_h;
        for (x=start; x<end; x+=(MAPBLOCKUNITS<<FRACBITS))
        {
            ml.a.x = x;
            ml.b.x = x;
            AM_drawMline(ml, color);
        }

        // Figure out start of horizontal gridlines
        start = m_y;
        if (((start-game.playerSetup.bmaporgy)%(MAPBLOCKUNITS<<FRACBITS))>0) {
            start += (MAPBLOCKUNITS<<FRACBITS)
                    - ((start-game.playerSetup.bmaporgy)%(MAPBLOCKUNITS<<FRACBITS));
        }
        end = m_y + m_h;

        // draw horizontal gridlines
        ml.a.x = m_x;
        ml.b.x = m_x + m_w;
        for (y=start; y<end; y+=(MAPBLOCKUNITS<<FRACBITS)) {
            ml.a.y = y;
            ml.b.y = y;
            AM_drawMline(ml, color);
        }

    }

    //
    // Determines visible lines, draws them.
    // This is LineDef based, not LineSeg based.
    //
    void AM_drawWalls() {
        Mline l = new Mline(0, 0, 0, 0);

        Line[] lines = game.playerSetup.lines;
        
        for (Line line : lines) {
            l.a.x = line.v1.x;
            l.a.y = line.v1.y;
            l.b.x = line.v2.x;
            l.b.y = line.v2.y;
            if (cheating>0 || (line.flags & ML_MAPPED) > 0) {
                if ((line.flags & LINE_NEVERSEE) > 0 && 0==cheating) {
                    continue;
                }
                if (line.backsector == null) {
                    AM_drawMline(l, WALLCOLORS+lightlev);
                } else {
                    if (line.special == 39) {
                        // teleporters
                        AM_drawMline(l, WALLCOLORS+WALLRANGE/2);
                    } else if ((line.flags & ML_SECRET) > 0) {
                        // secret door
                        if (cheating>0) {
                            AM_drawMline(l, SECRETWALLCOLORS + lightlev);
                        } else {
                            AM_drawMline(l, WALLCOLORS+lightlev);
                        }
                    } else if (line.backsector.floorheight != line.frontsector.floorheight) {
                        AM_drawMline(l, FDWALLCOLORS + lightlev); // floor level change
                    } else if (line.backsector.ceilingheight != line.frontsector.ceilingheight) {
                        AM_drawMline(l, CDWALLCOLORS+lightlev); // ceiling level change
                    } else if (cheating>0) {
                        AM_drawMline(l, TSWALLCOLORS+lightlev);
                    }
                }
            } else if (plr.powers[pw_allmap.ordinal()]>0) {
                if (0 == (line.flags & LINE_NEVERSEE)) {
                    AM_drawMline(l, GRAYS+3);
                }
            }
        }
    }


    //
    // Rotation in 2D.
    // Used to rotate player arrow line character.
    //
    void AM_rotate(
            Fpoint p,
            //int*	x,
            //int*	y,
            long	a ) {
        int tmpx;

        tmpx =
            FixedPoint.mul(p.x,finecosine(a>>ANGLETOFINESHIFT))
            - FixedPoint.mul(p.y,finesine(a>>ANGLETOFINESHIFT));

        p.y =
            FixedPoint.mul(p.x,finesine(a>>ANGLETOFINESHIFT))
            + FixedPoint.mul(p.y,finecosine(a>>ANGLETOFINESHIFT));

        p.x = tmpx;
    }

    void
    AM_drawLineCharacter
    ( Mline[]	lineguy,
      int		lineguylines,  //oh my
      int	scale,
      long	angle,
      int		color,
      int	x,
      int	y )
    {
        Mline	l = new Mline(0, 0, 0, 0);

        for (int i=0;i<lineguylines;i++) {
            l.a.x = lineguy[i].a.x;
            l.a.y = lineguy[i].a.y;

            if (scale>0) {
                l.a.x = FixedPoint.mul(scale, l.a.x);
                l.a.y = FixedPoint.mul(scale, l.a.y);
            }

            if (angle!=0) {
                AM_rotate(l.a, angle);
            }

            l.a.x += x;
            l.a.y += y;

            l.b.x = lineguy[i].b.x;
            l.b.y = lineguy[i].b.y;

            if (scale>0) {
                l.b.x = FixedPoint.mul(scale, l.b.x);
                l.b.y = FixedPoint.mul(scale, l.b.y);
            }

            if (angle!=0) {
                AM_rotate(l.b, angle);
            }

            l.b.x += x;
            l.b.y += y;

            AM_drawMline(l, color);
        }
    }

    void AM_drawPlayers() {
        Player	p;
        int 	their_colors[] = { GREENS, GRAYS, BROWNS, REDS };
        int		their_color = -1;
        int		color;

        if (!game.netgame) {
            if (cheating>0)
                AM_drawLineCharacter
                    (cheat_player_arrow, NUMCHEATPLYRLINES, 0,
                     plr.mo.angle, WHITE, plr.mo.x, plr.mo.y);
            else
                AM_drawLineCharacter
                    (player_arrow, NUMPLYRLINES, 0, plr.mo.angle,
                     WHITE, plr.mo.x, plr.mo.y);
            return;
        }

        for (int i=0;i<MAXPLAYERS;i++) {
            their_color++;
            p = game.players[i];

            if ( (game.deathmatch>0 && !game.singledemo) && p != plr) {
                continue;
            }

            if (!game.playeringame[i]) {
                continue;
            }

            if (p.powers[pw_invisibility.ordinal()]>0) {
                color = 246; // *close* to black
            } else {
                color = their_colors[their_color];
            }

            AM_drawLineCharacter
                (player_arrow, NUMPLYRLINES, 0, p.mo.angle,
                 color, p.mo.x, p.mo.y);
        }

    }

    private void AM_drawThings( int colors, int colorrange) {
        //int		i;
        //Degenmobj	thing;

//        for (i=0;i<game.playerSetup.sectors.length;i++) {
//            thing = game.playerSetup.sectors[i].thinglist;
//            while (thing!=null) {
//                AM_drawLineCharacter
//                    (thintriangle_guy, NUMTHINTRIANGLEGUYLINES,
//                     16<<FRACBITS, thing.angle, colors+lightlev, thing.x, thing.y);
//                thing = thing.snext;
//            }
//        }
        for (MapSector s: game.playerSetup.sectors) {
            Degenmobj thing = s.thinglist;
            while (thing!=null) {
                AM_drawLineCharacter( thintriangle_guy, NUMTHINTRIANGLEGUYLINES,
                     16<<FRACBITS, thing.angle, colors+lightlev, 
                     thing.x, thing.y
                );
                thing = thing.snext;
            }
        }
    }

    void AM_drawMarks() {
        int i, fx, fy, w, h;

        for (i=0;i<AM_NUMMARKPOINTS;i++) {
            if (markpoints[i].x != -1) {
                //      w = SHORT(marknums[i].width);
                //      h = SHORT(marknums[i].height);
                w = 5; // because something's wrong with the wad, i guess
                h = 6; // because something's wrong with the wad, i guess
                fx = CXMTOF(markpoints[i].x);
                fy = CYMTOF(markpoints[i].y);
                if (fx >= f_x && fx <= f_w - w && fy >= f_y && fy <= f_h - h) {
                    game.renderer.video.drawPatch(fx, fy, FB, marknums[i]);
                }
            }
        }

    }

    void AM_drawCrosshair(int color) {
//        Graphics g = fb.getGraphics();
//        g.setColor(game.wad.paletteList.get(0)[color]);
//        g.fillRect(f_w/2, (f_h+1)/2, 1, 1);
        
        //fb[(f_w*(f_h+1))/2] = color; // single point for now
        fb.area[(f_w*(f_h+1))/2] = color;
    }

    public void AM_Drawer () {
        if (!automapactive) {
            return;
        }

        Defines.logger.config("AM_Drawer()\n");
        AM_clearFB(BACKGROUND);
        if (grid) {
            AM_drawGrid(GRIDCOLORS);
        }
        AM_drawWalls();
        AM_drawPlayers();
        if (cheating==2) {
            AM_drawThings(THINGCOLORS, THINGRANGE);
        }
        AM_drawCrosshair(XHAIRCOLORS);

        AM_drawMarks();

        //V_MarkRect(f_x, f_y, f_w, f_h);

    }
    

}
