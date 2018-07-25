# OAuth协议

![](./20180526095100675.png '流程图')

整个开发流程简述一下：
1. 在客户端web项目中构造一个oauth的客户端请求对象（OAuthClientRequest），在此对象中携带客户端信息（clientId、accessTokenUrl、response_type、redirectUrl），将此信息放入http请求中，重定向到服务端。此步骤对应上图1
2. 在服务端web项目中接受第一步传过来的request，从中获取客户端信息，可以自行验证信息的可靠性。同时构造一个oauth的code授权许可对象（OAuthAuthorizationResponseBuilder），并在其中设置授权码code，将此对象传回客户端。此步骤对应上图2
3. 在在客户端web项目中接受第二步的请求request，从中获得code。同时构造一个oauth的客户端请求对象（OAuthClientRequest），此次在此对象中不仅要携带客户端信息（clientId、accessTokenUrl、clientSecret、GrantType、redirectUrl），还要携带接受到的code。再构造一个客户端请求工具对象（oAuthClient），这个工具封装了httpclient，用此对象将这些信息以post（一定要设置成post）的方式请求到服务端，目的是为了让服务端返回资源访问令牌。此步骤对应上图3。（另外oAuthClient请求服务端以后，会自行接受服务端的响应信息。
4. 在服务端web项目中接受第三步传过来的request，从中获取客户端信息和code，并自行验证。再按照自己项目的要求生成访问令牌（accesstoken），同时构造一个oauth响应对象（OAuthASResponse），携带生成的访问指令（accesstoken），返回给第三步中客户端的oAuthClient。oAuthClient接受响应之后获取accesstoken，此步骤对应上图4
5. 此时客户端web项目中已经有了从服务端返回过来的accesstoken，那么在客户端构造一个服务端资源请求对象（OAuthBearerClientRequest），在此对象中设置服务端资源请求URI，并携带上accesstoken。再构造一个客户端请求工具对象（oAuthClient），用此对象去服务端靠accesstoken换取资源。此步骤对应上图5
6. 在服务端web项目中接受第五步传过来的request，从中获取accesstoken并自行验证。之后就可以将客户端请求的资源返回给客户端了。

- client 客户端
 
- server 服务器
