/*
 * Map Node - Redundant of Node
 */
package thump.maplevel;

import thump.render.BoundingBox;
import thump.render.Node;

/**
 *
 * @author mark
 */
public class MapNode extends Node {
    
    public MapNode(short x, short y, short xc, short yc, BoundingBox bbRight, BoundingBox bbLeft, short cr, short cl) {
        super(x, y, xc, yc, bbRight, bbLeft, cr, cl);
    }
    
}
