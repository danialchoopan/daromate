package ir.danialchoopan.medimate.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.danialchoopan.medimate.domain.repository.*
import ir.danialchoopan.medimate.domain.usecase.*

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun provideAddMedicineUseCase(medRepo: MedicineRepository, remRepo: ReminderRepository) = AddMedicineUseCase(medRepo, remRepo)

    @Provides
    fun provideMarkAsTakenUseCase(medRepo: MedicineRepository, remRepo: ReminderRepository, logRepo: LogRepository) = MarkAsTakenUseCase(medRepo, remRepo, logRepo)

    @Provides
    fun provideGetDailyTimelineUseCase(medRepo: MedicineRepository, remRepo: ReminderRepository) = GetDailyTimelineUseCase(medRepo, remRepo)

    @Provides
    fun provideGetAdherenceReportUseCase(logRepo: LogRepository) = GetAdherenceReportUseCase(logRepo)
}
