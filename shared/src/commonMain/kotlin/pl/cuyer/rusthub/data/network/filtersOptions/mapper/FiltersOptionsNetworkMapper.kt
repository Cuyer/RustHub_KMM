package pl.cuyer.rusthub.data.network.filtersOptions.mapper

import pl.cuyer.rusthub.data.network.filtersOptions.model.FiltersOptionsDto
import pl.cuyer.rusthub.data.network.model.DifficultyDto
import pl.cuyer.rusthub.data.network.model.FlagDto
import pl.cuyer.rusthub.data.network.model.MapsDto
import pl.cuyer.rusthub.data.network.model.RegionDto
import pl.cuyer.rusthub.data.network.model.ServerStatusDto
import pl.cuyer.rusthub.data.network.model.WipeScheduleDto
import pl.cuyer.rusthub.data.network.model.WipeTypeDto
import pl.cuyer.rusthub.domain.model.Difficulty
import pl.cuyer.rusthub.domain.model.FiltersOptions
import pl.cuyer.rusthub.domain.model.Flag
import pl.cuyer.rusthub.domain.model.Maps
import pl.cuyer.rusthub.domain.model.Region
import pl.cuyer.rusthub.domain.model.ServerStatus
import pl.cuyer.rusthub.domain.model.WipeSchedule
import pl.cuyer.rusthub.domain.model.WipeType

fun FiltersOptionsDto.toDomain(): FiltersOptions {
    return FiltersOptions(
        flags = flags.map { it.toDomain() },
        maxRanking = maxRanking,
        maxPlayerCount = maxPlayerCount,
        maxGroupLimit = maxGroupLimit,
        maps = maps.map { it.toDomain() },
        regions = regions.map { it.toDomain() },
        difficulty = difficulty.map { it.toDomain() },
        wipeSchedules = wipeSchedules.map { it.toDomain() }
    )
}

fun DifficultyDto.toDomain(): Difficulty {
    return when (this) {
        DifficultyDto.VANILLA -> Difficulty.VANILLA
        DifficultyDto.SOFTCORE -> Difficulty.SOFTCORE
        DifficultyDto.HARDCORE -> Difficulty.HARDCORE
        DifficultyDto.PRIMITIVE -> Difficulty.PRIMITIVE
    }
}

fun WipeScheduleDto.toDomain(): WipeSchedule {
    return when (this) {
        WipeScheduleDto.BIWEEKLY -> WipeSchedule.BIWEEKLY
        WipeScheduleDto.WEEKLY -> WipeSchedule.WEEKLY
        WipeScheduleDto.MONTHLY -> WipeSchedule.MONTHLY
    }
}

fun MapsDto.toDomain(): Maps {
    return when (this) {
        MapsDto.CUSTOM -> Maps.CUSTOM
        MapsDto.PROCEDURAL -> Maps.PROCEDURAL
        MapsDto.BARREN -> Maps.BARREN
        MapsDto.CRAGGY_ISLAND -> Maps.CRAGGY_ISLAND
        MapsDto.HAPPIS_ISLAND -> Maps.HAPPIS_ISLAND
        MapsDto.SAVAS_ISLAND_KOTH -> Maps.SAVAS_ISLAND_KOTH
        MapsDto.SAVAS_ISLAND -> Maps.SAVAS_ISLAND
    }
}

fun RegionDto.toDomain(): Region {
    return when (this) {
        RegionDto.ASIA -> Region.ASIA
        RegionDto.EUROPE -> Region.EUROPE
        RegionDto.AMERICA -> Region.AMERICA
        RegionDto.AFRICA -> Region.AFRICA
        RegionDto.OCEANIA -> Region.OCEANIA
        RegionDto.AUSTRALIA -> Region.AUSTRALIA
    }
}

fun ServerStatusDto.toDomain(): ServerStatus {
    return when (this) {
        ServerStatusDto.ONLINE -> ServerStatus.ONLINE
        ServerStatusDto.OFFLINE -> ServerStatus.OFFLINE
    }
}

fun WipeTypeDto.toDomain(): WipeType {
    return when (this) {
        WipeTypeDto.MAP -> WipeType.MAP
        WipeTypeDto.FULL -> WipeType.FULL
        WipeTypeDto.BP -> WipeType.BP
        WipeTypeDto.UNKNOWN -> WipeType.UNKNOWN
    }
}

fun FlagDto.toDomain(): Flag {
    return when (this) {
        FlagDto.AD -> Flag.AD
        FlagDto.AE -> Flag.AE
        FlagDto.AF -> Flag.AF
        FlagDto.AG -> Flag.AG
        FlagDto.AI -> Flag.AI
        FlagDto.AL -> Flag.AL
        FlagDto.AM -> Flag.AM
        FlagDto.AO -> Flag.AO
        FlagDto.AQ -> Flag.AQ
        FlagDto.AR -> Flag.AR
        FlagDto.AS -> Flag.AS
        FlagDto.AT -> Flag.AT
        FlagDto.AU -> Flag.AU
        FlagDto.AW -> Flag.AW
        FlagDto.AX -> Flag.AX
        FlagDto.AZ -> Flag.AZ
        FlagDto.BA -> Flag.BA
        FlagDto.BB -> Flag.BB
        FlagDto.BD -> Flag.BD
        FlagDto.BE -> Flag.BE
        FlagDto.BF -> Flag.BF
        FlagDto.BG -> Flag.BG
        FlagDto.BH -> Flag.BH
        FlagDto.BI -> Flag.BI
        FlagDto.BJ -> Flag.BJ
        FlagDto.BL -> Flag.BL
        FlagDto.BM -> Flag.BM
        FlagDto.BN -> Flag.BN
        FlagDto.BO -> Flag.BO
        FlagDto.BQ -> Flag.BQ
        FlagDto.BR -> Flag.BR
        FlagDto.BS -> Flag.BS
        FlagDto.BT -> Flag.BT
        FlagDto.BV -> Flag.BV
        FlagDto.BW -> Flag.BW
        FlagDto.BY -> Flag.BY
        FlagDto.BZ -> Flag.BZ
        FlagDto.CA -> Flag.CA
        FlagDto.CC -> Flag.CC
        FlagDto.CD -> Flag.CD
        FlagDto.CF -> Flag.CF
        FlagDto.CG -> Flag.CG
        FlagDto.CH -> Flag.CH
        FlagDto.CI -> Flag.CI
        FlagDto.CK -> Flag.CK
        FlagDto.CL -> Flag.CL
        FlagDto.CM -> Flag.CM
        FlagDto.CN -> Flag.CN
        FlagDto.CO -> Flag.CO
        FlagDto.CR -> Flag.CR
        FlagDto.CU -> Flag.CU
        FlagDto.CV -> Flag.CV
        FlagDto.CW -> Flag.CW
        FlagDto.CX -> Flag.CX
        FlagDto.CY -> Flag.CY
        FlagDto.CZ -> Flag.CZ
        FlagDto.DE -> Flag.DE
        FlagDto.DJ -> Flag.DJ
        FlagDto.DK -> Flag.DK
        FlagDto.DM -> Flag.DM
        FlagDto.DO -> Flag.DO
        FlagDto.DZ -> Flag.DZ
        FlagDto.EC -> Flag.EC
        FlagDto.EE -> Flag.EE
        FlagDto.EG -> Flag.EG
        FlagDto.EH -> Flag.EH
        FlagDto.ER -> Flag.ER
        FlagDto.ES -> Flag.ES
        FlagDto.ET -> Flag.ET
        FlagDto.FI -> Flag.FI
        FlagDto.FJ -> Flag.FJ
        FlagDto.FK -> Flag.FK
        FlagDto.FM -> Flag.FM
        FlagDto.FO -> Flag.FO
        FlagDto.FR -> Flag.FR
        FlagDto.GA -> Flag.GA
        FlagDto.GB -> Flag.GB
        FlagDto.GD -> Flag.GD
        FlagDto.GE -> Flag.GE
        FlagDto.GF -> Flag.GF
        FlagDto.GG -> Flag.GG
        FlagDto.GH -> Flag.GH
        FlagDto.GI -> Flag.GI
        FlagDto.GL -> Flag.GL
        FlagDto.GM -> Flag.GM
        FlagDto.GN -> Flag.GN
        FlagDto.GP -> Flag.GP
        FlagDto.GQ -> Flag.GQ
        FlagDto.GR -> Flag.GR
        FlagDto.GS -> Flag.GS
        FlagDto.GT -> Flag.GT
        FlagDto.GU -> Flag.GU
        FlagDto.GW -> Flag.GW
        FlagDto.GY -> Flag.GY
        FlagDto.HK -> Flag.HK
        FlagDto.HM -> Flag.HM
        FlagDto.HN -> Flag.HN
        FlagDto.HR -> Flag.HR
        FlagDto.HT -> Flag.HT
        FlagDto.HU -> Flag.HU
        FlagDto.ID -> Flag.ID
        FlagDto.IE -> Flag.IE
        FlagDto.IL -> Flag.IL
        FlagDto.IM -> Flag.IM
        FlagDto.IN -> Flag.IN
        FlagDto.IO -> Flag.IO
        FlagDto.IQ -> Flag.IQ
        FlagDto.IR -> Flag.IR
        FlagDto.IS -> Flag.IS
        FlagDto.IT -> Flag.IT
        FlagDto.JE -> Flag.JE
        FlagDto.JM -> Flag.JM
        FlagDto.JO -> Flag.JO
        FlagDto.JP -> Flag.JP
        FlagDto.KE -> Flag.KE
        FlagDto.KG -> Flag.KG
        FlagDto.KH -> Flag.KH
        FlagDto.KI -> Flag.KI
        FlagDto.KM -> Flag.KM
        FlagDto.KN -> Flag.KN
        FlagDto.KP -> Flag.KP
        FlagDto.KR -> Flag.KR
        FlagDto.KW -> Flag.KW
        FlagDto.KY -> Flag.KY
        FlagDto.KZ -> Flag.KZ
        FlagDto.LA -> Flag.LA
        FlagDto.LB -> Flag.LB
        FlagDto.LC -> Flag.LC
        FlagDto.LI -> Flag.LI
        FlagDto.LK -> Flag.LK
        FlagDto.LR -> Flag.LR
        FlagDto.LS -> Flag.LS
        FlagDto.LT -> Flag.LT
        FlagDto.LU -> Flag.LU
        FlagDto.LV -> Flag.LV
        FlagDto.LY -> Flag.LY
        FlagDto.MA -> Flag.MA
        FlagDto.MC -> Flag.MC
        FlagDto.MD -> Flag.MD
        FlagDto.ME -> Flag.ME
        FlagDto.MF -> Flag.MF
        FlagDto.MG -> Flag.MG
        FlagDto.MH -> Flag.MH
        FlagDto.MK -> Flag.MK
        FlagDto.ML -> Flag.ML
        FlagDto.MM -> Flag.MM
        FlagDto.MN -> Flag.MN
        FlagDto.MO -> Flag.MO
        FlagDto.MP -> Flag.MP
        FlagDto.MQ -> Flag.MQ
        FlagDto.MR -> Flag.MR
        FlagDto.MS -> Flag.MS
        FlagDto.MT -> Flag.MT
        FlagDto.MU -> Flag.MU
        FlagDto.MV -> Flag.MV
        FlagDto.MW -> Flag.MW
        FlagDto.MX -> Flag.MX
        FlagDto.MY -> Flag.MY
        FlagDto.MZ -> Flag.MZ
        FlagDto.NA -> Flag.NA
        FlagDto.NC -> Flag.NC
        FlagDto.NE -> Flag.NE
        FlagDto.NF -> Flag.NF
        FlagDto.NG -> Flag.NG
        FlagDto.NI -> Flag.NI
        FlagDto.NL -> Flag.NL
        FlagDto.NO -> Flag.NO
        FlagDto.NP -> Flag.NP
        FlagDto.NR -> Flag.NR
        FlagDto.NU -> Flag.NU
        FlagDto.NZ -> Flag.NZ
        FlagDto.OM -> Flag.OM
        FlagDto.PA -> Flag.PA
        FlagDto.PE -> Flag.PE
        FlagDto.PF -> Flag.PF
        FlagDto.PG -> Flag.PG
        FlagDto.PH -> Flag.PH
        FlagDto.PK -> Flag.PK
        FlagDto.PL -> Flag.PL
        FlagDto.PM -> Flag.PM
        FlagDto.PN -> Flag.PN
        FlagDto.PR -> Flag.PR
        FlagDto.PS -> Flag.PS
        FlagDto.PT -> Flag.PT
        FlagDto.PW -> Flag.PW
        FlagDto.PY -> Flag.PY
        FlagDto.QA -> Flag.QA
        FlagDto.RE -> Flag.RE
        FlagDto.RO -> Flag.RO
        FlagDto.RS -> Flag.RS
        FlagDto.RU -> Flag.RU
        FlagDto.RW -> Flag.RW
        FlagDto.SA -> Flag.SA
        FlagDto.SB -> Flag.SB
        FlagDto.SC -> Flag.SC
        FlagDto.SD -> Flag.SD
        FlagDto.SE -> Flag.SE
        FlagDto.SG -> Flag.SG
        FlagDto.SH -> Flag.SH
        FlagDto.SI -> Flag.SI
        FlagDto.SJ -> Flag.SJ
        FlagDto.SK -> Flag.SK
        FlagDto.SL -> Flag.SL
        FlagDto.SM -> Flag.SM
        FlagDto.SN -> Flag.SN
        FlagDto.SO -> Flag.SO
        FlagDto.SR -> Flag.SR
        FlagDto.SS -> Flag.SS
        FlagDto.ST -> Flag.ST
        FlagDto.SV -> Flag.SV
        FlagDto.SX -> Flag.SX
        FlagDto.SY -> Flag.SY
        FlagDto.SZ -> Flag.SZ
        FlagDto.TC -> Flag.TC
        FlagDto.TD -> Flag.TD
        FlagDto.TF -> Flag.TF
        FlagDto.TG -> Flag.TG
        FlagDto.TH -> Flag.TH
        FlagDto.TJ -> Flag.TJ
        FlagDto.TK -> Flag.TK
        FlagDto.TL -> Flag.TL
        FlagDto.TM -> Flag.TM
        FlagDto.TN -> Flag.TN
        FlagDto.TO -> Flag.TO
        FlagDto.TR -> Flag.TR
        FlagDto.TT -> Flag.TT
        FlagDto.TV -> Flag.TV
        FlagDto.TW -> Flag.TW
        FlagDto.TZ -> Flag.TZ
        FlagDto.UA -> Flag.UA
        FlagDto.UG -> Flag.UG
        FlagDto.UM -> Flag.UM
        FlagDto.US -> Flag.US
        FlagDto.UY -> Flag.UY
        FlagDto.UZ -> Flag.UZ
        FlagDto.VA -> Flag.VA
        FlagDto.VC -> Flag.VC
        FlagDto.VE -> Flag.VE
        FlagDto.VG -> Flag.VG
        FlagDto.VI -> Flag.VI
        FlagDto.VN -> Flag.VN
        FlagDto.VU -> Flag.VU
        FlagDto.WF -> Flag.WF
        FlagDto.WS -> Flag.WS
        FlagDto.YE -> Flag.YE
        FlagDto.YT -> Flag.YT
        FlagDto.ZA -> Flag.ZA
        FlagDto.ZM -> Flag.ZM
        FlagDto.ZW -> Flag.ZW
        FlagDto.ZZ -> Flag.ZZ
        FlagDto.XK -> Flag.XK
    }
}