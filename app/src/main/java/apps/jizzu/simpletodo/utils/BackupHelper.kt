package apps.jizzu.simpletodo.utils

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Environment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Toast
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.adapter.RecyclerViewAdapter
import apps.jizzu.simpletodo.database.DBHelper
import apps.jizzu.simpletodo.model.ModelTask
import java.io.*

/**
 * Class for managing backup file.
 */
class BackupHelper(private val mContext: Context) {
    private val mFile = File(Environment.getExternalStoragePublicDirectory("SimpleToDo"), "Backup.ser")
    private val mAdapter = RecyclerViewAdapter.getInstance()

    fun showCreateDialog() {
        makeFolder()

        if (mFile.exists()) {
            val alertDialog = AlertDialog.Builder(mContext, R.style.DialogTheme)
            alertDialog.setMessage(R.string.backup_create_dialog_message)
            alertDialog.setPositiveButton(R.string.backup_create_dialog_button) { _, _ -> createBackup() }
            alertDialog.setNegativeButton(R.string.action_cancel) { _, _ -> }
            alertDialog.show()
        } else {
            createBackup()
        }
    }

    fun showRestoreDialog() {
        val alertDialog = AlertDialog.Builder(mContext, R.style.DialogTheme)
        alertDialog.setMessage(R.string.backup_restore_dialog_message)
        alertDialog.setPositiveButton(R.string.backup_restore_dialog_button) { _, _ -> restoreBackup() }
        alertDialog.setNegativeButton(R.string.action_cancel) { _, _ -> }
        alertDialog.show()
    }

    private fun createBackup() {
        try {
            mFile.delete()
            Log.d(TAG, "Previous backup file is deleted!")

            val fileOutputStream = FileOutputStream(mFile, true)
            val objectOutputStream = ObjectOutputStream(fileOutputStream)

            objectOutputStream.writeObject(RecyclerViewAdapter.mTaskList)

            for (task in RecyclerViewAdapter.mTaskList) {
                Log.d(TAG, "Object with 1) Title = ${task.title}; 2) Position = ${task.position}; 3) Date = ${task.date} added to backup file!")
            }
            Toast.makeText(mContext, R.string.backup_create_message_success, Toast.LENGTH_SHORT).show()
            objectOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(mContext, R.string.backup_create_message_failure, Toast.LENGTH_SHORT).show()
        }
    }

    private fun restoreBackup() {
        val dbHelper = DBHelper.getInstance(mContext)
        val restoredTasks: List<ModelTask>

        if (mFile.exists()) {
            try {
                val fileInputStream = FileInputStream(mFile)
                val objectInputStream = ObjectInputStream(fileInputStream)

                var newTaskPosition = mAdapter.itemCount - 1

                if (mAdapter.itemCount > 0) {

                    @Suppress("UNCHECKED_CAST")
                    restoredTasks = objectInputStream.readObject() as List<ModelTask>

                    for (task in restoredTasks) {
                        newTaskPosition++

                        task.position = newTaskPosition
                        Log.d(TAG, "Task wit title ${task.title} set to position = ${task.position}")

                        val id = dbHelper.saveTask(task)
                        task.id = id
                        mAdapter.addTask(task, newTaskPosition)
                    }
                } else {
                    @Suppress("UNCHECKED_CAST")
                    restoredTasks = objectInputStream.readObject() as List<ModelTask>

                    for (task in restoredTasks) {
                        dbHelper.saveTask(task)
                        mAdapter.addTask(task)
                    }
                }
                Toast.makeText(mContext, R.string.backup_restore_message_success, Toast.LENGTH_SHORT).show()
                objectInputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(mContext, R.string.backup_restore_message_failure, Toast.LENGTH_SHORT).show()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
                Toast.makeText(mContext, R.string.backup_restore_message_failure, Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(mContext, R.string.backup_restore_message_nothing, Toast.LENGTH_SHORT).show()
        }
    }

    private fun makeFolder() {
        val file = File(Environment.getExternalStorageDirectory().absolutePath, "SimpleToDo")

        if (!file.exists()) {
            val isCreated = file.mkdir()
            if (isCreated) {
                Log.d(TAG, "Folder created successfully!")
            } else {
                Log.d(TAG, "Failed to create folder!")
            }
        } else {
            Log.d(TAG, "Folder already exist!")
        }
    }
}
