package com.twofasapp.data.services.exceptions

import com.twofasapp.data.services.domain.BackupContent

class ImportNoPassword(val backupContent: BackupContent) : RuntimeException()