package eu.beegames.core.util;

/**
 * A helper Java class used to avoid using Kotlin's spread operator,
 * which is known to copy the whole array before passing it through,
 * resulting in a potential loss of performance.
 */
public class SpreadUtils {
    public static void setCancelReason(net.md_5.bungee.api.event.PreLoginEvent ev,
                                       net.md_5.bungee.api.chat.BaseComponent[] components) {
        ev.setCancelReason(components);
    }
}
