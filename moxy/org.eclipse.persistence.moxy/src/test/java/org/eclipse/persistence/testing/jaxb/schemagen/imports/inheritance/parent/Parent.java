/*
 * Copyright (c) 2011, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
// Denise Smith- 2.4
package org.eclipse.persistence.testing.jaxb.schemagen.imports.inheritance.parent;

import jakarta.xml.bind.annotation.XmlType;

@XmlType(namespace="parentnamespace")
public class Parent {
   public String parentThing;
   public boolean equals(Object obj){
       if(obj instanceof Parent){
           return parentThing.equals(((Parent)obj).parentThing);
       }
       return false;
   }
}
