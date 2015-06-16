# Scala-Github-Relationship

A graphical view of the relationships between github users.

## Environment

* SBT 0.13.8
* Java 1.6 or higher
* Scala 2.11

## Usage

First, the tool needs a account to get the higher rate limit in using Github API, so new a file named `userinfo.ini`, and the content of the file is the same as `userinfo.ini.example`. Then,

	sbt "run-main Main `login-name`"

login name here is the username, not the email or else. For example

<figure>
	<img src="http://gaocegege.com/scala-github-relationship/username.png", height="300">
</figure>

In the picture, the login name of the user is `gaocegege`.And the command should be `sbt "run-main Main gaocegege"`

PS: when run the command for the first time, it will takes some time to download the libraries.

## Output

Finally, you will get the `headless_simple.png & headless_simple.svg`

<figure>
	<img src="http://gaocegege.com/scala-github-relationship/example.png", height="400">
</figure>

The node in the graph represents a user, and a edge from A to B means A follows B. The color of the node depends on the degree, and the size of the node depends on the PageRank score.

## Q & A

* Why don't I use actor model to speed up the program?

If I use it, the github API will return [Abuse rate limit](https://developer.github.com/v3/#abuse-rate-limits), so.
