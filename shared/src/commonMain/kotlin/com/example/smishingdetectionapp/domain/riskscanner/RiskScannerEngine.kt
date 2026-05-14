package com.example.smishingdetectionapp.domain.riskscanner

// Logic for calculating risk scores
// Platform-specific checks are injected via RiskCheckProvider
object RiskScannerEngine {

    // Age-based risk scoring
    fun calculateAgeGroupRisk(age: Int): Int {
        return when {
            age in 18..24 -> 15
            age in 25..34 -> 10
            else -> 0
        }
    }

    fun scanHabits(
        provider: RiskCheckProvider,
        disableSmsRisk: Boolean = false,
        disableAgeRisk: Boolean = false,
        disableSecurityRisk: Boolean = false,
        userAge: Int = 0
    ): RiskScore {
        val results = mutableListOf<RiskCheckResult>()
        var totalScore = 0

        // Age risk
        if (!disableAgeRisk) {
            val ageRisk = calculateAgeGroupRisk(userAge)
            results.add(RiskCheckResult(
                name = "Age Group",
                passed = ageRisk == 0,
                riskPoints = ageRisk,
                failureMessage = "You are in a higher risk age group."
            ))
            totalScore += ageRisk
        } else {
            results.add(RiskCheckResult(
                name = "Age Group",
                passed = true,
                riskPoints = 0,
                failureMessage = ""
            ))
        }

// SMS behaviour
        if (!disableSmsRisk) {
            val smsRisk = provider.hasSuspiciousSms()
            results.add(RiskCheckResult(
                name = "SMS Behaviour",
                passed = !smsRisk,
                riskPoints = if (smsRisk) 15 else 0,
                failureMessage = "Some SMS messages on your device appear to be potentially suspicious."
            ))
            totalScore += if (smsRisk) 15 else 0
        } else {
            results.add(RiskCheckResult(
                name = "SMS Behaviour",
                passed = true,
                riskPoints = 0,
                failureMessage = ""
            ))
        }

        // Security checks
        if (!disableSecurityRisk) {
            val hasSecurityApp = provider.hasSecurityApp()
            results.add(RiskCheckResult(
                name = "Security App",
                passed = hasSecurityApp,
                riskPoints = if (!hasSecurityApp) 14 else 0,
                failureMessage = "No trusted security apps were found on your device."
            ))
            totalScore += if (!hasSecurityApp) 14 else 0

            val hasSpamFilter = provider.hasSpamFilter()
            val spamFilterRisk = if (hasSpamFilter) 4 else 10

            results.add(RiskCheckResult(
                name = "Spam Filter",
                passed = hasSpamFilter,
                riskPoints = spamFilterRisk,
                failureMessage = if (hasSpamFilter) {
                    "A spam filter app was detected, but this only provides partial protection."
                } else {
                    "No spam filter was detected on your device."
                }
            ))

            totalScore += spamFilterRisk

            val isDeviceSecured = provider.isDeviceSecured()
            results.add(RiskCheckResult(
                name = "Device Lock",
                passed = isDeviceSecured,
                riskPoints = if (!isDeviceSecured) 14 else 0,
                failureMessage = "Your device has no lock screen (PIN or password)."
            ))
            totalScore += if (!isDeviceSecured) 14 else 0
        } else {
            results.add(RiskCheckResult("Security App", true, 0, ""))
            results.add(RiskCheckResult("Spam Filter", true, 0, ""))
            results.add(RiskCheckResult("Device Lock", true, 0, ""))
        }

        // Unknown sources
        val unknownSources = provider.hasUnknownSourcesEnabled()
        results.add(RiskCheckResult(
            name = "Unknown Sources",
            passed = !unknownSources,
            riskPoints = if (unknownSources) 14 else 0,
            failureMessage = "Your device allows app installations from unknown sources."
        ))
        totalScore += if (unknownSources) 14 else 0

        // Trusted SMS app
        val trustedSms = provider.hasTrustedSmsApp()
        results.add(RiskCheckResult(
            name = "SMS App",
            passed = trustedSms,
            riskPoints = if (!trustedSms) 14 else 0,
            failureMessage = "Your current SMS app may not be from a trusted provider."
        ))
        totalScore += if (!trustedSms) 14 else 0

        val cappedScore = totalScore.coerceAtMost(100)
        val triggeredRisks = results
            .filter { !it.passed && it.riskPoints > 0 }
            .map { it.failureMessage }

        return RiskScore(
            totalScore = cappedScore,
            triggeredRisks = triggeredRisks,
            checkResults = results
        )
    }
}
