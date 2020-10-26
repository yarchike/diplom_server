package com.martynov.exception

class UserNotFoundException(message: String = "Пользователь не найден") : RuntimeException(message)