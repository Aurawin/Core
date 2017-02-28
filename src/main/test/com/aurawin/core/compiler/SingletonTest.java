package com.aurawin.core.compiler;

import static org.junit.Assert.*;
import com.aurawin.core.compiler.*;
import org.junit.Test;

import java.lang.annotation.Annotation;

public class SingletonTest {

    private String createJavaSource(){
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
        sb.append("    public void Verify(Session ssn){\n");
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
    @SuppressWarnings("unchecked")
    public void testSingletonClass() throws Exception {
        String src=createJavaSource();
        Singleton compiler = new Singleton();
        Result r = compiler.compile(src,"Noid");
        Object noid = r.Class.getConstructor().newInstance();
        Annotation[] as = noid.getClass().getAnnotations();
    }
    @Test
    public void testSingletonByteCode() throws Exception {
        String src = createJavaSource();
        Singleton compiler = new Singleton();
        byte[] ba = compiler.compileByteCode(src,"Noid");
    }
}