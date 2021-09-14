package eu.beegames.core.util

/**
 * @see eu.beegames.core.util.SpreadUtils
 */

fun net.md_5.bungee.api.event.PreLoginEvent.setCancelReason(components: Array<out net.md_5.bungee.api.chat.BaseComponent>) =
    SpreadUtils.setCancelReason(this, components)