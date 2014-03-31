package de.thokari.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import de.thokari.gradle.extensions.GebExtension
import de.thokari.gradle.tasks.GebTask
import de.undercouch.gradle.tasks.download.Download

class GebPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {

		project.with {

			extensions.create 'geb', GebExtension, project

			afterEvaluate {

				task('downloadPhantomJs', type: Download) {
					overwrite false
					src geb.phantomJsDownloadUrl
					dest buildDir
				}

				task('unzipPhantomJs', type: Copy, dependsOn: downloadPhantomJs) {
					if(isWindows()) {
						from zipTree("${buildDir}/${geb.phantomJsArchive}")
					} else {
						from tarTree("${buildDir}/${geb.phantomJsArchive}")
					}
					into (geb.phantomJsUnzipDir - geb.phantomJsArchiveBaseName)
				}

				tasks.withType(GebTask) { task -> task.dependsOn unzipPhantomJs }
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

	private def isWindows() {
		System.properties['os.name'] ==~ '[W|w]indows.*'
	}
}