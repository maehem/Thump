/*
    Each sector has a degenmobj_t in its center for sound origin purposes.
    I suppose this does not handle sound from moving
    objects  (doppler), because position is prolly just 
    buffered, not updated.
 */
package thump.render;

/**
 *
 * @author mark
 */
public class Degenmobj {
    public int x=0;
    public int y=0;
    public int z=0;
    
    //public Thinker thinker=null;
}
