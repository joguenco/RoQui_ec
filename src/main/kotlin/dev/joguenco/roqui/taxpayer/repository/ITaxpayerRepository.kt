package dev.joguenco.roqui.taxpayer.repository

import dev.joguenco.roqui.taxpayer.model.Taxpayer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ITaxpayerRepository : JpaRepository<Taxpayer, Long>
