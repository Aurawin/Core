package com.aurawin.core.stored.entities.loader;

import com.aurawin.core.Environment;
import com.aurawin.core.compiler.Singleton;
import com.aurawin.core.lang.Table;
import com.aurawin.core.stored.*;
import com.aurawin.core.lang.Database;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stored.entities.Entities;
import com.aurawin.core.stored.entities.Module;
import com.aurawin.core.stored.annotations.AnnotatedList;
import com.aurawin.core.compiler.Result.Kind;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LoaderTest {
    private Entities Entities;
    public Manifest Manifest;
    public Singleton Compiler;
    public Module Module;
    private String createModuleSource(Module m){
        StringBuilder sb = new StringBuilder();

        sb.append("package com.aurawin.core.stored.entities;\n");
        sb.append("import javax.persistence.*;\n");
        sb.append("import com.aurawin.core.lang.Database;\n");
        sb.append("import com.aurawin.core.stored.Stored;\n");
        sb.append("import com.aurawin.core.stored.annotations.*;\n");
        sb.append("import com.aurawin.core.stored.entities.Entities;\n");
        sb.append("import org.hibernate.Query;\n");
        sb.append("import org.hibernate.Session;\n");
        sb.append("import org.hibernate.Transaction;\n");
        sb.append("import org.hibernate.annotations.DynamicInsert;\n");
        sb.append("import org.hibernate.annotations.DynamicUpdate;\n");
        sb.append("import org.hibernate.annotations.SelectBeforeUpdate;\n");
        sb.append("import org.json.JSONObject;\n");
        sb.append("import javax.persistence.Entity;\n");
        sb.append("import javax.persistence.NamedQueries;\n");
        sb.append("import javax.persistence.NamedQuery;\n");
        sb.append("import javax.persistence.Table;\n");
        sb.append("@Entity\n");
        sb.append("@DynamicInsert(value=true)\n");
        sb.append("@DynamicUpdate(value=true)\n");
        sb.append("@SelectBeforeUpdate(value=true)\n");
        sb.append("@Table(name = \"$Table$\")\n");
        sb.append("@NamedQueries(\n");
        sb.append("        {\n");
        sb.append("                @NamedQuery(\n");
        sb.append("                        name  = \"Query"+m.getName()+"\",\n");
        sb.append("                        query = \"from "+m.getName()+" where Id=:Id\"\n");
        sb.append("                )\n");
        sb.append("        }\n");
        sb.append(")\n");
        sb.append("\n");

        sb.append("@EntityDispatch(\n");
        sb.append("        onCreated = true,\n");
        sb.append("        onDeleted = true,\n");
        sb.append("        onUpdated = true\n");
        sb.append(")\n");

        sb.append("public class "+m.getName()+" extends Stored {\n");
        sb.append("    @javax.persistence.Id\n");
        sb.append("    @GeneratedValue(strategy = GenerationType.IDENTITY)\n");
        sb.append("    @Column(name = Database.Field.Module.Id)\n");
        sb.append("    private long Id;\n");
        sb.append("    @Override\n");
        sb.append("    public long getId(){return Id;}\n");
        sb.append("\n");
        sb.append("    public "+m.getName()+"() {\n");
        sb.append("        Id=0;\n");
        sb.append("    }\n");
        sb.append("\n");
        sb.append("    public void Assign("+m.getName()+" src){\n");
        sb.append("        Id = src.Id;\n");
        sb.append("    }\n");
        sb.append("    public void Empty(){\n");
        sb.append("        Id = 0;\n");
        sb.append("    }\n");
        sb.append("    @Override\n");
        sb.append("    public boolean equals(Object u) {\n");
        sb.append("        return (( u instanceof "+m.getName()+") && (Id == (("+m.getName()+") u).Id) );\n");
        sb.append("    }\n");
        sb.append("    public void Identify(Session ssn){\n");
        sb.append("        if (Id == 0) {\n");
        sb.append("            "+m.getName()+" n = null;\n");
        sb.append("            Transaction tx = ssn.beginTransaction();\n");
        sb.append("            try {\n");
        sb.append("                Query q = ssn.getNamedQuery(\"Query"+m.getName()+"ById\")\n");
        sb.append("                             .setLong(\"Id\",Id);\n");
        sb.append("                n = ("+m.getName()+") q.uniqueResult();\n");
        sb.append("                if (n == null) {\n");
        sb.append("                    n = new "+m.getName()+"();\n");
        sb.append("                    ssn.save(n);\n");
        sb.append("                }\n");
        sb.append("                Assign(n);\n");
        sb.append("                tx.commit();\n");
        sb.append("            } catch (Exception e){\n");
        sb.append("                tx.rollback();\n");
        sb.append("                throw e;\n");
        sb.append("            }\n");
        sb.append("        }\n");
        sb.append("    }\n");
        sb.append("\n");
        sb.append("    public static void entityCreated(Entities List, Stored Entity){System.out.println(\""+m.getName()+" Created.\");}\n");
        sb.append("    public static void entityDeleted(Entities List, Stored Entity, boolean Cascade){System.out.println(\""+m.getName()+" Deleted.\");}\n");
        sb.append("    public static void entityUpdated(Entities List, Stored Entity, boolean Cascade){System.out.println(\""+m.getName()+" Updated.\");}\n");
        sb.append("}\n");

        return sb.toString();
    }



    @Before
    public void before() throws Exception {
        Settings.Initialize("loader.Test","Aurawin ServerTest","Test","1","0","0");
        Compiler=new Singleton();

        AnnotatedList annotations = new AnnotatedList();
        Manifest = new Manifest(
                Environment.getString(Table.DBMS.Username), // username
                Environment.getString(Table.DBMS.Password),  // password
                Environment.getString(Table.DBMS.Host),     // host
                Environment.getInteger(Table.DBMS.Port),     // port
                true,
                2,                                      // Min Poolsize
                20,                                     // Max Poolsize
                1,                                      // Pool Acquire Increment
                50,                                     // Max statements
                10,                                     // timeout
                Database.Config.Automatic.Update,       //
                "Test",                                 // database
                Dialect.Postgresql.getValue(),          // Dialect
                Driver.Postgresql.getValue(),           // Driver
                annotations
        );
        Entities=new Entities(Manifest);
    }
    @After
    public void after() throws Exception {
    }

    @Test
    public void Test() throws Exception{
        testModule("Noid","com.aurawin.core.stored.entities");
        Stored c = Entities.getLoader().New("com.aurawin.core.stored.entities.Noid");

        if (Entities.hasInjected()==true) {
            try {
                Entities.RecreateFactory();
            } catch (Exception e) {

            }
        }

        testModule("Noid","com.aurawin.core.stored.entities");
        if (Entities.hasInjected()==true) {
            try {
                Entities.RecreateFactory();
            } catch (Exception e) {

            }
        }

    }
    public void testModule(String Name, String Package){
        Session ssn = Entities.Factory.openSession();
        try{
            Transaction tx =ssn.beginTransaction();
            try {
                com.aurawin.core.compiler.Result rC = null;
                String Namespace=Package.toLowerCase()+'.'+Name.toLowerCase();
                Module = Entities.Lookup(Module.class, Namespace);
                if (Module == null) {
                    Module = new Module(Name,Namespace,Package);
                    if (Entities.Save(Module)) {
                        Module.setSource(createModuleSource(Module));
                        Module.setRevision(1);
                        ssn.update(Module);
                    }
                } else {
                    Module.setSource(createModuleSource(Module));
                    Module.setRevision(Module.getRevision()+1);
                    ssn.update(Module);
                }
                if (Module.getBuild()<Module.getRevision()) {
                    rC = Compiler.compile(Module.getSource(), Module.getName());
                    if (rC.Status==Kind.Success) {
                        Module.setCode(rC.Code);
                        Module.setBuild(Module.getRevision());
                        ssn.update(Module);
                    }
                } else {
                    rC = new com.aurawin.core.compiler.Result();
                    rC.Status= Kind.Success;
                    rC.Code=Module.getCode();
                }
                tx.commit();
                if (rC.Status==Kind.Success) {
                    Result r = Entities.Install(Module.getNamespace());
                    if (r.State==Result.Kind.Found){

                    }
                }
            } catch (Exception e){
                tx.rollback();
            }
        } finally{
            ssn.close();
        }
    }
}