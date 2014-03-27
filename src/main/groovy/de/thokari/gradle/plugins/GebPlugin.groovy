package de.thokari.gradle.plugins


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy

import org.openqa.selenium.phantomjs.PhantomJSDriver

import de.undercouch.gradle.tasks.download.Download

import geb.Browser

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

			task('usePhantomJs', dependsOn: unzipPhantomJs)  {

				System.setProperty 'phantomjs.binary.path', "${buildDir}/${phantomJsArchiveBaseName}/${phantomJsExecutable}"
				System.setProperty 'geb.build.reportsDir', "$buildDir/geb-plugin-reports"

				doLast {
					def gebBrowser = new Browser(driver: new PhantomJSDriver())
					gebBrowser.drive {
						go 'https://www.duckduckgo.com'
						$('#search_form_input_homepage') << 'wikipedia'
						$('#search_button_homepage').click()

						println $('a.large')*.text().join('\n')
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
