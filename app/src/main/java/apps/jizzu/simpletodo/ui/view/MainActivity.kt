package apps.jizzu.simpletodo.ui.view

import android.animation.ValueAnimator
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.data.models.Task
import apps.jizzu.simpletodo.service.alarm.AlarmHelper
import apps.jizzu.simpletodo.service.alarm.AlarmReceiver
import apps.jizzu.simpletodo.service.widget.WidgetProvider
import apps.jizzu.simpletodo.ui.recycler.RecyclerViewAdapter
import apps.jizzu.simpletodo.ui.settings.activity.SettingsActivity
import apps.jizzu.simpletodo.ui.settings.fragment.FragmentDateAndTime
import apps.jizzu.simpletodo.ui.settings.fragment.FragmentNotifications
import apps.jizzu.simpletodo.utils.*
import apps.jizzu.simpletodo.vm.TaskListViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotterknife.bindView
import top.wefor.circularanim.CircularAnim
import java.util.*

class MainActivity : AppCompatActivity() {
    private val mRecyclerView: RecyclerView by bindView(R.id.tasksList)
    private val mFab: FloatingActionButton by bindView(R.id.fab)
    private var mSnackbar: Snackbar? = null

    private lateinit var mAdapter: RecyclerViewAdapter
    private lateinit var mPreferenceHelper: PreferenceHelper
    private lateinit var mNotificationManager: NotificationManager
    private lateinit var mViewModel: TaskListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initToolbar()
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        AlarmHelper.getInstance().init(applicationContext)

        mViewModel = createViewModel()
        mViewModel.liveData.observe(this, Observer<List<Task>> { response -> updateViewState(response) })

        PreferenceHelper.getInstance().init(applicationContext)
        mPreferenceHelper = PreferenceHelper.getInstance()

        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = RecyclerViewAdapter()
        mRecyclerView.adapter = mAdapter

        showChangelogActivity()
        showRecyclerViewAnimation()
        restoreAlarmsAfterMigration()
        createItemTouchHelper()
        initListeners()
        initCallbacks()
    }

    private fun updateViewState(tasks: List<Task>) = if (tasks.isEmpty()) showEmptyView() else showTaskList(tasks)

    private fun showTaskList(tasks: List<Task>) {
        var isNeedToRecount = false
        if (mTaskList.size > tasks.size) isNeedToRecount = true
        mTaskList = tasks as ArrayList<Task>
        if (isNeedToRecount) recountTaskPositions()
        emptyView.gone()
        mAdapter.updateData(mTaskList)
        updateGeneralNotification()
        updateWidget()
    }

    private fun recountTaskPositions() {
        for ((newPosition, task) in mTaskList.withIndex()) {
            task.position = newPosition
        }
        mViewModel.updateTaskOrder(mTaskList)
    }

    private fun showEmptyView() {
        mTaskList = arrayListOf()
        mAdapter.updateData(mTaskList)
        emptyView.visible()
        val anim = AnimationUtils.loadAnimation(this, R.anim.empty_view_animation).apply {
            startOffset = 300
            duration = 300
        }
        updateGeneralNotification()
        updateWidget()
        emptyView.startAnimation(anim)
    }

    private fun showRecyclerViewAnimation() {
        if (mPreferenceHelper.getBoolean(PreferenceHelper.ANIMATION_IS_ON)) {
            val resId = R.anim.layout_animation
            val animation = AnimationUtils.loadLayoutAnimation(this, resId)
            mRecyclerView.layoutAnimation = animation
        }
    }

    private fun createItemTouchHelper() {
        val helper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN // Flags for up and down movement
                val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END // Flags for left and right movement
                return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                moveTask(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteTask(viewHolder.adapterPosition)
            }
        })
        helper.attachToRecyclerView(mRecyclerView)
    }

    private fun deleteTask(position: Int) {
        val deletedTask = mAdapter.getTaskAtPosition(position)
        val isDeletedTaskHasLastPosition = deletedTask.position == mAdapter.itemCount - 1
        val alarmHelper = AlarmHelper.getInstance()
        alarmHelper.removeAlarm(deletedTask.timeStamp)
        mAdapter.removeTask(position)
        mViewModel.deleteTask(deletedTask)
        var isUndoClicked = false

        mSnackbar = Snackbar.make(mRecyclerView, R.string.snackbar_remove_task, Snackbar.LENGTH_LONG)
        mSnackbar?.setAction(R.string.snackbar_undo) {
            mViewModel.saveTask(deletedTask)
            if (deletedTask.date != 0L && deletedTask.date > Calendar.getInstance().timeInMillis) {
                alarmHelper.setAlarm(deletedTask)
            }
            isUndoClicked = true
        }

        mSnackbar?.view?.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(view: View) {
                mFab.show()
            }

            override fun onViewDetachedFromWindow(view: View) {
                if (!isUndoClicked) {
                    alarmHelper.removeNotification(deletedTask.timeStamp, this@MainActivity)
                    if (!isDeletedTaskHasLastPosition) recountTaskPositions()
                }
            }
        })
        mSnackbar?.show()
    }

    private fun moveTask(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            // Move down
            for (i in fromPosition until toPosition) {
                Collections.swap(mTaskList, i, i + 1)
                mTaskList[i].position = i
                mTaskList[i + 1].position = i + 1
            }
        } else {
            // Move up
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(mTaskList, i, i - 1)
                mTaskList[i].position = i
                mTaskList[i - 1].position = i - 1
            }
        }
        mViewModel.updateTaskOrder(mTaskList)
        mAdapter.apply {
            updateTaskOrder(fromPosition, toPosition)
            updateData(mTaskList)
        }
        updateGeneralNotification()
        updateWidget()
    }

    private fun showChangelogActivity() {
        if (mPreferenceHelper.getBoolean(PreferenceHelper.IS_FIRST_APP_LAUNCH)) {
            startActivity(Intent(this, ChangelogActivity::class.java))
            mPreferenceHelper.putBoolean(PreferenceHelper.IS_FIRST_APP_LAUNCH, false)
        }
    }

    private fun restoreAlarmsAfterMigration() {
        if (mPreferenceHelper.getBoolean(PreferenceHelper.IS_AFTER_DATABASE_MIGRATION)) {
            val alarmHelper = AlarmHelper.getInstance()

            for (task in mTaskList) {
                if (task.date != 0L && task.date > Calendar.getInstance().timeInMillis) {
                    alarmHelper.setAlarm(task)
                }
            }
            mPreferenceHelper.putBoolean(PreferenceHelper.IS_AFTER_DATABASE_MIGRATION, false)
        }
    }

    private fun initToolbar() {
        title = ""

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.grey_white)
        }

        if (toolbar != null) {
            toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
            setSupportActionBar(toolbar)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.outline_settings_black_24)
        }
    }

    private fun initListeners() {
        mFab.setOnClickListener { view ->
            if (mSnackbar != null && mSnackbar!!.isShown) {
                mSnackbar?.dismiss()
                mSnackbar = null
            }

            if (mPreferenceHelper.getBoolean(PreferenceHelper.ANIMATION_IS_ON)) {
                val position = mAdapter.itemCount
                val intent = Intent(this@MainActivity, AddTaskActivity::class.java)
                intent.putExtra("position", position)

                CircularAnim.fullActivity(this@MainActivity, view)
                        .colorOrImageRes(R.color.colorPrimary)
                        .duration(300)
                        .go { startActivity(intent) }
            } else {
                val position = mAdapter.itemCount
                val intent = Intent(this@MainActivity, AddTaskActivity::class.java)
                intent.putExtra("position", position)
                startActivity(intent)
            }
        }

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && mFab.isVisible()) {
                    mFab.hide()
                } else if (dy < 0 && mFab.isNotVisible()) {
                    mFab.show()
                }
            }
        })
    }

    fun showTaskDetailsActivity(task: Task) {
        val intent = Intent(this, EditTaskActivity::class.java)

        intent.putExtra("id", task.id)
        intent.putExtra("title", task.title)
        intent.putExtra("position", task.position)
        intent.putExtra("time_stamp", task.timeStamp)

        if (task.date != 0L) {
            intent.putExtra("date", task.date)
        }
        startActivity(intent)
    }

    private fun createViewModel() = ViewModelProviders.of(this@MainActivity).get(TaskListViewModel(application)::class.java)

    private fun updateWidget() {
        val intent = Intent(this, WidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = AppWidgetManager.getInstance(this@MainActivity)
                .getAppWidgetIds(ComponentName(this@MainActivity, WidgetProvider::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(intent)
    }

    private fun updateGeneralNotification() {
        if (mPreferenceHelper.getBoolean(PreferenceHelper.GENERAL_NOTIFICATION_IS_ON)) {
            if (mAdapter.itemCount != 0) showGeneralNotification() else removeGeneralNotification()
        } else removeGeneralNotification()
    }

    private fun showGeneralNotification() {
        val stringBuilder = StringBuilder()
        val pendingIntent = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT)

        for (task in mTaskList) {
            stringBuilder.append("• ").append(task.title)

            if (task.position < mAdapter.itemCount - 1) {
                stringBuilder.append("\n\n")
            }
        }

        var notificationTitle = ""
        when (mAdapter.itemCount % 10) {
            1 -> notificationTitle = "${getString(R.string.general_notification_1)} ${mAdapter.itemCount} ${getString(R.string.general_notification_2)}"

            2, 3, 4 -> notificationTitle = "${getString(R.string.general_notification_1)} ${mAdapter.itemCount} ${getString(R.string.general_notification_3)}"

            0, 5, 6, 7, 8, 9 -> notificationTitle = "${getString(R.string.general_notification_1)} ${mAdapter.itemCount} ${getString(R.string.general_notification_4)}"
        }

        // Set NotificationChannel for Android Oreo and higher
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(AlarmReceiver.GENERAL_NOTIFICATION_CHANNEL_ID, getString(R.string.general_notification_channel),
                    NotificationManager.IMPORTANCE_LOW)
            channel.enableLights(false)
            channel.enableVibration(false)
            mNotificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, AlarmReceiver.GENERAL_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(notificationTitle)
                .setContentText(stringBuilder.toString())
                .setNumber(mAdapter.itemCount)
                .setStyle(NotificationCompat.BigTextStyle().bigText(stringBuilder.toString()))
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setSmallIcon(R.drawable.ic_check_circle_white_24dp)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
        mNotificationManager.notify(1, notification.build())
    }

    private fun removeGeneralNotification() = mNotificationManager.cancel(1)

    private fun initCallbacks() {
        val callbackGeneralNotification = object : FragmentNotifications.GeneralNotificationClickListener {
            override fun onGeneralNotificationStateChanged() = updateGeneralNotification()
        }
        FragmentNotifications.callback = callbackGeneralNotification

        val callbackDateAndTimeFormat = object : FragmentDateAndTime.DateAndTimeFormatCallback {
            override fun onDateAndTimeFormatChanged() {
                mAdapter.reloadTasks()
                updateWidget()
            }
        }
        FragmentDateAndTime.callback = callbackDateAndTimeFormat
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.action_search -> startActivity(Intent(this, SearchActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        mFab.gone()

        if (mPreferenceHelper.getBoolean(PreferenceHelper.ANIMATION_IS_ON)) {
            val handler = Handler()
            handler.postDelayed({
                mFab.visible()
                val animation = AnimationUtils.loadAnimation(this, R.anim.fab_animation)
                val interpolator = Interpolator(0.2, 20.0)
                animation.interpolator = interpolator
                mFab.startAnimation(animation)
            }, 300)
        } else {
            mFab.visible()
        }

        mAdapter.setOnItemClickListener(object : RecyclerViewAdapter.ClickListener {
            override fun onTaskClick(v: View, position: Int) {
                val task = mAdapter.getTaskAtPosition(position)
                showTaskDetailsActivity(task)
            }
        })
    }

    companion object {
        var mTaskList = arrayListOf<Task>()
    }
}