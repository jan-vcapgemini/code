/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package net.sf.mmm.code.impl.java.member;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.mmm.code.api.member.CodeMethod;
import net.sf.mmm.code.api.member.CodeProperty;
import net.sf.mmm.code.api.modifier.CodeModifiers;
import net.sf.mmm.code.api.modifier.CodeVisibility;
import net.sf.mmm.code.api.type.CodeGenericType;
import net.sf.mmm.code.api.type.CodeType;
import net.sf.mmm.code.impl.java.type.JavaGenericType;
import net.sf.mmm.code.impl.java.type.JavaType;

/**
 * Implementation of {@link CodeProperty} for Java.
 *
 * @author Joerg Hohwiller (hohwille at users.sourceforge.net)
 * @since 1.0.0
 */
public class JavaProperty extends JavaMember implements CodeProperty {

  private static final Logger LOG = LoggerFactory.getLogger(JavaProperty.class);

  private JavaGenericType type;

  private JavaMethod getter;

  private JavaMethod setter;

  private JavaField field;

  /**
   * The constructor.
   *
   * @param declaringType the {@link #getDeclaringType()}.
   */
  public JavaProperty(JavaType declaringType) {

    super(declaringType, null);
  }

  /**
   * The copy-constructor.
   *
   * @param template the {@link JavaProperty} to copy.
   * @param declaringType the {@link #getDeclaringType()}.
   */
  public JavaProperty(JavaProperty template, JavaType declaringType) {

    super(template, declaringType);
    this.type = template.type;
  }

  @Override
  public CodeModifiers getModifiers() {

    if (this.getter != null) {
      return this.getter.getModifiers();
    }
    if (this.setter != null) {
      return this.setter.getModifiers();
    }
    return this.field.getModifiers();
  }

  /**
   * @deprecated see {@link CodeProperty#setModifiers(CodeModifiers)}.
   */
  @Deprecated
  @Override
  public void setModifiers(CodeModifiers modifiers) {

    CodeProperty.super.setModifiers(modifiers);
  }

  @Override
  public JavaField getField() {

    return this.field;
  }

  void setField(JavaField field) {

    verifyMutalbe();
    this.field = field;
  }

  @Override
  public JavaMethod getGetter() {

    return this.getter;
  }

  void setGetter(JavaMethod getter) {

    verifyMutalbe();
    this.getter = getter;
  }

  @Override
  public JavaMethod getSetter() {

    return this.setter;
  }

  void setSetter(JavaMethod setter) {

    verifyMutalbe();
    this.setter = setter;
  }

  /**
   * @return {@code true} if this property is empty, {@code false} otherwise.
   */
  boolean isEmpty() {

    if (this.field != null) {
      return false;
    }
    if (this.getter != null) {
      return false;
    }
    if (this.setter != null) {
      return false;
    }
    return true;
  }

  @Override
  public JavaProperty inherit(CodeType declaring) {

    JavaGenericType resolvedType = this.type.resolve(declaring);
    if (resolvedType == this.type) {
      return this;
    }
    JavaProperty copy = new JavaProperty(this, (JavaType) declaring);
    copy.type = resolvedType;
    return copy;
  }

  void join(JavaMethod method) {

    verifyMutalbe();
    assert (getName().equals(getPropertyName(method, true))) : method;
    assert (getDeclaringType().equals(method.getDeclaringType())) : method;
    boolean isSetter = (method.getName().startsWith("set"));
    if (isSetter) {
      if (this.setter != null) {
        LOG.debug("Replacing setter {} with {}.", this.setter, method);
      }
      this.setter = method;
    } else {
      if (this.getter != null) {
        LOG.debug("Replacing getter {} with {}.", this.getter, method);
      }
      this.getter = method;
    }
  }

  void join(JavaField newField) {

    verifyMutalbe();
    assert (getName().equals(newField.getName())) : newField;
    assert (getDeclaringType().equals(newField.getDeclaringType())) : newField;
    if (this.field != null) {
      LOG.debug("Replacing field {} with {}.", this.field, newField);
    }
    this.field = newField;
  }

  @Override
  public boolean isReadable(CodeVisibility visibility) {

    if ((this.getter != null) && this.getter.getModifiers().getVisibility().isWeakerOrEqualTo(visibility)) {
      return true;
    }
    if ((this.field != null) && this.field.getModifiers().getVisibility().isWeakerOrEqualTo(visibility)) {
      return true;
    }
    return false;
  }

  @Override
  public boolean isWritable(CodeVisibility visibility) {

    if ((this.setter != null) && this.setter.getModifiers().getVisibility().isWeakerOrEqualTo(visibility)) {
      return true;
    }
    if ((this.field != null) && !this.field.getModifiers().isFinal() && this.field.getModifiers().getVisibility().isWeakerOrEqualTo(visibility)) {
      return true;
    }
    return false;
  }

  @Override
  public JavaGenericType getType() {

    return this.type;
  }

  @Override
  public void setType(CodeGenericType type) {

    verifyMutalbe();
    this.type = (JavaGenericType) type;
  }

  @Override
  protected void doWrite(Appendable sink, String defaultIndent, String currentIndent) throws IOException {

    // properties are virtual and will never be written...
  }

  @Override
  public JavaProperty copy(CodeType newDeclaringType) {

    return new JavaProperty(this, (JavaType) newDeclaringType);
  }

  static String getPropertyName(CodeMethod method, boolean checkSignature) {

    String methodName = method.getName();
    String propertyName = getPropertyName(methodName);
    if (checkSignature && (propertyName != null)) {
      if (methodName.startsWith("set")) {
        if (method.getParameters().size() != 1) {
          return null;
        }
      } else {
        if (method.getParameters().size() != 0) {
          return null;
        }
        if (method.getReturns().getType().asType().isVoid()) {
          return null;
        }
      }
    }
    return propertyName;
  }

  static String getPropertyName(String methodName) {

    String propertyName = methodName;
    if (propertyName.startsWith("set") || propertyName.startsWith("get") || propertyName.startsWith("has") || propertyName.startsWith("can")) {
      propertyName = propertyName.substring(3);
    } else if (propertyName.startsWith("is")) {
      propertyName = propertyName.substring(2);
    } else {
      return null;
    }
    if (!propertyName.isEmpty()) {
      char first = propertyName.charAt(0);
      char uncapitalized = Character.toLowerCase(first);
      if (first == uncapitalized) {
        LOG.debug("Invalid getter/setter without capitalized property will be ignored: {}", methodName);
        return null;
      }
      propertyName = uncapitalized + propertyName.substring(1);
    }
    return propertyName;
  }

}
