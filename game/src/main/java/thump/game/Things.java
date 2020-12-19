/*
 * Things
 */
package thump.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import static thump.base.Defines.SCREENWIDTH;
import static thump.base.Defines.logger;
import thump.base.FixedPoint;
import static thump.base.FixedPoint.FRACBITS;
import static thump.base.FixedPoint.FRACUNIT;
import thump.game.play.PSprite;
import thump.render.Bsp;
import thump.render.Data;
import thump.render.Draw;
import thump.render.DrawSeg;
import static thump.render.DrawSeg.SIL_BOTH;
import static thump.render.DrawSeg.SIL_BOTTOM;
import static thump.render.DrawSeg.SIL_TOP;
import thump.render.RThings;
import thump.render.Renderer;
import static thump.render.Renderer.LIGHTLEVELS;
import static thump.render.Renderer.LIGHTSEGSHIFT;
import static thump.render.Renderer.MAXLIGHTSCALE;
import thump.render.Segs;
import thump.render.Spritedef;
import thump.render.Spriteframe;
import thump.render.Vissprite;
import thump.render.colfuncs.ColFunc_DrawTranslatedColumn;
//import thump.render.colfuncs.ColFunc_DrawTranslatedColumn;
import thump.wad.Wad;
import static thump.wad.map.Degenmobj.MobileObjectFlag.MF_TRANSLATION;
import static thump.wad.map.Degenmobj.MobileObjectFlag.MF_TRANSSHIFT;
import thump.wad.mapraw.Column;
import thump.wad.mapraw.PatchData;

/**
 *
 * @author mark
 */
public class Things /*extends RThings*/ {

    public final RThings rThings = new RThings();
    
    private static final int BASEYCENTER = 100;
    
    private int newvissprite;
    public Vissprite vsprsortedhead = new Vissprite();

    private Spriteframe sprtemp[] = new Spriteframe[29];
    private int maxframe;
    private String spritename;

    private final Game game;
    private final Renderer renderer;

    public Things(Game game, Renderer renderer) {
        this.renderer = renderer;
        this.game = game;
    }
    
    

    //
    // R_InstallSpriteLump
    // Local function for R_InitSprites.
    //
    void R_InstallSpriteLump(
            int lump,
            int frame, // unsigned
            int _rotation, // unsigned
            boolean flipped
    ) {
        int r;
        int rotation = _rotation;

        if (frame >= 29 || rotation > 8) {
            //SystemInterface.I_Error(
            //        "R_InstallSpriteLump: Bad frame characters in lump {0}", new Object[]{lump});
            logger.log(Level.SEVERE, "R_InstallSpriteLump: Bad frame characters in lump {0}", new Object[]{lump});
        }

        if (frame > maxframe) {
            maxframe = frame;
        }

        if (rotation == 0) {
            // the lump should be used for all rotations
            if (sprtemp[frame].rotate == 0) {
//                SystemInterface.I_Error (
//                        "R_InitSprites: Sprite {0} frame {1} has "+
//                        "multip rot=0 lump\n", new Object[]{spritename, 'A'+frame} );
                logger.log(Level.SEVERE,
                        "R_InitSprites: Sprite {0} frame {1} has "+
                        "multip rot=0 lump", new Object[]{spritename, 'A'+frame} );
            }

            if (sprtemp[frame].rotate == 1) {
//                SystemInterface.I_Error (
//                        "R_InitSprites: Sprite {0} frame {1} has rotations "+
//                        "and a rot=0 lump\n", new Object[]{spritename, 'A'+frame});
                logger.log(Level.SEVERE,
                        "R_InitSprites: Sprite {0} frame {1} has rotations "+
                        "and a rot=0 lump", new Object[]{spritename, 'A'+frame});
            }

            sprtemp[frame].rotate = 0;
            for (r=0 ; r<8 ; r++) {
                sprtemp[frame].lump[r] = lump; // - Game.getInstance().renderer.data.firstspritelump;
                sprtemp[frame].flip[r] = (byte) (flipped?1:0);
            }
            return;
        }

        // the lump is only used for one rotation
        if (sprtemp[frame].rotate == 0) {
//            SystemInterface.I_Error (
//                    "R_InitSprites: Sprite {0} frame {1} has rotations "+
//                    "and a rot=0 lump", new Object[]{spritename, 'A'+frame});
            logger.log(Level.SEVERE,
                    "R_InitSprites: Sprite {0} frame {1} has rotations "+
                    "and a rot=0 lump", new Object[]{spritename, 'A'+frame});
        }

        sprtemp[frame].rotate = 1;

        // make 0 based
        rotation--;		
        if (sprtemp[frame].lump[rotation] != -1) {
//            SystemInterface.I_Error (
//                    "R_InitSprites: Sprite {0} : {1} : {2} " +
//                    "has two lumps mapped to it",
//                    new Object[]{spritename, 'A'+frame, '1'+rotation});
            logger.log(Level.SEVERE,
                    "R_InitSprites: Sprite {0} : {1} : {2} " +
                    "has two lumps mapped to it",
                    new Object[]{spritename, 'A'+frame, '1'+rotation});
        }

        sprtemp[frame].lump[rotation] = lump; // - Game.getInstance().renderer.data.firstspritelump;
        sprtemp[frame].flip[rotation] = (byte) (flipped?1:0);
    }

    //
    // R_InitSpriteDefs
    // Pass a null terminated list of sprite names
    //  (4 chars exactly) to be used.
    // Builds the sprite rotation matrixes to account
    //  for horizontally flipped sprites.
    // Will report an error if the lumps are inconsistant. 
    // Only called at startup.
    //
    // Sprite lump names are 4 characters for the actor,
    //  a letter for the frame, and a number for the rotation.
    // A sprite that is flippable will have an additional
    //  letter/number appended.
    // The rotation character can be 0 to signify no rotations.
    //
    void R_InitSpriteDefs(String[] namelist, Wad wad) 
    { 
        String	check[];
        int		i;
        int		l;
        String		intname;
        int		frame;
        int		rotation;
        int		start;
        int		end;
        int		patched;

        // count the number of sprite names
//        check = namelist;
//        i=0;
//        while (check[i] != null) {
//            i++;
//        }
//
//        numsprites = i;
        //int numsprites = rThings.numsprites;
        
        rThings.numsprites = namelist.length;

        if (rThings.numsprites==0) {
            return;
        }

        //sprites = Z_Malloc(numsprites *sizeof(*sprites), PU_STATIC, NULL);
        rThings.sprites = new Spritedef[rThings.numsprites];
        Data data = renderer.data;
        
        //start = data.firstspritelump-1;
        start = 0;
        end = data.lastspritelump;

        // scan all the lump names for each of the names,
        //  noting the highest frame letter.
        // Just compare 4 characters as ints
        for (i=0 ; i<rThings.numsprites ; i++) {
            rThings.sprites[i] = new Spritedef();
            
            spritename = namelist[i];
            
            //memset (sprtemp,-1, sizeof(sprtemp));
//            for ( Spriteframe f : sprtemp ) {
//                f.clear();
//            }
            for( int j=0; j< sprtemp.length; j++ ) {
                sprtemp[j] = new Spriteframe();
            }

            maxframe = -1;
            //intname = *(int *)namelist[i];
            intname = spritename.substring(0, 4);

            //List<Lump> lumpinfo = Game.getInstance().wad.lumps;
            ArrayList<PatchData> ssprites = wad.spritesLump.sprites;
            
            // scan the lumps,
            //  filling in the frames for whatever is found
            
            //for (l=start+1 ; l<end ; l++) {
            for ( l=0; l<ssprites.size(); l++) {
                //String lName = lumpinfo.get(l).name;
                String lName = ssprites.get(l).name;
                if (lName.startsWith(intname)) {
                    frame = lName.charAt(4) - 'A';
                    rotation = lName.charAt(5) - '0';

//                    if (Game.getInstance().modifiedgame) {
//                        patched = Game.getInstance().wad.W_GetNumForName (lName);
//                    } else {
                        patched = l;
//                    }

                    R_InstallSpriteLump (patched, frame, rotation, false);

                    //int len = lName.length();
                    if ( lName.length() > 7 ) {  // lName[6]
                        frame = lName.charAt(6) - 'A';  //  ABCD ==> 0123
                        rotation = lName.charAt(7) - '0';  // TODO use Integer.valueOf(String) ??
                        R_InstallSpriteLump (l, frame, rotation, true);
                    }
                }
            }

            // check the frames that were found for completeness
            if (maxframe == -1) {
                rThings.sprites[i].numframes = 0;
                continue;
            }

            maxframe++;

            for (frame = 0 ; frame < maxframe ; frame++) {
                switch (sprtemp[frame].rotate) {
                  case -1:
                    // no rotations were found for that frame at all
//                    SystemInterface.I_Error (
//                            "R_InitSprites: No patches found for {0} frame {1}\n", 
//                            new Object[]{namelist[i], frame+'A'} );
                    logger.log( Level.SEVERE,
                            "R_InitSprites: No patches found for {0} frame {1}", 
                            new Object[]{namelist[i], frame+'A'} );
                    break;

                  case 0:
                    // only the first rotation is needed
                    break;

                  case 1:
                    // must have all 8 frames
                    for (rotation=0 ; rotation<8 ; rotation++) {
                        if (sprtemp[frame].lump[rotation] == -1) {
//                            SystemInterface.I_Error (
//                                "R_InitSprites: Sprite {0} frame {1} is missing rotations\n",
//                                 new Object[]{namelist[i], frame+'A'});
                            logger.log( Level.SEVERE,
                                "R_InitSprites: Sprite {0} frame {1} is missing rotations",
                                 new Object[]{namelist[i], frame+'A'});
                        }
                    }
                    break;
                }
            }

            // allocate space for the frames present and copy sprtemp to it
            rThings.sprites[i].numframes = maxframe;
            //sprites[i].spriteframes = 
            //    Z_Malloc (maxframe * sizeof(spriteframe_t), PU_STATIC, NULL);
            rThings.sprites[i].spriteframes = new Spriteframe[maxframe];
            //memcpy (sprites[i].spriteframes, sprtemp, maxframe*sizeof(spriteframe_t));
            for ( int j=0; j<maxframe; j++ ) {
                rThings.sprites[i].spriteframes[j] = new Spriteframe(
                            sprtemp[j].rotate,
                            Arrays.copyOf(sprtemp[j].lump, sprtemp[j].lump.length),
                            Arrays.copyOf(sprtemp[j].flip, sprtemp[j].flip.length)
                        );
            }
        }

    }


    //
    // R_InitSprites
    // Called at program start.
    //
    public void R_InitSprites(String[] namelist, Wad wad) {

        for (int i=0 ; i<SCREENWIDTH ; i++) {
            rThings.negonearray[i] = -1;
            //rThings.negonearray[i] = Integer.MAX_VALUE;
        }

        R_InitSpriteDefs (namelist, wad);
    }

    //
    // R_ClearSprites
    // Called at frame start.
    //
    void R_ClearSprites() {
        rThings.vissprite_p = 0;
    }



    //
    // R_DrawVisSprite
    //  mfloorclip and mceilingclip should also be set.
    //
    void R_DrawVisSprite(Vissprite vis, int x1, int x2 ) {
        Column		column;
        int		texturecolumn;
        int		frac;
        PatchData		patch;

        logger.log(Level.CONFIG, "draw vis sprite:   x1:{0}   x2:{1}", new Object[]{x1,x2});

        Renderer rend = renderer;
        Draw draw = rend.draw;
        
        //patch = W_CacheLumpNum (vis.patch+firstspritelump, PU_CACHE);
        patch = game.wad.spritesLump.getSprite(vis.patch);

        draw.dc_colormap = vis.colormap;

        if (null!=draw.dc_colormap) {
            // NULL colormap = shadow draw
            rend.colfunc = rend.fuzzcolfunc;
        } else if ((vis.mobjflags & MF_TRANSLATION.getValue())>0) {
            rend.colfunc = new ColFunc_DrawTranslatedColumn();
//            draw.dc_translation = draw.translationtables 
//                    - 256 
//                    + ((vis.mobjflags & MF_TRANSLATION.getValue()) >> (MF_TRANSSHIFT.getValue()-8));
            int start = - 256 + ((vis.mobjflags & MF_TRANSLATION.getValue()) >> (MF_TRANSSHIFT.getValue()-8)); // ( -256 + 300 = 44 )
            draw.dc_translation = Arrays.copyOfRange(draw.translationtables, start, start+255 );
        }

        draw.dc_iscale = Math.abs(vis.xiscale)>>(rend.detailshift?1:0);
        draw.dc_texturemid = vis.texturemid;
        frac = vis.startfrac;
        rThings.spryscale = vis.scale;
        rThings.sprtopscreen = rend.centeryfrac - FixedPoint.mul(draw.dc_texturemid,rThings.spryscale);

        for (draw.dc_x=vis.x1 ; draw.dc_x<=vis.x2 ; draw.dc_x++, frac += vis.xiscale) {
            texturecolumn = frac>>FRACBITS;
//    #ifdef RANGECHECK
//            if (texturecolumn < 0 || texturecolumn >= SHORT(patch.width))
//                I_Error ("R_DrawSpriteRange: bad texturecolumn");
//    #endif
            //column = ((byte *)patch + LONG(patch.columnofs[texturecolumn]));
            column = patch.pixelData[texturecolumn];
            rThings.R_DrawMaskedColumn (rend,column);
        }

        rend.colfunc = rend.basecolfunc;
    }

    //
    // R_DrawPSprite
    //
    //void R_DrawPSprite(PSprite psp
    void R_DrawPSprite(
       int spriteNum, // psp.state.sprite.ordinal()
       long frame,    // psp.state.frame
       int sx,        // psp.sx
       int sy         // psp.sy
    ) {
        Spriteframe sprframe;
        Vissprite vis;
        //Vissprite avis;

        // decide which patch to use
//    #ifdef RANGECHECK
//        if ( (unsigned)psp.state.sprite >= numsprites)
//            I_Error ("R_ProjectSprite: invalid sprite number %i ",
//                     psp.state.sprite);
//    #endif
        //sprdef =  sprites[psp.state.sprite.ordinal()];
        Spritedef sprdef =  rThings.sprites[spriteNum];
//    #ifdef RANGECHECK
//        if ( (psp.state.frame & FF_FRAMEMASK)  >= sprdef.numframes)
//            I_Error ("R_ProjectSprite: invalid sprite frame %i : %i ",
//                     psp.state.sprite, psp.state.frame);
//    #endif
        //sprframe = sprdef.spriteframes[(int)psp.state.frame & FF_FRAMEMASK];
        sprframe = sprdef.spriteframes[(int)frame & rThings.FF_FRAMEMASK];

        int lump = sprframe.lump[0];
        boolean flip = sprframe.flip[0]>0;

        // calculate edges of the shape
        //tx = psp.sx - 160 * FRACUNIT;
        int tx = sx - 160 * FRACUNIT;

        tx -= renderer.data.spriteoffset[lump];
        int x1 = (renderer.centerxfrac + FixedPoint.mul(tx, rThings.pspritescale)) >> FRACBITS;

        // off the right side
        if (x1 > renderer.draw.viewwidth) {
            return;
        }

        tx += renderer.data.spritewidth[lump];
        int x2 = ((renderer.centerxfrac + FixedPoint.mul(tx, rThings.pspritescale)) >> FRACBITS) - 1;

        // off the left side
        if (x2 < 0) {
            return;
        }

        // store information in a vissprite
        vis =  new Vissprite();
        vis.mobjflags = 0;
        //vis.texturemid = (BASEYCENTER << FRACBITS) + FRACUNIT / 2 - (psp.sy - renderer.data.spritetopoffset[lump]);
        vis.texturemid = (BASEYCENTER << FRACBITS) + FRACUNIT / 2 - (sy - renderer.data.spritetopoffset[lump]);
        vis.x1 = x1 < 0 ? 0 : x1;
        vis.x2 = x2 >= renderer.draw.viewwidth ? renderer.draw.viewwidth - 1 : x2;
        vis.scale = rThings.pspritescale << (renderer.detailshift?1:0);

        if (flip) {
            vis.xiscale = -rThings.pspriteiscale;
            vis.startfrac = renderer.data.spritewidth[lump] - 1;
        } else {
            vis.xiscale = rThings.pspriteiscale;
            vis.startfrac = 0;
        }

        if (vis.x1 > x1) {
            vis.startfrac += vis.xiscale * (vis.x1 - x1);
        }

        vis.patch = lump;

//        if (renderer.viewplayer.powers[pw_invisibility.ordinal()] > 4 * 32
//                || (renderer.viewplayer.powers[pw_invisibility.ordinal()] & 8)>0) {
        if (renderer.viewplayer.invisibility > 4 * 32
                || (renderer.viewplayer.invisibility & 8)>0) {
            // shadow draw
            vis.colormap = null;
        } else if (renderer.fixedcolormap!=null) {
            // fixed color
            vis.colormap = renderer.fixedcolormap;
        //} else if ((psp.state.frame & FF_FULLBRIGHT)>0) {
        } else if ((frame & RThings.FF_FULLBRIGHT)>0) {
            // full bright
            vis.colormap = renderer.data.colormaps[0];
        } else {
            // local light
            vis.colormap = rThings.spritelights[MAXLIGHTSCALE - 1];
        }

        R_DrawVisSprite(vis, vis.x1, vis.x2);
    }


    //
    // R_DrawPlayerSprites
    //
    void R_DrawPlayerSprites() {
        int i;
        int lightnum;
        PSprite psp;

        // get light level
//        lightnum
//                = (renderer.viewplayer.mo.subsector.sector.lightlevel >> LIGHTSEGSHIFT)
//                + renderer.extralight;
        lightnum
                = (renderer.viewplayer.lightlevel >> LIGHTSEGSHIFT)
                + renderer.extralight;

        if (lightnum < 0) {
            rThings.spritelights = renderer.scalelight[0];
        } else if (lightnum >= LIGHTLEVELS) {
            rThings.spritelights = renderer.scalelight[LIGHTLEVELS - 1];
        } else {
            rThings.spritelights = renderer.scalelight[lightnum];
        }

        // clip to screen bounds
        rThings.mfloorclip = rThings.screenheightarray;
        rThings.mceilingclip = rThings.negonearray;

        // add all active psprites
        for (i = 0;
                //i < renderer.viewplayer.psprites.length;
                i < game.playerView.psprites.length;
                i++) {
            //psp = renderer.viewplayer.psprites[i];
            psp = game.playerView.psprites[i];
            if (psp.state != null) {
                R_DrawPSprite(
                    psp.state.sprite.ordinal(),
                    psp.state.frame,
                    psp.sx,
                    psp.sy   );
            }
        }
    }

    //
    // R_SortVisSprites
    //
    void R_SortVisSprites() {
//    int			i;
//    int			count;
//    vissprite_t*	ds;
//    vissprite_t*	best;
//    vissprite_t		unsorted;
//    fixed_t		bestscale;
        int i;
        int count;
        Vissprite ds;
        Vissprite unsorted = new Vissprite();
        Vissprite best=null; // = unsorted;
        int bestscale;

//    count = vissprite_p - vissprites;
//	
//    unsorted.next = unsorted.prev = &unsorted;
//
//    if (!count)
//	return;

        //count = vissprite_p - vissprites;
        //count = vissprites.length - vissprite_p;
        count = rThings.vissprite_p;
        if (0==count) {
            return;
        }

        unsorted.next = unsorted;
        unsorted.prev = unsorted;


        
        
//    for (ds=vissprites ; ds<vissprite_p ; ds++)
//    {
//	ds->next = ds+1;
//	ds->prev = ds-1;
//    }
        for (int dsc = 0; dsc < count; dsc++) {
            if ( dsc == count-1 ) {
                rThings.vissprites[dsc].next =  unsorted;                
            } else {
                rThings.vissprites[dsc].next = rThings.vissprites[dsc + 1];
            }

//            if ( dsc < rThings.vissprite_p ) {
//                rThings.vissprites[dsc].next = rThings.vissprites[dsc + 1];
//            }
//            if ( dsc != 0 ) { 
//                rThings.vissprites[dsc].prev = rThings.vissprites[dsc - 1];
//            }
            if ( dsc == 0 ) {
                rThings.vissprites[dsc].prev =  unsorted; 
                unsorted.next =  rThings.vissprites[dsc];
            } else {
                rThings.vissprites[dsc].prev = rThings.vissprites[dsc - 1];      
            }
        }

// Original C code
//    vissprites[0].prev = &unsorted;
//    unsorted.next = &vissprites[0];
//    (vissprite_p-1)->next = &unsorted;
//    unsorted.prev = vissprite_p-1;
            unsorted.prev = rThings.vissprites[count-1];

// Java code integrated into loop above
//        rThings.vissprites[0].prev =  unsorted;
//        unsorted.next =  rThings.vissprites[0];
//        rThings.vissprites[rThings.vissprite_p - 1].next =  unsorted;
//        unsorted.prev = rThings.vissprites[rThings.vissprite_p - 1];




        // pull the vissprites out by scale
        //best = 0;		// shut up the compiler warning
//    vsprsortedhead.next = vsprsortedhead.prev = &vsprsortedhead;
//    for (i=0 ; i<count ; i++)
//    {
//	bestscale = MAXINT;
//	for (ds=unsorted.next ; ds!= &unsorted ; ds=ds->next)
//	{
//	    if (ds->scale < bestscale)
//	    {
//		bestscale = ds->scale;
//		best = ds;
//	    }
//	}
//	best->next->prev = best->prev;
//	best->prev->next = best->next;
//	best->next = &vsprsortedhead;
//	best->prev = vsprsortedhead.prev;
//	vsprsortedhead.prev->next = best;
//	vsprsortedhead.prev = best;
//    }
        vsprsortedhead.next = vsprsortedhead;
        vsprsortedhead.prev = vsprsortedhead;
        best = unsorted;  // get rid of possible null reference
        for (i = 0; i < count; i++) {
            bestscale = Integer.MAX_VALUE;
            for (ds = unsorted.next; ds != unsorted; ds = ds.next) {
                if (ds.scale < bestscale) {
                    bestscale = ds.scale;
                    best = ds;
                }
            }
            best.next.prev = best.prev;
            best.prev.next = best.next;
            best.next =  vsprsortedhead;
            best.prev = vsprsortedhead.prev;
            vsprsortedhead.prev.next = best;
            vsprsortedhead.prev = best;
        }
    }

    //
    // R_DrawSprite
    //
    void R_DrawSprite(Vissprite spr) {
        DrawSeg ds;
        int clipbot[] = new int[SCREENWIDTH];
        int cliptop[] = new int[SCREENWIDTH];
        int x;
        int r1;
        int r2;
        int scale;
        int lowscale;
        int silhouette;

        logger.log(Level.CONFIG, "R_DrawSprite()");
        for (x = spr.x1; x <= spr.x2; x++) {
            clipbot[x] = -2;
            cliptop[x] = -2;
        }

        // Scan drawsegs from end to start for obscuring segs.
        // The first drawseg that has a greater scale
        //  is the clip seg.
        Bsp bsp = renderer.bsp;
        
        for (int i = bsp.ds_p - 1; i >= 0; i--) {
            logger.log(Level.CONFIG, "    render draw seg:{0}", i);
            ds = bsp.drawsegs[i];
            // determine if the drawseg obscures the sprite
            if (       ds.x1 > spr.x2
                    || ds.x2 < spr.x1
                    || (0==ds.silhouette && -1==ds.maskedtexturecol)
                ) {
                // does not cover sprite
                continue;
            }

            r1 = ds.x1 < spr.x1 ? spr.x1 : ds.x1;
            r2 = ds.x2 > spr.x2 ? spr.x2 : ds.x2;

            if (ds.scale1 > ds.scale2) {
                lowscale = ds.scale2;
                scale = ds.scale1;
            } else {
                lowscale = ds.scale1;
                scale = ds.scale2;
            }

            if (scale < spr.scale
                    || (lowscale < spr.scale
                    && !renderer.R_PointOnSegSide(spr.gx, spr.gy, ds.curline))) {
                // masked mid texture?
                if (-1!=ds.maskedtexturecol) {
                    Segs.getInstance().R_RenderMaskedSegRange(renderer, game.wad, ds, r1, r2);
                }
                // seg is behind sprite
                continue;
            }

            // clip this piece of the sprite
            silhouette = ds.silhouette;

            if (spr.gz >= ds.bsilheight) {
                silhouette &= ~SIL_BOTTOM;
            }

            if (spr.gzt <= ds.tsilheight) {
                silhouette &= ~SIL_TOP;
            }

            switch (silhouette) {
                case SIL_BOTTOM:
                    // bottom sil
                    logger.log(Level.CONFIG, "    bottom sil");
                    for (x = r1; x <= r2; x++) {
                        if (clipbot[x] == -2) {
                            if ( ds.sprbottomclip == null ) {
                                logger.log(Level.CONFIG, "     ^-- sprbottomclip is null!");
                            } else {
                                clipbot[x] = ds.sprbottomclip[x];
                            }
                        }
                    }
                    break;
                case SIL_TOP:
                    // top sil
                    logger.log(Level.CONFIG, "    top sil");
                    for (x = r1; x <= r2; x++) {
                        logger.log(Level.CONFIG, "  sil for-loop {0}", x);
                        if (cliptop[x] == -2) {
                            if ( ds.sprtopclip == null ) {
                                logger.log(Level.CONFIG, "     ^-- sprtopclip is null!");
                            } else {
                                cliptop[x] = ds.sprtopclip[x];
                            }
                        }
                    }
                    break;
                case SIL_BOTH:
                    // both
                    logger.log(Level.CONFIG, "    top and bottom sil");
                    for (x = r1; x <= r2; x++) {
                        if (clipbot[x] == -2) {
                            if( ds.sprbottomclip == null ) {
                                logger.log(Level.CONFIG, "        ^-- sprbottomclip is null!");
                            } else {
                                clipbot[x] = ds.sprbottomclip[x];
                            }
                        }
                        if (cliptop[x] == -2) {
                            if( ds.sprtopclip == null ) {
                                logger.log(Level.CONFIG, "        ^-- sprtopclip is null!");
                            } else {
                                cliptop[x] = ds.sprtopclip[x];
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
            logger.log(Level.CONFIG, "    sil switch done.");

        }
        logger.log(Level.CONFIG, "    render draw segs done.");

        // all clipping has been performed, so draw the sprite
        // check for unclipped columns
        for (x = spr.x1; x <= spr.x2; x++) {
            if (clipbot[x] == -2) {
                clipbot[x] = renderer.draw.viewheight;
            }

            if (cliptop[x] == -2) {
                cliptop[x] = -1;
            }
        }

        rThings.mfloorclip = clipbot;
        rThings.mceilingclip = cliptop;
        R_DrawVisSprite(spr, spr.x1, spr.x2);
    }

    //
    // R_DrawMasked
    //
    public void R_DrawMasked() {
        Vissprite spr;
        DrawSeg ds;

        logger.log(Level.CONFIG, "Things.R_DrawMasked()");
        
        R_SortVisSprites();

        //if (rThings.vissprite_p >= rThings.vissprites.length) {
        if (rThings.vissprite_p > 0) {
            // draw all vissprites back to front
            logger.log(Level.CONFIG, "    draw all visprites, back to front");
            for (spr = vsprsortedhead.next;
                    spr != vsprsortedhead;
                    spr = spr.next) {

                R_DrawSprite(spr);
            }
        }

        // render any remaining masked mid textures
        logger.log(Level.CONFIG, "    render any remaining masked mid textures");
        for (int i = renderer.bsp.ds_p - 1; i >= 0; i--) {
            ds = renderer.bsp.drawsegs[i];
            if (ds.maskedtexturecol!=-1) {
                Segs.getInstance().R_RenderMaskedSegRange(renderer, game.wad, ds, ds.x1, ds.x2);
            }
        }

        // draw the psprites on top of everything
        //  but does not draw on side views
        logger.log(Level.CONFIG, "    draw the psprites on top of everything but not on side views");
        if (0==renderer.viewangleoffset) {
            R_DrawPlayerSprites();
        }
    }

}
