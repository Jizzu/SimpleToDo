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

    fun updateTaskOrder(position: Int, id: Long) = Completable.fromCallable{ mTaskDao.updateTaskOrder(position, id) }.subscribeOn(Schedulers.io()).subscribe()

//
//    fun removeTask(task: ModelTask) {
//        mTaskDao.removeTask(task)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeBy(
//                        onComplete = { mLiveData.value = ViewStateCompleted }
//                )
//    }
//
//    fun deleteAllTasks() {
//        mTaskDao.deleteAllTasks()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeBy(
//                        onComplete = { mLiveData.value = ViewStateCompleted }
//                )
//    }
//
//    fun getTasksForSearch(title: String) {
//        mTaskDao.getTasksForSearch(title)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeBy(
//                        onNext = { mLiveData.value = ViewStateTaskListIsReady(it) }
//                )
//    }
}