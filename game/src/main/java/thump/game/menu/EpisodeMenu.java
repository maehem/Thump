/*
 * Episode Menu
 */
package thump.game.menu;

import static thump.game.menu.EpisodeMenu.Items.*;

/**
 *
 * @author mark
 */
public class EpisodeMenu extends Menu implements MenuAction {

    private final MenuItem fourthEpisodeItem; // Gets removed by Registered GameMode.

    enum Items { ep1, ep2, ep3, ep4 };

    //EnumMap<Items, MenuItem> menuItems = new EnumMap<>(Items.class);

    public EpisodeMenu(Menu prevMenu, int x, int y) {
        super(prevMenu, x, y);
        lastOn = ep1.ordinal();
        
        fourthEpisodeItem = new MenuItem(ep4, 1, "M_EPI4", this, 't');
        
        menuItems.add( new MenuItem(ep1, 1, "M_EPI1", this, 'k'));
        menuItems.add( new MenuItem(ep2, 1, "M_EPI2", this, 't'));
        menuItems.add( new MenuItem(ep3, 1, "M_EPI3", this, 'i'));
        menuItems.add( fourthEpisodeItem ); // Removed by Registered GameMode in Init.
    }

//    @Override
//    public void itemSelected(MenuItem choice) {
//        // Call M_Episode
//        for ( int i=0; i<Items.values().length; i++) {
//            if ( menuItems.)
//        }
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
    @Override
    public void draw() {
        MenuManager.getInstance().M_DrawEpisode();
    }
    
    void removeFourth() {
        menuItems.remove(fourthEpisodeItem);
    }

    @Override
    public void itemSelected(MenuItem menuItem, int choice) {
        MenuManager.getInstance().M_Episode(choice);
    }

}
