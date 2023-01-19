package com.twofasapp.services.domain

import com.twofasapp.base.usecase.UseCaseParameterized
import com.twofasapp.prefs.model.ServiceDto
import io.reactivex.Completable

interface DeleteServiceUseCase : UseCaseParameterized<ServiceDto, Completable>