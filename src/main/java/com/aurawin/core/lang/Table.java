package com.aurawin.core.lang;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Locale;

import com.aurawin.core.VarString;
import com.aurawin.core.solution.Settings;
import org.json.JSONObject;

public class Table {
    private static Boolean Loaded = false;
    public static void main(String[] args) {
        Load();
    }
    public static final int MaxSize = 1024*1024;
    public static final String defaultResource = "/core.lang."+ Settings.Language+".json";
    public static JSONObject Manifest;
    public static final String CRLF = "\r\n";
    public static Boolean getLoaded(){
        return Loaded;
    }

    public static String Print(int i){
        return NumberFormat.getNumberInstance(Locale.getDefault()).format(i);
    }
    public static final void Load(String Data){
        try {
            Manifest = new JSONObject(Data);
        } catch (java.lang.Exception E){
            Manifest  = null;
        }
    }
    public static final void Load(){
        Load(VarString.fromResource(defaultResource));
        Loaded=true;
    }
    public static final String String(String NameSpace){
        try {
            if (Loaded!=true) Load();
            return Manifest.getString(NameSpace);
        } catch (java.lang.Exception E){
            return "core.lang is missing "+NameSpace;
        }
    }
    public static String Format(String Namespace, String Arg){
        try{
            if (Loaded!=true) Load();
            try {
                Arg = Manifest.getString(Arg);
            } catch (java.lang.Exception e){
            }                return String.format(Manifest.getString(Namespace), Arg);
        } catch (java.lang.Exception e){
            return e.getMessage();
        }

    }
    public static String Format(String Namespace, String Arg1, String Arg2){
        try {
            if (Loaded != true) Load();
            try {
                try {
                    Arg1 = Manifest.getString(Arg1);
                } catch (java.lang.Exception e) {
                }
                try {
                    Arg2 = Manifest.getString(Arg2);
                } catch (java.lang.Exception e) {
                }
                return String.format(Manifest.getString(Namespace), Arg1, Arg2);
            } catch (java.lang.Exception e) {
                return e.getMessage();
            }
        } catch (java.lang.Exception e){
            return e.getMessage();

        }

    }
    public static String Format(String Namespace, String Arg1, String Arg2, String Arg3){
        try {
            if (Loaded != true) Load();
            try {
                try {
                    Arg1 = Manifest.getString(Arg1);
                } catch (java.lang.Exception e) {
                }
                try {
                    Arg2 = Manifest.getString(Arg2);
                } catch (java.lang.Exception e) {
                }
                try {
                    Arg3 = Manifest.getString(Arg3);
                } catch (java.lang.Exception e) {
                }
                return String.format(Manifest.getString(Namespace), Arg1, Arg2,Arg3);
            } catch (java.lang.Exception e) {
                return e.getMessage();
            }
        } catch (java.lang.Exception e){
            return e.getMessage();

        }

    }
    public static class Entities{
        public static class Domain{
            public static final String Root = "table.entities.domain.root";
            public static class Roster{
                public static final String Me = "table.entities.domain.roster.me";
            }
            public static class Network{
                public static class Default {
                    public static final String Title = "table.entities.domain.network.title";
                    public static final String Description = "table.entities.domain.network.description";
                }
            }

        }
    }
    public static class Action{
        public static final String a="table.action.a";
        public static final String an="table.action.an";
        public static final String one="table.action.one";
        public static final String selected="table.action.selected";
        public static final String $this="table.action.this";

    }
    public static class Dialog{
        public static final String New = "table.dialog.New";
        public static final String Open = "table.dialog.Open";
        public static final String Save = "table.dialog.Save";
        public static class Filter{
            public static final String All = "table.dialog.filter.All";
        }
    }
    public static class Label{

        public static final String New = "table.label.New";
        public static final String Open = "table.label.Open";
        public static final String Close = "table.label.Close";
        public static final String Save = "table.label.Save";
        public static final String SaveAs = "table.label.SaveAs";
        public static final String Notice = "table.label.Notice";
        public static final String File = "table.label.File";
        public static final String Folder = "table.label.Folder";

        public static final String Item = "table.label.Item";
        public static final String Items = "table.label.Implements";
        public static final String Collection = "table.label.Collection";
        public static final String Collections = "table.label.Collections";
        public static final String Name = "table.label.Name";
        public static final String Value = "table.label.Value";
        public static final String Untitled = "table.label.Untitled";
    }
    public static class Hint{
        public static final String Add = "table.hint.add";
        public static final String Create = "table.hint.create";
        public static final String Delete = "table.hint.delete";
        public static final String Rename = "table.hint.rename";
        public static final String Refresh = "table.hint.refresh";
        public static final String Input = "table.hint.input";
        public static final String Unsaved = "table.hint.unsaved";
    }
    public static class Item{
        public static final String Object = "table.item.Object";
        public static final String Mailbox = "table.item.Mailbox";
        public static final String Message = "table.item.Message";
    }
    public static class Status{
        public static final String Editing = "table.status.Editing";
        public static final String Loading = "table.status.Loading";
        public static final String Sending = "table.status.Sending";
        public static final String Deleting = "table.status.Deleting";
    }
    public static class JSON{
        public static final String Title = "table.json.Title";
        public static final String Document = "table.json.Document";
        public static final String Source = "table.json.Source";
        public static final String Array = "table.json.Array";
        public static final String Object = "table.json.Object";
        public static final String KeyPair = "table.json.Keypair";
    }
    public static class Exception {
        public static class Strings {
            final String InvalidEncoding = "table.exception.string.invalid-encoding";
        }
        public static class Syslog {
            public static final String UnableToWriteEntries = "table.exception.syslog.unable-to-write-entries";
            public static final String UnableToWriteEntry = "table.exception.syslog.unable-to-write-entry";
            public static final String UnableToCreateLogfile = "table.exception.syslog.unable-to-create-logfile";
            public static final String UnableToCreateDefaultLogfile = "table.exception.syslog.unable-to-create-default-logfile";

            public static String Notice(String Namespace) {
                return Label.Notice + ": " + String(Namespace);
            }

            public static String Notice(String Namespace, String Arg) {
                return Label.Notice + ": " + String.format(String(Namespace), Arg);
            }
        }

        public static class Settings {
            public static final String NotInitialized = "table.exception.settings.not-initialized";
            public static final String NoProgramName = "table.exception.settings.no-program-name";

        }

        public static class Entities {
            public static final String EntityCreatedMethodNotDefined = "table.exception.entities.entity-created-method-not-defined";
            public static final String EntityDeletedMethodNotDefined = "table.exception.entities.entity-deleted-method-not-defined";
            public static final String EntityUpdatedMethodNotDefined = "table.exception.entities.entity-updated-method-not-defined";

            public static final String EntityAnnotationForFetchNotDefined = "table.exception.entities.entity-annotation-for-fetch-not-defined";
            public static class UniqueId {
                public static String UnableToCreateNamespaceEmpty = "table.exception.entities.uniqueid.unable-to-create-namespace-empty";
            }
            public static class Vendor {
                public static class Manifest{
                    public static class Field{
                        public static final String ValueIsNotOfType = "table.exception.entities.vendor.manifest.value-is-not-of-type";
                    }
                }

            }
            public static class Domain{
                public static String UnableToCreateExists = "table.exception.entities.domain.unable-to-create-domain-exists";
                public static class Network{
                }
                public static class Folder{
                    public static String UnableToCreateExists = "table.exception.entities.domain.folder.unable-to-create-exists";
                }
                public static class UserAccount{
                    public static String UnableToCreateExists = "table.exception.entities.domain.useraccount.unable-to-create-user-exists";

                }
                public static class Avatar{
                    public static final String UnableToCreateExists = "table.exception.entities.domain.avatar.unable-to-create-avatar-exists";
                }
            }

        }

        public static class RSR {

            public static final String UnableToBindAddress = "table.exception.rsr.unable-to-bind-address";
            public static final String UnableToAcceptSocket = "table.exception.rsr.unable-to-accept-socket";
            public static final String UnableToCloseAcceptSocket = "table.exception.rsr.unable-to-close-accept-socket";
            public static final String UnableToCreateCommandInstance = "table.exception.rsr.unable-to-create-command-instance";
            public static final String UnableToAccessCommandInstance = "table.exception.rsr.unable-to-access-command-instance";
            public static final String UnableToCreateItemInstance = "table.exception.rsr.unable-to-create-item-instance";
            public static final String UnableToAccessItemInstance = "table.exception.rsr.unable-to-access-item-instance";
            public static final String UnableToOpenItemChannelSelector = "table.exception.rsr.unable-to-open-item-channel-selector";
            public static final String UnableToRegisterItemChannel = "table.exception.rsr.unable-to-register-item-channel";
            public static final String UnableToCloseItemChannel = "table.exception.rsr.unable-to-close-item-channel";
            public static final String UnableToSetReadBuffer = "table.exception.rsr.unable-to-set-read-buffer";
            public static final String UnableToSetWriteBuffer = "table.exception.rsr.unable-to-set-write-buffer";
            public static final String UnableToAccessConncurrently = "table.exception.rsr.unable-to-access-concurrently";
            public static final String UnableToSelectItemKeys = "table.exception.rsr.unable-to-select-item-keys";


            public static class WebSocket {
                public static class SecurityOption {
                    public static final String Invalid = "table.exception.rsr.websocket.securityoption.invalid";
                    public static final String AlreadySet = "table.exception.rsr.websocket.securityoption.alreadyset";

                    public static String getMessage(String OptionTarget, String OptionSource) {
                        try {
                            if (Loaded != true) Load();
                            return String.format(Manifest.getString(Invalid), OptionTarget) + ". " +
                                    String.format(Manifest.getString(AlreadySet), OptionSource) + ".";
                        } catch (java.lang.Exception e) {
                            return "missing";
                        }

                    }
                }
            }
        }
    }
    public static class Error {
        public static class RSR {

            public static final String AcceptFailure = "table.error.rsr.accept-failure";
            public static final String PeekFailure = "table.error.rsr.peek-failure";
            public static final String ProcessFailure = "table.error.rsr.process-failure";
            public static final String DisconnectFailure = "table.error.rsr.disconnect-failure";
            public static final String InitializeFailure = "table.error.rsr.initialize-failure";
            public static final String FinalizeFailure = "table.error.rsr.finalize-failure";

            public static final String Read="table.error.rsr.read";
            public static final String Reset="table.error.rsr.reset";
            public static final String DNS="table.error.rsr.dns";
            public static final String Timeout = "table.error.rsr.timeout";
            public static final String Write="table.error.rsr.write";
        }
    }


}
