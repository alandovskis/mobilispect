@file:Suppress("MatchingDeclarationName")
package com.mobilispect.backend.batch

import com.mobilispect.backend.data.Identified
import com.mobilispect.backend.data.Versioned

interface Entity : Identified, Versioned

/**
 * @return true if already existing or is newer, false otherwise
 */
fun <T : Entity> entityExistsOrIsNewer(
    localMap: Map<String, T>,
    entity: T
) = !localMap.contains(entity._id) || (localMap[entity._id]!!.version != entity.version)
