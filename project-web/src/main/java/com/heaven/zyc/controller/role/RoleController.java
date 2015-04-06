package com.heaven.zyc.controller.role;

import com.heaven.zyc.core.resource.domain.SysResource;
import com.heaven.zyc.core.resource.service.ResourceService;
import com.heaven.zyc.core.role.domain.RoleStatus;
import com.heaven.zyc.core.role.domain.SysRole;
import com.heaven.zyc.core.role.service.RoleService;
import com.heaven.zyc.exception.BusinessException;
import com.heaven.zyc.pagination.Pagination;
import com.heaven.zyc.support.spring.view.JsonView;
import com.heaven.zyc.support.utils.HttpParameterParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: heavenzyc
 * Date: 14-5-2
 * Time: 下午3:09
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/se")
public class RoleController {

    @Resource
    private RoleService roleService;
    @Resource
    private ResourceService resourceService;

    @RequestMapping(value = "/sys_role")
    public String toRolePage(){
        return "/se/content/role";
    }

    @RequestMapping(value = "/role.htm")
    public ModelAndView roleList(Pagination pagination){
        List<SysRole> roles = roleService.getAll(pagination);
        Map<String,Object> model = new HashMap<String, Object>();
        model.put("data",roles);
        model.put("pagination",pagination);
        return new ModelAndView(new JsonView("roleList",model));
    }

    @RequestMapping(value = "/data/json/resources.htm")
    public ModelAndView resourceList(){
        List<SysResource> resources = resourceService.getAllResource();
        return new ModelAndView(new JsonView(resources));
    }

    @RequestMapping(value = "/role/create.htm",method = RequestMethod.POST)
    public ModelAndView createRole(HttpServletRequest request){
        HttpParameterParser parser = new HttpParameterParser(request);
        String name = parser.getString("name");
        String description = parser.getString("description");
        Integer[] resources = parser.getIntegerArray("resources");
        SysRole role = new SysRole();
        role.setName(name);
        role.setDescription(description);
        role.setRoleStatus(RoleStatus.ACTIVE);
        Date date = new Date();
        role.setCreateTime(date);
        role.setUpdateTime(date);
        roleService.save(role,createResources(resources));
        return new ModelAndView(new JsonView("success",true));
    }

    private List<SysResource> createResources(Integer[] resourceIds){
        List<SysResource> resources = new ArrayList<SysResource>();
        if (resourceIds == null) return resources;
        for (Integer id : resourceIds){
            SysResource resource = resourceService.getById(id);
            resources.add(resource);
        }
        return resources;
    }

    /**
     * Whether role's name is exist
     */
    @RequestMapping(value = "/role/check_name",method = RequestMethod.GET)
    public ModelAndView checkNameUnique(HttpServletRequest request){
        String name = request.getParameter("name");
        return new ModelAndView(new JsonView(roleService.isExistByName(name)));
    }

    @RequestMapping(value = "/role/check_edit_name/{id}/{name}",method = RequestMethod.GET)
    public ModelAndView checkEditNameUnique(@PathVariable Integer id, @PathVariable String name){
        SysRole role = roleService.getByName(name);
        if (role != null && !role.getId().equals(id)){
            return new ModelAndView(new JsonView(true));
        }
        return new ModelAndView(new JsonView(false));
    }

    @RequestMapping(value = "/role/{id}/",method = RequestMethod.GET)
    public ModelAndView getRole(@PathVariable Integer id){
        SysRole role = roleService.getById(id);
        return new ModelAndView(new JsonView("role",role));
    }

    @RequestMapping(value = "/role/edit/{id}/",method = RequestMethod.POST)
    public ModelAndView editRole(HttpServletRequest request,@PathVariable Integer id){
        HttpParameterParser parser = new HttpParameterParser(request);
        String name = parser.getString("name");
        String description = parser.getString("description");
        Integer[] resources = parser.getIntegerArray("resources");
        SysRole role = roleService.getById(id);
        role.setName(name);
        role.setDescription(description);
        try {
            role.setUpdateTime(new Date());
            roleService.update(role,createResources(resources));
        } catch (BusinessException e) {
            return new ModelAndView(new JsonView(true,e.getMessage()));
        }
        return new ModelAndView(new JsonView(true,"修改成功！"));
    }

    @RequestMapping(value = "/role/delete/{id}",method = RequestMethod.GET)
    public ModelAndView deleteRole(@PathVariable Integer id){
        SysRole role = roleService.getById(id);
        if (role != null){
            try {
                roleService.delete(role);
            } catch (BusinessException e) {
                return new ModelAndView(new JsonView(true,e.getMessage()));
            }
        }
        return new ModelAndView(new JsonView(true,"删除成功！"));
    }

    @RequestMapping(value = "/role/freeze/{id}",method = RequestMethod.GET)
    public ModelAndView freezeRole(@PathVariable Integer id){
        SysRole role = roleService.getById(id);
        if (role != null){
            try {
                roleService.freeze(role);
            } catch (BusinessException e) {
                return new ModelAndView(new JsonView(true,e.getMessage()));
            }
        }
        return new ModelAndView(new JsonView(true,"删除成功！"));
    }
}
