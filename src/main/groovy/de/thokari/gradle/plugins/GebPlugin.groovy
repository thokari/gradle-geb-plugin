package de.thokari.gradle.plugins


import geb.Browser

import java.util.logging.Level

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.openqa.selenium.WebDriver
import org.openqa.selenium.logging.LogType
import org.openqa.selenium.logging.LoggingPreferences
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType
import org.openqa.selenium.remote.DesiredCapabilities

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

					PhantomJSDriver driver = new PhantomJSDriver()
					driver.setLogLevel(Level.OFF)

					ext {
						browser = new Browser(driver: driver)
					}
					browser.drive {
						go 'https://www.duckduckgo.com'
						println $('#tagline_homepage').text()
						//$('#search_form_input_homepage') << 'wikipedia'
						//$('#search_button_homepage').click()
						//println $('a.large')*.text().join('\n')
					}
				}
			}

			project.gradle.buildFinished {
				try {
					usePhantomJs.browser.quit()
				} catch (e) {
					println "Error when shutting down browser: $e"
				}
				//usePhantomJs.driver.quit()
				//usePhantomJs.browser.quit()
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
