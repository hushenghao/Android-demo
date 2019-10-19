package com.example.sqlitetrigger

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * db helper
 * SQLiteOpenHelper 实现类
 */
class DBHelper(context: Context) : SQLiteOpenHelper(context, "sql_trigger.db", null, 1) {

    private val tableName = "db_list_table"

    private val colId = "id"
    private val colUserId = "user_id"
    private val colItemId = "item_id"
    private val colDate = "date"

    override fun onCreate(db: SQLiteDatabase) {
        db.beginTransaction()
        try {
            /**
             * CREATE TABLE db_list_table (
             *      id INTEGER PRIMARY KEY AUTOINCREMENT,
             *      user_id TEXT NOT NULL,
             *      item_id INTEGER NOT NULL,
             *      date TimeStamp DEFAULT (datetime('now','localtime'))
             *  );
             *
             *  插入时间戳yyyy-MM-dd HH:mm:ss，更多请查看文章末尾SQLite时间函数
             *  date TimeStamp DEFAULT (datetime('now','localtime'))
             */
            db.execSQL("CREATE TABLE $tableName ($colId INTEGER PRIMARY KEY AUTOINCREMENT,$colUserId TEXT NOT NULL,$colItemId INTEGER NOT NULL,$colDate TimeStamp DEFAULT (datetime('now','localtime')));")
            /**
             * 旧数据触发器
             * CREATE TRIGGER auto_remove BEFORE INSERT
             *      ON db_list_table
             *      BEGIN
             *          DELETE FROM db_list_table WHERE
             *              strftime('%s','now') - strftime('%s',date) >= 30
             *          OR
             *              (NEW.user_id=user_id AND NEW.item_id=item_id);
             *      END;
             *
             * 将当前时间转换为秒数
             * strftime('%s','now')
             */
            db.execSQL("CREATE TRIGGER auto_remove BEFORE INSERT ON $tableName BEGIN DELETE FROM $tableName WHERE strftime('%s','now') - strftime('%s',$colDate) >= 30 OR (NEW.$colUserId=$colUserId AND NEW.$colItemId=$colItemId); END;")
            db.setTransactionSuccessful()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            db.endTransaction()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // upgrade db something
    }

    /**
     * 插入数据
     */
    fun insert(userId: String, itemId: Int): Long {
        val db = writableDatabase
        val values = ContentValues()
        values.put(colUserId, userId)
        values.put(colItemId, itemId)
        val insert = db.insert(tableName, null, values)
        db.close()
        return insert
    }

    /**
     * 查询当前数据的id
     */
    fun query(userId: String, itemId: Int): Long {
        val cursor = readableDatabase.query(
            tableName,
            arrayOf(colId),
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

    /**
     * 查询数据库数据
     */
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