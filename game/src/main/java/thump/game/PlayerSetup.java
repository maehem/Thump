/*
 * Player Setup - Map Setup
 */
package thump.game;


import java.util.ArrayList;
import java.util.logging.Level;
import thump.base.BoundingBox;
import static thump.base.Defines.logger;
import static thump.game.Defines.MAXPLAYERS;
import thump.game.Defines.Skill;
import static thump.game.ThingStateLUT.sprnames;
import thump.game.maplevel.MapNode;
import thump.game.maplevel.MapObject;
import thump.game.maplevel.MapSector;
import thump.game.maplevel.MapSeg;
import thump.game.maplevel.MapSideDef;
import thump.game.maplevel.MapSubSector;
import static thump.game.play.Local.MAPBLOCKSHIFT;
import static thump.game.play.Local.MAXRADIUS;
import thump.game.play.MObject;
import thump.game.play.SpecialEffects;
import thump.game.play.Switch;
import thump.game.play.Tick;
import thump.game.play.User;
import thump.wad.lump.BlockMapLump;
import thump.wad.lump.MapLump;
import thump.wad.lump.ThingsLump;
import thump.wad.map.Line;
import thump.wad.map.Node;
import thump.wad.map.Sector;
import thump.wad.map.Seg;
import thump.wad.map.Side;
import thump.wad.map.SubSector;
import thump.wad.map.Vertex;
import thump.wad.mapraw.MapThing;

/**
 *
 * @author mark
 */
public class PlayerSetup {
    //
    // MAP related Lookup tables.
    // Store VERTEXES, LINEDEFS, SIDEDEFS, etc.
    //
    //int		numvertexes;
    public Vertex[]	vertexes;

    //int		segs.length;
    //public MapSeg[]	segs;
    public final ArrayList<MapSeg> segs = new ArrayList<>();
    

    //int		numsectors;
    //public MapSector[]	sectors;
    public final ArrayList<MapSector> sectors = new ArrayList<>();

    //int		numsubsectors;
    //public MapSubSector[]	subsectors;
    public final ArrayList<MapSubSector> subsectors = new ArrayList<>();
    

    //int		numnodes;
    //public Node[]	nodes;     // Raw nodes.
    //public MapNode[]    nodes;  //  With state data
    public final ArrayList<MapNode> nodes = new ArrayList<>();

    //int		numlines;
    public Line[]	lines;

    //int		numsides;
    //public Side[]	sides;
    public final ArrayList<MapSideDef> sides = new ArrayList<>();
    

    /*
     * BLOCKMAP
     * Created from axis aligned bounding box of the map,
     * a rectangular array of blocks of size ...
     * Used to speed up collision detection by spatial subdivision in 2D.    
     */
    public int     bmapwidth;      // Blockmap size.
    public int     bmapheight;     // size in mapblocks

    //public BlockMapLump   blockmap;       // int for larger maps
    //public int[]   blockmaplump;   // offsets in blockmap are from here
    
    public int[]   bmapOffsetList;
    public Short[][] bmapList;
    
    public int     bmaporgx;       // origin of block map
    public int     bmaporgy;
    
    public MapObject[] blocklinks;   // for thing chains

    /*
     *  REJECT
     *  For fast sight rejection.
     *  Speeds up enemy AI by skipping detailed
     *  LineOf Sight calculation.
     *  Without special effect, this could be
     *  used as a PVS lookup as well.
     */
    public byte[] rejectmatrix;
    
    // Maintain single and multi player starting spots.
    public static final int MAX_DEATHMATCH_STARTS = 10;

    public MapThing[]	deathmatchstarts = new MapThing[MAX_DEATHMATCH_STARTS];
    //public MapThing	deathmatch_p = null; // Index of current last slot;
    public int	deathmatch_p = 0; // Index of current last slot;
    public MapThing[]	playerstarts = new MapThing[MAXPLAYERS];

    public MapLump map = null;
    
    public Switch svitch = new Switch();
    public SpecialEffects effects = new SpecialEffects(this);
    public User user = new User();
    
    public int getSecNum( Sector sector ) {
//        for (int i=0; i< sectors.length ; i++ ) {
//            if ( ms == sectors[i] ) {
//                return i;
//            }
//        }
        //return -1;
        
        return sectors.indexOf(sector);
    }
    
    public int getLineNum( Line line ) {
        for ( int i=0; i< lines.length; i++ ) {
            if ( line == lines[i] ) {
                return i;
            }
        }
        
        return -1;
    }
/*   Handled in WAD loader already
    //
    // P_LoadVertexes
    //
    void P_LoadVertexes (VertexesLump lump) {
        vertexes = lump.getFrackedVertexList();
        
        //TODO  may have to convert x,y using FRACTBITS (below)
        
        byte[]		data;
        int			i;
        MapVertex	ml;
        Vertex		li;

        // Determine number of lumps:
        //  total lump length / vertex record length.
        //numvertexes = W_LumpLength (lump) / sizeof(MapVertex);

        // Allocate zone memory for buffer.
        vertexes = Z_Malloc (numvertexes*sizeof(vertex_t),PU_LEVEL,0);	

        // Load data into cache.
        data = W_CacheLumpNum (lump,PU_STATIC);

        ml = (MapVertex)data;
        li = vertexes;

        // Copy and convert vertex coordinates,
        // internal representation as fixed.
        for (i=0 ; i<numvertexes ; i++, li++, ml++)
        {
            li.x = ml.x<<FRACBITS;
            li.y = ml.y<<FRACBITS;
        }

        // Free buffer memory.
        Z_Free (data);
    }
*/

    
    // SEGS are loaded in WAD Loader
    
    //
    // P_LoadSegs
    //
//    void P_LoadSegs (SegsLump lump)
//    {
//        segs = lump.getFrackedSegList();
//        byte[]		data;
//        int			i;
//        MapSeg		ml;
//        Seg		li;
//        Line		ldef;
//        int			linedef;
//        int			side;

//        segs.length = W_LumpLength (lump) / sizeof(mapseg_t);
//        segs = Z_Malloc (segs.length*sizeof(seg_t),PU_LEVEL,0);	
//        memset (segs, 0, segs.length*sizeof(seg_t));
//        data = W_CacheLumpNum (lump,PU_STATIC);
//
//        ml = (MapSeg)data;
//        li = segs;
//        for (i=0 ; i<segs.length ; i++, li++, ml++)
//        {
//            li.v1 = &vertexes[SHORT(ml.v1)];
//            li.v2 = &vertexes[SHORT(ml.v2)];
//
//            li.angle = (SHORT(ml.angle))<<16;
//            li.offset = (SHORT(ml.offset))<<16;
//            linedef = SHORT(ml.linedef);
//            ldef = lines[linedef];
//            li.linedef = ldef;
//            side = SHORT(ml.side);
//            li.sidedef = &sides[ldef.sidenum[side]];
//            li.frontsector = sides[ldef.sidenum[side]].ms;
//            if (ldef. flags & ML_TWOSIDED)
//                li.backsector = sides[ldef.sidenum[side^1]].ms;
//            else
//                li.backsector = 0;
//        }
//
//        Z_Free (data);
//    }


//    //
//    // P_LoadSubsectors
//    //
//    void P_LoadSubsectors (SubSectorsLump lump) {
//        subsectors = lump.toArray();
//        
//        byte[]		data;
//        int			i;
//        mapSubSector	ms;
//        SubSector	ssArray;
//
//        numsubsectors = W_LumpLength (lump) / sizeof(MapSubSector);
//        subsectors = Z_Malloc (numsubsectors*sizeof(SubSector),PU_LEVEL,0);	
//        data = W_CacheLumpNum (lump,PU_STATIC);
//
//        ms = (MapSubSector)data;
//        memset (subsectors,0, numsubsectors*sizeof(SubSector));
//        ssArray = subsectors;
//
//        for (i=0 ; i<numsubsectors ; i++, ssArray++, ms++)
//        {
//            ssArray.numlines = SHORT(ms.segs.length);
//            ssArray.firstline = SHORT(ms.firstseg);
//        }
//
//        Z_Free (data);
//    }



//    /**
//     * Load Sectors
//     * 
//     * @param lump 
//     */
//    void P_LoadSectors (SectorsLump lump) {
//        sectors = lump.toArray();
//        byte[]		data;
//        int			i;
//        mapsector_t*	ms;
//        sector_t*		ssArray;
//
//        numsectors = W_LumpLength (lump) / sizeof(mapsector_t);
//        sectors = Z_Malloc (numsectors*sizeof(sector_t),PU_LEVEL,0);	
//        memset (sectors, 0, numsectors*sizeof(sector_t));
//        data = W_CacheLumpNum (lump,PU_STATIC);
//
//        ms = (mapsector_t *)data;
//        ssArray = sectors;
//        for (i=0 ; i<numsectors ; i++, ssArray++, ms++)
//        {
//            ssArray.floorheight = SHORT(ms.floorheight)<<FRACBITS;
//            ssArray.ceilingheight = SHORT(ms.ceilingheight)<<FRACBITS;
//            ssArray.floorpic = R_FlatNumForName(ms.floorpic);
//            ssArray.ceilingpic = R_FlatNumForName(ms.ceilingpic);
//            ssArray.lightlevel = SHORT(ms.lightlevel);
//            ssArray.special = SHORT(ms.special);
//            ssArray.tag = SHORT(ms.tag);
//            ssArray.thinglist = NULL;
//        }
//
//        Z_Free (data);
//    }


//    //
//    // P_LoadNodes
//    //
//    void P_LoadNodes (NodesLump lump) {
//        nodes = lump.toArray();
//        
//        byte[]	data;
//        int		i;
//        int		j;
//        int		k;
//        mapnode_t*	mn;
//        node_t*	no;
//
//        numnodes = W_LumpLength (lump) / sizeof(mapnode_t);
//        nodes = Z_Malloc (numnodes*sizeof(node_t),PU_LEVEL,0);	
//        data = W_CacheLumpNum (lump,PU_STATIC);
//
//        mn = (mapnode_t *)data;
//        no = nodes;
//
//        for (i=0 ; i<numnodes ; i++, no++, mn++)
//        {
//            no.x = SHORT(mn.x)<<FRACBITS;
//            no.y = SHORT(mn.y)<<FRACBITS;
//            no.dx = SHORT(mn.dx)<<FRACBITS;
//            no.dy = SHORT(mn.dy)<<FRACBITS;
//            for (j=0 ; j<2 ; j++)
//            {
//                no.children[j] = SHORT(mn.children[j]);
//                for (k=0 ; k<4 ; k++)
//                    no.bbox[j][k] = SHORT(mn.bbox[j][k])<<FRACBITS;
//            }
//        }
//
//        Z_Free (data);
//    }


    //
    // P_LoadThings
    //
    void P_LoadThings (ThingsLump lump) {
        
        boolean spawn;
        
        MapThing[] things = lump.toArray();
        for (MapThing thing : things) {
            spawn = true;
            // Do not spawn cool, new monsters if !commercial
            if ( Game.getInstance().gameMode != Defines.GameMode.COMMERCIAL) {
                switch(thing.type) {
                    case 68:	// Arachnotron
                    case 64:	// Archvile
                    case 88:	// Boss Brain
                    case 89:	// Boss Shooter
                    case 69:	// Hell Knight
                    case 67:	// Mancubus
                    case 71:	// Pain Elemental
                    case 65:	// Former Human Commando
                    case 66:	// Revenant
                    case 84:	// Wolf SS
                        spawn = false;
                        break;
                }
            }
            if (spawn == false) {
                break;
            }
            
            // Do spawn all other stuff. 
//            mt.x = SHORT(mt.x);
//            mt.y = SHORT(mt.y);
//            mt.angle = SHORT(mt.angle);
//            mt.type = SHORT(mt.type);
//            mt.options = SHORT(mt.options);

            MObject.P_SpawnMapThing (thing);
        }
    }




//    //
//    // P_LoadLineDefs
//    // Also counts secret lines for intermissions.
//    //
//    void P_LoadLineDefs (int lump)
//    {
//        byte[]		data;
//        int			i;
//        maplinedef_t*	mld;
//        Line		ld;
//        Vertex		v1;
//        Vertex		v2;
//
//        numlines = W_LumpLength (lump) / sizeof(maplinedef_t);
//        lines = Z_Malloc (numlines*sizeof(line_t),PU_LEVEL,0);	
//        memset (lines, 0, numlines*sizeof(line_t));
//        data = W_CacheLumpNum (lump,PU_STATIC);
//
//        mld = (maplinedef_t *)data;
//        ld = lines;
//        for (i=0 ; i<numlines ; i++, mld++, ld++)
//        {
//            ld.flags = SHORT(mld.flags);
//            ld.special = SHORT(mld.special);
//            ld.tag = SHORT(mld.tag);
//            v1 = ld.v1 = &vertexes[SHORT(mld.v1)];
//            v2 = ld.v2 = &vertexes[SHORT(mld.v2)];
//            ld.dx = v2.x - v1.x;
//            ld.dy = v2.y - v1.y;
//
//            if (!ld.dx)
//                ld.slopetype = ST_VERTICAL;
//            else if (!ld.dy)
//                ld.slopetype = ST_HORIZONTAL;
//            else
//            {
//                if (FixedDiv (ld.dy , ld.dx) > 0)
//                    ld.slopetype = ST_POSITIVE;
//                else
//                    ld.slopetype = ST_NEGATIVE;
//            }
//
//            if (v1.x < v2.x)
//            {
//                ld.bbox.left = v1.x;
//                ld.bbox.right = v2.x;
//            }
//            else
//            {
//                ld.bbox.left = v2.x;
//                ld.bbox.right = v1.x;
//            }
//
//            if (v1.y < v2.y)
//            {
//                ld.bbox.bottom = v1.y;
//                ld.bbox.top = v2.y;
//            }
//            else
//            {
//                ld.bbox.bottom = v2.y;
//                ld.bbox.top = v1.y;
//            }
//
//            ld.sidenum[0] = SHORT(mld.sidenum[0]);
//            ld.sidenum[1] = SHORT(mld.sidenum[1]);
//
//            if (ld.sidenum[0] != -1)
//                ld.frontsector = sides[ld.sidenum[0]].ms;
//            else
//                ld.frontsector = 0;
//
//            if (ld.sidenum[1] != -1)
//                ld.backsector = sides[ld.sidenum[1]].ms;
//            else
//                ld.backsector = 0;
//        }
//
//        Z_Free (data);
//    }


//    //
//    // P_LoadSideDefs
//    //
//    void P_LoadSideDefs (int lump)
//    {
//        byte[]		data;
//        int			i;
//        mapsidedef_t*	msd;
//        side_t*		sd;
//
//        numsides = W_LumpLength (lump) / sizeof(mapsidedef_t);
//        sides = Z_Malloc (numsides*sizeof(side_t),PU_LEVEL,0);	
//        memset (sides, 0, numsides*sizeof(side_t));
//        data = W_CacheLumpNum (lump,PU_STATIC);
//
//        msd = (mapsidedef_t *)data;
//        sd = sides;
//        for (i=0 ; i<numsides ; i++, msd++, sd++)
//        {
//            sd.textureoffset = SHORT(msd.textureoffset)<<FRACBITS;
//            sd.rowoffset = SHORT(msd.rowoffset)<<FRACBITS;
//            sd.toptexture = R_TextureNumForName(msd.toptexture);
//            sd.bottomtexture = R_TextureNumForName(msd.bottomtexture);
//            sd.midtexture = R_TextureNumForName(msd.midtexture);
//            sd.ms = &sectors[SHORT(msd.ms)];
//        }
//
//        Z_Free (data);
//    }
//

    //
    // P_LoadBlockMap
    //
    void P_LoadBlockMap (/*int lump*/) {
        BlockMapLump mapLump = map.getBlockMap();
//        int		i;
//        int		count;
//
//        blockmaplump = W_CacheLumpNum (lump,PU_LEVEL);
//        blockmap = blockmaplump+4;
//        count = W_LumpLength (lump)/2;

        bmapOffsetList = mapLump.offsetList;
        bmapList = mapLump.blockLists;


//        for (i=0 ; i<count ; i++)
//            blockmaplump[i] = SHORT(blockmaplump[i]);


//        bmaporgx = blockmaplump[0]<<FRACBITS;
        bmaporgx = mapLump.xOrigin;
//        bmaporgy = blockmaplump[1]<<FRACBITS;
        bmaporgy = mapLump.yOrigin;
//        bmapwidth = blockmaplump[2];
        bmapwidth = mapLump.xBlocks;
//        bmapheight = blockmaplump[3];
        bmapheight = mapLump.yBlocks;

        
//        // clear out mobj chains
//        count = sizeof(*blocklinks)* bmapwidth*bmapheight;
//        blocklinks = Z_Malloc (count,PU_LEVEL, 0);
//        memset (blocklinks, 0, count);
        blocklinks = new MapObject[bmapOffsetList.length];
    }



    //
    // P_GroupLines
    // Builds ms line lists and subsector ms numbers.
    // Finds block bounding boxes for sectors.
    //
    void P_GroupLines(MapLump map) {
        //Line*		linebuffer;
        int			i;
        int			j;
        int			total;
        //Line		li;
        //Sector		ms;
        //SubSector	ssArray;
        MapSeg		seg;
        BoundingBox	bbox = new BoundingBox();
        int		block;

        // look up ms number for each subsector
        //ss = subsectors;
//        for (SubSector ss: map.getSubSectors().toArray()) {
//            seg = segs[ss.firstline];
//            ss.ms = seg.sidedef.getSector(map);
//        }
        for (MapSubSector mss: subsectors) {
            //seg = segs[ss.firstline];
            seg = segs.get(mss.subsector.firstline);
            //ss.mapSector = new MapSector(seg.sidedef.getSector(map));
            mss.mapSector = sectors.get(seg.mapSideDef.side.sectorNum);
            mss.subsector.sector = mss.mapSector.sector; // Synchronize the two sectors.
        }

        // count number of lines in each ms
        //li = lines;
        total = 0;
        for (Line line: lines) {
            total++;
            line.frontsector.linecount++;

            if (line.backsector!=null && line.backsector != line.frontsector) {
                line.backsector.linecount++;
                total++;
            }
        }

        // build line tables for each ms
        //linebuffer = Z_Malloc (total*4, PU_LEVEL, 0);
        Line lineBuffer[] = new Line[total];
        
        //sector = sectors;
        for (MapSector ms: sectors) {
            Sector sector = ms.sector;
            //M_ClearBox (bbox);
            bbox.M_ClearBox();
            sector.lines = lineBuffer;
            //li = lines;
            i=0;
            //for (j=0 ; j<numlines ; j++, li++) {
            for ( Line li: lines ) {
            
                if (li.frontsector == sector || li.backsector == sector) {
                    lineBuffer[i] = li;
                    i++;
                    //*linebuffer++ = li;
                    
                    //M_AddToBox (bbox, li.v1.x, li.v1.y);
                    bbox.M_AddToBox(li.v1.x, li.v1.y);
                    //M_AddToBox (bbox, li.v2.x, li.v2.y);
                    bbox.M_AddToBox(li.v2.x, li.v2.y);
                }
            }
            
            if (i != sector.linecount) {
                logger.log(Level.SEVERE, "P_GroupLines: miscounted");
            }

            // set the degenmobj_t to the middle of the bounding box
            ms.soundorg.x = (bbox.right+bbox.left)/2;
            ms.soundorg.y = (bbox.top+bbox.bottom)/2;

            // adjust bounding box to map blocks
            block = (bbox.top-bmaporgy+MAXRADIUS)>>MAPBLOCKSHIFT;
            block = block >= bmapheight ? bmapheight-1 : block;
            sector.blockbox.top=block;

            block = (bbox.bottom-bmaporgy-MAXRADIUS)>>MAPBLOCKSHIFT;
            block = block < 0 ? 0 : block;
            sector.blockbox.bottom=block;

            block = (bbox.right-bmaporgx+MAXRADIUS)>>MAPBLOCKSHIFT;
            block = block >= bmapwidth ? bmapwidth-1 : block;
            sector.blockbox.right=block;

            block = (bbox.left-bmaporgx-MAXRADIUS)>>MAPBLOCKSHIFT;
            block = block < 0 ? 0 : block;
            sector.blockbox.left=block;
        }
    }


    //
    // P_SetupLevel    //TODO Move this to Game?
    //
    void P_SetupLevel( int episode, int mapNum,int playermask, Skill skill) {
        //char	lumpname[9];
        String lumpname;
        int	lumpnum;
        Game game = Game.getInstance();
        
        game.totalkills = 0;
        game.totalitems = 0;
        game.totalsecret = 0;
        game.wminfo.maxfrags = 0;
        game.wminfo.partime = 180;
        for (int i=0 ; i<MAXPLAYERS ; i++) {
            if ( game.players[i] != null ) {
                game.players[i].killcount = 0;
                game.players[i].secretcount = 0;
                game.players[i].itemcount = 0;
            }
        }

        // Initial height of PointOfView
        // will be set by player think.
        game.players[game.consoleplayer].viewz = 1; 

        // Make sure all sounds are stopped before Z_FreeTags.
        game.sound.S_Start ();			


//    #if 0 // UNUSED
//        if (debugfile)
//        {
//            Z_FreeTags (PU_LEVEL, MAXINT);
//            Z_FileDumpHeap (debugfile);
//        }
//        else
//    #endif
            //Z_FreeTags (PU_LEVEL, PU_PURGELEVEL-1);


        // UNUSED W_Profile ();
        Tick.P_InitThinkers ();

        // if working with a devlopment mapNum, reload it
// TODO        W_Reload ();	  //wad.reload()		

        // find mapNum name
        if ( game.gameMode == Defines.GameMode.COMMERCIAL) {
            if (mapNum<10) {
                //sprintf (lumpname,"map0%i", mapNum);
                lumpname = "MAP0" + mapNum;
            } else {
                //sprintf (lumpname,"mapNum%i", mapNum);
                lumpname = "MAP" + mapNum;
            }
        } else {
//            lumpname[0] = 'E';
//            lumpname[1] = '0' + episode;
//            lumpname[2] = 'M';
//            lumpname[3] = '0' + mapNum;
//            lumpname[4] = 0;
                lumpname = "E0" + episode + "M0" + mapNum;
        }

        //lumpnum = W_GetNumForName (lumpname);
        map = (MapLump) game.wad.findByName(lumpname);

        Game.getInstance().leveltime = 0;

        // note: most of this ordering is important	
        P_LoadBlockMap ();
        
        //P_LoadVertexes (lumpnum+ML_VERTEXES);  //lump.vertexesLump
        vertexes = map.getVertexes().getVertexList();
        
        //P_LoadSectors (lumpnum+ML_SECTORS);
        //sectors = map.getSectorsLump().toArray();  //  Need a method to convert sectors to MapSectors
        Sector[] secArray = map.getSectorsLump().toArray();
        for ( Sector s : secArray ) {
            MapSector ms = new MapSector(s);
            sectors.add(ms);
            ms.soundtarget = new MapObject();
        }
        
        // TODO  Merge Side with MapSideDef
        //P_LoadSideDefs (lumpnum+ML_SIDEDEFS);
        Side[] sideArray = map.getSideDefs().toArray();
        for ( Side s: sideArray ) {
            sides.add(new MapSideDef(s));
        }

        //P_LoadLineDefs (lumpnum+ML_LINEDEFS);
        lines = map.getLineDefs().toArray();
        
        //P_LoadSubsectors (lumpnum+ML_SSECTORS);
        SubSector[] ssArray = map.getSubSectors().toArray();
        for ( SubSector ss: ssArray ) {
            subsectors.add(new MapSubSector(ss));
        }
        
        //P_LoadNodes (lumpnum+ML_NODES);
        Node[] nodeArray = map.getNodes().toArray();
        //mapNodes = new MapNode[nodes.length];
//        // Morph Nodes into MapNodes
//        for ( int i=0; i< nodes.length; i++ ) {
//            nodes[i] = new MapNode(nodes[i]);
//        }
        for ( Node n : nodeArray ) {
            nodes.add(new MapNode(n));
        }
        
        
        // Promote the Segs to MapSegs
        //P_LoadSegs (lumpnum+ML_SEGS);
        //segs = map.getSegs().toArray();
        Seg[] segArray = map.getSegs().toArray();
        for ( Seg s: segArray ) {
            MapSeg ms = new MapSeg(s);
            ms.mapSideDef = lookupMapSideDefFor(s.sidedef);
            segs.add(ms);
            
// TODO init s.mapsideef from sidedef.            
        }

        //rejectmatrix = W_CacheLumpNum (lumpnum+ML_REJECT,PU_LEVEL);
        rejectmatrix = map.getRejects().rejects;
        P_GroupLines (map);

        game.bodyqueslot = 0;
        //deathmatch_p = deathmatchstarts[0];
        deathmatch_p = 0;
        P_LoadThings (map.getThings());

        // if deathmatch, randomly spawn the active players
        if (game.deathmatch>0) {
            for (int i=0 ; i<MAXPLAYERS ; i++) {
                if (game.playeringame[i]) {
                    game.players[i].mo = null;
                    game.G_DeathMatchSpawnPlayer (i);
                }
            }
        }

        // clear special respawning que
        game.movingObject.iquehead = 0;
        game.movingObject.iquetail = 0;		

        // set up world state
        game.playerSetup.effects.P_SpawnSpecials ();  /// p_spec.c  // Special Effects

        // build subsector connect matrix
        //	UNUSED P_ConnectSubsectors ();

        // preload graphics
//        if (precache)
//            R_PrecacheLevel ();

        //printf ("free memory: 0x%x\n", Z_FreeMemory());

    }

    
    /**
     *  Initialize Player Setup
     */    
    void P_Init () {
        svitch.P_InitSwitchList();
        effects.P_InitPicAnims();
        Game game = Game.getInstance();
        game.things.R_InitSprites (sprnames, game.wad);
    }

    
    public Seg[] getSegs() {
        Seg[] sArray = new Seg[segs.size()];
        for ( int i=0; i<segs.size(); i++ ) {
            sArray[i] = segs.get(i).seg;
        }
        
        return sArray;
    }
    
    private MapSideDef lookupMapSideDefFor(Side s) {
        for ( MapSideDef msd : sides ) {
            if ( msd.side.equals(s)) return msd;
        }
        
        return null;
    }

    public MapSector lookupMapSectorFor(Sector sec) {
        for ( MapSector ms: sectors ) {
            if ( ms.sector.equals(sec)) return ms;
        }
        
        return null;
    }
}
