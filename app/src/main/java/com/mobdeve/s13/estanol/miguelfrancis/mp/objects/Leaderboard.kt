package com.mobdeve.s13.estanol.miguelfrancis.mp.objects

class Leaderboard {
    var userName: String
        private set
    var userLevel: Int = 0
        private set
    var weeklyExp: Int = 0
        private set

    constructor(userName: String, userLevel: Int, weeklyExp: Int) {
        this.userName = userName
        this.userLevel = userLevel
        this.weeklyExp = weeklyExp
    }
}