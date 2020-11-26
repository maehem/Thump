/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.game.thinkeraction;

import thump.game.play.LightFlash;
import thump.game.play.Lights;
import thump.wad.map.ThinkerAction;


/**
 *
 * @author mark
 */
public class T_LightFlash implements ThinkerAction {

    @Override
    public void doAction(Object thing) {
        Lights.T_LightFlash((LightFlash) thing);
    }
    
}
