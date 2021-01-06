/*
 * Copyright (C) 2020 PatrickKR
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.patrick.survival.optimized

import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import java.io.File
import java.util.jar.JarFile
import kotlin.reflect.full.isSubclassOf

@Suppress("unused")
class OptimizedSurvivalPlugin : JavaPlugin() {
    private lateinit var player: Player
    private lateinit var objective: Objective

    override fun onEnable() {
        saveDefaultConfig()

        objective = server.scoreboardManager.mainScoreboard.run {
            getObjective("optimized") ?: registerNewObjective("optimized", "dummy", "최적화 야생")
        }

        objective.displaySlot = DisplaySlot.SIDEBAR

        val playerName = requireNotNull(config.getString("player"))

        val listener = object : Listener {}
        val eventExecutor = EventExecutor { _, event ->
            val entities = event::class.java.methods.filter { method ->
                method.parameterCount < 1 && method.returnType.kotlin.isSubclassOf(Entity::class)
            }.mapNotNull { method ->
                method.invoke(event) as? Entity
            }

            if (!this::player.isInitialized) {
                player = server.getPlayerExact(playerName) ?: return@EventExecutor
                logger.info("New Register: ${player.name}")
            }

            if (player in entities) {
                val name = event.eventName.removePrefix("Player").removePrefix("Entity").removeSuffix("Event")
                objective.getScore(name).score++
            }
        }

        val pluginManager = server.pluginManager
        val added = ArrayList<String>()
        val exclusions = config.getStringList("exclusions")
        val packages = config.getStringList("packages")

        val jarFile = JarFile(File(Event::class.java.protectionDomain.codeSource.location.file))
        val entries = jarFile.entries()

        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            if (entry.isDirectory || !entry.name.endsWith(".class")) {
                continue
            }

            val className = entry.name.substring(0, entry.name.length - 6).replace('/', '.')
            if (!packages.any(className::contains)) {
                continue
            }

            val clazz = Class.forName(className)

            try {
                try {
                    clazz.getDeclaredMethod("getHandlerList")
                } catch (exception: NoSuchMethodException) {
                    continue
                }

                if (!clazz.methods.any { method ->
                    method.returnType.kotlin.isSubclassOf(Entity::class)
                }) {
                    continue
                }

                if (exclusions.any(clazz.name::contains) || clazz.getAnnotation(Deprecated::class.java) != null) {
                    continue
                }

                @Suppress("UNCHECKED_CAST")
                pluginManager.registerEvent(clazz as Class<out Event>,
                    listener,
                    EventPriority.HIGH,
                    eventExecutor,
                    this,
                    true
                )

                added.add(clazz.simpleName)
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
            }
        }

        logger.info("Loaded ${added.count()} classes.")
        logger.info(added.joinToString(", ", "[", "]"))
    }

    override fun onDisable() {
        objective.unregister()
    }
}