package com.framework.cloud.infrastructure.exception.error;

import com.framework.cloud.domain.model.basic.ComCodes;
import com.framework.cloud.infrastructure.exception.constant.ErrorException;
import com.framework.cloud.infrastructure.exception.constant.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * 修改 AbstractErrorController
 *
 * @author Yt on 2023/1/5 15:51
 */
@Slf4j
@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class CustomErrorController extends AbstractErrorController {

    public CustomErrorController(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    private static ErrorException newCustomErrorException(HttpServletRequest request) {
        Integer code = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        String message = (String) request.getAttribute("jakarta.servlet.error.message");
        String path = (String) request.getAttribute("jakarta.servlet.error.request_uri");
        ComCodes codes = ComCodes.getEnum(code, ComCodes.BAD_REQUEST);
        if (ComCodes.NOT_FOUND == codes) {
            return new NotFoundException(String.format(" '%s' ", path));
        } else {
            return new ErrorException(code, message);
        }
    }

    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView errorHtml(HttpServletRequest request) {
        throw newCustomErrorException(request);
    }

    @RequestMapping
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        throw newCustomErrorException(request);
    }
}
