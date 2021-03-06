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

package org.unigram.docvalidator.parser.markdown;

/**
 * Buffer list of to candidate sentence.
 */
final class CandidateSentence {
  private int lineNum;

  private String sentence;

  private String link;

  CandidateSentence(int line,
                    String lineCharacter, String linkCharacter) {
    this.lineNum = line;
    this.sentence = lineCharacter;
    this.link = linkCharacter;
  }

  public int getLineNum() {
    return lineNum;
  }

  public String getSentence() {
    return sentence;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String url) {
    this.link = url;
  }

  public void setSentence(String text) {
    this.sentence = text;
  }

  @Override
  public String toString() {
    return "CandidateSentence{"
        + "lineNum=" + lineNum
        + ", sentence='" + sentence + '\''
        + ", link='" + link + '\''
        + '}';
  }
}
