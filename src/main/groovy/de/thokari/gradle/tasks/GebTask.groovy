package de.thokari.gradle.tasks

import geb.Browser
import org.gradle.api.DefaultTask

class GebTask extends DefaultTask {

	public Browser getBrowser() {
		project.geb.browser
	}

	public Browser drive(Closure clos) {
		try {
			clos.delegate = browser
			clos.call()
			browser
		} catch (Throwable why) {
			try {
				browser.report "Exception encountered"
			}
			catch (Throwable all) {
				project.logger.error("Failed to generate report after exception", all)
			}
			throw why
		}
	}
}
