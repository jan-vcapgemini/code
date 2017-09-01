/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package net.sf.mmm.code.impl.java;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import net.sf.mmm.code.api.doc.CodeDocFormat;
import net.sf.mmm.code.impl.java.doc.JavaDoc;

/**
 * Test of {@link JavaDoc}.
 */
public class JavaDocTest extends Assertions {

  /**
   * Test of {@link JavaDoc} with links and markup including conversion to arbitary {@link CodeDocFormat}s.
   */
  @Test
  public void testJavaDoc() {

    // given
    JavaContext context = new JavaContext();
    JavaPackage rootPackage = context.getRootPackage();
    String pkg1Name = "pkg1";
    JavaPackage pkg1 = context.createPackage(rootPackage, pkg1Name);
    String pkg2Name = "pkg2";
    JavaPackage pkg2 = context.createPackage(rootPackage, pkg2Name);
    String simpleName1 = "Foo";
    JavaType class1 = context.createType(pkg1, simpleName1);
    String simpleName2 = "Bar";
    JavaType class2 = context.createType(pkg2, simpleName2);
    class1.getFile().addImport(class2);

    // when
    JavaDoc doc = class1.getDoc();
    doc.getLines().add("{@linkplain Foo} references {@link Bar#method()} and {@link Bar#method() link-title}.");
    doc.getLines().add("JavaDoc can contain markup like {@code code}, <em>italic</em>, <b>bold</b>, etc.");
    doc.getLines().add("Also lists may occur: <ul><li><strong>item1</strong></li><li>item2</li></ul>");

    // then
    assertThat(doc.getFormatted(CodeDocFormat.RAW)).isEqualTo("{@linkplain Foo} references {@link Bar#method()} and {@link Bar#method() link-title}.\n" + //
        "JavaDoc can contain markup like {@code code}, <em>italic</em>, <b>bold</b>, etc.\n" + //
        "Also lists may occur: <ul><li><strong>item1</strong></li><li>item2</li></ul>");
    assertThat(doc.getFormatted(CodeDocFormat.PLAIN_TEXT)).isEqualTo("Foo references Bar.method() and link-title.\n" + //
        "JavaDoc can contain markup like code, italic, bold, etc.\n" + //
        "Also lists may occur: \n" + //
        "* item1\n" + //
        "* item2\n");
    assertThat(doc.getFormatted(CodeDocFormat.HTML)).isEqualTo(
        "<a href='./Foo.html'>Foo</a> references <code><a href='../pkg2/Bar.html#method--'>Bar.method()</a></code> and <code><a href='../pkg2/Bar.html#method--'>link-title</a></code>.\n"
            + //
            "JavaDoc can contain markup like <code>code</code>, <em>italic</em>, <b>bold</b>, etc.\n" + //
            "Also lists may occur: <ul><li><strong>item1</strong></li><li>item2</li></ul>");
    assertThat(doc.getFormatted(CodeDocFormat.ASCII_DOC))
        .isEqualTo("link:./Foo.html[Foo] references `link:../pkg2/Bar.html#method--[Bar.method()]` and `link:../pkg2/Bar.html#method--[link-title]`.\n" + //
            "JavaDoc can contain markup like `code`, _italic_, *bold*, etc.\n" + //
            "Also lists may occur: \n" + //
            "\n" + //
            "* *item1*\n" + //
            "* item2\n");
  }

}