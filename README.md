gradle-geb-plugin
=================
[![Build Status](https://travis-ci.org/thokari/gradle-geb-plugin.svg?branch=master)](https://travis-ci.org/thokari/gradle-geb-plugin)

Automatically downloads the [PhantomJS binaries](http://phantomjs.org/download.html) for your OS (currently supporting Linux 64/32 and Windows).

Useful for quick and dirty automation.

Usage
---------
```groovy
buildscript {
	repositories { jcenter() }
	dependencies { classpath "de.thokari:gradle-geb-plugin:0.1" }
}

apply plugin: 'geb'

task('goDuckDuckGo', type: GebTask) << {

	drive {
		go 'https://www.duckduckgo.com'
		println $('#tagline_homepage').text()
		$('#search_form_input_homepage') << 'wikipedia'
		$('#search_button_homepage').click()
		println $('a.large')*.text().join('\n')
	}
}
```
