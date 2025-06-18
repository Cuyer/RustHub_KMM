package pl.cuyer.rusthub.presentation.model

data class FilterUi(
    val lists: List<Triple<String, List<String>, Int?>> = listOf(),
    val checkboxes: List<Pair<String, Boolean>> = listOf(),
    val ranges: List<Triple<String, Int, Int>> = listOf()
)
