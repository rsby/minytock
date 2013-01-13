package minytock;

import org.springframework.beans.factory.annotation.Autowired;

public class SomeService {
	
	@Autowired
	SomeOtherService service;
	
	public void doSomething() {
		System.out.println("asd" + service.getSomething().getClass());
	}
}
