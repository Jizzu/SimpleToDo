package apps.jizzu.simpletodo.vm

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import apps.jizzu.simpletodo.data.models.Task
import apps.jizzu.simpletodo.vm.base.BaseViewModel

class SearchTasksViewModel(app: Application) : BaseViewModel(app) {
    val searchInputLiveData: MutableLiveData<String> = MutableLiveData()
    val searchResultLiveData: LiveData<List<Task>> = Transformations.switchMap(searchInputLiveData) {
        if (it.isNotEmpty()) {
            repository.getTasksForSearch(it)
        } else {
            MutableLiveData()
        }
    }
}