package apps.jizzu.simpletodo.ui.recycler

import androidx.recyclerview.widget.DiffUtil
import apps.jizzu.simpletodo.data.models.Task

class TaskDiffUtilCallback constructor(private val oldList: List<Task>, private val updatedList: List<Task>)
    : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition].id == updatedList[newItemPosition].id

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = updatedList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldTask = oldList[oldItemPosition]
        val updatedTask = updatedList[newItemPosition]
        val isTitleTheSame = oldTask.title == updatedTask.title
        val isDateTheSame = oldTask.date == updatedTask.date
        val isNoteTheSame = oldTask.note == updatedTask.note

        return isTitleTheSame && isDateTheSame && isNoteTheSame
    }
}