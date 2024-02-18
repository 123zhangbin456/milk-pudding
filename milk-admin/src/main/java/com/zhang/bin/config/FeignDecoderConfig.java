package com.zhang.bin.config;

import com.zhang.bin.decoder.FeignDecoder;
import feign.codec.Decoder;
import feign.optionals.OptionalDecoder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;

/**
 * @ProjectName: milk-pudding
 * @ProjectPackage: com.zhang.bin.config
 * @Author: Mr.Pudding
 * @CreateTime: 2024-02-01  15:18
 * @Description: TODO
 * @Version: 1.0
 */
public class FeignDecoderConfig {

    /**
     * 该Java函数的主要作用是为Feign客户端框架配置一个自定义的解码器（Decoder）实例，用于处理从服务端接收到的HTTP响应数据到Java对象的转换过程。具体步骤如下：
     * 参数解释：
     *  参数messageConverters：该函数接受一个ObjectProvider<HttpMessageConverters>类型的参数messageConverters。
     *  ObjectProvider是Spring框架中的一个接口，它可以延迟提供或按需获取指定类型的Bean实例。
     *  这里的HttpMessageConverters是一个包含多种HTTP消息转换器的集合，它们主要用于将HTTP请求和响应体的数据（如JSON、XML等）转换为Java对象，或者反之。
     * 内部构造：
     *  (1)首先，创建了一个ResponseEntityDecoder实例，它是Feign的一个内置解码器，它能够处理HTTP响应并将其封装为Spring MVC的ResponseEntity对象。
     *  (2)然后，将上述ResponseEntityDecoder作为参数传递给FeignDecoder的构造函数，创建一个新的FeignDecoder实例。这个FeignDecoder的作用是在Feign调用中进一步解析HTTP响应，并将其内容转换为对应方法声明返回类型的Java对象。
     *  (3)这个FeignDecoder又被传递给了SpringDecoder的构造函数，结合传入的messageConverters来创建一个实际进行数据转换的SpringDecoder实例。
     *      这意味着在解码过程中，会根据HTTP响应的内容类型选择合适的HttpMessageConverter来进行具体的转换工作。
     *  (4)最后，使用创建好的FeignDecoder实例构建一个OptionalDecoder。
     *  OptionalDecoder是用来处理响应结果可能存在的空值情况，如果Feign调用返回的是一个Optional类型的响应，则此解码器可以正确地处理。
     * 返回结果：
     *  该函数最终返回构建好的OptionalDecoder实例，将其注入到Spring容器中，作为Feign客户端默认的解码器使用。
     *  这样，在后续执行Feign远程调用时，就能够通过这个自定义的解码器来正确解析并转换HTTP响应数据。
     * @return
     */
    @Bean
    public Decoder feignDecoder(ObjectProvider<HttpMessageConverters> messageConverters) {
        return new OptionalDecoder((new ResponseEntityDecoder(new FeignDecoder(new SpringDecoder(messageConverters)))));
    }
}
