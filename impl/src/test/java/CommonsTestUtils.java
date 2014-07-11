
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Some generic utilities used in testing
 * 
 * @author <a href="mailto:andrew.calafato@ixaris.com">andrew.calafato</a>
 * @author <a href="mailto:aldrin.seychell@ixaris.com">aldrin.seychell</a>
 */
public class CommonsTestUtils {
    
    private static final Logger LOG = LoggerFactory.getLogger(CommonsTestUtils.class);

    public static void sleep(final long millis) {
        try {
            Thread.sleep(millis);
        } catch (final InterruptedException e) {
            // Do nothing
        }
    }

    public static void sleepSeconds(final long seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (final InterruptedException e) {
            // Do nothing
        }
    }
    
    @SafeVarargs
    public static void initSingletonClassWithMocks(final Object clazzWithMocksInstance, final Class<?>... classes) {
        for (final Class<?> clazz : classes) {
            initSingletonClassWithMocks(clazzWithMocksInstance, clazz);
        }
    }
    
    public static <T> T constructInstance(final Class<T> clazz) {
        try {
            // Retrieve private constructor and initialise the class using this constructor
            final Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            final T instance = constructor.newInstance();
            constructor.setAccessible(false);
            return instance;
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public static <T> T initSingletonClassWithMocks(final Object clazzWithMocksInstance, final Class<T> clazz) {
        try {
            // Retrieve private constructor and initialise the class using this constructor
            final T singletonInstance = constructInstance(clazz);
            
            // Instantiate fields marked with @Autowired with the value of the mocks from the clazzWithMocks
            final Field[] clazzFields = clazz.getDeclaredFields();
            for (final Field field : clazzFields) {
                final Autowired annotation = field.getAnnotation(Autowired.class);
                final PersistenceContext annotationPC = field.getAnnotation(PersistenceContext.class);
                if (annotation == null && annotationPC == null) {
                    continue;
                }
                
                final Object mockValue = getMockForType(clazzWithMocksInstance, field.getType());
                
                field.setAccessible(true);
                field.set(singletonInstance, mockValue);
                field.setAccessible(false);
            }
            
            return singletonInstance;
            
        } catch (IllegalAccessException | SecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> T initSingletonClassWithMocks(final Object clazzWithMocksInstance, final Class<T> clazz,
                                                    final Class<?>[] constructorParameterTypes,
                                                    final Object... initArgs) {
        try {
            // Retrieve private constructor and initialise the class using this constructor
            final Constructor<T> constructor = clazz.getDeclaredConstructor(constructorParameterTypes);
            constructor.setAccessible(true);
            final T singletonInstance = constructor.newInstance(initArgs);
            constructor.setAccessible(false);
            
            // Instantiate fields marked with @Autowired with the value of the mocks from the clazzWithMocks
            final Field[] clazzFields = clazz.getDeclaredFields();
            for (final Field field : clazzFields) {
                final Autowired annotation = field.getAnnotation(Autowired.class);
                if (annotation == null) {
                    continue;
                }
                
                final Object mockValue = getMockForType(clazzWithMocksInstance, field.getType());
                
                field.setAccessible(true);
                field.set(singletonInstance, mockValue);
                field.setAccessible(false);
            }
            
            return singletonInstance;
            
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException(e);
        }
    }
    
    private static Object getMockForType(final Object clazzWithMocksInstance, final Class<?> mockType) throws IllegalAccessException {
        
        final Field[] fields = clazzWithMocksInstance.getClass().getDeclaredFields();
        for (final Field field : fields) {
            if (field.getType().equals(mockType)) {
                field.setAccessible(true);
                final Object object = field.get(clazzWithMocksInstance);
                field.setAccessible(false);
                return object;
            }
        }
        
        LOG.debug("Unable to find mock instance to inject for type [" + mockType.getName() + "]");
        return null;
    }
    
    
    public static Object reflectivelyGetField(final Object o, final String fieldName) {
        final Class<?> c = o.getClass();
        Field field;
        try {
            field = getDeclaredField(c, fieldName);
            field.setAccessible(true);
            final Object retVal = field.get(o);
            field.setAccessible(false);
            return retVal;
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }  
    
    public static void reflectivelySetField(final Object o, final String fieldName, final Object value) {
        final Class<?> c = o.getClass();
        Field field;
        try {
            field = getDeclaredField(c, fieldName);
            field.setAccessible(true);
            field.set(o, value);
            field.setAccessible(false);
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }  
    
    /**
     * Validates the string parameter with the passed regular expression
     * 
     * @param str
     * @param regex
     * @return
     */
    public static boolean validateString(final String str, final String regex) {
		final Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		return m.matches();
	}
    
    private static Field getDeclaredField(final Class<?> c, final String fieldName) {
        Class<?> x = c;
        Field f = null;
        do {
            try {
                f = x.getDeclaredField(fieldName);
            } catch (final NoSuchFieldException e) {
                // ignore
            }
            x = x.getSuperclass();
        } while(x != null && f == null);
        
        return f;
    }
    
    public static Object reflectivelyCallMethod(final Object o, 
                                                final String methodName,
                                                final Class<?>[] paramTypes,
                                                final Object... params) {
        final Class<?> c = o.getClass();
        Method method;
        try {
            method = getDeclaredMethod(c, methodName, paramTypes);
            method.setAccessible(true);
            final Object retVal = method.invoke(o, params);
            method.setAccessible(false);
            return retVal;
            
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }
    
    public static Object reflectivelyCallStaticMethod(final Class<?> c, 
                                                      final String methodName, 
                                                      final Class<?>[] paramTypes,
                                                      final Object... params) {
        Method method;
        try {
            method = getDeclaredMethod(c, methodName, paramTypes);
            method.setAccessible(true);
            final Object retVal = method.invoke(null, params);
            method.setAccessible(false);
            return retVal;
            
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }
    
    private static Method getDeclaredMethod(final Class<?> c, final String methodName, final Class<?>[] params) {
        Class<?> x = c;
        Method m = null;
        do {
            try {
                m = x.getDeclaredMethod(methodName, params);
            } catch (final NoSuchMethodException e) {
                // ignore
            }
            x = x.getSuperclass();
        } while(x != null && m == null);
        
        return m;
    }

}
