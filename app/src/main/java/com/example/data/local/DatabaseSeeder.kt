package com.example.data.local

import com.example.data.local.entity.AppNotificationEntity
import com.example.data.local.entity.AuditLogEntity
import com.example.data.local.entity.BloodBankEntity
import com.example.data.local.entity.DonationCampEntity
import com.example.data.local.entity.DonationCertificateEntity
import com.example.data.local.entity.DonorMatchEntity
import com.example.data.local.entity.DonorProfileEntity
import com.example.data.local.entity.EmergencyRequestEntity
import com.example.data.local.entity.EmergencyStatusHistoryEntity
import com.example.data.local.entity.HospitalEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.VolunteerEntity
import com.example.data.model.CampStatus
import com.example.data.model.DonorResponseStatus
import com.example.data.model.NotificationDeliveryStatus
import com.example.data.model.NotificationType
import com.example.data.model.RequestStatus
import com.example.data.model.RequestUrgency
import com.example.data.model.UserRole
import java.util.UUID

object DatabaseSeeder {
    // Current primary logged-in user id
    const val CURRENT_USER_ID = "usr_rahul_sharma_001"
    const val ADMIN_USER_ID = "usr_dr_swaminathan_999"
    const val VOLUNTEER_USER_ID = "usr_ananya_vol_007"

    suspend fun seedInitialData(db: BloodConnectDatabase) {
        // 1. Users
        val users = listOf(
            UserEntity(
                id = CURRENT_USER_ID,
                fullName = "Rahul Sharma",
                phone = "+91 98765 43210",
                email = "rahul.sharma@bloodconnect.org",
                bloodGroup = "O+",
                role = UserRole.DONOR,
                city = "New Delhi",
                savedAddress = "Connaught Place, Central Delhi",
                latitude = 28.6304,
                longitude = 77.2177,
                isDonorAvailable = true,
                emergencyContact = "+91 98765 43219"
            ),
            UserEntity(
                id = "usr_priya_patel_002",
                fullName = "Priya Patel",
                phone = "+91 98111 22334",
                email = "priya.patel@gmail.com",
                bloodGroup = "A+",
                role = UserRole.DONOR,
                city = "New Delhi",
                savedAddress = "Lajpat Nagar IV, South Delhi",
                latitude = 28.5678,
                longitude = 77.2433,
                isDonorAvailable = true
            ),
            UserEntity(
                id = "usr_amit_verma_003",
                fullName = "Amit Verma",
                phone = "+91 99223 34455",
                email = "amit.verma@outlook.com",
                bloodGroup = "O-", // Universal donor
                role = UserRole.DONOR,
                city = "New Delhi",
                savedAddress = "Saket District Centre, New Delhi",
                latitude = 28.5244,
                longitude = 77.2066,
                isDonorAvailable = true
            ),
            UserEntity(
                id = "usr_sneha_rao_004",
                fullName = "Sneha Rao",
                phone = "+91 97334 45566",
                email = "sneha.rao@gmail.com",
                bloodGroup = "B+",
                role = UserRole.DONOR,
                city = "New Delhi",
                savedAddress = "Karol Bagh, Central Delhi",
                latitude = 28.6517,
                longitude = 77.1906,
                isDonorAvailable = true
            ),
            UserEntity(
                id = "usr_vikram_singh_005",
                fullName = "Vikram Singh",
                phone = "+91 96445 56677",
                email = "vikram.singh@gmail.com",
                bloodGroup = "AB+", // Universal recipient
                role = UserRole.DONOR,
                city = "New Delhi",
                savedAddress = "Dwarka Sector 10, New Delhi",
                latitude = 28.5813,
                longitude = 77.0594,
                isDonorAvailable = true
            ),
            UserEntity(
                id = ADMIN_USER_ID,
                fullName = "Dr. Arvind Swaminathan",
                phone = "+91 98000 11223",
                email = "admin@bloodconnect.ngo",
                bloodGroup = "O+",
                role = UserRole.NGO_ADMIN,
                city = "New Delhi",
                savedAddress = "BloodConnect HQ, Barakhamba Road, New Delhi",
                latitude = 28.6291,
                longitude = 77.2285
            ),
            UserEntity(
                id = VOLUNTEER_USER_ID,
                fullName = "Ananya Deshmukh",
                phone = "+91 98555 66778",
                email = "ananya.vol@bloodconnect.org",
                bloodGroup = "B+",
                role = UserRole.VOLUNTEER,
                city = "New Delhi",
                savedAddress = "Hauz Khas, New Delhi",
                latitude = 28.5494,
                longitude = 77.2001
            )
        )
        db.userDao().insertUsers(users)

        // 2. Donor Profiles
        db.userDao().insertDonorProfile(
            DonorProfileEntity(
                userId = CURRENT_USER_ID,
                bloodGroup = "O+",
                totalDonations = 4,
                isEligible = true,
                preferredMaxRadiusKm = 15.0
            )
        )

        // 3. Hospitals
        val hospitals = listOf(
            HospitalEntity(
                id = "hosp_aiims_delhi",
                name = "AIIMS New Delhi",
                address = "Sri Aurobindo Marg, Ansari Nagar",
                city = "New Delhi",
                phone = "+91 11 2658 8500",
                emergencyContact = "+91 11 2659 4705",
                latitude = 28.5672,
                longitude = 77.2100,
                isVerified = true,
                hasBloodBank = true
            ),
            HospitalEntity(
                id = "hosp_safdarjung",
                name = "Safdarjung Hospital & Trauma Centre",
                address = "Ring Road, Opposite AIIMS",
                city = "New Delhi",
                phone = "+91 11 2616 5060",
                emergencyContact = "+91 11 2670 7444",
                latitude = 28.5701,
                longitude = 77.2078,
                isVerified = true,
                hasBloodBank = true
            ),
            HospitalEntity(
                id = "hosp_max_saket",
                name = "Max Super Speciality Hospital",
                address = "1, 2, Press Enclave Marg, Saket",
                city = "New Delhi",
                phone = "+91 11 2651 5050",
                emergencyContact = "+91 11 4055 4055",
                latitude = 28.5284,
                longitude = 77.2132,
                isVerified = true,
                hasBloodBank = true
            ),
            HospitalEntity(
                id = "hosp_gangaram",
                name = "Sir Ganga Ram Hospital",
                address = "Old Rajinder Nagar, New Delhi",
                city = "New Delhi",
                phone = "+91 11 2575 0000",
                emergencyContact = "+91 11 4225 4000",
                latitude = 28.6385,
                longitude = 77.1895,
                isVerified = true,
                hasBloodBank = true
            ),
            HospitalEntity(
                id = "hosp_fortis_escorts",
                name = "Fortis Escorts Heart Institute",
                address = "Okhla Road, New Delhi",
                city = "New Delhi",
                phone = "+91 11 4713 5000",
                emergencyContact = "+91 11 4713 5555",
                latitude = 28.5606,
                longitude = 77.2798,
                isVerified = true,
                hasBloodBank = true
            )
        )
        db.hospitalDao().insertHospitals(hospitals)

        // 4. Blood Banks
        val bloodBanks = listOf(
            BloodBankEntity(
                id = "bb_redcross_delhi",
                name = "Indian Red Cross Society National Blood Bank",
                address = "1 Red Cross Road, Sansad Marg, New Delhi",
                city = "New Delhi",
                phone = "+91 11 2371 6441",
                latitude = 28.6219,
                longitude = 77.2114,
                openingHours = "24 Hours (Daily)",
                services = "Whole Blood, Packed Red Cells, Platelets, FFP, Cryoprecipitate"
            ),
            BloodBankEntity(
                id = "bb_rotary_delhi",
                name = "Rotary Blood Bank Central",
                address = "56-57 Tughlakabad Institutional Area, New Delhi",
                city = "New Delhi",
                phone = "+91 11 2996 0524",
                latitude = 28.5142,
                longitude = 77.2625,
                openingHours = "24x7 Emergency Services",
                services = "NAT-Tested Blood, Platelet Apheresis, Plasma Services"
            ),
            BloodBankEntity(
                id = "bb_aiims_transfusion",
                name = "AIIMS Department of Transfusion Medicine",
                address = "Ground Floor, Main Hospital, AIIMS, New Delhi",
                city = "New Delhi",
                phone = "+91 11 2659 4700",
                latitude = 28.5675,
                longitude = 77.2105,
                openingHours = "24x7 Round the Clock",
                services = "Rare Blood Group Registry, Platelet Agitators, Emergency Crossmatch"
            ),
            BloodBankEntity(
                id = "bb_lions_blood",
                name = "Lions Blood Bank & Research Foundation",
                address = "AA Block, Sector 2, Shalimar Bagh, New Delhi",
                city = "New Delhi",
                phone = "+91 11 4225 8000",
                latitude = 28.7166,
                longitude = 77.1594,
                openingHours = "08:00 AM - 08:00 PM",
                services = "Voluntary Blood Collection, Component Storage"
            )
        )
        db.bloodBankDao().insertBloodBanks(bloodBanks)

        // 5. Donation Camps
        val camps = listOf(
            DonationCampEntity(
                id = "camp_delhi_mega_01",
                name = "BloodConnect Mega LifeSaver Drive",
                description = "Annual emergency blood drive in association with Indian Red Cross Society and Delhi Health Department. All voluntary donors receive a Certificate of Honor and health checkup.",
                ngoName = "BloodConnect NGO",
                organizer = "BloodConnect Central Operations & Red Cross",
                address = "Central Park Amphitheater, Connaught Place, New Delhi",
                city = "New Delhi",
                latitude = 28.6328,
                longitude = 77.2197,
                date = "Tomorrow",
                startTime = "09:00 AM",
                endTime = "05:00 PM",
                contactPhone = "+91 98000 11223",
                bloodGroupsNeeded = "All Blood Groups (Critical: O-, O+, B+)",
                capacity = 250,
                registrationCount = 142,
                status = CampStatus.ACTIVE,
                instructions = "Please have a light breakfast prior to donation. Carry a valid photo ID (Aadhaar / Voter ID). Drinking plenty of fluids is recommended."
            ),
            DonationCampEntity(
                id = "camp_aiims_youth_02",
                name = "AIIMS Youth Blood Donation Camp",
                description = "Joint humanitarian camp organized by AIIMS Transfusion Department and BloodConnect volunteers for thalassemia and trauma patients.",
                ngoName = "BloodConnect NGO",
                organizer = "AIIMS Doctors Association",
                address = "JLN Auditorium Foyer, AIIMS Campus, Ansari Nagar",
                city = "New Delhi",
                latitude = 28.5672,
                longitude = 77.2100,
                date = "Saturday, 10:00 AM",
                startTime = "10:00 AM",
                endTime = "04:30 PM",
                contactPhone = "+91 11 2658 8500",
                bloodGroupsNeeded = "O+, A+, B+, AB+",
                capacity = 180,
                registrationCount = 68,
                status = CampStatus.UPCOMING,
                instructions = "Age limit: 18-65 years. Minimum weight: 45 kg. Hemoglobin checkup will be provided on spot."
            ),
            DonationCampEntity(
                id = "camp_gurugram_cyber_03",
                name = "DLF CyberCity LifeStream Drive",
                description = "Corporate donation camp supporting regional emergency trauma centers and maternity wards in NCR.",
                ngoName = "BloodConnect NGO",
                organizer = "BloodConnect NCR Chapter & DLF Foundation",
                address = "Building 10 Courtyard, Cyber City, Phase II, Gurugram",
                city = "Gurugram",
                latitude = 28.4950,
                longitude = 77.0895,
                date = "Sunday",
                startTime = "09:30 AM",
                endTime = "04:00 PM",
                contactPhone = "+91 98555 66778",
                bloodGroupsNeeded = "All Blood Groups",
                capacity = 300,
                registrationCount = 210,
                status = CampStatus.UPCOMING,
                instructions = "Express registration available for pre-registered donors. Medical refreshment kits provided."
            ),
            DonationCampEntity(
                id = "camp_south_ex_04",
                name = "South Extension Community Donation Drive",
                description = "Community-led blood donation initiative focusing on negative blood types and rare units.",
                ngoName = "BloodConnect NGO",
                organizer = "South Delhi Citizen Welfare & BloodConnect",
                address = "Community Center Hall, South Extension Part 2, New Delhi",
                city = "New Delhi",
                latitude = 28.5690,
                longitude = 77.2215,
                date = "Next Monday",
                startTime = "10:00 AM",
                endTime = "03:00 PM",
                contactPhone = "+91 99223 34455",
                bloodGroupsNeeded = "O-, A-, B-, AB- (Negative Types Priority)",
                capacity = 120,
                registrationCount = 45,
                status = CampStatus.UPCOMING,
                instructions = "Exclusive consultation with hematology experts. Donors will receive digital certificate."
            )
        )
        db.donationCampDao().insertCamps(camps)

        // 6. Volunteers
        val volunteers = listOf(
            VolunteerEntity(
                id = "vol_01",
                userId = VOLUNTEER_USER_ID,
                name = "Ananya Deshmukh",
                phone = "+91 98555 66778",
                city = "New Delhi",
                activeAssignments = 2,
                completedCoordinations = 18,
                isAvailable = true
            ),
            VolunteerEntity(
                id = "vol_02",
                userId = "usr_karan_vol_02",
                name = "Karan Malhotra",
                phone = "+91 98777 88990",
                city = "New Delhi",
                activeAssignments = 1,
                completedCoordinations = 14,
                isAvailable = true
            ),
            VolunteerEntity(
                id = "vol_03",
                userId = "usr_riya_vol_03",
                name = "Riya Sengupta",
                phone = "+91 98333 44112",
                city = "Gurugram",
                activeAssignments = 0,
                completedCoordinations = 9,
                isAvailable = true
            )
        )
        db.volunteerDao().insertVolunteers(volunteers)

        // 7. Seed Active Emergency Requests
        val req1Id = "req_aiims_emergency_001"
        val req1 = EmergencyRequestEntity(
            id = req1Id,
            patientName = "Meera Kapoor (Age 34)",
            requesterUserId = "usr_family_meera",
            requesterPhone = "+91 98100 44556",
            bloodGroup = "O+",
            unitsRequired = 2,
            hospitalName = "AIIMS Trauma Centre",
            hospitalAddress = "Sri Aurobindo Marg, Ansari Nagar, New Delhi",
            latitude = 28.5672,
            longitude = 77.2100,
            urgency = RequestUrgency.CRITICAL,
            requiredDate = "Today",
            requiredTime = "Immediately (Within 2 Hours)",
            attendantPhone = "+91 98100 44556",
            additionalNotes = "Emergency trauma surgery underway. Patient urgently requires 2 units of whole blood.",
            status = RequestStatus.FINDING_DONORS,
            verificationStatus = "APPROVED",
            verifiedByAdminId = ADMIN_USER_ID,
            assignedVolunteerId = "vol_01",
            assignedVolunteerName = "Ananya Deshmukh",
            currentRadiusKm = 5.0,
            matchedDonorsCount = 3,
            acceptedDonorsCount = 1
        )
        db.emergencyRequestDao().insertRequest(req1)

        // Status history for req 1
        db.emergencyStatusHistoryDao().insertHistory(
            EmergencyStatusHistoryEntity(
                requestId = req1Id,
                status = RequestStatus.SUBMITTED,
                title = "Emergency Request Submitted",
                description = "Request submitted for 2 units O+ at AIIMS Trauma Centre.",
                actorName = "Attendant (Family)",
                timestamp = System.currentTimeMillis() - 3600000
            )
        )
        db.emergencyStatusHistoryDao().insertHistory(
            EmergencyStatusHistoryEntity(
                requestId = req1Id,
                status = RequestStatus.VERIFIED,
                title = "Verified by BloodConnect NGO",
                description = "Medical requirement and hospital admission verified by Dr. Swaminathan.",
                actorName = "Dr. Arvind Swaminathan (NGO Admin)",
                timestamp = System.currentTimeMillis() - 2700000
            )
        )
        db.emergencyStatusHistoryDao().insertHistory(
            EmergencyStatusHistoryEntity(
                requestId = req1Id,
                status = RequestStatus.FINDING_DONORS,
                title = "Matching Engine Activated",
                description = "Smart Radius (0-5 km) searched. Compatible nearby donors notified.",
                actorName = "System Matching Engine",
                timestamp = System.currentTimeMillis() - 1800000
            )
        )

        // Matches for req 1
        db.donorMatchDao().insertMatches(
            listOf(
                DonorMatchEntity(
                    id = "match_001",
                    requestId = req1Id,
                    donorUserId = CURRENT_USER_ID,
                    donorName = "Rahul Sharma",
                    donorPhone = "+91 98765 43210",
                    bloodGroup = "O+",
                    distanceKm = 3.8,
                    matchScore = 98.5,
                    matchRank = 1,
                    notificationStatus = NotificationDeliveryStatus.OPENED,
                    responseStatus = DonorResponseStatus.PENDING
                ),
                DonorMatchEntity(
                    id = "match_002",
                    requestId = req1Id,
                    donorUserId = "usr_amit_verma_003",
                    donorName = "Amit Verma",
                    donorPhone = "+91 99223 34455",
                    bloodGroup = "O-",
                    distanceKm = 4.2,
                    matchScore = 95.0,
                    matchRank = 2,
                    notificationStatus = NotificationDeliveryStatus.ACCEPTED,
                    responseStatus = DonorResponseStatus.ACCEPTED,
                    respondedAt = System.currentTimeMillis() - 900000
                )
            )
        )

        // Second Request: Verification Pending
        val req2Id = "req_max_emergency_002"
        db.emergencyRequestDao().insertRequest(
            EmergencyRequestEntity(
                id = req2Id,
                patientName = "Kabir Sengupta (Age 52)",
                requesterUserId = "usr_requester_kabir",
                requesterPhone = "+91 98222 33445",
                bloodGroup = "B+",
                unitsRequired = 3,
                hospitalName = "Max Super Speciality Hospital",
                hospitalAddress = "1, 2, Press Enclave Marg, Saket",
                latitude = 28.5284,
                longitude = 77.2132,
                urgency = RequestUrgency.URGENT,
                requiredDate = "Today",
                requiredTime = "By 06:00 PM",
                attendantPhone = "+91 98222 33445",
                additionalNotes = "Scheduled cardiac procedure. Platelets & PRBC needed.",
                status = RequestStatus.VERIFICATION_PENDING,
                verificationStatus = "PENDING"
            )
        )
        db.emergencyStatusHistoryDao().insertHistory(
            EmergencyStatusHistoryEntity(
                requestId = req2Id,
                status = RequestStatus.VERIFICATION_PENDING,
                title = "Verification in Progress",
                description = "Hospital desk contacted to confirm patient registration.",
                actorName = "BloodConnect Verification Desk",
                timestamp = System.currentTimeMillis() - 1200000
            )
        )

        // 8. Notifications for current user
        db.notificationDao().insertNotification(
            AppNotificationEntity(
                targetUserId = CURRENT_USER_ID,
                title = "🚨 Emergency Blood Request Nearby",
                body = "O+ blood urgently required at AIIMS Trauma Centre (approx. 3.8 km away). Can you donate?",
                type = NotificationType.EMERGENCY_REQUEST,
                relatedId = req1Id,
                status = NotificationDeliveryStatus.OPENED
            )
        )
        db.notificationDao().insertNotification(
            AppNotificationEntity(
                targetUserId = CURRENT_USER_ID,
                title = "🩸 Upcoming Blood Donation Camp",
                body = "BloodConnect Mega LifeSaver Camp is tomorrow at Central Park, Connaught Place. Register now!",
                type = NotificationType.CAMP_ALERT,
                relatedId = "camp_delhi_mega_01",
                status = NotificationDeliveryStatus.DELIVERED
            )
        )

        // 9. Certificates for user
        db.certificateDao().insertCertificates(
            listOf(
                DonationCertificateEntity(
                    id = "cert_001",
                    certificateNumber = "BC-2025-DEL-49102",
                    donorUserId = CURRENT_USER_ID,
                    donorName = "Rahul Sharma",
                    donationDate = "15 Nov 2025",
                    hospitalOrCamp = "AIIMS Central Blood Transfusion Unit",
                    bloodGroup = "O+",
                    units = 1,
                    verificationCode = "BC-VER-984210",
                    qrCodeData = "https://bloodconnect.org/verify?cert=BC-2025-DEL-49102&donor=Rahul+Sharma&date=2025-11-15&units=1&status=VERIFIED"
                ),
                DonationCertificateEntity(
                    id = "cert_002",
                    certificateNumber = "BC-2025-DEL-18923",
                    donorUserId = CURRENT_USER_ID,
                    donorName = "Rahul Sharma",
                    donationDate = "10 Aug 2025",
                    hospitalOrCamp = "BloodConnect Independence Day Drive",
                    bloodGroup = "O+",
                    units = 1,
                    verificationCode = "BC-VER-741289",
                    qrCodeData = "https://bloodconnect.org/verify?cert=BC-2025-DEL-18923&donor=Rahul+Sharma&date=2025-08-10&units=1&status=VERIFIED"
                )
            )
        )

        // 10. Audit Log
        db.auditLogDao().insertAuditLog(
            AuditLogEntity(
                actorName = "Dr. Arvind Swaminathan",
                actorRole = "NGO_ADMIN",
                action = "VERIFY_EMERGENCY_REQUEST",
                targetEntity = "EmergencyRequest",
                targetId = req1Id,
                metadata = "Verified hospital documentation with AIIMS Trauma ICU. Matching Engine initiated for O+."
            )
        )
    }
}
