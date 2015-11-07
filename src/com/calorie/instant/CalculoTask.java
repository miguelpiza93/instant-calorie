package com.calorie.instant;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

public class CalculoTask extends AsyncTask< String, Integer, Double >
{

	private static final String  TAGCAL = "TaskCalcular";
	private Context context;

	public CalculoTask(Context context)
	{
		this.context = context;
	}

	@Override
	protected Double doInBackground( String... params )
	{
		return calcularArea( params[0] );
	}

	private double calcularArea( String imgPath )
	{
		try
		{
			Mat src = Highgui.imread( imgPath, Highgui.IMREAD_COLOR );
			Mat src_gray = new Mat();
			Imgproc.cvtColor( src, src_gray, Imgproc.COLOR_BGR2GRAY );
			Imgproc.blur( src_gray, src_gray, new Size(3,3) );

			int thresh = 50;
			String descargas = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOWNLOADS ).getPath( );	

			Mat canny_output = new Mat( );
			List<MatOfPoint> contours = new ArrayList< MatOfPoint >();
			Mat hierarchy = new Mat( );

			/// Detect edges using canny
			Imgproc.Canny( src_gray, canny_output, thresh, thresh*2 );
			/// Find contours
			Imgproc.findContours( canny_output, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0) );

			// Draw contours
			Mat drawing = Mat.zeros( canny_output.size(), CvType.CV_8UC3 );

			Scalar color = new Scalar( 255, 255, 255 );
			for( int i = 0; i< contours.size(); i++ )
			{
				Imgproc.drawContours( drawing, contours, i, color, 2, 8, hierarchy, 0, new Point() );
			}
			Highgui.imwrite( descargas + "/contorno.jpg", drawing );

			Moments[] mu = new Moments[contours.size( )];

			/// Get the moments
			for ( int i = 0; i < contours.size( ); i++ )
			{
				mu[i] = Imgproc.moments(contours.get( i ));
			}

			double areas = 0;
			for( int i = 0; i< contours.size(); i++ )
			{
				areas += mu[i].get_m00( );
			}
			return areas;
		}
		catch ( Exception e )
		{
			android.util.Log.i(TAGCAL, "Error" );
		}
		return -1;
	}

	@Override
	protected void onPostExecute( Double result )
	{
		super.onPostExecute( result );
		Toast.makeText( context, "Proceso terminado: ".concat( String.valueOf( result ) ), Toast.LENGTH_LONG ).show( );
	}
}
