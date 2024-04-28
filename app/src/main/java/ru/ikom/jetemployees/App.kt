package ru.ikom.jetemployees

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.ikom.details.featureDetailsModule
import ru.ikom.home.di.featureHomeModule

class App : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(appModule, featureHomeModule, featureDetailsModule)
        }
    }

    override fun newImageLoader(): ImageLoader =
        ImageLoader(this@App).newBuilder()
            .crossfade(true)
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir)
                    .maxSizePercent(0.02)
                    .build()
            }
            .build()
}