package com.tangclub.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "survey")
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 姓名 */
    @Column(length = 50)
    private String name;

    /** 性别 */
    @Column(length = 10)
    private String gender;

    /** 年龄段 */
    @Column(length = 20)
    private String age;

    /** 手机号（前3****后4 格式） */
    @Column(length = 20)
    private String phone;

    /** 置业顾问 */
    @Column(length = 50)
    private String advisor;

    /** 居住区域 */
    @Column(name = "living_area", length = 50)
    private String livingArea;

    /** 工作区域 */
    @Column(name = "work_area", length = 50)
    private String workArea;

    /** 所属行业 */
    @Column(length = 50)
    private String industry;

    /** 职业 */
    @Column(length = 50)
    private String occupation;

    /** 目前居住户型面积 */
    @Column(name = "floor_area", length = 30)
    private String floorArea;

    /** 意向户型面积 */
    @Column(name = "preferred_area", length = 30)
    private String preferredArea;

    /** 意向户型结构（多选，逗号分隔） */
    @Column(name = "unit_layout_preference", length = 200)
    private String unitLayoutPreference;

    /** 置业次数 */
    @Column(name = "property_purchase_count", length = 30)
    private String propertyPurchaseCount;

    /** 对晓棠的认可点（多选，逗号分隔） */
    @Column(name = "accreditation_metrics", length = 500)
    private String accreditationMetrics;

    /** 关注的项目（多选，逗号分隔） */
    @Column(name = "tracked_items", length = 500)
    private String trackedItems;

    /** 是否了解城西CID产城更新规划 */
    @Column(name = "master_plan_review", length = 30)
    private String masterPlanReview;

    /** 是否了解西川一校三区的布局 */
    @Column(name = "xichuan_campus_layout", length = 30)
    private String xichuanCampusLayout;

    /** 感兴趣的活动（多选，逗号分隔） */
    @Column(name = "event_interest", length = 500)
    private String eventInterest;

    /** 建议 */
    @Column(name = "customer_interests", columnDefinition = "TEXT")
    private String customerInterests;

    /** 会员编号 */
    @Column(name = "member_number", length = 50, unique = true)
    private String memberNumber;

    /** 创建时间 */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
