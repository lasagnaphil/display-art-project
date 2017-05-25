import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lasagnaphil on 2017-05-21.
 */

enum CaptureMode {
    Wait, Capture
}


@FunctionalInterface
interface Procedure {
    void execute();
}


public class CaptureState {
    public CaptureMode mode = CaptureMode.Wait;
    public int videoId = 1;
    public float timer = 0.0f;
    public float captureTime = 10.0f;

    public String getCurrentVideoName() {
        return Integer.toString(videoId) + ".mp4";
    }

    public String getPreviousVideoName() {
        return Integer.toString(videoId - 1) + ".mp4";
    }

    private ArrayList<Procedure> onCaptureEndCallbacks;
    private ArrayList<Procedure> onCaptureStartCallbacks;

    public CaptureState() {
        onCaptureEndCallbacks = new ArrayList<Procedure>();
        onCaptureStartCallbacks = new ArrayList<Procedure>();
    }

    public void loadFromConfig() {
        try {
            // load config file
            List<String> configData = Files.readAllLines(Paths.get("config.txt"));
            videoId = Integer.parseInt(configData.get(0).split(" = ")[1]);
            captureTime = Float.parseFloat(configData.get(1).split(" = ")[1]);

        } catch (NoSuchFileException ex) {
            // cannot find config file -> create new
            try {
                Files.write(Paths.get("config.txt"), "currentVideoId = 1\ncaptureTime = 10.0".getBytes());
            } catch (IOException ex2){
                throw new RuntimeException(ex2);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void saveToConfig() {
        try {
            String config = String.format("currentVideoId = %d\n" +
                          "captureTime = %f", videoId, captureTime);
            Files.write(Paths.get("config.txt"), config.getBytes());
            System.out.println("Config saved.");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void addCaptureEndListener(Procedure f) {
        onCaptureEndCallbacks.add(f);
    }
    public void addCaptureStartListener(Procedure f) { onCaptureStartCallbacks.add(f); }

    public void startCapture() {
        if (mode == CaptureMode.Wait) {
            videoId++;
            timer = 0.0f;
            mode = CaptureMode.Capture;
            for (Procedure f : onCaptureStartCallbacks) f.execute();
        }
    }

    public void stopCapture() {
        if (mode == CaptureMode.Capture) {
            mode = CaptureMode.Wait;
            for (Procedure f : onCaptureEndCallbacks) f.execute();
        }
    }

    public void update(float dt) {
        if (mode == CaptureMode.Capture) {
            timer += dt;
            if (timer >= captureTime) {
                stopCapture();
            }
        }
    }
}
