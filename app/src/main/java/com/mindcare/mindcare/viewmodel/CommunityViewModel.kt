package com.mindcare.mindcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindcare.mindcare.community.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class CommunityViewModel : ViewModel() {
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts
    
    private val _currentUser = MutableStateFlow<CommunityUser?>(null)
    val currentUser: StateFlow<CommunityUser?> = _currentUser
    
    private val _supportGroups = MutableStateFlow<List<SupportGroup>>(emptyList())
    val supportGroups: StateFlow<List<SupportGroup>> = _supportGroups

    private val _selectedPost = MutableStateFlow<Post?>(null)
    val selectedPost: StateFlow<Post?> = _selectedPost

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments

    init {
        loadPosts()
        loadCurrentUser()
        loadSupportGroups()
    }

    fun createPost(
        content: String,
        mood: String? = null,
        imageUrl: String? = null,
        tags: List<String> = emptyList(),
        isAnonymous: Boolean = false
    ) {
        viewModelScope.launch {
            val currentUser = _currentUser.value ?: return@launch
            
            val newPost = Post(
                userId = if (isAnonymous) "anonymous" else currentUser.id,
                userName = if (isAnonymous) "Anónimo" else currentUser.name,
                userAvatar = if (isAnonymous) null else currentUser.avatar,
                content = content,
                mood = mood,
                imageUrl = imageUrl,
                tags = tags,
                timestamp = Date(),
                isAnonymous = isAnonymous
            )

            // Aquí se implementará la persistencia en Room
            _posts.value = listOf(newPost) + _posts.value
        }
    }

    fun likePost(postId: Long) {
        viewModelScope.launch {
            val updatedPosts = _posts.value.map { post ->
                if (post.id == postId) post.copy(likes = post.likes + 1)
                else post
            }
            _posts.value = updatedPosts
        }
    }

    fun addComment(postId: Long, content: String, isAnonymous: Boolean = false) {
        viewModelScope.launch {
            val currentUser = _currentUser.value ?: return@launch
            
            val newComment = Comment(
                postId = postId,
                userId = if (isAnonymous) "anonymous" else currentUser.id,
                userName = if (isAnonymous) "Anónimo" else currentUser.name,
                userAvatar = if (isAnonymous) null else currentUser.avatar,
                content = content,
                timestamp = Date(),
                isAnonymous = isAnonymous
            )

            _comments.value = listOf(newComment) + _comments.value
            
            // Actualizar el contador de comentarios del post
            val updatedPosts = _posts.value.map { post ->
                if (post.id == postId) post.copy(comments = post.comments + 1)
                else post
            }
            _posts.value = updatedPosts
        }
    }

    fun selectPost(post: Post) {
        _selectedPost.value = post
        loadComments(post.id)
    }

    fun clearSelectedPost() {
        _selectedPost.value = null
        _comments.value = emptyList()
    }

    private fun loadPosts() {
        // Aquí se implementará la carga desde Room
        // Por ahora, datos de ejemplo
        _posts.value = generateSamplePosts()
    }

    private fun loadCurrentUser() {
        // Aquí se implementará la carga del usuario actual
        _currentUser.value = CommunityUser(
            id = "user123",
            name = "María García",
            avatar = null,
            bio = "Buscando paz interior y crecimiento personal",
            joinDate = Date(),
            badges = listOf(
                Badge(
                    id = "badge1",
                    name = "Apoyo Activo",
                    icon = "🌟",
                    description = "Ayudó a 10 personas",
                    category = BadgeCategory.SUPPORT
                )
            ),
            stats = UserStats(
                postsCount = 15,
                commentsCount = 45,
                likesGiven = 78,
                likesReceived = 123,
                supportScore = 0.85f
            )
        )
    }

    private fun loadSupportGroups() {
        _supportGroups.value = listOf(
            SupportGroup(
                id = "group1",
                name = "Mindfulness Diario",
                description = "Grupo para practicar mindfulness juntos",
                category = GroupCategory.MINDFULNESS,
                members = 156,
                posts = 478,
                isPrivate = false,
                moderators = listOf("user123", "user456")
            )
            // Más grupos...
        )
    }

    private fun loadComments(postId: Long) {
        // Aquí se implementará la carga desde Room
        _comments.value = generateSampleComments(postId)
    }

    private fun generateSamplePosts(): List<Post> {
        return listOf(
            Post(
                id = 1,
                userId = "user456",
                userName = "Juan Pérez",
                userAvatar = null,
                content = "Hoy me siento más tranquilo después de meditar",
                mood = "😊",
                imageUrl = null,
                tags = listOf("meditación", "bienestar", "paz"),
                timestamp = Date(),
                likes = 5,
                comments = 2
            )
            // Más posts...
        )
    }

    private fun generateSampleComments(postId: Long): List<Comment> {
        return listOf(
            Comment(
                id = 1,
                postId = postId,
                userId = "user789",
                userName = "Ana López",
                userAvatar = null,
                content = "¡Me alegro por ti! La meditación es transformadora",
                timestamp = Date(),
                likes = 2
            )
            // Más comentarios...
        )
    }
} 