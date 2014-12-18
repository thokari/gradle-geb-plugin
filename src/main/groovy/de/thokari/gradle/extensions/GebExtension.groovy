package de.thokari.gradle.extensions

import geb.Browser

import java.util.logging.Level

import org.gradle.api.Project
import org.openqa.selenium.Dimension;
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import static de.thokari.gradle.utils.OsUtils.is32BitLinux
import static de.thokari.gradle.utils.OsUtils.isMacOs
import static de.thokari.gradle.utils.OsUtils.isWindows

class GebExtension {

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
		setPhantomJsUnzipDir "${project.buildDir}/${phantomJsArchiveBaseName}"
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
	}

	public void setGebReportsDir(path) {
		gebReportsDir = path
	}

	public Browser getBrowser() {
		if(!browser) {		    
			usedBrowser = true
			DesiredCapabilities desiredCapabilities = new DesiredCapabilities()
			desiredCapabilities.setCapability(
				PhantomJSDriverService.PHANTOMJS_CLI_ARGS, 
				[ "--web-security=false",
				  "--ssl-protocol=any",
				  "--ignore-ssl-errors=true",
		        ]
			);
			desiredCapabilities.setCapability(
				PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
				phantomJsBinaryPath
			)
			
		    PhantomJSDriver driver = new PhantomJSDriver(desiredCapabilities)
			driver.manage().window().setSize(new Dimension(1028, 768))
			browser = new Browser(driver: driver)
			browser.config.reportsDir = new File(gebReportsDir)			
		}
		browser
	}
}