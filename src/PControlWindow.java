import processing.core.*;
import controlP5.*;

public class PControlWindow extends PApplet {
    private PCaptureWindow captureWindow;
    private PCameraWindow camWindow;

    private ControlP5 cp5;

    private CaptureState state;
    private float prevTime;

    private String modeText;
    private String timeText;

    private Numberbox captureTimeTextbox;

    public void settings() {
        size(320, 240);
    }

    public void setup() {
        cp5 = new ControlP5(this);

        state = new CaptureState();
        state.loadFromConfig();

        captureTimeTextbox = cp5.addNumberbox("captureTime")
                .setPosition(10, 120)
                .setSize(120, 40)
                .setRange(0, 200)
                .setValue(state.captureTime);

        camWindow = new PCameraWindow(state);
        captureWindow = new PCaptureWindow(state);

    }

    public void draw() {
        state.update((millis() - prevTime)/1000);
        prevTime = millis();

        background(0);

        if (state.mode == CaptureMode.Wait) modeText = "Waiting...";
        else modeText = "Capturing...";
        timeText = String.format("%.2f", state.timer);

        state.captureTime = captureTimeTextbox.getValue();

        textSize(30);
        text(modeText, 10, 40);
        text(timeText, 10, 80);
    }

    public void clear() {
    }

    public void exit() {
        state.stopCapture();
        state.saveToConfig();
        super.exit();
    }

    public static void main(String[] args) {
        PApplet.main("PControlWindow", args);
    }
}
