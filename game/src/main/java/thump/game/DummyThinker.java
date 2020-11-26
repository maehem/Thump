/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thump.game;

import thump.wad.map.Thinker;
import thump.wad.map.ThinkerAction;

/**
 *
 * @author mark
 */
public class DummyThinker implements Thinker {

    private ThinkerAction function;
    private Thinker thinker;
    private Thinker next;

    @Override
    public void setPrevThinker(Thinker thinker) {
        this.thinker = thinker;
    }

    @Override
    public Thinker getPrevThinker() {
        return null;
    }

    @Override
    public void setNextThinker(Thinker thinker) {
        next = thinker;
    }

    @Override
    public Thinker getNextThinker() {
        return next;
    }

    @Override
    public void setFunction(ThinkerAction function) {
        this.function = function;
    }

    @Override
    public ThinkerAction getFunction() {
        return function;
    }

    
}
