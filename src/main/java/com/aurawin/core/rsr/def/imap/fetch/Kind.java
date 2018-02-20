package com.aurawin.core.rsr.def.imap.fetch;

public enum Kind {
    ifkUID, ifkFlags, ifkRFC822Size, ifkRFC822Header,
    ifkInternalDate, ifkBodyHeaderFields, ifkBodyStructure, ifkBody, ifkBodyHeader,
    ifkBodyPeek, ifkBodyPeekHeader, ifkBodyText, ifkBodyPeekText,
    ifkBodySection, ifkBodySectionPartial
}
