package dev.joguenco.roqui.security.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Immutable

@Entity
@Immutable
@Table(name = "v_users")
class User {
    @Id
    val id: Int? = null

    @Column(name = "username")
    val username: String? = null

    @Column(name = "password")
    val password: String? = null

    @Column(name = "role")
    val role: String? = null

    @Column(name = "status")
    val status: Boolean? = null
}