/*
 * PropertyDeclaration.java
 * Copyright (c) 2004 Torbj�rn Gannholm, Patrick Wright
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 */
package org.xhtmlrenderer.css.sheet;


import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.sheet.factory.*;


/**
 * Represents a single property declared in a CSS rule set. A
 * PropertyDeclaration is created from an CSSValue and is immutable. The
 * declaration knows its origin, importance and specificity, and thus is
 * prepared to be sorted out among properties of the same name, within a matched
 * group, for the CSS cascade, into a {@link
 * org.xhtmlrenderer.css.newmatch.CascadedStyle}.
 *
 * @author   Torbj�rn Gannholm
 * @author   Patrick Wright
 */
public class PropertyDeclaration {
    /** Description of the Field */
    private String propName;

    /** Description of the Field */
    private CSSName cssName;
    /** Description of the Field */
    private org.w3c.dom.css.CSSPrimitiveValue cssPrimitiveValue;

    /** Whether the property was declared as important! by the user.  */
    private boolean important;

    /**
     * Origin constant from the list defined in {@link Stylesheet}. See {@link
     * Stylesheet#USER_AGENT}, {@link StylesheetInfo#USER}, and {@link
     * Stylesheet#AUTHOR}.
     */
    private int origin;
    /** Description of the Field */
    private IdentValue _identVal;

    /** Description of the Field */
    private boolean identIsSet;

    /** ImportanceAndOrigin of stylesheet - how many different  */
    public final static int IMPORTANCE_AND_ORIGIN_COUNT = 6;

    /** Description of the Field */
    private final static PropertyDeclarationFactory[] PROPERTY_FACTORIES;
    /** Description of the Field */
    private final static PropertyDeclarationFactory DEFAULT_PD_FACTORY;

    /** ImportanceAndOrigin of stylesheet - user agent  */
    private final static int USER_AGENT = 1;

    /** ImportanceAndOrigin of stylesheet - user normal  */
    private final static int USER_NORMAL = 2;

    /** ImportanceAndOrigin of stylesheet - author normal  */
    private final static int AUTHOR_NORMAL = 3;

    /** ImportanceAndOrigin of stylesheet - author important  */
    private final static int AUTHOR_IMPORTANT = 4;

    /** ImportanceAndOrigin of stylesheet - user important  */
    private final static int USER_IMPORTANT = 5;

    /**
     * Creates a new instance of PropertyDeclaration from an {@link
     * CSSPrimitiveValue} instance.
     *
     * @param cssName
     * @param value    The CSSValue to wrap
     * @param imp      True if property was declared important! and false if
     *      not.
     * @param orig     int constant from {@link Stylesheet} for the origin of
     *      the property declaration, that is, the origin of the style sheet
     *      where it was declared. See {@link StylesheetInfo#USER_AGENT}, {@link
     *      StylesheetInfo#USER}, and {@link StylesheetInfo#AUTHOR}.
     */
    public PropertyDeclaration( CSSName cssName,
                                org.w3c.dom.css.CSSPrimitiveValue value,
                                boolean imp,
                                int orig ) {
        this.propName = cssName.toString();
        this.cssName = cssName;
        this.cssPrimitiveValue = value;
        this.important = imp;
        this.origin = orig;
    }

    /**
     * Converts to a String representation of the object.
     *
     * @return   A string representation of the object.
     */
    public String toString() {
        return getPropertyName() + ": " + getValue().toString();
    }

    /**
     * Description of the Method
     *
     * @return   Returns
     */
    public IdentValue asIdentValue() {
        if ( !identIsSet ) {
            _identVal = IdentValue.getByIdentString( cssPrimitiveValue.getCssText() );
            identIsSet = true;
        }
        return _identVal;
    }

    /**
     * Returns an int representing the combined origin and importance of the
     * property as declared. The int is assigned such that default origin and
     * importance is 0, and highest an important! property defined by the user
     * (origin is Stylesheet.USER). The combined value would allow this property
     * to be sequenced in the CSS cascade along with other properties matched to
     * the same element with the same property name. In that sort, the highest
     * sequence number returned from this method would take priority in the
     * cascade, so that a user important! property would override a user
     * non-important! property, and so on. The actual integer value returned by
     * this method is unimportant, but has a lowest value of 0 and increments
     * sequentially by 1 for each increase in origin/importance..
     *
     * @return   See method javadoc.
     */
    public int getImportanceAndOrigin() {
        if ( origin == StylesheetInfo.USER_AGENT ) {
            return PropertyDeclaration.USER_AGENT;
        } else if ( origin == StylesheetInfo.USER ) {
            if ( important ) {
                return PropertyDeclaration.USER_IMPORTANT;
            }
            return PropertyDeclaration.USER_NORMAL;
        } else {
            if ( important ) {
                return PropertyDeclaration.AUTHOR_IMPORTANT;
            }
            return PropertyDeclaration.AUTHOR_NORMAL;
        }
    }

    /**
     * Returns the CSS name of this property, e.g. "font-family".
     *
     * @return   See desc.
     */
    public String getPropertyName() {
        return propName;
    }

    /**
     * Gets the cSSName attribute of the PropertyDeclaration object
     *
     * @return   The cSSName value
     */
    public CSSName getCSSName() {
        return cssName;
    }

    /**
     * Returns the specified {@link org.w3c.dom.css.CSSValue} for this property.
     * Specified means the value as entered by the user. Modifying the CSSValue
     * returned here will result in indeterminate behavior--consider it
     * immutable.
     *
     * @return   See desc.
     */
    public org.w3c.dom.css.CSSPrimitiveValue getValue() {
        return cssPrimitiveValue;
    }

    /**
     * Description of the Method
     *
     * @param cssName  PARAM
     * @return         Returns
     */
    public static PropertyDeclarationFactory newFactory( CSSName cssName ) {
        PropertyDeclarationFactory pdf = PROPERTY_FACTORIES[cssName.getAssignedID()];
        if ( pdf == null ) {
            pdf = DEFAULT_PD_FACTORY;
        }
        return pdf;
    }

    static {
        DEFAULT_PD_FACTORY = DefaultPropertyDeclarationFactory.instance();

        PROPERTY_FACTORIES = new PropertyDeclarationFactory[CSSName.countCSSNames()];
        PROPERTY_FACTORIES[CSSName.BACKGROUND_SHORTHAND.getAssignedID()] = BackgroundPropertyDeclarationFactory.instance();
        PROPERTY_FACTORIES[CSSName.BACKGROUND_POSITION.getAssignedID()] = BackgroundPositionPropertyDeclarationFactory.instance();
        PROPERTY_FACTORIES[CSSName.BORDER_SHORTHAND.getAssignedID()] = BorderPropertyDeclarationFactory.instance();
        PROPERTY_FACTORIES[CSSName.BORDER_COLOR_SHORTHAND.getAssignedID()] = BorderColorPropertyDeclarationFactory.instance();
        PROPERTY_FACTORIES[CSSName.BORDER_STYLE_SHORTHAND.getAssignedID()] = BorderStylePropertyDeclarationFactory.instance();
        PROPERTY_FACTORIES[CSSName.BORDER_WIDTH_SHORTHAND.getAssignedID()] = BorderWidthPropertyDeclarationFactory.instance();
        PROPERTY_FACTORIES[CSSName.BORDER_TOP_SHORTHAND.getAssignedID()] = BorderSidePropertyDeclarationFactory.instance();
        PROPERTY_FACTORIES[CSSName.BORDER_RIGHT_SHORTHAND.getAssignedID()] = BorderSidePropertyDeclarationFactory.instance();
        PROPERTY_FACTORIES[CSSName.BORDER_BOTTOM_SHORTHAND.getAssignedID()] = BorderSidePropertyDeclarationFactory.instance();
        PROPERTY_FACTORIES[CSSName.BORDER_LEFT_SHORTHAND.getAssignedID()] = BorderSidePropertyDeclarationFactory.instance();
        PROPERTY_FACTORIES[CSSName.FONT_SHORTHAND.getAssignedID()] = FontPropertyDeclarationFactory.instance();
        PROPERTY_FACTORIES[CSSName.FONT_FAMILY.getAssignedID()] = FontFamilyPropertyDeclarationFactory.instance();
        PROPERTY_FACTORIES[CSSName.LIST_STYLE_SHORTHAND.getAssignedID()] = ListStylePropertyDeclarationFactory.instance();
        PROPERTY_FACTORIES[CSSName.MARGIN_SHORTHAND.getAssignedID()] = MarginPropertyDeclarationFactory.instance();
        PROPERTY_FACTORIES[CSSName.OUTLINE_SHORTHAND.getAssignedID()] = OutlinePropertyDeclarationFactory.instance();
        PROPERTY_FACTORIES[CSSName.PADDING_SHORTHAND.getAssignedID()] = PaddingPropertyDeclarationFactory.instance();
    }
}// end class

/*
 * $Id$
 *
 * $Log$
 * Revision 1.10  2005/01/29 12:07:37  pdoubleya
 * Changed to use array for PD factories.
 *
 * Revision 1.9  2005/01/25 14:45:56  pdoubleya
 * Added support for IdentValue mapping on property declarations. On both CascadedStyle and PropertyDeclaration you can now request the value as an IdentValue, for object-object comparisons. Updated 99% of references that used to get the string value of PD to return the IdentValue instead; remaining cases are for pseudo-elements where the PD content needs to be manipulated as a String.
 *
 * Revision 1.8  2005/01/24 19:01:08  pdoubleya
 * Mass checkin. Changed to use references to CSSName, which now has a Singleton instance for each property, everywhere property names were being used before. Removed commented code. Cascaded and Calculated style now store properties in arrays rather than maps, for optimization.
 *
 * Revision 1.7  2005/01/24 14:54:32  pdoubleya
 * Removed references to XRProperty (unused).
 *
 * Revision 1.6  2005/01/24 14:36:30  pdoubleya
 * Mass commit, includes: updated for changes to property declaration instantiation, and new use of DerivedValue. Removed any references to older XR... classes (e.g. XRProperty). Cleaned imports.
 *
 * Revision 1.5  2004/12/11 18:18:07  tobega
 * Still broken, won't even compile at the moment. Working hard to fix it, though. Replace the StyleReference interface with our only concrete implementation, it was a bother changing in two places all the time.
 *
 * Revision 1.4  2004/11/28 23:29:01  tobega
 * Now handles media on Stylesheets, still need to handle at-media-rules. The media-type should be set in Context.media (set by default to "screen") before calling setContext on StyleReference.
 *
 * Revision 1.3  2004/11/16 10:39:34  pdoubleya
 * Made members all private where appropriate.
 * Comments.
 *
 * Revision 1.2  2004/11/15 12:42:23  pdoubleya
 * Across this checkin (all may not apply to this particular file)
 * Changed default/package-access members to private.
 * Changed to use XRRuntimeException where appropriate.
 * Began move from System.err.println to std logging.
 * Standard code reformat.
 * Removed some unnecessary SAC member variables that were only used in initialization.
 * CVS log section.
 *
 *
 */

