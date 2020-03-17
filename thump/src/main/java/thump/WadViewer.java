/*
 * WAD viewer/browser.  Maybe someday, editor?
 */
package thump;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.util.ListIterator;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.swing.JPanel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import static thump.global.Defines.logger;
import thump.maplevel.MapTexture;
import thump.render.Flat;
import thump.render.Patch;
import thump.wad.Wad;
import thump.wad.WadLoader;
import thump.wad.lump.EndDoomLump;
import thump.wad.lump.FlatsLump;
import thump.wad.lump.LineDefsLump;
import thump.wad.lump.Lump;
import thump.wad.lump.MapLump;
import thump.wad.lump.MusicLump;
import thump.wad.lump.PatchesLump;
import thump.wad.lump.PictureLump;
import thump.wad.lump.PlaypalLump;
import thump.wad.lump.SoundEffectLump;
import thump.wad.lump.SpritesLump;
import thump.wad.lump.TextureLump;
import thump.wad.lump.ThingsLump;

/**
 *
 * @author mark
 */
@SuppressWarnings("serial")
public class WadViewer extends javax.swing.JFrame implements TreeSelectionListener {

    private final String wadFile = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "Doom" + File.separator + "doom2.wad";
    private final Wad wad;
    private final ImagePanel imagePreviewPanel;
    private Sequencer sequencer = null;

    /**
     * Creates new form WadViewer
     */
    public WadViewer() {
        initComponents();
        setLoggerLevel();

        wad = WadLoader.getWad(new File(wadFile));

        // Build Tree Model
        DefaultMutableTreeNode top
                = new DefaultMutableTreeNode(wad.identification);
        generateTree(top);
        wadTree.setModel(new DefaultTreeModel(top));
        wadTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        wadTree.addTreeSelectionListener(this);
        imagePreviewPanel = new ImagePanel();
        imagePanel.add(imagePreviewPanel);

    }

    private void generateTree(DefaultMutableTreeNode t) {
        DefaultMutableTreeNode mapTop = null;
        DefaultMutableTreeNode musicTop = null;
        DefaultMutableTreeNode pcSoundTop = null;
        DefaultMutableTreeNode scSoundTop = null;
        DefaultMutableTreeNode menusTop = null;
        DefaultMutableTreeNode statusTop = null;
        DefaultMutableTreeNode miscTop = null;
        DefaultMutableTreeNode spritesTop = null;
        DefaultMutableTreeNode patchesTop = null;
        DefaultMutableTreeNode flatsTop = null;
        DefaultMutableTreeNode texture1Top = null;
        DefaultMutableTreeNode texture2Top = null;

        for (Lump l : wad.lumps) {
            if (l.name.startsWith("MAP")) {
                // Create the Map top node at the right place.
                if (mapTop == null) {
                    mapTop = new DefaultMutableTreeNode("Maps");
                    t.add(mapTop);
                }
                mapTop.add(getMapNode(l));
            } else if (l.name.startsWith("S_START")) {
                if (spritesTop == null) {
                    spritesTop = getSpritesNode(l);
                    t.add(spritesTop);
                }
                //spritesTop.add(getSpritesNode(l));
            } else if (l.name.startsWith("P_START") ) {
                if (patchesTop == null) {
                    patchesTop = new DefaultMutableTreeNode("Patches");
                    t.add(patchesTop);
                }
                //patchesTop.add(getPatchesNode( l));  // Do like flats.
                getPatchesNode(l, patchesTop); 
            } else if (l.name.startsWith("F_START")
                    //|| l.name.startsWith("F2_START")
                    //|| l.name.startsWith("F3_START")
                    ) {
                if (flatsTop == null) {
                    flatsTop = new DefaultMutableTreeNode("Flats");
                    t.add(flatsTop);
                }
                //flatsTop.add(getFlatsNode(l));
                FlatsLump fl = (FlatsLump) l;
                ListIterator<Flat> flats = fl.getFlats();
                while (flats.hasNext()) {
                    flatsTop.add(new FlatNode(flats.next()));
                }
            } else if (l.name.startsWith("D_")) {
                // Create the Map top node at the right place.
                if (musicTop == null) {
                    musicTop = new DefaultMutableTreeNode("Music");
                    t.add(musicTop);
                }
                musicTop.add(new MusicNode((MusicLump) l));
            } else if (l.name.startsWith("DP")) {
                // Create the Map top node at the right place.
                if (pcSoundTop == null) {
                    pcSoundTop = new DefaultMutableTreeNode("PC Sound");
                    t.add(pcSoundTop);
                }
                pcSoundTop.add(new LumpNode(l));
            } else if (l.name.startsWith("DS")) {
                // Create the Map top node at the right place.
                if (scSoundTop == null) {
                    scSoundTop = new DefaultMutableTreeNode("Sound Card Sound");
                    t.add(scSoundTop);
                }
                scSoundTop.add(new SoundNode((SoundEffectLump) l));
            } else if (l.name.startsWith("M_")) {
                // Create the Map top node at the right place.
                if (menusTop == null) {
                    menusTop = new DefaultMutableTreeNode("Menus");
                    t.add(menusTop);
                }
                menusTop.add(new LumpNode(l));
            } else if (l.name.startsWith("ST")) {
                // Create the Map top node at the right place.
                if (statusTop == null) {
                    statusTop = new DefaultMutableTreeNode("Status");
                    t.add(statusTop);
                }
                statusTop.add(new LumpNode(l));
            } else if (l.name.startsWith("TEXTURE1")) {
                if (texture1Top == null) {
                    texture1Top = new DefaultMutableTreeNode("Texture1");
                    t.add(texture1Top);
                    createTexturesNodes(texture1Top, l);
                }
            } else if (l.name.startsWith("TEXTURE2")) {
                if (texture2Top == null) {
                    texture2Top = new DefaultMutableTreeNode("Texture2");
                    t.add(texture2Top);
                    createTexturesNodes(texture2Top, l);
                }
            } else if (l.name.startsWith("WI")
                    || l.name.startsWith("AMMN")
                    || l.name.startsWith("BRDR")
                    || l.name.startsWith("CWILV")
                    || l.name.startsWith("BOSSBACK")) {
                // Create the Map top node at the right place.
                if (miscTop == null) {
                    miscTop = new DefaultMutableTreeNode("Misc. Images");
                    t.add(miscTop);
                }
                miscTop.add(new LumpNode(l));
            } else {
                t.add(new LumpNode(l));
            }
        }
    }

    private MutableTreeNode getMapNode(Lump l) {
        DefaultMutableTreeNode n = new LumpNode(l);

        MapLump ml = (MapLump) l;

        DefaultMutableTreeNode subNode;

        subNode = new LumpNode(ml.getBlockMap());
        n.add(subNode);

        subNode = new LumpNode(ml.getLineDefs());
        n.add(subNode);

        subNode = new LumpNode(ml.getNodes());
        n.add(subNode);

        subNode = new LumpNode(ml.getRejects());
        n.add(subNode);

        subNode = new LumpNode(ml.getSectorsLump());
        n.add(subNode);

        subNode = new LumpNode(ml.getSegs());
        n.add(subNode);

        subNode = new LumpNode(ml.getSideDefs());
        n.add(subNode);

        subNode = new LumpNode(ml.getSubSectors());
        n.add(subNode);

        subNode = new LumpNode(ml.getThings());
        n.add(subNode);

        subNode = new LumpNode(ml.getVertexes());
        n.add(subNode);

        return n;
    }

    @Override
    public void valueChanged(TreeSelectionEvent tse) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) wadTree.getLastSelectedPathComponent();
        if (sequencer != null && sequencer.isOpen()) {
            sequencer.stop();
        }
        //Logger.getLogger(WadViewer.class.getName()).log(Level.SEVERE, null, ex);
        if (node instanceof LumpNode) {
            Lump lump = ((LumpNode) node).getLump();
            if (lump instanceof PictureLump) {
                PictureLump pl = (PictureLump) lump;
                // Get the picture!
                imagePreviewPanel.setImage(pl.pic.getColorImage(0/*wad.getPlayPalLump().paletteList.get(0)*/));
            } else if (lump instanceof PlaypalLump) {
                PlaypalLump pl = (PlaypalLump) lump;
                imagePreviewPanel.setImage(pl.getPreviewImage());
            } else if ( lump instanceof EndDoomLump ) {
                EndDoomLump l = (EndDoomLump) lump;
                imagePreviewPanel.setImage(l.getAsImage(720,400));
            } else if ( lump instanceof LineDefsLump ) {
                LineDefsLump l = (LineDefsLump)lump;
                //imagePreviewPanel.setImage(l.getImage(2560));
            } else if ( lump instanceof MapLump ) {
                logger.log(Level.CONFIG, "Map Lump: {0} selected.\n", lump.name);
                MapLump l = (MapLump)lump;
                imagePreviewPanel.setImage(l.getImage());
            } else if ( lump instanceof ThingsLump ) {
                logger.log(Level.CONFIG, "{0}\n", lump.toString());
            } else {
                logger.log(Level.CONFIG, "{0}\n{1}\n", new Object[]{lump.name, lump.toString()});
            }

        } else if (node instanceof PictureNode) {
            Patch p = ((PictureNode) node).getPicture();
            imagePreviewPanel.setImage(p.getColorImage(0/*wad.getPlayPalLump().paletteList.get(0)*/));
        } else if (node instanceof FlatNode) {
            Flat f = ((FlatNode) node).getFlat();
            imagePreviewPanel.setImage(f.getColorImage(wad));
        } else if (node instanceof MapTextureNode) {
            MapTextureNode n = (MapTextureNode) node;
            imagePreviewPanel.setImage(n.getTexture().getImage());
        } else if (node instanceof MusicNode) {
            MusicLump lump = ((MusicNode) node).getLump();
            Synthesizer synthesizer = null;
            try {
                sequencer = MidiSystem.getSequencer();
                synthesizer = MidiSystem.getSynthesizer();
                synthesizer.open();
                sequencer.getTransmitter().setReceiver(synthesizer.getReceiver());
                sequencer.open();
                sequencer.stop();
                sequencer.setSequence(lump.sequence);
                // Start playing
                sequencer.start();
            } catch (MidiUnavailableException | InvalidMidiDataException ex) {
                Logger.getLogger(WadViewer.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if ( sequencer != null ) {
                    //sequencer.close();
                }
                if ( synthesizer != null ) {
                    //synthesizer.close();
                }
            }
        } else if (node instanceof SoundNode) {
            ((SoundNode) node).getLump().playSound();
        }
    }

    private DefaultMutableTreeNode getSpritesNode(Lump l) {
        DefaultMutableTreeNode n = new DefaultMutableTreeNode("Sprites");
        SpritesLump sl = (SpritesLump) l;
        ListIterator<Patch> sprites = sl.getSprites();
        while (sprites.hasNext()) {
            n.add(new PictureNode(sprites.next()));
        }

        return n;
    }

    private void createTexturesNodes( DefaultMutableTreeNode n, Lump l ) {
        // Add a node to n for each item on Lump.
        ((TextureLump)l).texture.stream().forEach((mt) -> {
            n.add(new MapTextureNode(mt));
        });
    }
    
    
    private DefaultMutableTreeNode getPatchesNode(Lump l, DefaultMutableTreeNode n) {
        // Add getPatches tp PatchesLump
        //DefaultMutableTreeNode n;
//        if (l.name.startsWith("P1_")) {
//            n = new DefaultMutableTreeNode("1");
//        } else if (l.name.startsWith("P2_")) {
//            n = new DefaultMutableTreeNode("2");
//        } else if (l.name.startsWith("P3_")) {
//            n = new DefaultMutableTreeNode("3");
//        } else {
//            n = new DefaultMutableTreeNode("patches");
//        }
        PatchesLump pl = (PatchesLump) l;
        ListIterator<Patch> patches = pl.getPatches().listIterator();
        while (patches.hasNext()) {
            n.add(new PictureNode(patches.next()));
        }

        return n;
    }

    private DefaultMutableTreeNode getFlatsNode(Lump l) {
        // Add getFlats to FlatsLump
        DefaultMutableTreeNode n;
        if (l.name.startsWith("F1_")) {
            n = new DefaultMutableTreeNode("1");
        } else if (l.name.startsWith("F2_")) {
            n = new DefaultMutableTreeNode("2");
        } else {
            n = new DefaultMutableTreeNode("3");
        } 
        FlatsLump fl = (FlatsLump) l;
        ListIterator<Flat> flats = fl.getFlats();
        while (flats.hasNext()) {
            n.add(new FlatNode(flats.next()));
        }

        return n;
    }

    private class ImagePanel extends JPanel {

        private Image image = null;

        public ImagePanel() {
//            try {
//                image = ImageIO.read(new File(System.getProperty("user.home") + File.separator + "test.jpg"));
                //this.setPreferredSize(new Dimension(1024, 1024));
//            } catch (IOException ex) {
//                Logger.getLogger(WadViewer.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
//                g.drawImage(
//                        //image.getScaledInstance(image.getWidth(this), image.getHeight(this), Image.SCALE_DEFAULT),
//                        image.getScaledInstance(image.getWidth(ImagePanel.this), image.getHeight(ImagePanel.this), Image.SCALE_DEFAULT),
//                        0, 0, null);
                g.drawImage(image, 0,0, this);
                
            }
        }

        public void setImage(Image image) {
            this.image = image;
            //ImagePanel.this.setPreferredSize(new Dimension(image.getWidth(this), image.getHeight(this)));
            //ImagePanel.this.setPreferredSize(jScrollPane1.getSize());
            //this.pack();
            ImagePanel.this.validate();
            imagePanel.setPreferredSize(new Dimension(image.getWidth(this), image.getHeight(this)));
            imagePanel.validate();
            jScrollPane1.revalidate();
            this.validate();
            jScrollPane1.repaint();
            repaint();
        }

    }

    @SuppressWarnings("serial")
    private class LumpNode extends DefaultMutableTreeNode {

        private final Lump lump;

        public LumpNode(Lump l) {
            this.lump = l;
        }

        @Override
        public String toString() {
            return lump.name;
        }

        public Lump getLump() {
            return lump;
        }
    }
    
    @SuppressWarnings("serial")
    private class MapTextureNode extends DefaultMutableTreeNode {

        private final MapTexture texture;

        public MapTextureNode(MapTexture mt) {
            this.texture = mt;
        }

        @Override
        public String toString() {
            return texture.name;
        }

        public MapTexture getTexture() {
            return texture;
        }
    }
    
    @SuppressWarnings("serial")
    private class SoundNode extends DefaultMutableTreeNode {

        private final SoundEffectLump lump;

        public SoundNode(SoundEffectLump l) {
            this.lump = l;
        }

        @Override
        public String toString() {
            return lump.name;
        }

        public SoundEffectLump getLump() {
            return lump;
        }
    }

    @SuppressWarnings("serial")
    private class PictureNode extends DefaultMutableTreeNode {

        private final Patch picture;

        public PictureNode(Patch p) {
            this.picture = p;
        }

        @Override
        public String toString() {
            return picture.name;
        }

        public Patch getPicture() {
            return picture;
        }
    }

    @SuppressWarnings("serial")
    private class FlatNode extends DefaultMutableTreeNode {

        private final Flat flat;

        public FlatNode(Flat p) {
            this.flat = p;
        }

        @Override
        public String toString() {
            return flat.name;
        }

        public Flat getFlat() {
            return flat;
        }
    }

    @SuppressWarnings("serial")
    private class MusicNode extends DefaultMutableTreeNode {

        private final MusicLump lump;

        public MusicNode(MusicLump l) {
            this.lump = l;
        }

        @Override
        public String toString() {
            return lump.name;
        }

        public MusicLump getLump() {
            return lump;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolBar = new javax.swing.JToolBar();
        jScrollPane1 = new javax.swing.JScrollPane();
        imagePanel = new javax.swing.JPanel();
        wadTreeScrollPanel = new javax.swing.JScrollPane();
        wadTree = new javax.swing.JTree();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1600, 1000));

        toolBar.setRollover(true);

        imagePanel.setLayout(new java.awt.BorderLayout());
        jScrollPane1.setViewportView(imagePanel);

        wadTreeScrollPanel.setViewportView(wadTree);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(wadTreeScrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 532, Short.MAX_VALUE)
                .addGap(14, 14, 14))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(wadTreeScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(WadViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new WadViewer().setVisible(true);
        });
    }
    
    private void setLoggerLevel() {
        logger.setLevel(Level.ALL);
        // Handler for console (reuse it if it already exists)
        Handler consoleHandler = null;
        //see if there is already a console handler
        for (Handler handler : logger.getHandlers()) {
            if (handler instanceof ConsoleHandler) {
                //found the console handler
                consoleHandler = handler;
                break;
            }
        }


        if (consoleHandler == null) {
            //there was no console handler found, create a new one
            consoleHandler = new ConsoleHandler();
            logger.addHandler(consoleHandler);
        }
        
        //set the console handler to fine:
        consoleHandler.setLevel(java.util.logging.Level.FINEST);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel imagePanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JTree wadTree;
    private javax.swing.JScrollPane wadTreeScrollPanel;
    // End of variables declaration//GEN-END:variables
}
