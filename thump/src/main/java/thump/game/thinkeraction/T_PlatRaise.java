/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.game.thinkeraction;

import thump.game.ThinkerAction;
import thump.play.Platform;

/**
 *
 * @author mark
 */
public class T_PlatRaise implements ThinkerAction {

    @Override
    public void doAction(Object thing) {
        Platform.T_PlatRaise((Platform) thing);
    }
    
}
