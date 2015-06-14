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

class GithubHttpClient() {
	val client = new DefaultHttpClient

	def getContentByUrl(url: String): String = {
		val httpGet = new HttpGet(url)
		val res = client.execute(httpGet)
		var rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()))
		var content = ""
		var line = rd.readLine()
		while (line != null) {
			content += line
			line = rd.readLine()
		}

		// close the connection
		val entity = res.getEntity()
		EntityUtils.consume(entity)

		content
	}
}