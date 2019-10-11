package config

import scala.collection.{ mutable, immutable, generic }
import scala.collection.mutable.ListBuffer
import scala.io.Source
import utils.Reporters._

class Parser(filename: String, reporter: Reporter) {
	var username: String = ""
	var pwd: String = ""
	var token: String = ""

	class Context(filename: String) {
		var ctx: ListBuffer[String] = ListBuffer()

		for(line <- Source.fromFile(filename).getLines()) {
			ctx += line.toString()
		}
	}

	class AbstractExp() {
		def interpret(context: Context): Unit = {
			if (context.ctx.length == 0) {
				reporter.error("The <userinfo.ini> has no content.")
			}
			else {
				var line = context.ctx.head
				if (line != "<account>") {
					reporter.error("account needed in <userinfo.ini>")
				}
				else {
					context.ctx = context.ctx.tail
					var accountExp = new AccountExp().interpret(context)
				}
			}
		}
	}

	class AccountExp() extends AbstractExp {

		override def interpret(context: Context): Unit = {
			var userLine = context.ctx.head
			context.ctx = context.ctx.tail

			var pwdLine = context.ctx.head
			context.ctx = context.ctx.tail

			var tokenLine = context.ctx.head
			context.ctx = context.ctx.tail

			if (userLine.split("=").length > 1) {
				username = userLine.split("=")(1)
			}
			if (pwdLine.split("=").length > 1) {
				pwd = pwdLine.split("=")(1)

			}
			if (tokenLine.split("=").length > 1) {
				token = tokenLine.split("=")(1)
			}

			reporter.info("Get the info of <" + username + ">")
		}
	}

	var context: Context = new Context(filename)
	var exp: AbstractExp = new AbstractExp()
	def run() = {
		exp.interpret(context)
	}

	def getUsername(): String = {
		return username
	}

	def getPwd(): String = {
		return pwd
	}

	def getToken(): String = {
		return token
	}
}