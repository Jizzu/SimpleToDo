package apps.jizzu.simpletodo.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import apps.jizzu.simpletodo.model.ModelTask;

import static android.content.ContentValues.TAG;

/**
 * Class for managing the SQLite database.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "simpletodo_database";

    public static final String TASKS_TABLE = "tasks_table";

    public static final String TASK_ID_COLUMN = "_id";
    public static final String TASK_TITLE_COLUMN = "task_title";
    public static final String TASK_DATE_COLUMN = "task_date";
    public static final String TASK_POSITION_COLUMN = "task_position";

    private static final String TASKS_TABLE_CREATE_SCRIPT = "CREATE TABLE "
            + TASKS_TABLE + " (" + TASK_ID_COLUMN + " INTEGER PRIMARY KEY, "
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
    public long saveTask(ModelTask task) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues newValues = new ContentValues();

        // Don't read the id value of the task, because SQLite will generate it itself when adding new entry to the database.
        newValues.put(TASK_TITLE_COLUMN, task.getTitle());
        newValues.put(TASK_DATE_COLUMN, task.getDate());
        newValues.put(TASK_POSITION_COLUMN, task.getPosition());

        long id = db.insert(TASKS_TABLE, null, newValues);
        db.close();

        Log.d(TAG, "Task with ID (" + id + "), Title (" + task.getTitle() + "), Date (" + task.getDate() + ") Position (" + task.getPosition() + ") saved to DB!");
        return id;
    }

    /**
     * Updates the task position in the database.
     */
    public void updateTask(ModelTask task) {
        ContentValues updatedValues = new ContentValues();
        updatedValues.put(TASK_POSITION_COLUMN, task.getPosition());
        getWritableDatabase().update(TASKS_TABLE, updatedValues, TASK_ID_COLUMN + " = ?", new String[]{String.valueOf(task.getId())});

        Log.d(TAG, "Task with ID (" + task.getId() + "), Title (" + task.getTitle() + "), Date (" + task.getDate() + ") Position (" + task.getPosition() + ") updated in DB!");
    }

    /**
     * Removes a specific task from the database.
     */
    public void deleteTask(ModelTask task) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TASKS_TABLE, TASK_ID_COLUMN + " = ?", new String[]{String.valueOf(task.getId())});
        db.close();

        Log.d(TAG, "Task with ID (" + task.getId() + "), Title (" + task.getTitle() + "), Date (" + task.getDate() + ") Position (" + task.getPosition() + ") deleted from DB!");
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
                task.setPosition(Integer.parseInt(cursor.getString(3)));

                Log.d(TAG, "Task with ID (" + task.getId() + "), Title (" + task.getTitle() + "), Date (" + task.getDate() + ") Position (" + task.getPosition() + ") extracted from DB!");
                tasksList.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Just a simple bubble sort algorithm to sort array of tasks by their position.
        for (int i = 0; i < tasksList.size() - 1; i++) {
            for (int j = 0; j < tasksList.size() - i - 1; j++) {
                if (tasksList.get(j).getPosition() > tasksList.get(j + 1).getPosition()) {
                    Collections.swap(tasksList, j, j + 1);
                }
            }
        }
        return tasksList;
    }
}
