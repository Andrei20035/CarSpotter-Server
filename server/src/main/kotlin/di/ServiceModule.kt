package com.carspotter.di


import com.carspotter.data.service.auth_credential.AuthCredentialServiceImpl
import com.carspotter.data.service.auth_credential.IAuthCredentialService
import com.carspotter.data.service.car_model.CarModelServiceImpl
import com.carspotter.data.service.car_model.ICarModelService
import com.carspotter.data.service.comment.CommentServiceImpl
import com.carspotter.data.service.comment.ICommentService
import com.carspotter.data.service.post.IPostService
import com.carspotter.data.service.post.PostServiceImpl
import com.carspotter.data.service.user.IUserService
import com.carspotter.data.service.user.UserServiceImpl
import org.koin.dsl.module

val serviceModule = module {
    single<IUserService> { UserServiceImpl(get()) }
    single<ICommentService> { CommentServiceImpl(get()) }
    single<IPostService> { PostServiceImpl(get()) }
    single<ICarModelService> { CarModelServiceImpl(get()) }
    single<IAuthCredentialService> { AuthCredentialServiceImpl(get()) }
}