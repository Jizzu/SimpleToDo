package apps.jizzu.simpletodo.adapter

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.TextView
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.activity.EditTaskActivity
import apps.jizzu.simpletodo.alarm.AlarmHelper
import apps.jizzu.simpletodo.database.DBHelper
import apps.jizzu.simpletodo.database.TasksOrderUpdate
import apps.jizzu.simpletodo.model.ModelTask
import apps.jizzu.simpletodo.utils.Utils
import java.util.*
import kotlin.collections.ArrayList

/**
 * Adapters connect the list views (RecyclerView for example) to it's contents (uses the Singleton pattern).
 */
class RecyclerViewAdapter private constructor() : RecyclerViewEmptySupport.EmptyAdapter<RecyclerView.ViewHolder>() {

    private lateinit var mHelper: DBHelper
    private lateinit var mContext: Context
    private lateinit var mCallback: AdapterCallback

    private val mAlarmHelper = AlarmHelper.getInstance()
    private var mCancelButtonIsClicked: Boolean = true

    /**
     * Callback for update general notification data and show FAB from another class.
     */
    interface AdapterCallback {
        fun updateData()
        fun showFAB()
    }

    /**
     * Registers callback from another class.
     */
    fun registerCallback(callback: AdapterCallback) {
        mCallback = callback
    }

    /**
     * Adds a new item to the end of the list.
     */
    fun addTask(item: ModelTask) {
        mTaskList.add(item)
        notifyItemInserted(itemCount - 1)

        Log.d(TAG, "Task with title (${item.title}) and position (${item.position}) added to RecyclerView!")
    }

    /**
     * Adds a new item to the specific position of the list.
     */
    fun addTask(item: ModelTask, position: Int) {
        mTaskList.add(position, item)
        notifyItemInserted(position)

        Log.d(TAG, "Task with title (${mTaskList[position].title}) and position ($position) added to RecyclerView!")
    }

    /**
     * Updates the data of the specific item in the list.
     */
    fun updateTask(updatedTask: ModelTask, position: Int) {
        mTaskList[position] = updatedTask
        notifyItemChanged(position)

        Log.d(TAG, "Task with title (${mTaskList[position].title}) and position ($position) updated in RecyclerView!")
    }

    /**
     * Removes an item from the list (with Snackbar).
     */
    fun removeTask(position: Int, recyclerView: RecyclerView) {
        val taskID = mTaskList[position].id
        val isRemoved = booleanArrayOf(true)
        val timeStamp = mTaskList[position].timeStamp
        mCancelButtonIsClicked = false

        Log.d(TAG, "taskID = $taskID, position = $position")
        Log.d(TAG, "Removing item from position  $position ...")

        mTaskList.removeAt(position)
        notifyItemRemoved(position)

        val snackbar = Snackbar.make(recyclerView, R.string.snackbar_remove_task, Snackbar.LENGTH_LONG)
        snackbar.setAction(R.string.snackbar_undo) {
            if (!mCancelButtonIsClicked) {
                mCancelButtonIsClicked = true
                val task = mHelper.getTask(taskID)
                addTask(task, task.position)
                isRemoved[0] = false
            }
            mCallback.updateData()
        }

        snackbar.view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            // Called when Snackbar appears on the screen.
            override fun onViewAttachedToWindow(view: View) {
                mCallback.showFAB()
            }

            // Called when Snackbar disappears from the screen.
            override fun onViewDetachedFromWindow(view: View) {
                if (isRemoved[0]) {
                    // Removes a notification and alarm
                    mAlarmHelper.removeNotification(timeStamp, mContext)
                    mAlarmHelper.removeAlarm(timeStamp)

                    // Removes a task
                    mHelper.deleteTask(taskID)
                    saveTasksOrderFromDB()
                }
            }
        })
        snackbar.show()
    }

    /**
     * Removes an item from the list (without Snackbar).
     */
    fun removeTask(position: Int) {
        val taskID = mTaskList[position].id
        val timeStamp = mTaskList[position].timeStamp

        mTaskList.removeAt(position)
        notifyItemRemoved(position)

        // Removes a notification and alarm
        mAlarmHelper.removeNotification(timeStamp, mContext)
        mAlarmHelper.removeAlarm(timeStamp)

        // Removes a task
        mHelper.deleteTask(taskID)
        saveTasksOrderFromDB()
    }

    /**
     * Removes all items from the list.
     */
    fun removeAllTasks() {
        if (itemCount != 0) {
            mTaskList = ArrayList()
            notifyDataSetChanged()
        }
    }

    /**
     * Moves an item in the list.
     */
    fun moveTask(fromPosition: Int, toPosition: Int) {
        Log.d(TAG, "fromPosition: $fromPosition toPosition: $toPosition")

        if (fromPosition < toPosition) {
            // Move down
            for (i in fromPosition until toPosition) {
                Collections.swap(mTaskList, i, i + 1)
                mTaskList[i].position = i
                mTaskList[i + 1].position = i + 1

                Log.d(TAG, "Task with title ${mTaskList[i].title} has new position = ${mTaskList[i].position}")
                Log.d(TAG, "Task with title ${mTaskList[i + 1].title} has new position = ${mTaskList[i + 1].position}")
            }
        } else {
            // Move up
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(mTaskList, i, i - 1)
                mTaskList[i].position = i
                mTaskList[i - 1].position = i - 1

                Log.d(TAG, "Task with title ${mTaskList[i].title} has new position = ${mTaskList[i].position}")
                Log.d(TAG, "Task with title ${mTaskList[i - 1].title} has new position = ${mTaskList[i - 1].position}")
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        saveTasksOrderFromRV()
    }

    /**
     * Saves the new tasks order from RecyclerView list to the database.
     */
    private fun saveTasksOrderFromRV() {
        for (task in mTaskList) {
            task.position = mTaskList.indexOf(task)

            val order = TasksOrderUpdate(mContext)
            order.execute(task)
        }
    }

    /**
     * Saves the new tasks order to the database.
     */
    private fun saveTasksOrderFromDB() {
        val taskList = mHelper.getAllTasks()

        for (task in taskList) {
            task.position = taskList.indexOf(task)

            val order = TasksOrderUpdate(mContext)
            order.execute(task)
        }
    }

    fun reloadTasks() {
        val backupList = ArrayList<ModelTask>()
        backupList.addAll(mTaskList)

        removeAllTasks()
        for (task in backupList) {
            addTask(task)
        }
    }

    /**
     * Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item
     * parent: The ViewGroup into which the new View will be added after it is bound to an adapter position
     * viewType: The view type of the new View.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.model_task, parent, false)
        val title = v.findViewById<TextView>(R.id.tvTaskTitle)
        val date = v.findViewById<TextView>(R.id.tvTaskDate)

        mContext = parent.context
        mHelper = DBHelper.getInstance(mContext)

        return TaskViewHolder(v, title, date)
    }

    /**
     * Called by RecyclerView to display the data at the specified position
     * holder: The ViewHolder which should be updated to represent the contents of the item at the given position in the data set
     * position: The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val task = mTaskList[position]

        val taskViewHolder = holder as TaskViewHolder
        val itemView = taskViewHolder.itemView
        itemView.setOnClickListener {
            val intent = Intent(mContext, EditTaskActivity::class.java)

            intent.putExtra("id", task.id)
            intent.putExtra("title", task.title)
            intent.putExtra("position", position)
            intent.putExtra("time_stamp", task.timeStamp)

            if (task.date != 0L) {
                intent.putExtra("date", task.date)
            }
            mContext.startActivity(intent)
        }

        holder.itemView.isEnabled = true

        taskViewHolder.title.text = task.title

        if (task.date != 0L) {
            Log.d(TAG, "TASK WITH DATE")
            taskViewHolder.title.setPadding(0, 0, 0, 0)
            taskViewHolder.title.gravity = Gravity.CENTER_VERTICAL
            taskViewHolder.date.visibility = View.VISIBLE
            when {
                DateUtils.isToday(task.date) -> {
                    taskViewHolder.date.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                    taskViewHolder.date.text = mContext.getString(R.string.reminder_today) + " " + Utils.getTime(task.date)
                }
                DateUtils.isToday(task.date + DateUtils.DAY_IN_MILLIS) -> {
                    taskViewHolder.date.setTextColor(ContextCompat.getColor(mContext, R.color.red))
                    taskViewHolder.date.text = mContext.getString(R.string.reminder_yesterday) + " " + Utils.getTime(task.date)
                }
                DateUtils.isToday(task.date - DateUtils.DAY_IN_MILLIS) -> taskViewHolder.date.text = mContext.getString(R.string.reminder_tomorrow) + " " + Utils.getTime(task.date)
                task.date < Calendar.getInstance().timeInMillis -> {
                    taskViewHolder.date.setTextColor(ContextCompat.getColor(mContext, R.color.red))
                    taskViewHolder.date.text = Utils.getFullDate(task.date)
                }
                else -> taskViewHolder.date.text = Utils.getFullDate(task.date)
            }
        } else {
            Log.d(TAG, "TASK WITHOUT DATE")

            // Get the resolution of the user's screen
            val displayMetrics = DisplayMetrics()
            (mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(displayMetrics)
            val width = displayMetrics.widthPixels
            val height = displayMetrics.heightPixels
            Log.d(TAG, "width = $width, height = $height")

            taskViewHolder.date.visibility = View.GONE
            if (width >= 1080 || height >= 1776) {
                taskViewHolder.title.setPadding(0, 27, 0, 27)
            } else if (width >= 720 || height >= 1184) {
                taskViewHolder.title.setPadding(0, 20, 0, 20)
            } else if (width >= 480 || height >= 800) {
                taskViewHolder.title.setPadding(0, 15, 0, 15)
            }
            taskViewHolder.title.gravity = Gravity.CENTER_VERTICAL
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     */
    override fun getItemCount() = mTaskList.size

    /**
     * This class helps to get a reference to each element of a particular list item.
     */
    inner class TaskViewHolder internal constructor(itemView: View, internal var title: TextView, internal var date: TextView) : RecyclerView.ViewHolder(itemView)

    companion object {
        private var mInstance: RecyclerViewAdapter? = null

        var mTaskList: MutableList<ModelTask> = ArrayList()

        /**
         * This static method ensures that only one RecyclerViewAdapter will ever exist at any given time.
         */
        fun getInstance(): RecyclerViewAdapter {
            if (mInstance == null) {
                mInstance = RecyclerViewAdapter()
            }
            return mInstance as RecyclerViewAdapter
        }
    }
}