/*
Level thing data is stored in the THINGS lump. Each entry is 10 bytes long. This format is used by both Doom and Heretic.

Offset	Size (bytes)	Description
0	2	x position
2	2	y position
4	2	Angle facing
6	2	DoomEd thing type
8	2	Flags

Flags
bit	hex	description
0	0x0001	MapThing is on skill levels 1 & 2
1	0x0002	MapThing is on skill level 3
2	0x0004	MapThing is on skill levels 4 & 5
3	0x0008	MapThing is waiting in ambush. Commonly known as "deaf" flag.
In fact, it does not render monsters deaf per se.
4	0x0010	MapThing is not in single player

 */
package thump.wad.lump;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import thump.wad.mapraw.MapThing;

/**
 *
 * @author mark
 */
public class ThingsLump extends Lump {

    private final ArrayList<MapThing> thingList = new ArrayList<>();
    
    public ThingsLump(FileChannel fc, String name, int filepos, int size) throws IOException {
        super(name, filepos, size);

        fc.position(filepos);
        ByteBuffer bb = ByteBuffer.allocate(size);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        fc.read(bb);
        bb.position(0);
        
        for (int i = 0; i < size / 10; i++) {
            thingList.add(new MapThing(bb.getShort(), bb.getShort(),bb.getShort(),bb.getShort(),bb.getShort()));
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Things:\n");
        
        Iterator<MapThing> it = thingList.iterator();
        int index=0;
        while (it.hasNext()) {
            sb.append("    ").append(index).append("::").append(it.next().toString()).append("\n");
            index++;
        }
        
        return sb.toString();
    }

    public MapThing[] toArray() {
        return thingList.toArray(new MapThing[thingList.size()]);
    }
}
