package com.narcis.application.presentation.mapper

import com.narcis.application.presentation.viewModel.state.model.SignInObj
import com.narcis.domain.model.SignIn

fun SignIn.mapToPresentation() = SignInObj(this.email, this.password)

fun SignInObj.mapToDomain() = SignIn(this.email, this.password)