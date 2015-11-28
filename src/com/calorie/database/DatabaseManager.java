package com.calorie.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager extends SQLiteOpenHelper
{
	//Sentencias SQL para creacion de la tabla
    private String sqlCreateGrupo = "CREATE TABLE Imagen (Id INTEGER PRIMARY KEY, Nombre TEXT, " +
    		"Tipo Text, Area REAL, Peso INTEGER)";
    
	public DatabaseManager( Context context, String name, CursorFactory factory, int version )
	{
		super( context, name, factory, version );
	}

	@Override
	public void onCreate( SQLiteDatabase db )
	{
		db.execSQL(sqlCreateGrupo);
		db.execSQL(sqlCreateGrupo);
	}	

	@Override
	public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion )
	{
		//NOTA: Por simplicidad del ejemplo aqu� utilizamos directamente la opci�n de
        //      eliminar la tabla anterior y crearla de nuevo vac�a con el nuevo formato.
        //      Sin embargo lo normal ser� que haya que migrar datos de la tabla antigua
        //      a la nueva, por lo que este m�todo deber�a ser m�s elaborado.
		//Se elimina la versi�n anterior de la tabla
        db.execSQL("DROP TABLE IF EXISTS GrupoAlimenticio");
        db.execSQL(sqlCreateGrupo);
	}
}
