/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.base;

import java.util.logging.Logger;

/**
 *
 * @author mark
 */
public class Defines {

    public static final Logger logger = Logger.getGlobal();

    //
    // For resize of screen, at start of game.
    // It will not work dynamically, see visplanes.
    //
    public final static int BASE_WIDTH = 320;

    // It is educational but futile to change this
    //  scaling e.g. to 2. Drawing of status bar,
    //  menues etc. is tied to the scale implied
    //  by the graphics.
    public final static int SCREEN_MUL = 1;
    public final static double INV_ASPECT_RATIO = 0.625; // 0.75, ideally

    // Defines suck. C sucks.
    // C++ might sucks for OOP, but it sure is a better C.
    // So there.
    public final static int SCREENWIDTH = 320;
    //SCREEN_MUL*BASE_WIDTH //320
    public final static int SCREENHEIGHT = 200;
    //(int)(SCREEN_MUL*BASE_WIDTH*INV_ASPECT_RATIO) //200

    public static enum SpriteNum {
        SPR_TROO,
        SPR_SHTG,
        SPR_PUNG,
        SPR_PISG,
        SPR_PISF,
        SPR_SHTF,
        SPR_SHT2,
        SPR_CHGG,
        SPR_CHGF,
        SPR_MISG,
        SPR_MISF,
        SPR_SAWG,
        SPR_PLSG,
        SPR_PLSF,
        SPR_BFGG,
        SPR_BFGF,
        SPR_BLUD,
        SPR_PUFF,
        SPR_BAL1,
        SPR_BAL2,
        SPR_PLSS,
        SPR_PLSE,
        SPR_MISL,
        SPR_BFS1,
        SPR_BFE1,
        SPR_BFE2,
        SPR_TFOG,
        SPR_IFOG,
        SPR_PLAY,
        SPR_POSS,
        SPR_SPOS,
        SPR_VILE,
        SPR_FIRE,
        SPR_FATB,
        SPR_FBXP,
        SPR_SKEL,
        SPR_MANF,
        SPR_FATT,
        SPR_CPOS,
        SPR_SARG,
        SPR_HEAD,
        SPR_BAL7,
        SPR_BOSS,
        SPR_BOS2,
        SPR_SKUL,
        SPR_SPID,
        SPR_BSPI,
        SPR_APLS,
        SPR_APBX,
        SPR_CYBR,
        SPR_PAIN,
        SPR_SSWV,
        SPR_KEEN,
        SPR_BBRN,
        SPR_BOSF,
        SPR_ARM1,
        SPR_ARM2,
        SPR_BAR1,
        SPR_BEXP,
        SPR_FCAN,
        SPR_BON1,
        SPR_BON2,
        SPR_BKEY,
        SPR_RKEY,
        SPR_YKEY,
        SPR_BSKU,
        SPR_RSKU,
        SPR_YSKU,
        SPR_STIM,
        SPR_MEDI,
        SPR_SOUL,
        SPR_PINV,
        SPR_PSTR,
        SPR_PINS,
        SPR_MEGA,
        SPR_SUIT,
        SPR_PMAP,
        SPR_PVIS,
        SPR_CLIP,
        SPR_AMMO,
        SPR_ROCK,
        SPR_BROK,
        SPR_CELL,
        SPR_CELP,
        SPR_SHEL,
        SPR_SBOX,
        SPR_BPAK,
        SPR_BFUG,
        SPR_MGUN,
        SPR_CSAW,
        SPR_LAUN,
        SPR_PLAS,
        SPR_SHOT,
        SPR_SGN2,
        SPR_COLU,
        SPR_SMT2,
        SPR_GOR1,
        SPR_POL2,
        SPR_POL5,
        SPR_POL4,
        SPR_POL3,
        SPR_POL1,
        SPR_POL6,
        SPR_GOR2,
        SPR_GOR3,
        SPR_GOR4,
        SPR_GOR5,
        SPR_SMIT,
        SPR_COL1,
        SPR_COL2,
        SPR_COL3,
        SPR_COL4,
        SPR_CAND,
        SPR_CBRA,
        SPR_COL6,
        SPR_TRE1,
        SPR_TRE2,
        SPR_ELEC,
        SPR_CEYE,
        SPR_FSKU,
        SPR_COL5,
        SPR_TBLU,
        SPR_TGRN,
        SPR_TRED,
        SPR_SMBT,
        SPR_SMGT,
        SPR_SMRT,
        SPR_HDB1,
        SPR_HDB2,
        SPR_HDB3,
        SPR_HDB4,
        SPR_HDB5,
        SPR_HDB6,
        SPR_POB1,
        SPR_POB2,
        SPR_BRS1,
        SPR_TLMP,
        SPR_TLP2
    }
}
