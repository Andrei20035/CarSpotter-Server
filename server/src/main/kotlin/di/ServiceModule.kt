package com.carspotter.di

import com.carspotter.data.service.auth_credential.AuthCredentialServiceImpl
import com.carspotter.data.service.auth_credential.GoogleTokenVerifier
import com.carspotter.data.service.auth_credential.GoogleTokenVerifierImpl
import com.carspotter.data.service.auth_credential.IAuthCredentialService
import com.carspotter.data.service.car_model.CarModelServiceImpl
import com.carspotter.data.service.car_model.ICarModelService
import com.carspotter.data.service.comment.CommentServiceImpl
import com.carspotter.data.service.comment.ICommentService
import com.carspotter.data.service.friend.FriendServiceImpl
import com.carspotter.data.service.friend.IFriendService
import com.carspotter.data.service.friend_request.FriendRequestServiceImpl
import com.carspotter.data.service.friend_request.IFriendRequestService
import com.carspotter.data.service.like.ILikeService
import com.carspotter.data.service.like.LikeServiceImpl
import com.carspotter.data.service.post.IPostService
import com.carspotter.data.service.post.PostServiceImpl
import com.carspotter.data.service.user.IUserService
import com.carspotter.data.service.user.UserServiceImpl
import com.carspotter.data.service.user_car.IUserCarService
import com.carspotter.data.service.user_car.UserCarServiceImpl
import org.koin.dsl.module

val serviceModule = module {
    single<GoogleTokenVerifier> { GoogleTokenVerifierImpl() }
    single<IUserService> { UserServiceImpl(get()) }
    single<ICommentService> { CommentServiceImpl(get()) }
    single<IPostService> { PostServiceImpl(get()) }
    single<ICarModelService> { CarModelServiceImpl(get()) }
    single<IAuthCredentialService> { AuthCredentialServiceImpl(get(), get()) }
    single<IFriendService> { FriendServiceImpl(get()) }
    single<IFriendRequestService> { FriendRequestServiceImpl(get()) }
    single<ILikeService> { LikeServiceImpl(get()) }
    single<IUserCarService> { UserCarServiceImpl(get())}
}