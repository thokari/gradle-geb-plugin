package de.thokari.gradle.extensions

import geb.Browser

import java.util.logging.Level

import org.gradle.api.Project
import org.openqa.selenium.phantomjs.PhantomJSDriver

class GebExtension {

	final static String PHANTOM_JS_BINARY_PATH_KEY = 'phantomjs.binary.path'
	final static String GEB_REPORTS_DIR_KEY = 'geb.build.reportsDir'
	final static String DEFAULT_GEB_REPORTS_DIR = 'geb-plugin-reports'

	Project project

	String phantomJsBinaryPath
	String gebReportsDir
	Level logLevel
	Browser browser
	boolean usedBrowser = false

	public GebExtension(Project project) {

		this.project = project
		this.logLevel = Level.OFF
		setPhantomJsBinaryPath "${project.buildDir}/${project.phantomJsArchiveBaseName}/${project.phantomJsExecutable}"
		setGebReportsDir "${project.buildDir}/${DEFAULT_GEB_REPORTS_DIR}"
	}

	public void setPhantomJsBinaryPath(path) {
		phantomJsBinaryPath = path
		System.setProperty PHANTOM_JS_BINARY_PATH_KEY, phantomJsBinaryPath
	}

	public void setGebReportsDir(path) {
		gebReportsDir = path
		System.setProperty GEB_REPORTS_DIR_KEY, gebReportsDir
	}

	public Browser getBrowser() {
		if(!browser) {
			PhantomJSDriver driver = new PhantomJSDriver()
			driver.setLogLevel logLevel
			browser = new Browser(driver: driver)
			usedBrowser = true
		}
		browser
	}
}