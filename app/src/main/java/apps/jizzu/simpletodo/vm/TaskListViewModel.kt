package apps.jizzu.simpletodo.vm

import android.app.Application
import apps.jizzu.simpletodo.data.models.Task
import apps.jizzu.simpletodo.vm.base.BaseViewModel

class TaskListViewModel(app: Application) : BaseViewModel(app) {
    val liveData = repository.getAllTasks()

    fun getAllTasks() = repository.getAllTasks()

    fun updateTask(task: Task) = repository.updateTask(task)

    fun updateTaskOrder(position: Int, id: Long) = repository.updateTaskOrder(position, id)

    fun deleteTask(task: Task) = repository.deleteTask(task)

    fun saveTask(task: Task) = repository.saveTask(task)
}