package dev.joguenco.roqui.taxpayer.service

import dev.joguenco.roqui.taxpayer.dto.TaxpayerDto
import dev.joguenco.roqui.taxpayer.mapper.ITaxpayerMapper
import dev.joguenco.roqui.taxpayer.mapper.TaxpayerMapperImpl
import dev.joguenco.roqui.taxpayer.repository.ITaxpayerRepository
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service

@Service
class TaxpayerService(private val taxPayerRepository: ITaxpayerRepository
, val taxpayerMapper: ITaxpayerMapper) {

    fun getTaxpayer(): TaxpayerDto {
        val taxpayer = taxPayerRepository.findById(1).get()
        // Mapper with implementation
        val taxpayerMapper= TaxpayerMapperImpl()
        // Mapper with interface, without implementation
        //val taxpayerMapper= Mappers.getMapper(ITaxpayerMapper::class.java)
        return taxpayerMapper.toDto(taxpayer)
    }
}