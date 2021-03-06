package com.example.runningtracker.DI

import android.content.Context
import androidx.room.Room
import com.example.runningtracker.DB.RunningDatabase
import com.example.runningtracker.other.Constants.RUNNING_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import java.net.ContentHandler
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {


    @Singleton
    @Provides
    fun providesRunningDatabase(
        @ApplicationContext app: Context)
            = Room.databaseBuilder(
            app,
            RunningDatabase::class.java,
            RUNNING_DATABASE_NAME
    ).build()


    @Singleton
    @Provides
    fun provideDao(db:RunningDatabase) = db.getRunDao()
}