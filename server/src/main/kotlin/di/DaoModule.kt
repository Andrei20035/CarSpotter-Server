package com.carspotter.di
import com.carspotter.data.dao.auth_credential.AuthCredentialDaoImpl
import com.carspotter.data.dao.car_model.CarModelDaoImpl
import com.carspotter.data.dao.comment.CommentDaoImpl
import com.carspotter.data.dao.friend.FriendDaoImpl
import com.carspotter.data.dao.friend_request.FriendRequestDaoImpl
import com.carspotter.data.dao.like.LikeDaoImpl
import com.carspotter.data.dao.post.PostDaoImpl
import com.carspotter.data.dao.user.UserDaoImpl
import com.carspotter.data.dao.user_car.UserCarDaoImpl
import org.koin.dsl.module

val daoModule = module {
    single { UserDaoImpl() }
    single { CommentDaoImpl() }
    single { PostDaoImpl() }
    single { CarModelDaoImpl() }
    single { AuthCredentialDaoImpl() }
    single { FriendDaoImpl() }
    single { LikeDaoImpl() }
    single { FriendRequestDaoImpl() }
    single { UserCarDaoImpl() }
}