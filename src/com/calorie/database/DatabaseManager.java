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

		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Lechuga', 'Verduras', 52604.80, 20)");
		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Lechuga', 'Verduras', 59392.00, 65)");
		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Lechuga', 'Verduras', 68104.00, 80)");
		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Lechuga', 'Verduras', 76816.65, 100)");
		
		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Tomate', 'Verduras', 20012.80, 50)");
		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Tomate', 'Verduras', 27516.80, 100)");
		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Tomate', 'Verduras', 36996.80, 143)");
		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Tomate', 'Verduras', 43545.60, 200)");
		
		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Manzana', 'Frutas', 19529.33, 50)");
		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Manzana', 'Frutas', 23985.33, 125)");
		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Manzana', 'Frutas', 29397.33, 143)");
		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Manzana', 'Frutas', 32896.00, 200)");
		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Manzana', 'Frutas', 37056.00, 250)");

		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Zanahoria', 'Verduras', 35771.20, 65)");
		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Zanahoria', 'Verduras', 50168.40, 100)");
		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Zanahoria', 'Verduras', 58283.60, 150)");
		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Zanahoria', 'Verduras', 58998.80, 200)");
		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Zanahoria', 'Verduras', 62398.40, 250)");
		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Zanahoria', 'Verduras', 81451.20, 300)");
	
		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Uvas', 'Frutas', 30491.20, 100)");		
		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Uvas', 'Frutas', 37875.20, 200");
		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Uvas', 'Frutas', 41890.20, 300");

		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Arroz', 'Granos', 42208.00, 100)");
		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Arroz', 'Granos', 69470.40, 300)");

		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Pasta', 'Granos', 40516.80, 100)");
		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Pasta', 'Granos', 49622.40, 150)");
		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Pasta', 'Granos', 54830.40, 200)");
		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Pasta', 'Granos', 60165.40, 250)");
		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Pasta', 'Granos', 65438.40, 300)");	
		
		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Pollo', 'Proteina', 28145.60, 100)");
		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Pollo', 'Proteina', 30446.40, 200)");
		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Pollo', 'Proteina', 38473.60, 300)");
		
		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Carne', 'Proteina', 22638.40, 100)");
		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Carne', 'Proteina', 26617.60, 200)");
		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Carne', 'Proteina', 29875.20, 250)");
		db.execSQL("INSERT INTO Imagen (Nombre, Tipo, Area, Peso) VALUES ('Carne', 'Proteina', 37670.40, 300)");
	}	

	@Override
	public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion )
	{
		//NOTA: Por simplicidad del ejemplo aquí utilizamos directamente la opción de
        //      eliminar la tabla anterior y crearla de nuevo vacía con el nuevo formato.
        //      Sin embargo lo normal será que haya que migrar datos de la tabla antigua
        //      a la nueva, por lo que este método debería ser más elaborado.
		//Se elimina la versión anterior de la tabla
		
        db.execSQL("DROP TABLE IF EXISTS GrupoAlimenticio");
        db.execSQL(sqlCreateGrupo);
	}
}

