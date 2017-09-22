// CHECK
def request(url: String) = {
	val request = ws
		.url(url)
		.withHeaders(hdrs)
		.withAuth(username, password, scheme)
		.withBody(body)
		.withFollowRedirects(follow)
		.withMethod(method)
		.withProxyServer(proxyServer)
		.withRequestFilter(filter)
		.withRequestTimeout(timeout)
		.withVirtualHost(vh)
		.withQueryString(parameters)
}