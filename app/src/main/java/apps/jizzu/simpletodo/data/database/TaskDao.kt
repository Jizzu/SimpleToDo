package apps.jizzu.simpletodo.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import apps.jizzu.simpletodo.data.models.Task

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveTask(task: Task)

    @Update
    fun updateTask(task: Task)

    @Update
    fun updateTaskOrder(tasks: List<Task>)

    @Delete
    fun deleteTask(task: Task)

    @Query("DELETE FROM tasks_table")
    fun deleteAllTasks()

    @Query("SELECT * FROM tasks_table ORDER BY task_position")
    fun getAllTasksLiveData(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks_table ORDER BY task_position")
    fun getAllTasks(): List<Task>

    @Query("SELECT * FROM tasks_table WHERE task_title LIKE '%' || :searchText || '%'")
    fun getTasksForSearch(searchText: String): LiveData<List<Task>>
}