package pl.cuyer.rusthub.common

import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.getImageByFileName
import pl.cuyer.rusthub.SharedRes

fun getImageByFileName(name: String): ImageResource {
    val fallbackImage = SharedRes.images.pl
    return SharedRes.images.getImageByFileName(name) ?: fallbackImage
}

