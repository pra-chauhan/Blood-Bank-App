package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
import com.example.data.model.DonorResponseStatus
import com.example.data.model.RequestStatus
import com.example.data.model.RequestUrgency
import com.example.data.model.UserRole
import com.example.data.repository.BloodConnectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppNavTab {
    HOME,
    CAMPS,
    MAP,
    EMERGENCY,
    ADMIN,
    PROFILE
}

class BloodConnectViewModel(application: Application) : AndroidViewModel(application) {

    private val database = BloodConnectDatabase.getDatabase(application, viewModelScope)
    val repository = BloodConnectRepository(database)

    // Current navigation tab
    private val _currentTab = MutableStateFlow(AppNavTab.HOME)
    val currentTab: StateFlow<AppNavTab> = _currentTab.asStateFlow()

    // Current user and active role
    val currentUser: StateFlow<UserEntity?> = repository.currentUserFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    // All Emergency requests
    val emergencyRequests: StateFlow<List<EmergencyRequestEntity>> = repository.allEmergencyRequestsFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val pendingRequests: StateFlow<List<EmergencyRequestEntity>> = repository.pendingVerificationRequestsFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Camps, Facilities, Volunteers
    val camps: StateFlow<List<DonationCampEntity>> = repository.allCampsFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val hospitals: StateFlow<List<HospitalEntity>> = repository.allHospitalsFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val bloodBanks: StateFlow<List<BloodBankEntity>> = repository.allBloodBanksFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val volunteers: StateFlow<List<VolunteerEntity>> = repository.allVolunteersFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allUsers: StateFlow<List<UserEntity>> = repository.allUsersFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val auditLogs: StateFlow<List<AuditLogEntity>> = repository.auditLogsFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Notifications for current user
    val notifications: StateFlow<List<AppNotificationEntity>> = repository.getNotificationsForUserFlow(DatabaseSeeder.CURRENT_USER_ID).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Certificates for current user
    val certificates: StateFlow<List<DonationCertificateEntity>> = repository.getCertificatesForUserFlow(DatabaseSeeder.CURRENT_USER_ID).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // User's Camp Registrations
    val userRegistrations: StateFlow<List<CampRegistrationEntity>> = repository.getUserCampRegistrationsFlow(DatabaseSeeder.CURRENT_USER_ID).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // UI Dialog & Selection States
    private val _showCreateEmergencyDialog = MutableStateFlow(false)
    val showCreateEmergencyDialog: StateFlow<Boolean> = _showCreateEmergencyDialog.asStateFlow()

    private val _selectedRequest = MutableStateFlow<EmergencyRequestEntity?>(null)
    val selectedRequest: StateFlow<EmergencyRequestEntity?> = _selectedRequest.asStateFlow()

    private val _selectedCamp = MutableStateFlow<DonationCampEntity?>(null)
    val selectedCamp: StateFlow<DonationCampEntity?> = _selectedCamp.asStateFlow()

    private val _selectedCertificate = MutableStateFlow<DonationCertificateEntity?>(null)
    val selectedCertificate: StateFlow<DonationCertificateEntity?> = _selectedCertificate.asStateFlow()

    private val _showRoleSwitcher = MutableStateFlow(false)
    val showRoleSwitcher: StateFlow<Boolean> = _showRoleSwitcher.asStateFlow()

    private val _showNotificationsSheet = MutableStateFlow(false)
    val showNotificationsSheet: StateFlow<Boolean> = _showNotificationsSheet.asStateFlow()

    private val _incomingEmergencyAlert = MutableStateFlow<EmergencyRequestEntity?>(null)
    val incomingEmergencyAlert: StateFlow<EmergencyRequestEntity?> = _incomingEmergencyAlert.asStateFlow()

    private val _userFeedbackMessage = MutableStateFlow<String?>(null)
    val userFeedbackMessage: StateFlow<String?> = _userFeedbackMessage.asStateFlow()

    // Map filters
    private val _mapRadiusFilterKm = MutableStateFlow(25.0)
    val mapRadiusFilterKm: StateFlow<Double> = _mapRadiusFilterKm.asStateFlow()

    private val _mapTypeFilter = MutableStateFlow("ALL") // ALL, CAMPS, HOSPITALS, BLOOD_BANKS
    val mapTypeFilter: StateFlow<String> = _mapTypeFilter.asStateFlow()

    init {
        // Trigger initial check for active emergency requests
        viewModelScope.launch {
            repository.activeEmergencyRequestsFlow.collect { activeList ->
                val user = repository.currentUserFlow.first() ?: return@collect
                if (user.isDonorAvailable && activeList.isNotEmpty()) {
                    val matchingRequest = activeList.firstOrNull { req ->
                        req.status == RequestStatus.FINDING_DONORS &&
                        req.bloodGroup.equals(user.bloodGroup, ignoreCase = true)
                    }
                    if (matchingRequest != null && _incomingEmergencyAlert.value == null) {
                        _incomingEmergencyAlert.value = matchingRequest
                    }
                }
            }
        }
    }

    fun selectTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    fun openCreateEmergencyDialog() {
        _showCreateEmergencyDialog.value = true
    }

    fun closeCreateEmergencyDialog() {
        _showCreateEmergencyDialog.value = false
    }

    fun selectRequest(request: EmergencyRequestEntity?) {
        _selectedRequest.value = request
    }

    fun selectCamp(camp: DonationCampEntity?) {
        _selectedCamp.value = camp
    }

    fun selectCertificate(cert: DonationCertificateEntity?) {
        _selectedCertificate.value = cert
    }

    fun toggleRoleSwitcher(show: Boolean) {
        _showRoleSwitcher.value = show
    }

    fun toggleNotificationsSheet(show: Boolean) {
        _showNotificationsSheet.value = show
    }

    fun dismissEmergencyAlert() {
        _incomingEmergencyAlert.value = null
    }

    fun clearFeedbackMessage() {
        _userFeedbackMessage.value = null
    }

    fun setMapRadiusFilter(radius: Double) {
        _mapRadiusFilterKm.value = radius
    }

    fun setMapTypeFilter(type: String) {
        _mapTypeFilter.value = type
    }

    fun updateAvailability(isAvailable: Boolean) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            repository.updateDonorAvailability(user.id, isAvailable)
            _userFeedbackMessage.value = if (isAvailable) "You are now AVAILABLE to donate blood." else "Status updated to UNAVAILABLE."
        }
    }

    fun switchRole(newRole: UserRole) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            repository.switchUserRole(user.id, newRole)
            _showRoleSwitcher.value = false
            _userFeedbackMessage.value = "Switched persona to ${newRole.name}."
            if (newRole == UserRole.NGO_ADMIN || newRole == UserRole.NGO_STAFF) {
                _currentTab.value = AppNavTab.ADMIN
            }
        }
    }

    fun createEmergencyRequest(
        patientName: String,
        bloodGroup: String,
        units: Int,
        hospitalName: String,
        hospitalAddress: String,
        urgency: RequestUrgency,
        requiredTime: String,
        attendantPhone: String,
        notes: String
    ) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val reqId = repository.submitEmergencyRequest(
                patientName = patientName,
                requesterUserId = user.id,
                requesterPhone = attendantPhone.ifEmpty { user.phone },
                bloodGroup = bloodGroup,
                unitsRequired = units,
                hospitalName = hospitalName,
                hospitalAddress = hospitalAddress,
                latitude = user.latitude,
                longitude = user.longitude,
                urgency = urgency,
                requiredDate = "Today",
                requiredTime = requiredTime,
                attendantPhone = attendantPhone.ifEmpty { user.phone },
                additionalNotes = notes
            )
            _showCreateEmergencyDialog.value = false
            _userFeedbackMessage.value = "Emergency request submitted! Status: Verification Pending by NGO."
            // View newly created request
            val created = repository.allEmergencyRequestsFlow.first().find { it.id == reqId }
            _selectedRequest.value = created
        }
    }

    fun verifyRequest(requestId: String, approved: Boolean, reason: String = "") {
        viewModelScope.launch {
            val admin = currentUser.value
            repository.verifyEmergencyRequest(
                requestId = requestId,
                adminUserId = admin?.id ?: DatabaseSeeder.ADMIN_USER_ID,
                adminName = admin?.fullName ?: "Dr. Arvind Swaminathan (Admin)",
                approved = approved,
                rejectionReason = reason
            )
            _userFeedbackMessage.value = if (approved) "Request VERIFIED! Matching Engine dispatched notifications." else "Request marked REJECTED."
            // Refresh selection
            _selectedRequest.value = repository.allEmergencyRequestsFlow.first().find { it.id == requestId }
        }
    }

    fun expandRadius(requestId: String) {
        viewModelScope.launch {
            val admin = currentUser.value
            repository.expandNotificationRadius(
                requestId = requestId,
                adminName = admin?.fullName ?: "NGO Administrator"
            )
            _userFeedbackMessage.value = "Search radius expanded. Additional nearby donors notified."
            _selectedRequest.value = repository.allEmergencyRequestsFlow.first().find { it.id == requestId }
        }
    }

    fun respondToEmergency(requestId: String, response: DonorResponseStatus) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            repository.recordDonorResponse(requestId, user.id, response)
            _incomingEmergencyAlert.value = null
            _userFeedbackMessage.value = when (response) {
                DonorResponseStatus.ACCEPTED -> "Thank you! NGO notified. Hospital coordinator will reach out."
                DonorResponseStatus.MAYBE -> "Response noted. We appreciate your willingness."
                DonorResponseStatus.NOT_AVAILABLE -> "Status updated to unavailable for this request."
                else -> ""
            }
        }
    }

    fun progressRequestStatus(requestId: String, nextStatus: RequestStatus, notes: String = "") {
        viewModelScope.launch {
            val user = currentUser.value
            val actor = user?.fullName ?: "BloodConnect Coordinator"
            repository.updateRequestStatus(requestId, nextStatus, actor, notes)
            _userFeedbackMessage.value = "Status progressed to: ${nextStatus.display}."
            _selectedRequest.value = repository.allEmergencyRequestsFlow.first().find { it.id == requestId }
        }
    }

    fun registerForCamp(camp: DonationCampEntity) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            repository.registerForCamp(camp.id, user)
            _selectedCamp.value = null
            _userFeedbackMessage.value = "Registered for ${camp.name}! Confirmation saved."
        }
    }

    fun cancelCampRegistration(campId: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            repository.cancelCampRegistration(campId, user.id)
            _userFeedbackMessage.value = "Camp registration cancelled."
        }
    }

    fun getRequestTimeline(requestId: String): Flow<List<EmergencyStatusHistoryEntity>> {
        return repository.getStatusHistoryFlow(requestId)
    }

    fun getRequestMatches(requestId: String): Flow<List<DonorMatchEntity>> {
        return repository.getMatchesForRequestFlow(requestId)
    }
}
