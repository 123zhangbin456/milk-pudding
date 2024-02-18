package com.zhang.bin.controller;

import com.zhang.bin.dto.UserAuthInfo;
import com.zhang.bin.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ProjectName: milk-pudding
 * @ProjectPackage: com.zhang.bin.controller
 * @Author: Mr.Pudding
 * @CreateTime: 2024-02-01  11:08
 * @Description: TODO
 * @Version: 1.0
 */
//@Tag(name = "01.用户接口")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class SysUserController {

//    @Operation(summary = "根据用户名获取认证信息", hidden = true)
    @GetMapping("/{username}/authInfo")
    public Result<UserAuthInfo> getUserAuthInfo(
            /*@Parameter(description = "用户名")*/ @PathVariable String username
    ) {
//        UserAuthInfo userAUthInfo = userService.getUserAuthInfo(username);
        UserAuthInfo info = new UserAuthInfo();
        info.setUsername("pudding");
        info.setPassword("pudding");
        return Result.success(info);
    }
    //    @Operation(summary = "根据用户名获取认证信息", hidden = true)
//    @GetMapping("/{username}/authInfoReturnString")
//    public Result<String> getUserAuthInfoReturnString(
//            /*@Parameter(description = "用户名")*/ @PathVariable String username
//    ) {
//        return Result.success("pudding");
//    }
}
