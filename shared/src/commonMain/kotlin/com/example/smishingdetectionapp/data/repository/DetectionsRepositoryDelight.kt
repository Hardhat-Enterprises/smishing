package com.example.smishingdetectionapp.data.repository

import com.example.smishingdetectionapp.detections.DetectionsQueries

// SQLDelight implementation of DetectionRepository interface
class DetectionsRepositoryDelight(
    private val queries: DetectionsQueries
) : DetectionRepository {

    // Get total number of detections
    override suspend fun getDetectionCount(): Long {
        return queries.getDetectionCount().executeAsOne()
    }

    // Get all detections
    override suspend fun getAllDetections(): List<DetectionRecord> {
        return queries.getAllDetections().executeAsList().map {
            DetectionRecord(
                id = it._id,
                phoneNumber = it.Phone_Number,
                message = it.Message,
                date = it.Date
            )
        }
    }

    // Get detections matching selected date
    override suspend fun getDetectionsForDate(date: String): List<DetectionRecord> {
        return queries.getDetectionsForDate("%$date%").executeAsList().map {
            DetectionRecord(
                id = it._id,
                phoneNumber = it.Phone_Number,
                message = it.Message,
                date = it.Date
            )
        }
    }

    // Search detections by phone number/message/date
    override suspend fun searchDetections(query: String): List<DetectionRecord> {
        val search = "%$query%"
        return queries.searchDetections(search, search, search).executeAsList().map {
            DetectionRecord(
                id = it._id,
                phoneNumber = it.Phone_Number,
                message = it.Message,
                date = it.Date
            )
        }
    }

    //Get detections ordered newest first
    override suspend fun getDetectionsNewestFirst(): List<DetectionRecord> {
        return queries.getDetectionsNewestFirst().executeAsList().map {
            DetectionRecord(
                id = it._id,
                phoneNumber = it.Phone_Number,
                message = it.Message,
                date = it.Date
            )
        }
    }

    // Get detections ordered oldest first
    override suspend fun getDetectionsOldestFirst(): List<DetectionRecord> {
        return queries.getDetectionsOldestFirst().executeAsList().map {
            DetectionRecord(
                id = it._id,
                phoneNumber = it.Phone_Number,
                message = it.Message,
                date = it.Date
            )
        }
    }

    // Delete detection by ID
    override suspend fun deleteDetectionById(id: Long) {
        queries.deleteDetectionById(id)
    }

    // Get available years from detection dates
    override suspend fun getAvailableDetectionYears(): List<String> {
        return queries.getAvailableDetectionYears().executeAsList().mapNotNull { it.year }
    }

    // Save user-submitted report and return the inserted row ID
    override suspend fun insertReport(phoneNumber: Long, message: String, date: String): Long {
        queries.insertReport(phoneNumber, message, date)
        return queries.getLastInsertRowId().executeAsOne()
    }

    //Get all reports
    override suspend fun getAllReports(): List<ReportRecord> {
        return queries.getAllReports().executeAsList().map {
            ReportRecord(
                phoneNumber = it.Phone_Number,
                message = it.Message,
                date = it.Date
            )
        }
    }

    // Get reports matching selected date
    override suspend fun getReportsForDate(date: String): List<ReportRecord> {
        return queries.getReportsForDate("%$date%").executeAsList().map {
            ReportRecord(
                phoneNumber = it.Phone_Number,
                message = it.Message,
                date = it.Date
            )
        }
    }

    // Get reports ordered by newest first
    override suspend fun getReportsNewestFirst(): List<ReportRecord> {
        return queries.getReportsNewestFirst().executeAsList().map {
            ReportRecord(
                phoneNumber = it.Phone_Number,
                message = it.Message,
                date = it.Date
            )
        }
    }

    // Get reports ordered oldest first
    override suspend fun getReportsOldestFirst(): List<ReportRecord> {
        return queries.getReportsOldestFirst().executeAsList().map {
            ReportRecord(
                phoneNumber = it.Phone_Number,
                message = it.Message,
                date = it.Date
            )
        }
    }

    // Search reports by phone number/message/date
    override suspend fun searchReports(query: String): List<ReportRecord> {
        val search = "%$query%"
        return queries.searchReports(search, search, search).executeAsList().map {
            ReportRecord(
                phoneNumber = it.Phone_Number,
                message = it.Message,
                date = it.Date
            )
        }
    }

    //Delete report by phone number and message
    override suspend fun deleteReport(phoneNumber: Long, message: String) {
        queries.deleteReport(phoneNumber, message)
    }

    //Delete reports linked to phone number
    override suspend fun deleteReportByPhoneNumber(phoneNumber: Long) {
        queries.deleteReportByPhoneNumber(phoneNumber)
    }

    // Get total number of reports
    override suspend fun getReportCount(): Long {
        return queries.getReportCount().executeAsOne()
    }
}