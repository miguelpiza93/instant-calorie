package com.calorie.instant;

import java.util.List;
import java.util.List;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.calorie.instant.util.BitmapHelper;
import com.calorie.instant.util.ColorBlobDetector;
import com.calorie.instant.util.Log;

public class MainActivity extends Activity
{
	private static final int REQ_CAMERA_IMAGE = 123;

	private static final String  TAG = "OCVSample::MainActivity";
	
	private ColorBlobDetector mDetector;
	private boolean openCVLoaded;

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
			calcularArea(imgPath);
		} else
			if(requestCode == REQ_CAMERA_IMAGE && resultCode == RESULT_CANCELED){
				Log.i("User didn't take an image");
			}
	}

	private void calcularArea( String imgPath )
	{
		android.util.Log.i(TAG, "Entra a calcular area");
		if(openCVLoaded)
		{
			android.util.Log.i(TAG, "Entra a calcular area y opencv esta cargado");
			try
			{
				Bitmap bitmap = BitmapFactory.decodeResource( getResources(), R.raw.c300 );
				Mat src = new Mat();
				Utils.bitmapToMat( bitmap, src );
				mDetector.process(src);
				List<MatOfPoint> contours = mDetector.getContours();
				
				Moments[] mu = new Moments[contours.size( )];

				/// Get the moments
				for ( int i = 0; i < contours.size( ); i++ )
				{
					mu[i] = Imgproc.moments(contours.get( i ));
				}

				String areas = "";
				for( int i = 0; i< contours.size(); i++ )
				{
					areas += mu[i].get_m00( ) + "\n";
				}
				
				android.util.Log.i(TAG, areas);
				
			}
			catch ( Exception e )
			{
				e.printStackTrace();
			}
		}
		else
		{
			android.util.Log.e( TAG, "OpenCV no cargado" );
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
				mDetector = new ColorBlobDetector();
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
