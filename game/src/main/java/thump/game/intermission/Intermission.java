/*
 * Intermission
 */
package thump.game.intermission;

import java.util.logging.Level;
import static thump.base.Defines.SCREENHEIGHT;
import static thump.base.Defines.SCREENWIDTH;
import static thump.base.Defines.logger;
import thump.game.Defines;
import static thump.game.Defines.MAXPLAYERS;
import static thump.game.Defines.TICRATE;
import thump.game.Event;
import static thump.game.Event.BT_ATTACK;
import static thump.game.Event.BT_USE;
import thump.game.Game;
import thump.game.Player;
import thump.game.WbPlayer;
import thump.game.WbStart;
import static thump.game.intermission.Intermission.animenum_t.ANIM_ALWAYS;
import static thump.game.intermission.Intermission.animenum_t.ANIM_LEVEL;
import thump.game.play.Random;
import static thump.game.sound.sfx.Sounds.MusicEnum.mus_dm2int;
import static thump.game.sound.sfx.Sounds.MusicEnum.mus_inter;
import static thump.game.sound.sfx.Sounds.SfxEnum.sfx_barexp;
import static thump.game.sound.sfx.Sounds.SfxEnum.sfx_pistol;
import static thump.game.sound.sfx.Sounds.SfxEnum.sfx_pldeth;
import static thump.game.sound.sfx.Sounds.SfxEnum.sfx_sgcock;
import static thump.game.sound.sfx.Sounds.SfxEnum.sfx_slop;
import thump.wad.mapraw.PatchData;

/**
 *
 * @author mark
 */
public class Intermission {

    private final Game game;

    public Intermission( Game game) {
        this.game = game;
    }
    
    public enum stateenum_t
    {
        NoState(-1),
        StatCount(0),
        ShowNextLoc(1);

        private int value;
        private stateenum_t(int value) {
                this.value = value;
        }

        public int getVal() {
            return value;
        }
    };
    
    //
    // Data needed to add patches to full screen intermission pics.
    // Patches are statistics messages, and animations.
    // Loads of by-pixel layout and placement, offsets etc.
    //


    //
    // Different vetween registered DOOM (1994) and
    //  Ultimate DOOM - Final edition (retail, 1995?).
    // This is supposedly ignored for Defines.GameMode.COMMERCIAL
    //  release (aka DOOM II), which had 34 maps
    //  in one episode. So there.
    public static final int NUMEPISODES	=4;
    public static final int NUMMAPS		=9;


    // in tics
    //U public static final int PAUSELEN		(TICRATE*2) 
    //U public static final int SCORESTEP		100
    //U public static final int ANIMPERIOD		32
    // pixel distance from "(YOU)" to "PLAYER N"
    //U public static final int STARDIST		10 
    //U public static final int WK 1


    // GLOBAL LOCATIONS
    public static final int WI_TITLEY		=2;
    public static final int WI_SPACINGY    	=	33;

    // SINGPLE-PLAYER STUFF
    public static final int SP_STATSX		=50;
    public static final int SP_STATSY	=	50;

    public static final int SP_TIMEX	=	16;
    public static final int SP_TIMEY	=	(SCREENHEIGHT-32);


    // NET GAME STUFF
    public static final int NG_STATSY	=	50;
    public int NG_STATSX;

    public static final int NG_SPACINGX    =		64;


    // DEATHMATCH STUFF
    public static final int DM_MATRIXX	=	42;
    public static final int DM_MATRIXY	=	68;

    public static final int DM_SPACINGX	=	40;

    public static final int DM_TOTALSX	=	269;

    public static final int DM_KILLERSX	=	10;
    public static final int DM_KILLERSY	=	100;
    public static final int DM_VICTIMSX    =		5;
    public static final int DM_VICTIMSY	=	50;



    public enum animenum_t
    {
        ANIM_ALWAYS,
        ANIM_RANDOM,
        ANIM_LEVEL

    };


//    typedef struct
//    {
//        int		x;
//        int		y;
//
//    } point_t;


    //
    // Animation.
    // There is another anim_t used in p_spec.
    //
//    typedef struct
//    {
//        animenum_t	type;
//
//        // period in tics between animations
//        int		period;
//
//        // number of animation frames
//        int		nanims;
//
//        // location of animation
//        point_t	loc;
//
//        // ALWAYS: n/a,
//        // RANDOM: period deviation (<256),
//        // LEVEL: level
//        int		data1;
//
//        // ALWAYS: n/a,
//        // RANDOM: random base period,
//        // LEVEL: n/a
//        int		data2; 
//
//        // actual graphics for frames of animations
//        Patch	p[3]; 
//
//        // following must be initialized to zero before use!
//
//        // next value of bcnt (used in conjunction with period)
//        int		nexttic;
//
//        // last drawn animation frame
//        int		lastdrawn;
//
//        // next frame number to animate
//        int		ctr;
//
//        // used by RANDOM and LEVEL when animating
//        int		state;  
//
//    } anim_t;
//

    static Point lnodes[][] =  
    {
        // Episode 0 World Map
        {
            new Point( 185, 164 ),	// location of level 0 (CJ)
            new Point( 148, 143 ),	// location of level 1 (CJ)
            new Point( 69, 122 ),	// location of level 2 (CJ)
            new Point( 209, 102 ),	// location of level 3 (CJ)
            new Point( 116, 89 ),	// location of level 4 (CJ)
            new Point( 166, 55 ),	// location of level 5 (CJ)
            new Point( 71, 56 ),	// location of level 6 (CJ)
            new Point( 135, 29 ),	// location of level 7 (CJ)
            new Point( 71, 24 )         // location of level 8 (CJ)
        },

        // Episode 1 World Map should go here
        {
            new Point( 254, 25 ),	// location of level 0 (CJ)
            new Point( 97, 50 ),	// location of level 1 (CJ)
            new Point( 188, 64 ),	// location of level 2 (CJ)
            new Point( 128, 78 ),	// location of level 3 (CJ)
            new Point( 214, 92 ),	// location of level 4 (CJ)
            new Point( 133, 130 ),	// location of level 5 (CJ)
            new Point( 208, 136 ),	// location of level 6 (CJ)
            new Point( 148, 140 ),	// location of level 7 (CJ)
            new Point( 235, 158 )	// location of level 8 (CJ)
        },

        // Episode 2 World Map should go here
        {
            new Point( 156, 168 ),	// location of level 0 (CJ)
            new Point(  48, 154 ),	// location of level 1 (CJ)
            new Point( 174, 95 ),	// location of level 2 (CJ)
            new Point( 265, 75 ),	// location of level 3 (CJ)
            new Point( 130, 48 ),	// location of level 4 (CJ)
            new Point( 279, 23 ),	// location of level 5 (CJ)
            new Point( 198, 48 ),	// location of level 6 (CJ)
            new Point( 140, 25 ),	// location of level 7 (CJ)
            new Point( 281, 136 )	// location of level 8 (CJ)
        }

    };


    //
    // Animation locations for episode 0 (1).
    // Using patches saves a lot of space,
    //  as they replace 320x200 full screen frames.
    //
    static anim_t epsd0animinfo[] =
    {
        new anim_t( ANIM_ALWAYS, TICRATE/3, 3,  224, 104  ),
        new anim_t( ANIM_ALWAYS, TICRATE/3, 3,  184, 160  ),
        new anim_t( ANIM_ALWAYS, TICRATE/3, 3,  112, 136  ),
        new anim_t( ANIM_ALWAYS, TICRATE/3, 3,   72, 112  ),
        new anim_t( ANIM_ALWAYS, TICRATE/3, 3,   88,  96  ),
        new anim_t( ANIM_ALWAYS, TICRATE/3, 3,   64,  48  ),
        new anim_t( ANIM_ALWAYS, TICRATE/3, 3,  192,  40  ),
        new anim_t( ANIM_ALWAYS, TICRATE/3, 3,  136,  16  ),
        new anim_t( ANIM_ALWAYS, TICRATE/3, 3,   80,  16  ),
        new anim_t( ANIM_ALWAYS, TICRATE/3, 3,   64,  24  )
    };

    static anim_t epsd1animinfo[] =
    {
        new anim_t( ANIM_LEVEL, TICRATE/3, 1,  128, 136 , 1 ),
        new anim_t( ANIM_LEVEL, TICRATE/3, 1,  128, 136 , 2 ),
        new anim_t( ANIM_LEVEL, TICRATE/3, 1,  128, 136 , 3 ),
        new anim_t( ANIM_LEVEL, TICRATE/3, 1,  128, 136 , 4 ),
        new anim_t( ANIM_LEVEL, TICRATE/3, 1,  128, 136 , 5 ),
        new anim_t( ANIM_LEVEL, TICRATE/3, 1,  128, 136 , 6 ),
        new anim_t( ANIM_LEVEL, TICRATE/3, 1,  128, 136 , 7 ),
        new anim_t( ANIM_LEVEL, TICRATE/3, 3,  192, 144 , 8 ),
        new anim_t( ANIM_LEVEL, TICRATE/3, 1,  128, 136 , 8 )
    };

    static anim_t epsd2animinfo[] =
    {
        new anim_t( ANIM_ALWAYS, TICRATE/3, 3,  104, 168  ),
        new anim_t( ANIM_ALWAYS, TICRATE/3, 3,  40, 136  ),
        new anim_t( ANIM_ALWAYS, TICRATE/3, 3,  160, 96  ),
        new anim_t( ANIM_ALWAYS, TICRATE/3, 3,  104, 80  ),
        new anim_t( ANIM_ALWAYS, TICRATE/3, 3,  120, 32  ),
        new anim_t( ANIM_ALWAYS, TICRATE/4, 3,  40, 0  )
    };

    static int NUMANIMS[] = new int[3];
//    {
//        sizeof(epsd0animinfo)/sizeof(anim_t),
//        sizeof(epsd1animinfo)/sizeof(anim_t),
//        sizeof(epsd2animinfo)/sizeof(anim_t)
//    };

    static anim_t anims[][] =
    {
        epsd0animinfo,
        epsd1animinfo,
        epsd2animinfo
    };


    //
    // GENERAL DATA
    //

    //
    // Locally used stuff.
    //
    public static final int FB= 0;


    // States for single-player
    public static final int SP_KILLS		=0;
    public static final int SP_ITEMS		=2;
    public static final int SP_SECRET		=4;
    public static final int SP_FRAGS		=6 ;
    public static final int SP_TIME		=	8 ;
//    public static final int SP_PAR		=	ST_TIME;

    public static final int SP_PAUSE		=1;

    // in seconds
    public static final int SHOWNEXTLOCDELAY	=4;
    //public static final int SHOWLASTLOCDELAY	=SHOWNEXTLOCDELAY;


    // used to accelerate or skip a stage
    static int		acceleratestage;

    // wbs.pnum
    static int		me;

     // specifies current state
    static stateenum_t	state;

    // contains information passed into intermission
    static WbStart	wbs;

    static WbPlayer plrs[];  // wbs.plyr[]

    // used for general timing
    static int 		cnt;  

    // used for timing of background animation
    static int 		bcnt;

    // signals to refresh everything for one frame
    static int 		firstrefresh; 

    static int		cnt_kills[] = new int[MAXPLAYERS];
    static int		cnt_items[] = new int[MAXPLAYERS];
    static int		cnt_secret[] = new int[MAXPLAYERS];
    static int		cnt_time;
    static int		cnt_par;
    static int		cnt_pause;

    // # of Defines.GameMode.COMMERCIAL levels
    static int		NUMCMAPS; 


    //
    //	GRAPHICS
    //

    // background (map of levels).
    static PatchData		bg;

    // You Are Here graphic
    static PatchData		yah[] = new PatchData[2]; 

    // splat
    static PatchData		splat[] = new PatchData[2];

    // %, : graphics
    static PatchData		percent;
    static PatchData		colon;

    // 0-9 graphic
    static PatchData		num[] = new PatchData[10];

    // minus sign
    static PatchData		wiminus;

    // "Finished!" graphics
    static PatchData		finished;

    // "Entering" graphic
    static PatchData		entering; 

    // "secret"
    static PatchData		sp_secret;

     // "Kills", "Scrt", "Items", "Frags"
    static PatchData		kills;
    static PatchData		secret;
    static PatchData		items;
    static PatchData		frags;

    // Time sucks.
    static PatchData		time;
    static PatchData		par;
    static PatchData		sucks;

    // "killers", "victims"
    static PatchData		killers;
    static PatchData		victims; 

    // "Total", your face, your dead face
    static PatchData		total;
    static PatchData		star;
    static PatchData		bstar;

    // "red P[1..MAXPLAYERS]"
    static PatchData		p[] = new PatchData[MAXPLAYERS];

    // "gray P[1..MAXPLAYERS]"
    static PatchData		bp[] = new PatchData[MAXPLAYERS];

     // Name graphics of each level (centered)
    static PatchData	lnames[];

    //
    // CODE
    //
    

    // slam background
    // UNUSED static unsigned char *background=0;


    void WI_slamBackground(){
        // TODO  add 
        game.renderer.video.copyScreen( 1, 0 );
        //memcpy(screens[0], screens[1], SCREENWIDTH * SCREENHEIGHT);
        //V_MarkRect (0, 0, SCREENWIDTH, SCREENHEIGHT);
    }

    // The ticker is used to detect keys
    //  because of timing issues in netgames.
    boolean WI_Responder(Event ev){
        return false;
    }


    // Draws "<Levelname> Finished!"
    void WI_drawLF() {
        int y = WI_TITLEY;

        // draw <LevelName> 
        game.renderer.video.drawPatch((SCREENWIDTH - lnames[wbs.last].width)/2,
                    y, FB, lnames[wbs.last]);

        // draw "Finished!"
        y += (5*lnames[wbs.last].height)/4;

        game.renderer.video.drawPatch((SCREENWIDTH - finished.width)/2,
                    y, FB, finished);
    }



    // Draws "Entering <LevelName>"
    void WI_drawEL() {
        int y = WI_TITLEY;

        // draw "Entering"
        game.renderer.video.drawPatch((SCREENWIDTH - entering.width)/2,
                    y, FB, entering);

        // draw level
        y += (5*lnames[wbs.next].height)/4;

        game.renderer.video.drawPatch((SCREENWIDTH - lnames[wbs.next].width)/2,
                    y, FB, lnames[wbs.next]);

    }

    void WI_drawOnLnode(int n, PatchData c[]) {

        int i;
        int left;
        int top;
        int right;
        int bottom;
        boolean fits = false;

        i = 0;
        do {
            left = lnodes[wbs.epsd][n].x - c[i].leftOffset;
            top = lnodes[wbs.epsd][n].y - c[i].topOffset;
            right = left + c[i].width;
            bottom = top + c[i].height;

            if (left >= 0
                    && right < SCREENWIDTH
                    && top >= 0
                    && bottom < SCREENHEIGHT) {
                fits = true;
            } else {
                i++;
            }
        } while (!fits && i != 2);

        if (fits && i < 2) {
            game.renderer.video.drawPatch(lnodes[wbs.epsd][n].x, lnodes[wbs.epsd][n].y,
                    FB, c[i]);
        } else {
            // DEBUG
            logger.log(Level.WARNING, "Could not place patch on level {0}\n", n + 1);
        }
    }



    void WI_initAnimatedBack() {
        int i;
        anim_t a;

        if (game.gameMode == Defines.GameMode.COMMERCIAL) {
            return;
        }

        if (wbs.epsd > 2) {
            return;
        }

        for (i = 0; i < NUMANIMS[wbs.epsd]; i++) {
            a = anims[wbs.epsd][i];

            // init variables
            a.ctr = -1;

            if (null != a.type) { // specify the next time to draw it
                switch (a.type) {
                    case ANIM_ALWAYS:
                        a.nexttic = bcnt + 1 + (Random.getInstance().M_Random() % a.period);
                        break;
                    case ANIM_RANDOM:
                        a.nexttic = bcnt + 1 + a.data2 + (Random.getInstance().M_Random() % a.data1);
                        break;
                    case ANIM_LEVEL:
                        a.nexttic = bcnt + 1;
                        break;
                    default:
                        break;
                }
            }
        }

    }

    void WI_updateAnimatedBack() {
        anim_t a;

        if (game.gameMode == Defines.GameMode.COMMERCIAL) {
            return;
        }

        if (wbs.epsd > 2) {
            return;
        }

        for (int i = 0; i < NUMANIMS[wbs.epsd]; i++) {
            a = anims[wbs.epsd][i];

            if (bcnt == a.nexttic) {
                switch (a.type) {
                    case ANIM_ALWAYS:
                        if (++a.ctr >= a.nanims) {
                            a.ctr = 0;
                        }
                        a.nexttic = bcnt + a.period;
                        break;

                    case ANIM_RANDOM:
                        a.ctr++;
                        if (a.ctr == a.nanims) {
                            a.ctr = -1;
                            a.nexttic = bcnt + a.data2 + (Random.getInstance().M_Random() % a.data1);
                        } else {
                            a.nexttic = bcnt + a.period;
                        }
                        break;

                    case ANIM_LEVEL:
                        // gawd-awful hack for level anims
                        if (!(state == stateenum_t.StatCount && i == 7)
                                && wbs.next == a.data1) {
                            a.ctr++;
                            if (a.ctr == a.nanims) {
                                a.ctr--;
                            }
                            a.nexttic = bcnt + a.period;
                        }
                        break;
                }
            }

        }

    }


    void WI_drawAnimatedBack() {
        anim_t		a;

        if (game.gameMode == Defines.GameMode.COMMERCIAL) {
            return;
        }

        if (wbs.epsd > 2) {
            return;
        }

        for (int i=0 ; i<NUMANIMS[wbs.epsd] ; i++) {
            a = anims[wbs.epsd][i];

            if (a.ctr >= 0) {
                game.renderer.video.drawPatch(a.loc.x, a.loc.y, FB, a.p[a.ctr]);
            }
        }

    }

    //
    // Draws a number.
    // If digits > 0, then use that many digits minimum,
    //  otherwise only use as many as necessary.
    // Returns new x position.
    //
    int WI_drawNum(int _x, int _y, int _n, int _digits) {

        int fontwidth = num[0].width;
        boolean neg;
        int temp;
        int x = _x;
        int y = _y;
        int digits = _digits;
        int n = _n;

        if (digits < 0) {
            if (0 == n) {
                // make variable-length zeros 1 digit long
                digits = 1;
            } else {
                // figure out # of digits in #
                digits = 0;
                temp = n;

                while (temp > 0) {
                    temp /= 10;
                    digits++;
                }
            }
        }

        neg = n < 0;
        if (neg) {
            n = -n;
        }

        // if non-number, do not draw it
        if (n == 1994) {
            return 0;
        }

        // draw the new number
        while (digits > 0) {
            digits--;
            x -= fontwidth;
            game.renderer.video.drawPatch(x, y, FB, num[n % 10]);
            n /= 10;
        }

        // draw a minus sign if necessary
        if (neg) {
            x -= 8;
            game.renderer.video.drawPatch(x, y, FB, wiminus);
        }

        return x;

    }

    void WI_drawPercent(int x, int y, int p) {
        if (p < 0) {
            return;
        }

        game.renderer.video.drawPatch(x, y, FB, percent);
        WI_drawNum(x, y, p, -1);
    }

    //
    // Display level completion time and par,
    //  or "sucks" message if overflow.
    //
    void WI_drawTime(int _x, int _y, int t) {

        int div;
        int n;
        int x = _x;
        int y = _y;

        if (t < 0) {
            return;
        }

        if (t <= 61 * 59) {
            div = 1;

            do {
                n = (t / div) % 60;
                x = WI_drawNum(x, y, n, 2) - colon.width;
                div *= 60;

                // draw
                if (div == 60 || (t / div)>0 ) {
                    game.renderer.video.drawPatch(x, y, FB, colon);
                }

            } while (t / div>0);
        } else {
            // "sucks"
            game.renderer.video.drawPatch(x - sucks.width, y, FB, sucks);
        }
    }

    void WI_End() {
        //void WI_unloadData();
        WI_unloadData();
    }


    void WI_initNoState() {
        state = stateenum_t.NoState;
        acceleratestage = 0;
        cnt = 10;
    }

    void WI_updateNoState() {

        WI_updateAnimatedBack();

        cnt--;
        if (0==cnt) {
            WI_End();
            game.G_WorldDone();
        }
    }

    boolean snl_pointeron = false;


    void WI_initShowNextLoc() {
        state = stateenum_t.ShowNextLoc;
        acceleratestage = 0;
        cnt = SHOWNEXTLOCDELAY * TICRATE;

        WI_initAnimatedBack();
    }

    void WI_updateShowNextLoc() {
        WI_updateAnimatedBack();

        cnt--;
        if (0==cnt || acceleratestage>0) {
            WI_initNoState();
        } else {
            snl_pointeron = (cnt & 31) < 20;
        }
    }

    void WI_drawShowNextLoc() {

        int		i;
        int		last;

        WI_slamBackground();

        // draw animated background
        WI_drawAnimatedBack(); 

        if ( game.gameMode != Defines.GameMode.COMMERCIAL) {
            if (wbs.epsd > 2) {
                WI_drawEL();
                return;
            }

            last = (wbs.last == 8) ? wbs.next - 1 : wbs.last;

            // draw a splat on taken cities.
            for (i=0 ; i<=last ; i++) {
                WI_drawOnLnode(i, splat);
            }

            // splat the secret level?
            if (wbs.didsecret) {
                WI_drawOnLnode(8, splat);
            }

            // draw flashing ptr
            if (snl_pointeron) {
                WI_drawOnLnode(wbs.next, yah);
            } 
        }

        // draws which level you are entering..
        if ( (game.gameMode != Defines.GameMode.COMMERCIAL)
             || wbs.next != 30) {
            WI_drawEL();
        }  

    }

    void WI_drawNoState() {
        snl_pointeron = true;
        WI_drawShowNextLoc();
    }

    int WI_fragSum(int playernum) {
        int		i;
        int		frags = 0;

        for (i=0 ; i<MAXPLAYERS ; i++) {
            if (game.playeringame[i] && i!=playernum) {
                frags += plrs[playernum].frags[i];
            }
        }

        // JDC hack - negative frags.
        frags -= plrs[playernum].frags[playernum];
        // UNUSED if (frags < 0)
        // 	frags = 0;

        return frags;
    }

    static int dm_state;
    static int dm_frags[][] = new int[MAXPLAYERS][MAXPLAYERS];
    static int dm_totals[] = new int[MAXPLAYERS];

    void WI_initDeathmatchStats() {

        int i;
        int j;

        state = stateenum_t.StatCount;
        acceleratestage = 0;
        dm_state = 1;

        cnt_pause = TICRATE;

        for (i = 0; i < MAXPLAYERS; i++) {
            if (game.playeringame[i]) {
                for (j = 0; j < MAXPLAYERS; j++) {
                    if (game.playeringame[j]) {
                        dm_frags[i][j] = 0;
                    }
                }

                dm_totals[i] = 0;
            }
        }

        WI_initAnimatedBack();
    }

    void WI_updateDeathmatchStats() {

        int i;
        int j;

        boolean stillticking;

        WI_updateAnimatedBack();

        if (acceleratestage > 0 && dm_state != 4) {
            acceleratestage = 0;

            for (i = 0; i < MAXPLAYERS; i++) {
                if (game.playeringame[i]) {
                    for (j = 0; j < MAXPLAYERS; j++) {
                        if (game.playeringame[j]) {
                            dm_frags[i][j] = plrs[i].frags[j];
                        }
                    }

                    dm_totals[i] = WI_fragSum(i);
                }
            }

            game.sound.S_StartSound(0, sfx_barexp);
            dm_state = 4;
        }

        if (dm_state == 2) {
            if (0 == (bcnt & 3)) {
                game.sound.S_StartSound(0, sfx_pistol);
            }

            stillticking = false;

            for (i = 0; i < MAXPLAYERS; i++) {
                if (game.playeringame[i]) {
                    for (j = 0; j < MAXPLAYERS; j++) {
                        if (game.playeringame[j]
                                && dm_frags[i][j] != plrs[i].frags[j]) {
                            if (plrs[i].frags[j] < 0) {
                                dm_frags[i][j]--;
                            } else {
                                dm_frags[i][j]++;
                            }

                            if (dm_frags[i][j] > 99) {
                                dm_frags[i][j] = 99;
                            }

                            if (dm_frags[i][j] < -99) {
                                dm_frags[i][j] = -99;
                            }

                            stillticking = true;
                        }
                    }
                    dm_totals[i] = WI_fragSum(i);

                    if (dm_totals[i] > 99) {
                        dm_totals[i] = 99;
                    }

                    if (dm_totals[i] < -99) {
                        dm_totals[i] = -99;
                    }
                }

            }
            if (!stillticking) {
                game.sound.S_StartSound(0, sfx_barexp);
                dm_state++;
            }

        } else if (dm_state == 4) {
            if (acceleratestage > 0) {
                game.sound.S_StartSound(0, sfx_slop);

                if (game.gameMode == Defines.GameMode.COMMERCIAL) {
                    WI_initNoState();
                } else {
                    WI_initShowNextLoc();
                }
            }
        } else if ((dm_state & 1) > 0) {
            cnt--;
            if (0 == cnt_pause) {
                dm_state++;
                cnt_pause = TICRATE;
            }
        }
    }

    void WI_drawDeathmatchStats() {

        int i;
        int j;
        int x;
        int y;
        int w;

        int lh;	// line height

        lh = WI_SPACINGY;

        WI_slamBackground();

        // draw animated background
        WI_drawAnimatedBack();
        WI_drawLF();

        // draw stat titles (top line)
        game.renderer.video.drawPatch(DM_TOTALSX - (total.width / 2),
                DM_MATRIXY - WI_SPACINGY + 10,
                FB,
                total);

        game.renderer.video.drawPatch(DM_KILLERSX, DM_KILLERSY, FB, killers);
        game.renderer.video.drawPatch(DM_VICTIMSX, DM_VICTIMSY, FB, victims);

        // draw P?
        x = DM_MATRIXX + DM_SPACINGX;
        y = DM_MATRIXY;

        for (i = 0; i < MAXPLAYERS; i++) {
            if (game.playeringame[i]) {
                game.renderer.video.drawPatch(x - p[i].width / 2,
                        DM_MATRIXY - WI_SPACINGY,
                        FB,
                        p[i]);

                game.renderer.video.drawPatch(DM_MATRIXX - p[i].width / 2,
                        y,
                        FB,
                        p[i]);

                if (i == me) {
                    game.renderer.video.drawPatch(x - p[i].width / 2,
                            DM_MATRIXY - WI_SPACINGY,
                            FB,
                            bstar);

                    game.renderer.video.drawPatch(DM_MATRIXX - p[i].width / 2,
                            y,
                            FB,
                            star);
                }
            } else {
                // game.video.drawPatch(x-SHORT(bp[i].width)/2,
                //   DM_MATRIXY - WI_SPACINGY, FB, bp[i]);
                // game.video.drawPatch(DM_MATRIXX-SHORT(bp[i].width)/2,
                //   y, FB, bp[i]);
            }
            x += DM_SPACINGX;
            y += WI_SPACINGY;
        }

        // draw stats
        y = DM_MATRIXY + 10;
        w = num[0].width;

        for (i = 0; i < MAXPLAYERS; i++) {
            x = DM_MATRIXX + DM_SPACINGX;

            if (game.playeringame[i]) {
                for (j = 0; j < MAXPLAYERS; j++) {
                    if (game.playeringame[j]) {
                        WI_drawNum(x + w, y, dm_frags[i][j], 2);
                    }

                    x += DM_SPACINGX;
                }
                WI_drawNum(DM_TOTALSX + w, y, dm_totals[i], 2);
            }
            y += WI_SPACINGY;
        }
    }

    int cnt_frags[] = new int[MAXPLAYERS];
    int dofrags;
    int ng_state;

    void WI_initNetgameStats() {

        int i;

        state = stateenum_t.StatCount;
        acceleratestage = 0;
        ng_state = 1;

        cnt_pause = TICRATE;

        for (i = 0; i < MAXPLAYERS; i++) {
            if (!game.playeringame[i]) {
                continue;
            }

            cnt_kills[i] = 0;
            cnt_items[i] = 0;
            cnt_secret[i] = 0;
            cnt_frags[i] = 0;

            dofrags += WI_fragSum(i);
        }

        dofrags = dofrags>0?1:0;

        WI_initAnimatedBack();
    }

    void WI_updateNetgameStats() {

        int i;
        int fsum;

        boolean stillticking;

        WI_updateAnimatedBack();

        if (acceleratestage>0 && ng_state != 10) {
            acceleratestage = 0;

            for (i = 0; i < MAXPLAYERS; i++) {
                if (!game.playeringame[i]) {
                    continue;
                }

                cnt_kills[i] = (plrs[i].skills * 100) / wbs.maxkills;
                cnt_items[i] = (plrs[i].sitems * 100) / wbs.maxitems;
                cnt_secret[i] = (plrs[i].ssecret * 100) / wbs.maxsecret;

                if (dofrags>0) {
                    cnt_frags[i] = WI_fragSum(i);
                }
            }
            game.sound.S_StartSound(0, sfx_barexp);
            ng_state = 10;
        }

        if (ng_state == 2) {
            if (0==(bcnt & 3)) {
                game.sound.S_StartSound(0, sfx_pistol);
            }

            stillticking = false;

            for (i = 0; i < MAXPLAYERS; i++) {
                if (!game.playeringame[i]) {
                    continue;
                }

                cnt_kills[i] += 2;

                if (cnt_kills[i] >= (plrs[i].skills * 100) / wbs.maxkills) {
                    cnt_kills[i] = (plrs[i].skills * 100) / wbs.maxkills;
                } else {
                    stillticking = true;
                }
            }

            if (!stillticking) {
                game.sound.S_StartSound(0, sfx_barexp);
                ng_state++;
            }
        } else if (ng_state == 4) {
            if (0==(bcnt & 3)) {
                game.sound.S_StartSound(0, sfx_pistol);
            }

            stillticking = false;

            for (i = 0; i < MAXPLAYERS; i++) {
                if (!game.playeringame[i]) {
                    continue;
                }

                cnt_items[i] += 2;
                if (cnt_items[i] >= (plrs[i].sitems * 100) / wbs.maxitems) {
                    cnt_items[i] = (plrs[i].sitems * 100) / wbs.maxitems;
                } else {
                    stillticking = true;
                }
            }
            if (!stillticking) {
                game.sound.S_StartSound(0, sfx_barexp);
                ng_state++;
            }
        } else if (ng_state == 6) {
            if (0==(bcnt & 3)) {
                game.sound.S_StartSound(0, sfx_pistol);
            }

            stillticking = false;

            for (i = 0; i < MAXPLAYERS; i++) {
                if (!game.playeringame[i]) {
                    continue;
                }

                cnt_secret[i] += 2;

                if (cnt_secret[i] >= (plrs[i].ssecret * 100) / wbs.maxsecret) {
                    cnt_secret[i] = (plrs[i].ssecret * 100) / wbs.maxsecret;
                } else {
                    stillticking = true;
                }
            }

            if (!stillticking) {
                game.sound.S_StartSound(0, sfx_barexp);
                ng_state += 1 + 2 * dofrags>0?0:1;
            }
        } else if (ng_state == 8) {
            if (0==(bcnt & 3)) {
                game.sound.S_StartSound(0, sfx_pistol);
            }

            stillticking = false;

            for (i = 0; i < MAXPLAYERS; i++) {
                if (!game.playeringame[i]) {
                    continue;
                }

                cnt_frags[i] += 1;

                if (cnt_frags[i] >= (fsum = WI_fragSum(i))) {
                    cnt_frags[i] = fsum;
                } else {
                    stillticking = true;
                }
            }

            if (!stillticking) {
                game.sound.S_StartSound(0, sfx_pldeth);
                ng_state++;
            }
        } else if (ng_state == 10) {
            if (acceleratestage > 0) {
                game.sound.S_StartSound(0, sfx_sgcock);
                if (game.gameMode == Defines.GameMode.COMMERCIAL) {
                    WI_initNoState();
                } else {
                    WI_initShowNextLoc();
                }
            }
        } else if ((ng_state & 1) > 0) {
            cnt--;
            if (0 == cnt_pause) {
                ng_state++;
                cnt_pause = TICRATE;
            }
        }
    }



    void WI_drawNetgameStats() {
        int		i;
        int		x;
        int		y;
        int		pwidth = percent.width;

        WI_slamBackground();

        // draw animated background
        WI_drawAnimatedBack(); 

        WI_drawLF();
        NG_STATSX = (32 + star.width / 2 + 32 * (dofrags>0? 0 : 1));

        // draw stat titles (top line)
        game.renderer.video.drawPatch(NG_STATSX+NG_SPACINGX-kills.width,
                    NG_STATSY, FB, kills);

        game.renderer.video.drawPatch(NG_STATSX+2*NG_SPACINGX-items.width,
                    NG_STATSY, FB, items);

        game.renderer.video.drawPatch(NG_STATSX+3*NG_SPACINGX-secret.width,
                    NG_STATSY, FB, secret);

        if (dofrags>0) {
            game.renderer.video.drawPatch(NG_STATSX+4*NG_SPACINGX-frags.width,
                    NG_STATSY, FB, frags);
        }

        // draw stats
        y = NG_STATSY + kills.height;

        for (i=0 ; i<MAXPLAYERS ; i++)
        {
            if (!game.playeringame[i]) {
                continue;
            }

            x = NG_STATSX;
            game.renderer.video.drawPatch(x-p[i].width, y, FB, p[i]);

            if (i == me) {
                game.renderer.video.drawPatch(x-p[i].width, y, FB, star);
            }

            x += NG_SPACINGX;
            WI_drawPercent(x-pwidth, y+10, cnt_kills[i]);	x += NG_SPACINGX;
            WI_drawPercent(x-pwidth, y+10, cnt_items[i]);	x += NG_SPACINGX;
            WI_drawPercent(x-pwidth, y+10, cnt_secret[i]);	x += NG_SPACINGX;

            if (dofrags>0) {
                WI_drawNum(x, y+10, cnt_frags[i], -1);
            }

            y += WI_SPACINGY;
        }

    }

    int	sp_state;

    void WI_initStats() {
        state = stateenum_t.StatCount;
        acceleratestage = 0;
        sp_state = 1;
        cnt_kills[0] = -1;
        cnt_items[0] = -1;
        cnt_secret[0] = -1;
        cnt_time = -1;
        cnt_par = -1;
        cnt_pause = TICRATE;

        WI_initAnimatedBack();
    }

    void WI_updateStats() {

        WI_updateAnimatedBack();

        if (acceleratestage>0 && sp_state != 10) {
            acceleratestage = 0;
            cnt_kills[0] = (plrs[me].skills * 100) / wbs.maxkills;
            cnt_items[0] = (plrs[me].sitems * 100) / wbs.maxitems;
            cnt_secret[0] = (plrs[me].ssecret * 100) / wbs.maxsecret;
            cnt_time = plrs[me].stime / TICRATE;
            cnt_par = wbs.partime / TICRATE;
            game.sound.S_StartSound(0, sfx_barexp);
            sp_state = 10;
        }

        if (sp_state == 2) {
            cnt_kills[0] += 2;

            if (0==(bcnt & 3)) {
                game.sound.S_StartSound(0, sfx_pistol);
            }

            if (cnt_kills[0] >= (plrs[me].skills * 100) / wbs.maxkills) {
                cnt_kills[0] = (plrs[me].skills * 100) / wbs.maxkills;
                game.sound.S_StartSound(0, sfx_barexp);
                sp_state++;
            }
        } else if (sp_state == 4) {
            cnt_items[0] += 2;

            if (0==(bcnt & 3)) {
                game.sound.S_StartSound(0, sfx_pistol);
            }

            if (cnt_items[0] >= (plrs[me].sitems * 100) / wbs.maxitems) {
                cnt_items[0] = (plrs[me].sitems * 100) / wbs.maxitems;
                game.sound.S_StartSound(0, sfx_barexp);
                sp_state++;
            }
        } else if (sp_state == 6) {
            cnt_secret[0] += 2;

            if (0==(bcnt & 3)) {
                game.sound.S_StartSound(0, sfx_pistol);
            }

            if (cnt_secret[0] >= (plrs[me].ssecret * 100) / wbs.maxsecret) {
                cnt_secret[0] = (plrs[me].ssecret * 100) / wbs.maxsecret;
                game.sound.S_StartSound(0, sfx_barexp);
                sp_state++;
            }
        } else if (sp_state == 8) {
            if (0==(bcnt & 3)) {
                game.sound.S_StartSound(0, sfx_pistol);
            }

            cnt_time += 3;

            if (cnt_time >= plrs[me].stime / TICRATE) {
                cnt_time = plrs[me].stime / TICRATE;
            }

            cnt_par += 3;

            if (cnt_par >= wbs.partime / TICRATE) {
                cnt_par = wbs.partime / TICRATE;

                if (cnt_time >= plrs[me].stime / TICRATE) {
                    game.sound.S_StartSound(0, sfx_barexp);
                    sp_state++;
                }
            }
        } else if (sp_state == 10) {
            if (acceleratestage>0) {
                game.sound.S_StartSound(0, sfx_sgcock);

                if (game.gameMode == Defines.GameMode.COMMERCIAL) {
                    WI_initNoState();
                } else {
                    WI_initShowNextLoc();
                }
            }
        } else if ((sp_state & 1)>0) {
            cnt_pause--;
            if (0==cnt_pause) {
                sp_state++;
                cnt_pause = TICRATE;
            }
        }

    }

    void WI_drawStats() {
        // line height
        int lh;	

        lh = (3*num[0].height)/2;

        WI_slamBackground();

        // draw animated background
        WI_drawAnimatedBack();

        WI_drawLF();

        game.renderer.video.drawPatch(SP_STATSX, SP_STATSY, FB, kills);
        WI_drawPercent(SCREENWIDTH - SP_STATSX, SP_STATSY, cnt_kills[0]);

        game.renderer.video.drawPatch(SP_STATSX, SP_STATSY+lh, FB, items);
        WI_drawPercent(SCREENWIDTH - SP_STATSX, SP_STATSY+lh, cnt_items[0]);

        game.renderer.video.drawPatch(SP_STATSX, SP_STATSY+2*lh, FB, sp_secret);
        WI_drawPercent(SCREENWIDTH - SP_STATSX, SP_STATSY+2*lh, cnt_secret[0]);

        game.renderer.video.drawPatch(SP_TIMEX, SP_TIMEY, FB, time);
        WI_drawTime(SCREENWIDTH/2 - SP_TIMEX, SP_TIMEY, cnt_time);

        if (wbs.epsd < 3)
        {
            game.renderer.video.drawPatch(SCREENWIDTH/2 + SP_TIMEX, SP_TIMEY, FB, par);
            WI_drawTime(SCREENWIDTH - SP_TIMEX, SP_TIMEY, cnt_par);
        }

    }

    void WI_checkForAccelerate() {
        int i;
        Player player;

        // check for button presses to skip delays
        for (i = 0; i < MAXPLAYERS; i++) {
            player = game.players[i];
            if (game.playeringame[i]) {
                if ((player.cmd.buttons & BT_ATTACK)>0) {
                    if (!player.attackdown) {
                        acceleratestage = 1;
                    }
                    player.attackdown = true;
                } else {
                    player.attackdown = false;
                }
                if ((player.cmd.buttons & BT_USE)>0) {
                    if (!player.usedown) {
                        acceleratestage = 1;
                    }
                    player.usedown = true;
                } else {
                    player.usedown = false;
                }
            }
        }
    }



    // Updates stuff each tick
    public void WI_Ticker() {
        // counter for general background animation
        bcnt++;

        if (bcnt == 1) {
            // intermission music
            if (game.gameMode == Defines.GameMode.COMMERCIAL) {
                game.sound.S_ChangeMusic(mus_dm2int, true);
            } else {
                game.sound.S_ChangeMusic(mus_inter, true);
            }
        }

        WI_checkForAccelerate();

        switch (state) {
            case StatCount:
                if (game.deathmatch>0) {
                    WI_updateDeathmatchStats();
                } else if (game.netgame) {
                    WI_updateNetgameStats();
                } else {
                    WI_updateStats();
                }
                break;

            case ShowNextLoc:
                WI_updateShowNextLoc();
                break;

            case NoState:
                WI_updateNoState();
                break;
        }

    }

    void WI_loadData() {
        int i;
        int j;
        String name;
        anim_t a;

        NG_STATSX = (32 + star.width / 2 + 32 * (dofrags>0? 0 : 1));
        
        if (game.gameMode == Defines.GameMode.COMMERCIAL) {
            name = "INTERPIC";
        } else {
            name = "WIMAP" + wbs.epsd;
        }

        if (game.gameMode == Defines.GameMode.RETAIL) {
            if (wbs.epsd == 3) {
                name = "INTERPIC";
            }
        }

        // background
        //bg = game.wad.getPatchByName(name, PU_CACHE);
        bg = game.wad.getPatchByName(name);
        game.renderer.video.drawPatch(0, 0, 1, bg);

        // UNUSED unsigned char *pic = screens[1];
        // if (game.gameMode == Defines.GameMode.COMMERCIAL)
        // {
        // darken the background image
        // while (pic != screens[1] + SCREENHEIGHT*SCREENWIDTH)
        // {
        //   *pic = colormaps[256*25 + *pic];
        //   pic++;
        // }
        //}
        if (game.gameMode == Defines.GameMode.COMMERCIAL) {
            NUMCMAPS = 32;
//            lnames = (patch_t **) Z_Malloc(sizeof(Patch) * NUMCMAPS,
//                                           PU_STATIC, 0);
            lnames = new PatchData[NUMCMAPS];
            for (i = 0; i < NUMCMAPS; i++) {
                //name = String.format("CWILV%2.2d", i);
                name = String.format("CWILV%2d", i);
                //lnames[i] = game.wad.getPatchByName(name);
                lnames[i] = game.wad.getPatchByName(name);
            }
        } else {
//            lnames = (patch_t **) Z_Malloc(sizeof(Patch) * NUMMAPS,
//                                           PU_STATIC, 0);
            lnames = new PatchData[NUMMAPS];
            for (i = 0; i < NUMMAPS; i++) {
                //name = String.format("WILV%d%d", wbs.epsd, i);
                name = String.format("WILV%d%d", wbs.epsd, i);
                lnames[i] = game.wad.getPatchByName(name);
            }

            // you are here
            yah[0] = game.wad.getPatchByName("WIURH0");

            // you are here (alt.)
            yah[1] = game.wad.getPatchByName("WIURH1");

            // splat
            splat[0] = game.wad.getPatchByName("WISPLAT");
            splat[1] = game.wad.getPatchByName("WISPLAT");

            if (wbs.epsd < 3) {
                for (j = 0; j < NUMANIMS[wbs.epsd]; j++) {
                    a = anims[wbs.epsd][j];
                    for (i = 0; i < a.nanims; i++) {
                        // MONDO HACK!
                        if (wbs.epsd != 1 || j != 8) {
                            // animations
                            name = String.format("WIA%d%2d%2d", wbs.epsd, j, i);
                            a.p[i] = game.wad.getPatchByName(name);
                        } else {
                            // HACK ALERT!
                            a.p[i] = anims[1][4].p[i];
                        }
                    }
                }
            }
        }

        // More hacks on minus sign.
        wiminus = game.wad.getPatchByName("WIMINUS");

        for (i = 0; i < 10; i++) {
            // numbers 0-9
            name = String.format("WINUM%d", i);
            num[i] = game.wad.getPatchByName(name);
        }

        // percent sign
        percent = game.wad.getPatchByName("WIPCNT");

        // "finished"
        finished = game.wad.getPatchByName("WIF");

        // "entering"
        entering = game.wad.getPatchByName("WIENTER");

        // "kills"
        kills = game.wad.getPatchByName("WIOSTK");

        // "scrt"
        secret = game.wad.getPatchByName("WIOSTS");

        // "secret"
        sp_secret = game.wad.getPatchByName("WISCRT2");

        // Yuck. 
        if (Game.language.equals("fr")) {
            // "items"
            if (game.netgame && 0 == game.deathmatch) {
                items = game.wad.getPatchByName("WIOBJ");
            } else {
                items = game.wad.getPatchByName("WIOSTI");
            }
        } else {
            items = game.wad.getPatchByName("WIOSTI");
        }

        // "frgs"
        frags = game.wad.getPatchByName("WIFRGS");

        // ":"
        colon = game.wad.getPatchByName("WICOLON");

        // "time"
        time = game.wad.getPatchByName("WITIME");

        // "sucks"
        sucks = game.wad.getPatchByName("WISUCKS");

        // "par"
        par = game.wad.getPatchByName("WIPAR");

        // "killers" (vertical)
        killers = game.wad.getPatchByName("WIKILRS");

        // "victims" (horiz)
        victims = game.wad.getPatchByName("WIVCTMS");

        // "total"
        total = game.wad.getPatchByName("WIMSTT");

        // your face
        star = game.wad.getPatchByName("STFST01");

        // dead face
        bstar = game.wad.getPatchByName("STFDEAD0");

        for (i = 0; i < MAXPLAYERS; i++) {
            // "1,2,3,4"
            name = String.format("STPB%d", i);
            p[i] = game.wad.getPatchByName(name);

            // "1,2,3,4"
            name = String.format("WIBP%d", i + 1);
            bp[i] = game.wad.getPatchByName(name);
        }

    }

    void WI_unloadData() {
//        int		i;
//        int		j;
//
//        Z_ChangeTag(wiminus, PU_CACHE);
//
//        for (i=0 ; i<10 ; i++)
//            Z_ChangeTag(num[i], PU_CACHE);
//
//        if (game.gameMode == Defines.GameMode.COMMERCIAL)
//        {
//            for (i=0 ; i<NUMCMAPS ; i++)
//                Z_ChangeTag(lnames[i], PU_CACHE);
//        }
//        else
//        {
//            Z_ChangeTag(yah[0], PU_CACHE);
//            Z_ChangeTag(yah[1], PU_CACHE);
//
//            Z_ChangeTag(splat, PU_CACHE);
//
//            for (i=0 ; i<NUMMAPS ; i++)
//                Z_ChangeTag(lnames[i], PU_CACHE);
//
//            if (wbs.epsd < 3)
//            {
//                for (j=0;j<NUMANIMS[wbs.epsd];j++)
//                {
//                    if (wbs.epsd != 1 || j != 8)
//                        for (i=0;i<anims[wbs.epsd][j].nanims;i++)
//                            Z_ChangeTag(anims[wbs.epsd][j].p[i], PU_CACHE);
//                }
//            }
//        }
//
//        Z_Free(lnames);
//
//        Z_ChangeTag(percent, PU_CACHE);
//        Z_ChangeTag(colon, PU_CACHE);
//        Z_ChangeTag(finished, PU_CACHE);
//        Z_ChangeTag(entering, PU_CACHE);
//        Z_ChangeTag(kills, PU_CACHE);
//        Z_ChangeTag(secret, PU_CACHE);
//        Z_ChangeTag(sp_secret, PU_CACHE);
//        Z_ChangeTag(items, PU_CACHE);
//        Z_ChangeTag(frags, PU_CACHE);
//        Z_ChangeTag(time, PU_CACHE);
//        Z_ChangeTag(sucks, PU_CACHE);
//        Z_ChangeTag(par, PU_CACHE);
//
//        Z_ChangeTag(victims, PU_CACHE);
//        Z_ChangeTag(killers, PU_CACHE);
//        Z_ChangeTag(total, PU_CACHE);
//        //  Z_ChangeTag(star, PU_CACHE);
//        //  Z_ChangeTag(bstar, PU_CACHE);
//
//        for (i=0 ; i<MAXPLAYERS ; i++)
//            Z_ChangeTag(p[i], PU_CACHE);
//
//        for (i=0 ; i<MAXPLAYERS ; i++)
//            Z_ChangeTag(bp[i], PU_CACHE);
    }

    public void WI_Drawer() {
        switch (state) {
            case StatCount:
                if (game.deathmatch > 0) {
                    WI_drawDeathmatchStats();
                } else if (game.netgame) {
                    WI_drawNetgameStats();
                } else {
                    WI_drawStats();
                }
                break;

            case ShowNextLoc:
                WI_drawShowNextLoc();
                break;

            case NoState:
                WI_drawNoState();
                break;
        }
    }

    void WI_initVariables(WbStart wbstartstruct) {

        wbs = wbstartstruct;

//    #ifdef RANGECHECKING
//        if (game.gameMode != Defines.GameMode.COMMERCIAL)
//        {
//          if ( game.gameMode == retail )
//            RNGCHECK(wbs.epsd, 0, 3);
//          else
//            RNGCHECK(wbs.epsd, 0, 2);
//        }
//        else
//        {
//            RNGCHECK(wbs.last, 0, 8);
//            RNGCHECK(wbs.next, 0, 8);
//        }
//        RNGCHECK(wbs.pnum, 0, MAXPLAYERS);
//        RNGCHECK(wbs.pnum, 0, MAXPLAYERS);
//    #endif
        acceleratestage = 0;
        cnt = 0;
        bcnt = 0;
        firstrefresh = 1;
        me = wbs.pnum;
        plrs = wbs.plyr;

        if (0 == wbs.maxkills) {
            wbs.maxkills = 1;
        }

        if (0 == wbs.maxitems) {
            wbs.maxitems = 1;
        }

        if (0 == wbs.maxsecret) {
            wbs.maxsecret = 1;
        }

        if (game.gameMode != Defines.GameMode.RETAIL) {
            if (wbs.epsd > 2) {
                wbs.epsd -= 3;
            }
        }
    }

    public void WI_Start(WbStart wbstartstruct) {

        WI_initVariables(wbstartstruct);
        WI_loadData();

        if (game.deathmatch>0) {
            WI_initDeathmatchStats();
        } else if (game.netgame) {
            WI_initNetgameStats();
        } else {
            WI_initStats();
        }
    }

}
