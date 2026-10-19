package com.prueba.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record IssueCardRequest(

    @NotBlank(message = "Product ID es obligatorio")
    @Pattern(regexp = "\\d{6}", message = "Product ID debe tener 6 dígitos")
    String productId,

    @NotBlank(message = "holderName es obligatorio")
    @Size(max = 100, message = "holderName no puede tener más de 100 caracteres")
    String holderName
){

}

