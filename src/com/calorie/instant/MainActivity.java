package com.calorie.instant;

import com.calorie.instant.util.BitmapHelper;
import com.calorie.instant.util.Log;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity
{
	private static final int REQ_CAMERA_IMAGE = 123;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		String message = "Click the button below to start";
		if(cameraNotDetected()){
			message = "No camera detected, clicking the button below will have unexpected behaviour.";
		}

		TextView cameraDescriptionTextView = (TextView) findViewById(R.id.text_view_camera_description);
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
		} else
			if(requestCode == REQ_CAMERA_IMAGE && resultCode == RESULT_CANCELED){
				Log.i("User didn't take an image");
			}
	}

	private void displayImage(String path) {
		ImageView imageView = (ImageView) findViewById(R.id.image_view_captured_image);
		imageView.setImageBitmap(BitmapHelper.decodeSampledBitmap(path, 300, 250));
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater( ).inflate( R.menu.main, menu );
		return true;
	}

}
