/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.game.thinkeraction;

import thump.game.ThinkerAction;
import thump.play.FireFlicker;
import thump.play.Lights;

/**
 *
 * @author mark
 */
public class T_FireFlicker implements ThinkerAction {

    @Override
    public void doAction(Object thing) {
        Lights.T_FireFlicker((FireFlicker) thing);
    }
    
}
