package de.thokari.gradle.plugins


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy

import de.thokari.gradle.extensions.GebExtension
import de.undercouch.gradle.tasks.download.Download


class GebPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {

		project.with {

			apply plugin: 'groovy'
			apply plugin: 'eclipse'

			ext {
				phantomJsDownloadBaseUrl = 'https://bitbucket.org/ariya/phantomjs/downloads'
				phantomJsArchiveBaseName = "phantomjs-${phantomJsVersion}-linux-x86_64"
				phantomJsArchiveExtension = 'tar.bz2'
				phantomJsExecutable = 'bin/phantomjs'
				underWindows {
					phantomJsArchiveBaseName = "phantomjs-${phantomJsVersion}-windows"
					phantomJsArchiveExtension = 'zip'
					phantomJsExecutable = 'phantomjs.exe'
				}
				under32BitLinux { phantomJsArchiveBaseName = "phantomjs-${phantomJsVersion}-linux-i686" }
				phantomJsArchive = "${phantomJsArchiveBaseName}.${phantomJsArchiveExtension}"
				phantomJsDownloadUrl = "${phantomJsDownloadBaseUrl}/${phantomJsArchive}"
			}

			extensions.create 'geb', GebExtension, project

			ext {
				GebTask = de.thokari.gradle.tasks.GebTask
			}

			task('downloadPhantomJs', type: Download) {
				overwrite false
				src phantomJsDownloadUrl
				dest buildDir
			}

			task('unzipPhantomJs', type: Copy, dependsOn: downloadPhantomJs) {
				if(isWindows()) {
					from zipTree("${buildDir}/${phantomJsArchive}")
				} else {
					from tarTree("${buildDir}/${phantomJsArchive}")
				}
				into buildDir
			}

			afterEvaluate {
				tasks.withType(de.thokari.gradle.tasks.GebTask) { task -> task.dependsOn unzipPhantomJs }
			}

			gradle.buildFinished {
				if(geb.usedBrowser) {
					try {
						geb.browser.quit()
					} catch (e) {
						println "Error when shutting down browser: $e"
					}
				}
			}
		}
	}

	def underWindows(Closure clos) {
		if(isWindows()) {
			clos.call()
		}
	}

	def isWindows() {
		System.properties['os.name'] ==~ '[W|w]indows.*'
	}

	def under32BitLinux(Closure clos) {
		if(System.properties['os.arch'] =~ 'x86') {
			clos.call()
		}
	}
}
