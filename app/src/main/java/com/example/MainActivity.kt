package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.components.BloodConnectTopBar
import com.example.ui.components.CreateEmergencyDialog
import com.example.ui.components.DigitalCertificateDialog
import com.example.ui.components.IncomingEmergencyNotificationDialog
import com.example.ui.components.NotificationsDialog
import com.example.ui.components.RequestDetailDialog
import com.example.ui.components.RoleSwitcherDialog
import com.example.ui.screens.admin.AdminScreen
import com.example.ui.screens.camps.CampsScreen
import com.example.ui.screens.emergency.EmergencyScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.map.MapScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.theme.BloodConnectTheme
import com.example.ui.theme.BloodPrimary
import com.example.ui.theme.BloodPrimaryContainer
import com.example.ui.theme.BloodPrimaryDark
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.BloodConnectViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: BloodConnectViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BloodConnectTheme {
                BloodConnectApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun BloodConnectApp(viewModel: BloodConnectViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val emergencyRequests by viewModel.emergencyRequests.collectAsState()
    val camps by viewModel.camps.collectAsState()
    val hospitals by viewModel.hospitals.collectAsState()
    val bloodBanks by viewModel.bloodBanks.collectAsState()
    val volunteers by viewModel.volunteers.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val certificates by viewModel.certificates.collectAsState()
    val userRegistrations by viewModel.userRegistrations.collectAsState()

    // Modals
    val showCreateEmergencyDialog by viewModel.showCreateEmergencyDialog.collectAsState()
    val selectedRequest by viewModel.selectedRequest.collectAsState()
    val selectedCertificate by viewModel.selectedCertificate.collectAsState()
    val showRoleSwitcher by viewModel.showRoleSwitcher.collectAsState()
    val showNotificationsSheet by viewModel.showNotificationsSheet.collectAsState()
    val incomingAlert by viewModel.incomingEmergencyAlert.collectAsState()
    val feedbackMsg by viewModel.userFeedbackMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(feedbackMsg) {
        feedbackMsg?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedbackMessage()
        }
    }

    val currentRole = currentUser?.role ?: UserRole.DONOR

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            BloodConnectTopBar(
                currentRole = currentRole,
                unreadNotificationCount = notifications.size,
                onRoleClick = { viewModel.toggleRoleSwitcher(true) },
                onNotificationClick = { viewModel.toggleNotificationsSheet(true) }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 6.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                // 1. Home
                NavigationBarItem(
                    selected = currentTab == AppNavTab.HOME,
                    onClick = { viewModel.selectTab(AppNavTab.HOME) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 11.sp, fontWeight = if (currentTab == AppNavTab.HOME) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BloodPrimary,
                        selectedTextColor = BloodPrimaryDark,
                        indicatorColor = BloodPrimaryContainer
                    ),
                    modifier = Modifier.testTag("nav_item_home")
                )

                // 2. Camps
                NavigationBarItem(
                    selected = currentTab == AppNavTab.CAMPS,
                    onClick = { viewModel.selectTab(AppNavTab.CAMPS) },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Camps") },
                    label = { Text("Camps", fontSize = 11.sp, fontWeight = if (currentTab == AppNavTab.CAMPS) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BloodPrimary,
                        selectedTextColor = BloodPrimaryDark,
                        indicatorColor = BloodPrimaryContainer
                    ),
                    modifier = Modifier.testTag("nav_item_camps")
                )

                // 3. Map
                NavigationBarItem(
                    selected = currentTab == AppNavTab.MAP,
                    onClick = { viewModel.selectTab(AppNavTab.MAP) },
                    icon = { Icon(Icons.Default.Place, contentDescription = "Map") },
                    label = { Text("Map", fontSize = 11.sp, fontWeight = if (currentTab == AppNavTab.MAP) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BloodPrimary,
                        selectedTextColor = BloodPrimaryDark,
                        indicatorColor = BloodPrimaryContainer
                    ),
                    modifier = Modifier.testTag("nav_item_map")
                )

                // 4. Emergency
                NavigationBarItem(
                    selected = currentTab == AppNavTab.EMERGENCY,
                    onClick = { viewModel.selectTab(AppNavTab.EMERGENCY) },
                    icon = {
                        val activeEmergenciesCount = emergencyRequests.count { it.status.name.startsWith("FINDING") || it.status.name.startsWith("VERIF") }
                        BadgedBox(
                            badge = {
                                if (activeEmergenciesCount > 0) {
                                    Badge(containerColor = BloodPrimary) { Text("$activeEmergenciesCount") }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = "Emergency", tint = if (currentTab == AppNavTab.EMERGENCY) BloodPrimary else Color(0xFFD32F2F))
                        }
                    },
                    label = { Text("Emergency", fontSize = 11.sp, fontWeight = if (currentTab == AppNavTab.EMERGENCY) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BloodPrimary,
                        selectedTextColor = BloodPrimaryDark,
                        indicatorColor = BloodPrimaryContainer
                    ),
                    modifier = Modifier.testTag("nav_item_emergency")
                )

                // 5. Admin (Accessible easily or highlighted if Admin role)
                NavigationBarItem(
                    selected = currentTab == AppNavTab.ADMIN,
                    onClick = { viewModel.selectTab(AppNavTab.ADMIN) },
                    icon = { Icon(Icons.Default.Security, contentDescription = "Admin") },
                    label = { Text("Admin", fontSize = 11.sp, fontWeight = if (currentTab == AppNavTab.ADMIN) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BloodPrimary,
                        selectedTextColor = BloodPrimaryDark,
                        indicatorColor = BloodPrimaryContainer
                    ),
                    modifier = Modifier.testTag("nav_item_admin")
                )

                // 6. Profile
                NavigationBarItem(
                    selected = currentTab == AppNavTab.PROFILE,
                    onClick = { viewModel.selectTab(AppNavTab.PROFILE) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile", fontSize = 11.sp, fontWeight = if (currentTab == AppNavTab.PROFILE) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BloodPrimary,
                        selectedTextColor = BloodPrimaryDark,
                        indicatorColor = BloodPrimaryContainer
                    ),
                    modifier = Modifier.testTag("nav_item_profile")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppNavTab.HOME -> {
                    HomeScreen(
                        currentUser = currentUser,
                        activeEmergencies = emergencyRequests.filter { it.status.name != "FULFILLED" && it.status.name != "CANCELLED" },
                        camps = camps,
                        hospitals = hospitals,
                        bloodBanks = bloodBanks,
                        certificates = certificates,
                        onRequestBloodClick = { viewModel.openCreateEmergencyDialog() },
                        onFindCampsClick = { viewModel.selectTab(AppNavTab.CAMPS) },
                        onEmergencyClick = { req -> viewModel.selectRequest(req) },
                        onCampClick = { camp -> viewModel.selectCamp(camp) },
                        onCertificateClick = { cert -> viewModel.selectCertificate(cert) },
                        onAvailabilityToggle = { isAvail -> viewModel.updateAvailability(isAvail) }
                    )
                }
                AppNavTab.CAMPS -> {
                    CampsScreen(
                        camps = camps,
                        userRegistrations = userRegistrations,
                        onRegisterCamp = { camp -> viewModel.registerForCamp(camp) },
                        onCancelRegistration = { campId -> viewModel.cancelCampRegistration(campId) }
                    )
                }
                AppNavTab.MAP -> {
                    MapScreen(
                        camps = camps,
                        hospitals = hospitals,
                        bloodBanks = bloodBanks,
                        onCampClick = { camp ->
                            viewModel.selectTab(AppNavTab.CAMPS)
                            viewModel.selectCamp(camp)
                        }
                    )
                }
                AppNavTab.EMERGENCY -> {
                    EmergencyScreen(
                        requests = emergencyRequests,
                        onRequestBloodClick = { viewModel.openCreateEmergencyDialog() },
                        onRequestSelected = { req -> viewModel.selectRequest(req) }
                    )
                }
                AppNavTab.ADMIN -> {
                    AdminScreen(
                        requests = emergencyRequests,
                        donors = allUsers,
                        camps = camps,
                        volunteers = volunteers,
                        auditLogs = auditLogs,
                        onSelectRequest = { req -> viewModel.selectRequest(req) },
                        onVerifyRequest = { reqId, approved -> viewModel.verifyRequest(reqId, approved) },
                        onExpandRadius = { reqId -> viewModel.expandRadius(reqId) }
                    )
                }
                AppNavTab.PROFILE -> {
                    ProfileScreen(
                        currentUser = currentUser,
                        certificates = certificates,
                        onToggleAvailability = { isAvail -> viewModel.updateAvailability(isAvail) },
                        onCertificateClick = { cert -> viewModel.selectCertificate(cert) },
                        onRoleSwitchClick = { viewModel.toggleRoleSwitcher(true) }
                    )
                }
            }
        }
    }

    // Modal Dialogs
    if (showCreateEmergencyDialog) {
        CreateEmergencyDialog(
            hospitals = hospitals,
            onDismiss = { viewModel.closeCreateEmergencyDialog() },
            onSubmit = { pName, bGroup, units, hName, hAddress, urg, time, phone, notes ->
                viewModel.createEmergencyRequest(pName, bGroup, units, hName, hAddress, urg, time, phone, notes)
            }
        )
    }

    selectedRequest?.let { req ->
        RequestDetailDialog(
            request = req,
            currentRole = currentRole,
            timelineFlow = viewModel.getRequestTimeline(req.id),
            matchesFlow = viewModel.getRequestMatches(req.id),
            onVerify = { approved -> viewModel.verifyRequest(req.id, approved) },
            onExpandRadius = { viewModel.expandRadius(req.id) },
            onProgressStatus = { nextStatus -> viewModel.progressRequestStatus(req.id, nextStatus) },
            onDonorRespond = { resp -> viewModel.respondToEmergency(req.id, resp) },
            onDismiss = { viewModel.selectRequest(null) }
        )
    }

    selectedCertificate?.let { cert ->
        DigitalCertificateDialog(
            certificate = cert,
            onDismiss = { viewModel.selectCertificate(null) }
        )
    }

    incomingAlert?.let { alertReq ->
        IncomingEmergencyNotificationDialog(
            request = alertReq,
            onAccept = {
                viewModel.respondToEmergency(alertReq.id, com.example.data.model.DonorResponseStatus.ACCEPTED)
            },
            onMaybe = {
                viewModel.respondToEmergency(alertReq.id, com.example.data.model.DonorResponseStatus.MAYBE)
            },
            onDecline = {
                viewModel.respondToEmergency(alertReq.id, com.example.data.model.DonorResponseStatus.NOT_AVAILABLE)
            },
            onDismiss = { viewModel.dismissEmergencyAlert() }
        )
    }

    if (showRoleSwitcher) {
        RoleSwitcherDialog(
            currentRole = currentRole,
            onRoleSelected = { newRole -> viewModel.switchRole(newRole) },
            onDismiss = { viewModel.toggleRoleSwitcher(false) }
        )
    }

    if (showNotificationsSheet) {
        NotificationsDialog(
            notifications = notifications,
            onNotificationClick = { notif ->
                viewModel.toggleNotificationsSheet(false)
                if (notif.relatedId != null) {
                    val req = emergencyRequests.find { it.id == notif.relatedId }
                    if (req != null) viewModel.selectRequest(req)
                }
            },
            onDismiss = { viewModel.toggleNotificationsSheet(false) }
        )
    }
}
