package utils

// httpclient
import java.io._
import org.apache.commons._
import org.apache.http._
import org.apache.http.client._
import org.apache.http.client.methods.{ HttpPost, HttpGet }
import java.util.{ ArrayList }
import org.apache.http.message.BasicNameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.impl.client.{BasicResponseHandler, DefaultHttpClient}
import org.apache.http.client.params.{CookiePolicy, HttpClientParams}
import org.apache.http.util.EntityUtils
import org.apache.http.params.CoreProtocolPNames

class HttpParser() {
	val client = new GithubHttpClient
	def oauth(username: String) = {
		val url = "https://github.com/login/oauth/authorize?client_id=%s" format username
		println(client.getContentByUrl(url))
	}

	def getAllFollowers(username: String): String = {
		val baseUrl = "https://api.github.com"
		val url = baseUrl + "/users/%s/followers" format username
		client.getContentByUrl(url)
	}
}