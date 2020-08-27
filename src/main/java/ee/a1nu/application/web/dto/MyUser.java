package ee.a1nu.application.web.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class MyUser extends User {
    public MyUser(String username,
                  String password,
                  Collection<GrantedAuthority> authorities,
                  String phone) {
        super(username, password, authorities);
        this.id = id;
        this.avatar = avatar;
    }

    private String id;
    private String avatar;

    public String getId() {
        return id;
    }

    public String getAvatar() {
        return avatar;
    }
}
