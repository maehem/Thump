/*
 * Map Node - Redundant of Node
 */
package thump.game.maplevel;

import thump.game.Game;
import thump.wad.map.Node;
import static thump.wad.map.Node.NF_SUBSECTOR;

/**
 *
 * @author mark
 */
public class MapNode extends Node {
    //public final Node node;
//    public MapNode(int x, int y, int xc, int yc, BoundingBox bbRight, BoundingBox bbLeft, int cr, int cl) {
//        super(x, y, xc, yc, bbRight, bbLeft, cr, cl);
//    }
    
    public MapNode( Node node) {
        super( node );
    }
    

    //
    // R_PointInSubsector
    // TODO:  Move this to Game
    //
    public static MapSubSector R_PointInSubsector(
            int x,
            int y               ) {
        MapNode node;
        int side;
        int nodenum;

        Game game = Game.getInstance();
        
        // single subsector is a special case
        //if (game.playerSetup.nodes.length==0) {
        if (game.playerSetup.nodes.size()==0) {
            //return game.playerSetup.subsectors[0];
            return game.playerSetup.subsectors.get(0);
        }

        //nodenum = game.playerSetup.nodes.length-1;
        nodenum = game.playerSetup.nodes.size()-1;

        while ( (nodenum & NF_SUBSECTOR)==0 ) {
            //node = game.playerSetup.nodes[nodenum];
            node = game.playerSetup.nodes.get(nodenum);
            //side = R_PointOnSide (x, y, node)?1:0;
            side = node.R_PointOnSide(x, y)?1:0;
            nodenum = node.children[side];
        }

        //return game.playerSetup.subsectors[(nodenum&0xFFFF) & ~NF_SUBSECTOR];
        return game.playerSetup.subsectors.get((nodenum&0xFFFF) & ~NF_SUBSECTOR);
    }



}
