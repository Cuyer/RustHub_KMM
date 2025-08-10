package pl.cuyer.rusthub.domain.model

import pl.cuyer.rusthub.common.getImageByFileName

fun Flag?.toDrawable(): Int {
    return when (this) {
        Flag.IN -> getImageByFileName("ind").drawableResId
        Flag.AS -> getImageByFileName("asm").drawableResId
        Flag.DO -> getImageByFileName("dom").drawableResId
        Flag.IS -> getImageByFileName("isl").drawableResId
        else -> getImageByFileName(this?.name?.lowercase() ?: "pl").drawableResId
    }
}
