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

class GithubNetwork(val level: Int = 1, github: Github) {
	var nodes: Set[User] = Set()
	var edges = new HashMap[User, Set[User]] with MultiMap[User, User]
	val system = ActorSystem("ActorSystem")

	def InitNodes() = {
		var count = level
		nodes += github.me
		while (count != 0) {
			for( node <- nodes) {
				nodes ++= github.getFollowees(node.getLogin)
				nodes ++= github.getFollowers(node.getLogin)
			}
			count -= 1
		}
	}

	class WorkActor() extends Actor {
		val log = Logging(context.system, this)
		def receive: Receive = {
			case "recruit" => 
			sender() ! "WorkDown"
			case user: User => 
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
		val sa = system.actorOf(Props(new SuperActor(4)), "SuperActor")
		sa ! "begin"
		println("End")
	}
}