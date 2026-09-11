package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.CampStatus
import com.example.data.model.DonorResponseStatus
import com.example.data.model.NotificationDeliveryStatus
import com.example.data.model.NotificationType
import com.example.data.model.RequestStatus
import com.example.data.model.RequestUrgency
import com.example.data.model.UserRole

class Converters {
    @TypeConverter
    fun fromUserRole(role: UserRole?): String = role?.name ?: UserRole.DONOR.name

    @TypeConverter
    fun toUserRole(value: String?): UserRole = value?.let {
        try { UserRole.valueOf(it) } catch (e: Exception) { UserRole.DONOR }
    } ?: UserRole.DONOR

    @TypeConverter
    fun fromRequestStatus(status: RequestStatus?): String = status?.name ?: RequestStatus.VERIFICATION_PENDING.name

    @TypeConverter
    fun toRequestStatus(value: String?): RequestStatus = value?.let {
        try { RequestStatus.valueOf(it) } catch (e: Exception) { RequestStatus.VERIFICATION_PENDING }
    } ?: RequestStatus.VERIFICATION_PENDING

    @TypeConverter
    fun fromRequestUrgency(urgency: RequestUrgency?): String = urgency?.name ?: RequestUrgency.NORMAL.name

    @TypeConverter
    fun toRequestUrgency(value: String?): RequestUrgency = value?.let {
        try { RequestUrgency.valueOf(it) } catch (e: Exception) { RequestUrgency.NORMAL }
    } ?: RequestUrgency.NORMAL

    @TypeConverter
    fun fromCampStatus(status: CampStatus?): String = status?.name ?: CampStatus.UPCOMING.name

    @TypeConverter
    fun toCampStatus(value: String?): CampStatus = value?.let {
        try { CampStatus.valueOf(it) } catch (e: Exception) { CampStatus.UPCOMING }
    } ?: CampStatus.UPCOMING

    @TypeConverter
    fun fromNotificationType(type: NotificationType?): String = type?.name ?: NotificationType.EMERGENCY_REQUEST.name

    @TypeConverter
    fun toNotificationType(value: String?): NotificationType = value?.let {
        try { NotificationType.valueOf(it) } catch (e: Exception) { NotificationType.EMERGENCY_REQUEST }
    } ?: NotificationType.EMERGENCY_REQUEST

    @TypeConverter
    fun fromNotificationDelivery(status: NotificationDeliveryStatus?): String = status?.name ?: NotificationDeliveryStatus.SENT.name

    @TypeConverter
    fun toNotificationDelivery(value: String?): NotificationDeliveryStatus = value?.let {
        try { NotificationDeliveryStatus.valueOf(it) } catch (e: Exception) { NotificationDeliveryStatus.SENT }
    } ?: NotificationDeliveryStatus.SENT

    @TypeConverter
    fun fromDonorResponseStatus(status: DonorResponseStatus?): String = status?.name ?: DonorResponseStatus.PENDING.name

    @TypeConverter
    fun toDonorResponseStatus(value: String?): DonorResponseStatus = value?.let {
        try { DonorResponseStatus.valueOf(it) } catch (e: Exception) { DonorResponseStatus.PENDING }
    } ?: DonorResponseStatus.PENDING
}
