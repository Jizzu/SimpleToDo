package apps.jizzu.simpletodo.vm

import android.app.Application
import apps.jizzu.simpletodo.data.models.Task
import apps.jizzu.simpletodo.vm.base.BaseViewModel

class AddTaskViewModel(app: Application) : BaseViewModel(app) {
    fun saveTask(task: Task) = repository.saveTask(task)
}