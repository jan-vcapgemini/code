/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package net.sf.mmm.code.base;

import net.sf.mmm.code.base.source.BaseSource;
import net.sf.mmm.code.base.source.BaseSourceImpl;

/**
 * Base implementation of {@link BaseContext}.
 *
 * @author Joerg Hohwiller (hohwille at users.sourceforge.net)
 * @since 1.0.0
 */
public abstract class BaseContextImpl extends BaseProviderImpl implements BaseContext {

  private final BaseSourceImpl source;

  /**
   * The constructor.
   *
   * @param source the {@link #getSource() source}.
   */
  public BaseContextImpl(BaseSourceImpl source) {

    super();
    this.source = source;
    this.source.setContext(this);
  }

  @Override
  public abstract BaseContext getParent();

  @Override
  public BaseContext getContext() {

    return this;
  }

  @Override
  public BaseSource getSource() {

    return this.source;
  }

}