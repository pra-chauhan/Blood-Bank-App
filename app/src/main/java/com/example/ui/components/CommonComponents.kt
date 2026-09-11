package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.DonationCertificateEntity
import com.example.data.local.entity.EmergencyRequestEntity
import com.example.data.local.entity.EmergencyStatusHistoryEntity
import com.example.data.model.DonorResponseStatus
import com.example.data.model.RequestStatus
import com.example.data.model.RequestUrgency
import com.example.data.model.UserRole
import com.example.ui.theme.BloodPrimary
import com.example.ui.theme.BloodPrimaryContainer
import com.example.ui.theme.BloodPrimaryDark
import com.example.ui.theme.BloodSecondary
import com.example.ui.theme.BloodSuccess
import com.example.ui.theme.BloodSuccessContainer
import com.example.ui.theme.BloodWarning
import com.example.ui.theme.BloodWarningContainer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BloodConnectTopBar(
    currentRole: UserRole,
    unreadNotificationCount: Int,
    onRoleClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.testTag("app_brand_header")
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(BloodPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🩸",
                        fontSize = 22.sp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "BloodConnect",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = BloodPrimaryDark
                    )
                    Text(
                        text = "Connect. Donate. Save Lives.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Right Actions: Role Selector Pill & Notifications
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Role Chip Switcher
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = when (currentRole) {
                        UserRole.NGO_ADMIN, UserRole.SUPER_ADMIN -> BloodPrimaryContainer
                        UserRole.VOLUNTEER -> BloodWarningContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    modifier = Modifier
                        .clickable { onRoleClick() }
                        .testTag("role_switcher_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (currentRole) {
                                UserRole.NGO_ADMIN, UserRole.SUPER_ADMIN -> Icons.Default.Security
                                UserRole.VOLUNTEER -> Icons.Default.Verified
                                else -> Icons.Default.Person
                            },
                            contentDescription = "Role icon",
                            modifier = Modifier.size(14.dp),
                            tint = when (currentRole) {
                                UserRole.NGO_ADMIN, UserRole.SUPER_ADMIN -> BloodPrimaryDark
                                UserRole.VOLUNTEER -> Color(0xFFE65100)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when (currentRole) {
                                UserRole.NGO_ADMIN -> "NGO Admin"
                                UserRole.VOLUNTEER -> "Volunteer"
                                UserRole.HOSPITAL_PARTNER -> "Hospital"
                                else -> "Donor / User"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = when (currentRole) {
                                UserRole.NGO_ADMIN -> BloodPrimaryDark
                                UserRole.VOLUNTEER -> Color(0xFFE65100)
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Notification Bell
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier.testTag("notifications_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadNotificationCount > 0) {
                                Badge(containerColor = BloodPrimary) {
                                    Text("$unreadNotificationCount")
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BloodGroupBadge(
    bloodGroup: String,
    modifier: Modifier = Modifier,
    size: Int = 44,
    textSize: Int = 16
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(BloodPrimaryContainer)
            .border(1.5.dp, BloodPrimary, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = bloodGroup,
            fontWeight = FontWeight.ExtraBold,
            fontSize = textSize.sp,
            color = BloodPrimaryDark
        )
    }
}

@Composable
fun EmergencyUrgencyBadge(urgency: RequestUrgency) {
    val (bg, textColor, label) = when (urgency) {
        RequestUrgency.CRITICAL -> Triple(Color(0xFFFFEBEE), BloodPrimary, "🚨 CRITICAL")
        RequestUrgency.URGENT -> Triple(Color(0xFFFFF3E0), Color(0xFFE65100), "⚠️ URGENT")
        RequestUrgency.NORMAL -> Triple(Color(0xFFE8F5E9), BloodSuccess, "✓ NORMAL")
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bg,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun RequestStatusBadge(status: RequestStatus) {
    val (bg, fg) = when (status) {
        RequestStatus.VERIFICATION_PENDING, RequestStatus.DRAFT -> Pair(Color(0xFFFFF8E1), Color(0xFFF57F17))
        RequestStatus.VERIFIED, RequestStatus.FINDING_DONORS, RequestStatus.DONOR_CONTACTED -> Pair(Color(0xFFE3F2FD), Color(0xFF1565C0))
        RequestStatus.DONOR_CONFIRMED, RequestStatus.DONOR_ARRIVED -> Pair(Color(0xFFE8F5E9), BloodSuccess)
        RequestStatus.DONATION_COMPLETED, RequestStatus.FULFILLED -> Pair(Color(0xFFE8F5E9), Color(0xFF1B5E20))
        RequestStatus.CANCELLED, RequestStatus.EXPIRED, RequestStatus.REJECTED -> Pair(Color(0xFFFFEBEE), BloodPrimary)
        else -> Pair(Color(0xFFF5F5F5), Color.DarkGray)
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bg
    ) {
        Text(
            text = status.display,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = fg,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun EmergencyTimelineView(
    history: List<EmergencyStatusHistoryEntity>,
    currentStatus: RequestStatus
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Live Request Timeline",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (history.isEmpty()) {
                Text(
                    text = "Request in progress. NGO verification underway.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                history.forEachIndexed { index, item ->
                    val isLast = index == history.size - 1
                    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    val timeStr = sdf.format(Date(item.timestamp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Timeline dot & connector line
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(28.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(if (isLast) BloodPrimary else BloodSuccess),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                            if (!isLast) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(38.dp)
                                        .background(Color(0xFFE0E0E0))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = item.title,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = timeStr,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = item.description,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Procedural Vector QR Code Visualizer
 * Creates an authentic high-contrast digital QR matrix pattern
 */
@Composable
fun ProceduralQrCode(
    dataString: String,
    modifier: Modifier = Modifier,
    sizeDp: Int = 180
) {
    val hash = dataString.hashCode()
    Canvas(
        modifier = modifier
            .size(sizeDp.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        val gridSize = 19
        val cellSize = size.width / gridSize
        val colorBlack = Color.Black

        // Corner locator squares (top-left, top-right, bottom-left)
        fun drawCornerMarker(gridX: Int, gridY: Int) {
            // Outer 7x7 box
            drawRect(
                color = colorBlack,
                topLeft = Offset(gridX * cellSize, gridY * cellSize),
                size = Size(7 * cellSize, 7 * cellSize)
            )
            // Inner white 5x5
            drawRect(
                color = Color.White,
                topLeft = Offset((gridX + 1) * cellSize, (gridY + 1) * cellSize),
                size = Size(5 * cellSize, 5 * cellSize)
            )
            // Center solid 3x3
            drawRect(
                color = colorBlack,
                topLeft = Offset((gridX + 2) * cellSize, (gridY + 2) * cellSize),
                size = Size(3 * cellSize, 3 * cellSize)
            )
        }

        drawCornerMarker(0, 0)
        drawCornerMarker(gridSize - 7, 0)
        drawCornerMarker(0, gridSize - 7)

        // Seeded deterministic QR bit pattern based on hash
        var bitIndex = 0
        for (r in 0 until gridSize) {
            for (c in 0 until gridSize) {
                // Skip corner marker zones
                val inTopLeft = r < 8 && c < 8
                val inTopRight = r < 8 && c >= gridSize - 8
                val inBottomLeft = r >= gridSize - 8 && c < 8

                if (!inTopLeft && !inTopRight && !inBottomLeft) {
                    val bitVal = ((hash ushr (bitIndex % 31)) xor (r * 17 + c * 31)) % 3 == 0
                    if (bitVal) {
                        drawRect(
                            color = colorBlack,
                            topLeft = Offset(c * cellSize, r * cellSize),
                            size = Size(cellSize, cellSize)
                        )
                    }
                    bitIndex++
                }
            }
        }
    }
}

/**
 * Digital Certificate Modal with QR Verification
 */
@Composable
fun DigitalCertificateDialog(
    certificate: DonationCertificateEntity,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("certificate_modal")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header seal
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(BloodPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🩸", fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "BLOODCONNECT NGO",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = BloodPrimary,
                    letterSpacing = 1.5.sp
                )

                Text(
                    text = "Certificate of LifeSaver Honor",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF212121)
                )

                Text(
                    text = "This is proudly presented to",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = certificate.donorName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = BloodPrimaryDark,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Text(
                    text = "for selflessly donating ${certificate.units} unit of ${certificate.bloodGroup} blood at ${certificate.hospitalOrCamp} on ${certificate.donationDate}. Your noble act helped save precious lives.",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF424242),
                    lineHeight = 17.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Verification QR Code
                ProceduralQrCode(
                    dataString = certificate.qrCodeData,
                    sizeDp = 140
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Cert ID: ${certificate.certificateNumber}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace,
                    color = Color.DarkGray
                )

                Text(
                    text = "Verification: ${certificate.verificationCode}",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = BloodSuccess
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = BloodPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("close_certificate_button")
                ) {
                    Text("Close Certificate")
                }
            }
        }
    }
}

/**
 * Incoming Emergency Notification Prompt
 * Shows prompt: [ I CAN DONATE ] [ MAYBE ] [ NOT AVAILABLE ]
 */
@Composable
fun IncomingEmergencyNotificationDialog(
    request: EmergencyRequestEntity,
    onAccept: () -> Unit,
    onMaybe: () -> Unit,
    onDecline: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("emergency_alert_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header Alert
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFEBEE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = BloodPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Emergency Blood Request",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = BloodPrimary
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = Color(0xFFFAFAFA),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Blood Group Urgently Required",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "${request.bloodGroup} (${request.unitsRequired} Units Required)",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BloodPrimaryDark
                                )
                            }
                            BloodGroupBadge(bloodGroup = request.bloodGroup, size = 42, textSize = 15)
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Hospital: ${request.hospitalName}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF212121)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Approximate Distance: ~3.8 km away",
                            fontSize = 12.sp,
                            color = BloodSuccess,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 22.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Required: ${request.requiredTime}",
                            fontSize = 12.sp,
                            color = Color(0xFF616161),
                            modifier = Modifier.padding(start = 22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Can you donate to help save this life?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF212121),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Actions: [ I CAN DONATE ] [ MAYBE ] [ NOT AVAILABLE ]
                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(containerColor = BloodPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_accept_emergency")
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("I CAN DONATE", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onMaybe,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).testTag("btn_maybe_emergency")
                    ) {
                        Text("MAYBE", fontSize = 12.sp, color = Color(0xFF424242))
                    }

                    OutlinedButton(
                        onClick = onDecline,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).testTag("btn_decline_emergency")
                    ) {
                        Text("NOT AVAILABLE", fontSize = 12.sp, color = Color(0xFF757575))
                    }
                }
            }
        }
    }
}
