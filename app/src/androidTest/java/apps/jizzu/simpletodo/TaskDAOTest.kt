package apps.jizzu.simpletodo

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import apps.jizzu.simpletodo.data.database.TaskDao
import apps.jizzu.simpletodo.data.database.TasksDatabase
import apps.jizzu.simpletodo.data.models.Task
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

@RunWith(AndroidJUnit4::class)
class TaskDAOTest {

    private lateinit var taskDao: TaskDao
    private lateinit var tasksDatabase: TasksDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        tasksDatabase = Room.inMemoryDatabaseBuilder(
                context, TasksDatabase::class.java
        ).build()
        taskDao = tasksDatabase.taskDAO()
    }

    @Test
    fun testSaveTask() {
        val task = Task(1, "", "", Date().time, 1, Date().time)
        taskDao.saveTask(task)

        val tasks = taskDao.getAllTasks()
        assertEquals(1, tasks.size)
    }

    @Test
    fun testGetAllTasks() {
        var task = Task(1, "", "", Date().time, 1, Date().time)
        taskDao.saveTask(task)

        var tasks = taskDao.getAllTasks()
        assertEquals(1, tasks.size)

        task = Task(2, "", "", Date().time, 2, Date().time, false)
        taskDao.saveTask(task)

        tasks = taskDao.getAllTasks()
        assertEquals(2, tasks.size)

        task = Task(3, "", "", Date().time, 2, Date().time, true)
        taskDao.saveTask(task)

        tasks = taskDao.getAllTasks()
        assertEquals(3, tasks.size)
    }

    @Test
    fun testGetAllOpenTasks() {
        var task = Task(1, "", "", Date().time, 1, Date().time)
        taskDao.saveTask(task)

        var tasks = taskDao.getAllOpenTasks()
        assertEquals(1, tasks.size)

        task = Task(2, "", "", Date().time, 2, Date().time, false)
        taskDao.saveTask(task)

        tasks = taskDao.getAllOpenTasks()
        assertEquals(2, tasks.size)

        task = Task(3, "", "", Date().time, 2, Date().time, true)
        taskDao.saveTask(task)

        tasks = taskDao.getAllOpenTasks()
        assertEquals(2, tasks.size)
    }

    @Test
    fun testUpdateTask() {
        var tasks = taskDao.getAllTasks()
        assertEquals(0, tasks.size)

        val task = Task(1, "", "", Date().time, 1, Date().time)
        taskDao.saveTask(task)

        tasks = taskDao.getAllOpenTasks()
        assertEquals("", tasks[0].title)

        tasks[0].title = "Test"
        taskDao.updateTask(tasks[0])

        tasks = taskDao.getAllTasks()
        assertEquals(1, tasks.size)
        assertEquals("Test", tasks[0].title)
    }

    @Test
    fun testUpdateStatusTask() {
        var tasks = taskDao.getAllTasks()
        assertEquals(0, tasks.size)

        val task = Task(1, "Test Me", "Task to Complete", Date().time, 1, Date().time)
        taskDao.saveTask(task)

        tasks = taskDao.getAllOpenTasks()
        assertFalse(tasks[0].taskStatus)

        tasks[0].taskStatus = tasks[0].taskStatus.not()
        taskDao.updateTask(tasks[0])

        tasks = taskDao.getAllOpenTasks()
        assertEquals(0, tasks.size)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        tasksDatabase.close()
    }
}