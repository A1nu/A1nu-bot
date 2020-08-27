package ee.a1nu.application.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorDTO {
    public int errorCode;
    public String errorMessage;
}
