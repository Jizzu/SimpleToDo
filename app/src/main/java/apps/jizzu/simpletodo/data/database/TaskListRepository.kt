package apps.jizzu.simpletodo.data.database

import android.app.Application
import apps.jizzu.simpletodo.data.models.Task
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

class TaskListRepository(app: Application) {
    private val mTaskDao = TasksDatabase.getInstance(app).taskDAO()
    private val mLiveData = mTaskDao.getAllTasks()

    fun getAllTasks() = mLiveData

    fun saveTask(task: Task) = Completable.fromCallable{ mTaskDao.saveTask(task) }.subscribeOn(Schedulers.io()).subscribe()

    fun deleteTask(task: Task) = Completable.fromCallable{ mTaskDao.deleteTask(task) }.subscribeOn(Schedulers.io()).subscribe()

    fun updateTask(task: Task) = Completable.fromCallable{ mTaskDao.updateTask(task) }.subscribeOn(Schedulers.io()).subscribe()

    fun updateTaskOrder(tasks: List<Task>) = Completable.fromCallable{ mTaskDao.updateTaskOrder(tasks) }.subscribeOn(Schedulers.io()).subscribe()
}