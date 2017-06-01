import com.hamoid.VideoExport;
import processing.core.*;
import processing.video.Capture;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

public class CaptureWindow {

    private JFrame frame;
    private JPanel contentPane;
    private Canvas canvas;
    private MediaPlayerFactory factory;
    private EmbeddedMediaPlayer mediaPlayer;
    private CanvasVideoSurface videoSurface;

    private VideoExport videoExport;
    private CaptureState state;

    private String inputParams;
    private String outputParams;

    public CaptureWindow(CaptureState state) {
        this.state = state;

        canvas = new Canvas();
        canvas.setBackground(Color.black);

        contentPane = new JPanel();
        contentPane.setBackground(Color.black);
        contentPane.setLayout(new BorderLayout());
        contentPane.add(canvas, BorderLayout.CENTER);

        frame = new JFrame("Capture");
        frame.setContentPane(contentPane);
        frame.setSize(1280, 720);
        factory = new MediaPlayerFactory();
        mediaPlayer = factory.newEmbeddedMediaPlayer();
        videoSurface = factory.newVideoSurface(canvas);
        mediaPlayer.setVideoSurface(videoSurface);

        frame.setVisible(true);
        state.addCaptureStartListener(() -> startCapture());
        state.addCaptureEndListener(() -> stopCapture());
    }

    public void startCapture() {
        String fileName = state.getCurrentVideoName();
        String[] options;
        try {
            options = Files.readAllLines(Paths.get("vlc_config.txt")).toArray(new String[0]);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        options[options.length - 1].replace("<filename>", fileName);
        mediaPlayer.playMedia("dshow://", options);
    }

    public void stopCapture() {
        mediaPlayer.stop();
        mediaPlayer.release();
        factory.release();
    }
}
