package com.qcadoo.mes.model.search;

import org.apache.commons.beanutils.PropertyUtils;

import com.qcadoo.mes.api.Entity;
import com.qcadoo.mes.internal.DefaultEntity;
import com.qcadoo.mes.model.FieldDefinition;
import com.qcadoo.mes.model.search.restrictions.internal.BelongsToRestriction;
import com.qcadoo.mes.model.search.restrictions.internal.IsNotNullRestriction;
import com.qcadoo.mes.model.search.restrictions.internal.IsNullRestriction;
import com.qcadoo.mes.model.search.restrictions.internal.LikeRestriction;
import com.qcadoo.mes.model.search.restrictions.internal.SimpleRestriction;

/**
 * @apiviz.uses com.qcadoo.mes.core.data.search.Restriction
 */
public final class Restrictions {

    private Restrictions() {
    }

    public static Restriction eq(final FieldDefinition fieldDefinition, final Object expectedValue) {
        Entity validatedEntity = new DefaultEntity("", "");
        Object value = validateValue(fieldDefinition, expectedValue, validatedEntity);
        if (!validatedEntity.getErrors().isEmpty()) {
            return null;
        }
        return createEqRestriction(fieldDefinition.getName(), value);
    }

    public static Restriction eq(final String fieldName, final String expectedValue) {
        return createEqRestriction(fieldName, expectedValue);
    }

    private static Restriction createEqRestriction(final String fieldName, final Object expectedValue) {
        if (expectedValue instanceof String && ((String) expectedValue).matches(".*[\\*%\\?_].*")) {
            String preperadValue = ((String) expectedValue).replace('*', '%').replace('?', '_');
            return new LikeRestriction(fieldName, preperadValue);
        }
        return new SimpleRestriction(fieldName, expectedValue, RestrictionOperator.EQ);
    }

    public static Restriction belongsTo(final FieldDefinition fieldDefinition, final Object entityOrId) {
        if (entityOrId instanceof Long) {
            return new BelongsToRestriction(fieldDefinition.getName(), (Long) entityOrId);
        } else {
            try {
                return new BelongsToRestriction(fieldDefinition.getName(), (Long) PropertyUtils.getProperty(entityOrId, "id"));
            } catch (Exception e) {
                throw new IllegalStateException("cannot get value of the property: " + entityOrId.getClass().getSimpleName()
                        + ", id", e);
            }
        }
    }

    public static Restriction idRestriction(final Long id, final RestrictionOperator restrictionOperator) {
        return new SimpleRestriction("id", id, restrictionOperator);
    }

    public static Restriction ge(final FieldDefinition fieldDefinition, final Object expectedValue) {
        Entity validatedEntity = new DefaultEntity("", "");
        Object value = validateValue(fieldDefinition, expectedValue, validatedEntity);
        if (!validatedEntity.getErrors().isEmpty()) {
            return null;
        }
        return new SimpleRestriction(fieldDefinition.getName(), value, RestrictionOperator.GE);
    }

    public static Restriction gt(final FieldDefinition fieldDefinition, final Object expectedValue) {
        Entity validatedEntity = new DefaultEntity("", "");
        Object value = validateValue(fieldDefinition, expectedValue, validatedEntity);
        if (!validatedEntity.getErrors().isEmpty()) {
            return null;
        }
        return new SimpleRestriction(fieldDefinition.getName(), value, RestrictionOperator.GT);
    }

    public static Restriction isNotNull(final FieldDefinition fieldDefinition) {
        return new IsNotNullRestriction(fieldDefinition.getName());
    }

    public static Restriction isNull(final FieldDefinition fieldDefinition) {
        return new IsNullRestriction(fieldDefinition.getName());
    }

    public static Restriction le(final FieldDefinition fieldDefinition, final Object expectedValue) {
        Entity validatedEntity = new DefaultEntity("", "");
        Object value = validateValue(fieldDefinition, expectedValue, validatedEntity);
        if (!validatedEntity.getErrors().isEmpty()) {
            return null;
        }
        return new SimpleRestriction(fieldDefinition.getName(), value, RestrictionOperator.LE);
    }

    public static Restriction lt(final FieldDefinition fieldDefinition, final Object expectedValue) {
        Entity validatedEntity = new DefaultEntity("", "");
        Object value = validateValue(fieldDefinition, expectedValue, validatedEntity);
        if (!validatedEntity.getErrors().isEmpty()) {
            return null;
        }
        return new SimpleRestriction(fieldDefinition.getName(), value, RestrictionOperator.LT);
    }

    public static Restriction ne(final FieldDefinition fieldDefinition, final Object expectedValue) {
        Entity validatedEntity = new DefaultEntity("", "");
        Object value = validateValue(fieldDefinition, expectedValue, validatedEntity);
        if (!validatedEntity.getErrors().isEmpty()) {
            return null;
        }
        return new SimpleRestriction(fieldDefinition.getName(), value, RestrictionOperator.NE);
    }

    private static Object validateValue(final FieldDefinition fieldDefinition, final Object value, final Entity validatedEntity) {
        Object fieldValue = value;
        if (fieldValue != null && !fieldDefinition.getType().getType().isInstance(fieldValue)) {
            if (fieldValue instanceof String) {
                fieldValue = fieldDefinition.getType().toObject(fieldDefinition, fieldValue, validatedEntity);
            } else {
                validatedEntity.addError(fieldDefinition, "core.validation.error.wrongType", fieldValue.getClass()
                        .getSimpleName(), fieldDefinition.getType().getType().getSimpleName());
                return null;
            }
        }
        return fieldValue;
    }
}
