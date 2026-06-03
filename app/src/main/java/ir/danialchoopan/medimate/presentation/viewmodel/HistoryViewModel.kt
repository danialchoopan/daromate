package ir.danialchoopan.medimate.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.danialchoopan.medimate.domain.usecase.AdherenceReport
import ir.danialchoopan.medimate.domain.usecase.GetAdherenceReportUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    getAdherenceReportUseCase: GetAdherenceReportUseCase
) : ViewModel() {

    val adherenceReport: StateFlow<AdherenceReport> = getAdherenceReportUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AdherenceReport(0, 0, 100f)
        )
}
