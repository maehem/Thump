/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.game.play;

import static thump.base.FixedPoint.FRACBITS;
import static thump.base.FixedPoint.FRACUNIT;

/**
 *
 * @author mark
 */
public class Local {
    public static final int FLOATSPEED      = (FRACUNIT * 4);

    public static final int MAXHEALTH       = 100;
    public static final int VIEWHEIGHT      = (41 * FRACUNIT);

    // mapblocks are used to check movement
    // against lines and things
    public static final int MAPBLOCKUNITS   = 128;
    public static final int MAPBLOCKSIZE    = (MAPBLOCKUNITS * FRACUNIT);
    public static final int MAPBLOCKSHIFT   = (FRACBITS + 7);
    public static final int MAPBMASK        = (MAPBLOCKSIZE - 1);
    public static final int MAPBTOFRAC      = (MAPBLOCKSHIFT - FRACBITS);

    // player radius for movement checking
    public static final int PLAYERRADIUS    = 16 * FRACUNIT;

    // MAXRADIUS is for precalculated sector block boxes
    // the spider demon is larger,
    // but we do not have any moving sectors nearby
    public static final int MAXRADIUS       = 32 * FRACUNIT;

    public static final int GRAVITY         = FRACUNIT;
    public static final int MAXMOVE         = (30 * FRACUNIT);

    public static final int USERANGE        = (64 * FRACUNIT);
    public static final int MELEERANGE      = (64 * FRACUNIT);
    public static final int MISSILERANGE    = (32 * 64 * FRACUNIT);

    // follow a player exlusively for 3 seconds
    public static final int BASETHRESHOLD   = 100;
}
