/*
 * Map is zero bytes but denotes the beginning of a required series of
 * lumps needed for the level shown here in our Fields.
 */
package thump.wad.lump;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 *
 * @author mark
 */
public class MapLump extends Lump {
    public int scale = 120000;
    public int margin = 20;

    private ThingsLump      things      = null;
    private LineDefsLump    lineDefs    = null;
    private SideDefsLump    sideDefs    = null;
    private VertexesLump    vertexes    = null;
    private SegsLump        segs        = null;
    private SubSectorsLump  ssectors    = null;
    private NodesLump       nodes       = null;
    private SectorsLump     sectorsLump = null;
    private RejectLump      rejects     = null;
    private BlockMapLump    blockMap    = null;
    
    
    public MapLump(FileChannel fc, String name, int filepos, int size) throws IOException {
        
        
        super(name, filepos, size);
        //loadData(fc);
    }

    /**
     * @return the things
     */
    public ThingsLump getThings() {
        return things;
    }

    /**
     * @param things the things to set
     */
    public void setThings(ThingsLump things) {
        this.things = things;
    }

    /**
     * @return the lineDefs
     */
    public LineDefsLump getLineDefs() {
        return lineDefs;
    }

    /**
     * @return the sideDefs
     */
    public SideDefsLump getSideDefs() {
        return sideDefs;
    }

    /**
     * @return the vertexes
     */
    public VertexesLump getVertexes() {
        return vertexes;
    }

    /**
     * @return the segs
     */
    public SegsLump getSegs() {
        return segs;
    }

    /**
     * @return the ssectors
     */
    public SubSectorsLump getSubSectors() {
        return ssectors;
    }

    /**
     * @return the nodes
     */
    public NodesLump getNodes() {
        return nodes;
    }

    /**
     * @return the sectorsLump
     */
    public SectorsLump getSectorsLump() {
        return sectorsLump;
    }

    /**
     * @return the rejects
     */
    public RejectLump getRejects() {
        return rejects;
    }

    /**
     * @return the blockMap
     */
    public BlockMapLump getBlockMap() {
        return blockMap;
    }

    /**
     * @param lineDefs the lineDefs to set
     */
    public void setLineDefs(LineDefsLump lineDefs) {
        this.lineDefs = lineDefs;
    }

    /**
     * @param sideDefs the sideDefs to set
     */
    public void setSideDefs(SideDefsLump sideDefs) {
        this.sideDefs = sideDefs;
    }

    /**
     * @param vertexes the vertexes to set
     */
    public void setVertexes(VertexesLump vertexes) {
        this.vertexes = vertexes;
    }

    /**
     * @param segs the segs to set
     */
    public void setSegs(SegsLump segs) {
        this.segs = segs;
    }

    /**
     * @param ssectors the ssectors to set
     */
    public void setSubSectors(SubSectorsLump ssectors) {
        this.ssectors = ssectors;
    }

    /**
     * @param nodes the nodes to set
     */
    public void setNodes(NodesLump nodes) {
        this.nodes = nodes;
    }

    /**
     * @param sectorsLump the sectorsLump to set
     */
    public void setSectorsLump(SectorsLump sectorsLump) {
        this.sectorsLump = sectorsLump;
    }

    /**
     * @param rejects the rejects to set
     */
    public void setRejects(RejectLump rejects) {
        this.rejects = rejects;
    }

    /**
     * @param blockMap the blockMap to set
     */
    public void setBlockMap(BlockMapLump blockMap) {
        this.blockMap = blockMap;
    }

//    public void setLineDefs(Lump lump) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }

    public void init() {
        // note: most of this ordering is important	
        //P_LoadBlockMap (lumpnum+ML_BLOCKMAP);
        getBlockMap().init();
        
        //P_LoadVertexes (lumpnum+ML_VERTEXES);  //lump.vertexesLump
        getVertexes().init();
        
        //P_LoadSectors (lumpnum+ML_SECTORS);
        getSectorsLump().init();
        
        //P_LoadSideDefs (lumpnum+ML_SIDEDEFS);
        getSideDefs().init();

        //P_LoadLineDefs (lumpnum+ML_LINEDEFS);
        getLineDefs().initVertexes();
        
        //P_LoadSubsectors (lumpnum+ML_SSECTORS);
        getSubSectors().init();
        
        //P_LoadNodes (lumpnum+ML_NODES);
        getNodes().init();
        //P_LoadSegs (lumpnum+ML_SEGS);
        getSegs().init();
    }

    /**
     * Draw the map and details into an image.
     * @return 
     */
    public Image getImage() {
        // Get a base image with lines and vertex drawn.
        Image img = this.lineDefs.getImage(1200);
        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null)+(2*this.margin), img.getHeight(null)+(2*this.margin), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.setColor(Color.BLACK);
        bGr.fillRect(0, 0, bimage.getWidth(), bimage.getHeight());
        bGr.setStroke(new BasicStroke(5));
        bGr.drawRect(2, 2, bimage.getWidth()-2, bimage.getHeight()-2);
        bGr.setColor(Color.BLUE);
        bGr.drawImage(img, this.margin, this.margin, null);
        
        // Draw
        bGr.dispose();

        return bimage;        
    }

    
}
