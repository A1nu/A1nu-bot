package ee.a1nu.application.utils;

import discord4j.common.util.Snowflake;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import ee.a1nu.application.bot.Bot;

import java.util.Objects;

public class PermissionsUtils {
    public static boolean checkAdministrativeAllowance(Bot bot, Long userId, Long guildId) {
        PermissionSet permissionSet = bot.getMemberPermissions(Snowflake.of(userId), Snowflake.of(guildId));
        return !permissionSet.stream().filter(permission -> permission == Permission.ADMINISTRATOR).findAny().get().requiresMfa();
    }
}
