package com.example.data.model

enum class UserRole {
    DONOR,
    USER,
    NGO_ADMIN,
    NGO_STAFF,
    VOLUNTEER,
    HOSPITAL_PARTNER,
    SUPER_ADMIN
}

enum class BloodGroup(val display: String) {
    A_POS("A+"),
    A_NEG("A-"),
    B_POS("B+"),
    B_NEG("B-"),
    AB_POS("AB+"),
    AB_NEG("AB-"),
    O_POS("O+"),
    O_NEG("O-");

    companion object {
        fun fromString(value: String): BloodGroup {
            return entries.find { it.display.equals(value.trim(), ignoreCase = true) || it.name.equals(value.trim(), ignoreCase = true) } ?: O_POS
        }
    }
}

enum class RequestUrgency {
    CRITICAL,
    URGENT,
    NORMAL
}

enum class RequestStatus(val display: String) {
    DRAFT("Draft"),
    SUBMITTED("Submitted"),
    VERIFICATION_PENDING("Verification Pending"),
    VERIFIED("Verified"),
    FINDING_DONORS("Finding Donors"),
    DONOR_CONTACTED("Donors Contacted"),
    DONOR_CONFIRMED("Donor Confirmed"),
    DONOR_ARRIVED("Donor Arrived at Hospital"),
    DONATION_COMPLETED("Donation Completed"),
    FULFILLED("Request Fulfilled"),
    CANCELLED("Cancelled"),
    EXPIRED("Expired"),
    REJECTED("Rejected")
}

enum class DonorResponseStatus {
    PENDING,
    ACCEPTED,
    MAYBE,
    NOT_AVAILABLE,
    CANCELLED
}

enum class CampStatus {
    UPCOMING,
    ACTIVE,
    COMPLETED,
    CANCELLED
}

enum class NotificationType {
    EMERGENCY_REQUEST,
    CAMP_ALERT,
    DONOR_ACCEPTED,
    STATUS_UPDATE,
    CERTIFICATE_ISSUED,
    SYSTEM_ANNOUNCEMENT
}

enum class NotificationDeliveryStatus {
    SENT,
    DELIVERED,
    OPENED,
    ACCEPTED,
    REJECTED,
    EXPIRED
}
