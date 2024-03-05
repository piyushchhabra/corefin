package com.corefin.server.exception;


import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

@Component
public class AppExceptionResolver extends AbstractHandlerExceptionResolver
{
    private static final Logger logger = Logger.getLogger(AppExceptionResolver.class.getSimpleName());

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
    {
        logger.severe("Application error in: [" + ex.getClass().getName() + "]" + ex);
        return null;
    }
}
