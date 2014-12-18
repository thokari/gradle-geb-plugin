package de.thokari.gradle.extensions

import geb.Browser

import java.util.logging.Level

import org.gradle.api.Project
import org.openqa.selenium.phantomjs.PhantomJSDriver

import static de.thokari.gradle.utils.OsUtils.is32BitLinux
import static de.thokari.gradle.utils.OsUtils.isMacOs
import static de.thokari.gradle.utils.OsUtils.isWindows

class GebExtension {

	final static String PHANTOM_JS_BINARY_PATH_KEY = 'phantomjs.binary.path'
	final static String GEB_REPORTS_DIR_KEY = 'geb.build.reportsDir'
	final static String DEFAULT_GEB_REPORTS_DIR = 'geb-plugin-reports'

	final static String PHANTOM_JS_DEFAULT_DOWNLOAD_BASE_URL = 'https://bitbucket.org/ariya/phantomjs/downloads'
	final static String PHANTOM_JS_DEFAULT_VERSION = '1.9.7'

	Project project

	String phantomJsVersion
	String phantomJsDownloadBaseUrl

	String phantomJsArchiveBaseName
	String phantomJsArchiveExtension
	String phantomJsExecutable
	String phantomJsArchive

	String phantomJsDownloadUrl
	String phantomJsUnzipDir

	String phantomJsBinaryPath
	String gebReportsDir
	Level logLevel

	Browser browser
	boolean usedBrowser = false

	public GebExtension(Project project) {

		this.project = project

		phantomJsArchiveExtension = 'tar.bz2'
		phantomJsExecutable = 'bin/phantomjs'

		logLevel = Level.OFF

		setPhantomJsVersion PHANTOM_JS_DEFAULT_VERSION
		setPhantomJsDownloadBaseUrl PHANTOM_JS_DEFAULT_DOWNLOAD_BASE_URL

		setGebReportsDir "${project.buildDir}/${DEFAULT_GEB_REPORTS_DIR}"
	}

	public void setPhantomJsVersion(version) {
		phantomJsVersion = version
		phantomJsArchiveBaseName = "phantomjs-${version}-linux-x86_64"

		if(isWindows()) {
			phantomJsArchiveBaseName = "phantomjs-${version}-windows"
			phantomJsArchiveExtension = 'zip'
			phantomJsExecutable = 'phantomjs.exe'
		} else if(isMacOs()) {
			phantomJsArchiveBaseName = "phantomjs-${version}-macosx"
			phantomJsArchiveExtension = 'zip'
		} else if(is32BitLinux()) {
			phantomJsArchiveBaseName = "phantomjs-${version}-linux-i686"
		}
		phantomJsArchive = "${phantomJsArchiveBaseName}.${phantomJsArchiveExtension}"
	}

	public void setPhantomJsDownloadBaseUrl(url) {
		phantomJsDownloadBaseUrl = url
		phantomJsDownloadUrl = "${phantomJsDownloadBaseUrl}/${phantomJsArchive}"
	}

	public void setPhantomJsUnzipDir(dir) {
		phantomJsUnzipDir = dir
		setPhantomJsBinaryPath "${phantomJsUnzipDir}/${phantomJsExecutable}"
	}

	private void setPhantomJsBinaryPath(path) {
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