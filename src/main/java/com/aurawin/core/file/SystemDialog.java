package com.aurawin.core.file;

import com.aurawin.core.lang.Table;

import javax.swing.*;
import java.io.File;

public class SystemDialog extends JFileChooser {
    private DialogKind Kind;
    private DialogMode Mode;
    public File Path = getCurrentDirectory();
    public DialogKind getKind(){
        return Kind;
    }
    public void setKind(DialogKind kind){
        Kind=kind;
         switch (Kind) {
            case dkNew:
                setDialogType(JFileChooser.SAVE_DIALOG);
                setDialogTitle(Table.Format(Table.Dialog.New, Table.Action.a, Table.JSON.Document));
                break;
            case dkSave:
                 setDialogType(JFileChooser.SAVE_DIALOG);
                setDialogTitle(Table.Format(Table.Dialog.Save, Table.Action.$this, Table.JSON.Document));
                break;
            case dkOpen:
                setDialogType(JFileChooser.OPEN_DIALOG);
                setDialogTitle(Table.Format(Table.Dialog.Open, Table.Action.a, Table.JSON.Document));
                break;
        }
        setDialogType(JFileChooser.FILES_AND_DIRECTORIES);

    }
    public void setMode(DialogMode mode){
        Mode = mode;
        int m = getFileSelectionMode();
        switch (Mode){
            case dmFile:
                m = JFileChooser.FILES_ONLY;
                break;
            case dmFolder:
                m = JFileChooser.DIRECTORIES_ONLY;
                break;
        }
        setFileSelectionMode(m);

    }
    public SystemDialog(DialogKind kind, DialogMode mode){
        super();
        setAcceptAllFileFilterUsed(true);
        resetChoosableFileFilters();
        setKind(kind);
        setMode(mode);
        if (Path.length()>0) {
            setCurrentDirectory(Path);
        }

    }
}
