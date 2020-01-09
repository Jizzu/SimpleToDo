package apps.jizzu.simpletodo.ui.view

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.data.models.Task
import apps.jizzu.simpletodo.service.alarm.AlarmHelper
import apps.jizzu.simpletodo.ui.recycler.RecyclerViewAdapter
import apps.jizzu.simpletodo.ui.view.base.BaseActivity
import apps.jizzu.simpletodo.utils.PreferenceHelper
import apps.jizzu.simpletodo.utils.gone
import apps.jizzu.simpletodo.utils.toast
import apps.jizzu.simpletodo.utils.visible
import apps.jizzu.simpletodo.vm.TaskListViewModel
import daio.io.dresscode.matchDressCode
import kotlinx.android.synthetic.main.activity_search.*
import kotterknife.bindView

class CompletedTasksActivity : BaseActivity() {
    private val mRecyclerView: RecyclerView by bindView(R.id.rvCompletedTasksList)
    private lateinit var mViewModel: TaskListViewModel
    private lateinit var mAdapter: RecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        matchDressCode()
        setContentView(R.layout.activity_completed_tasks)
        initToolbar(getString(R.string.completed_tasks_title))

        if (intent.getBooleanExtra("isShortcut", false)) {
            PreferenceHelper.getInstance().init(applicationContext)
            AlarmHelper.getInstance().init(applicationContext)
        }

        mViewModel = createViewModel()
        mViewModel.completedTasksLiveData.observe(this, Observer<List<Task>> { response -> updateViewState(response) })

        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = RecyclerViewAdapter()
        mRecyclerView.adapter = mAdapter
        llEmptyView.visible()
    }

    override fun onResume() {
        super.onResume()

        mAdapter.setOnItemClickListener(object : RecyclerViewAdapter.ClickListener {
            override fun onTaskClick(v: View, position: Int) {
                showTaskDetailsActivity(mAdapter.getTaskAtPosition(position))
            }
        })

        mAdapter.setTaskCompletionListener(object : RecyclerViewAdapter.TaskCompletionListener {
            override fun onTaskStatusChanged(v: View, position: Int) {
                toast(getString(R.string.move_task_to_open))
                val task = mAdapter.getTaskAtPosition(position)
                task.taskStatus = task.taskStatus.not()
                mViewModel.updateTask(task)
                completeTask(position)
            }
        })
    }

    private fun completeTask(position: Int) {
        val completedTask = mAdapter.getTaskAtPosition(position)
        val isCompletedTaskHasLastPosition = completedTask.position == mAdapter.itemCount - 1
        mAdapter.removeTask(position)
        val alarmHelper = AlarmHelper.getInstance()
        alarmHelper.removeAlarm(completedTask.timeStamp)
//        var isUndoClicked = false

        /* mSnackbar = Snackbar.make(mRecyclerView, R.string.complete_task_status, Snackbar.LENGTH_LONG)
         mSnackbar?.setAction(R.string.snackbar_undo) {
             mViewModel.saveTask(completedTask)
             if (completedTask.date != 0L && completedTask.date > Calendar.getInstance().timeInMillis) {
                 alarmHelper.setAlarm(completedTask)
             }
             isUndoClicked = true

             Handler().postDelayed({
                 val firstCompletelyVisibleItem = (mRecyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                 if (firstCompletelyVisibleItem != 0 && !RecyclerViewScrollListener.isShadowShown) {
                     setToolbarShadow(0f, 10f)
                     RecyclerViewScrollListener.isShadowShown = true
                 }
             }, 100)
         }

         mSnackbar?.view?.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
             override fun onViewAttachedToWindow(view: View) {
                 val firstCompletelyVisibleItem = (mRecyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                 val lastCompletelyVisibleItem = (mRecyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()

                 if (firstCompletelyVisibleItem == 0 && lastCompletelyVisibleItem == MainActivity.mTaskList.size - 1 && RecyclerViewScrollListener.isShadowShown) {
                     setToolbarShadow(10f, 0f)
                     RecyclerViewScrollListener.isShadowShown = false
                 }
             }

             override fun onViewDetachedFromWindow(view: View) {
                 if (!isUndoClicked) {
                     alarmHelper.removeNotification(completedTask.timeStamp, this@MainActivity)
                     if (!isCompletedTaskHasLastPosition) recountTaskPositions()
                 }
             }
         })
         mSnackbar?.anchorView = mFab
         mSnackbar?.show()*/
    }

    private fun updateViewState(tasks: List<Task>) = if (tasks.isEmpty()) showEmptyView()
    else showTaskList(tasks)

    private fun showEmptyView() {
        mAdapter.updateData(arrayListOf())
        llEmptyView.visible()
        ivEmptyIllustration.setImageDrawable(AppCompatResources.getDrawable(this,
                R.drawable.illustration_not_found))
        tvEmptyTitle.text = getString(R.string.search_view_not_found_text)
    }

    private fun showTaskList(tasks: List<Task>) {
        llEmptyView.gone()
        mAdapter.updateData(tasks)
    }

    private fun createViewModel() = ViewModelProviders.of(this).get(TaskListViewModel(application)::class.java)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return false
    }
}
