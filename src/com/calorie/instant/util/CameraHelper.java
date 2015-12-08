package com.calorie.instant.util;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;

/**
 * Used to make camera use in the tutorial a bit more obvious
 * in a production environment you wouldn't make these calls static
 * as you have no way to mock them for testing
 * @author paul.blundell
 *
 */
public class CameraHelper {

	public static boolean cameraAvailable(Camera camera) {
		return camera != null;
	}

	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open();
			Parameters p = c.getParameters();
			p.setFlashMode(Parameters.FLASH_MODE_TORCH);
			p.setPictureSize(1000, 670);
			c.setParameters(p);
			c.startPreview();
		} catch (Exception e) {
			// Camera is not available or doesn't exist
			Log.d("getCamera failed", e);
		}
		return c;
	}

}
