package com.calorie.instant;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.calorie.instant.util.MediaHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.TextView;
import android.widget.Toast;

public class RecorteTask extends AsyncTask<String, Integer, Void>
{
	private TextView text;
	private Context contexto;

	public RecorteTask(Context applicationContext,
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
		publishProgress(1);
		//setTexto("Un momento por favor... Procesando Imagen 1/4");
		//crop vegetables
		cropImage(drawBack, 0, auxVegetables, mWidth, drawBack.getHeight()-auxVegetables, "vegetables" + MediaHelper.secuence);
		publishProgress(2);
		//setTexto("Un momento por favor... Procesando Imagen 2/4");
		//crop grains
		cropImage(drawBack, mWidth, 0, mWidth, auxGrain, "grain"+MediaHelper.secuence);
		publishProgress(3);
		//setTexto("Un momento por favor... Procesando Imagen 3/4");
		//crop protein
		cropImage(drawBack, mWidth, auxGrain, mWidth, drawBack.getHeight()-auxGrain, "protein" +MediaHelper.secuence);
		publishProgress(4);
		//setTexto("Un momento por favor... Procesando Imagen 4/4");
		//setTexto("Procesamiento completado !! ");
	}
	private void cropImage(Bitmap bitmapOrig, int x1, int y1, int newWidth, int newHeight, String name)
	{
		Bitmap croppedBmp = bitmapOrig.createBitmap(bitmapOrig, x1, y1, newWidth, newHeight);
		FileOutputStream os;
		try 
		{
			os = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/InstantCalorie/"+ name+".png");
			croppedBmp.compress(CompressFormat.PNG, 50, os);
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
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		Toast.makeText(contexto, "Procesamiento completado !!", Toast.LENGTH_SHORT).show();
	}


}
