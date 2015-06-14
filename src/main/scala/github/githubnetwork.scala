package github

import org.eclipse.egit.github.core._

import akka.actor.{ActorRef, Props, Actor, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import akka.event.Logging

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import collection.mutable.{ HashMap, MultiMap, Set }

import utils.Grapher
import utils.Reporters._

class GithubNetwork(val level: Int = 1, github: Github, reporter: Reporter) {
	var nodes: Set[GithubUser] = Set()
	var edges = new HashMap[GithubUser, Set[GithubUser]] with MultiMap[GithubUser, GithubUser]
	val system = ActorSystem("ActorSystem")

	def InitNodes() = {
		var count = level
		nodes += github.me
		var workNodes: Set[GithubUser] = Set()
		if (count != 0) {
			for( node <- nodes) {
				workNodes ++= github.getFollowees(node.getLogin)
				workNodes ++= github.getFollowers(node.getLogin)
			}
			nodes ++= workNodes
			count -= 1
		}
		while (count != 0) {
			var nodesBuf: Set[GithubUser] = Set()
			for( node <- workNodes) {
				nodesBuf ++= github.getFollowees(node.getLogin)
				nodesBuf ++= github.getFollowers(node.getLogin)
			}
			workNodes = nodesBuf
			nodes ++= workNodes
			count -= 1
		}
		// remove me 
		nodes -= github.me
	}

	class WorkActor() extends Actor {
		val log = Logging(context.system, this)
		def receive: Receive = {
			case "recruit" => 
			sender() ! "WorkDown"
			case user: GithubUser => 
			log.info("Working on " + user.getLogin)
			var followerList = github.getFollowers(user.getLogin)
			var followeeList = github.getFollowees(user.getLogin)

			for( follower <- followerList) {
				edges.addBinding(follower, user)
			}

			for( followee <- followeeList) {
				edges.addBinding(user, followee)
			}
			log.info("Work end on " + user.getLogin)
			sender() ! "WorkDown"
			case "Finish" =>
			sender() ! "Finish"
			context.stop(self)
		}
	}

	class SuperActor(thread: Int) extends Actor {
		val log = Logging(context.system, this)
		var workNodes = nodes
		var actorList: List[ActorRef] = List()
		var finishCount = thread
		for( i <- 1 to thread) {
			log.info("create " + i.toString + "th actor")
			val actor = system.actorOf(Props(new WorkActor), "WorkActor" + i.toString)
			actorList = actor :: actorList
		}
		def receive: Receive = {
			case "begin" =>
			for( actor <- actorList) {
			 	actor ! "recruit"
			 } 
			case "WorkDown" => 
			if (!workNodes.isEmpty) {
				sender() ! workNodes.head
				workNodes = workNodes.tail
			}
			else {
				sender() ! "Finish"
			}
			case "Finish" => 
			finishCount -= 1
			if (finishCount == 0) {
				context.stop(self)
			}
		}
	}

	def InitEdges() = {
		var counter = 0
		var time = -1
		for( user <- nodes) {
			if (time != 0) {
				time -= 1
				reporter.info(counter.toString + ".parsing " + user.getLogin)
				counter += 1
				var followerList = github.getFollowers(user.getLogin)
				var followeeList = github.getFollowees(user.getLogin)

				for( follower <- followerList) {
					if (nodes contains follower) {
						edges.addBinding(follower, user)
					}
				}

				for( followee <- followeeList) {
					if (nodes contains followee) {
						edges.addBinding(user, followee)
					}
				}
			}
		}
	}

	def draw() = {
		val grapher = new Grapher(nodes, edges)
		grapher.script
	}
}