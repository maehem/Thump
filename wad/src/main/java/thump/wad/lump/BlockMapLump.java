/*
        <BLOCKMAP>      := <xorigin>            ;<short>
                           <yorigin>            ;<short>
                           <xblocks>            ;<short>
                           <yblocks>            ;<short>
                           <listoffsets>
                           <blocklists>
        <listoffsets>   := <listoffset> [numofblocks]
        <listoffset>    := <ushort>
        <numofblocks>   := <short>              ;note it equals <xblocks> * <yblocks>
        <blocklists>    := <blocklist> [numofblocks]
        <blocklist>     := <short: 0>           ;for dynamic thinglist pointer
                           <lines_in_block>
                           <short: -1>
        <lines_in_block>:= <linedef_num> [...]  ;the numbers of all the <linedef>s
                                                ;that are in the block
        <linedef_num>   := <short>

 */
package thump.wad.lump;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static thump.base.FixedPoint.FRACBITS;

/**
 *
 * @author mark
 */
public class BlockMapLump extends Lump {

    private static final Logger logger = Logger.getGlobal();

    public final int xOrigin;
    public final int yOrigin;
    public final short xBlocks;
    public final short yBlocks;
    public final int[] offsetList; // Size is numBlocks
    public final short numBlocks;
    public final Short[][] blockLists;  // aka blocklinks, point to Mobj's

    public BlockMapLump(FileChannel fc, String name, int filepos, int size) throws IOException {
        super(name, filepos, size);
        fc.position(filepos);
        ByteBuffer bb = ByteBuffer.allocate(size);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        fc.read(bb);
        bb.position(0);

        xOrigin = bb.getShort()<<FRACBITS;
        yOrigin = bb.getShort()<<FRACBITS;
        xBlocks = bb.getShort();
        yBlocks = bb.getShort();
        numBlocks = (short)(xBlocks * yBlocks);
        offsetList = new int[numBlocks];
        for (int i = 0; i < offsetList.length; i++) {
            // Old DOOM had index from lump start. But we don't need that now.
            // The first index value starts after the lump x/y vals and offset list.
            offsetList[i] = (bb.getShort() & 0x0000FFFF)-offsetList.length-4;
        }
        // Read offsetList
        blockLists = new Short[numBlocks][];
        int position = bb.position();        
        
        // Simulation uses the block links in the WAD.
        // Game zeros them out.
        for ( int i=0; i< numBlocks; i++) {
                ArrayList<Short> blockList = new ArrayList<>();
                bb.position(position+offsetList[i]*2);  // offset is measured in shorts
                logger.log(Level.FINEST, "    read block {0} at offset:{1}   ", new Object[]{i, bb.position()});
                short block = 0;
                do {
                    //try{
                    block = bb.getShort();
                    //logger.log(Level.CONFIG, "    {0}", Integer.toHexString(block&0xFFFF));
                    //} catch (java.nio.BufferUnderflowException e) {
                    //    int ii=0;
                    //}
                    blockList.add(block);
                } while ( block != -1 );

                Short t[] = new Short[blockList.size()];
                blockLists[i]= blockList.toArray(t);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("BlockMap: \n");
        sb.append("    xO:").append(xOrigin>>FRACBITS);
        sb.append("    yO:").append(yOrigin>>FRACBITS);
        sb.append("    xB:").append(xBlocks);
        sb.append("    yB:").append(yBlocks);
        sb.append("\n");

        sb.append("    offsetList.length: ").append(offsetList.length);
        sb.append("\n    ");
        for (int i = 0; i < offsetList.length; i++) {

            sb.append(String.format("%04d ", offsetList[i] & 0xFFFF));
            if ( (i+1)%xBlocks == 0 ) {
                sb.append("\n    ");
            }
        }
        sb.append("\n");
        
        sb.append("           numBlocks: ").append(numBlocks).append("\n");
        sb.append("    blockList Length: ").append(blockLists.length).append("\n");
        for (int i = 0; i < blockLists.length; i++) {
            Short[] list = blockLists[i];
            sb.append("    ").append(i).append(": [");
            for (Short list1 : list) {
                if ( list1 != -1) {
                     sb.append(String.format("%04X ", list1 & 0xFFFF));
                } else {
                     sb.append("]");
                }
            }
            sb.append("\n");
        }
        return sb.toString(); //To change body of generated methods, choose Tools | Templates.
    }

    void init() {
//    int		i;
//    int		count;
//	
//    blockmaplump = W_CacheLumpNum (lump,PU_LEVEL);
//    blockmap = blockmaplump+4;
//    count = W_LumpLength (lump)/2;
//
//    for (i=0 ; i<count ; i++)
//	blockmaplump[i] = SHORT(blockmaplump[i]);
//		
//    bmaporgx = blockmaplump[0]<<FRACBITS;
//    bmaporgy = blockmaplump[1]<<FRACBITS;
//    bmapwidth = blockmaplump[2];
//    bmapheight = blockmaplump[3];
//	
//    // clear out mobj chains
//    count = sizeof(*blocklinks)* bmapwidth*bmapheight;
//    blocklinks = Z_Malloc (count,PU_LEVEL, 0);
//    memset (blocklinks, 0, count);
    }

}
