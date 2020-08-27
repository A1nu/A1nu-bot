package ee.a1nu.application.web.dto;

import lombok.*;

import java.io.Serializable;


@Data
@Getter
@Setter
public class GuildPOJO implements Serializable {
    String id;
    String name;
    String icon;
    Boolean owner;
    Integer permissions;
    Boolean botExists;
}
