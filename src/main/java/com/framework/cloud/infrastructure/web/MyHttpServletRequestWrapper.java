package com.framework.cloud.infrastructure.web;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author youtao531 on 2023/4/26 11:34
 */
@Slf4j
@Getter
public class MyHttpServletRequestWrapper extends jakarta.servlet.http.HttpServletRequestWrapper {

    /**
     * 复制请求body
     */
    private final String body;

    public MyHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        try {
            //设置编码格式, 防止中文乱码
            request.setCharacterEncoding(request.getCharacterEncoding());
            //将请求中的流取出来放到body里，后面都只操作body就行
            this.body = read(request);
        } catch (Exception e) {
            log.error("MyHttpServletRequestWrapper 读取请求流异常e={}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 读取请求流
     */
    public static String read(HttpServletRequest request) {
        try (BufferedReader reader = request.getReader()) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("MyHttpServletRequestWrapper 读取请求流异常e={}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ServletInputStream getInputStream() {
        //返回body的流信息即可
        try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(body.getBytes())) {
            return getServletInputStream(inputStream);
        } catch (IOException e) {
            log.error("MyHttpServletRequestWrapper 读取请求流异常e={}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    /**
     * 重写 getServletInputStream
     */
    private static ServletInputStream getServletInputStream(ByteArrayInputStream inputStream) {
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }

            @Override
            public int read() {
                return inputStream.read();
            }
        };
    }
}
