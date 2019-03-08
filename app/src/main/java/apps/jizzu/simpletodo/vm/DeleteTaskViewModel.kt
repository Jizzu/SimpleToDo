package apps.jizzu.simpletodo.vm

import android.app.Application
import apps.jizzu.simpletodo.data.models.Task
import apps.jizzu.simpletodo.vm.base.BaseViewModel

class DeleteTaskViewModel(app: Application) : BaseViewModel(app) {
    fun deleteTask(task: Task) = repository.deleteTask(task)
}