package dev.joguenco.roqui.security.repository

import dev.joguenco.roqui.security.model.User
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository

@Transactional
@Repository
class UserRepository : UserCustomRepository {

    @PersistenceContext lateinit var entityManager: EntityManager

    override fun findOneByUsername(username: String): User? {
        val user =
            entityManager
                .createQuery("from User where username = :username", User::class.java)
                .setParameter("username", username)
                .resultList
                .firstOrNull()

        return user
    }
}
