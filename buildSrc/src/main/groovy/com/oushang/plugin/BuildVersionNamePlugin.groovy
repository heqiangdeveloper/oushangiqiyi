package com.oushang.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class BuildVersionNamePlugin implements Plugin<Project> {

    @Override
    void apply(Project target) {
        def extension = target.extensions.create('buildVersionName', BuildVersionNameExtension)
        if (!target.hasProperty("android")) {
            println "BuildVersionNamePlugin: Non Android projects. Do not execute \"BuildVersionName\" task."
            return
        }
        println "BuildVersionNamePlugin: execute \"BuildVersionName\" task..."
        target.android.applicationVariants.all { variant ->
            println "BuildVersionNamePlugin: buildType [${variant.buildType.name}]"
            println "BuildVersionNamePlugin: canGenerateVersionNameType ${extension.canGenerateVersionNameType}"
            if (variant.buildType.name in extension.canGenerateVersionNameType) {
                println "BuildVersionNamePlugin: softwareStatus [${extension.softwareStatus}] -- versionCode [${variant.mergedFlavor.versionCode}]"
                def versionName = extension.generateVersionName.call()
                println "BuildVersionNamePlugin: generate versionName [$versionName]"
                target.android.defaultConfig.versionName = versionName
                variant.outputs.each { output ->
                    output.versionNameOverride = versionName
                }
            }
        }
    }
}