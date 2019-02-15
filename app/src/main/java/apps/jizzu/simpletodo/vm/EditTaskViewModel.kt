package apps.jizzu.simpletodo.vm

import android.app.Application
import apps.jizzu.simpletodo.data.models.Task
import apps.jizzu.simpletodo.vm.base.BaseViewModel

class EditTaskViewModel(app: Application) : BaseViewModel(app) {
    fun deleteTask(task: Task) = repository.deleteTask(task)

    fun updateTask(task: Task) = repository.updateTask(task)
}