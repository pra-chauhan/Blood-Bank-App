package com.example.ui.screens.camps

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.CampRegistrationEntity
import com.example.data.local.entity.DonationCampEntity
import com.example.ui.theme.BloodPrimary
import com.example.ui.theme.BloodPrimaryContainer
import com.example.ui.theme.BloodPrimaryDark
import com.example.ui.theme.BloodSuccess

@Composable
fun CampsScreen(
    camps: List<DonationCampEntity>,
    userRegistrations: List<CampRegistrationEntity>,
    onRegisterCamp: (DonationCampEntity) -> Unit,
    onCancelRegistration: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }
    var viewingCamp by remember { mutableStateOf<DonationCampEntity?>(null) }

    val registeredCampIds = remember(userRegistrations) {
        userRegistrations.filter { it.status == "CONFIRMED" }.map { it.campId }.toSet()
    }

    val filteredCamps = remember(camps, searchQuery, selectedFilter) {
        camps.filter { camp ->
            (searchQuery.isBlank() || camp.name.contains(searchQuery, ignoreCase = true) || camp.city.contains(searchQuery, ignoreCase = true) || camp.address.contains(searchQuery, ignoreCase = true)) &&
            (selectedFilter == "ALL" || (selectedFilter == "ACTIVE" && camp.status.name == "ACTIVE") || (selectedFilter == "UPCOMING" && camp.status.name == "UPCOMING"))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("camps_screen")
    ) {
        // Header
        Text(
            text = "Blood Donation Camps",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Discover and register for nearby voluntary donation drives",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search camps by name, area, or city...", fontSize = 13.sp) },
            modifier = Modifier.fillMaxWidth().testTag("camps_search_input"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("ALL" to "All Drives", "ACTIVE" to "Active Today", "UPCOMING" to "Upcoming").forEach { (key, label) ->
                val isSelected = selectedFilter == key
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) BloodPrimary else MaterialTheme.colorScheme.surface,
                    border = if (isSelected) null else CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.clickable { selectedFilter = key }
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Camps List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredCamps) { camp ->
                val isRegistered = registeredCampIds.contains(camp.id)
                val progress = (camp.registrationCount.toFloat() / camp.capacity.toFloat()).coerceIn(0f, 1f)

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewingCamp = camp }
                        .testTag("camp_card_${camp.id}")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = camp.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Organized by ${camp.organizer}",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (camp.status.name == "ACTIVE") Color(0xFFE8F5E9) else Color(0xFFE3F2FD)
                            ) {
                                Text(
                                    text = camp.status.name,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (camp.status.name == "ACTIVE") BloodSuccess else Color(0xFF1565C0),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DateRange, contentDescription = null, tint = BloodPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${camp.date} • ${camp.startTime} to ${camp.endTime}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BloodPrimaryDark
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = camp.address,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Capacity meter
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Slots: ${camp.registrationCount}/${camp.capacity} Registered",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "${camp.capacity - camp.registrationCount} slots left",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (camp.capacity - camp.registrationCount < 30) BloodPrimary else BloodSuccess
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = BloodPrimary,
                            trackColor = Color(0xFFEEEEEE)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = camp.bloodGroupsNeeded,
                                fontSize = 11.sp,
                                color = Color(0xFF616161),
                                modifier = Modifier.weight(1f)
                            )

                            if (isRegistered) {
                                Button(
                                    onClick = { onCancelRegistration(camp.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8F5E9), contentColor = BloodSuccess),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("btn_registered_${camp.id}")
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("REGISTERED", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = { onRegisterCamp(camp) },
                                    colors = ButtonDefaults.buttonColors(containerColor = BloodPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("btn_register_${camp.id}")
                                ) {
                                    Text("REGISTER", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Camp Details Modal
    viewingCamp?.let { camp ->
        Dialog(onDismissRequest = { viewingCamp = null }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().padding(12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(camp.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BloodPrimaryDark)
                    Text("Organized by ${camp.organizer}", fontSize = 12.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(camp.description, fontSize = 12.sp, lineHeight = 17.sp, color = Color(0xFF424242))

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("📍 Address: ${camp.address}", fontSize = 12.sp)
                    Text("⏰ Time: ${camp.date}, ${camp.startTime} - ${camp.endTime}", fontSize = 12.sp)
                    Text("📞 Contact: ${camp.contactPhone}", fontSize = 12.sp)

                    if (camp.instructions.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFAFAFA),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Text(
                                text = "Instructions: ${camp.instructions}",
                                fontSize = 11.sp,
                                color = Color(0xFF616161),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val isReg = registeredCampIds.contains(camp.id)
                    Button(
                        onClick = {
                            if (isReg) onCancelRegistration(camp.id) else onRegisterCamp(camp)
                            viewingCamp = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isReg) Color(0xFFD32F2F) else BloodPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isReg) "Cancel Registration" else "Register For Camp 🩸")
                    }
                }
            }
        }
    }
}
