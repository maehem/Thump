/*
     A SubSector.
     References a Sector.
     Basically, this is a list of LineSegs,
      indicating the visible walls that define
      (all or some) sides of a convex BSP leaf.


        Offset	Size (bytes)	Description
        0	2	Seg numlines
        2	2	First seg number
 */
package thump.render;

/**
 *
 * @author mark
 */
public class SubSector {
    public final short numlines;
    public final short firstline;
    public Sector       sector;   // Not used by MapSubSector

    public SubSector(short count, short first) {
        this.numlines = count;
        this.firstline = first;
    }
    
    @Override
    public String toString() {
        return  "ssector: count:" + numlines + "  first:" + firstline;
    }
    
}
