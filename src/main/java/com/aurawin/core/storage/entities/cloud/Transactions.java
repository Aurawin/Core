package com.aurawin.core.storage.entities.cloud;

import com.aurawin.core.lang.Database;
import com.aurawin.core.storage.annotations.EntityDispatch;
import com.aurawin.core.storage.annotations.QueryById;
import com.aurawin.core.storage.entities.Entities;
import com.aurawin.core.storage.entities.Stored;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.*;

@Entity
@DynamicInsert(value = true)
@DynamicUpdate(value =true)
@SelectBeforeUpdate(value =true)
@Table(name = Database.Table.Cloud.Transactions)
@EntityDispatch(
        onCreated = false,
        onDeleted = false,
        onUpdated = false
)
@QueryById(
        Name = Database.Query.Cloud.Transactions.ById.name,
        Fields = ("Id")
)
@NamedQueries(
        {
                @NamedQuery(
                        name  = Database.Query.Cloud.Transactions.ById.name,
                        query = Database.Query.Cloud.Transactions.ById.value
                )
        }
)
public class Transactions extends Stored {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Field.Cloud.Transactions.Id)
    protected long Id;
    public long getId(){return Id;}

    @Column(name = Database.Field.Cloud.Transactions.Filtered)
    protected long Filtered;
    public long getFiltered() {   return Filtered;  }
    public void setFiltered(long filtered) {        Filtered = filtered;    }

    @Column(name = Database.Field.Cloud.Transactions.Received)
    protected long Received;
    public long getReceived() {       return Received;   }
    public void setReceived(long received) {        Received = received;    }

    @Column(name=Database.Field.Cloud.Transactions.Sent)
    protected long Sent;
    public long getSent() {       return Sent;   }
    public void setSent(long sent) {        Sent = sent;    }

    @Column(name=Database.Field.Cloud.Transactions.Streams)
    protected long Streams;
    public long getStreams() {        return Streams;    }
    public void setStreams(long streams) {        Streams = streams;    }

    @ManyToOne(targetEntity = Node.class, cascade = CascadeType.ALL,fetch=FetchType.EAGER)
    @JoinColumn(name = Database.Field.Cloud.Transactions.NodeId)
    protected Node Node;
    public Node getNode() {        return Node;    }
    public void setNode(Node node) {        Node = node;    }

    public static void entityCreated(Entities List, Stored Entity) {}
    public static void entityDeleted(Entities List, Stored Entity, boolean Cascade) {}
    public static void entityUpdated(Entities List, Stored Entity, boolean Cascade) {}
}
