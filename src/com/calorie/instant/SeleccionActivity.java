package com.calorie.instant;

import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.calorie.instant.util.BitmapHelper;
import com.calorie.instant.util.ColorBlobDetector;

public class SeleccionActivity extends Activity implements OnClickListener, OnTouchListener
{
	public static final String RUTA_RECORTES = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/InstantCalorie/";

	private ImageView imgSeleccion;
	private int imgActual;
	private TextView txtPorcionActual;


	private Mat                  mRgba;
	private Scalar               mBlobColorRgba;//color escogido
	private Scalar               mBlobColorHsv;// color promedio de la region tocada
	private ColorBlobDetector    mDetector;
	private Mat                  mSpectrum;
	private Size                 SPECTRUM_SIZE;
	private Scalar               CONTOUR_COLOR;

	private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS:
			{
				mRgba = Highgui.imread(RUTA_RECORTES.concat( "fruit2.jpg" ) );
				mDetector = new ColorBlobDetector();
				mSpectrum = new Mat();
				mBlobColorRgba = new Scalar(255);
				mBlobColorHsv = new Scalar(255);
				SPECTRUM_SIZE = new Size(200, 64);
				CONTOUR_COLOR = new Scalar(255,0,0,255);
			} break;
			default:
			{
				super.onManagerConnected(status);
			} break;
			}
		}
	};

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_seleccion );

		imgActual = 0;

		txtPorcionActual = (TextView)findViewById( R.id.txtPorcionActual );
		txtPorcionActual.setText( "Seleccione la porci�n de fruta" );
		imgSeleccion = (ImageView)findViewById( R.id.imgSelec );
		imgSeleccion.setImageBitmap(BitmapHelper.decodeSampledBitmap(RUTA_RECORTES.concat( "fruit2.jpg" ), 300, 300));
		imgSeleccion.setOnTouchListener( this );
		findViewById( R.id.btnContinuarSel ).setOnClickListener( this );
	}

	@Override
	public void onClick( View arg0 )
	{
		imgActual++;

		switch ( imgActual )
		{
		case 1:
			txtPorcionActual.setText( "Seleccione la porción de grano" );
			imgSeleccion.setImageBitmap(BitmapHelper.decodeSampledBitmap(RUTA_RECORTES.concat( "grain2.jpg" ), 300, 300));
			mRgba = Highgui.imread(RUTA_RECORTES.concat( "grain2.jpg" ) );
			break;
		case 2:
			txtPorcionActual.setText( "Seleccione la porción de proteina" );
			imgSeleccion.setImageBitmap(BitmapHelper.decodeSampledBitmap(RUTA_RECORTES.concat( "protein2.jpg" ), 300, 300));
			mRgba = Highgui.imread(RUTA_RECORTES.concat( "protein2.jpg" ) );
			break;
		case 3:
			txtPorcionActual.setText( "Seleccione la porción de vegetales" );
			imgSeleccion.setImageBitmap(BitmapHelper.decodeSampledBitmap(RUTA_RECORTES.concat( "vegetables2.jpg" ), 300, 300));
			mRgba = Highgui.imread(RUTA_RECORTES.concat( "vegetables2.jpg" ) );
			break;

		default:
			calcular();
			break;
		}
	}

	private void calcular( )
	{

	}

	public boolean onTouch(View v, MotionEvent event) 
	{
		int cols = mRgba.cols();
		int rows = mRgba.rows();

		int x = 0;
		int y = 0;

		double percentX= (double)event.getX()/(double)imgSeleccion.getWidth();
		double percentY= (double)event.getY()/(double)imgSeleccion.getHeight();

		x = ( int ) ( percentX*cols );
		y = ( int ) ( percentY*rows );

		if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;

		Rect touchedRect = new Rect();

		touchedRect.x = (x>4) ? x-4 : 0;
		touchedRect.y = (y>4) ? y-4 : 0;

		touchedRect.width = (x+4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
		touchedRect.height = (y+4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;

		Mat touchedRegionRgba = mRgba.submat(touchedRect);

		Mat touchedRegionHsv = new Mat();
		Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

		// Calculate average color of touched region
		mBlobColorHsv = Core.sumElems(touchedRegionHsv);
		int pointCount = touchedRect.width*touchedRect.height;
		for (int i = 0; i < mBlobColorHsv.val.length; i++)
			mBlobColorHsv.val[i] /= pointCount;

		mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);

		mDetector.setHsvColor(mBlobColorHsv);

		Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);

		touchedRegionRgba.release();
		touchedRegionHsv.release();

		Mat borde = dibujarContornos( );
		Highgui.imwrite(RUTA_RECORTES.concat( "porcionSel.jpg" ), borde );
		imgSeleccion.setImageBitmap(BitmapHelper.decodeSampledBitmap(RUTA_RECORTES.concat( "porcionSel.jpg" ), 300, 300));
		return true; // don't need subsequent touch events
	}

	private Mat dibujarContornos()
	{
		 mDetector.process(mRgba);
         List<MatOfPoint> contours = mDetector.getContours();
         Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR);

         //cuadrado del color escogido
         Mat colorLabel = mRgba.submat(4, 68, 4, 68);
         colorLabel.setTo(mBlobColorRgba);

         Mat spectrumLabel = mRgba.submat(4, 4 + mSpectrum.rows(), 70, 70 + mSpectrum.cols());
         mSpectrum.copyTo(spectrumLabel);
		return mRgba;
	}

	private Scalar converScalarHsv2Rgba(Scalar hsvColor) 
	{
		Mat pointMatRgba = new Mat();
		Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
		Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

		return new Scalar(pointMatRgba.get(0, 0));
	}

	@Override
	public void onResume()
	{
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
	}

}
