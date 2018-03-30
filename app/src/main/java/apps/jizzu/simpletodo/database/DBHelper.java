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
 * Class for managing the SQLite database (uses the Singleton pattern).
 */
public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper mInstance;

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "simpletodo_database";

    public static final String TASKS_TABLE = "tasks_table";

    public static final String TASK_ID_COLUMN = "_id";
    public static final String TASK_TITLE_COLUMN = "task_title";
    public static final String TASK_DATE_COLUMN = "task_date";
    public static final String TASK_POSITION_COLUMN = "task_position";
    public static final String TASK_TIME_STAMP_COLUMN = "task_time_stamp";

    public static final String TASKS_TABLE_CREATE_SCRIPT = "CREATE TABLE "
            + TASKS_TABLE + " (" + TASK_ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TASK_TITLE_COLUMN + " TEXT NOT NULL, " + TASK_DATE_COLUMN + " LONG, " + TASK_POSITION_COLUMN + " INTEGER, " + TASK_TIME_STAMP_COLUMN + " LONG);";

    public static final String SELECTION_LIKE_TITLE = TASK_TITLE_COLUMN + " LIKE ?";

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
     * Constructor is private to prevent direct instantiation.
     */
    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This static method ensures that only one DBHelper will ever exist at any given time.
     * If the mInstance object has not been initialized, one will be created.
     * If one has already been created then it will simply be returned.
     */
    public static synchronized DBHelper getInstance(Context context) {

        if (mInstance == null) {
            mInstance = new DBHelper(context.getApplicationContext());
        }
        return mInstance;
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
        newValues.put(TASK_TIME_STAMP_COLUMN, task.getTimeStamp());

        long id = db.insert(TASKS_TABLE, null, newValues);
        db.close();

        Log.d(TAG, "Task with ID (" + id + "), Title (" + task.getTitle() + "), Date (" + task.getDate() + "), Position (" + task.getPosition() + ") saved to DB!");
        return id;
    }

    /**
     * Updates the task position in the database.
     */
    public void updateTaskPosition(ModelTask task) {
        ContentValues updatedValues = new ContentValues();
        updatedValues.put(TASK_POSITION_COLUMN, task.getPosition());
        this.getWritableDatabase().update(TASKS_TABLE, updatedValues, TASK_ID_COLUMN + " = ?", new String[]{String.valueOf(task.getId())});

        Log.d(TAG, "Task with ID (" + task.getId() + "), Title (" + task.getTitle() + "), Date (" + task.getDate() + "), Position (" + task.getPosition() + ") updated in DB!");
    }

    /**
     * Updates the task title and date in the database.
     */
    public void updateTask(ModelTask task) {
        ContentValues updatedValues = new ContentValues();
        updatedValues.put(TASK_TITLE_COLUMN, task.getTitle());
        updatedValues.put(TASK_DATE_COLUMN, task.getDate());

        this.getWritableDatabase().update(TASKS_TABLE, updatedValues, TASK_ID_COLUMN + " = ?", new String[]{String.valueOf(task.getId())});
        Log.d(TAG, "Task with ID (" + task.getId() + "), Title (" + task.getTitle() + "), Date (" + task.getDate() + "), Position (" + task.getPosition() + ") updated in DB!");
    }

    /**
     * Removes a specific task from the database.
     */
    public void deleteTask(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TASKS_TABLE, TASK_ID_COLUMN + " = ?", new String[]{String.valueOf(id)});
        db.close();

        Log.d(TAG, "Task with ID (" + id + ") deleted from DB!");
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
     * Gets a specific task from the database.
     */
    public ModelTask getTask(long id) {
        ModelTask task = null;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TASKS_TABLE, new String[]{TASK_ID_COLUMN,
                        TASK_TITLE_COLUMN, TASK_DATE_COLUMN, TASK_POSITION_COLUMN, TASK_TIME_STAMP_COLUMN}, TASK_ID_COLUMN + " = ?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor.moveToFirst()) {
            String title = cursor.getString(1);
            long date = Long.parseLong(cursor.getString(2));
            int position = Integer.parseInt(cursor.getString(3));
            long timeStamp = Long.parseLong(cursor.getString(4));

            task = new ModelTask(id, title, date, position, timeStamp);
            Log.d(TAG, "Task with ID (" + task.getId() + "), Title (" + task.getTitle() + "), Date (" + task.getDate() + "), Position (" + task.getPosition() + ") get from DB!");
        }
        cursor.close();

        return task;
    }

    /**
     * Gets a specific tasks for searching by title.
     */
    public List<ModelTask> getTasks(String selection, String[] selectionArgs, String orderBy) {
        List<ModelTask> tasks = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.query(DBHelper.TASKS_TABLE, null, selection, selectionArgs, null, null, orderBy);

        if (c.moveToFirst()) {
            do {
                int id = c.getInt(c.getColumnIndex(DBHelper.TASK_ID_COLUMN));
                String title = c.getString(c.getColumnIndex(DBHelper.TASK_TITLE_COLUMN));
                long date = c.getLong(c.getColumnIndex(DBHelper.TASK_DATE_COLUMN));
                int position = c.getInt(c.getColumnIndex(DBHelper.TASK_POSITION_COLUMN));
                long timeStamp = c.getLong(c.getColumnIndex(DBHelper.TASK_TIME_STAMP_COLUMN));

                ModelTask modelTask = new ModelTask(id, title, date, position, timeStamp);
                tasks.add(modelTask);
            } while (c.moveToNext());
        }
        c.close();

        return tasks;
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
                task.setTimeStamp(Long.parseLong(cursor.getString(4)));

                Log.d(TAG, "Task with ID (" + task.getId() + "), Title (" + task.getTitle() + "), Date (" + task.getDate() + "), Position (" + task.getPosition() + ") extracted from DB!");
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
