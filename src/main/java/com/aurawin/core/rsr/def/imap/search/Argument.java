package com.aurawin.core.rsr.def.imap.search;

import com.aurawin.core.solution.Settings;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.aurawin.core.solution.Settings.RSR.Items.IMAP.Search.Argument.*;

public enum Argument {
    srchArgAll,
    srchArgAnswered,     // 1
    srchArgBcc,          // 2
    srchArgBefore,       // 2
    srchArgBody,         // 2
    srchArgCC,           // 2
    srchArgDeleted,      // 1
    srchArgDraft,        // 1
    srchArgFlagged,      // 1
    srchArgFrom,         // 2
    srchArgHeader,       // 3
    srchArgKeyword,
    srchArgLarger,
    srchArgNew,
    srchArgNot,
    srchArgOld,
    srchArgOn,
    srchArgOr,
    srchArgRecent,
    srchArgSeen,
    srchArgSentBefore,
    srchArgSentOn,
    srchArgSentSince,
    srchArgSince,
    srchArgSmaller,
    srchArgSubject,
    srchArgMessageID,
    srchArgText,
    srchArgTo,
    srchArgUID,
    srchArgUnAnwered,
    srchArgUnDeleted,
    srchArgUnDraft,
    srchArgUnFlagged,
    srchArgUnKeyword,
    srchArgUnseen,
    srchArgUnknown;
    static Set<Argument>ReadAhead=  new HashSet<>(Arrays.asList(
            srchArgBcc,
            srchArgBefore,
            srchArgBody,
            srchArgCC,
            srchArgFrom,
            srchArgKeyword,
            srchArgSubject,
            srchArgText,
            srchArgTo,
            srchArgUID,
            srchArgMessageID,
            srchArgUnKeyword,
            srchArgHeader
    ));
    static Set<Argument>SearchSingle = new HashSet<>(Arrays.asList(
            srchArgAnswered,
            srchArgDeleted,
            srchArgDraft,
            srchArgFlagged,
            srchArgNew,
            srchArgOld,
            srchArgRecent,
            srchArgSeen,
            srchArgUnAnwered,
            srchArgUnDeleted,
            srchArgUnDraft,
            srchArgUnFlagged,
            srchArgUnseen
    ));
    static Set<Argument>SearchDouble = new HashSet<>(Arrays.asList(
            srchArgBcc,
            srchArgBefore,
            srchArgBody,
            srchArgCC,
            srchArgFrom,
            srchArgKeyword,
            srchArgLarger,
            srchArgOn,
            srchArgSentBefore,
            srchArgSentOn,
            srchArgSentSince,
            srchArgSince,
            srchArgSmaller,
            srchArgSubject,
            srchArgText,
            srchArgTo,
            srchArgUID,
            srchArgMessageID,
            srchArgUnKeyword

    ));
    static Set<Argument>SearchTripple = new HashSet<>(Arrays.asList(
            srchArgHeader
    ));
    static Argument fromString(String sData) {
        if (sData.equalsIgnoreCase(ALL)) {
            return srchArgAll;
        } else if (sData.equalsIgnoreCase(ANSWERED)) {
            return srchArgAnswered;
        } else if (sData.equalsIgnoreCase(BCC)) {
            return srchArgBcc;
        } else if (sData.equalsIgnoreCase(BEFORE)) {
            return srchArgBefore;
        } else if (sData.equalsIgnoreCase(BODY)){
            return srchArgBody;
        } else if (sData.equalsIgnoreCase(CC)){
            return srchArgCC;
        } else if (sData.equalsIgnoreCase(DELETED)){
            return srchArgDeleted;
        } else if (sData.equalsIgnoreCase(DRAFT)){
            return srchArgDraft;
        } else if (sData.equalsIgnoreCase(FLAGGED)) {
            return srchArgFlagged;
        } else if (sData.equalsIgnoreCase(FROM)) {
            return srchArgFrom;
        } else if (sData.equalsIgnoreCase(HEADER)) {
            return srchArgHeader;
        } else if (sData.equalsIgnoreCase(KEYWORD)) {
            return srchArgKeyword;
        } else if (sData.equalsIgnoreCase(LARGER)) {
            return srchArgLarger;
        } else if (sData.equalsIgnoreCase(NEW)) {
            return srchArgNew;
        } else if (sData.equalsIgnoreCase(NOT)) {
            return srchArgNot;
        } else if (sData.equalsIgnoreCase(ON)) {
            return srchArgOn;
        } else if (sData.equalsIgnoreCase(OR)) {
            return srchArgOr;
        } else if (sData.equalsIgnoreCase(RECENT)) {
            return srchArgRecent;
        } else if (sData.equalsIgnoreCase(SEEN)) {
            return srchArgSeen;
        } else if (sData.equalsIgnoreCase(SENTBEFORE)) {
            return srchArgSentBefore;
        } else if (sData.equalsIgnoreCase(SENTON)) {
            return srchArgSentOn;
        } else if (sData.equalsIgnoreCase(SENTSINCE)) {
            return srchArgSentSince;
        } else if (sData.equalsIgnoreCase(SINCE)) {
            return srchArgSince;
        } else if (sData.equalsIgnoreCase(SMALLER)) {
            return srchArgSmaller;
        } else if (sData.equalsIgnoreCase(SUBJECT)) {
            return srchArgSubject;
        } else if (sData.equalsIgnoreCase(MESSAGEID)) {
            return srchArgMessageID;
        } else if (sData.equalsIgnoreCase(TEXT)) {
            return srchArgText;
        } else if (sData.equalsIgnoreCase(TO)) {
            return srchArgTo;
        } else if (sData.equalsIgnoreCase(UID)) {
            return srchArgUID;
        } else if (sData.equalsIgnoreCase(UNANSWERED)) {
            return srchArgUnAnwered;
        } else if (sData.equalsIgnoreCase(UNDELETED)) {
            return srchArgUnDeleted;
        } else if (sData.equalsIgnoreCase(UNDRAFT)) {
            return srchArgUnDraft;
        } else if (sData.equalsIgnoreCase(UNFLAGGED)) {
            return srchArgUnFlagged;
        } else if (sData.equalsIgnoreCase(UNKEYWORD)) {
            return srchArgUnKeyword;
        } else if (sData.equalsIgnoreCase(UNSEEN)) {
            return srchArgUnseen;
        } else {
            return srchArgUnknown;
        }

    }
}
