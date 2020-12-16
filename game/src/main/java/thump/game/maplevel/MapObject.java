/*
    Map Object

    NOTES: mobj_t

    mobj_ts are used to tell the refresh where to draw an image, tell the world
    simulation when objects are contacted, and tell the sound driver how to 
    position a sound.

    The refresh uses the next and prev links to follow lists of things in '
    sectors as they are being drawn.
    
    The sprite, frame, and angle elements determine which patch_t is used to 
    draw the sprite if it is visible.  The sprite and frame values are allmost 
    always set from state_t structures.
    
    The statescr.exe utility generates the states.h and states.c files that 
    contain the sprite/frame numbers from the statescr.txt source file.
    
    The xyz origin point represents a point at the bottom middle of the sprite 
    (between the feet of a biped). This is the default origin position for 
    patch_ts grabbed with lumpy.exe. A walking creature will have its z equal to 
    the floor it is standing on.

    The sound code uses the x,y, and subsector fields to do stereo positioning 
    of any sound effited by the mobj_t.

    The play simulation uses the blocklinks, x,y,z, radius, height to determine 
    when mobj_ts are touching each other, touching lines in the map, or hit by 
    trace lines (gunshots, lines of sight, etc).

    The mobj_t.options element has various bit options used by the simulation.

    Every mobj_t is linked into a single sector based on its origin coordinates.

    The subsector_t is found with R_PointInSubsector(x,y), and the sector_t can 
    be found with subsector.sector.

    The sector links are only used by the rendering code. The play simulation 
    does not care about them at all.

    Any mobj_t that needs to be acted upon by something else in the play world 
    (block movement, be shot, etc) will also need to be linked into the blockmap.

    If the thing has the MF_NOBLOCK flag set, it will not use the block links. 
    It can still interact with other things, but only as the instigator 
    (missiles will run into other things, but nothing can run into a missile).
    
    Each block in the grid is 128*128 units, and knows about every line_t that 
    it contains a piece of, and every interactable mobj_t that has its origin 
    contained.  

    A valid mobj_t is a mobj_t that has the proper subsector_t filled in for its
    xy coordinates and is linked into the sector from which the subsector was 
    made, or has the MF_NOSECTOR flag set (the subsector_t needs to be valid
    even if MF_NOSECTOR is set), and is linked into a blockmap block or has the 
    MF_NOBLOCKMAP flag set.
    
    Links should only be modified by the P_[Un]SetThingPosition() functions.
    Do not change the MF_NO? options while a thing is valid.

    Any questions?

 */package thump.game.maplevel;

import thump.game.Enemy.DirType;
import thump.game.MobJInfo;
import thump.game.Player;
import thump.game.State;
import thump.game.ThingStateLUT;
 
import thump.wad.map.Degenmobj;
import thump.wad.map.Thinker;
import thump.wad.map.ThinkerAction;
import thump.wad.mapraw.MapThing;

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

    
    public MapSubSector	subsector;

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
    
    // TODO:  Are these the same???
    public State state = ThingStateLUT.states[0];
    //public State stateNum;
    
    
    public int			health;

    // Movement direction, movement generation (zig-zagging).
    public DirType		movedir;	// 0-7
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

    // Interaction info, by BLOCKMAP.
    // Links in blocks (if needed).
    public MapObject	bnext;
    public MapObject	bprev;

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
        
}
