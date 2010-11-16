/**
 * ********************************************************************
 * Code developed by amazing QCADOO developers team.
 * Copyright (c) Qcadoo Limited sp. z o.o. (2010)
 * ********************************************************************
 */

package com.qcadoo.mes.view.containers;

/**
 * View value of Form component.
 * 
 * @see com.qcadoo.mes.view.containers.FormComponent
 * @see com.qcadoo.mes.view.ViewValue
 */
public final class FormValue {

    private Long id;

    private String header;

    private String headerEntityIdentifier;

    private boolean valid = true;

    public FormValue() {
    }

    public FormValue(final Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(final String header) {
        this.header = header;
    }

    public void setHeaderEntityIdentifier(final String headerEntityIdentifier) {
        this.headerEntityIdentifier = headerEntityIdentifier;
    }

    public String getHeaderEntityIdentifier() {
        return headerEntityIdentifier;
    }

    public void setValid(final boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return valid;
    }

    @Override
    public String toString() {
        if (id == null) {
            return ""; // FIXME masz toString cannot return null
        }
        return id.toString();
    }

}
