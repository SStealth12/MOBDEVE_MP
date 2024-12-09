package com.mobdeve.s13.estanol.miguelfrancis.mp.objects

data class Task(
    val id: Int,
    var title: String,       // Task title
    var dueDate: String?,    // Nullable due date (or "None")
    var type: String,         // Type of task (e.g., "School", "Work", "None")
    var isCompleted: Boolean
)


