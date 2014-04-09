package de.thokari.gradle.utils

/**
 * Created by Rene on 07/04/14.
 */
class OsUtils {
	
	static boolean isMacOs() {
		String osString = System.properties['os.name'].toLowerCase()
		osString.contains("mac os x") || osString.contains("darwin")
	}

	static boolean isWindows() {
		System.properties['os.name'] ==~ '[W|w]indows.*'
	}

	static boolean is32BitLinux() {
		System.properties['os.arch'] ==~ '.*x86.*'
	}
}
