package net.sunrise.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.core.io.ClassPathResource;

import lombok.Builder;
import net.sunrise.common.ListUtility;
import net.sunrise.exceptions.EcosysException;
import net.sunrise.framework.component.RootComponent;
import net.sunrise.model.SimpleDataContainer;
import net.sunrise.model.base.IDataContainer;


@Builder
public class GlobalDataServicesRepository extends RootComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4103738524977948563L;

	public IDataContainer<String> readCsvFile(InputStream inputStream, boolean processColumnHeaders, String separator) throws EcosysException {
		IDataContainer<String> dataContainer = null;
		BufferedReader br = null;
		String line = null;
		try {
			br = new BufferedReader(new InputStreamReader(inputStream));

			dataContainer = SimpleDataContainer.builder().build();

			// Skip first line that only holds headers.
			if (true==processColumnHeaders){
				line = br.readLine();
				dataContainer.addHeaderItems(ListUtility.arraysAsList(line.split(separator)));
			}

			while ((line = br.readLine()) != null) {
				dataContainer.addDataItems(ListUtility.arraysAsList(line.split(separator)));
			}
		} catch (IOException e) {
			throw new EcosysException(e);
		} finally {
			try {
				// Release resources.
				br.close();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
		return dataContainer;
	}

	public IDataContainer<String> readCsvFile(String fileName, boolean processColumnHeaders, String separator) throws EcosysException {
		IDataContainer<String> dataContainer = null;
		ClassPathResource classPathResource = null;
		try {
			classPathResource = new ClassPathResource(fileName);
			dataContainer = this.readCsvFile(classPathResource.getInputStream(), processColumnHeaders, separator);
		} catch (IOException e) {
			throw new EcosysException(e);
		} 
		return dataContainer;
	}

	public IDataContainer<String> readCsvFileExt(String fileName, boolean columnHeadersFlag, String separator) throws EcosysException {
		IDataContainer<String> dataContainer = null;
		ClassPathResource classPathResource = null;
		BufferedReader br = null;
		String line = null;
		try {
			classPathResource = new ClassPathResource(fileName);
			br = new BufferedReader(new InputStreamReader(classPathResource.getInputStream()));

			dataContainer = SimpleDataContainer.builder().build();

			// Skip first line that only holds headers.
			if (true==columnHeadersFlag){
				line = br.readLine();
				dataContainer.addHeaderItems(ListUtility.arraysAsList(line.split(separator)));
			}

			while ((line = br.readLine()) != null) {
				dataContainer.addDataItems(ListUtility.arraysAsList(line.split(separator)));
			}
		} catch (IOException e) {
			throw new EcosysException(e);
		} finally {
			try {
				// Release resources.
				br.close();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
		return dataContainer;
	}

}
