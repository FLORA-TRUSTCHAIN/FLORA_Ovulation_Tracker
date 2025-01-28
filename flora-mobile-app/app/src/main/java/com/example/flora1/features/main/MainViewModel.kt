package com.example.flora1.features.main

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flora1.core.network.clients.WebSocketClient
import com.example.flora1.data.db.PeriodDatabase
import com.example.flora1.domain.Preferences
import com.example.flora1.domain.util.DataError
import com.example.flora1.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.math.abs


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    db: PeriodDatabase,
    private val preferences: Preferences,
    private val webSocketClient: WebSocketClient,
) : ViewModel() {
    private val _selectedDay = MutableStateFlow(LocalDate.now().dayOfMonth)
    val selectedDay: StateFlow<Int> = _selectedDay

    private val _shouldShowPredictionDialog =
        MutableStateFlow(preferences.shouldShowPredictionDialog)

    val shouldShowPredictionDialog: StateFlow<Boolean> = _shouldShowPredictionDialog
        .onEach {
            preferences.saveShouldShowPredictionDialog(it)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            preferences.shouldShowPredictionDialog
        )

    fun onShouldShowPredictionDialogChanged(shouldShow: Boolean) {
        _shouldShowPredictionDialog.value = shouldShow
    }

    private val _shouldShowPredictions = MutableStateFlow(preferences.shouldShowPredictions)
    val shouldShowPredictions: StateFlow<Boolean> = _shouldShowPredictions
        .onEach {
            preferences.saveShouldShowPredictions(it)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            preferences.shouldShowPredictionDialog
        )

    fun onShouldShowPredictionsChanged(shouldShow: Boolean) {
        _shouldShowPredictions.value = shouldShow
    }


    val periodDaysForCurrentMonth = db.dao()
        .run {
            val localDate = LocalDate.now()
            getPeriodLogsForMonth(localDate.monthValue, localDate.year)
        }
        .map { fetchedCurrentMonthPeriodLogs ->
            fetchedCurrentMonthPeriodLogs.map {
                it.day
            }
        }
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    val fertileDays = periodDaysForCurrentMonth.mapLatest { periodDays ->
        val daysOfMonth = LocalDate.now().month.maxLength()

        when {
            periodDays.isEmpty() -> emptyList<Int>()
            daysOfMonth - periodDays.last() < 10 -> emptyList()
            else -> {
                buildList<Int> {
                    (periodDays.last() + 6..periodDays.last() + 11).forEach { candidateDay ->
                        if (candidateDay <= daysOfMonth) {
                            add(candidateDay)
                        } else {
                            return@forEach
                        }
                    }
                }
            }
        }
    }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    val ovulationDay: StateFlow<Int?> = fertileDays.mapLatest { fertileDays ->
        when {
            fertileDays.isEmpty() -> null
            fertileDays.size < 6 -> null
            else -> fertileDays[4]
        }
    }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )


    val primaryText = combine(
        selectedDay, periodDaysForCurrentMonth, ovulationDay, fertileDays,
    ) { selectedDay, periodDaysForCurrentMonth, ovulationDay, fertileDays ->

        if (selectedDay == ovulationDay)
            "Ovulation Day"
        else if (selectedDay in fertileDays)
            "Fertile days"
        else if (selectedDay in periodDaysForCurrentMonth) {
            if (periodDaysForCurrentMonth.last() - selectedDay == 1)
                "1 Day Left"
            else if (periodDaysForCurrentMonth.last() == selectedDay)
                "Last Day"
            else
                "${periodDaysForCurrentMonth.last() - selectedDay} days left"
        } else if (periodDaysForCurrentMonth.isNotEmpty() && periodDaysForCurrentMonth[0] - selectedDay == 1)
            "Your next period is on 1 day"
        else if (periodDaysForCurrentMonth.isNotEmpty() && periodDaysForCurrentMonth[0] > selectedDay)
            "Your next period\nstarts on ${periodDaysForCurrentMonth[0] - selectedDay} days"
        else
            "Nothing else\nfor this month"

    }
        .flowOn(Dispatchers.Default)
        .stateIn(
            viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = "",
        )


    val selectedDate: StateFlow<LocalDate> = selectedDay.mapLatest { day ->
        val localDate = LocalDate.now()
        LocalDate.of(
            localDate.year,
            localDate.monthValue,
            day,
        )
    }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = LocalDate.now()
        )


    fun onArcClicked(
        offsetClicked: Offset,
        circlePositions: List<Pair<Float, Float>>,
        circleRadius: Float,
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                circlePositions.forEachIndexed { index, position ->
                    if (abs(offsetClicked.x - position.first) < circleRadius * 2f
                        && abs(offsetClicked.y - position.second) < circleRadius * 2f
                    ) {
                        _selectedDay.update { index + 1 }
                        return@forEachIndexed
                    }
                }
            }
        }
    }

    private val _isConnectedToSocket = MutableStateFlow(false)
    val isConnectedToSocket: StateFlow<Boolean> = _isConnectedToSocket

    val latestReceivedMessage =
        _isConnectedToSocket
            .flatMapLatest {
                if (!it)
                    flow { emit("Pending") }
                else
                    webSocketClient.listenToSocket()
                        .flatMapLatest { result ->
                            val formattedTime = DateTimeFormatter
                                .ofPattern("dd-MM-yyyy, hh:mm:ss")
                                .format(LocalDateTime.now())

                            flow {
                                emit(
                                    when (result) {
                                        is Result.Error -> when (result.error) {
                                            DataError.Network.SOCKET_ERROR -> "Cannot establish connection"
                                            else -> "Problem occurred"
                                        }

                                        is Result.Success -> "Latest WS Msg: ${result.data}, $formattedTime"
                                    }
                                )
                            }
                        }
            }
            .stateIn(
                viewModelScope, SharingStarted.Lazily, ""
            )

    fun manageWebSocketConnection() {
        if (_isConnectedToSocket.value)
            disconnectFromSocket()
        else
            connectToSocket()
    }

    private fun connectToSocket() {
        _isConnectedToSocket.value = true
//        val numParams = 320
//
//        val randomData = mapOf(
//            "params" to List(numParams) { Random.nextFloat() * 1.2f - 0.6f },  // Random floats between -0.6 and 0.6
//            "num_samples" to 10  // Static integer value
//        )
//        val data = TrainingData(
//            params = List(numParams) { Random.nextFloat() * 1.2f - 0.6f },
//            numSamples = 10,
//        )
//
//        viewModelScope.launch {
//            webSocketClient.sendMessage(data)
//        }
    }

    private fun disconnectFromSocket() {
        _isConnectedToSocket.update { false }
        viewModelScope.launch {
            webSocketClient.disconnect()
        }
    }
}

@Serializable
data class TrainingData(
    @SerialName("params")
    val params: List<Float>,

    @SerialName("num_samples")
    val numSamples: Int,
)
