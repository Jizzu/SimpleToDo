package apps.jizzu.simpletodo.utils;

import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import apps.jizzu.simpletodo.R;
import apps.jizzu.simpletodo.adapter.RecyclerViewAdapter;
import apps.jizzu.simpletodo.database.DBHelper;
import apps.jizzu.simpletodo.model.ModelTask;

import static android.content.ContentValues.TAG;

/**
 * Class for managing backup file.
 */
public class BackupHelper {

    private Context mContext;
    private File mFile = new File(Environment.getExternalStoragePublicDirectory("SimpleToDo"), "Backup.ser");
    private RecyclerViewAdapter mAdapter = RecyclerViewAdapter.getInstance();


    public BackupHelper(Context context) {
        mContext = context;
    }

    public void showCreateDialog() {
        makeFolder();

        if (mFile.exists()) {
            AlertDialog.Builder alertDialog;
            alertDialog = new AlertDialog.Builder(mContext, R.style.DialogTheme);
            alertDialog.setMessage(R.string.backup_create_dialog_message);
            alertDialog.setPositiveButton(R.string.backup_create_dialog_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    createBackup();
                }
            });
            alertDialog.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            alertDialog.show();
        } else {
            createBackup();
        }
    }

    public void showRestoreDialog() {
        AlertDialog.Builder alertDialog;
        alertDialog = new AlertDialog.Builder(mContext, R.style.DialogTheme);
        alertDialog.setMessage(R.string.backup_restore_dialog_message);
        alertDialog.setPositiveButton(R.string.backup_restore_dialog_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                restoreBackup();
            }
        });
        alertDialog.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.show();
    }

    private void createBackup() {
        try {
            mFile.delete();
            Log.d(TAG, "Previous backup file is deleted!");

            FileOutputStream fileOutputStream = new FileOutputStream(mFile, true);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(RecyclerViewAdapter.mItems);

            for (ModelTask task : RecyclerViewAdapter.mItems) {
                Log.d(TAG, "Object with 1) Title = " + task.getTitle() + "; 2) Position = " + task.getPosition() + "; 3) Date = " + task.getDate() + " added to backup file!");
            }
            Toast.makeText(mContext, R.string.backup_create_message_success, Toast.LENGTH_SHORT).show();
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(mContext, R.string.backup_create_message_failure, Toast.LENGTH_SHORT).show();
        }
    }

    private void restoreBackup() {
        DBHelper dbHelper = DBHelper.getInstance(mContext);
        List<ModelTask> restoredTasks;

        if (mFile.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(mFile);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

                int newTaskPosition = mAdapter.getItemCount() - 1;

                if (mAdapter.getItemCount() > 0) {

                    restoredTasks = (List<ModelTask>) objectInputStream.readObject();

                    for (ModelTask task : restoredTasks) {
                        newTaskPosition++;

                        task.setPosition(newTaskPosition);
                        Log.d(TAG, "Task wit title " + task.getTitle() + " set to position = " + task.getPosition());

                        long id = dbHelper.saveTask(task);
                        task.setId(id);
                        mAdapter.addItem(task, newTaskPosition);
                    }
                } else {
                    restoredTasks = (List<ModelTask>) objectInputStream.readObject();

                    for (ModelTask task : restoredTasks) {
                        dbHelper.saveTask(task);
                        mAdapter.addItem(task);
                    }
                }
                Toast.makeText(mContext, R.string.backup_restore_message_success, Toast.LENGTH_SHORT).show();
                objectInputStream.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(mContext, R.string.backup_restore_message_failure, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, R.string.backup_restore_message_nothing, Toast.LENGTH_SHORT).show();
        }
    }

    private void makeFolder() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "SimpleToDo");

        if (!file.exists()) {
            Boolean isCreated = file.mkdir();
            if (isCreated) {
                Log.d(TAG, "Folder created successfully!");
            } else {
                Log.d(TAG, "Failed to create folder!");
            }
        } else {
            Log.d(TAG, "Folder already exist!");
        }
    }
}
