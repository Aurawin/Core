package core.array;


public class KeyItem {

    public String Name;
    public String Value;
    public long Id;
    public Object Data;

    public KeyItem() {
        this.Name="";
        this.Value="";
        this.Id=0;
        this.Data=null;
    }
    public KeyItem(String name){
        this.Name=name;
        this.Value="";
        this.Id=0;
        this.Data=null;
    }
    public KeyItem(String name, String value){
        this.Name=name;
        this.Value=value;
        this.Id=0;
        this.Data=null;
    }
}