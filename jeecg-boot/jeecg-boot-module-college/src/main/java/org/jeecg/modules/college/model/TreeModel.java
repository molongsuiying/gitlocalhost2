package org.jeecg.modules.college.model;

import org.jeecg.modules.college.entity.College;

import org.jeecg.modules.college.entity.Department;
import org.jeecg.modules.college.vo.BunchVo;
import org.jeecg.modules.college.vo.DepartVo;
import org.jeecg.modules.college.vo.MajorVo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
  * 树形列表用到
 */
public class TreeModel implements Serializable {
	
	private static final long serialVersionUID = 4013193970046502756L;

	private String id;

	private String key;
	
	private String title;

	private String slotTitle;
	
	private boolean isLeaf;

	private Integer level;

	private Integer sort;


	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private List<TreeModel> children;

	public List<TreeModel> getChildren() {
		return children;
	}

	public void setChildren(List<TreeModel> children) {
		this.children = children;
	}

	public TreeModel() {
		
	}
	
	public TreeModel(College college) {
		this.key = college.getId();
		this.level = 0;
		this.title = college.getCollegeName();
		this.slotTitle =  college.getCollegeName();
		this.value = college.getId();
		this.isLeaf = college.getHasDeparts() < 1;
		this.label = college.getCollegeName();

	}

	public TreeModel(DepartVo department) {

		this.key = department.getId();
		this.level = 1;
		this.title = department.getDepartmentName();
		this.slotTitle =  department.getDepartmentName();
		this.value = department.getId();
		this.isLeaf = department.getMajorNum() < 1;
		this.parentId = department.getCollegeId();
		this.label = department.getDepartmentName();
		this.id = this.key;
	}

	public TreeModel(Department department) {
		this.key = department.getId();
		this.level = 1;
		this.title = department.getDepartmentName();
		this.slotTitle =  department.getDepartmentName();
		this.value = department.getId();
		this.parentId = department.getCollegeId();
		this.label = department.getDepartmentName();
		this.id = this.key;
	}

	public TreeModel(MajorVo majorVo) {
		this.key = majorVo.getId();
		this.level = 2;
		this.title = majorVo.getMajorName();
		this.slotTitle =  majorVo.getMajorName();
		this.value = majorVo.getId();
		this.parentId = majorVo.getDepartmentId();
		this.isLeaf = majorVo.getBunchNum() < 1;
		this.label = majorVo.getMajorName();
		this.id = this.key;
	}

	public TreeModel(BunchVo bunchVo) {
		this.key = bunchVo.getId();
		this.level = 3;
		this.title = bunchVo.getBunchName();
		this.slotTitle =  bunchVo.getDescribe();
		this.value = bunchVo.getId();
		this.parentId = bunchVo.getMajorId();
		this.isLeaf = true;
		this.label =  bunchVo.getMajorName();
		this.sort = bunchVo.getSort();

	}

	 
	private String parentId;
		
	private String label;
	
	private String value;
	
	
	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	public String getSlotTitle() {
		return slotTitle;
	}

	public void setSlotTitle(String slotTitle) {
		this.slotTitle = slotTitle;
	}


	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
}
