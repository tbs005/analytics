package com.orienttech.statics.web.controller.dataSummarize;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springside.modules.mapper.JsonMapper;

import com.alibaba.fastjson.JSON;
import com.orienttech.statics.commons.base.BaseController;
import com.orienttech.statics.commons.base.Result;
import com.orienttech.statics.commons.datatables.DataTablesPage;
import com.orienttech.statics.commons.utils.Utils;
import com.orienttech.statics.dao.entity.submission.TblReportTemplate;
import com.orienttech.statics.dao.entity.submission.TblTemplateSubmit;
import com.orienttech.statics.dao.entity.submission.TblTemplateSum;
import com.orienttech.statics.service.dateSummarize.DateSummarizeService;
import com.orienttech.statics.service.reportResultQuery.ReportResultQueryService;
import com.orienttech.statics.service.templateMng.TemplateMngService;
@RequestMapping("/dataSummarize")
@Controller
public class DataSummarizeController extends BaseController {
	
	@Autowired
	private TemplateMngService templateMngService;
	@Autowired
	private ReportResultQueryService reportResultQueryService;
	/**
	 * 根据报表类型加载不同的页面
	 * @param type	
	 * @param model
	 * @return
	 */
	@RequestMapping
	public String index(@RequestParam(defaultValue="")Model model){
		
		return "/reportResultQuery/";
	}
	
	@Autowired
	private DateSummarizeService dateSummarizeService;

	/**
	 * 分页查询所有角色
	 * 
	 * @param draw
	 * @param firstIndex
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/findAll", method = RequestMethod.POST)
	@ResponseBody
	public DataTablesPage findAll(String workflowId, Integer draw,
			@RequestParam("search[value]") String search,
			@RequestParam("start") Integer firstIndex,
			@RequestParam("length") Integer pageSize) {

		DataTablesPage dataTablesPage = dateSummarizeService.findAll(workflowId,draw, search, firstIndex
				/ pageSize + 1, pageSize);

		return dataTablesPage;
	}
	
	@RequestMapping(value = "/ifShowSelectAll", method = RequestMethod.POST)
	@ResponseBody
	public String ifShowSelectAll(String workflowId) {
		
		String flag = dateSummarizeService.ifShowSelectAll(workflowId);
		
		return flag;
	}
	
	/**
	 * TODO
	 * 报表汇总
	 * @param ck_Id
	 * @param workflowId
	 * @param templateId
	 * @return
	 */
	@RequestMapping(value = "/summarizeReport", method = RequestMethod.POST)
	@ResponseBody
	public String summarizeReport(String checkedId, String workflowId,String templateId) {

		String flag="";
		//汇总机构勾选相id(报送id)
		checkedId = checkedId.substring(0, checkedId.length()-1);
		try {
			String message = dateSummarizeService.reportSummarize(checkedId, workflowId, templateId);
			flag = message;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return flag;
	}
	/**
	 * 根据workflowId获得汇总表
	 * @param workflowId
	 * @return
	 */
	@RequestMapping(value = "/getTemplateSumByWorkflowId", method = RequestMethod.POST)
	@ResponseBody
	public Result getTemplateSumByWorkflowId(String workflowId) {
		
		TblTemplateSum templateSum = null;
		if(StringUtils.isNotEmpty(workflowId)){
			templateSum = reportResultQueryService.queryByWorkflowId(workflowId);
		}
		
		return success("成功汇总",templateSum);
	}
	
	@RequestMapping(value = "/templateInit", method = RequestMethod.POST)
	@ResponseBody
	public TblReportTemplate queryTemplateById(String templateId,String workflowId) {
		
		TblReportTemplate tblReportTemplate = templateMngService.queryTemplateById(templateId);
		String orgName = templateMngService.queryOrgNameByOrgId(tblReportTemplate.getSubmitOrg());
		String roleName = templateMngService.queryRoleNameByRoleId(tblReportTemplate.getCheckRole());
		String state = dateSummarizeService.findStatusByTemplateId(templateId);
		String submitTime = dateSummarizeService.findSubmitTimeByTemplateId(templateId,tblReportTemplate.getTimeLimit(),workflowId);//组装报送时间
		tblReportTemplate.setSubmitOrg(orgName);
		tblReportTemplate.setCheckRole(roleName);
		tblReportTemplate.setState(state);
		tblReportTemplate.setReleasePeople(submitTime);//暂且用这个字段存放
		return tblReportTemplate;
	}
	
	@RequestMapping(value = "/reportSumInit", method = RequestMethod.POST)
	@ResponseBody
	public String reportSumInit(String workflowId){
		
		TblTemplateSum templateSum = reportResultQueryService.queryByWorkflowId(workflowId);
		String reportSumPath = templateSum.getPath();
		return reportSumPath;
	}

}
