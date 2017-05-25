import processing.core.*;
import processing.video.*;

import java.io.File;

/**
 * Created by lasagnaphil on 2017-05-21.
 */

// 2nd window to show previous capture of person
public class PCameraWindow extends PApplet {
    private CaptureState state;
    public Movie movie;
    private boolean isPlaying = false;
    private String infoText = "";

    public PCameraWindow(CaptureState state) {
        super();
        this.state = state;
        PApplet.runSketch(new String[] {this.getClass().getSimpleName()}, this);
    }

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        //fullScreen(2);
        state.addCaptureStartListener(() -> {
            String movieName = state.getPreviousVideoName();
            File movieFile = new File(movieName);
            if (movieFile.exists()) {
                movie = new Movie(this, movieName);
                movie.play();
                isPlaying = true;
                infoText = "Playing: " + movieName;
            }
            else {
                movie = null;
                infoText = state.getPreviousVideoName() + " does not exist.";
            }
        });
        state.addCaptureEndListener(() -> {
            if (movie != null) movie.stop();
            isPlaying = false;
        });
    }

    public void draw() {
        background(0);
        if (state.mode == CaptureMode.Wait) {
            text("Waiting...", 10, 10);
        }
        else {
            if (isPlaying) {
                image(movie, 0, 0);
            }
            text("Capturing...", 10, 10);
            text(infoText, 10, 50);
        }
    }

    public void exit() {
        state.stopCapture();
        state.saveToConfig();
        super.exit();
    }

    public void movieEvent(Movie m) {
        m.read();
    }

}
