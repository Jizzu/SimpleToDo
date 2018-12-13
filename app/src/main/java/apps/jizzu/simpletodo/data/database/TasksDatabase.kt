package apps.jizzu.simpletodo.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import apps.jizzu.simpletodo.data.models.Task

@Database(entities = [Task::class], version = 2)
abstract class TasksDatabase : RoomDatabase() {

    abstract fun taskDAO(): TaskDao

    companion object {
        private var mInstance: TasksDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {

            }
        }

        fun getInstance(context: Context): TasksDatabase {
            if (mInstance == null) {
                synchronized(TasksDatabase::class.java) {
                    if (mInstance == null) {
                        mInstance = Room.databaseBuilder(context,
                                TasksDatabase::class.java, "database")
                                .addMigrations(MIGRATION_1_2)
                                .build()
                    }
                }
            }
            return mInstance!!
        }
    }
}