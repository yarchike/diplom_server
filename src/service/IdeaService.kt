package com.martynov.service

import com.martynov.repository.IdeaRepository
import com.martynov.repository.UserRepository

class IdeaService(
    val repoUser: UserRepository,
    val repo: IdeaRepository
) {
}