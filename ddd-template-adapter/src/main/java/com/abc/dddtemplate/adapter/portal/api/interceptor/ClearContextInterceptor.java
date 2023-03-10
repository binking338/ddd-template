package com.abc.dddtemplate.adapter.portal.api.interceptor;

import com.abc.dddtemplate.convention.DomainEventSupervisor;
import com.abc.dddtemplate.convention.UnitOfWork;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author <template/>
 * @date 2023-03-10
 */
public class ClearContextInterceptor implements HandlerInterceptor {

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UnitOfWork.clearContext();
        DomainEventSupervisor.clearDispatchedIntergrationEvents();
    }
}
