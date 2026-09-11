package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.AppNotificationEntity
import com.example.data.local.entity.AuditLogEntity
import com.example.data.local.entity.CampRegistrationEntity
import com.example.data.local.entity.DonationCampEntity
import com.example.data.local.entity.DonationCertificateEntity
import com.example.data.local.entity.DonorMatchEntity
import com.example.data.local.entity.DonorProfileEntity
import com.example.data.local.entity.EmergencyRequestEntity
import com.example.data.local.entity.EmergencyStatusHistoryEntity
import com.example.data.local.entity.HospitalEntity
import com.example.data.local.entity.BloodBankEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.VolunteerEntity
import com.example.data.model.CampStatus
import com.example.data.model.DonorResponseStatus
import com.example.data.model.NotificationDeliveryStatus
import com.example.data.model.RequestStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserSync(id: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    suspend fun getAllUsersSync(): List<UserEntity>

    @Query("SELECT * FROM users WHERE isDonorAvailable = 1 AND accountStatus = 'ACTIVE'")
    suspend fun getAvailableDonors(): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET isDonorAvailable = :isAvailable WHERE id = :userId")
    suspend fun updateAvailability(userId: String, isAvailable: Boolean)

    @Query("SELECT * FROM donor_profiles WHERE userId = :userId LIMIT 1")
    suspend fun getDonorProfile(userId: String): DonorProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonorProfile(profile: DonorProfileEntity)
}

@Dao
interface EmergencyRequestDao {
    @Query("SELECT * FROM emergency_requests ORDER BY createdAt DESC")
    fun getAllRequestsFlow(): Flow<List<EmergencyRequestEntity>>

    @Query("SELECT * FROM emergency_requests WHERE id = :id")
    fun getRequestByIdFlow(id: String): Flow<EmergencyRequestEntity?>

    @Query("SELECT * FROM emergency_requests WHERE id = :id LIMIT 1")
    suspend fun getRequestByIdSync(id: String): EmergencyRequestEntity?

    @Query("SELECT * FROM emergency_requests WHERE status = 'VERIFICATION_PENDING' ORDER BY createdAt DESC")
    fun getPendingRequestsFlow(): Flow<List<EmergencyRequestEntity>>

    @Query("SELECT * FROM emergency_requests WHERE status IN ('VERIFIED', 'FINDING_DONORS', 'DONOR_CONTACTED', 'DONOR_CONFIRMED', 'DONOR_ARRIVED') ORDER BY createdAt DESC")
    fun getActiveEmergencyRequestsFlow(): Flow<List<EmergencyRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: EmergencyRequestEntity)

    @Update
    suspend fun updateRequest(request: EmergencyRequestEntity)

    @Query("UPDATE emergency_requests SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateStatus(id: String, status: RequestStatus, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE emergency_requests SET verificationStatus = :verStatus, verifiedByAdminId = :adminId, status = :newStatus, updatedAt = :updatedAt WHERE id = :id")
    suspend fun verifyRequest(id: String, verStatus: String, adminId: String, newStatus: RequestStatus, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE emergency_requests SET assignedVolunteerId = :volId, assignedVolunteerName = :volName WHERE id = :id")
    suspend fun assignVolunteer(id: String, volId: String, volName: String)

    @Query("UPDATE emergency_requests SET currentRadiusKm = :radius, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateSearchRadius(id: String, radius: Double, updatedAt: Long = System.currentTimeMillis())
}

@Dao
interface EmergencyStatusHistoryDao {
    @Query("SELECT * FROM emergency_status_history WHERE requestId = :requestId ORDER BY timestamp ASC")
    fun getHistoryForRequestFlow(requestId: String): Flow<List<EmergencyStatusHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: EmergencyStatusHistoryEntity)
}

@Dao
interface DonorMatchDao {
    @Query("SELECT * FROM donor_matches WHERE requestId = :requestId ORDER BY matchRank ASC")
    fun getMatchesForRequestFlow(requestId: String): Flow<List<DonorMatchEntity>>

    @Query("SELECT * FROM donor_matches WHERE donorUserId = :donorUserId ORDER BY notificationSentAt DESC")
    fun getMatchesForDonorFlow(donorUserId: String): Flow<List<DonorMatchEntity>>

    @Query("SELECT * FROM donor_matches WHERE requestId = :requestId AND donorUserId = :donorUserId LIMIT 1")
    suspend fun getMatch(requestId: String, donorUserId: String): DonorMatchEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matches: List<DonorMatchEntity>)

    @Query("UPDATE donor_matches SET responseStatus = :status, respondedAt = :respondedAt WHERE id = :matchId")
    suspend fun updateResponse(matchId: String, status: DonorResponseStatus, respondedAt: Long = System.currentTimeMillis())

    @Query("UPDATE donor_matches SET notificationStatus = :status WHERE id = :matchId")
    suspend fun updateNotificationDelivery(matchId: String, status: NotificationDeliveryStatus)
}

@Dao
interface DonationCampDao {
    @Query("SELECT * FROM donation_camps ORDER BY date ASC")
    fun getAllCampsFlow(): Flow<List<DonationCampEntity>>

    @Query("SELECT * FROM donation_camps WHERE status = :status ORDER BY date ASC")
    fun getCampsByStatusFlow(status: CampStatus): Flow<List<DonationCampEntity>>

    @Query("SELECT * FROM donation_camps WHERE id = :id")
    fun getCampByIdFlow(id: String): Flow<DonationCampEntity?>

    @Query("SELECT * FROM donation_camps WHERE id = :id LIMIT 1")
    suspend fun getCampByIdSync(id: String): DonationCampEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCamp(camp: DonationCampEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCamps(camps: List<DonationCampEntity>)

    @Update
    suspend fun updateCamp(camp: DonationCampEntity)

    @Query("UPDATE donation_camps SET registrationCount = registrationCount + 1 WHERE id = :id")
    suspend fun incrementCampRegistration(id: String)
}

@Dao
interface CampRegistrationDao {
    @Query("SELECT * FROM camp_registrations WHERE userId = :userId ORDER BY registeredAt DESC")
    fun getRegistrationsForUserFlow(userId: String): Flow<List<CampRegistrationEntity>>

    @Query("SELECT * FROM camp_registrations WHERE campId = :campId ORDER BY registeredAt DESC")
    fun getCampRegistrationsFlow(campId: String): Flow<List<CampRegistrationEntity>>

    @Query("SELECT * FROM camp_registrations WHERE campId = :campId AND userId = :userId LIMIT 1")
    suspend fun getUserRegistrationForCamp(campId: String, userId: String): CampRegistrationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegistration(registration: CampRegistrationEntity)

    @Query("DELETE FROM camp_registrations WHERE campId = :campId AND userId = :userId")
    suspend fun cancelRegistration(campId: String, userId: String)
}

@Dao
interface HospitalDao {
    @Query("SELECT * FROM hospitals ORDER BY name ASC")
    fun getAllHospitalsFlow(): Flow<List<HospitalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHospitals(hospitals: List<HospitalEntity>)
}

@Dao
interface BloodBankDao {
    @Query("SELECT * FROM blood_banks ORDER BY name ASC")
    fun getAllBloodBanksFlow(): Flow<List<BloodBankEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBloodBanks(bloodBanks: List<BloodBankEntity>)
}

@Dao
interface VolunteerDao {
    @Query("SELECT * FROM volunteers ORDER BY name ASC")
    fun getAllVolunteersFlow(): Flow<List<VolunteerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVolunteers(volunteers: List<VolunteerEntity>)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE targetUserId = :userId ORDER BY timestamp DESC")
    fun getNotificationsForUserFlow(userId: String): Flow<List<AppNotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotificationEntity)

    @Query("UPDATE notifications SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: NotificationDeliveryStatus)
}

@Dao
interface CertificateDao {
    @Query("SELECT * FROM donation_certificates WHERE donorUserId = :userId ORDER BY donationDate DESC")
    fun getCertificatesForUserFlow(userId: String): Flow<List<DonationCertificateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCertificate(certificate: DonationCertificateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCertificates(certificates: List<DonationCertificateEntity>)
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 100")
    fun getAllAuditLogsFlow(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)
}
