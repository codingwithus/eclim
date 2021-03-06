/**
 * Copyright (C) 2005 - 2013  Eric Van Dewoestine
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.eclim.plugin.core.command.complete;

import java.text.Collator;

import java.util.Locale;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Represents a code completion result.
 *
 * @author Eric Van Dewoestine
 */
public class CodeCompleteResult
  implements Comparable<CodeCompleteResult>
{
  private static final Pattern FIRST_LINE =
    Pattern.compile("(\\.\\s|\\.<|<br|<BR|<p|<P)");
  private static final int MAX_SHORT_DESCRIPTION_LENGTH = 74;

  public static final String VARIABLE = "v";
  public static final String FUNCTION = "f";
  public static final String TYPE = "t";
  public static final String KEYWORD = "k";

  private String completion;
  private String menu;
  private String info;
  private String type;

  /**
   * Constructs a new instance.
   *
   * @param completion The completion string.
   * @param menu The menu text of the completion.
   * @param info The completion info details.
   */
  public CodeCompleteResult (String completion, String menu, String info)
  {
    this(completion, menu, info, StringUtils.EMPTY);
  }

  /**
   * Constructs a new instance.
   *
   * @param completion The completion string.
   * @param menu The menu text of the completion.
   * @param info The completion info details.
   * @param type The completion type.
   */
  public CodeCompleteResult (
      String completion, String menu, String info, String type)
  {
    this.completion = completion;
    this.menu = menu;
    this.info = info;
    this.type = type != null ? type : StringUtils.EMPTY;

    if(this.info != null){
      this.info = StringUtils.replace(this.info, "\n", "<br/>");
    }

    if(this.menu != null){
      this.menu =
        StringUtils.replace(this.menu, "\n", "<br/>");
    }

    if(this.info != null && this.menu == null){
      this.menu = menuFromInfo(this.info);
    }
  }

  /**
   * Gets the completion string.
   *
   * @return The completion.
   */
  public String getCompletion()
  {
    return completion;
  }

  /**
   * Gets the completion info.
   *
   * @return The completion info.
   */
  public String getInfo()
  {
    return info;
  }

  /**
   * Gets the menu text.
   *
   * @return The menu text.
   */
  public String getMenu()
  {
    return menu;
  }

  /**
   * Gets the type of this completion.
   *
   * @return The type.
   */
  public String getType()
  {
    return this.type;
  }

  /**
   * Creates the menu text based on the supplied text info.
   *
   * @param info The info text.
   * @return The menu text
   */
  public static String menuFromInfo(String info)
  {
    if(info == null){
      return null;
    }

    String menu = info;
    Matcher matcher = FIRST_LINE.matcher(menu);
    if(menu.length() > 1 && matcher.find(1)){
      menu = menu.substring(0, matcher.start() + 1);
      if(menu.endsWith("<")){
        menu = menu.substring(0, menu.length() - 1);
      }
    }
    menu = menu.replaceAll("\n", StringUtils.EMPTY);
    menu = menu.replaceAll("<.*?>", StringUtils.EMPTY);
    return StringUtils.abbreviate(menu, MAX_SHORT_DESCRIPTION_LENGTH);
  }

  /**
   * Determines if this object is equal to the supplied object.
   *
   * @param other The object to test equality with.
   * @return true if the objects are equal, false otherwise.
   */
  public boolean equals(Object other)
  {
    if (!(other instanceof CodeCompleteResult)) {
      return false;
    }
    if (this == other) {
      return true;
    }
    CodeCompleteResult result = (CodeCompleteResult)other;
    boolean equal = new EqualsBuilder()
      .append(getCompletion(), result.getCompletion())
      .append(getMenu(), result.getMenu())
      .append(getType(), result.getType())
      .isEquals();

    return equal;
  }

  /**
   * Gets the hash code for this object.
   *
   * @return The hash code for this object.
   */
  public int hashCode()
  {
    return new HashCodeBuilder(18, 38)
      .append(completion)
      .append(menu)
      .toHashCode();
  }

  @Override
  public int compareTo(CodeCompleteResult o)
  {
    return Collator.getInstance(Locale.US)
      .compare(getCompletion(), o.getCompletion());
  }
}
