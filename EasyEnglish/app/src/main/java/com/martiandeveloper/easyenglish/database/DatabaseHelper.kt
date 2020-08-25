@file:Suppress("PrivatePropertyName")

package com.martiandeveloper.easyenglish.database

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class DatabaseHelper(private val myContext: Context) :
    SQLiteOpenHelper(myContext, DB_NAME, null, 1) {

    private val DB_PATH: String
    private var myDataBase: SQLiteDatabase? = null

    fun createDataBase() {
        val dbExist = checkDataBase()

        if (!dbExist) {
            this.readableDatabase
            try {
                myContext.deleteDatabase(DB_NAME)
                copyDataBase()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun checkDataBase(): Boolean {
        var checkDB: SQLiteDatabase? = null

        try {
            val myPath = DB_PATH + DB_NAME
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY)
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }

        checkDB?.close()

        return checkDB != null
    }

    @Throws(IOException::class)
    private fun copyDataBase() {
        val myInput = myContext.assets.open(DB_NAME)
        val outFileName = DB_PATH + DB_NAME
        val myOutput: OutputStream = FileOutputStream(outFileName)
        val buffer = ByteArray(1024)
        var length: Int

        while (myInput.read(buffer).also { length = it } > 0) {
            myOutput.write(buffer, 0, length)
        }

        myOutput.flush()
        myOutput.close()
        myInput.close()
    }

    @Throws(SQLException::class)
    fun openDataBase() {
        val myPath = DB_PATH + DB_NAME
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE)
    }

    @Synchronized
    override fun close() {
        if (myDataBase != null) myDataBase!!.close()
        super.close()
    }

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {}

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        try {
            this.readableDatabase
            myContext.deleteDatabase(DB_NAME)
            copyDataBase()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @SuppressLint("Recycle")
    fun getMeaning(text: String): Cursor? {
        return try {
            myDataBase!!.rawQuery(
                "SELECT en_definition,example,synonyms,antonyms FROM words WHERE en_word==UPPER('$text')",
                null
            )
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        private const val DB_NAME = "eng_dictionary.db"
    }

    init {
        @SuppressLint("SdCardPath") val path = "/data/data/"
        DB_PATH = path + myContext.packageName + "/" + "databases/"
    }
}
