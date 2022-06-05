package com.mfgpker.worklog.data;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserConfig implements Serializable {
    public  static final long serialVersionUID = 1337L;

    public String currentCompany;

    public List<String> companies = new ArrayList<String>();

    public  UserConfig()  {

    }



    public  UserConfig(String currentCompany)  {
        this.currentCompany = currentCompany;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
