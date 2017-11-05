package apps.jizzu.simpletodo.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import apps.jizzu.simpletodo.model.ModelTask;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "simpletodo_database";

    public static final String TASKS_TABLE = "tasks_table";

    public static final String TASK_ID_COLUMN = "_id";
    public static final String TASK_TITLE_COLUMN = "task_title";
    public static final String TASK_DATE_COLUMN = "task_date";
    public static final String TASK_POSITION_COLUMN = "task_position";

    private static final String TASKS_TABLE_CREATE_SCRIPT = "CREATE TABLE "
            + TASKS_TABLE + " (" + TASK_ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TASK_TITLE_COLUMN + " TEXT NOT NULL, " + TASK_DATE_COLUMN + " LONG, " + TASK_POSITION_COLUMN + " INTEGER);";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TASKS_TABLE_CREATE_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE " + TASKS_TABLE);
        onCreate(sqLiteDatabase);
    }

    /**
     * Saves a specific task to the database.
     */
    public void saveTask(ModelTask task) {
        ContentValues newValues = new ContentValues();

        newValues.put(TASK_TITLE_COLUMN, task.getTitle());
        newValues.put(TASK_DATE_COLUMN, task.getDate());

        getWritableDatabase().insert(TASKS_TABLE, null, newValues);
    }

    /**
     * Removes a specific task from the database.
     */
    public void deleteTask(int position) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TASKS_TABLE, TASK_ID_COLUMN + " = ?", new String[]{String.valueOf(position)});
        db.close();
    }

    /**
     * Removes all tasks from the database.
     */
    public void deleteAllTasks() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TASKS_TABLE, null, null);
        db.close();
    }

    /**
     * Gets all tasks from the database.
     */
    public List<ModelTask> getAllTasks() {
        List<ModelTask> tasksList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + DBHelper.TASKS_TABLE;

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ModelTask task = new ModelTask();
                task.setId(Integer.parseInt(cursor.getString(0)));
                task.setTitle(cursor.getString(1));
                task.setDate(Long.parseLong(cursor.getString(2)));
                tasksList.add(task);
            } while (cursor.moveToNext());
        }
        return tasksList;
    }
}
