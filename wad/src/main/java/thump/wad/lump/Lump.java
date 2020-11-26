/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.wad.lump;

/**
 *
 * @author mark
 */
public abstract class Lump {
        public final int     filepos;  // Position in the WAD file.
        public final int     size;     // Size of the lump in the WAD file.
        public final String  name;     // Name of the lump.

    public Lump(String name, int filepos, int size ) {
        this.filepos = filepos;
        this.size = size;
        this.name = name;
    }
        
}
