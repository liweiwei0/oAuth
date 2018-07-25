package fun.lww.client;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 客户端
 */
@Controller
public class ClientController {

    String clientId = null;
    String clientSecret = null;
    String accessTokenUrl = null;
    String userInfoUrl = null;
    String redirectUrl = null;
    String response_type = null;

    /**
     * 第一步
     *
     * 提交申请授权码的请求
     *
     * @return
     */
    @RequestMapping("/requestCode")
    public String requestCode() {

        clientId = "client-001";
        accessTokenUrl = "responseCode"; //服务器响应获取授权码请求的方法
        redirectUrl = "http://localhost:8081/client/callbackCode"; //回调地址
        response_type = "code";

        try {
            //构建oauth的请求。设置请求服务地址（accessTokenUrl）、clientId、response_type、redirectUrl
            OAuthClientRequest oAuthClientRequest = OAuthClientRequest
                    .authorizationLocation(accessTokenUrl)
                    .setResponseType(response_type)
                    .setClientId(clientId)
                    .setRedirectURI(redirectUrl)
                    .buildQueryMessage();
            System.out.println("申请授权码的请求地址: " + oAuthClientRequest.getLocationUri());
            return "redirect:http://localhost:8082/server/" + oAuthClientRequest.getLocationUri();
        } catch (OAuthSystemException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 第三步
     *
     * 接受客户端返回的code，提交申请access token的请求
     *
     * @param request
     * @return
     * @throws OAuthProblemException
     */
    @RequestMapping("/callbackCode")
    public Object requestAccessToken(HttpServletRequest request) throws OAuthProblemException {

        clientId = "client-001";
        clientSecret = "clientSecret";
        accessTokenUrl = "http://localhost:8082/server/responseAccessToken";
        redirectUrl = "http://localhost:8081/client/accessToken";

        try {
            OAuthClientRequest accessTokenRequest = OAuthClientRequest
                    .tokenLocation(accessTokenUrl)
                    .setGrantType(GrantType.AUTHORIZATION_CODE)
                    .setClientId(clientId)
                    .setClientSecret(clientSecret)
                    .setCode(request.getParameter("code"))
                    .setRedirectURI(redirectUrl)
                    .buildQueryMessage();

            OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
            //去服务端请求access token，并返回响应
            OAuthAccessTokenResponse oAuthResponse = oAuthClient.accessToken(accessTokenRequest, OAuth.HttpMethod.POST);

            //获取服务端返回过来的access token
            String accessToken = oAuthResponse.getAccessToken();
            System.out.println("accessToken: " + accessToken);

            //查看access token是否过期
//            Long expiresIn = oAuthResponse.getExpiresIn();
//            System.out.println(expiresIn);

            return "redirect:http://localhost:8081/client/accessToken?accessToken=" + accessToken;
        } catch (OAuthSystemException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 第五步
     *
     * 接受服务端传回来的access token，由此token去请求服务端的资源（用户信息等）
     *
     * @param accessToken
     * @return
     */
    @RequestMapping("/accessToken")
    public ModelAndView accessToken(String accessToken) {

        userInfoUrl = "http://localhost:8082/server/userInfo";

        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
        try {
            OAuthClientRequest userInfoRequest = new OAuthBearerClientRequest(userInfoUrl).setAccessToken(accessToken).buildQueryMessage();
            OAuthResourceResponse resourceResponse = oAuthClient.resource(userInfoRequest, OAuth.HttpMethod.GET, OAuthResourceResponse.class);
            String username = resourceResponse.getBody();
            ModelAndView modelAndView = new ModelAndView("usernamePage");
            modelAndView.addObject("username", username);
            return modelAndView;
        } catch (OAuthSystemException e) {
            e.printStackTrace();
        } catch (OAuthProblemException e) {
            e.printStackTrace();
        }
        return null;
    }

}
