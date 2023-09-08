package com.abrnoc.application.presentation.mapper

import com.abrnoc.application.presentation.viewModel.state.model.SignInObj
import com.abrnoc.domain.model.SignIn

fun SignIn.mapToPresentation() = SignInObj(this.email, this.password)

fun SignInObj.mapToDomain() = SignIn(this.email, this.password)