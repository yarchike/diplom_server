package com.martynov.exception

import java.lang.RuntimeException

class UserNotFoundException (message:String = "Пользователь не найден"): RuntimeException(message)