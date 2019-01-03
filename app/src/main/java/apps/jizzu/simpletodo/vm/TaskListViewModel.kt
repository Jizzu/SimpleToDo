package apps.jizzu.simpletodo.vm

import android.app.Application
import apps.jizzu.simpletodo.data.models.Task
import apps.jizzu.simpletodo.vm.base.BaseViewModel

class TaskListViewModel(app: Application) : BaseViewModel(app) {
    val liveData = repository.getAllTasksLiveData()

    fun updateTaskOrder(tasks: List<Task>) = repository.updateTaskOrder(tasks)

    fun deleteTask(task: Task) = repository.deleteTask(task)

    fun saveTask(task: Task) = repository.saveTask(task)
}