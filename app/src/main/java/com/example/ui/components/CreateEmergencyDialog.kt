package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.local.entity.HospitalEntity
import com.example.data.model.RequestUrgency
import com.example.ui.theme.BloodPrimary
import com.example.ui.theme.BloodPrimaryContainer
import com.example.ui.theme.BloodPrimaryDark
import com.example.ui.theme.BloodWarning

@Composable
fun CreateEmergencyDialog(
    hospitals: List<HospitalEntity>,
    onDismiss: () -> Unit,
    onSubmit: (
        patientName: String,
        bloodGroup: String,
        units: Int,
        hospitalName: String,
        hospitalAddress: String,
        urgency: RequestUrgency,
        requiredTime: String,
        attendantPhone: String,
        notes: String
    ) -> Unit
) {
    val bloodGroups = listOf("O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-")
    var selectedBloodGroup by remember { mutableStateOf("O+") }
    var unitsRequired by remember { mutableIntStateOf(2) }
    var patientName by remember { mutableStateOf("") }
    var selectedHospital by remember { mutableStateOf(hospitals.firstOrNull()?.name ?: "AIIMS New Delhi") }
    var hospitalAddress by remember { mutableStateOf(hospitals.firstOrNull()?.address ?: "Ansari Nagar, New Delhi") }
    var selectedUrgency by remember { mutableStateOf(RequestUrgency.CRITICAL) }
    var requiredTime by remember { mutableStateOf("Immediately (Within 2 Hours)") }
    var attendantPhone by remember { mutableStateOf("+91 98100 23456") }
    var additionalNotes by remember { mutableStateOf("") }
    var hasConsent by remember { mutableStateOf(true) }
    var showConfirmation by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("create_emergency_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(BloodPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Emergency Blood Request",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = BloodPrimaryDark
                            )
                            Text(
                                text = "Fast-track verification by NGO",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (!showConfirmation) {
                    // Form View
                    // Patient Name
                    Text("Patient Full Name *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF424242))
                    OutlinedTextField(
                        value = patientName,
                        onValueChange = { patientName = it },
                        placeholder = { Text("e.g. Ramesh Chandra (Age 42)", fontSize = 13.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 12.dp)
                            .testTag("input_patient_name"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    // Blood Group Selector Chips
                    Text("Blood Group Required *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF424242))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        bloodGroups.take(4).forEach { bg ->
                            val isSelected = selectedBloodGroup == bg
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) BloodPrimary else Color(0xFFF5F5F5),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 2.dp)
                                    .clickable { selectedBloodGroup = bg }
                                    .testTag("chip_blood_$bg")
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = bg,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isSelected) Color.White else Color.Black
                                    )
                                }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        bloodGroups.takeLast(4).forEach { bg ->
                            val isSelected = selectedBloodGroup == bg
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) BloodPrimary else Color(0xFFF5F5F5),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 2.dp)
                                    .clickable { selectedBloodGroup = bg }
                                    .testTag("chip_blood_$bg")
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = bg,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isSelected) Color.White else Color.Black
                                    )
                                }
                            }
                        }
                    }

                    // Units Required Stepper
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Units Required (Bags)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF424242))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFEEEEEE),
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { if (unitsRequired > 1) unitsRequired-- }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Text(
                                text = "$unitsRequired",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 14.dp)
                            )

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BloodPrimaryContainer,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { if (unitsRequired < 8) unitsRequired++ }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BloodPrimaryDark)
                                }
                            }
                        }
                    }

                    // Urgency Selector
                    Text("Urgency Level *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF424242))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp, bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RequestUrgency.entries.forEach { urgency ->
                            val isSelected = selectedUrgency == urgency
                            val (color, label) = when (urgency) {
                                RequestUrgency.CRITICAL -> Pair(BloodPrimary, "🚨 Critical")
                                RequestUrgency.URGENT -> Pair(Color(0xFFE65100), "⚠️ Urgent")
                                RequestUrgency.NORMAL -> Pair(Color(0xFF2E7D32), "Normal")
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) color.copy(alpha = 0.15f) else Color(0xFFF5F5F5),
                                border = if (isSelected) CardDefaults.outlinedCardBorder() else null,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedUrgency = urgency }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) color else Color.Gray
                                    )
                                }
                            }
                        }
                    }

                    // Hospital Selection
                    Text("Hospital / Medical Facility *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF424242))
                    Column(modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)) {
                        hospitals.take(3).forEach { hosp ->
                            val isHospSelected = selectedHospital == hosp.name
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isHospSelected) Color(0xFFFFEBEE) else Color(0xFFF9F9F9),
                                border = if (isHospSelected) CardDefaults.outlinedCardBorder() else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .clickable {
                                        selectedHospital = hosp.name
                                        hospitalAddress = hosp.address
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalHospital,
                                        contentDescription = null,
                                        tint = if (isHospSelected) BloodPrimary else Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = hosp.name,
                                            fontSize = 12.sp,
                                            fontWeight = if (isHospSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isHospSelected) BloodPrimaryDark else Color(0xFF212121)
                                        )
                                        Text(
                                            text = hosp.address,
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Attendant Phone
                    Text("Hospital Attendant / Contact Phone *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF424242))
                    OutlinedTextField(
                        value = attendantPhone,
                        onValueChange = { attendantPhone = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 12.dp)
                            .testTag("input_attendant_phone"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    // Additional Notes
                    Text("Clinical Notes / Diagnosis (Optional)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF424242))
                    OutlinedTextField(
                        value = additionalNotes,
                        onValueChange = { additionalNotes = it },
                        placeholder = { Text("e.g. ICU Bed 14, Platelets + PRBC required", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 12.dp),
                        shape = RoundedCornerShape(10.dp),
                        maxLines = 2
                    )

                    // Consent checkbox
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp)
                    ) {
                        Checkbox(
                            checked = hasConsent,
                            onCheckedChange = { hasConsent = it },
                            colors = CheckboxDefaults.colors(checkedColor = BloodPrimary)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "I verify this emergency request is genuine and give consent for BloodConnect NGO to verify with the hospital.",
                            fontSize = 11.sp,
                            color = Color(0xFF424242),
                            lineHeight = 15.sp
                        )
                    }

                    Button(
                        onClick = { showConfirmation = true },
                        enabled = (patientName.isNotBlank() || true) && hasConsent,
                        colors = ButtonDefaults.buttonColors(containerColor = BloodPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_review_emergency")
                    ) {
                        Text("REVIEW & SUBMIT EMERGENCY", fontWeight = FontWeight.Bold)
                    }
                } else {
                    // Confirmation View
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFFAFAFA),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Confirm Emergency Submission", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BloodPrimaryDark)
                            Spacer(modifier = Modifier.height(10.dp))

                            Text("Patient: ${patientName.ifEmpty { "Urgent Trauma Patient" }}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("Requirement: $unitsRequired Units of $selectedBloodGroup", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BloodPrimary)
                            Text("Hospital: $selectedHospital", fontSize = 13.sp)
                            Text("Urgency: ${selectedUrgency.name}", fontSize = 12.sp, color = BloodPrimary)
                            Text("Attendant: $attendantPhone", fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Notice: After submission, this request enters 'Verification Pending'. NGO coordinators will validate patient details before dispatching emergency donor alerts.",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            onSubmit(
                                patientName.ifEmpty { "Patient (Urgent Care)" },
                                selectedBloodGroup,
                                unitsRequired,
                                selectedHospital,
                                hospitalAddress,
                                selectedUrgency,
                                requiredTime,
                                attendantPhone,
                                additionalNotes
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BloodPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_confirm_submit_emergency")
                    ) {
                        Text("CONFIRM EMERGENCY REQUEST 🚨", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { showConfirmation = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE), contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Edit Details")
                    }
                }
            }
        }
    }
}
