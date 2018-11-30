package apps.jizzu.simpletodo.vm.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import apps.jizzu.simpletodo.data.database.TaskListRepository

abstract class BaseViewModel(app: Application) : AndroidViewModel(app) {
    val repository = TaskListRepository(app)
}