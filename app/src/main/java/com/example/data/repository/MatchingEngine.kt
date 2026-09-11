package com.example.data.repository

import com.example.data.local.entity.DonorMatchEntity
import com.example.data.local.entity.EmergencyRequestEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.NotificationDeliveryStatus
import com.example.data.model.DonorResponseStatus
import java.util.UUID
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object MatchingEngine {

    /**
     * Medically approved Red Blood Cell transfusion compatibility matrix.
     * Maps recipient blood group -> set of compatible donor blood groups.
     * Medical Safety: This rule cannot be overridden by non-medical users.
     */
    val COMPATIBILITY_MATRIX: Map<String, Set<String>> = mapOf(
        "O-" to setOf("O-"),
        "O+" to setOf("O-", "O+"),
        "A-" to setOf("O-", "A-"),
        "A+" to setOf("O-", "O+", "A-", "A+"),
        "B-" to setOf("O-", "B-"),
        "B+" to setOf("O-", "O+", "B-", "B+"),
        "AB-" to setOf("O-", "A-", "B-", "AB-"),
        "AB+" to setOf("O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+")
    )

    fun isBloodCompatible(donorGroup: String, recipientGroup: String): Boolean {
        val cleanRecipient = recipientGroup.trim().uppercase()
        val cleanDonor = donorGroup.trim().uppercase()
        val allowedDonors = COMPATIBILITY_MATRIX[cleanRecipient] ?: return false
        return allowedDonors.contains(cleanDonor)
    }

    fun getCompatibleDonorGroups(recipientGroup: String): Set<String> {
        val cleanRecipient = recipientGroup.trim().uppercase()
        return COMPATIBILITY_MATRIX[cleanRecipient] ?: emptySet()
    }

    /**
     * Haversine formula to compute great-circle distance between two coordinates in kilometers.
     */
    fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadiusKm * c
    }

    /**
     * Privacy Requirement:
     * Never expose donor's exact home address.
     * Return formatted approximate distance.
     */
    fun formatApproximateDistance(distanceKm: Double): String {
        return "Compatible donor approximately %.1f km away".format(distanceKm)
    }

    /**
     * Finds and ranks suitable donors for a verified emergency blood request.
     *
     * Matching factors:
     * 1. Blood compatibility (Strict medical rule)
     * 2. Donor availability (isDonorAvailable == true)
     * 3. Donor account status (ACTIVE)
     * 4. Distance within current search radius (Stage 1: 0-5km, Stage 2: 5-10km, Stage 3: 10-25km)
     * 5. Rank donors by compatibility (exact match bonus), availability, and proximity.
     */
    fun matchAndRankDonors(
        request: EmergencyRequestEntity,
        allDonors: List<UserEntity>,
        searchRadiusKm: Double = request.currentRadiusKm
    ): List<DonorMatchEntity> {
        val compatibleCandidates = allDonors.filter { donor ->
            donor.id != request.requesterUserId &&
            donor.accountStatus.equals("ACTIVE", ignoreCase = true) &&
            donor.isDonorAvailable &&
            isBloodCompatible(donor.bloodGroup, request.bloodGroup)
        }

        val candidatesWithDistance = compatibleCandidates.map { donor ->
            val dist = calculateDistanceKm(
                request.latitude,
                request.longitude,
                donor.latitude,
                donor.longitude
            )
            Triple(donor, dist, isExactMatch(donor.bloodGroup, request.bloodGroup))
        }
        .filter { (_, dist, _) -> dist <= searchRadiusKm }

        // Ranking formula:
        // Score = 100 - (distance * 2.0) + (if exact match 10 else 0)
        val ranked = candidatesWithDistance.sortedWith(
            compareByDescending<Triple<UserEntity, Double, Boolean>> { (_, _, exact) -> exact }
                .thenBy { (_, dist, _) -> dist }
        )

        return ranked.mapIndexed { index, (donor, dist, exact) ->
            val matchScore = (100.0 - (dist * 1.5) + (if (exact) 10.0 else 0.0)).coerceIn(10.0, 100.0)
            DonorMatchEntity(
                id = UUID.randomUUID().toString(),
                requestId = request.id,
                donorUserId = donor.id,
                donorName = donor.fullName,
                donorPhone = donor.phone,
                bloodGroup = donor.bloodGroup,
                distanceKm = Math.round(dist * 10.0) / 10.0,
                matchScore = Math.round(matchScore * 10.0) / 10.0,
                matchRank = index + 1,
                notificationStatus = NotificationDeliveryStatus.SENT,
                responseStatus = DonorResponseStatus.PENDING,
                notificationSentAt = System.currentTimeMillis()
            )
        }
    }

    private fun isExactMatch(donorGroup: String, recipientGroup: String): Boolean {
        return donorGroup.trim().equals(recipientGroup.trim(), ignoreCase = true)
    }

    /**
     * Smart radius expansion stages:
     * Stage 1: 5 km
     * Stage 2: 10 km
     * Stage 3: 25 km
     */
    fun getNextRadiusStage(currentRadiusKm: Double): Double {
        return when {
            currentRadiusKm < 5.0 -> 5.0
            currentRadiusKm < 10.0 -> 10.0
            currentRadiusKm < 25.0 -> 25.0
            else -> 50.0
        }
    }
}
