package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.CampStatus
import com.example.data.model.DonorResponseStatus
import com.example.data.model.NotificationDeliveryStatus
import com.example.data.model.NotificationType
import com.example.data.model.RequestStatus
import com.example.data.model.RequestUrgency
import com.example.data.model.UserRole
import java.util.UUID

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val fullName: String,
    val phone: String,
    val email: String,
    val dateOfBirth: String = "1998-05-15",
    val gender: String = "Male",
    val bloodGroup: String = "O+",
    val role: UserRole = UserRole.DONOR,
    val city: String = "New Delhi",
    val savedAddress: String = "Connaught Place, Central Delhi",
    val latitude: Double = 28.6139,
    val longitude: Double = 77.2090,
    val emergencyContact: String = "+91 9876543210",
    val isDonorAvailable: Boolean = true,
    val lastDonationDate: String? = null,
    val accountStatus: String = "ACTIVE",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "donor_profiles")
data class DonorProfileEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val bloodGroup: String,
    val totalDonations: Int = 0,
    val isEligible: Boolean = true,
    val lastDonationTimestamp: Long? = null,
    val preferredMaxRadiusKm: Double = 25.0,
    val notificationsEnabled: Boolean = true,
    val quietHoursEnabled: Boolean = false,
    val quietHoursStart: String = "22:00",
    val quietHoursEnd: String = "07:00"
)

@Entity(tableName = "emergency_requests")
data class EmergencyRequestEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val patientName: String,
    val requesterUserId: String,
    val requesterPhone: String,
    val bloodGroup: String,
    val unitsRequired: Int = 1,
    val hospitalName: String,
    val hospitalAddress: String,
    val latitude: Double,
    val longitude: Double,
    val urgency: RequestUrgency = RequestUrgency.CRITICAL,
    val requiredDate: String,
    val requiredTime: String,
    val attendantPhone: String,
    val additionalNotes: String = "",
    val supportingDocument: String = "",
    val hasConsent: Boolean = true,
    val status: RequestStatus = RequestStatus.VERIFICATION_PENDING,
    val verificationStatus: String = "PENDING", // PENDING, APPROVED, REJECTED
    val verifiedByAdminId: String? = null,
    val assignedVolunteerId: String? = null,
    val assignedVolunteerName: String? = null,
    val currentRadiusKm: Double = 5.0,
    val matchedDonorsCount: Int = 0,
    val acceptedDonorsCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "emergency_status_history")
data class EmergencyStatusHistoryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val requestId: String,
    val status: RequestStatus,
    val title: String,
    val description: String,
    val actorName: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "donor_matches")
data class DonorMatchEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val requestId: String,
    val donorUserId: String,
    val donorName: String,
    val donorPhone: String,
    val bloodGroup: String,
    val distanceKm: Double,
    val matchScore: Double,
    val matchRank: Int,
    val notificationStatus: NotificationDeliveryStatus = NotificationDeliveryStatus.SENT,
    val responseStatus: DonorResponseStatus = DonorResponseStatus.PENDING,
    val notificationSentAt: Long = System.currentTimeMillis(),
    val respondedAt: Long? = null
)

@Entity(tableName = "donation_camps")
data class DonationCampEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val ngoName: String = "BloodConnect NGO",
    val organizer: String,
    val address: String,
    val city: String,
    val latitude: Double,
    val longitude: Double,
    val date: String,
    val startTime: String,
    val endTime: String,
    val contactPhone: String,
    val bloodGroupsNeeded: String, // Comma separated, e.g. "O+, A+, B+, AB+, All"
    val capacity: Int,
    val registrationCount: Int = 0,
    val status: CampStatus = CampStatus.UPCOMING,
    val instructions: String,
    val imageUrl: String = ""
)

@Entity(tableName = "camp_registrations")
data class CampRegistrationEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val campId: String,
    val userId: String,
    val userName: String,
    val bloodGroup: String,
    val slotTime: String,
    val registeredAt: Long = System.currentTimeMillis(),
    val status: String = "CONFIRMED" // CONFIRMED, ATTENDED, CANCELLED
)

@Entity(tableName = "hospitals")
data class HospitalEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val address: String,
    val city: String,
    val phone: String,
    val emergencyContact: String,
    val latitude: Double,
    val longitude: Double,
    val isVerified: Boolean = true,
    val hasBloodBank: Boolean = true
)

@Entity(tableName = "blood_banks")
data class BloodBankEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val address: String,
    val city: String,
    val phone: String,
    val latitude: Double,
    val longitude: Double,
    val openingHours: String = "24x7 Emergency Service",
    val services: String = "Whole Blood, Platelets, Plasma, Component Separation",
    val isVerified: Boolean = true
)

@Entity(tableName = "volunteers")
data class VolunteerEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val name: String,
    val phone: String,
    val city: String,
    val activeAssignments: Int = 0,
    val completedCoordinations: Int = 12,
    val isAvailable: Boolean = true
)

@Entity(tableName = "notifications")
data class AppNotificationEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val targetUserId: String,
    val title: String,
    val body: String,
    val type: NotificationType,
    val relatedId: String? = null,
    val status: NotificationDeliveryStatus = NotificationDeliveryStatus.SENT,
    val actionTaken: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "donation_certificates")
data class DonationCertificateEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val certificateNumber: String,
    val donorUserId: String,
    val donorName: String,
    val donationDate: String,
    val hospitalOrCamp: String,
    val bloodGroup: String,
    val units: Int = 1,
    val verificationCode: String,
    val qrCodeData: String,
    val issuedBy: String = "BloodConnect NGO Central Registry"
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val actorName: String,
    val actorRole: String,
    val action: String,
    val targetEntity: String,
    val targetId: String,
    val metadata: String = "",
    val organization: String = "BloodConnect NGO",
    val timestamp: Long = System.currentTimeMillis()
)
