/*
        byte    range   purpose

        0       104-106 version. 104=1.4 beta, 105=1.5 beta, 106=1.6 beta or 1.666
        1       0-4     skill level. 0="I'm too young to die", 4="Nightmare!"
        2       1-3     episode. In DOOM 2 this is always 1.
        3       1-32    mission/map/level. In DOOM 1, it's 1-9. In DOOM 2, it's 1-32.
        4       0-2     mode. 0=single or cooperative, 1=deathmatch, 2=altdeath
        5       0-      respawn. 0=no respawn parameter, (any other value)=respawn.
        6       0-      fast. 0=no fast parameter, (any other value)=fast.
        7       0-      nomonsters. 0=monsters exist, (any other value)=nomonsters.
        8       0-3     viewpoint. 0=player 1's status bar, ..., 3=player 4.
        9       0-1     player 1 is present if this is 1.
        10 0x0a 0-1     player 2.
        11 0x0b 0-1     player 3.
        12 0x0c 0-1     player 4.

        (2) Move data  ( four byte chunks )  see @Move
        (3) The last byte of a demo has the value 128 (0x80)
 */
package thump.render;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import thump.wad.lump.Lump;
import thump.wad.Move;

/**
 *
 * @author mark
 */
public class DemoLump extends Lump {
    public final byte version;  //        0       104-106 version. 104=1.4 beta, 105=1.5 beta, 106=1.6 beta or 1.666
    public final byte skill;    //        1       0-4     skill level. 0="I'm too young to die", 4="Nightmare!"
    public final byte episode;  //        2       1-3     episode. In DOOM 2 this is always 1.
    public final byte mission;  //        3       1-32    mission/map/level. In DOOM 1, it's 1-9. In DOOM 2, it's 1-32.
    public final byte mode;     //        4       0-2     mode. 0=single or cooperative, 1=deathmatch, 2=altdeath
    public final byte respawn;  //        5       0-      respawn. 0=no respawn parameter, (any other value)=respawn.
    public final byte fast;     //        6       0-      fast. 0=no fast parameter, (any other value)=fast.
    public final byte nomonsters;//       7       0-      nomonsters. 0=monsters exist, (any other value)=nomonsters.
    public final byte viewpoint;//        8       0-3     viewpoint. 0=player 1's status bar, ..., 3=player 4.
    public final byte player1;  //        9       0-1     player 1 is present if this is 1.
    public final byte player2;  //        10 0x0a 0-1     player 2.
    public final byte player3;  //        11 0x0b 0-1     player 3.
    public final byte player4;  //        12 0x0c 0-1     player 4.
    
    public final Move move[];
    
    public DemoLump(FileChannel fc, String name, int filepos, int size) throws IOException {
        super(name, filepos, size);
        
        fc.position(filepos);
        ByteBuffer bb = ByteBuffer.allocate(size);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        fc.read(bb);
        bb.position(0);
        
        version = bb.get();
        skill = bb.get();
        episode = bb.get();
        mission = bb.get();
        mode = bb.get();
        respawn = bb.get();
        fast = bb.get();
        nomonsters = bb.get();
        viewpoint = bb.get();
        player1 = bb.get();
        player2 = bb.get();
        player3 = bb.get();
        player4 = bb.get();
        
        move = new Move[(size-bb.position()-1)/4];
        for (int i=0; i< move.length; i++ ) {
            move[i] = new Move(bb.getInt());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder( "Demo:" ) ;
        sb.append("   name:").append(name);        
        sb.append("   filePos:").append(filepos);
        sb.append("   size:").append(size);
        sb.append("\n");
        
        sb.append("   ve:").append(version);        
        sb.append("   sk:").append(skill);        
        sb.append("   ep:").append(episode);        
        sb.append("   mi:").append(mission);        
        sb.append("   mo:").append(mode);        
        sb.append("   re:").append(respawn);        
        sb.append("   fa:").append(fast);        
        sb.append("   nm:").append(nomonsters);        
        sb.append("   vp:").append(viewpoint);        
        sb.append("   p1:").append(player1);        
        sb.append("   p2:").append(player2);        
        sb.append("   p3:").append(player3);        
        sb.append("   p4:").append(player4); 
        sb.append("\n");

        for (Move m : move) {
            sb.append(m.toString());
            sb.append("\n");
        }

        return sb.toString();
    }
    
    
}
