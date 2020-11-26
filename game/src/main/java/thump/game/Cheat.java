/*
 * Cheat Utils -  m_cheat
 */
package thump.game;

import java.util.Arrays;

/**
 *
 * @author mark
 */
public class Cheat {
    
    int[]	sequence;
    int[]	p;

    public Cheat(int[] sequence) {
        this.sequence = sequence;
        this.p = new int[sequence.length];
        
        if (firsttime) {
            firsttime = false;
            for (int i=0;i<256;i++) {
                cheat_xlate_table[i] = SCRAMBLE((byte) i);
            }
        }

    }
    
    static boolean	firsttime = true;
    static byte         cheat_xlate_table[] = new byte[256];

    // makes me want breakfast real bad.
    public static byte SCRAMBLE(byte aa) {
        int a = aa&0xff;
        return (byte)((  
                  (((a)&1  )<<7) 
                + (((a)&2  )<<5) 
                + ( (a)&4  ) 
                + (((a)&8  )<<1) 
                + (((a)&16 )>>1) 
                + ( (a)&32 ) 
                + (((a)&64 )>>5) 
                + (((a)&128)>>7) 
              )&0xff);
    }
    


    //
    // Called in st_stuff module, which handles the input.
    // Returns a 1 if the cheat was successful, 0 if failed.
    //
    public boolean cht_CheckCheat( int key ) {
        boolean rc = false;
        int pi = 0;

        if (p == null) {
            p = Arrays.copyOf(sequence, sequence.length); // initialize if first time
        }

        if (p[pi] == 0) {
            p[pi] = key;
            pi++;
        } else if (cheat_xlate_table[key] == p[pi]) {
            pi++;
        } else {
            p = Arrays.copyOf(sequence, sequence.length);
            pi = 0;
        }

        if (p[pi] == 1) {
            pi++;
        } else if (p[pi] == 0xff) {    // end of sequence character
            p = Arrays.copyOf(sequence, sequence.length);
            rc = true;
        }

        return rc;
    }

   public void cht_GetParam( int[] buffer ) {

        int bi = 0;
        int i=0;
        int[] pp;
        int c;

        pp = Arrays.copyOf(sequence, sequence.length);
    
        while (pp[i] != 1) {
            i++;
        }

        do {
            c = pp[i];
            buffer[bi] = c;
            bi++;
            pp[i] = 0;
            i++;
        } while (c!=0 && pp[i]!=0xff );

        if (pp[i]==0xff) {
            buffer[bi] = 0;
        }

    }

}
