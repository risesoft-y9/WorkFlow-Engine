package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.api.org.DepartmentApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.entity.ActRuDetail;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.Department;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.repository.jpa.ActRuDetailRepository;
import net.risesoft.service.ActRuDetailService;
import net.risesoft.service.ProcessParamService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;
import y9.client.rest.processadmin.IdentityApiClient;
import y9.client.rest.processadmin.TaskApiClient;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Transactional(readOnly = true)
@Service(value = "actRuDetailService")
public class ActRuDetailServiceImpl implements ActRuDetailService {

	@Autowired
	private ActRuDetailRepository actRuDetailRepository;

	@Autowired
	private HistoricTaskApi historicTaskManager;

	@Autowired
	private ProcessParamService processParamService;

	@Autowired
	private SpmApproveItemService itemService;

	@Autowired
	private TaskApiClient taskManager;

	@Autowired
	private IdentityApiClient identityManager;

	@Autowired
	private DepartmentApi departmentApi;

	@Override
	@Transactional(readOnly = false)
	public void copy(String oldProcessSerialNumber, String newProcessSerialNumber, String newProcessInstanceId) {
		try {
			ProcessParam processParam = processParamService.findByProcessSerialNumber(newProcessSerialNumber);
			String itemId = processParam.getItemId();
			SpmApproveItem item = itemService.findById(itemId);
			List<ActRuDetail> list = actRuDetailRepository.findByProcessSerialNumber(oldProcessSerialNumber);
			List<ActRuDetail> listTemp = new ArrayList<>();
			ActRuDetail neward = null;
			for (ActRuDetail actRuDetail : list) {
				neward = new ActRuDetail();
				Y9BeanUtil.copyProperties(actRuDetail, neward);
				neward.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
				neward.setProcessSerialNumber(newProcessSerialNumber);
				neward.setProcessInstanceId(newProcessInstanceId);
				neward.setStarted(true);
				neward.setItemId(processParam.getItemId());
				neward.setProcessDefinitionKey(item.getWorkflowGuid());
				neward.setStatus(ActRuDetail.DOING);
				neward.setTaskId("");
				listTemp.add(neward);
			}
			actRuDetailRepository.saveAll(listTemp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int countBySystemName(String systemName) {
		return actRuDetailRepository.countBySystemNameAndEndedTrueAndDeletedFalse(systemName);
	}

	@Override
	public int countBySystemNameAndAssignee(String systemName, String assignee) {
		return actRuDetailRepository.countBySystemNameAndAssigneeAndEndedTrueAndDeletedFalseAndPlaceOnFileFalse(systemName, assignee);
	}

	@Override
	public int countBySystemNameAndAssigneeAndDeletedTrue(String systemName, String assignee) {
		return actRuDetailRepository.countBySystemNameAndAssigneeAndDeletedTrue(systemName, assignee);
	}

	@Override
	public int countBySystemNameAndAssigneeAndStatus(String systemName, String assignee, int status) {
		int count = 0;
		if (0 == status) {
			count = actRuDetailRepository.countBySystemNameAndAssigneeAndStatusAndDeletedFalse(systemName, assignee, status);
		} else {
			count = actRuDetailRepository.countBySystemNameAndAssigneeAndStatusAndEndedFalseAndDeletedFalse(systemName, assignee, status);
		}
		return count;
	}

	@Override
	public int countBySystemNameAndDeletedTrue(String systemName) {
		return actRuDetailRepository.countBySystemNameAndDeletedTrue(systemName);
	}

	@Override
	public int countBySystemNameAndDeptIdAndDeletedTrue(String systemName, String deptId) {
		return actRuDetailRepository.countBySystemNameAndDeptIdAndDeletedTrue(systemName, deptId);
	}

	@Override
	public int countBySystemNameAndDeptIdAndEndedTrueAndDeletedTrue(String systemName, String deptId) {
		return actRuDetailRepository.countBySystemNameAndDeptIdAndEndedTrueAndDeletedTrue(systemName, deptId);
	}

	@Override
	public int countBySystemNameAndEndedTrueAndDeletedTrue(String systemName) {
		return actRuDetailRepository.countBySystemNameAndEndedTrueAndDeletedTrue(systemName);
	}

	@Override
	@Transactional(readOnly = false)
	public boolean deletedByProcessSerialNumber(String processSerialNumber) {
		try {
			List<ActRuDetail> list = actRuDetailRepository.findByProcessSerialNumber(processSerialNumber);
			List<ActRuDetail> listTemp = new ArrayList<>();
			for (ActRuDetail actRuDetail : list) {
				actRuDetail.setDeleted(true);
				listTemp.add(actRuDetail);
			}
			actRuDetailRepository.saveAll(listTemp);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	@Transactional(readOnly = false)
	public boolean endByProcessInstanceId(String processInstanceId) {
		try {
			List<ActRuDetail> list = actRuDetailRepository.findByProcessInstanceId(processInstanceId);
			List<ActRuDetail> listTemp = new ArrayList<>();
			for (ActRuDetail actRuDetail : list) {
				actRuDetail.setEnded(true);
				listTemp.add(actRuDetail);
			}
			actRuDetailRepository.saveAll(listTemp);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	@Transactional(readOnly = false)
	public boolean endByProcessSerialNumber(String processSerialNumber) {
		try {
			List<ActRuDetail> list = actRuDetailRepository.findByProcessSerialNumber(processSerialNumber);
			List<ActRuDetail> listTemp = new ArrayList<>();
			for (ActRuDetail actRuDetail : list) {
				actRuDetail.setStatus(1);
				actRuDetail.setEnded(true);
				listTemp.add(actRuDetail);
			}
			actRuDetailRepository.saveAll(listTemp);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public List<ActRuDetail> findByAssigneeAndStatus(String assignee, int status) {
		List<ActRuDetail> pageList = actRuDetailRepository.findBySystemNameNotAndAssigneeAndStatusAndDeletedFalseOrderByCreateTimeDesc("chuanyuewen", assignee, status);
		return pageList;
	}

	@Override
	public Page<ActRuDetail> findByAssigneeAndStatus(String assignee, int status, int rows, int page, Sort sort) {
		PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
		Page<ActRuDetail> pageList = actRuDetailRepository.findBySystemNameNotAndAssigneeAndStatusAndDeletedFalse("chuanyuewen", assignee, status, pageable);
		return pageList;
	}

	@Override
	public Page<ActRuDetail> findByAssigneeAndStatusAndSystemNameIn(String assignee, int status, List<String> systemNameList, int rows, int page, Sort sort) {
		PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
		Page<ActRuDetail> pageList = actRuDetailRepository.findBySystemNameInAndAssigneeAndStatusAndDeletedFalse(systemNameList, assignee, status, pageable);
		return pageList;
	}

	@Override
	public ActRuDetail findByProcessInstanceIdAndAssignee(String processInstanceId, String assignee) {
		return actRuDetailRepository.findByProcessInstanceIdAndAssignee(processInstanceId, assignee);
	}

	@Override
	public List<ActRuDetail> findByProcessInstanceIdAndStatus(String processInstanceId, int status) {
		return actRuDetailRepository.findByProcessInstanceIdAndStatusOrderByCreateTimeAsc(processInstanceId, status);
	}

	@Override
	public List<ActRuDetail> findByProcessSerialNumber(String processSerialNumber) {
		return actRuDetailRepository.findByProcessSerialNumber(processSerialNumber);
	}

	@Override
	public ActRuDetail findByProcessSerialNumberAndAssignee(String processSerialNumber, String assignee) {
		return actRuDetailRepository.findByProcessSerialNumberAndAssignee(processSerialNumber, assignee);
	}

	@Override
	public List<ActRuDetail> findByProcessSerialNumberAndEnded(String processSerialNumber, boolean ended) {
		return actRuDetailRepository.findByProcessSerialNumberAndEnded(processSerialNumber, ended);
	}

	@Override
	public List<ActRuDetail> findByProcessSerialNumberAndStatus(String processSerialNumber, int status) {
		return actRuDetailRepository.findByProcessSerialNumberAndStatusOrderByCreateTimeAsc(processSerialNumber, status);
	}

	@Override
	public Page<ActRuDetail> findBySystemName(String systemName, int rows, int page, Sort sort) {
		PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
		Page<ActRuDetail> pageList = actRuDetailRepository.findBySystemNameAndStatusAndEndedFalse(systemName, 0, pageable);
		return pageList;
	}

	@Override
	public Page<ActRuDetail> findBySystemNameAndAssignee(String systemName, String assignee, int rows, int page, Sort sort) {
		PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
		Page<ActRuDetail> pageList = actRuDetailRepository.findBySystemNameAndAssigneeAndDeletedFalse(systemName, assignee, pageable);
		return pageList;
	}

	@Override
	public Page<ActRuDetail> findBySystemNameAndAssigneeAndEndedTrue(String systemName, String assignee, int rows, int page, Sort sort) {
		PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
		Page<ActRuDetail> pageList = actRuDetailRepository.findBySystemNameAndAssigneeAndEndedTrueAndDeletedFalseAndPlaceOnFileFalse(systemName, assignee, pageable);
		return pageList;
	}

	@Override
	public Page<ActRuDetail> findBySystemNameAndAssigneeAndStatus(String systemName, String assignee, int status, int rows, int page, Sort sort) {
		PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
		Page<ActRuDetail> pageList = null;
		if (0 == status) {
			pageList = actRuDetailRepository.findBySystemNameAndAssigneeAndStatusAndDeletedFalse(systemName, assignee, status, pageable);
		} else {
			pageList = actRuDetailRepository.findBySystemNameAndAssigneeAndStatusAndEndedFalseAndDeletedFalse(systemName, assignee, status, pageable);
		}
		return pageList;
	}

	@Override
	@Transactional(readOnly = false)
	public boolean placeOnFileByProcessSerialNumber(String processSerialNumber) {
		try {
			List<ActRuDetail> list = actRuDetailRepository.findByProcessSerialNumber(processSerialNumber);
			List<ActRuDetail> listTemp = new ArrayList<>();
			for (ActRuDetail actRuDetail : list) {
				actRuDetail.setPlaceOnFile(true);
				actRuDetail.setStatus(1);
				listTemp.add(actRuDetail);
			}
			actRuDetailRepository.saveAll(listTemp);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	@Transactional(readOnly = false)
	public boolean recoveryByProcessSerialNumber(String processSerialNumber) {
		try {
			List<ActRuDetail> list = actRuDetailRepository.findByProcessSerialNumber(processSerialNumber);
			List<ActRuDetail> listTemp = new ArrayList<>();
			for (ActRuDetail actRuDetail : list) {
				actRuDetail.setDeleted(false);
				listTemp.add(actRuDetail);
			}
			actRuDetailRepository.saveAll(listTemp);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	@Transactional(readOnly = false)
	public boolean recoveryTodoByProcessSerialNumber(String processSerialNumber, String todoPersonId) {
		try {
			List<ActRuDetail> list = actRuDetailRepository.findByProcessSerialNumber(processSerialNumber);
			List<ActRuDetail> listTemp = new ArrayList<>();
			for (ActRuDetail actRuDetail : list) {
				actRuDetail.setEnded(false);
				if (StringUtils.isEmpty(todoPersonId)) {
					actRuDetail.setStatus(0);
				} else {
					if (todoPersonId.equals(actRuDetail.getAssignee())) {
						actRuDetail.setStatus(0);
						actRuDetail.setLastTime(new Date());
					}
				}
				listTemp.add(actRuDetail);
			}
			actRuDetailRepository.saveAll(listTemp);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	@Transactional(readOnly = false)
	public boolean removeByProcessInstanceId(String processInstanceId) {
		try {
			List<ActRuDetail> list = actRuDetailRepository.findByProcessInstanceId(processInstanceId);
			actRuDetailRepository.deleteAll(list);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	@Transactional(readOnly = false)
	public boolean removeByProcessSerialNumber(String processSerialNumber) {
		try {
			List<ActRuDetail> list = actRuDetailRepository.findByProcessSerialNumber(processSerialNumber);
			actRuDetailRepository.deleteAll(list);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	@Transactional(readOnly = false)
	public boolean removeByProcessSerialNumberAndAssignee(String processSerialNumber, String assignee) {
		try {
			ActRuDetail actRuDetail = actRuDetailRepository.findByProcessSerialNumberAndAssignee(processSerialNumber, assignee);
			if (null != actRuDetail) {
				actRuDetailRepository.delete(actRuDetail);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	@Transactional(readOnly = false)
	public boolean revokePlaceOnFileByProcessSerialNumber(String processSerialNumber, String todoPersonId) {
		try {
			List<ActRuDetail> list = actRuDetailRepository.findByProcessSerialNumber(processSerialNumber);
			List<ActRuDetail> listTemp = new ArrayList<>();
			for (ActRuDetail actRuDetail : list) {
				actRuDetail.setPlaceOnFile(false);
				actRuDetail.setEnded(false);
				if (StringUtils.isEmpty(todoPersonId)) {
					actRuDetail.setStatus(0);
				} else {
					if (todoPersonId.equals(actRuDetail.getAssignee())) {
						actRuDetail.setStatus(0);
						actRuDetail.setLastTime(new Date());
					}
				}
				listTemp.add(actRuDetail);
			}
			actRuDetailRepository.saveAll(listTemp);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	@Transactional(readOnly = false)
	public boolean saveOrUpdate(ActRuDetail actRuDetail) {
		String processSerialNumber = actRuDetail.getProcessSerialNumber();
		String assignee = actRuDetail.getAssignee();
		try {
			ActRuDetail oldActRuDetail = this.findByProcessSerialNumberAndAssignee(processSerialNumber, assignee);
			if (null != oldActRuDetail) {
				oldActRuDetail.setLastTime(actRuDetail.getLastTime());
				oldActRuDetail.setStatus(actRuDetail.getStatus());
				if (StringUtils.isNotBlank(actRuDetail.getTaskId())) {
					oldActRuDetail.setTaskId(actRuDetail.getTaskId());
				}
				oldActRuDetail.setProcessInstanceId(actRuDetail.getProcessInstanceId());
				oldActRuDetail.setStarted(actRuDetail.isStarted());
				actRuDetailRepository.save(oldActRuDetail);
				return true;
			}

			Department dept = departmentApi.getDepartment(Y9LoginUserHolder.getTenantId(), actRuDetail.getDeptId());
			ActRuDetail newActRuDetail = new ActRuDetail();
			newActRuDetail.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
			newActRuDetail.setProcessSerialNumber(actRuDetail.getProcessSerialNumber());
			newActRuDetail.setAssignee(assignee);
			newActRuDetail.setAssigneeName(actRuDetail.getAssigneeName());
			newActRuDetail.setDeptId(actRuDetail.getDeptId());
			newActRuDetail.setDeptName(dept.getName());
			newActRuDetail.setCreateTime(actRuDetail.getCreateTime());
			newActRuDetail.setLastTime(actRuDetail.getLastTime());
			newActRuDetail.setProcessDefinitionKey(actRuDetail.getProcessDefinitionKey());
			newActRuDetail.setSystemName(actRuDetail.getSystemName());
			newActRuDetail.setStatus(actRuDetail.getStatus());
			newActRuDetail.setTaskId(actRuDetail.getTaskId());
			newActRuDetail.setStarted(actRuDetail.isStarted());
			newActRuDetail.setEnded(actRuDetail.isEnded());
			newActRuDetail.setItemId(actRuDetail.getItemId());
			newActRuDetail.setProcessInstanceId(actRuDetail.getProcessInstanceId());

			ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
			newActRuDetail.setItemId(processParam.getItemId());
			newActRuDetail.setSystemName(processParam.getSystemName());
			actRuDetailRepository.save(newActRuDetail);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	@Transactional(readOnly = false)
	public boolean syncByProcessInstanceId(String processInstanceId) {
		try {
			ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
			String systemName = processParam.getSystemName(), tenantId = Y9LoginUserHolder.getTenantId();
			List<HistoricTaskInstanceModel> htiList = historicTaskManager.findTaskByProcessInstanceIdOrByEndTimeAsc(tenantId, processInstanceId, "");
			ActRuDetail actRuDetail = null;
			String assignee = null;
			String owner = null;
			TaskModel taskTemp = null;
			for (HistoricTaskInstanceModel hti : htiList) {
				actRuDetail = new ActRuDetail();
				assignee = hti.getAssignee();
				if (StringUtils.isNotBlank(assignee)) {
					/**
					 * 1owner不为空，是恢复待办且恢复的人员不是办理人员的情况，要取出owner,并保存
					 * owner的Status为1，并判断当前taskId是不是正在运行，正在运行的话assignee的Status为0否则为1(因为恢复待办的时候，没有把历史任务的结束时间设为null)
					 */
					owner = hti.getOwner();
					if (StringUtils.isNotBlank(owner)) {
						/** 先保存owner */
						actRuDetail.setAssignee(owner);
						actRuDetail.setLastTime(hti.getEndTime());
						actRuDetail.setProcessDefinitionKey(hti.getProcessDefinitionId().split(":")[0]);
						actRuDetail.setSystemName(systemName);
						actRuDetail.setProcessInstanceId(hti.getProcessInstanceId());
						actRuDetail.setStatus(1);
						actRuDetail.setTaskId(hti.getId());
						actRuDetail.setStarted(true);
						actRuDetail.setEnded(false);
						this.saveOrUpdate(actRuDetail);

						/** 再保存assignee */
						taskTemp = taskManager.findById(tenantId, hti.getId());
						if (null != taskTemp) {
							actRuDetail.setStatus(0);
							actRuDetail.setLastTime(null);
						} else {
							actRuDetail.setStatus(1);
							actRuDetail.setLastTime(hti.getEndTime());
						}
						actRuDetail.setAssignee(assignee);
						actRuDetail.setProcessDefinitionKey(hti.getProcessDefinitionId().split(":")[0]);
						actRuDetail.setProcessInstanceId(hti.getProcessInstanceId());
						actRuDetail.setTaskId(hti.getId());
						this.saveOrUpdate(actRuDetail);
					} else {
						/**
						 * 2assignee不为null也有可能是恢复待办的人员是当前任务的办理人，这个时候要查出当前任务是否正在运行，正在运行
						 * Status为0，lastTime为null;当前任务不存在，Status为1，，lastTime为endTime
						 */
						taskTemp = taskManager.findById(tenantId, hti.getId());
						if (null != taskTemp) {
							actRuDetail.setStatus(0);
							actRuDetail.setLastTime(null);
						} else {
							actRuDetail.setStatus(1);
							actRuDetail.setLastTime(hti.getEndTime());
						}
						actRuDetail.setAssignee(assignee);
						actRuDetail.setProcessDefinitionKey(hti.getProcessDefinitionId().split(":")[0]);
						actRuDetail.setProcessInstanceId(hti.getProcessInstanceId());
						actRuDetail.setTaskId(hti.getId());
						this.saveOrUpdate(actRuDetail);
					}
				} else {
					/** 2办理人为空，说明是区长办件，可以从历史的参与人查找对应任务的办理人 */
					taskTemp = taskManager.findById(tenantId, hti.getId());
					if (null != taskTemp) {
						actRuDetail.setStatus(0);
						actRuDetail.setLastTime(null);
					} else {
						actRuDetail.setStatus(1);
						actRuDetail.setLastTime(hti.getEndTime());
					}
					List<IdentityLinkModel> identityLinkList = new ArrayList<>();
					try {
						identityLinkList = identityManager.getIdentityLinksForTask(tenantId, hti.getId());
					} catch (Exception e) {
						e.printStackTrace();
					}
					for (IdentityLinkModel il : identityLinkList) {
						if (StringUtils.isNotBlank(il.getUserId()) && "assignee".equals(il.getType())) {
							assignee = il.getUserId();
							break;
						}
					}
					actRuDetail.setAssignee(assignee);
					actRuDetail.setProcessDefinitionKey(hti.getProcessDefinitionId().split(":")[0]);
					actRuDetail.setProcessInstanceId(hti.getProcessInstanceId());
					actRuDetail.setTaskId(hti.getId());
					this.saveOrUpdate(actRuDetail);
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}