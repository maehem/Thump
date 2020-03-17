/*
        HELP1           Ad-screen says Register!, with some screen shots.
        HELP2           Actual help, all the controls explained.
        TITLEPIC        Maybe this is the title screen? Gee, I dunno...
        CREDIT          People at id Software who created this great game.
        VICTORY2  r     Screen shown after a victorious end to episode 2.
        PFUB1     r     A nice little rabbit minding his own peas and queues...
        PFUB2     r     ...a hint of what's waiting in Doom 2.
 */
package thump.wad.lump;

import java.awt.Color;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import thump.render.Patch;

/**
 *
 * @author mark
 */
public class PictureLump extends Lump {
    public final Patch pic;
    
    public PictureLump(FileChannel fc, String name, int filepos, int size, ArrayList<Color[]> paletteList) throws IOException {
        super(name, filepos, size);
        
        
        fc.position(filepos);
        // Load up BB
        ByteBuffer bb = ByteBuffer.allocate(size);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        fc.read(bb);
        bb.position(0);

        pic = new Patch(name, bb, paletteList);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("PictureLump:\n");
        
        sb.append(pic.name).append("\n");
        sb.append(pic.toString());
        
        return sb.toString(); //To change body of generated methods, choose Tools | Templates.
    }
   
}
