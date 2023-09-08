package com.abrnoc.application.di

import com.abrnoc.application.repository.remote.DefaultConfigApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
//    @Singleton
//    @Provides
//    fun provideApplicationContext(@ApplicationContext context: Context): Context = context

//    @Provides
//    @Singleton
//    fun provideOkHttp(): OkHttpClient {
//        return OkHttpClient.Builder()
//            .apply {
//            addInterceptor(
//                LoggingInterceptor.Builder()
//                    .setLevel(Level.BASIC)
//                    .log(Platform.INFO)
//                    .request("LOG")
//                    .response("LOG")
//                    .build(),
//            )
//        }.hostnameVerifier(HostnameVerifier { hostname, session -> true })
//            .build()
//
//    }
//
//    @Provides
//    @Singleton
//    fun provideRetrofitConnection(
//        okHttpClient: OkHttpClient,
//    ): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl("https://172.86.76.146:8080/")  //https://172.86.76.146:8080/
//            .client(okHttpClient)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }

    @Provides
    @Singleton
    fun provideGithubUsersApi(retrofit: Retrofit): DefaultConfigApi {
        return retrofit.create(DefaultConfigApi::class.java)
    }
}
