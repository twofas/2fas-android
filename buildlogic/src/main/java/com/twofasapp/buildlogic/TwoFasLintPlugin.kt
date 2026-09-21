package com.twofasapp.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project

class TwoFasLintPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jmailen.kotlinter")
        }
    }
}
