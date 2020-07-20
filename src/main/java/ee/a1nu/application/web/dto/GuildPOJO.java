package ee.a1nu.application.web.dto;

import lombok.*;

import java.io.Serializable;


@Data public class GuildPOJO implements Serializable {
    String id;
    String name;
    String icon;
    Boolean owner;
    Integer permissions;

    public String getId() { return this.id; }
    public String getName() { return name; }
    public String getIcon() { return icon; }
    public boolean isOwner() {
        return this.owner;
    }
    public Integer getPermissions() { return this.permissions; }
}
