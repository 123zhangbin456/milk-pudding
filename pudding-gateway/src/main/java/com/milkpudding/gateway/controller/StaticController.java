package com.milkpudding.gateway.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;

/**
 * 静态文件服务控制器
 * 提供前端页面和静态资源访问
 */
@Controller
public class StaticController {
    
    /**
     * 首页重定向到登录页面
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/login.html";
    }
    
    /**
     * 登录页面
     */
    @GetMapping("/login.html")
    public ResponseEntity<Resource> loginPage() throws IOException {
        return serveStaticFile("static/login.html", MediaType.TEXT_HTML);
    }
    
    /**
     * 注册页面
     */
    @GetMapping("/register.html")
    public ResponseEntity<Resource> registerPage() throws IOException {
        return serveStaticFile("static/register.html", MediaType.TEXT_HTML);
    }
    
    /**
     * CSS 样式文件
     */
    @GetMapping("/styles.css")
    public ResponseEntity<Resource> styles() throws IOException {
        return serveStaticFile("static/styles.css", MediaType.valueOf("text/css"));
    }
    
    /**
     * JavaScript 文件
     */
    @GetMapping("/script.js")
    public ResponseEntity<Resource> script() throws IOException {
        return serveStaticFile("static/script.js", MediaType.valueOf("application/javascript"));
    }
    
    /**
     * 通用静态文件服务方法
     */
    private ResponseEntity<Resource> serveStaticFile(String path, MediaType mediaType) throws IOException {
        Resource resource = new ClassPathResource(path);
        
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, mediaType.toString())
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600") // 1小时缓存
                .body(resource);
    }
}
