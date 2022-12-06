package com.share.co.kcl.dtp.monitor.controller.console;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.share.co.kcl.dtp.monitor.model.domain.SecurityAuthDo;
import com.share.co.kcl.dtp.monitor.model.po.entity.DtpAdministrator;
import com.share.co.kcl.dtp.monitor.model.vo.admin.*;
import com.share.co.kcl.dtp.monitor.security.annotation.Auth;
import com.share.co.kcl.dtp.monitor.security.authentication.holder.DtpConsoleUserHolder;
import com.share.co.kcl.dtp.monitor.service.DtpAdministratorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "管理者控制路由")
@RequestMapping(value = "/console/administrator")
public class AdminConsoleController {

    @Autowired
    private DtpAdministratorService dtpAdministratorService;

    @ApiOperation(value = "登入")
    @PostMapping(value = "/v1/signIn")
    public AdministratorSignInResponse signIn(@RequestBody @Validated AdministratorSignInRequest request) {
        Long userId = dtpAdministratorService.check(request.getAccount(), request.getPassword());
        String authToken = new SecurityAuthDo(String.valueOf(userId)).echoToken();
        return new AdministratorSignInResponse(authToken);
    }

    @Auth
    @ApiOperation(value = "登出")
    @PostMapping(value = "/v1/signOut")
    public void signOut() {
    }

    @Auth
    @ApiOperation(value = "获取管理者信息")
    @GetMapping(value = "/v1/getAdministratorInfo")
    public AdministratorInfoGetResponse getAdministratorInfo() {
        String administratorId = DtpConsoleUserHolder.getAdministratorId();
        String administratorName = DtpConsoleUserHolder.getAdministratorName();
        return new AdministratorInfoGetResponse(administratorId, administratorName);
    }

    @Auth
    @ApiOperation(value = "获取管理者列表")
    @GetMapping(value = "/v1/getAdministrators")
    public AdministratorsGetResponse getAdministrators(@ModelAttribute @Validated AdministratorsGetRequest request) {
        IPage<DtpAdministrator> dtpAdministrators = dtpAdministratorService.page(new Page<>(request.getPageNo(), request.getPageSize()), dtpAdministratorService.getQueryWrapper()
                .eq(StringUtils.isNotBlank(request.getAccount()), DtpAdministrator::getAccount, request.getAccount())
                .eq(StringUtils.isNotBlank(request.getEmail()), DtpAdministrator::getEmail, request.getEmail())
                .eq(StringUtils.isNotBlank(request.getUsername()), DtpAdministrator::getUsername, request.getUsername()));
        return new AdministratorsGetResponse(dtpAdministrators);
    }

    @Auth
    @ApiOperation(value = "添加管理者")
    @PostMapping(value = "/v1/insertAdministrator")
    public void insertAdministrator(@RequestBody @Validated AdministratorInsertRequest request) {
        DtpAdministrator dtpAdministrator = new DtpAdministrator();
        BeanUtils.copyProperties(request, dtpAdministrator);
        dtpAdministratorService.save(dtpAdministrator);
    }

    @Auth
    @ApiOperation(value = "更新管理者")
    @PostMapping(value = "/v1/updateAdministrator")
    public void updateAdministrator(@RequestBody @Validated AdministratorUpdateRequest request) {
        DtpAdministrator dtpAdministrator = new DtpAdministrator();
        BeanUtils.copyProperties(request, dtpAdministrator);
        dtpAdministratorService.updateById(dtpAdministrator);
    }

    @Auth
    @ApiOperation(value = "删除管理者")
    @PostMapping(value = "/v1/deleteAdministrator")
    public void deleteAdministrator(@RequestBody @Validated AdministratorDeleteRequest request) {
        dtpAdministratorService.removeById(request.getId());
    }
}
