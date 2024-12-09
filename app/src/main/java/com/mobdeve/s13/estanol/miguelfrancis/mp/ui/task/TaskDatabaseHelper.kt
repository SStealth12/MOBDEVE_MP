package com.mobdeve.s13.estanol.miguelfrancis.mp.ui.task

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.mobdeve.s13.estanol.miguelfrancis.mp.objects.Task

class TaskDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "tasks.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "tasks"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DUE_DATE = "due_date"
        const val COLUMN_TYPE = "type"
        const val COLUMN_IS_COMPLETED = "is_completed"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_DUE_DATE TEXT,
                $COLUMN_TYPE TEXT,
                $COLUMN_IS_COMPLETED INTEGER DEFAULT 0
            )
        """.trimIndent()
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addTask(title: String, dueDate: String?, type: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_DUE_DATE, dueDate)
            put(COLUMN_TYPE, type)
            put(COLUMN_IS_COMPLETED, 0)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getAllTasks(): List<Task> {
        val tasks = mutableListOf<Task>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME, null, "$COLUMN_IS_COMPLETED = ?", arrayOf("0"),
            null, null, "$COLUMN_DUE_DATE ASC"
        )
        if (cursor != null && cursor.moveToFirst()) {
            do {
                tasks.add(
                    Task(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor?.close()
        db.close()
        return tasks
    }

    fun updateTask(taskId: Int, title: String, dueDate: String?, type: String, isCompleted: Boolean) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_DUE_DATE, dueDate)
            put(COLUMN_TYPE, type)
            put(COLUMN_IS_COMPLETED, if (isCompleted) 1 else 0)
        }
        db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(taskId.toString()))
        db.close()
    }

    fun markTaskCompleted(taskId: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IS_COMPLETED, 1)
        }
        db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(taskId.toString()))
        db.close()
    }
}
