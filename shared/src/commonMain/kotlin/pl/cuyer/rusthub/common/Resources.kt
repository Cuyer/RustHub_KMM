package pl.cuyer.rusthub.common

import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.getImageByFileName
import pl.cuyer.rusthub.SharedRes

fun getImageByFileName(name: String): ImageResource = SharedRes.images.getImageByFileName(name)
    ?: SharedRes.images.pl

