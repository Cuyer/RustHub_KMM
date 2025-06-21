package pl.cuyer.rusthub.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toNSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDateFormatterMediumStyle
import platform.Foundation.NSDateFormatterShortStyle
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale

actual fun formatLocalDateTime(dateTime: LocalDateTime): String {
    val formatter = NSDateFormatter()
    formatter.dateStyle = NSDateFormatterMediumStyle
    formatter.timeStyle = NSDateFormatterShortStyle
    formatter.locale = NSLocale.currentLocale
    val instant = dateTime.toInstant(TimeZone.currentSystemDefault())
    val nsDate = instant.toNSDate()
    return formatter.stringFromDate(nsDate)
}