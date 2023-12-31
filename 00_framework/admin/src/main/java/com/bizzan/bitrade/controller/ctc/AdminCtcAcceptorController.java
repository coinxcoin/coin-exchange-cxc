package com.bizzan.bitrade.controller.ctc;

import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.controller.BaseController;
import com.bizzan.bitrade.entity.Admin;
import com.bizzan.bitrade.entity.CtcAcceptor;
import com.bizzan.bitrade.service.CtcAcceptorService;
import com.bizzan.bitrade.service.LocaleMessageSourceService;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.core.Encrypt;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.Assert.notNull;
/**
 * @author Hevin  E-mail:bizzanhevin@gmail.com
 * @description otc承兑商
 * @date 2019/1/11 13:35
 */
@RestController
@RequestMapping("/ctc/acceptor")
public class AdminCtcAcceptorController  extends BaseController {
	@Autowired
	private CtcAcceptorService acceptorService;
	
	@Value("${spark.system.md5.key}")
    private String md5Key;
	

    @Autowired
    private LocaleMessageSourceService messageSource;
    
	/**
	 * 分页查询
	 * @param pageModel
	 * @return
	 */
	@RequiresPermissions("ctc:acceptor:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.CTC, operation = "分页查看CTC承兑商列表AdminCtcAcceptorController")
    public MessageResult orderList(PageModel pageModel) {
		if (pageModel.getProperty() == null) {
            List<String> list = new ArrayList<>();
            list.add("status");
            List<Sort.Direction> directions = new ArrayList<>();
            directions.add(Sort.Direction.DESC);
            pageModel.setProperty(list);
            pageModel.setDirection(directions);
        }
        Page<CtcAcceptor> all = acceptorService.findAll(null, pageModel.getPageable());
        return success(all);
    }
	
	/**
	 * 切换状态
	 * @param id
	 * @param status
	 * @return
	 */
	@RequiresPermissions("ctc:acceptor:switch")
    @AccessLog(module = AdminModule.CTC, operation = "标记已付款并完成CTC订单")
	@PostMapping("switch")
	@Transactional(rollbackFor = Exception.class)
    public MessageResult payOrder(@RequestParam("id") Long id,
    		@RequestParam(value = "password") String password,
            @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {
		
		password = Encrypt.MD5(password + md5Key);
        Assert.isTrue(password.equals(admin.getPassword()), messageSource.getMessage("WRONG_PASSWORD"));
        
        CtcAcceptor acceptor = acceptorService.findOne(id);
        notNull(acceptor, "validate order.id!");
        if(acceptor.getStatus() == 1) {
        	acceptor.setStatus(0);
        }else {
        	acceptor.setStatus(1);
        }
        acceptorService.save(acceptor);
        return success();
    }
}
