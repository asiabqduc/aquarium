package com.sunrise.stock;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;

import com.sunrise.domain.BaseEntity;

import java.util.List;

/**
 * Created on January, 2018
 *
 * @author bqduc
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Tag extends BaseEntity {

	private String reference;

	@Lob
	private String description;

	private String color;

	@ManyToMany
	private List<Service> services;

	//TODO add service collection and annotate as @ManyToMany

}
