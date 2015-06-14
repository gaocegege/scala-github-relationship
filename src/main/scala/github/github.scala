package github

import utils._

import org.eclipse.egit.github.core._
import org.eclipse.egit.github.core.service._
import org.eclipse.egit.github.core.client.GitHubClient

import scala.collection.JavaConverters._

class Github(username: String, pwd: String) {
	val client = new GitHubClient()
	client.setCredentials(username, pwd)
	var userService = new UserService(client)
	var me = userService.getUser

	def getFollowers(login: String=""): List[User] = {
		if (login == "") {
			userService.getFollowers.asScala.toList
		}
		else {
			userService.getFollowers(login).asScala.toList
		}
	}

	def getFollowees(login: String=""): List[User] = {
		if (login == "") {
			userService.getFollowing.asScala.toList
		}
		else {
			userService.getFollowing(login).asScala.toList
		}
	}
}