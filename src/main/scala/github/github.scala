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
	var me = new GithubUser(userService.getUser)

	def getFollowers(login: String=""): List[GithubUser] = {
		var list: List[User] = List()
		if (login == "") {
			list = userService.getFollowers.asScala.toList.asInstanceOf[List[GithubUser]]
		}
		else {
			list = userService.getFollowers(login).asScala.toList.asInstanceOf[List[GithubUser]]
		}
		list.map( ele => new GithubUser(ele))
	}

	def getFollowees(login: String=""): List[GithubUser] = {
		var list: List[User] = List()
		if (login == "") {
			list = userService.getFollowing.asScala.toList
		}
		else {
			list = userService.getFollowing(login).asScala.toList
		}
		list.map( ele => new GithubUser(ele))
	}
}