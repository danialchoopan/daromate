package ir.danialchoopan.medimate.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.danialchoopan.medimate.data.local.dao.*
import ir.danialchoopan.medimate.data.repository.*
import ir.danialchoopan.medimate.domain.repository.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideMedicineRepository(medDao: MedicineDao, invDao: InventoryDao): MedicineRepository = MedicineRepositoryImpl(medDao, invDao)

    @Provides
    @Singleton
    fun provideReminderRepository(dao: ReminderDao): ReminderRepository = ReminderRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideLogRepository(dao: MedicationLogDao): LogRepository = LogRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideDrugInteractionRepository(dao: DrugInteractionDao): DrugInteractionRepository = DrugInteractionRepositoryImpl(dao)
}
