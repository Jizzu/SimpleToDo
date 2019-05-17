package apps.jizzu.simpletodo.ui.recycler

import android.content.ContentValues.TAG
import android.content.Context
import android.text.format.DateUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.data.models.Task
import apps.jizzu.simpletodo.utils.DateAndTimeFormatter
import apps.jizzu.simpletodo.utils.gone
import apps.jizzu.simpletodo.utils.visible
import java.util.*
import kotlin.collections.ArrayList

class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.TaskViewHolder>() {
    private var mTaskList = arrayListOf<Task>()
    private lateinit var mContext: Context

    fun updateData(tasks: List<Task>) {
        val result = DiffUtil.calculateDiff(TaskDiffUtilCallback(mTaskList, tasks))
        mTaskList = tasks as ArrayList<Task>
        result.dispatchUpdatesTo(this)
    }

    fun updateTaskOrder(fromPosition: Int, toPosition: Int) = notifyItemMoved(fromPosition, toPosition)

    fun removeTask(position: Int) {
        mTaskList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun reloadTasks() {
        val backupList = ArrayList<Task>()
        backupList.addAll(mTaskList)
        mTaskList.clear()

        for (task in backupList) {
            mTaskList.add(task)
        }
        notifyDataSetChanged()
    }

    fun getTaskAtPosition(position: Int) = mTaskList[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.task_item_view, parent, false)
        val status = v.findViewById<AppCompatCheckBox>(R.id.cbTaskStatus)
        val title = v.findViewById<TextView>(R.id.tvTaskTitle)
        val note = v.findViewById<ImageView>(R.id.ivTaskNote)
        val date = v.findViewById<TextView>(R.id.tvTaskDate)
        mContext = parent.context

        return TaskViewHolder(v, status, title, note, date)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = mTaskList[holder.adapterPosition]

        val itemView = holder.itemView
        itemView.setOnClickListener { view -> clickListener?.onTaskClick(view, holder.adapterPosition) }

        holder.itemView.isEnabled = true
        holder.title.text = task.title

        holder.status.setOnClickListener {
            taskCompletionListener?.onTaskStatusChanged(itemView, holder.adapterPosition)
        }
        holder.status.isChecked = task.taskStatus

        if (task.note.isNotEmpty()) {
            holder.note.visible()
        } else holder.note.gone()

        if (task.date != 0L) {
            holder.title.setPadding(0, 0, 0, 0)
            holder.title.gravity = Gravity.CENTER_VERTICAL
            holder.date.visible()
            when {
                // Today
                DateUtils.isToday(task.date) -> {
                    holder.date.setTextColor(ContextCompat.getColor(mContext, R.color.blue))
                    holder.date.text = mContext.getString(R.string.reminder_today, DateAndTimeFormatter.getTime(task.date))
                }

                // Yesterday
                DateUtils.isToday(task.date + DateUtils.DAY_IN_MILLIS) -> {
                    holder.date.setTextColor(ContextCompat.getColor(mContext, R.color.red))
                    holder.date.text = mContext.getString(R.string.reminder_yesterday, DateAndTimeFormatter.getTime(task.date))
                }

                // Tomorrow
                DateUtils.isToday(task.date - DateUtils.DAY_IN_MILLIS) -> {
                    holder.date.setTextColor(ContextCompat.getColor(mContext, R.color.blue))
                    holder.date.text = mContext.getString(R.string.reminder_tomorrow, DateAndTimeFormatter.getTime(task.date))
                }

                // Far past
                task.date < Calendar.getInstance().timeInMillis -> {
                    holder.date.setTextColor(ContextCompat.getColor(mContext, R.color.red))
                    holder.date.text = DateAndTimeFormatter.getFullDate(task.date)
                }

                // Far future
                else -> {
                    holder.date.setTextColor(ContextCompat.getColor(mContext, R.color.blue))
                    holder.date.text = mContext.getString(R.string.date_format_at, DateAndTimeFormatter.getFullDate(task.date), DateAndTimeFormatter.getTime(task.date))
                }
            }
        } else {
            // Get the resolution of the user's screen
            val displayMetrics = DisplayMetrics()
            (mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(displayMetrics)
            val width = displayMetrics.widthPixels
            val height = displayMetrics.heightPixels
            Log.d(TAG, "width = $width, height = $height")

            holder.date.gone()
            if (width >= 1080 || height >= 1776) {
                holder.title.setPadding(0, 27, 0, 27)
            } else if (width >= 720 || height >= 1184) {
                holder.title.setPadding(0, 20, 0, 20)
            } else if (width >= 480 || height >= 800) {
                holder.title.setPadding(0, 15, 0, 15)
            }
            holder.title.gravity = Gravity.CENTER_VERTICAL
        }
    }

    override fun getItemCount() = mTaskList.size

    fun setOnItemClickListener(clickListener: ClickListener) {
        RecyclerViewAdapter.clickListener = clickListener
    }

    fun setTaskCompletionListener(completionListener: TaskCompletionListener) {
        taskCompletionListener = completionListener
    }

    interface ClickListener {
        fun onTaskClick(v: View, position: Int)
    }

    interface TaskCompletionListener {
        fun onTaskStatusChanged(v: View, position: Int)
    }

    inner class TaskViewHolder internal constructor(itemView: View, internal var status: AppCompatCheckBox, internal var title: TextView, internal var note: ImageView, internal var date: TextView) : RecyclerView.ViewHolder(itemView)

    companion object {
        private var clickListener: ClickListener? = null
        private var taskCompletionListener: TaskCompletionListener? = null
    }
}