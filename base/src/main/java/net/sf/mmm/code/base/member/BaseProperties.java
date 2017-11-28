/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package net.sf.mmm.code.base.member;

import java.util.HashMap;
import java.util.Map;

import net.sf.mmm.code.api.copy.CodeCopyMapper;
import net.sf.mmm.code.api.member.CodeField;
import net.sf.mmm.code.api.member.CodeMember;
import net.sf.mmm.code.api.member.CodeProperties;
import net.sf.mmm.code.api.member.CodeProperty;
import net.sf.mmm.code.api.type.CodeGenericType;
import net.sf.mmm.code.base.type.BaseType;

/**
 * Base implementation of {@link CodeProperties}.
 *
 * @author Joerg Hohwiller (hohwille at users.sourceforge.net)
 * @since 1.0.0
 */
public class BaseProperties extends BaseMembers<CodeProperty> implements CodeProperties {

  /**
   * The constructor.
   *
   * @param parent the {@link #getParent() parent}.
   */
  public BaseProperties(BaseType parent) {

    super(parent);
  }

  /**
   * The copy-constructor.
   *
   * @param template the {@link BaseProperties} to copy.
   * @param mapper the {@link CodeCopyMapper}.
   */
  public BaseProperties(BaseProperties template, CodeCopyMapper mapper) {

    super(template, mapper);
  }

  @Override
  public Iterable<? extends CodeProperty> getAll() {

    Map<String, CodeProperty> map = new HashMap<>(getMap());
    collectProperties(map);
    return map.values();
  }

  private void collectProperties(Map<String, CodeProperty> map) {

    for (CodeGenericType superType : getDeclaringType().getSuperTypes()) {
      BaseProperties properties = (BaseProperties) superType.asType().getProperties();
      for (CodeProperty property : properties) {
        map.putIfAbsent(property.getName(), property.inherit(getParent()));
      }
      properties.collectProperties(map);
    }
  }

  @Override
  public CodeProperty get(String name) {

    CodeProperty property = getByName(name);
    if (property != null) {
      return property;
    }
    for (CodeGenericType superType : getDeclaringType().getSuperTypes().getDeclared()) {
      CodeProperties javaProperties = superType.asType().getProperties();
      property = javaProperties.get(name);
      if (property != null) {
        return property.inherit(getParent());
      }
    }
    return null;
  }

  @Override
  public BaseProperty add(String name) {

    verifyMutalbe();
    BaseProperty property = new BaseProperty(this, name);
    add(property);
    return property;
  }

  @Override
  public CodeProperty getDeclared(String name) {

    return getByName(name);
  }

  void renameMember(CodeMember member, String oldName, String newName) {

    if (member instanceof CodeField) {
      rename((CodeField) member, oldName, newName);
    } else if (member instanceof BaseMethod) {
      rename((BaseMethod) member, oldName, newName);
    }
  }

  private void rename(CodeField field, String oldName, String newName) {

    Map<String, CodeProperty> map = getMap();
    BaseProperty property = (BaseProperty) map.get(oldName);
    if ((property != null) && property.getField() == field) {
      property.setField(null);
      if (property.isEmpty()) {
        remove(property);
      }
    }
    property = (BaseProperty) getDeclaredOrCreate(newName);
    property.join(field);
  }

  private void rename(BaseMethod method, String oldName, String newName) {

    Map<String, CodeProperty> map = getMap();
    String propertyName = BaseProperty.getPropertyName(oldName);
    if (propertyName != null) {
      BaseProperty property = (BaseProperty) map.get(propertyName);
      if (property != null) {
        if (oldName.startsWith("set")) {
          if (property.getSetter() == method) {
            property.setSetter(null);
          }
        } else {
          if (property.getGetter() == method) {
            property.setGetter(null);
          }
        }
        if (property.isEmpty()) {
          remove(property);
        }
      }
    }
    propertyName = BaseProperty.getPropertyName(method, true);
    if (propertyName != null) {
      BaseProperty property = (BaseProperty) getDeclaredOrCreate(propertyName);
      property.join(method);
    }
  }

  /**
   * @deprecated a {@link BaseProperties} is a virtual object that can never have a source-code object.
   */
  @Deprecated
  @Override
  public CodeProperties getSourceCodeObject() {

    return null;
  }

  @Override
  public BaseProperties copy() {

    return copy(getDefaultCopyMapper());
  }

  @Override
  public BaseProperties copy(CodeCopyMapper mapper) {

    return new BaseProperties(this, mapper);
  }

}
