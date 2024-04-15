package br.com.joaov.persistence.di

import android.content.Context
import br.com.joaov.persistence.data.database.AppDatabase
import br.com.joaov.persistence.data.datasource.LocalDataSource
import br.com.joaov.persistence.data.repository.HomeRepository
import org.koin.dsl.module

class PersistenceModule {

    companion object {
        fun loadModule(context: Context) = module {
            single { AppDatabase.build(context) }
            factory { get<AppDatabase>().productDao() }
            factory { get<AppDatabase>().orderDao() }
            factory { get<AppDatabase>().saleDao() }
            factory<HomeRepository> { LocalDataSource(get(), get(), get()) }
        }
    }
}