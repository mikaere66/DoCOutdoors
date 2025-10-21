package com.michaelrmossman.docoutdoors.data

import com.michaelrmossman.docoutdoors.database.RegionsDao
import com.michaelrmossman.docoutdoors.enums.ByRegionType

/**
 * Actual implementation of RegionsRepoBase
 */
class RegionsRepository(
    private val regionsDao: RegionsDao,
) : RegionsRepoBase {

    override suspend fun getRegionCodeByActualId(
        id: Int, byRegionType: ByRegionType?
    ) : String {
        /* Record the fact that we're downloading for specific
           region, then if user goes back to ALL items, show
           a message about list incomplete, if appropriate ...
           only relevant to Alerts | Tracks entity types, but
           is not used when called from AutoUpdateWorker() */
        byRegionType?.let { type ->
            when (type) {
                ByRegionType.Alerts -> regionsDao.setAlertsDload(
                    id = id
                )
                ByRegionType.Tracks -> regionsDao.setTracksDload(
                    id = id
                )
            }
        }

        return regionsDao.getRegionCodeByActualId(id)
    }

    override suspend fun resetAlertsDload() =
        regionsDao.resetAlertsDload()

    override suspend fun resetTracksDload() =
        regionsDao.resetTracksDload()
}