/*
 * 	Game completion, final screen animation.
 */
package thump.game;

import static thump.base.Defines.SCREENHEIGHT;
import static thump.base.Defines.SCREENWIDTH;
import static thump.game.Defines.GameMode.COMMERCIAL;
import static thump.game.Defines.GameState.GS_FINALE;
import static thump.game.Defines.MAXPLAYERS;
import static thump.game.Event.GameAction.ga_nothing;
import static thump.game.Event.GameAction.ga_worlddone;
import static thump.game.MobJInfo.Type.MT_BABY;
import static thump.game.MobJInfo.Type.MT_BRUISER;
import static thump.game.MobJInfo.Type.MT_CHAINGUY;
import static thump.game.MobJInfo.Type.MT_CYBORG;
import static thump.game.MobJInfo.Type.MT_FATSO;
import static thump.game.MobJInfo.Type.MT_HEAD;
import static thump.game.MobJInfo.Type.MT_KNIGHT;
import static thump.game.MobJInfo.Type.MT_PAIN;
import static thump.game.MobJInfo.Type.MT_PLAYER;
import static thump.game.MobJInfo.Type.MT_POSSESSED;
import static thump.game.MobJInfo.Type.MT_SERGEANT;
import static thump.game.MobJInfo.Type.MT_SHOTGUY;
import static thump.game.MobJInfo.Type.MT_SKULL;
import static thump.game.MobJInfo.Type.MT_SPIDER;
import static thump.game.MobJInfo.Type.MT_TROOP;
import static thump.game.MobJInfo.Type.MT_UNDEAD;
import static thump.game.MobJInfo.Type.MT_VILE;
import static thump.game.State.StateNum.S_PLAY_ATK1;
import static thump.game.ThingStateLUT.mobjinfo;
import static thump.game.ThingStateLUT.states;
import static thump.game.headup.Stuff.HU_FONTSIZE;
import static thump.game.headup.Stuff.HU_FONTSTART;
import thump.game.sound.sfx.Sounds;
import static thump.game.sound.sfx.Sounds.MusicEnum.mus_bunny;
import static thump.game.sound.sfx.Sounds.MusicEnum.mus_evil;
import static thump.game.sound.sfx.Sounds.MusicEnum.mus_read_m;
import static thump.game.sound.sfx.Sounds.MusicEnum.mus_victor;
import thump.game.sound.sfx.Sounds.SfxEnum;
import static thump.game.sound.sfx.Sounds.SfxEnum.sfx_claw;
import static thump.game.sound.sfx.Sounds.SfxEnum.sfx_dshtgn;
import static thump.game.sound.sfx.Sounds.SfxEnum.sfx_firsht;
import static thump.game.sound.sfx.Sounds.SfxEnum.sfx_pistol;
import static thump.game.sound.sfx.Sounds.SfxEnum.sfx_plasma;
import static thump.game.sound.sfx.Sounds.SfxEnum.sfx_rlaunc;
import static thump.game.sound.sfx.Sounds.SfxEnum.sfx_sgtatk;
import static thump.game.sound.sfx.Sounds.SfxEnum.sfx_shotgn;
import static thump.game.sound.sfx.Sounds.SfxEnum.sfx_skeatk;
import static thump.game.sound.sfx.Sounds.SfxEnum.sfx_skepch;
import static thump.game.sound.sfx.Sounds.SfxEnum.sfx_skeswg;
import static thump.game.sound.sfx.Sounds.SfxEnum.sfx_sklatk;
import static thump.game.sound.sfx.Sounds.SfxEnum.sfx_vilatk;
import static thump.render.RThings.FF_FRAMEMASK;
import thump.render.Renderer;
import thump.render.Spritedef;
import thump.render.Spriteframe;
import thump.wad.mapraw.Column;
import thump.wad.mapraw.PatchData;

/**
 *
 * @author mark
 */
public class Finale {
    
    // Stage of animation:
    //  0 = text, 1 = art screen, 2 = character cast
    int		finalestage;

    int		finalecount;

    public static final int TEXTSPEED = 3;
    public static final int TEXTWAIT = 250;

//    String	e1text = E1TEXT;
//    String	e2text = E2TEXT;
//    String	e3text = E3TEXT;
//    String	e4text = E4TEXT;
//
//    String	c1text = C1TEXT;
//    String	c2text = C2TEXT;
//    String	c3text = C3TEXT;
//    String	c4text = C4TEXT;
//    String	c5text = C5TEXT;
//    String	c6text = C6TEXT;

//    String	p1text = P1TEXT;
//    String	p2text = P2TEXT;
//    String	p3text = P3TEXT;
//    String	p4text = P4TEXT;
//    String	p5text = P5TEXT;
//    String	p6text = P6TEXT;
//
//    String	t1text = T1TEXT;
//    String	t2text = T2TEXT;
//    String	t3text = T3TEXT;
//    String	t4text = T4TEXT;
//    String	t5text = T5TEXT;
//    String	t6text = T6TEXT;

    String	finaletext;
    String	finaleflat;
    private final Game game;

    Finale(Game game) {
        this.game = game;
    }


    //
    // F_StartFinale
    //
    void F_StartFinale () {
        game.gameaction = ga_nothing;
        game.gamestate = GS_FINALE;
        game.viewactive = false;
        game.autoMap.automapactive = false;

        // Okay - IWAD dependend stuff.
        // This has been changed severly, and
        //  some stuff might have changed in the process.
        switch ( game.gameMode )
        {

          // DOOM 1 - E1, E3 or E4, but each nine missions
          case SHAREWARE:
          case REGISTERED:
          case RETAIL:
          {
            game.sound.S_ChangeMusic(mus_victor, true);

            switch (game.gameepisode)
            {
              case 1:
                finaleflat = "FLOOR4_8";
                finaletext = Game.getMessage("E1TEXT");
                break;
              case 2:
                finaleflat = "SFLR6_1";
                finaletext = Game.getMessage("E2TEXT");
                break;
              case 3:
                finaleflat = "MFLR8_4";
                finaletext = Game.getMessage("E3TEXT");
                break;
              case 4:
                finaleflat = "MFLR8_3";
                finaletext = Game.getMessage("E4TEXT");
                break;
              default:
                // Ouch.
                break;
            }
            break;
          }

          // DOOM II and missions packs with E1, M34
          case COMMERCIAL:
          {
              game.sound.S_ChangeMusic(mus_read_m, true);

              switch (game.gamemap) {
                case 6:
                  finaleflat = "SLIME16";
                  finaletext = Game.getMessage("C1TEXT");
                  break;
                case 11:
                  finaleflat = "RROCK14";
                  finaletext = Game.getMessage("C2TEXT");
                  break;
                case 20:
                  finaleflat = "RROCK07";
                  finaletext = Game.getMessage("C3TEXT");
                  break;
                case 30:
                  finaleflat = "RROCK17";
                  finaletext = Game.getMessage("C4TEXT");
                  break;
                case 15:
                  finaleflat = "RROCK13";
                  finaletext = Game.getMessage("C5TEXT");
                  break;
                case 31:
                  finaleflat = "RROCK19";
                  finaletext = Game.getMessage("C6TEXT");
                  break;
                default:
                  // Ouch.
                  break;
              }
              break;
          }	


          // Indeterminate.
          default:
            game.sound.S_ChangeMusic(mus_read_m, true);
            finaleflat = "F_SKY1"; // Not used anywhere else.
            finaletext = Game.getMessage("C1TEXT");  // FIXME - other text, music?
            break;
        }

        finalestage = 0;
        finalecount = 0;

    }



    public boolean F_Responder (Event event) {
        if (finalestage == 2) {
            return F_CastResponder (event);
        }

        return false;
    }


    //
    // F_Ticker
    //
    void F_Ticker () {
        
        int i;

        // check for skipping
        if ( (game.gameMode == COMMERCIAL) && ( finalecount > 50) ) {
          // go on to the next level
          for (i=0 ; i<MAXPLAYERS ; i++) {
              if (game.players[i].cmd.buttons>0) {
                  break;
              }
          }

          if (i < MAXPLAYERS) {	
            if (game.gamemap == 30) {
                F_StartCast ();
            } else {
                game.gameaction = ga_worlddone;
            }
          }
        }

        // advance animation
        finalecount++;

        if (finalestage == 2) {
            F_CastTicker ();
            return;
        }

        if ( game.gameMode == COMMERCIAL) {
            return;
        }

        if (0==finalestage && finalecount>(finaletext.length()*TEXTSPEED + TEXTWAIT)) {
            finalecount = 0;
            finalestage = 1;
            game.doomMain.wipegamestate = null;		// force a wipe
            if (game.gameepisode == 3) {
                game.sound.S_StartMusic (mus_bunny);
            }
        }
    }



    //
    // F_TextWrite
    //

    //#include "hu_stuff.h"
    //extern	patch_t *hu_font[HU_FONTSIZE];


    void F_TextWrite () {
        PatchData	src;
        int	dest;

        //int		x,y;
        int w;
        int		count;
        String	ch = finaletext;
        int		c;
        int		cx;
        int		cy;

        // TODO create a video.tileScreen(screenNum, patch);
        // erase the entire screen to a tiled background
        src = game.wad.getPatchByName(finaleflat);
        dest = 0;

//        for (y=0 ; y<SCREENHEIGHT ; y++)
//        {
//            for (x=0 ; x<SCREENWIDTH/64 ; x++)
//            {
//                memcpy (dest, src+((y&63)<<6), 64);
//                game.video.drawPatch(cx, cy, cy, src);
//                dest += 64;
//            }
//            if (SCREENWIDTH&63)
//            {
//                memcpy (dest, src+((y&63)<<6), SCREENWIDTH&63);
//                dest += (SCREENWIDTH&63);
//            }
//        }
        for ( int y=0; y<SCREENHEIGHT; y+=64 ) {
            for( int x=0; x<SCREENWIDTH; x+=64 ) {
                game.renderer.video.drawPatch(x, y, dest, src);
            }
        }

        //V_MarkRect (0, 0, SCREENWIDTH, SCREENHEIGHT);

        // draw some of the text onto the screen
        cx = 10;
        cy = 10;
        //ch = finaletext;

        count = (finalecount - 10)/TEXTSPEED;
        if (count < 0) {
            count = 0;
        }
        int i=0;
        for ( ; count>0 ; count-- ) {
            try {
                c = ch.charAt(i);
                i++;
                if (c == '\n') {
                    cx = 10;
                    cy += 11;
                } else {
                    c = Character.toUpperCase(c) - HU_FONTSTART;
                    if (c < 0 || c > HU_FONTSIZE) {
                        cx += 4;
                    } else {
                        w = game.headUp.hu_font[c].width;
//                        if (cx + w > SCREENWIDTH) {
//                            break;
//                        }
                        game.renderer.video.drawPatch(cx, cy, 0, game.headUp.hu_font[c]);
                        cx += w;
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
    }
 
    //
    // Final DOOM 2 animation
    // Casting by id Software.
    //   in order of appearance
    //
//    typedef struct
//    {
//        char		*name;
//        mobjtype_t	type;
//    } CastInfo;

    CastInfo	castorder[] = {
        new CastInfo( Game.getMessage("CC_ZOMBIE"), MT_POSSESSED),
        new CastInfo( Game.getMessage("CC_SHOTGUN"), MT_SHOTGUY),
        new CastInfo( Game.getMessage("CC_HEAVY"), MT_CHAINGUY),
        new CastInfo( Game.getMessage("CC_IMP"), MT_TROOP),
        new CastInfo( Game.getMessage("CC_DEMON"), MT_SERGEANT),
        new CastInfo( Game.getMessage("CC_LOST"), MT_SKULL),
        new CastInfo( Game.getMessage("CC_CACO"), MT_HEAD),
        new CastInfo( Game.getMessage("CC_HELL"), MT_KNIGHT),
        new CastInfo( Game.getMessage("CC_BARON"), MT_BRUISER),
        new CastInfo( Game.getMessage("CC_ARACH"), MT_BABY),
        new CastInfo( Game.getMessage("CC_PAIN"), MT_PAIN),
        new CastInfo( Game.getMessage("CC_REVEN"), MT_UNDEAD),
        new CastInfo( Game.getMessage("CC_MANCU"), MT_FATSO),
        new CastInfo( Game.getMessage("CC_ARCH"), MT_VILE),
        new CastInfo( Game.getMessage("CC_SPIDER"), MT_SPIDER),
        new CastInfo( Game.getMessage("CC_CYBER"), MT_CYBORG),
        new CastInfo( Game.getMessage("CC_HERO"), MT_PLAYER)
    };

    int     castnum;
    int     casttics;
    State   caststate;
    boolean castdeath;
    int     castframes;
    int     castonmelee;
    boolean castattacking;

    //
    // F_StartCast
    //


    void F_StartCast () {
        game.doomMain.wipegamestate = null;		// force a screen wipe
        castnum = 0;
        caststate = states[mobjinfo[castorder[castnum].type.ordinal()].seestate.ordinal()];
        casttics = (int) caststate.tics;
        castdeath = false;
        finalestage = 2;	
        castframes = 0;
        castonmelee = 0;
        castattacking = false;
        game.sound.S_ChangeMusic(mus_evil, true);
    }


    //
    // F_CastTicker
    //
    void F_CastTicker() {
        State.StateNum		st;
        Sounds.SfxEnum		sfx;
        boolean stopattack = false;
        
        casttics--;
        if (casttics > 0) {
            return;			// not time to change state yet
        }

        if (caststate.tics == -1 || caststate.nextstate == null) {
            // switch from deathstate to next monster
            castnum++;
            castdeath = false;
            if (castorder[castnum].name == null) {
                castnum = 0;
            }
            MobJInfo mInfo = mobjinfo[castorder[castnum].type.ordinal()];
            if (mInfo.seesound!=null) {
                game.sound.S_StartSound (null, mInfo.seesound);
            }
            caststate = states[mInfo.seestate.ordinal()];
            castframes = 0;
        } else if (caststate == states[S_PLAY_ATK1.ordinal()]) {  // just advance to next state in animation
            stopattack = true;
                //goto stopattack;	// Oh, gross hack!
        } else {
            st = caststate.nextstate;
            caststate = states[st.ordinal()];
            castframes++;

            // sound hacks....
            switch (st) {
              case S_PLAY_ATK1:	sfx = sfx_dshtgn; break;
              case S_POSS_ATK2:	sfx = sfx_pistol; break;
              case S_SPOS_ATK2:	sfx = sfx_shotgn; break;
              case S_VILE_ATK2:	sfx = sfx_vilatk; break;
              case S_SKEL_FIST2:	sfx = sfx_skeswg; break;
              case S_SKEL_FIST4:	sfx = sfx_skepch; break;
              case S_SKEL_MISS2:	sfx = sfx_skeatk; break;
              case S_FATT_ATK8:
              case S_FATT_ATK5:
              case S_FATT_ATK2:	sfx = sfx_firsht; break;
              case S_CPOS_ATK2:
              case S_CPOS_ATK3:
              case S_CPOS_ATK4:	sfx = sfx_shotgn; break;
              case S_TROO_ATK3:	sfx = sfx_claw; break;
              case S_SARG_ATK2:	sfx = sfx_sgtatk; break;
              case S_BOSS_ATK2:
              case S_BOS2_ATK2:
              case S_HEAD_ATK2:	sfx = sfx_firsht; break;
              case S_SKULL_ATK2:	sfx = sfx_sklatk; break;
              case S_SPID_ATK2:
              case S_SPID_ATK3:	sfx = sfx_shotgn; break;
              case S_BSPI_ATK2:	sfx = sfx_plasma; break;
              case S_CYBER_ATK2:
              case S_CYBER_ATK4:
              case S_CYBER_ATK6:	sfx = sfx_rlaunc; break;
              case S_PAIN_ATK3:	sfx = sfx_sklatk; break;
              default: sfx = null; break;
            }

            if (sfx!=null) {
                game.sound.S_StartSound (null, sfx);
            }
        }

        if ( !stopattack ) {
            if (castframes == 12) {
                // go into attack frame
                castattacking = true;
                if (castonmelee>0) {
                    caststate=states[mobjinfo[castorder[castnum].type.ordinal()].meleestate.ordinal()];
                } else {
                    caststate=states[mobjinfo[castorder[castnum].type.ordinal()].missilestate.ordinal()];
                }
                castonmelee ^= 1;
                if (caststate == null) {
                    if (castonmelee>0) {
                        caststate=
                                states[mobjinfo[castorder[castnum].type.ordinal()].meleestate.ordinal()];
                    } else {
                        caststate=
                                states[mobjinfo[castorder[castnum].type.ordinal()].missilestate.ordinal()];
                    }
                }
            }
        }
        
        if (    stopattack 
                || ( castattacking 
                     && (castframes == 24 || caststate  == states[mobjinfo[castorder[castnum].type.ordinal()].seestate.ordinal()]) 
            )      )
        {
              //stopattack:
              castattacking = false;
              castframes = 0;
              caststate = states[mobjinfo[castorder[castnum].type.ordinal()].seestate.ordinal()];
        }
        

        casttics = (int) caststate.tics;
        if (casttics == -1) {
            casttics = 15;
        }
    }


    //
    // F_CastResponder
    //

    boolean F_CastResponder (Event ev) {
        if (ev.type != Event.EventType.ev_keydown) {
            return false;
        }

        if (castdeath) {
            return true; // already in dying frames
        }

        // go into death frame
        castdeath = true;
        caststate = states[mobjinfo[castorder[castnum].type.ordinal()].deathstate.ordinal()];
        casttics = (int) caststate.tics;
        castframes = 0;
        castattacking = false;
        SfxEnum sfx = mobjinfo[castorder[castnum].type.ordinal()].deathsound;
        if (sfx!=null) {
            game.sound.S_StartSound (null, sfx);
        }

        return true;
    }

    void F_CastPrint(String text) {
        byte[] ch;
        int chix = 0;
        char c;
        int cx;
        int w;
        int width;

        // find width
        //ch = text.getBytes(StandardCharsets.US_ASCII);
        
        width = 0;

        while (chix<text.length()) {
            c =  text.charAt(chix);
            chix++;
            
            if (0==c) {
                break;
            }
            c = (char) (Character.toUpperCase(c) - HU_FONTSTART);
            if (c < 0 || c > HU_FONTSIZE) {
                width += 4;
                continue;
            }

            w = game.headUp.hu_font[c].width;
            width += w;
        }

        // draw it
        cx = 160 - width / 2;
        //ch = text;
        chix = 0;
        while (chix<text.length()) {
            c =  text.charAt(chix);
            chix++;

            if (0==c) {
                break;
            }
            c = (char) (Character.toUpperCase(c) - HU_FONTSTART);
            if (c < 0 || c > HU_FONTSIZE) {
                cx += 4;
                continue;
            }

            w = game.headUp.hu_font[c].width;
            game.renderer.video.drawPatch(cx, 180, 0, game.headUp.hu_font[c]);
            cx += w;
        }

    }


    //
    // F_CastDrawer
    //

    void F_CastDrawer ()
    {
        Spritedef	sprdef;
        Spriteframe	sprframe;
        int			lump;
        boolean		flip;
        PatchData		patch;

        // erase the entire screen to a background
        game.renderer.video.drawPatch (0,0,0, game.wad.getPatchByName("BOSSBACK"));

        F_CastPrint (castorder[castnum].name);

        // draw the current frame in the middle of the screen
        sprdef = game.things.rThings.sprites[caststate.sprite.ordinal()];
        sprframe = sprdef.spriteframes[ (int)caststate.frame & FF_FRAMEMASK];
        lump = sprframe.lump[0];
        flip = sprframe.flip[0]>0;

        //patch = W_CacheLumpNum (lump+firstspritelump, PU_CACHE);
        patch = game.wad.spritesLump.sprites.get(lump);
        if (flip) {
            game.renderer.video.drawPatchFlipped (160,170,0,patch);
        } else {
            game.renderer.video.drawPatch (160,170,0,patch);
        }
    }


    //
    // F_DrawPatchCol
    //
    void F_DrawPatchCol( int x, PatchData patch, int col ) {
        Column	column = patch.pixelData[col];
        
        //column.draw(game.renderer.video.screens[0], x,0);
        Renderer.drawColumn(column, game.renderer.video.screens[0], x, 0);
        
//        byte*	source;
//        byte*	dest;
//        byte*	desttop;
//        int		count;
//
//        //column = (Column)((byte *)patch + LONG(patch.columnofs[col]));
//        //column = patch.pixelData[col];
//        
//        // column.draw( screen, x );
//        desttop = screens[0]+x;
//
//        // step through the posts in a column
//        while (column.topdelta != 0xff )
//        {
//            source = (byte *)column + 3;
//            dest = desttop + column.topdelta*SCREENWIDTH;
//            count = column.length;
//
//            while (count--)
//            {
//                *dest = *source++;
//                dest += SCREENWIDTH;
//            }
//            column = (Column)(  (byte *)column + column.length + 4 );
//        }
    }


    private int	laststage=0;
    //
    // F_BunnyScroll
    //
    void F_BunnyScroll ()
    {
        int		scrolled;
        int		x;
        PatchData	p1;
        PatchData	p2;
        String          name;
        int		stage;

        //p1 = game.wad.getPatchByName ("PFUB2", PU_LEVEL);
        p1 = game.wad.getPatchByName("PFUB2");
        //p2 = game.wad.getPatchByName ("PFUB1", PU_LEVEL);
        p2 = game.wad.getPatchByName("PFUB1");

        //game.video.V_MarkRect (0, 0, SCREENWIDTH, SCREENHEIGHT);

        scrolled = 320 - (finalecount-230)/2;
        if (scrolled > 320) {
            scrolled = 320;
        }
        if (scrolled < 0) {
            scrolled = 0;
        }

        for ( x=0 ; x<SCREENWIDTH ; x++)
        {
            if (x+scrolled < 320) {
                F_DrawPatchCol (x, p1, x+scrolled);
            } else {
                F_DrawPatchCol (x, p2, x+scrolled - 320);
            }		
        }

        if (finalecount < 1130) {
            return;
        }
        if (finalecount < 1180)
        {
            game.renderer.video.drawPatch ((SCREENWIDTH-13*8)/2,
                         (SCREENHEIGHT-8*8)/2,0, game.wad.getPatchByName ("END0"));
            laststage = 0;
            return;
        }

        stage = (finalecount-1180) / 5;
        if (stage > 6) {
            stage = 6;
        }
        if (stage > laststage) {
            game.sound.S_StartSound (null, sfx_pistol);
            laststage = stage;
        }

        //sprintf (name,"END%i",stage);
        name = "END" + stage;
        game.renderer.video.drawPatch ((SCREENWIDTH-13*8)/2, (SCREENHEIGHT-8*8)/2,0, game.wad.getPatchByName(name));
    }


    //
    // F_Drawer
    //
    void F_Drawer() {
        if (finalestage == 2) {
            F_CastDrawer();
            return;
        }

        if (0 == finalestage) {
            F_TextWrite();
        } else {
            switch (game.gameepisode) {
                case 1:
                    if (game.gameMode == Defines.GameMode.RETAIL) {
                        game.renderer.video.drawPatch(0, 0, 0,
                                game.wad.getPatchByName("CREDIT"));
                    } else {
                        game.renderer.video.drawPatch(0, 0, 0,
                                game.wad.getPatchByName("HELP2"));
                    }
                    break;
                case 2:
                    game.renderer.video.drawPatch(0, 0, 0,
                            game.wad.getPatchByName("VICTORY2"));
                    break;
                case 3:
                    F_BunnyScroll();
                    break;
                case 4:
                    game.renderer.video.drawPatch(0, 0, 0,
                            game.wad.getPatchByName("ENDPIC"));
                    break;
            }
        }

    }


}
