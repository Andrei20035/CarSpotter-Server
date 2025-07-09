package com.carspotter.data.repository.post

import com.carspotter.data.dao.friend.IFriendDAO
import com.carspotter.data.dao.post.IPostDAO
import com.carspotter.data.dto.CreatePostDTO
import com.carspotter.data.dto.response.FeedResponse
import com.carspotter.data.dto.toDTO
import com.carspotter.data.model.FeedCursor
import com.carspotter.data.model.Post
import com.carspotter.data.model.toCursor
import java.time.Instant
import java.util.*

class PostRepositoryImpl(
    private val postDao: IPostDAO,
    private val friendDao: IFriendDAO,
) : IPostRepository {
    override suspend fun createPost(createPostDTO: CreatePostDTO): UUID {
        return postDao.createPost(createPostDTO)
    }

    override suspend fun getPostById(postId: UUID): Post? {
        return postDao.getPostById(postId)
    }

    override suspend fun getAllPosts(): List<Post> {
        return postDao.getAllPosts()
    }

    override suspend fun getCurrentDayPostsForUser(userId: UUID, startOfDay: Instant, endOfDay: Instant): List<Post> {
        return postDao.getCurrentDayPostsForUser(userId, startOfDay, endOfDay)
    }


    override suspend fun editPost(postId: UUID, postText: String?): Int {
        return postDao.editPost(postId, postText)
    }

    override suspend fun deletePost(postId: UUID): Int {
        return postDao.deletePost(postId)
    }

    override suspend fun getUserIdByPost(postId: UUID): UUID {
        return postDao.getUserIdByPost(postId)
    }

    override suspend fun getFeedPostsForUser(
        userId: UUID,
        latitude: Double?,
        longitude: Double?,
        radiusKm: Int?,
        country: String,
        limit: Int,
        cursor: FeedCursor?
    ): FeedResponse {
        val friendsIds = friendDao.getFriendIdsForUser(userId)
        val collectedPosts = mutableListOf<Post>()
        var remaining = limit
        var currentCursor = cursor

        // Friend posts
        if (remaining > 0) {
            val friendPosts = postDao.getFriendPosts(friendsIds, cursor?.lastCreatedAt, remaining)
            collectedPosts.addAll(friendPosts)
            remaining -= friendPosts.size
            currentCursor = friendPosts.lastOrNull()?.toCursor() ?: currentCursor
        }

        // Nearby posts
        if (remaining > 0 && latitude != null && longitude != null && radiusKm != null) {
            val nearbyPosts = postDao.getNearbyPosts(friendsIds + userId, latitude, longitude, radiusKm, currentCursor?.lastCreatedAt, remaining)
            collectedPosts.addAll(nearbyPosts)
            remaining -= nearbyPosts.size
            currentCursor = nearbyPosts.lastOrNull()?.toCursor() ?: currentCursor
        }

        // Global posts
        if (remaining > 0) {
            val globalPosts = postDao.getGlobalPosts(friendsIds + userId, currentCursor?.lastCreatedAt, remaining)
            collectedPosts.addAll(globalPosts)
            currentCursor = globalPosts.lastOrNull()?.toCursor() ?: currentCursor
        }

        val hasMore = collectedPosts.size == limit

        return FeedResponse(
            posts = collectedPosts.toDTO(),
            nextCursor = if (hasMore) currentCursor else null,
            hasMore = hasMore
        )
    }
}