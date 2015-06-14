package github

import org.eclipse.egit.github.core.User

// for the equals and hashcode
class GithubUser extends User {
	def this(user: User) = {
		this()
		this.setHireable(user.isHireable)
		this.setCreatedAt(user.getCreatedAt)
		this.setCollaborators(user.getCollaborators)
		this.setDiskUsage(user.getDiskUsage)
		this.setFollowers(user.getFollowers)
		this.setFollowing(user.getFollowing)
		this.setId(user.getId)
		this.setOwnedPrivateRepos(user.getOwnedPrivateRepos)
		this.setPrivateGists(user.getPrivateGists)
		this.setPublicGists(user.getPublicGists)
		this.setPublicRepos(user.getPublicRepos)
		this.setTotalPrivateRepos(user.getTotalPrivateRepos)
		this.setAvatarUrl(user.getAvatarUrl)
		this.setBlog(user.getBlog)
		this.setCompany(user.getCompany)
		this.setEmail(user.getEmail)
		this.setGravatarId(user.getGravatarId)
		this.setHtmlUrl(user.getHtmlUrl)
		this.setLocation(user.getLocation)
		this.setLogin(user.getLogin)
		this.setName(user.getName)
		this.setType(user.getType)
		this.setUrl(user.getUrl)
		this.setPlan(user.getPlan)
	}

	override def equals(o: Any) = o match {
		case that: GithubUser => this.getLogin == that.getLogin
		case _ => false
	}

	override def hashCode = this.getLogin.hashCode

	override def toString(): String =  "GithubUser(%s)" format this.getLogin
	
}