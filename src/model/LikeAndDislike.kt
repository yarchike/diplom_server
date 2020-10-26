package com.martynov.model

import com.martynov.dto.AutorIdeaRequest

enum class TypeLikeDisLike {
    LIKE, DISLIKE
}

data class LikeAndDislike(val autor: AutorIdeaRequest, val date: Long, val type: TypeLikeDisLike)