/*
 * Weapon Info
 */
package thump.game;

import thump.global.Defines.AmmoType;
import thump.global.State;

/**
 *
 * @author mark
 */
public class WeaponInfo {
    public final AmmoType       ammo;
    public final State.StateNum	upstate;
    public final State.StateNum	downstate;
    public final State.StateNum	readystate;
    public final State.StateNum	atkstate;
    public final State.StateNum	flashstate;

    public WeaponInfo(AmmoType ammoType, 
            State.StateNum upstate, 
            State.StateNum downstate, 
            State.StateNum readystate, 
            State.StateNum atkstate, 
            State.StateNum flashstate) {
        this.ammo = ammoType;
        this.upstate = upstate;
        this.downstate = downstate;
        this.readystate = readystate;
        this.atkstate = atkstate;
        this.flashstate = flashstate;
    }
 }
