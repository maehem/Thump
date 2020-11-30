/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.wad;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;
import static thump.base.Defines.logger;
import thump.wad.lump.DemoLump;
import thump.wad.map.Flat;
import thump.wad.lump.BlockMapLump;
import thump.wad.lump.ColorMapLump;
import thump.wad.lump.DmxGusLump;
import thump.wad.lump.EndDoomLump;
import thump.wad.lump.FlatsLump;
import thump.wad.lump.GenMidiLump;
import thump.wad.lump.LineDefsLump;
import thump.wad.lump.Lump;
import thump.wad.lump.LumpFactory;
import thump.wad.lump.MapLump;
import thump.wad.lump.MusicLump;
import thump.wad.lump.NodesLump;
import thump.wad.lump.PCSoundLump;
import thump.wad.lump.PNamesLump;
import thump.wad.lump.PatchesLump;
import thump.wad.lump.PictureLump;
import thump.wad.lump.PlaypalLump;
import thump.wad.lump.RejectLump;
import thump.wad.lump.SectorsLump;
import thump.wad.lump.SegsLump;
import thump.wad.lump.SideDefsLump;
import thump.wad.lump.SoundEffectLump;
import thump.wad.lump.SpritesLump;
import thump.wad.lump.SubSectorsLump;
import thump.wad.lump.TextureLump;
import thump.wad.lump.ThingsLump;
import thump.wad.lump.VertexesLump;
import thump.wad.mapraw.MapTexture;
import thump.wad.mapraw.PatchData;

/**
 *
 * @author mark
 */
public class WadLoader {

    private WadLoader() {}
    
    public static Wad getWad(File wadFile) {
        Wad wad = new Wad();
        FileChannel fc = null;
        logger.log(Level.CONFIG, "Get WAD File: {0}\n", wadFile.getAbsolutePath());
        
        try {
            RandomAccessFile aFile     = new RandomAccessFile(wadFile, "r");

            fc = aFile.getChannel();
            fc.position(0);
            //in = new DataInputStream(new BufferedInputStream(new FileInputStream(wadFile)));

            ByteBuffer bb = ByteBuffer.allocate(4);  // For ints.
            ByteBuffer nn = ByteBuffer.allocate(8);  // For Lump Names
            
            int nread;
            do {
                nread = fc.read(bb);
            } while (nread != -1 && bb.hasRemaining());
            //fc.read(bb);
            //in.read(id);
            wad.identification = new String(bb.array(), "ASCII");
            bb.clear();
            fc.read(bb);
            bb.position(0);
            bb = bb.order(ByteOrder.LITTLE_ENDIAN);
            wad.numlumps = bb.getInt();
            bb.clear();
            fc.read(bb);
            bb.position(0);
            wad.infotableofs = bb.getInt();

            logger.log(Level.CONFIG, "    type: {0}   numlumps: {1}    tableOffset: {2}\n", new Object[]{wad.identification, wad.numlumps, wad.infotableofs});

            fc.position(wad.infotableofs);
            MapLump currentMap = null;  // TODO:  Have MapLump consume the whole section.
            SpritesLump spritesLump = null;
            PatchesLump patchesLump = null;
            //PatchesLump patchesLump2 = null;
            //PatchesLump patchesLump3 = null;
            FlatsLump    flatsLump = null;
            //FlatsLump    flatsLump2 = null;
            //FlatsLump    flatsLump3 = null;
            TextureLump  textureLump1 = null;
            TextureLump  textureLump2 = null;
            
            
            
            // Go to infotable offset and create list of lumps.
            for (int i = 0; i < wad.numlumps; i++) {
                bb.clear();
                fc.read(bb);
                bb.position(0);
                int filePos = bb.getInt();
                
                bb.clear();
                fc.read(bb);
                bb.position(0);
                int lumpSize = bb.getInt();
                
                nn.clear();
                fc.read(nn);
                nn.position(0);
                String name = new String(nn.array(), "ASCII").trim();
                int len = name.length();
                //logger.log(Level.CONFIG, "    Lump::  name: {0}  filePos: {1}  lumpSize: {2}", new Object[]{name, filePos, lumpSize});
                Lump lump = null;
                long mark = fc.position();
                if ( name.startsWith("PLAYPAL") ) {
                    lump = new PlaypalLump(fc, name, filePos, lumpSize);
                    wad.paletteList = ((PlaypalLump)lump).paletteList;
                    //lump.loadData(fc);
                } else if ( name.startsWith("COLORMAP") ) {
                    lump = new ColorMapLump(fc, name, filePos, lumpSize);
                } else if ( name.startsWith("ENDOOM") ) {
                    lump = new EndDoomLump(fc, name, filePos, lumpSize);
                    //logger.config(lump.toString());
                } else if ( name.startsWith("S_START") ) {
                    spritesLump = new SpritesLump(fc, name, filePos, lumpSize/*, wad.getPlayPalLump().paletteList*/);
                    lump = spritesLump;
                    wad.spritesLump = spritesLump;
                    //logger.config(lump.toString());
                } else if ( spritesLump != null ) {
                    if ( name.startsWith("S_END") ) {
                        spritesLump = null;
                        lump = null;
                        //logger.config("End of SPRITES lump.");
                    } else {
                        PatchData pic = spritesLump.addSprite( fc, name, filePos, lumpSize );
                        //logger.config(pic.toString());
                    }
                } else if ( name.startsWith("P_START") ) {
                    patchesLump = new PatchesLump(fc, name, filePos, lumpSize /*, wad.getPlayPalLump().paletteList*/);
                    lump = patchesLump;
                    wad.patchesLump = patchesLump;
                    logger.config(lump.toString());
                } else if ( 
                           name.startsWith("P1_START") || name.startsWith("P1_END") 
                        || name.startsWith("P2_START") || name.startsWith("P2_END") 
                        || name.startsWith("P3_START") || name.startsWith("P3_END") 
                        ) {
                    logger.config(name);
                    // Do nothing.
                    lump = null;
                } else if ( patchesLump != null ) {
                    if ( name.startsWith("P_END") ) {
                        patchesLump = null;
                        lump = null;
                        //logger.config("End of FLATS lumps.");
                    } else {
                        PatchData patch = patchesLump.addPatch( fc, name, filePos, lumpSize );
                        //logger.config(patch.toString());
                    }

////////////
                } else if ( name.startsWith("F_START") ) {
                    flatsLump = new FlatsLump(fc, name, filePos, lumpSize);
                    lump = flatsLump;
                    wad.flatsLump = flatsLump;

                    //logger.config(lump.toString());
                } else if ( 
                           name.startsWith("F1_START") || name.startsWith("F1_END") 
                        || name.startsWith("F2_START") || name.startsWith("F2_END") 
                        || name.startsWith("F3_START") || name.startsWith("F3_END") 
                        ) {
                    // Do nothing.
                    lump = null;
                } else if ( flatsLump != null ) {
                    if ( name.startsWith("F_END") ) {
                        flatsLump = null;
                        lump = null;
                        //logger.config("End of FLATS lumps.");
                    } else {
                        Flat flat = flatsLump.addFlat( fc, name, filePos, lumpSize );
                        //logger.config(flat.toString());
                    }
//                } else if ( name.startsWith("F_START") || name.startsWith("F_END") ) {
//                    // Do nothing.
//                    lump = null;
//                } else if ( name.startsWith("F1_START") ) {
//                    flatsLump = new FlatsLump(fc, name, filePos, lumpSize);
//                    lump = flatsLump;
//                    //logger.config(lump.toString());
//                } else if ( flatsLump != null ) {
//                    if ( name.startsWith("F1_END") ) {
//                        flatsLump = null;
//                        lump = null;
//                        //logger.config("End of Flats 1 lump.\n");
//                    } else {
//                        Flat flat = flatsLump.addFlat( fc, name, filePos, lumpSize );
//                        //logger.config(flat.toString());
//                    }
//                } else if ( name.startsWith("F2_START") ) {
//                    flatsLump2 = new FlatsLump(fc, name, filePos, lumpSize);
//                    lump = flatsLump2;
//                    //logger.config(lump.toString());
//                } else if ( flatsLump2 != null ) {
//                    if ( name.startsWith("F2_END") ) {
//                        flatsLump2 = null;
//                        lump = null;
//                        //logger.config("End of Flats 2 lump.\n");
//                    } else {
//                        Flat flat = flatsLump2.addFlat( fc, name, filePos, lumpSize );
//                        //logger.config(flat.toString());
//                    }
//                } else if ( name.startsWith("F3_START") ) {
//                    flatsLump3 = new FlatsLump(fc, name, filePos, lumpSize);
//                    lump = flatsLump3;
//                    //logger.config(lump.toString());
//                } else if ( flatsLump3 != null ) {
//                    if ( name.startsWith("F3_END") ) {
//                        flatsLump3 = null;
//                        lump = null;
//                        //logger.config("End of Flats 3 lump.\n");
//                    } else {
//                        Flat flat = flatsLump3.addFlat( fc, name, filePos, lumpSize );
//                        //logger.config(flat.toString());
//                    }
                } else if ( (name.startsWith("MAP") || name.matches("E[1-9]M[1-9](.*)")) && lumpSize == 0 ) {
                    lump = new MapLump(fc, name, filePos, lumpSize);
                    currentMap = (MapLump) lump;
                    wad.mapLumps.add(currentMap);
                } else if ( name.startsWith("THINGS") ) {
                    lump = new ThingsLump(fc, name, filePos, lumpSize);
                    currentMap.setThings((ThingsLump) lump);
                    //logger.config(((ThingsLump)lump).toString());
                    lump = null;
                } else if ( name.startsWith("LINEDEFS") ) {
                    lump = new LineDefsLump(currentMap, fc, name, filePos, lumpSize);
                    currentMap.setLineDefs((LineDefsLump)lump);
                    //logger.config(((LineDefsLump)lump).toString());
                    lump = null;
                } else if ( name.startsWith("SIDEDEFS") ) {
                    lump = new SideDefsLump(fc, name, filePos, lumpSize);
                    currentMap.setSideDefs((SideDefsLump) lump);
                    //logger.config(((SideDefsLump)lump).toString());
                    lump = null;
                } else if ( name.startsWith("VERTEXES") ) {
                    lump = new VertexesLump(currentMap, fc, name, filePos, lumpSize);
                    //currentMap.setVertexes((VertexesLump) lump);
                    //logger.config(((VertexesLump)lump).toString());
                    lump = null;
                } else if ( name.startsWith("SEGS") ) {
                    lump = new SegsLump(currentMap, fc, name, filePos, lumpSize);
                    currentMap.setSegs((SegsLump) lump);
                    //logger.config(((SegsLump)lump).toString());
                    lump = null;
                } else if ( name.startsWith("NODES") ) {
                    lump = new NodesLump(fc, name, filePos, lumpSize);
                    currentMap.setNodes((NodesLump) lump);
                    //logger.config(((NodesLump)lump).toString());
                    lump = null;
                } else if ( name.startsWith("SECTORS") ) {
                    lump = new SectorsLump(fc, name, filePos, lumpSize);
                    currentMap.setSectorsLump((SectorsLump) lump);
                    //logger.config(((SectorsLump)lump).toString());
                    lump = null;
                } else if ( name.startsWith("SSECTORS") ) {
                    lump = new SubSectorsLump(fc, name, filePos, lumpSize);
                    currentMap.setSubSectors((SubSectorsLump) lump);
                    //logger.config(((SubSectorsLump)lump).toString());
                    lump = null;
                } else if ( name.startsWith("REJECT") ) {
                    lump = new RejectLump(fc, name, filePos, lumpSize);
                    currentMap.setRejects((RejectLump) lump);
                    //logger.config(((RejectLump)lump).toString());
                    lump = null;
                } else if ( name.startsWith("BLOCKMAP") ) {
                    lump = new BlockMapLump(fc, name, filePos, lumpSize);
                    currentMap.setBlockMap((BlockMapLump) lump);
                    //logger.config(lump.toString());
                    lump = null;
                } else if ( name.startsWith("PNAMES") ) {
                    lump = new PNamesLump(fc, name, filePos, lumpSize);
                    wad.pNamesLump = (PNamesLump) lump;
                    //logger.config(lump.toString());
                } else if ( name.startsWith("GENMIDI") ) {
                    // Not implemented in this port.
                    lump = new GenMidiLump(name, filePos, lumpSize);
                } else if ( name.startsWith("TEXTURE1") ) {
                    lump = new TextureLump(wad, fc, name, filePos, lumpSize);
                    textureLump1 = (TextureLump) lump;
                    //logger.config(lump.toString());
                } else if ( name.startsWith("TEXTURE2") ) {
                    lump = new TextureLump(wad, fc, name, filePos, lumpSize);
                    textureLump2 = (TextureLump) lump;
                    //logger.config(lump.toString());
//                } else if ( name.startsWith("TEXTURE1") || name.startsWith("TEXTURE2") ) {
//                    if ( textureLump == null ) {
//                        textureLump = new TextureLump(wad, fc, name, filePos, lumpSize);
//                    }
//                    lump = textureLump;
                    //logger.config(lump.toString());
//                } else if ( 
//                           name.startsWith("F1_START") || name.startsWith("F1_END") 
//                        || name.startsWith("F2_START") || name.startsWith("F2_END") 
//                        || name.startsWith("F3_START") || name.startsWith("F3_END") 
//                        ) {
//                    // Do nothing.
//                    lump = null;
//                } else if ( flatsLump != null ) {
//                    if ( name.startsWith("F_END") ) {
//                        flatsLump = null;
//                        lump = null;
//                        //logger.config("End of FLATS lumps.");
//                    } else {
//                        Flat flat = flatsLump.addFlat( fc, name, filePos, lumpSize );
//                        //logger.config(flat.toString());
//                    }
                } else if ( name.startsWith("DMXGUS") ) {
                    // Not implemented in this port.
                    lump = new DmxGusLump(fc, name, filePos, lumpSize);
//                } else if ( name.startsWith("FLAT") ) {
//                    // Shouldn't need this. Flats are either S_ F_ or P_
//                    lump = new Flat(fc, name, filePos, lumpSize);
//                    //logger.config(lump.toString());
                } else if ( name.startsWith("D_") ) {
                    lump = new MusicLump(fc, name, filePos, lumpSize); //logger.config(lump.toString());
                    //logger.config(lump.toString());
                } else if ( name.startsWith("DP") ) {
                    lump = new PCSoundLump(fc, name, filePos, lumpSize);
                    //logger.config(lump.toString());
                } else if ( name.startsWith("DS") ) {
                    lump = new SoundEffectLump(fc, name, filePos, lumpSize);
                    //logger.config(lump.toString());
                } else if ( name.startsWith("DEMO") ) {
                    lump = new DemoLump(fc, name, filePos, lumpSize);
                    //logger.config(lump.toString());
                } else if ( name.startsWith("HELP") ||
                        name.startsWith("HELP") ||
                        name.startsWith("TITLEPIC") ||
                        name.startsWith("CREDIT") ||
                        name.startsWith("VICTORY") ||
                        name.startsWith("PFUB") ||
                        name.startsWith("WI") ||
                        name.startsWith("ST") ||
                        name.startsWith("M_") ||
                        name.startsWith("BRDR") ||
                        name.startsWith("INTERPIC") ||
                        name.startsWith("AMMNUM") ||
                        name.startsWith("CWILV") ||
                        name.startsWith("BOSSBACK") ||
                        name.startsWith("END")
                        
                  ) {
                    lump = new PictureLump(fc, name, filePos, lumpSize/*, wad.getPlayPalLump().paletteList*/);
                    logger.config(lump.toString());
                } else {
                    lump = LumpFactory.getLump(fc, name, filePos, lumpSize);
                    logger.config(lump.toString());
                    
                    //  Shouldn't need this.  Throw exception for unknown lump.
                }
                
                // fc.position may have changed when loading lump
                fc.position(mark);   // Set fc back to where we were.
                                
                if ( lump != null ) {
                    wad.lumps.add(lump);
                }
            }
            
            logger.config("Finished Loading Lumps.\n\n\n\n\n\n\n\n\n\n");
            
            // Re-index the patch lump for correct patch order.
            wad.patchesLump.sortPatches(wad.pNamesLump);
        
            // Merge patches to create fullPatch for each texture.
            if (textureLump1 != null) {
                logger.config("Merge Texture1 Lump Textures.");
                for (MapTexture t : textureLump1.textures) {
                    t.merge(wad);
                }
            }
            // Merge patches to create fullPatch for each texture.
            if (textureLump2 != null) {
                logger.config("Merge Texture2 Lump Textures.");
                for (MapTexture t : textureLump2.textures) {
                    t.merge(wad);
                }
            }
            
            
            // TODO:   logger of wad.lumps.toString() for summary of lumps.
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WadLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WadLoader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (fc != null ) {
                    fc.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(WadLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        // Init stuff for each map.
        wad.mapLumps.stream().forEach((map) -> {
            map.init();
        });
        
        
        return wad;
    }

//    public Wad getWad() {
//        return wad;
//    }
}
