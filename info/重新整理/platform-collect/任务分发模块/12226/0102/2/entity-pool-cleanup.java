// ObjectPoolConfig.java
@Configuration
public class ObjectPoolConfig {
    @Bean
    public ObjectPool<UriEntity> uriEntityPool() {
        return new GenericObjectPool<>(new BasePooledObjectFactory<>() {
            @Override
            public UriEntity create() {
                return new UriEntity();
            }

            @Override
            public PooledObject<UriEntity> wrap(UriEntity entity) {
                return new DefaultPooledObject<>(entity);
            }
            
            @Override
            public void passivateObject(PooledObject<UriEntity> p) {
                UriEntity entity = p.getObject();
                // 使用反射清空所有字段，包括父类字段
                resetAllFields(entity);
            }

            private void resetAllFields(Object object) {
                Class<?> clazz = object.getClass();
                while (clazz != null) {
                    for (Field field : clazz.getDeclaredFields()) {
                        try {
                            field.setAccessible(true);
                            // 跨过static和final字段
                            if (!Modifier.isStatic(field.getModifiers()) && 
                                !Modifier.isFinal(field.getModifiers())) {
                                field.set(object, null);
                            }
                        } catch (Exception e) {
                            // 忽略不可设置的字段
                        }
                    }
                    clazz = clazz.getSuperclass();
                }
                
                // 重置基本类型字段为默认值
                ((BaseEntity) object).setVersion(0L);
                ((BaseEntity) object).setDeleted(false);
            }
        });
    }
}