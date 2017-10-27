/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package net.sf.mmm.code.base.element;

import net.sf.mmm.code.api.element.CodeElement;
import net.sf.mmm.code.base.node.BaseNodeItem;
import net.sf.mmm.code.base.type.BaseType;

/**
 * Base interface for {@link CodeElement}.
 *
 * @author Joerg Hohwiller (hohwille at users.sourceforge.net)
 * @since 1.0.0
 */
public interface BaseElement extends CodeElement, BaseNodeItem {

  @Override
  BaseType getDeclaringType();

}
