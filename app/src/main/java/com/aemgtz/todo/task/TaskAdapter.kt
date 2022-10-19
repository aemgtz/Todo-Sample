package com.aemgtz.todo.task

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.aemgtz.todo.R
import com.aemgtz.todo.data.Task
import com.google.android.material.card.MaterialCardView


class TaskAdapter(private val listener: TaskItemActionsListener?) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    var dataSource : List<Task>? = null
      set (value){
          field = value
          notifyDataSetChanged()
      }
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val checkBox: CheckBox
        val cardView: MaterialCardView

        init {
            // Define click listener for the ViewHolder's View.
            checkBox = view.findViewById(R.id.check_box)
            cardView = view.findViewById(R.id.card_view)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.task_row_item, viewGroup, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val task = dataSource?.get(position)
        viewHolder.checkBox.text = task?.title
        viewHolder.checkBox.isChecked = task?.isCompleted?:false
        viewHolder.cardView.setOnClickListener {
            task?.let {
                listener?.onTaskClicked(task)
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSource?.size?:0


    interface TaskItemActionsListener {
        fun onTaskClicked(task: Task)
    }
}
