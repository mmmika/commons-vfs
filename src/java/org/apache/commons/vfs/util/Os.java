/* ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002, 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.commons.vfs.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Class to help determining the OS.
 *
 * @author <a href="mailto:stefan.bodewig@epost.de">Stefan Bodewig</a>
 * @author <a href="mailto:umagesh@apache.org">Magesh Umasankar</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public final class Os
{
    private static final String OS_NAME =
        System.getProperty( "os.name" ).toLowerCase( Locale.US );
    private static final String OS_ARCH =
        System.getProperty( "os.arch" ).toLowerCase( Locale.US );
    private static final String OS_VERSION =
        System.getProperty( "os.version" ).toLowerCase( Locale.US );
    private static final String PATH_SEP =
        System.getProperty( "path.separator" );
    private static final OsFamily OS_FAMILY;
    private static final OsFamily[] OS_ALL_FAMILIES;

    /** All Windows based OSes. */
    public static final OsFamily OS_FAMILY_WINDOWS = new OsFamily( "windows" );

    /** All DOS based OSes. */
    public static final OsFamily OS_FAMILY_DOS = new OsFamily( "dos" );

    /** All Windows NT based OSes. */
    public static final OsFamily OS_FAMILY_WINNT =
        new OsFamily( "nt", new OsFamily[]{OS_FAMILY_WINDOWS} );

    /** All Windows 9x based OSes. */
    public static final OsFamily OS_FAMILY_WIN9X =
        new OsFamily( "win9x", new OsFamily[]{OS_FAMILY_WINDOWS, OS_FAMILY_DOS} );

    /** OS/2 */
    public static final OsFamily OS_FAMILY_OS2 =
        new OsFamily( "os/2", new OsFamily[]{OS_FAMILY_DOS} );

    /** Netware */
    public static final OsFamily OS_FAMILY_NETWARE =
        new OsFamily( "netware" );

    /** All UNIX based OSes. */
    public static final OsFamily OS_FAMILY_UNIX = new OsFamily( "unix" );

    /** All Mac based OSes. */
    public static final OsFamily OS_FAMILY_MAC = new OsFamily( "mac" );

    /** OSX */
    public static final OsFamily OS_FAMILY_OSX =
        new OsFamily( "osx", new OsFamily[]{OS_FAMILY_UNIX, OS_FAMILY_MAC} );

    private static final OsFamily[] ALL_FAMILIES = new OsFamily[]
    {
        OS_FAMILY_DOS,
        OS_FAMILY_MAC,
        OS_FAMILY_NETWARE,
        OS_FAMILY_OS2,
        OS_FAMILY_OSX,
        OS_FAMILY_UNIX,
        OS_FAMILY_WINDOWS,
        OS_FAMILY_WINNT,
        OS_FAMILY_WIN9X
    };

    static
    {
        OS_FAMILY = determineOsFamily();
        OS_ALL_FAMILIES = determineAllFamilies();
    }

    /**
     * Private constructor to block instantiation.
     */
    private Os()
    {
    }

    /**
     * Determines if the OS on which Ant is executing matches the given OS
     * version.
     */
    public static boolean isVersion( final String version )
    {
        return isOs( (OsFamily)null, null, null, version );
    }

    /**
     * Determines if the OS on which Ant is executing matches the given OS
     * architecture.
     */
    public static boolean isArch( final String arch )
    {
        return isOs( (OsFamily)null, null, arch, null );
    }

    /**
     * Determines if the OS on which Ant is executing matches the given OS
     * family.
     */
    public static boolean isFamily( final String family )
    {
        return isOs( family, null, null, null );
    }

    /**
     * Determines if the OS on which Ant is executing matches the given OS
     * family.
     */
    public static boolean isFamily( final OsFamily family )
    {
        return isOs( family, null, null, null );
    }

    /**
     * Determines if the OS on which Ant is executing matches the given OS name.
     *
     * @param name Description of Parameter
     * @return The Name value
     * @since 1.7
     */
    public static boolean isName( final String name )
    {
        return isOs( (OsFamily)null, name, null, null );
    }

    /**
     * Determines if the OS on which Ant is executing matches the given OS
     * family, name, architecture and version.
     *
     * @param family The OS family
     * @param name The OS name
     * @param arch The OS architecture
     * @param version The OS version
     * @return The Os value
     */
    public static boolean isOs( final String family,
                                final String name,
                                final String arch,
                                final String version )
    {
        return isOs( getFamily( family ), name, arch, version );
    }

    /**
     * Determines if the OS on which Ant is executing matches the given OS
     * family, name, architecture and version
     *
     * @param family The OS family
     * @param name The OS name
     * @param arch The OS architecture
     * @param version The OS version
     * @return The Os value
     */
    public static boolean isOs( final OsFamily family,
                                final String name,
                                final String arch,
                                final String version )
    {
        if ( family != null || name != null || arch != null || version != null )
        {
            final boolean isFamily = familyMatches( family );
            final boolean isName = nameMatches( name );
            final boolean isArch = archMatches( arch );
            final boolean isVersion = versionMatches( version );

            return isFamily && isName && isArch && isVersion;
        }
        else
        {
            return false;
        }
    }

    /**
     * Locates an OsFamily by name (case-insensitive).
     *
     * @return the OS family, or null if not found.
     */
    public static OsFamily getFamily( final String name )
    {
        for ( int i = 0; i < ALL_FAMILIES.length; i++ )
        {
            final OsFamily osFamily = ALL_FAMILIES[ i ];
            if ( osFamily.getName().equalsIgnoreCase( name ) )
            {
                return osFamily;
            }
        }

        return null;
    }

    private static boolean versionMatches( final String version )
    {
        boolean isVersion = true;
        if ( version != null )
        {
            isVersion = version.equalsIgnoreCase( OS_VERSION );
        }
        return isVersion;
    }

    private static boolean archMatches( final String arch )
    {
        boolean isArch = true;
        if ( arch != null )
        {
            isArch = arch.equalsIgnoreCase( OS_ARCH );
        }
        return isArch;
    }

    private static boolean nameMatches( final String name )
    {
        boolean isName = true;
        if ( name != null )
        {
            isName = name.equalsIgnoreCase( OS_NAME );
        }
        return isName;
    }

    private static boolean familyMatches( final OsFamily family )
    {
        if ( family == null )
        {
            return false;
        }
        for ( int i = 0; i < OS_ALL_FAMILIES.length; i++ )
        {
            final OsFamily osFamily = OS_ALL_FAMILIES[ i ];
            if ( family == osFamily )
            {
                return true;
            }
        }
        return false;
    }

    private static OsFamily[] determineAllFamilies()
    {
        // Determine all families the current OS belongs to
        Set allFamilies = new HashSet();
        if ( OS_FAMILY != null )
        {
            List queue = new ArrayList();
            queue.add( OS_FAMILY );
            while ( queue.size() > 0 )
            {
                final OsFamily family = (OsFamily)queue.remove( 0 );
                allFamilies.add( family );
                final OsFamily[] families = family.getFamilies();
                for ( int i = 0; i < families.length; i++ )
                {
                    OsFamily parent = families[ i ];
                    queue.add( parent );
                }
            }
        }
        return (OsFamily[])allFamilies.toArray( new OsFamily[ allFamilies.size() ] );
    }

    private static OsFamily determineOsFamily()
    {
        // Determine the most specific OS family
        if ( OS_NAME.indexOf( "windows" ) > -1 )
        {
            if ( OS_NAME.indexOf( "xp" ) > -1
                || OS_NAME.indexOf( "2000" ) > -1
                || OS_NAME.indexOf( "nt" ) > -1 )
            {
                return OS_FAMILY_WINNT;
            }
            else
            {
                return OS_FAMILY_WIN9X;
            }
        }
        else if ( OS_NAME.indexOf( "os/2" ) > -1 )
        {
            return OS_FAMILY_OS2;
        }
        else if ( OS_NAME.indexOf( "netware" ) > -1 )
        {
            return OS_FAMILY_NETWARE;
        }
        else if ( OS_NAME.indexOf( "mac" ) > -1 )
        {
            if ( OS_NAME.endsWith( "x" ) )
            {
                return OS_FAMILY_OSX;
            }
            else
            {
                return OS_FAMILY_MAC;
            }
        }
        else if ( PATH_SEP.equals( ":" ) )
        {
            return OS_FAMILY_UNIX;
        }
        else
        {
            return null;
        }
    }
}
