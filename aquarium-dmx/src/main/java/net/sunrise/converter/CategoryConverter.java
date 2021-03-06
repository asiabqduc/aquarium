/**
 * 
 */
package net.sunrise.converter;

import javax.inject.Inject;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import net.sunrise.domain.entity.general.Category;
import net.sunrise.manager.catalog.CategoryManager;

/**
 * @author bqduc
 *
 */
@Component
public class CategoryConverter implements Converter<String, Category> {
	@Inject
	private CategoryManager categoryService;

	public CategoryConverter(CategoryManager categoryService){
		this.categoryService = categoryService;
	}

	@Override
	public Category convert(String param) {
		Long objectId = new Long(param);
		Category category = categoryService.get(objectId);
		return category;
	}

}
