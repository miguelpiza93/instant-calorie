package com.calorie.instant;

import java.util.ArrayList;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.calorie.database.DatabaseManager;
import com.calorie.instant.util.BitmapHelper;
import com.calorie.instant.util.Log;

public class MainActivity extends Activity
{
	private static final int REQ_CAMERA_IMAGE = 123;

	private static final String  TAG = "OCVSample::MainActivity";

	private boolean openCVLoaded;

	private TextView resumeTextView;

	private ProgressBar pg;

	private Spinner spFruit;

	private Spinner spGranos;

	private Spinner spProteinas;

	private Spinner spVegetales;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_foods);

		spFruit = (Spinner) findViewById(R.id.spFrutas);
		List<String> list = new ArrayList<String>();
		list.add("Manzana");
		list.add("Uvas");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spFruit.setAdapter(dataAdapter);

		spGranos = (Spinner) findViewById(R.id.spGranos);
		list = new ArrayList<String>();
		list.add("Pasta");
		list.add("Arroz");
		dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spGranos.setAdapter(dataAdapter);

		spProteinas = (Spinner) findViewById(R.id.spProteinas);
		list = new ArrayList<String>();
		list.add("Pollo");
		list.add("Carne");
		dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spProteinas.setAdapter(dataAdapter);

		spVegetales = (Spinner) findViewById(R.id.spVegetales);
		list = new ArrayList<String>();
		list.add("Zanahoria");
		list.add("Lechuga");
		list.add("Tomate");
		dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spVegetales.setAdapter(dataAdapter);

		consultarPesoCercano( "Tomate", "30000" );
	}

	public void onClickContinuar(View button)
	{
		setContentView(R.layout.activity_main);
		pg = (ProgressBar)findViewById( R.id.progressBar );

		String message = "";
		if(cameraNotDetected()){
			message = "No camera detected, clicking the button below will have unexpected behaviour.";
		}
		resumeTextView = (TextView) findViewById(R.id.resume);
		resumeTextView.setText(message);
	}

	private boolean cameraNotDetected() {
		return !getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
	}

	public void onUseCameraClick(View button){
		resumeTextView.setText("");
		Intent intent = new Intent(this, CameraActivity.class);
		startActivityForResult(intent, REQ_CAMERA_IMAGE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQ_CAMERA_IMAGE && resultCode == RESULT_OK){
			String imgPath = data.getStringExtra(CameraActivity.EXTRA_IMAGE_PATH);
			Log.i("Got image path: "+ imgPath);
			displayImage(imgPath);
			RecorteTask recorte = new RecorteTask(this);
			recorte.execute(imgPath);		
			pg.setVisibility( View.VISIBLE );
		} 
		else if(requestCode == 2 && resultCode == RESULT_OK)
		{
			double[] areas = data.getDoubleArrayExtra("areas");
			resumeTextView.setText("Fruta: "+ areas[0] +"\n"+ "Grano: " + areas[1] +"\n"+ "Proteina: "+areas[2] +"\n"+ "Vegetales: " +areas[3]);
		}
		else
			if(requestCode == REQ_CAMERA_IMAGE && resultCode == RESULT_CANCELED){
				Log.i("User didn't take an image");
			}
	}

	private void displayImage(String path) {
		ImageView imageView = (ImageView) findViewById(R.id.image_view_captured_image);
		imageView.setImageBitmap(BitmapHelper.decodeSampledBitmap(path, 300, 250));
	}

	public void mostrarActividadSeleccion()
	{
		pg.setVisibility( View.GONE );
		Intent intent = new Intent( this, SeleccionActivity.class );
		startActivityForResult(intent, 2);
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
				Toast.makeText( getApplicationContext( ), "No se carg√≥ opencv", Toast.LENGTH_SHORT ).show( );
			} break;
			}
		}
	};

	@Override
	public void onResume()
	{
		super.onResume();
		if (!OpenCVLoader.initDebug()) {
			android.util.Log.i(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
			OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
		} else {
			android.util.Log.d(TAG, "OpenCV library found inside package. Using it!");
			mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
		}
	}

	public int consultarPesoCercano(String comida, String area )
	{
		DatabaseManager dbm = new DatabaseManager( this, "AlimentosDB", null, 1 );
		SQLiteDatabase db = dbm.getWritableDatabase( );

		if(db != null)
		{
			String[]  columnas = {"max(Area)", "Peso"};
			String[] args = {comida, area};
			Cursor c = db.query( true, "Imagen", columnas, "Nombre=? And Area <=?", args, null, null, null, null );

			//Nos aseguramos de que existe al menos un registro
			if (c.moveToFirst()) 
			{
				//Recorremos el cursor hasta que no haya m·s registros
				double areaM= c.getDouble(0);
				int peso = c.getInt(1);
			}
		}				
		return -1;
	}
}
