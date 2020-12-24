/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.render;

import java.util.logging.Level;
import static thump.base.Defines.SCREENWIDTH;
import static thump.base.Defines.logger;
import thump.base.FixedPoint;
import static thump.base.FixedPoint.FRACBITS;
import static thump.base.FixedPoint.FRACUNIT;
import static thump.base.Tables.ANG45;
import static thump.render.Renderer.LIGHTLEVELS;
import static thump.render.Renderer.LIGHTSCALESHIFT;
import static thump.render.Renderer.LIGHTSEGSHIFT;
import static thump.render.Renderer.MAXLIGHTSCALE;
import thump.wad.map.Degenmobj;
import static thump.wad.map.Degenmobj.MobileObjectFlag.MF_SHADOW;
import thump.wad.map.Sector;
import thump.wad.mapraw.Column;

/**
 *
 * @author mark
 */
public class RThings {

    protected static final int MINZ = (FRACUNIT * 4);
    public static final int MAXVISSPRITES = 128;

    public Vissprite vissprites[] = new Vissprite[MAXVISSPRITES];
    public int vissprite_p; // Index of vissprite
    //
    // R_NewVisSprite
    //
    private Vissprite overflowsprite;

    public Spritedef sprites[];
    public int numsprites;  // use sprites.length

    // frontsector
    // pspritescale
    // screenheightarray[]
    // spryscale
    // mfloorclip
    // mceilingclip
    // sprtopscreen
    // negonarray
    public int spryscale;
    public int sprtopscreen;
    public int pspritescale;
    public int pspriteiscale;

    // Constant arrays used for psprite clipping
    //  and initializing clipping.
    public int negonearray[] = new int[SCREENWIDTH];
    public int screenheightarray[] = new int[SCREENWIDTH];

    // vars for R_DrawMaskedColumn
    public int[] mfloorclip;
    public int[] mceilingclip;

    public byte[][] spritelights;

    // Frame flags:
    // handles maximum brightness (torches, muzzle flare, light sources)
    public static final int FF_FULLBRIGHT = 0x8000;	// flag in thing.frame
    public static final int FF_FRAMEMASK  = 0x7fff;

    // R_AddSprites()
    // R_DrawMaskedColumn    TODO:  Move to Draw.
    // Used for sprites and masked mid textures.
    // Masked means: partly transparent, i.e. stored
    //  in posts/runs of opaque pixels.
    //
    public void R_DrawMaskedColumn(Renderer renderer, Column column) {
        int topscreen;
        int bottomscreen;
        int basetexturemid;

        //logger.log(Level.CONFIG, "R_DrawMaskedColumn");
        Draw draw = renderer.draw;

        basetexturemid = draw.dc_texturemid;

        //for ( ; column.topdelta != 0xff ; )  {
//        for ( Post p: column.posts ) {
//            // calculate unclipped screen coordinates
//            //  for post
//            topscreen = sprtopscreen + spryscale*p.rowStart;
//            bottomscreen = topscreen + spryscale*p.pixels.length;
//
//            draw.dc_yl = (topscreen+FRACUNIT-1)>>FRACBITS;
//            draw.dc_yh = (bottomscreen-1)>>FRACBITS;
//
//            if (draw.dc_yh >= mfloorclip[draw.dc_x]) {
//                draw.dc_yh = mfloorclip[draw.dc_x] - 1;
//            }
//            if (draw.dc_yl <= mceilingclip[draw.dc_x]) {
//                draw.dc_yl = mceilingclip[draw.dc_x] + 1;
//            }
//
//            if (draw.dc_yl <= draw.dc_yh) {
//                //draw.dc_source = (byte *)column + 3;
//                draw.dc_source = p;
//                draw.dc_texturemid = basetexturemid - (p.rowStart << FRACBITS);
//                // draw.dc_source = (byte *)column + 3 - column.topdelta;
//
//                // Drawn by either R_DrawColumn
//                //  or (SHADOW) R_DrawFuzzColumn.
//                Game.getInstance().renderer.colfunc.doColFunc(game);
//            }
//            //column = (  (byte *)column + column.length + 4);
//        }
        int[] colVals = column.getRawVals();
        topscreen = sprtopscreen; // + spryscale*column.posts.get(0).rowStart;
        bottomscreen = topscreen + spryscale * column.height;

        draw.dc_yl = (topscreen + FRACUNIT - 1) >> FRACBITS;
        draw.dc_yh = (bottomscreen - 1) >> FRACBITS;

        if ( null == mfloorclip || null == mceilingclip ) {
            int ii=0;  // debug breakpoint
        }
        if (null != mfloorclip && draw.dc_yh >= mfloorclip[draw.dc_x]) {
            draw.dc_yh = mfloorclip[draw.dc_x] - 1;
        }
        if (null != mceilingclip && draw.dc_yl <= mceilingclip[draw.dc_x]) {
            draw.dc_yl = mceilingclip[draw.dc_x] + 1;
        }

//        if (draw.dc_yl <= draw.dc_yh) {
        //draw.dc_source = (byte *)column + 3;
        draw.dc_source = column;
        draw.dc_texturemid = basetexturemid; // - (p.rowStart << FRACBITS);
        // draw.dc_source = (byte *)column + 3 - column.topdelta;

        // Drawn by either R_DrawColumn
        //  or (SHADOW) R_DrawFuzzColumn.
        renderer.colfunc.doColFunc(renderer.draw);
//        }

        draw.dc_texturemid = basetexturemid;
    }

    //
    // R_AddSprites
    // During BSP traversal, this adds sprites by sector.
    //
    void R_AddSprites(Sector sec, Renderer renderer) {
        //MapObject thing;
        //int lightnum;

        logger.log(Level.FINE, "RThings.R_AddSprites for sector:\n    {0}", sec.toString());
        // BSP is traversed by subsector.
        // A sector might have been split into several
        //  subsectors during BSP building.
        // Thus we check whether its already added.
        if (sec.validcount == renderer.validcount) {
            return;
        }

        // Well, now it will be done.
        sec.validcount = renderer.validcount;

        int lightnum = (sec.lightlevel >> LIGHTSEGSHIFT) + renderer.extralight;

        if (lightnum < 0) {
            spritelights = renderer.scalelight[0];
        } else if (lightnum >= LIGHTLEVELS) {
            spritelights = renderer.scalelight[LIGHTLEVELS - 1];
        } else {
            spritelights = renderer.scalelight[lightnum];
        }

        //MapObject thing = sec.thinglist;
        Degenmobj thing = sec.thinglist;
        if ( thing != null ) {
            logger.log(Level.CONFIG, "    project sprites:\n{0}", thing.toString());
        }
        
        // Handle all things in sector.
        //for (thing = sec.thinglist; thing; thing = thing.snext) {
        while (thing != null) {
            R_ProjectSprite(thing, renderer);
            thing = thing.snext;
        }
    }

    //
    // R_ProjectSprite
    // Generates a vissprite for a thing
    //  if it might be visible.
    //
    void R_ProjectSprite(Degenmobj thing, Renderer renderer) {
        int		tr_x;
        int		tr_y;

        int		gxt;
        int		gyt;

        int		tx;
        int		tz;

        int		xscale;

        int			x1;
        int			x2;

        Spritedef	sprdef;
        Spriteframe	sprframe;
        int			lump;

        long		rot;
        boolean		flip;

        int			index;

        Vissprite	vis;

        long		ang;
        int		iscale;
        
        // transform the origin point
        tr_x = thing.x - renderer.viewx;
        tr_y = thing.y - renderer.viewy;

        gxt = FixedPoint.mul(tr_x,renderer.viewcos); 
        gyt = -FixedPoint.mul(tr_y,renderer.viewsin);

        tz = gxt-gyt; 

        // thing is behind view plane?
        if (tz < MINZ) {
            return;
        }

        xscale = FixedPoint.div(renderer.projection, tz);

        gxt = -FixedPoint.mul(tr_x,renderer.viewsin); 
        gyt = FixedPoint.mul(tr_y,renderer.viewcos); 
        tx = -(gyt+gxt); 

        // too far off the side?
        if (Math.abs(tx)>(tz<<2)) {
            return;
        }

        // decide which patch to use for sprite relative to player
//    #ifdef RANGECHECK
//        if ((unsigned)thing.sprite >= numsprites)
//            I_Error ("R_ProjectSprite: invalid sprite number %i ",
//                     thing.sprite);
//    #endif

    if ( thing.sprite == null ) {
        int i=0;  // Debug breakpoint
    }
        sprdef = sprites[thing.sprite.ordinal()];
//    #ifdef RANGECHECK
//        if ( (thing.frame&FF_FRAMEMASK) >= sprdef.numframes )
//            I_Error ("R_ProjectSprite: invalid sprite frame %i : %i ",
//                     thing.sprite, thing.frame);
//    #endif
        sprframe = sprdef.spriteframes[ (int)thing.frame & FF_FRAMEMASK];

        if (sprframe.rotate>0) {
            // choose a different rotation based on player view
            ang = renderer.R_PointToAngle (thing.x, thing.y)&0xFFFFFFFFL;
            rot = ((ang-thing.angle+(ANG45/2)*9)&0xFFFFFFFFL)>>29;
            logger.log(Level.CONFIG, 
                    "Rot:  ang:{0}  -  thing.angle:{1}  =  {2}\n" +
                    "      ANG45/2:{3}  ==>  * 9 : {4}   ((ang-thing.angle+(ANG45/2)*9)>>29): {5}", 
                    new Object[]{
                        Long.toHexString(ang), Long.toHexString(thing.angle), Long.toHexString((ang-thing.angle)&0xFFFFFFFFL),
                        Long.toHexString(ANG45/2), Long.toHexString( ang-thing.angle+(ANG45/2)*9 ),
                        rot
                    }
            );
//            rot = (int)(ang-(thing.angle&0xffffffff));
//            long xxx = ANG45/2;
//            rot += xxx*9;
//            rot >>= 29;
            logger.log(Level.CONFIG, 
                    "    ang: 0x{0}   rot: 0x{1}", 
                    new Object[]{Long.toHexString(ang), rot}
            );
            lump = sprframe.lump[(int)rot];
            flip = sprframe.flip[(int)rot]>0;
        } else {
            // use single rotation for all views
            lump = sprframe.lump[0];
            flip = sprframe.flip[0]>0;
        }

        // calculate edges of the shape
        tx -= renderer.data.spriteoffset[lump];	
        x1 = (renderer.centerxfrac + FixedPoint.mul (tx,xscale) ) >>FRACBITS;

        // off the right side?
        if (x1 > renderer.draw.viewwidth) {
            return;
        }

        tx +=  renderer.data.spritewidth[lump];
        x2 = ((renderer.centerxfrac + FixedPoint.mul (tx,xscale) ) >>FRACBITS) - 1;

        // off the left side
        if (x2 < 0) {
            return;
        }

        // store information in a vissprite
        vis = R_NewVisSprite ();
        vis.mobjflags = thing.flags;
        vis.scale = xscale<<(renderer.detailshift?1:0);
        vis.gx = thing.x;
        vis.gy = thing.y;
        vis.gz = thing.z;
        vis.gzt = thing.z + renderer.data.spritetopoffset[lump];
        vis.texturemid = vis.gzt - renderer.viewz;
        vis.x1 = x1 < 0 ? 0 : x1;
        vis.x2 = x2 >= renderer.draw.viewwidth ? renderer.draw.viewwidth-1 : x2;	
        iscale = FixedPoint.div(FRACUNIT, xscale);

        if (flip) {
            vis.startfrac = renderer.data.spritewidth[lump]-1;
            vis.xiscale = -iscale;
        }
        else {
            vis.startfrac = 0;
            vis.xiscale = iscale;
        }

        if (vis.x1 > x1) {
            vis.startfrac += vis.xiscale*(vis.x1-x1);
        }
        vis.patch = lump;

        // get light level
        if ((thing.flags & MF_SHADOW.getValue())>0) {
            // shadow draw
            vis.colormap = null;
        }
        else if (renderer.fixedcolormap!=null) {
            // fixed map
            vis.colormap = renderer.fixedcolormap;
        }
        else if ((thing.frame & FF_FULLBRIGHT)>0) {
            // full bright
            vis.colormap = renderer.data.colormaps[0];
        } 
        else {
            // diminished light
            index = xscale>>(LIGHTSCALESHIFT-(renderer.detailshift?1:0));

            if (index >= MAXLIGHTSCALE) {
                index = MAXLIGHTSCALE-1;
            }

            vis.colormap = spritelights[index];
        }
        
        logger.log(Level.CONFIG, "    new visSprite created:\n{0}", vis.toString());
    }

    //
    // R_NewVisSprite
    //
    Vissprite R_NewVisSprite() {
        if (vissprite_p == MAXVISSPRITES) {
            return overflowsprite;
        }

        vissprites[vissprite_p] = new Vissprite();
        vissprite_p++;
        return vissprites[vissprite_p-1];
    }
}
