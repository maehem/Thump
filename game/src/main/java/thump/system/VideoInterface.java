/*
 * Video Interface   We draw things to the window here.
 */
package thump.system;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import thump.base.Defines;
import static thump.base.Defines.SCREENHEIGHT;
import static thump.base.Defines.SCREENWIDTH;
import static thump.base.Defines.logger;
import thump.game.Event;
import thump.game.Event.EventType;
import thump.game.Game;
import thump.render.Screen;
import thump.render.ViewCanvas;

/**
 *
 * @author mark
 */
public class VideoInterface implements KeyListener, ComponentListener {
    
    private static VideoInterface instance = null;
    private boolean firsttime=true;
    private int displaynum;
    private JFrame window;       // Shows screen 0.
    private JFrame debugWindow; // Shows the other four screen buffers.
    
    private ViewCanvas viewCanvas;
    private final ViewCanvas [] dbgCanvas = new ViewCanvas[5];
    
    private final JTextPane consolePane = new JTextPane();
    //private final JScrollPane consoleScrollPane = new JScrollPane(consolePane);
    //private Color[] palette;
    private int[] palette;
    public float scaleX = 1.0f;
    public float scaleY = 1.0f;

    private VideoInterface() {}
    
    public static VideoInterface getInstance() {
        if ( instance == null ) {
            instance = new VideoInterface();
        }
        
        return instance;
    }
    
    public int multiply = 1;
    
    // Called by D_DoomMain,
    // determines the hardware configuration
    // and sets up the video mode
    public void I_InitGraphics (){
        logger.log(Level.CONFIG, "Init Graphics: Creating Window.");

        if (!firsttime) {
            logger.warning("Tried to init graphics again.  Fix me.");
            return;
        }        
        firsttime = false;
        
        Game game = Game.getInstance();
        
        if (game.isParam("-2")) {
            multiply = 2;
        }

        if (game.isParam("-3")) {
            multiply = 3;
        }

        if (game.isParam("-4")) {
            multiply = 4;
        }

        /*
          Hope to be able to address multiple displays.
        */
        int p=game.args.indexOf("-disp");
        // check for command-line display name
        if ( p > -1 ) { 
            displaynum = Integer.valueOf(game.args.get(p+1));
        } else {
            displaynum = 0;
        }
        
        
        window = new JFrame(Game.getMessage("TITLE"));
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // SHould be something different
        
        debugWindow = new JFrame( "Other Buffers" );
        // TODO: Need a hot-key to toggle displaying
        debugWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        
        //window.setLayout(new BorderLayout(0, 0));
        viewCanvas = new ViewCanvas();
        viewCanvas.setPreferredSize(new Dimension(Defines.SCREENWIDTH*multiply, Defines.SCREENHEIGHT*multiply));
        //consoleScrollPane.setPreferredSize(new Dimension(Defines.SCREENWIDTH*multiply, Defines.SCREENWIDTH*multiply/3));
        
        window.getContentPane().add(viewCanvas, BorderLayout.CENTER);
        //window.getContentPane().add(consoleScrollPane, BorderLayout.SOUTH);
        
        JPanel dbgPanel = new JPanel();
        dbgPanel.setLayout(new BoxLayout(dbgPanel, BoxLayout.PAGE_AXIS));
        for (int sn = 0; sn<5; sn++) {
            dbgCanvas[sn] = new ViewCanvas();
            dbgCanvas[sn].setPreferredSize(new Dimension(Defines.SCREENWIDTH, Defines.SCREENHEIGHT));
            dbgPanel.add(dbgCanvas[sn]);
        }
        
        debugWindow.getContentPane().add(dbgPanel, BorderLayout.CENTER);
        
        window.pack();
        window.setLocationRelativeTo(null); // Centered on screen
        
        debugWindow.pack();
        debugWindow.setLocationRelativeTo(window);

        viewCanvas.initStrategy();
        viewCanvas.requestFocus();
        viewCanvas.setFocusTraversalKeysEnabled(false);

        for (int sn = 0; sn<5; sn++) {
            dbgCanvas[sn].initStrategy();
        }

        window.setVisible(true);
        debugWindow.setVisible(true);
        
        viewCanvas.addKeyListener(this);
        //consoleScrollPane.addKeyListener(this);
        consolePane.addKeyListener(this);
        
        viewCanvas.addComponentListener(this);
        
    }


    public void I_ShutdownGraphics(){}

    // Takes full 8 bit values.
    //public void I_SetPalette (Color[] pal) {
    public void I_SetPalette (int[] pal) {
        palette = pal;
    }

    /**
     * Moves the content of screens[0] into the draw buffer.
     * Applies current palette colors.
     */
    public void I_UpdateNoBlit () {
        logger.log(Level.FINEST, "I_UpdateNoBlit()");
        //BufferedImage s = Game.getInstance().video.screenImage[0];
        for (int sn = 0; sn<5; sn++) {
            //viewCanvas.repaint();
            int[] area = Game.getInstance().renderer.video.screens[sn].area;
            BufferedImage img = Game.getInstance().renderer.video.screenImage[sn];
            for( int i=0; i< area.length; i++ ) {

                //int x = 0;
                int val = area[i];
                int cc;
//                if (/*val == -1*/ val < 0 || val>255 ) {
//                    cc = 0;
//                } else {
//                    Color c = palette[val];
//                    cc = 0xFF << 24 | (c.getRed() & 0xFF) << 16 | (c.getGreen() & 0xFF) << 8 | c.getBlue() & 0xFF;
//                }
                if (/*val == -1*/ val < 0 || val>255 ) {
                    cc = 0;
                } else {
                    cc = palette[val];
                    //cc = 0xFF << 24 | (c.getRed() & 0xFF) << 16 | (c.getGreen() & 0xFF) << 8 | c.getBlue() & 0xFF;
                }
                //img.setRGB(x, y, cc);
                img.setRGB(i%SCREENWIDTH, i/SCREENWIDTH, cc);

                //x++;
            }
        }
    }
    
    /**
     * Draws the draw buffer to the screen.
     */
    public void I_FinishUpdate (){
        logger.log(Level.FINEST, "I_FinishUpdate()");
        BufferedImage s = Game.getInstance().renderer.video.screenImage[0];
        Graphics g = viewCanvas.getStrategy().getDrawGraphics();
        g.drawImage(s, 0, 0 , 
                (int) (s.getWidth()*scaleX), (int) (s.getHeight()*scaleY), viewCanvas);
        viewCanvas.getStrategy().show();
        g.dispose();
        
        
        for (int sn = 0; sn < 5; sn++) {
            s = Game.getInstance().renderer.video.screenImage[sn];
            g = dbgCanvas[sn].getStrategy().getDrawGraphics();
            g.drawImage(s, 0, 0, s.getWidth(), s.getHeight(), dbgCanvas[sn]);
            dbgCanvas[sn].getStrategy().show();
            g.dispose();
        }

    }
    
//    public void flipBuffer() {
//        viewCanvas.getStrategy().show();
//    }

    // Wait for vertical retrace or pause a bit.
    public void I_WaitVBL(int count){}

    public void I_ReadScreen (Screen scr){
        //memcpy (scr, screenImage[0], SCREENWIDTH*SCREENHEIGHT);
        System.arraycopy(Game.getInstance().renderer.video.screens[0].area, 0, scr.area, 0, SCREENWIDTH*SCREENHEIGHT); //scr.area = Arrays.copyOf(Game.getInstance().video.screens[0].area, SCREENWIDTH*SCREENHEIGHT);
    }

    public void I_BeginRead (){}
    public void I_EndRead (){}

    //
    // I_StartTic
    //
    public void I_StartTic (){

//        if (!X_display)
//            return;
//
//        while (XPending(X_display))
//            I_GetEvent();
//
//        // Warp the pointer back to the middle of the window
//        //  or it will wander off - that is, the game will
//        //  loose input focus within X11.
//        if (grabMouse)
//        {
//            if (!--doPointerWarp)
//            {
//                XWarpPointer( X_display,
//                              None,
//                              X_mainWindow,
//                              0, 0,
//                              0, 0,
//                              X_width/2, X_height/2);
//
//                doPointerWarp = POINTER_WARP_COUNTDOWN;
//            }
//        }
//
//        mousemoved = false;

    }


    int	lastmousex = 0;
    int	lastmousey = 0;
    //boolean		mousemoved = false;
    boolean		shmFinished;

    void I_GetEvent()  {

//        Event event;
//
//        // put event-grabbing stuff in here
//        XNextEvent(X_display, &X_event);
//        switch (X_event.type)
//        {
//          case KeyPress:
//            event.type = ev_keydown;
//            event.data1 = xlatekey();
//            D_PostEvent(&event);
//            // fprintf(stderr, "k");
//            break;
//          case KeyRelease:
//            event.type = ev_keyup;
//            event.data1 = xlatekey();
//            D_PostEvent(&event);
//            // fprintf(stderr, "ku");
//            break;
//          case ButtonPress:
//            event.type = ev_mouse;
//            event.data1 =
//                (X_event.xbutton.state & Button1Mask)
//                | (X_event.xbutton.state & Button2Mask ? 2 : 0)
//                | (X_event.xbutton.state & Button3Mask ? 4 : 0)
//                | (X_event.xbutton.button == Button1)
//                | (X_event.xbutton.button == Button2 ? 2 : 0)
//                | (X_event.xbutton.button == Button3 ? 4 : 0);
//            event.data2 = event.data3 = 0;
//            D_PostEvent(&event);
//            // fprintf(stderr, "b");
//            break;
//          case ButtonRelease:
//            event.type = ev_mouse;
//            event.data1 =
//                (X_event.xbutton.state & Button1Mask)
//                | (X_event.xbutton.state & Button2Mask ? 2 : 0)
//                | (X_event.xbutton.state & Button3Mask ? 4 : 0);
//            // suggest parentheses around arithmetic in operand of |
//            event.data1 =
//                event.data1
//                ^ (X_event.xbutton.button == Button1 ? 1 : 0)
//                ^ (X_event.xbutton.button == Button2 ? 2 : 0)
//                ^ (X_event.xbutton.button == Button3 ? 4 : 0);
//            event.data2 = event.data3 = 0;
//            D_PostEvent(&event);
//            // fprintf(stderr, "bu");
//            break;
//          case MotionNotify:
//            event.type = ev_mouse;
//            event.data1 =
//                (X_event.xmotion.state & Button1Mask)
//                | (X_event.xmotion.state & Button2Mask ? 2 : 0)
//                | (X_event.xmotion.state & Button3Mask ? 4 : 0);
//            event.data2 = (X_event.xmotion.x - lastmousex) << 2;
//            event.data3 = (lastmousey - X_event.xmotion.y) << 2;
//
//            if (event.data2 || event.data3)
//            {
//                lastmousex = X_event.xmotion.x;
//                lastmousey = X_event.xmotion.y;
//                if (X_event.xmotion.x != X_width/2 &&
//                    X_event.xmotion.y != X_height/2)
//                {
//                    D_PostEvent(&event);
//                    // fprintf(stderr, "m");
//                    mousemoved = false;
//                } else
//                {
//                    mousemoved = true;
//                }
//            }
//            break;
//
//          case Expose:
//          case ConfigureNotify:
//            break;
//
//          default:
//            if (doShm && X_event.type == X_shmeventtype) shmFinished = true;
//            break;
//        }

    }

    @Override
    public void keyTyped(KeyEvent ke) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        logger.log(Level.FINE, "key pressed. {0}", ke.getKeyCode());
             
        Game.getInstance().doomMain.D_PostEvent(new Event(EventType.ev_keydown, ke.getKeyCode()));
//            event.type = ev_keydown;
//            event.data1 = xlatekey();
//            D_PostEvent(&event);
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        Game.getInstance().doomMain.D_PostEvent(new Event(EventType.ev_keyup, ke.getKeyCode()));
//            event.type = ev_keyup;
//            event.data1 = xlatekey();
//            D_PostEvent(&event);
    }
        
    
    public int getWidth() {
        return viewCanvas.getWidth();
    }
    
    public int getHeight() {
        return viewCanvas.getHeight();
    }

    @Override
    public void componentResized(ComponentEvent ce) {
        logger.finer("Canvas resized.");
        scaleX = (float)viewCanvas.getWidth()/Defines.SCREENWIDTH;
        scaleY = (float)viewCanvas.getHeight()/Defines.SCREENHEIGHT;
    }

    @Override
    public void componentMoved(ComponentEvent ce) {}

    @Override
    public void componentShown(ComponentEvent ce) {}

    @Override
    public void componentHidden(ComponentEvent ce) {}
            
}
