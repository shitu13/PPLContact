package com.example.rowan.pplcontact;

import java.io.Serializable;

public class DataHolder implements Serializable{


    String  Id,Name,Phone,Email,FbId,Mobile,Designation,Branch,Image,EmpId,Whtsapp,Viber;

    public String getImage() {
        return Image;
    }

    public String getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public String getPhone() {
        return Phone;
    }

    public String getEmail() {
        return Email;
    }

    public String getFbId() {
        return FbId;
    }

    public String getMobile() {
        return Mobile;
    }

    public String getDesignation() {
        return Designation;
    }

    public String getBranch() {
        return Branch;
    }

    public String getEmpId() {
        return EmpId;
    }

    public DataHolder(String name, String phone, String email, String fbId, String mobile,
                      String designation, String branch, String image, String empId,String whtsapp,
                      String viber) {
        Name = name;
        Phone = phone;
        Email = email;
        FbId = fbId;
        Mobile = mobile;
        Designation = designation;
        Branch = branch;
        Image = image;
        EmpId=empId;
        Whtsapp=whtsapp;
        Viber=viber;
    }

    public String getWhtsapp() {
        return Whtsapp;
    }

    public String getViber() {
        return Viber;
    }

    public DataHolder(String image, String id, String name, String phone, String email,
                      String fbId, String mobile, String designation, String branch, String empId,
                      String whtsapp, String viber) {
        Image = image;
        Id = id;
        Name = name;
        Phone = phone;
        Email = email;
        FbId = fbId;
        Mobile = mobile;
        Designation = designation;
        Branch = branch;
        EmpId=empId;
        Whtsapp=whtsapp;
        Viber=viber;

    }

}
