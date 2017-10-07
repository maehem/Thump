/*

    All the clipping: columns, horizontal spans, sky columns.

 */
package thump.render;

import java.util.Arrays;
import thump.game.Game;
import thump.global.FixedPoint;
import static thump.global.FixedPoint.FRACBITS;
import static thump.global.Tables.ANG180;
import static thump.global.Tables.ANG90;
import static thump.global.Tables.ANGLETOFINESHIFT;
import static thump.global.Tables.finesine;
import static thump.global.Tables.finetangent;
import static thump.render.Defines.MAXDRAWSEGS;
import static thump.render.Defines.SIL_BOTH;
import static thump.render.Defines.SIL_BOTTOM;
import static thump.render.Defines.SIL_TOP;
import static thump.render.Line.ML_DONTPEGBOTTOM;
import static thump.render.Line.ML_DONTPEGTOP;
import static thump.render.Line.ML_MAPPED;
import static thump.render.Renderer.LIGHTLEVELS;
import static thump.render.Renderer.LIGHTSCALESHIFT;
import static thump.render.Renderer.LIGHTSEGSHIFT;
import static thump.render.Renderer.MAXLIGHTSCALE;

/**
 *
 * @author mark
 */
public class Segs {
    
    private static Segs instance = null;
    
    private Segs() {}
    
    public static Segs getInstance() {
        if ( instance == null ) {
            instance = new Segs();
        }
        
        return instance;
    }
    
    private static final int HEIGHTBITS=12;
    private static final int HEIGHTUNIT=(1<<HEIGHTBITS);

    // OPTIMIZE: closed two sided lines as single sided

    public boolean  segtextured;    // True if any of the segs textures might be visible.

    public boolean  markfloor;      // False if the back side is the same plane.
    public boolean  markceiling;

    public boolean  maskedtexture;
    public int      toptexture;
    public int      bottomtexture;
    public int      midtexture;

    public int      rw_normalangle; //unsigned!
    public int      rw_angle1;    // angle to line origin

    //
    // regular wall
    //
    public int rw_x;
    public int rw_stopx;
    public int rw_centerangle; // unsigned!
    public int rw_offset;
    public int rw_distance;
    public int rw_scale;
    public int rw_scalestep;
    public int rw_midtexturemid;
    public int rw_toptexturemid;
    public int rw_bottomtexturemid;

    public int worldtop;
    public int worldbottom;
    public int worldhigh;
    public int worldlow;

    public int pixhigh;
    public int pixlow;
    public int pixhighstep;
    public int pixlowstep;

    public int topfrac;
    public int topstep;

    public int bottomfrac;
    public int bottomstep;

    public byte[][]  walllights;

    public int maskedtexturecol;

//
// R_RenderMaskedSegRange
//
public void R_RenderMaskedSegRange( 
        DrawSeg	ds,
        int		x1,
        int		x2 ){
    int	index;
    Column	col;
    int		lightnum;
    int		texnum;
    
    // Calculate light table.
    // Use different light tables
    //   for horizontal / vertical / diagonal. Diagonal?
    // OPTIMIZE: get rid of LIGHTSEGSHIFT globally
    //Bsp bsp = Bsp.getInstance();
    Bsp bsp = Game.getInstance().renderer.bsp;
    Seg curline = bsp.curline;
    Sector frontsector = bsp.frontsector;
    Sector backsector = bsp.backsector;
    
    Renderer renderer = Game.getInstance().renderer;
    
    curline = ds.curline;
    frontsector = curline.frontsector;
    backsector = curline.backsector;
    byte[][][] scalelight = renderer.scalelight;
    
    texnum = renderer.data.texturetranslation[curline.sidedef.getMidTexture()];
	
    lightnum = (bsp.frontsector.lightlevel >> LIGHTSEGSHIFT)+renderer.extralight;

    if (curline.v1.y == curline.v2.y) {
        lightnum--;
    } else if (curline.v1.x == curline.v2.x) {
        lightnum++;
    }

    if (lightnum < 0) {
        walllights = scalelight[0];
    } else if (lightnum >= LIGHTLEVELS) {
        walllights = scalelight[LIGHTLEVELS-1];
    } else {
        walllights = scalelight[lightnum];
    }

    maskedtexturecol = ds.maskedtexturecol;
    
    Things things = Game.getInstance().things;
    Plane plane = Game.getInstance().renderer.plane;
    
    rw_scalestep = ds.scalestep;		
    things.spryscale = ds.scale1 + (x1 - ds.x1)*rw_scalestep;
    things.mfloorclip = ds.sprbottomclip;
    things.mceilingclip = ds.sprtopclip;
    
    Draw draw = renderer.draw;
    // find positioning
    if ((curline.linedef.flags & ML_DONTPEGBOTTOM)>0) {
	draw.dc_texturemid = frontsector.floorheight > backsector.floorheight
	    ? frontsector.floorheight : backsector.floorheight;
	draw.dc_texturemid = draw.dc_texturemid 
                + Game.getInstance().wad.getTextures().get(texnum).height 
                - renderer.viewz;
    } else {
	draw.dc_texturemid =frontsector.ceilingheight<backsector.ceilingheight
	    ? frontsector.ceilingheight : backsector.ceilingheight;
	draw.dc_texturemid -= renderer.viewz;
    }
    draw.dc_texturemid += curline.sidedef.rowoffset;
			
    if (renderer.fixedcolormap!=null) {
        draw.dc_colormap = renderer.fixedcolormap;
    }
    
    // draw the columns
    for (draw.dc_x = x1 ; draw.dc_x <= x2 ; draw.dc_x++) {
	// calculate lighting
	if (plane.openings[maskedtexturecol+draw.dc_x] != Short.MAX_VALUE) {
	    if (renderer.fixedcolormap==null) {
		index = things.spryscale>>LIGHTSCALESHIFT;

		if (index >=  MAXLIGHTSCALE ) {
                    index = MAXLIGHTSCALE-1;
                }

		//draw.dc_colormap = walllights[index];
		draw.dc_colormap = Arrays.copyOfRange(walllights[index], 0, 256);
	    }
			
	    things.sprtopscreen = renderer.centeryfrac - FixedPoint.mul(draw.dc_texturemid, things.spryscale);
	    draw.dc_iscale = 0xffffffff / things.spryscale;
	    
	    // draw the texture
	    //col = renderer.data.R_GetColumn(texnum,maskedtexturecol[draw.dc_x]);
            col = Game.getInstance().wad.getTextures().get(texnum).getColumn(plane.openings[maskedtexturecol+draw.dc_x]);
            //-3);  // All that minus three?  WTF?
			
	    things.R_DrawMaskedColumn (col);
	    plane.openings[maskedtexturecol+draw.dc_x] = Short.MAX_VALUE;
	}
	things.spryscale += rw_scalestep;
    }
	
}




//
// R_RenderSegLoop
// Draws zero, one, or two textures (and possibly a masked
//  texture) for walls.
// Can draw or mark the starting pixel of floor and ceiling
//  textures.
// CALLED: CORE LOOPING ROUTINE.
//
void R_RenderSegLoop () {
    int angle;
    int index;
    int yl;
    int yh;
    int mid;
    int texturecolumn=0;
    int top;
    int bottom;

    //texturecolumn = 0;				// shut up compiler warning
    
    Renderer renderer = Game.getInstance().renderer;
    Draw draw = Game.getInstance().renderer.draw;
    
    for ( ; rw_x < rw_stopx ; rw_x++) {
	// mark floor / ceiling areas
	yl = (topfrac+HEIGHTUNIT-1)>>HEIGHTBITS;

	// no space above wall?
	if (yl < renderer.plane.ceilingclip[rw_x]+1) {
            yl = renderer.plane.ceilingclip[rw_x]+1;
        }
	
	if (markceiling) {
	    top = renderer.plane.ceilingclip[rw_x]+1;
	    bottom = yl-1;

	    if (bottom >= renderer.plane.floorclip[rw_x]) {
                bottom = renderer.plane.floorclip[rw_x]-1;
            }

	    if (top <= bottom) {
		renderer.plane.ceilingplane.top[rw_x] = (byte) top;
		renderer.plane.ceilingplane.bottom[rw_x] = (byte) bottom;
	    }
	}
		
	yh = bottomfrac>>HEIGHTBITS;

	if (yh >= renderer.plane.floorclip[rw_x]) {
            yh = renderer.plane.floorclip[rw_x]-1;
        }

	if (markfloor) {
	    top = yh+1;
	    bottom = renderer.plane.floorclip[rw_x]-1;
	    if (top <= renderer.plane.ceilingclip[rw_x]) {
                top = renderer.plane.ceilingclip[rw_x]+1;
            }
	    if (top <= bottom) {
		renderer.plane.floorplane.top[rw_x] = (byte) top;
		renderer.plane.floorplane.bottom[rw_x] = (byte) bottom;
	    }
	}
	
	// texturecolumn and lighting are independent of wall tiers
	if (segtextured) {
	    // calculate texture offset
	    angle = (rw_centerangle + renderer.xtoviewangle[rw_x])>>ANGLETOFINESHIFT;
	    texturecolumn = rw_offset-FixedPoint.mul(finetangent(angle),rw_distance);
	    texturecolumn >>= FRACBITS;
	    // calculate lighting
	    index = rw_scale>>LIGHTSCALESHIFT;

	    if (index >=  MAXLIGHTSCALE ) {
                index = MAXLIGHTSCALE-1;
            }

	    draw.dc_colormap = Arrays.copyOfRange(walllights[index], 0, 256);
	    draw.dc_x = rw_x;
	    draw.dc_iscale = 0xffffffff / rw_scale;
	}
	
	// draw the wall tiers
	if (midtexture>0) {
	    // single sided line
	    draw.dc_yl = yl;
	    draw.dc_yh = yh;
	    draw.dc_texturemid = rw_midtexturemid;
	    //draw.dc_source = renderer.data.R_GetColumn(midtexture,texturecolumn);
            draw.dc_source = Game.getInstance().wad.getTextures().get(midtexture).getColumn(texturecolumn);
	    Game.getInstance().renderer.colfunc.doColFunc(Game.getInstance());
	    renderer.plane.ceilingclip[rw_x] = draw.viewheight;
	    renderer.plane.floorclip[rw_x] = -1;
	} else {
	    // two sided line
	    if (toptexture>0) {
		// top wall
		mid = pixhigh>>HEIGHTBITS;
		pixhigh += pixhighstep;

		if (mid >= renderer.plane.floorclip[rw_x]) {
                    mid = renderer.plane.floorclip[rw_x]-1;
                }

		if (mid >= yl)
		{
		    draw.dc_yl = yl;
		    draw.dc_yh = mid;
		    draw.dc_texturemid = rw_toptexturemid;
		    //draw.dc_source = renderer.data.R_GetColumn(toptexture,texturecolumn);
                    draw.dc_source = Game.getInstance().wad.getTextures().get(toptexture).getColumn(texturecolumn);
		    Game.getInstance().renderer.colfunc.doColFunc(Game.getInstance());
		    renderer.plane.ceilingclip[rw_x] = mid;
		}
		else {
                    renderer.plane.ceilingclip[rw_x] = yl-1;
                }
	    } else {
		// no top wall
		if (markceiling) {
                    renderer.plane.ceilingclip[rw_x] = yl-1;
                }
	    }
			
	    if (bottomtexture>0) {
		// bottom wall
		mid = (pixlow+HEIGHTUNIT-1)>>HEIGHTBITS;
		pixlow += pixlowstep;

		// no space above wall?
		if (mid <= renderer.plane.ceilingclip[rw_x]) {
                    mid = renderer.plane.ceilingclip[rw_x]+1;
                }
		
		if (mid <= yh) {
		    draw.dc_yl = mid;
		    draw.dc_yh = yh;
		    draw.dc_texturemid = rw_bottomtexturemid;
		    //draw.dc_source = renderer.data.R_GetColumn(bottomtexture, texturecolumn);
                    draw.dc_source = Game.getInstance().wad.getTextures().get(bottomtexture).getColumn(texturecolumn);
		    renderer.colfunc.doColFunc(Game.getInstance());
		    renderer.plane.floorclip[rw_x] = mid;
		}
		else {
                    renderer.plane.floorclip[rw_x] = yh+1;
                }
	    } else {
		// no bottom wall
		if (markfloor) {
                    renderer.plane.floorclip[rw_x] = yh+1;
                }
	    }
			
	    if (maskedtexture) {
		// save texturecol
		//  for backdrawing of masked mid texture
		renderer.plane.openings[maskedtexturecol+rw_x] = texturecolumn;
	    }
	}
		
	rw_scale += rw_scalestep;
	topfrac += topstep;
	bottomfrac += bottomstep;
    }
}


//
// R_StoreWallRange
// A wall segment will be drawn
//  between start and stop pixels (inclusive).
//
    void R_StoreWallRange(int start, int stop) {
        int hyp;
        int sineval;
        int distangle, offsetangle;
        int vtop;
        int lightnum;

        Renderer r = Game.getInstance().renderer;
        Bsp bsp = r.bsp;
        Side sidedef = bsp.sidedef;
        Line linedef = bsp.linedef;
        Seg curline = bsp.curline;
        Sector frontsector = bsp.frontsector;
        Sector backsector = bsp.backsector;
        int viewz = r.viewz;
        int[] negonearray = Game.getInstance().things.negonearray;
        Plane plane = r.plane;
        
        // don't overflow and crash
        //if (bsp.ds_p == drawsegs[MAXDRAWSEGS]) {
        if (bsp.ds_p == MAXDRAWSEGS) {
            return;
        }

//#ifdef RANGECHECK
//    if (start >=viewwidth || start > stop)
//	SystemInterface.I_Error ("Bad R_RenderWallRange: %i to %i", start , stop);
//#endif
        sidedef = curline.sidedef;
        linedef = curline.linedef;

        // mark the segment as visible for auto map
        linedef.flags |= ML_MAPPED;

        // calculate rw_distance for scale calculation
        rw_normalangle = curline.angle + ANG90;
        offsetangle = Math.abs(rw_normalangle - rw_angle1);

        if (offsetangle > ANG90) {
            offsetangle = ANG90;
        }

        distangle = ANG90 - offsetangle;
        hyp = r.R_PointToDist(curline.v1.x, curline.v1.y);
        sineval = finesine(distangle >> ANGLETOFINESHIFT);
        rw_distance = FixedPoint.mul(hyp, sineval);

        bsp.drawsegs[bsp.ds_p].x1 = start;
        rw_x = start;
        bsp.drawsegs[bsp.ds_p].x2 = stop;
        bsp.drawsegs[bsp.ds_p].curline = curline;
        rw_stopx = stop + 1;

        // calculate scale at both ends and step
        rw_scale = r.R_ScaleFromGlobalAngle(r.viewangle + r.xtoviewangle[start]);
        bsp.drawsegs[bsp.ds_p].scale1 = rw_scale;
        
        if (stop > start) {
            bsp.drawsegs[bsp.ds_p].scale2 = r.R_ScaleFromGlobalAngle(r.viewangle + r.xtoviewangle[stop]);
            rw_scalestep = (bsp.drawsegs[bsp.ds_p].scale2 - rw_scale) / (stop - start);
            bsp.drawsegs[bsp.ds_p].scalestep = rw_scalestep;
        } else {
            // UNUSED: try to fix the stretched line bug
//#if 0
//	if (rw_distance < FRACUNIT/2)
//	{
//	    int		trx,try;
//	    int		gxt,gyt;
//
//	    trx = curline.v1.x - viewx;
//	    try = curline.v1.y - viewy;
//			
//	    gxt = FixedPoint.mul(trx,viewcos); 
//	    gyt = -FixedPoint.mul(try,viewsin); 
//	    bsp.drawsegs[bsp.ds_p].scale1 = FixedDiv(projection, gxt-gyt)<<detailshift;
//	}
//#endif
            bsp.drawsegs[bsp.ds_p].scale2 = bsp.drawsegs[bsp.ds_p].scale1;
        }

        // calculate texture boundaries
        //  and decide if floor / ceiling marks are needed
        worldtop = frontsector.ceilingheight - viewz;
        worldbottom = frontsector.floorheight - viewz;

        midtexture = 0;
        toptexture = 0;
        bottomtexture = 0;
        maskedtexture = false;
        bsp.drawsegs[bsp.ds_p].maskedtexturecol = -1;

        if (null==backsector) {
            // single sided line
            midtexture = r.data.texturetranslation[sidedef.midTextureNum];
            // a single sided line is terminal, so it must mark ends
            markfloor = true;
            markceiling = true;
            if ((linedef.flags & ML_DONTPEGBOTTOM)>0) {
                vtop = frontsector.floorheight
                //        + textureheight[sidedef.midTextureNum];
                      +  Game.getInstance().wad.getTextures().get(sidedef.midTextureNum).height;
                // bottom of texture at bottom
                rw_midtexturemid = vtop - viewz;
            } else {
                // top of texture at top
                rw_midtexturemid = worldtop;
            }
            rw_midtexturemid += sidedef.rowoffset;

            bsp.drawsegs[bsp.ds_p].silhouette = SIL_BOTH;
            bsp.drawsegs[bsp.ds_p].sprtopclip = Game.getInstance().things.screenheightarray;
            bsp.drawsegs[bsp.ds_p].sprbottomclip = negonearray;
            bsp.drawsegs[bsp.ds_p].bsilheight = Integer.MAX_VALUE;
            bsp.drawsegs[bsp.ds_p].tsilheight = Integer.MIN_VALUE;
        } else {
            // two sided line
            bsp.drawsegs[bsp.ds_p].sprtopclip = null;
            bsp.drawsegs[bsp.ds_p].sprbottomclip = null;
            bsp.drawsegs[bsp.ds_p].silhouette = 0;

            if (frontsector.floorheight > backsector.floorheight) {
                bsp.drawsegs[bsp.ds_p].silhouette = SIL_BOTTOM;
                bsp.drawsegs[bsp.ds_p].bsilheight = frontsector.floorheight;
            } else if (backsector.floorheight > viewz) {
                bsp.drawsegs[bsp.ds_p].silhouette = SIL_BOTTOM;
                bsp.drawsegs[bsp.ds_p].bsilheight = Integer.MAX_VALUE;
                // bsp.drawsegs[bsp.ds_p].sprbottomclip = negonearray;
            }

            if (frontsector.ceilingheight < backsector.ceilingheight) {
                bsp.drawsegs[bsp.ds_p].silhouette |= SIL_TOP;
                bsp.drawsegs[bsp.ds_p].tsilheight = frontsector.ceilingheight;
            } else if (backsector.ceilingheight < viewz) {
                bsp.drawsegs[bsp.ds_p].silhouette |= SIL_TOP;
                bsp.drawsegs[bsp.ds_p].tsilheight = Integer.MIN_VALUE;
                // bsp.drawsegs[bsp.ds_p].sprtopclip = screenheightarray;
            }

            if (backsector.ceilingheight <= frontsector.floorheight) {
                bsp.drawsegs[bsp.ds_p].sprbottomclip = negonearray;
                bsp.drawsegs[bsp.ds_p].bsilheight = Integer.MAX_VALUE;
                bsp.drawsegs[bsp.ds_p].silhouette |= SIL_BOTTOM;
            }

            if (backsector.floorheight >= frontsector.ceilingheight) {
                bsp.drawsegs[bsp.ds_p].sprtopclip = Game.getInstance().things.screenheightarray;
                bsp.drawsegs[bsp.ds_p].tsilheight = Integer.MIN_VALUE;
                bsp.drawsegs[bsp.ds_p].silhouette |= SIL_TOP;
            }

            worldhigh = backsector.ceilingheight - viewz;
            worldlow = backsector.floorheight - viewz;

            // hack to allow height changes in outdoor areas
            if (  frontsector.ceilingpic.equals(Renderer.SKYFLATNAME) /*r.skyflatnum*/
                && backsector.ceilingpic.equals(Renderer.SKYFLATNAME) ) {
                worldtop = worldhigh;
            }

            if (worldlow != worldbottom
                    || !backsector.floorpic.equals(frontsector.floorpic)
                    || backsector.lightlevel != frontsector.lightlevel) {
                markfloor = true;
            } else {
                // same plane on both sides
                markfloor = false;
            }

            if (worldhigh != worldtop
                    || !backsector.ceilingpic.equals(frontsector.ceilingpic)
                    || backsector.lightlevel != frontsector.lightlevel) {
                markceiling = true;
            } else {
                // same plane on both sides
                markceiling = false;
            }

            if (backsector.ceilingheight <= frontsector.floorheight
                    || backsector.floorheight >= frontsector.ceilingheight) {
                // closed door
                markceiling = true;
                markfloor = true;
            }

            if (worldhigh < worldtop) {
                // top texture
                toptexture = r.data.texturetranslation[sidedef.topTextureNum];
                if ((linedef.flags & ML_DONTPEGTOP)>0) {
                    // top of texture at top
                    rw_toptexturemid = worldtop;
                } else {
                    vtop
                            = backsector.ceilingheight
                            + r.data.textureheight[sidedef.topTextureNum];

                    // bottom of texture
                    rw_toptexturemid = vtop - viewz;
                }
            }
            if (worldlow > worldbottom) {
                // bottom texture
                bottomtexture = r.data.texturetranslation[sidedef.bottomTextureNum];

                if ((linedef.flags & ML_DONTPEGBOTTOM)>0) {
                    // bottom of texture at bottom
                    // top of texture at top
                    rw_bottomtexturemid = worldtop;
                } else // top of texture at top
                {
                    rw_bottomtexturemid = worldlow;
                }
            }
            rw_toptexturemid += sidedef.rowoffset;
            rw_bottomtexturemid += sidedef.rowoffset;

            // allocate space for masked texture tables
            if (sidedef.midTextureNum>0) {
                // masked midtexture
                maskedtexture = true;
                maskedtexturecol = plane.lastopening - rw_x;
                bsp.drawsegs[bsp.ds_p].maskedtexturecol = maskedtexturecol;
                plane.lastopening += rw_stopx - rw_x;
            }
        }

        // calculate rw_offset (only needed for textured lines)
        segtextured = midtexture>0 | toptexture>0 | bottomtexture>0 | maskedtexture;

        if (segtextured) {
            offsetangle = rw_normalangle - rw_angle1;

            if (offsetangle > ANG180) {
                offsetangle = -offsetangle;
            }

            if (offsetangle > ANG90) {
                offsetangle = ANG90;
            }

            sineval = finesine(offsetangle >> ANGLETOFINESHIFT);
            rw_offset = FixedPoint.mul(hyp, sineval);

            if (rw_normalangle - rw_angle1 < ANG180) {
                rw_offset = -rw_offset;
            }

            rw_offset += sidedef.textureoffset + curline.offset;
            rw_centerangle = ANG90 + r.viewangle - rw_normalangle;

            // calculate light table
            //  use different light tables
            //  for horizontal / vertical / diagonal
            // OPTIMIZE: get rid of LIGHTSEGSHIFT globally
            if (null==r.fixedcolormap) {
                lightnum = (frontsector.lightlevel >> LIGHTSEGSHIFT) + r.extralight;

                if (curline.v1.y == curline.v2.y) {
                    lightnum--;
                } else if (curline.v1.x == curline.v2.x) {
                    lightnum++;
                }

                if (lightnum < 0) {
                    walllights = r.scalelight[0];
                } else if (lightnum >= LIGHTLEVELS) {
                    walllights = r.scalelight[LIGHTLEVELS - 1];
                } else {
                    walllights = r.scalelight[lightnum];
                }
            }
        }

        // if a floor / ceiling plane is on the wrong side
        //  of the view plane, it is definitely invisible
        //  and doesn't need to be marked.
        if (frontsector.floorheight >= viewz) {
            // above view plane
            markfloor = false;
        }

        if (frontsector.ceilingheight <= viewz
                && !frontsector.ceilingpic.equals(Renderer.SKYFLATNAME) ) {
            // below view plane
            markceiling = false;
        }

        // calculate incremental stepping values for texture edges
        worldtop >>= 4;
        worldbottom >>= 4;

        topstep = -FixedPoint.mul(rw_scalestep, worldtop);
        topfrac = (r.centeryfrac >> 4) - FixedPoint.mul(worldtop, rw_scale);

        bottomstep = -FixedPoint.mul(rw_scalestep, worldbottom);
        bottomfrac = (r.centeryfrac >> 4) - FixedPoint.mul(worldbottom, rw_scale);

        if (null!=backsector) {
            worldhigh >>= 4;
            worldlow >>= 4;

            if (worldhigh < worldtop) {
                pixhigh = (r.centeryfrac >> 4) - FixedPoint.mul(worldhigh, rw_scale);
                pixhighstep = -FixedPoint.mul(rw_scalestep, worldhigh);
            }

            if (worldlow > worldbottom) {
                pixlow = (r.centeryfrac >> 4) - FixedPoint.mul(worldlow, rw_scale);
                pixlowstep = -FixedPoint.mul(rw_scalestep, worldlow);
            }
        }

        // render it
        if (markceiling) {
            r.plane.ceilingplane = r.plane.R_CheckPlane(r.plane.ceilingplane, rw_x, rw_stopx - 1);
        }

        if (markfloor) {
            r.plane.floorplane = r.plane.R_CheckPlane(r.plane.floorplane, rw_x, rw_stopx - 1);
        }

        R_RenderSegLoop();

        // save sprite clipping info
        if (((bsp.drawsegs[bsp.ds_p].silhouette & SIL_TOP)>0 || maskedtexture)
                && null==bsp.drawsegs[bsp.ds_p].sprtopclip) {
            //memcpy(plane.lastopening, r.plane.ceilingclip[start], 2 * (rw_stopx - start));
            System.arraycopy(r.plane.ceilingclip, start, plane.openings,plane.lastopening, 2 * (rw_stopx - start));
            bsp.drawsegs[bsp.ds_p].sprtopclip = Arrays.copyOfRange(r.plane.openings,plane.lastopening - start,r.plane.openings.length);
            plane.lastopening += rw_stopx - start;
        }

        if (((bsp.drawsegs[bsp.ds_p].silhouette & SIL_BOTTOM)>0 || maskedtexture)
                && null==bsp.drawsegs[bsp.ds_p].sprbottomclip) {
            //memcpy(plane.lastopening, r.plane.floorclip[start], 2 * (rw_stopx - start));
            System.arraycopy(r.plane.floorclip, start, plane.openings, plane.lastopening, 2 * (rw_stopx - start));
            bsp.drawsegs[bsp.ds_p].sprbottomclip = Arrays.copyOfRange(r.plane.openings,plane.lastopening - start,r.plane.openings.length);
            plane.lastopening += rw_stopx - start;
        }

        if (maskedtexture && 0==(bsp.drawsegs[bsp.ds_p].silhouette & SIL_TOP)) {
            bsp.drawsegs[bsp.ds_p].silhouette |= SIL_TOP;
            bsp.drawsegs[bsp.ds_p].tsilheight = Integer.MIN_VALUE;
        }
        if (maskedtexture && 0==(bsp.drawsegs[bsp.ds_p].silhouette & SIL_BOTTOM)) {
            bsp.drawsegs[bsp.ds_p].silhouette |= SIL_BOTTOM;
            bsp.drawsegs[bsp.ds_p].bsilheight = Integer.MAX_VALUE;
        }
        bsp.ds_p++;
    }

}
