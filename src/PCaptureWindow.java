import com.hamoid.VideoExport;
import processing.core.*;
import processing.video.Capture;

/**
 * Created by lasagnaphil on 2017-05-21.
 */

/*
class DisposeHandler {
    PCaptureWindow controlWindow;

    DisposeHandler(PCaptureWindow controlWindow) {
        this.controlWindow = controlWindow;
        controlWindow.registerMethod("dispose", this);
    }

    public void dispose() {
    }
}
*/

public class PCaptureWindow extends PApplet {

    private Capture cam;
    private VideoExport videoExport;
    private CaptureState state;

    public PCaptureWindow(CaptureState state) {
        super();
        this.state = state;
        PApplet.runSketch(new String[] {this.getClass().getSimpleName()}, this);
    }

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        cam = new Capture(this, 640, 480, 30);
        cam.start();

        // register callbacks
        state.addCaptureStartListener(() -> {
            if (videoExport == null) {
                videoExport = new VideoExport(this, state.getCurrentVideoName(), cam);
            }
            else {
                videoExport.setMovieFileName(state.getCurrentVideoName());
            }
            videoExport.startMovie();
        });
        state.addCaptureEndListener(() -> {
            if (videoExport != null) {
                videoExport.endMovie();
            }
        });
    }

    public void draw() {

        if (cam.available()) {
            cam.read();
        }
        background(0);

        image(cam, 0, 0);
        if (state.mode == CaptureMode.Capture) {
            videoExport.saveFrame();
        }
    }

    public void exit() {
        state.stopCapture();
        state.saveToConfig();
        super.exit();
    }

    public void keyPressed() {
        if (state.mode == CaptureMode.Wait && key == ENTER) {
            state.startCapture();
        }
        else if (key == ESC) {
            state.stopCapture();
            videoExport.endMovie();
            exit();
        }
    }
}
