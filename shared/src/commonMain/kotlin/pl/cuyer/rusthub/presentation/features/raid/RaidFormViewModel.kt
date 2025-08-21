package pl.cuyer.rusthub.presentation.features.raid

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.model.Raid
import pl.cuyer.rusthub.domain.model.SteamUser
import pl.cuyer.rusthub.domain.usecase.SaveRaidUseCase
import pl.cuyer.rusthub.domain.usecase.SearchSteamUserUseCase
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.util.AlarmScheduler
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.notifications.REMOTE_NOTIFICATION
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class RaidFormViewModel(
    raid: Raid?,
    private val saveRaidUseCase: SaveRaidUseCase,
    private val searchSteamUserUseCase: SearchSteamUserUseCase,
    private val alarmScheduler: AlarmScheduler,
    private val permissionsController: PermissionsController,
) : BaseViewModel() {

    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val initialDateTime = raid?.dateTime ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    private val _state = MutableStateFlow(
        RaidFormState(
            id = raid?.id,
            name = raid?.name.orEmpty(),
            dateTime = "${initialDateTime.date} ${initialDateTime.time.toString().substring(0, 5)}",
            steamIds = raid?.steamIds ?: emptyList(),
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
            is RaidFormAction.OnToggleFoundUser -> _state.update { state ->
                val new = state.selectedFoundIds.toMutableSet()
                if (!new.add(action.id)) new.remove(action.id)
                state.copy(selectedFoundIds = new)
            }
            RaidFormAction.OnAddFoundUsers -> addFoundUsers()
            RaidFormAction.OnDismissSearch -> _state.update {
                it.copy(
                    searchDialogVisible = false,
                    searchQuery = "",
                    foundUsers = emptyList(),
                    selectedFoundIds = emptySet(),
                    searchNotFound = false
                )
            }
            is RaidFormAction.OnDescriptionChange -> _state.update { it.copy(description = action.value) }
            RaidFormAction.OnSave -> saveRaid()
        }
    }

    private fun searchUser() {
        val rawQuery = _state.value.searchQuery
        val existing = _state.value.steamIds.size
        val queries = rawQuery.split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .take(4 - existing)
        if (queries.isEmpty()) {
            _state.update {
                it.copy(searchNotFound = true, selectedFoundIds = emptySet())
            }
            return
        }
        coroutineScope.launch {
            _state.update {
                it.copy(
                    searchLoading = true,
                    foundUsers = emptyList(),
                    searchNotFound = false,
                    selectedFoundIds = emptySet()
                )
            }
            when (val result = searchSteamUserUseCase(queries).first()) {
                is Result.Success -> _state.update {
                    it.copy(
                        foundUsers = result.data,
                        searchLoading = false,
                        searchNotFound = result.data.isEmpty()
                    )
                }
                is Result.Error -> _state.update {
                    it.copy(searchLoading = false, searchNotFound = true)
                }
            }
        }
    }

    private fun addFoundUsers() {
        val current = _state.value
        val toAdd = if (current.selectedFoundIds.isNotEmpty()) {
            current.foundUsers.filter { it.steamId in current.selectedFoundIds }
        } else {
            current.foundUsers
        }
        val newIds = (current.steamIds + toAdd.map { it.steamId }).distinct().take(4)
        _state.update {
            it.copy(
                steamIds = newIds,
                searchDialogVisible = false,
                searchQuery = "",
                foundUsers = emptyList(),
                selectedFoundIds = emptySet(),
                searchNotFound = false
            )
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
        if (dateTime.toInstant(TimeZone.currentSystemDefault()) <= Clock.System.now()) {
            return
        }
        val raid = Raid(
            id = id,
            name = current.name,
            dateTime = dateTime,
            steamIds = current.steamIds,
            description = current.description.ifBlank { null }
        )
        coroutineScope.launch {
            saveRaidUseCase(raid)
            runCatching {
                permissionsController.providePermission(Permission.REMOTE_NOTIFICATION)
                alarmScheduler.cancel(raid)
                alarmScheduler.schedule(raid)
            }.onFailure { error ->
                when (error) {
                    is DeniedAlwaysException -> permissionsController.openAppSettings()
                    is DeniedException -> Unit
                }
            }
            _uiEvent.send(UiEvent.NavigateUp)
        }
    }
}
