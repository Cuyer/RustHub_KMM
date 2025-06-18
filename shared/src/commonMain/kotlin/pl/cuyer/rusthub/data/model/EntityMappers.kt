package pl.cuyer.rusthub.data.model

import pl.cuyer.rusthub.domain.model.*

fun DifficultyEntity?.toDomain(): Difficulty? = this?.let { Difficulty.valueOf(it.name) }
fun Difficulty?.toEntity(): DifficultyEntity? = this?.let { DifficultyEntity.valueOf(it.name) }

fun FlagEntity?.toDomain(): Flag? = this?.let { Flag.valueOf(it.name) }
fun Flag?.toEntity(): FlagEntity? = this?.let { FlagEntity.valueOf(it.name) }

fun MapsEntity?.toDomain(): Maps? = this?.let { Maps.valueOf(it.name) }
fun Maps?.toEntity(): MapsEntity? = this?.let { MapsEntity.valueOf(it.name) }

fun RegionEntity?.toDomain(): Region? = this?.let { Region.valueOf(it.name) }
fun Region?.toEntity(): RegionEntity? = this?.let { RegionEntity.valueOf(it.name) }

fun WipeScheduleEntity?.toDomain(): WipeSchedule? = this?.let { WipeSchedule.valueOf(it.name) }
fun WipeSchedule?.toEntity(): WipeScheduleEntity? = this?.let { WipeScheduleEntity.valueOf(it.name) }

fun OrderEntity?.toDomain(): Order? = this?.let { Order.valueOf(it.name) }
fun Order?.toEntity(): OrderEntity? = this?.let { OrderEntity.valueOf(it.name) }

