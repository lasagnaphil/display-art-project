import com.hamoid.VideoExport;
import processing.core.*;
import processing.video.Capture;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import uk.co.caprica.vlcj.medialist.MediaListItem;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private static List<MediaListItem> EMPTY = new ArrayList<MediaListItem>();

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

        MediaListItem dev0 = new MediaListItem("Microsoft¢ç LifeCam HD-3000", "dshow://", EMPTY);
        Webcam.setDriver(new VlcjDriver(Arrays.asList(dev0)));

        webcam = Webcam.getDefault();
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
