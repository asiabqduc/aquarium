/**
 * 
 */
package net.sunrise.domain.entity.aquacultural;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.sunrise.domain.entity.general.Category;
import net.sunrise.domain.entity.general.MeasureUnit;
import net.sunrise.framework.entity.BizObjectBase;

/**
 * Food of shrimp
 * @author bqduc
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "livestock_feed")
public class LivestockFeed extends BizObjectBase {
	@Column(name = "code", unique = true)
	private String code;

	@Column(name = "name")
	private String name;

	@ManyToOne
	@JoinColumn(name = "unit_id")
	private MeasureUnit unit;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

	@Column(name = "comments")
	private String comments;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MeasureUnit getUnit() {
		return unit;
	}

	public void setUnit(MeasureUnit unit) {
		this.unit = unit;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
}