package com.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.utils.ValidatorUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.annotation.IgnoreAuth;

import com.entity.ZujiedingdanEntity;
import com.entity.view.ZujiedingdanView;

import com.service.ZujiedingdanService;
import com.service.TokenService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MD5Util;
import com.utils.MPUtil;
import com.utils.CommonUtil;


/**
 * 租借订单
 * 后端接口
 * @author 
 * @email 
 * @date 2021-04-04 23:32:25
 */
@RestController
@RequestMapping("/zujiedingdan")
public class ZujiedingdanController {
    @Autowired
    private ZujiedingdanService zujiedingdanService;
    


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,ZujiedingdanEntity zujiedingdan,
		HttpServletRequest request){
		String tableName = request.getSession().getAttribute("tableName").toString();
		if(tableName.equals("shangjia")) {
			zujiedingdan.setShangjiazhanghao((String)request.getSession().getAttribute("username"));
		}
		if(tableName.equals("yonghu")) {
			zujiedingdan.setZhanghao((String)request.getSession().getAttribute("username"));
		}
        EntityWrapper<ZujiedingdanEntity> ew = new EntityWrapper<ZujiedingdanEntity>();
		PageUtils page = zujiedingdanService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, zujiedingdan), params), params));

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,ZujiedingdanEntity zujiedingdan, HttpServletRequest request){
        EntityWrapper<ZujiedingdanEntity> ew = new EntityWrapper<ZujiedingdanEntity>();
		PageUtils page = zujiedingdanService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, zujiedingdan), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( ZujiedingdanEntity zujiedingdan){
       	EntityWrapper<ZujiedingdanEntity> ew = new EntityWrapper<ZujiedingdanEntity>();
      	ew.allEq(MPUtil.allEQMapPre( zujiedingdan, "zujiedingdan")); 
        return R.ok().put("data", zujiedingdanService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(ZujiedingdanEntity zujiedingdan){
        EntityWrapper< ZujiedingdanEntity> ew = new EntityWrapper< ZujiedingdanEntity>();
 		ew.allEq(MPUtil.allEQMapPre( zujiedingdan, "zujiedingdan")); 
		ZujiedingdanView zujiedingdanView =  zujiedingdanService.selectView(ew);
		return R.ok("查询租借订单成功").put("data", zujiedingdanView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        ZujiedingdanEntity zujiedingdan = zujiedingdanService.selectById(id);
        return R.ok().put("data", zujiedingdan);
    }

    /**
     * 前端详情
     */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        ZujiedingdanEntity zujiedingdan = zujiedingdanService.selectById(id);
        return R.ok().put("data", zujiedingdan);
    }
    



    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody ZujiedingdanEntity zujiedingdan, HttpServletRequest request){
    	zujiedingdan.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(zujiedingdan);
        zujiedingdanService.insert(zujiedingdan);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody ZujiedingdanEntity zujiedingdan, HttpServletRequest request){
    	zujiedingdan.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(zujiedingdan);
        zujiedingdanService.insert(zujiedingdan);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody ZujiedingdanEntity zujiedingdan, HttpServletRequest request){
        //ValidatorUtils.validateEntity(zujiedingdan);
        zujiedingdanService.updateById(zujiedingdan);//全部更新
        return R.ok();
    }
    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        zujiedingdanService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
    /**
     * 提醒接口
     */
	@RequestMapping("/remind/{columnName}/{type}")
	public R remindCount(@PathVariable("columnName") String columnName, HttpServletRequest request, 
						 @PathVariable("type") String type,@RequestParam Map<String, Object> map) {
		map.put("column", columnName);
		map.put("type", type);
		
		if(type.equals("2")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar c = Calendar.getInstance();
			Date remindStartDate = null;
			Date remindEndDate = null;
			if(map.get("remindstart")!=null) {
				Integer remindStart = Integer.parseInt(map.get("remindstart").toString());
				c.setTime(new Date()); 
				c.add(Calendar.DAY_OF_MONTH,remindStart);
				remindStartDate = c.getTime();
				map.put("remindstart", sdf.format(remindStartDate));
			}
			if(map.get("remindend")!=null) {
				Integer remindEnd = Integer.parseInt(map.get("remindend").toString());
				c.setTime(new Date());
				c.add(Calendar.DAY_OF_MONTH,remindEnd);
				remindEndDate = c.getTime();
				map.put("remindend", sdf.format(remindEndDate));
			}
		}
		
		Wrapper<ZujiedingdanEntity> wrapper = new EntityWrapper<ZujiedingdanEntity>();
		if(map.get("remindstart")!=null) {
			wrapper.ge(columnName, map.get("remindstart"));
		}
		if(map.get("remindend")!=null) {
			wrapper.le(columnName, map.get("remindend"));
		}

		String tableName = request.getSession().getAttribute("tableName").toString();
		if(tableName.equals("shangjia")) {
			wrapper.eq("shangjiazhanghao", (String)request.getSession().getAttribute("username"));
		}
		if(tableName.equals("yonghu")) {
			wrapper.eq("zhanghao", (String)request.getSession().getAttribute("username"));
		}

		int count = zujiedingdanService.selectCount(wrapper);
		return R.ok().put("count", count);
	}
	


}
