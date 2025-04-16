package com.carspotter.di
import com.carspotter.data.dao.auth_credential.AuthCredentialDaoImpl
import com.carspotter.data.dao.auth_credentials.IAuthCredentialDAO
import com.carspotter.data.dao.car_model.CarModelDaoImpl
import com.carspotter.data.dao.car_model.ICarModelDAO
import com.carspotter.data.dao.comment.CommentDaoImpl
import com.carspotter.data.dao.comment.ICommentDAO
import com.carspotter.data.dao.friend.FriendDaoImpl
import com.carspotter.data.dao.friend.IFriendDAO
import com.carspotter.data.dao.friend_request.FriendRequestDaoImpl
import com.carspotter.data.dao.friend_request.IFriendRequestDAO
import com.carspotter.data.dao.like.ILikeDAO
import com.carspotter.data.dao.like.LikeDaoImpl
import com.carspotter.data.dao.post.IPostDAO
import com.carspotter.data.dao.post.PostDaoImpl
import com.carspotter.data.dao.user.IUserDAO
import com.carspotter.data.dao.user.UserDaoImpl
import com.carspotter.data.dao.user_car.IUserCarDAO
import com.carspotter.data.dao.user_car.UserCarDaoImpl
import org.koin.dsl.module

val daoModule = module {
    single<IUserDAO> { UserDaoImpl() }
    single<ICommentDAO> { CommentDaoImpl() }
    single<IPostDAO> { PostDaoImpl() }
    single<ICarModelDAO> { CarModelDaoImpl() }
    single<IAuthCredentialDAO> { AuthCredentialDaoImpl() }
    single<IFriendDAO> { FriendDaoImpl() }
    single<ILikeDAO> { LikeDaoImpl() }
    single<IFriendRequestDAO> { FriendRequestDaoImpl() }
    single<IUserCarDAO> { UserCarDaoImpl() }
}