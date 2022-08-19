package com.example.a7minexercise

class ExerciseModel(
    private var id: Int,
    private var name: String,
    private var image: Int,
    private var isSelected: Boolean = false,
    private var isCompleted: Boolean = false
    ){
    fun getId() : Int {
        return id
    }
    fun setId(id:Int){
        this.id = id
    }
    fun getName():String{
        return name
    }
    fun setName(name:String){
        this.name = name
    }
    fun getImage(): Int {
        return image
    }
    fun setImage(Image:Int){
        this.image= image
    }
    fun getIsCompleted():Boolean{
        return isCompleted
    }
    fun setIsCompleted(isCompleted: Boolean){
        this.isCompleted = isCompleted
    }

    fun getIsSelected(): Boolean {
        return isSelected
    }
    fun setIsSelected(isCompleted: Boolean){
        this.isSelected = isSelected
    }



}