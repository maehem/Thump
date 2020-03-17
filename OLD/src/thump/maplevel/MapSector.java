/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.maplevel;

import thump.render.Sector;

/**
 *
 * @author mark
 */
public class MapSector extends Sector {
    
    public MapSector(short floor, short ceiling, String floorText, String ceilingText, short light, short type, short tag) {
        super(floor, ceiling, floorText, ceilingText, light, type, tag);
    }
    
}
