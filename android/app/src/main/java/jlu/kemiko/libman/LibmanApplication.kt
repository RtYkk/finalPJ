package jlu.kemiko.libman

import android.app.Application
import jlu.kemiko.libman.common.di.AppContainer
import jlu.kemiko.libman.common.di.DefaultAppContainer

class LibmanApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
