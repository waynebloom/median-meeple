package com.waynebloom.scorekeeper.ext

fun String.sentenceCase(): String {
	val result: StringBuilder = StringBuilder(this[0].uppercase())
	for (i in 1 until this.length) {
		if (this[i - 1] == ' ') {
			result.append(this[i].uppercase())
		} else result.append(this[i].lowercase())
	}
	return result.toString()
}
