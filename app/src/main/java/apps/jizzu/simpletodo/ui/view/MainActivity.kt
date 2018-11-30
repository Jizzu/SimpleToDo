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
import apps.jizzu.simpletodo.ui.adapter.RecyclerViewAdapter
import apps.jizzu.simpletodo.ui.adapter.RecyclerViewScrollListener
import apps.jizzu.simpletodo.ui.settings.activity.SettingsActivity
import apps.jizzu.simpletodo.utils.Interpolator
import apps.jizzu.simpletodo.utils.PreferenceHelper
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

    private lateinit var mAdapter: RecyclerViewAdapter
    private lateinit var mPreferenceHelper: PreferenceHelper
    private lateinit var mNotificationManager: NotificationManager
    private lateinit var mViewModel: TaskListViewModel
    private var mTaskList = arrayListOf<Task>()

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
        createItemTouchHelper()
        initListeners()
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
        val task = mAdapter.getTaskAtPosition(position)
        mAdapter.removeTask(position)
        mViewModel.deleteTask(task)

        val snackbar = Snackbar.make(mRecyclerView, R.string.snackbar_remove_task, Snackbar.LENGTH_LONG)
        snackbar.setAction(R.string.snackbar_undo) {
            mViewModel.saveTask(task)
        }

        snackbar.view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(view: View) {
                mFab.show()
            }

            override fun onViewDetachedFromWindow(view: View) {

            }
        })
        snackbar.show()
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
        mAdapter.updateTaskOrder(fromPosition, toPosition)
        updateGeneralNotification(mTaskList)
    }

    override fun onPause() {
        super.onPause()

        for (task in mTaskList) {
            mViewModel.updateTask(task)
        }
    }

    private fun showChangelogActivity() {
        if (mPreferenceHelper.getBoolean(PreferenceHelper.IS_FIRST_APP_LAUNCH)) {
            startActivity(Intent(this, ChangelogActivity::class.java))
            mPreferenceHelper.putBoolean(PreferenceHelper.IS_FIRST_APP_LAUNCH, false)
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

        mAdapter.setOnItemClickListener(object : RecyclerViewAdapter.ClickListener {
            override fun onTaskClick(v: View, position: Int) {
                val task = mAdapter.getTaskAtPosition(position)
                showTaskDetailsActivity(task)
            }
        })

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && mFab.visibility == View.VISIBLE) {
                    mFab.hide()
                } else if (dy < 0 && mFab.visibility != View.VISIBLE) {
                    mFab.show()
                }
            }
        })

        mRecyclerView.addOnScrollListener(object : RecyclerViewScrollListener() {
            override fun onShow() {
                setToolbarShadow(0f, 30f)
            }

            override fun onHide() {
                setToolbarShadow(30f, 0f)
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

    private fun createViewModel() = ViewModelProviders.of(this).get(TaskListViewModel::class.java)

    private fun setToolbarShadow(start: Float, end: Float) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ValueAnimator.ofFloat(start, end).apply {
                addUpdateListener { updatedAnimation ->
                    toolbar.elevation = updatedAnimation.animatedValue as Float
                }
                duration = 500
                start()
            }
        }
    }

    private fun updateViewState(tasks: List<Task>) = if (tasks.isEmpty()) showEmptyView()
        else showTaskList(tasks)

    private fun showTaskList(tasks: List<Task>) {
        mTaskList = tasks as ArrayList<Task>
        emptyView.visibility = View.GONE
        mAdapter.setData(tasks)
        updateGeneralNotification(tasks)
        updateWidget()
    }

    private fun showEmptyView() {
        mAdapter.setData(arrayListOf())
        emptyView.visibility = View.VISIBLE
        val anim = AnimationUtils.loadAnimation(this, R.anim.empty_view_animation).apply {
            startOffset = 300
            duration = 300
        }
        emptyView.startAnimation(anim)
    }

    private fun updateWidget() {
        val intent = Intent(this, WidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = AppWidgetManager.getInstance(this@MainActivity)
                .getAppWidgetIds(ComponentName(this@MainActivity, WidgetProvider::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(intent)
    }

    private fun updateGeneralNotification(tasks: List<Task>) {
        if (mPreferenceHelper.getBoolean(PreferenceHelper.GENERAL_NOTIFICATION_IS_ON)) {
            if (mAdapter.itemCount != 0) {
                showGeneralNotification(tasks)
            } else {
                removeGeneralNotification()
            }
        } else {
            removeGeneralNotification()
        }
    }

    private fun showGeneralNotification(tasks: List<Task>) {
        val stringBuilder = StringBuilder()
        val pendingIntent = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT)

        for (task in tasks) {
            stringBuilder.append("• ").append(task.title)

            if (task.position < mAdapter.itemCount - 1) {
                stringBuilder.append("\n\n")
            }
        }

        var notificationTitle = ""
        when (mAdapter.itemCount % 10) {
            1 -> notificationTitle = getString(R.string.general_notification_1) + " " + mAdapter.itemCount + " " + getString(R.string.general_notification_2)

            2, 3, 4 -> notificationTitle = getString(R.string.general_notification_1) + " " + mAdapter.itemCount + " " + getString(R.string.general_notification_3)

            0, 5, 6, 7, 8, 9 -> notificationTitle = getString(R.string.general_notification_1) + " " + mAdapter.itemCount + " " + getString(R.string.general_notification_4)
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

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.menu, menu)
//
//        val item = menu.findItem(R.id.action_search)
//        mSearchView.setMenuItem(item)
//
//        return true
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            startActivity(Intent(this, SettingsActivity::class.java))
            true
        } else false
    }

    override fun onResume() {
        super.onResume()

        (mFab as View).visibility = View.GONE

        if (mPreferenceHelper.getBoolean(PreferenceHelper.ANIMATION_IS_ON)) {
            val handler = Handler()
            handler.postDelayed({
                (mFab as View).visibility = View.VISIBLE
                val animation = AnimationUtils.loadAnimation(this, R.anim.fab_animation)
                val interpolator = Interpolator(0.2, 20.0)
                animation.interpolator = interpolator
                mFab.startAnimation(animation)
            }, 300)
        } else {
            (mFab as View).visibility = View.VISIBLE
        }
    }
}
