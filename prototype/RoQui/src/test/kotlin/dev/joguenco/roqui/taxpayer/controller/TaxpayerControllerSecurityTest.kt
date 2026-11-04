package dev.joguenco.roqui.taxpayer.controller

import dev.joguenco.roqui.taxpayer.dto.TaxpayerDto
import dev.joguenco.roqui.taxpayer.service.TaxpayerService
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
class TaxpayerControllerSecurityTest {

    @Autowired lateinit var mockMvc: MockMvc

    @MockitoBean lateinit var taxpayerService: TaxpayerService

    private val taxpayer =
        TaxpayerDto(
            id = 1,
            identification = "9999999999999",
            legalName = "Mi Empresa",
            forcedAccounting = "SI",
        )

    @Test
    fun `returns 401 when no token is provided`() {
        mockMvc.get("/roqui/v1/taxpayer").andExpect { status { isUnauthorized() } }
    }

    @Test
    fun `returns 403 when the token has none of the required roles`() {
        mockMvc.get("/roqui/v1/taxpayer") { with(jwt()) }.andExpect { status { isForbidden() } }
    }

    @Test
    fun `returns the taxpayer when the token carries the user role`() {
        given(taxpayerService.getTaxpayer()).willReturn(taxpayer)

        mockMvc
            .get("/roqui/v1/taxpayer") {
                with(jwt().authorities(SimpleGrantedAuthority("ROLE_USER")))
            }
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(1) }
                jsonPath("$.legalName") { value("Mi Empresa") }
            }
    }

    @Test
    fun `returns 204 when there is no taxpayer`() {
        given(taxpayerService.getTaxpayer()).willReturn(TaxpayerDto())

        mockMvc
            .get("/roqui/v1/taxpayer") {
                with(jwt().authorities(SimpleGrantedAuthority("ROLE_ADMIN")))
            }
            .andExpect { status { isNoContent() } }
    }
}
