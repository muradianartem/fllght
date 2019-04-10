package tambowskip.com.flashlight;

import android.app.Activity;

/**
 * Created by ldemyanenko on 16.03.2017.
 */

public interface FlashLightInterface {
    void getCamera();

    void setFlashOn(boolean aBoolean);

    void checkForFlash(Activity mainActivity);

    boolean isFlashOn();

    void turnOffFlash();

    void workWithFlash();

    void turnOnFlash();

    void releaseCamera();
}
