package com.example.smishingdetectionapp.data.repository

// Repository interface for detection and user-submitted report operations
//Pre SQLDelight implementations
interface DetectionRepository {

    // Get total number of detections
    suspend fun getDetectionCount(): Long

    // Get all detections
    suspend fun getAllDetections(): List<DetectionRecord>

    // Get detections matching a selected date
    suspend fun getDetectionsForDate(date: String): List<DetectionRecord>

    // Search detections by phone number, message or date
    suspend fun searchDetections(query: String): List<DetectionRecord>

    // Get detections ordered newest first
    suspend fun getDetectionsNewestFirst(): List<DetectionRecord>

    // Get detections ordered oldest first
    suspend fun getDetectionsOldestFirst(): List<DetectionRecord>

    // Delete a detection by ID
    suspend fun deleteDetectionById(id: Long)

    // Get available years from stored detection dates
    suspend fun getAvailableDetectionYears(): List<String>

    // Save a user-submitted report
    suspend fun insertReport(phoneNumber: Long, message: String, date: String): Long

    // Get all user-submitted reports
    suspend fun getAllReports(): List<ReportRecord>

    // Get reports matching a selected date
    suspend fun getReportsForDate(date: String): List<ReportRecord>

    // Get reports ordered newest first
    suspend fun getReportsNewestFirst(): List<ReportRecord>

    // Get reports ordered oldest first
    suspend fun getReportsOldestFirst(): List<ReportRecord>

    // Search reports by phone number, message or date
    suspend fun searchReports(query: String): List<ReportRecord>

    // Delete a report by phone number and message
    suspend fun deleteReport(phoneNumber: Long, message: String)

    // Delete reports linked to a phone number
    suspend fun deleteReportByPhoneNumber(phoneNumber: Long)

    // Get total number of reports
    suspend fun getReportCount(): Long
}

// Shared model for a detected smishing message record
data class DetectionRecord(
    val id: Long,
    val phoneNumber: Long?,
    val message: String?,
    val date: String?
)

// Shared model for a user-submitted report record
data class ReportRecord(
    val phoneNumber: Long?,
    val message: String?,
    val date: String?
)