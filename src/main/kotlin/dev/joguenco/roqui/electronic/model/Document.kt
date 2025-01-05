package dev.joguenco.roqui.electronic.model

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "ele_documents")
class Document {

    constructor()
    constructor(code: String, number: String, observation: String, status: String){
        this.code = code
        this.number = number
        this.observation = observation
        this.status = status
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id : Long? = null

    @Column(name = "code", nullable = false)
    var code : String? = null

    @Column(name = "number", nullable = false)
    var number : String? = null

    @Column(name = "authorization")
    var authorization : String? = null

    @Column(name = "authorization_date")
    @Temporal(TemporalType.TIMESTAMP)
    var authorizationDate : Date? = null

    @Column(name = "observation")
    var observation : String? = null

    @Column(name = "status")
    var status : String? = null
}