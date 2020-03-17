/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.global;

import thump.global.State.StateNum;
import thump.sound.sfx.Sounds;

/**
 *
 * @author mark
 */
public class MobJInfo {

    public MobJInfo(
            int doomednum, StateNum spawnstate, int spawnhealth,
            StateNum seestate, Sounds.SfxEnum seesound, int reactiontime,
            Sounds.SfxEnum attacksound, StateNum painstate, int painchance,
            Sounds.SfxEnum painsound, StateNum meleestate, StateNum missilestate,
            StateNum deathstate, StateNum xdeathstate, Sounds.SfxEnum deathsound,
            int speed, int radius, int height, int mass,
            int damage, Sounds.SfxEnum activesound, int flags, StateNum raisestate
    ) {
        this.doomednum = doomednum;
        this.spawnstate = spawnstate;
        this.spawnhealth = spawnhealth;
        this.seestate = seestate;
        this.seesound = seesound;
        this.reactiontime = reactiontime;
        this.attacksound = attacksound;
        this.painstate = painstate;
        this.painchance = painchance;
        this.painsound = painsound;
        this.meleestate = meleestate;
        this.missilestate = missilestate;
        this.deathstate = deathstate;
        this.xdeathstate = xdeathstate;
        this.deathsound = deathsound;
        this.speed = speed;
        this.radius = radius;
        this.height = height;
        this.mass = mass;
        this.damage = damage;
        this.activesound = activesound;
        this.flags = flags;
        this.raisestate = raisestate;
    }

    public int              doomednum;
    public StateNum         spawnstate;
    public int              spawnhealth;
    public StateNum         seestate;
    public Sounds.SfxEnum   seesound;
    public int              reactiontime;
    public Sounds.SfxEnum   attacksound;
    public StateNum         painstate;
    public int              painchance;
    public Sounds.SfxEnum   painsound;
    public StateNum         meleestate;
    public StateNum         missilestate;
    public StateNum         deathstate;
    public StateNum         xdeathstate;
    public Sounds.SfxEnum   deathsound;
    public int speed;
    public int radius;
    public int height;
    public int mass;
    public int damage;
    public Sounds.SfxEnum activesound;
    public int flags;
    public StateNum raisestate;

    public enum Type {
        MT_PLAYER,
        MT_POSSESSED,
        MT_SHOTGUY,
        MT_VILE,
        MT_FIRE,
        MT_UNDEAD,
        MT_TRACER,
        MT_SMOKE,
        MT_FATSO,
        MT_FATSHOT,
        MT_CHAINGUY,
        MT_TROOP,
        MT_SERGEANT,
        MT_SHADOWS,
        MT_HEAD,
        MT_BRUISER,
        MT_BRUISERSHOT,
        MT_KNIGHT,
        MT_SKULL,
        MT_SPIDER,
        MT_BABY,
        MT_CYBORG,
        MT_PAIN,
        MT_WOLFSS,
        MT_KEEN,
        MT_BOSSBRAIN,
        MT_BOSSSPIT,
        MT_BOSSTARGET,
        MT_SPAWNSHOT,
        MT_SPAWNFIRE,
        MT_BARREL,
        MT_TROOPSHOT,
        MT_HEADSHOT,
        MT_ROCKET,
        MT_PLASMA,
        MT_BFG,
        MT_ARACHPLAZ,
        MT_PUFF,
        MT_BLOOD,
        MT_TFOG,
        MT_IFOG,
        MT_TELEPORTMAN,
        MT_EXTRABFG,
        MT_MISC0,
        MT_MISC1,
        MT_MISC2,
        MT_MISC3,
        MT_MISC4,
        MT_MISC5,
        MT_MISC6,
        MT_MISC7,
        MT_MISC8,
        MT_MISC9,
        MT_MISC10,
        MT_MISC11,
        MT_MISC12,
        MT_INV,
        MT_MISC13,
        MT_INS,
        MT_MISC14,
        MT_MISC15,
        MT_MISC16,
        MT_MEGA,
        MT_CLIP,
        MT_MISC17,
        MT_MISC18,
        MT_MISC19,
        MT_MISC20,
        MT_MISC21,
        MT_MISC22,
        MT_MISC23,
        MT_MISC24,
        MT_MISC25,
        MT_CHAINGUN,
        MT_MISC26,
        MT_MISC27,
        MT_MISC28,
        MT_SHOTGUN,
        MT_SUPERSHOTGUN,
        MT_MISC29,
        MT_MISC30,
        MT_MISC31,
        MT_MISC32,
        MT_MISC33,
        MT_MISC34,
        MT_MISC35,
        MT_MISC36,
        MT_MISC37,
        MT_MISC38,
        MT_MISC39,
        MT_MISC40,
        MT_MISC41,
        MT_MISC42,
        MT_MISC43,
        MT_MISC44,
        MT_MISC45,
        MT_MISC46,
        MT_MISC47,
        MT_MISC48,
        MT_MISC49,
        MT_MISC50,
        MT_MISC51,
        MT_MISC52,
        MT_MISC53,
        MT_MISC54,
        MT_MISC55,
        MT_MISC56,
        MT_MISC57,
        MT_MISC58,
        MT_MISC59,
        MT_MISC60,
        MT_MISC61,
        MT_MISC62,
        MT_MISC63,
        MT_MISC64,
        MT_MISC65,
        MT_MISC66,
        MT_MISC67,
        MT_MISC68,
        MT_MISC69,
        MT_MISC70,
        MT_MISC71,
        MT_MISC72,
        MT_MISC73,
        MT_MISC74,
        MT_MISC75,
        MT_MISC76,
        MT_MISC77,
        MT_MISC78,
        MT_MISC79,
        MT_MISC80,
        MT_MISC81,
        MT_MISC82,
        MT_MISC83,
        MT_MISC84,
        MT_MISC85,
        MT_MISC86
    }

}
