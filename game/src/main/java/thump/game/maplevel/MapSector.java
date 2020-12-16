/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.game.maplevel;

import thump.wad.map.Sector;

/**
 *
 * @author mark
 */
public class MapSector /*extends Sector*/ {
    public final Sector sector;
    public MapObject	soundtarget;  // thing that made a sound (or null)

    public MapObject    soundorg = new MapObject();    // origin for any sounds played by the sector
     //public MapObject	thinglist;    // list of mobjs in sector

    
//    public MapSector(short floor, short ceiling, String floorText, String ceilingText, short light, short type, short tag) {
//        super(floor, ceiling, floorText, ceilingText, light, type, tag);
//    }
    
    public MapSector(Sector sector) {
        //super(sector);
        this.sector = sector;
        //this.soundorg = new MapObject();
    }
}
