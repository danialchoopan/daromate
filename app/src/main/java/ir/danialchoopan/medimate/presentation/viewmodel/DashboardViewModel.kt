package ir.danialchoopan.medimate.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.danialchoopan.medimate.domain.usecase.GetDailyTimelineUseCase
import ir.danialchoopan.medimate.domain.usecase.TimelineItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    getDailyTimelineUseCase: GetDailyTimelineUseCase
) : ViewModel() {

    val timelineState: StateFlow<List<TimelineItem>> = getDailyTimelineUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
