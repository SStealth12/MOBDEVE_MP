package com.mobdeve.s13.estanol.miguelfrancis.mp.ui.task

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mobdeve.s13.estanol.miguelfrancis.mp.R
import com.mobdeve.s13.estanol.miguelfrancis.mp.objects.Task

class TaskFragment<Task> : Fragment() {

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var dbHelper: TaskDatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_task, container, false)

        dbHelper = TaskDatabaseHelper(requireContext())
        val recyclerView = view.findViewById<RecyclerView>(R.id.taskRecyclerView)
        val addButton = view.findViewById<FloatingActionButton>(R.id.addTaskButton)

        // Initialize RecyclerView
        taskAdapter = TaskAdapter(dbHelper.getAllTasks(), ::onTaskCompleted, ::onTaskEdited)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = taskAdapter

        // Add Task Button
        addButton.setOnClickListener {
            openAddTaskDialog()
        }

        return view
    }

    private fun openAddTaskDialog() {
        // Inflate dialog layout
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_task, null)

        // Find views in the dialog layout
        val editTextTitle = dialogView.findViewById<EditText>(R.id.editTextTaskTitle)
        val editTextDueDate = dialogView.findViewById<EditText>(R.id.editTextTaskDueDate)
        val spinnerType = dialogView.findViewById<Spinner>(R.id.spinnerTaskType)

        // Populate the spinner with task types
        val types = arrayOf("School", "Work", "None")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, types)
        spinnerType.adapter = adapter

        // Build the dialog
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add Task")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val title = editTextTitle.text.toString().trim()
                val dueDate = editTextDueDate.text.toString().trim()
                val type = spinnerType.selectedItem.toString()

                if (title.isEmpty()) {
                    Toast.makeText(requireContext(), "Task title cannot be empty.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                dbHelper.addTask(title, if (dueDate.isNotEmpty()) dueDate else null, type)
                taskAdapter.updateTasks(dbHelper.getAllTasks())
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }


    private fun onTaskCompleted(taskId: Int) {
        dbHelper.markTaskCompleted(taskId)
        taskAdapter.updateTasks(dbHelper.getAllTasks())
    }

    private fun onTaskEdited(task: com.mobdeve.s13.estanol.miguelfrancis.mp.objects.Task) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_task, null)

        val editTextTitle = dialogView.findViewById<EditText>(R.id.editTextTaskTitle)
        val editTextDueDate = dialogView.findViewById<EditText>(R.id.editTextTaskDueDate)
        val spinnerType = dialogView.findViewById<Spinner>(R.id.spinnerTaskType)

        // Populate the spinner
        val types = arrayOf("School", "Work", "None")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, types)
        spinnerType.adapter = adapter

        // Pre-fill data from the Task object
        editTextTitle.setText(task.title)
        editTextDueDate.setText(task.dueDate ?: "")
        spinnerType.setSelection(types.indexOf(task.type))

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Edit Task")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val updatedTitle = editTextTitle.text.toString().trim()
                val updatedDueDate = editTextDueDate.text.toString().trim()
                val updatedType = spinnerType.selectedItem.toString()

                if (updatedTitle.isEmpty()) {
                    Toast.makeText(requireContext(), "Task title cannot be empty.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                dbHelper.updateTask(
                    task.id,
                    updatedTitle,
                    if (updatedDueDate.isNotEmpty()) updatedDueDate else null,
                    updatedType,
                    task.isCompleted // Preserve the original completion state
                )
                taskAdapter.updateTasks(dbHelper.getAllTasks())
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }


}
