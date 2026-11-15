package dev.joguenco.roqui.security.repository

import dev.joguenco.roqui.security.model.User
import org.springframework.stereotype.Repository

@Repository
interface UserCustomRepository {

    fun findOneByUsername(username: String): User?
}
