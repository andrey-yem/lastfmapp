package com.example.andrey.lastfmapp.utils

import io.reactivex.Scheduler

interface IScheduler {
    fun background(): Scheduler
    fun main(): Scheduler
}
