/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.wad.lump;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

/**
 *
 * @author mark
 */
public class MysteryLump extends Lump {
    private String header;
    
    public MysteryLump(FileChannel fc, String name, int filepos, int size) throws IOException {
        super(name, filepos, size);
        
        fc.position(filepos);
        ByteBuffer bb = ByteBuffer.allocate(size);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        fc.read(bb);
        bb.position(0);

        int bSize = 24;
        if ( size < 24 ) {
            bSize = size;
        }
        byte[] hBytes = new byte[bSize];
        bb.get(hBytes);
        header = new String(hBytes);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("MysteryLump:\n    ");
        sb.append("    name:").append(name).append("   fpos:").append(this.filepos).append("   size:").append(this.size).append("\n");
        sb.append(header).append("\n");
        
        return sb.toString();
    }
    
}
