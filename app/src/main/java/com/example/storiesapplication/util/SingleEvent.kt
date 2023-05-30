package com.example.storiesapplication.util

class SingleEvent<out T>(private val data: T) {
    var hasBeenDone = false
        private set

    fun getData(): T? {
        return if (hasBeenDone) {
            null
        } else {
            hasBeenDone = true
            return data
        }
    }
}