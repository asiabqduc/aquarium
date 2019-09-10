/**
 * 
 */
package net.sunrise.runnable;

import javax.inject.Inject;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import net.sunrise.common.CommonUtility;
import net.sunrise.framework.runnable.RunnableBase;
import net.sunrise.manager.system.SystemSequenceManager;

/**
 * @author bqduc
 *
 */
@Component
@Scope("prototype")
public class UpdateSystemSequenceThread extends RunnableBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4749460145500847138L;

	@Inject 
	private SystemSequenceManager systemSequenceManager;

	private String sequentialNumber;

	public UpdateSystemSequenceThread(String sequentialNumber){
		this.sequentialNumber = sequentialNumber;
	}

	@Override
	public void execute() {
		log.info("Start to run system sequence updating thread....");
		//System.out.println("SystemSequenceService: [" + systemSequenceService + "]");
		syncPersistenceServce();
		log.info("System sequence updating thread is done");
	}

	private void syncPersistenceServce(){
		if (CommonUtility.isEmpty(this.sequentialNumber)){
			log.info("There is an empty system sequence object");
			return;
		}

		systemSequenceManager.registerSequence(this.sequentialNumber);
	}
}
