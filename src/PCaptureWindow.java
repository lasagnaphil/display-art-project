import com.hamoid.VideoExport;
import processing.core.*;
import processing.video.Capture;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import java.awt.*;
import java.awt.image.BufferedImage;

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

    //private Capture cam;
    private Webcam webcam;
    private PImage screen;
    private VideoExport videoExport;
    private CaptureState state;

    public PCaptureWindow(CaptureState state) {
        super();
        this.state = state;
        PApplet.runSketch(new String[] {this.getClass().getSimpleName()}, this);
    }

    public void settings() {
        size(state.resolutionX, state.resolutionY);
    }

    public void setup() {
        frameRate(state.fps);

        for (Webcam cam : Webcam.getWebcams()) {
            System.out.println(cam.getName());
            if (cam.getName().contains(state.cameraName)) {
                webcam = cam;
            }
            for (Dimension res : cam.getViewSizes()) {
                System.out.println(res.toString());
            }
        }

        webcam.setViewSize(WebcamResolution.VGA.getSize());
        webcam.open(true);
        /*
        String[] captureList = Capture.list();
        for (String capture : captureList) System.out.println(capture);

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
        */

        // register callbacks
        state.addCaptureStartListener(() -> {
            if (videoExport == null) {
                videoExport = new VideoExport(this, state.getCurrentVideoName(), screen);
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

        BufferedImage cap = webcam.getImage();
        screen = new PImage(cap.getWidth(), cap.getHeight(), PConstants.ARGB);
        cap.getRGB(0, 0, screen.width, screen.height, screen.pixels, 0, screen.width);
        screen.updatePixels();

        background(0);

        image(screen, 0, 0);
        if (state.mode == CaptureMode.Capture) {
            videoExport.saveFrame();
        }
    }

    public void exit() {
        state.stopCapture();
        state.saveToConfig();
        super.exit();
    }

}
