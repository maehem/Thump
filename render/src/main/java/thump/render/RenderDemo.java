/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.render;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import static java.awt.event.KeyEvent.VK_EQUALS;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_MINUS;
import static java.awt.event.KeyEvent.VK_RIGHT;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import static thump.base.Defines.logger;
import thump.wad.DrawUtils;
import thump.wad.Wad;
import thump.wad.WadLoader;
import thump.wad.mapraw.MapPatch;
import thump.wad.mapraw.MapTexture;
import thump.wad.mapraw.PatchData;

/**
 *
 * @author mark
 */
public class RenderDemo extends JFrame implements KeyListener {

    //private final String wadFile = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "Doom" + File.separator + "doom2.wad";
    private final String wadFile = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "Doom" + File.separator + "doom2.wad";
    private final Wad wad;
    private final JPanel infoPanel = new JPanel();
    private final JPanel panel = new JPanel();
    private final JPanel patchesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
    private ImageLabel label;
    private int currentTexture = 0;
    
    private final JLabel itemDetailsLabel = new JLabel("Name      W:999    H:999");
    
    public RenderDemo() {
        setLoggerLevel();
        
        this.wad = WadLoader.getWad(new File(wadFile));

        Renderer r = new Renderer();
        r.R_Init(wad, 11 /* 11 is default screen size */, true);

        initContent();

        initFrame();

        /*
        TODO:   
        
        Render patches for texture.
        Label patches for texture.
        Draw boxes according to the patch mapping.        
        
        */       
    }

    private void initFrame() {
        setTitle("Thump: Render Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(patchesPanel, BorderLayout.SOUTH);
        getContentPane().add(infoPanel, BorderLayout.NORTH);
        pack();
        
        setFocusable(true);
        addKeyListener(this);
        //this.setPreferredSize(new Dimension(400, 300));

    }

    private void initContent() {
        // MapTexture  "TEXTURE1"
        MapTexture texture = wad.getTextures().get(currentTexture);
        //ImageIcon imageIcon = new ImageIcon(DrawUtils.getTextureImage(wad, texture));
        
        label = new ImageLabel(DrawUtils.getColorImage(
                texture.getPatch(), wad.getPlayPalLump().paletteList, 0
        ));

        //label.addKeyListener(this);
        //label.setPreferredSize(new Dimension(300,300));
        panel.add(label);
        panel.setPreferredSize(new Dimension(800,600));
        
        populatePatches(texture);
        
        infoPanel.add(itemDetailsLabel);
        
        updateInfoPanel(texture);
    }

    private void populatePatches(MapTexture mt) {
        patchesPanel.removeAll();
        patchesPanel.revalidate();
        patchesPanel.repaint();
        for ( MapPatch mp: mt.patches ) {
            PatchData pd = wad.patchesLump.getPatch(mp.getPatchNum());
            JLabel pLabel = new JLabel(new ImageIcon(
                    DrawUtils.getColorImage(pd, wad.getPlayPalLump().paletteList, 0))
            );
            patchesPanel.add(pLabel);
        }
        patchesPanel.doLayout();
        
    }
    
    private void updateInfoPanel(MapTexture mt) {
        itemDetailsLabel.setText("Name: " + mt.name 
                + "  W:" + mt.width + "  H:" + mt.height
                + " patches:" + mt.patches.length
        );
        itemDetailsLabel.revalidate();
        infoPanel.revalidate();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new RenderDemo().setVisible(true);
        });
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        //logger.log(Level.SEVERE,"Key Pressed. {0}", e.getKeyCode());
        ArrayList<MapTexture> textures = wad.getTextures();
        MapTexture mt;
        switch(e.getKeyCode()) {
            case VK_MINUS:
                //logger.config("Zoom Out");
                label.downScale();
                break;
            case VK_EQUALS:   // non-shifted  PLUS
                //logger.config("Zoom In");
                label.upScale();
                break;
            case VK_LEFT:
                //logger.config("Previous Texture");
                currentTexture--;
                if (currentTexture >= textures.size()) {
                    currentTexture = textures.size()-1;
                }
                if (currentTexture < 0) {
                    currentTexture = 0;
                }
//                mt = wad.getTextures().get(currentTexture);
//                label.setImage(DrawUtils.getTextureImage2(wad, mt));
//                populatePatches(mt);
                update();
                break;

            case VK_RIGHT:
                //logger.config("Next Texture");
                currentTexture++;
                if (currentTexture >= textures.size()) {
                    currentTexture = textures.size()-1;
                }
                if (currentTexture < 0) {
                    currentTexture = 0;
                }
//                mt = wad.getTextures().get(currentTexture);
//                label.setImage(DrawUtils.getTextureImage2(wad, mt));
//                populatePatches(mt);
                update();
                break;

            default:
                logger.log(Level.CONFIG, "KeyCode:{0}", e.getKeyCode());
        }
    }

    private void update() {
                MapTexture mt = wad.getTextures().get(currentTexture);
                label.setImage(DrawUtils.getColorImage(
                        mt.getPatch(), wad.getPlayPalLump().paletteList, 0
                ));
                populatePatches(mt);   
                updateInfoPanel(mt);
    }
    
    @Override
    public void keyReleased(KeyEvent e) {}

    /**
     * Borrowed from https://stackoverflow.com/questions/9342233/zoom-in-and-out-of-images-in-java
     */
    private class ImageLabel extends JLabel {

        Image image;
        int width, height;
        int originalWidth;
        int originalHeight;
        
        float scale = 1.0f;
        final float MAX_SCALE = 4.0f;
        final float MIN_SCALE = 0.25f;

        public ImageLabel(Image image) {
            this.image = image;
            this.width = image.getWidth(this);
            this.height = image.getHeight(this);
            this.originalWidth = this.width;
            this.originalHeight = this.height;
            
            this.setPreferredSize(new Dimension(width, height));
        }

        
        public void paint(Graphics g) {
            int x, y;
            //this is to center the image
            x = (this.getWidth() - width) < 0 ? 0 : (this.getWidth() - width);
            y = (this.getHeight() - height) < 0 ? 0 : (this.getHeight() - height);

            g.drawImage(image, x, y, width, height, null);
        }

        public void upScale() {
            scale += 0.25f;
            if ( scale > MAX_SCALE ) scale = MAX_SCALE;
            if ( scale < MIN_SCALE ) scale = MIN_SCALE;
            //this.setPreferredSize(new Dimension((int)(originalWidth*scale), (int)(originalHeight*scale)));
            this.setDimensions((int)(originalWidth*scale), (int)(originalHeight*scale));
        }
        
        public void downScale() {
            scale -= 0.25f;
            if ( scale > MAX_SCALE ) scale = MAX_SCALE;
            if ( scale < MIN_SCALE ) scale = MIN_SCALE;
            //this.setPreferredSize(new Dimension((int)(originalWidth*scale), (int)(originalHeight*scale)));
            this.setDimensions((int)(originalWidth*scale), (int)(originalHeight*scale));
        }
        
        public void setImage(Image image ) {
            this.image = image;
            
            this.originalWidth = image.getWidth(this);
            this.originalHeight = image.getHeight(this);

            this.setDimensions((int)(originalWidth*scale), (int)(originalHeight*scale));
        }
        
        public void setDimensions(int width, int height) {
            this.height = height;
            this.width = width;

            this.setPreferredSize(new Dimension(width, height));
            this.revalidate();
            image = image.getScaledInstance(width, height, Image.SCALE_FAST);
            Container parent = this.getParent();
            if (parent != null) {
                parent.repaint();
            }
            this.repaint();
        }
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
        logger.setUseParentHandlers(false);

    }
}
