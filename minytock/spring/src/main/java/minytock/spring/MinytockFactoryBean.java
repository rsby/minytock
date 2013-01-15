package minytock.spring;

import minytock.Minytock;

import org.springframework.beans.factory.FactoryBean;

/**
 *
 * <p>
 * User: reesbyars
 * Date: 9/10/12
 * Time: 11:21 PM
 * <p/>
 * MinytockFactoryBean
 */
public class MinytockFactoryBean<C> implements FactoryBean<C> {

    private Class<C> type;
    public MinytockFactoryBean(Class<C> type){
        this.type = type;
    }

    @Override
    public C getObject() throws Exception {
        return Minytock.newEmptyMock(type);
    }

    @Override
    public Class<C> getObjectType() {
        return type;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
    
    public static <C> MinytockFactoryBean<C> getFor(Class<C> type) {
    	return new MinytockFactoryBean<C>(type);
    }

}
