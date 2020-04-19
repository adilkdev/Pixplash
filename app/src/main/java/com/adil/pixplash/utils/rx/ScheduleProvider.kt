package com.adil.pixplash.utils.rx

import io.reactivex.Scheduler

interface ScheduleProvider {

    fun computation(): Scheduler

    fun io(): Scheduler

    fun ui(): Scheduler

}