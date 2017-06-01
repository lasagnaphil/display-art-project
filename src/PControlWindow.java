import com.sun.jna.NativeLibrary;
import processing.core.*;
import controlP5.*;
import sun.plugin2.util.NativeLibLoader;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PControlWindow extends PApplet {
    private PCaptureWindow captureWindow;
    private PCameraWindow camWindow;

    private ControlP5 cp5;

    private PFont korFont;

    private CaptureState state;
    private float prevTime;

    private String modeText;
    private String timeText;

    private Numberbox captureTimeTextbox;

    public void settings() {
        size(1600, 900);
        fullScreen();
    }

    public void setup() {
        cp5 = new ControlP5(this);

        state = new CaptureState();
        state.loadFromConfig();

        korFont = createFont("NanumBarunGothic.ttf", 100);

        captureTimeTextbox = cp5.addNumberbox("captureTime")
                .setPosition(10, 10)
                .setSize(80, 20)
                .setRange(0, 200)
                .setValue(state.captureTime);

        camWindow = new PCameraWindow(state);
        captureWindow = new PCaptureWindow(state);

    }

    public void draw() {
        state.update((millis() - prevTime)/1000);
        prevTime = millis();

        background(0);

        if (state.mode == CaptureMode.Wait) modeText = "대기중...\nEnter를 누르고 들어가세요";
        else modeText = "녹화중...\n아직 들어가지 마세요";
        timeText = String.format("남은 시간: %.2fs", state.captureTime - state.timer);

        state.captureTime = captureTimeTextbox.getValue();

        textFont(korFont);
        textAlign(CENTER);
        textSize(100);
        text(modeText, width/2, height/2);
        if (state.mode == CaptureMode.Capture) text(timeText, width / 2, height / 2 + 300);
    }

    public void clear() {
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
    }

    public static void main(String[] args) {
        String nativeLibraryPath;
        try {
            nativeLibraryPath = new String(Files.readAllBytes(Paths.get("vlcpath.txt")));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), nativeLibraryPath);
        System.out.println(LibVlc.INSTANCE.libvlc_get_version());

        PApplet.main("PControlWindow", args);
    }
}
