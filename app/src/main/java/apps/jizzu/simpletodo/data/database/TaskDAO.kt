package apps.jizzu.simpletodo.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import apps.jizzu.simpletodo.data.models.Task

@Dao
interface TaskDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveTask(task: Task)

    @Update
    fun updateTask(task: Task)

    @Query("UPDATE tasks_table SET task_position = :position WHERE _id = :id")
    fun updateTaskOrder(position: Int, id: Long)

    @Delete
    fun deleteTask(task: Task)

    @Query("SELECT * FROM tasks_table ORDER BY task_position")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("SELECT count(*) FROM tasks_table")
    fun getTasksCount(): Int

//    @Query("DELETE FROM tasks_table")
//    fun deleteAllTasks(): Completable
//
//    @Query("SELECT * FROM tasks_table WHERE _id = :id")
//    fun getTask(id: Long): Single<ModelTask>
//
//    @Query("SELECT * FROM tasks_table WHERE task_title = :title")
//    fun getTasksForSearch(title: String): Observable<List<ModelTask>>
}