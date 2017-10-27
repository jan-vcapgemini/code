/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package net.sf.mmm.code.base;

import java.lang.reflect.Type;

import net.sf.mmm.code.api.CodeContext;
import net.sf.mmm.code.api.CodeName;
import net.sf.mmm.code.api.element.CodeElement;
import net.sf.mmm.code.api.type.CodeGenericType;
import net.sf.mmm.code.api.type.CodeType;
import net.sf.mmm.code.base.element.BaseElement;
import net.sf.mmm.code.base.type.BaseGenericType;
import net.sf.mmm.code.base.type.BaseType;
import net.sf.mmm.code.base.type.BaseTypeWildcard;
import net.sf.mmm.util.exception.api.ObjectNotFoundException;

/**
 * Base implementation of {@link CodeContext}.
 *
 * @author Joerg Hohwiller (hohwille at users.sourceforge.net)
 * @since 1.0.0
 */
public interface BaseContext extends CodeContext {

  @Override
  BaseType getRootType();

  @Override
  BaseType getRootEnumerationType();

  @Override
  BaseType getVoidType();

  @Override
  BaseType getRootExceptionType();

  @Override
  BaseTypeWildcard getUnboundedWildcard();

  /**
   * @param clazz the {@link Class} to get as {@link CodeGenericType}.
   * @return the existing or otherwise newly created {@link CodeGenericType}. Typically a {@link CodeType} but
   *         may also be a {@link CodeType#createArray() array type} in case an {@link Class#isArray() array}
   *         was given.
   */
  BaseGenericType getType(Class<?> clazz);

  @Override
  default BaseType getRequiredType(String qualifiedName) {

    BaseType type = getType(qualifiedName);
    if (type == null) {
      throw new ObjectNotFoundException(CodeType.class, qualifiedName);
    }
    return type;
  }

  /**
   * @param type the {@link Type} to get as {@link CodeGenericType}.
   * @param declaringElement the owning {@link CodeElement} declaring the {@link Type}.
   * @return the existing or otherwise newly created {@link CodeGenericType}.
   */
  BaseGenericType getType(Type type, BaseElement declaringElement);

  @Override
  BaseType getType(String qualifiedName);

  @Override
  BaseType getType(CodeName qualifiedName);

  /**
   * @param javaType the {@link BaseType} that might be {@link BaseType#isPrimitive() primitive}.
   * @return the corresponding {@link BaseType#getNonPrimitiveType() non-primitive type}.
   */
  BaseType getNonPrimitiveType(BaseType javaType);

}
