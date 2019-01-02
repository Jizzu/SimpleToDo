package apps.jizzu.simpletodo.data.database

import android.app.Application
import apps.jizzu.simpletodo.data.models.Task
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class TaskListRepository(app: Application) {
    private val mTaskDao = TasksDatabase.getInstance(app).taskDAO()
    private val mAllTasksLiveData = mTaskDao.getTasksLiveData()

    fun getAllTasks() = mAllTasksLiveData

    fun saveTask(task: Task) = Completable.fromCallable { mTaskDao.saveTask(task) }.subscribeOn(Schedulers.io()).subscribe()!!

    fun deleteTask(task: Task) = Completable.fromCallable { mTaskDao.deleteTask(task) }.subscribeOn(Schedulers.io()).subscribe()!!

    fun updateTask(task: Task) = Completable.fromCallable { mTaskDao.updateTask(task) }.subscribeOn(Schedulers.io()).subscribe()!!

    fun updateTaskOrder(tasks: List<Task>) = Completable.fromCallable { mTaskDao.updateTaskOrder(tasks) }.subscribeOn(Schedulers.io()).subscribe()!!

    fun getTasksForSearch(searchText: String) = mTaskDao.getTasksForSearch(searchText)

    fun getTasksList(): ArrayList<Task> {
        val taskList = arrayListOf<Task>()
        Observable.fromCallable { mTaskDao.getTasksList() }.subscribeOn(Schedulers.io())
                .flatMap { tasks -> Observable.fromIterable(tasks) }
                .subscribeBy(onNext = { task -> taskList.add(task) })
        return taskList
    }
}