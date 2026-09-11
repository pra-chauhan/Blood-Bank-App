package com.example.ui.screens.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.DonationCampEntity
import com.example.data.local.entity.HospitalEntity
import com.example.data.local.entity.BloodBankEntity
import com.example.ui.theme.BloodPrimary
import com.example.ui.theme.BloodPrimaryContainer
import com.example.ui.theme.BloodPrimaryDark
import com.example.ui.theme.BloodSuccess

data class MapMarker(
    val id: String,
    val title: String,
    val subtitle: String,
    val address: String,
    val phone: String,
    val type: String, // CAMP, HOSPITAL, BLOOD_BANK
    val lat: Double,
    val lon: Double,
    val extra: String = ""
)

@Composable
fun MapScreen(
    camps: List<DonationCampEntity>,
    hospitals: List<HospitalEntity>,
    bloodBanks: List<BloodBankEntity>,
    onCampClick: (DonationCampEntity) -> Unit
) {
    var selectedTypeFilter by remember { mutableStateOf("ALL") }
    var selectedRadiusKm by remember { mutableStateOf(15.0) }
    var selectedMarker by remember { mutableStateOf<MapMarker?>(null) }

    // User center: Connaught Place, New Delhi (28.6304, 77.2177)
    val centerLat = 28.6304
    val centerLon = 77.2177

    val allMarkers = remember(camps, hospitals, bloodBanks) {
        val list = mutableListOf<MapMarker>()
        camps.forEach { camp ->
            list.add(
                MapMarker(
                    id = camp.id,
                    title = camp.name,
                    subtitle = "Blood Donation Camp • ${camp.date}",
                    address = camp.address,
                    phone = camp.contactPhone,
                    type = "CAMP",
                    lat = camp.latitude,
                    lon = camp.longitude,
                    extra = "Slots: ${camp.registrationCount}/${camp.capacity} Registered"
                )
            )
        }
        hospitals.forEach { hosp ->
            list.add(
                MapMarker(
                    id = hosp.id,
                    title = hosp.name,
                    subtitle = "Hospital • Emergency Blood Transfusion",
                    address = hosp.address,
                    phone = hosp.emergencyContact,
                    type = "HOSPITAL",
                    lat = hosp.latitude,
                    lon = hosp.longitude,
                    extra = if (hosp.hasBloodBank) "In-house Blood Bank Available" else "External Crossmatch"
                )
            )
        }
        bloodBanks.forEach { bb ->
            list.add(
                MapMarker(
                    id = bb.id,
                    title = bb.name,
                    subtitle = "Authorized Blood Bank • ${bb.openingHours}",
                    address = bb.address,
                    phone = bb.phone,
                    type = "BLOOD_BANK",
                    lat = bb.latitude,
                    lon = bb.longitude,
                    extra = bb.services
                )
            )
        }
        list
    }

    val filteredMarkers = remember(allMarkers, selectedTypeFilter) {
        if (selectedTypeFilter == "ALL") allMarkers
        else allMarkers.filter { it.type == selectedTypeFilter }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F4F6))
            .testTag("map_screen")
    ) {
        // Map Canvas Area
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(filteredMarkers) {
                    detectTapGestures { tapOffset ->
                        // Detect marker tap within radius
                        val width = size.width
                        val height = size.height
                        val minLat = 28.45
                        val maxLat = 28.75
                        val minLon = 77.00
                        val maxLon = 77.35

                        val tapped = filteredMarkers.firstOrNull { marker ->
                            val normX = (marker.lon - minLon) / (maxLon - minLon)
                            val normY = 1.0 - (marker.lat - minLat) / (maxLat - minLat)
                            val px = (normX * width).toFloat()
                            val py = (normY * height).toFloat()
                            val dist = kotlin.math.hypot(tapOffset.x - px, tapOffset.y - py)
                            dist < 60f
                        }
                        selectedMarker = tapped
                    }
                }
        ) {
            val width = size.width
            val height = size.height

            // Delhi NCR Coordinate bounding box
            val minLat = 28.45
            val maxLat = 28.75
            val minLon = 77.00
            val maxLon = 77.35

            // Draw Road/Sector Grid lines
            val gridColor = Color(0xFFE5E7EB)
            for (i in 1..8) {
                val y = height * (i / 9f)
                drawLine(gridColor, Offset(0f, y), Offset(width, y), strokeWidth = 2f)
            }
            for (i in 1..8) {
                val x = width * (i / 9f)
                drawLine(gridColor, Offset(x, 0f), Offset(x, height), strokeWidth = 2f)
            }

            // Draw Yamuna River blue curve
            val riverColor = Color(0xFFBBDEFB)
            val riverPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(width * 0.75f, 0f)
                cubicTo(
                    width * 0.70f, height * 0.35f,
                    width * 0.65f, height * 0.65f,
                    width * 0.85f, height
                )
            }
            drawPath(riverPath, riverColor, style = Stroke(width = 16f))

            // User Center Pulse (Connaught Place)
            val userNormX = (centerLon - minLon) / (maxLon - minLon)
            val userNormY = 1.0 - (centerLat - minLat) / (maxLat - minLat)
            val userPx = (userNormX * width).toFloat()
            val userPy = (userNormY * height).toFloat()

            // Search Radius circle
            val radiusPx = (selectedRadiusKm / 25.0) * (width * 0.45f)
            drawCircle(
                color = BloodPrimary.copy(alpha = 0.08f),
                radius = radiusPx.toFloat(),
                center = Offset(userPx, userPy)
            )
            drawCircle(
                color = BloodPrimary.copy(alpha = 0.4f),
                radius = radiusPx.toFloat(),
                center = Offset(userPx, userPy),
                style = Stroke(
                    width = 2.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                )
            )

            // User location dot
            drawCircle(color = Color(0xFF1976D2), radius = 10f, center = Offset(userPx, userPy))
            drawCircle(color = Color.White, radius = 5f, center = Offset(userPx, userPy))

            // Draw Facility Markers
            filteredMarkers.forEach { marker ->
                val normX = (marker.lon - minLon) / (maxLon - minLon)
                val normY = 1.0 - (marker.lat - minLat) / (maxLat - minLat)
                val px = (normX * width).toFloat()
                val py = (normY * height).toFloat()

                val pinColor = when (marker.type) {
                    "CAMP" -> BloodPrimary
                    "HOSPITAL" -> Color(0xFF1565C0)
                    else -> Color(0xFF880E4F)
                }

                val isSelected = selectedMarker?.id == marker.id

                if (isSelected) {
                    drawCircle(
                        color = pinColor.copy(alpha = 0.25f),
                        radius = 32f,
                        center = Offset(px, py)
                    )
                }

                // Marker circle with white border
                drawCircle(
                    color = pinColor,
                    radius = if (isSelected) 18f else 14f,
                    center = Offset(px, py)
                )
                drawCircle(
                    color = Color.White,
                    radius = if (isSelected) 18f else 14f,
                    center = Offset(px, py),
                    style = Stroke(width = 3f)
                )
                // Center inner white dot
                drawCircle(
                    color = Color.White,
                    radius = if (isSelected) 6f else 4f,
                    center = Offset(px, py)
                )
            }
        }

        // Top Overlay: Search & Type Filter Chips
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Facility & Camp Map (Delhi NCR)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF212121)
                    )
                    Text(
                        text = "Showing verified hospitals, blood banks and active camps",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Type filters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "ALL" to "All (${allMarkers.size})",
                            "CAMP" to "Camps",
                            "HOSPITAL" to "Hospitals",
                            "BLOOD_BANK" to "Blood Banks"
                        ).forEach { (type, label) ->
                            val isSel = selectedTypeFilter == type
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSel) BloodPrimary else Color(0xFFF0F0F0),
                                modifier = Modifier
                                    .clickable { selectedTypeFilter = type }
                                    .testTag("map_filter_$type")
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSel) Color.White else Color(0xFF424242),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Radius filters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Search Radius:", fontSize = 11.sp, color = Color.Gray)
                        listOf(5.0 to "5 km", 10.0 to "10 km", 25.0 to "25 km").forEach { (radius, label) ->
                            val isRadSel = selectedRadiusKm == radius
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isRadSel) BloodPrimaryContainer else Color.Transparent,
                                border = if (isRadSel) CardDefaults.outlinedCardBorder() else null,
                                modifier = Modifier.clickable { selectedRadiusKm = radius }
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isRadSel) BloodPrimaryDark else Color.Gray,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Marker Preview Drawer Card
        selectedMarker?.let { marker ->
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .testTag("map_marker_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (marker.type) {
                                            "CAMP" -> Color(0xFFFFEBEE)
                                            "HOSPITAL" -> Color(0xFFE3F2FD)
                                            else -> Color(0xFFFCE4EC)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (marker.type) {
                                        "CAMP" -> Icons.Default.Event
                                        "HOSPITAL" -> Icons.Default.LocalHospital
                                        else -> Icons.Default.Place
                                    },
                                    contentDescription = null,
                                    tint = when (marker.type) {
                                        "CAMP" -> BloodPrimary
                                        "HOSPITAL" -> Color(0xFF1565C0)
                                        else -> Color(0xFF880E4F)
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = marker.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF212121)
                                )
                                Text(
                                    text = marker.subtitle,
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        IconButton(onClick = { selectedMarker = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = marker.address, fontSize = 11.sp, color = Color(0xFF424242))
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = BloodSuccess, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Helpline: ${marker.phone}", fontSize = 11.sp, color = BloodSuccess, fontWeight = FontWeight.SemiBold)
                    }

                    if (marker.extra.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = marker.extra,
                            fontSize = 11.sp,
                            color = BloodPrimaryDark,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (marker.type == "CAMP") {
                        val camp = camps.find { it.id == marker.id }
                        Button(
                            onClick = { camp?.let { onCampClick(it) } },
                            colors = ButtonDefaults.buttonColors(containerColor = BloodPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("btn_map_view_camp")
                        ) {
                            Text("View & Register for Camp 🩸")
                        }
                    } else {
                        Button(
                            onClick = { /* Simulated direct dial */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Call Emergency Desk: ${marker.phone}")
                        }
                    }
                }
            }
        }
    }
}
