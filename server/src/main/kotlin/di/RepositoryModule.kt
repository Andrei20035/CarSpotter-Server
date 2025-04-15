package com.carspotter.di


import com.carspotter.data.repository.auth_credential.IAuthCredentialRepository
import com.carspotter.data.repository.auth_credentials.AuthCredentialRepositoryImpl
import com.carspotter.data.repository.car_model.CarModelRepositoryImpl
import com.carspotter.data.repository.car_model.ICarModelRepository
import com.carspotter.data.repository.comment.CommentRepositoryImpl
import com.carspotter.data.repository.comment.ICommentRepository
import com.carspotter.data.repository.friend.FriendRepositoryImpl
import com.carspotter.data.repository.friend.IFriendRepository
import com.carspotter.data.repository.friend_request.FriendRequestRepositoryImpl
import com.carspotter.data.repository.friend_request.IFriendRequestRepository
import com.carspotter.data.repository.like.ILikeRepository
import com.carspotter.data.repository.like.LikeRepositoryImpl
import com.carspotter.data.repository.post.IPostRepository
import com.carspotter.data.repository.post.PostRepositoryImpl
import com.carspotter.data.repository.user.IUserRepository
import com.carspotter.data.repository.user.UserRepositoryImpl
import com.carspotter.data.repository.user_car.IUserCarRepository
import com.carspotter.data.repository.user_car.UserCarRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<IUserRepository> { UserRepositoryImpl(get()) }
    single<ICommentRepository> { CommentRepositoryImpl(get()) }
    single<IPostRepository> { PostRepositoryImpl(get()) }
    single<ICarModelRepository> { CarModelRepositoryImpl(get()) }
    single<IAuthCredentialRepository> { AuthCredentialRepositoryImpl(get()) }
    single<IFriendRepository> { FriendRepositoryImpl(get()) }
    single<IFriendRequestRepository> { FriendRequestRepositoryImpl(get())}
    single<ILikeRepository> { LikeRepositoryImpl(get())}
    single<IUserCarRepository> { UserCarRepositoryImpl(get())}
}