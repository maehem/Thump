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
import java.util.ArrayList;
import java.util.ListIterator;
import thump.wad.map.Flat;

/**
 *
 * @author mark
 */
public class FlatsLump extends Lump {
    public final ArrayList<Flat> flats = new ArrayList<>();

    public FlatsLump(FileChannel fc, String name, int filepos, int size) {
        super(name, filepos, size);
    }
    
    public Flat addFlat(FileChannel fc, String name, int filePos, int lumpSize) throws IOException {
        fc.position(filePos);
        // Load up BB
        ByteBuffer bb = ByteBuffer.allocate(lumpSize);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        fc.read(bb);
        bb.position(0);

        Flat flat = new Flat(name, bb);
        flats.add(flat);
    
        return flat;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Flats:\n");
        
        for ( int i=0; i<flats.size(); i++) {
            sb.append(flats.get(i).name).append("  ");
            if ( (i+1)% 10 == 0 ) {
                sb.append("\n");
            }
        }
        return sb.toString(); //To change body of generated methods, choose Tools | Templates.
    }
    
    public ListIterator<Flat> getFlats() {
        return flats.listIterator();
    }
    
    public int getNumForName( String name ) {
        for( int i=0; i< flats.size(); i++ ) {
            Flat f = flats.get(i);
            if ( name.equals(f.name)) {
                return i;
            }
        }
        
        return -1;
    }
    
    public Flat getFlatByName( String pName ) {
        ListIterator<Flat> flatList = getFlats();
        
        while ( flatList.hasNext() ) {
            Flat p = flatList.next();
            if (p.name.equals(pName) ) {
                return p;
            }
        }
        
        return null;
    }
    
    public Flat get( int num ) {
        return flats.get(num);
    }
}
