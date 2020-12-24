/*
        Offset	Size (bytes)	Description
        0	2	Floor height
        2	2	Ceiling height
        4	8	Name of floorheight texture
        12	8	Name of ceilingheight texture
        20	2	Light level
        22	2	Type/Special
        24	2	Tag number
 */
package thump.wad.map;

import java.util.logging.Level;
import thump.base.BoundingBox;
import static thump.base.Defines.logger;
import static thump.base.FixedPoint.FRACBITS;
//import thump.game.Game;
//import thump.game.MapObject;
import thump.wad.Wad;

/**
 *
 * @author mark
 */
public class Sector {
    public int  floorheight;
    public int  ceilingheight;
    public final String floorpic;
    public final String ceilingpic;
    public int  lightlevel;
    public int  special;
    public short  tag;
    public Degenmobj	thinglist;    // list of mobjs in sector
    
    public int          soundtraversed;     // 0 = untraversed, 1,2 = sndlines -1
    // soundtarget moved to MapSector
    public BoundingBox  blockbox = new BoundingBox(0,0,0,0);    // mapblock bounding box for height changes
    
    public int		validcount;    // if == validcount, already checked
    // thinglist moved to MapSector
    public Object	specialdata;    // thinker_t for reversable actions
    public int          linecount;
    public Line[]       lines;	// [linecount] size
    
    //public Object    soundorg;    // MapObject origin for any sounds played by the sector
    private int floorpicNum = -1;
    private int ceilingpicNum = -1;

    public Sector(short floor, short ceiling, String floorText, String ceilingText, short light, short type, short tag) {
        this.floorheight = floor<<FRACBITS;
        this.ceilingheight = ceiling<<FRACBITS;
        this.floorpic = floorText;
        this.ceilingpic = ceilingText;
        this.lightlevel = light;
        this.special = type;
        this.tag = tag;
        
//        // If original values were 32-bit
//        if ( (floorheight & 0x8000000) > 0 ) {
//            floorheight = -(floorheight&0x7FFFFFFF);
//        }
        //logger.log(Level.CONFIG, "floorheight:{0}", Integer.toHexString(floorheight));
    }
    
//    public Sector( Sector sector ) {
//        this.floorheight = sector.floorheight;
//        this.ceilingheight = sector.ceilingheight;
//        this.floorpic = sector.floorpic;
//        this.ceilingpic = sector.ceilingpic;
//        this.lightlevel = sector.lightlevel;
//        this.special = sector.special;
//        this.tag = sector.tag;
//        this.thinglist = sector.thinglist;
//    }
    
    public int getFloorPic(Wad wad) {
        if (floorpicNum == -1 ) {
            //floorpicNum =  Game.getInstance().wad.getFlats().getNumForName(floorpic);
            floorpicNum = wad.getFlats().getNumForName(floorpic);
        }
        return floorpicNum;
    }
    
    public void setFloorPic(int num) {
        this.floorpicNum = num;
    }
    
    public int getCeilingPic(Wad wad) {
        if (ceilingpicNum == -1) {
            //ceilingpicNum = Game.getInstance().wad.getFlats().getNumForName(ceilingpic);
            ceilingpicNum = wad.getFlats().getNumForName(ceilingpic);
        } 
        
        return ceilingpicNum;
    }
    
    public void setCeilingPic( int num ) {
        this.ceilingpicNum = num;
    }
    
    @Override
    public String toString() {
        return  "node: floorH:" + Integer.toHexString(floorheight) + " [" + floorheight + "] " +
                " ceilH:" + Integer.toHexString(ceilingheight) + " [" + ceilingheight + "] " +
                " flTxt:" + floorpic + "    ceilTxt:" + ceilingpic +
                " light:" + lightlevel + "    type:" + special +
                " tag:" + tag
                ;
    }
    
}
