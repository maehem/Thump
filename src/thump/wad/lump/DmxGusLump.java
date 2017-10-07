/*
 *     DMXGUS  -- Not supported by this port.
 */
package thump.wad.lump;

import java.nio.channels.FileChannel;

/**
 *
 * @author mark
 */
public class DmxGusLump extends Lump {
    
    public DmxGusLump(FileChannel fc, String name, int filepos, int size) {
        super(name, filepos, size);
    }

}
