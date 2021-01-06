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

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21"
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "com.github.patrick-mc"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://repo.maven.apache.org/maven2/")
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly(kotlin("reflect"))
    compileOnly("com.destroystokyo.paper:paper-api:1.16.4-R0.1-SNAPSHOT")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    create<Copy>("distJar") {
        from(shadowJar)

        val fileName = "${rootProject.name.split("-").joinToString("", transform = String::capitalize)}.jar"
        rename {
            fileName
        }

        var dest = File("W:/Servers/1.16.4/plugins")
        if (File(dest, fileName).exists()) {
            dest = File(dest, "update")
        }

        into(dest)
    }
}