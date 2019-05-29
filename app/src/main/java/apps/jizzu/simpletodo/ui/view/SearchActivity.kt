package apps.jizzu.simpletodo.ui.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
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
import apps.jizzu.simpletodo.utils.visible
import apps.jizzu.simpletodo.vm.SearchTasksViewModel
import daio.io.dresscode.dressCodeStyleId
import daio.io.dresscode.matchDressCode
import kotlinx.android.synthetic.main.activity_search.*
import kotterknife.bindView

class SearchActivity : BaseActivity(), SearchView.OnQueryTextListener {
    private val mRecyclerView: RecyclerView by bindView(R.id.rvSearchResultsList)
    private lateinit var mViewModel: SearchTasksViewModel
    private lateinit var mAdapter: RecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        matchDressCode()
        setContentView(R.layout.activity_search)
        initToolbar()

        if (intent.getBooleanExtra("isShortcut", false)) {
            PreferenceHelper.getInstance().init(applicationContext)
            AlarmHelper.getInstance().init(applicationContext)
        }

        mViewModel = createViewModel()
        mViewModel.searchResultLiveData.observe(this, Observer<List<Task>> { response -> updateViewState(response) })

        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = RecyclerViewAdapter()
        mRecyclerView.adapter = mAdapter
        llEmptyView.visible()

        mAdapter.setOnItemClickListener(object : RecyclerViewAdapter.ClickListener {
            override fun onTaskClick(v: View, position: Int) {
                val task = mAdapter.getTaskAtPosition(position)
                showTaskDetailsActivity(task)
            }
        })
    }

    private fun updateViewState(tasks: List<Task>) = if (tasks.isEmpty()) showEmptyView(false)
        else showTaskList(tasks)

    private fun showEmptyView(isSearchFieldEmpty: Boolean) {
        mAdapter.updateData(arrayListOf())
        llEmptyView.visible()
        if (isSearchFieldEmpty) {
            ivEmptyIllustration.setImageDrawable(AppCompatResources.getDrawable(this,
                    R.drawable.illustration_search))
            tvEmptyTitle.text = getString(R.string.search_view_empty_text)
        } else {
            ivEmptyIllustration.setImageDrawable(AppCompatResources.getDrawable(this,
                    R.drawable.illustration_not_found))
            tvEmptyTitle.text = getString(R.string.search_view_not_found_text)
        }
    }

    private fun showTaskList(tasks: List<Task>) {
        llEmptyView.gone()
        mAdapter.updateData(tasks)
    }

    private fun createViewModel() = ViewModelProviders.of(this).get(SearchTasksViewModel(application)::class.java)

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.isIconified = false

        val close: ImageView = searchView.findViewById(androidx.appcompat.R.id.search_close_btn)
        val searchText: TextView = searchView.findViewById(androidx.appcompat.R.id.search_src_text)
        close.setOnClickListener { searchText.text = "" }
        searchView.setOnQueryTextListener(this)
        searchView.queryHint = getString(R.string.search)
        searchText.setBackgroundResource(R.drawable.search_view_background)
        val view: View = searchView.findViewById(androidx.appcompat.R.id.search_plate)

        when(dressCodeStyleId) {
            R.style.AppTheme_Light -> {
                searchText.setHintTextColor(ContextCompat.getColor(this, R.color.black))
                view.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            }

            R.style.AppTheme_Dark -> {
                searchText.setHintTextColor(ContextCompat.getColor(this, R.color.white))
                view.setBackgroundColor(ContextCompat.getColor(this, R.color.deepBlueGrey))
            }

            R.style.AppTheme_Black -> {
                searchText.setHintTextColor(ContextCompat.getColor(this, R.color.white))
                view.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return false
    }

    override fun onQueryTextSubmit(query: String?) = false

    override fun onQueryTextChange(newText: String): Boolean {
        if (newText.isEmpty()) {
            showEmptyView(true)
        } else mViewModel.searchInputLiveData.value = newText
        return true
    }
}
