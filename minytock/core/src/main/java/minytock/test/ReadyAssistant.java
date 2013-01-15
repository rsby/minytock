package minytock.test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import minytock.Minytock;

public class ReadyAssistant {
	
	List<Field> mockedFields = new ArrayList<Field>();
	
	public ReadyAssistant(Class<?> testClass, Class<? extends Annotation> ... annotations) {
		for (Field field : testClass.getDeclaredFields()) {
			for (Class<? extends Annotation> annotationClass : annotations) {
				if (field.isAnnotationPresent(annotationClass)) {
					field.setAccessible(true);
					mockedFields.add(field);
					break;
				}
			}
		}
	}
	
	public void prepare(Object testInstance) throws IllegalArgumentException, IllegalAccessException {
		for (Field field : mockedFields) {
			Object value = field.get(testInstance);
			if (value == null) {
				field.set(testInstance, Minytock.newEmptyMock(field.getType()));
			} else {
				field.set(testInstance, Minytock.prepare(value));
			}
		}
	}
	
	public void afterBefores(Object testInstance) throws IllegalArgumentException, IllegalAccessException {
		for (Field field : mockedFields) {
			Object value = field.get(testInstance);
			field.set(testInstance, Minytock.prepare(value));
		}
	}

}
