import processing.core.*;
import controlP5.*;

import java.io.File;
import java.net.URISyntaxException;

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

    private float waitTimer = 0.0f;
    private boolean isWaiting = true;

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

        state.addCaptureStartListener(() -> isWaiting = true);
    }

    public void draw() {
        float dt = (millis() - prevTime)/1000;
        state.update(dt);
        prevTime = millis();

        background(0);

        if (state.mode == CaptureMode.Wait) modeText = "Enter를 누르고 들어가세요";
        else {
            if (isWaiting) {
                waitTimer += dt;
            }
            if (waitTimer > 5.0f) {
                isWaiting = false;
                waitTimer = 0.0f;
            }
            modeText = isWaiting? "녹화중...\n지금 들어가세요" :
                    "녹화중...\n사람이 나올때까지 대기해주세요";
        }
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
        PApplet.main("PControlWindow", args);
    }
}
