package apps.jizzu.simpletodo.vm

import android.app.Application
import android.os.Environment
import apps.jizzu.simpletodo.data.models.Task
import apps.jizzu.simpletodo.service.alarm.AlarmHelper
import apps.jizzu.simpletodo.vm.base.BaseViewModel
import java.io.*
import java.util.*

class RestoreBackupViewModel(val app: Application) : BaseViewModel(app) {
    private val mFile = File(Environment.getExternalStoragePublicDirectory("SimpleToDo"), "Backup.ser")
    private var isRestoredSuccessfully = false

    fun isBackupExist() = mFile.exists()

    fun isBackupRestoredSuccessfully() = isRestoredSuccessfully

    fun restoreBackup() {
        val restoredTasks: List<Task>
        val alarmHelper = AlarmHelper.getInstance()

        isRestoredSuccessfully = try {
            val fileInputStream = FileInputStream(mFile)
            val objectInputStream = ObjectInputStream(fileInputStream)

            @Suppress("UNCHECKED_CAST")
            restoredTasks = objectInputStream.readObject() as List<Task>

            repository.deleteAllTasks()
            for (task in restoredTasks) {
                repository.saveTask(task)

                if (task.date != 0L && task.date > Calendar.getInstance().timeInMillis) {
                    alarmHelper.setAlarm(task)
                }
            }
            objectInputStream.close()
            true
        } catch (exception: Exception) {
            exception.printStackTrace()
            false
        }
    }
}