package com.cms.scaffold.route.operate.controller.sys;

import com.cms.scaffold.common.base.BaseController;
import com.cms.scaffold.common.builder.Builder;
import com.cms.scaffold.common.response.ResponseModel;
import com.cms.scaffold.feign.sys.SysMenuFeign;
import com.cms.scaffold.feign.sys.SysOperateFeign;
import com.cms.scaffold.micro.sys.ao.SysMenuAO;
import com.cms.scaffold.micro.sys.ao.SysOperateAO;
import com.cms.scaffold.micro.sys.bo.SysMenuBO;
import com.cms.scaffold.micro.sys.bo.SysOperateBO;
import com.cms.scaffold.route.operate.response.SysMenuResp;
import com.cms.scaffold.route.operate.shiro.ShiroService;
import com.cms.scaffold.route.operate.util.UserUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhangjiaheng
 */
@Controller
@RequestMapping("/sys/sysMenu")
public class SysMenuController extends BaseController {

    private static final String ftlPath = "/sys/sysMenu/";

    @Resource
    SysMenuFeign sysMenuFeign;
    @Resource
    SysOperateFeign sysOperateFeign;
    @Resource
    ShiroService shiroService;

    @GetMapping("/sysMenuIndex")
    public String sysMenuIndex() {
        return ftlPath + "sysMenuIndex";
    }

    @GetMapping("/SysMenuEdit")
    public String SysMenuEdit(Model model, SysMenuAO sysMenu) {
        SysMenuResp sysMenuResp;
        if (null == sysMenu.getId()) {
            sysMenuResp = Builder.build(sysMenu, SysMenuResp.class);
        } else {
            ResponseModel<SysMenuBO> resp = sysMenuFeign.selectById(sysMenu.getId());
            sysMenuResp = Builder.build(resp.getData(), SysMenuResp.class);
        }
        model.addAttribute("sysMenu", sysMenuResp);
        shiroService.updatePermission();
        return ftlPath + "SysMenuEdit";
    }

    @PostMapping("/saveOrUpdate")
    @ResponseBody
    public ResponseModel saveOrUpdate(SysMenuAO menu) {
        ResponseModel resp = sysMenuFeign.saveOrUpdate(menu);
        shiroService.updatePermission();
        return resp;
    }

    @RequestMapping("/findSysMenuByPid")
    @ResponseBody
    public List<SysMenuBO> listMenuByPid(Long parentId) {
        return sysMenuFeign.listMenuByPid(parentId).getData();
    }

    @GetMapping("/selectById")
    @ResponseBody
    public ResponseModel selectById(Long id) {
        return sysMenuFeign.selectById(id);
    }

    @RequestMapping("/findFatherIds")
    @ResponseBody
    public String findFatherIds(Long id) {
        ResponseModel<String> resp = sysMenuFeign.findFatherIds(id);
        return resp.getData();
    }

    @GetMapping("/rightMenus")
    @ResponseBody
    public List<SysMenuResp> rightMenus(Long pid) {
        List<SysMenuBO> menuBOS = sysMenuFeign.findByPidAndOperateId(pid, UserUtil.getOperatorId()).getData();
        return Builder.buildList(menuBOS, SysMenuResp.class);
    }

    @RequestMapping("/test")
    @ResponseBody
    public ResponseModel test(Long a){

        SysMenuAO menu = new SysMenuAO();
        menu.setName("test");
        menu.setPid(1L);
        menu.setRemark("权威测试");
        final ResponseModel responseModel1 = sysMenuFeign.saveOrUpdate(menu);
        System.out.println(responseModel1);
        SysOperateAO sysOperate = new SysOperateAO();
        sysOperate.setRealName("test");
        final ResponseModel<SysOperateBO> responseModel2 = sysOperateFeign.insert(sysOperate);
        System.out.println(responseModel2);
        System.out.println(1/a);
        return success();
    }
}
