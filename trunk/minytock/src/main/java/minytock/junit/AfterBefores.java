package minytock.junit;

import minytock.test.ReadyAssistant;

import org.junit.runners.model.Statement;

public class AfterBefores extends Statement {
	
	Statement next; 
	ReadyAssistant assistant;
	Object test;
	
	public AfterBefores(Statement next, ReadyAssistant assistant, Object test) {
		this.next = next;
		this.assistant = assistant;
		this.test = test;
	}

	@Override
	public void evaluate() throws Throwable {
		assistant.afterBefores(test);
		next.evaluate(); //this executes the test
	}

}
