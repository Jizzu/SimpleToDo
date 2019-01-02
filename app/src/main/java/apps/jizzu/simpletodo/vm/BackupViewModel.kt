package apps.jizzu.simpletodo.vm

import android.app.Application
import android.content.ContentValues.TAG
import android.os.Environment
import android.util.Log
import apps.jizzu.simpletodo.data.models.Task
import apps.jizzu.simpletodo.vm.base.BaseViewModel
import java.io.*


class BackupViewModel(val app: Application) : BaseViewModel(app) {
    private val mFile = File(Environment.getExternalStoragePublicDirectory("SimpleToDo"), "Backup.ser")
    private val mTasks = repository.getTasksList()
    private var isCreatedSuccessfully = false
    private var isRestoredSuccessfully = false

    fun isBackupExist() = mFile.exists()

    fun isBackupCreatedSuccessfully() = isCreatedSuccessfully

    fun isBackupRestoredSuccessfully() = isRestoredSuccessfully

    fun createBackup() {
        val file = File(Environment.getExternalStorageDirectory().absolutePath, "SimpleToDo")
        if (!file.exists()) file.mkdir()
        isCreatedSuccessfully = try {
            mFile.delete()

            val fileOutputStream = FileOutputStream(mFile, true)
            val objectOutputStream = ObjectOutputStream(fileOutputStream)

            objectOutputStream.writeObject(mTasks)
            objectOutputStream.close()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun restoreBackup() {
        val restoredTasks: List<Task>

        isRestoredSuccessfully = try {
            val fileInputStream = FileInputStream(mFile)
            val objectInputStream = ObjectInputStream(fileInputStream)

            if (mTasks.isNotEmpty()) {
                @Suppress("UNCHECKED_CAST")
                restoredTasks = objectInputStream.readObject() as List<Task>
                var newTaskPosition = mTasks.size - 1

                for (task in restoredTasks) {
                    newTaskPosition++

                    task.position = newTaskPosition
                    Log.d(TAG, "Task wit title ${task.title} set to position = ${task.position}")
                    repository.saveTask(task)
                }
            } else {
                @Suppress("UNCHECKED_CAST")
                restoredTasks = objectInputStream.readObject() as List<Task>

                for (task in restoredTasks) {
                    repository.saveTask(task)
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
