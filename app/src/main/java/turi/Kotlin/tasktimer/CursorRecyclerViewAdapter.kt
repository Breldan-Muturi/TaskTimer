package turi.Kotlin.tasktimer

import android.database.Cursor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.task_list_item.*
import java.lang.IllegalStateException

class TaskViewHolder(override val containerView: View) :
    RecyclerView.ViewHolder(containerView),
    LayoutContainer {
    fun bind(task: Task) {
        tli_name.text = task.name
        tli_description.text = task.description
        tli_edit.visibility = View.VISIBLE
        tli_delete.visibility = View.VISIBLE
        tli_edit.setOnClickListener {
            Log.d(TAG, "edit button tapped. task name is ${task.name}")
        }
        tli_delete.setOnClickListener {
            Log.d(TAG, "delete button tapped. task name is ${task.name}")
        }
        containerView.setOnLongClickListener {
            Log.d(TAG, "OnLongClick: task name is ${task.name}")
            true
        }
    }
}

private const val TAG = "CursorRecyclerViewAdapt"

class CursorRecyclerViewAdapter(private var cursor: Cursor?) :
    RecyclerView.Adapter<TaskViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        Log.d(TAG, "onCreateViewHolder: new view requested")
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.task_list_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: starts")
        val cursor = cursor //avoid problems with smart cast
        if (cursor == null || cursor.count == 0) {
            Log.d(TAG, "onBindViewHolder: providing instructions")
            holder.tli_name.setText(R.string.instructions_heading)
            holder.tli_description.setText(R.string.instructions)
            holder.tli_edit.visibility = View.GONE
            holder.tli_delete.visibility = View.GONE
        } else {
            if (!cursor.moveToPosition(position)) {
                throw IllegalStateException("Couldn't move cursor to position $position")
            }
//            Create a task object from the data in the cursor
            val task = Task(
                cursor.getString(cursor.getColumnIndex(TasksContract.Columns.TASK_NAME)),
                cursor.getString(cursor.getColumnIndex(TasksContract.Columns.TASK_DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndex(TasksContract.Columns.TASK_SORT_ORDER))
            )
//            Remember the ID isn't set in the constructor
            task.id = cursor.getLong(cursor.getColumnIndex(TasksContract.Columns.ID))
            holder.bind(task)
        }

    }

    override fun getItemCount(): Int {
        Log.d(TAG, "getItemCount: starts")
        val cursor = cursor
        val count = if (cursor == null || cursor.count == 0) {
            1
        } else {
            cursor.count
        }
        Log.d(TAG, "getItemCount: returning $count")
        return count
    }

    /**
     * Swap in a new cursor, returning the old cursor.
     * The returned old cursor is *not* closed.
     * @param newCursor The new cursor to be used
     * @return returns the previously set Cursor, or null if there wasn't one
     * If the given new instance is the same instance as the previously set cursor null is also returned
     */

    fun swapCursor(newCursor: Cursor?): Cursor? {
        if (newCursor === cursor) {
            return null
        }
        val numItems = itemCount
        val oldCursor = cursor
        cursor = newCursor
        if (newCursor != null) {
//            notify the observers about the new cursor
            notifyDataSetChanged()
        } else {
//            notify the observers about the lack of a data set
            notifyItemRangeRemoved(0, numItems)
        }
        return oldCursor
    }

}