package ee.a1nu.application.commandConstants;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Argument {
    String name;
    Boolean required;
    String description;

    public static Argument build(String name, Boolean isRequired, String description) {
        return Argument.builder()
                .name(name)
                .required(isRequired)
                .description(description)
                .build();
    }
}
