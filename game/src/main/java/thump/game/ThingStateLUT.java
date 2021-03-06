/*
 * Thing States
 */
package thump.game;

import static thump.base.FixedPoint.FRACUNIT;
import static thump.base.Defines.SpriteNum.*;
import static thump.game.State.StateNum.*;
import static thump.game.sound.sfx.Sounds.SfxEnum.*;
import static thump.wad.map.Degenmobj.MobileObjectFlag.*;
import thump.game.play.action.*;

/**
 *
 * @author mark
 */
public class ThingStateLUT {

public static final String sprnames[] = {
    "TROO","SHTG","PUNG","PISG","PISF","SHTF","SHT2","CHGG","CHGF","MISG",
    "MISF","SAWG","PLSG","PLSF","BFGG","BFGF","BLUD","PUFF","BAL1","BAL2",
    "PLSS","PLSE","MISL","BFS1","BFE1","BFE2","TFOG","IFOG","PLAY","POSS",
    "SPOS","VILE","FIRE","FATB","FBXP","SKEL","MANF","FATT","CPOS","SARG",
    "HEAD","BAL7","BOSS","BOS2","SKUL","SPID","BSPI","APLS","APBX","CYBR",
    "PAIN","SSWV","KEEN","BBRN","BOSF","ARM1","ARM2","BAR1","BEXP","FCAN",
    "BON1","BON2","BKEY","RKEY","YKEY","BSKU","RSKU","YSKU","STIM","MEDI",
    "SOUL","PINV","PSTR","PINS","MEGA","SUIT","PMAP","PVIS","CLIP","AMMO",
    "ROCK","BROK","CELL","CELP","SHEL","SBOX","BPAK","BFUG","MGUN","CSAW",
    "LAUN","PLAS","SHOT","SGN2","COLU","SMT2","GOR1","POL2","POL5","POL4",
    "POL3","POL1","POL6","GOR2","GOR3","GOR4","GOR5","SMIT","COL1","COL2",
    "COL3","COL4","CAND","CBRA","COL6","TRE1","TRE2","ELEC","CEYE","FSKU",
    "COL5","TBLU","TGRN","TRED","SMBT","SMGT","SMRT","HDB1","HDB2","HDB3",
    "HDB4","HDB5","HDB6","POB1","POB2","BRS1","TLMP","TLP2"
};


public static final State states[] = {
   new State(SPR_TROO,0,-1,null,S_NULL,0,0),          // S_NULL
   new State(SPR_SHTG,4,0,new A_Light0(),S_NULL,0,0),	// S_LIGHTDONE
   new State(SPR_PUNG,0,1,new A_WeaponReady(),S_PUNCH,0,0),	// S_PUNCH
   new State(SPR_PUNG,0,1,new A_Lower(),S_PUNCHDOWN,0,0),	// S_PUNCHDOWN
   new State(SPR_PUNG,0,1,new A_Raise(),S_PUNCHUP,0,0),	// S_PUNCHUP
   new State(SPR_PUNG,1,4,null,S_PUNCH2,0,0),		// S_PUNCH1
   new State(SPR_PUNG,2,4,new A_Punch(),S_PUNCH3,0,0),	// S_PUNCH2
   new State(SPR_PUNG,3,5,null,S_PUNCH4,0,0),		// S_PUNCH3
   new State(SPR_PUNG,2,4,null,S_PUNCH5,0,0),		// S_PUNCH4
   new State(SPR_PUNG,1,5,new A_ReFire(),S_PUNCH,0,0),	// S_PUNCH5
   new State(SPR_PISG,0,1,new A_WeaponReady(),S_PISTOL,0,0),// S_PISTOL
   new State(SPR_PISG,0,1,new A_Lower(),S_PISTOLDOWN,0,0),	// S_PISTOLDOWN
   new State(SPR_PISG,0,1,new A_Raise(),S_PISTOLUP,0,0),	// S_PISTOLUP
   new State(SPR_PISG,0,4,null,S_PISTOL2,0,0),	// S_PISTOL1
   new State(SPR_PISG,1,6,new A_FirePistol(),S_PISTOL3,0,0),// S_PISTOL2
   new State(SPR_PISG,2,4,null,S_PISTOL4,0,0),	// S_PISTOL3
   new State(SPR_PISG,1,5,new A_ReFire(),S_PISTOL,0,0),	// S_PISTOL4
   new State(SPR_PISF,32768,7,new A_Light1(),S_LIGHTDONE,0,0),	// S_PISTOLFLASH
   new State(SPR_SHTG,0,1,new A_WeaponReady(),S_SGUN,0,0),	// S_SGUN
   new State(SPR_SHTG,0,1,new A_Lower(),S_SGUNDOWN,0,0),	// S_SGUNDOWN
   new State(SPR_SHTG,0,1,new A_Raise(),S_SGUNUP,0,0),	// S_SGUNUP
   new State(SPR_SHTG,0,3,null,S_SGUN2,0,0),	// S_SGUN1
   new State(SPR_SHTG,0,7,new A_FireShotgun(),S_SGUN3,0,0),	// S_SGUN2
   new State(SPR_SHTG,1,5,null,S_SGUN4,0,0),	// S_SGUN3
   new State(SPR_SHTG,2,5,null,S_SGUN5,0,0),	// S_SGUN4
   new State(SPR_SHTG,3,4,null,S_SGUN6,0,0),	// S_SGUN5
   new State(SPR_SHTG,2,5,null,S_SGUN7,0,0),	// S_SGUN6
   new State(SPR_SHTG,1,5,null,S_SGUN8,0,0),	// S_SGUN7
   new State(SPR_SHTG,0,3,null,S_SGUN9,0,0),	// S_SGUN8
   new State(SPR_SHTG,0,7,new A_ReFire(),S_SGUN,0,0),	// S_SGUN9
   new State(SPR_SHTF,32768,4,new A_Light1(),S_SGUNFLASH2,0,0),	// S_SGUNFLASH1
   new State(SPR_SHTF,32769,3,new A_Light2(),S_LIGHTDONE,0,0),	// S_SGUNFLASH2
   new State(SPR_SHT2,0,1,new A_WeaponReady(),S_DSGUN,0,0),	// S_DSGUN
   new State(SPR_SHT2,0,1,new A_Lower(),S_DSGUNDOWN,0,0),	// S_DSGUNDOWN
   new State(SPR_SHT2,0,1,new A_Raise(),S_DSGUNUP,0,0),	// S_DSGUNUP
   new State(SPR_SHT2,0,3,null,S_DSGUN2,0,0),	// S_DSGUN1
   new State(SPR_SHT2,0,7,new A_FireShotgun2(),S_DSGUN3,0,0),	// S_DSGUN2
   new State(SPR_SHT2,1,7,null,S_DSGUN4,0,0),	// S_DSGUN3
   new State(SPR_SHT2,2,7,new A_CheckReload(),S_DSGUN5,0,0),	// S_DSGUN4
   new State(SPR_SHT2,3,7,new A_OpenShotgun2(),S_DSGUN6,0,0),	// S_DSGUN5
   new State(SPR_SHT2,4,7,null,S_DSGUN7,0,0),	// S_DSGUN6
   new State(SPR_SHT2,5,7,new A_LoadShotgun2(),S_DSGUN8,0,0),	// S_DSGUN7
   new State(SPR_SHT2,6,6,null,S_DSGUN9,0,0),	// S_DSGUN8
   new State(SPR_SHT2,7,6,new A_CloseShotgun2(),S_DSGUN10,0,0),	// S_DSGUN9
   new State(SPR_SHT2,0,5,new A_ReFire(),S_DSGUN,0,0),	// S_DSGUN10
   new State(SPR_SHT2,1,7,null,S_DSNR2,0,0),	// S_DSNR1
   new State(SPR_SHT2,0,3,null,S_DSGUNDOWN,0,0),	// S_DSNR2
   new State(SPR_SHT2,32776,5,new A_Light1(),S_DSGUNFLASH2,0,0),	// S_DSGUNFLASH1
   new State(SPR_SHT2,32777,4,new A_Light2(),S_LIGHTDONE,0,0),	// S_DSGUNFLASH2
   new State(SPR_CHGG,0,1,new A_WeaponReady(),S_CHAIN,0,0),	// S_CHAIN
   new State(SPR_CHGG,0,1,new A_Lower(),S_CHAINDOWN,0,0),	// S_CHAINDOWN
   new State(SPR_CHGG,0,1,new A_Raise(),S_CHAINUP,0,0),	// S_CHAINUP
   new State(SPR_CHGG,0,4,new A_FireCGun(),S_CHAIN2,0,0),	// S_CHAIN1
   new State(SPR_CHGG,1,4,new A_FireCGun(),S_CHAIN3,0,0),	// S_CHAIN2
   new State(SPR_CHGG,1,0,new A_ReFire(),S_CHAIN,0,0),	// S_CHAIN3
   new State(SPR_CHGF,32768,5,new A_Light1(),S_LIGHTDONE,0,0),	// S_CHAINFLASH1
   new State(SPR_CHGF,32769,5,new A_Light2(),S_LIGHTDONE,0,0),	// S_CHAINFLASH2
   new State(SPR_MISG,0,1,new A_WeaponReady(),S_MISSILE,0,0),	// S_MISSILE
   new State(SPR_MISG,0,1,new A_Lower(),S_MISSILEDOWN,0,0),	// S_MISSILEDOWN
   new State(SPR_MISG,0,1,new A_Raise(),S_MISSILEUP,0,0),	// S_MISSILEUP
   new State(SPR_MISG,1,8,new A_GunFlash(),S_MISSILE2,0,0),	// S_MISSILE1
   new State(SPR_MISG,1,12,new A_FireMissile(),S_MISSILE3,0,0),	// S_MISSILE2
   new State(SPR_MISG,1,0,new A_ReFire(),S_MISSILE,0,0),	// S_MISSILE3
   new State(SPR_MISF,32768,3,new A_Light1(),S_MISSILEFLASH2,0,0),	// S_MISSILEFLASH1
   new State(SPR_MISF,32769,4,null,S_MISSILEFLASH3,0,0),	// S_MISSILEFLASH2
   new State(SPR_MISF,32770,4,new A_Light2(),S_MISSILEFLASH4,0,0),	// S_MISSILEFLASH3
   new State(SPR_MISF,32771,4,new A_Light2(),S_LIGHTDONE,0,0),	// S_MISSILEFLASH4
   new State(SPR_SAWG,2,4,new A_WeaponReady(),S_SAWB,0,0),	// S_SAW
   new State(SPR_SAWG,3,4,new A_WeaponReady(),S_SAW,0,0),	// S_SAWB
   new State(SPR_SAWG,2,1,new A_Lower(),S_SAWDOWN,0,0),	// S_SAWDOWN
   new State(SPR_SAWG,2,1,new A_Raise(),S_SAWUP,0,0),	// S_SAWUP
   new State(SPR_SAWG,0,4,new A_Saw(),S_SAW2,0,0),	// S_SAW1
   new State(SPR_SAWG,1,4,new A_Saw(),S_SAW3,0,0),	// S_SAW2
   new State(SPR_SAWG,1,0,new A_ReFire(),S_SAW,0,0),	// S_SAW3
   new State(SPR_PLSG,0,1,new A_WeaponReady(),S_PLASMA,0,0),	// S_PLASMA
   new State(SPR_PLSG,0,1,new A_Lower(),S_PLASMADOWN,0,0),	// S_PLASMADOWN
   new State(SPR_PLSG,0,1,new A_Raise(),S_PLASMAUP,0,0),	// S_PLASMAUP
   new State(SPR_PLSG,0,3,new A_FirePlasma(),S_PLASMA2,0,0),	// S_PLASMA1
   new State(SPR_PLSG,1,20,new A_ReFire(),S_PLASMA,0,0),	// S_PLASMA2
   new State(SPR_PLSF,32768,4,new A_Light1(),S_LIGHTDONE,0,0),	// S_PLASMAFLASH1
   new State(SPR_PLSF,32769,4,new A_Light1(),S_LIGHTDONE,0,0),	// S_PLASMAFLASH2
   new State(SPR_BFGG,0,1,new A_WeaponReady(),S_BFG,0,0),	// S_BFG
   new State(SPR_BFGG,0,1,new A_Lower(),S_BFGDOWN,0,0),	// S_BFGDOWN
   new State(SPR_BFGG,0,1,new A_Raise(),S_BFGUP,0,0),	// S_BFGUP
   new State(SPR_BFGG,0,20,new A_BFGsound(),S_BFG2,0,0),	// S_BFG1
   new State(SPR_BFGG,1,10,new A_GunFlash(),S_BFG3,0,0),	// S_BFG2
   new State(SPR_BFGG,1,10,new A_FireBFG(),S_BFG4,0,0),	// S_BFG3
   new State(SPR_BFGG,1,20,new A_ReFire(),S_BFG,0,0),	// S_BFG4
   new State(SPR_BFGF,32768,11,new A_Light1(),S_BFGFLASH2,0,0),	// S_BFGFLASH1
   new State(SPR_BFGF,32769,6,new A_Light2(),S_LIGHTDONE,0,0),	// S_BFGFLASH2
   new State(SPR_BLUD,2,8,null,S_BLOOD2,0,0),	// S_BLOOD1
   new State(SPR_BLUD,1,8,null,S_BLOOD3,0,0),	// S_BLOOD2
   new State(SPR_BLUD,0,8,null,S_NULL,0,0),	// S_BLOOD3
   new State(SPR_PUFF,32768,4,null,S_PUFF2,0,0),	// S_PUFF1
   new State(SPR_PUFF,1,4,null,S_PUFF3,0,0),	// S_PUFF2
   new State(SPR_PUFF,2,4,null,S_PUFF4,0,0),	// S_PUFF3
   new State(SPR_PUFF,3,4,null,S_NULL,0,0),	// S_PUFF4
   new State(SPR_BAL1,32768,4,null,S_TBALL2,0,0),	// S_TBALL1
   new State(SPR_BAL1,32769,4,null,S_TBALL1,0,0),	// S_TBALL2
   new State(SPR_BAL1,32770,6,null,S_TBALLX2,0,0),	// S_TBALLX1
   new State(SPR_BAL1,32771,6,null,S_TBALLX3,0,0),	// S_TBALLX2
   new State(SPR_BAL1,32772,6,null,S_NULL,0,0),	// S_TBALLX3
   new State(SPR_BAL2,32768,4,null,S_RBALL2,0,0),	// S_RBALL1
   new State(SPR_BAL2,32769,4,null,S_RBALL1,0,0),	// S_RBALL2
   new State(SPR_BAL2,32770,6,null,S_RBALLX2,0,0),	// S_RBALLX1
   new State(SPR_BAL2,32771,6,null,S_RBALLX3,0,0),	// S_RBALLX2
   new State(SPR_BAL2,32772,6,null,S_NULL,0,0),	// S_RBALLX3
   new State(SPR_PLSS,32768,6,null,S_PLASBALL2,0,0),	// S_PLASBALL
   new State(SPR_PLSS,32769,6,null,S_PLASBALL,0,0),	// S_PLASBALL2
   new State(SPR_PLSE,32768,4,null,S_PLASEXP2,0,0),	// S_PLASEXP
   new State(SPR_PLSE,32769,4,null,S_PLASEXP3,0,0),	// S_PLASEXP2
   new State(SPR_PLSE,32770,4,null,S_PLASEXP4,0,0),	// S_PLASEXP3
   new State(SPR_PLSE,32771,4,null,S_PLASEXP5,0,0),	// S_PLASEXP4
   new State(SPR_PLSE,32772,4,null,S_NULL,0,0),	// S_PLASEXP5
   new State(SPR_MISL,32768,1,null,S_ROCKET,0,0),	// S_ROCKET
   new State(SPR_BFS1,32768,4,null,S_BFGSHOT2,0,0),	// S_BFGSHOT
   new State(SPR_BFS1,32769,4,null,S_BFGSHOT,0,0),	// S_BFGSHOT2
   new State(SPR_BFE1,32768,8,null,S_BFGLAND2,0,0),	// S_BFGLAND
   new State(SPR_BFE1,32769,8,null,S_BFGLAND3,0,0),	// S_BFGLAND2
   new State(SPR_BFE1,32770,8,new A_BFGSpray(),S_BFGLAND4,0,0),	// S_BFGLAND3
   new State(SPR_BFE1,32771,8,null,S_BFGLAND5,0,0),	// S_BFGLAND4
   new State(SPR_BFE1,32772,8,null,S_BFGLAND6,0,0),	// S_BFGLAND5
   new State(SPR_BFE1,32773,8,null,S_NULL,0,0),	// S_BFGLAND6
   new State(SPR_BFE2,32768,8,null,S_BFGEXP2,0,0),	// S_BFGEXP
   new State(SPR_BFE2,32769,8,null,S_BFGEXP3,0,0),	// S_BFGEXP2
   new State(SPR_BFE2,32770,8,null,S_BFGEXP4,0,0),	// S_BFGEXP3
   new State(SPR_BFE2,32771,8,null,S_NULL,0,0),	// S_BFGEXP4
   new State(SPR_MISL,32769,8,new A_Explode(),S_EXPLODE2,0,0),	// S_EXPLODE1
   new State(SPR_MISL,32770,6,null,S_EXPLODE3,0,0),	// S_EXPLODE2
   new State(SPR_MISL,32771,4,null,S_NULL,0,0),	// S_EXPLODE3
   new State(SPR_TFOG,32768,6,null,S_TFOG01,0,0),	// S_TFOG
   new State(SPR_TFOG,32769,6,null,S_TFOG02,0,0),	// S_TFOG01
   new State(SPR_TFOG,32768,6,null,S_TFOG2,0,0),	// S_TFOG02
   new State(SPR_TFOG,32769,6,null,S_TFOG3,0,0),	// S_TFOG2
   new State(SPR_TFOG,32770,6,null,S_TFOG4,0,0),	// S_TFOG3
   new State(SPR_TFOG,32771,6,null,S_TFOG5,0,0),	// S_TFOG4
   new State(SPR_TFOG,32772,6,null,S_TFOG6,0,0),	// S_TFOG5
   new State(SPR_TFOG,32773,6,null,S_TFOG7,0,0),	// S_TFOG6
   new State(SPR_TFOG,32774,6,null,S_TFOG8,0,0),	// S_TFOG7
   new State(SPR_TFOG,32775,6,null,S_TFOG9,0,0),	// S_TFOG8
   new State(SPR_TFOG,32776,6,null,S_TFOG10,0,0),	// S_TFOG9
   new State(SPR_TFOG,32777,6,null,S_NULL,0,0),	// S_TFOG10
   new State(SPR_IFOG,32768,6,null,S_IFOG01,0,0),	// S_IFOG
   new State(SPR_IFOG,32769,6,null,S_IFOG02,0,0),	// S_IFOG01
   new State(SPR_IFOG,32768,6,null,S_IFOG2,0,0),	// S_IFOG02
   new State(SPR_IFOG,32769,6,null,S_IFOG3,0,0),	// S_IFOG2
   new State(SPR_IFOG,32770,6,null,S_IFOG4,0,0),	// S_IFOG3
   new State(SPR_IFOG,32771,6,null,S_IFOG5,0,0),	// S_IFOG4
   new State(SPR_IFOG,32772,6,null,S_NULL,0,0),	// S_IFOG5
   new State(SPR_PLAY,0,-1,null,S_NULL,0,0),	// S_PLAY
   new State(SPR_PLAY,0,4,null,S_PLAY_RUN2,0,0),	// S_PLAY_RUN1
   new State(SPR_PLAY,1,4,null,S_PLAY_RUN3,0,0),	// S_PLAY_RUN2
   new State(SPR_PLAY,2,4,null,S_PLAY_RUN4,0,0),	// S_PLAY_RUN3
   new State(SPR_PLAY,3,4,null,S_PLAY_RUN1,0,0),	// S_PLAY_RUN4
   new State(SPR_PLAY,4,12,null,S_PLAY,0,0),	// S_PLAY_ATK1
   new State(SPR_PLAY,32773,6,null,S_PLAY_ATK1,0,0),	// S_PLAY_ATK2
   new State(SPR_PLAY,6,4,null,S_PLAY_PAIN2,0,0),	// S_PLAY_PAIN
   new State(SPR_PLAY,6,4,new A_Pain(),S_PLAY,0,0),	// S_PLAY_PAIN2
   new State(SPR_PLAY,7,10,null,S_PLAY_DIE2,0,0),	// S_PLAY_DIE1
   new State(SPR_PLAY,8,10,new A_PlayerScream(),S_PLAY_DIE3,0,0),	// S_PLAY_DIE2
   new State(SPR_PLAY,9,10,new A_Fall(),S_PLAY_DIE4,0,0),	// S_PLAY_DIE3
   new State(SPR_PLAY,10,10,null,S_PLAY_DIE5,0,0),	// S_PLAY_DIE4
   new State(SPR_PLAY,11,10,null,S_PLAY_DIE6,0,0),	// S_PLAY_DIE5
   new State(SPR_PLAY,12,10,null,S_PLAY_DIE7,0,0),	// S_PLAY_DIE6
   new State(SPR_PLAY,13,-1,null,S_NULL,0,0),	// S_PLAY_DIE7
   new State(SPR_PLAY,14,5,null,S_PLAY_XDIE2,0,0),	// S_PLAY_XDIE1
   new State(SPR_PLAY,15,5,new A_XScream(),S_PLAY_XDIE3,0,0),	// S_PLAY_XDIE2
   new State(SPR_PLAY,16,5,new A_Fall(),S_PLAY_XDIE4,0,0),	// S_PLAY_XDIE3
   new State(SPR_PLAY,17,5,null,S_PLAY_XDIE5,0,0),	// S_PLAY_XDIE4
   new State(SPR_PLAY,18,5,null,S_PLAY_XDIE6,0,0),	// S_PLAY_XDIE5
   new State(SPR_PLAY,19,5,null,S_PLAY_XDIE7,0,0),	// S_PLAY_XDIE6
   new State(SPR_PLAY,20,5,null,S_PLAY_XDIE8,0,0),	// S_PLAY_XDIE7
   new State(SPR_PLAY,21,5,null,S_PLAY_XDIE9,0,0),	// S_PLAY_XDIE8
   new State(SPR_PLAY,22,-1,null,S_NULL,0,0),	// S_PLAY_XDIE9
   new State(SPR_POSS,0,10,new A_Look(),S_POSS_STND2,0,0),	// S_POSS_STND
   new State(SPR_POSS,1,10,new A_Look(),S_POSS_STND,0,0),	// S_POSS_STND2
   new State(SPR_POSS,0,4,new A_Chase(),S_POSS_RUN2,0,0),	// S_POSS_RUN1
   new State(SPR_POSS,0,4,new A_Chase(),S_POSS_RUN3,0,0),	// S_POSS_RUN2
   new State(SPR_POSS,1,4,new A_Chase(),S_POSS_RUN4,0,0),	// S_POSS_RUN3
   new State(SPR_POSS,1,4,new A_Chase(),S_POSS_RUN5,0,0),	// S_POSS_RUN4
   new State(SPR_POSS,2,4,new A_Chase(),S_POSS_RUN6,0,0),	// S_POSS_RUN5
   new State(SPR_POSS,2,4,new A_Chase(),S_POSS_RUN7,0,0),	// S_POSS_RUN6
   new State(SPR_POSS,3,4,new A_Chase(),S_POSS_RUN8,0,0),	// S_POSS_RUN7
   new State(SPR_POSS,3,4,new A_Chase(),S_POSS_RUN1,0,0),	// S_POSS_RUN8
   new State(SPR_POSS,4,10,new A_FaceTarget(),S_POSS_ATK2,0,0),	// S_POSS_ATK1
   new State(SPR_POSS,5,8,new A_PosAttack(),S_POSS_ATK3,0,0),	// S_POSS_ATK2
   new State(SPR_POSS,4,8,null,S_POSS_RUN1,0,0),	// S_POSS_ATK3
   new State(SPR_POSS,6,3,null,S_POSS_PAIN2,0,0),	// S_POSS_PAIN
   new State(SPR_POSS,6,3,new A_Pain(),S_POSS_RUN1,0,0),	// S_POSS_PAIN2
   new State(SPR_POSS,7,5,null,S_POSS_DIE2,0,0),	// S_POSS_DIE1
   new State(SPR_POSS,8,5,new A_Scream(),S_POSS_DIE3,0,0),	// S_POSS_DIE2
   new State(SPR_POSS,9,5,new A_Fall(),S_POSS_DIE4,0,0),	// S_POSS_DIE3
   new State(SPR_POSS,10,5,null,S_POSS_DIE5,0,0),	// S_POSS_DIE4
   new State(SPR_POSS,11,-1,null,S_NULL,0,0),	// S_POSS_DIE5
   new State(SPR_POSS,12,5,null,S_POSS_XDIE2,0,0),	// S_POSS_XDIE1
   new State(SPR_POSS,13,5,new A_XScream(),S_POSS_XDIE3,0,0),	// S_POSS_XDIE2
   new State(SPR_POSS,14,5,new A_Fall(),S_POSS_XDIE4,0,0),	// S_POSS_XDIE3
   new State(SPR_POSS,15,5,null,S_POSS_XDIE5,0,0),	// S_POSS_XDIE4
   new State(SPR_POSS,16,5,null,S_POSS_XDIE6,0,0),	// S_POSS_XDIE5
   new State(SPR_POSS,17,5,null,S_POSS_XDIE7,0,0),	// S_POSS_XDIE6
   new State(SPR_POSS,18,5,null,S_POSS_XDIE8,0,0),	// S_POSS_XDIE7
   new State(SPR_POSS,19,5,null,S_POSS_XDIE9,0,0),	// S_POSS_XDIE8
   new State(SPR_POSS,20,-1,null,S_NULL,0,0),	// S_POSS_XDIE9
   new State(SPR_POSS,10,5,null,S_POSS_RAISE2,0,0),	// S_POSS_RAISE1
   new State(SPR_POSS,9,5,null,S_POSS_RAISE3,0,0),	// S_POSS_RAISE2
   new State(SPR_POSS,8,5,null,S_POSS_RAISE4,0,0),	// S_POSS_RAISE3
   new State(SPR_POSS,7,5,null,S_POSS_RUN1,0,0),	// S_POSS_RAISE4
   new State(SPR_SPOS,0,10,new A_Look(),S_SPOS_STND2,0,0),	// S_SPOS_STND
   new State(SPR_SPOS,1,10,new A_Look(),S_SPOS_STND,0,0),	// S_SPOS_STND2
   new State(SPR_SPOS,0,3,new A_Chase(),S_SPOS_RUN2,0,0),	// S_SPOS_RUN1
   new State(SPR_SPOS,0,3,new A_Chase(),S_SPOS_RUN3,0,0),	// S_SPOS_RUN2
   new State(SPR_SPOS,1,3,new A_Chase(),S_SPOS_RUN4,0,0),	// S_SPOS_RUN3
   new State(SPR_SPOS,1,3,new A_Chase(),S_SPOS_RUN5,0,0),	// S_SPOS_RUN4
   new State(SPR_SPOS,2,3,new A_Chase(),S_SPOS_RUN6,0,0),	// S_SPOS_RUN5
   new State(SPR_SPOS,2,3,new A_Chase(),S_SPOS_RUN7,0,0),	// S_SPOS_RUN6
   new State(SPR_SPOS,3,3,new A_Chase(),S_SPOS_RUN8,0,0),	// S_SPOS_RUN7
   new State(SPR_SPOS,3,3,new A_Chase(),S_SPOS_RUN1,0,0),	// S_SPOS_RUN8
   new State(SPR_SPOS,4,10,new A_FaceTarget(),S_SPOS_ATK2,0,0),	// S_SPOS_ATK1
   new State(SPR_SPOS,32773,10,new A_SPosAttack(),S_SPOS_ATK3,0,0),	// S_SPOS_ATK2
   new State(SPR_SPOS,4,10,null,S_SPOS_RUN1,0,0),	// S_SPOS_ATK3
   new State(SPR_SPOS,6,3,null,S_SPOS_PAIN2,0,0),	// S_SPOS_PAIN
   new State(SPR_SPOS,6,3,new A_Pain(),S_SPOS_RUN1,0,0),	// S_SPOS_PAIN2
   new State(SPR_SPOS,7,5,null,S_SPOS_DIE2,0,0),	// S_SPOS_DIE1
   new State(SPR_SPOS,8,5,new A_Scream(),S_SPOS_DIE3,0,0),	// S_SPOS_DIE2
   new State(SPR_SPOS,9,5,new A_Fall(),S_SPOS_DIE4,0,0),	// S_SPOS_DIE3
   new State(SPR_SPOS,10,5,null,S_SPOS_DIE5,0,0),	// S_SPOS_DIE4
   new State(SPR_SPOS,11,-1,null,S_NULL,0,0),	// S_SPOS_DIE5
   new State(SPR_SPOS,12,5,null,S_SPOS_XDIE2,0,0),	// S_SPOS_XDIE1
   new State(SPR_SPOS,13,5,new A_XScream(),S_SPOS_XDIE3,0,0),	// S_SPOS_XDIE2
   new State(SPR_SPOS,14,5,new A_Fall(),S_SPOS_XDIE4,0,0),	// S_SPOS_XDIE3
   new State(SPR_SPOS,15,5,null,S_SPOS_XDIE5,0,0),	// S_SPOS_XDIE4
   new State(SPR_SPOS,16,5,null,S_SPOS_XDIE6,0,0),	// S_SPOS_XDIE5
   new State(SPR_SPOS,17,5,null,S_SPOS_XDIE7,0,0),	// S_SPOS_XDIE6
   new State(SPR_SPOS,18,5,null,S_SPOS_XDIE8,0,0),	// S_SPOS_XDIE7
   new State(SPR_SPOS,19,5,null,S_SPOS_XDIE9,0,0),	// S_SPOS_XDIE8
   new State(SPR_SPOS,20,-1,null,S_NULL,0,0),	// S_SPOS_XDIE9
   new State(SPR_SPOS,11,5,null,S_SPOS_RAISE2,0,0),	// S_SPOS_RAISE1
   new State(SPR_SPOS,10,5,null,S_SPOS_RAISE3,0,0),	// S_SPOS_RAISE2
   new State(SPR_SPOS,9,5,null,S_SPOS_RAISE4,0,0),	// S_SPOS_RAISE3
   new State(SPR_SPOS,8,5,null,S_SPOS_RAISE5,0,0),	// S_SPOS_RAISE4
   new State(SPR_SPOS,7,5,null,S_SPOS_RUN1,0,0),	// S_SPOS_RAISE5
   new State(SPR_VILE,0,10,new A_Look(),S_VILE_STND2,0,0),	// S_VILE_STND
   new State(SPR_VILE,1,10,new A_Look(),S_VILE_STND,0,0),	// S_VILE_STND2
   new State(SPR_VILE,0,2,new A_VileChase(),S_VILE_RUN2,0,0),	// S_VILE_RUN1
   new State(SPR_VILE,0,2,new A_VileChase(),S_VILE_RUN3,0,0),	// S_VILE_RUN2
   new State(SPR_VILE,1,2,new A_VileChase(),S_VILE_RUN4,0,0),	// S_VILE_RUN3
   new State(SPR_VILE,1,2,new A_VileChase(),S_VILE_RUN5,0,0),	// S_VILE_RUN4
   new State(SPR_VILE,2,2,new A_VileChase(),S_VILE_RUN6,0,0),	// S_VILE_RUN5
   new State(SPR_VILE,2,2,new A_VileChase(),S_VILE_RUN7,0,0),	// S_VILE_RUN6
   new State(SPR_VILE,3,2,new A_VileChase(),S_VILE_RUN8,0,0),	// S_VILE_RUN7
   new State(SPR_VILE,3,2,new A_VileChase(),S_VILE_RUN9,0,0),	// S_VILE_RUN8
   new State(SPR_VILE,4,2,new A_VileChase(),S_VILE_RUN10,0,0),	// S_VILE_RUN9
   new State(SPR_VILE,4,2,new A_VileChase(),S_VILE_RUN11,0,0),	// S_VILE_RUN10
   new State(SPR_VILE,5,2,new A_VileChase(),S_VILE_RUN12,0,0),	// S_VILE_RUN11
   new State(SPR_VILE,5,2,new A_VileChase(),S_VILE_RUN1,0,0),	// S_VILE_RUN12
   new State(SPR_VILE,32774,0,new A_VileStart(),S_VILE_ATK2,0,0),	// S_VILE_ATK1
   new State(SPR_VILE,32774,10,new A_FaceTarget(),S_VILE_ATK3,0,0),	// S_VILE_ATK2
   new State(SPR_VILE,32775,8,new A_VileTarget(),S_VILE_ATK4,0,0),	// S_VILE_ATK3
   new State(SPR_VILE,32776,8,new A_FaceTarget(),S_VILE_ATK5,0,0),	// S_VILE_ATK4
   new State(SPR_VILE,32777,8,new A_FaceTarget(),S_VILE_ATK6,0,0),	// S_VILE_ATK5
   new State(SPR_VILE,32778,8,new A_FaceTarget(),S_VILE_ATK7,0,0),	// S_VILE_ATK6
   new State(SPR_VILE,32779,8,new A_FaceTarget(),S_VILE_ATK8,0,0),	// S_VILE_ATK7
   new State(SPR_VILE,32780,8,new A_FaceTarget(),S_VILE_ATK9,0,0),	// S_VILE_ATK8
   new State(SPR_VILE,32781,8,new A_FaceTarget(),S_VILE_ATK10,0,0),	// S_VILE_ATK9
   new State(SPR_VILE,32782,8,new A_VileAttack(),S_VILE_ATK11,0,0),	// S_VILE_ATK10
   new State(SPR_VILE,32783,20,null,S_VILE_RUN1,0,0),	// S_VILE_ATK11
   new State(SPR_VILE,32794,10,null,S_VILE_HEAL2,0,0),	// S_VILE_HEAL1
   new State(SPR_VILE,32795,10,null,S_VILE_HEAL3,0,0),	// S_VILE_HEAL2
   new State(SPR_VILE,32796,10,null,S_VILE_RUN1,0,0),	// S_VILE_HEAL3
   new State(SPR_VILE,16,5,null,S_VILE_PAIN2,0,0),	// S_VILE_PAIN
   new State(SPR_VILE,16,5,new A_Pain(),S_VILE_RUN1,0,0),	// S_VILE_PAIN2
   new State(SPR_VILE,16,7,null,S_VILE_DIE2,0,0),	// S_VILE_DIE1
   new State(SPR_VILE,17,7,new A_Scream(),S_VILE_DIE3,0,0),	// S_VILE_DIE2
   new State(SPR_VILE,18,7,new A_Fall(),S_VILE_DIE4,0,0),	// S_VILE_DIE3
   new State(SPR_VILE,19,7,null,S_VILE_DIE5,0,0),	// S_VILE_DIE4
   new State(SPR_VILE,20,7,null,S_VILE_DIE6,0,0),	// S_VILE_DIE5
   new State(SPR_VILE,21,7,null,S_VILE_DIE7,0,0),	// S_VILE_DIE6
   new State(SPR_VILE,22,7,null,S_VILE_DIE8,0,0),	// S_VILE_DIE7
   new State(SPR_VILE,23,5,null,S_VILE_DIE9,0,0),	// S_VILE_DIE8
   new State(SPR_VILE,24,5,null,S_VILE_DIE10,0,0),	// S_VILE_DIE9
   new State(SPR_VILE,25,-1,null,S_NULL,0,0),	// S_VILE_DIE10
   new State(SPR_FIRE,32768,2,new A_StartFire(),S_FIRE2,0,0),	// S_FIRE1
   new State(SPR_FIRE,32769,2,new A_Fire(),S_FIRE3,0,0),	// S_FIRE2
   new State(SPR_FIRE,32768,2,new A_Fire(),S_FIRE4,0,0),	// S_FIRE3
   new State(SPR_FIRE,32769,2,new A_Fire(),S_FIRE5,0,0),	// S_FIRE4
   new State(SPR_FIRE,32770,2,new A_FireCrackle(),S_FIRE6,0,0),	// S_FIRE5
   new State(SPR_FIRE,32769,2,new A_Fire(),S_FIRE7,0,0),	// S_FIRE6
   new State(SPR_FIRE,32770,2,new A_Fire(),S_FIRE8,0,0),	// S_FIRE7
   new State(SPR_FIRE,32769,2,new A_Fire(),S_FIRE9,0,0),	// S_FIRE8
   new State(SPR_FIRE,32770,2,new A_Fire(),S_FIRE10,0,0),	// S_FIRE9
   new State(SPR_FIRE,32771,2,new A_Fire(),S_FIRE11,0,0),	// S_FIRE10
   new State(SPR_FIRE,32770,2,new A_Fire(),S_FIRE12,0,0),	// S_FIRE11
   new State(SPR_FIRE,32771,2,new A_Fire(),S_FIRE13,0,0),	// S_FIRE12
   new State(SPR_FIRE,32770,2,new A_Fire(),S_FIRE14,0,0),	// S_FIRE13
   new State(SPR_FIRE,32771,2,new A_Fire(),S_FIRE15,0,0),	// S_FIRE14
   new State(SPR_FIRE,32772,2,new A_Fire(),S_FIRE16,0,0),	// S_FIRE15
   new State(SPR_FIRE,32771,2,new A_Fire(),S_FIRE17,0,0),	// S_FIRE16
   new State(SPR_FIRE,32772,2,new A_Fire(),S_FIRE18,0,0),	// S_FIRE17
   new State(SPR_FIRE,32771,2,new A_Fire(),S_FIRE19,0,0),	// S_FIRE18
   new State(SPR_FIRE,32772,2,new A_FireCrackle(),S_FIRE20,0,0),	// S_FIRE19
   new State(SPR_FIRE,32773,2,new A_Fire(),S_FIRE21,0,0),	// S_FIRE20
   new State(SPR_FIRE,32772,2,new A_Fire(),S_FIRE22,0,0),	// S_FIRE21
   new State(SPR_FIRE,32773,2,new A_Fire(),S_FIRE23,0,0),	// S_FIRE22
   new State(SPR_FIRE,32772,2,new A_Fire(),S_FIRE24,0,0),	// S_FIRE23
   new State(SPR_FIRE,32773,2,new A_Fire(),S_FIRE25,0,0),	// S_FIRE24
   new State(SPR_FIRE,32774,2,new A_Fire(),S_FIRE26,0,0),	// S_FIRE25
   new State(SPR_FIRE,32775,2,new A_Fire(),S_FIRE27,0,0),	// S_FIRE26
   new State(SPR_FIRE,32774,2,new A_Fire(),S_FIRE28,0,0),	// S_FIRE27
   new State(SPR_FIRE,32775,2,new A_Fire(),S_FIRE29,0,0),	// S_FIRE28
   new State(SPR_FIRE,32774,2,new A_Fire(),S_FIRE30,0,0),	// S_FIRE29
   new State(SPR_FIRE,32775,2,new A_Fire(),S_NULL,0,0),	// S_FIRE30
   new State(SPR_PUFF,1,4,null,S_SMOKE2,0,0),	// S_SMOKE1
   new State(SPR_PUFF,2,4,null,S_SMOKE3,0,0),	// S_SMOKE2
   new State(SPR_PUFF,1,4,null,S_SMOKE4,0,0),	// S_SMOKE3
   new State(SPR_PUFF,2,4,null,S_SMOKE5,0,0),	// S_SMOKE4
   new State(SPR_PUFF,3,4,null,S_NULL,0,0),	// S_SMOKE5
   new State(SPR_FATB,32768,2,new A_Tracer(),S_TRACER2,0,0),	// S_TRACER
   new State(SPR_FATB,32769,2,new A_Tracer(),S_TRACER,0,0),	// S_TRACER2
   new State(SPR_FBXP,32768,8,null,S_TRACEEXP2,0,0),	// S_TRACEEXP1
   new State(SPR_FBXP,32769,6,null,S_TRACEEXP3,0,0),	// S_TRACEEXP2
   new State(SPR_FBXP,32770,4,null,S_NULL,0,0),	// S_TRACEEXP3
   new State(SPR_SKEL,0,10,new A_Look(),S_SKEL_STND2,0,0),	// S_SKEL_STND
   new State(SPR_SKEL,1,10,new A_Look(),S_SKEL_STND,0,0),	// S_SKEL_STND2
   new State(SPR_SKEL,0,2,new A_Chase(),S_SKEL_RUN2,0,0),	// S_SKEL_RUN1
   new State(SPR_SKEL,0,2,new A_Chase(),S_SKEL_RUN3,0,0),	// S_SKEL_RUN2
   new State(SPR_SKEL,1,2,new A_Chase(),S_SKEL_RUN4,0,0),	// S_SKEL_RUN3
   new State(SPR_SKEL,1,2,new A_Chase(),S_SKEL_RUN5,0,0),	// S_SKEL_RUN4
   new State(SPR_SKEL,2,2,new A_Chase(),S_SKEL_RUN6,0,0),	// S_SKEL_RUN5
   new State(SPR_SKEL,2,2,new A_Chase(),S_SKEL_RUN7,0,0),	// S_SKEL_RUN6
   new State(SPR_SKEL,3,2,new A_Chase(),S_SKEL_RUN8,0,0),	// S_SKEL_RUN7
   new State(SPR_SKEL,3,2,new A_Chase(),S_SKEL_RUN9,0,0),	// S_SKEL_RUN8
   new State(SPR_SKEL,4,2,new A_Chase(),S_SKEL_RUN10,0,0),	// S_SKEL_RUN9
   new State(SPR_SKEL,4,2,new A_Chase(),S_SKEL_RUN11,0,0),	// S_SKEL_RUN10
   new State(SPR_SKEL,5,2,new A_Chase(),S_SKEL_RUN12,0,0),	// S_SKEL_RUN11
   new State(SPR_SKEL,5,2,new A_Chase(),S_SKEL_RUN1,0,0),	// S_SKEL_RUN12
   new State(SPR_SKEL,6,0,new A_FaceTarget(),S_SKEL_FIST2,0,0),	// S_SKEL_FIST1
   new State(SPR_SKEL,6,6,new A_SkelWhoosh(),S_SKEL_FIST3,0,0),	// S_SKEL_FIST2
   new State(SPR_SKEL,7,6,new A_FaceTarget(),S_SKEL_FIST4,0,0),	// S_SKEL_FIST3
   new State(SPR_SKEL,8,6,new A_SkelFist(),S_SKEL_RUN1,0,0),	// S_SKEL_FIST4
   new State(SPR_SKEL,32777,0,new A_FaceTarget(),S_SKEL_MISS2,0,0),	// S_SKEL_MISS1
   new State(SPR_SKEL,32777,10,new A_FaceTarget(),S_SKEL_MISS3,0,0),	// S_SKEL_MISS2
   new State(SPR_SKEL,10,10,new A_SkelMissile(),S_SKEL_MISS4,0,0),	// S_SKEL_MISS3
   new State(SPR_SKEL,10,10,new A_FaceTarget(),S_SKEL_RUN1,0,0),	// S_SKEL_MISS4
   new State(SPR_SKEL,11,5,null,S_SKEL_PAIN2,0,0),	// S_SKEL_PAIN
   new State(SPR_SKEL,11,5,new A_Pain(),S_SKEL_RUN1,0,0),	// S_SKEL_PAIN2
   new State(SPR_SKEL,11,7,null,S_SKEL_DIE2,0,0),	// S_SKEL_DIE1
   new State(SPR_SKEL,12,7,null,S_SKEL_DIE3,0,0),	// S_SKEL_DIE2
   new State(SPR_SKEL,13,7,new A_Scream(),S_SKEL_DIE4,0,0),	// S_SKEL_DIE3
   new State(SPR_SKEL,14,7,new A_Fall(),S_SKEL_DIE5,0,0),	// S_SKEL_DIE4
   new State(SPR_SKEL,15,7,null,S_SKEL_DIE6,0,0),	// S_SKEL_DIE5
   new State(SPR_SKEL,16,-1,null,S_NULL,0,0),	// S_SKEL_DIE6
   new State(SPR_SKEL,16,5,null,S_SKEL_RAISE2,0,0),	// S_SKEL_RAISE1
   new State(SPR_SKEL,15,5,null,S_SKEL_RAISE3,0,0),	// S_SKEL_RAISE2
   new State(SPR_SKEL,14,5,null,S_SKEL_RAISE4,0,0),	// S_SKEL_RAISE3
   new State(SPR_SKEL,13,5,null,S_SKEL_RAISE5,0,0),	// S_SKEL_RAISE4
   new State(SPR_SKEL,12,5,null,S_SKEL_RAISE6,0,0),	// S_SKEL_RAISE5
   new State(SPR_SKEL,11,5,null,S_SKEL_RUN1,0,0),	// S_SKEL_RAISE6
   new State(SPR_MANF,32768,4,null,S_FATSHOT2,0,0),	// S_FATSHOT1
   new State(SPR_MANF,32769,4,null,S_FATSHOT1,0,0),	// S_FATSHOT2
   new State(SPR_MISL,32769,8,null,S_FATSHOTX2,0,0),	// S_FATSHOTX1
   new State(SPR_MISL,32770,6,null,S_FATSHOTX3,0,0),	// S_FATSHOTX2
   new State(SPR_MISL,32771,4,null,S_NULL,0,0),	// S_FATSHOTX3
   new State(SPR_FATT,0,15,new A_Look(),S_FATT_STND2,0,0),	// S_FATT_STND
   new State(SPR_FATT,1,15,new A_Look(),S_FATT_STND,0,0),	// S_FATT_STND2
   new State(SPR_FATT,0,4,new A_Chase(),S_FATT_RUN2,0,0),	// S_FATT_RUN1
   new State(SPR_FATT,0,4,new A_Chase(),S_FATT_RUN3,0,0),	// S_FATT_RUN2
   new State(SPR_FATT,1,4,new A_Chase(),S_FATT_RUN4,0,0),	// S_FATT_RUN3
   new State(SPR_FATT,1,4,new A_Chase(),S_FATT_RUN5,0,0),	// S_FATT_RUN4
   new State(SPR_FATT,2,4,new A_Chase(),S_FATT_RUN6,0,0),	// S_FATT_RUN5
   new State(SPR_FATT,2,4,new A_Chase(),S_FATT_RUN7,0,0),	// S_FATT_RUN6
   new State(SPR_FATT,3,4,new A_Chase(),S_FATT_RUN8,0,0),	// S_FATT_RUN7
   new State(SPR_FATT,3,4,new A_Chase(),S_FATT_RUN9,0,0),	// S_FATT_RUN8
   new State(SPR_FATT,4,4,new A_Chase(),S_FATT_RUN10,0,0),	// S_FATT_RUN9
   new State(SPR_FATT,4,4,new A_Chase(),S_FATT_RUN11,0,0),	// S_FATT_RUN10
   new State(SPR_FATT,5,4,new A_Chase(),S_FATT_RUN12,0,0),	// S_FATT_RUN11
   new State(SPR_FATT,5,4,new A_Chase(),S_FATT_RUN1,0,0),	// S_FATT_RUN12
   new State(SPR_FATT,6,20,new A_FatRaise(),S_FATT_ATK2,0,0),	// S_FATT_ATK1
   new State(SPR_FATT,32775,10,new A_FatAttack1(),S_FATT_ATK3,0,0),	// S_FATT_ATK2
   new State(SPR_FATT,8,5,new A_FaceTarget(),S_FATT_ATK4,0,0),	// S_FATT_ATK3
   new State(SPR_FATT,6,5,new A_FaceTarget(),S_FATT_ATK5,0,0),	// S_FATT_ATK4
   new State(SPR_FATT,32775,10,new A_FatAttack2(),S_FATT_ATK6,0,0),	// S_FATT_ATK5
   new State(SPR_FATT,8,5,new A_FaceTarget(),S_FATT_ATK7,0,0),	// S_FATT_ATK6
   new State(SPR_FATT,6,5,new A_FaceTarget(),S_FATT_ATK8,0,0),	// S_FATT_ATK7
   new State(SPR_FATT,32775,10,new A_FatAttack3(),S_FATT_ATK9,0,0),	// S_FATT_ATK8
   new State(SPR_FATT,8,5,new A_FaceTarget(),S_FATT_ATK10,0,0),	// S_FATT_ATK9
   new State(SPR_FATT,6,5,new A_FaceTarget(),S_FATT_RUN1,0,0),	// S_FATT_ATK10
   new State(SPR_FATT,9,3,null,S_FATT_PAIN2,0,0),	// S_FATT_PAIN
   new State(SPR_FATT,9,3,new A_Pain(),S_FATT_RUN1,0,0),	// S_FATT_PAIN2
   new State(SPR_FATT,10,6,null,S_FATT_DIE2,0,0),	// S_FATT_DIE1
   new State(SPR_FATT,11,6,new A_Scream(),S_FATT_DIE3,0,0),	// S_FATT_DIE2
   new State(SPR_FATT,12,6,new A_Fall(),S_FATT_DIE4,0,0),	// S_FATT_DIE3
   new State(SPR_FATT,13,6,null,S_FATT_DIE5,0,0),	// S_FATT_DIE4
   new State(SPR_FATT,14,6,null,S_FATT_DIE6,0,0),	// S_FATT_DIE5
   new State(SPR_FATT,15,6,null,S_FATT_DIE7,0,0),	// S_FATT_DIE6
   new State(SPR_FATT,16,6,null,S_FATT_DIE8,0,0),	// S_FATT_DIE7
   new State(SPR_FATT,17,6,null,S_FATT_DIE9,0,0),	// S_FATT_DIE8
   new State(SPR_FATT,18,6,null,S_FATT_DIE10,0,0),	// S_FATT_DIE9
   new State(SPR_FATT,19,-1,new A_BossDeath(),S_NULL,0,0),	// S_FATT_DIE10
   new State(SPR_FATT,17,5,null,S_FATT_RAISE2,0,0),	// S_FATT_RAISE1
   new State(SPR_FATT,16,5,null,S_FATT_RAISE3,0,0),	// S_FATT_RAISE2
   new State(SPR_FATT,15,5,null,S_FATT_RAISE4,0,0),	// S_FATT_RAISE3
   new State(SPR_FATT,14,5,null,S_FATT_RAISE5,0,0),	// S_FATT_RAISE4
   new State(SPR_FATT,13,5,null,S_FATT_RAISE6,0,0),	// S_FATT_RAISE5
   new State(SPR_FATT,12,5,null,S_FATT_RAISE7,0,0),	// S_FATT_RAISE6
   new State(SPR_FATT,11,5,null,S_FATT_RAISE8,0,0),	// S_FATT_RAISE7
   new State(SPR_FATT,10,5,null,S_FATT_RUN1,0,0),	// S_FATT_RAISE8
   new State(SPR_CPOS,0,10,new A_Look(),S_CPOS_STND2,0,0),	// S_CPOS_STND
   new State(SPR_CPOS,1,10,new A_Look(),S_CPOS_STND,0,0),	// S_CPOS_STND2
   new State(SPR_CPOS,0,3,new A_Chase(),S_CPOS_RUN2,0,0),	// S_CPOS_RUN1
   new State(SPR_CPOS,0,3,new A_Chase(),S_CPOS_RUN3,0,0),	// S_CPOS_RUN2
   new State(SPR_CPOS,1,3,new A_Chase(),S_CPOS_RUN4,0,0),	// S_CPOS_RUN3
   new State(SPR_CPOS,1,3,new A_Chase(),S_CPOS_RUN5,0,0),	// S_CPOS_RUN4
   new State(SPR_CPOS,2,3,new A_Chase(),S_CPOS_RUN6,0,0),	// S_CPOS_RUN5
   new State(SPR_CPOS,2,3,new A_Chase(),S_CPOS_RUN7,0,0),	// S_CPOS_RUN6
   new State(SPR_CPOS,3,3,new A_Chase(),S_CPOS_RUN8,0,0),	// S_CPOS_RUN7
   new State(SPR_CPOS,3,3,new A_Chase(),S_CPOS_RUN1,0,0),	// S_CPOS_RUN8
   new State(SPR_CPOS,4,10,new A_FaceTarget(),S_CPOS_ATK2,0,0),	// S_CPOS_ATK1
   new State(SPR_CPOS,32773,4,new A_CPosAttack(),S_CPOS_ATK3,0,0),	// S_CPOS_ATK2
   new State(SPR_CPOS,32772,4,new A_CPosAttack(),S_CPOS_ATK4,0,0),	// S_CPOS_ATK3
   new State(SPR_CPOS,5,1,new A_CPosRefire(),S_CPOS_ATK2,0,0),	// S_CPOS_ATK4
   new State(SPR_CPOS,6,3,null,S_CPOS_PAIN2,0,0),	// S_CPOS_PAIN
   new State(SPR_CPOS,6,3,new A_Pain(),S_CPOS_RUN1,0,0),	// S_CPOS_PAIN2
   new State(SPR_CPOS,7,5,null,S_CPOS_DIE2,0,0),	// S_CPOS_DIE1
   new State(SPR_CPOS,8,5,new A_Scream(),S_CPOS_DIE3,0,0),	// S_CPOS_DIE2
   new State(SPR_CPOS,9,5,new A_Fall(),S_CPOS_DIE4,0,0),	// S_CPOS_DIE3
   new State(SPR_CPOS,10,5,null,S_CPOS_DIE5,0,0),	// S_CPOS_DIE4
   new State(SPR_CPOS,11,5,null,S_CPOS_DIE6,0,0),	// S_CPOS_DIE5
   new State(SPR_CPOS,12,5,null,S_CPOS_DIE7,0,0),	// S_CPOS_DIE6
   new State(SPR_CPOS,13,-1,null,S_NULL,0,0),	// S_CPOS_DIE7
   new State(SPR_CPOS,14,5,null,S_CPOS_XDIE2,0,0),	// S_CPOS_XDIE1
   new State(SPR_CPOS,15,5,new A_XScream(),S_CPOS_XDIE3,0,0),	// S_CPOS_XDIE2
   new State(SPR_CPOS,16,5,new A_Fall(),S_CPOS_XDIE4,0,0),	// S_CPOS_XDIE3
   new State(SPR_CPOS,17,5,null,S_CPOS_XDIE5,0,0),	// S_CPOS_XDIE4
   new State(SPR_CPOS,18,5,null,S_CPOS_XDIE6,0,0),	// S_CPOS_XDIE5
   new State(SPR_CPOS,19,-1,null,S_NULL,0,0),	// S_CPOS_XDIE6
   new State(SPR_CPOS,13,5,null,S_CPOS_RAISE2,0,0),	// S_CPOS_RAISE1
   new State(SPR_CPOS,12,5,null,S_CPOS_RAISE3,0,0),	// S_CPOS_RAISE2
   new State(SPR_CPOS,11,5,null,S_CPOS_RAISE4,0,0),	// S_CPOS_RAISE3
   new State(SPR_CPOS,10,5,null,S_CPOS_RAISE5,0,0),	// S_CPOS_RAISE4
   new State(SPR_CPOS,9,5,null,S_CPOS_RAISE6,0,0),	// S_CPOS_RAISE5
   new State(SPR_CPOS,8,5,null,S_CPOS_RAISE7,0,0),	// S_CPOS_RAISE6
   new State(SPR_CPOS,7,5,null,S_CPOS_RUN1,0,0),	// S_CPOS_RAISE7
   new State(SPR_TROO,0,10,new A_Look(),S_TROO_STND2,0,0),	// S_TROO_STND
   new State(SPR_TROO,1,10,new A_Look(),S_TROO_STND,0,0),	// S_TROO_STND2
   new State(SPR_TROO,0,3,new A_Chase(),S_TROO_RUN2,0,0),	// S_TROO_RUN1
   new State(SPR_TROO,0,3,new A_Chase(),S_TROO_RUN3,0,0),	// S_TROO_RUN2
   new State(SPR_TROO,1,3,new A_Chase(),S_TROO_RUN4,0,0),	// S_TROO_RUN3
   new State(SPR_TROO,1,3,new A_Chase(),S_TROO_RUN5,0,0),	// S_TROO_RUN4
   new State(SPR_TROO,2,3,new A_Chase(),S_TROO_RUN6,0,0),	// S_TROO_RUN5
   new State(SPR_TROO,2,3,new A_Chase(),S_TROO_RUN7,0,0),	// S_TROO_RUN6
   new State(SPR_TROO,3,3,new A_Chase(),S_TROO_RUN8,0,0),	// S_TROO_RUN7
   new State(SPR_TROO,3,3,new A_Chase(),S_TROO_RUN1,0,0),	// S_TROO_RUN8
   new State(SPR_TROO,4,8,new A_FaceTarget(),S_TROO_ATK2,0,0),	// S_TROO_ATK1
   new State(SPR_TROO,5,8,new A_FaceTarget(),S_TROO_ATK3,0,0),	// S_TROO_ATK2
   new State(SPR_TROO,6,6,new A_TroopAttack(),S_TROO_RUN1,0,0),	// S_TROO_ATK3
   new State(SPR_TROO,7,2,null,S_TROO_PAIN2,0,0),	// S_TROO_PAIN
   new State(SPR_TROO,7,2,new A_Pain(),S_TROO_RUN1,0,0),	// S_TROO_PAIN2
   new State(SPR_TROO,8,8,null,S_TROO_DIE2,0,0),	// S_TROO_DIE1
   new State(SPR_TROO,9,8,new A_Scream(),S_TROO_DIE3,0,0),	// S_TROO_DIE2
   new State(SPR_TROO,10,6,null,S_TROO_DIE4,0,0),	// S_TROO_DIE3
   new State(SPR_TROO,11,6,new A_Fall(),S_TROO_DIE5,0,0),	// S_TROO_DIE4
   new State(SPR_TROO,12,-1,null,S_NULL,0,0),	// S_TROO_DIE5
   new State(SPR_TROO,13,5,null,S_TROO_XDIE2,0,0),	// S_TROO_XDIE1
   new State(SPR_TROO,14,5,new A_XScream(),S_TROO_XDIE3,0,0),	// S_TROO_XDIE2
   new State(SPR_TROO,15,5,null,S_TROO_XDIE4,0,0),	// S_TROO_XDIE3
   new State(SPR_TROO,16,5,new A_Fall(),S_TROO_XDIE5,0,0),	// S_TROO_XDIE4
   new State(SPR_TROO,17,5,null,S_TROO_XDIE6,0,0),	// S_TROO_XDIE5
   new State(SPR_TROO,18,5,null,S_TROO_XDIE7,0,0),	// S_TROO_XDIE6
   new State(SPR_TROO,19,5,null,S_TROO_XDIE8,0,0),	// S_TROO_XDIE7
   new State(SPR_TROO,20,-1,null,S_NULL,0,0),	// S_TROO_XDIE8
   new State(SPR_TROO,12,8,null,S_TROO_RAISE2,0,0),	// S_TROO_RAISE1
   new State(SPR_TROO,11,8,null,S_TROO_RAISE3,0,0),	// S_TROO_RAISE2
   new State(SPR_TROO,10,6,null,S_TROO_RAISE4,0,0),	// S_TROO_RAISE3
   new State(SPR_TROO,9,6,null,S_TROO_RAISE5,0,0),	// S_TROO_RAISE4
   new State(SPR_TROO,8,6,null,S_TROO_RUN1,0,0),	// S_TROO_RAISE5
   new State(SPR_SARG,0,10,new A_Look(),S_SARG_STND2,0,0),	// S_SARG_STND
   new State(SPR_SARG,1,10,new A_Look(),S_SARG_STND,0,0),	// S_SARG_STND2
   new State(SPR_SARG,0,2,new A_Chase(),S_SARG_RUN2,0,0),	// S_SARG_RUN1
   new State(SPR_SARG,0,2,new A_Chase(),S_SARG_RUN3,0,0),	// S_SARG_RUN2
   new State(SPR_SARG,1,2,new A_Chase(),S_SARG_RUN4,0,0),	// S_SARG_RUN3
   new State(SPR_SARG,1,2,new A_Chase(),S_SARG_RUN5,0,0),	// S_SARG_RUN4
   new State(SPR_SARG,2,2,new A_Chase(),S_SARG_RUN6,0,0),	// S_SARG_RUN5
   new State(SPR_SARG,2,2,new A_Chase(),S_SARG_RUN7,0,0),	// S_SARG_RUN6
   new State(SPR_SARG,3,2,new A_Chase(),S_SARG_RUN8,0,0),	// S_SARG_RUN7
   new State(SPR_SARG,3,2,new A_Chase(),S_SARG_RUN1,0,0),	// S_SARG_RUN8
   new State(SPR_SARG,4,8,new A_FaceTarget(),S_SARG_ATK2,0,0),	// S_SARG_ATK1
   new State(SPR_SARG,5,8,new A_FaceTarget(),S_SARG_ATK3,0,0),	// S_SARG_ATK2
   new State(SPR_SARG,6,8,new A_SargAttack(),S_SARG_RUN1,0,0),	// S_SARG_ATK3
   new State(SPR_SARG,7,2,null,S_SARG_PAIN2,0,0),	// S_SARG_PAIN
   new State(SPR_SARG,7,2,new A_Pain(),S_SARG_RUN1,0,0),	// S_SARG_PAIN2
   new State(SPR_SARG,8,8,null,S_SARG_DIE2,0,0),	// S_SARG_DIE1
   new State(SPR_SARG,9,8,new A_Scream(),S_SARG_DIE3,0,0),	// S_SARG_DIE2
   new State(SPR_SARG,10,4,null,S_SARG_DIE4,0,0),	// S_SARG_DIE3
   new State(SPR_SARG,11,4,new A_Fall(),S_SARG_DIE5,0,0),	// S_SARG_DIE4
   new State(SPR_SARG,12,4,null,S_SARG_DIE6,0,0),	// S_SARG_DIE5
   new State(SPR_SARG,13,-1,null,S_NULL,0,0),	// S_SARG_DIE6
   new State(SPR_SARG,13,5,null,S_SARG_RAISE2,0,0),	// S_SARG_RAISE1
   new State(SPR_SARG,12,5,null,S_SARG_RAISE3,0,0),	// S_SARG_RAISE2
   new State(SPR_SARG,11,5,null,S_SARG_RAISE4,0,0),	// S_SARG_RAISE3
   new State(SPR_SARG,10,5,null,S_SARG_RAISE5,0,0),	// S_SARG_RAISE4
   new State(SPR_SARG,9,5,null,S_SARG_RAISE6,0,0),	// S_SARG_RAISE5
   new State(SPR_SARG,8,5,null,S_SARG_RUN1,0,0),	// S_SARG_RAISE6
   new State(SPR_HEAD,0,10,new A_Look(),S_HEAD_STND,0,0),	// S_HEAD_STND
   new State(SPR_HEAD,0,3,new A_Chase(),S_HEAD_RUN1,0,0),	// S_HEAD_RUN1
   new State(SPR_HEAD,1,5,new A_FaceTarget(),S_HEAD_ATK2,0,0),	// S_HEAD_ATK1
   new State(SPR_HEAD,2,5,new A_FaceTarget(),S_HEAD_ATK3,0,0),	// S_HEAD_ATK2
   new State(SPR_HEAD,32771,5,new A_HeadAttack(),S_HEAD_RUN1,0,0),	// S_HEAD_ATK3
   new State(SPR_HEAD,4,3,null,S_HEAD_PAIN2,0,0),	// S_HEAD_PAIN
   new State(SPR_HEAD,4,3,new A_Pain(),S_HEAD_PAIN3,0,0),	// S_HEAD_PAIN2
   new State(SPR_HEAD,5,6,null,S_HEAD_RUN1,0,0),	// S_HEAD_PAIN3
   new State(SPR_HEAD,6,8,null,S_HEAD_DIE2,0,0),	// S_HEAD_DIE1
   new State(SPR_HEAD,7,8,new A_Scream(),S_HEAD_DIE3,0,0),	// S_HEAD_DIE2
   new State(SPR_HEAD,8,8,null,S_HEAD_DIE4,0,0),	// S_HEAD_DIE3
   new State(SPR_HEAD,9,8,null,S_HEAD_DIE5,0,0),	// S_HEAD_DIE4
   new State(SPR_HEAD,10,8,new A_Fall(),S_HEAD_DIE6,0,0),	// S_HEAD_DIE5
   new State(SPR_HEAD,11,-1,null,S_NULL,0,0),	// S_HEAD_DIE6
   new State(SPR_HEAD,11,8,null,S_HEAD_RAISE2,0,0),	// S_HEAD_RAISE1
   new State(SPR_HEAD,10,8,null,S_HEAD_RAISE3,0,0),	// S_HEAD_RAISE2
   new State(SPR_HEAD,9,8,null,S_HEAD_RAISE4,0,0),	// S_HEAD_RAISE3
   new State(SPR_HEAD,8,8,null,S_HEAD_RAISE5,0,0),	// S_HEAD_RAISE4
   new State(SPR_HEAD,7,8,null,S_HEAD_RAISE6,0,0),	// S_HEAD_RAISE5
   new State(SPR_HEAD,6,8,null,S_HEAD_RUN1,0,0),	// S_HEAD_RAISE6
   new State(SPR_BAL7,32768,4,null,S_BRBALL2,0,0),	// S_BRBALL1
   new State(SPR_BAL7,32769,4,null,S_BRBALL1,0,0),	// S_BRBALL2
   new State(SPR_BAL7,32770,6,null,S_BRBALLX2,0,0),	// S_BRBALLX1
   new State(SPR_BAL7,32771,6,null,S_BRBALLX3,0,0),	// S_BRBALLX2
   new State(SPR_BAL7,32772,6,null,S_NULL,0,0),	// S_BRBALLX3
   new State(SPR_BOSS,0,10,new A_Look(),S_BOSS_STND2,0,0),	// S_BOSS_STND
   new State(SPR_BOSS,1,10,new A_Look(),S_BOSS_STND,0,0),	// S_BOSS_STND2
   new State(SPR_BOSS,0,3,new A_Chase(),S_BOSS_RUN2,0,0),	// S_BOSS_RUN1
   new State(SPR_BOSS,0,3,new A_Chase(),S_BOSS_RUN3,0,0),	// S_BOSS_RUN2
   new State(SPR_BOSS,1,3,new A_Chase(),S_BOSS_RUN4,0,0),	// S_BOSS_RUN3
   new State(SPR_BOSS,1,3,new A_Chase(),S_BOSS_RUN5,0,0),	// S_BOSS_RUN4
   new State(SPR_BOSS,2,3,new A_Chase(),S_BOSS_RUN6,0,0),	// S_BOSS_RUN5
   new State(SPR_BOSS,2,3,new A_Chase(),S_BOSS_RUN7,0,0),	// S_BOSS_RUN6
   new State(SPR_BOSS,3,3,new A_Chase(),S_BOSS_RUN8,0,0),	// S_BOSS_RUN7
   new State(SPR_BOSS,3,3,new A_Chase(),S_BOSS_RUN1,0,0),	// S_BOSS_RUN8
   new State(SPR_BOSS,4,8,new A_FaceTarget(),S_BOSS_ATK2,0,0),	// S_BOSS_ATK1
   new State(SPR_BOSS,5,8,new A_FaceTarget(),S_BOSS_ATK3,0,0),	// S_BOSS_ATK2
   new State(SPR_BOSS,6,8,new A_BruisAttack(),S_BOSS_RUN1,0,0),	// S_BOSS_ATK3
   new State(SPR_BOSS,7,2,null,S_BOSS_PAIN2,0,0),	// S_BOSS_PAIN
   new State(SPR_BOSS,7,2,new A_Pain(),S_BOSS_RUN1,0,0),	// S_BOSS_PAIN2
   new State(SPR_BOSS,8,8,null,S_BOSS_DIE2,0,0),	// S_BOSS_DIE1
   new State(SPR_BOSS,9,8,new A_Scream(),S_BOSS_DIE3,0,0),	// S_BOSS_DIE2
   new State(SPR_BOSS,10,8,null,S_BOSS_DIE4,0,0),	// S_BOSS_DIE3
   new State(SPR_BOSS,11,8,new A_Fall(),S_BOSS_DIE5,0,0),	// S_BOSS_DIE4
   new State(SPR_BOSS,12,8,null,S_BOSS_DIE6,0,0),	// S_BOSS_DIE5
   new State(SPR_BOSS,13,8,null,S_BOSS_DIE7,0,0),	// S_BOSS_DIE6
   new State(SPR_BOSS,14,-1,new A_BossDeath(),S_NULL,0,0),	// S_BOSS_DIE7
   new State(SPR_BOSS,14,8,null,S_BOSS_RAISE2,0,0),	// S_BOSS_RAISE1
   new State(SPR_BOSS,13,8,null,S_BOSS_RAISE3,0,0),	// S_BOSS_RAISE2
   new State(SPR_BOSS,12,8,null,S_BOSS_RAISE4,0,0),	// S_BOSS_RAISE3
   new State(SPR_BOSS,11,8,null,S_BOSS_RAISE5,0,0),	// S_BOSS_RAISE4
   new State(SPR_BOSS,10,8,null,S_BOSS_RAISE6,0,0),	// S_BOSS_RAISE5
   new State(SPR_BOSS,9,8,null,S_BOSS_RAISE7,0,0),	// S_BOSS_RAISE6
   new State(SPR_BOSS,8,8,null,S_BOSS_RUN1,0,0),	// S_BOSS_RAISE7
   new State(SPR_BOS2,0,10,new A_Look(),S_BOS2_STND2,0,0),	// S_BOS2_STND
   new State(SPR_BOS2,1,10,new A_Look(),S_BOS2_STND,0,0),	// S_BOS2_STND2
   new State(SPR_BOS2,0,3,new A_Chase(),S_BOS2_RUN2,0,0),	// S_BOS2_RUN1
   new State(SPR_BOS2,0,3,new A_Chase(),S_BOS2_RUN3,0,0),	// S_BOS2_RUN2
   new State(SPR_BOS2,1,3,new A_Chase(),S_BOS2_RUN4,0,0),	// S_BOS2_RUN3
   new State(SPR_BOS2,1,3,new A_Chase(),S_BOS2_RUN5,0,0),	// S_BOS2_RUN4
   new State(SPR_BOS2,2,3,new A_Chase(),S_BOS2_RUN6,0,0),	// S_BOS2_RUN5
   new State(SPR_BOS2,2,3,new A_Chase(),S_BOS2_RUN7,0,0),	// S_BOS2_RUN6
   new State(SPR_BOS2,3,3,new A_Chase(),S_BOS2_RUN8,0,0),	// S_BOS2_RUN7
   new State(SPR_BOS2,3,3,new A_Chase(),S_BOS2_RUN1,0,0),	// S_BOS2_RUN8
   new State(SPR_BOS2,4,8,new A_FaceTarget(),S_BOS2_ATK2,0,0),	// S_BOS2_ATK1
   new State(SPR_BOS2,5,8,new A_FaceTarget(),S_BOS2_ATK3,0,0),	// S_BOS2_ATK2
   new State(SPR_BOS2,6,8,new A_BruisAttack(),S_BOS2_RUN1,0,0),	// S_BOS2_ATK3
   new State(SPR_BOS2,7,2,null,S_BOS2_PAIN2,0,0),	// S_BOS2_PAIN
   new State(SPR_BOS2,7,2,new A_Pain(),S_BOS2_RUN1,0,0),	// S_BOS2_PAIN2
   new State(SPR_BOS2,8,8,null,S_BOS2_DIE2,0,0),	// S_BOS2_DIE1
   new State(SPR_BOS2,9,8,new A_Scream(),S_BOS2_DIE3,0,0),	// S_BOS2_DIE2
   new State(SPR_BOS2,10,8,null,S_BOS2_DIE4,0,0),	// S_BOS2_DIE3
   new State(SPR_BOS2,11,8,new A_Fall(),S_BOS2_DIE5,0,0),	// S_BOS2_DIE4
   new State(SPR_BOS2,12,8,null,S_BOS2_DIE6,0,0),	// S_BOS2_DIE5
   new State(SPR_BOS2,13,8,null,S_BOS2_DIE7,0,0),	// S_BOS2_DIE6
   new State(SPR_BOS2,14,-1,null,S_NULL,0,0),	// S_BOS2_DIE7
   new State(SPR_BOS2,14,8,null,S_BOS2_RAISE2,0,0),	// S_BOS2_RAISE1
   new State(SPR_BOS2,13,8,null,S_BOS2_RAISE3,0,0),	// S_BOS2_RAISE2
   new State(SPR_BOS2,12,8,null,S_BOS2_RAISE4,0,0),	// S_BOS2_RAISE3
   new State(SPR_BOS2,11,8,null,S_BOS2_RAISE5,0,0),	// S_BOS2_RAISE4
   new State(SPR_BOS2,10,8,null,S_BOS2_RAISE6,0,0),	// S_BOS2_RAISE5
   new State(SPR_BOS2,9,8,null,S_BOS2_RAISE7,0,0),	// S_BOS2_RAISE6
   new State(SPR_BOS2,8,8,null,S_BOS2_RUN1,0,0),	// S_BOS2_RAISE7
   new State(SPR_SKUL,32768,10,new A_Look(),S_SKULL_STND2,0,0),	// S_SKULL_STND
   new State(SPR_SKUL,32769,10,new A_Look(),S_SKULL_STND,0,0),	// S_SKULL_STND2
   new State(SPR_SKUL,32768,6,new A_Chase(),S_SKULL_RUN2,0,0),	// S_SKULL_RUN1
   new State(SPR_SKUL,32769,6,new A_Chase(),S_SKULL_RUN1,0,0),	// S_SKULL_RUN2
   new State(SPR_SKUL,32770,10,new A_FaceTarget(),S_SKULL_ATK2,0,0),	// S_SKULL_ATK1
   new State(SPR_SKUL,32771,4,new A_SkullAttack(),S_SKULL_ATK3,0,0),	// S_SKULL_ATK2
   new State(SPR_SKUL,32770,4,null,S_SKULL_ATK4,0,0),	// S_SKULL_ATK3
   new State(SPR_SKUL,32771,4,null,S_SKULL_ATK3,0,0),	// S_SKULL_ATK4
   new State(SPR_SKUL,32772,3,null,S_SKULL_PAIN2,0,0),	// S_SKULL_PAIN
   new State(SPR_SKUL,32772,3,new A_Pain(),S_SKULL_RUN1,0,0),	// S_SKULL_PAIN2
   new State(SPR_SKUL,32773,6,null,S_SKULL_DIE2,0,0),	// S_SKULL_DIE1
   new State(SPR_SKUL,32774,6,new A_Scream(),S_SKULL_DIE3,0,0),	// S_SKULL_DIE2
   new State(SPR_SKUL,32775,6,null,S_SKULL_DIE4,0,0),	// S_SKULL_DIE3
   new State(SPR_SKUL,32776,6,new A_Fall(),S_SKULL_DIE5,0,0),	// S_SKULL_DIE4
   new State(SPR_SKUL,9,6,null,S_SKULL_DIE6,0,0),	// S_SKULL_DIE5
   new State(SPR_SKUL,10,6,null,S_NULL,0,0),	// S_SKULL_DIE6
   new State(SPR_SPID,0,10,new A_Look(),S_SPID_STND2,0,0),	// S_SPID_STND
   new State(SPR_SPID,1,10,new A_Look(),S_SPID_STND,0,0),	// S_SPID_STND2
   new State(SPR_SPID,0,3,new A_Metal(),S_SPID_RUN2,0,0),	// S_SPID_RUN1
   new State(SPR_SPID,0,3,new A_Chase(),S_SPID_RUN3,0,0),	// S_SPID_RUN2
   new State(SPR_SPID,1,3,new A_Chase(),S_SPID_RUN4,0,0),	// S_SPID_RUN3
   new State(SPR_SPID,1,3,new A_Chase(),S_SPID_RUN5,0,0),	// S_SPID_RUN4
   new State(SPR_SPID,2,3,new A_Metal(),S_SPID_RUN6,0,0),	// S_SPID_RUN5
   new State(SPR_SPID,2,3,new A_Chase(),S_SPID_RUN7,0,0),	// S_SPID_RUN6
   new State(SPR_SPID,3,3,new A_Chase(),S_SPID_RUN8,0,0),	// S_SPID_RUN7
   new State(SPR_SPID,3,3,new A_Chase(),S_SPID_RUN9,0,0),	// S_SPID_RUN8
   new State(SPR_SPID,4,3,new A_Metal(),S_SPID_RUN10,0,0),	// S_SPID_RUN9
   new State(SPR_SPID,4,3,new A_Chase(),S_SPID_RUN11,0,0),	// S_SPID_RUN10
   new State(SPR_SPID,5,3,new A_Chase(),S_SPID_RUN12,0,0),	// S_SPID_RUN11
   new State(SPR_SPID,5,3,new A_Chase(),S_SPID_RUN1,0,0),	// S_SPID_RUN12
   new State(SPR_SPID,32768,20,new A_FaceTarget(),S_SPID_ATK2,0,0),	// S_SPID_ATK1
   new State(SPR_SPID,32774,4,new A_SPosAttack(),S_SPID_ATK3,0,0),	// S_SPID_ATK2
   new State(SPR_SPID,32775,4,new A_SPosAttack(),S_SPID_ATK4,0,0),	// S_SPID_ATK3
   new State(SPR_SPID,32775,1,new A_SpidRefire(),S_SPID_ATK2,0,0),	// S_SPID_ATK4
   new State(SPR_SPID,8,3,null,S_SPID_PAIN2,0,0),	// S_SPID_PAIN
   new State(SPR_SPID,8,3,new A_Pain(),S_SPID_RUN1,0,0),	// S_SPID_PAIN2
   new State(SPR_SPID,9,20,new A_Scream(),S_SPID_DIE2,0,0),	// S_SPID_DIE1
   new State(SPR_SPID,10,10,new A_Fall(),S_SPID_DIE3,0,0),	// S_SPID_DIE2
   new State(SPR_SPID,11,10,null,S_SPID_DIE4,0,0),	// S_SPID_DIE3
   new State(SPR_SPID,12,10,null,S_SPID_DIE5,0,0),	// S_SPID_DIE4
   new State(SPR_SPID,13,10,null,S_SPID_DIE6,0,0),	// S_SPID_DIE5
   new State(SPR_SPID,14,10,null,S_SPID_DIE7,0,0),	// S_SPID_DIE6
   new State(SPR_SPID,15,10,null,S_SPID_DIE8,0,0),	// S_SPID_DIE7
   new State(SPR_SPID,16,10,null,S_SPID_DIE9,0,0),	// S_SPID_DIE8
   new State(SPR_SPID,17,10,null,S_SPID_DIE10,0,0),	// S_SPID_DIE9
   new State(SPR_SPID,18,30,null,S_SPID_DIE11,0,0),	// S_SPID_DIE10
   new State(SPR_SPID,18,-1,new A_BossDeath(),S_NULL,0,0),	// S_SPID_DIE11
   new State(SPR_BSPI,0,10,new A_Look(),S_BSPI_STND2,0,0),	// S_BSPI_STND
   new State(SPR_BSPI,1,10,new A_Look(),S_BSPI_STND,0,0),	// S_BSPI_STND2
   new State(SPR_BSPI,0,20,null,S_BSPI_RUN1,0,0),	// S_BSPI_SIGHT
   new State(SPR_BSPI,0,3,new A_BabyMetal(),S_BSPI_RUN2,0,0),	// S_BSPI_RUN1
   new State(SPR_BSPI,0,3,new A_Chase(),S_BSPI_RUN3,0,0),	// S_BSPI_RUN2
   new State(SPR_BSPI,1,3,new A_Chase(),S_BSPI_RUN4,0,0),	// S_BSPI_RUN3
   new State(SPR_BSPI,1,3,new A_Chase(),S_BSPI_RUN5,0,0),	// S_BSPI_RUN4
   new State(SPR_BSPI,2,3,new A_Chase(),S_BSPI_RUN6,0,0),	// S_BSPI_RUN5
   new State(SPR_BSPI,2,3,new A_Chase(),S_BSPI_RUN7,0,0),	// S_BSPI_RUN6
   new State(SPR_BSPI,3,3,new A_BabyMetal(),S_BSPI_RUN8,0,0),	// S_BSPI_RUN7
   new State(SPR_BSPI,3,3,new A_Chase(),S_BSPI_RUN9,0,0),	// S_BSPI_RUN8
   new State(SPR_BSPI,4,3,new A_Chase(),S_BSPI_RUN10,0,0),	// S_BSPI_RUN9
   new State(SPR_BSPI,4,3,new A_Chase(),S_BSPI_RUN11,0,0),	// S_BSPI_RUN10
   new State(SPR_BSPI,5,3,new A_Chase(),S_BSPI_RUN12,0,0),	// S_BSPI_RUN11
   new State(SPR_BSPI,5,3,new A_Chase(),S_BSPI_RUN1,0,0),	// S_BSPI_RUN12
   new State(SPR_BSPI,32768,20,new A_FaceTarget(),S_BSPI_ATK2,0,0),	// S_BSPI_ATK1
   new State(SPR_BSPI,32774,4,new A_BspiAttack(),S_BSPI_ATK3,0,0),	// S_BSPI_ATK2
   new State(SPR_BSPI,32775,4,null,S_BSPI_ATK4,0,0),	// S_BSPI_ATK3
   new State(SPR_BSPI,32775,1,new A_SpidRefire(),S_BSPI_ATK2,0,0),	// S_BSPI_ATK4
   new State(SPR_BSPI,8,3,null,S_BSPI_PAIN2,0,0),	// S_BSPI_PAIN
   new State(SPR_BSPI,8,3,new A_Pain(),S_BSPI_RUN1,0,0),	// S_BSPI_PAIN2
   new State(SPR_BSPI,9,20,new A_Scream(),S_BSPI_DIE2,0,0),	// S_BSPI_DIE1
   new State(SPR_BSPI,10,7,new A_Fall(),S_BSPI_DIE3,0,0),	// S_BSPI_DIE2
   new State(SPR_BSPI,11,7,null,S_BSPI_DIE4,0,0),	// S_BSPI_DIE3
   new State(SPR_BSPI,12,7,null,S_BSPI_DIE5,0,0),	// S_BSPI_DIE4
   new State(SPR_BSPI,13,7,null,S_BSPI_DIE6,0,0),	// S_BSPI_DIE5
   new State(SPR_BSPI,14,7,null,S_BSPI_DIE7,0,0),	// S_BSPI_DIE6
   new State(SPR_BSPI,15,-1,new A_BossDeath(),S_NULL,0,0),	// S_BSPI_DIE7
   new State(SPR_BSPI,15,5,null,S_BSPI_RAISE2,0,0),	// S_BSPI_RAISE1
   new State(SPR_BSPI,14,5,null,S_BSPI_RAISE3,0,0),	// S_BSPI_RAISE2
   new State(SPR_BSPI,13,5,null,S_BSPI_RAISE4,0,0),	// S_BSPI_RAISE3
   new State(SPR_BSPI,12,5,null,S_BSPI_RAISE5,0,0),	// S_BSPI_RAISE4
   new State(SPR_BSPI,11,5,null,S_BSPI_RAISE6,0,0),	// S_BSPI_RAISE5
   new State(SPR_BSPI,10,5,null,S_BSPI_RAISE7,0,0),	// S_BSPI_RAISE6
   new State(SPR_BSPI,9,5,null,S_BSPI_RUN1,0,0),	// S_BSPI_RAISE7
   new State(SPR_APLS,32768,5,null,S_ARACH_PLAZ2,0,0),	// S_ARACH_PLAZ
   new State(SPR_APLS,32769,5,null,S_ARACH_PLAZ,0,0),	// S_ARACH_PLAZ2
   new State(SPR_APBX,32768,5,null,S_ARACH_PLEX2,0,0),	// S_ARACH_PLEX
   new State(SPR_APBX,32769,5,null,S_ARACH_PLEX3,0,0),	// S_ARACH_PLEX2
   new State(SPR_APBX,32770,5,null,S_ARACH_PLEX4,0,0),	// S_ARACH_PLEX3
   new State(SPR_APBX,32771,5,null,S_ARACH_PLEX5,0,0),	// S_ARACH_PLEX4
   new State(SPR_APBX,32772,5,null,S_NULL,0,0),	// S_ARACH_PLEX5
   new State(SPR_CYBR,0,10,new A_Look(),S_CYBER_STND2,0,0),	// S_CYBER_STND
   new State(SPR_CYBR,1,10,new A_Look(),S_CYBER_STND,0,0),	// S_CYBER_STND2
   new State(SPR_CYBR,0,3,new A_Hoof(),S_CYBER_RUN2,0,0),	// S_CYBER_RUN1
   new State(SPR_CYBR,0,3,new A_Chase(),S_CYBER_RUN3,0,0),	// S_CYBER_RUN2
   new State(SPR_CYBR,1,3,new A_Chase(),S_CYBER_RUN4,0,0),	// S_CYBER_RUN3
   new State(SPR_CYBR,1,3,new A_Chase(),S_CYBER_RUN5,0,0),	// S_CYBER_RUN4
   new State(SPR_CYBR,2,3,new A_Chase(),S_CYBER_RUN6,0,0),	// S_CYBER_RUN5
   new State(SPR_CYBR,2,3,new A_Chase(),S_CYBER_RUN7,0,0),	// S_CYBER_RUN6
   new State(SPR_CYBR,3,3,new A_Metal(),S_CYBER_RUN8,0,0),	// S_CYBER_RUN7
   new State(SPR_CYBR,3,3,new A_Chase(),S_CYBER_RUN1,0,0),	// S_CYBER_RUN8
   new State(SPR_CYBR,4,6,new A_FaceTarget(),S_CYBER_ATK2,0,0),	// S_CYBER_ATK1
   new State(SPR_CYBR,5,12,new A_CyberAttack(),S_CYBER_ATK3,0,0),	// S_CYBER_ATK2
   new State(SPR_CYBR,4,12,new A_FaceTarget(),S_CYBER_ATK4,0,0),	// S_CYBER_ATK3
   new State(SPR_CYBR,5,12,new A_CyberAttack(),S_CYBER_ATK5,0,0),	// S_CYBER_ATK4
   new State(SPR_CYBR,4,12,new A_FaceTarget(),S_CYBER_ATK6,0,0),	// S_CYBER_ATK5
   new State(SPR_CYBR,5,12,new A_CyberAttack(),S_CYBER_RUN1,0,0),	// S_CYBER_ATK6
   new State(SPR_CYBR,6,10,new A_Pain(),S_CYBER_RUN1,0,0),	// S_CYBER_PAIN
   new State(SPR_CYBR,7,10,null,S_CYBER_DIE2,0,0),	// S_CYBER_DIE1
   new State(SPR_CYBR,8,10,new A_Scream(),S_CYBER_DIE3,0,0),	// S_CYBER_DIE2
   new State(SPR_CYBR,9,10,null,S_CYBER_DIE4,0,0),	// S_CYBER_DIE3
   new State(SPR_CYBR,10,10,null,S_CYBER_DIE5,0,0),	// S_CYBER_DIE4
   new State(SPR_CYBR,11,10,null,S_CYBER_DIE6,0,0),	// S_CYBER_DIE5
   new State(SPR_CYBR,12,10,new A_Fall(),S_CYBER_DIE7,0,0),	// S_CYBER_DIE6
   new State(SPR_CYBR,13,10,null,S_CYBER_DIE8,0,0),	// S_CYBER_DIE7
   new State(SPR_CYBR,14,10,null,S_CYBER_DIE9,0,0),	// S_CYBER_DIE8
   new State(SPR_CYBR,15,30,null,S_CYBER_DIE10,0,0),	// S_CYBER_DIE9
   new State(SPR_CYBR,15,-1,new A_BossDeath(),S_NULL,0,0),	// S_CYBER_DIE10
   new State(SPR_PAIN,0,10,new A_Look(),S_PAIN_STND,0,0),	// S_PAIN_STND
   new State(SPR_PAIN,0,3,new A_Chase(),S_PAIN_RUN2,0,0),	// S_PAIN_RUN1
   new State(SPR_PAIN,0,3,new A_Chase(),S_PAIN_RUN3,0,0),	// S_PAIN_RUN2
   new State(SPR_PAIN,1,3,new A_Chase(),S_PAIN_RUN4,0,0),	// S_PAIN_RUN3
   new State(SPR_PAIN,1,3,new A_Chase(),S_PAIN_RUN5,0,0),	// S_PAIN_RUN4
   new State(SPR_PAIN,2,3,new A_Chase(),S_PAIN_RUN6,0,0),	// S_PAIN_RUN5
   new State(SPR_PAIN,2,3,new A_Chase(),S_PAIN_RUN1,0,0),	// S_PAIN_RUN6
   new State(SPR_PAIN,3,5,new A_FaceTarget(),S_PAIN_ATK2,0,0),	// S_PAIN_ATK1
   new State(SPR_PAIN,4,5,new A_FaceTarget(),S_PAIN_ATK3,0,0),	// S_PAIN_ATK2
   new State(SPR_PAIN,32773,5,new A_FaceTarget(),S_PAIN_ATK4,0,0),	// S_PAIN_ATK3
   new State(SPR_PAIN,32773,0,new A_PainAttack(),S_PAIN_RUN1,0,0),	// S_PAIN_ATK4
   new State(SPR_PAIN,6,6,null,S_PAIN_PAIN2,0,0),	// S_PAIN_PAIN
   new State(SPR_PAIN,6,6,new A_Pain(),S_PAIN_RUN1,0,0),	// S_PAIN_PAIN2
   new State(SPR_PAIN,32775,8,null,S_PAIN_DIE2,0,0),	// S_PAIN_DIE1
   new State(SPR_PAIN,32776,8,new A_Scream(),S_PAIN_DIE3,0,0),	// S_PAIN_DIE2
   new State(SPR_PAIN,32777,8,null,S_PAIN_DIE4,0,0),	// S_PAIN_DIE3
   new State(SPR_PAIN,32778,8,null,S_PAIN_DIE5,0,0),	// S_PAIN_DIE4
   new State(SPR_PAIN,32779,8,new A_PainDie(),S_PAIN_DIE6,0,0),	// S_PAIN_DIE5
   new State(SPR_PAIN,32780,8,null,S_NULL,0,0),	// S_PAIN_DIE6
   new State(SPR_PAIN,12,8,null,S_PAIN_RAISE2,0,0),	// S_PAIN_RAISE1
   new State(SPR_PAIN,11,8,null,S_PAIN_RAISE3,0,0),	// S_PAIN_RAISE2
   new State(SPR_PAIN,10,8,null,S_PAIN_RAISE4,0,0),	// S_PAIN_RAISE3
   new State(SPR_PAIN,9,8,null,S_PAIN_RAISE5,0,0),	// S_PAIN_RAISE4
   new State(SPR_PAIN,8,8,null,S_PAIN_RAISE6,0,0),	// S_PAIN_RAISE5
   new State(SPR_PAIN,7,8,null,S_PAIN_RUN1,0,0),	// S_PAIN_RAISE6
   new State(SPR_SSWV,0,10,new A_Look(),S_SSWV_STND2,0,0),	// S_SSWV_STND
   new State(SPR_SSWV,1,10,new A_Look(),S_SSWV_STND,0,0),	// S_SSWV_STND2
   new State(SPR_SSWV,0,3,new A_Chase(),S_SSWV_RUN2,0,0),	// S_SSWV_RUN1
   new State(SPR_SSWV,0,3,new A_Chase(),S_SSWV_RUN3,0,0),	// S_SSWV_RUN2
   new State(SPR_SSWV,1,3,new A_Chase(),S_SSWV_RUN4,0,0),	// S_SSWV_RUN3
   new State(SPR_SSWV,1,3,new A_Chase(),S_SSWV_RUN5,0,0),	// S_SSWV_RUN4
   new State(SPR_SSWV,2,3,new A_Chase(),S_SSWV_RUN6,0,0),	// S_SSWV_RUN5
   new State(SPR_SSWV,2,3,new A_Chase(),S_SSWV_RUN7,0,0),	// S_SSWV_RUN6
   new State(SPR_SSWV,3,3,new A_Chase(),S_SSWV_RUN8,0,0),	// S_SSWV_RUN7
   new State(SPR_SSWV,3,3,new A_Chase(),S_SSWV_RUN1,0,0),	// S_SSWV_RUN8
   new State(SPR_SSWV,4,10,new A_FaceTarget(),S_SSWV_ATK2,0,0),	// S_SSWV_ATK1
   new State(SPR_SSWV,5,10,new A_FaceTarget(),S_SSWV_ATK3,0,0),	// S_SSWV_ATK2
   new State(SPR_SSWV,32774,4,new A_CPosAttack(),S_SSWV_ATK4,0,0),	// S_SSWV_ATK3
   new State(SPR_SSWV,5,6,new A_FaceTarget(),S_SSWV_ATK5,0,0),	// S_SSWV_ATK4
   new State(SPR_SSWV,32774,4,new A_CPosAttack(),S_SSWV_ATK6,0,0),	// S_SSWV_ATK5
   new State(SPR_SSWV,5,1,new A_CPosRefire(),S_SSWV_ATK2,0,0),	// S_SSWV_ATK6
   new State(SPR_SSWV,7,3,null,S_SSWV_PAIN2,0,0),	// S_SSWV_PAIN
   new State(SPR_SSWV,7,3,new A_Pain(),S_SSWV_RUN1,0,0),	// S_SSWV_PAIN2
   new State(SPR_SSWV,8,5,null,S_SSWV_DIE2,0,0),	// S_SSWV_DIE1
   new State(SPR_SSWV,9,5,new A_Scream(),S_SSWV_DIE3,0,0),	// S_SSWV_DIE2
   new State(SPR_SSWV,10,5,new A_Fall(),S_SSWV_DIE4,0,0),	// S_SSWV_DIE3
   new State(SPR_SSWV,11,5,null,S_SSWV_DIE5,0,0),	// S_SSWV_DIE4
   new State(SPR_SSWV,12,-1,null,S_NULL,0,0),	// S_SSWV_DIE5
   new State(SPR_SSWV,13,5,null,S_SSWV_XDIE2,0,0),	// S_SSWV_XDIE1
   new State(SPR_SSWV,14,5,new A_XScream(),S_SSWV_XDIE3,0,0),	// S_SSWV_XDIE2
   new State(SPR_SSWV,15,5,new A_Fall(),S_SSWV_XDIE4,0,0),	// S_SSWV_XDIE3
   new State(SPR_SSWV,16,5,null,S_SSWV_XDIE5,0,0),	// S_SSWV_XDIE4
   new State(SPR_SSWV,17,5,null,S_SSWV_XDIE6,0,0),	// S_SSWV_XDIE5
   new State(SPR_SSWV,18,5,null,S_SSWV_XDIE7,0,0),	// S_SSWV_XDIE6
   new State(SPR_SSWV,19,5,null,S_SSWV_XDIE8,0,0),	// S_SSWV_XDIE7
   new State(SPR_SSWV,20,5,null,S_SSWV_XDIE9,0,0),	// S_SSWV_XDIE8
   new State(SPR_SSWV,21,-1,null,S_NULL,0,0),	// S_SSWV_XDIE9
   new State(SPR_SSWV,12,5,null,S_SSWV_RAISE2,0,0),	// S_SSWV_RAISE1
   new State(SPR_SSWV,11,5,null,S_SSWV_RAISE3,0,0),	// S_SSWV_RAISE2
   new State(SPR_SSWV,10,5,null,S_SSWV_RAISE4,0,0),	// S_SSWV_RAISE3
   new State(SPR_SSWV,9,5,null,S_SSWV_RAISE5,0,0),	// S_SSWV_RAISE4
   new State(SPR_SSWV,8,5,null,S_SSWV_RUN1,0,0),	// S_SSWV_RAISE5
   new State(SPR_KEEN,0,-1,null,S_KEENSTND,0,0),	// S_KEENSTND
   new State(SPR_KEEN,0,6,null,S_COMMKEEN2,0,0),	// S_COMMKEEN
   new State(SPR_KEEN,1,6,null,S_COMMKEEN3,0,0),	// S_COMMKEEN2
   new State(SPR_KEEN,2,6,new A_Scream(),S_COMMKEEN4,0,0),	// S_COMMKEEN3
   new State(SPR_KEEN,3,6,null,S_COMMKEEN5,0,0),	// S_COMMKEEN4
   new State(SPR_KEEN,4,6,null,S_COMMKEEN6,0,0),	// S_COMMKEEN5
   new State(SPR_KEEN,5,6,null,S_COMMKEEN7,0,0),	// S_COMMKEEN6
   new State(SPR_KEEN,6,6,null,S_COMMKEEN8,0,0),	// S_COMMKEEN7
   new State(SPR_KEEN,7,6,null,S_COMMKEEN9,0,0),	// S_COMMKEEN8
   new State(SPR_KEEN,8,6,null,S_COMMKEEN10,0,0),	// S_COMMKEEN9
   new State(SPR_KEEN,9,6,null,S_COMMKEEN11,0,0),	// S_COMMKEEN10
   new State(SPR_KEEN,10,6,new A_KeenDie(),S_COMMKEEN12,0,0),// S_COMMKEEN11
   new State(SPR_KEEN,11,-1,null,S_NULL,0,0),		// S_COMMKEEN12
   new State(SPR_KEEN,12,4,null,S_KEENPAIN2,0,0),	// S_KEENPAIN
   new State(SPR_KEEN,12,8,new A_Pain(),S_KEENSTND,0,0),	// S_KEENPAIN2
   new State(SPR_BBRN,0,-1,null,S_NULL,0,0),		// S_BRAIN
   new State(SPR_BBRN,1,36,new A_BrainPain(),S_BRAIN,0,0),	// S_BRAIN_PAIN
   new State(SPR_BBRN,0,100,new A_BrainScream(),S_BRAIN_DIE2,0,0),	// S_BRAIN_DIE1
   new State(SPR_BBRN,0,10,null,S_BRAIN_DIE3,0,0),	// S_BRAIN_DIE2
   new State(SPR_BBRN,0,10,null,S_BRAIN_DIE4,0,0),	// S_BRAIN_DIE3
   new State(SPR_BBRN,0,-1,new A_BrainDie(),S_NULL,0,0),	// S_BRAIN_DIE4
   new State(SPR_SSWV,0,10,new A_Look(),S_BRAINEYE,0,0),	// S_BRAINEYE
   new State(SPR_SSWV,0,181,new A_BrainAwake(),S_BRAINEYE1,0,0),	// S_BRAINEYESEE
   new State(SPR_SSWV,0,150,new A_BrainSpit(),S_BRAINEYE1,0,0),	// S_BRAINEYE1
   new State(SPR_BOSF,32768,3,new A_SpawnSound(),S_SPAWN2,0,0),	// S_SPAWN1
   new State(SPR_BOSF,32769,3,new A_SpawnFly(),S_SPAWN3,0,0),	// S_SPAWN2
   new State(SPR_BOSF,32770,3,new A_SpawnFly(),S_SPAWN4,0,0),	// S_SPAWN3
   new State(SPR_BOSF,32771,3,new A_SpawnFly(),S_SPAWN1,0,0),	// S_SPAWN4
   new State(SPR_FIRE,32768,4,new A_Fire(),S_SPAWNFIRE2,0,0),	// S_SPAWNFIRE1
   new State(SPR_FIRE,32769,4,new A_Fire(),S_SPAWNFIRE3,0,0),	// S_SPAWNFIRE2
   new State(SPR_FIRE,32770,4,new A_Fire(),S_SPAWNFIRE4,0,0),	// S_SPAWNFIRE3
   new State(SPR_FIRE,32771,4,new A_Fire(),S_SPAWNFIRE5,0,0),	// S_SPAWNFIRE4
   new State(SPR_FIRE,32772,4,new A_Fire(),S_SPAWNFIRE6,0,0),	// S_SPAWNFIRE5
   new State(SPR_FIRE,32773,4,new A_Fire(),S_SPAWNFIRE7,0,0),	// S_SPAWNFIRE6
   new State(SPR_FIRE,32774,4,new A_Fire(),S_SPAWNFIRE8,0,0),	// S_SPAWNFIRE7
   new State(SPR_FIRE,32775,4,new A_Fire(),S_NULL,0,0),		// S_SPAWNFIRE8
   new State(SPR_MISL,32769,10,null,S_BRAINEXPLODE2,0,0),	// S_BRAINEXPLODE1
   new State(SPR_MISL,32770,10,null,S_BRAINEXPLODE3,0,0),	// S_BRAINEXPLODE2
   new State(SPR_MISL,32771,10,new A_BrainExplode(),S_NULL,0,0),	// S_BRAINEXPLODE3
   new State(SPR_ARM1,0,6,null,S_ARM1A,0,0),	// S_ARM1
   new State(SPR_ARM1,32769,7,null,S_ARM1,0,0),	// S_ARM1A
   new State(SPR_ARM2,0,6,null,S_ARM2A,0,0),	// S_ARM2
   new State(SPR_ARM2,32769,6,null,S_ARM2,0,0),	// S_ARM2A
   new State(SPR_BAR1,0,6,null,S_BAR2,0,0),	// S_BAR1
   new State(SPR_BAR1,1,6,null,S_BAR1,0,0),	// S_BAR2
   new State(SPR_BEXP,32768,5,null,S_BEXP2,0,0),	// S_BEXP
   new State(SPR_BEXP,32769,5,new A_Scream(),S_BEXP3,0,0),	// S_BEXP2
   new State(SPR_BEXP,32770,5,null,S_BEXP4,0,0),	// S_BEXP3
   new State(SPR_BEXP,32771,10,new A_Explode(),S_BEXP5,0,0),	// S_BEXP4
   new State(SPR_BEXP,32772,10,null,S_NULL,0,0),	// S_BEXP5
   new State(SPR_FCAN,32768,4,null,S_BBAR2,0,0),	// S_BBAR1
   new State(SPR_FCAN,32769,4,null,S_BBAR3,0,0),	// S_BBAR2
   new State(SPR_FCAN,32770,4,null,S_BBAR1,0,0),	// S_BBAR3
   new State(SPR_BON1,0,6,null,S_BON1A,0,0),	// S_BON1
   new State(SPR_BON1,1,6,null,S_BON1B,0,0),	// S_BON1A
   new State(SPR_BON1,2,6,null,S_BON1C,0,0),	// S_BON1B
   new State(SPR_BON1,3,6,null,S_BON1D,0,0),	// S_BON1C
   new State(SPR_BON1,2,6,null,S_BON1E,0,0),	// S_BON1D
   new State(SPR_BON1,1,6,null,S_BON1,0,0),	// S_BON1E
   new State(SPR_BON2,0,6,null,S_BON2A,0,0),	// S_BON2
   new State(SPR_BON2,1,6,null,S_BON2B,0,0),	// S_BON2A
   new State(SPR_BON2,2,6,null,S_BON2C,0,0),	// S_BON2B
   new State(SPR_BON2,3,6,null,S_BON2D,0,0),	// S_BON2C
   new State(SPR_BON2,2,6,null,S_BON2E,0,0),	// S_BON2D
   new State(SPR_BON2,1,6,null,S_BON2,0,0),	// S_BON2E
   new State(SPR_BKEY,0,10,null,S_BKEY2,0,0),	// S_BKEY
   new State(SPR_BKEY,32769,10,null,S_BKEY,0,0),	// S_BKEY2
   new State(SPR_RKEY,0,10,null,S_RKEY2,0,0),	// S_RKEY
   new State(SPR_RKEY,32769,10,null,S_RKEY,0,0),	// S_RKEY2
   new State(SPR_YKEY,0,10,null,S_YKEY2,0,0),	// S_YKEY
   new State(SPR_YKEY,32769,10,null,S_YKEY,0,0),	// S_YKEY2
   new State(SPR_BSKU,0,10,null,S_BSKULL2,0,0),	// S_BSKULL
   new State(SPR_BSKU,32769,10,null,S_BSKULL,0,0),	// S_BSKULL2
   new State(SPR_RSKU,0,10,null,S_RSKULL2,0,0),	// S_RSKULL
   new State(SPR_RSKU,32769,10,null,S_RSKULL,0,0),	// S_RSKULL2
   new State(SPR_YSKU,0,10,null,S_YSKULL2,0,0),	// S_YSKULL
   new State(SPR_YSKU,32769,10,null,S_YSKULL,0,0),	// S_YSKULL2
   new State(SPR_STIM,0,-1,null,S_NULL,0,0),	// S_STIM
   new State(SPR_MEDI,0,-1,null,S_NULL,0,0),	// S_MEDI
   new State(SPR_SOUL,32768,6,null,S_SOUL2,0,0),	// S_SOUL
   new State(SPR_SOUL,32769,6,null,S_SOUL3,0,0),	// S_SOUL2
   new State(SPR_SOUL,32770,6,null,S_SOUL4,0,0),	// S_SOUL3
   new State(SPR_SOUL,32771,6,null,S_SOUL5,0,0),	// S_SOUL4
   new State(SPR_SOUL,32770,6,null,S_SOUL6,0,0),	// S_SOUL5
   new State(SPR_SOUL,32769,6,null,S_SOUL,0,0),	// S_SOUL6
   new State(SPR_PINV,32768,6,null,S_PINV2,0,0),	// S_PINV
   new State(SPR_PINV,32769,6,null,S_PINV3,0,0),	// S_PINV2
   new State(SPR_PINV,32770,6,null,S_PINV4,0,0),	// S_PINV3
   new State(SPR_PINV,32771,6,null,S_PINV,0,0),	// S_PINV4
   new State(SPR_PSTR,32768,-1,null,S_NULL,0,0),	// S_PSTR
   new State(SPR_PINS,32768,6,null,S_PINS2,0,0),	// S_PINS
   new State(SPR_PINS,32769,6,null,S_PINS3,0,0),	// S_PINS2
   new State(SPR_PINS,32770,6,null,S_PINS4,0,0),	// S_PINS3
   new State(SPR_PINS,32771,6,null,S_PINS,0,0),	// S_PINS4
   new State(SPR_MEGA,32768,6,null,S_MEGA2,0,0),	// S_MEGA
   new State(SPR_MEGA,32769,6,null,S_MEGA3,0,0),	// S_MEGA2
   new State(SPR_MEGA,32770,6,null,S_MEGA4,0,0),	// S_MEGA3
   new State(SPR_MEGA,32771,6,null,S_MEGA,0,0),	// S_MEGA4
   new State(SPR_SUIT,32768,-1,null,S_NULL,0,0),	// S_SUIT
   new State(SPR_PMAP,32768,6,null,S_PMAP2,0,0),	// S_PMAP
   new State(SPR_PMAP,32769,6,null,S_PMAP3,0,0),	// S_PMAP2
   new State(SPR_PMAP,32770,6,null,S_PMAP4,0,0),	// S_PMAP3
   new State(SPR_PMAP,32771,6,null,S_PMAP5,0,0),	// S_PMAP4
   new State(SPR_PMAP,32770,6,null,S_PMAP6,0,0),	// S_PMAP5
   new State(SPR_PMAP,32769,6,null,S_PMAP,0,0),	// S_PMAP6
   new State(SPR_PVIS,32768,6,null,S_PVIS2,0,0),	// S_PVIS
   new State(SPR_PVIS,1,6,null,S_PVIS,0,0),	// S_PVIS2
   new State(SPR_CLIP,0,-1,null,S_NULL,0,0),	// S_CLIP
   new State(SPR_AMMO,0,-1,null,S_NULL,0,0),	// S_AMMO
   new State(SPR_ROCK,0,-1,null,S_NULL,0,0),	// S_ROCK
   new State(SPR_BROK,0,-1,null,S_NULL,0,0),	// S_BROK
   new State(SPR_CELL,0,-1,null,S_NULL,0,0),	// S_CELL
   new State(SPR_CELP,0,-1,null,S_NULL,0,0),	// S_CELP
   new State(SPR_SHEL,0,-1,null,S_NULL,0,0),	// S_SHEL
   new State(SPR_SBOX,0,-1,null,S_NULL,0,0),	// S_SBOX
   new State(SPR_BPAK,0,-1,null,S_NULL,0,0),	// S_BPAK
   new State(SPR_BFUG,0,-1,null,S_NULL,0,0),	// S_BFUG
   new State(SPR_MGUN,0,-1,null,S_NULL,0,0),	// S_MGUN
   new State(SPR_CSAW,0,-1,null,S_NULL,0,0),	// S_CSAW
   new State(SPR_LAUN,0,-1,null,S_NULL,0,0),	// S_LAUN
   new State(SPR_PLAS,0,-1,null,S_NULL,0,0),	// S_PLAS
   new State(SPR_SHOT,0,-1,null,S_NULL,0,0),	// S_SHOT
   new State(SPR_SGN2,0,-1,null,S_NULL,0,0),	// S_SHOT2
   new State(SPR_COLU,32768,-1,null,S_NULL,0,0),	// S_COLU
   new State(SPR_SMT2,0,-1,null,S_NULL,0,0),	// S_STALAG
   new State(SPR_GOR1,0,10,null,S_BLOODYTWITCH2,0,0),	// S_BLOODYTWITCH
   new State(SPR_GOR1,1,15,null,S_BLOODYTWITCH3,0,0),	// S_BLOODYTWITCH2
   new State(SPR_GOR1,2,8,null,S_BLOODYTWITCH4,0,0),	// S_BLOODYTWITCH3
   new State(SPR_GOR1,1,6,null,S_BLOODYTWITCH,0,0),	// S_BLOODYTWITCH4
   new State(SPR_PLAY,13,-1,null,S_NULL,0,0),	// S_DEADTORSO
   new State(SPR_PLAY,18,-1,null,S_NULL,0,0),	// S_DEADBOTTOM
   new State(SPR_POL2,0,-1,null,S_NULL,0,0),	// S_HEADSONSTICK
   new State(SPR_POL5,0,-1,null,S_NULL,0,0),	// S_GIBS
   new State(SPR_POL4,0,-1,null,S_NULL,0,0),	// S_HEADONASTICK
   new State(SPR_POL3,32768,6,null,S_HEADCANDLES2,0,0),	// S_HEADCANDLES
   new State(SPR_POL3,32769,6,null,S_HEADCANDLES,0,0),	// S_HEADCANDLES2
   new State(SPR_POL1,0,-1,null,S_NULL,0,0),	// S_DEADSTICK
   new State(SPR_POL6,0,6,null,S_LIVESTICK2,0,0),	// S_LIVESTICK
   new State(SPR_POL6,1,8,null,S_LIVESTICK,0,0),	// S_LIVESTICK2
   new State(SPR_GOR2,0,-1,null,S_NULL,0,0),	// S_MEAT2
   new State(SPR_GOR3,0,-1,null,S_NULL,0,0),	// S_MEAT3
   new State(SPR_GOR4,0,-1,null,S_NULL,0,0),	// S_MEAT4
   new State(SPR_GOR5,0,-1,null,S_NULL,0,0),	// S_MEAT5
   new State(SPR_SMIT,0,-1,null,S_NULL,0,0),	// S_STALAGTITE
   new State(SPR_COL1,0,-1,null,S_NULL,0,0),	// S_TALLGRNCOL
   new State(SPR_COL2,0,-1,null,S_NULL,0,0),	// S_SHRTGRNCOL
   new State(SPR_COL3,0,-1,null,S_NULL,0,0),	// S_TALLREDCOL
   new State(SPR_COL4,0,-1,null,S_NULL,0,0),	// S_SHRTREDCOL
   new State(SPR_CAND,32768,-1,null,S_NULL,0,0),	// S_CANDLESTIK
   new State(SPR_CBRA,32768,-1,null,S_NULL,0,0),	// S_CANDELABRA
   new State(SPR_COL6,0,-1,null,S_NULL,0,0),	// S_SKULLCOL
   new State(SPR_TRE1,0,-1,null,S_NULL,0,0),	// S_TORCHTREE
   new State(SPR_TRE2,0,-1,null,S_NULL,0,0),	// S_BIGTREE
   new State(SPR_ELEC,0,-1,null,S_NULL,0,0),	// S_TECHPILLAR
   new State(SPR_CEYE,32768,6,null,S_EVILEYE2,0,0),	// S_EVILEYE
   new State(SPR_CEYE,32769,6,null,S_EVILEYE3,0,0),	// S_EVILEYE2
   new State(SPR_CEYE,32770,6,null,S_EVILEYE4,0,0),	// S_EVILEYE3
   new State(SPR_CEYE,32769,6,null,S_EVILEYE,0,0),	// S_EVILEYE4
   new State(SPR_FSKU,32768,6,null,S_FLOATSKULL2,0,0),	// S_FLOATSKULL
   new State(SPR_FSKU,32769,6,null,S_FLOATSKULL3,0,0),	// S_FLOATSKULL2
   new State(SPR_FSKU,32770,6,null,S_FLOATSKULL,0,0),	// S_FLOATSKULL3
   new State(SPR_COL5,0,14,null,S_HEARTCOL2,0,0),	// S_HEARTCOL
   new State(SPR_COL5,1,14,null,S_HEARTCOL,0,0),	// S_HEARTCOL2
   new State(SPR_TBLU,32768,4,null,S_BLUETORCH2,0,0),	// S_BLUETORCH
   new State(SPR_TBLU,32769,4,null,S_BLUETORCH3,0,0),	// S_BLUETORCH2
   new State(SPR_TBLU,32770,4,null,S_BLUETORCH4,0,0),	// S_BLUETORCH3
   new State(SPR_TBLU,32771,4,null,S_BLUETORCH,0,0),	// S_BLUETORCH4
   new State(SPR_TGRN,32768,4,null,S_GREENTORCH2,0,0),	// S_GREENTORCH
   new State(SPR_TGRN,32769,4,null,S_GREENTORCH3,0,0),	// S_GREENTORCH2
   new State(SPR_TGRN,32770,4,null,S_GREENTORCH4,0,0),	// S_GREENTORCH3
   new State(SPR_TGRN,32771,4,null,S_GREENTORCH,0,0),	// S_GREENTORCH4
   new State(SPR_TRED,32768,4,null,S_REDTORCH2,0,0),	// S_REDTORCH
   new State(SPR_TRED,32769,4,null,S_REDTORCH3,0,0),	// S_REDTORCH2
   new State(SPR_TRED,32770,4,null,S_REDTORCH4,0,0),	// S_REDTORCH3
   new State(SPR_TRED,32771,4,null,S_REDTORCH,0,0),	// S_REDTORCH4
   new State(SPR_SMBT,32768,4,null,S_BTORCHSHRT2,0,0),	// S_BTORCHSHRT
   new State(SPR_SMBT,32769,4,null,S_BTORCHSHRT3,0,0),	// S_BTORCHSHRT2
   new State(SPR_SMBT,32770,4,null,S_BTORCHSHRT4,0,0),	// S_BTORCHSHRT3
   new State(SPR_SMBT,32771,4,null,S_BTORCHSHRT,0,0),	// S_BTORCHSHRT4
   new State(SPR_SMGT,32768,4,null,S_GTORCHSHRT2,0,0),	// S_GTORCHSHRT
   new State(SPR_SMGT,32769,4,null,S_GTORCHSHRT3,0,0),	// S_GTORCHSHRT2
   new State(SPR_SMGT,32770,4,null,S_GTORCHSHRT4,0,0),	// S_GTORCHSHRT3
   new State(SPR_SMGT,32771,4,null,S_GTORCHSHRT,0,0),	// S_GTORCHSHRT4
   new State(SPR_SMRT,32768,4,null,S_RTORCHSHRT2,0,0),	// S_RTORCHSHRT
   new State(SPR_SMRT,32769,4,null,S_RTORCHSHRT3,0,0),	// S_RTORCHSHRT2
   new State(SPR_SMRT,32770,4,null,S_RTORCHSHRT4,0,0),	// S_RTORCHSHRT3
   new State(SPR_SMRT,32771,4,null,S_RTORCHSHRT,0,0),	// S_RTORCHSHRT4
   new State(SPR_HDB1,0,-1,null,S_NULL,0,0),	// S_HANGNOGUTS
   new State(SPR_HDB2,0,-1,null,S_NULL,0,0),	// S_HANGBNOBRAIN
   new State(SPR_HDB3,0,-1,null,S_NULL,0,0),	// S_HANGTLOOKDN
   new State(SPR_HDB4,0,-1,null,S_NULL,0,0),	// S_HANGTSKULL
   new State(SPR_HDB5,0,-1,null,S_NULL,0,0),	// S_HANGTLOOKUP
   new State(SPR_HDB6,0,-1,null,S_NULL,0,0),	// S_HANGTNOBRAIN
   new State(SPR_POB1,0,-1,null,S_NULL,0,0),	// S_COLONGIBS
   new State(SPR_POB2,0,-1,null,S_NULL,0,0),	// S_SMALLPOOL
   new State(SPR_BRS1,0,-1,null,S_NULL,0,0),		// S_BRAINSTEM
   new State(SPR_TLMP,32768,4,null,S_TECHLAMP2,0,0),	// S_TECHLAMP
   new State(SPR_TLMP,32769,4,null,S_TECHLAMP3,0,0),	// S_TECHLAMP2
   new State(SPR_TLMP,32770,4,null,S_TECHLAMP4,0,0),	// S_TECHLAMP3
   new State(SPR_TLMP,32771,4,null,S_TECHLAMP,0,0),	// S_TECHLAMP4
   new State(SPR_TLP2,32768,4,null,S_TECH2LAMP2,0,0),	// S_TECH2LAMP
   new State(SPR_TLP2,32769,4,null,S_TECH2LAMP3,0,0),	// S_TECH2LAMP2
   new State(SPR_TLP2,32770,4,null,S_TECH2LAMP4,0,0),	// S_TECH2LAMP3
   new State(SPR_TLP2,32771,4,null,S_TECH2LAMP,0,0)	// S_TECH2LAMP4
};


public static final MobJInfo mobjinfo[] = {

    new MobJInfo(		// MT_PLAYER
	-1,		// doomednum
	S_PLAY,		// spawnstate
	100,		// spawnhealth
	S_PLAY_RUN1,		// seestate
	sfx_None,		// seesound
	0,		// reactiontime
	sfx_None,		// attacksound
	S_PLAY_PAIN,		// painstate
	255,		// painchance
	sfx_plpain,		// painsound
	S_NULL,		// meleestate
	S_PLAY_ATK1,		// missilestate
	S_PLAY_DIE1,		// deathstate
	S_PLAY_XDIE1,		// xdeathstate
	sfx_pldeth,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	56*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue()|MF_SHOOTABLE.getValue()|MF_DROPOFF.getValue()|MF_PICKUP.getValue()|MF_NOTDMATCH.getValue(),		// flags
	S_NULL		// raisestate
),

    new MobJInfo(		// MT_POSSESSED
	3004,		// doomednum
	S_POSS_STND,		// spawnstate
	20,		// spawnhealth
	S_POSS_RUN1,		// seestate
	sfx_posit1,		// seesound
	8,		// reactiontime
	sfx_pistol,		// attacksound
	S_POSS_PAIN,		// painstate
	200,		// painchance
	sfx_popain,		// painsound
	S_NULL,		// meleestate
	S_POSS_ATK1,		// missilestate
	S_POSS_DIE1,		// deathstate
	S_POSS_XDIE1,		// xdeathstate
	sfx_podth1,		// deathsound
	8,		// speed
	20*FRACUNIT,		// radius
	56*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_posact,		// activesound
	MF_SOLID.getValue()|MF_SHOOTABLE.getValue()|MF_COUNTKILL.getValue(),		// flags
	S_POSS_RAISE1		// raisestate
    ),

    new MobJInfo(		// MT_SHOTGUY
	9,		// doomednum
	S_SPOS_STND,		// spawnstate
	30,		// spawnhealth
	S_SPOS_RUN1,		// seestate
	sfx_posit2,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_SPOS_PAIN,		// painstate
	170,		// painchance
	sfx_popain,		// painsound
	S_NULL,		// meleestate
	S_SPOS_ATK1,		// missilestate
	S_SPOS_DIE1,		// deathstate
	S_SPOS_XDIE1,		// xdeathstate
	sfx_podth2,		// deathsound
	8,		// speed
	20*FRACUNIT,		// radius
	56*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_posact,		// activesound
	MF_SOLID.getValue()|MF_SHOOTABLE.getValue()|MF_COUNTKILL.getValue(),		// flags
	S_SPOS_RAISE1		// raisestate
    ),

    new MobJInfo(		// MT_VILE
	64,		// doomednum
	S_VILE_STND,		// spawnstate
	700,		// spawnhealth
	S_VILE_RUN1,		// seestate
	sfx_vilsit,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_VILE_PAIN,		// painstate
	10,		// painchance
	sfx_vipain,		// painsound
	S_NULL,		// meleestate
	S_VILE_ATK1,		// missilestate
	S_VILE_DIE1,		// deathstate
	S_NULL,		// xdeathstate
	sfx_vildth,		// deathsound
	15,		// speed
	20*FRACUNIT,		// radius
	56*FRACUNIT,		// height
	500,		// mass
	0,		// damage
	sfx_vilact,		// activesound
	MF_SOLID.getValue()|MF_SHOOTABLE.getValue()|MF_COUNTKILL.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_FIRE
	-1,		// doomednum
	S_FIRE1,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_NOBLOCKMAP.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_UNDEAD
	66,		// doomednum
	S_SKEL_STND,		// spawnstate
	300,		// spawnhealth
	S_SKEL_RUN1,		// seestate
	sfx_skesit,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_SKEL_PAIN,		// painstate
	100,		// painchance
	sfx_popain,		// painsound
	S_SKEL_FIST1,		// meleestate
	S_SKEL_MISS1,		// missilestate
	S_SKEL_DIE1,		// deathstate
	S_NULL,		// xdeathstate
	sfx_skedth,		// deathsound
	10,		// speed
	20*FRACUNIT,		// radius
	56*FRACUNIT,		// height
	500,		// mass
	0,		// damage
	sfx_skeact,		// activesound
	MF_SOLID.getValue()|MF_SHOOTABLE.getValue()|MF_COUNTKILL.getValue(),		// flags
	S_SKEL_RAISE1		// raisestate
    ),

    new MobJInfo(		// MT_TRACER
	-1,		// doomednum
	S_TRACER,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_skeatk,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_TRACEEXP1,		// deathstate
	S_NULL,		// xdeathstate
	sfx_barexp,		// deathsound
	10*FRACUNIT,		// speed
	11*FRACUNIT,		// radius
	8*FRACUNIT,		// height
	100,		// mass
	10,		// damage
	sfx_None,		// activesound
	MF_NOBLOCKMAP.getValue()|MF_MISSILE.getValue()|MF_DROPOFF.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_SMOKE
	-1,		// doomednum
	S_SMOKE1,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_NOBLOCKMAP.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_FATSO
	67,		// doomednum
	S_FATT_STND,		// spawnstate
	600,		// spawnhealth
	S_FATT_RUN1,		// seestate
	sfx_mansit,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_FATT_PAIN,		// painstate
	80,		// painchance
	sfx_mnpain,		// painsound
	S_NULL,		// meleestate
	S_FATT_ATK1,		// missilestate
	S_FATT_DIE1,		// deathstate
	S_NULL,		// xdeathstate
	sfx_mandth,		// deathsound
	8,		// speed
	48*FRACUNIT,		// radius
	64*FRACUNIT,		// height
	1000,		// mass
	0,		// damage
	sfx_posact,		// activesound
	MF_SOLID.getValue()|MF_SHOOTABLE.getValue()|MF_COUNTKILL.getValue(),		// flags
	S_FATT_RAISE1		// raisestate
    ),

    new MobJInfo(		// MT_FATSHOT
	-1,		// doomednum
	S_FATSHOT1,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_firsht,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_FATSHOTX1,		// deathstate
	S_NULL,		// xdeathstate
	sfx_firxpl,		// deathsound
	20*FRACUNIT,		// speed
	6*FRACUNIT,		// radius
	8*FRACUNIT,		// height
	100,		// mass
	8,		// damage
	sfx_None,		// activesound
	MF_NOBLOCKMAP.getValue()|MF_MISSILE.getValue()|MF_DROPOFF.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_CHAINGUY
	65,		// doomednum
	S_CPOS_STND,		// spawnstate
	70,		// spawnhealth
	S_CPOS_RUN1,		// seestate
	sfx_posit2,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_CPOS_PAIN,		// painstate
	170,		// painchance
	sfx_popain,		// painsound
	S_NULL,		// meleestate
	S_CPOS_ATK1,		// missilestate
	S_CPOS_DIE1,		// deathstate
	S_CPOS_XDIE1,		// xdeathstate
	sfx_podth2,		// deathsound
	8,		// speed
	20*FRACUNIT,		// radius
	56*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_posact,		// activesound
	MF_SOLID.getValue()|MF_SHOOTABLE.getValue()|MF_COUNTKILL.getValue(),		// flags
	S_CPOS_RAISE1		// raisestate
    ),

    new MobJInfo(		// MT_TROOP
	3001,		// doomednum
	S_TROO_STND,		// spawnstate
	60,		// spawnhealth
	S_TROO_RUN1,		// seestate
	sfx_bgsit1,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_TROO_PAIN,		// painstate
	200,		// painchance
	sfx_popain,		// painsound
	S_TROO_ATK1,		// meleestate
	S_TROO_ATK1,		// missilestate
	S_TROO_DIE1,		// deathstate
	S_TROO_XDIE1,		// xdeathstate
	sfx_bgdth1,		// deathsound
	8,		// speed
	20*FRACUNIT,		// radius
	56*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_bgact,		// activesound
	MF_SOLID.getValue()|MF_SHOOTABLE.getValue()|MF_COUNTKILL.getValue(),		// flags
	S_TROO_RAISE1		// raisestate
    ),

    new MobJInfo(		// MT_SERGEANT
	3002,		// doomednum
	S_SARG_STND,		// spawnstate
	150,		// spawnhealth
	S_SARG_RUN1,		// seestate
	sfx_sgtsit,		// seesound
	8,		// reactiontime
	sfx_sgtatk,		// attacksound
	S_SARG_PAIN,		// painstate
	180,		// painchance
	sfx_dmpain,		// painsound
	S_SARG_ATK1,		// meleestate
	S_NULL,		// missilestate
	S_SARG_DIE1,		// deathstate
	S_NULL,		// xdeathstate
	sfx_sgtdth,		// deathsound
	10,		// speed
	30*FRACUNIT,		// radius
	56*FRACUNIT,		// height
	400,		// mass
	0,		// damage
	sfx_dmact,		// activesound
	MF_SOLID.getValue()|MF_SHOOTABLE.getValue()|MF_COUNTKILL.getValue(),		// flags
	S_SARG_RAISE1		// raisestate
    ),

    new MobJInfo(		// MT_SHADOWS
	58,		// doomednum
	S_SARG_STND,		// spawnstate
	150,		// spawnhealth
	S_SARG_RUN1,		// seestate
	sfx_sgtsit,		// seesound
	8,		// reactiontime
	sfx_sgtatk,		// attacksound
	S_SARG_PAIN,		// painstate
	180,		// painchance
	sfx_dmpain,		// painsound
	S_SARG_ATK1,		// meleestate
	S_NULL,		// missilestate
	S_SARG_DIE1,		// deathstate
	S_NULL,		// xdeathstate
	sfx_sgtdth,		// deathsound
	10,		// speed
	30*FRACUNIT,		// radius
	56*FRACUNIT,		// height
	400,		// mass
	0,		// damage
	sfx_dmact,		// activesound
	MF_SOLID.getValue()|MF_SHOOTABLE.getValue()|MF_SHADOW.getValue()|MF_COUNTKILL.getValue(),		// flags
	S_SARG_RAISE1		// raisestate
    ),

    new MobJInfo(		// MT_HEAD
	3005,		// doomednum
	S_HEAD_STND,		// spawnstate
	400,		// spawnhealth
	S_HEAD_RUN1,		// seestate
	sfx_cacsit,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_HEAD_PAIN,		// painstate
	128,		// painchance
	sfx_dmpain,		// painsound
	S_NULL,		// meleestate
	S_HEAD_ATK1,		// missilestate
	S_HEAD_DIE1,		// deathstate
	S_NULL,		// xdeathstate
	sfx_cacdth,		// deathsound
	8,		// speed
	31*FRACUNIT,		// radius
	56*FRACUNIT,		// height
	400,		// mass
	0,		// damage
	sfx_dmact,		// activesound
	MF_SOLID.getValue()|MF_SHOOTABLE.getValue()|MF_FLOAT.getValue()|MF_NOGRAVITY.getValue()|MF_COUNTKILL.getValue(),		// flags
	S_HEAD_RAISE1		// raisestate
    ),

    new MobJInfo(		// MT_BRUISER
	3003,		// doomednum
	S_BOSS_STND,		// spawnstate
	1000,		// spawnhealth
	S_BOSS_RUN1,		// seestate
	sfx_brssit,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_BOSS_PAIN,		// painstate
	50,		// painchance
	sfx_dmpain,		// painsound
	S_BOSS_ATK1,		// meleestate
	S_BOSS_ATK1,		// missilestate
	S_BOSS_DIE1,		// deathstate
	S_NULL,		// xdeathstate
	sfx_brsdth,		// deathsound
	8,		// speed
	24*FRACUNIT,		// radius
	64*FRACUNIT,		// height
	1000,		// mass
	0,		// damage
	sfx_dmact,		// activesound
	MF_SOLID.getValue()|MF_SHOOTABLE.getValue()|MF_COUNTKILL.getValue(),		// flags
	S_BOSS_RAISE1		// raisestate
    ),

    new MobJInfo(		// MT_BRUISERSHOT
	-1,		// doomednum
	S_BRBALL1,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_firsht,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_BRBALLX1,		// deathstate
	S_NULL,		// xdeathstate
	sfx_firxpl,		// deathsound
	15*FRACUNIT,		// speed
	6*FRACUNIT,		// radius
	8*FRACUNIT,		// height
	100,		// mass
	8,		// damage
	sfx_None,		// activesound
	MF_NOBLOCKMAP.getValue()|MF_MISSILE.getValue()|MF_DROPOFF.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_KNIGHT
	69,		// doomednum
	S_BOS2_STND,		// spawnstate
	500,		// spawnhealth
	S_BOS2_RUN1,		// seestate
	sfx_kntsit,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_BOS2_PAIN,		// painstate
	50,		// painchance
	sfx_dmpain,		// painsound
	S_BOS2_ATK1,		// meleestate
	S_BOS2_ATK1,		// missilestate
	S_BOS2_DIE1,		// deathstate
	S_NULL,		// xdeathstate
	sfx_kntdth,		// deathsound
	8,		// speed
	24*FRACUNIT,		// radius
	64*FRACUNIT,		// height
	1000,		// mass
	0,		// damage
	sfx_dmact,		// activesound
	MF_SOLID.getValue()|MF_SHOOTABLE.getValue()|MF_COUNTKILL.getValue(),		// flags
	S_BOS2_RAISE1		// raisestate
    ),

    new MobJInfo(		// MT_SKULL
	3006,		// doomednum
	S_SKULL_STND,		// spawnstate
	100,		// spawnhealth
	S_SKULL_RUN1,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_sklatk,		// attacksound
	S_SKULL_PAIN,		// painstate
	256,		// painchance
	sfx_dmpain,		// painsound
	S_NULL,		// meleestate
	S_SKULL_ATK1,		// missilestate
	S_SKULL_DIE1,		// deathstate
	S_NULL,		// xdeathstate
	sfx_firxpl,		// deathsound
	8,		// speed
	16*FRACUNIT,		// radius
	56*FRACUNIT,		// height
	50,		// mass
	3,		// damage
	sfx_dmact,		// activesound
	MF_SOLID.getValue()|MF_SHOOTABLE.getValue()|MF_FLOAT.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_SPIDER
	7,		// doomednum
	S_SPID_STND,		// spawnstate
	3000,		// spawnhealth
	S_SPID_RUN1,		// seestate
	sfx_spisit,		// seesound
	8,		// reactiontime
	sfx_shotgn,		// attacksound
	S_SPID_PAIN,		// painstate
	40,		// painchance
	sfx_dmpain,		// painsound
	S_NULL,		// meleestate
	S_SPID_ATK1,		// missilestate
	S_SPID_DIE1,		// deathstate
	S_NULL,		// xdeathstate
	sfx_spidth,		// deathsound
	12,		// speed
	128*FRACUNIT,		// radius
	100*FRACUNIT,		// height
	1000,		// mass
	0,		// damage
	sfx_dmact,		// activesound
	MF_SOLID.getValue()|MF_SHOOTABLE.getValue()|MF_COUNTKILL.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_BABY
	68,		// doomednum
	S_BSPI_STND,		// spawnstate
	500,		// spawnhealth
	S_BSPI_SIGHT,		// seestate
	sfx_bspsit,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_BSPI_PAIN,		// painstate
	128,		// painchance
	sfx_dmpain,		// painsound
	S_NULL,		// meleestate
	S_BSPI_ATK1,		// missilestate
	S_BSPI_DIE1,		// deathstate
	S_NULL,		// xdeathstate
	sfx_bspdth,		// deathsound
	12,		// speed
	64*FRACUNIT,		// radius
	64*FRACUNIT,		// height
	600,		// mass
	0,		// damage
	sfx_bspact,		// activesound
	MF_SOLID.getValue()|MF_SHOOTABLE.getValue()|MF_COUNTKILL.getValue(),		// flags
	S_BSPI_RAISE1		// raisestate
    ),

    new MobJInfo(		// MT_CYBORG
	16,		// doomednum
	S_CYBER_STND,		// spawnstate
	4000,		// spawnhealth
	S_CYBER_RUN1,		// seestate
	sfx_cybsit,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_CYBER_PAIN,		// painstate
	20,		// painchance
	sfx_dmpain,		// painsound
	S_NULL,		// meleestate
	S_CYBER_ATK1,		// missilestate
	S_CYBER_DIE1,		// deathstate
	S_NULL,		// xdeathstate
	sfx_cybdth,		// deathsound
	16,		// speed
	40*FRACUNIT,		// radius
	110*FRACUNIT,		// height
	1000,		// mass
	0,		// damage
	sfx_dmact,		// activesound
	MF_SOLID.getValue()|MF_SHOOTABLE.getValue()|MF_COUNTKILL.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_PAIN
	71,		// doomednum
	S_PAIN_STND,		// spawnstate
	400,		// spawnhealth
	S_PAIN_RUN1,		// seestate
	sfx_pesit,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_PAIN_PAIN,		// painstate
	128,		// painchance
	sfx_pepain,		// painsound
	S_NULL,		// meleestate
	S_PAIN_ATK1,		// missilestate
	S_PAIN_DIE1,		// deathstate
	S_NULL,		// xdeathstate
	sfx_pedth,		// deathsound
	8,		// speed
	31*FRACUNIT,		// radius
	56*FRACUNIT,		// height
	400,		// mass
	0,		// damage
	sfx_dmact,		// activesound
	MF_SOLID.getValue()|MF_SHOOTABLE.getValue()|MF_FLOAT.getValue()|MF_NOGRAVITY.getValue()|MF_COUNTKILL.getValue(),		// flags
	S_PAIN_RAISE1		// raisestate
    ),

    new MobJInfo(		// MT_WOLFSS
	84,		// doomednum
	S_SSWV_STND,		// spawnstate
	50,		// spawnhealth
	S_SSWV_RUN1,		// seestate
	sfx_sssit,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_SSWV_PAIN,		// painstate
	170,		// painchance
	sfx_popain,		// painsound
	S_NULL,		// meleestate
	S_SSWV_ATK1,		// missilestate
	S_SSWV_DIE1,		// deathstate
	S_SSWV_XDIE1,		// xdeathstate
	sfx_ssdth,		// deathsound
	8,		// speed
	20*FRACUNIT,		// radius
	56*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_posact,		// activesound
	MF_SOLID.getValue()|MF_SHOOTABLE.getValue()|MF_COUNTKILL.getValue(),		// flags
	S_SSWV_RAISE1		// raisestate
    ),

    new MobJInfo(		// MT_KEEN
	72,		// doomednum
	S_KEENSTND,		// spawnstate
	100,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_KEENPAIN,		// painstate
	256,		// painchance
	sfx_keenpn,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_COMMKEEN,		// deathstate
	S_NULL,		// xdeathstate
	sfx_keendt,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	72*FRACUNIT,		// height
	10000000,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue()|MF_SPAWNCEILING.getValue()|MF_NOGRAVITY.getValue()|MF_SHOOTABLE.getValue()|MF_COUNTKILL.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_BOSSBRAIN
	88,		// doomednum
	S_BRAIN,		// spawnstate
	250,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_BRAIN_PAIN,		// painstate
	255,		// painchance
	sfx_bospn,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_BRAIN_DIE1,		// deathstate
	S_NULL,		// xdeathstate
	sfx_bosdth,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	10000000,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue()|MF_SHOOTABLE.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_BOSSSPIT
	89,		// doomednum
	S_BRAINEYE,		// spawnstate
	1000,		// spawnhealth
	S_BRAINEYESEE,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	32*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_NOBLOCKMAP.getValue()|MF_NOSECTOR.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_BOSSTARGET
	87,		// doomednum
	S_NULL,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	32*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_NOBLOCKMAP.getValue()|MF_NOSECTOR.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_SPAWNSHOT
	-1,		// doomednum
	S_SPAWN1,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_bospit,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_firxpl,		// deathsound
	10*FRACUNIT,		// speed
	6*FRACUNIT,		// radius
	32*FRACUNIT,		// height
	100,		// mass
	3,		// damage
	sfx_None,		// activesound
	MF_NOBLOCKMAP.getValue()|MF_MISSILE.getValue()|MF_DROPOFF.getValue()|MF_NOGRAVITY.getValue()|MF_NOCLIP.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_SPAWNFIRE
	-1,		// doomednum
	S_SPAWNFIRE1,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_NOBLOCKMAP.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_BARREL
	2035,		// doomednum
	S_BAR1,		// spawnstate
	20,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_BEXP,		// deathstate
	S_NULL,		// xdeathstate
	sfx_barexp,		// deathsound
	0,		// speed
	10*FRACUNIT,		// radius
	42*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue()|MF_SHOOTABLE.getValue()|MF_NOBLOOD.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_TROOPSHOT
	-1,		// doomednum
	S_TBALL1,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_firsht,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_TBALLX1,		// deathstate
	S_NULL,		// xdeathstate
	sfx_firxpl,		// deathsound
	10*FRACUNIT,		// speed
	6*FRACUNIT,		// radius
	8*FRACUNIT,		// height
	100,		// mass
	3,		// damage
	sfx_None,		// activesound
	MF_NOBLOCKMAP.getValue()|MF_MISSILE.getValue()|MF_DROPOFF.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_HEADSHOT
	-1,		// doomednum
	S_RBALL1,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_firsht,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_RBALLX1,		// deathstate
	S_NULL,		// xdeathstate
	sfx_firxpl,		// deathsound
	10*FRACUNIT,		// speed
	6*FRACUNIT,		// radius
	8*FRACUNIT,		// height
	100,		// mass
	5,		// damage
	sfx_None,		// activesound
	MF_NOBLOCKMAP.getValue()|MF_MISSILE.getValue()|MF_DROPOFF.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_ROCKET
	-1,		// doomednum
	S_ROCKET,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_rlaunc,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_EXPLODE1,		// deathstate
	S_NULL,		// xdeathstate
	sfx_barexp,		// deathsound
	20*FRACUNIT,		// speed
	11*FRACUNIT,		// radius
	8*FRACUNIT,		// height
	100,		// mass
	20,		// damage
	sfx_None,		// activesound
	MF_NOBLOCKMAP.getValue()|MF_MISSILE.getValue()|MF_DROPOFF.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_PLASMA
	-1,		// doomednum
	S_PLASBALL,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_plasma,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_PLASEXP,		// deathstate
	S_NULL,		// xdeathstate
	sfx_firxpl,		// deathsound
	25*FRACUNIT,		// speed
	13*FRACUNIT,		// radius
	8*FRACUNIT,		// height
	100,		// mass
	5,		// damage
	sfx_None,		// activesound
	MF_NOBLOCKMAP.getValue()|MF_MISSILE.getValue()|MF_DROPOFF.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_BFG
	-1,		// doomednum
	S_BFGSHOT,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_BFGLAND,		// deathstate
	S_NULL,		// xdeathstate
	sfx_rxplod,		// deathsound
	25*FRACUNIT,		// speed
	13*FRACUNIT,		// radius
	8*FRACUNIT,		// height
	100,		// mass
	100,		// damage
	sfx_None,		// activesound
	MF_NOBLOCKMAP.getValue()|MF_MISSILE.getValue()|MF_DROPOFF.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_ARACHPLAZ
	-1,		// doomednum
	S_ARACH_PLAZ,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_plasma,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_ARACH_PLEX,		// deathstate
	S_NULL,		// xdeathstate
	sfx_firxpl,		// deathsound
	25*FRACUNIT,		// speed
	13*FRACUNIT,		// radius
	8*FRACUNIT,		// height
	100,		// mass
	5,		// damage
	sfx_None,		// activesound
	MF_NOBLOCKMAP.getValue()|MF_MISSILE.getValue()|MF_DROPOFF.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_PUFF
	-1,		// doomednum
	S_PUFF1,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_NOBLOCKMAP.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_BLOOD
	-1,		// doomednum
	S_BLOOD1,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_NOBLOCKMAP.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_TFOG
	-1,		// doomednum
	S_TFOG,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_NOBLOCKMAP.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_IFOG
	-1,		// doomednum
	S_IFOG,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_NOBLOCKMAP.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_TELEPORTMAN
	14,		// doomednum
	S_NULL,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_NOBLOCKMAP.getValue()|MF_NOSECTOR.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_EXTRABFG
	-1,		// doomednum
	S_BFGEXP,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_NOBLOCKMAP.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC0
	2018,		// doomednum
	S_ARM1,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC1
	2019,		// doomednum
	S_ARM2,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC2
	2014,		// doomednum
	S_BON1,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue()|MF_COUNTITEM.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC3
	2015,		// doomednum
	S_BON2,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue()|MF_COUNTITEM.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC4
	5,		// doomednum
	S_BKEY,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue()|MF_NOTDMATCH.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC5
	13,		// doomednum
	S_RKEY,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue()|MF_NOTDMATCH.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC6
	6,		// doomednum
	S_YKEY,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue()|MF_NOTDMATCH.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC7
	39,		// doomednum
	S_YSKULL,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue()|MF_NOTDMATCH.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC8
	38,		// doomednum
	S_RSKULL,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue()|MF_NOTDMATCH.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC9
	40,		// doomednum
	S_BSKULL,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue()|MF_NOTDMATCH.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC10
	2011,		// doomednum
	S_STIM,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC11
	2012,		// doomednum
	S_MEDI,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC12
	2013,		// doomednum
	S_SOUL,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue()|MF_COUNTITEM.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_INV
	2022,		// doomednum
	S_PINV,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue()|MF_COUNTITEM.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC13
	2023,		// doomednum
	S_PSTR,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue()|MF_COUNTITEM.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_INS
	2024,		// doomednum
	S_PINS,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue()|MF_COUNTITEM.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC14
	2025,		// doomednum
	S_SUIT,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC15
	2026,		// doomednum
	S_PMAP,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue()|MF_COUNTITEM.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC16
	2045,		// doomednum
	S_PVIS,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue()|MF_COUNTITEM.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MEGA
	83,		// doomednum
	S_MEGA,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue()|MF_COUNTITEM.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_CLIP
	2007,		// doomednum
	S_CLIP,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC17
	2048,		// doomednum
	S_AMMO,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC18
	2010,		// doomednum
	S_ROCK,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC19
	2046,		// doomednum
	S_BROK,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC20
	2047,		// doomednum
	S_CELL,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC21
	17,		// doomednum
	S_CELP,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC22
	2008,		// doomednum
	S_SHEL,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC23
	2049,		// doomednum
	S_SBOX,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC24
	8,		// doomednum
	S_BPAK,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC25
	2006,		// doomednum
	S_BFUG,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_CHAINGUN
	2002,		// doomednum
	S_MGUN,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC26
	2005,		// doomednum
	S_CSAW,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC27
	2003,		// doomednum
	S_LAUN,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC28
	2004,		// doomednum
	S_PLAS,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_SHOTGUN
	2001,		// doomednum
	S_SHOT,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_SUPERSHOTGUN
	82,		// doomednum
	S_SHOT2,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPECIAL.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC29
	85,		// doomednum
	S_TECHLAMP,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC30
	86,		// doomednum
	S_TECH2LAMP,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC31
	2028,		// doomednum
	S_COLU,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC32
	30,		// doomednum
	S_TALLGRNCOL,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC33
	31,		// doomednum
	S_SHRTGRNCOL,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC34
	32,		// doomednum
	S_TALLREDCOL,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC35
	33,		// doomednum
	S_SHRTREDCOL,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC36
	37,		// doomednum
	S_SKULLCOL,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC37
	36,		// doomednum
	S_HEARTCOL,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC38
	41,		// doomednum
	S_EVILEYE,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC39
	42,		// doomednum
	S_FLOATSKULL,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC40
	43,		// doomednum
	S_TORCHTREE,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC41
	44,		// doomednum
	S_BLUETORCH,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC42
	45,		// doomednum
	S_GREENTORCH,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC43
	46,		// doomednum
	S_REDTORCH,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC44
	55,		// doomednum
	S_BTORCHSHRT,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC45
	56,		// doomednum
	S_GTORCHSHRT,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC46
	57,		// doomednum
	S_RTORCHSHRT,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC47
	47,		// doomednum
	S_STALAGTITE,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC48
	48,		// doomednum
	S_TECHPILLAR,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC49
	34,		// doomednum
	S_CANDLESTIK,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	0,		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC50
	35,		// doomednum
	S_CANDELABRA,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC51
	49,		// doomednum
	S_BLOODYTWITCH,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	68*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue()|MF_SPAWNCEILING.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC52
	50,		// doomednum
	S_MEAT2,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	84*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue()|MF_SPAWNCEILING.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC53
	51,		// doomednum
	S_MEAT3,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	84*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue()|MF_SPAWNCEILING.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC54
	52,		// doomednum
	S_MEAT4,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	68*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue()|MF_SPAWNCEILING.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC55
	53,		// doomednum
	S_MEAT5,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	52*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue()|MF_SPAWNCEILING.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC56
	59,		// doomednum
	S_MEAT2,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	84*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPAWNCEILING.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC57
	60,		// doomednum
	S_MEAT4,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	68*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPAWNCEILING.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC58
	61,		// doomednum
	S_MEAT3,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	52*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPAWNCEILING.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC59
	62,		// doomednum
	S_MEAT5,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	52*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPAWNCEILING.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC60
	63,		// doomednum
	S_BLOODYTWITCH,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	68*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SPAWNCEILING.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC61
	22,		// doomednum
	S_HEAD_DIE6,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	0,		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC62
	15,		// doomednum
	S_PLAY_DIE7,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	0,		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC63
	18,		// doomednum
	S_POSS_DIE5,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	0,		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC64
	21,		// doomednum
	S_SARG_DIE6,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	0,		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC65
	23,		// doomednum
	S_SKULL_DIE6,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	0,		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC66
	20,		// doomednum
	S_TROO_DIE5,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	0,		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC67
	19,		// doomednum
	S_SPOS_DIE5,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	0,		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC68
	10,		// doomednum
	S_PLAY_XDIE9,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	0,		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC69
	12,		// doomednum
	S_PLAY_XDIE9,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	0,		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC70
	28,		// doomednum
	S_HEADSONSTICK,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC71
	24,		// doomednum
	S_GIBS,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	0,		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC72
	27,		// doomednum
	S_HEADONASTICK,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC73
	29,		// doomednum
	S_HEADCANDLES,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC74
	25,		// doomednum
	S_DEADSTICK,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC75
	26,		// doomednum
	S_LIVESTICK,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC76
	54,		// doomednum
	S_BIGTREE,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	32*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC77
	70,		// doomednum
	S_BBAR1,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC78
	73,		// doomednum
	S_HANGNOGUTS,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	88*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue()|MF_SPAWNCEILING.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC79
	74,		// doomednum
	S_HANGBNOBRAIN,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	88*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue()|MF_SPAWNCEILING.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC80
	75,		// doomednum
	S_HANGTLOOKDN,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	64*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue()|MF_SPAWNCEILING.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(	// MT_MISC81
	76,		// doomednum
	S_HANGTSKULL,	// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,	// seesound
	8,		// reactiontime
	sfx_None,	// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,	// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,	// deathsound
	0,		// speed
	16*FRACUNIT,	// radius
	64*FRACUNIT,	// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue()|MF_SPAWNCEILING.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC82
	77,		// doomednum
	S_HANGTLOOKUP,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	64*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue()|MF_SPAWNCEILING.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC83
	78,		// doomednum
	S_HANGTNOBRAIN,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	16*FRACUNIT,		// radius
	64*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_SOLID.getValue()|MF_SPAWNCEILING.getValue()|MF_NOGRAVITY.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC84
	79,		// doomednum
	S_COLONGIBS,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_NOBLOCKMAP.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC85
	80,		// doomednum
	S_SMALLPOOL,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_NOBLOCKMAP.getValue(),		// flags
	S_NULL		// raisestate
    ),

    new MobJInfo(		// MT_MISC86
	81,		// doomednum
	S_BRAINSTEM,		// spawnstate
	1000,		// spawnhealth
	S_NULL,		// seestate
	sfx_None,		// seesound
	8,		// reactiontime
	sfx_None,		// attacksound
	S_NULL,		// painstate
	0,		// painchance
	sfx_None,		// painsound
	S_NULL,		// meleestate
	S_NULL,		// missilestate
	S_NULL,		// deathstate
	S_NULL,		// xdeathstate
	sfx_None,		// deathsound
	0,		// speed
	20*FRACUNIT,		// radius
	16*FRACUNIT,		// height
	100,		// mass
	0,		// damage
	sfx_None,		// activesound
	MF_NOBLOCKMAP.getValue(),		// flags
	S_NULL		// raisestate
    )
    
};

    public static int indexOfState(State state) {
        for ( int i=0; i<states.length; i++ ) {
            if ( states[i].equals(state) ) {
                return i;
            }
        }
        return -1;
    }
    
}
