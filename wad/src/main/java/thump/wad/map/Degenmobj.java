/*
    Each sector has a degenmobj_t in its center for sound origin purposes.
    I suppose this does not handle sound from moving objects  (doppler), 
    because position is prolly just buffered, not updated.
 */
package thump.wad.map;

import java.text.MessageFormat;
import thump.base.Defines.SpriteNum;

/**
 *
 * @author mark
 */
public class Degenmobj {
    public int x=0;
    public int y=0;
    public int z=0;
    
    public long	angle;	// orientation
    public long	frame;	// might be ORed with FF_FULLBRIGHT
    public int  flags;
    //More drawing info: to determine current sprite.
    public SpriteNum	sprite;	// used to find patch_t and flip value

    // More list: links in sector (if needed)
    public Degenmobj	snext = null;
    public Degenmobj	sprev = null;

    //public Thinker thinker=null;

    @Override
    public String toString() {
        return MessageFormat.format(
                "    Degenmobj:  x:{0}  y:{1}  z:{2}   angle:{3}  frame:{4}  flags:{5}  sprite:{6}\n        list:{7}",
                new Object[]{x,y,z,Long.toHexString(angle),frame,Integer.toHexString(flags),sprite, nextList()}
        );
                
    }

    private String nextList() {
        StringBuilder sb = new StringBuilder("this-->");
        Degenmobj next = snext;
        while (next!=null) {
            sb.append("  --> ").append(next.hashCode());
            next = next.snext;
        }
        return sb.toString();
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
