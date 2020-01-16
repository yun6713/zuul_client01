package com.bonc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.authentication.DefaultGatewayResolverImpl;
import org.jasig.cas.client.authentication.GatewayResolver;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.util.AssertionThreadLocalFilter;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.AssertionImpl;
import org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.fastjson.JSON;
//@Configuration
public class CasConfig {
	//单点登录会暴露此地址
	@Value("http://${server.address:localhost}:${server.port:8080}")
	private String SERVER_NAME;
	@Bean
	//用于单点退出，该过滤器用于实现单点登出功能，通知其他应用单点登出
	public ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> casListener(){
		SingleSignOutHttpSessionListener listener=new SingleSignOutHttpSessionListener();
		ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> registry=new ServletListenerRegistrationBean<>();
		registry.setListener(listener);
		return registry;
	}
	
	@Bean
	//该过滤器用于实现单点退出功能，可选配置。
	public FilterRegistrationBean<SingleSignOutFilter> casFilter1(){
		SingleSignOutFilter filter=new SingleSignOutFilter();
		FilterRegistrationBean<SingleSignOutFilter> registry = new FilterRegistrationBean<>();
		registry.setFilter(filter);
		registry.addUrlPatterns("/*");
		registry.setName("CAS Single Sign Out Filter");
		return registry;
	}
	
//	@Bean
	//该过滤器负责用户的认证工作，必须启用它 
	public FilterRegistrationBean<AuthenticationFilter> casFilter2(){
		AuthenticationFilter filter=new AuthenticationFilter();
		FilterRegistrationBean<AuthenticationFilter> registry = new FilterRegistrationBean<>();
		registry.setFilter(filter);
		Map<String,String> initParameters=new HashMap<>();
		initParameters.put("casServerLoginUrl", "http://localhost:8080/cas/login");
		initParameters.put("serverName", SERVER_NAME);
		registry.setInitParameters(initParameters);
		registry.addUrlPatterns("/*");
		registry.setName("CASFilter");
		return registry;
	}
	@Bean
	//该过滤器负责用户的认证工作，必须启用它 
	public FilterRegistrationBean<AbstractCasFilter> casFilter2Ext(){
		AbstractCasFilter filter=new NewAuthenticationFilter();
		FilterRegistrationBean<AbstractCasFilter> registry = new FilterRegistrationBean<>();
		registry.setFilter(filter);
		Map<String,String> initParameters=new HashMap<>();
		initParameters.put("casServerLoginUrl", "http://localhost:8080/cas/login");
		initParameters.put("serverName", SERVER_NAME);
		registry.setInitParameters(initParameters);
		registry.addUrlPatterns("/*");
		registry.setName("CASFilter");
		return registry;
	}
	
	@Bean
	//该过滤器负责对Ticket的校验工作，必须启用它
	public FilterRegistrationBean<Cas20ProxyReceivingTicketValidationFilter> casFilter3(){
		Cas20ProxyReceivingTicketValidationFilter filter=new Cas20ProxyReceivingTicketValidationFilter();
		FilterRegistrationBean<Cas20ProxyReceivingTicketValidationFilter> registry = new FilterRegistrationBean<>();
		registry.setFilter(filter);
		Map<String,String> initParameters=new HashMap<>();
		initParameters.put("casServerUrlPrefix", "http://localhost:8080/cas");
		initParameters.put("serverName", SERVER_NAME);
		registry.setInitParameters(initParameters);
		registry.addUrlPatterns("/*");
		registry.setName("CAS Validation Filter");
		return registry;
	}

	@Bean
	public FilterRegistrationBean<HttpServletRequestWrapperFilter> casFilter4(){
		HttpServletRequestWrapperFilter filter=new HttpServletRequestWrapperFilter();
		FilterRegistrationBean<HttpServletRequestWrapperFilter> registry = new FilterRegistrationBean<>();
		registry.setFilter(filter);
		registry.addUrlPatterns("/*");
		registry.setName("CAS HttpServletRequest Wrapper Filter");
		return registry;
	}
	
	@Bean
	public FilterRegistrationBean<AssertionThreadLocalFilter> casFilter5(){
		AssertionThreadLocalFilter filter=new AssertionThreadLocalFilter();
		FilterRegistrationBean<AssertionThreadLocalFilter> registry = new FilterRegistrationBean<>();
		registry.setFilter(filter);
		registry.addUrlPatterns("/*");
		registry.setName("CAS Assertion Thread Local Filter");
		return registry;
	}
}
class NewAuthenticationFilter extends MyAuthenticationFilter {
	private static Logger log = LoggerFactory.getLogger(NewAuthenticationFilter.class);
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) servletRequest;
		final HttpSession session = req.getSession();
        String casAuthInfo = req.getHeader(CONST_CAS_ASSERTION);
		if(session!=null && session.getAttribute(CONST_CAS_ASSERTION)==null && casAuthInfo!=null) {
			try {
				AssertionImpl assertion = JSON.parseObject(casAuthInfo, AssertionImpl.class);
//				final HttpSession session = req.getSession(false);
				session.setAttribute(CONST_CAS_ASSERTION, assertion);
		        filterChain.doFilter(servletRequest, servletResponse);
	            return;
			}catch (Exception e) {
				//json转换异常，原逻辑认证。
				log.error("Error :{}",e);
			}
		}
		super.doFilter(servletRequest, servletResponse, filterChain);
	}
}
/**
 * 
 * @author litianlin
 * @date   2020年1月9日下午3:40:04
 * @Description 复制cas AuthenticationFilter；doFilter方法去除final标记，便于重写。
 */
class MyAuthenticationFilter extends AbstractCasFilter {

    /**
     * The URL to the CAS Server login.
     */
    private String casServerLoginUrl;

    /**
     * Whether to send the renew request or not.
     */
    private boolean renew = false;

    /**
     * Whether to send the gateway request or not.
     */
    private boolean gateway = false;
    
    private GatewayResolver gatewayStorage = new DefaultGatewayResolverImpl();

    protected void initInternal(final FilterConfig filterConfig) throws ServletException {
        if (!isIgnoreInitConfiguration()) {
            super.initInternal(filterConfig);
            setCasServerLoginUrl(getPropertyFromInitParams(filterConfig, "casServerLoginUrl", null));
            log.trace("Loaded CasServerLoginUrl parameter: " + this.casServerLoginUrl);
            setRenew(parseBoolean(getPropertyFromInitParams(filterConfig, "renew", "false")));
            log.trace("Loaded renew parameter: " + this.renew);
            setGateway(parseBoolean(getPropertyFromInitParams(filterConfig, "gateway", "false")));
            log.trace("Loaded gateway parameter: " + this.gateway);

            final String gatewayStorageClass = getPropertyFromInitParams(filterConfig, "gatewayStorageClass", null);

            if (gatewayStorageClass != null) {
                try {
                    this.gatewayStorage = (GatewayResolver) Class.forName(gatewayStorageClass).newInstance();
                } catch (final Exception e) {
                    log.error(e,e);
                    throw new ServletException(e);
                }
            }
        }
    }

    public void init() {
        super.init();
        CommonUtils.assertNotNull(this.casServerLoginUrl, "casServerLoginUrl cannot be null.");
    }

    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        final HttpSession session = request.getSession(false);
        final Assertion assertion = session != null ? (Assertion) session.getAttribute(CONST_CAS_ASSERTION) : null;

        if (assertion != null) {
            filterChain.doFilter(request, response);
            return;
        }

        final String serviceUrl = constructServiceUrl(request, response);
        final String ticket = CommonUtils.safeGetParameter(request,getArtifactParameterName());
        final boolean wasGatewayed = this.gatewayStorage.hasGatewayedAlready(request, serviceUrl);

        if (CommonUtils.isNotBlank(ticket) || wasGatewayed) {
            filterChain.doFilter(request, response);
            return;
        }

        final String modifiedServiceUrl;

        log.debug("no ticket and no assertion found");
        if (this.gateway) {
            log.debug("setting gateway attribute in session");
            modifiedServiceUrl = this.gatewayStorage.storeGatewayInformation(request, serviceUrl);
        } else {
            modifiedServiceUrl = serviceUrl;
        }

        if (log.isDebugEnabled()) {
            log.debug("Constructed service url: " + modifiedServiceUrl);
        }

        final String urlToRedirectTo = CommonUtils.constructRedirectUrl(this.casServerLoginUrl, getServiceParameterName(), modifiedServiceUrl, this.renew, this.gateway);

        if (log.isDebugEnabled()) {
            log.debug("redirecting to \"" + urlToRedirectTo + "\"");
        }

        response.sendRedirect(urlToRedirectTo);
    }

    public final void setRenew(final boolean renew) {
        this.renew = renew;
    }

    public final void setGateway(final boolean gateway) {
        this.gateway = gateway;
    }

    public final void setCasServerLoginUrl(final String casServerLoginUrl) {
        this.casServerLoginUrl = casServerLoginUrl;
    }
    
    public final void setGatewayStorage(final GatewayResolver gatewayStorage) {
    	this.gatewayStorage = gatewayStorage;
    }
}
