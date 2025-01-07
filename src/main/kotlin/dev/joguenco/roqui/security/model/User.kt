package dev.joguenco.roqui.security.model

import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.Immutable

@Entity
@Immutable
@Table(name = "v_ele_users")
class User(
    var id: Int? = null,
    var username: String? = null,
    val password: String? = null,
    val role: String? = null
)