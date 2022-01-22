package com.c0rporation.broodminderexporter

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import java.io.File


class DatabaseParser(val context: Context, val dbFile: File) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        // Nothing to do
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Nothing to do
    }

    override fun getWritableDatabase(): SQLiteDatabase {
        throw RuntimeException("The $DATABASE_NAME database is not writable.")
    }

    override fun getReadableDatabase(): SQLiteDatabase {
        return SQLiteDatabase.openDatabase(dbFile, SQLiteDatabase.OpenParams.Builder().addOpenFlags(SQLiteDatabase.OPEN_READONLY).build())
    }

    companion object {
        const val DATABASE_NAME = "mydb"
        const val DATABASE_VERSION = 1
    }

    fun getBroodMinderData(): BroodMinderRecord {
        val c = readableDatabase.rawQuery("SELECT DeviceId,Timestamp,Temperature,Humidity,Battery FROM StoredSensorReading", arrayOf())
        c.moveToFirst()
        return BroodMinderRecord(c.getString(0).removePrefix("loc_"), c.getInt(1), c.getFloat(2), c.getInt(3), c.getInt(4))
    }

}