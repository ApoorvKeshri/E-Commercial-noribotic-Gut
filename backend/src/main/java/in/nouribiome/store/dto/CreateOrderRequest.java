package in.nouribiome.store.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record CreateOrderRequest(
        @NotNull @Min(1) @Max(10) Integer quantity,
        @NotNull @Valid CustomerDetails customer
) {
    public record CustomerDetails(
            @NotBlank @Size(max = 100) String name,
            @NotBlank @Email @Size(max = 254) String email,
            @NotBlank @Size(max = 20) String phone,
            @NotBlank @Size(max = 1000) String address
    ) {}
}