package dev.joguenco.roqui.shared.dto

import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.validation.constraints.NotEmpty

@JsonInclude
class Message {
    @NotEmpty var message = ""
}
