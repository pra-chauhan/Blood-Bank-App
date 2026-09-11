package com.example.ui.screens.emergency

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.EmergencyRequestEntity
import com.example.data.model.RequestStatus
import com.example.data.model.RequestUrgency
import com.example.ui.components.BloodGroupBadge
import com.example.ui.components.EmergencyUrgencyBadge
import com.example.ui.components.RequestStatusBadge
import com.example.ui.theme.BloodPrimary
import com.example.ui.theme.BloodPrimaryContainer
import com.example.ui.theme.BloodPrimaryDark
import com.example.ui.theme.BloodSuccess

@Composable
fun EmergencyScreen(
    requests: List<EmergencyRequestEntity>,
    onRequestBloodClick: () -> Unit,
    onRequestSelected: (EmergencyRequestEntity) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("ALL") }
    var selectedGroupFilter by remember { mutableStateOf<String?>(null) }

    val bloodGroups = listOf("O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-")

    val filteredRequests = remember(requests, selectedFilter, selectedGroupFilter) {
        requests.filter { req ->
            val matchStatus = when (selectedFilter) {
                "ACTIVE" -> req.status == RequestStatus.FINDING_DONORS || req.status == RequestStatus.VERIFIED || req.status == RequestStatus.DONOR_CONFIRMED
                "PENDING" -> req.status == RequestStatus.VERIFICATION_PENDING || req.verificationStatus == "PENDING"
                "FULFILLED" -> req.status == RequestStatus.FULFILLED || req.status == RequestStatus.DONATION_COMPLETED
                else -> true
            }
            val matchGroup = selectedGroupFilter == null || req.bloodGroup.equals(selectedGroupFilter, ignoreCase = true)
            matchStatus && matchGroup
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("emergency_screen")
    ) {
        // Header Banner
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BloodPrimary),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Emergency Blood Hub",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Fast-track hospital verification and smart radius donor matching",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            lineHeight = 16.sp
                        )
                    }
                    Text("🚨", fontSize = 36.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onRequestBloodClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = BloodPrimaryDark),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("btn_emergency_create_request")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("POST EMERGENCY REQUEST", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Status Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("ALL" to "All", "ACTIVE" to "Active", "PENDING" to "Verification Pending", "FULFILLED" to "Fulfilled").forEach { (key, label) ->
                val isSel = selectedFilter == key
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSel) BloodPrimary else MaterialTheme.colorScheme.surface,
                    border = if (isSel) null else CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.clickable { selectedFilter = key }
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Blood Group Quick Filter Row
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (selectedGroupFilter == null) Color.DarkGray else Color(0xFFEEEEEE),
                    modifier = Modifier.clickable { selectedGroupFilter = null }
                ) {
                    Text(
                        text = "Any Group",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedGroupFilter == null) Color.White else Color.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            items(bloodGroups) { bg ->
                val isSel = selectedGroupFilter == bg
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSel) BloodPrimary else Color(0xFFEEEEEE),
                    modifier = Modifier.clickable {
                        selectedGroupFilter = if (isSel) null else bg
                    }
                ) {
                    Text(
                        text = bg,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSel) Color.White else Color.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Requests List
        if (filteredRequests.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No emergency requests match this filter.",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredRequests) { req ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onRequestSelected(req) }
                            .testTag("emergency_item_${req.id}")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    BloodGroupBadge(bloodGroup = req.bloodGroup, size = 42, textSize = 15)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = req.patientName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "${req.unitsRequired} Units Required",
                                            fontSize = 12.sp,
                                            color = BloodPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                                EmergencyUrgencyBadge(urgency = req.urgency)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${req.hospitalName} • ${req.hospitalAddress}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Needed: ${req.requiredTime}",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }

                                RequestStatusBadge(status = req.status)
                            }
                        }
                    }
                }
            }
        }
    }
}
