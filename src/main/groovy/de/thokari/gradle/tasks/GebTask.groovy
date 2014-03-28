package de.thokari.gradle.tasks

import geb.Browser
import org.gradle.api.DefaultTask

class GebTask extends DefaultTask {

	public Browser drive(Closure clos) {
		Browser browser = project.geb.browser
		clos.delegate = browser
		clos.call()
		browser
	}
}
