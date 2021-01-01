/*

    All the clipping: columns, horizontal spans, sky columns.

 */
package thump.render;

import java.util.Arrays;
import java.util.logging.Level;
import static thump.base.Defines.logger;
import thump.base.FixedPoint;
import static thump.base.FixedPoint.FRACBITS;
import static thump.base.Tables.ANG180;
import static thump.base.Tables.ANG90;
import static thump.base.Tables.ANGLETOFINESHIFT;
import static thump.base.Tables.finesine;
import static thump.base.Tables.finetangent;
import static thump.render.DrawSeg.MAXDRAWSEGS;
import static thump.render.DrawSeg.SIL_BOTH;
import static thump.render.DrawSeg.SIL_BOTTOM;
import static thump.render.DrawSeg.SIL_NONE;
import static thump.render.DrawSeg.SIL_TOP;
import static thump.render.Renderer.LIGHTLEVELS;
import static thump.render.Renderer.LIGHTSCALESHIFT;
import static thump.render.Renderer.LIGHTSEGSHIFT;
import static thump.render.Renderer.MAXLIGHTSCALE;
import thump.wad.Wad;
import static thump.wad.map.Line.ML_DONTPEGBOTTOM;
import static thump.wad.map.Line.ML_DONTPEGTOP;
import static thump.wad.map.Line.ML_MAPPED;
import thump.wad.map.Sector;
import thump.wad.map.Seg;
import thump.wad.mapraw.Column;
import thump.wad.mapraw.MapTexture;

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
        Renderer renderer,
        Wad wad,
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
    Bsp bsp = renderer.bsp;
    Seg curline = bsp.curline;
    
    //curline = ds.curline;
    bsp.frontsector = curline.frontsector;
    bsp.backsector = curline.backsector;
    byte[][][] scalelight = renderer.scalelight;
    
    Sector frontsector = bsp.frontsector;
    Sector backsector = bsp.backsector;

    texnum = renderer.data.texturetranslation[curline.sidedef.getMidTextureNum(wad)];
    MapTexture get = wad.getTextures().get(texnum);

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
    
    RThings things = renderer.things;  // TODO: Need to split Things into lower level Render stuff.
    Plane plane = renderer.plane;
    
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
                + wad.getTextures().get(texnum).height 
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
	    draw.dc_iscale = (int) (0xffffffffL / things.spryscale&0xffffffffL);
	    
	    // draw the texture
	    //col = renderer.data.R_GetColumn(texnum,maskedtexturecol[draw.dc_x]);
            col = wad.getTextures().get(texnum).getColumn(plane.openings[maskedtexturecol+draw.dc_x]);
            //-3);  // All that minus three?  WTF?
			
	    things.R_DrawMaskedColumn (renderer,col);
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
void R_RenderSegLoop (Renderer renderer, Wad wad) {
    int angle;
    int index;
    int yl;
    int yh;
    int mid;
    int texturecolumn=0;
    int top;
    int bottom;

    logger.log(Level.CONFIG, "Render Seg Loop():   rw_x:{0}   rx_stopx:{1}    actual rendering happens here!", new Object[]{rw_x,rw_stopx});
    
    Draw draw = renderer.draw;
    
    for ( ; rw_x < rw_stopx ; rw_x++) {
	// mark floor / ceiling areas
        logger.log(Level.FINER, "    topfrac:{0}  bottomfrac:{1}", new Object[]{topfrac>>FRACBITS,bottomfrac>>FRACBITS});
	yl = (topfrac+HEIGHTUNIT-1)>>HEIGHTBITS;
        
        logger.log(Level.FINER, "    yl set to: {0}", yl);

        int cClip = renderer.plane.ceilingclip[rw_x];
        int fClip = renderer.plane.floorclip[rw_x];
        
        logger.log(Level.FINER, "    ceilingclip:{0}   floorclip:{1}", new Object[]{cClip,fClip} );
        
	// no space above wall?
	if (yl < cClip+1) {
            yl = cClip+1;
            logger.log(Level.FINER, "    ...no space above wall.  yl set to: {0}", yl);
        }
	
	if (markceiling) {
	    top = cClip+1;
	    bottom = yl-1;

            logger.log(Level.FINER, 
                    "    mark ceiling:  top:{0}  bottom:{1}  rw_x:{2}",
                    new Object[]{top, bottom, rw_x}
            );
            
	    if (bottom >= fClip) {
                bottom = fClip-1;
            }

	    if (top <= bottom) {
//		renderer.plane.ceilingplane.top[rw_x] = (byte) top;
//		renderer.plane.ceilingplane.bottom[rw_x] = (byte) bottom;
		renderer.plane.ceilingplane.top[rw_x] = top;
		renderer.plane.ceilingplane.bottom[rw_x] = bottom;
	    }
	}
		
	yh = bottomfrac>>HEIGHTBITS;
        logger.log(Level.FINER, "    yh set to: {0}", yh);

	if (yh >= fClip) {
            yh = fClip-1;
            logger.log(Level.FINER, "    yh set to: {0}", yh);
        }

	if (markfloor) {
	    top = yh+1;
	    bottom = fClip-1;
            logger.log(Level.FINER, 
                    "    mark floor:  top:{0}  bottom:{1}  rw_x:{2}",
                    new Object[]{top, bottom, rw_x}
            );
	    if (top <= cClip) {
                top = cClip+1;
                logger.log(Level.FINER, "    top set to: {0}", top);
            }
	    if (top <= bottom) {
		renderer.plane.floorplane.top[rw_x] = top;
		renderer.plane.floorplane.bottom[rw_x] = bottom;
	    }
	}
	
	// texturecolumn and lighting are independent of wall tiers
	if (segtextured) {
            logger.finer("    textured column");
	    // calculate texture offset
	    angle = (int) (((rw_centerangle + renderer.xtoviewangle[rw_x])&0xFFFFFFFFL)>>ANGLETOFINESHIFT);
            //angle &= 0xFFFFFFFFL;
            angle&=0xFFF;
            logger.log(Level.FINER, "    angle set to: 0x{0}  (int){1}", new Object[]{ Integer.toHexString(angle), (int)angle});

            int finetan = finetangent(angle);
            int mul = FixedPoint.mul(finetan,rw_distance);
            logger.log(Level.FINER, "    finetan:{0}   mul:{1}   rw_offset:{2}", new Object[]{finetan, mul, rw_offset});
	    texturecolumn = rw_offset-mul;
            if ( texturecolumn < 0 ) texturecolumn = 0;  //  Can't be negative
            
	    //texturecolumn = (int) (Integer.toUnsignedLong(rw_offset)-Integer.toUnsignedLong(mul));
	    texturecolumn >>= FRACBITS;
            logger.log(Level.FINER, "    texturecolumn set to: {0}", texturecolumn);

            
            // debug 
            //texturecolumn = 60;
            
	    // calculate lighting
	    index = rw_scale>>LIGHTSCALESHIFT;

	    if (index >=  MAXLIGHTSCALE ) {
                index = MAXLIGHTSCALE-1;
            }

	    draw.dc_colormap = Arrays.copyOfRange(walllights[index], 0, 256);
	    draw.dc_x = rw_x;
	    //draw.dc_iscale = 0xffffffffu / (unsigned)rw_scale;
	    //draw.dc_iscale = Integer.MAX_VALUE / rw_scale;
	    //draw.dc_iscale = (int) (0xFFFFFFFFL / (long)rw_scale);
	    draw.dc_iscale = (int) (0xFFFFFFFFL / Integer.toUnsignedLong(rw_scale) );
	}
	
	// draw the wall tiers
	if (midtexture>0) {
	    // single sided line
            logger.config("wall tiers: midtexture: single sided line.");
	    draw.dc_yl = yl;
	    draw.dc_yh = yh;
	    draw.dc_texturemid = rw_midtexturemid;
            //draw.dc_source = renderer.data.R_GetColumn(midtexture,texturecolumn);
            MapTexture texture = wad.getTextures().get(midtexture);
            if ( texturecolumn == -1 ) {
                int ii=0;  // debug break point
            }
            draw.dc_source = wad.getTextures().get(midtexture).getColumn(texturecolumn);
            logger.log(Level.CONFIG, "    rw_x={0}: Draw a mid texture.  [{1}]", new Object[]{rw_x,texture.name});
	    renderer.colfunc.doColFunc(draw);
	    renderer.plane.ceilingclip[rw_x] = draw.viewheight;
	    renderer.plane.floorclip[rw_x] = -1;
	} else {
	    // two sided line
            logger.config("wall tiers: midtexture: two sided line.");
	    if (toptexture>0) {
		// top wall
                logger.config("    top wall.");
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
                    draw.dc_source = wad.getTextures().get(toptexture).getColumn(texturecolumn);
                    logger.log(Level.CONFIG, "    {0}: Draw a top texture.  yl:{1}  yh:{2}  texturemid:{3}", new Object[]{rw_x,yl,mid,rw_toptexturemid});
		    renderer.colfunc.doColFunc(draw);
                    logger.log(Level.CONFIG, "    ... done.");
		    renderer.plane.ceilingclip[rw_x] = mid;
                    logger.log(Level.CONFIG, "    ceilingclip = mid.");
		} else {
                    renderer.plane.ceilingclip[rw_x] = yl-1;
                }
	    } else {
		// no top wall
                logger.config("    no top wall.");
		if (markceiling) {
                    renderer.plane.ceilingclip[rw_x] = yl-1;
                }
	    }
			
	    if (bottomtexture>0) {
		// bottom wall
                logger.config("    bottom wall.");
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
                    draw.dc_source = wad.getTextures().get(bottomtexture).getColumn(texturecolumn);
                    logger.log(Level.CONFIG, "    {0}: Draw a bottom texture., rw_x");
		    renderer.colfunc.doColFunc(draw);
		    renderer.plane.floorclip[rw_x] = mid;
		} else {
                    renderer.plane.floorclip[rw_x] = yh+1;
                }
	    } else {
		// no bottom wall
                logger.config("    no bottom wall.");
		if (markfloor) {
                    renderer.plane.floorclip[rw_x] = yh+1;
                }
	    }
			
	    if (maskedtexture) {
		// save texturecol
		//  for backdrawing of masked mid texture
                logger.config("save texturecol for backdrawing of masked mid texture");
		renderer.plane.openings[maskedtexturecol+rw_x] = texturecolumn;
	    }
	}
		
	rw_scale += rw_scalestep;
	topfrac += topstep;
        logger.log(Level.FINER, "    topfrac+= {0} == {1}", new Object[]{Integer.toHexString(topstep), Integer.toHexString(topfrac)});
	bottomfrac += bottomstep;
    }
}


//
// R_StoreWallRange
// A wall segment will be drawn
//  between start and stop pixels (inclusive).
//
// TODO: This should be a method of Bsp.java
    void R_StoreWallRange(int start, int stop, Renderer r, Wad wad) {
        int hyp;
        int sineval;
        int distangle, offsetangle;
        int vtop;
        int lightnum;

        logger.log(Level.CONFIG, "Segs.R_StoreWallRange( {0}, {1} )", new Object[]{start, stop});
        if ( stop < 0 ) {
            int ii=0;  // debugger breakpoint
            logger.config("    stop < 0 !!!     BAD!");
        }
        //Renderer r = Game.getInstance().renderer;
        Bsp bsp = r.bsp;
        Plane plane = r.plane;
        //Side sidedef = bsp.sidedef;
        //Line linedef = bsp.linedef;
        //Seg curline = bsp.curline;
        //Sector frontsector = bsp.frontsector;
        //Sector backsector = bsp.backsector;
        int viewz = r.viewz;
        int[] negonearray = r.things.negonearray;
        
        // don't overflow and crash
        //if (bsp.ds_p == drawsegs[MAXDRAWSEGS]) {
        if (bsp.ds_p == MAXDRAWSEGS) {
            return;
        }

        logger.log(Level.FINER, "    plane.lastopening: {0}", plane.lastopening);
        
//#ifdef RANGECHECK
//    if (start >=viewwidth || start > stop)
//	SystemInterface.I_Error ("Bad R_RenderWallRange: %i to %i", start , stop);
//#endif
        bsp.sidedef = bsp.curline.sidedef;
        bsp.linedef = bsp.curline.linedef;

        // mark the segment as visible for auto map
        bsp.linedef.flags |= ML_MAPPED;

        // calculate rw_distance for scale calculation
        rw_normalangle = (bsp.curline.angle + ANG90);//&0xFFFFFFFFL;
        offsetangle = Math.abs(rw_normalangle - rw_angle1);
        logger.log(Level.CONFIG, "    offsetangle:{0}", Integer.toHexString(offsetangle));

        if ((offsetangle&0xFFFFFFFFL) > (ANG90&0xFFFFFFFFL) ) {
            offsetangle = ANG90;
        }
        logger.log(Level.FINER, "    set offset angle: {0}", Integer.toHexString(offsetangle) );
        distangle = (ANG90 - offsetangle);//&0xFFFFFFFFL;
        hyp = r.R_PointToDist(bsp.curline.v1.x, bsp.curline.v1.y);
        sineval = finesine(distangle >> ANGLETOFINESHIFT);
        rw_distance = FixedPoint.mul(hyp, sineval);
        logger.log(Level.FINER, "    set rw_distance: {0}", rw_distance);

        bsp.drawsegs[bsp.ds_p].x1 = start;
        rw_x = start;
        bsp.drawsegs[bsp.ds_p].x2 = stop;
        bsp.drawsegs[bsp.ds_p].curline = bsp.curline;
        logger.log(Level.CONFIG, "    set rw_stopx:{0}", (stop+1) );
        rw_stopx = stop + 1;

        // calculate scale at both ends and step
        logger.log(Level.FINER, 
                "    set rw_scale:  r.viewangle:{0}   r.xtoviewangle[start]:{1}",
                new Object[]{ Integer.toHexString(r.viewangle), Integer.toHexString(r.xtoviewangle[start]) }
        );
        rw_scale = r.R_ScaleFromGlobalAngle(r.viewangle + r.xtoviewangle[start]);
        logger.log(Level.FINER, "    rw_scale: {0}", rw_scale);
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
        logger.log(Level.CONFIG, "    calculate texture boundaries");
        worldtop = bsp.frontsector.ceilingheight - viewz;
        worldbottom = bsp.frontsector.floorheight - viewz;

        midtexture = 0;
        toptexture = 0;
        bottomtexture = 0;
        maskedtexture = false;
        bsp.drawsegs[bsp.ds_p].maskedtexturecol = -1;

        if (null==bsp.backsector) {
            // single sided line
            logger.log(Level.CONFIG, "    single sided line");
            midtexture = r.data.texturetranslation[wad.getTextureNum(bsp.sidedef.midtexture)];
            logger.log(Level.CONFIG, "    midtexture set to: {0}  {1}", new Object[]{midtexture, bsp.sidedef.midtexture});
            // a single sided line is terminal, so it must mark ends
            markfloor = true;
            markceiling = true;
            if ((bsp.linedef.flags & ML_DONTPEGBOTTOM)>0) {
                vtop = bsp.frontsector.floorheight
                //        + textureheight[sidedef.midTextureNum];
                      +  wad.getTextures().get(bsp.sidedef.getMidTextureNum(wad)).height;
                // bottom of texture at bottom
                rw_midtexturemid = vtop - viewz;
            } else {
                // top of texture at top
                rw_midtexturemid = worldtop;
            }
            rw_midtexturemid += bsp.sidedef.rowoffset;

            bsp.drawsegs[bsp.ds_p].silhouette = SIL_BOTH;
            bsp.drawsegs[bsp.ds_p].sprtopclip = r.things.screenheightarray;
            bsp.drawsegs[bsp.ds_p].sprbottomclip = negonearray;
            bsp.drawsegs[bsp.ds_p].bsilheight = Integer.MAX_VALUE;
            bsp.drawsegs[bsp.ds_p].tsilheight = Integer.MIN_VALUE;
        } else {
            // two sided line
            logger.log(Level.CONFIG, "    two sided line");
            bsp.drawsegs[bsp.ds_p].sprtopclip = null;
            bsp.drawsegs[bsp.ds_p].sprbottomclip = null;
            bsp.drawsegs[bsp.ds_p].silhouette = SIL_NONE;

            if (bsp.frontsector.floorheight > bsp.backsector.floorheight) {
                bsp.drawsegs[bsp.ds_p].silhouette = SIL_BOTTOM;
                bsp.drawsegs[bsp.ds_p].bsilheight = bsp.frontsector.floorheight;
            } else if (bsp.backsector.floorheight > viewz) {
                bsp.drawsegs[bsp.ds_p].silhouette = SIL_BOTTOM;
                bsp.drawsegs[bsp.ds_p].bsilheight = Integer.MAX_VALUE;
                //bsp.drawsegs[bsp.ds_p].sprbottomclip = negonearray; // commented out in C-source
            }

            if (bsp.frontsector.ceilingheight < bsp.backsector.ceilingheight) {
                bsp.drawsegs[bsp.ds_p].silhouette |= SIL_TOP;
                bsp.drawsegs[bsp.ds_p].tsilheight = bsp.frontsector.ceilingheight;
            } else if (bsp.backsector.ceilingheight < viewz) {
                bsp.drawsegs[bsp.ds_p].silhouette |= SIL_TOP;
                bsp.drawsegs[bsp.ds_p].tsilheight = Integer.MIN_VALUE;
                // bsp.drawsegs[bsp.ds_p].sprtopclip = screenheightarray;
            }

            if (bsp.backsector.ceilingheight <= bsp.frontsector.floorheight) {
                bsp.drawsegs[bsp.ds_p].sprbottomclip = negonearray;
                bsp.drawsegs[bsp.ds_p].bsilheight = Integer.MAX_VALUE;
                bsp.drawsegs[bsp.ds_p].silhouette |= SIL_BOTTOM;
            }

            if (bsp.backsector.floorheight >= bsp.frontsector.ceilingheight) {
                bsp.drawsegs[bsp.ds_p].sprtopclip = r.things.screenheightarray;
                bsp.drawsegs[bsp.ds_p].tsilheight = Integer.MIN_VALUE;
                bsp.drawsegs[bsp.ds_p].silhouette |= SIL_TOP;
            }

            worldhigh = bsp.backsector.ceilingheight - viewz;
            worldlow = bsp.backsector.floorheight - viewz;

            // hack to allow height changes in outdoor areas
            if (  bsp.frontsector.ceilingpic.equals(Renderer.SKYFLATNAME) /*r.skyflatnum*/
                && bsp.backsector.ceilingpic.equals(Renderer.SKYFLATNAME) ) {
                worldtop = worldhigh;
            }

            if (worldlow != worldbottom
                    || !bsp.backsector.floorpic.equals(bsp.frontsector.floorpic)
                    || bsp.backsector.lightlevel != bsp.frontsector.lightlevel) {
                markfloor = true;
            } else {
                // same plane on both sides
                markfloor = false;
            }

            if (worldhigh != worldtop
                    || !bsp.backsector.ceilingpic.equals(bsp.frontsector.ceilingpic)
                    || bsp.backsector.lightlevel != bsp.frontsector.lightlevel) {
                markceiling = true;
            } else {
                // same plane on both sides
                markceiling = false;
            }

            if (bsp.backsector.ceilingheight <= bsp.frontsector.floorheight
                    || bsp.backsector.floorheight >= bsp.frontsector.ceilingheight) {
                // closed door
                markceiling = true;
                markfloor = true;
            }

            if (worldhigh < worldtop) {
                // top texture
                //try {
                    toptexture = r.data.texturetranslation[bsp.sidedef.getTopTextureNum(wad)];
                //} catch( ArrayIndexOutOfBoundsException e ) {
                //    toptexture = -1;
                //}
                
                if ((bsp.linedef.flags & ML_DONTPEGTOP)>0) {
                    // top of texture at top
                    logger.log(Level.FINER, "    top texture at top");
                    rw_toptexturemid = worldtop;
                } else {
                    int topTextureNum = bsp.sidedef.getTopTextureNum(wad);
                    if ( topTextureNum > 0 ) {
                        vtop
                                = bsp.backsector.ceilingheight
                                + r.data.textureheight[bsp.sidedef.getTopTextureNum(wad)];
                    } else {
                        vtop = bsp.backsector.ceilingheight;
                    }
                    // bottom of texture
                    logger.log(Level.FINER, "    bottom of texture");
                    rw_toptexturemid = vtop - viewz;
                }
            }
            if (worldlow > worldbottom) {
                // bottom texture
                logger.log(Level.FINER, "    bottom texture");
                int bottomTextureNum;
                    bottomTextureNum = bsp.sidedef.getBottomTextureNum(wad);
                //try {
                    bottomtexture = r.data.texturetranslation[bottomTextureNum];
                //} catch( ArrayIndexOutOfBoundsException e ) {
                //    bottomtexture = -1;
                    //throw e;
                //}
                
                if ((bsp.linedef.flags & ML_DONTPEGBOTTOM)>0) {
                    // bottom of texture at bottom
                    // top of texture at top
                    rw_bottomtexturemid = worldtop;
                } else {  // top of texture at top
                    rw_bottomtexturemid = worldlow;
                }
            }
            rw_toptexturemid += bsp.sidedef.rowoffset;
            rw_bottomtexturemid += bsp.sidedef.rowoffset;

            // allocate space for masked texture tables
            if (bsp.sidedef.getMidTextureNum(wad)>0) {
                // masked midtexture
                maskedtexture = true;
                maskedtexturecol = plane.lastopening - rw_x;
                bsp.drawsegs[bsp.ds_p].maskedtexturecol = maskedtexturecol;
                logger.log(Level.FINER,
                        "masked midtexture:  plane.lastopening += rw_stopx - rw_x   {0} += {1}-{2} == {3}",
                        new Object[]{plane.lastopening + rw_stopx - rw_x, rw_stopx, rw_x});
                plane.lastopening += rw_stopx - rw_x;
            }
        }

        // calculate rw_offset (only needed for textured lines)
        segtextured = midtexture>0 | toptexture>0 | bottomtexture>0 | maskedtexture;

        if (segtextured) {
            // rw_normalangle: 1073741824
            // rw_angle1     : 2037829458
            logger.log(Level.FINER, "    segtextured:  rw_normalangle:{0} - rw_angle1:{1} == offsetangle:{2}",
                    new Object[]{
                        Integer.toHexString(rw_normalangle), 
                        Integer.toHexString(rw_angle1), 
                        Integer.toHexString(rw_normalangle - rw_angle1)
                    }
            );
            offsetangle = (rw_normalangle - rw_angle1);//&0xFFFFFFFFL;

            if ( (offsetangle&0xFFFFFFFFL) > (ANG180&0xFFFFFFFFL) ) {
                logger.log(Level.FINER, "        it's higher than 180 deg");
                //offsetangle = -offsetangle;
                //offsetangle = (~offsetangle&0xFFFFFFFFL)+1;
                offsetangle = ((~offsetangle)+1);//&0xFFFFFFFFL;   // two's compliment
                logger.log(Level.CONFIG, "        offsetangle now: {0}", offsetangle);
            }

            if ((offsetangle&0xFFFFFFFFL) > (ANG90&0xFFFFFFFFL)) {
                logger.log(Level.FINER, "        it's higher than 90 deg");
                offsetangle = ANG90;
                logger.log(Level.FINER, "        offsetangle now: {0}", offsetangle);
            }

            sineval = finesine(offsetangle >> ANGLETOFINESHIFT);
            rw_offset = FixedPoint.mul(hyp, sineval);
            logger.log(Level.FINER, "    rw_offset: {0}", rw_offset);

            if ( ((rw_normalangle - rw_angle1)&0xFFFFFFFFL) < (ANG180&0xFFFFFFFFL) ) {
                rw_offset = -rw_offset;
            logger.log(Level.FINER, "    rw_offset: {0}", rw_offset);
            }
//            if ( rw_normalangle - rw_angle1 > 0) {
//                rw_offset = -rw_offset;
//            }
            
            rw_offset += bsp.sidedef.textureoffset + bsp.curline.offset;
            logger.log(Level.FINER, "    rw_offset: {0}", rw_offset);
            rw_centerangle = (ANG90 + r.viewangle - rw_normalangle);//&0xFFFFFFFFL;
//            if ( rw_centerangle < 0 ) {
//                rw_centerangle =   ANG180 + ANG90 + r.viewangle - rw_normalangle;
//            }
            // calculate light table
            //  use different light tables
            //  for horizontal / vertical / diagonal
            // OPTIMIZE: get rid of LIGHTSEGSHIFT globally
            if (null==r.fixedcolormap) {
                lightnum = (bsp.frontsector.lightlevel >> LIGHTSEGSHIFT) + r.extralight;

                if (bsp.curline.v1.y == bsp.curline.v2.y) {
                    lightnum--;
                } else if (bsp.curline.v1.x == bsp.curline.v2.x) {
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
        if (bsp.frontsector.floorheight >= viewz) {
            // above view plane
            markfloor = false;
        }

        if (bsp.frontsector.ceilingheight <= viewz
                && !bsp.frontsector.ceilingpic.equals(Renderer.SKYFLATNAME) ) {
            // below view plane
            markceiling = false;
        }

        // calculate incremental stepping values for texture edges
        worldtop >>= 4;
        worldbottom >>= 4;

        topstep = -FixedPoint.mul(rw_scalestep, worldtop);
        logger.log(Level.FINER, "    topstep:{0}", topstep);
        logger.log(Level.FINER,
                "    r.centeryfrac: {0}   worldtop:{1}   rw_scale:{2}",
                new Object[]{Integer.toHexString(r.centeryfrac), Integer.toHexString(worldtop), Integer.toHexString(rw_scale) }
        );
        topfrac = (r.centeryfrac >> 4) - FixedPoint.mul(worldtop, rw_scale);
        logger.log(Level.FINER, "    topfrac:{0} == centeryfrac>>4:{1}  -  fixedpoint.mul():{2}", 
                new Object[]{
                    Integer.toHexString(topfrac),
                    Integer.toHexString(r.centeryfrac>>4),
                    Integer.toHexString(FixedPoint.mul(worldtop, rw_scale)),
                });
        logger.log(Level.FINER, "    bottomstep:{0}", bottomstep);
        bottomstep = -FixedPoint.mul(rw_scalestep, worldbottom);
        logger.log(Level.FINER, "    bottomfrac:{0}", bottomfrac);
        bottomfrac = (r.centeryfrac >> 4) - FixedPoint.mul(worldbottom, rw_scale);

        if (null!=bsp.backsector) {
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
            logger.log(Level.FINE, "    ceiling check plane: {0} {1}", new Object[]{rw_x, rw_stopx - 1});
            plane.ceilingplane = plane.R_CheckPlane(plane.ceilingplane, rw_x, rw_stopx - 1);
        }

        if (markfloor) {
            logger.log(Level.FINE, "    floor check plane: {0} {1}", new Object[]{rw_x, rw_stopx - 1});
            plane.floorplane = plane.R_CheckPlane(plane.floorplane, rw_x, rw_stopx - 1);
        }

        R_RenderSegLoop(r, wad);
        
        logger.log(Level.FINER, "       rw_x = {0}   rw_stopx = {1}", new Object[]{rw_x, rw_stopx});

        DrawSeg seg = bsp.drawsegs[bsp.ds_p];
        // save sprite clipping info
        if (((seg.silhouette & SIL_TOP)>0 || maskedtexture)
                && null==seg.sprtopclip) {
            //memcpy(plane.lastopening, r.plane.ceilingclip[start], 2 * (rw_stopx - start));
            try {
            //System.arraycopy(plane.ceilingclip, start, plane.openings,plane.lastopening, /*2 **/ (rw_stopx - start));
            System.arraycopy(plane.ceilingclip, start, plane.openings,plane.lastopening, 2 * (rw_stopx - start));
            } catch ( ArrayIndexOutOfBoundsException e ) {
                throw e;
            }
            
            if ( plane.lastopening - start > 0 ) {
                seg.sprtopclip = Arrays.copyOfRange(plane.openings,plane.lastopening - start,plane.openings.length);
            }
            plane.lastopening += rw_stopx - start;
            logger.log(Level.FINER, "    plane.lastopening += rw_stopx - start   {0} += {1} - {2}", new Object[]{ plane.lastopening, rw_stopx, start});
        }

        if (((seg.silhouette & SIL_BOTTOM)>0 || maskedtexture)
                && null==seg.sprbottomclip) {
            //memcpy(plane.lastopening, r.plane.floorclip[start], 2 * (rw_stopx - start));
            logger.log(Level.FINE, "    Arrays.copyOfRange( src:floorclip, startPos:{0}, dest:openings:, len:{1} )", 
                    new Object[]{start, (rw_stopx - start)});
            System.arraycopy(
                    r.plane.floorclip,  start, 
                    plane.openings,     plane.lastopening, 
                    /* 2 * */ (rw_stopx - start)
           //         2 *  (rw_stopx - start)
            );
            
            if ( plane.lastopening - start > 0 ) {
                logger.log(Level.FINE, 
                        "    seg.sprbottomclip = Arrays.copyOfRange( openings:{0}, lastopening-start:{1}, len:{2} )", 
                        new Object[]{r.plane.openings, plane.lastopening - start, plane.openings.length});
                seg.sprbottomclip = Arrays.copyOfRange(
                        plane.openings,plane.lastopening - start,plane.openings.length);
            } else {
                int ii=0;  // debug beark point
            }
            
            plane.lastopening += rw_stopx - start;
            logger.log(Level.FINER, "     plane.lastopening += rw_stopx - start   {0} += {1} - {2}", new Object[]{ plane.lastopening, rw_stopx, start});
        }

        if (maskedtexture && 0==(seg.silhouette & SIL_TOP)) {
            seg.silhouette |= SIL_TOP;
            seg.tsilheight = Integer.MIN_VALUE;
        }
        if (maskedtexture && 0==(seg.silhouette & SIL_BOTTOM)) {
            seg.silhouette |= SIL_BOTTOM;
            seg.bsilheight = Integer.MAX_VALUE;
        }
        bsp.ds_p++;
    }

}
