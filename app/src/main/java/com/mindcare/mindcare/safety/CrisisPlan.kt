class CrisisPlan {
    fun generatePlan(userProfile: UserProfile): SafetyPlan {
        return SafetyPlan(
            emergencyContacts = userProfile.emergencyContacts,
            copingStrategies = personalizeCopingStrategies(userProfile),
            resources = getNearbyResources(userProfile.location)
        )
    }
} 