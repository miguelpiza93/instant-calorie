package com.calorie.instant;

import static com.calorie.instant.util.CameraHelper.cameraAvailable;
import static com.calorie.instant.util.CameraHelper.getCameraInstance;
import static com.calorie.instant.util.MediaHelper.getOutputMediaFile;
import static com.calorie.instant.util.MediaHelper.saveToFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.calorie.instant.ui.CameraPreview;
import com.calorie.instant.util.Log;
import com.calorie.instant.util.MediaHelper;

/**
 * Takes a photo saves it to the SD card and returns the path of this photo to the calling Activity
 * @author paul.blundell
 *
 */
public class CameraActivity extends Activity implements PictureCallback {

	protected static final String EXTRA_IMAGE_PATH = "com.blundell.tut.cameraoverlay.ui.CameraActivity.EXTRA_IMAGE_PATH";
	public static String FOLDER_IMAGE = "calorieInstant";
	private Camera camera;
	private CameraPreview cameraPreview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		setResult(RESULT_CANCELED);
		// Camera may be in use by another activity or the system or not available at all
		camera = getCameraInstance();
		if(cameraAvailable(camera)){
			initCameraPreview();
		} else {
			finish();
		}
	}

	// Show the camera view on the activity
	private void initCameraPreview() {
		cameraPreview = (CameraPreview) findViewById(R.id.camera_preview);
		cameraPreview.init(camera);
	}

	public void onCaptureClick(View button){
		// Take a picture with a callback when the photo has been created
		// Here you can add callbacks if you want to give feedback when the picture is being taken
		camera.takePicture(null, null, this);
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		Log.d("Picture taken");

		String path = savePictureToFileSystem(data, data);
		/**		Bitmap background = BitmapFactory.decodeFile(path);
		Bitmap drawBack = background.copy(Bitmap.Config.ARGB_8888, true);
		Bitmap form = BitmapFactory.decodeResource(getResources(), R.drawable.plate);
		Canvas image = new Canvas(drawBack);
		image.drawBitmap(form, 0f, 0f, null);
		FileOutputStream os;
		try {
			os = new FileOutputStream("/sdcard/DCIM/Camera/" + "myNewFileName.png");
			drawBack.compress(CompressFormat.PNG, 50, os);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}**/
		setResult(path);
		finish();
	}

	private static String savePictureToFileSystem(byte[] data, byte[] byteArray) {
		File file = getOutputMediaFile();
		saveToFile(data, file);
		//saveToFile(byteArray, file);
		return file.getAbsolutePath();
	}

	private void setResult(String path) {
		Intent intent = new Intent();
		intent.putExtra(EXTRA_IMAGE_PATH, path);
		setResult(RESULT_OK, intent);
	}

	// ALWAYS remember to release the camera when you are finished
	@Override
	protected void onPause() {		
		super.onPause();
		camera.stopPreview();
		camera.release();
		releaseCamera();
	}

	private void releaseCamera() {
		if(camera != null){
			camera.release();
			camera = null;
		}
	}
}
