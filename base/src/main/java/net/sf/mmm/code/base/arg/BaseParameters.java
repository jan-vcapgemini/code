/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package net.sf.mmm.code.base.arg;

import java.io.IOException;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.List;

import net.sf.mmm.code.api.arg.CodeParameter;
import net.sf.mmm.code.api.arg.CodeParameters;
import net.sf.mmm.code.api.node.CodeNodeItemWithGenericParent;
import net.sf.mmm.code.api.syntax.CodeSyntax;
import net.sf.mmm.code.base.member.BaseOperation;

/**
 * Base implementation of {@link CodeParameters}.
 *
 * @author Joerg Hohwiller (hohwille at users.sourceforge.net)
 * @since 1.0.0
 */
public class BaseParameters extends BaseOperationArgs<BaseParameter>
    implements CodeParameters<BaseParameter>, CodeNodeItemWithGenericParent<BaseOperation, BaseParameters> {

  /**
   * The constructor.
   *
   * @param parent the {@link #getParent() parent}.
   */
  public BaseParameters(BaseOperation parent) {

    super(parent);
  }

  /**
   * The copy-constructor.
   *
   * @param template the {@link BaseParameters} to copy.
   * @param parent the {@link #getParent() parent}.
   */
  public BaseParameters(BaseParameters template, BaseOperation parent) {

    super(template, parent);
  }

  @Override
  protected void doInitialize() {

    super.doInitialize();
    Executable reflectiveObject = getParent().getReflectiveObject();
    if (reflectiveObject != null) {
      List<BaseParameter> list = getList();
      for (Parameter param : reflectiveObject.getParameters()) {
        BaseParameter parameter = new BaseParameter(this, param);
        list.add(parameter);
      }
    }
  }

  @Override
  public BaseParameter getDeclared(String name) {

    initialize();
    return getByName(name);
  }

  @Override
  public BaseParameter add(String name) {

    BaseParameter parameter = new BaseParameter(this, name);
    add(parameter);
    return parameter;
  }

  @Override
  public BaseParameters copy() {

    return copy(getParent());
  }

  @Override
  public BaseParameters copy(BaseOperation newParent) {

    return new BaseParameters(this, newParent);
  }

  @Override
  protected void doWrite(Appendable sink, String newline, String defaultIndent, String currentIndent, CodeSyntax syntax) throws IOException {

    writeReference(sink, newline, true);
  }

  void writeReference(Appendable sink, String newline, boolean declaration) throws IOException {

    String prefix = "";
    for (CodeParameter parameter : getList()) {
      sink.append(prefix);
      parameter.write(sink, newline, null, null);
      prefix = ", ";
    }
  }

}