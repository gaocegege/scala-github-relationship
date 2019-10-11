import config.Parser
import utils.Reporters._
import utils.Dumper
import github.{ Github, GithubNetwork }

object Main {
	val reporter: Reporter = new Reporter()
	reporter.attach(new ConsoleReporterHandler())
    reporter.open()
    val dumper: Dumper = new Dumper("Output-Data.txt")

	def main(args: Array[String]): Unit = {
		// get username and password
		val parser = new Parser("userinfo.ini", reporter)
		parser.run()
		val username = parser.getUsername()
		val pwd = parser.getPwd()
		var token = parser.getToken()

		var login = "gaocegege"
		if (args.length != 0) {
			login = args(0)
		}

		val github = new Github(username, pwd, token, login)
		val network = new GithubNetwork(1, github, reporter)
		network.InitNodes
		network.InitEdgesWithActor(4)
		network.draw
	}
}