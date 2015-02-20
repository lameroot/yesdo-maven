package ru.yesdo.model;


import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StringType;
import org.hibernate.usertype.UserType;
import org.springframework.data.neo4j.fieldaccess.DynamicProperties;
import org.springframework.data.neo4j.fieldaccess.DynamicPropertiesContainer;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * User: Krainov
 * Date: 20.02.2015
 * Time: 15:54
 */
public class DynamicPropertiesType implements UserType{

    @Override
    public int[] sqlTypes() {
        return new int[]{
                StringType.INSTANCE.sqlType()
        };
    }

    @Override
    public Class returnedClass() {
        return DynamicProperties.class;
    }

    @Override
    public boolean equals(Object o, Object o1) throws HibernateException {
        return false;
    }

    @Override
    public int hashCode(Object o) throws HibernateException {
        return 0;
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] strings, SessionImplementor sessionImplementor, Object o) throws HibernateException, SQLException {
        String json = StringType.INSTANCE.nullSafeGet(resultSet, strings[0], sessionImplementor);
        return null != json ? new DynamicPropertiesContainer(JsonUtil.toSafeObject(Map.class, json)) : null;
    }

    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, Object o, int i, SessionImplementor sessionImplementor) throws HibernateException, SQLException {
        if ( null == o ) {
            StringType.INSTANCE.set(preparedStatement, null, i, sessionImplementor);
        }
        else {
            final DynamicProperties dynamicProperties = (DynamicProperties)o;
            StringType.INSTANCE.set(preparedStatement, JsonUtil.toSafeJson(dynamicProperties.asMap()),i,sessionImplementor);
        }
    }

    @Override
    public Object deepCopy(Object o) throws HibernateException {
        return null;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object o) throws HibernateException {
        return null;
    }

    @Override
    public Object assemble(Serializable serializable, Object o) throws HibernateException {
        return null;
    }

    @Override
    public Object replace(Object o, Object o1, Object o2) throws HibernateException {
        return null;
    }
}
