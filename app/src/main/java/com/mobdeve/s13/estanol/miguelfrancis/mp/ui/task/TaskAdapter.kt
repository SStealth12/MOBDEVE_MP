package com.mobdeve.s13.estanol.miguelfrancis.mp.ui.task

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s13.estanol.miguelfrancis.mp.R
import com.mobdeve.s13.estanol.miguelfrancis.mp.objects.Task

class TaskAdapter(
    private var tasks: List<Task>,
    private val onTaskCompleted: (Int) -> Unit,
    private val onTaskEdited: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.taskTitle)
        val dueDate: TextView = itemView.findViewById(R.id.taskDueDate)
        val typeIndicator: View = itemView.findViewById(R.id.taskTypeIndicator)
        val completeButton: Button = itemView.findViewById(R.id.completeTaskButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.title.text = task.title
        holder.dueDate.text = task.dueDate ?: "No Due Date"

        val color = when (task.type) {
            "School" -> Color.RED
            "Work" -> Color.BLUE
            else -> Color.GRAY
        }
        holder.typeIndicator.setBackgroundColor(color)

        holder.completeButton.setOnClickListener {
            onTaskCompleted(task.id)
        }

        holder.itemView.setOnClickListener {
            onTaskEdited(task)
        }
    }

    override fun getItemCount(): Int = tasks.size

    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }
}
