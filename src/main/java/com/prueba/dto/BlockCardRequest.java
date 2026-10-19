package com.prueba.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BlockCardRequest (
    
    @NotBlank(message = "reason es obligatorio")
    @Size(max = 100, message = "reason no puede tener más de 100 caracteres")
    String reason
) {

}
