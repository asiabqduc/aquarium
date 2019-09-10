package net.sunrise.domain.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Digits;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.sunrise.domain.GeneralStatus;
import net.sunrise.domain.entity.admin.UserAccount;
import net.sunrise.domain.entity.crx.Team;
import net.sunrise.domain.entity.general.Currency;
import net.sunrise.domain.enums.CRXGeneralType;
import net.sunrise.framework.entity.BizObjectBase;

/**
 * A schedule or BRX.
 */
@Builder
@NoArgsConstructor 
@AllArgsConstructor
@Entity
@Table(name = "brx_schedule")
@EqualsAndHashCode(callSuper=false)
public class Schedule extends BizObjectBase{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7649888120546451221L;

	@ManyToOne(targetEntity=BusinessPackage.class, fetch=FetchType.EAGER)
	@JoinColumn(name = "business_package_id", insertable=false, updatable=false)
	private BusinessPackage businessPackage;

	@Column(name = "name", nullable = false, unique=true, length=200)
	private String name;

	@ManyToOne(targetEntity=UserAccount.class, fetch=FetchType.EAGER)
	@JoinColumn(name = "assigned_account_id")
	private UserAccount assignedTo;

	@Column(name="status")
  @Enumerated(EnumType.ORDINAL)
	private GeneralStatus status;

	@ManyToOne(targetEntity=Team.class, fetch=FetchType.EAGER)
	@JoinColumn(name = "team_id", insertable=false, updatable=false)
	private Team team;

  @Column(name="start_date")
	@DateTimeFormat(iso = ISO.DATE)
  private Date startDate;

  @Column(name="end_date")
	@DateTimeFormat(iso = ISO.DATE)
  private Date endDate;

	@Column(name="campaign_type_id")
  @Enumerated(EnumType.ORDINAL)
	private CRXGeneralType campaignType;

	@ManyToOne(targetEntity=Currency.class, fetch=FetchType.EAGER)
	@JoinColumn(name = "currency_id", insertable=false, updatable=false)
	private Currency currency;

	@Column(name = "impressions")
	private Integer impressions;

	@Digits(integer=12, fraction=2)
	@Column(name = "budget")
	private BigDecimal budget;

	@Digits(integer=12, fraction=2)
	@Column(name = "actual_cost")
	private BigDecimal actualCost;

	@Digits(integer=12, fraction=2)
	@Column(name = "expected_revenue")
	private BigDecimal expectedRevenue;

	@Digits(integer=12, fraction=2)
	@Column(name = "expected_cost")
	private BigDecimal expectedCost;

	@Column(name = "content", columnDefinition="TEXT")
	private String content;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UserAccount getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(UserAccount assignedTo) {
		this.assignedTo = assignedTo;
	}

	public GeneralStatus getStatus() {
		return status;
	}

	public void setStatus(GeneralStatus status) {
		this.status = status;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public CRXGeneralType getCampaignType() {
		return campaignType;
	}

	public void setCampaignType(CRXGeneralType campaignType) {
		this.campaignType = campaignType;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public Integer getImpressions() {
		return impressions;
	}

	public void setImpressions(Integer impressions) {
		this.impressions = impressions;
	}

	public BigDecimal getBudget() {
		return budget;
	}

	public void setBudget(BigDecimal budget) {
		this.budget = budget;
	}

	public BigDecimal getActualCost() {
		return actualCost;
	}

	public void setActualCost(BigDecimal actualCost) {
		this.actualCost = actualCost;
	}

	public BigDecimal getExpectedRevenue() {
		return expectedRevenue;
	}

	public void setExpectedRevenue(BigDecimal expectedRevenue) {
		this.expectedRevenue = expectedRevenue;
	}

	public BigDecimal getExpectedCost() {
		return expectedCost;
	}

	public void setExpectedCost(BigDecimal expectedCost) {
		this.expectedCost = expectedCost;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
