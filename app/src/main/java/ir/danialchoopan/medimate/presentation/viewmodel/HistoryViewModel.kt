package ir.danialchoopan.medimate.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.danialchoopan.medimate.domain.model.MedicationLog
import ir.danialchoopan.medimate.domain.repository.LogRepository
import ir.danialchoopan.medimate.domain.usecase.AdherenceReport
import ir.danialchoopan.medimate.domain.usecase.ExportHistoryUseCase
import ir.danialchoopan.medimate.domain.usecase.GetAdherenceReportUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ExportState {
    data object Idle : ExportState()
    data object Loading : ExportState()
    data class Success(val uri: Uri) : ExportState()
    data class Error(val message: String) : ExportState()
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    getAdherenceReportUseCase: GetAdherenceReportUseCase,
    logRepository: LogRepository,
    private val exportHistoryUseCase: ExportHistoryUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val adherenceReport: StateFlow<AdherenceReport> = getAdherenceReportUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AdherenceReport(0, 0, 100f)
        )

    val allLogs: StateFlow<List<MedicationLog>> = logRepository.getAllLogs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportState: StateFlow<ExportState> = _exportState

    fun exportHistory() {
        viewModelScope.launch {
            _exportState.value = ExportState.Loading
            try {
                val uri = exportHistoryUseCase(context)
                _exportState.value = ExportState.Success(uri)
            } catch (e: Exception) {
                _exportState.value = ExportState.Error(e.message ?: "Export failed")
            }
        }
    }

    fun resetExportState() {
        _exportState.value = ExportState.Idle
    }
}
