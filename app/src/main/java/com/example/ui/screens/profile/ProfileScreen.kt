package com.example.ui.screens.profile

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.DonationCertificateEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.UserRole
import com.example.ui.components.BloodGroupBadge
import com.example.ui.theme.BloodPrimary
import com.example.ui.theme.BloodPrimaryContainer
import com.example.ui.theme.BloodPrimaryDark
import com.example.ui.theme.BloodSuccess

@Composable
fun ProfileScreen(
    currentUser: UserEntity?,
    certificates: List<DonationCertificateEntity>,
    onToggleAvailability: (Boolean) -> Unit,
    onCertificateClick: (DonationCertificateEntity) -> Unit,
    onRoleSwitchClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("profile_screen")
    ) {
        // User Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BloodGroupBadge(bloodGroup = currentUser?.bloodGroup ?: "O+", size = 56, textSize = 22)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentUser?.fullName ?: "Life Saver",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = currentUser?.phone ?: "+91 98765 43210",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${currentUser?.savedAddress ?: "New Delhi"} • ${currentUser?.city ?: "Delhi"}",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BloodPrimaryContainer
                    ) {
                        Text(
                            text = currentUser?.role?.name ?: "DONOR",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BloodPrimaryDark,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Availability toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Donor Availability Status",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (currentUser?.isDonorAvailable == true) "You can receive emergency alerts nearby" else "Paused notifications",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    Switch(
                        checked = currentUser?.isDonorAvailable ?: true,
                        onCheckedChange = onToggleAvailability,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = BloodSuccess
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Role Switcher Card (MANDATORY for testing all personas smoothly)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder(),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onRoleSwitchClick() }
                .testTag("btn_profile_switch_role")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(BloodPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = BloodPrimaryDark, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Switch Platform Persona", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Test Donor, NGO Admin, Volunteer, Hospital", fontSize = 11.sp, color = Color.Gray)
                    }
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Certificates of LifeSaver Honor
        Text(
            text = "Your LifeSaver Certificates",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (certificates.isEmpty()) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("No certificates yet. Complete a verified donation to receive your official QR-verified Certificate of Honor!", fontSize = 12.sp, color = Color.Gray)
                }
            }
        } else {
            certificates.forEach { cert ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onCertificateClick(cert) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE8F5E9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = BloodSuccess, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(cert.certificateNumber, fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                                Text("${cert.donationDate} • ${cert.hospitalOrCamp}", fontSize = 11.sp, color = Color.Gray)
                                Text("${cert.units} Unit of ${cert.bloodGroup}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = BloodPrimaryDark)
                            }
                        }

                        Icon(Icons.Default.QrCode, contentDescription = "View QR", tint = BloodPrimary, modifier = Modifier.size(24.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Medical Safety & Privacy Notice
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = BloodPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Privacy & Donor Protection Policy", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "• BloodConnect never shares your exact GPS or home address with requesters. Only approximate distance (~km) is displayed.\n• Direct donor contact is strictly facilitated through NGO verification desks.\n• Medical guidelines: Minimum 90 days gap between whole blood donations.",
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = Color(0xFF424242)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Emergency Helpline Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
            border = CardDefaults.outlinedCardBorder(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Emergency NGO Helplines", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BloodPrimaryDark)
                Spacer(modifier = Modifier.height(4.dp))
                Text("• BloodConnect Central: 1800-11-2233 (Toll Free, 24x7)", fontSize = 11.sp)
                Text("• Delhi Health Emergency: 102 / 108", fontSize = 11.sp)
                Text("• Red Cross National Blood Bank: 011-23716441", fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
