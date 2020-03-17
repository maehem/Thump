/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.wad.lump;

import thump.wad.lump.Lump;
import thump.wad.lump.MysteryLump;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 *
 * @author mark
 */
public class LumpFactory {
    
    public static Lump getLump( FileChannel fc, String name, int filepos, int size ) throws IOException {
        return new MysteryLump(fc, name, filepos, size);        
    }
}
