package com.example

import com.example.data.repository.MatchingEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testUniversalDonorCompatibility() {
        // O- is universal red cell donor
        assertTrue(MatchingEngine.isBloodCompatible("O-", "O-"))
        assertTrue(MatchingEngine.isBloodCompatible("O-", "O+"))
        assertTrue(MatchingEngine.isBloodCompatible("O-", "A-"))
        assertTrue(MatchingEngine.isBloodCompatible("O-", "A+"))
        assertTrue(MatchingEngine.isBloodCompatible("O-", "B-"))
        assertTrue(MatchingEngine.isBloodCompatible("O-", "B+"))
        assertTrue(MatchingEngine.isBloodCompatible("O-", "AB-"))
        assertTrue(MatchingEngine.isBloodCompatible("O-", "AB+"))
    }

    @Test
    fun testUniversalRecipientCompatibility() {
        // AB+ can receive from all groups
        assertTrue(MatchingEngine.isBloodCompatible("O-", "AB+"))
        assertTrue(MatchingEngine.isBloodCompatible("O+", "AB+"))
        assertTrue(MatchingEngine.isBloodCompatible("A-", "AB+"))
        assertTrue(MatchingEngine.isBloodCompatible("A+", "AB+"))
        assertTrue(MatchingEngine.isBloodCompatible("B-", "AB+"))
        assertTrue(MatchingEngine.isBloodCompatible("B+", "AB+"))
        assertTrue(MatchingEngine.isBloodCompatible("AB-", "AB+"))
        assertTrue(MatchingEngine.isBloodCompatible("AB+", "AB+"))
    }

    @Test
    fun testIncompatibleBloodTransfusionSafety() {
        // Positive cannot donate to Negative
        assertFalse(MatchingEngine.isBloodCompatible("O+", "O-"))
        assertFalse(MatchingEngine.isBloodCompatible("A+", "A-"))
        assertFalse(MatchingEngine.isBloodCompatible("B+", "B-"))
        assertFalse(MatchingEngine.isBloodCompatible("AB+", "AB-"))

        // Cross ABO incompatibility
        assertFalse(MatchingEngine.isBloodCompatible("A+", "B+"))
        assertFalse(MatchingEngine.isBloodCompatible("B+", "A+"))
        assertFalse(MatchingEngine.isBloodCompatible("AB+", "O+"))
    }

    @Test
    fun testHaversineDistanceCalculation() {
        // Distance between Connaught Place (28.6304, 77.2177) and AIIMS Delhi (28.5672, 77.2100)
        val distance = MatchingEngine.calculateDistanceKm(28.6304, 77.2177, 28.5672, 77.2100)
        // Distance is approx 7 km
        assertTrue("Distance should be between 6.5 and 7.5 km", distance in 6.5..7.5)
    }

    @Test
    fun testPrivacySafeDistanceFormatting() {
        val formatted = MatchingEngine.formatApproximateDistance(3.82)
        assertEquals("Compatible donor approximately 3.8 km away", formatted)
    }

    @Test
    fun testSmartRadiusExpansionStages() {
        assertEquals(5.0, MatchingEngine.getNextRadiusStage(0.0), 0.01)
        assertEquals(10.0, MatchingEngine.getNextRadiusStage(5.0), 0.01)
        assertEquals(25.0, MatchingEngine.getNextRadiusStage(10.0), 0.01)
        assertEquals(50.0, MatchingEngine.getNextRadiusStage(25.0), 0.01)
    }
}
