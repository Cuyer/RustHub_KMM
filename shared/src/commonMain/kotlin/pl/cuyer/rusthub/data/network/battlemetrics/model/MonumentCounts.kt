package domain.models.server.battlemetrics


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MonumentCounts(
    @SerialName("Airfield")
    val airfield: Int?,
    @SerialName("Anvil Rock")
    val anvilRock: Int?,
    @SerialName("Arctic Research Base")
    val arcticResearchBase: Int?,
    @SerialName("Canyon")
    val canyon: Int?,
    @SerialName("Cave Large Hard")
    val caveLargeHard: Int?,
    @SerialName("Cave Large Sewers Hard")
    val caveLargeSewersHard: Int?,
    @SerialName("Cave Medium Easy")
    val caveMediumEasy: Int?,
    @SerialName("Cave Medium Hard")
    val caveMediumHard: Int?,
    @SerialName("Cave Medium Medium")
    val caveMediumMedium: Int?,
    @SerialName("Cave Small Easy")
    val caveSmallEasy: Int?,
    @SerialName("Cave Small Hard")
    val caveSmallHard: Int?,
    @SerialName("Cave Small Medium")
    val caveSmallMedium: Int?,
    @SerialName("Excavator")
    val excavator: Int?,
    @SerialName("Ferry Terminal")
    val ferryTerminal: Int?,
    @SerialName("Fishing Village")
    val fishingVillage: Int?,
    @SerialName("Gas Station")
    val gasStation: Int?,
    @SerialName("Hqm Quarry")
    val hqmQuarry: Int?,
    @SerialName("Ice Lake")
    val iceLake: Int?,
    @SerialName("Iceberg")
    val iceberg: Int?,
    @SerialName("Junkyard")
    val junkyard: Int?,
    @SerialName("Lake")
    val lake: Int?,
    @SerialName("Large Barn")
    val largeBarn: Int?,
    @SerialName("Large God Rock")
    val largeGodRock: Int?,
    @SerialName("Large Harbor")
    val largeHarbor: Int?,
    @SerialName("Large Oilrig")
    val largeOilrig: Int?,
    @SerialName("Launch Site")
    val launchSite: Int?,
    @SerialName("Lighthouse")
    val lighthouse: Int?,
    @SerialName("Medium God Rock")
    val mediumGodRock: Int?,
    @SerialName("Military Base")
    val militaryBase: Int?,
    @SerialName("Military Tunnels")
    val militaryTunnels: Int?,
    @SerialName("Nuclear Missile Silo")
    val nuclearMissileSilo: Int?,
    @SerialName("Oasis")
    val oasis: Int?,
    @SerialName("Outpost")
    val outpost: Int?,
    @SerialName("Power Substation Big")
    val powerSubstationBig: Int?,
    @SerialName("Power Substation Small")
    val powerSubstationSmall: Int?,
    @SerialName("Powerline")
    val powerline: Int?,
    @SerialName("Powerplant")
    val powerplant: Int?,
    @SerialName("Radtown")
    val radtown: Int?,
    @SerialName("Ranch")
    val ranch: Int?,
    @SerialName("Ruin")
    val ruin: Int?,
    @SerialName("Satellite Dish")
    val satelliteDish: Int?,
    @SerialName("Sewer Branch")
    val sewerBranch: Int?,
    @SerialName("Small Harbor")
    val smallHarbor: Int?,
    @SerialName("Small Oilrig")
    val smallOilrig: Int?,
    @SerialName("Sphere Tank")
    val sphereTank: Int?,
    @SerialName("Stone Quarry")
    val stoneQuarry: Int?,
    @SerialName("Sulfur Quarry")
    val sulfurQuarry: Int?,
    @SerialName("Supermarket")
    val supermarket: Int?,
    @SerialName("Tiny God Rock")
    val tinyGodRock: Int?,
    @SerialName("Trainyard")
    val trainyard: Int?,
    @SerialName("Tunnel Entrance")
    val tunnelEntrance: Int?,
    @SerialName("Tunnel Entrance Transition")
    val tunnelEntranceTransition: Int?,
    @SerialName("Underwater Lab")
    val underwaterLab: Int?,
    @SerialName("3 Wall Rock")
    val wallRock: Int?,
    @SerialName("Warehouse")
    val warehouse: Int?,
    @SerialName("Water Treatment")
    val waterTreatment: Int?,
    @SerialName("Water Well")
    val waterWell: Int?
)