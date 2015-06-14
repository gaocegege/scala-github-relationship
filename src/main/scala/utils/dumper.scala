package utils

import java.io._

class Dumper(filename: String) {
	val writer = new PrintWriter(new File(filename))

	def write(content: String) = {
		writer.write(content)
	}

	def writeLine(content: String) = {
		writer.write(content)
		writer.write("\n")
	}

	def close() = {
		writer.close()
	}

	def writeSchoolRank(list: List[(String, Int)]) = {
		writeLine("---Friend School Rank---")
		for( (k, v) <- list) {
			writeLine("School: " + k + ", Count: " + v.toString)
		}
	}
}