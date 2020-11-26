/*
 * Player Object
 */
package thump.game;

import java.util.Arrays;
import thump.base.Defines.SpriteNum;
import thump.game.maplevel.MapObject;
import thump.game.play.PSprite;

/**
 *
 * @author mark
 */
public final class Player {

    public Player() {
        clearSettings();
    }
    
    /**
     * Player states.
     */
    public static enum PlayerState {
        PST_LIVE,   // Playing or camping.
        PST_DEAD,   // Dead on the ground, view follows killer.
        PST_REBORN  // Ready to restart/respawn???		
    }

    /**
     * Player internal flags, for cheats and debug.
     */
    public static enum Cheat {
        CF_NOCLIP(1),       // No clipping, walk through barriers.
        CF_GODMODE(2),      // No damage, no health loss.
        CF_NOMOMENTUM(4);   // Not really a cheat, just a debug aid.

        private final int value;

        private Cheat(int value ) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }

    // Extended player object info
    //
    public MapObject    mo;
    public PlayerState  playerstate;
    public TickCommand  cmd;

    //  Determine POV,
    //  including viewpoint bobbing during movement.
    public int      viewz;              // Focal origin above r.z
    public int      viewheight;         // Base height above floor for viewz.
    public int      deltaviewheight;    // Bob/squat speed.
    public int      bob;                // bounded/scaled total momentum.

    public Integer  health;             // This is only used between levels,
    public Integer  armorpoints;        // mo.health is used during levels.

    public int      armortype;          // Armor type is 0-2.	

    public int      powers[]    = new int[Defines.PowerType.values().length]; // Power ups. invinc and invis are tic counters.
    public boolean  cards[]     = new boolean[Defines.Card.values().length];
    public boolean  backpack;

    public int      frags[]     = new int[Defines.MAXPLAYERS];    // Frags, kills of other players.

    public Defines.WeaponType readyweapon;
    public Defines.WeaponType pendingweapon;    // Is wp_nochange if not changing.

    public Boolean  weaponowned[] = new Boolean[Defines.WeaponType.values().length];
    public Integer  ammo[]        = new Integer[Defines.AmmoType.values().length];
    public Integer  maxammo[]     = new Integer[Defines.AmmoType.values().length];

    // True if button down last tic.
    public boolean  attackdown;
    public boolean  usedown;

    public int      cheats;         // Bit flags, for cheats and debug. See cheat_t, above.

    public int      refire;		// Refired shots are less accurate.

    public int      killcount;      // For intermission stats.
    public int      itemcount;
    public int      secretcount;

    public String   message;        // Hint messages.	

    public int      damagecount;    // For screen flashing (red or bright).
    public int      bonuscount;

    public MapObject attacker;       // Who did damage (NULL for floors/ceilings).

    public int      extralight;     // So gun flashes light up areas.

    public int      fixedcolormap;  // Current PLAYPAL, ???  Can be set to REDCOLORMAP for pain, etc.

    // Player skin colorshift,
    //  0-3 for which color to draw player.
    public int      colormap;

    // Overlay view sprites (gun, etc).
    public PSprite psprites[] = new PSprite[SpriteNum.values().length];

    // True if secret level has been done.
    public boolean didsecret;

    void clearSettings() {
        mo = null;
        playerstate = PlayerState.PST_DEAD;
        cmd = new TickCommand();
        viewz = 0;
        viewheight = 0;
        deltaviewheight = 0;
        bob = 0;
        health = 0;
        armorpoints = 0;
        armortype = 0;
        Arrays.fill(powers, 0);
        Arrays.fill(cards, false);
        backpack = false;
        Arrays.fill(frags, 0);
        readyweapon = Defines.WeaponType.wp_fist;
        pendingweapon = Defines.WeaponType.wp_fist;
        Arrays.fill(weaponowned, false);
        Arrays.fill(ammo, 0);
        Arrays.fill(maxammo, 0);
        attackdown = false;
        usedown = false;
        cheats = 0;
        refire = 0;
        killcount = 0;
        itemcount = 0;
        secretcount = 0;
        message = "";
        damagecount = 0;
        bonuscount = 0;
        attacker = null;
        extralight = 0;
        fixedcolormap = 0;
        colormap = 0;
        Arrays.fill(psprites, new PSprite());
        didsecret = false;
    }
    
    public void setFrags( int[] f ) {
        if ( f.length != this.frags.length ) {
            //SystemInterface.I_Error("set Frags count does not match!");
            thump.base.Defines.logger.severe("set Frags count does not match!");
        }
        
        System.arraycopy(f, 0, frags, 0, f.length);
    }

}
