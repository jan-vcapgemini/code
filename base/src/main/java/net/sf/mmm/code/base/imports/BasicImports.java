/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package net.sf.mmm.code.base.imports;

import java.io.IOException;
import java.util.List;
import java.util.TreeSet;

import net.sf.mmm.code.api.CodePackage;
import net.sf.mmm.code.api.imports.CodeImport;
import net.sf.mmm.code.api.imports.CodeImports;
import net.sf.mmm.code.api.node.CodeNodeItemWithGenericParent;
import net.sf.mmm.code.api.syntax.CodeSyntax;
import net.sf.mmm.code.api.type.CodeType;
import net.sf.mmm.code.base.BaseFile;
import net.sf.mmm.code.base.node.BaseNodeItemContainerFlat;
import net.sf.mmm.code.base.type.BaseType;

/**
 * Implementation of {@link CodeImports} for Java.
 *
 * @author Joerg Hohwiller (hohwille at users.sourceforge.net)
 * @since 1.0.0
 */
public class BasicImports extends BaseNodeItemContainerFlat<BaseImport>
    implements CodeImports<BaseImport>, CodeNodeItemWithGenericParent<BaseFile, BasicImports> {

  private static final String DUMMY_PACKAGE_PREFIX = ".";

  private final BaseFile parent;

  /**
   * The constructor.
   *
   * @param parent the {@link #getParent() parent}.
   */
  public BasicImports(BaseFile parent) {

    super();
    this.parent = parent;
  }

  /**
   * The copy-constructor.
   *
   * @param template the {@link BasicImports} to copy.
   * @param parent the {@link #getParent() parent}.
   */
  public BasicImports(BasicImports template, BaseFile parent) {

    super(template);
    this.parent = parent;
  }

  @Override
  public BaseFile getParent() {

    return this.parent;
  }

  @Override
  public BaseType getDeclaringType() {

    return null;
  }

  @Override
  public BaseImport add(CodeType type) {

    verifyMutalbe();
    CodePackage pkg = type.getParentPackage();
    if (!pkg.isRequireImport()) {
      return null;
    }
    CodePackage myPkg = getParent().getParentPackage();
    if ((pkg == myPkg) || pkg.getQualifiedName().equals(myPkg.getQualifiedName())) {
      return null;
    }
    String qname = type.getQualifiedName();
    for (BaseImport imp : getDeclared()) {
      if (!imp.isStatic() && imp.getReference().equals(qname)) {
        return null;
      }
    }
    BaseImport imp = new BaseImport(qname, false);
    add(imp);
    return imp;
  }

  /**
   * @param reference the {@link BaseImport#getReference() reference} to import.
   * @param staticFlag the {@link BaseImport#isStatic() static} flag.
   * @return the {@link BaseImport} that has been created and added.
   */
  public BaseImport add(String reference, boolean staticFlag) {

    verifyMutalbe();
    BaseImport imp = new BaseImport(reference, staticFlag);
    add(imp);
    return imp;
  }

  @Override
  public BasicImports copy() {

    return copy(this.parent);
  }

  @Override
  public BasicImports copy(BaseFile newParent) {

    return new BasicImports(this, newParent);
  }

  @Override
  protected void doWrite(Appendable sink, String newline, String defaultIndent, String currentIndent, CodeSyntax syntax) throws IOException {

    List<BaseImport> imports = getList();
    if (imports.isEmpty()) {
      return;
    }
    TreeSet<CodeImport> sortedImportSet = new TreeSet<>(imports);

    String dummyPackagePrefix = DUMMY_PACKAGE_PREFIX;

    String currentSegmentPrefix = dummyPackagePrefix;
    for (CodeImport imp : sortedImportSet) {
      String reference = imp.getReference();
      if (!reference.startsWith(currentSegmentPrefix)) {
        if (!dummyPackagePrefix.equals(currentSegmentPrefix)) {
          sink.append(newline);
        }
        int firstDot = reference.indexOf('.');
        if (firstDot > 0) {
          currentSegmentPrefix = reference.substring(0, firstDot + 1);
        } else {
          currentSegmentPrefix = dummyPackagePrefix + dummyPackagePrefix;
        }
      }
      imp.write(sink, defaultIndent, currentIndent);
    }
    sink.append(newline);
  }

}