package com.mindcare.mindcare.community.moderation

import com.mindcare.mindcare.community.models.CommunityPost
import com.mindcare.mindcare.community.models.Comment

class ContentModerator {
    private val inappropriateWords = setOf(
        // Lista de palabras inapropiadas
    )

    private val triggerWords = setOf(
        "suicidio", "autolesión", "crisis"
        // Más palabras que requieren atención inmediata
    )

    fun moderatePost(post: CommunityPost): ModerationResult {
        val containsInappropriate = containsInappropriateContent(post.content)
        val containsTriggers = containsTriggerWords(post.content)

        return when {
            containsTriggers -> ModerationResult(
                isApproved = false,
                requiresReview = true,
                triggerWarning = true,
                suggestedResources = getSupportResources()
            )
            containsInappropriate -> ModerationResult(
                isApproved = false,
                requiresReview = true,
                triggerWarning = false,
                message = "El contenido contiene lenguaje inapropiado"
            )
            else -> ModerationResult(
                isApproved = true,
                requiresReview = false
            )
        }
    }

    private fun containsInappropriateContent(text: String): Boolean {
        return inappropriateWords.any { text.toLowerCase().contains(it) }
    }

    private fun containsTriggerWords(text: String): Boolean {
        return triggerWords.any { text.toLowerCase().contains(it) }
    }

    private fun getSupportResources(): List<SupportResource> {
        return listOf(
            SupportResource(
                "Línea de Crisis",
                "1-800-XXX-XXXX",
                "24/7 Apoyo inmediato"
            ),
            SupportResource(
                "Chat de Ayuda",
                "www.ayuda.org",
                "Chat en línea con profesionales"
            )
        )
    }
}

data class ModerationResult(
    val isApproved: Boolean,
    val requiresReview: Boolean,
    val triggerWarning: Boolean = false,
    val message: String? = null,
    val suggestedResources: List<SupportResource> = emptyList()
)

data class SupportResource(
    val name: String,
    val contact: String,
    val description: String
) 