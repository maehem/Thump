/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.menu;

/**
 *
 * @author mark
 */
class SoundMenu extends Menu implements MenuAction {

    enum Items {
        sfx_vol,
        sfx_empty1,
        music_vol,
        sfx_empty2,
    };

    public SoundMenu(Menu prevMenu, int x, int y) {
        super(prevMenu, x, y);
        super.lastOn = Items.sfx_vol.ordinal();
        
        menuItems.add( new MenuItem(Items.sfx_vol, 2, "M_SFXVOL",  this,  's'));
        menuItems.add( new BlankMenuItem(Items.sfx_empty1));
        menuItems.add( new MenuItem(Items.music_vol, 2, "M_MUSVOL",this,  'm'));
        menuItems.add( new BlankMenuItem(Items.sfx_empty2));
    }

    @Override
    public void itemSelected(MenuItem item, int choice) {
        switch( (Items)item.key ) {
            case sfx_vol:
                MenuManager.getInstance().M_SfxVol(choice );
                break;
            case music_vol:
                MenuManager.getInstance().M_MusicVol(choice );
                break;
                
            case sfx_empty1: // Should never happen.
            case sfx_empty2:
            default:
                throw new AssertionError(((Items)item.key).name());
        }
    }
    

@Override
    public void draw() {
        MenuManager.getInstance().M_DrawSound();
    }
    
}
