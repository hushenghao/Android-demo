package com.example.sqlitetrigger

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "sql_trigger.db", null, 1) {


    private val tableName = "db_list_table"
    private val colUserId = "user_id"
    private val colItemId = "item_id"
    private val colDate = "date"

    override fun onCreate(db: SQLiteDatabase?) {
        if (db == null) return

        db.beginTransaction()
        db.execSQL("CREATE TABLE $tableName (id INTEGER PRIMARY KEY AUTOINCREMENT,$colUserId TEXT NOT NULL,$colItemId INTEGER NOT NULL,$colDate TimeStamp DEFAULT (datetime('now','localtime')));")
        // 旧数据触发器
        db.execSQL("CREATE TRIGGER auto_remove BEFORE INSERT ON $tableName BEGIN DELETE FROM $tableName WHERE strftime('%s','now') - strftime('%s',$colDate) >= 30 OR (new.$colUserId=$colUserId AND new.$colItemId=$colItemId); END;")
        db.setTransactionSuccessful()
        db.endTransaction()
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (db == null) return
    }

    fun insert(userId: String, itemId: Int): Long {
        val db = writableDatabase
        val values = ContentValues()
        values.put(colUserId, userId)
        values.put(colItemId, itemId)
        val insert = db.insert(tableName, null, values)
        db.close()
        return insert
    }

    fun query(userId: String, itemId: Int): Long {
        val cursor = readableDatabase.query(
            tableName,
            arrayOf("id"),
            "$colUserId=? and $colItemId=?",
            arrayOf(userId, itemId.toString()),
            null,
            null,
            null
        )
        val id = if (cursor.moveToFirst()) {
            cursor.getLong(0)
        } else {
            -1
        }
        cursor.close()
        return id
    }

    fun query(): List<ListItem> {
        val cursor = readableDatabase.query(
            tableName, null, null,
            null, null, null, null
        )
        val list = ArrayList<ListItem>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            var index = cursor.getColumnIndex(colUserId)
            val userId = cursor.getString(index)
            index = cursor.getColumnIndex(colItemId)
            val itemId = cursor.getInt(index)
            index = cursor.getColumnIndex(colDate)
            val date = cursor.getString(index)
            list.add(ListItem(id, userId, itemId, date))
        }
        cursor.close()
        return list
    }
}