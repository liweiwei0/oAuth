package fun.lww.server;

import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * 资源所有者 用户确认授权 允许客户端获取用户资源
 */
@Controller
public class ResourceOwnerController {

    /**
     * 第二步
     *
     * 接收到授权码申请 向客户端返回授权码
     *
     * @param request
     * @return
     */
    @RequestMapping("/responseCode")
    public Object responseCode(HttpServletRequest request) {
        try {
            //构建OAuth 授权请求
            OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);
            System.out.println("oauthRequest: clientId=" + oauthRequest.getClientId() + " responseType=" + oauthRequest.getResponseType()
                + " redirectURI=" + oauthRequest.getRedirectURI());

            if (oauthRequest.getClientId() != null && oauthRequest.getClientId() != "") {
                //设置授权码
                String authorizationCode = "QAZWSXEDCRFVTGBYHNUJMIKOLP";

                //进行OAuth响应构建
                OAuthASResponse.OAuthAuthorizationResponseBuilder builder = OAuthASResponse.authorizationResponse(request, HttpServletResponse.SC_FOUND);

                //设置授权码
                builder.setCode(authorizationCode);

                //得到到客户端重定向地址
                String redirectURI = oauthRequest.getParam(OAuth.OAUTH_REDIRECT_URI);

                //构建响应
                OAuthResponse response = builder.location(redirectURI).buildQueryMessage();
                String responseUri = response.getLocationUri();

                System.out.println("请求重定向地址: " + redirectURI);
                System.out.println("响应地址: " + responseUri);

                //根据OAuthResponse返回ResponseEntity响应
                HttpHeaders headers = new HttpHeaders();
                try {
                    headers.setLocation(new URI(responseUri));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                return "redirect:" + responseUri;
            }
        } catch (OAuthSystemException e) {
            e.printStackTrace();
        } catch (OAuthProblemException e) {
            e.printStackTrace();
        }
        return null;
    }

}
