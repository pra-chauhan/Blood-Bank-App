package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.AuditLogDao
import com.example.data.local.dao.BloodBankDao
import com.example.data.local.dao.CampRegistrationDao
import com.example.data.local.dao.CertificateDao
import com.example.data.local.dao.DonationCampDao
import com.example.data.local.dao.DonorMatchDao
import com.example.data.local.dao.EmergencyRequestDao
import com.example.data.local.dao.EmergencyStatusHistoryDao
import com.example.data.local.dao.HospitalDao
import com.example.data.local.dao.NotificationDao
import com.example.data.local.dao.UserDao
import com.example.data.local.dao.VolunteerDao
import com.example.data.local.entity.AppNotificationEntity
import com.example.data.local.entity.AuditLogEntity
import com.example.data.local.entity.BloodBankEntity
import com.example.data.local.entity.CampRegistrationEntity
import com.example.data.local.entity.DonationCampEntity
import com.example.data.local.entity.DonationCertificateEntity
import com.example.data.local.entity.DonorMatchEntity
import com.example.data.local.entity.DonorProfileEntity
import com.example.data.local.entity.EmergencyRequestEntity
import com.example.data.local.entity.EmergencyStatusHistoryEntity
import com.example.data.local.entity.HospitalEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.VolunteerEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        DonorProfileEntity::class,
        EmergencyRequestEntity::class,
        EmergencyStatusHistoryEntity::class,
        DonorMatchEntity::class,
        DonationCampEntity::class,
        CampRegistrationEntity::class,
        HospitalEntity::class,
        BloodBankEntity::class,
        VolunteerEntity::class,
        AppNotificationEntity::class,
        DonationCertificateEntity::class,
        AuditLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BloodConnectDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun emergencyRequestDao(): EmergencyRequestDao
    abstract fun emergencyStatusHistoryDao(): EmergencyStatusHistoryDao
    abstract fun donorMatchDao(): DonorMatchDao
    abstract fun donationCampDao(): DonationCampDao
    abstract fun campRegistrationDao(): CampRegistrationDao
    abstract fun hospitalDao(): HospitalDao
    abstract fun bloodBankDao(): BloodBankDao
    abstract fun volunteerDao(): VolunteerDao
    abstract fun notificationDao(): NotificationDao
    abstract fun certificateDao(): CertificateDao
    abstract fun auditLogDao(): AuditLogDao

    companion object {
        @Volatile
        private var INSTANCE: BloodConnectDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): BloodConnectDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BloodConnectDatabase::class.java,
                    "bloodconnect_database"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        DatabaseSeeder.seedInitialData(database)
                    }
                }
            }
        }
    }
}
