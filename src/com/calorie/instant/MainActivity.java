package com.calorie.instant;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.calorie.instant.util.BitmapHelper;
import com.calorie.instant.util.Log;

public class MainActivity extends Activity
{
	private static final int REQ_CAMERA_IMAGE = 123;

	private static final String  TAG = "OCVSample::MainActivity";
	
	private boolean openCVLoaded;

	private TextView cameraDescriptionTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		String message = "Click the button below to start";
		if(cameraNotDetected()){
			message = "No camera detected, clicking the button below will have unexpected behaviour.";
		}

		cameraDescriptionTextView = (TextView) findViewById(R.id.text_view_camera_description);
		cameraDescriptionTextView.setText(message);
	}

	private boolean cameraNotDetected() {
		return !getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
	}


	public void onUseCameraClick(View button){
		Intent intent = new Intent(this, CameraActivity.class);
		startActivityForResult(intent, REQ_CAMERA_IMAGE);
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQ_CAMERA_IMAGE && resultCode == RESULT_OK){
			String imgPath = data.getStringExtra(CameraActivity.EXTRA_IMAGE_PATH);
			Log.i("Got image path: "+ imgPath);
			displayImage(imgPath);
			RecorteTask recorte = new RecorteTask(this.getApplicationContext(), cameraDescriptionTextView);	
			recorte.execute(imgPath);
			
		} else
			if(requestCode == REQ_CAMERA_IMAGE && resultCode == RESULT_CANCELED){
				Log.i("User didn't take an image");
			}
	}


	private void displayImage(String path) {
		ImageView imageView = (ImageView) findViewById(R.id.image_view_captured_image);
		imageView.setImageBitmap(BitmapHelper.decodeSampledBitmap(path, 300, 250));
	}
	
	private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
		

		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS:
			{
				android.util.Log.i(TAG, "OpenCV loaded successfully");
				openCVLoaded=true;				
			} break;
			default:
			{
				super.onManagerConnected(status);
			} break;
			}
		}
	};
	
	@Override
	public void onResume()
	{
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
	}

}
