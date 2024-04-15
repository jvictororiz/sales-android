package br.com.joaov.sales

import android.app.Application
import br.com.joaov.home.di.HomeModule
import br.com.joaov.navigation.di.NavigationModule
import br.com.joaov.order.di.OrderModule
import br.com.joaov.persistence.di.PersistenceModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@BaseApplication)
            modules(
                NavigationModule.loadModule(),
                PersistenceModule.loadModule(this@BaseApplication),
                HomeModule.loadModule(),
                OrderModule.loadModule()
            )
        }
    }
}