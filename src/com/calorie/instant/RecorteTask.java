package com.calorie.instant;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.TextView;
import android.widget.Toast;

import com.calorie.instant.util.MediaHelper;

public class RecorteTask extends AsyncTask<String, Integer, Void>
{
	private TextView text;
	private MainActivity contexto;	

	public RecorteTask(MainActivity applicationContext,
			TextView cameraDescriptionTextView) 
	{
		contexto = applicationContext;
		text = cameraDescriptionTextView;
		text.setText("Un momento por favor... Procesando Imagen");
	}

	public void obtenerSecciones (String pathImage)
	{
		Bitmap drawBack = BitmapFactory.decodeFile(pathImage).copy(Bitmap.Config.ARGB_8888, true);
		int mWidth = drawBack.getWidth()/2;
		int mHeight = drawBack.getHeight()/2;
		int auxVegetables = (int) (drawBack.getHeight()*0.44);
		int auxGrain = (int) (drawBack.getHeight()*0.55);
		
		//crop fruit
		cropImage(drawBack, 0, 0, mWidth, auxVegetables, "fruit"+MediaHelper.secuence);
		//crop vegetables
		cropImage(drawBack, 0, auxVegetables, mWidth, drawBack.getHeight()-auxVegetables, "vegetables" + MediaHelper.secuence);
		//crop grains
		cropImage(drawBack, mWidth, 0, mWidth, auxGrain, "grain"+MediaHelper.secuence);
		//crop protein
		cropImage(drawBack, mWidth, auxGrain, mWidth, drawBack.getHeight()-auxGrain, "protein" +MediaHelper.secuence);
	}
	private void cropImage(Bitmap bitmapOrig, int x1, int y1, int newWidth, int newHeight, String name)
	{
		Bitmap croppedBmp = Bitmap.createBitmap(bitmapOrig, x1, y1, newWidth, newHeight);
		FileOutputStream os;
		try 
		{
			os = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/InstantCalorie/"+ name+".jpg");
			croppedBmp.compress(CompressFormat.JPEG, 100, os);
			os.flush();
			os.close();
			croppedBmp = null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		Toast.makeText(contexto, "Un momento por favor... Procesando Imagen ".concat(String.valueOf(values[0])).concat("/4"), Toast.LENGTH_SHORT).show();
	}
	@Override
	protected Void doInBackground(String... params) {
		obtenerSecciones(params[0]);
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		Toast.makeText(contexto, "Procesamiento completado !!", Toast.LENGTH_SHORT).show();
		contexto.mostrarActividadSeleccion( );
	}

}
