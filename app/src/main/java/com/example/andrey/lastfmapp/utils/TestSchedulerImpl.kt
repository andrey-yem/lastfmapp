package com.example.andrey.lastfmapp.utils

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers


class TestSchedulerImpl @JvmOverloads constructor(private val scheduler: Scheduler = Schedulers.trampoline()) : IScheduler {

    override fun background(): Scheduler = scheduler

    override fun main(): Scheduler = scheduler
}
