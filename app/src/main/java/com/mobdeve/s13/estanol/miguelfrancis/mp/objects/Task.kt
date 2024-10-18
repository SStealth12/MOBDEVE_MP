package com.mobdeve.s13.estanol.miguelfrancis.mp.objects

class Task {
    var taskName: String
        private set
    var totalPomodoro: Int = 0
        private set
    var taskStatus: String = "Active"
        private set

    constructor(taskName: String, totalPomodoro: Int, taskStatus: String) {
        this.taskName = taskName
        this.totalPomodoro = totalPomodoro
        this.taskStatus = taskStatus
    }
}