package thump;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

public class PlayNote {

    public static void main(String[] args) {
        try {
            // Locate the default synthesizer
            Synthesizer synth = MidiSystem.getSynthesizer();

            // Open the synthesizer
            synth.open();

            // Get the available Midi channels - there are usually 16
            MidiChannel channels[] = synth.getChannels();

            // Play a note on channel 1
            channels[1].noteOn(64, 127);

            // Give the note some time to play
            Thread.sleep(2000);

            // Turn the note off
            channels[1].noteOff(64);

            // Close the synthesizer device
            synth.close();

            // Terminate the program
            System.exit(0);
        } catch (MidiUnavailableException | InterruptedException exc) {
            exc.printStackTrace();
        }
    }
}
