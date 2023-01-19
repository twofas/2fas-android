package com.twofasapp.services.domain

import com.twofasapp.base.usecase.UseCase
import com.twofasapp.prefs.model.ServiceDto
import io.reactivex.Single

interface GetServicesUseCase : UseCase<Single<List<ServiceDto>>>