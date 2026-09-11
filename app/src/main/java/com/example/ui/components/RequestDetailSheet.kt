package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.DonorMatchEntity
import com.example.data.local.entity.EmergencyRequestEntity
import com.example.data.local.entity.EmergencyStatusHistoryEntity
import com.example.data.model.DonorResponseStatus
import com.example.data.model.RequestStatus
import com.example.data.model.UserRole
import com.example.ui.theme.BloodPrimary
import com.example.ui.theme.BloodPrimaryContainer
import com.example.ui.theme.BloodPrimaryDark
import com.example.ui.theme.BloodSuccess
import com.example.ui.theme.BloodWarning
import kotlinx.coroutines.flow.Flow

@Composable
fun RequestDetailDialog(
    request: EmergencyRequestEntity,
    currentRole: UserRole,
    timelineFlow: Flow<List<EmergencyStatusHistoryEntity>>,
    matchesFlow: Flow<List<DonorMatchEntity>>,
    onVerify: (Boolean) -> Unit,
    onExpandRadius: () -> Unit,
    onProgressStatus: (RequestStatus) -> Unit,
    onDonorRespond: (DonorResponseStatus) -> Unit,
    onDismiss: () -> Unit
) {
    val timeline by timelineFlow.collectAsState(initial = emptyList())
    val matches by matchesFlow.collectAsState(initial = emptyList())

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("request_detail_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Top bar with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BloodGroupBadge(bloodGroup = request.bloodGroup, size = 44, textSize = 16)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Request #${request.id.takeLast(6).uppercase()}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                fontFamily = FontFamily.Monospace,
                                color = BloodPrimaryDark
                            )
                            EmergencyUrgencyBadge(urgency = request.urgency)
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Verification Banner
                if (request.verificationStatus == "PENDING") {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFFF8E1),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF57F17), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Verification Pending", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFF57F17))
                                Text("NGO coordinators are confirming requirement with hospital.", fontSize = 11.sp, color = Color(0xFF616161))
                            }
                        }
                    }
                } else if (request.verificationStatus == "APPROVED") {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFE8F5E9),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = BloodSuccess, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Verified by BloodConnect NGO", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BloodSuccess)
                                Text("Hospital admission and blood requirement validated.", fontSize = 11.sp, color = Color(0xFF2E7D32))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Patient and Hospital Details
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFAFAFA),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Patient: ${request.patientName}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF212121)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Requirement: ${request.unitsRequired} Units of ${request.bloodGroup} blood",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = BloodPrimary
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = request.hospitalName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Text(
                            text = request.hospitalAddress,
                            fontSize = 11.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 22.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Attendant: ${request.attendantPhone}", fontSize = 12.sp, color = Color(0xFF424242))
                        }

                        if (request.additionalNotes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Notes: ${request.additionalNotes}",
                                fontSize = 11.sp,
                                color = Color(0xFF616161)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Current Status:", fontSize = 12.sp, color = Color.Gray)
                            RequestStatusBadge(status = request.status)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Timeline component
                EmergencyTimelineView(history = timeline, currentStatus = request.status)

                Spacer(modifier = Modifier.height(14.dp))

                // Role-Specific Action Buttons

                // 1. If user is NGO_ADMIN / NGO_STAFF:
                if (currentRole == UserRole.NGO_ADMIN || currentRole == UserRole.NGO_STAFF || currentRole == UserRole.SUPER_ADMIN) {
                    Text(
                        text = "NGO Admin Controls",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = BloodPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    if (request.verificationStatus == "PENDING") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onVerify(true) },
                                colors = ButtonDefaults.buttonColors(containerColor = BloodSuccess),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).testTag("admin_btn_verify")
                            ) {
                                Text("VERIFY & MATCH")
                            }
                            OutlinedButton(
                                onClick = { onVerify(false) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).testTag("admin_btn_reject")
                            ) {
                                Text("REJECT", color = BloodPrimary)
                            }
                        }
                    } else {
                        // Radius expansion
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onExpandRadius,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).testTag("admin_btn_expand_radius")
                            ) {
                                Text("EXPAND RADIUS (${request.currentRadiusKm.toInt()}km)", fontSize = 11.sp)
                            }

                            if (request.status != RequestStatus.FULFILLED && request.status != RequestStatus.DONATION_COMPLETED) {
                                Button(
                                    onClick = {
                                        val next = when (request.status) {
                                            RequestStatus.DONOR_CONFIRMED -> RequestStatus.DONOR_ARRIVED
                                            RequestStatus.DONOR_ARRIVED -> RequestStatus.DONATION_COMPLETED
                                            RequestStatus.DONATION_COMPLETED -> RequestStatus.FULFILLED
                                            else -> RequestStatus.DONOR_CONFIRMED
                                        }
                                        onProgressStatus(next)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BloodPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f).testTag("admin_btn_progress_status")
                                ) {
                                    Text(
                                        when (request.status) {
                                            RequestStatus.DONOR_CONFIRMED -> "MARK ARRIVED"
                                            RequestStatus.DONOR_ARRIVED -> "COMPLETE DONATION"
                                            RequestStatus.DONATION_COMPLETED -> "MARK FULFILLED"
                                            else -> "PROGRESS STATUS"
                                        },
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    // Matched Donors Section
                    if (matches.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Matched Donors (${matches.size}) - Search Radius: ${request.currentRadiusKm} km",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF212121)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        matches.forEach { match ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFAFAFA),
                                border = CardDefaults.outlinedCardBorder(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        BloodGroupBadge(bloodGroup = match.bloodGroup, size = 32, textSize = 11)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(match.donorName, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                            Text(
                                                "~${match.distanceKm} km away • Score: ${match.matchScore}%",
                                                fontSize = 10.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = when (match.responseStatus) {
                                            DonorResponseStatus.ACCEPTED -> BloodSuccess.copy(alpha = 0.15f)
                                            DonorResponseStatus.NOT_AVAILABLE -> Color(0xFFFFEBEE)
                                            else -> Color(0xFFEEEEEE)
                                        }
                                    ) {
                                        Text(
                                            text = match.responseStatus.name,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (match.responseStatus) {
                                                DonorResponseStatus.ACCEPTED -> BloodSuccess
                                                DonorResponseStatus.NOT_AVAILABLE -> BloodPrimary
                                                else -> Color.DarkGray
                                            },
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. If user is Donor / Normal User:
                if (currentRole == UserRole.DONOR || currentRole == UserRole.USER) {
                    if (request.status == RequestStatus.FINDING_DONORS || request.status == RequestStatus.VERIFIED) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Can you donate for this emergency?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = BloodPrimaryDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { onDonorRespond(DonorResponseStatus.ACCEPTED) },
                            colors = ButtonDefaults.buttonColors(containerColor = BloodPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("btn_donor_accept")
                        ) {
                            Text("I CAN DONATE 🩸", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onDonorRespond(DonorResponseStatus.MAYBE) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("MAYBE", fontSize = 11.sp, color = Color.DarkGray)
                            }
                            OutlinedButton(
                                onClick = { onDonorRespond(DonorResponseStatus.NOT_AVAILABLE) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("NOT AVAILABLE", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}
