package ee.a1nu.application.utils;

import discord4j.common.util.Snowflake;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import ee.a1nu.application.bot.Bot;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Objects;
import java.util.Optional;

public class PermissionsUtils {
    public static boolean checkAdministrativeAllowance(Bot bot, Long guildId) {
        DefaultOAuth2User principal = (DefaultOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PermissionSet permissionSet = bot.getMemberPermissions(Snowflake.of(Long.parseLong(Objects.requireNonNull(principal.getAttribute("id")))), Snowflake.of(guildId));
        Optional<Permission> administratorPermission = permissionSet.stream().filter(permission -> permission == Permission.ADMINISTRATOR).findAny();
        return administratorPermission.filter(permission -> !permission.requiresMfa()).isPresent();
    }

    public static boolean checkUserExistsOnGuild(Bot bot, Long guildId) {
        DefaultOAuth2User principal = (DefaultOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return bot.getGuildMember(Snowflake.of(guildId), Snowflake.of(Long.parseLong(Objects.requireNonNull(principal.getAttribute("id"))))) != null;
    }
}
