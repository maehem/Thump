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
package thump.render;

import thump.game.Game;
import static thump.global.FixedPoint.FRACBITS;
import thump.maplevel.MapObject;

/**
 *
 * @author mark
 */
public class Sector {
    public int  floorheight;
    public  int  ceilingheight;
    public final String floorpic;
    public final String ceilingpic;
    public int  lightlevel;
    public int  special;
    public short  tag;
    
    public int          soundtraversed;     // 0 = untraversed, 1,2 = sndlines -1
    public MapObject	soundtarget;    // thing that made a sound (or null)
    public BoundingBox  blockbox = new BoundingBox(0,0,0,0);    // mapblock bounding box for height changes
    public Degenmobj    soundorg = new Degenmobj();    // origin for any sounds played by the sector
    public int		validcount;    // if == validcount, already checked
    public MapObject	thinglist;    // list of mobjs in sector
    public Object	specialdata;    // thinker_t for reversable actions
    public int          linecount;
    public Line[]       lines;	// [linecount] size
    
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
    }
    
    public int getFloorPic() {
        if (floorpicNum == -1 ) {
            floorpicNum =  Game.getInstance().wad.getFlats().getNumForName(floorpic);
        }
        return floorpicNum;
    }
    
    public void setFloorPic(int num) {
        this.floorpicNum = num;
    }
    
    public int getCeilingPic() {
        if (ceilingpicNum == -1) {
            ceilingpicNum = Game.getInstance().wad.getFlats().getNumForName(ceilingpic);
        } 
        
        return ceilingpicNum;
    }
    
    public void setCeilingPic( int num ) {
        this.ceilingpicNum = num;
    }
    
    @Override
    public String toString() {
        return  "node: floorH:" + floorheight + "  ceilH:" + ceilingheight +
                "    flTxt:" + floorpic + "    ceilTxt:" + ceilingpic +
                "    light:" + lightlevel + "    type:" + special +
                "    tag:" + tag
                ;
    }
    
}
