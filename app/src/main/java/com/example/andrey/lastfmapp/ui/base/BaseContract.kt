package com.example.andrey.lastfmapp.ui.base

class BaseContract {

    interface Presenter<in T> {
        fun bindView(view: T)
        fun unbindView()
    }

    interface View
}