package pl.cuyer.rusthub.data.local

import pl.cuyer.rusthub.database.RustHubDatabase

abstract class Queries(
    db: RustHubDatabase
) {
    val queries = db.rusthubDBQueries
}