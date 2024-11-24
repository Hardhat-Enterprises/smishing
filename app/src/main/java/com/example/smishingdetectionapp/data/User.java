package com.example.smishingdetectionapp.data;

//retrieves data from Json file and stores it in variables that can be referenced.
public class User {

    public int dbid;
    public String dbname;
    public String dbusername;
    public String dbemail;
    public String dbpassword;
    public String dbdateofbirth;
    public String dbdatecreated;

    public User() {
    }

    public int getDbid() {
        return dbid;
    }

    public void setDbid(int dbid) {
        this.dbid = dbid;
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public String getDbusername() {
        return dbusername;
    }

    public void setDbusername(String dbusername) {
        this.dbusername = dbusername;
    }

    public String getDbemail() {
        return dbemail;
    }

    public void setDbemail(String dbemail) {
        this.dbemail = dbemail;
    }

    public String getDbpassword() {
        return dbpassword;
    }

    public void setDbpassword(String dbpassword) {
        this.dbpassword = dbpassword;
    }

    public String getDbdateofbirth() {
        return dbdateofbirth;
    }

    public void setDbdateofbirth(String dbdateofbirth) {
        this.dbdateofbirth = dbdateofbirth;
    }

    public String getDbdatecreated() {
        return dbdatecreated;
    }

    public void setDbdatecreated(String dbdatecreated) {
        this.dbdatecreated = dbdatecreated;
    }
}