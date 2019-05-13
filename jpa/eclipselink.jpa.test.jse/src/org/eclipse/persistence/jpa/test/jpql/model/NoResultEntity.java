/*******************************************************************************
 * Copyright (c) 2019 Oracle, IBM Corporation and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     IBM - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.test.jpql.model;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * The purpose of this class is for querying an Entity table with no results.
 * Only use this Entity class if your test does not require populating the table.
 */
@Entity
public class NoResultEntity {

    @Id
    private int id;

    private String content;

    @Basic
    private int primitive;

    private Integer wrapper;

    NoResultEntity() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPrimitive() {
        return primitive;
    }

    public void setPrimitive(int primitive) {
        this.primitive = primitive;
    }

    public Integer getWrapper() {
        return wrapper;
    }

    public void setWrapper(Integer wrapper) {
        this.wrapper = wrapper;
    }
}
