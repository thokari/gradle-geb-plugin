gradle-geb-plugin
=================
[![Build Status](https://travis-ci.org/thokari/gradle-geb-plugin.svg?branch=master)](https://travis-ci.org/thokari/gradle-geb-plugin)

With this plugin you can use [Geb](http://www.gebish.org/) with a [PhantomJS](http://phantomjs.org/) headless browser in your automation scripts.
Automatically downloads the [PhantomJS binaries](http://phantomjs.org/download.html) for your OS (currently supporting Linux 64/32 and Windows).

The `GebTask` has a `drive()` method taking a closure, just like [Geb's drive method](http://www.gebish.org/manual/0.9.2/api/geb/Browser.html#drive(groovy.lang.Closure)).

See Geb's [NavigableSupport](http://www.gebish.org/manual/0.9.2/api/geb/content/NavigableSupport.html) and [Navigator](http://www.gebish.org/manual/0.9.2/api/index.html?geb/navigator/Navigator.html) API for information on how to interact with web content.

Useful for quick and dirty automation, once all dependencies are downloaded.

Usage
---------
```groovy
buildscript {
	repositories { jcenter() }
	dependencies { classpath "de.thokari:gradle-geb-plugin:0.1" }
}

apply plugin: 'geb'

task duckDuckGoSearch(type: GebTask) << {

	drive {
		go 'https://www.duckduckgo.com'
		println $('#tagline_homepage').text()
		$('#search_form_input_homepage') << 'wikipedia'
		$('#search_button_homepage').click()
		println $('a.large')*.text().join('\n')
	}
}
```
