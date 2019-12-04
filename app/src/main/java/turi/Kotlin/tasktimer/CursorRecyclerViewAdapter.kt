package turi.Kotlin.tasktimer

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer

class TaskViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
    LayoutContainer {
}

private const val TAG = "CursorRecyclerViewAdapt"

class CursorRecyclerViewAdapter : RecyclerView.Adapter<TaskViewHolder>() {
}