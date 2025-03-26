package com.carspotter.data.repo

import com.carspotter.data.model.*

class PostgresAppRepository : AppRepository {
    override suspend fun createUser(user: User): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getUserByID(userId: Int): User? {
        TODO("Not yet implemented")
    }

    override suspend fun getUserByUsername(username: String): User? {
        TODO("Not yet implemented")
    }

    override suspend fun getAllUsers(): List<User> {
        TODO("Not yet implemented")
    }

    override suspend fun updateProfilePicture(userId: Int, imagePath: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUser(userId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun createCarModel(carModel: CarModel): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getCarModel(carModelId: Int): CarModel {
        TODO("Not yet implemented")
    }

    override suspend fun getAllCarModels(): List<CarModel> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCarModel(carModelId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun createUserCar(userCar: UserCar): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getUserCarById(userCarId: Int): CarModel? {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserCar(userId: Int, imagePath: String, carModelId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUserCar(userId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllUserCars(): List<UserCar> {
        TODO("Not yet implemented")
    }

    override suspend fun createPost(post: Post): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getPostById(postId: Int): Post? {
        TODO("Not yet implemented")
    }

    override suspend fun getAllPosts(): List<Post> {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentDayPostsForUser(userId: Int): List<Post> {
        TODO("Not yet implemented")
    }

    override suspend fun deletePost(postId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllFriends(userId: Int): List<User> {
        TODO("Not yet implemented")
    }

    override suspend fun addFriend(userId: Int, friendId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFriend(userId: Int, friendId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun sendFriendRequest(senderId: Int, receiverId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun acceptFriendRequest(senderId: Int, receiverId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun declineFriendRequest(senderId: Int, receiverId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllFriendRequests(userId: Int): List<User> {
        TODO("Not yet implemented")
    }

    override suspend fun likePost(userId: Int, postId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun unlikePost(userId: Int, postId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun getLikesForPost(postId: Int): List<User> {
        TODO("Not yet implemented")
    }

    override suspend fun addComment(userId: Int, postId: Int, commentText: String) {
        TODO("Not yet implemented")
    }

    override suspend fun removeComment(commentId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun getCommentsForPost(postId: Int): List<Comment> {
        TODO("Not yet implemented")
    }

}