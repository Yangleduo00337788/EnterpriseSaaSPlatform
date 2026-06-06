package com.flowcloud.approval.dto;

import lombok.Data;

import java.util.List;

@Data
public class FlowNodeDTO {

    private Integer index;
    private String name;
    /** 节点类型: approval（顺序/审批）/ self（自审）/ cc（抄送） */
    private String type;
    /** 会签模式: sequential（顺序，默认）/ countersign（会签，全部通过）/ or-sign（或签，任一通过） */
    private String nodeMode;
    /** 审批人来源: users（指定用户）/ dept_leader（部门负责人）/ manager（直属上级） */
    private String approverSource;
    private List<Long> approverIds;
    private String condition;
}
