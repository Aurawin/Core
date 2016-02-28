package test.com.aurawin.core.stored.entities.loader;

import com.aurawin.core.compiler.Singleton;
import com.aurawin.core.stored.entities.loader.Loader;
import com.aurawin.core.lang.Database;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stored.Dialect;
import com.aurawin.core.stored.Driver;
import com.aurawin.core.stored.Hibernate;
import com.aurawin.core.stored.Manifest;
import com.aurawin.core.stored.entities.Entities;
import com.aurawin.core.stored.entities.Module;
import com.aurawin.core.stored.annotations.StoredAnnotations;
import com.aurawin.core.stored.entities.loader.Result;
import com.aurawin.core.compiler.Result.Kind;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LoaderTest {
    private SessionFactory sf;
    private Session ssn;
    private Entities Entities;
    public Manifest Manifest;
    public Singleton Compiler;
    public Module Module;
    private String createModuleSource(){
        StringBuilder sb = new StringBuilder();
        sb.append("import javax.persistence.*;\n");
        sb.append("import com.aurawin.core.lang.Database;\n");
        sb.append("import com.aurawin.core.stored.Stored;\n");
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
        sb.append("@Table(name = Database.Table.Noid)\n");
        sb.append("@NamedQueries(\n");
        sb.append("        {\n");
        sb.append("                @NamedQuery(\n");
        sb.append("                        name  = Database.Query.Noid.ById.name,\n");
        sb.append("                        query = Database.Query.Noid.ById.value\n");
        sb.append("                )\n");
        sb.append("        }\n");
        sb.append(")\n");
        sb.append("\n");
        sb.append("public class Noid extends Stored {\n");
        sb.append("    @javax.persistence.Id\n");
        sb.append("    @GeneratedValue(strategy = GenerationType.IDENTITY)\n");
        sb.append("    @Column(name = Database.Field.Noid.Id)\n");
        sb.append("    private long Id;\n");
        sb.append("    @Override\n");
        sb.append("    public long getId(){return Id;}\n");
        sb.append("\n");
        sb.append("    public Noid() {\n");
        sb.append("        Id=0;\n");
        sb.append("    }\n");
        sb.append("\n");
        sb.append("    public void Assign(Noid src){\n");
        sb.append("        Id = src.Id;\n");
        sb.append("    }\n");
        sb.append("    public void Empty(){\n");
        sb.append("        Id = 0;\n");
        sb.append("    }\n");
        sb.append("    @Override\n");
        sb.append("    public boolean equals(Object u) {\n");
        sb.append("        return (( u instanceof Noid) && (Id == ((Noid) u).Id) );\n");
        sb.append("    }\n");
        sb.append("    public void Identify(Session ssn){\n");
        sb.append("        if (Id == 0) {\n");
        sb.append("            Noid n = null;\n");
        sb.append("            Transaction tx = ssn.beginTransaction();\n");
        sb.append("            try {\n");
        sb.append("                Query q = Database.Query.Noid.ById.Create(ssn,Id);\n");
        sb.append("                n = (Noid) q.uniqueResult();\n");
        sb.append("                if (n == null) {\n");
        sb.append("                    n = new Noid();\n");
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
        sb.append("    public static void entityCreated(Entities List, Stored Entity){}\n");
        sb.append("    public static void entityDeleted(Entities List, Stored Entity, boolean Cascade){}\n");
        sb.append("    public static void entityUpdated(Entities List, Stored Entity, boolean Cascade){}\n");
        sb.append("}\n");

        return sb.toString();
    }

    @Before
    public void before() throws Exception {
        Settings.Initialize("loader.Test");
        Compiler=new Singleton();

        StoredAnnotations annotations = new StoredAnnotations();
        Manifest = new Manifest(
                "Test",                                 // username
                "Test",                                 // password
                "172.16.1.1",                           // host
                5432,                                   // port
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

        sf = Hibernate.openSession(Manifest);
        ssn = sf.openSession();
    }
    @After
    public void after() throws Exception {
        ssn.close();
    }

    @Test
    public void Test() throws Exception{
        Transaction tx =ssn.beginTransaction();
        try {
            Module = (Module) Entities.Lookup(Module.class, Entities, "com.aurawin.core.stored.entities.noid");
            if (Module == null) {
                if (Entities.Create(Entities, Module)) {
                    Module.setSource(createModuleSource());
                    Module.setRevision(1);
                    ssn.update(Module);
                }
            } else {
                Module.setSource(createModuleSource());
                Module.setRevision(Module.getRevision() + 1);
                ssn.update(Module);
            }
            com.aurawin.core.compiler.Result rC = Compiler.compile(Module.getSource(),Module.getName());
            if (rC.Status==Kind.Success) {
                Module.setCode(rC.Code);
                Loader l = new Loader();
                Result r = l.Check(Module);
                if (r.State==Result.Kind.Found){
                    Module.setBuild(Module.getBuild()+1);
                    ssn.update(Module);
                }
            }
            tx.commit();
        } catch (Exception e){
            tx.rollback();
        }
    }
}