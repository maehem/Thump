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
public class MenuItem {
    protected Enum<?> key;    
    int status;  // 0 = no cursor here, 1 = ok, 2 = arrows ok
    String name;

    // action = menu item #.
    // if status = 2,
    //   action=0:leftarrow,1:rightarrow
    //void	(*routine)(int action);
    MenuAction action;
    
    char alphaKey;  // hotkey in menu

    public MenuItem(Enum<?> key, int status, String name, MenuAction action, char alphaKey) {
        this.key = key;
        this.status = status;
        this.name = name;
        this.action = action;
        this.alphaKey = alphaKey;
    }
}
