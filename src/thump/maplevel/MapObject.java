/*
    Map Object

     NOTES: mobj_t

     mobj_ts are used to tell the refresh where to draw an image,
     tell the world simulation when objects are contacted,
     and tell the sound driver how to position a sound.

     The refresh uses the next and prev links to follow
     lists of things in sectors as they are being drawn.
     The sprite, frame, and angle elements determine which patch_t
     is used to draw the sprite if it is visible.
     The sprite and frame values are allmost allways set
     from state_t structures.
     The statescr.exe utility generates the states.h and states.c
     files that contain the sprite/frame numbers from the
     statescr.txt source file.
     The xyz origin point represents a point at the bottom middle
     of the sprite (between the feet of a biped).
     This is the default origin position for patch_ts grabbed
     with lumpy.exe.
     A walking creature will have its z equal to the floor
     it is standing on.

     The sound code uses the x,y, and subsector fields
     to do stereo positioning of any sound effited by the mobj_t.

     The play simulation uses the blocklinks, x,y,z, radius, height
     to determine when mobj_ts are touching each other,
     touching lines in the map, or hit by trace lines (gunshots,
     lines of sight, etc).
     The mobj_t.options element has various bit options
     used by the simulation.

     Every mobj_t is linked into a single sector
     based on its origin coordinates.
     The subsector_t is found with R_PointInSubsector(x,y),
     and the sector_t can be found with subsector.sector.
     The sector links are only used by the rendering code,
     the play simulation does not care about them at all.

     Any mobj_t that needs to be acted upon by something else
     in the play world (block movement, be shot, etc) will also
     need to be linked into the blockmap.
     If the thing has the MF_NOBLOCK flag set, it will not use
     the block links. It can still interact with other things,
     but only as the instigator (missiles will run into other
     things, but nothing can run into a missile).
     Each block in the grid is 128*128 units, and knows about
     every line_t that it contains a piece of, and every
     interactable mobj_t that has its origin contained.  

     A valid mobj_t is a mobj_t that has the proper subsector_t
     filled in for its xy coordinates and is linked into the
     sector from which the subsector was made, or has the
     MF_NOSECTOR flag set (the subsector_t needs to be valid
     even if MF_NOSECTOR is set), and is linked into a blockmap
     block or has the MF_NOBLOCKMAP flag set.
     Links should only be modified by the P_[Un]SetThingPosition()
     functions.
     Do not change the MF_NO? options while a thing is valid.

     Any questions?

 */package thump.maplevel;

import thump.game.Enemy.DirType;
import thump.game.Player;
import thump.game.Thinker;
import thump.game.ThinkerAction;
import thump.global.MobJInfo;
import thump.global.State;
import thump.global.ThingStateLUT;
import thump.render.Degenmobj;
import thump.render.SubSector;

/**
 *
 * @author mark
 */
public class MapObject extends Degenmobj implements Thinker {
    //
    // P_MOBJ
    //
    public final static int ONFLOORZ = Integer.MIN_VALUE;
    public final static int ONCEILINGZ = Integer.MAX_VALUE;


    // List: thinker links.
    //public Thinker	thinker;

    // Info for drawing: position.
    //public int		x;
    //public int		y;
    //public int		z;

    // More list: links in sector (if needed)
    public MapObject	snext = null;
    public MapObject	sprev = null;

    //More drawing info: to determine current sprite.
    public int		angle;	// orientation
    public State.SpriteNum	sprite;	// used to find patch_t and flip value
    public long			frame;	// might be ORed with FF_FULLBRIGHT

    // Interaction info, by BLOCKMAP.
    // Links in blocks (if needed).
    public MapObject	bnext;
    public MapObject	bprev;
    
    public SubSector	subsector;

    // The closest interval over all contacted Sectors.
    public int		floorz;
    public int		ceilingz;

    // For movement checking.
    public int		radius;
    public int		height;	

    // Momentums, used to update position.
    public int		momx;
    public int		momy;
    public int		momz;

    // If == validcount, already checked.
    public int			validcount;

    public MobJInfo.Type	type;
    public MobJInfo		info;	// &mobjinfo[mobj.type]
    
    public long			tics;	// state tic counter
    public State		state = ThingStateLUT.states[0];
    public int			flags;
    public int			health;

    // Movement direction, movement generation (zig-zagging).
    public DirType			movedir;	// 0-7
    public int			movecount;	// when 0, select a new dir

    // Thing being chased/attacked (or NULL),
    // also the originator for missiles.
    public MapObject	target;

    // Reaction time: if non 0, don't attack yet.
    // Used by player to freeze a bit after teleporting.
    public int		reactiontime;   

    // If >0, the target will be chased
    // no matter what (even if shot)
    public int		threshold;

    // Additional info record for player avatars only.
    // Only valid if type == MT_PLAYER
    public Player	player;

    // Player number last looked for.
    public int		lastlook;	

    // For nightmare respawn.
    public MapThing	spawnpoint;	

    // Thing being chased/attacked for tracers.
    public MapObject	tracer;	
    
    private Thinker prevThinker;
    private Thinker nextThinker;
    private ThinkerAction function;
    public State stateNum;

    @Override
    public void setPrevThinker(Thinker thinker) {
        this.prevThinker = thinker;
    }

    @Override
    public Thinker getPrevThinker() {
        return prevThinker;
    }

    @Override
    public void setNextThinker(Thinker thinker) {
        this.nextThinker = thinker;
    }

    @Override
    public Thinker getNextThinker() {
        return nextThinker;
    }

    @Override
    public void setFunction(ThinkerAction function) {
        this.function = function;
    }

    @Override
    public ThinkerAction getFunction() {
        return function;
    }
        
    public enum MobileObjectFlag {
        MF_SPECIAL(1),          // Call P_SpecialThing when touched.
        MF_SOLID(2),            // Blocks.
        MF_SHOOTABLE(4),        // Can be hit.
        MF_NOSECTOR(8),         // Don't use the sector links (invisible but touchable).
        MF_NOBLOCKMAP(16),      // Don't use the blocklinks (inert but displayable)
        MF_AMBUSH(32),          // Not to be activated by sound, deaf monster.
        MF_JUSTHIT(64),         // Will try to attack right back.
        MF_JUSTATTACKED(128),   // Will take at least one step before attacking.
        MF_SPAWNCEILING(256),   // On level spawning (initial position),
                                //     hang from ceiling instead of stand on floor.
        MF_NOGRAVITY(512),      // Don't apply gravity (every tic),
                                //    that is, object will float, keeping current height
                                //    or changing it actively.

        MF_DROPOFF(0x400),      // Movement options. This allows jumps from high places.
        MF_PICKUP(0x800),       // For players, will pick up items.
        MF_NOCLIP(0x1000),      // Player cheat. ???
        MF_SLIDE(0x2000),       // Player: keep info about sliding along walls.
        MF_FLOAT(0x4000),       // Allow moves to any height, no gravity.
                                //     For active floaters, e.g. cacodemons, pain elementals.

        MF_TELEPORT(0x8000),    // Don't cross lines ??? or look at heights on teleport.
        MF_MISSILE(0x10000),    // Don't hit same species, explode on block.
                                //      Player missiles as well as fireballs of various kinds.

        MF_DROPPED(0x20000),    // Dropped by a demon, not level spawned.
                                //      E.g. ammo clips dropped by dying former humans.

        MF_SHADOW(0x40000),     // Use fuzzy draw (shadow demons or spectres),
                                //      temporary player invisibility powerup.

        MF_NOBLOOD(0x80000),    // Flag: don't bleed when shot (use puff),
                                //      barrels and shootable furniture shall not bleed.

        MF_CORPSE(0x100000),    // Don't stop moving halfway off a step,
                                //  that is, have dead bodies slide down all the way.

        MF_INFLOAT(0x200000),   // Floating to a height for a move, ???
                                //  don't auto float to target's height.

        MF_COUNTKILL(0x400000), // On kill, count this enemy object
                                //  towards intermission kill total.
                                // Happy gathering.

        MF_COUNTITEM(0x800000), // On picking up, count this item object
                                //  towards intermission item total.

        MF_SKULLFLY(0x1000000), // Special handling: skull in flight.
                                // Neither a cacodemon nor a missile.

        MF_NOTDMATCH(0x2000000),// Don't spawn this object
                                //  in death match mode (e.g. key cards).

        MF_TRANSLATION(0xc000000),// Player sprites in multiplayer modes are modified
                                //  using an internal color lookup table for re-indexing.
                                // If 0x4 0x8 or 0xc,
                                //  use a translation table for player colormaps

        MF_TRANSSHIFT(26) ;     // Hmm ???.

        private final int value;

        private MobileObjectFlag(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
