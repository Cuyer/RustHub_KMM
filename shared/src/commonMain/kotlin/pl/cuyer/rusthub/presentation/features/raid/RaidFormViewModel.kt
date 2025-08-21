package pl.cuyer.rusthub.presentation.features.raid

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.model.Raid
import pl.cuyer.rusthub.domain.usecase.SaveRaidUseCase
import pl.cuyer.rusthub.domain.usecase.SearchSteamUserUseCase
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class RaidFormViewModel(
    raid: Raid?,
    private val saveRaidUseCase: SaveRaidUseCase,
    private val searchSteamUserUseCase: SearchSteamUserUseCase,
) : BaseViewModel() {

    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val initialDateTime = raid?.dateTime ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    private val _state = MutableStateFlow(
        RaidFormState(
            id = raid?.id,
            name = raid?.name.orEmpty(),
            dateTime = "${initialDateTime.date} ${initialDateTime.time.toString().substring(0, 5)}",
            steamId = raid?.steamId.orEmpty(),
            description = raid?.description.orEmpty(),
        )
    )
    val state = _state.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = _state.value
    )

    fun onAction(action: RaidFormAction) {
        when (action) {
            is RaidFormAction.OnNameChange -> _state.update { it.copy(name = action.value, nameError = false) }
            is RaidFormAction.OnDateTimeChange -> _state.update { it.copy(dateTime = action.value) }
            is RaidFormAction.OnSelectTargetClick -> _state.update { it.copy(searchDialogVisible = true) }
            is RaidFormAction.OnSearchQueryChange -> _state.update { it.copy(searchQuery = action.value) }
            RaidFormAction.OnSearchUser -> searchUser()
            is RaidFormAction.OnUserSelected -> _state.update {
                it.copy(
                    steamId = action.user.steamId,
                    searchDialogVisible = false,
                    searchQuery = "",
                    foundUser = null,
                    searchNotFound = false
                )
            }
            RaidFormAction.OnDismissSearch -> _state.update {
                it.copy(
                    searchDialogVisible = false,
                    searchQuery = "",
                    foundUser = null,
                    searchNotFound = false
                )
            }
            is RaidFormAction.OnDescriptionChange -> _state.update { it.copy(description = action.value) }
            RaidFormAction.OnSave -> saveRaid()
        }
    }

    private fun searchUser() {
        val query = _state.value.searchQuery
        coroutineScope.launch {
            _state.update { it.copy(searchLoading = true, foundUser = null, searchNotFound = false) }
            searchSteamUserUseCase(query).collect { result ->
                _state.update {
                    when (result) {
                        is Result.Success -> {
                            val user = result.data
                            if (user != null) {
                                it.copy(foundUser = user, searchLoading = false)
                            } else {
                                it.copy(searchLoading = false, searchNotFound = true)
                            }
                        }
                        is Result.Error -> it.copy(searchLoading = false, searchNotFound = true)
                    }
                }
            }
        }
    }

    private fun saveRaid() {
        val current = _state.value
        if (current.name.isBlank()) {
            _state.update { it.copy(nameError = true) }
            return
        }
        val id = current.id ?: Clock.System.now().toEpochMilliseconds().toString()
        val dateTime = try {
            LocalDateTime.parse(current.dateTime.replace(' ', 'T'))
        } catch (e: Exception) {
            LocalDateTime.parse("1970-01-01T00:00")
        }
        val raid = Raid(
            id = id,
            name = current.name,
            dateTime = dateTime,
            steamId = current.steamId,
            description = current.description.ifBlank { null }
        )
        coroutineScope.launch {
            saveRaidUseCase(raid)
            _uiEvent.send(UiEvent.NavigateUp)
        }
    }
}
