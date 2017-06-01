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

    private boolean shouldSaveFrame = false;

    public PCaptureWindow(CaptureState state) {
        super();
        this.state = state;
        PApplet.runSketch(new String[] {this.getClass().getSimpleName()}, this);
    }

    public void settings() {
        size(state.resolutionX, state.resolutionY);
    }

    public void setup() {
        String[] captureList = Capture.list();
        for (String capture : captureList) System.out.println(capture);
        frameRate(state.fps);
        if (state.fps == 0) {
            if (state.cameraName.equals("default"))
                cam = new Capture(this, state.resolutionX, state.resolutionY);
            else
                cam = new Capture(this, state.resolutionX, state.resolutionY, state.cameraName);
        }
        else {
            if (state.cameraName.equals("default"))
                cam = new Capture(this, state.resolutionX, state.resolutionY, state.fps);
            else
                cam = new Capture(this, state.resolutionX, state.resolutionY, state.cameraName, state.fps);
        }

        cam.start();

        videoExport = new VideoExport(this, state.getCurrentVideoName(), cam);
        videoExport.setFrameRate(state.fps);

        // register callbacks
        state.addCaptureStartListener(() -> {
            videoExport.setMovieFileName(state.getCurrentVideoName());
            videoExport.startMovie();
            shouldSaveFrame = true;
        });
        state.addCaptureEndListener(() -> {
            shouldSaveFrame = false;
            videoExport.endMovie();
        });
    }

    public void draw() {

        if (cam.available()) {
            cam.read();
        }
        background(0);

        image(cam, 0, 0);
        if (state.mode == CaptureMode.Capture && shouldSaveFrame) {
            videoExport.saveFrame();
        }
    }

    public void exit() {
        state.stopCapture();
        state.saveToConfig();
        super.exit();
    }

}
