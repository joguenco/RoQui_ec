package dev.joguenco.roqui.security.service

import dev.joguenco.roqui.security.repository.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

typealias ApplicationUser = dev.joguenco.roqui.security.model.User

@Service
class CustomUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user =
            userRepository.findOneByUsername(username)?.mapToUserDetails()
                ?: throw UsernameNotFoundException("Not found!")
        return user
    }

    private fun ApplicationUser.mapToUserDetails(): UserDetails {
        val user =
            User.builder().username(this.username).password(this.password).roles(this.role).build()
        return user
    }
}
