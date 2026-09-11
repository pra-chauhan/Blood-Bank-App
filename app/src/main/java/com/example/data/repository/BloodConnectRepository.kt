package com.example.data.repository

import com.example.data.local.BloodConnectDatabase
import com.example.data.local.DatabaseSeeder
import com.example.data.local.entity.AppNotificationEntity
import com.example.data.local.entity.AuditLogEntity
import com.example.data.local.entity.CampRegistrationEntity
import com.example.data.local.entity.DonationCampEntity
import com.example.data.local.entity.DonationCertificateEntity
import com.example.data.local.entity.DonorMatchEntity
import com.example.data.local.entity.EmergencyRequestEntity
import com.example.data.local.entity.EmergencyStatusHistoryEntity
import com.example.data.local.entity.HospitalEntity
import com.example.data.local.entity.BloodBankEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.VolunteerEntity
import com.example.data.model.CampStatus
import com.example.data.model.DonorResponseStatus
import com.example.data.model.NotificationDeliveryStatus
import com.example.data.model.NotificationType
import com.example.data.model.RequestStatus
import com.example.data.model.RequestUrgency
import com.example.data.model.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class BloodConnectRepository(
    private val database: BloodConnectDatabase
) {
    // Current Active User / Persona
    val currentUserFlow: Flow<UserEntity?> = database.userDao().getUserById(DatabaseSeeder.CURRENT_USER_ID)

    // Emergency Requests
    val allEmergencyRequestsFlow: Flow<List<EmergencyRequestEntity>> = database.emergencyRequestDao().getAllRequestsFlow()
    val activeEmergencyRequestsFlow: Flow<List<EmergencyRequestEntity>> = database.emergencyRequestDao().getActiveEmergencyRequestsFlow()
    val pendingVerificationRequestsFlow: Flow<List<EmergencyRequestEntity>> = database.emergencyRequestDao().getPendingRequestsFlow()

    // Facilities & Camps
    val allCampsFlow: Flow<List<DonationCampEntity>> = database.donationCampDao().getAllCampsFlow()
    val allHospitalsFlow: Flow<List<HospitalEntity>> = database.hospitalDao().getAllHospitalsFlow()
    val allBloodBanksFlow: Flow<List<BloodBankEntity>> = database.bloodBankDao().getAllBloodBanksFlow()
    val allVolunteersFlow: Flow<List<VolunteerEntity>> = database.volunteerDao().getAllVolunteersFlow()

    // Users & Donors
    val allUsersFlow: Flow<List<UserEntity>> = database.userDao().getAllUsersFlow()

    // Audit logs & Certificates
    val auditLogsFlow: Flow<List<AuditLogEntity>> = database.auditLogDao().getAllAuditLogsFlow()

    fun getRequestByIdFlow(requestId: String): Flow<EmergencyRequestEntity?> =
        database.emergencyRequestDao().getRequestByIdFlow(requestId)

    fun getStatusHistoryFlow(requestId: String): Flow<List<EmergencyStatusHistoryEntity>> =
        database.emergencyStatusHistoryDao().getHistoryForRequestFlow(requestId)

    fun getMatchesForRequestFlow(requestId: String): Flow<List<DonorMatchEntity>> =
        database.donorMatchDao().getMatchesForRequestFlow(requestId)

    fun getNotificationsForUserFlow(userId: String): Flow<List<AppNotificationEntity>> =
        database.notificationDao().getNotificationsForUserFlow(userId)

    fun getCertificatesForUserFlow(userId: String): Flow<List<DonationCertificateEntity>> =
        database.certificateDao().getCertificatesForUserFlow(userId)

    fun getUserCampRegistrationsFlow(userId: String): Flow<List<CampRegistrationEntity>> =
        database.campRegistrationDao().getRegistrationsForUserFlow(userId)

    // User profile updates
    suspend fun updateDonorAvailability(userId: String, isAvailable: Boolean) {
        database.userDao().updateAvailability(userId, isAvailable)
        database.auditLogDao().insertAuditLog(
            AuditLogEntity(
                actorName = "Donor",
                actorRole = "DONOR",
                action = "UPDATE_AVAILABILITY",
                targetEntity = "User",
                targetId = userId,
                metadata = "Changed availability to $isAvailable"
            )
        )
    }

    suspend fun updateUserProfile(user: UserEntity) {
        database.userDao().updateUser(user)
    }

    // Role switching for testing all roles seamlessly
    suspend fun switchUserRole(userId: String, newRole: UserRole) {
        val user = database.userDao().getUserSync(userId) ?: return
        database.userDao().updateUser(user.copy(role = newRole))
    }

    // Emergency Request Creation
    suspend fun submitEmergencyRequest(
        patientName: String,
        requesterUserId: String,
        requesterPhone: String,
        bloodGroup: String,
        unitsRequired: Int,
        hospitalName: String,
        hospitalAddress: String,
        latitude: Double,
        longitude: Double,
        urgency: RequestUrgency,
        requiredDate: String,
        requiredTime: String,
        attendantPhone: String,
        additionalNotes: String
    ): String {
        val requestId = "req_" + UUID.randomUUID().toString().take(8)
        val newRequest = EmergencyRequestEntity(
            id = requestId,
            patientName = patientName,
            requesterUserId = requesterUserId,
            requesterPhone = requesterPhone,
            bloodGroup = bloodGroup,
            unitsRequired = unitsRequired,
            hospitalName = hospitalName,
            hospitalAddress = hospitalAddress,
            latitude = latitude,
            longitude = longitude,
            urgency = urgency,
            requiredDate = requiredDate,
            requiredTime = requiredTime,
            attendantPhone = attendantPhone,
            additionalNotes = additionalNotes,
            status = RequestStatus.VERIFICATION_PENDING,
            verificationStatus = "PENDING"
        )
        database.emergencyRequestDao().insertRequest(newRequest)

        // Status history entry
        database.emergencyStatusHistoryDao().insertHistory(
            EmergencyStatusHistoryEntity(
                requestId = requestId,
                status = RequestStatus.SUBMITTED,
                title = "Emergency Request Submitted",
                description = "Request submitted for $unitsRequired units $bloodGroup at $hospitalName.",
                actorName = "Requester ($attendantPhone)"
            )
        )

        // Notification to NGO Admins
        database.notificationDao().insertNotification(
            AppNotificationEntity(
                targetUserId = DatabaseSeeder.ADMIN_USER_ID,
                title = "🚨 New Emergency Request to Verify",
                body = "$urgency request: $unitsRequired units $bloodGroup needed for $patientName at $hospitalName.",
                type = NotificationType.EMERGENCY_REQUEST,
                relatedId = requestId
            )
        )

        database.auditLogDao().insertAuditLog(
            AuditLogEntity(
                actorName = "Requester",
                actorRole = "USER",
                action = "CREATE_EMERGENCY_REQUEST",
                targetEntity = "EmergencyRequest",
                targetId = requestId,
                metadata = "Blood group: $bloodGroup, Units: $unitsRequired, Hospital: $hospitalName"
            )
        )

        return requestId
    }

    // NGO Verification Workflow
    suspend fun verifyEmergencyRequest(
        requestId: String,
        adminUserId: String,
        adminName: String,
        approved: Boolean,
        rejectionReason: String = ""
    ) {
        val request = database.emergencyRequestDao().getRequestByIdSync(requestId) ?: return
        if (!approved) {
            database.emergencyRequestDao().verifyRequest(
                id = requestId,
                verStatus = "REJECTED",
                adminId = adminUserId,
                newStatus = RequestStatus.REJECTED
            )
            database.emergencyStatusHistoryDao().insertHistory(
                EmergencyStatusHistoryEntity(
                    requestId = requestId,
                    status = RequestStatus.REJECTED,
                    title = "Request Rejected by NGO",
                    description = rejectionReason.ifEmpty { "Verification could not be confirmed with hospital authority." },
                    actorName = adminName
                )
            )
            database.auditLogDao().insertAuditLog(
                AuditLogEntity(
                    actorName = adminName,
                    actorRole = "NGO_ADMIN",
                    action = "REJECT_EMERGENCY_REQUEST",
                    targetEntity = "EmergencyRequest",
                    targetId = requestId,
                    metadata = rejectionReason
                )
            )
            return
        }

        // Approved
        database.emergencyRequestDao().verifyRequest(
            id = requestId,
            verStatus = "APPROVED",
            adminId = adminUserId,
            newStatus = RequestStatus.VERIFIED
        )

        database.emergencyStatusHistoryDao().insertHistory(
            EmergencyStatusHistoryEntity(
                requestId = requestId,
                status = RequestStatus.VERIFIED,
                title = "Hospital & Need Verified",
                description = "Patient registration and requirement validated with ${request.hospitalName}.",
                actorName = adminName
            )
        )

        // Automatically trigger Donor Matching Engine
        triggerDonorMatching(requestId, searchRadiusKm = request.currentRadiusKm)
    }

    // Matching Engine Trigger
    suspend fun triggerDonorMatching(requestId: String, searchRadiusKm: Double) {
        val request = database.emergencyRequestDao().getRequestByIdSync(requestId) ?: return
        val allUsers = database.userDao().getAllUsersSync()

        val matchedDonors = MatchingEngine.matchAndRankDonors(
            request = request,
            allDonors = allUsers,
            searchRadiusKm = searchRadiusKm
        )

        if (matchedDonors.isNotEmpty()) {
            database.donorMatchDao().insertMatches(matchedDonors)
            database.emergencyRequestDao().updateStatus(requestId, RequestStatus.FINDING_DONORS)

            database.emergencyStatusHistoryDao().insertHistory(
                EmergencyStatusHistoryEntity(
                    requestId = requestId,
                    status = RequestStatus.FINDING_DONORS,
                    title = "Matching Engine Activated",
                    description = "Found ${matchedDonors.size} compatible donors within ${searchRadiusKm} km radius. Notifications dispatched.",
                    actorName = "BloodConnect Matching Engine"
                )
            )

            // Dispatch Push Notifications to matched donors
            matchedDonors.forEach { match ->
                database.notificationDao().insertNotification(
                    AppNotificationEntity(
                        targetUserId = match.donorUserId,
                        title = "🚨 Emergency Blood Request Nearby",
                        body = "${request.bloodGroup} blood urgently required at ${request.hospitalName} (${MatchingEngine.formatApproximateDistance(match.distanceKm)}). Can you donate?",
                        type = NotificationType.EMERGENCY_REQUEST,
                        relatedId = requestId,
                        status = NotificationDeliveryStatus.SENT
                    )
                )
            }
        } else {
            database.emergencyStatusHistoryDao().insertHistory(
                EmergencyStatusHistoryEntity(
                    requestId = requestId,
                    status = RequestStatus.FINDING_DONORS,
                    title = "Expanding Search Radius",
                    description = "No immediate donors found within ${searchRadiusKm} km. Recommended radius expansion.",
                    actorName = "BloodConnect Matching Engine"
                )
            )
        }
    }

    // Smart Radius Expansion
    suspend fun expandNotificationRadius(requestId: String, adminName: String) {
        val request = database.emergencyRequestDao().getRequestByIdSync(requestId) ?: return
        val nextRadius = MatchingEngine.getNextRadiusStage(request.currentRadiusKm)
        database.emergencyRequestDao().updateSearchRadius(requestId, nextRadius)

        database.auditLogDao().insertAuditLog(
            AuditLogEntity(
                actorName = adminName,
                actorRole = "NGO_ADMIN",
                action = "EXPAND_SEARCH_RADIUS",
                targetEntity = "EmergencyRequest",
                targetId = requestId,
                metadata = "Expanded radius from ${request.currentRadiusKm} km to $nextRadius km"
            )
        )

        triggerDonorMatching(requestId, searchRadiusKm = nextRadius)
    }

    // Donor Response
    suspend fun recordDonorResponse(
        requestId: String,
        donorUserId: String,
        response: DonorResponseStatus
    ) {
        val match = database.donorMatchDao().getMatch(requestId, donorUserId)
        if (match != null) {
            database.donorMatchDao().updateResponse(match.id, response)
        }
        val request = database.emergencyRequestDao().getRequestByIdSync(requestId) ?: return
        val donor = database.userDao().getUserSync(donorUserId)

        if (response == DonorResponseStatus.ACCEPTED) {
            database.emergencyRequestDao().updateStatus(requestId, RequestStatus.DONOR_CONFIRMED)

            database.emergencyStatusHistoryDao().insertHistory(
                EmergencyStatusHistoryEntity(
                    requestId = requestId,
                    status = RequestStatus.DONOR_CONFIRMED,
                    title = "Donor Accepted Request",
                    description = "${donor?.fullName ?: "Compatible donor"} confirmed willingness to donate. Coordination underway.",
                    actorName = donor?.fullName ?: "Donor"
                )
            )

            // Notify Admin and Attendant
            database.notificationDao().insertNotification(
                AppNotificationEntity(
                    targetUserId = DatabaseSeeder.ADMIN_USER_ID,
                    title = "✅ Donor Confirmed for Emergency",
                    body = "${donor?.fullName} accepted request for ${request.patientName} at ${request.hospitalName}.",
                    type = NotificationType.DONOR_ACCEPTED,
                    relatedId = requestId
                )
            )
        }
    }

    // Step-by-step Request Progression (Donor Arrived, Donation Completed, Fulfilled)
    suspend fun updateRequestStatus(
        requestId: String,
        newStatus: RequestStatus,
        actorName: String,
        notes: String = ""
    ) {
        val request = database.emergencyRequestDao().getRequestByIdSync(requestId) ?: return
        database.emergencyRequestDao().updateStatus(requestId, newStatus)

        val title = when (newStatus) {
            RequestStatus.DONOR_ARRIVED -> "Donor Arrived at Hospital"
            RequestStatus.DONATION_COMPLETED -> "Donation Successfully Completed"
            RequestStatus.FULFILLED -> "Emergency Request Fulfilled"
            RequestStatus.CANCELLED -> "Request Cancelled"
            else -> newStatus.display
        }

        database.emergencyStatusHistoryDao().insertHistory(
            EmergencyStatusHistoryEntity(
                requestId = requestId,
                status = newStatus,
                title = title,
                description = notes.ifEmpty { "Status updated to ${newStatus.display} by $actorName." },
                actorName = actorName
            )
        )

        // If completed or fulfilled, generate official Certificate for donor if donor exists
        if (newStatus == RequestStatus.DONATION_COMPLETED || newStatus == RequestStatus.FULFILLED) {
            generateDonationCertificate(request)
        }
    }

    private suspend fun generateDonationCertificate(request: EmergencyRequestEntity) {
        val matches = database.donorMatchDao().getMatchesForRequestFlow(request.id).first()
        val acceptedMatch = matches.find { it.responseStatus == DonorResponseStatus.ACCEPTED }
        val donorId = acceptedMatch?.donorUserId ?: DatabaseSeeder.CURRENT_USER_ID
        val donor = database.userDao().getUserSync(donorId)
        val donorName = donor?.fullName ?: acceptedMatch?.donorName ?: "Rahul Sharma"

        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        val todayStr = dateFormat.format(Date())
        val certNum = "BC-2026-DEL-" + (10000..99999).random()
        val verificationCode = "BC-VER-" + (100000..999999).random()

        val qrData = "https://bloodconnect.org/verify?cert=$certNum&donor=${donorName.replace(" ", "+")}&hospital=${request.hospitalName.replace(" ", "+")}&units=${request.unitsRequired}&status=VERIFIED"

        val certificate = DonationCertificateEntity(
            certificateNumber = certNum,
            donorUserId = donorId,
            donorName = donorName,
            donationDate = todayStr,
            hospitalOrCamp = request.hospitalName,
            bloodGroup = request.bloodGroup,
            units = request.unitsRequired,
            verificationCode = verificationCode,
            qrCodeData = qrData
        )
        database.certificateDao().insertCertificate(certificate)

        // Notify donor
        database.notificationDao().insertNotification(
            AppNotificationEntity(
                targetUserId = donorId,
                title = "🎉 Donation Certificate Issued!",
                body = "Thank you for saving lives! Your Certificate $certNum has been generated.",
                type = NotificationType.CERTIFICATE_ISSUED,
                relatedId = certificate.id
            )
        )
    }

    // Camp Registration
    suspend fun registerForCamp(campId: String, user: UserEntity, slotTime: String = "10:30 AM") {
        val registration = CampRegistrationEntity(
            campId = campId,
            userId = user.id,
            userName = user.fullName,
            bloodGroup = user.bloodGroup,
            slotTime = slotTime
        )
        database.campRegistrationDao().insertRegistration(registration)
        database.donationCampDao().incrementCampRegistration(campId)

        val camp = database.donationCampDao().getCampByIdSync(campId)
        database.notificationDao().insertNotification(
            AppNotificationEntity(
                targetUserId = user.id,
                title = "🩸 Camp Registration Confirmed",
                body = "You are confirmed for ${camp?.name ?: "Blood Donation Camp"} on ${camp?.date}. Slot: $slotTime.",
                type = NotificationType.CAMP_ALERT,
                relatedId = campId
            )
        )
    }

    suspend fun cancelCampRegistration(campId: String, userId: String) {
        database.campRegistrationDao().cancelRegistration(campId, userId)
    }

    // Admin Camp Management
    suspend fun createCamp(camp: DonationCampEntity, adminName: String) {
        database.donationCampDao().insertCamp(camp)
        database.auditLogDao().insertAuditLog(
            AuditLogEntity(
                actorName = adminName,
                actorRole = "NGO_ADMIN",
                action = "CREATE_CAMP",
                targetEntity = "DonationCamp",
                targetId = camp.id,
                metadata = "Camp: ${camp.name}, City: ${camp.city}, Capacity: ${camp.capacity}"
            )
        )
    }
}
