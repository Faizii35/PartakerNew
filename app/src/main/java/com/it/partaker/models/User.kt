package com.it.partaker.models

import java.io.Serializable

data class User (
    private var id : String = "", private var fullName : String = "",
    private var phoneNumber : String = "", private var city : String = "",
    private var email : String = "", private var password : String = "",
    private var gender : String = "", private var registerAs : String = "",
    private var bloodGroup : String = "", private var reports: String = "",
    private var profilePic : String = ""
) : Serializable {

    //Default No Argument Constructor
    fun User(){}
    fun User(
        id: String, fullName: String, phoneNumber: String, city: String, email: String,
        password: String, gender: String, registerAs: String, bloodGroup: String,reports: String, profilePic: String){
        this.id = id
        this.fullName = fullName
        this.phoneNumber = phoneNumber
        this.city = city
        this.email = email
        this.password = password
        this.gender = gender
        this.registerAs = registerAs
        this.bloodGroup = bloodGroup
        this.reports = reports
        this.profilePic = profilePic
}

    fun setReport(reports: String){
        this.reports = reports
    }
    fun getId(): String { return id }
    fun getFullName(): String { return fullName }
    fun getPhoneNumber(): String { return phoneNumber }
    fun getCity(): String { return city }
    fun getEmail(): String { return email }
    fun getPassword(): String { return password }
    fun getRegisterAs(): String { return registerAs }
    fun getGender(): String { return gender }
    fun getProfilePic(): String { return profilePic }
    fun getBloodGroup(): String { return bloodGroup }
    fun getReport(): String { return reports }

}