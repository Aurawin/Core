package com.aurawin.core.lang;

import com.aurawin.core.array.KeyItem;
import org.hibernate.Query;
import org.hibernate.Session;

public class Database {
    public static class Table{
        public static class Domain{
            public static class Network{
                public static final String List = "tbl_d_ntk";
                public static final String Member = "tbl_d_ntk_m";
            }
            public static final String Items = "tbl_d_itm";
            public static final String UserAccounts = "tbl_d_uas";
            public static final String Folder= "tbl_d_frs";
            public static final String File= "tbl_d_fls";

            public static final String Avatar = "tbl_d_avr";
            public static final String Roster = "tbl_d_rtr";
            public static final String RosterField = "tbl_d_rtf";

        }
        public static final String UniqueId = "tbl_k_uid";
    }
    public static class Query{
        public static class Domain{
            public static class ById {
                public static final String name = "QueryDomainById";
                public static final String value = "from Domain where Id=:Id";
                public static org.hibernate.Query Create(Session ssn, long Id){
                    return ssn.getNamedQuery(name)
                            .setLong("Id",Id);
                }
            }
            public static class ByName {
                public static final String name = "QueryDomainByName";
                public static final String value = "from Domain where Name=:Name";
                public static org.hibernate.Query Create(Session ssn, String Name){
                    return ssn.getNamedQuery(name)
                            .setString("Name", Name);
                }
            }
            public static class UserAccount{
                public static class ByName {
                    public static final String name = "QueryDomainUserAccountByName";
                    public static final String value = "from UserAccount where DomainId=:DomainId and User=:Name";
                    public static org.hibernate.Query Create(Session ssn, long DomainId, String Name){
                        return ssn.getNamedQuery(name)
                                .setLong("DomainId", DomainId)
                                .setString("Name", Name);
                    }
                }
                public static class ByAuth {
                    public static final String name = "QueryDomainUserAccountByAuth";
                    public static final String value = "from UserAccount where DomainId=:DomainId and Auth=:Auth";
                    public static org.hibernate.Query Create(Session ssn, long DomainId,String Auth){
                        return ssn.getNamedQuery(name)
                                .setLong("DomainId",DomainId)
                                .setString("Auth", Auth);
                    }
                }
                public static class ById {
                    public static final String name = "QueryDomainUserAccountById";
                    public static final String value = "from UserAccount where DomainId=:DomainId and Id=:Id";
                    public static org.hibernate.Query Create(Session ssn, long DomainId,long Id){
                        return ssn.getNamedQuery(name)
                                .setLong("DomainId",DomainId)
                                .setLong("Id", Id);
                    }
                }
            }
            public static class Network{
                public static class ByOwner{
                    public static final String name = "QueryDomainNetworkByOwner";
                    public static final String value = "from Network where DomainId=:DomainId and OwnerId=:OwnerId";
                    public static org.hibernate.Query Create(Session ssn, long DomainId, long OwnerId){
                        return ssn.getNamedQuery(name)
                                .setLong("DomainId", DomainId)
                                .setLong("OwnerId", OwnerId);
                    }
                }
            }
            public static class Avatar{
                public static class ByOwnerAndKind{
                    public static final String name = "QueryDomainAvatarByOwnerAndKind";
                    public static final String value = "from Avatar where DomainId=:DomainId and OwnerId=:OwnerId and Kind=:Kind";
                    public static org.hibernate.Query Create(Session ssn, long DomainId, long OwnerId, long Kind){
                        return ssn.getNamedQuery(name)
                                .setLong("DomainId", DomainId)
                                .setLong("OwnerId", OwnerId)
                                .setLong("Kind",Kind);
                    }
                }
                public static class ById{
                    public static final String name = "QueryDomainAvatarById";
                    public static final String value = "from Avatar where Id=:Id";
                    public static org.hibernate.Query Create(Session ssn, long Id){
                        return ssn.getNamedQuery(name).setLong("Id", Id);
                    }
                }
            }
            public static class Folder{
                public static class ByName {
                    public static final String name = "QueryDomainFolderByPath";
                    public static final String value = "from Folder where DomainId=:DomainId and Kind=:Kind and Path=:Path";
                    public static org.hibernate.Query Create(Session ssn, long DomainId, long Kind, String Path){
                        return ssn.getNamedQuery(name)
                                .setLong("DomainId", DomainId)
                                .setLong("Kind",Kind)
                                .setString("Path", Path);
                    }
                }
                public static class ById {
                    public static final String name = "QueryDomainFolderById";
                    public static final String value = "from Folder where DomainId=:DomainId and Kind=:Kind and Id=:Id";
                    public static org.hibernate.Query Create(Session ssn, long DomainId, long Kind, long Id){
                        return ssn.getNamedQuery(name)
                                .setLong("DomainId", DomainId)
                                .setLong("Kind",Kind)
                                .setLong("Id",Id );
                    }
                }
            }

        }
        public static class UniqueId{
            public static class ById {
                public static final String name = "QueryUniqueIdById";
                public static final String value = "from UniqueId where Id=:Id";
                public static org.hibernate.Query Create(Session ssn, long Id){
                    return ssn.getNamedQuery(name)
                            .setLong("Id", Id);
                }
            }
            public static class ByNamespace {
                public static final String name = "QueryUniqueIdByNamspace";
                public static final String value = "from UniqueId where Namespace=:Namespace";
                public static org.hibernate.Query Create(Session ssn, String Namespace){
                    return ssn.getNamedQuery(name)
                            .setString("Namespace", Namespace);
                }
            }
        }
    }
    public static class Field{
        public static class UniqueId{
            public static final String Id="itmid";
            public static final String Namespace="itmns";
        }
        public static class Domain{
            public static final String Id="itmid";
            public static final String CertId = "itmcid";
            public static final String Name="itnme";
            public static final String Root="itmrt";
            public static final String FriendlyName="itfme";
            public static final String DefaultOptionCatchAll="itmdca";
            public static final String DefaultOptionQuota="itmdqo";
            public static final String DefaultOptionFiltering="itmdfl";


            public static class UserAccount{
                public static final String Id="itmid";
                public static final String DomainId="itmdi";
                public static final String CabinetId="itcbi";
                public static final String AvatarId="itmad";
                public static final String RosterId="itmrid";
                public static final String User="itmun";
                public static final String Pass="itmpswd";
                public static final String Auth="itmauth";
                public static final String LastIP="itmlip";
                public static final String FirstIP="itmfip";
                public static final String LastLogin="itmlln";
                public static final String LockCount="itmlct";
                public static final String LastConsumptionCalc="itmlcc";
                public static final String Consumption="itmcspn";
                public static final String Quota="itmquo";
            }
            public static class Roster{
                public static final String Id = "itmid";
                public static final String DomainId="itmdi";
                public static final String OwnerId="itmoid";
                public static final String AvatarId  = "iaid";
                public static final String FirstName ="ifnme";
                public static final String MiddleName = "imnme";
                public static final String FamilyName = "ifmle";
                public static final String Alias = "ianme";
                public static final String Emails = "iemls";
                public static final String Phones = "iphns";
                public static final String Addresses = "iadrs";
                public static final String City = "icty";
                public static final String State = "iste";
                public static final String Postal = "izip";
                public static final String Country = "itry";
                public static final String Websites = "iwebs";
                public static final String Custom = "icst";
            }
            public static class RosterField{
                public static final String Id = "itmid";
                public static final String DomainId="itmdi";
                public static final String OwnerId="itmoid";
                public static final String Key = "itmk";
                public static final String Value = "itmv";
            }
            public static class Avatar{
                public static final String Id ="itmid";
                public static final String DomainId="itmdi";
                public static final String OwnerId="itmoid";
                public static final String Kind = "itmkd";
                public static final String Ext = "itmext";
                public static final String Created ="itctd";
                public static final String Modified = "itmtd";
                public static final String Data = "itmdat";
            }
            public static class Network{
                public static final String Id = "itmid";
                public static final String DomainId = "itmdi";
                public static final String OwnerId = "itmoid";
                public static final String AvatarId = "itmaid";
                public static final String Members = "itmbrs";
                public static final String Exposition = "itme";
                public static final String Flags    = "itmf";
                public static final String Created = "itmctd";
                public static final String Modified = "itmmtd";
                public static final String Title = "itmtit";
                public static final String Description = "itmdsc";
                public static final String CustomFolders = "icflds";
                public static class Member{
                    public static final String Id = "itmid";
                    public static final String DomainId = "itmdi";
                    public static final String NetworkId = "itmni";
                    public static final String UserId = "itmoid";
                    public static final String Exposure = "itmexp";
                    public static final String Standing = "itmstd";
                    public static final String ACL = "itmacl";
                }
                public static class Folders{
                    public static final String Id ="itmid";
                    public static final String DomainId="itmdi";
                    public static final String NetworkId="itmni";
                    public static final String Kind = "itmkd";
                    public static final String Exposition = "itme";
                    public static final String Path = "itmp";
                    public static final String Created ="itctd";
                    public static final String Modified = "itmtd";
                }
                public static class Files{
                    public static final String Id = "itmid";
                    public static final String DomainId = "itmdi";
                    public static final String NetworkId = "itmni";
                    public static final String FolderId = "itmfi";
                    public static final String Name = "itmnm";
                    public static final String Digest = "itmde";
                    public static final String Created = "itctd";
                    public static final String Modified = "itmtd";
                    public static final String Size = "itsze";
                    public static final String Summary = "ismry";

                }
            }


        }
    }
    public static class Config{
        public static class Automatic{
            public static final String Create = "create";
            public static final String Update = "update";
            public static final String CreateDrop = "create-drop";
            public static final String Validate = "validate";
        }
    }
    public static class Test{
        public static class Entities{
            public static class Domain{
                public static final String UserAccount = "/test/storage.entities.domain.UserAccount.json";
            }
        }
    }

}
