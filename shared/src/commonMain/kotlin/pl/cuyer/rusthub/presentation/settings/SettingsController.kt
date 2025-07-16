package pl.cuyer.rusthub.presentation.settings

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.domain.model.Language
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.domain.usecase.GetSettingsUseCase
import pl.cuyer.rusthub.util.getCurrentAppLanguage

class SettingsController(
    private val getSettingsUseCase: GetSettingsUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _theme = MutableStateFlow(Theme.SYSTEM)
    val theme: StateFlow<Theme> = _theme

    private val _language = MutableStateFlow(getCurrentAppLanguage())
    val language: StateFlow<Language> = _language

    init {
        scope.launch {
            getSettingsUseCase().collect { settings ->
                settings?.let {
                    if (_theme.value != it.theme) {
                        _theme.value = it.theme
                    }
                    if (_language.value != it.language) {
                        _language.value = it.language
                    }
                }
            }
        }
    }
}
