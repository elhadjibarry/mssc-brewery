package guru.springframework.msscbrewery.web.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.UUID;

public class CustomerDto {

    private UUID id;

    @NotBlank
    @Size(min = 3, max = 100)
    private String name;
}
