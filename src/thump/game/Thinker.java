/*
 * Thinker Linked list management.
 */
package thump.game;

/**
 *
 * @author mark
 */
public interface Thinker {
    //public Thinker         prev;
    public void setPrevThinker(Thinker thinker);
    public Thinker getPrevThinker();
    //public Thinker         next;
    public void setNextThinker(Thinker thinker);
    public Thinker getNextThinker();
    //public ThinkerAction   function;
    public void setFunction( ThinkerAction function);
    public ThinkerAction getFunction();
}
