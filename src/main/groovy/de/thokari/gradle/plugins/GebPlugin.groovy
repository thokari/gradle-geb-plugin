package de.thokari.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task;
import org.gradle.api.tasks.Copy

import de.thokari.gradle.extensions.GebExtension
import de.thokari.gradle.tasks.GebTask
import de.undercouch.gradle.tasks.download.Download
import static de.thokari.gradle.utils.OsUtils.isMacOs
import static de.thokari.gradle.utils.OsUtils.isWindows

class GebPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {

		project.with {
			
			project.apply plugin:"base"

			extensions.create 'geb', GebExtension, project

			afterEvaluate {
				def topLevelProject = project
				while (topLevelProject.parent != null) {
					topLevelProject = project
				}
				
				Copy unzipPhantomJs 
				if ( topLevelProject.tasks.find { it.name == "unzipPhantomJs" }) {
						unzipPhantomJs = topLevelProject.tasks.unzipPhantomJs
				} else {
					topLevelProject.task('downloadPhantomJs', type: Download) {
						overwrite false
						src geb.phantomJsDownloadUrl
						dest topLevelProject.buildDir
					}
	
					unzipPhantomJs = topLevelProject.task('unzipPhantomJs', 
						type: Copy, dependsOn: topLevelProject.tasks.downloadPhantomJs
					) {
						if(isWindows() || isMacOs()) {
							from zipTree("${topLevelProject.buildDir}/${geb.phantomJsArchive}")
						} else {
							from tarTree("${topLevelProject.buildDir}/${geb.phantomJsArchive}")
						}
						into topLevelProject.buildDir
					}		
				}				
				geb.setPhantomJsUnzipDir "${unzipPhantomJs.destinationDir}/${geb.phantomJsArchiveBaseName}"				
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
}