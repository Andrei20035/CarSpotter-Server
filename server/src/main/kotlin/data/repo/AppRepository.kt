package com.carspotter.data.repo

import com.carspotter.data.model.*

interface AppRepository {

    // User methods
    suspend fun createUser(user: User): Int
    suspend fun getUserByID(userId: Int): User?
    suspend fun getUserByUsername(username: String): User?
    suspend fun getAllUsers(): List<User>
    suspend fun updateProfilePicture(userId: Int, imagePath: String)
    suspend fun deleteUser(userId: Int)

    // CarModel methods
    suspend fun createCarModel(carModel: CarModel): Int
    suspend fun getCarModel(carModelId: Int): CarModel
    suspend fun getAllCarModels(): List<CarModel>
    suspend fun deleteCarModel(carModelId: Int)

    // UserCar methods
    suspend fun createUserCar(userCar: UserCar): Int
    suspend fun getUserCarById(userCarId: Int): CarModel?
    suspend fun updateUserCar(userId: Int, imagePath: String, carModelId: Int)
    suspend fun deleteUserCar(userId: Int)
    suspend fun getAllUserCars(): List<UserCar>

    // Post methods
    suspend fun createPost(post: Post): Int
    suspend fun getPostById(postId: Int): Post?
    suspend fun getAllPosts(): List<Post>
    suspend fun getCurrentDayPostsForUser(userId: Int): List<Post>
    suspend fun deletePost(postId: Int)

    // Friend methods
    suspend fun getAllFriends(userId: Int): List<User>
    suspend fun addFriend(userId: Int, friendId: Int)
    suspend fun deleteFriend(userId: Int, friendId: Int)

    // FriendRequest methods
    suspend fun sendFriendRequest(senderId: Int, receiverId: Int)
    suspend fun acceptFriendRequest(senderId: Int, receiverId: Int)
    suspend fun declineFriendRequest(senderId: Int, receiverId: Int)
    suspend fun getAllFriendRequests(userId: Int): List<User>

    // Like methods
    suspend fun likePost(userId: Int, postId: Int)
    suspend fun unlikePost(userId: Int, postId: Int)
    suspend fun getLikesForPost(postId: Int): List<User>

    // Comments Methods
    suspend fun addComment(userId: Int, postId: Int, commentText: String)
    suspend fun removeComment(commentId: Int)
    suspend fun getCommentsForPost(postId: Int): List<Comment>
}