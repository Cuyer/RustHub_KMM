package pl.cuyer.rusthub.presentation.features.settings

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.domain.model.Language
import pl.cuyer.rusthub.domain.model.Settings
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.domain.usecase.GetSettingsUseCase
import pl.cuyer.rusthub.domain.usecase.SaveSettingsUseCase
import pl.cuyer.rusthub.domain.usecase.LogoutUserUseCase
import pl.cuyer.rusthub.presentation.navigation.Onboarding
import pl.cuyer.rusthub.presentation.navigation.UiEvent

class SettingsViewModel(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val saveSettingsUseCase: SaveSettingsUseCase,
    private val logoutUserUseCase: LogoutUserUseCase
) : BaseViewModel() {

    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = SettingsState()
    )

    init {
        coroutineScope.launch {
            getSettingsUseCase().collect { settings ->
                settings?.let { updateFromSettings(it) }
            }
        }
    }

    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.OnThemeChange -> updateTheme(action.theme)
            is SettingsAction.OnLanguageChange -> updateLanguage(action.language)
            SettingsAction.OnLogout -> logout()
        }
    }

    private fun updateTheme(theme: Theme) {
        _state.update { it.copy(theme = theme) }
        save()
    }

    private fun updateLanguage(language: Language) {
        _state.update { it.copy(language = language) }
        save()
    }

    private fun save() {
        val settings = Settings(_state.value.theme, _state.value.language)
        coroutineScope.launch { saveSettingsUseCase(settings) }
    }

    private fun updateFromSettings(settings: Settings) {
        _state.update { it.copy(theme = settings.theme, language = settings.language) }
    }

    private fun logout() {
        coroutineScope.launch {
            logoutUserUseCase()
            _uiEvent.send(UiEvent.Navigate(Onboarding))
        }
    }
}
