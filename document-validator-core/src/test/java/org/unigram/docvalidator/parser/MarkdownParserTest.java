/*
 * DocumentValidator
 * Copyright (c) 2013-, Takahiko Ito, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package org.unigram.docvalidator.parser;

import org.junit.Before;
import org.junit.Test;
import org.unigram.docvalidator.store.FileContent;
import org.unigram.docvalidator.store.Paragraph;
import org.unigram.docvalidator.store.Section;
import org.unigram.docvalidator.util.DVResource;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.util.ValidatorConfiguration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;

public class MarkdownParserTest {

  @Before
  public void setup() {
  }

  @Test
  public void testNullDocument() {
    try {
      FileContent doc = createFileContentFromInputStream(null);
      fail("no error");
    } catch (AssertionError as) {

    }
  }

  @Test
  public void testBasicDocument() throws UnsupportedEncodingException {
    String sampleText = "";
    sampleText += "# About Gekioko.\n";
    sampleText += "Gekioko pun pun maru means very very angry.\n";
    sampleText += "\n";
    sampleText += "The word also have posive meaning.\n";
    sampleText += "## About Gunma.\n";
    sampleText += "\n";
    sampleText += "Gunma is located at west of Saitama.\n";
    sampleText += "\n";
    sampleText += "* Features\n";
    sampleText += "    * Main City: Gumma City\n";
    sampleText += "    * Capical: 200 Millon\n";
    sampleText += "* Location\n";
    sampleText += "    * Japan\n";
    sampleText += "\n";
    sampleText += "The word also have posive meaning. Hower it is a bit wired.";

    FileContent doc = createFileContent(sampleText);
    assertNotNull("doc is null", doc);
    assertEquals(3, doc.getNumberOfSections());
    Section lastSection = doc.getSection(doc.getNumberOfSections() - 1);
    assertEquals(1, lastSection.getNumberOfLists());
    assertEquals(5, lastSection.getListBlock(0).getNumberOfListElements());
    assertEquals(3, lastSection.getNumberOfParagraphs());
    assertEquals(1, lastSection.getHeaderContentsListSize());
    assertEquals("About Gunma.", lastSection.getHeaderContent(0).content);
  }


  @Test
  public void testGenerateDocumentWithList() {
    String sampleText =
        "Threre are several railway companies in Japan as follows.\n";
    sampleText += "\n";
    sampleText += "- Tokyu\n";
    sampleText += "    - Toyoko Line\n";
    sampleText += "    - Denentoshi Line\n";
    sampleText += "- Keio\n";
    sampleText += "- Odakyu\n";
    FileContent doc = createFileContent(sampleText);
    assertEquals(5, doc.getSection(0).getListBlock(0).getNumberOfListElements());
    assertEquals("Tokyu", doc.getSection(0).getListBlock(0).getListElement(0).getSentence(0).content);
    assertEquals(1, doc.getSection(0).getListBlock(0).getListElement(0).getLevel());
    assertEquals("Toyoko Line", doc.getSection(0).getListBlock(0).getListElement(1).getSentence(0).content);
    assertEquals(2, doc.getSection(0).getListBlock(0).getListElement(1).getLevel());
    assertEquals("Denentoshi Line", doc.getSection(0).getListBlock(0).getListElement(2).getSentence(0).content);
    assertEquals(2, doc.getSection(0).getListBlock(0).getListElement(2).getLevel());
    assertEquals("Keio", doc.getSection(0).getListBlock(0).getListElement(3).getSentence(0).content);
    assertEquals(1, doc.getSection(0).getListBlock(0).getListElement(3).getLevel());
    assertEquals("Odakyu", doc.getSection(0).getListBlock(0).getListElement(4).getSentence(0).content);
    assertEquals(1, doc.getSection(0).getListBlock(0).getListElement(4).getLevel());
  }

  @Test
  public void testGenerateDocumentWithMultipleSentenceInOneSentence() {
    String sampleText =
        "Tokyu is a good railway company. The company is reliable. In addition it is rich.";
    String[] expectedResult = {"Tokyu is a good railway company.",
        " The company is reliable.", " In addition it is rich."};
    FileContent doc = createFileContent(sampleText);
    Section firstSections = doc.getSection(0);
    Paragraph firstParagraph = firstSections.getParagraph(0);
    assertEquals(3, firstParagraph.getNumberOfSentences());
    for (int i = 0; i < expectedResult.length; i++) {
      assertEquals(expectedResult[i], firstParagraph.getSentence(i).content);
    }
  }


  @Test
  public void testGenerateDocumentWithMultipleSentenceInMultipleSentences() {
    String sampleText = "Tokyu is a good railway company. The company is reliable. In addition it is rich.\n";
    sampleText += "I like the company. Howerver someone does not like it.";
    FileContent doc = createFileContent(sampleText);
    Section firstSections = doc.getSection(0);
    Paragraph firstParagraph = firstSections.getParagraph(0);
    assertEquals(5, firstParagraph.getNumberOfSentences());
  }

  @Test
  public void testGenerateDocumentWitVoidContent() {
    String sampleText = "";
    FileContent doc = createFileContent(sampleText);
    Section firstSections = doc.getSection(0);
    assertEquals(false, firstSections.getParagraphs().hasNext());
  }

  @Test
  public void testGenerateDocumentWithPeriodInSuccession() {
    String sampleText = "...";
    FileContent doc = createFileContent(sampleText);
    Section firstSections = doc.getSection(0);
    Paragraph firstParagraph = firstSections.getParagraph(0);
    assertEquals(1, firstParagraph.getNumberOfSentences());
  }


  @Test
  public void testGenerateDocumentWitoutPeriodInLastSentence() {
    String sampleText = "Hongo is located at the west of Tokyo. Saitama is located at the north";
    FileContent doc = createFileContent(sampleText);
    Section firstSections = doc.getSection(0);
    Paragraph firstParagraph = firstSections.getParagraph(0);
    assertEquals(2, firstParagraph.getNumberOfSentences());
  }

  @Test
  public void testGenerateDocumentWithSentenceLongerThanOneLine() {
    String sampleText = "This is a good day.\n";
    sampleText += "Hongo is located at the west of Tokyo ";
    sampleText += "which is the capital of Japan ";
    sampleText += "which is not located in the south of the earth.";
    FileContent doc = createFileContent(sampleText);
    Section firstSections = doc.getSection(0);
    Paragraph firstParagraph = firstSections.getParagraph(0);
    assertEquals(2, firstParagraph.getNumberOfSentences());
  }


  private Parser loadParser(DVResource resource) {
    Parser parser = null;
    try {
      parser = DocumentParserFactory.generate("markdown", resource);
    } catch (DocumentValidatorException e1) {
      fail();
      e1.printStackTrace();
    }
    return parser;
  }

  private FileContent createFileContentFromInputStream(
      InputStream inputStream) {
    ValidatorConfiguration conf = new ValidatorConfiguration("dummy");
    Parser parser = loadParser(new DVResource(conf));
    FileContent doc = null;
    try {
      parser.generateDocument(inputStream);
    } catch (DocumentValidatorException e) {
      e.printStackTrace();
      fail();
    }
    return doc;
  }

  private FileContent createFileContent(
      String inputDocumentString) {
    ValidatorConfiguration conf = new ValidatorConfiguration("dummy");
    Parser parser = loadParser(new DVResource(conf));
    InputStream is;
    try {
      is = new ByteArrayInputStream(inputDocumentString.getBytes("utf-8"));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      fail();
      return null;
    }
    FileContent doc = null;
    try {
      doc = parser.generateDocument(is);
    } catch (DocumentValidatorException e) {
      e.printStackTrace();
      fail();
    }
    return doc;
  }


}