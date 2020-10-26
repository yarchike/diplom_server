package com.martynov.service

import com.google.gson.Gson
import com.martynov.FILE_LOG
import com.martynov.FILE_USER
import com.martynov.dto.*
import com.martynov.exception.PasswordChangeException
import com.martynov.exception.UserAddException
import com.martynov.exception.UserNotFoundException
import com.martynov.model.UserModel
import com.martynov.repository.UserRepository
import io.ktor.features.*
import org.springframework.security.crypto.password.PasswordEncoder
import java.io.File

class UserService(
    private val repo: UserRepository,
    private val tokenService: JWTTokenService,
    private val passwordEncoder: PasswordEncoder
) {
    suspend fun getModelByid(id: Long): UserModel? {
        return repo.getById(id)
    }

    suspend fun authenticate(input: AuthenticationRequestDto): AuthenticationResponseDto {
        val model = repo.getByUsername(input.username) ?: throw NotFoundException()
        if (!passwordEncoder.matches(input.password, model.password)) {
            throw PasswordChangeException("Неверный пароль")
        }
        val token = tokenService.generate(model.id)
        return AuthenticationResponseDto(token)
    }

    suspend fun registration(iteam: RegistrationRequestDto): AuthenticationResponseDto {
        val model = UserModel(
            id = repo.getSizeListUser().toLong(),
            username = iteam.username,
            password = passwordEncoder.encode(iteam.password),
            attachment = iteam.attachmentModel,
            token = tokenService.generate(repo.getSizeListUser().toLong()),
            readOnlyIdea = false
        )
        val chekingIsUser = repo.addUser(model)
        if (chekingIsUser) {
            return AuthenticationResponseDto(model.token)
        }
        return throw UserAddException("\"error\": Пользователь с таким логином уже зарегистрирован")
    }
    suspend fun changePassword(id: Long, input: PasswordChangeRequestDto):AutorIdeaRequest {
        val model = repo.getById(id) ?: throw UserNotFoundException()
        if (!passwordEncoder.matches(input.old_password, model.password)) {
            throw PasswordChangeException("Неверный пароль!")

        }
        val copy = model.copy(password = passwordEncoder.encode(input.new_password))
        repo.save(copy)
        return AutorIdeaRequest.fromModel(copy)

    }
    suspend fun addTokenDevice(id: Long?, tokenDevice: String): AutorIdeaRequest {
        return AutorIdeaRequest.fromModel(repo.addTokenDevice(id, tokenDevice))
    }
    suspend fun findTokenDeviceUser(id: Long?):String{

        val tokenDevice = repo.findTokenDevice(id)
        File(FILE_LOG).writeText(Gson().toJson(tokenDevice))

        //print(tokenDevice)
        return tokenDevice
    }



}